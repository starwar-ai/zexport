<template>
  <Dialog
    v-model="dialogTableVisible"
    title="转报关单"
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
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'DeclarationDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
let tableColumns = reactive([
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: '120',
    sortable: true
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '150'
  },
  // {
  //   field: 'shippedAddress',
  //   label: '发货地点',
  //   width: '150',
  //   formatter: (item, row, index) => {
  //     return getDictLabel(DICT_TYPE.SHIPPED_ADDRESS, row.shippedAddress)
  //   }
  // },
  // {
  //   field: 'skuCode',
  //   label: '产品编号',
  //   width: '150'
  // },
  {
    field: 'custPo',
    label: '客户PO号',
    width: '150px',
    sortable: true
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    sortable: true,
    width: '150'
  },
  {
    field: 'declarationName',
    label: '报关中文名',
    width: '200'
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文名',
    width: '200'
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    flxed: 'right',
    width: '100'
  },
  {
    field: 'declarationQuantityOld',
    label: '已报数量',
    flxed: 'right',
    width: '100'
  },
  {
    field: 'declarationQuantity',
    label: '报关数量',
    flxed: 'right',
    width: '100'
  },
  {
    field: 'declarationQuantityCurrent',
    label: '本次报关数量',
    flxed: 'right',
    width: '120',
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <el-input-number
            v-model={row.declarationQuantityCurrent}
            min={0}
            max={row.declarationQuantity - row.declarationQuantityOld}
            controls={false}
            precision={0}
            class="!w-90%"
          />
        </>
      )
    },
    rules: [{ required: true, message: '请输入报关数量' }]
  }
])
const TableRef = ref()
const tableList = ref([])
const open = async (list = []) => {
  tableList.value = []
  list.map((item: any) => {
    if (item.declarationQuantity > item.declarationQuantityOld) {
      tableList.value.push({
        ...item,
        declarationQuantityCurrent:
          Number(item.declarationQuantity) - Number(item.declarationQuantityOld)
      })
    }
  })
  if (isValidArray(tableList.value)) {
    dialogTableVisible.value = true
    nextTick(() => {
      TableRef.value?.handleAllToggleRowSelection()
    })
  } else {
    message.warning('无可转报关单数据')
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
    let shipmentItemList = cloneDeep(selectList.value).map((item: any) => {
      return {
        id: item.id,
        declarationQuantityCurrent: item.declarationQuantityCurrent
      }
    })
    let res = await ShipmentApi.toDeclaration({ declarationReqList: shipmentItemList })
    message.notifyPushSuccess('转报关单成功', res, 'shipment-declaration')
    handleCancel()
    emit('sure')
  } else {
    message.error('请先选择数据')
  }
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
