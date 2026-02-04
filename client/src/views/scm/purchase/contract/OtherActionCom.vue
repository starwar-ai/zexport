<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
    :destroyOnClose="true"
  >
    <template #edit="{ key, param }">
      <ContractForm
        :type="param"
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <ToWareHouseDialog
    ref="ToWareHouseDialogRef"
    @sure="handleRefresh"
  />
  <ToInspectDialog
    ref="ToInspectDialogRef"
    @sure="handleRefresh"
  />
  <ToPaymentApplyDialog
    ref="ToPaymentApplyDialogRef"
    @sure="handleRefresh"
  />
  <SignBackForm
    ref="signBackRef"
    @success="handleRefresh"
  />
  <ToOrderNotice
    ref="ToOrderNoticeRef"
    @success="handleRefresh"
  />

  <ToAuxiliaryPlan
    ref="ToAuxiliaryPlanRef"
    @success="handleRefresh"
  />

  <ProducedDia
    ref="ProducedDiaRef"
    @success="handleRefresh"
  />
</template>
<script setup lang="tsx">
import ContractForm from './ContractForm.vue'
import ToWareHouseDialog from './ToWareHouseDialog.vue'
import ToInspectDialog from './ToInspectDialog.vue'
import ToPaymentApplyDialog from '../components/ToPaymentApplyDialog.vue'
import SignBackForm from '../components/SignBackForm.vue'
import ToOrderNotice from '../components/ToOrderNotice.vue'
import ToAuxiliaryPlan from './ToAuxiliaryPlan.vue'
import ProducedDia from './ProducedDia.vue'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'

const eplusDialogRef = ref()
const ToWareHouseDialogRef = ref()
const ToInspectDialogRef = ref()
const ToPaymentApplyDialogRef = ref()
const signBackRef = ref()
const ToOrderNoticeRef = ref()
const ToAuxiliaryPlanRef = ref()
const ProducedDiaRef = ref()

const message = useMessage()

const detailAction = async (data) => {
  if (data.type === 'toPaymentApply') {
    ToPaymentApplyDialogRef.value.open(data.params)
  } else if (data.type === 'toWareHouse') {
    ToWareHouseDialogRef.value.open(data.params?.map((item) => item?.id))
  } else if (data.type === 'toAuxiliaryPlan') {
    ToAuxiliaryPlanRef.value.open(data.params?.map((item) => item?.id).join(','))
  } else if (data.type === 'signBackContract') {
    handleSignBack(data.params)
  } else if (data.type === 'produceCompleted') {
    ProducedDiaRef.value.open(data.params)
  } else if (data.type === 'checkContract') {
    ToInspectDialogRef.value.open(data.params?.id)
  } else if (data.type === 'exchangeContract') {
    handleExchange(data.params)
  } else if (data.type === 'toOrderNotice') {
    ToOrderNoticeRef.value.open(data.params)
  } else if (data.type === 'changeContract') {
    handleUpdate(data.params.id, 'change')
  } else if (data.type === 'scmPurchaseContractFinish') {
    handleClose(data.params)
  }
}

const handleSignBack = async (row, title = '回签') => {
  let obj = {
    id: row.id,
    code: row.code,
    createTime: row.createTime
  }
  signBackRef.value.open(title, obj)
}

const handleExchange = async (row) => {
  await message.confirm('是否确认退换货？')
  await PurchaseContractApi.exchangePurchaseContract([row.id])
  message.success('退换货成功')
  handleRefresh()
}

const handleClose = async (row) => {
  await message.confirm('是否确认作废？')
  await PurchaseContractApi.batchFinishPurchaseContract([row.id])
  message.success('退换货成功')
  handleRefresh()
}

const handleUpdate = (id: number, type?) => {
  eplusDialogRef.value?.openEdit(id, type === 'change' ? '变更' : '采购合同', type)
}
const emit = defineEmits(['success'])
const handleRefresh = () => {
  emit('success')
}

defineExpose({
  handleUpdate,
  detailAction
})
</script>
