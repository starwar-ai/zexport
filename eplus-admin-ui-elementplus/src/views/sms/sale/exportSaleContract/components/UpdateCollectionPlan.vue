<template>
  <Dialog
    v-model="dialogTableVisible"
    title="修改收款计划状态"
    width="800"
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
import { updateCollectionPlanApi } from '@/api/sms/saleContract/export'

defineOptions({ name: 'UpdateCollectionPlan' })
const dialogTableVisible = ref(false)
const message = useMessage()
let tableList = ref([])
let tableColumns = reactive<TableColumn[]>([
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
      return getDictLabel(DICT_TYPE.SETTLEMENT_DATE_TYPE, row.dateType)
    }
  },
  {
    field: 'collectionRatio',
    label: '收款比例%',
    width: columnWidth.l
  },
  {
    field: 'controlPurchaseFlag',
    label: '是否控制采购',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.controlPurchaseFlag}
          dictType={DICT_TYPE.IS_INT}
          clearable={false}
        />
      )
    },
    rules: [{ required: true, message: '请选择是否控制采购' }]
  },
  {
    field: 'controlShipmentFlag',
    label: '是否控制出运',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.controlShipmentFlag}
          dictType={DICT_TYPE.IS_INT}
          clearable={false}
        />
      )
    },
    rules: [{ required: true, message: '请选择是否控制出运' }]
  }
])
const apiType = ref('')
const open = async (data, type) => {
  tableList.value = data
  apiType.value = type == 1 ? 'export' : type == 2 ? 'domestic' : 'factory'
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
  await updateCollectionPlanApi({ collectionPlanList: tableList.value }, apiType.value)
  message.success('修改成功')
  emit('success')
  handleCancel()
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
