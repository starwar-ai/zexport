<template>
  <el-dialog
    v-model="dialogVisible"
    title="修改快递公司"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="快递公司" prop="venderId">
        <el-select
          v-model="formData.venderId"
          placeholder="请选择快递公司"
          style="width: 100%"
          filterable
        >
          <el-option
            v-for="item in venderList"
            :key="item.id"
            :label="item.nameShort || item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import * as EmsListApi from '@/api/ems/emsList'

const props = defineProps<{
  sendId?: number
  currentVenderId?: number
}>()

const emit = defineEmits(['success'])
const message = useMessage()

const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref()
const venderList = ref([])

const formData = reactive({
  venderId: undefined
})

const rules = {
  venderId: [{ required: true, message: '请选择快递公司', trigger: 'change' }]
}

const open = async () => {
  dialogVisible.value = true
  formData.venderId = props.currentVenderId
  
  // Load express company list
  const result = await EmsListApi.getSimpleList()
  venderList.value = result.list
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return
  
  loading.value = true
  try {
    await EmsListApi.updateVender({
      id: props.sendId,
      venderId: formData.venderId
    })
    message.success('修改成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    loading.value = false
  }
}

defineExpose({
  open
})
</script>
