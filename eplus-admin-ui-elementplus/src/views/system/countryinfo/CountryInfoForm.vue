<template>
  <Dialog
    :title="dialogTitle"
    v-model="dialogVisible"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item
        label="国家名称"
        prop="name"
      >
        <el-input
          v-model="formData.name"
          placeholder="请输入国家名称"
        />
      </el-form-item>
      <el-form-item
        label="国家编码"
        prop="code"
      >
        <el-input
          v-model="formData.code"
          placeholder="请输入国家编码"
        />
      </el-form-item>
      <el-form-item
        label="区域编码"
        prop="regionCode"
      >
        <el-input
          v-model="formData.regionCode"
          placeholder="请输入区域编码"
        />
      </el-form-item>
      <el-form-item
        label="区域名称"
        prop="regionName"
      >
        <el-input
          v-model="formData.regionName"
          placeholder="请输入区域名称"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        @click="submitForm"
        type="primary"
        :disabled="formLoading"
        >确 定</el-button
      >
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import * as CountryInfoApi from '@/api/system/countryinfo'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  code: undefined,
  regionCode: undefined,
  regionName: undefined
})
const formRules = reactive({
  name: [{ required: true, message: '国家名称不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '国家编码不能为空', trigger: 'blur' }],
  regionCode: [{ required: true, message: '区域编码不能为空', trigger: 'blur' }],
  regionName: [{ required: true, message: '区域名称不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await CountryInfoApi.getCountryInfo(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as CountryInfoApi.CountryInfoVO
    if (formType.value === 'create') {
      await CountryInfoApi.createCountryInfo(data)
      message.success(t('common.createSuccess'))
    } else {
      await CountryInfoApi.updateCountryInfo(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    code: undefined,
    regionCode: undefined,
    regionName: undefined
  }
  formRef.value?.resetFields()
}
</script>
