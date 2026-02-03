<template>
  <Dialog
    v-model="dialogTableVisible"
    title="批量计划付款"
    width="80%"
    append-to-body
    destroy-on-close
  >
    <eplus-description
      title=""
      :data="formDesc"
      :items="formDescSchemas"
    />
    <eplus-form-table
      ref="TableRef"
      :list="tableList"
      :defaultVal="{}"
      :schema="tableColumns"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { cloneDeep } from 'lodash-es'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { columnWidth } from '@/utils/table'
import { getAcountConfig } from '@/api/infra/acountConfig'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import EplusNumInput from '@/components/EplusMoney/src/EplusNumInput.vue'
import { useUserStore } from '@/store/modules/user'
import { batchPlanPayment } from '@/api/finance/fees'

const message = useMessage()

const dialogTableVisible = ref(false)
const TableRef = ref()
const tableList = ref([])
const bankList = ref([])

const formDescSchemas = [
  {
    field: 'companyName',
    label: '付款单位',
    span: 8
  },
  {
    field: 'inputUser',
    label: '付款人',
    span: 12
  }
]

let tableColumns = reactive<TableColumn[]>([
  {
    field: 'code',
    label: '付款单号',
    minWidth: columnWidth.l
  },
  {
    field: 'businessType',
    label: '费用类型',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.BUSINESS_TYPE, row.businessType)
    }
  },
  {
    field: 'businessSubjectName',
    label: '付款对象名称',
    minWidth: columnWidth.l
  },
  {
    field: 'bankAccount',
    label: '银行账户',
    minWidth: columnWidth.l
  },
  {
    field: 'applyAmount',
    label: '付款金额',
    minWidth: columnWidth.m,
    formatter: (item, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.applyAmount} />
    }
  },
  {
    field: 'paidAmount',
    label: '已付金额',
    minWidth: columnWidth.m,
    formatter: (item, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.paidAmount} />
    }
  },

  {
    field: 'amount',
    label: '计划付款金额',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.amount}
          min={0}
          max={row.maxAmount}
        />
      )
    },
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (value <= 0) {
          callback(new Error(`付款金额必须大于0`))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'currency',
    label: '支付币种',
    minWidth: columnWidth.m
  },
  {
    field: 'paymentMethod',
    label: '支付方式',
    batchEditFlag: true,
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.paymentMethod}
          dictType={DICT_TYPE.PAY_METHOD}
          clearable
          onChangeEmit={(val) => {
            row.acceptanceDays = undefined
          }}
        />
      )
    },
    rules: [{ required: true, message: '请选择支付方式' }]
  },
  {
    field: 'acceptanceDays',
    label: '承兑天数',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.acceptanceDays}
          dictType={DICT_TYPE.ACCEPTANCE_DAYS}
          disabled={row.paymentMethod !== 3}
        />
      )
    }
  },
  {
    field: 'bank',
    label: '付款银行',
    batchEditFlag: true,
    batchEditCom: (
      <eplus-input-select
        dataList={bankList}
        label="bankName"
        value="bankName"
        formatLabel={(el) => `${el.bankName}（${el.bankCode.slice(-4)}）`}
      />
    ),
    minWidth: columnWidth.xl,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-input-select
          v-model={row.bank}
          dataList={bankList.value}
          label="bankName"
          value="bankName"
          onChangeEmit={(...$event) => changeBank($event, row)}
          formatLabel={(el) => `${el.bankName}（${el.bankCode.slice(-4)}）`}
        />
      )
    }
  }
])

const changeBank = (val, row) => {
  if (row.bank) {
    let bankObj: any = bankList.value.find((item: any) => item.bankName === row.bank)
    row.bankAccount = bankObj.bankCode
  }
}
const formDesc = ref()
const open = async (list) => {
  formDesc.value = {
    companyName: list[0]?.companyName,
    inputUser: useUserStore().getUser?.nickname
  }
  let accountRes = await getAcountConfig({ id: list[0]?.companyId })
  bankList.value = accountRes?.companyBankList
  tableList.value = cloneDeep(list)
  tableList.value.forEach((item: any) => {
    item.amount = item.applyAmount[0]?.amount - item.paidAmount?.amount || 0
    item.maxAmount = item.applyAmount[0]?.amount - item.paidAmount?.amount || 0
    item.currency = item.applyAmount[0]?.currency
    item.paymentMethod = item.paymentMethod || 2
    item.bank = item.paymentBank
    item.bankAccount = item.paymentBackAccount
  })
  dialogTableVisible.value = true
}

const handleCancel = () => {
  dialogTableVisible.value = false
}
const emit = defineEmits<{
  success
}>()
const handleSure = async () => {
  let valid = await TableRef.value.validate()
  if (valid && tableList.value?.length > 0) {
    for (let i = 0; i < tableList.value.length; i++) {
      const item: any = tableList.value[i]
      if (getDictLabel(DICT_TYPE.PAY_METHOD, item.paymentMethod) === '承兑汇票') {
        if (!item.acceptanceDays) {
          message.warning(`第${i + 1}条数据请选择承兑天数`)
          return
        }
      }
      if (getDictLabel(DICT_TYPE.PAY_METHOD, item.paymentMethod) !== '现金') {
        if (!item.bank) {
          message.warning(`第${i + 1}条数据请选择付款银行`)
          return
        }
      }
    }
    let params = tableList.value.map((item: any) => {
      return {
        paymentId: item.id,
        amount: { amount: item.amount, currency: item.currency },
        companyId: item.companyId,
        bank: item.bank,
        bankAccount: item.bankAccount,
        paymentMethod: item.paymentMethod
      }
    })

    await batchPlanPayment(params)
    message.success('操作成功')
    emit('success')
    dialogTableVisible.value = false
  } else {
    message.warning('请核验信息')
  }
}
defineExpose({ open })
</script>
