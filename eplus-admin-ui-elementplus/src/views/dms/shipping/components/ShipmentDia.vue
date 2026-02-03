<template>
  <Dialog
    v-model="dialogVisible"
    title="确认出运"
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
        label="实际开船日期"
        prop="estDepartureTime"
        :rules="[
          {
            required: true,
            message: '请选择实际开船日期'
          }
        ]"
      >
        <el-date-picker
          v-model="form.estDepartureTime"
          type="date"
          placeholder="请选择"
          value-format="x"
          class="!w-100%"
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
import * as ShipmentApi from '@/api/dms/shipment/index'

defineOptions({ name: 'ShipmentDia' })
const message = useMessage()
const formRef = ref()
const dialogVisible = ref(false)

const form = reactive({
  id: undefined,
  estDepartureTime: undefined
})

const pageInfo: any = ref({})
const open = async (row) => {
  pageInfo.value = cloneDeep(row)
  form.id = pageInfo.value.id
  form.estDepartureTime = pageInfo.value.estDepartureTime
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
      await ShipmentApi.handleShipment(form)
      message.success('操作成功!')
      emit('success')
      handleCancel()
    }
  })
}

defineExpose({ open })
</script>
