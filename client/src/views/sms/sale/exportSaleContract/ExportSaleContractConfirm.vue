<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="confirmDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
  >
    <!-- <div class="content-box">
      <div class="left-table"> </div>
      <div calss="details"> </div>
    </div> -->
    <el-row :gutter="20">
      <el-col :span="4">
        <Table
          :columns="pocColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo.children"
        />
      </el-col>
      <el-col :span="20">
        <component
          :is="currentComponent"
          :key="saleId"
          :id="saleId"
          :isFromComfirmPage="true"
        />
      </el-col>
    </el-row>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/export-sale-contract'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import * as ShipmentApi from '@/api/dms/shipment/index'
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn } from '@/utils/table'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import * as AuxiliaryApi from '@/api/scm/auxiliaryPurchaseContract'
import CustChangeDetail from '@/views/crm/custChange/CustChangeDetail.vue' //客户
import VenderChangeDetail from '@/views/scm/vender/components/VenderChangeDetail.vue' // 供应商
import CustSkuChangeDetail from '@/views/pms/product/custSkuChange/custSkuChangeDetail.vue' // 产品
import ShippDetail from '@/views/dms/shippingChange/ChangeDetail.vue' // 出运
import SaleContractChangeDetail from '@/views/sms/sale/exportSaleContractChange/ExportSaleContractChangeDetail.vue' // 销售
import PurchaseChangeDetail from '@/views/scm/purchase/change/ChangeDetail.vue' // 采购
import { EplusDetail } from '@/components/EplusTemplate'

const showProcessInstanceTaskListFlag = ref(false)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()

const props = defineProps<{
  title?: string
  id?: number
  row?: Object
  type?: any
}>()
const { close, goEdit, goChange } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val, type) => void
      goChange: (val, type) => void
    })
  : { close: () => {}, goEdit: () => {}, goChange: () => {} }

const emit = defineEmits(['handleSuccess', 'success'])

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const confirmDetail = ref({})
const saleId = ref()
const currentComponent = ref()

const components = {
  CustChangeDetail,
  VenderChangeDetail,
  CustSkuChangeDetail,
  ShippDetail,
  SaleContractChangeDetail,
  PurchaseChangeDetail
}
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()

const pageInfo = reactive({
  cust: true,
  sku: true,
  vender: true,
  children: []
})

const pocColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'code',
    label: '变更来源编号'
  },
  {
    field: 'confirmSourceType',
    label: '变更来源类型',
    formatter: formatDictColumn(DICT_TYPE.EFFECT_RANGE)
  },
  {
    label: '操作',
    width: 60,
    fixed: 'right',
    slots: {
      default: (data) => {
        return (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={async () => {
                handleDatail(data.row)
              }}
            >
              详情
            </el-button>
          </div>
        )
      }
    }
  }
])

const loading = ref(true)

const handleDatail = (data) => {
  clearDialogActions()
  setDialogActions()
  saleId.value = data.id
  setDetails(data)
}

const setDetails = (data) => {
  switch (data?.confirmSourceType) {
    case 1: //销售
      currentComponent.value = SaleContractChangeDetail
      break
    case 2: // 采购
      currentComponent.value = PurchaseChangeDetail
      break
    case 3: // 出运
      currentComponent.value = ShippDetail
      break
    case 4: //客户
      currentComponent.value = CustChangeDetail
      break
    case 5: // 供应商
      currentComponent.value = VenderChangeDetail
      break
    case 6: // 产品
      currentComponent.value = CustSkuChangeDetail
      break
  }
}

if (query?.id) {
  showProcessInstanceTaskListFlag.value = false
  outDialogFlag.value = true
}
if (props.id) {
  showProcessInstanceTaskListFlag.value = false
  outDialogFlag.value = false
}

const getInfo = async () => {
  confirmDetail.value = props?.row
  try {
    switch (props?.type) {
      case 1:
        pageInfo.children = await ExportSaleContractApi.getConfirmSource({ id: props.id })
        break
      case 2:
        pageInfo.children = await PurchaseContractApi.getPurchaseConfirmSource({ id: props.id })
        break
      case 3:
        pageInfo.children = await ShipmentApi.getConfirmSource({ id: props.id })
        break
      case 4:
        pageInfo.children = await AuxiliaryApi.getAuxiliaryConfirmSource({ id: props.id })
        break
    }

    saleId.value = pageInfo?.children[0]?.id
    setDetails(pageInfo?.children[0])
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  await getInfo()
  nextTick(() => {
    setDialogActions()
  })
})

const setDialogActions = () => {
  if (props?.type === 2) {
    if (props?.row?.changeType || props?.row.status === 1) {
      updateDialogActions(
        <el-button
          onClick={() => {
            toEdit()
          }}
          key="change"
        >
          我已知晓并变更
        </el-button>
      )
    }
  } else {
    if (props?.row?.changeType || props?.row.status === 1) {
      updateDialogActions(
        <el-button
          onClick={() => {
            toEdit()
          }}
          key="change"
        >
          我已知晓并变更
        </el-button>,
        <el-button
          onClick={() => {
            handleChangeConfirm()
          }}
          key="unchange"
        >
          我已知晓
        </el-button>
      )
    } else {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleChangeConfirm()
          }}
          key="unchange"
        >
          我已知晓
        </el-button>
      )
    }
  }
}
const toEdit = () => {
  // 采购 4.5 待下单  待到货
  // 销售 3.5.6  回签 采购 出运
  // 出运 5 确认出运
  if (props?.row?.changeType) {
    close()
    emit('success', { id: props.id, type: 'change' })
    return false
  }
  if (props?.row.status == 1) {
    if (props?.type === 1) {
      goChange('变更', 'change')
    } else if (props?.type === 2) {
      goEdit('变更', 'change')
    } else if (props?.type === 3) {
      goEdit('变更', 'change')
    } else if (props?.type === 4) {
      goChange('包材采购合同变更', 'change')
    }
  }
}

const handleChangeConfirm = async () => {
  let url
  switch (props?.type) {
    case 1:
      url = ExportSaleContractApi.changeConfirm(props.id)
      break
    case 2:
      url = PurchaseContractApi.changeConfirm(props.id)
      break
    case 3:
      url = ShipmentApi.changeConfirm(props.id)
      break
    case 4:
      url = AuxiliaryApi.changeConfirm(props.id)
      break
  }
  try {
    await url
    message.success('确认成功')
    close()
    emit('handleSuccess')
  } catch (error) {
    message.error('确认失败')
  }
}
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: #fff;
}

.content-box {
  display: flex;
}

.left-table {
  width: 25%;
  // background: #ccc;
  margin: 20px 20px 0 0;
}

.details {
  flex: 1;
}
</style>
