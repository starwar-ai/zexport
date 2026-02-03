<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="pageType === 'add' ? '分摊' : '取消分摊'"
    width="800"
    append-to-body
    destroy-on-close
  >
    <div
      class="flex items-center justify-between"
      v-if="pageType === 'add'"
    >
      <p>分摊金额：<EplusMoneyLabel :val="formData?.totalAmount" /> </p>
      <p>
        分摊规则：
        <el-select
          v-model="ruleType"
          placeholder="Select"
          style="width: 240px"
          :clearable="false"
          @change="ruleTypeChange"
        >
          <el-option
            label="自定义"
            :value="1"
          />
          <el-option
            label="按数量分摊"
            :value="2"
          />
        </el-select>
      </p>
    </div>
    <Table
      :columns="pageType === 'add' ? tableSchema : tableColumns"
      headerAlign="center"
      align="center"
      :data="formData?.list || tableList"
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
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import { formatMoneyColumn } from '@/utils/table'
import { formatterPrice } from '@/utils/index'

defineOptions({ name: 'ShareDia' })
const dialogTableVisible = ref(false)
const message = useMessage()
const pageType = ref('add')
const ruleType = ref(1)
const formData = ref({})
const tableList = ref([])
const open = async (obj, type) => {
  pageType.value = type
  formData.value = obj
  if (type === 'add') {
    ruleType.value = 1
    setItemAmount()
  } else {
    await getTableData(obj)
  }

  dialogTableVisible.value = true
}

const getTableData = async (obj) => {
  tableList.value = await ExportSaleContractApi.allocationInfo(obj)
}

const setItemAmount = () => {
  let TotalQuantity = 0
  formData.value?.list.forEach((item) => {
    TotalQuantity += item.quantity
  })
  formData.value?.list.forEach((item) => {
    item.itemAmount = 0
    item.quantityAmount = formatterPrice(
      (formData.value.totalAmount.amount / TotalQuantity) * item.quantity,
      2
    )
  })
}

const ruleTypeChange = () => {
  if (ruleType.value == 2) {
    formData.value?.list.forEach((item) => {
      item.itemAmount = item.quantityAmount
    })
  } else {
    formData.value?.list.forEach((item) => {
      item.itemAmount = 0
    })
  }
}

const tableSchema = reactive([
  {
    label: '客户货号',
    field: 'cskuCode',
    width: 180
  },
  {
    label: '产品名称',
    field: 'name',
    width: 180
  },
  {
    label: '采购数量',
    field: 'quantity',
    width: 180
  },
  {
    label: '分摊金额',
    field: 'itemAmount',
    width: 180,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <el-input-number
            controls={false}
            v-model={row.itemAmount}
            min={0}
            precision={3}
            class="!w-100%"
            disabled={ruleType.value == 2}
          />
        )
      }
    }
  }
])

const tableColumns = reactive([
  {
    label: '客户货号',
    field: 'cskuCode',
    width: 180
  },
  {
    label: '产品名称',
    field: 'skuName',
    width: 180
  },
  {
    label: '采购数量',
    field: 'quantity',
    width: 180
  },
  {
    label: '分摊金额',
    field: 'allocationAmount',
    width: 180,
    formatter: formatMoneyColumn()
  }
])

const emit = defineEmits(['sure'])
const handleSure = async () => {
  if (pageType.value === 'add') {
    let totalAmount = 0
    formData.value.list.forEach((item) => {
      totalAmount += Number(item.itemAmount)
      item.allocationAmount = {
        amount: Number(item.itemAmount),
        currency: formData.value.totalAmount.currency
      }
    })
    if (totalAmount != formData.value.totalAmount.amount) {
      message.warning('产品分摊金额与分摊总金额不相等')
      return false
    }
    await ExportSaleContractApi.saveAllocation(formData.value)
    message.success('分摊成功')
    handleCancel()
    emit('sure')
  } else {
    await ExportSaleContractApi.cancelAllocation(formData.value.purchaseItemId)
    message.success('取消成功')
    handleCancel()
    emit('sure')
  }
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
