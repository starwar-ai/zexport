<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加产品
    </el-button>
    <!-- <el-button @click="handleRemove">移除产品</el-button> -->
  </div>
  <Table
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
    @selection-change="handleSelectionChange"
  />

  <SelectProDia
    ref="SelectProDiaRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import { formatDateColumn, formatMoneyColumn } from '@/utils/table'
import SelectProDia from './SelectProDia.vue'

defineOptions({ name: 'AddPro' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const tableList = ref([])

let tableColumns = reactive([
  {
    field: 'companyName',
    label: '归属公司'
  },
  {
    field: 'batchCode',
    label: '批次编号'
  },
  {
    field: 'skuCode',
    label: '产品编号'
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: '150px'
  },
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'receiptTime',
    label: '入库时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
  },
  {
    field: 'stockQuantity',
    label: '库存数'
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量'
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量'
  },
  {
    field: 'position',
    label: '位置'
  },
  {
    field: 'price',
    label: '单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'totalAmount',
    label: '总价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    slots: {
      default: (data) => {
        const { row } = data
        return row.cskuCode || '-'
      }
    }
  },
  {
    field: 'custName',
    label: '客户名称',
    slots: {
      default: (data) => {
        const { row } = data
        return row.custName || '-'
      }
    }
  },
  {
    field: 'saleContractCode',
    label: '销售合同号',
    slots: {
      default: (data) => {
        const { row } = data
        return row.saleContractCode || '-'
      }
    }
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'remark',
    label: '备注'
  },
  {
    field: 'action',
    label: '操作',
    width: '80px',
    fixed: 'right',
    slots: {
      default: (data) => {
        const { $index } = data
        return (
          <div class="flex items-center justify-center">
            <el-button
              link
              type="primary"
              onClick={async () => {
                await delRow($index)
              }}
            >
              移除
            </el-button>
          </div>
        )
      }
    }
  }
])
const SelectProDiaRef = ref()
const handleAdd = async () => {
  // if (props.formData?.companyId && props.formData?.warehouseId) {
  //   SelectProDiaRef.value.open(
  //     { companyId: props.formData?.companyId, warehouseId: props.formData?.warehouseId },
  //     tableList
  //   )
  // } else {
  //   message.warning('请先选择归属公司和盘点仓库')
  // }
  if (props.formData?.warehouseId) {
    SelectProDiaRef.value.open({ warehouseId: props.formData?.warehouseId }, tableList)
  } else {
    message.warning('请先选择盘点仓库')
  }
}

// const handleRemove = () => {
//   if (selectedList.value?.length > 0) {
//     let delArr = selectedList.value.map((el) => {
//       return el.code
//     })
//     tableList.value = tableList.value.filter((item, index) => {
//       if (!delArr.includes(item.code)) {
//         return item
//       }
//     })
//   } else {
//     message.error('请选择移除的数据')
//   }
// }

const delRow = (index) => {
  tableList.value.splice(index, 1)
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  tableList.value = list
}

const checkData = () => {
  if (tableList.value.length > 0) {
    return tableList.value
  } else {
    message.warning('请选择盘点产品')
    return false
  }
}

const setTableData = () => {
  tableList.value = []
}
defineExpose({ tableList, checkData, setTableData })
watchEffect(() => {
  tableList.value = props.formData.stocktakeItemRespVOList || []
})
</script>
