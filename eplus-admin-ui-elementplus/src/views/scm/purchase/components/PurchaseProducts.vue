<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      v-if="props.type === 'plan'"
    >
      添加采购产品
    </el-button>
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <div class="d-block total-header flex justify-between">
    <span>合计</span>
    <span>
      客户产品种类 :{{ totalHeader.cskuListNum }}&nbsp;采购总量 :{{
        formatNum(totalHeader.purchaseNum)
      }}
    </span>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
  <ProductDialog
    ref="ProductDialogRef"
    @sure="handleSure"
    :selectionFlag="true"
    :defaultVal="{}"
    :schema="productColumns"
    :isShowTabs="true"
    :ownCustCodeFlag="true"
  />

  <MoqDia
    ref="MoqDiaRef"
    @sure="updateRow"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import { ProductDialog } from '@/components/ProductDialog'
import { TableColumn } from '@/types/table'
import { PurchaseSourceTypeEnum, PurchaseTypeEnum } from '@/utils/constants'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'
import ContractUpdateSku from './ContractUpdateSku.vue'
import MoqDia from '@/views/sms/quotation/components/MoqDia.vue'
import { EplusNumInput } from '@/components/EplusMoney'
import { moneyPrecision } from '@/utils/config'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { formatNum } from '@/utils'

defineOptions({ name: 'PurchaseProducts' })
const props = defineProps<{
  formData
  type: string
  changeFlag?: boolean
}>()
const emit = defineEmits(['loaded'])
const TableRef = ref()
const message = useMessage()
const btnText = ref('')
const ProductDialogRef = ref()
const tableList = ref([])
const totalHeader = ref({
  purchaseNum: 0,
  giftNum: 0,
  purchaseMoney: 0,
  cskuListNum: 0
})

//弹框的字段
let productColumns = reactive<TableColumn[]>([
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: '150px'
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    width: '180px'
  },
  {
    field: 'mainPicture',
    label: '图片',
    width: '60px',
    slot: (item, row: Recordable, index: number) => {
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
    label: '中文名称',
    width: '150px'
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '150px'
  },
  {
    field: 'categoryName',
    label: '分类',
    width: '150px'
  },
  {
    field: 'brandName',
    label: '品牌',
    width: '150px'
  },
  {
    field: 'skuType',
    label: '产品类型',
    width: '150px',
    slot: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType)
    }
  }
])
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'mainPicture',
    label: '图片',
    width: '80px',
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
    label: '品名',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <EplusSkuName
          name={row.skuName}
          type={row.skuType}
        />
      )
    }
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: '220px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <ContractUpdateSku
          row={row}
          onUpdate={() => handleUpdateSku(row, index)}
        />
      )
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: '150px'
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '150px'
  },
  {
    field: 'needPurQuantity',
    label: '采购数量',
    width: '150px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.needPurQuantity}
          min={1}
          controls={false}
          precision={0}
          style="width: 150px"
          onBlur={(...$event) => quantityChange($event, row)}
        />
      )
    },
    rules: [{ required: true, message: '请输入采购数量' }]
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: '150px'
  },
  {
    field: 'currency',
    label: '供应商币种',
    width: '100px'
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    width: '150px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.withTaxPrice.amount}
          min={0}
          controls={false}
          precision={moneyPrecision}
          class="!w-90%"
        />
      )
    },
    rules: [{ required: true, message: '请输入单价' }]
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    width: '150px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-user-select
          disabled={props?.formData?.sourceType === PurchaseSourceTypeEnum.SALECONTRACT.type}
          v-model={row.purchaseUserId}
          onChange={(val) => {
            if (val) {
              row.purchaseUserName = val.nickname
              row.purchaseUserDeptId = val?.deptId
              row.purchaseUserDeptName = val?.deptName
            }
          }}
        ></eplus-user-select>
      )
    },
    span: 6,
    rules: [
      {
        required: true,
        message: '请选择采购员'
      }
    ]
  },
  {
    field: 'purchaseType',
    label: '采购类型',
    width: '150px',
    showOverflowTooltip: false,
    slot: (item, row: Recordable, index: number) => {
      let flag =
        props.type === 'plan' && (!props.formData?.sourceType || props.formData?.sourceType === 1)
      return (
        <eplus-dict-select
          v-model={row.purchaseType}
          style="width: 150px"
          dictType={DICT_TYPE.PURCHASE_TYPE}
          disabled={!flag}
          clearable={true}
          filter={(val) => {
            if (
              (props.type === 'contract' && props?.formData?.planSourceType === 1) ||
              (props.type === 'plan' && !props?.formData?.sourceType)
            ) {
              return val.filter((item) => item.label !== '标准采购')
            } else {
              return val
            }
          }}
        />
      )
    },
    rules: [{ required: true, message: '请选择采购类型' }]
  },
  {
    field: 'qtyPerOuterbox',
    label: '单箱数量',
    width: '100px'
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: '100px',
    slot: (item, row: Recordable, index: number) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: '100px',
    slot: (item, row: Recordable, index: number) => {
      return <span>{row.boxCount * (row.specificationList?.length || 1)}</span>
    }
  }
])

