<template>
  <eplus-form
    v-if="!showFlag"
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :submitAction="{
      permi: 'pms:agent-sku:submit',
      handler: () => saveForm(1)
    }"
  >
    <eplus-form-items
      title="产品信息"
      :formSchemas="basicSchemas"
    >
      <template #custCode>
        <eplus-input-search-select
          v-model="formData.custCode"
          :api="TravelReimbApi.getCustSimpleList"
          :params="{ pageSize: 100, pageNo: 1 }"
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          :disabled="['edit', 'change'].includes(props.mode)"
          :defaultObj="{
            code: formData?.custCode || undefined,
            name: formData?.custName || undefined
          }"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title=""
      :formSchemas="basic2Schemas"
    >
      <template #annex>
        <UploadList
          ref="UploadListRef"
          :fileList="formData.annex"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="供应商报价"
      :formSchemas="quoteitemListSchemas"
    >
      <template #quoteitemList>
        <QuoteItemCom
          :fromPage="fromPage"
          :formData="formData"
          ref="QuoteItemComRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="报关信息"
      :formSchemas="hsdataSchemas"
    >
      <template #hsCode>
        {{ formData.hsCode || '' }} <el-button @click="handleHsCode">选择HsCode</el-button>
      </template>
    </eplus-form-items>
  </eplus-form>

  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />

  <SelectHsCode
    ref="selectHsCodeRef"
    @sure="hsdataCodeChange"
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as agentApi from '@/api/pms/agent/index'
import UploadList from '@/components/UploadList/index.vue'
// import UploadPic from '@/components/UploadPic/index.vue'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import { cloneDeep } from 'lodash-es'
import ChangeTips from '@/components/ChangeTips/index.vue'
import { UploadZoomPic } from '@/components/UploadPic'
import AdornInput from '../main/components/AdornInput.vue'
import { formatterEngDes } from '@/utils'
import QuoteItemCom from '@/views/pms/product/main/components/QuoteItemCom.vue'
import SelectHsCode from '@/views/pms/product/components/SelectHsCode.vue'

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const showFlag = ref(true)
const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'AgentForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  changeEdit?: Boolean
}>()
const fromPage = 'agentPage'
const changeTipsRef = ref()
const oldData = ref()

const submitData = ref()
const QuoteItemComRef = ref()
const loading = ref(false)
// const ProductDialogRef = ref()
let formData: EplusAuditable = reactive({
  auditStatus: 0,
  custCode: '',
  cskuCode: '',
  price: {
    amount: undefined,
    currency: 'USD'
  },
  sourceId: '',
  sourceCode: '',
  name: '',
  nameEng: '',
  categoryId: '',
  code: '',
  sourceFlag: '',
  skuType: '',
  ownBrandFlag: '',
  brandId: '',
  material: '',
  measureUnit: '',
  singleNetweight: {
    weight: '',
    unit: 'g'
  },
  purchaseUserId: '',
  purchaseUserDeptId: '',
  purchaseUserDeptName: '',
  purchaseUserName: '',
  description: '',
  descriptionEng: '',
  annex: [],
  picture: [],
  specLength: undefined,
  specWidth: undefined,
  specHeight: undefined,
  hsdata: {},
  quoteitemList: []
})
const classTree = ref<Tree[]>([]) // 树形结构

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const UploadListRef = ref()

const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'custCode',
    label: '客户名称',
    rules: {
      required: true,
      message: '请选择客户名称'
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    component: <el-input placeholder="请输入" />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入客户货号'))
        } else {
          callback()
        }
      }
    }
  },

  {
    field: 'name',
    label: '中文品名',
    component: (
      <el-input
        placeholder="请输入"
        onBlur={(e) => {
          const val = e.target.value
          if (val && !formData.declarationName) {
            formData.declarationName = val
          }
        }}
      />
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入中文品名'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'declarationName',
    label: '报关中文品名',
    component: <el-input placeholder="请输入" />
  },
  {
    field: 'nameEng',
    label: '英文品名',
    component: (
      <el-input
        placeholder="请输入"
        onBlur={(e) => {
          const val = e.target.value
          if (val && !formData.customsDeclarationNameEng) {
            formData.customsDeclarationNameEng = val
          }
        }}
      />
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入英文品名'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文品名',
    component: <el-input placeholder="请输入" />
  }
])

