<template>
  <div :class="[prefixCls]">
    <Dialog
      v-model="dialogVisible"
      append-to="#appView"
      :destroy-on-close="true"
      title="还款申请单"
    >
      <el-form
        :model="formData"
        :loading="loading"
        ref="formRef"
      >
        <eplus-description
          title=""
          :data="formData"
          :items="BasicInfoSchema"
        >
          <template #amount>
            <span>{{ formData?.amount?.amount ? formData.amount.amount : '' }}</span
            >&nbsp;
            <span>{{ formData?.amount?.currency ? formData.amount.currency : '' }}</span>
          </template>
          <template #repayer>
            <span>{{
              formData?.repayer?.nickname ? formData?.repayer?.nickname : defaultData
            }}</span>
            <span>{{
              '(' + (formData?.repayer?.deptName ? formData?.repayer?.deptName : defaultData) + ')'
            }}</span>
          </template>
        </eplus-description>
        <!-- <eplus-form-items
          title="银行账户信息"
          :formSchemas="bankAccountSchemas"
          v-if="formData?.repayType === 2"
        >
          <template #bankaccountList>
            <Table
              :columns="bankColumns"
              headerAlign="center"
              align="center"
              :data="formData?.bankaccountList"
            />
          </template>
        </eplus-form-items> -->
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="payAction"
          >
            确认收款
          </el-button>
          <el-button @click="close">取消</el-button>
        </div>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="tsx">
import { useDesign } from '@/hooks/web/useDesign'
import { propTypes } from '@/utils/propTypes'
import * as ReceiptApi from '@/api/finance/receipt'
import * as RepayAppApi from '@/api/oa/repayapp'
import { DICT_TYPE } from '@/utils/dict'

const message = useMessage()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('eplus-dialog')
const props = defineProps({
  destroyOnClose: propTypes.bool.def(true),
  beforeClose: propTypes.string.def('')
})
const formData = ref({})
provide('formData', formData)
const loading = ref(false)
const emit = defineEmits(['success', 'failure'])
const dialogVisible = ref<boolean>(false)
const defaultData = '-'
let BasicInfoSchema = [
  {
    field: 'code',
    label: '还款单号',
    span: 12
  },
  {
    field: 'amount',
    label: '本次还款金额',
    span: 12,
    slotName: 'amount'
  },
  {
    field: 'repayType',
    label: '还款方式',
    span: 12,
    dictType: DICT_TYPE.LOAN_TYPE
  },
  {
    field: 'repayer',
    label: '申请人',
    span: 12,
    slotName: 'repayer'
  },
  {
    field: 'repayTime',
    label: '申请时间',
    span: 12,
    type: 'time'
  }
]
//银行账户信息
const bankAccountSchemas = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
const bankColumns = reactive([
  {
    field: 'bank',
    label: '开户行'
  },
  {
    field: 'bankAddress',
    label: '开户行地址'
  },
  {
    field: 'bankPoc',
    label: '账户名称'
  },
  {
    field: 'bankAccount',
    label: '账户号码'
  },
  {
    field: 'bankCode',
    label: '银行行号'
  }
])

const payAction = async () => {
  await ReceiptApi.confirmReceipt(formData.value?.receiptId)
    .then(() => {
      message.success('确认收款成功')
      emit('success')
      close()
    })
    .catch(() => {
      message.warning('确认收款失败，请重试')
      close()
    })
}
const close = async () => {
  dialogVisible.value = false
}
const open = async (code, id) => {
  dialogVisible.value = true
  await RepayAppApi.getRepayAppBycode(code).then((res) => {
    const { bank, bankAccount, bankAddress, bankPoc, bankCode } = res
    formData.value = {
      ...res,
      bankaccountList: [{ bank, bankAccount, bankAddress, bankPoc, bankCode }],
      receiptId: id
    }
  })
}
defineExpose({ open, close })

onMounted(() => {})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-eplus-dialog;

.#{$prefix-cls} {
  :deep(.el-overlay),
  :deep(.el-overlay-dialog) {
    position: absolute;
  }

  :deep(.el-dialog) {
    display: flex;
    flex-direction: column;
  }

  :deep(.el-dialog__body) {
    flex: 1 1 auto;
    overflow: auto;
  }
  .el-form {
    padding-top: 15px;
  }
}
</style>
