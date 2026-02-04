<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
  />
</template>
<script setup lang="tsx">
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'

defineOptions({ name: 'ProCom' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const tableList = ref([])

let tableColumns = reactive([
  {
    field: 'batchCode',
    label: '批次编号',
    width: '150'
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: '150'
  },
  {
    field: 'skuName',
    label: '产品名称',
    width: '150'
  },
  {
    field: 'receiptTime',
    label: '入库时间',
    width: '150'
  },
  {
    field: 'stockQuantity',
    label: '库存数',
    width: '150'
  },
  {
    field: 'stockDiff',
    label: '盘点差异',
    width: '150',
    formatter: (item, row, index) => {
      return Number(row.stocktakeStockQuantity) - Number(row.stockQuantity)
    }
  },
  {
    field: 'diffReasons',
    label: '差异原因',
    component: <el-input type="textarea" />,
    width: '150'
  },
  {
    field: 'stocktakeStockQuantity',
    label: '实际库存数',
    width: '150',
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入实际库存数' }]
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input type="textarea" />,
    width: '150'
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: '150'
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: '150'
  },
  {
    field: 'position',
    label: '位置',
    width: '150'
  },
  {
    field: 'stocktakePosition',
    label: '实际位置',
    width: '150',
    component: <el-input class="!w-90%" />,
    rules: [{ required: true, message: '请输入实际库存数' }]
  },
  {
    field: 'price',
    label: '单价',
    width: '150',
    formatter: (item, row, index) => {
      return <EplusMoneyLabel val={row.price} />
    }
  },
  {
    field: 'totalAmount',
    label: '总价',
    width: '150',
    formatter: (item, row, index) => {
      return <EplusMoneyLabel val={row.totalAmount} />
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '150'
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '150'
  },
  {
    field: 'saleContractCode',
    label: '销售合同号',
    width: '150'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号',
    width: '150'
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: '150'
  }
])

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
  tableList.value = props.formData.stocktakeItemRespVOList || []
})
</script>
