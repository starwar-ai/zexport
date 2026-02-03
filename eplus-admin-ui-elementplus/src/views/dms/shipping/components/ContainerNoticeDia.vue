<template>
  <Dialog
    v-model="dialogTableVisible"
    title="转拉柜通知单"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <eplus-table
        :eplusTableSchema="eplusTableSchema"
        ref="eplusTableRef"
        key="eplusTable"
        :height="400"
      />
    </div>
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
import * as ContainerNoticeApi from '@/api/dms/containerNotice'

defineOptions({ name: 'SettlementFormDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
const TableRef = ref()
const tableList = ref([])

const open = async (list = []) => {
  tableList.value = list.map((item) => ({ ...item, quantity: item.shippingQuantity }))
  dialogTableVisible.value = true
  nextTick(() => {
    TableRef.value?.handleAllToggleRowSelection()
  })
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

const handleSure = async () => {
  let paramsList = TableRef.value.getSelectedRows()
  if (paramsList.length == 0) {
    message.warning('请选择需要转结汇的记录')
    return
  }
  var res = await ContainerNoticeApi.toSettlement({
    settlementFormList: paramsList.map((item) => {
      return {
        id: item.id,
        quantity: item.quantity
      }
    })
  })
  message.success('操作成功')
  handleCancel()
  emit('sure')
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
