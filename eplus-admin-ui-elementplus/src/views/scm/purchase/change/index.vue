<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tabs> </template>
    <template #tableActions>&nbsp;</template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #edit="{ key }">
      <change-form
        :id="key"
        mode="edit"
      />
    </template>
    <template #detail="{ key }">
      <change-detail :id="key" />
      <!-- {{ key }} -->
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as changeApi from '@/api/scm/purchaseChange'
import * as UserApi from '@/api/system/user'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import ChangeDetail from './ChangeDetail.vue'
import ChangeForm from './ChangeForm.vue'
import { formatDate } from '@/utils/formatTime'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'PurchaseChange' })

const eplusListRef = ref()

const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'creator',
      label: '创建人',
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
          label: '创建日期',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '变更单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'sourceCode',
      label: '采购单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'venderName',
      label: '供应商'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await changeApi.getChangePage({ ...ps })
    let resultList = res.list?.map((item) => {
      item?.children
        ?.filter((item1) => item1?.changeFlag === 3)
        .forEach((item2) => {
          item2.oldChildren = item.oldData?.children.filter((item3) => item3?.id === item2?.id)
          return item2
        })
      return item
    })
    return {
      list: resultList,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await changeApi.deleteChange(id)
  },
  //**是否包含多个表格显示 */
  showTabs: false,
  oldChangeField: 'oldData',
  columns: [
    {
      prop: 'code',
      label: '变更单号',
      minWidth: columnWidth.m,
      parent: true,
      isHyperlink: true
    },
    {
      prop: 'sourceCode',
      label: '采购单号',
      minWidth: columnWidth.m,
      parent: true
    },
    {
      prop: 'companyName',
      label: '采购主体',
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'custName',
      label: '客户名称',
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'creatorName',
      label: '创建人',
      minWidth: columnWidth.m,
      parent: true,
      slots: {
        default: (row) => {
          return row.createUser?.nickname
        }
      }
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
      minWidth: columnWidth.m,
      parent: true,
      formatter: formatDateColumn()
    },
    {
      prop: 'venderName',
      label: '供应商',
      minWidth: columnWidth.m,
      parent: true,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="venderName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'purchaseUserName',
      label: '采购员',
      minWidth: columnWidth.m,
      parent: true
    },
    {
      prop: 'trackUserName',
      label: '跟单员',
      minWidth: columnWidth.m,
      parent: true,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="trackUserName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      parent: true,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'mainPicture',
      label: '图片',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.mainPicture && row.changeFlag !== 4) {
            return (
              <EplusImgEnlarge
                mainPicture={row.mainPicture}
                thumbnail={row?.thumbnail}
              />
            )
          } else {
            return '-'
          }
        }
      }
    },
    {
      prop: 'skuCode',
      label: '产品编号',
      isCopy: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'cskuCode',
      label: '客户货号',
      isCopy: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'basicSkuCode',
      label: '基础产品编号',
      isCopy: true,
      minWidth: columnWidth.l
    },
    {
      prop: 'skuName',
      label: '品名',
      minWidth: columnWidth.m
    },
    {
      prop: 'venderProdCode',
      label: '工厂货号',
      isCopy: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'planArriveDate',
      label: '到料时间',
      minWidth: columnWidth.m,
      isShowChange: true,
      formatter: (val) => {
        return formatDate(val, 'YYYY-MM-DD')
      }
      // formatter: formatDateColumn('YYYY-MM-DD')
    },
    // {
    //   prop: 'checkedQuantity',
    //   label: '已验货数量',
    //   summary: true,
    //   minWidth: '120px',
    //   isShowChange: true
    // },
    // {
    //   prop: 'receivedQuantity',
    //   label: '已收货数量',
    //   summary: true,
    //   minWidth: '120px',
    //   isShowChange: true
    // },
    {
      prop: 'unitPrice',
      label: '采购单价',
      minWidth: columnWidth.m,
      isShowChange: true,
      formatter: (val) => {
        return val?.amount ? `${val.amount} ${val.currency}` : '-'
      }
    },
    {
      prop: 'quantity',
      label: '采购数量',
      summary: true,
      minWidth: columnWidth.m,
      isShowChange: true
    },
    {
      prop: 'withTaxPrice',
      label: '采购金额（含税总计）',
      minWidth: columnWidth.xl,
      summary: true,
      isShowChange: true,
      formatter: (val) => {
        return val?.amount ? `${val.amount} ${val.currency}` : '-'
      }
    },
    {
      prop: 'purchaseType',
      label: '采购类型',
      minWidth: columnWidth.m,
      isShowChange: true,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.PURCHASE_TYPE, row)
      }
    },
    // bug 622
    // {
    //   prop: 'checkStatus',
    //   label: '验货结果',
    //   minWidth: columnWidth.m,
    //   isShowChange: true,
    //   formatter: (row) => {
    //     return getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, row)
    //   }
    // },

    {
      prop: 'invoiceStatus',
      label: '开票通知状态',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.INVOICE_NOTICE_STATUS, row?.invoiceStatus)
      }
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: columnWidth.l,
      fixed: 'right',
      parent: true,
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
                hasPermi="scm:purchase-contract-change:detail"
              >
                详情
              </el-button>
              <eplus-dropdown
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'scm:purchase-contract-change:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'scm:purchase-contract-change:update',
                //   handler: () => {
                //     handleUpdate(row.id)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'scm:purchase-contract-change:delete',
                //   handler: async (row) => {
                //     await handleDelete(row.id)
                //   }
                // }}
                otherItems={[]}
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    }
  ]
}
const message = useMessage()
const eplusDialogRef = ref()
const handleRefresh = () => {}
const handleDialogFailure = () => {}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}
const handleSubmit = async (data) => {
  // 提交请求
  let res = await changeApi.submitChange(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
