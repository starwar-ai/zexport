<template>
  <Dialog
    v-model="dialogTableVisible"
    title="修改付款计划状态"
    width="600"
    append-to-body
    destroy-on-close
  >
    <eplus-form-table
      ref="TableRef"
      :list="tableList"
      :defaultVal="{}"
      :schema="tableColumns"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { getDictLabel } from '@/utils/dict'
import { columnWidth } from '@/utils/table'
import { updatePaymentPlan } from '@/api/scm/purchaseContract'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'UpdatePaymentPlan' })
const dialogTableVisible = ref(false)
const message = useMessage()
let tableList = ref([])
let tableColumns = reactive([
  {
    field: 'step',
    label: '步骤',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, row.step)
    }
  },
  {
    field: 'dateType',
    label: '起始点',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.DATE_TYPE, row.dateType)
    }
  },
  {
    field: 'paymentRatio',
    label: '付款比例%',
    width: columnWidth.l
  },
  {
    field: 'controlInvoiceFlag',
    label: '是否控制登票',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.controlInvoiceFlag}
          dictType={DICT_TYPE.IS_INT}
          clearable={false}
        />
      )
    },
    rules: [{ required: true, message: '请选择是否控制登票' }]
  }
])
const open = async (data) => {
  tableList.value = cloneDeep(data.purchasePaymentPlanList)
  dialogTableVisible.value = true
}

const handleCancel = () => {
  tableList.value = []
  dialogTableVisible.value = false
}
const emit = defineEmits<{
  success
}>()
const handleSure = async () => {
  await updatePaymentPlan({ paymentPlanList: tableList.value })
  message.success('修改成功')
  emit('success')
  handleCancel()
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
