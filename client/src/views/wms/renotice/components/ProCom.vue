<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
  />
</template>
<script setup lang="tsx">
import { formatNum } from '@/utils'
import { VolumeUnit } from '@/utils/config'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import { columnWidth } from '@/utils/table'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'

defineOptions({ name: 'ProCom' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const tableList = ref([])

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
    field: 'skuName',
    label: '中文名称',
    width: columnWidth.l
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号',
    width: columnWidth.l
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: columnWidth.l
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'custName',
    label: '客户名称',
    width: columnWidth.l
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l
  },
  {
    field: 'orderQuantity',
    label: '总入库数量',
    width: columnWidth.l
    // bug 660  改为不可编辑
    // component: (
    //   <el-input-number
    //     min={1}
    //     controls={false}
    //     precision={0}
    //     class="!w-90%"
    //   />
    // ),
    // rules: [{ required: true, message: '请输入应收数量' }]
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.l
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l
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
          {getOuterboxVal(row, 'vol') * row.orderBoxQuantity}
          {VolumeUnit}
        </div>
      )
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.totalWeight ? `${row.totalWeight.weight} ${row.totalWeight.unit}` : '-'
    }
  },
  {
    field: 'realBillQuantity',
    label: '实际入库数量',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.realBillQuantity || 0
    }
  },
  {
    field: 'pendingStockQuantity',
    label: '待入库数量',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.pendingStockQuantity || 0
    }
  },
  {
    field: 'remark',
    label: '备注',
    width: '200'
  }
])

watch(
  () => tableList.value,
  (list) => {
    list?.forEach((item: any) => {
      item.totalWeight.weight = formatNum(
        Number(item.orderBoxQuantity) * Number(item.outerboxGrossweight.weight)
      )
      item.orderBoxQuantity = Math.ceil(Number(item.orderQuantity) / Number(item.qtyPerOuterbox))
    })
  },
  { immediate: true, deep: true }
)

const TableRef = ref()
const checkData = async () => {
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
</script>
