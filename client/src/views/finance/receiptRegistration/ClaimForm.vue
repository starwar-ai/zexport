<template>
  <eplus-form
    ref="formRef"
    v-bind="{ id: id, loading: loading, mode: mode }"
    v-model="formData"
    :saveAction="{
      permi: 'fms:bank-registration:create',
      handler: () => submitForm()
    }"
    :submitAction="{
      permi: 'fms:bank-registration:submit',
      handler: () => submitForm('submit')
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="productSchemas"
    />
    <eplus-form-items
      title="登记入账信息"
      :formSchemas="quoteitemListSchemas"
    >
      <template #quoteitemList>
        <QuoteItemCom
          :formData="formData"
          ref="QuoteItemComRef"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import * as ReportApi from '@/api/system/poiReport'
import QuoteItemCom from './components/QuoteItemCom.vue'
import * as RegistrationApi from '@/api/finance/receiptRegistration'
import { cloneDeep } from 'lodash-es'

const message = useMessage()
const formRef = ref()
const loading = ref(false)
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  param?
}>()
const QuoteItemComRef = ref()
// const { close } = inject('dialogEmits') as {
//   close: () => void
// }
const queryId: string = (props.id || '') as string
interface FormData {
  code: string
}
let formData = reactive<FormData>({
  code: ''
})
const { query } = useRoute()
const { updateDialogActions = () => {}, clearDialogActions = () => {} } = query.id
  ? {}
  : (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
provide('formData', formData)

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
  return params
}
const onChangeCode = (val, list) => {
  if (val) {
    formData.sourceCode = val
    if (list?.length) {
      formData.sourceName = list.find((item) => item.code === val)?.name
    }
  }
}
let productSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '登记人',
    editable: true,
    span: 8,
    component: <el-input disabled />
  },
  {
    field: 'deptName',
    label: '部门名称',
    editable: true,
    span: 8,
    component: <el-input disabled />
  },
  {
    field: 'date',
    label: '登记日期',
    editable: true,
    disabled: true,
    span: 8,
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        valueFormat="x"
        class="!w-100%"
        disabledDate={(date) => {
          const dateTime = new Date(date).getTime()
          let today = new Date() // 获取当前时间
          today.setHours(0, 0, 0, 0) // 将当前时间的小时、分钟、秒和毫秒设置为0，得到今天0点的时间
          let todayTimestamp = today.getTime()
          return dateTime < todayTimestamp
        }}
      />
    )
  },
  {
    field: 'sourceName',
    label: '入账单位',
    editable: true,
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
    )
  }
])
const quoteitemListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'quoteitemList',
    label: '供应商报价',
    span: 24
  }
])
onMounted(async () => {
  try {
  } catch {
  } finally {
  }
})
const submitForm = async (type?) => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    let result: any = await getParams()
    console.log(result, 'result')
    if (valid && result) {
      try {
        if (queryId) {
          await RegistrationApi.updateRegistration({
            submitFlag: type === 'submit' ? 1 : 0,
            claimStatus: 0,
            ...result
          })
        } else {
          await RegistrationApi.createRegistration({
            submitFlag: type === 'submit' ? 1 : 0,
            ...result
          })
        }
        message.success('提交成功')
        // close()
      } catch {}
    }
  } finally {
    loading.value = false
  }
}
</script>
<style scoped lang="scss"></style>
