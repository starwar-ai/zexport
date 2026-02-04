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
import { TableColumn } from '@/types/table'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'InventoryQuantityDialog' })
const dialogTableVisible = ref(false)
const message = useMessage()
const tableData = ref([])
const index = ref(0)
const level = ref(1)
// const totalHeader = ref({
//   skuName: '',
//   totalNum: 0,
//   usedNum: 0,
//   canUseNum: 0
// })

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
const saleQuantity = ref(0)
const saleLockQuantity = ref(0)
const saleContractItemId = ref('')
const open = async (data) => {
  saleContractItemId.value = data.saleContractItemId
  saleLockQuantity.value = data.saleLockQuantity
  saleQuantity.value = data.saleQuantity
  index.value = data.index
  level.value = data.level
  tableData.value = await DomesticSaleContractApi.listBatch(data)
  if (isValidArray(data.stockLockSaveReqVOList)) {
    tableData.value.forEach((item: any) => {
      data.stockLockSaveReqVOList.forEach((el: any) => {
        if (item.batchCode === el.batchCode) {
          item.sourceOrderLockedQuantity = el.sourceOrderLockedQuantity
        }
      })
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
  let lockQuantityTotal = 0,
    availableQuantityTotal = 0
  let list = []
  tableData.value.map((item: any) => {
    availableQuantityTotal += item.availableQuantity
    lockQuantityTotal += item.sourceOrderLockedQuantity
    if (item.sourceOrderLockedQuantity > 0) {
      let obj = {
        stockId: item.id,
        batchCode: item.batchCode,
        skuCode: item.skuCode,
        sourceOrderLockedQuantity: item.sourceOrderLockedQuantity,
        companyName: item.companyName,
        companyId: item.companyId,
        saleContractItemId: saleContractItemId.value
      }
      list.push(obj)
    }
  })
  if (lockQuantityTotal < saleLockQuantity.value) {
    message.warning(`锁定数量不能小于销售锁定数量${saleLockQuantity.value}，请重新输入锁定数量！`)
    return false
  }
  if (lockQuantityTotal > saleQuantity.value) {
    message.warning(`锁定数量不能大于销售数量${saleQuantity.value}，请重新输入锁定数量！`)
    return false
  }
  emit('sure', {
    index: index.value,
    lockQuantityTotal,
    list,
    level: level.value,
    availableQuantityTotal
  })
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
