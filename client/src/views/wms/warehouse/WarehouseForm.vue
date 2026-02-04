<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="diaType === 'add' ? '新增仓库' : '编辑仓库'"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <el-form
        ref="FormRef"
        :model="form"
        label-width="100px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item
              label="仓库名称"
              prop="name"
              :rules="[
                {
                  required: true,
                  message: '请输入仓库名称',
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
          <el-col
            :span="12"
            v-if="diaType != 'add'"
          >
            <el-form-item label="仓库编号">
              <el-input
                v-model="form.code"
                autocomplete="off"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库地址">
              <el-input
                v-model="form.address"
                autocomplete="off"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="仓管员"
              prop="managerIds"
              :rules="[
                {
                  required: true,
                  message: '请选择仓管员',
                  trigger: 'change'
                }
              ]"
            >
              <eplus-user-select
                multiple
                v-model="form.managerIds"
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
import * as WarehouseApi from '@/api/wms/warehouse/index'

defineOptions({ name: 'WarehouseForm' })
const message = useMessage()
const dialogTableVisible = ref(false)
const loading = ref(false)
const diaType = ref('')
const emit = defineEmits<{
  sure: [link: string]
}>()
const FormRef = ref()
let form = reactive({
  name: '',
  address: '',
  managerIds: [],
  remark: '',
  enableFlag: 1,
  venderFlag: 0
})

const open = async (type, obj? = {}) => {
  diaType.value = type
  if (type !== 'add') {
    form = obj
  } else {
    form.id = ''
    form.name = ''
    form.address = ''
    form.managerIds = []
    form.remark = ''
    form.enableFlag = 1
    form.venderFlag = 0
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
        WarehouseApi.updateWarehouse(form).then((res) => {
          message.success('保存成功')
          emit('sure')
          handleCancel()
        })
      } else {
        WarehouseApi.createWarehouse(form).then((res) => {
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
