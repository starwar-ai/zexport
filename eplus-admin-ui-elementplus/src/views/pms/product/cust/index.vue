<template>
  <div class="h-45px bg-[var(--left-menu-bg-color)] pl-15px pr-15px">
    <el-tabs
      v-model="activeName"
      @tab-click="handleTabsClick"
    >
      <el-tab-pane
        label="普通产品"
        name="1,2"
        key="1"
      />
      <!-- <el-tab-pane
        label="组合产品"
        name="2"
        key="2"
      /> -->
      <el-tab-pane
        label="配件产品"
        name="3"
        key="3"
      />
      <el-tab-pane
        label="全部"
        name="all"
        key="8"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        @click="handleCreate()"
        v-hasPermi="['pms:csku:create']"
      >
        新增
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <cust-product-detail :id="key" /></template>

    <template #edit="{ key }">
      <cust-product-form
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <cust-product-form mode="create" />
    </template>
    <template #change="{ key }">
      <cust-product-form
        :id="key"
        mode="change"
      />
    </template>
    <template #copy="{ key }">
      <cust-product-form
        :id="key"
        mode="copy"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as skuApi from '@/api/pms/main/index'
import * as custSkuApi from '@/api/pms/cust/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import custProductForm from './custProductForm.vue'
import custProductDetail from './custProductDetail.vue'
// import custSkuChangeForm from '../custSkuChange/custSkuChangeForm.vue'
import { useCountryStore } from '@/store/modules/countrylist'
import { getVenderSimpleList, regionList } from '@/api/common'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { isValidArray } from '@/utils/is'
import * as UserApi from '@/api/system/user'
import { EplusSearchAmountRange, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { useAppStore } from '@/store/modules/app'
import router from '@/router'
import { EplusCountrySelect } from '@/components/EplusSelect'

const countryListData = useCountryStore()

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'CustSku' })

const handleDialogFailure = () => {}
const searchCountryName = ref('')
const searchRegionName = ref('')
const skuTypeFilter = (arr) => {
  if (activeName.value == 'all') {
    return arr.filter((item) => item.value < 4)
  } else {
    return arr.filter((item) => activeName.value.split(',').indexOf(String(item.value)) >= 0)
  }
}
const activeName = ref('1,2')
const handleTabsClick = (val) => {
  activeName.value = val.props.name
  handleRefresh()
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <eplus-sku-code-search></eplus-sku-code-search>,
      name: 'name',
      nameList: 'nameListStr',
      label: '中文品名',
      className: '!w-200px'
    },
    {
      component: <eplus-sku-code-search></eplus-sku-code-search>,
      name: 'basicSkuCode',
      nameList: 'basicSkuCodeListStr',
      label: '基础产品编号',
      className: '!w-200px'
    },
    {
      component: <eplus-sku-code-search></eplus-sku-code-search>,
      name: 'cskuCode',
      nameList: 'cskuCodeListStr',
      label: '客户货号',
      className: '!w-200px'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'nameEng',
      label: '英文名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'hsCode',
      label: 'HS编码'
    },
    {
      component: <el-input></el-input>,
      name: 'taxRefundRate',
      label: '退税率'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.IS_INT}></eplus-dict-select>,
      name: 'advantageFlag',
      label: '优势产品',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.IS_INT, args[0])
      }
    },
    {
      component: <el-input></el-input>,
      name: 'custName',
      label: '客户名称'
    },

    {
      component: (
        <EplusCountrySelect
          onChange={(item) => {
            searchCountryName.value = item.name
          }}
        />
      ),
      name: 'countryId',
      label: '国家',
      formatter: () => {
        return searchCountryName.value
      }
    },
    {
      component: (
        <eplus-custom-select
          api={regionList}
          label="name"
          value="code"
          placeholder="请选择"
          style="width:100%"
          onChange={(item) => {
            searchRegionName.value = item.name
          }}
        />
      ),
      name: 'regionCode',
      label: '区域',
      formatter: () => {
        return searchRegionName.value
      }
    },
    {
      component: (
        <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}></eplus-dict-select>
      ),
      name: 'auditStatus',
      label: '审核状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1, skuQuoteFlag: 1 }}
          keyword="name"
          label="name"
          value="code"
          multiple={true}
          class="!w-100%"
          placeholder="请选择"
          onChangeEmit={(...$event) => getVenderName($event)}
        />
      ),
      name: 'venderCodeList',
      label: '供应商',
      formatter: async (args: any[]) => {
        return venderNameStr.value
      }
    },
    {
      component: (
        <eplus-dict-select
          dictType={DICT_TYPE.SKU_TYPE}
          filter={skuTypeFilter}
        ></eplus-dict-select>
      ),
      name: 'skuType',
      label: '产品类型',
      formatter: async (args: any[]) => {
        return getDictLabel(DICT_TYPE.SKU_TYPE, args[0])
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaseUserId',
      label: '采购员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '创建时间',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'amount', label: '供应商报价', formatter: '从{0}到{1}' }]
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.CURRENCY_TYPE}></eplus-dict-select>,
      name: 'currency',
      label: '报价币种',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.CURRENCY_TYPE, args[0])
      }
    }
  ],
  moreFields: []
}

