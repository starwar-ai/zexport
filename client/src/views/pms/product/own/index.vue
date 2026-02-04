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
        v-hasPermi="['pms:own-brand-sku:create']"
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
    <template #detail="{ key }"> <own-product-detail :id="key" /></template>

    <template #edit="{ key }">
      <own-product-form
        :id="key"
        mode="edit"
        :changeEdit="true"
      />
    </template>
    <template #create>
      <own-product-form mode="create" />
    </template>
    <template #change="{ key }">
      <own-product-form
        :id="key"
        mode="change"
        :changeEdit="false"
      />
    </template>
    <template #copy="{ key }">
      <own-product-form
        :id="key"
        mode="copy"
        :changeEdit="true"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as skuApi from '@/api/pms/main/index'
import * as ownSkuApi from '@/api/pms/own/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import ownProductForm from './ownProductForm.vue'
import ownProductDetail from './ownProductDetail.vue'
// import ownSkuChangeForm from '../ownSkuChange/custSkuChangeForm.vue'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { LengthUnit } from '@/utils/config'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { getVenderSimpleList } from '@/api/common'
import { isValidArray } from '@/utils/is'
import { EplusSearchAmountRange, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as brandApi from '@/api/pms/brand'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'
import { useAppStore } from '@/store/modules/app'
import router from '@/router'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'OwnSku' })

const handleDialogFailure = () => {}

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

const brandName = ref('')
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
      name: 'oskuCode',
      nameList: 'oskuCodeListStr',
      label: '自营产品货号',
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
    },
    {
      component: (
        <eplus-custom-select
          api={brandApi.getBrandSimpleList}
          label="name"
          value="id"
          class="!w-100%"
          placeholder="请选择"
          onChange={(item) => {
            brandName.value = item.name
          }}
        />
      ),
      name: 'brandId',
      label: '品牌',
      formatter: async (args: any[]) => {
        return brandName.value
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'creator',
      label: '录入人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'belongingDeptId',
      label: '归属部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
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
    const res = await ownSkuApi.getOwnSkuPage({
      ...ps,
      skuTypeInList: activeName.value === 'all' ? undefined : activeName.value.split(',')
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
    //return await ownSkuApi.exportOwnSku({ ...ps, ownBrandFlag: 1, custProFlag: 0 })
    let list = eplusListRef.value?.checkedItems
    if (isValidArray(list)) {
      if (list.length > 50) {
        ElMessage.error('最多导出50条记录')
        return
      }
      let ids = list.map((item) => {
        return item.id
      })
      return await ownSkuApi.exportOwnSku({ ...ps, idList: ids })
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
          prop: 'oskuCode',
          label: '自营产品货号'
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
          minWidth: columnWidth.s,
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
        // {
        //   prop: 'custName',
        //   label: '客户名称',
        //   minWidth: '100px'
        // },
        // {
        //   prop: 'changeStatus',
        //   label: '变更标识',
        //   formatter: formatDictColumn(DICT_TYPE.CHANGE_TYPE),
        //   minWidth: columnWidth.m
        // },
        // {
        //   prop: 'cskuCode',
        //   label: '客户货号',
        //   minWidth: '100px'
        // },
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
        {
          prop: 'oskuCode',
          label: '自营产品货号',
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.m,
          isHyperlink: true
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
          prop: 'measureUnit',
          label: '计量单位',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
        },
        {
          prop: 'material',
          label: '产品材质',
          minWidth: columnWidth.m
        },
        {
          prop: 'totalAmountList',
          label: '单品规格/净重',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              if (row.singleNetweight?.weight) {
                return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/${row.singleNetweight.weight} ${row.singleNetweight.unit}`
              } else {
                return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/-`
              }
            }
          }
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
          prop: 'brandName',
          label: '品牌',
          minWidth: columnWidth.l
        },
        {
          prop: 'belongingDeptName',
          label: '归属部门',
          minWidth: columnWidth.l
        },
        {
          prop: 'creatorName',
          label: '录入人',
          minWidth: columnWidth.l
        },

        // 2025/1/9 小郭让注释
        // {
        //   prop: 'delivery',
        //   label: '采购交期',
        //   minWidth: columnWidth.m,
        //   slots: {
        //     default: (data) => {
        //       const { row } = data
        //       return row.delivery ? `${row.delivery}天` : '-'
        //     }
        //   }
        // },
        // {
        //   prop: 'withTaxPrice',
        //   label: '采购成本',
        //   minWidth: columnWidth.m,
        //   slots: {
        //     default: (data) => {
        //       const { row } = data
        //       return `${row.withTaxPrice} ${row.currency}`
        //     }
        //   }
        // },

        {
          label: '单品毛重',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              if (row.singleGrossweight) {
                return `${row.singleGrossweight.weight} ${row.singleGrossweight.unit}`
              } else {
                return '-'
              }
            }
          }
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
                    hasPermi="['pms:own-brand-sku:detail']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    editItem={{
                      permi: 'pms:own-brand-sku:update',
                      handler: () => {
                        handleUpdate(row.id)
                      }
                    }}
                    deleteItem={{
                      permi: 'pms:own-brand-sku:delete',
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
                        // isShow: false,
                        otherKey: 'cskuChange',
                        label: '变更',
                        permi: 'pms:own-brand-sku:change',
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
const exportFileName = computed(() => {
  return checkPermi(['pms:own-brand-sku:export']) ? '自营产品.xlsx' : ''
})

const handleSubmit = async (data) => {
  // 提交请求
  let res = await ownSkuApi.submitOsku(data.id)
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

const handleCopy = (id: number) => {
  eplusDialogRef.value?.openCopy(id, '自营产品复制')
}
/// 打开详情
const appStore = useAppStore()
const handleDetail = async (id: number) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/base/product-manage/ownDetail/${id}`
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
  eplusDialogRef.value?.openChange(id, '自营产品变更')
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
