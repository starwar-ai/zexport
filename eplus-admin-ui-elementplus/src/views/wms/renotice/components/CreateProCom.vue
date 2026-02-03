<template>
  <div class="mb-2">
    <el-button @click="handleAdd"> 选择产品 </el-button>
    <el-button @click="handleRemove"> 移除产品 </el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
  <SelectSku
    ref="SelectSkuRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import { formatNum } from '@/utils'
import { VolumeUnit } from '@/utils/config'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import { columnWidth } from '@/utils/table'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import SelectSku from './SelectSku.vue'
import { isValidArray } from '@/utils/is'
import { getStockList } from '@/api/common'

defineOptions({ name: 'CreateProCom' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
let tableList = ref<any[]>([])
const stockList = ref<any[]>([])
let tableColumns = reactive([
  {
    field: 'skuCode',
    label: '产品编号',
    width: columnWidth.l
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    width: columnWidth.xl
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'skuName',
    label: '中文名称',
    width: columnWidth.l
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: columnWidth.l
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l
  },
  {
    field: 'warehouseId',
    label: '采购仓库',
    batchEditFlag: true,
    width: columnWidth.l,
    component: (
      <el-select>
        {stockList.value?.map((item) => {
          return (
            <el-option
              label={item?.name}
              value={item?.id}
              key={item?.id}
            />
          )
        })}
      </el-select>
    ),
    rules: [{ required: true, message: '请选择采购仓库' }]
  },
  {
    field: 'orderQuantity',
    label: '通知入库数量',
    width: columnWidth.l,
    component: (
      <el-input-number
        min={1}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输通知入库数量' }]
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l,
    component: (
      <el-input-number
        min={1}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入外箱装量' }]
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'orderBoxQuantity',
    label: '总入库箱数',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <span>{formatNum(row.orderBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '单箱毛重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: '总体积',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <div>
          {formatNum(row.totalVolume / 1000000)}
          {VolumeUnit}
        </div>
      )
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    width: columnWidth.l,
    formatter: (item, row) => {
      return row.totalWeight?.weight
        ? `${formatNum(row.totalWeight.weight)} ${row.totalWeight.unit}`
        : '-'
    }
  },

  {
    field: 'remark',
    label: '备注',
    width: '200',
    component: (
      <el-input
        type="textarea"
        rows={2}
        placeholder="请输入备注"
      />
    )
  }
])

const SelectSkuRef = ref()
const handleAdd = () => {
  SelectSkuRef.value.open(tableList.value)
}

const selectedList = ref<any[]>([])
const handleSelectionChange = (list) => {
  selectedList.value = list
}

const handleRemove = () => {
  if (isValidArray(selectedList.value)) {
    tableList.value = tableList.value.filter((item) => !selectedList.value.includes(item))
  } else {
    message.warning('请选择要移除的产品')
  }
}
const handleSure = (list) => {
  let defaultWarehouse = stockList.value.find((item: any) => item.defaultFlag == 1)

  tableList.value = [
    ...list.map((item) => {
      return {
        ...item,
        purchaserId: item.purchaseUserId,
        purchaserDeptId: item.purchaseUserDeptId,
        warehouseId: defaultWarehouse?.id,
        totalWeight: {
          weight: 0,
          unit: 'kg'
        }
      }
    })
  ]
}

watch(
  () => tableList.value,
  (list) => {
    list?.forEach((item: any) => {
      item.orderBoxQuantity = Math.ceil(Number(item.orderQuantity) / Number(item.qtyPerOuterbox))
      item.totalVolume = getOuterboxVal(item, 'vol') * item.orderBoxQuantity
      item.totalWeight.weight =
        Number(item.orderBoxQuantity) * Number(getOuterboxVal(item, 'grossweight'))
    })
  },
  { immediate: true, deep: true }
)

const TableRef = ref()
const checkData = async () => {
  if (tableList.value.length === 0) {
    message.warning('请选择产品')
    return false
  }
  let valid = await TableRef.value.validate()
  if (valid) {
    return TableRef.value.tableData
  } else {
    message.warning('产品信息提交有误')
    return false
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  tableList.value = props.formData.stockNoticeItemRespVOList || []
})
onMounted(async () => {
  stockList.value = await getStockList()
})
</script>
