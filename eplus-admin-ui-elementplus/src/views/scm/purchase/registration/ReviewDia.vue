<template>
  <Dialog
    v-model="dialogVisible"
    title="复核"
    width="500"
    append-to-body
    destroy-on-close
    @close="handleCancel"
  >
    <el-form
      ref="formRef"
      :model="form"
      label-width="100"
    >
      <el-form-item
        label="收票日期"
        prop="invoicedDate"
        :rules="[
          {
            required: true,
            message: '请选择收票日期'
          }
        ]"
      >
        <el-date-picker
          v-model="form.invoicedDate"
          type="date"
          placeholder="请选择收票日期"
          value-format="x"
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
import * as RegistrationApi from '@/api/scm/registration'

defineOptions({ name: 'ReviewDia' })
const message = useMessage()
const formRef = ref()
const dialogVisible = ref(false)
const form = reactive({
  ids: undefined,
  invoicedDate: Date.now()
})

const open = async (ids) => {
  form.ids = ids
  dialogVisible.value = true
}

const handleCancel = () => {
  formRef.value.resetFields()
  dialogVisible.value = false
}

const emit = defineEmits(['success'])
const handleSure = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await RegistrationApi.reviewApi(form)
      message.success('操作成功')
      emit('success')
      handleCancel()
    }
  })
}

defineExpose({ open })
</script>