const venderNameStr = ref('')
const getVenderName = (e) => {
  let nameList: string[] = []
  e[1].forEach((item) => {
    e[0].forEach((el) => {
      if (item.code === el) {
        nameList.push(item.name)
      }
    })
  })
  venderNameStr.value = nameList.join(',')
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await custSkuApi.getCustSkuPage({
      ...ps,
      skuTypeList: activeName.value === 'all' ? undefined : activeName.value.split(',')
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await skuApi.deleteSku(id)
  },
  exportListApi: async (ps) => {
    //return await custSkuApi.exportCustSku({ ...ps, custProFlag: 1 })
    let list = eplusListRef.value?.checkedItems
    if (isValidArray(list)) {
      if (list.length > 50) {
        ElMessage.error('最多导出50条记录')
        return
      }
      let ids = list.map((item) => {
        return item.id
      })
      return await custSkuApi.exportCustSku({ ...ps, idList: ids })
    } else {
      ElMessage.error('请选择要导出的数据项')
      return
    }
  },
  showPictures: true,
  showTabs: true,
  summary: false,
  tabs: [
    {
      label: '图片',
      selection: false,
      isTree: true,
      picture: true,
      columns: [
        {
          prop: 'cskuCode',
          label: '客户货号'
        },
        {
          prop: 'name',
          label: '中文品名'
        }
      ]
    },
    {
      label: '列表',
      selection: true,
      columns: [
        {
          label: '图片',
          prop: 'thumbnail',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="flex items-center">
                  <EplusImgEnlarge
                    mainPicture={row.mainPicture}
                    thumbnail={row.thumbnail}
                    width="40"
                    height="40"
                  />
                </div>
              )
            }
          }
        },
        {
          prop: 'custCode',
          label: '客户编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'countryName',
          label: '国家名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'regionName',
          label: '地区名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'cskuCode',
          label: '客户货号',
          minWidth: columnWidth.m
        },
        {
          prop: 'name',
          label: '中文品名',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <EplusSkuName
                  name={row.name}
                  type={row.skuType}
                />
              )
            }
          }
        },
        // {
        //   prop: 'code',
        //   label: '产品编号',
        //   minWidth: columnWidth.l,
        //   isHyperlink: true
        // },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.l
        },
        {
          prop: 'hsCode',
          label: 'HS编码',
          minWidth: columnWidth.m
        },
        {
          prop: 'taxRefundRate',
          label: '退税率(%)',
          minWidth: columnWidth.m
        },
        {
          prop: 'skuType',
          label: '产品类型',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.SKU_TYPE)
        },
        {
          prop: 'venderName',
          label: '供应商',
          minWidth: columnWidth.l
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.l
        },
        {
          prop: 'withTaxPrice',
          label: '采购成本',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'auditStatus',
          label: '审核状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
        },
        {
          prop: 'createTime',
          label: '创建日期',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: columnWidth.l,
          fixed: 'right',
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="flex items-center">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['pms:csku:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    editItem={{
                      permi: 'pms:csku:update',
                      handler: () => {
                        handleUpdate(row.id)
                      }
                    }}
                    deleteItem={{
                      permi: 'pms:csku:delete',
                      handler: async (row) => {
                        await handleDelete(row.id)
                      }
                    }}
                    otherItems={[
                      {
                        isShow: true,
                        otherKey: 'cskuCopy',
                        label: '复制',
                        permi: 'pms:csku:create',
                        handler: (row) => {
                          handleCopy(row.id)
                        }
                      },
                      {
                        isShow: row.changeStatus !== 2 && row.auditStatus == 2 ? true : false,
                        otherKey: 'cskuChange',
                        label: '变更',
                        permi: 'pms:csku:change',
                        // checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                        handler: (row) => {
                          handleChange(row.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'managerDel',
                        label: '管理员删除',
                        permi: 'pms:sku:manager-delete',
                        handler: (row) => {
                          handleManagerDel(row.id)
                        }
                      }
                    ]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        }
      ]
    }
  ]
}
const exportFileName = ref('客户产品.xlsx')
const handleExport = async () => {
  return await eplusListRef.value.exportList('客户产品.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await custSkuApi.submitCsku(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

const handleManagerDel = async (id: number) => {
  await message.confirm('确定要删除吗？')
  await skuApi.manageDel(id)
  await eplusListRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

const appStore = useAppStore()
/// 打开详情
const handleDetail = async (id: number) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/base/product-manage/cskuDetail/${id}`
    })
  }
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const handleChange = (id: number) => {
  eplusDialogRef.value?.openChange(id, '客户产品变更')
}

const handleCopy = (id: number) => {
  eplusDialogRef.value?.openCopy(id, '客户产品复制')
}

const handleRevertAudit = async (id) => {
  let res = await skuApi.revertAudit(id)
  if (res) {
    message.success('返审核成功！')
  }
  await eplusListRef.value.handleRefresh()
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
