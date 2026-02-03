<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
    :destroyOnClose="true"
  >
    <template #edit="{ key, param }">
      <ExportSaleContractForm
        :type="param"
        :id="key"
        mode="edit"
        :contractType="type"
        @success="handleRefresh"
      />
    </template>
    <template #change="{ key, param }">
      <ExportSaleContractForm
        :type="param"
        :id="key"
        mode="change"
        :contractType="type"
        @success="handleRefresh"
      />
    </template>
    <template #copy="{ key, param }">
      <ExportSaleContractForm
        :type="param"
        :id="key"
        mode="copy"
        :contractType="type"
        @handle-success="handleRefresh"
      />
    </template>
    <template #confirm="{ key }">
      <ExportSaleContractConfirm
        :id="key"
        mode="confirm"
        :row="confirmRow"
        :type="1"
        @handle-success="handleRefresh"
        @success="backChange"
      />
    </template>
  </eplus-dialog>

  <eplus-dialog
    ref="planDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
    :destroyOnClose="true"
  >
    <template #create>
      <ShippingPlanForm
        :params="planParams"
        mode="create"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import ExportSaleContractForm from '../ExportSaleContractForm.vue'
import ShippingPlanForm from '@/views/dms/shippingPlan/ShippingPlanForm.vue'
import ExportSaleContractConfirm from '../ExportSaleContractConfirm.vue'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import { SaleContractStatusEnum } from '@/utils/constants'

const emit = defineEmits(['success'])
const eplusDialogRef = ref()
const handleRefresh = () => {
  emit('success')
}
const type = ref(1)

const handleUpdate = (id: number, contractType = 1) => {
  type.value = contractType
  eplusDialogRef.value?.openEdit(
    id,
    contractType == 1 ? '外销合同' : contractType == 2 ? '内销合同' : '外币采购'
  )
}

const handleChange = (id: number, contractType = 1) => {
  type.value = contractType
  eplusDialogRef.value?.openChange(
    id,
    contractType == 1 ? '外销合同变更' : contractType == 2 ? '内销合同变更' : '外币采购变更',
    'change'
  )
}

const handleCopy = (id: number, contractType = 1) => {
  type.value = contractType
  eplusDialogRef.value?.openCopy(
    id,
    contractType == 1 ? '外销合同' : contractType == 2 ? '内销合同' : '外币采购'
  )
}
const confirmRow = ref({})
const confirmChange = (row, contractType) => {
  type.value = contractType
  confirmRow.value = {
    ...row,
    changeType: [
      SaleContractStatusEnum.INSINGBACK.status,
      SaleContractStatusEnum.INPURCHASE.status,
      SaleContractStatusEnum.INSHIPPING.status
    ].includes(row?.status)
  }
  // INSINGBACK.status 待回签
  // INPURCHASE.status 待采购
  // INSHIPPING.status 待出运
  eplusDialogRef.value?.openConfirm(row.id, '确认')
}
const backChange = (data) => {
  handleChange(data.id, type.value)
}

const planDialogRef = ref()
const planParams = ref({})

const createShipmentPlan = async (row) => {
  planParams.value = {
    companyId: row.foreignTradeCompanyId,
    companyName: row.foreignTradeCompanyName,
    childrenIdList: row.children
      ?.filter((obj) => Number(obj.quantity) - Number(obj.shippedQuantity) > 0)
      .map((item) => item.id)
      .join(',')
  }
  await ExportSaleContractApi.checkContractStatus(planParams.value)
  planDialogRef.value?.openCreate('出运计划')
}

defineExpose({
  handleUpdate,
  handleChange,
  handleCopy,
  createShipmentPlan,
  confirmChange
})
</script>
