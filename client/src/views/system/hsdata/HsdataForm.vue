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
      <!--      <el-form-item label="版本号" prop="ver">-->
      <!--        <el-input v-model="formData.ver" placeholder="请输入版本号" />-->
      <!--      </el-form-item>-->
      <el-form-item
        label="编码"
        prop="code"
      >
        <el-input
          v-model="formData.code"
          placeholder="请输入编码"
        />
      </el-form-item>
      <el-form-item
        label="商品名称"
        prop="name"
      >
        <el-input
          v-model="formData.name"
          placeholder="请输入商品名称"
        />
      </el-form-item>
      <el-form-item
        label="报关单位"
        prop="unit"
      >
        <el-input
          v-model="formData.unit"
          placeholder="请输入报关单位"
        />
      </el-form-item>
      <el-form-item
        label="退税率"
        prop="taxRefundRate"
      >
        <el-input
          v-model="formData.taxRefundRate"
          placeholder="请输入退税率"
        />
      </el-form-item>
      <el-form-item
        label="征税率"
        prop="rate"
      >
        <el-input
          v-model="formData.rate"
          placeholder="请输入征税率"
        />
      </el-form-item>
      <el-form-item
        label="备注"
        prop="remark"
      >
        <el-input
          v-model="formData.remark"
          placeholder="请输入备注"
        />
      </el-form-item>
      <el-form-item
        label="商品全称"
        prop="chname"
      >
        <el-input
          v-model="formData.chname"
          placeholder="请输入商品全称"
        />
      </el-form-item>
      <el-form-item
        label="征收率"
        prop="addrate"
      >
        <el-input
          v-model="formData.addrate"
          placeholder="请输入征收率"
        />
      </el-form-item>
      <el-form-item
        label="第二单位"
        prop="code2"
      >
        <el-input
          v-model="formData.code2"
          placeholder="请输入第二单位"
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
import * as HsdataApi from '@/api/system/hsdata'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  ver: undefined,
  code: undefined,
  name: undefined,
  unit: undefined,
  taxRefundRate: undefined,
  rate: undefined,
  remark: undefined,
  chname: undefined,
  addrate: undefined,
  code2: undefined
})
const formRules = reactive({
  ver: [{ required: true, message: '版本号不能为空', trigger: 'blur' }]
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
      formData.value = await HsdataApi.getHsdata(id)
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
    const data = formData.value as unknown as HsdataApi.HsdataVO
    if (formType.value === 'create') {
      await HsdataApi.createHsdata(data)
      message.success(t('common.createSuccess'))
    } else {
      await HsdataApi.updateHsdata(data)
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
    ver: undefined,
    code: undefined,
    name: undefined,
    unit: undefined,
    taxRefundRate: undefined,
    rate: undefined,
    remark: undefined,
    chname: undefined,
    addrate: undefined,
    code2: undefined
  }
  formRef.value?.resetFields()
}
</script>
