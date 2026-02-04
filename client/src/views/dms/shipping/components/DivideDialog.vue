<template>
  <Dialog
    v-model="dialogTableVisible"
    v-if="dialogTableVisible"
    title="拆分明细"
    width="500"
    append-to-body
    destroy-on-close
  >
    <div class="center">产品名称：{{ dataRef[1].name }}</div>
    <div class="center">出运数量：{{ shippingQuantityRef }}</div>
    <div class="center"
      >拆分数量：<el-input-number
        v-model="dataRef[1].shippingQuantity"
        class="!w-50%"
        :controls="false"
        :min="1"
        :max="maximumDivide"
    /></div>
    <div class="tip">{{
      `提示：可拆分数量为${maximumDivide}（已报关数量：${formatter(dataRef[0]?.declarationQuantityOld)}、已结汇数量：${formatter(dataRef[0]?.settlementQuantity)}、已开票通知数量：${formatter(dataRef[0]?.invoicedQuantity)}）`
    }}</div>
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
import * as ShipmentApi from '@/api/dms/shipment/index'

const message = useMessage()
defineOptions({ name: 'DivideDialog' })
const dialogTableVisible = ref(false)
const tableType = ref()
const shippingQuantityRef = ref()
const dataRef = ref([])
const maximumDivide = ref()
const emit = defineEmits<{
  sure
}>()
const props = defineProps<{
  detailInfo: any
}>()
const formatter = (val) => {
  if (val === '-' || val === undefined || val === null || !val) {
    return 0
  } else {
    return Number(val)
  }
}
const open = async (row: any) => {
  try {
    const maximum = Math.max(
      formatter(row?.declarationQuantityOld),
      formatter(row?.settlementQuantity),
      formatter(row?.invoicedQuantity)
    )
    maximumDivide.value = Number(row?.shippingQuantity) - maximum
    if (maximumDivide.value <= 0) {
      message.warning('无可拆分数量')
      return false
    }
    shippingQuantityRef.value = row?.shippingQuantity
    dataRef.value = await ShipmentApi.getSplitList(row?.id)
    dataRef.value[1].shippingQuantity = maximumDivide.value
    dialogTableVisible.value = true
  } catch (e) {
    console.log(e)
  }
}
const handleCancel = () => {
  dialogTableVisible.value = false
  tableType.value = undefined
}

const handleSure = async () => {
  try {
    if (dataRef.value[1].shippingQuantity === shippingQuantityRef.value) {
      message.warning('不可以全部拆分')
      return false
    }
    emit('sure', dataRef.value)
    handleCancel()
  } catch (e) {
    console.log(e, 'e')
    message.error('转单失败')
  }
}
defineExpose({ open })
</script>

<style lang="scss" scoped>
.tip {
  font-size: 12px;
  color: #409eff;
}

.center {
  width: 70%;
  margin: 5px auto;
}
</style>
