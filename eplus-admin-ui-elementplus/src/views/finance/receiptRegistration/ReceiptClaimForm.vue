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
      title="收款登记信息"
      :formSchemas="claimSchemas"
    />
    <eplus-form-items
      title="登记入账信息"
      :formSchemas="receiptSchemas"
    >
      <template #receiptItemList>
        <eplus-form-table
          ref="TableRef"
          selectionFlag
          :list="tableList"
          :defaultVal="{}"
          :schema="tableColumns"
          @selection-change="handleSelectionChange"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import * as RegistrationApi from '@/api/finance/receiptRegistration'
import { cloneDeep } from 'lodash-es'
import * as CommonApi from '@/api/common'

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
console.log(props?.mode, 'mode')
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
let claimSchemas: EplusFormSchema[] = reactive([
  {
    field: 'head',
    label: '付款抬头',
    editable: true,
    span: 8
  },
  {
    field: 'deptName',
    label: '入账单位',
    editable: true,
    span: 8
  },
  {
    field: 'deptName',
    label: '入账银行',
    editable: true,
    span: 8
  },
  {
    field: 'deptName',
    label: '入账币种',
    editable: true,
    span: 8
  },
  {
    field: 'deptName',
    label: '入账金额',
    editable: true,
    span: 8
  },
  {
    field: 'remark',
    label: '备注',
    editable: true,
    span: 8
  },
  {
    field: 'custCode',
    label: '客户编码',
    editable: true,
    span: 8
  },
  {
    field: 'custName',
    label: '客户名称',
    editable: true,
    span: 8,
    component: (
      <eplus-input-search-select
        api={CommonApi.getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择"
      />
    )
  },
  {
    field: 'trackUserName',
    label: '业务员',
    editable: true,
    span: 8
  }
])
const receiptSchemas: EplusFormSchema[] = reactive([
  {
    field: 'receiptItemList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])
const tableColumns = reactive([
  {
    field: 'code',
    label: '订单合同号',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'name',
    label: '客户名称',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'code',
    label: '订单币种',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'paymentType',
    label: '收款方式',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'code',
    label: '来源',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'code',
    label: '应收金额',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'code',
    label: '已收金额',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'code',
    label: '入账应收',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'code',
    label: '本次认领金额',
    width: '150px',
    showOverflowTooltip: false
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
