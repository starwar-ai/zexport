<template>
  <Dialog
    v-model="dialogTableVisible"
    title="库存"
    width="700"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <el-row
        style="margin-bottom: 20px; width: 100%"
        :gutter="24"
      >
        <el-col :span="6">
          <span style="color: #888c94">中文品名 :</span>
          {{ totalHeader.skuName }}
        </el-col>
        <el-col :span="6">
          <span style="color: #888c94">总可用数量 :</span>
          {{ totalHeader.totalNum }}
        </el-col>
        <el-col :span="6">
          <span style="color: #888c94">剩余可用数量 :</span>
          {{ totalHeader.canUseNum }}
        </el-col>
        <el-col :span="6">
          <span style="color: #888c94">已使用数量 :</span>
          {{ totalHeader.usedNum }}
        </el-col>
      </el-row>
      <eplus-form-table
        ref="tableRef"
        selectionFlag
        :list="tableData"
        :defaultVal="{}"
        :schema="tableColumns"
        @selection-change="handleSelectionChange"
      />
      <!-- </div> -->
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
import * as StockApi from '@/api/wms/stock'
import { TableColumn } from '@/types/table'

defineOptions({ name: 'InventoryQuantityDialog' })
const dialogTableVisible = ref(false)
const message = useMessage()
const tableRef = ref()
const tableData = ref([])
const index = ref(0)
const totalHeader = ref({
  skuName: '',
  totalNum: 0,
  usedNum: 0,
  canUseNum: 0
})

let tableColumns = reactive<TableColumn[]>([
  {
    field: 'batchCode',
    label: '批次',
    width: '200px'
  },
  {
    field: 'availableQuantity',
    label: '可用库存',
    width: '200px'
  },
  {
    field: 'lockQuantity',
    label: '锁定数量',
    width: '210px',
    slot: (item, row: Recordable, index: number) => {
      var ids = selectedList.value.map((it) => {
        return it.id
      })
      let disabled = ids.length ? ids.indexOf(row.id) < 0 : true
      return (
        <el-input-number
          v-model={row.lockQuantity}
          disabled={disabled}
          style="width: 150px"
          precision={0}
          min={0}
          max={row.availableQuantity}
          clearable={true}
          valueOnClear={1}
        />
      )
    }
  }
])

const emit = defineEmits<{
  sure: [link: string]
}>()

const selectedList = ref([])

const handleSelectionChange = (val) => {
  selectedList.value = val
}

const open = async (params) => {
  index.value = params.index
  let headerObj = {
    skuName: params.skuName,
    totalNum: 0,
    usedNum: 0,
    canUseNum: 0
  }
  dialogTableVisible.value = true
  let result = await StockApi.queryBatchForSale(params)
  result.forEach((res) => {
    headerObj.totalNum += Number(res.availableQuantity)
    params.usedStockList?.forEach((element) => {
      var usedList = element.list?.filter((it) => {
        return it.batchCode == res.batchCode
      })
      if (usedList?.length) {
        if (element.index == index.value) {
          //同一产品同一记录，锁定库存赋值
          res.lockQuantity = usedList[0].lockQuantity
        } else {
          //同一产品不同记录，可用库存需扣除其余记录已锁定库存
          usedList?.forEach((el) => {
            if (el.lockQuantity) {
              res.availableQuantity = Number(res.availableQuantity) - Number(el.lockQuantity)
              headerObj.usedNum += Number(el.lockQuantity)
            }
          })
        }
      }
    })
    headerObj.canUseNum += Number(res.availableQuantity)
  })
  totalHeader.value = headerObj
  tableData.value = result
}

defineExpose({ open })

const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}

const handleSure = async () => {
  let valid = await tableRef.value.validate()
  if (valid) {
    let len = selectedList.value?.length
    if (len > 0) {
      var items = selectedList.value.filter((item) => !item.lockQuantity)
      if (items?.length) {
        message.error('锁定库存数必须大于0,请检查！')
        return
      }
      let arr = []
      selectedList.value.forEach((td) => {
        if (td.lockQuantity) {
          td.stockId = td.id
          arr.push(td)
        }
      })
      emit(
        'sure',
        // @ts-ignore
        [index.value, arr]
      )
      handleCancel()
    } else {
      message.error('请选择需要锁定库存的记录')
    }
  }
}

onMounted(async () => {
  try {
  } catch {}
})
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
