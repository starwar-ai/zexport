<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加采购产品</el-button
    >
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <div class="d-block total-header flex justify-between">
    <span>合计</span>
    <span
      >客户产品种类 :{{ totalHeader.cskuListNum }}&nbsp;采购总量 :{{
        numberFormat(totalHeader.purchaseNum)
      }}&nbsp;赠品 :{{ numberFormat(totalHeader.giftNum) }}&nbsp;含税总金额 :{{
        numberFormat(totalHeader.purchaseMoney)
      }}</span
    >
  </div>
  <!-- <Table
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
    @selection-change="handleSelectionChange"
  /> -->
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
  <SelectProductDia
    ref="ProductDialogRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import SelectProductDia from './SelectProductDia.vue'
import { TableColumn } from '@/types/table'
import { LengthUnit } from '@/utils/config'

defineOptions({ name: 'CostAttrsCom' })
const props = defineProps<{
  formData
}>()
const numberFormat = (num) => {
  if (num) {
    if (!num === num) {
      return 0
    } else {
      return Number(num).toFixed(3)
    }
  } else return 0
}
const TableRef = ref()
const message = useMessage()
const ProductDialogRef = ref()
const tableList = ref(props.formData.children || [])
const totalHeader = ref({
  purchaseNum: 0,
  giftNum: 0,
  purchaseMoney: 0,
  cskuListNum: 0
})
const formatLength = (length, width, height) => {
  if (length && width && height) {
    return (
      <span>
        <span>
          {length.toFixed(2) +
            ' *' +
            width.toFixed(2) +
            ' *' +
            height.toFixed(2) +
            ' ' +
            LengthUnit}
        </span>
      </span>
    )
  } else {
    return '-'
  }
}
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'mainPicture',
    label: '物料图片',
    minWidth: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.thumbnail}
        />
      )
    }
  },
  {
    field: 'skuName',
    label: '物料名称',
    minWidth: '150px'
  },
  {
    field: 'deliveryDate',
    label: '到料日期',
    width: '220px',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        valueFormat="x"
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请选择到料日期' }]
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '175px'
  },
  {
    field: 'purchaseType',
    label: '采购类型',
    minWidth: '150px',
    component: (
      <eplus-dict-select
        style="width: 90px"
        dictType={DICT_TYPE.PURCHASE_TYPE}
      />
    ),
    rules: [{ required: true, message: '请选择采购类型' }]
  },
  {
    field: 'quantity',
    label: '采购数量',
    width: '150px',
    component: (
      <el-input-number
        min={1}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入采购数量' }]
  },
  {
    field: 'unitPrice',
    label: '单价',
    minWidth: '150px',
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={3}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入单价' }]
  },
  {
    field: 'freeFlag',
    label: '是否赠品',
    width: '120px',
    component: <eplus-dict-select dictType={DICT_TYPE.CONFIRM_TYPE} />,
    rules: [{ required: true, message: '请选择是否赠品' }]
  },
  {
    field: 'taxRate',
    label: '税率(%)',
    minWidth: '150px',
    component: (
      <el-input-number
        min={0}
        max={100}
        precision={2}
        controls={false}
        placeholder="请输入税率"
      />
    ),
    rules: [{ required: true, message: '请输入税率' }]
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    formatter: (item, row: Recordable, index) => {
      if (row?.withTaxPrice) {
        return Number(row.withTaxPrice).toFixed(3)
      } else {
        return 0
      }
    }
  },
  {
    field: 'amount',
    label: '含税总金额'
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: '150px',
    component: (
      <el-input-number
        min={0}
        precision={0}
        controls={false}
      />
    ),
    rules: [{ required: true, message: '请输入箱数' }]
  },
  {
    field: 'packageFlag',
    label: '是否含包装',
    width: '120px',
    component: <eplus-dict-select dictType={DICT_TYPE.CONFIRM_TYPE} />,
    rules: [{ required: true, message: '请选择是否含包装' }]
  },
  {
    field: 'freightFlag',
    label: '是否含运费',
    width: '120px',
    component: <eplus-dict-select dictType={DICT_TYPE.CONFIRM_TYPE} />,
    rules: [{ required: true, message: '请选择是否含运费' }]
  },
  {
    field: 'qtyPerInnerbox',
    label: '单箱数量'
  },
  {
    field: 'package',
    label: '包装规格',
    minWidth: '180px',
    formatter: (item, row: Recordable, index) => {
      return formatLength(row?.packageLength, row?.packageWidth, row?.packageHeight)
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    minWidth: '180px',
    formatter: (item, row: Recordable, index) => {
      return formatLength(row?.outerboxLength, row?.outerboxWidth, row?.outerboxHeight)
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <>
          <el-button
            link
            type="success"
            onClick={() => delRow(index)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
])
watch(
  () => tableList,
  (list: any) => {
    if (list.value) {
      //避免重复监听引起总价重复累加
      if (totalHeader.value.purchaseMoney) {
        totalHeader.value.purchaseMoney = 0
        totalHeader.value.purchaseNum = 0
        totalHeader.value.giftNum = 0
      }
      let cskuCodeList: any = []
      list.value.map((item, index, arr) => {
        if (item?.cskuCode) {
          cskuCodeList.push(item.cskuCode)
        }
        if (item?.unitPrice?.amount) {
          item.unitPrice = item.unitPrice.amount
        }
        item.withTaxPrice = item?.unitPrice * (1 + item?.taxRate / 100)
        item.amount = (item.quantity * item.withTaxPrice || 0).toFixed(3)
        // if ((item.boxCount || isNaN(item.boxCount)) && item.qtyPerInnerbox) {
        //   item.boxCount = Math.ceil(item.quantity / item.qtyPerInnerbox)
        // }
        item.index = index + 1
        totalHeader.value.purchaseMoney =
          (Number(item.amount) || 0) + (Number(totalHeader.value?.purchaseMoney) || 0)
        totalHeader.value.purchaseNum =
          (Number(item.quantity) || 0) + (totalHeader.value?.purchaseNum || 0)
        if (item.freeFlag) {
          totalHeader.value.giftNum += 1
        }
      })
      if (cskuCodeList.length) {
        cskuCodeList = [...new Set(cskuCodeList)]
        totalHeader.value.cskuListNum = cskuCodeList.length || 0
      }
    }
  },
  { immediate: true, deep: true }
)
const handleAdd = async () => {
  if (props.formData?.venderCode) {
    ProductDialogRef.value.open(tableList, props.formData?.venderCode)
  } else {
    message.warning('缺少供应商信息')
  }
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.code
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.code)) {
        return item
      }
    })
  } else {
    message.warning('请选择移除的数据')
  }
}

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

const checkData = async () => {
  if (tableList.value?.length == 0) {
    message.warning(`请选择产品`)
    return false
  } else {
    let valid = await TableRef.value.validate()
    if (valid) {
      return TableRef.value.tableData
    } else {
      message.warning('产品信息有误')
      return false
    }
  }
}
defineExpose({ selectedList, tableList, checkData })

onMounted(() => {})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: #000;
}

.total-header {
  width: 100%;
}
</style>
