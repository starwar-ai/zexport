<template>
  <eplus-form
    ref="formRef"
    v-bind="{ id: id, loading: loading, mode: mode }"
    v-model="formData"
    :saveAction="{
      permi: 'scm:purchase-plan:create',
      handler: () => submitForm()
    }"
    :submitAction="{
      permi: 'scm:purchase-plan:submit',
      handler: () => submitForm('submit')
    }"
  >
    <eplus-form-items
      title="模板信息"
      :formSchemas="productSchemas"
    />
    <eplus-form-items
      title="附件信息"
      :formSchemas="annexSchemas"
    >
      <template #annex>
        <UploadList
          v-if="formData.annex && formData.businessType == '2'"
          v-bind="{ ref: 'UploadRef', fileList: formData?.annex, limit: 1, accept: '.xlsx, .xls' }"
          @success="getFileList"
        />
        <UploadList
          v-else
          v-bind="{ ref: 'UploadRef', fileList: formData?.annex, limit: 1, accept: '.doc,.docx' }"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import UploadList from '@/components/UploadList/index.vue'
import { DICT_TYPE } from '@/utils/dict'
import * as ReportApi from '@/api/system/poiReport'
import { ReportTypeEnum } from '@/utils/constants'
import * as LoanAppApi from '@/api/oa/loanapp'

