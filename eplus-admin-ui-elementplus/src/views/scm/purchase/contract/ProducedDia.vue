<template>
  <Dialog
    v-model="dialogVisible"
    title="生产完成"
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
        label="完成时间"
        prop="doneTime"
        :rules="[
          {
            required: true,
            message: '请选择完成时间'
          }
        ]"
      >
        <el-date-picker
          v-model="form.doneTime"
          type="date"
          placeholder="请选择完成时间"
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
import { cloneDeep } from 'lodash-es'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'

defineOptions({ name: 'ProducedDia' })
const message = useMessage()
const formRef = ref()
const dialogVisible = ref(false)
const form = reactive({
  contractId: undefined,
  doneTime: Date.now()
})

const open = async (row) => {
  let data = cloneDeep(row)
  form.contractId = data.id
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
      await PurchaseContractApi.produceDonePurchaseContract(form)
      message.success('操作成功')
      emit('success')
      handleCancel()
    }
  })
}

defineExpose({ open })
</script>
