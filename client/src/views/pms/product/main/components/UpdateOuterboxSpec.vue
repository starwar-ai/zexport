<template>
  <Dialog
    v-model="dialogVisible"
    title="更新规格"
    width="1000"
    append-to-body
    destroy-on-close
    @close="handleCancel"
  >
    <OuterboxSpec
      :formData="rowData"
      ref="OuterboxSpecRef"
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
import OuterboxSpec from './OuterboxSpec.vue'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'UpdateOuterboxSpec' })
const message = useMessage()
const dialogVisible = ref(false)
const rowData = ref({})
const rowIndex = ref(0)
const open = async (row, index) => {
  rowData.value = cloneDeep(row)
  rowIndex.value = index
  dialogVisible.value = true
}

const handleCancel = () => {
  dialogVisible.value = false
}
const OuterboxSpecRef = ref()
const emit = defineEmits(['sure'])
const handleSure = async () => {
  let res = await OuterboxSpecRef.value.checkData()
  if (res) {
    emit('sure', res, rowIndex.value)
    handleCancel()
  }
}
defineExpose({ open })
</script>
