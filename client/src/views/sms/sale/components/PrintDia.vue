<template>
  <Dialog
    v-model="dialogTableVisible"
    title="按条件打印"
    width="500"
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
          label="选择打印类型"
          prop="reportId"
          :rules="[
            {
              required: true,
              message: '请选择打印类型',
              trigger: 'change'
            }
          ]"
        >
           <el-select
              v-model="form.reportId"
              value-key="id"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="item in reportDOListRef"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
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
defineOptions({ name: 'SettlementFormDia' })
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
const form = reactive({ reportId: 1})
var reportDOListRef = ref()
const open = async (reportDOList) => {
  if(reportDOList?.length){
    form.reportId = reportDOList[0].id
  }
  reportDOListRef.value = reportDOList
  dialogTableVisible.value = true
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

const FormRef = ref()
const handleSure = () => {
  FormRef.value.validate(async (valid) => {
    if (valid) {
      emit(
        'sure',
         form.reportId
       )
      handleCancel()
    }
  })
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
