<template>
  <Dialog
    v-model="dialogTableVisible"
    title="付款计划"
    width="800"
    append-to-body
    destroy-on-close
  >
    <Table
      :columns="tableColumns"
      headerAlign="center"
      align="center"
      :data="tableList"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="handleCancel"
          >关闭
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { paymentPlanList } from '@/api/scm/purchaseContract'

import { columnWidth, formatDictColumn, formatMoneyColumn } from '@/utils/table'
// import { formatterPrice } from '@/utils/index'
// import { getDictLabel } from '@/utils/dict'
// import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'PaymentPlanListDia' })
const dialogTableVisible = ref(false)
const tableList = ref([])
const open = async (code) => {
  let res = await paymentPlanList(code)
  tableList.value = res || []
  dialogTableVisible.value = true
}

const tableColumns = reactive([
  {
    field: 'contractCode',
    label: '采购合同号',
    minWidth: columnWidth.l
  },
  {
    field: 'step',
    label: '步骤',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.PAYMENT_PLAN_STEP)
  },
  {
    field: 'paymentRatio',
    label: '付款比例%',
    minWidth: columnWidth.l
  },
  {
    field: 'receivableAmount',
    label: '应付金额',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'appliedAmount',
    label: '已申请金额',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'receivedAmount',
    label: '已付金额',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  }
])

const handleCancel = () => {
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
