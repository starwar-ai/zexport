<template>
  <Dialog
    v-model="dialogTableVisible"
    title="完成"
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
          label="实际时间"
          prop="date"
          :rules="[
            {
              required: true,
              message: '请选择实际时间',
              trigger: 'change'
            }
          ]"
        >
          <el-date-picker
            v-model="form.date"
            type="daterange"
            placeholder="请选择"
            value-format="x"
          />
        </el-form-item>
        <!-- <el-form-item
          label="实际结束时间"
          prop="endDate"
          :rules="[
            {
              required: true,
              message: '请选择实际结束时间',
              trigger: 'change'
            }
          ]"
        >
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="请选择"
            value-format="x"
          />
        </el-form-item> -->
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
import * as ProjectApi from '@/api/pjms/project/index'

defineOptions({ name: 'ProjectDoneDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
const FormRef = ref()
let form = reactive({
  type: '',
  id: undefined,
  date: ''
})

const open = async (type, id) => {
  form.type = type
  form.id = id || undefined
  form.date = ''
  dialogTableVisible.value = true
}

const handleCancel = () => {
  FormRef.value.resetFields()
  dialogTableVisible.value = false
}
const handleSure = async () => {
  await FormRef.value.validate((valid, fields) => {
    if (valid) {
      ProjectApi.doneProject({
        id: form.id,
        date: form.date
      }).then((res) => {
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
