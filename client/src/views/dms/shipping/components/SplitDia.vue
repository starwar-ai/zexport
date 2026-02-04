<template>
  <Dialog
    v-model="dialogTableVisible"
    title="下推出运明细"
    width="1210"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <eplus-form-table
        ref="TableRef"
        selectionFlag
        :list="tableList"
        :defaultVal="{}"
        :schema="tableColumns"
        @selection-change="handleSelectionChange"
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
// import { isValidArray } from '@/utils/is'
import * as ShipmentApi from '@/api/dms/shipment/index'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { isValidArray } from '@/utils/is'
import { columnWidth } from '@/utils/table'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'SplitDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  success
}>()
let tableColumns = reactive([
  {
    field: 'saleContractCode',
    label: '销售合同',
    minWidth: columnWidth.l,
    sortable: true
  },
  {
    field: 'custPo',
    label: '客户PO号',
    minWidth: columnWidth.l,
    sortable: true
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    minWidth: columnWidth.l,
    sortable: true
  },
  {
    field: 'name',
    label: '产品名称',
    minWidth: columnWidth.l
  },
  {
    field: 'saleUnitPrice',
    label: '销售单价',
    width: columnWidth.l,
    slot: (item, row, index) => {
      return <EplusMoneyLabel val={row.saleUnitPrice} />
    }
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l
  },
  {
    field: 'unSplitQuantity',
    label: '待转产品数量',
    width: columnWidth.l
  },
  {
    field: 'thisSplitQuantity',
    label: '本次下推数量',
    flxed: 'right',
    width: columnWidth.l,
    slot: (item, row, index) => {
      return (
        <>
          <el-input-number
            v-model={row.thisSplitQuantity}
            min={0}
            max={row.unSplitQuantity}
            controls={false}
            precision={0}
          />
        </>
      )
    },
    rules: [{ required: true, message: '请输入本次下推数量' }]
  }
])
const TableRef = ref()
const tableList = ref<any[]>([])
const open = async (list = []) => {
  tableList.value = []
  list.forEach((item: any) => {
    if (item.shippingQuantity - item.splitQuantity > 0) {
      tableList.value.push({
        ...item,
        unSplitQuantity: item.shippingQuantity - item.splitQuantity,
        thisSplitQuantity: item.shippingQuantity - item.splitQuantity
      })
    }
  })

  if (isValidArray(tableList.value)) {
    dialogTableVisible.value = true
    nextTick(() => {
      TableRef.value?.handleAllToggleRowSelection()
    })
  } else {
    message.warning('无可拆分的数据')
  }
}
const selectList = ref([])
const handleSelectionChange = (val) => {
  selectList.value = val
}

const handleCancel = () => {
  selectList.value = []
  dialogTableVisible.value = false
}
const handleSure = async () => {
  if (selectList.value.length > 0) {
    let itemList = cloneDeep(selectList.value).map((item: any) => {
      return {
        id: item.id,
        splitQuantity: item.thisSplitQuantity
      }
    })
    await ShipmentApi.splitShipment({ itemList: itemList })
    message.success('操作成功')
    handleCancel()
    emit('success')
  } else {
    message.error('请先选择数据')
  }
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