const basic2Schemas: EplusFormSchema[] = reactive([
  {
    field: 'description',
    label: '中文描述',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入产品中文描述"
        rows="15"
      ></el-input>
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入中文描述'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'descriptionEng',
    label: '英文描述',
    span: 12,
    component: (
      <el-input
        type="textarea"
        rows="15"
        placeholder="Please enter the English product description..."
        formatter={(value) => formatterEngDes(value)}
      ></el-input>
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('Please enter the English product description...'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'picture',
    label: '图片',
    span: 24,
    component: <UploadZoomPic mainFlag />
  }
])

const selectHsCodeRef = ref()

const handleHsCode = () => {
  selectHsCodeRef.value.open()
}
const hsdataCodeChange = (val) => {
  formData.hsdata = val || {}
  formData.hsCodeId = val?.id || ''
  formData.hsCode = val?.code || ''
  formData.hsdataUnit = val?.unit || ''
  formData.hsdataTaxRefundRate = val?.taxRefundRate || ''
}

const hsdataSchemas: EplusFormSchema[] = reactive([
  {
    field: 'hsCode',
    label: '报关HSCODE'
  },
  {
    field: 'hsdataUnit',
    label: '报关单位',
    component: <el-input disabled />
  },
  {
    field: 'hsdataTaxRefundRate',
    label: '退税率',
    component: (
      <AdornInput
        disabled
        appendVal="%"
      />
    )
  }
])
const quoteitemListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'quoteitemList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])

const getFileList = (params) => {
  formData.annex = params
}

const emit = defineEmits(['handleSuccess'])
const formRef = ref()

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  if (QuoteItemComRef.value) {
    let result = await QuoteItemComRef.value?.saveForm()
    if (result) {
      params.quoteitemList = result
    } else {
      params.quoteitemList = []
      return false
    }
  }
  if (params?.quoteitemList?.length === 0) {
    message.warning('请先添加供应商报价')
    return false
  }

  if (props.mode === 'edit' || props.mode === 'change') {
    return {
      ...params,
      skuType: 1,
      custProFlag: 1,
      agentFlag: 1
    }
  } else {
    return {
      ...params,
      skuType: 1,
      agentFlag: 1,
      custProFlag: 1,
      auditStatus: 0
    }
  }
}

const saveForm = async (submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await agentApi.createAgentSku({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.createSuccess'))
      close()
    } else {
      await agentApi.updateAgentSku({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.updateSuccess'))
      close()
    }
  } finally {
    loading.value = false
  }
}

const openTip = async () => {
  let paramsResult = await getParams()
  if (!paramsResult) return false
  submitData.value = { ...paramsResult }
  changeTipsRef.value.open(submitData.value, 'sku')
}
const submitChange = async () => {
  await agentApi.agentSkuChange({ ...submitData.value, oldData: oldData.value })
  message.success('提交成功')
  close()
}

onMounted(async () => {
  showFlag.value = true
  if (props.mode === 'edit' || props.mode === 'change') {
    const res = await agentApi.getSkuInfo({ id: props.id })
    oldData.value = cloneDeep(res)
    Object.assign(formData, res)
    showFlag.value = false
    nextTick(() => {
      if (props.mode === 'change') {
        clearDialogActions()
        updateDialogActions(
          <el-button
            onClick={() => {
              openTip()
            }}
            key="changeSubmitSku"
            hasPermi="pms:agent-sku:change"
          >
            提交
          </el-button>
        )
      }
    })
  } else {
    showFlag.value = false
  }
})
</script>
