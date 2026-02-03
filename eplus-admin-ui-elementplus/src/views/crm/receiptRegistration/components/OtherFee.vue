<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加行
    </el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
  />
  <ReceiptDialog
    ref="ReceiptDialogRef"
    @success="dialogSuccess"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import ReceiptDialog from './ReceiptDialog.vue'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'OtherFee' })
const props = defineProps<{
  formData
}>()
const TableRef = ref()
const message = useMessage()
const tableList = ref([])
const custList = ref([])
const emit = defineEmits(['update'])
const setCustList = (list) => {
  custList.value = list
}
const getName = (e, row) => {
  let item = e[1].find((item) => item.payeeCode === e[0])
  if (!item?.payeeName) {
    return false
  }
  row.custName = item.payeeName
}
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'otherFeeType',
    label: '其他收费类型',
    width: '200px',
    component: <eplus-dict-select dictType={DICT_TYPE.CLAIM_OTHER_FEE_TYPE} />,
    rules: { required: true, message: '请选择其他收费类型' }
  },
  {
    field: 'custCode',
    label: '客户名称',
    width: '220px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-input-select
          v-model={row.custCode}
          dataList={custList.value}
          label="payeeName"
          value="payeeCode"
          class="!w-100%"
          onChangeEmit={(...$event) => getName($event, row)}
        />
      )
    },
    rules: { required: true, message: '请选择客户名称' }
  },
  {
    field: 'receivableAmount',
    label: '应收金额',
    width: '150px',
    // component: (
    //   <el-input-number
    //     controls={false}
    //     precision={2}
    //     class="!w-90%"
    //   />
    // ),
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.receivableAmount}
          min={0}
          controls={false}
          precision={3}
          class="!w-90%"
          onChange={() => {
            getDifferenceAmount(row)
          }}
        />
      )
    },
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (typeof value === 'number' && value <= 0) {
            callback(new Error(`应收金额必须大于0`))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'claimedAmount',
    label: '认领金额',
    width: '150px',
    showOverflowTooltip: false,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.claimedAmount}
          min={0}
          controls={false}
          precision={3}
          onChange={() => {
            getDifferenceAmount(row)
          }}
        />
      )
    },
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (typeof value === 'number' && value <= 0) {
            callback(new Error(`认领金额必须大于0`))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'currency',
    label: '币种',
    width: '100px'
  },
  {
    field: 'completedFlag',
    label: '收款完成',
    width: '150px',
    showOverflowTooltip: false,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.completedFlag}
          disabled={row?.disabledFlag}
          dictType={DICT_TYPE.CONFIRM_TYPE}
          style="width:100%"
          clearable={false}
        />
      )
    },
    rules: [
      {
        validator: (rule: any, value: any, callback: any) => {
          if (value === null) {
            callback(new Error(`请选择是否收款完成`))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'differenceAmount',
    label: '差异总金额',
    width: '150px',
    component: (
      <el-input-number
        disabled
        controls={false}
        precision={3}
        class="!w-90%"
      />
    )
  },
  {
    field: 'differenceReason',
    label: '差异原因',
    width: 100,
    slot: (item, row: Recordable, index: number) => {
      return (
        <>
          {row?.completedFlag && Number(row?.differenceAmount) !== 0 ? (
            <el-button
              link
              type="primary"
              onClick={() => {
                openReceiptDialog(index, row)
              }}
            >
              录入
            </el-button>
          ) : (
            ''
          )}
        </>
      )
    }
  },
  {
    field: 'action',
    label: '操作',
    width: 100,
    fixed: 'right',
    slot: (item, row: Recordable, index: number) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => {
              delRow(index)
            }}
          >
            移除
          </el-button>
        </>
      )
    }
  }
])
const ReceiptDialogRef = ref()
const openReceiptDialog = (index, row) => {
  ReceiptDialogRef.value.open(index, row)
}
const dialogSuccess = (val) => {
  tableList.value[val.indexRef.value].differenceReason = val.tableList
}

const getDifferenceAmount = (row) => {
  if (row.receivableAmount && row.claimedAmount) {
    row.differenceAmount = Number(row.receivableAmount) - Number(row.claimedAmount)
  }
}

watch(
  () => tableList.value,
  (list) => {
    let validList = list?.filter(
      (item: any) => item.custCode && item.receivableAmount > 0 && item.claimedAmount > 0
    )
    if (isValidArray(validList)) {
      emit('update', validList)
    }
  },
  { immediate: true, deep: true }
)

const handleAdd = async () => {
  tableList.value.push({
    type: 1,
    currency: props.formData.currency,
    completedFlag: 0,
    differenceReason: []
  })
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
}

const checkData = async () => {
  if (tableList.value.length) {
    let valid = await TableRef.value.validate()
    if (valid) {
      let tableData = cloneDeep(TableRef.value.tableData)
      return tableData
    } else {
      message.warning('其他收费信息提交有误')
      return false
    }
  } else {
    return []
  }
}

defineExpose({ tableList, checkData, setCustList })
// watchEffect(() => {
//   if (isValidArray(props.formData.payeeEntityList)) {
//     console.log(props.formData.payeeEntityList)
//     props.formData.payeeEntityList
//   }
// })
onMounted(async () => {
  setCustList(props.formData.payeeEntityList)
})
</script>
