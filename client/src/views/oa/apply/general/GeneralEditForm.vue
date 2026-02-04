<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :closeAction="{
      handler: close
    }"
    :saveAction="{
      permi: 'oa:general-apply:create',
      handler: () => saveForm()
    }"
    :submitAction="{
      permi: 'oa:general-apply:submit',
      handler: () => submitForm()
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="purchaseSchemas"
    >
      <template #amount>
        <EplusMoney
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
        />
      </template>
    </eplus-form-items>

    <!-- 费用归集部分 -->
    <div class="fee-share-section">
      <div class="section-title">费用归集</div>
      <div class="fee-share-content">
        <eplus-form-items
          title=""
          :formSchemas="companySchema"
        />
        <FeeShareOrderFormCom
          v-if="dataLoaded || mode === 'create'"
          ref="FeeShareOrderFormComRef"
          :feeType="0"
          :info="formData.feeShare"
          :mode="mode"
        />
      </div>
    </div>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import * as OaGeneralApplyApi from '@/api/oa/generalApply'
import { cloneDeep } from 'lodash-es'
import { getcompanySimpleList } from '@/api/common'
import FeeShareOrderFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'

const { t } = useI18n() // 国际化
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const tabLoading = ref(false)
const dataLoaded = ref(false) // 添加数据加载状态
const message = useMessage()
defineOptions({ name: 'ConfirmInspection' })
const loading = ref(true)
const simpleUserList = ref([])
const FeeShareOrderFormComRef = ref()
const formData = reactive({
  amount: {
    amount: 0,
    currency: 'RMB'
  },
  remark: '',
  belongFlag: 0,
  venderName: '',
  actualUserId: undefined as any,
  actualUserDeptName: '',
  actualUser: {} as any,
  companyName: '',
  feeShare: null as any,
  generalEntourageIds: []
})
provide('formData', formData)
const childrenRef = ref()
const { close } = inject('dialogEmits') as {
  close: () => void
}
const saveForm = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  try {
    let data = cloneDeep(formData)
    const params: any = {
      ...data,
      submitFlag: 0
    }
    let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
    if (feeShareParams) {
      params.auxiliaryType = 1
      params.feeShare = feeShareParams
    } else {
      message.warning('缺少费用归集信息！')
      return false
    }
    loading.value = true
    if (props.mode == 'create') {
      await OaGeneralApplyApi.createGeneralApply(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await OaGeneralApplyApi.updateGeneralApply(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  try {
    let data = cloneDeep(formData)
    const params: any = {
      ...data,
      submitFlag: 1
    }
    let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
    if (feeShareParams) {
      params.auxiliaryType = 1
      params.feeShare = feeShareParams
    } else {
      return false
    }
    loading.value = true
    if (props.mode == 'create') {
      await OaGeneralApplyApi.createGeneralApply(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await OaGeneralApplyApi.updateGeneralApply(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}
const changeUser = (val) => {
  if (val) {
    formData.actualUserDeptName = val.deptName
    formData.actualUser = { userId: val?.id, ...val }
  }
}
const getCompanyName = (item) => {
  formData.companyName = item.name
}

const companySchema = [
  {
    field: 'companyId',
    label: '归属主体',
    editable: true,
    component: (
      <eplus-custom-select
        api={getcompanySimpleList}
        label="name"
        value="id"
        clearable={false}
        placeholder="请选择"
        class="!w-100%"
        onChange={getCompanyName}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择归属主体'
      }
    ]
  }
]
// 信息
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'amount',
    label: '预估费用',
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          let regx = /^(\d{1,11})(\.?\d{1,3})?$/
          if (value.amount <= 0) {
            callback(new Error(`金额必须大于0`))
          } else if (!regx.test(value.amount)) {
            callback(new Error('金额只能输入正数,最多11位整数,3位小数'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'amountDesc',
    label: '预估费用明细说明',
    labelWidth: '140px',
    span: 12,
    component: (
      <el-input
        placeholder="请输入"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    ),
    rules: [
      {
        required: false,
        message: '请输入预估费用明细说明'
      }
    ]
  },
  {
    field: 'purpose',
    label: '申请事由',
    span: 12,
    component: (
      <el-input
        placeholder="字数不小于10个字，标注关键信息，含客户/供应商/展会/项目等具体名称之一，或者费用产生的主要原因"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    ),
    rules: [
      {
        required: true,
        message: '请输入申请事由'
      }
    ]
  },
  {
    field: 'remarks',
    label: '备注',
    labelWidth: '140px',
    span: 12,
    component: (
      <el-input
        placeholder="请输入备注"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    )
  }
])

const collectionSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24,
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (childrenRef.value.tableList?.length === 0) {
            callback(new Error('请选择产品信息'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
]

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

const feeShareObj = reactive({
  type: '',
  item: {}
})

onMounted(async () => {
  try {
    loading.value = true
    if (props.mode === 'edit') {
      tabLoading.value = true
      let res = await OaGeneralApplyApi.getGeneralApplyInfo({ id: props.id })
      feeShareObj.type = res.entertainType
      Object.assign(formData, { ...res })
      if (res.feeShare && Array.isArray(res.feeShare)) {
        formData.feeShare = res.feeShare
      }
      if (formData.actualUser) {
        if (formData.actualUser.userId) {
          formData.actualUserId = formData.actualUser.userId
        }
        if (formData.actualUser.deptName) {
          formData.actualUserDeptName = formData.actualUser.deptName
        }
      }
      tabLoading.value = false
      dataLoaded.value = true
    } else {
      dataLoaded.value = true
    }
    await nextTick()
    setTimeout(() => {
      loading.value = false
    }, 100)
  } finally {
    // loading的隐藏已移到setTimeout中
  }
})
</script>

<style lang="scss" scoped>
.fee-share-section {
  margin-top: 24px;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20px;
    padding-bottom: 8px;
    border-bottom: 1px solid #ebeef5;
  }

  .fee-share-content {
    padding-top: 16px;

    :deep(.eplus-form-items) {
      margin-bottom: 20px;

      .el-form-item {
        margin-bottom: 18px;

        .el-form-item__label {
          font-weight: 500;
          color: #606266;
        }

        .el-form-item__content {
          .el-select {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>
