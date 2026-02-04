<template>
  <Dialog
    v-model="dialogVisible"
    title="修改采购单号"
    width="600"
    append-to-body
    destroy-on-close
    @close="handleCancel"
  >
    <el-form
      ref="formDataRef"
      :model="formData"
      label-width="100"
    >
      <el-form-item
        label="采购单号"
        prop="code"
        :rules="{
          required: true,
          message: '请输入采购单号'
        }"
      >
        <el-input v-model.trim="formData.code" />
      </el-form-item>
    </el-form>
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
import { updateCode } from '@/api/scm/purchaseContract'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'SetPriceCom' })
const message = useMessage()
const formDataRef = ref()
const dialogVisible = ref(false)
const formData = reactive({
  id: undefined,
  code: undefined
})

const open = async (form) => {
  let data = cloneDeep(form)
  formData.id = data.id
  formData.code = data.code
  dialogVisible.value = true
}

const handleCancel = () => {
  formDataRef.value.resetFields()
  dialogVisible.value = false
}

const emit = defineEmits(['success'])
const handleSure = async () => {
  await formDataRef.value.validate(async (valid, fields) => {
    if (valid) {
      await updateCode(formData)
      handleCancel()
      message.success('修改成功')
      emit('success')
    }
  })
}
defineExpose({ open })
</script>
