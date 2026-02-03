<template>
  <div
    class="mb15px"
    v-if="editFlag"
  >
    <el-button
      v-if="!isEdit"
      type="primary"
      @click="isEdit = !isEdit"
    >
      编辑
    </el-button>
  </div>
  <Table
    v-if="!isEdit"
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="info?.forwarderFeeList"
  />

  <ForwarderFeeCom
    v-if="isEdit"
    :formData="info"
    @save="handleSave"
    @cancel="isEdit = !isEdit"
    channel="detail"
    ref="ForwarderFeeComRef"
  />
</template>
<script setup lang="tsx">
import { formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import ForwarderFeeCom from './ForwarderFee.vue'
import * as ShipmentApi from '@/api/dms/shipment/index'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'ForwarderFeeDetail' })
const props = defineProps<{
  info: any
  readonly?: boolean
}>()

const tableColumns = [
  {
    field: 'venderName',
    label: '船代公司'
  },
  {
    field: 'dictSubjectName',
    label: '费用名称'
  },
  // {
  //   field: 'feeType',
  //   label: '费用类型',
  //   formatter: formatDictColumn(DICT_TYPE.FORWARDER_FEE_TYPE)
  // },
  {
    field: 'amount',
    label: '金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'payStatus',
    label: '申请状态',
    formatter: formatDictColumn(DICT_TYPE.APPLY_FEE_STATUS)
  },
  {
    field: 'paymentStatus',
    label: '支付状态',
    formatter: formatDictColumn(DICT_TYPE.PAYMENT_STATUS)
  },
  {
    field: 'paymentDate',
    label: '支付日期',
    formatter: formatDateColumn()
  }
]

const isEdit = ref(false)
const ForwarderFeeComRef = ref()
const emit = defineEmits(['refresh'])
const handleSave = async (data) => {
  await ShipmentApi.forwarderFeeUpdate(data)
  isEdit.value = false
  emit('refresh')
}
const editFlag = ref(false)
const permissions = useUserStore().getPermissions
watchEffect(() => {
  editFlag.value = permissions.includes('dms:shipment:forwarder-fee-update') && !props.readonly
  isEdit.value = false
})
</script>
<style lang="scss" scoped>
.btn_box {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>
