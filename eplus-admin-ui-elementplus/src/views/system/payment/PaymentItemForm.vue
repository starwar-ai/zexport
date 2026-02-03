<template>
  <Dialog
    :title="dialogTitle"
    v-model="dialogVisible"
    width="1000"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
      :inline="true"
    >
      <el-form-item
        label="付款方式名称"
        prop="name"
        :style="{ width: '40%' }"
        :rules="[
          {
            required: true,
            message: '请输入付款方式名称',
            trigger: 'blur'
          }
        ]"
        label-width="120px"
      >
        <el-input
          v-model="formData.name"
          placeholder="请输入付款方式名称"
        />
      </el-form-item>
      <!-- <el-form-item
        label="英文名称"
        prop="nameEng"
        :style="{ width: '40%' }"
        :required="true"
        label-width="120px"
      >
        <el-input
          v-model="formData.nameEng"
          placeholder="请输入英文名称"
        />
      </el-form-item> -->
    </el-form>
    <eplus-form-table
      ref="TableRef"
      :list="tableList"
      :defaultVal="defaultValObj"
      :schema="tableSchema"
    />
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
<script setup lang="tsx">
import * as PaymentApi from '@/api/system/payment'
import { columnWidth } from '@/utils/table'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  nameEng: undefined
})
let defaultValObj = reactive({
  isShow: true
})
const TableRef = ref()
const formRules = reactive({})
const formRef = ref() // 表单 Ref
let tableList = reactive([])

const tableSchema = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '步骤',
    field: 'step',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PAYMENT_PLAN_STEP}
        clearable={true}
      />
    ),
    minWidth: columnWidth.l,
    rules: { required: true, message: '请选择步骤' }
  },
  {
    label: '起始日',
    field: 'dateType',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.DATE_TYPE}
        clearable={true}
      />
    ),
    minWidth: columnWidth.l,
    rules: { required: true, message: '请选择日期' }
  },
  {
    label: '是否控制发票',
    field: 'controlInvoiceFlag',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.IS_INT}
        clearable={true}
      />
    ),
    minWidth: columnWidth.l,
    rules: { required: true, message: '请选择是否控制发票' }
  },
  {
    label: '天数',
    field: 'days',
    minWidth: columnWidth.l,
    component: (
      <el-input-number
        min={1}
        controls={false}
        class="!w-100%"
      />
    ),
    rules: { required: true, message: '请输入天数' }
  },
  {
    label: '付款比例(%)',
    field: 'paymentRatio',
    width: columnWidth.xl,
    component: (
      <el-input-number
        min={0}
        max={100}
        controls={false}
        precision={2}
        class="!w-100%"
      />
    ),
    rules: [{ required: true, message: '请输入付款比例' }]
  },
  // {
  //   label: '支付方式',
  //   field: 'paymentMethod',
  //   isShow: true,
  //   minWidth: 200,
  //   component: (
  //     <eplus-dict-select
  //       style="width: 90px"
  //       dictType={DICT_TYPE.PAY_METHOD}
  //       clearable={true}
  //     />
  //   ),
  //   rules: [{ required: true, message: '请选择支付方式' }]
  // },
  {
    label: '操作',
    width: 100,
    fixed: 'right',
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => deleteAction(index)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
]
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
      formData.value = await PaymentApi.getPaymentItem(id)
      tableList = formData.value?.systemPaymentPlanList
      if (!tableList?.length) {
        tableList = [{ index: '' }]
      }
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const getParams = () => {
  let stepList = tableList.map((item) => item.step)
  if ([...new Set(stepList)]?.length !== stepList?.length) {
    message.error('步骤不能重复')
    return false
  }
  Object.assign(formData.value, { systemPaymentPlanList: tableList })
  return true
}
const submitForm = async () => {
  // 校验表单
  let valid = await formRef.value.validate()
  let tableValid = await TableRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    if (valid && tableValid) {
      let flag = await getParams()
      if (!flag) return false
      const data = formData.value as unknown as PaymentApi.PaymentItemVO
      if (formType.value === 'create') {
        await PaymentApi.createPaymentItem(data)
        message.success(t('common.createSuccess'))
      } else {
        await PaymentApi.updatePaymentItem(data)
        message.success(t('common.updateSuccess'))
      }
      dialogVisible.value = false
      // 发送操作成功的事件
      emit('success')
    } else {
      message.error('请填写完整信息')
    }
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    nameEng: undefined
  }
  tableList = []
  formRef.value?.resetFields()
}
</script>
