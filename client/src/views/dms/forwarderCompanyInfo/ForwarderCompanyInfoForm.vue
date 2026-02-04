<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="diaType === 'add' ? '新增船代公司' : '编辑船代公司'"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <el-form
        ref="FormRef"
        :model="form"
        label-width="120px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item
              label="归属公司"
              prop="companyId"
              :rules="[
                {
                  required: true,
                  message: '请选择归属公司',
                  trigger: 'change'
                }
              ]"
            >
              <eplus-custom-select
                v-model="form.companyId"
                :api="getcompanySimpleList"
                label="name"
                value="id"
                placeholder="请选择"
                style="width: 100%"
                @change="getCompanyName"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="船代公司名称"
              prop="name"
              :rules="[
                {
                  required: true,
                  message: '请输入船代公司名称',
                  trigger: 'blur'
                }
              ]"
            >
              <el-input
                v-model="form.name"
                autocomplete="off"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="联系人"
              prop="contactName"
              :rules="[
                {
                  required: true,
                  message: '请输入联系人',
                  trigger: 'blur'
                }
              ]"
            >
              <el-input
                v-model="form.contactName"
                autocomplete="off"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="联系电话"
              prop="contactPhoneNumber"
              :rules="[
                {
                  required: true,
                  message: '请输入联系电话',
                  trigger: 'blur'
                }
              ]"
            >
              <el-input
                v-model="form.contactPhoneNumber"
                autocomplete="off"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                type="textarea"
                v-model="form.remark"
                autocomplete="off"
              />
            </el-form-item>
          </el-col>
        </el-row>
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
import * as ForwarderCompanyInfoApi from '@/api/dms/forwarderCompanyInfo/index'
import { getcompanySimpleList } from '@/api/common/index'

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
  companyId: '',
  companyName: '',
  name: '',
  contactName: '',
  contactPhoneNumber: '',
  remark: '',
  status: 1
})

const getCompanyName = (item) => {
  form.companyName = item.name
}

const open = async (type, obj? = {}) => {
  diaType.value = type
  if (type !== 'add') {
    form = obj
  } else {
    form.companyId = ''
    form.name = ''
    form.contactName = ''
    form.contactPhoneNumber = ''
    form.companyName = ''
    form.remark = ''
  }
  dialogTableVisible.value = true
}

const handleCancel = () => {
  FormRef.value.resetFields()
  dialogTableVisible.value = false
}
const handleSure = async () => {
  await FormRef.value.validate((valid, fields) => {
    if (valid) {
      if (form.id) {
        ForwarderCompanyInfoApi.updateForwarderCompany(form).then((res) => {
          message.success('保存成功')
          emit('sure')
          handleCancel()
        })
      } else {
        ForwarderCompanyInfoApi.createForwarderCompany(form).then((res) => {
          message.success('保存成功')
          emit('sure')
          handleCancel()
        })
      }
    }
  })
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
