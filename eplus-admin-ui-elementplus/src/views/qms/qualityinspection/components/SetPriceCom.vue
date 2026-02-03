<template>
  <Dialog
    v-model="dialogVisible"
    title="设置验货金额"
    width="600"
    append-to-body
    destroy-on-close
    @close="handleCancel"
  >
    <el-form
      ref="priceFormRef"
      :model="priceForm"
      label-width="100"
    >
      <el-form-item
        label="验货金额"
        prop="amount.amount"
        :rules="[
          {
            required: true,
            validator: (rule: any, value: any, callback: any) => {
              let regx = /^(\d{1,11})(\.?\d{1,3})?$/
              if (value <= 0) {
                callback(new Error(`金额必须大于0`))
              } else if (!regx.test(value)) {
                callback(new Error('金额只能输入正数,最多11位整数,3位小数'))
              } else {
                callback()
              }
            }
          }
        ]"
      >
        <eplus-money
          v-model:amount="priceForm.amount.amount"
          v-model:currency="priceForm.amount.currency"
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
import { updateAmount } from '@/api/qms/qualityinspection'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'SetPriceCom' })
const message = useMessage()
const priceFormRef = ref()
const dialogVisible = ref(false)
const priceForm = reactive({
  id: undefined,
  amount: {
    amount: undefined,
    currency: 'RMB'
  }
})

const open = async (form) => {
  let data = cloneDeep(form)
  priceForm.id = data.id
  if (data.amount.amount) {
    priceForm.amount.amount = data.amount.amount
  }
  priceForm.amount.currency = data.amount.currency ? data.amount.currency : 'RMB'
  dialogVisible.value = true
}

const handleCancel = () => {
  priceFormRef.value.resetFields()
  dialogVisible.value = false
}

const emit = defineEmits(['sure'])
const handleSure = async () => {
  await priceFormRef.value.validate(async (valid) => {
    if (valid) {
      await updateAmount(priceForm)
      handleCancel()
      message.success('保存成功')
      emit('sure')
    }
  })
}
defineExpose({ open })
</script>
