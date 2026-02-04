<template>
  <Dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="40%"
  >
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-form-item
        prop="name"
        label="回签人"
      >
        <el-input
          v-model="formData.name"
          disabled
        />
      </el-form-item>
      <el-form-item
        prop="signBackTime"
        label="回签日期"
      >
        <el-date-picker
          v-model="formData.signBackTime"
          clearable
          valueFormat="x"
          type="date"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item
        prop="description"
        label="描述"
      >
        <el-input
          v-model="formData.description"
          type="textarea"
          placeholder="请输入描述信息"
        />
      </el-form-item>
      <el-form-item
        prop="signBackAnnexList"
        label="回签附件"
      >
        <UploadList
          v-model="formData.signBackAnnexList"
          :fileList="formData.signBackAnnexList"
          @success="
            (data) => {
              formData.signBackAnnexList = data
            }
          "
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        :disabled="formLoading"
        type="primary"
        @click="submitForm"
        >确 定</el-button
      >
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="tsx" setup>
import * as PurchaseContractApi from '@/api/scm/auxiliaryPurchaseContract'
import UploadList from '@/components/UploadList/index.vue'
import { FormRules } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getConfigKey } from '@/api/common'

defineOptions({ name: 'SignBackForm' })
// 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用

const formData = ref({
  name: '',
  description: '',
  signBackTime: Date.now(),
  signBackAnnexList: []
})

// 回签附件是否必填配置（从系统配置中读取）
// 配置值为 "0" 时，附件非必填；其他值时，附件必填
const signBackAnnexRequired = ref(false)

// 开发环境下回签附件不是必填项
const isDev = import.meta.env.DEV

const formRules = reactive<FormRules>({
  signBackTime: [{ required: true, message: '回签日期不能为空', trigger: 'blur' }],
  signBackAnnexList: [
    {
      validator: (rule, value, callback) => {
        // 开发环境下不校验
        if (isDev) {
          callback()
          return
        }
        // 根据配置判断是否必填
        if (signBackAnnexRequired.value && (!value || value.length === 0)) {
          callback(new Error('请上传回签附件'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
})
const formRef = ref() // 表单 Ref
const simpleDataRef = ref()

/** 获取回签附件配置 */
const getSignBackAnnexConfig = async () => {
  try {
    const annexValue = await getConfigKey('purchase.annex')
    // 配置值为 "0" 时，附件非必填；其他值时，附件必填
    signBackAnnexRequired.value = annexValue !== '0'
  } catch (error) {
    console.error('获取回签附件配置失败:', error)
    // 默认设置为必填
    signBackAnnexRequired.value = true
  }
}

/** 打开弹窗 */
const open = async (title: string, simpleData: any) => {
  simpleDataRef.value = simpleData
  dialogTitle.value = title
  resetForm()
  dialogVisible.value = true
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

// 组件挂载时获取配置
onMounted(() => {
  getSignBackAnnexConfig()
})

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    // let paramsResult: any
    // if (simpleDataRef.value) {
    //   paramsResult = [{ ...formData.value, ...simpleDataRef.value, signBackAnnexList: [] }]
    // }
    await PurchaseContractApi.signBackPurchaseContract({
      contractId: simpleDataRef.value?.id,
      signBackDate: formData.value?.signBackTime,
      remark: formData.value?.description,
      signBackAnnexList: formData.value?.signBackAnnexList
    })
    message.success('回签成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  var nickname = useUserStore().getUser.nickname
  formData.value = {
    name: nickname,
    description: '',
    signBackTime: Date.now(),
    signBackAnnexList: []
  }
  formRef.value?.resetFields()
}
</script>
