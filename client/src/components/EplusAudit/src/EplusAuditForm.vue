<template>
  <Dialog
    v-model="dialogVisible"
    :title="title"
    width="500"
    append-to-body
    ref="bodyDialogRef"
    class="bodyDialog"
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
      <template v-if="!delegateFlag">
        <el-form-item
          label="是否通过"
          prop="isPass"
          v-if="!delegateFlag"
        >
          <el-radio-group v-model="formData.isPass">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">不通过</el-radio>
            <el-radio :label="3">转派</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="审核意见："
          prop="reason"
          v-if="formData.isPass === 1 || formData.isPass === 2"
        >
          <el-input
            v-model="formData.reason"
            clearable
            type="textarea"
            placeholder="请输入审核意见"
          />
        </el-form-item>
        <el-form-item
          label="被转派人："
          prop="assigneeUserId"
          v-if="formData.isPass === 3"
        >
          <eplus-user-select v-model="formData.assigneeUserId" />
        </el-form-item>
        <el-form-item
          label="转派原因："
          prop="reason"
          v-if="formData.isPass === 3"
        >
          <el-input
            v-model="formData.reason"
            clearable
            type="textarea"
            placeholder="请输入转派原因"
          />
        </el-form-item>
      </template>
      <template v-if="delegateFlag">
        <el-form-item
          label="被转派人："
          prop="assigneeUserId"
        >
          <eplus-user-select v-model="formData.assigneeUserId" />
        </el-form-item>
      </template>
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
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false)
const userName = ref('')
import { delegateTask, transferTask } from '@/api/bpm/task'

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
  isPass: 1,
  reason: '',
  ids: [],
  delegateUserId: '',
  assigneeUserId: ''
})
const title = ref('')
const delegateFlag = ref(false)
const bodyDialogRef = ref() // 弹窗 Ref
const formRef = ref() // 表单 Ref
const formRules = computed(() => ({
  reason: [{ required: formData.value.isPass !== 1, message: '此项不能为空', trigger: 'blur' }],
  delegateUserId: [{ required: true, message: '此项不能为空', trigger: 'change' }],
  assigneeUserId: [{ required: true, message: '此项不能为空', trigger: 'change' }]
}))
/** 打开弹窗 */
const open = async (params) => {
  dialogVisible.value = true
  resetForm()
  if (params.ids) {
    formData.value.ids = params.ids
  } else {
    formData.value.id = params.id
  }
  userName.value = params.createName
  title.value = params.title || '审 核'
  delegateFlag.value = params.delegateFlag || false
  if (delegateFlag.value) {
    formData.value.isPass = 3
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

const submitForm = async (clickType) => {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (delegateFlag.value) {
      const res = await transferTask(formData.value)
      if (res.code == 0) {
        message.success('转派成功')
        dialogVisible.value = false
        emit('success')
      } else {
        dialogVisible.value = false
      }
    } else {
      if (clickType === 1) {
        const res = await props.approve(formData.value)
        if (res.code == 0) {
          message.success('审核成功')
          dialogVisible.value = false
          emit('success')
        } else {
          dialogVisible.value = false
        }
      } else if (clickType === 2) {
        const res = await props.reject(formData.value)
        if (res.code == 0) {
          message.success('操作完成')
          emit('success')
        } else {
          dialogVisible.value = false
        }
      } else if (clickType === 3) {
        const res = await delegateTask(formData.value)
        if (res.code == 0) {
          message.success('转派成功')
          emit('success')
        } else {
          dialogVisible.value = false
        }
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
