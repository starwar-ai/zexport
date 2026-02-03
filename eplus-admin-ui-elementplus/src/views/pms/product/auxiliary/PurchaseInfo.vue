<template>
  <div class="mb20px">
    <el-input
      v-model="inputRef"
      placeholder="请输入"
      clearable
      style="width: 350px"
      @change="getList"
    >
      <template #prepend>
        <el-select
          v-model="selectRef"
          @change="handleSelect"
          style="width: 120px"
        >
          <el-option
            v-for="field in purchaseFields"
            :key="field.name"
            :label="field.label"
            :value="field.name"
          />
        </el-select>
      </template>
    </el-input>
  </div>

  <Table
    :columns="purchaseTableColumns"
    headerAlign="center"
    align="center"
    :data="tableData"
  />
  <Pagination
    v-show="tableData?.length"
    v-model:limit="pageForm.pageSize"
    v-model:page="pageForm.pageNo"
    :total="pageForm.total"
    @pagination="getList"
    layout="total, sizes, prev, pager, next, jumper"
  />
</template>
<script setup lang="tsx">
import * as auxiliaryApi from '@/api/pms/auxiliary/index'
import { formatMoneyColumn } from '@/utils/table'

const props = defineProps<{
  code?: string
}>()
const inputRef = ref('')
const selectRef = ref('specDesc')
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
const tableData = ref([])
const getList = async (val) => {
  console.log(111111)
  const tempKey = selectRef.value
  let res = await auxiliaryApi.getPurchaseInfo({
    [tempKey]: val,
    skuCode: props.code,
    pageSize: pageForm.pageSize,
    pageNo: pageForm.pageNo
  })
  tableData.value = res?.list || []
  pageForm.total = res?.total || 0
}
const handleSelect = (val) => {
  inputRef.value = ''
}

onMounted(async () => {
  await getList()
})

defineExpose({
  getList
})

//采购信息搜索逻辑
const purchaseFields = [
  {
    name: 'specDesc',
    label: '规格描述'
  },
  {
    name: 'saleContractCode',
    label: '销售合同'
  },
  {
    name: 'purchaseContractCode',
    label: '采购合同'
  },
  {
    name: 'auxiliaryPurchaseContractCode',
    label: '包材采购合同'
  },
  {
    name: 'venderName',
    label: '供应商名称'
  }
]

const purchaseTableColumns = [
  {
    type: 'index',
    label: '序号',
    width: '80px'
  },
  // {
  //   field: 'purchaseModel',
  //   label: '采购类型',
  //   minWidth: '80px'
  // },
  // {
  //   field: 'skuName',
  //   label: '包材名称',
  //   minWidth: '80px'
  // },
  // {
  //   field: 'skuUnit',
  //   label: '计量单位',
  //   minWidth: '80px'
  // },
  // {
  //   field: 'specRemark',
  //   label: '规格描述',
  //   minWidth: '80px'
  // },
  // {
  //   field: 'mainPicture',
  //   label: '图片',
  //   minWidth: '80px'
  // },
  {
    field: 'planQuantity',
    label: '计划采购量',
    minWidth: '80px'
  },
  {
    field: 'quantity',
    label: '实际采购量',
    minWidth: '80px'
  },
  {
    field: 'venderName',
    label: '供应商名称',
    minWidth: '80px'
  },
  {
    field: 'unitPrice',
    label: '供应商报价',
    minWidth: '80px',
    formatter: formatMoneyColumn()
  },
  {
    field: 'saleContractCode',
    label: '销售合同',
    minWidth: '80px'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同',
    minWidth: '80px'
  },
  {
    field: 'auxiliarySkuCode',
    label: '产品编号',
    minWidth: '80px'
  },
  {
    field: 'auxiliarySkuName',
    label: '产品品名',
    minWidth: '80px'
  },
  {
    field: 'auxiliaryCskuCode',
    label: '客户货号',
    minWidth: '80px'
  }
  // {
  //   field: 'purchaseModel',
  //   label: '采购时间',
  //   minWidth: '80px'
  // }
]
</script>
