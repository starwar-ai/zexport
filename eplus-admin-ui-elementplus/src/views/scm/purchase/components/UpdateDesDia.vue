<template>
  <Dialog
    v-model="dialogVisible"
    title="更新产品描述"
    width="600"
    append-to-body
    destroy-on-close
    @close="handleCancel"
    :closeOnClickModal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      label-width="100"
    >
      <el-form-item
        label="产品描述"
        prop="description"
        :rules="[
          {
            required: true,
            message: '请输入产品描述'
          }
        ]"
      >
        <el-input
          type="textarea"
          v-model="form.description"
          :rows="15"
        />
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
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'UpdateDesDia' })
const formRef = ref()
const dialogVisible = ref(false)
const form = reactive({
  index: undefined,
  description: undefined
})

const open = async (description, index) => {
  form.description = cloneDeep(description)
  form.index = index
  dialogVisible.value = true
}

const handleCancel = () => {
  formRef.value.resetFields()
  dialogVisible.value = false
}

const emit = defineEmits(['sure'])
const handleSure = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      emit('sure', form.description, form.index)
      handleCancel()
    }
  })
}
defineExpose({ open })
</script>
