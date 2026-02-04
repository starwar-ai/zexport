<template>
  <el-date-picker
    v-model="saleTime"
    type="daterange"
    range-separator="To"
    start-placeholder="开始时间"
    end-placeholder="结束时间"
    class="mb15px"
    :clearable="false"
    @change="getList"
    value-format="YYYY-MM-DD 00:00:00"
  />
  <Table
    style="width: 100%"
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
  />
</template>

<script lang="tsx" setup>
import { ref } from 'vue'
import { columnWidth, formatDateColumn } from '@/utils/table'
import * as skuApi from '@/api/pms/main/index'
import { formatDate } from '@/utils/formatTime'
import { getOuterbox } from '@/utils/outboxSpec'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'

const props = defineProps<{
  code?: string
}>()
let st = formatDate(Date.now() - 1000 * 60 * 60 * 24 * 90)
let et = formatDate(Date.now())

const saleTime = ref([st, et])
let tableList = ref([])
const tableColumns = [
  {
    label: '客户货号',
    field: 'cskuCode'
  },
  {
    label: '客户名称',
    field: 'custName'
  },
  {
    label: '客户国别',
    field: 'custCountryName'
  },
  {
    label: '中文品名',
    field: 'skuName'
  },
  {
    label: '成交价',
    field: 'tradePrice',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.tradePrice.amount} ${row.tradePrice.currency}`
      }
    }
  },
  {
    label: '销售数量',
    field: 'quantity'
  },
  {
    label: '采购价',
    field: 'purchasePrice',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.purchasePrice.amount} ${row.purchasePrice.currency}`
      }
    }
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: '100',
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.xl,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'netweight')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    label: '销售合同',
    field: 'saleContractCode'
  },
  {
    label: '销售时间',
    field: 'saleTime',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
    width: 180
  }
  // 采购合同
  // purchaseContractCode
  // 采购时间
  // purchaseTime
]
const getList = async () => {
  if (props.code) {
    tableList.value = await skuApi.historyTradePrice({
      skuCode: props.code,
      saleTime: saleTime.value
    })
  }
}
watchEffect(async () => {
  getList()
})
</script>
