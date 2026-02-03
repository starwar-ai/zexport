<template>
  <Dialog
    :title="dialogTitle"
    v-model="dialogVisible"
    width="80%"
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
        label="收款方式名称"
        prop="name"
        :style="{ width: '40%' }"
        :rules="[
          {
            required: true,
            message: '请输入收款方式名称',
            trigger: 'blur'
          }
        ]"
        label-width="120px"
      >
        <el-input
          v-model="formData.name"
          placeholder="请输入收款方式名称"
        />
      </el-form-item>
      <el-form-item
        label="英文名称"
        prop="nameEng"
        :style="{ width: '40%' }"
        :rules="[
          {
            required: true,
            message: '请输入英文名称',
            trigger: 'blur'
          }
        ]"
        label-width="120px"
      >
        <el-input
          v-model="formData.nameEng"
          placeholder="请输入英文名称"
        />
      </el-form-item>
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
import * as SettlementApi from '@/api/system/settlement'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  nameEng: undefined,
  controlPurchaseFlag: 0,
  controlShipmentFlag: 0
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
    isShow: true,
    component: (
      <eplus-dict-select
        style="width: 90px"
        dictType={DICT_TYPE.COLLECTION_PLAN_STEP}
        clearable={true}
      />
    ),
    width: 220,
    rules: { required: true, message: '请选择步骤' }
  },
  {
    label: '起始日',
    field: 'dateType',
    isShow: true,
    component: (
      <eplus-dict-select
        style="width: 90px"
        dictType={DICT_TYPE.SETTLEMENT_DATE_TYPE}
        clearable={true}
      />
    ),
    width: 220,
    rules: { required: true, message: '请选择日期' }
  },
  {
    label: '天数',
    field: 'days',
    isShow: true,
    width: 220,
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
    label: '收款比例(%)',
    field: 'collectionRatio',
    isShow: true,
    width: 200,
    component: (
      <el-input-number
        min={0}
        max={100}
        controls={false}
        precision={2}
        class="!w-100%"
      />
    ),
    rules: [{ required: true, message: '请输入收款比例' }]
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
    label: '是否控制采购',
    field: 'controlPurchaseFlag',
    isShow: true,
    component: (
      <eplus-dict-select
        style="width: 90px"
        dictType={DICT_TYPE.CONFIRM_TYPE}
        clearable={true}
        defaultValue={0}
      />
    ),
    rules: [{ required: true, message: '请选择控制采购' }],
    width: 120
  },
  {
    label: '是否控制出运',
    field: 'controlShipmentFlag',
    isShow: true,
    component: (
      <eplus-dict-select
        style="width: 90px"
        dictType={DICT_TYPE.CONFIRM_TYPE}
        clearable={true}
        defaultValue={0}
      />
    ),
    rules: [{ required: true, message: '请选择控制出运' }],
    width: 120
  },
  {
    label: '操作',
    width: 100,
    fixed: 'right',
    isShow: true,
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
      formData.value = await SettlementApi.getSettlement(id)
      tableList = formData.value?.collectionPlanList
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
  const { collectionPlanList, ...result } = formData.value
  return { collectionPlanList: tableList, ...result }
}
const submitForm = async () => {
  // 校验表单
  let valid = await formRef.value.validate()
  let tableValid = await TableRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    if (valid && tableValid) {
      const data = await getParams()
      if (!data) return false
      if (formType.value === 'create') {
        await SettlementApi.createSettlement(data)
        message.success(t('common.createSuccess'))
      } else {
        await SettlementApi.updateSettlement(data)
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
