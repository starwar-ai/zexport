<template>
  <Dialog
    v-model="dialogTableVisible"
    title="分配库存"
    width="900"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <Table
        style="width: 100%"
        :columns="columns"
        headerAlign="center"
        align="center"
        :data="tableData"
      />
    </div>
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
import * as DomesticSaleContractApi from '@/api/sms/saleContract/domestic'
import { planItemUpdate } from '@/api/scm/purchasePlan'
import { TableColumn } from '@/types/table'

defineOptions({ name: 'StockLockCreateDia' })
const dialogTableVisible = ref(false)
const message = useMessage()
const tableRef = ref()
const tableData = ref([])
const index = ref(0)
const initRow = ref()

let columns = reactive<TableColumn[]>([
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '130px'
  },
  {
    field: 'skuName',
    label: '产品名称',
    width: '120px'
  },
  {
    field: 'companyName',
    label: '公司主体',
    width: '100px'
  },
  {
    field: 'warehouseName',
    label: '仓库名称',
    width: '120px'
  },
  {
    field: 'batchCode',
    label: '批次',
    width: '180px'
  },
  {
    field: 'availableQuantity',
    label: '可用库存',
    width: '100px'
  },
  {
    field: 'sourceOrderLockedQuantity',
    label: '锁定数量',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <el-input-number
            controls={false}
            v-model={row.sourceOrderLockedQuantity}
            min={0}
            max={row.availableQuantity}
            precision={0}
            class="!w-100%"
          />
        )
      }
    }
  }
])

const open = async (data) => {
  initRow.value = data
  if (data.stockDetailRespVOList) {
    tableData.value = data.stockDetailRespVOList
  } else {
    tableData.value = await DomesticSaleContractApi.listBatch({
      skuCode: data.skuCode,
      companyIdList: data.companyIdList,
      sourceOrderCode: data?.parent?.code,
      sourceOrderItemId: data?.id,
      saleContractItemId: data?.id
    })
  }
  dialogTableVisible.value = true
}

const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}
const emit = defineEmits<{
  sure
}>()
const handleSure = async () => {
  await DomesticSaleContractApi.cancelBatch(initRow.value?.parent.saleContractCode)
  let lockQuantityTotal = 0
  let list = []
  tableData.value.map((item) => {
    lockQuantityTotal += item.sourceOrderLockedQuantity
    if (item.sourceOrderLockedQuantity > 0) {
      let obj = {
        stockId: item.id,
        batchCode: item.batchCode,
        skuCode: item.skuCode,
        lockQuantity: item.sourceOrderLockedQuantity,
        companyName: item.companyName,
        companyId: item.companyId,
        sourceOrderType: 4,
        sourceOrderId: initRow.value?.parent.id,
        sourceOrderCode: initRow.value?.parent.code,
        sourceOrderItemId: initRow.value?.id,
        saleContractId: initRow.value?.parent.saleContractId,
        saleContractCode: initRow.value?.parent.saleContractCode
      }
      list.push(obj)
    }
  })
  await DomesticSaleContractApi.createBatch(list)
  let needVal = Number(initRow.value?.quantity) - Number(lockQuantityTotal)
  initRow.value.needPurQuantity = needVal <= 0 ? 0 : needVal
  initRow.value.currentLockQuantity = lockQuantityTotal
  await planItemUpdate({ ...initRow.value, parent: undefined })
  emit('sure')
  handleCancel()
}
defineExpose({ open })
</script>

<style lang="scss" scoped>
.totalAction {
  cursor: pointer;
  width: 2000px;
}

.totalAction:hover {
  border-color: #ccc;
  box-shadow: 0 0 0 1px #eee;
}
</style>