const handleUpdateSku = async (row, index) => {
  const oldId = tableList.value[index]?.id
  let res = await getSkuInfo(row.skuCode)
  let defaultQuoteitemList = res?.quoteitemDTOList?.filter((item) => item.defaultFlag === 1)[0]
  let defaultUser =
    defaultQuoteitemList?.buyers.find((obj) => obj.defaultFlag === 1) ||
    defaultQuoteitemList?.buyers[0]
  tableList.value[index] = Object.assign(tableList.value[index], {
    ...res,
    ...defaultQuoteitemList,
    skuId: res.id,
    skuName: res?.name,
    skuCode: res?.code,
    purchaseUserList: defaultQuoteitemList?.buyers,
    purchaseUserId: defaultUser?.userId || '',
    purchaseUserName: defaultUser?.nickname || '',
    venderName: defaultQuoteitemList.venderName,
    currency: defaultQuoteitemList.currency,
    qtyPerOuterbox: defaultQuoteitemList.qtyPerOuterbox,
    skuDeletedFlag: 0,
    mainPicture: res.picture.find((el) => el.mainFlag == 1) || res.picture[0],
    id: oldId
  })
}
//箱数计算
const MoqDiaRef = ref()
const currentRow = ref()
const quantityChange = (e, row) => {
  currentRow.value = row
  let quantity = props?.type === 'plan' ? Number(row.needPurQuantity) : Number(row.quantity)
  let remainder = quantity % Number(row.qtyPerOuterbox),
    integer = Math.floor(quantity / Number(row.qtyPerOuterbox))
  if (remainder > 0) {
    row.boxCount = integer + 1
    let des = `输入的采购数量计算得出箱数为${integer},余数为${remainder}。您可以选择向下取整起订量为${integer * Number(row.qtyPerOuterbox)}箱数为${integer}，或者向上取整箱数起订量为${(integer + 1) * Number(row.qtyPerOuterbox)}为${integer + 1}`
    MoqDiaRef.value.open(des)
  } else {
    row.boxCount = quantity / Number(row.qtyPerOuterbox)
  }
}
const updateRow = (type) => {
  let quantity = Number(currentRow.value.needPurQuantity)
  let integer = Math.floor(quantity / Number(currentRow.value.qtyPerOuterbox))
  if (type === 'ceil') {
    currentRow.value.boxCount = integer + 1
    currentRow.value.needPurQuantity = (integer + 1) * Number(currentRow.value.qtyPerOuterbox)
  } else if (type === 'floor') {
    currentRow.value.boxCount = integer
    currentRow.value.needPurQuantity = integer * Number(currentRow.value.qtyPerOuterbox)
  }
}

