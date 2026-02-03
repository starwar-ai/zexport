<template>
  <Dialog
    v-model="dialogTableVisible"
    title="采购主体"
    width="400"
    append-to-body
    destroy-on-close
  >
    <el-form
      :model="formData"
      ref="formRef"
    >
      <el-form-item
        label="采购主体"
        prop="purchaseCompanyId"
        :rules="[
          {
            required: true,
            message: '请选择采购主体',
            trigger: 'change'
          }
        ]"
      >
        <el-select
          v-model="formData.purchaseCompanyId"
          placeholder="请选择"
        >
          <el-option
            v-for="item in companyList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
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
import * as PurchasePlanApi from '@/api/scm/purchasePlan'

defineOptions({ name: 'SelectCompany' })
const dialogTableVisible = ref(false)
const message = useMessage()
const formRef = ref()
const companyList = ref([])
const row = ref({})
const formData = reactive({
  purchaseCompanyId: '',
  purchaseCompanyName: ''
})
const open = async (data) => {
  row.value = data
  companyList.value = await PurchasePlanApi.getProcessedCompanySimpleList()
  dialogTableVisible.value = true
}

const handleCancel = () => {
  formRef.value.resetFields()
  dialogTableVisible.value = false
}
const emit = defineEmits<{
  sure
}>()
const handleSure = async () => {
  formRef.value.validate((valid) => {
    if (valid) {
      formData.purchaseCompanyName = companyList.value.find(
        (item) => item.id === formData.purchaseCompanyId
      ).name
      emit('sure', { ...row.value, ...formData })
      handleCancel()
    }
  })
}
defineExpose({ open })
</script>

<style lang="scss" scoped>
.totalAction {
  cursor: pointer;
  width: 2000px;
}

.totalAction:hover {
  border-color: #ccc;
  box-shadow: 0 0 0 1px #eee;
}
</style>
