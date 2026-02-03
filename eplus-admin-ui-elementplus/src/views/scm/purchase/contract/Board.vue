<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusTableRef"
  >
    <template #tableActions> </template>
  </eplus-table>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { formatDateColumn } from '@/utils/table'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker, EplusSearchMultiInput } from '@/components/EplusSearch'
// import { BpmProcessInstanceResultEnum, PurchaseContractStatusEnum } from '@/utils/constants'
import * as UserApi from '@/api/system/user'

const eplusTableRef = ref()

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
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
      component: <eplus-user-select></eplus-user-select>,
      name: 'trackUserId',
      label: '跟单员',
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
          label: '采购合同编号'
        },
        {
          name: 'venderCode',
          label: '供应商编号'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await PurchaseContractApi.getPurchaseContractBoardPage(ps)
    return {
      list: res.list,
      total: res.total,
      sum: { quantity: res?.summary?.qtySum }
    }
  },
  summary: true,
  columns: [
    {
      prop: 'contractCode',
      label: '采购合同编号',
      minWidth: '150px',
      iscopy: true
    },
    {
      prop: 'companyName',
      label: '采购主体',
      minWidth: '150px'
    },
    {
      prop: 'venderCode',
      label: '供应商编号',
      minWidth: '150px'
    },
    {
      prop: 'venderName',
      label: '供应商名称',
      minWidth: '250px'
    },
    {
      prop: 'purchasePlanCode',
      label: '采购计划编号',
      minWidth: '150px'
    },
    {
      prop: 'sortNum',
      label: '产品序号',
      minWidth: '150px'
    },
    {
      prop: 'skuCode',
      label: '产品编号',
      minWidth: '150px'
    },

    {
      prop: 'mainPicture',
      label: '图片',
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <EplusImgEnlarge
              mainPicture={row?.mainPicture}
              thumbnail={row?.thumbnail}
            />
          )
        }
      }
    },
    {
      prop: 'skuName',
      label: '客户产品名称',
      minWidth: '150px'
    },
    {
      prop: 'cskuCode',
      label: '客户货号',
      minWidth: '150px'
    },
    {
      prop: 'purchaseUserName',
      label: '采购员',
      minWidth: '150px'
    },
    {
      prop: 'quantity',
      label: '采购量',
      minWidth: '150px'
    },
    {
      prop: 'trackUserName',
      label: '跟单员',
      minWidth: '150px'
    },
    {
      prop: 'wmsNames',
      label: '仓库',
      minWidth: '150px'
    },
    {
      prop: 'receivedQuantity',
      label: '到货量',
      minWidth: '150px'
    },
    {
      prop: 'unReceivedQuantity',
      label: '待到货量',
      minWidth: '150px',
      formatter: (row) => {
        return row.quantity - row.receivedQuantity
      }
    },
    {
      prop: 'checkedQuantity',
      label: '验货量',
      minWidth: '150px'
    },
    {
      prop: 'unCheckedQuantity',
      label: '待验货量',
      minWidth: '150px',
      formatter: (row) => {
        return row.quantity - row.checkedQuantity
      }
    },
    {
      prop: 'returnQuantity',
      label: '退货量',
      minWidth: '150px'
    },
    {
      prop: 'exchangeQuantity',
      label: '换货量',
      minWidth: '150px'
    },

    {
      prop: 'resultDiffQuantity',
      label: '结束差异值',
      minWidth: '150px',
      formatter: (row) => {
        var resultDiffQuantity = row.quantity - row.checkedQuantity
        return resultDiffQuantity
      }
    },
    {
      prop: 'overQuantity',
      label: '超量收货',
      minWidth: '150px',
      formatter: (row) => {
        var overQuantity = row.checkedQuantity - row.quantity
        return overQuantity < 0 ? 0 : overQuantity
      }
    },

    {
      prop: 'unitPrice',
      label: '单价',
      minWidth: '150px',
      slots: {
        default: (data) => {
          const { row } = data
          return row.unitPrice ? row.unitPrice.amount + ' ' + row.unitPrice.currency : ''
        }
      }
    },
    {
      prop: 'receivedToatl',
      label: '到货金额',
      minWidth: '150px',
      slots: {
        default: (data) => {
          const { row } = data
          if (row.receivedQuantity && row.unitPrice) {
            return row.unitPrice.amount * row.receivedQuantity + ' ' + row.unitPrice.currency
          }
        }
      }
    },
    {
      prop: 'unReceivedToatl',
      label: '待到货金额',
      minWidth: '150px',
      slots: {
        default: (data) => {
          const { row } = data
          if (row.receivedQuantity && row.unitPrice && row.quantity) {
            return (
              row.unitPrice.amount * (row.quantity - row.receivedQuantity) +
              ' ' +
              row.unitPrice.currency
            )
          }
        }
      }
    },
    {
      prop: 'contractOtherCost',
      label: '退货扣款金额',
      minWidth: '150px',
      slots: {
        default: (data) => {
          const { row } = data
          if (row.returnQuantity && row.unitPrice) {
            return row.unitPrice.amount * row.returnQuantity + ' ' + row.unitPrice.currency
          }
        }
      }
    },
    {
      prop: 'plannedArrivalTime',
      label: '预计到货时间',
      minWidth: '150px',
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'planArriveDate',
      label: '到料时间',
      minWidth: '150px',
      formatter: formatDateColumn('YYYY-MM-DD')
    }
  ],
  spanMethod: ({ row, column, rowIndex, columnIndex }: any) => {
    if (rowIndex === 0) return { rowspan: 1, colspan: 4 }
    return { rowspan: 1, colspan: 1 }
  }
}
</script>
<style lang="scss" scoped></style>
