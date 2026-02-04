<template>
  <Dialog
    v-model="dialogVisible"
    title="审 核"
    width="500"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
    >
      <el-form-item
        label="流程发起人："
        prop="formData.id"
      >
        {{ userName }}
      </el-form-item>
      <el-form-item
        label="是否通过"
        prop="isPass"
      >
        <el-radio-group v-model="formData.isPass">
          <el-radio :label="true">通过</el-radio>
          <el-radio :label="false">不通过</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        label="审核意见："
        prop="reason"
        v-if="!formData.isPass"
      >
        <el-input
          v-model="formData.reason"
          clearable
          type="textarea"
          placeholder="请输入审核意见"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        :disabled="formLoading"
        type="primary"
        @click="submitForm(formData.isPass)"
        plain
        >确 认</el-button
      >
      <!-- <el-button
        :disabled="formLoading"
        type="warning"
        @click="submitForm('不通过')"
        plain
        >不通过</el-button
      > -->
      <el-button
        @click="dialogVisible = false"
        plain
        >取 消</el-button
      >
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/inspect'

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false)
const userName = ref('')

const props = defineProps({
  approve: {
    type: Function,
    required: true
  },
  reject: {
    type: Function,
    required: true
  }
})
const formData = ref({
  id: '',
  isPass: true,
  reason: ''
})
const formRef = ref() // 表单 Ref
const formRules = ref({
  reason: [{ required: true, message: '审批意见不能为空', trigger: 'blur' }]
})
/** 打开弹窗 */
const open = async (params) => {
  dialogVisible.value = true
  resetForm()
  formData.value.id = params.processInstanceId
  userName.value = params.applyInspectorName
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

const submitForm = async () => {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (formData.value.isPass) {
      const res = await examineApprove(formData.value)
      if (res.code == 0) {
        message.success('审核成功')
        dialogVisible.value = false
        emit('success')
      } else {
        dialogVisible.value = false
      }
    } else {
      const res = await examineReject(formData.value)
      if (res.code == 0) {
        message.success('操作完成')
        emit('success')
      } else {
        dialogVisible.value = false
      }
    }

    resetForm()
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formRef.value?.resetFields()
}
</script>
