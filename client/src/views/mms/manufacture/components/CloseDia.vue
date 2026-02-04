<template>
  <Dialog
    v-model="dialogTableVisible"
    title="作废"
    width="600"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <el-form
        ref="FormRef"
        :model="form"
        label-width="120px"
      >
        <el-form-item
          label="作废原因"
          prop="remark"
          :rules="[
            {
              required: true,
              message: '请输入作废原因',
              trigger: 'blur'
            }
          ]"
        >
          <el-input
            type="textarea"
            v-model="form.remark"
            autocomplete="off"
          />
        </el-form-item>
      </el-form>
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
import * as ManufactureApi from '@/api/mms/manufacture/index'

defineOptions({ name: 'ForwarderCompanyInfoForm' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
const FormRef = ref()
let form = reactive({
  type: '',
  id: undefined,
  desc: ''
})

const open = async (type, id) => {
  form.type = type
  form.id = id || undefined
  form.desc = ''
  dialogTableVisible.value = true
}

const handleCancel = () => {
  FormRef.value.resetFields()
  dialogTableVisible.value = false
}
const handleSure = async () => {
  await FormRef.value.validate((valid, fields) => {
    if (valid) {
      ManufactureApi.handleManufacture(form.type, { id: form.id, desc: form.desc }).then((res) => {
        message.success('操作成功')
        emit('sure')
        handleCancel()
      })
    }
  })
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
