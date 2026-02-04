<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { getOuterbox } from '@/utils/outboxSpec'
import { formatDictColumn } from '@/utils/table'

defineOptions({ name: 'ProCom' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const tableList = ref([])

let tableColumns = reactive([
  {
    field: 'sortNum',
    label: '序号',
    width: '80',
    fixed: 'left'
  },
  {
    field: 'sourceSortNum',
    label: '来源单据明细序号',
    width: '150'
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: '150'
  },
  {
    field: 'skuName',
    label: '中文名称',
    width: '150'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'purchaserUserName',
    label: '采购员'
  },
  {
    field: 'ownBrandFlag',
    label: '自主品牌',
    formatter: formatDictColumn(DICT_TYPE.IS_INT),
    width: '150'
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '150'
  },
  {
    field: 'custPo',
    label: '客户PO号',
    width: '150'
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '150'
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: '150'
  },
  {
    field: 'orderQuantity',
    label: '应出数量',
    width: '150',
    component: (
      <el-input-number
        min={1}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入应出数量' }]
  },
  {
    field: 'orderBoxQuantity',
    label: '应出箱数',
    width: '100'
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: '100'
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: '100'
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '单箱毛重',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: '总体积',
    width: '150'
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.totalWeight ? `${row.totalWeight.weight} ${row.totalWeight.unit}` : '-'
    }
  },
  {
    field: 'palletVolume',
    label: '单托体积',
    width: '150'
  },
  {
    field: 'palletWeight',
    label: '单托毛重',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.palletWeight ? `${row.palletWeight.weight} ${row.palletWeight.unit}` : '-'
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
    list?.forEach((item) => {
      item.totalWeight.weight =
        Number(item.orderBoxQuantity) * Number(item.outerboxGrossweight.weight)
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
