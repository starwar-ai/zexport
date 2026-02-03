<template>
  <div class="!w-80%">
    <Dialog
      v-model="dialogVisible"
      append-to="#appView"
      :destroy-on-close="true"
      title="录入差异原因"
      width="50%"
      append-to-body
    >
      <div class="mb15px flex items-center">
        <el-button @click="handleAdd">添加行 </el-button>
        <span class="ml20px">差异总金额：{{ differenceAmount }}</span>
      </div>
      <eplus-form-table
        ref="TableRef"
        :list="tableList"
        :defaultVal="{}"
        :schema="BasicInfoSchema"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleSure"
          >
            确认
          </el-button>
          <el-button @click="close">取消</el-button>
        </div>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="tsx">
import { formatterPrice } from '@/utils'
import { DICT_TYPE } from '@/utils/dict'
import { isValidArray } from '@/utils/is'

const message = useMessage()
const TableRef = ref()

let tableList = ref([])
const formData = ref({})
provide('formData', formData)
const emit = defineEmits(['success'])
const dialogVisible = ref<boolean>(false)
let BasicInfoSchema = [
  {
    field: 'differenceType',
    label: '差异原因',
    width: 180,
    isShow: true,
    showOverflowTooltip: false,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.differenceType}
          dictType={DICT_TYPE.DIFFERENCE_TYPE}
          style="width:100%"
          disabled={row.his}
        />
      )
    },
    rules: { required: true, message: '请选择差异原因' }
  },
  {
    field: 'differenceAmount',
    label: '差异金额',
    width: 180,
    isShow: true,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.differenceAmount}
          controls={false}
          precision={3}
          class="!w-90%"
          disabled={row.his}
        />
      )
    },
    rules: {
      required: true,
      validator: (rule, value, callback) => {
        if (value === 0) {
          callback(new Error('差异金额不能为0！'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'remark',
    label: '备注',
    isShow: true,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input
          v-model={row.remark}
          disabled={row.his}
        />
      )
    }
  },
  {
    label: '操作',
    width: 100,
    fixed: 'right',
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => deleteAction(index)}
            disabled={row.his}
          >
            移除
          </el-button>
        </>
      )
    }
  }
]
const handleAdd = async () => {
  const newRecord = {
    differenceAmount: 0
  }
  tableList.value.push(newRecord)
}
const deleteAction = (index) => {
  tableList.value.splice(index, 1)
}
const handleSure = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    // let total = 0
    // tableList.value.forEach((item) => {
    //   total += item.differenceAmount || 0
    // })
    // if (Number(total) !== Number(differenceAmount.value)) {
    //   message.warning('表单差异金额总和与单据不一致，请重新录入差异原因！')
    //   return
    // }

    if (Number(differenceAmount.value) > Number(maxVal.value)) {
      message.warning(`差异总金额不能大于${maxVal.value}！`)
      return
    }
    emit('success', {
      tableList: tableList.value,
      indexRef,
      differenceAmount: differenceAmount.value
    })

    dialogVisible.value = false
  } else {
    message.warning('请完善表单信息')
  }
}
const close = async () => {
  emit('success', { tableList: [], indexRef })
  dialogVisible.value = false
}
const indexRef = ref()
const differenceAmount = ref()
const maxVal = ref(0)
const open = async (index, row) => {
  let history = []
  tableList.value = []
  if (isValidArray(row?.hisDifferenceReason)) {
    row?.hisDifferenceReason
    history = row?.hisDifferenceReason.map((item) => {
      return {
        ...item,
        his: true
      }
    })
  }
  if (isValidArray(row?.differenceReason)) {
    tableList.value = row?.differenceReason
  } else {
    tableList.value = [...history]
  }
  maxVal.value = formatterPrice(
    Number(row.receivableAmount) -
      Number(row.receivedAmount || 0) -
      Number(row.contractAmount || 0),
    3
  )

  dialogVisible.value = true
  indexRef.value = index
}
watch(
  () => tableList.value,
  (list) => {
    differenceAmount.value = list.reduce((total, item: any) => {
      return total + (item.differenceAmount || 0)
    }, 0)
  },
  {
    immediate: true,
    deep: true
  }
)
defineExpose({ open, close })
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-eplus-dialog;

.#{$prefix-cls} {
  :deep(.el-overlay),
  :deep(.el-overlay-dialog) {
    position: absolute;
  }

  :deep(.el-dialog) {
    display: flex;
    flex-direction: column;
  }

  :deep(.el-dialog__body) {
    flex: 1 1 auto;
    overflow: auto;
  }

  .el-form {
    padding-top: 15px;
  }
}
</style>
