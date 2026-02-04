<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="defaultValObj"
    :schema="tableSchema"
  />
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
import { DICT_TYPE } from '@/utils/dict'
import { EplusFormMode, EplusFormTableSchema } from '@/components/EplusTemplate'
import * as RegistrationApi from '@/api/finance/receiptRegistration'

defineOptions({ name: 'QuoteItemCom' })
type QuoteItemComSchema = EplusFormTableSchema & {
  isShow?: boolean
}
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
  isShow: false,
  name: '1',
  quoteDate: Date.now()
})
const headChange = async (val, item, row) => {
  if (val) {
    const res = await RegistrationApi.getBankRegistration({ bankPoc: val })
    try {
      row.code = res ? res?.code : ''
      row.name = res ? res?.name : ''
      row.trackUserName = res
        ? res?.managerList
            ?.map((item) => {
              return item.nickname
            })
            ?.join(',')
        : ''
      if (!res) {
        row.isShow = false
      }
    } finally {
      row.isShow = true
      // TableRef.value.validateField(item, row)
    }
  } else {
    row.isShow = false
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
    isShow: true,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <el-input
          v-model={row.companyTitle}
          onChange={(val) => headChange(val, item, row)}
        />
      )
    },
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
    isShow: false,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <el-select
          v-model={row.bank}
          onChange={(val) => headChange(val, item, row)}
        >
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
    component: (
      <eplus-dict-select
        style="width: 90px"
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
        class="!w-90%"
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
        class="!w-90%"
      />
    ),
    width: 240
  },
  // {
  //   label: '客户/供应商编号',
  //   field: 'code',
  //   width: 240,
  //   slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
  //     return <>{row?.code}</>
  //   }
  // },
  // {
  //   label: '客户/供应商名称',
  //   field: 'name',
  //   width: 240,
  //   slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
  //     return <>{row?.name}</>
  //   }
  // },
  // {
  //   label: '业务员',
  //   field: 'trackUserName',
  //   width: 240,
  //   slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
  //     return <>{row?.trackUserName}</>
  //   }
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
            type="success"
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
  console.log(valid)
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