const message = useMessage()
const formRef = ref()
const loading = ref(false)
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  param?
}>()
const { close } = inject('dialogEmits') as {
  close: () => void
}
const queryId: string = (props.id || '') as string
interface FormData {
  code: string
  sourceName: string
  sourceType: string | number
  sourceCode: string
  annex: any | null
  reportType: any | null
}
let formData = reactive<FormData>({
  code: '',
  sourceName: '',
  sourceType: '',
  sourceCode: '',
  annex: null,
  reportType: ''
})
const { query } = useRoute()
const { updateDialogActions = () => {}, clearDialogActions = () => {} } = query.id
  ? {}
  : (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
provide('formData', formData)
//附件信息
const getFileList = (params) => {
  formData.annex = params
}
const getParams = () => {
  const { reportType, ...result } = formData
  return {
    reportType: reportTypeRef.value,
    ...result
  }
}

const toggleSchemaDisabled = async (val) => {
  formData.sourceName = ''
  productSchemas.forEach((item) => {
    if (item.label == '供应商名称' || item.label == '客户名称') {
      item.disabled = item.label === (val === 1 ? '供应商名称' : '客户名称')
    }
  })
}

const businessTypeChanged = async (val) => {}

const onChangeCode = (val, list) => {
  if (val) {
    formData.sourceCode = val
    if (list?.length) {
      formData.sourceName = list.find((item) => item.code === val)?.name
    }
  }
}
const onChangeCompany = (item) => {
  formData.companyName = item.name
}
let productSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '模板编码',
    editable: true,
    span: 6,
    component: <el-input disabled={props.id ? true : false} />,
    rules: [
      {
        required: true,
        message: '请输入模板编码'
      }
    ]
  },
  {
    field: 'name',
    label: '模板名称',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入模板名称'
      }
    ]
  },
  {
    field: 'businessType',
    label: '模版业务类型',
    editable: true,
    span: 6,
    component: (
      <eplus-dict-select
        style="width: 100%"
        dictType={DICT_TYPE.REPORT_BUSINESS_TYPE}
        clearable={true}
        onChangeEmit={(val) => {
          businessTypeChanged(val)
        }}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择模版业务类型',
        trigger: 'click'
      }
    ]
  },
  {
    field: 'sourceType',
    label: '外部模板类型',
    editable: true,
    disabled: true,
    span: 6,
    component: (
      <eplus-dict-select
        style="width: 100%"
        dictType={DICT_TYPE.REPORT_SOURCE_TYPE}
        clearable={true}
        onChangeEmit={(val) => {
          toggleSchemaDisabled(val)
        }}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择外部模板类型',
        trigger: 'click'
      }
    ]
  },
  {
    field: 'sourceCode',
    label: '客户名称',
    editable: true,
    disabled: true,
    span: 6,
    component: (
      <eplus-input-search-select
        api={ReportApi.getCustList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={onChangeCode}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择客户名称'
      }
    ]
  },
  {
    field: 'sourceCode',
    label: '供应商名称',
    editable: true,
    disabled: true,
    span: 6,
    component: (
      <eplus-input-search-select
        api={ReportApi.getVenderList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={onChangeCode}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择外部模板类型'
      }
    ]
  },
  {
    field: 'companyId',
    label: '账套信息',
    editable: true,
    disabled: true,
    span: 6,
    component: (
      <eplus-custom-select
        api={LoanAppApi.getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择"
        style="width:100%"
        onChange={onChangeCompany}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择账套信息'
      }
    ]
  }
])
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    span: 8,
    editable: true,
    labelWidth: '0px'
  }
]
const loadDialogButtons = () => {
  clearDialogActions()
  updateDialogActions(
    <el-button
      type="primary"
      onClick={() => submitForm()}
      key="toTemplate"
    >
      转模板
    </el-button>
  )
}
var reportTypeRef = ref(ReportTypeEnum.BASIC.status)
onMounted(async () => {
  try {
    console.log(props?.param)
    if (props?.mode === 'edit' || props?.mode === 'change') {
      const res = await ReportApi.getReport({ id: props?.id })
      if (
        (props?.param === 'toDiy' ||
          props?.param === 'toSpecific' ||
          props?.param === 'toCompany') &&
        props?.mode === 'edit'
      ) {
        loadDialogButtons()
      }
      if (props?.mode === 'change') {
        clearDialogActions()
        updateDialogActions(
          <el-button
            type="primary"
            onClick={() => directlyUpdate()}
            key="toTemplate"
          >
            变更
          </el-button>
        )
      }
      Object.assign(formData, res)
      if (
        props?.param === 'toDiy' ||
        props?.param === 'diy' ||
        res.reportType == ReportTypeEnum.DIY.status
      ) {
        reportTypeRef.value = ReportTypeEnum.DIY.status
        productSchemas.forEach((item) => {
          if (
            item.field === 'sourceType' ||
            (formData.sourceType === 1 && item.label === '客户名称') ||
            (formData.sourceType === 2 && item.label === '供应商名称')
          ) {
            item.disabled = false
          }
        })
      } else if (
        props?.param === 'toSpecific' ||
        props?.param === 'specific' ||
        res.reportType == ReportTypeEnum.SPECIFIC.status
      ) {
        reportTypeRef.value = ReportTypeEnum.SPECIFIC.status
        productSchemas.forEach((item) => {
          if (item.field === 'specificDictType' || item.field === 'specificValue') {
            item.disabled = false
          }
        })
      } else if (
        props?.param === 'toCompany' ||
        props?.param === 'company' ||
        res.reportType == ReportTypeEnum.COMPANY.status
      ) {
        reportTypeRef.value = ReportTypeEnum.COMPANY.status
        productSchemas.forEach((item) => {
          if (item.field === 'companyId') {
            item.disabled = false
          }
        })
      }
    }
  } catch {
  } finally {
    if (!formData.annex) {
      formData.annex = []
    }
  }
})
const submitForm = async (type?) => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    if (formData?.annex?.length === 0) {
      message.warning('请上传附件')
      return false
    }
    if (valid) {
      let result: any = getParams()
      try {
        if (
          queryId &&
          (!props?.param ||
            props?.param === 'company' ||
            props?.param === 'diy' ||
            props?.param === 'specific')
        ) {
          await ReportApi.updateReport({ submitFlag: type === 'submit' ? 1 : 0, ...result })
        } else {
          const { id, ...noIdResult } = result
          await ReportApi.createReport({ submitFlag: type === 'submit' ? 1 : 0, ...noIdResult })
        }
        message.success('提交成功')
        close()
      } catch {}
    }
  } finally {
    loading.value = false
  }
}

const directlyUpdate = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  if (formData?.annex?.length === 0) {
    message.warning('请上传附件')
    return false
  }
  await ReportApi.updateReport(getParams())
  message.success('修改成功')
  close()
}
</script>
<style scoped lang="scss"></style>
