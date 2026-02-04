<template>
  <Dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="40%"
  >
    <el-form
      ref="formRef"
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
        prop="signBackDate"
        label="回签日期"
      >
        <el-date-picker
          v-model="formData.signBackDate"
          clearable
          type="date"
          value-format="x"
          :disabled-date="disabledDate"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item
        prop="signBackDesc"
        label="描述"
      >
        <el-input
          v-model="formData.signBackDesc"
          type="textarea"
          placeholder="请输入描述信息"
        />
      </el-form-item>
      <el-form-item
        prop="signBackAnnex"
        label="回签附件"
      >
        <UploadList
          v-model="formData.signBackAnnex"
          :fileList="formData.signBackAnnex"
          @success="
            (data) => {
              formData.signBackAnnex = data
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
      <el-button @click="handleCancel">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import * as DomesticSaleContractApi from '@/api/sms/saleContract/domestic'
import { FormRules } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getConfigKey } from '@/api/common'

defineOptions({ name: 'SignBackForm' })
const props = defineProps<{
  type?: number
}>()

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用

const formData = ref({
  contractId: '',
  signBackDesc: '',
  name: useUserStore().getUser.nickname,
  signBackDate: new Date().getTime(),
  signBackAnnex: []
})

// 回签附件是否必填配置（从系统配置中读取）
// 配置值为 "0" 时，附件非必填；其他值时，附件必填
const signBackAnnexRequired = ref(false)

// 开发环境下回签附件不是必填项
const isDev = import.meta.env.DEV

const formRules = reactive<FormRules>({
  signBackDate: [{ required: true, message: '回签日期不能为空', trigger: 'blur' }],
  signBackAnnex: [
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

//--回签日期大于创建时间且不可大于当天日期
const disabledDate = (time: Date) => {
  return time.getTime() < rowCreateTime.value || time.getTime() > Date.now()
}

const formRef = ref() // 表单 Ref
const rowCreateTime = ref(0)

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
const open = async (title: string, obj: any) => {
  formData.value.contractId = obj.id
  rowCreateTime.value = obj.createTime
  dialogTitle.value = title
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
    let paramsResult = formData.value
    props.type == 1
      ? await ExportSaleContractApi.signBack(paramsResult)
      : await DomesticSaleContractApi.signBack(paramsResult)
    message.success(t('回签成功'))
    handleCancel()
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const handleCancel = () => {
  resetForm()
  dialogVisible.value = false
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    contractId: '',
    name: useUserStore().getUser.nickname,
    signBackDesc: '',
    signBackDate: new Date().getTime(),
    signBackAnnex: []
  }
  formRef.value?.resetFields()
}
</script>