watch(
  () => tableList,
  (list: any) => {
    if (list.value) {
      //避免重复监听引起总价重复累加
      if (totalHeader.value.purchaseMoney) {
        totalHeader.value.purchaseMoney = 0
        totalHeader.value.purchaseNum = 0
      }
      let cskuCodeList: any = []
      let giftNum = 0
      list.value.map((item, index, arr) => {
        if (item?.cskuCode) {
          cskuCodeList.push(item.cskuCode)
        }

        // if (!item?.purchaseUserList?.length && props?.type === 'plan') {
        //   message.warning(`${item.skuName}产品未进行供应商报价`)
        // }
        // item.qtyPerInnerbox = item.qtyPerInnerbox ? item.qtyPerInnerbox : 1

        // if ((item.boxCount || isNaN(item.boxCount)) && item.qtyPerInnerbox) {
        //   item.boxCount = Math.ceil(item.quantity / item.qtyPerInnerbox)
        // }
        if (props?.type === 'plan') {
          item.boxCount =
            typeof item.needPurQuantity === 'number' && item.qtyPerOuterbox !== 0
              ? Math.ceil(item.needPurQuantity / item.qtyPerOuterbox)
              : 0
          if (item.freeFlag == getDictValue(DICT_TYPE.CONFIRM_TYPE, '是')) {
            giftNum++
            item.withTotalTaxPrice = 0
          } else {
            item.withTotalTaxPrice = (item.needPurQuantity * item.withTaxPrice.amount || 0).toFixed(
              2
            )
          }
          totalHeader.value.purchaseNum =
            (Number(item.needPurQuantity) || 0) + (totalHeader.value?.purchaseNum || 0)
        }

        item.index = index + 1
        totalHeader.value.purchaseMoney =
          (Number(item.withTotalTaxPrice) || 0) + (Number(totalHeader.value?.purchaseMoney) || 0)

        item.purchaseType = item.purchaseType || PurchaseTypeEnum.STOCK.type
      })
      if (cskuCodeList.length) {
        cskuCodeList = [...new Set(cskuCodeList)]
        totalHeader.value.cskuListNum = cskuCodeList.length || 0
      }
      totalHeader.value.giftNum = giftNum
      // 数据加载完成后通知父组件
      nextTick(() => {
        emit('loaded')
      })
    } else {
      emit('loaded')
    }
  },
  { immediate: true, deep: true }
)
const handleAdd = async () => {
  if (props.formData?.custId && props.formData?.custCode) {
    ProductDialogRef.value.open([], props.formData?.custCode)
  } else {
    ProductDialogRef.value.open([])
  }
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.sortNum
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.sortNum)) {
        return item
      }
    })
    tableList.value.forEach((item, index) => {
      return (item.sortNum = index + 1)
    })
  } else {
    message.error('请选择移除的数据')
  }
}

const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  if (list && list.length > 0) {
    list.forEach((item, index) => {
      let defaultQuoteitem = item.quoteitemList.find((el) => el.defaultFlag == 1)

      //根据产品弹窗是否多选来控制序号的递增
      item.sortNum =
        list.length === 1 ? tableList.value.length + 1 : tableList.value.length + index + 1
      // let defaultPurchaseUser = item.purchaseUserList.find((el) => el.defaultFlag == 1)
      // item.purchaseUserId = defaultPurchaseUser?.userId || ''
      // item.purchaseUserName = defaultPurchaseUser?.nickname || ''
      item.venderName = defaultQuoteitem.venderName
      item.currency = defaultQuoteitem.currency
      item.qtyPerOuterbox = defaultQuoteitem.qtyPerOuterbox
    })
    tableList.value?.push(...list)
  }
  //tableList.value=list
}
// const handleSelectSure = (list) => {
//   // if(list&&)
//   tableList.value.forEach((item: any) => {
//     if (item?.sortNum === list[0]?.value) {
//       return Object.assign(item, list[1])
//     }
//   })
//   // selectedQuote=list[0]
// }
const checkData = async () => {
  // tableList.value.forEach((item: any) => {
  //   item?.id && delete item?.id
  // })
  if (tableList.value.length == 0) {
    message.warning(`请选择${btnText.value}`)
    return false
  } else {
    let valid = await TableRef.value.validate()
    if (valid) {
      return TableRef.value.tableData
    } else {
      message.warning('产品提交信息有误')
      return false
    }
  }
}
const clearTableList = () => {
  tableList.value = []
}
defineExpose({ selectedList, tableList, checkData, clearTableList, totalHeader })

onMounted(async () => {})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: rgba(0, 0, 0, 0);
}

.total-header {
  width: 100%;
}
</style>
