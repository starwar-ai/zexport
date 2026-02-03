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
        label="日期"
        prop="dailyCurrDate"
      >
        <el-date-picker
          v-model="formData.dailyCurrDate"
          type="date"
          value-format="x"
          placeholder="选择日期"
        />
      </el-form-item>
      <el-form-item
        label="币种"
        prop="dailyCurrName"
      >
        <el-input
          v-model="formData.dailyCurrName"
          placeholder="请输入币种"
        />
      </el-form-item>
      <el-form-item
        label="汇率"
        prop="dailyCurrRate"
      >
        <el-input
          v-model="formData.dailyCurrRate"
          placeholder="请输入汇率"
        />
      </el-form-item>
      <el-form-item
        label="来源 1-自动 0-手动"
        prop="dailyCurrSource"
      >
        <el-input
          v-model="formData.dailyCurrSource"
          placeholder="请输入来源 1-自动 0-手动"
        />
      </el-form-item>
      <el-form-item
        label="中间汇率"
        prop="dailyCurrMidRate"
      >
        <el-input
          v-model="formData.dailyCurrMidRate"
          placeholder="请输入中间汇率"
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
import * as CurrencysRateApi from '@/api/system/rate'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  dailyCurrDate: undefined,
  dailyCurrName: undefined,
  dailyCurrRate: undefined,
  dailyCurrSource: undefined,
  dailyCurrMidRate: undefined
})
const formRules = reactive({})
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
      formData.value = await CurrencysRateApi.getCurrencysRate(id)
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
    const data = formData.value as unknown as CurrencysRateApi.CurrencysRateVO
    if (formType.value === 'create') {
      await CurrencysRateApi.createCurrencysRate(data)
      message.success(t('common.createSuccess'))
    } else {
      await CurrencysRateApi.updateCurrencysRate(data)
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
    dailyCurrDate: undefined,
    dailyCurrName: undefined,
    dailyCurrRate: undefined,
    dailyCurrSource: undefined,
    dailyCurrMidRate: undefined
  }
  formRef.value?.resetFields()
}
</script>
