<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >选择产品</el-button
    >
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <!-- <div class="d-block total-header flex justify-between">
    <span>合计</span>
    <span>总量 :{{ numberFormat(totalHeader.purchaseNum) }} </span>
  </div> -->
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
  <TransferOrderDialog
    ref="TransferOrderDialogRef"
    @sure="handleSure"
    :selectionFlag="true"
    :defaultVal="{}"
    :isShowTabs="true"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import TransferOrderDialog from './TransferOrderDialog.vue'

const props = defineProps<{
  type: string
}>()
const formData: any = inject('formData')
const TableRef = ref()
const numberFormat = (num, fixNum?) => {
  if (num) {
    if (!num === num) {
      return 0
    } else if (fixNum) {
      return Number(num).toFixed(fixNum)
    } else {
      return Number(num)
    }
  } else return 0
}
const message = useMessage()
const btnText = ref('')
const TransferOrderDialogRef = ref()
const tableList = ref([])
let tableColumns = reactive<TableColumn[]>([
  {
    label: '序号',
    field: 'sortNum',
    fixed: 'left',
    width: 80
  },
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '175px'
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'skuName',
    label: '产品名称',
    minWidth: '150px'
  },
  {
    field: 'batchCode',
    label: '批号',
    minWidth: '175px'
  },
  {
    field: 'availableQuantity',
    label: '当前可用库存',
    minWidth: '175px'
  },
  {
    field: 'transferQuantity',
    label: '拨出数量',
    minWidth: '175px',
    slot: (item, row: Recordable, index: number) => {
      return <el-input v-model={row.transferQuantity} />
    }
  },
  {
    field: 'saleContractCode',
    label: '销售订单号',
    minWidth: '175px'
  },
  {
    field: 'custCode',
    label: '客户编号',
    minWidth: '175px'
  },
  {
    field: 'custName',
    label: '客户名称',
    minWidth: '175px'
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            await delRow(index)
          }}
        >
          移除
        </el-button>
      )
    }
  }
])
watch(
  () => tableList,
  (list: any) => {
    if (list.value) {
      //避免重复监听引起总价重复累加
      list.value.forEach((item, index, arr) => {
        item.sortNum = index + 1
      })
    }
  },
  { immediate: true, deep: true }
)
const handleAdd = async () => {
  TransferOrderDialogRef.value.open(tableList.value, formData?.saleContractCode || '')
}
const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el: any) => {
      return el.sortNum
    })
    tableList.value = tableList.value.filter((item: any, index) => {
      if (!delArr.includes(item.sortNum)) {
        return item
      }
    })
    // tableList.value.forEach((item: any, index) => {
    //   return (item.sortNum = index + 1)
    // })
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
  // tableList.value.forEach((item, index) => {
  //   return (item.sortNum = index + 1)
  // })
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  if (list && list.length > 0) {
    list.forEach((item, index) => {
      //根据产品弹窗是否多选来控制序号的递增
      item.sortNum = list.length === 1 ? list.length + 1 : list.length + index + 1
    })
    tableList.value = list
  }
  //tableList.value=list
}
// const handleSelectSure = (list) => {
//   // if(list&&)
//   tableList.value.forEach((item: any) => {
//     if (item?.sortNum === list[0]?.value) {
//       return Object.assign(item, list[1])
//     }
//   })
//   // selectedQuote=list[0]
// }
const checkData = async () => {
  if (tableList.value.length == 0) {
    message.warning(`请选择${btnText.value}`)
    return false
  } else {
    let valid = await TableRef.value.validate()
    if (valid) {
      return TableRef.value.tableData
    } else {
      message.warning('产品提交信息有误')
      return false
    }
  }
}
const clearTableList = () => {
  tableList.value = []
}
defineExpose({ selectedList, tableList, checkData, clearTableList })

onMounted(async () => {})
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
