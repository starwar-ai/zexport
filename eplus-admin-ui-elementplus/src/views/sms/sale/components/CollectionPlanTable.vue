<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { TableColumn } from '@/types/table'
import { formatDate } from '@/utils/formatTime'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { formatterPrice } from '@/utils'

defineOptions({ name: 'CostAttrsCom' })
const props = defineProps<{
  formData
  type: string
  loading?: boolean
}>()
const emit = defineEmits(['loaded'])
const TableRef = ref()
const message = useMessage()
const tableList = ref([])
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'step',
    label: '步骤',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, row.step)
    }
  },
  {
    field: 'paymentMethod',
    label: '支付方式',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.PAY_METHOD, row.paymentMethod)
    }
  },
  {
    field: 'dateType',
    label: '起始点',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.SETTLEMENT_DATE_TYPE, row.dateType)
    }
  },
  {
    field: 'startDate',
    label: '起始日',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.startDate) {
        return formatDate(row.startDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'days',
    label: '天数',
    width: '150px',
    component: (
      <el-input-number
        min={0}
        precision={0}
        controls={false}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入天数' }]
  },
  {
    field: 'expectedReceiptDate',
    label: '预计收款日',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.expectedReceiptDate) {
        return formatDate(row.expectedReceiptDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'collectionRatio',
    width: '150px',
    label: '收款比例%'
  },
  {
    field: 'realCollectionRatio',
    width: '150px',
    label: '实际收款比例%'
  },
  {
    field: 'receivableAmount',
    label: '应收金额',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <div>
          <EplusMoneyLabel val={row.receivableAmount} />
        </div>
      )
    }
  },
  {
    field: 'receivedAmount',
    label: '实收金额',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <div>
          <EplusMoneyLabel val={row.receivedAmount} />
        </div>
      )
    }
  },
  {
    field: 'controlPurchaseFlag',
    label: '是否控制采购',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row.controlPurchaseFlag)
    }
  },
  {
    field: 'controlShipmentFlag',
    label: '是否控制出运',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row.controlShipmentFlag)
    }
  }
])

const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const tableListExtend = computed(() => {
  let tableData = cloneDeep(TableRef.value.tableData)
  let arr = tableData.map((item, index) => {
    return {
      ...item
    }
  })
  return arr
})

const checkData = async (submitFlag?) => {
  // tableList.value.forEach((item: any) => {
  //   item?.id && delete item?.id
  // })
  if (tableList.value?.length) {
    let valid = await TableRef.value.validate()
    if (valid || !submitFlag) {
      let tableData = cloneDeep(TableRef.value.tableData)
      let arr = tableData.map((item, index) => {
        return {
          ...item
        }
      })
      return arr
    } else {
      message.warning('收款计划提交有误')
      return false
    }
  } else {
    message.warning('缺少收款计划信息')
    return false
  }
}
//更新table值
const resetItems = () => {
  let totalGoodsValue = props.formData?.collectionTotal
  let executed = tableList.value.filter((item: any) => item.exeStatus === 1)
  let receivedAmountValue = executed.reduce((acc, item: any) => {
    return acc + Number(item.receivedAmount?.amount || 0)
  }, 0)
  let notExecuted = tableList.value.filter((item: any) => item.exeStatus !== 1)
  let totalRatio = notExecuted.reduce((acc, item: any) => {
    return acc + Number(item.collectionRatio)
  }, 0)
  let pendingAmount = Number(totalGoodsValue?.amount) - (receivedAmountValue || 0)
  // tableList.value.filter(item => item.exeStatus !== 1)
  if (pendingAmount) {
    tableList.value?.forEach((item: any) => {
      if (item.exeStatus === 0 && item.collectionRatio) {
        if (!item.receivableAmount) {
          item.receivableAmount = {
            amount: '0',
            currency: totalGoodsValue?.currency
          }
        }
        item.receivableAmount.amount =
          (Number(item.collectionRatio) * Number(pendingAmount)) / totalRatio
        item.receivableAmount.currency = totalGoodsValue?.currency
      } else {
        item.realCollectionRatio = formatterPrice(
          (Number(item.receivedAmount?.amount) / Number(totalGoodsValue?.amount)) * 100,
          2
        )
      }
    })
  }
}
const setScorllBar = () => {
  TableRef.value.setScorllBar()
}

const resetTable = () => {
  tableList.value = []
}
defineExpose({
  selectedList,
  tableList,
  tableListExtend,
  checkData,
  resetItems,
  setScorllBar,
  resetTable
})
const hasLoadedData = ref(false)
const isUpdating = ref(false)
watch(
  () => props.formData.collectionPlanList,
  async (newList) => {
    console.log('[CollectionPlanTable] 数据watch触发, 数据长度:', newList?.length, 'isUpdating:', isUpdating.value, 'hasLoadedData:', hasLoadedData.value)
    if (isUpdating.value) return
    if (isValidArray(newList)) {
      isUpdating.value = true
      try {
        let tableData = cloneDeep(newList)
        tableList.value = tableData.map((item, index) => {
          return {
            ...item
          }
        })
        resetItems()
        await nextTick()
        console.log('[CollectionPlanTable] 从数据watch触发loaded事件')
        hasLoadedData.value = true
        emit('loaded')
      } finally {
        isUpdating.value = false
      }
    } else {
      tableList.value = []
      // 只在第一次已有数据的情况下才emit loaded，避免初始化时过早触发
      if (hasLoadedData.value) {
        console.log('[CollectionPlanTable] 数据变空，从数据watch触发loaded事件')
        emit('loaded')
      }
    }
  },
  { immediate: true }
)
// 监听父组件loading状态变化，当父组件数据加载完成且本组件数据为空时触发loaded
watch(
  () => props.loading,
  async (newVal, oldVal) => {
    console.log('[CollectionPlanTable] loading变化:', oldVal, '->', newVal, 'hasLoadedData:', hasLoadedData.value, 'isUpdating:', isUpdating.value)
    if (oldVal === true && newVal === false && !hasLoadedData.value && !isUpdating.value) {
      await nextTick()
      if (!hasLoadedData.value && !isUpdating.value) {
        console.log('[CollectionPlanTable] 从loading watch触发loaded事件')
        hasLoadedData.value = true
        emit('loaded')
      }
    }
  }
)
onMounted(async () => {
  console.log('[CollectionPlanTable] onMounted, loading:', props.loading, 'hasLoadedData:', hasLoadedData.value, 'isUpdating:', isUpdating.value)
  // 新增模式下数组为空，watchEffect不会触发loaded事件，需要在mounted时触发
  // 只有在父组件数据已加载完成时才触发
  await nextTick()
  if (!props.loading && !hasLoadedData.value && !isUpdating.value) {
    console.log('[CollectionPlanTable] 从onMounted触发loaded事件')
    hasLoadedData.value = true
    emit('loaded')
  }
})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: rgba(0, 0, 0, 0);
}

.total-header {
  width: 100%;
}
</style>
