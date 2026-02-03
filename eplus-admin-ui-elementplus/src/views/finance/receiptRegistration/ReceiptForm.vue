<template>
  <eplus-form
    ref="formRef"
    v-bind="{ id: id, loading: loading, mode: mode }"
    v-model="formData"
    :saveAction="{
      permi: ['fms:bank-registration:create', 'fms:bank-registration:update'],
      handler: submitForm
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="receiptSchemas"
    />

    <eplus-form-items
      v-if="props.mode === 'edit'"
      title="登记入账信息"
      :formSchemas="editRegistrationSchemas"
    />
    <eplus-form-items
      v-else
      title="登记入账信息"
      :formSchemas="registrationListSchemas"
    >
      <template #registrationList>
        <BankRegistrationList
          :mode="mode"
          :formData="formData"
          ref="QuoteItemComRef"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import * as CommonApi from '@/api/common'
import BankRegistrationList from './components/BankRegistrationList.vue'
import * as RegistrationApi from '@/api/finance/receiptRegistration'
import { cloneDeep } from 'lodash-es'
import { useUserStore } from '@/store/modules/user'

const message = useMessage()
const formRef = ref()
const loading = ref(false)
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  param?
}>()

const QuoteItemComRef = ref()
const { close } = inject('dialogEmits') as {
  close: () => void
}
const queryId: string = (props.id || '') as string
interface FormData {
  code: string
  companyBankList: Array<[]>
  companyName: string
  nickname: string
  deptName: string
  registeredBy: any
  bank: string
  registrationDate: any
}
let formData = reactive<FormData>({
  code: '',
  companyBankList: [],
  companyName: '',
  nickname: '',
  deptName: '',
  registeredBy: {},
  bank: '',
  registrationDate: Date.now()
})

provide('formData', formData)

let receiptSchemas: EplusFormSchema[] = reactive([
  {
    field: 'nickname',
    label: '登记人',
    component: <el-input disabled />
  },
  {
    field: 'deptName',
    label: '部门名称',
    component: <el-input disabled />
  },
  {
    field: 'registrationDate',
    label: '登记日期',
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
    ),
    rules: [
      {
        required: true,
        message: '请选择登记日期'
      }
    ]
  },
  {
    field: 'companyId',
    label: '入账单位',
    component: (
      <eplus-custom-select
        api={CommonApi.getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择入账单位"
        class="!w-100%"
        clearable={false}
        onChange={(val) => {
          formData.companyName = val?.name
          formData.companyBankList = val?.companyBankList
        }}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择入账单位'
      }
    ]
  }
])
const registrationListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'registrationList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])
const headChange = (val) => {
  formData.bank = val
}
const getBackPoc = async (query: string, cb: any) => {
  if (query) {
    let res = await RegistrationApi.getBankPocList({ bankPoc: query })
    cb(
      res.map((item) => {
        return { value: item }
      })
    )
  } else {
    cb([])
  }
}
const editRegistrationSchemas: EplusFormSchema[] = reactive([
  {
    label: '付款抬头',
    field: 'companyTitle',
    component: (
      <el-autocomplete
        fetch-suggestions={getBackPoc}
        trigger-on-focus={false}
        class="!w-100%"
        clearable
        placeholder="请输入付款抬头"
      />
    ),
    rules: { required: true, message: '请输入付款抬头' }
  },
  {
    label: '银行入账日期',
    field: 'bankPostingDate',
    component: (
      <el-date-picker
        class="!w-100%"
        type="date"
        placeholder="请选择"
        value-format="x"
      />
    ),
    width: 220,
    rules: { required: true, message: '请选择日期' }
  },
  {
    label: '入账银行账户',
    field: 'bank',
    isShow: false,
    component: (
      <el-select
        v-model={formData.bank}
        onChange={(val) => headChange(val)}
        style="width:100%"
      >
        {formData?.companyBankList &&
          formData.companyBankList?.length &&
          formData.companyBankList?.map((item: any) => {
            return (
              <el-option
                key={item.bankName}
                label={item.bankName}
                value={item.bankName}
              />
            )
          })}
      </el-select>
    ),
    rules: { required: true, message: '请输入付款抬头' }
  },
  {
    label: '币种',
    field: 'currency',
    width: 200,
    component: (
      <eplus-dict-select
        style="width: 100%"
        dictType={DICT_TYPE.CURRENCY_TYPE}
        clearable={true}
      />
    )
  },
  {
    label: '入账金额',
    field: 'amount',
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={3}
        class="!w-100%"
      />
    ),
    rules: [{ required: true, message: '请输入入账金额' }],
    width: 120
  },
  {
    label: '备注',
    field: 'remark',
    component: (
      <el-input
        autosize
        type="textarea"
        class="!w-100%"
      />
    ),
    width: 240
  }
])
onMounted(async () => {
  try {
    if (queryId) {
      let res = await RegistrationApi.getRegistration({ id: queryId })
      const { registeredBy } = res
      formData.nickname = registeredBy?.nickname
      formData.deptName = registeredBy?.deptName
      Object.assign(formData, res)
    } else {
      const user = useUserStore().getUser
      formData.registeredBy = user
      formData.nickname = user.nickname
      formData.deptName = user.deptName
    }
    await nextTick()
  } catch {
  } finally {
  }
})
const getParams = async () => {
  let params: any = cloneDeep(formData)
  const { registeredBy, registrationDate, companyId, companyName } = params
  if (props.mode === 'edit') {
    return params
  } else {
    let result = await QuoteItemComRef.value?.saveForm()
    if (!result) return false
    const resultList = result?.map((item) => {
      return { registeredBy, registrationDate, companyName, companyId, ...item }
    })
    return { bankRegistrationList: resultList }
  }
}
const submitForm = async () => {
  try {
    let valid = await formRef.value.validate()
    let tableValid = await QuoteItemComRef.value?.validate()
    if (props.mode !== 'edit' && (!valid || !tableValid)) return false
    let result: any = await getParams()
    if (result) {
      try {
        if (queryId) {
          await RegistrationApi.updateRegistration({
            claimStatus: 0,
            ...result,
            submitFlag: 1
          })
        } else {
          await RegistrationApi.createRegistration({
            ...result,
            submitFlag: 1
          })
        }
        message.success('提交成功')
        close()
      } catch {}
    }
  } finally {
    loading.value = false
  }
}
</script>
<style scoped lang="scss"></style>
