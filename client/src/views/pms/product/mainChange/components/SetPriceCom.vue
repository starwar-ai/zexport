<template>
  <Dialog
    v-model="dialogVisible"
    title="设置定价差价"
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
        label="产品价格"
        prop="jsonAmount.amount"
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
          v-model:amount="priceForm.jsonAmount.amount"
          v-model:currency="priceForm.jsonAmount.currency"
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
import * as skuApi from '@/api/pms/main/index.ts'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'SetPriceCom' })
const message = useMessage()
const priceFormRef = ref()
const dialogVisible = ref(false)
const priceForm = reactive({
  id: undefined,
  jsonAmount: {
    amount: undefined,
    currency: 'RMB'
  }
})

const open = async (form) => {
  let data = cloneDeep(form)
  priceForm.id = data.id
  if (data.companyPrice.amount) {
    priceForm.jsonAmount.amount = data.companyPrice.amount
  }
  // priceForm.jsonAmount.amount = data.companyPrice.amount ? data.companyPrice.amount : ''
  priceForm.jsonAmount.currency = data.companyPrice?.currency ? data.companyPrice?.currency : 'RMB'
  dialogVisible.value = true
}

const handleCancel = () => {
  priceFormRef.value.resetFields()
  dialogVisible.value = false
}

const emit = defineEmits(['sure'])
const handleSure = async () => {
  await priceFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      await skuApi.setPrice(priceForm.id, priceForm.jsonAmount)
      handleCancel()
      message.success('保存成功')
      emit('sure')
    }
  })
}
defineExpose({ open })
</script>
