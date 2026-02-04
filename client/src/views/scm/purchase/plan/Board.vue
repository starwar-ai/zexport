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
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker, EplusSearchMultiInput } from '@/components/EplusSearch'
import { formatDateColumn } from '@/utils/table'
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
          label: '采购计划编号'
        },
        {
          name: 'custCode',
          label: '客户编号'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await PurchasePlanApi.getPurchasePlanPageBoard(ps)
    return {
      list: res.list,
      total: res.total,
      sum: { quantity: res?.summary?.qtySum }
    }
  },
  summary: true,
  columns: [
    {
      prop: 'code',
      label: '计划编号',
      minWidth: '150px'
    },
    {
      prop: 'companyName',
      label: '采购主体',
      minWidth: '150px'
    },
    {
      prop: 'custCode',
      label: '客户编号',
      minWidth: '150px'
    },
    {
      prop: 'custName',
      label: '客户名称',
      minWidth: '150px'
    },
    {
      prop: 'creatorName',
      label: '计划创建人',
      minWidth: '150px'
    },
    {
      prop: 'planStatus',
      label: '计划状态',
      minWidth: '150px'
    },
    {
      prop: 'code',
      label: '计划编号',
      minWidth: '150px'
    },
    {
      prop: 'sortNum',
      label: '产品序号',
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
      label: '客户产品品名',
      minWidth: '150px'
    },
    {
      prop: 'cskuCode',
      label: '客户货号',
      minWidth: '150px'
    },
    {
      prop: 'contractCode',
      label: '采购合同编号',
      minWidth: '150px'
    },
    {
      prop: 'purchaseUserName',
      label: '采购员',
      minWidth: '150px'
    },
    {
      prop: 'quantity',
      label: '计划采购量',
      minWidth: '150px'
    },
    {
      prop: 'contractQuantity',
      label: '采购量',
      minWidth: '150px'
    },
    {
      prop: 'contractReceivedQuantity',
      label: '收货量',
      minWidth: '150px'
    },
    {
      prop: 'contractCheckedQuantity',
      label: '验货量',
      minWidth: '150px'
    },
    {
      prop: 'contractReturnedQuantity',
      label: '退货量',
      minWidth: '150px'
    },
    {
      prop: 'contractExchangedQuantity',
      label: '换货量',
      minWidth: '150px'
    },
    {
      prop: 'contractCreateTime',
      label: '下单时间',
      minWidth: '220px',
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    },
    {
      prop: 'planDate',
      label: '计划交期',
      minWidth: '150px',
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'contractFreight',
      label: '运费',
      minWidth: '150px',
      slots: {
        default: (data) => {
          const { row } = data
          return row.contractFreight
            ? row.contractFreight.amount + ' ' + row.contractFreight.currency
            : ''
        }
      }
    },
    {
      prop: 'contractOtherCost',
      label: '其他费用',
      minWidth: '150px',
      slots: {
        default: (data) => {
          const { row } = data
          return row.contractOtherCost
            ? row.contractOtherCost.amount + ' ' + row.contractOtherCost.currency
            : ''
        }
      }
    }
  ],
  spanMethod: ({ row, column, rowIndex, columnIndex }: any) => {
    if (rowIndex === 0) return { rowspan: 1, colspan: 4 }
    return { rowspan: 1, colspan: 1 }
  }
}
</script>
<style lang="scss" scoped></style>
