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
import * as ShippingPlanApi from '@/api/dms/shippingPlan/index'

defineOptions({ name: 'ForwarderCompanyInfoForm' })
const message = useMessage()
const dialogTableVisible = ref(false)
const loading = ref(false)
const diaType = ref('')
const emit = defineEmits<{
  sure: [link: string]
}>()
const FormRef = ref()
let form = reactive({
  parentId: undefined,
  itemId: undefined,
  remark: ''
})

const open = async (obj = {}) => {
  console.log(obj)
  form.parentId = obj.parentId || undefined
  form.itemId = obj.itemId || undefined
  form.remark = ''
  dialogTableVisible.value = true
}

const handleCancel = () => {
  FormRef.value.resetFields()
  dialogTableVisible.value = false
}
const handleSure = async () => {
  await FormRef.value.validate((valid, fields) => {
    if (valid) {
      ShippingPlanApi.closeShipmentPlan(form).then((res) => {
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
