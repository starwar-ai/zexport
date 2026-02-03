<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="defaultValObj"
    :schema="tableSchema"
    value-key="label"
  />
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
import { DICT_TYPE } from '@/utils/dict'
import { EplusFormMode, EplusFormTableSchema } from '@/components/EplusTemplate'
import * as RegistrationApi from '@/api/finance/receiptRegistration'

defineOptions({ name: 'BankRegistrationList' })

const props = defineProps<{
  mode: EplusFormMode
  formData
}>()
const message = useMessage()
const TableRef = ref()
type BankObjType = {
  bankName: string
  id: number
  bankCode: string
}
let companyBankList: Array<BankObjType> = reactive([])
let tableList = reactive([])

let defaultValObj = reactive({
  bankPostingDate: Date.now()
})

const querySearch = async (query: string, cb: any) => {
  if (query) {
    let res = await RegistrationApi.getBankPocList({ bankPoc: query })
    cb(
      res.map((item) => {
        return { value: item.bankPoc, label: item.bankPoc + '(' + item.custName + ')' }
      })
    )
  } else {
    cb([])
  }
}

const handleSelect = async (row) => {
  if (row.companyTitle.indexOf('(') > 0) {
    row.companyTitle = row.companyTitle.split('(')[0]
  }
}

const tableSchema: QuoteItemComSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '付款抬头',
    field: 'companyTitle',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-autocomplete
          v-model={row.companyTitle}
          fetch-suggestions={querySearch}
          trigger-on-focus={false}
          onSelect={(value) => {
            handleSelect(row)
          }}
          class="!w-100%"
          clearable
          value-key="label"
          placeholder="请输入付款抬头"
        />
      )
    },
    // component: (
    //   <el-autocomplete
    //     fetch-suggestions={querySearch}
    //     trigger-on-focus={false}
    //     class="!w-100%"
    //     clearable
    //     value-key="label"
    //     placeholder="请输入付款抬头"
    //   />
    // ),
    width: 300,
    rules: { required: true, message: '请输入付款抬头' }
  },
  {
    label: '银行入账日期',
    field: 'bankPostingDate',
    component: (
      <el-date-picker
        class="!w-90%"
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
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <el-select v-model={row.bank}>
          {companyBankList &&
            companyBankList?.length &&
            companyBankList?.map((item) => {
              return (
                <el-option
                  key={item.bankName}
                  label={item.bankName}
                  value={item.bankName}
                />
              )
            })}
        </el-select>
      )
    },
    width: 220,
    rules: { required: true, message: '请输入付款抬头' }
  },
  {
    label: '币种',
    field: 'currency',
    width: 150,
    component: (
      <eplus-dict-select
        style="width: 90px"
        dictType={DICT_TYPE.CURRENCY_TYPE}
        clearable={true}
      />
    ),
    rules: { required: true, message: '请选择币种' }
  },
  {
    label: '入账金额',
    field: 'amount',
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={3}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入入账金额' }],
    width: 150
  },
  {
    label: '备注',
    field: 'remark',
    component: (
      <el-input
        autosize
        type="textarea"
        class="!w-90%"
      />
    ),
    width: 240
  },

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

const saveForm = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    let tableData = cloneDeep(TableRef.value.tableData)
    if (tableData?.length === 1 && tableData[0]?.isShow === false) {
      message.warning('登记入账信息提交有误')
      return false
    }
    return tableData
  } else {
    message.warning('登记入账信息提交有误')
    return false
  }
}
const validate = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    return true
  } else {
    return false
  }
}
defineExpose({ saveForm, validate })
watch(props?.formData, (val) => {
  companyBankList = val?.companyBankList
})
</script>
