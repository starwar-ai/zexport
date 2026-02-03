<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :auditable="receiptDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="receiptDetail"
      :items="receiptSchemas"
    >
      <template #registeredBy>
        {{ receiptDetail?.registeredBy?.nickname ?? '' }}
        ( {{ receiptDetail?.registeredBy?.deptName }})
      </template>
    </eplus-description>
    <eplus-description
      title="登记入账信息"
      :data="receiptDetail"
      :items="editRegistrationSchemas"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as RegistrationApi from '@/api/finance/receiptRegistration'

const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()
const props = defineProps<{
  type: string
  title?: string
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

interface registeredBy {
  nickname: string
}
interface ReceiptDetail {
  creator: string
  annex: any[]
  children: any[]
  contractList: any[]
  registeredBy: registeredBy
}
const receiptDetail = ref<ReceiptDetail>({
  creator: '',
  annex: [],
  children: [],
  contractList: [],
  registeredBy: { nickname: '' }
})
//采购计划信息
const receiptSchemas = reactive([
  {
    field: 'registeredBy',
    label: '登记人',
    slotName: 'registeredBy'
  },
  {
    field: 'registrationDate',
    label: '登记日期',
    type: 'date'
  },
  {
    field: 'companyName',
    label: '入账单位'
  },
  {
    field: 'claimStatus',
    label: '认领状态',
    dictType: DICT_TYPE.CLAIM_STATUS
  }
])
//采购计划附件信息
const editRegistrationSchemas = [
  {
    label: '付款抬头',
    field: 'companyTitle'
  },
  {
    label: '银行入账日期',
    field: 'bankPostingDate',
    type: 'date'
  },
  {
    label: '入账银行账户',
    field: 'bank'
  },
  {
    label: '币种',
    field: 'currency'
  },
  {
    label: '入账金额',
    field: 'amount',
    type: 'num'
  },
  {
    label: '已认领金额',
    field: 'claimedAmount',
    type: 'num'
  },
  {
    label: '备注',
    field: 'remark'
  }
]
const loading = ref(true)
const getreceiptDetail = () => {}
const handleUpdate = async () => {
  await getreceiptDetail()
}

const getInfo = async () => {
  loading.value = true
  try {
    receiptDetail.value = await RegistrationApi.getRegistration({ id: props?.id })
  } finally {
    loading.value = false
  }
}

const { updateDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('收款登记ID不能为空')
    if (!props.id) {
      close()
    }
  }
  showProcessInstanceTaskListFlag.value = true
  outDialogFlag.value = false
  await getInfo()
  if (receiptDetail.value?.claimStatus == 0) {
    updateDialogActions(
      <el-button
        onClick={() => {
          goEdit('收款登记')
        }}
      >
        编辑
      </el-button>
    )
  }
})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}
</style>
