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
    <!-- <template #edit="{ key }">
      <export-sale-contract-change-form
        :id="key"
        mode="edit"
      />
    </template> -->

    <template #edit="{ key, param }">
      <ExportSaleContractForm
        :type="param"
        :changeEdit="true"
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <export-sale-contract-change-detail
        :id="key"
        :type="1"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as ExportSaleContractChangeApi from '@/api/sms/saleContract/export/change'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import * as CommonApi from '@/api/common'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import ExportSaleContractChangeDetail from './ExportSaleContractChangeDetail.vue'
import ExportSaleContractForm from '../exportSaleContract/ExportSaleContractForm.vue'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { DICT_TYPE, getDictValue } from '@/utils/dict'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'ExportSaleContractChange' })
const saleType = getDictValue(DICT_TYPE.SALE_TYPE, '外销合同')
const eplusListRef = ref()
const exportFileName = ref()
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
      component: <EplusSearchMultiInput />,
      subfields: [
        {
          name: 'code',
          label: '变更单号'
        },
        {
          name: 'contractCode',
          label: '外销单号'
        }
      ]
    },
    {
      component: <EplusSearchMultiInput />,
      subfields: [
        {
          name: 'custName',
          label: '客户名称'
        },
        {
          name: 'custCode',
          label: '客户编号'
        }
      ]
    },
    {
      component: (
        <eplus-custom-select
          api={CommonApi.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择下单主体"
          style="width:100%"
        />
      ),
      name: 'companyId',
      label: '下单主体',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const companyList = await CommonApi.getcompanySimpleList()
          const company = companyList.find((item) => item.id === args[0])
          return company ? company.name : ''
        }
        return ''
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'salesUserId',
      label: '业务员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'salesDeptId',
      label: '部门',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const dept = await DeptApi.getSimpleDept(args[0])
          return dept.name
        }
        return ''
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'sourceCode',
      label: '销售合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <EplusSearchMultiInput />,
      subfields: [
        {
          name: 'name',
          label: '中文品名'
        },
        {
          name: 'nameEng',
          label: '英文品名'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ExportSaleContractChangeApi.getChangePage({ ...ps, saleType: saleType })
    let resultList = res.list?.map((item) => {
      item.changeFlag = false
      item.oldData?.children?.forEach((e) => {
        // e.unitPrice.amount = e.unitPrice?.amount.toFixed(2)
        e.purchaseTotalAmount =
          e.purchaseWithTaxPrice?.amount && e.needPurQuantity
            ? {
                amount: e.needPurQuantity * e.purchaseWithTaxPrice?.amount || 0,
                currency: e.purchaseWithTaxPrice?.currency
                // ? e.purchaseWithTaxPrice?.currency
                // : 'RMB'
              }
            : '0'
      })
      item.children
        ?.filter((item1) => item1?.changeFlag === 3)
        ?.forEach((item2) => {
          item2.oldChildren = item.oldData?.children?.filter((item3) => item3?.id === item2?.id)
          // item2.unitPrice.amount = item2.unitPrice?.amount.toFixed(2)
          item2.purchaseTotalAmount =
            item2.purchaseWithTaxPrice?.amount && item2.needPurQuantity
              ? {
                  amount: item2.needPurQuantity * item2.purchaseWithTaxPrice?.amount || 0,
                  currency: item2.purchaseWithTaxPrice?.currency
                  // ? item2.purchaseWithTaxPrice?.currency
                  // : 'RMB'
                }
              : '0'
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
    await ExportSaleContractChangeApi.deleteChange(id)
  },
  //**是否包含多个表格显示 */
  showTabs: false,
  oldChangeField: 'oldChildren',
  columns: [
    {
      prop: 'code',
      label: '外销合同编号',
      isCopy: true,
      parent: true,
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '下单主体',
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'custCode',
      label: '客户编号',
      isCopy: true,
      parent: true,
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="custCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'custName',
      label: '客户名称',
      parent: true,
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="custName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    // {
    //   prop: 'skuCode',
    //   label: '产品编码',
    //   minWidth: columnWidth.xxl,
    //   formatter: (row) => {
    //     return (
    //       <>
    //         <EplusFieldComparison
    //           filed="skuCode"
    //           oldChangeField="oldData"
    //           item={row}
    //         />
    //       </>
    //     )
    //   }
    // },
    {
      prop: 'cskuCode',
      label: '客户货号',
      minWidth: columnWidth.xxl,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="cskuCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'basicSkuCode',
      label: '基础产品编码',
      minWidth: columnWidth.xxl,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="basicSkuCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'inputDate',
      label: '录入日期',
      parent: true,
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'auditStatus',
      label: '变更状态',
      parent: true,
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
      parent: true,
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },

    {
      prop: 'mainPicture',
      label: '图片',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.changeFlag === 4) {
            return '-'
          } else {
            return (
              <EplusImgEnlarge
                mainPicture={row?.mainPicture}
                thumbnail={row?.thumbnail}
              />
            )
          }
        }
      }
    },
    {
      prop: 'name',
      label: '中文名称',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="name"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'nameEng',
      label: '英文名',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="nameEng"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'quantity',
      label: '数量',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="quantity"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'inventoryQuantity',
      label: '库存',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="inventoryQuantity"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'needPurQuantity',
      label: '待采购',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="needPurQuantity"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'unitPrice',
      label: '单价',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="unitPrice"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'totalSaleAmount',
      label: '总金额',
      minWidth: columnWidth.m,
      // formatter: formatMoneyColumn()
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <EplusFieldComparison
              filed="totalSaleAmount"
              oldChangeField="oldData"
              item={row}
            />
          )
        }
      }
    },
    {
      prop: 'purchaseWithTaxPrice',
      label: '采购单价',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <EplusFieldComparison
              filed="purchaseWithTaxPrice"
              oldChangeField="oldData"
              item={row}
            />
          )
        }
      }
    },
    {
      prop: 'purchaseTotalAmount',
      label: '采购总金额',
      minWidth: columnWidth.m,
      isCompare: true,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <EplusFieldComparison
              filed="purchaseTotalAmount"
              oldChangeField="oldData"
              item={row}
            />
          )
        }
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
                hasPermi="sms:export-sale-contract-change:detail"
              >
                详情
              </el-button>
              <eplus-dropdown
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'sms:export-sale-contract-change:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'sms:export-sale-contract-change:update',
                //   handler: () => {
                //     handleUpdate(row.id)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'sms:export-sale-contract-change:delete',
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
const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const handleDialogFailure = () => {}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '外销变更编辑', 'change')
}

const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}
const handleSubmit = async (data) => {
  // 提交请求
  let res = await ExportSaleContractChangeApi.submitChange(data.id)
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
