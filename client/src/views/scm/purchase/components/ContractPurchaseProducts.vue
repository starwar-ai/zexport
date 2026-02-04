<template>
  <div class="mb15px">
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <div class="d-block total-header flex justify-between">
    <span>合计</span>
    <span>
      客户产品种类:{{ totalHeader.cskuListNum }}&nbsp;采购总量:{{
        formatNum(totalHeader.purchaseNum)
      }}&nbsp;赠品:{{ formatNum(totalHeader.giftNum) }}&nbsp;{{
        `含税总金额:${formatNum(totalHeader.purchaseMoney, moneyTotalPrecision)}`
      }}&nbsp;采购总金额(RMB):{{
        formatNum(totalHeader.purchaseMoney * props.formData.taxRate, moneyTotalPrecision)
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
  />

  <MoqDia
    ref="MoqDiaRef"
    @sure="updateRow"
  />

  <UpdateDesDia
    ref="UpdateDesDiaRef"
    @sure="
      (val, index) => {
        tableList[index].description = val
      }
    "
  />

  <UpdateOuterboxSpec
    ref="UpdateOuterboxSpecRef"
    @sure="
      (val, index) => {
        tableList[index].specificationList = val
      }
    "
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { ProductDialog } from '@/components/ProductDialog'
import { TableColumn } from '@/types/table'
import { PurchaseTypeEnum, SkuTypeEnum } from '@/utils/constants'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'
import { packageTypeList } from '@/api/common/index'
import ContractUpdateSku from './ContractUpdateSku.vue'
import MoqDia from '@/views/sms/quotation/components/MoqDia.vue'
import UpdateDesDia from './UpdateDesDia.vue'
import { useRateStore } from '@/store/modules/rate'
import { EplusNumInput } from '@/components/EplusMoney'
import { moneyPrecision, moneyTotalPrecision } from '@/utils/config'
import { getOuterbox } from '@/utils/outboxSpec'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { columnWidth } from '@/utils/table'
import { formatNum, formatterPrice } from '@/utils'
import UpdateOuterboxSpec from '@/views/pms/product/main/components/UpdateOuterboxSpec.vue'

defineOptions({ name: 'ContractPurchaseProducts' })
const rateList = useRateStore().rateList
const props = defineProps<{
  formData
  type: string
  changeFlag?: boolean
}>()
const TableRef = ref()

const message = useMessage()
const btnText = ref('')
const ProductDialogRef = ref()
const UpdateOuterboxSpecRef = ref()
const tableList = ref([])
const totalHeader = ref({
  purchaseNum: 0,
  giftNum: 0,
  purchaseMoney: 0,
  cskuListNum: 0
})

const UpdateDesDiaRef = ref()
//弹框的字段
let productColumns = reactive<TableColumn[]>([
  {
    field: 'code',
    label: '产品编号',
    width: columnWidth.l
  },
  {
    field: 'mainPicture',
    label: '图片',
    width: columnWidth.l,
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
    width: columnWidth.l
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    width: columnWidth.xl
  },
  {
    field: 'material',
    label: '产品材质',
    width: columnWidth.l
  },
  {
    field: 'categoryName',
    label: '分类',
    width: columnWidth.l
  },
  {
    field: 'brandName',
    label: '品牌',
    width: columnWidth.l
  },
  {
    field: 'skuType',
    label: '产品类型',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <el-tag type="info">{getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType)}</el-tag>
    }
  }
])
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'mainPicture',
    label: '物料图片',
    width: columnWidth.m,
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
    label: '产品名称',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <div class="h-50px flex items-center">
          {/* 普通产品的角标不展示 */}
          <el-badge
            class="item"
            value={getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType).split('')[0] || ''}
            type="primary"
            hidden={row?.skuType === SkuTypeEnum.COMMON.status ? true : false}
          >
            {row?.skuName}
          </el-badge>
        </div>
      )
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
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
    field: 'quantity',
    label: '采购数量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.quantity}
          precision={0}
          min={1}
          controls={false}
          clearable={true}
          onBlur={(...$event) => quantityChange($event, row)}
        />
      )
    },
    rules: [{ required: true, message: '请输入采购数量' }]
  },

  {
    field: 'outerbox',
    label: '外箱规格',
    minWidth: columnWidth.xl,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <div class="flex items-center justify-between">
          <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>

          <Icon
            icon="ep:edit"
            class="ml5px cursor-pointer text-#409EFF"
            onClick={() => {
              UpdateOuterboxSpecRef.value.open(row, index)
            }}
          />
        </div>
      )
    }
  },

  {
    field: 'description',
    label: '中文描述',
    width: '220px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <div class="flex items-center">
          <el-tooltip
            effect="dark"
            content={row.description}
            placement="top"
            popper-class="desTooltip"
          >
            <span style="width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
              {row.description}
            </span>
          </el-tooltip>

          <Icon
            icon="ep:edit"
            class="text-#409EFF ml5px cursor-pointer"
            onClick={() => {
              UpdateDesDiaRef.value.open(row.description, index)
            }}
          />
        </div>
      )
    }
  },
  {
    field: 'currency',
    label: '供应商币种',
    width: columnWidth.l
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.withTaxPrice.amount}
          min={0}
          precision={moneyPrecision}
          class="!w-90%"
        />
      )
    },
    rules: [{ required: true, message: '请输入单价' }]
  },
  {
    field: 'withTotalTaxPrice',
    label: '采购总金额',
    width: columnWidth.l
  },
  {
    field: 'freeFlag',
    label: '是否含赠品',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.freeFlag}
          dictType={DICT_TYPE.CONFIRM_TYPE}
          onChangeEmit={(val) => {
            if (!val) {
              row.freeQuantity = 0
            }
          }}
        />
      )
    },
    rules: [{ required: true, message: '请选择是否含赠品' }]
  },
  {
    field: 'freeQuantity',
    label: '赠品数量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.freeQuantity}
          precision={0}
          min={0}
          max={row.quantity}
          controls={false}
          clearable={true}
          disabled={!row.freeFlag || row.quantity <= 0}
        />
      )
    }
  },
  {
    field: 'syncQuoteFlag',
    label: '是否同步产品报价',
    width: columnWidth.xl,
    component: <eplus-dict-select dictType={DICT_TYPE.CONFIRM_TYPE} />,
    rules: [{ required: true, message: '请选择是否同步产品报价' }]
  },
  {
    field: 'taxRate',
    label: '税率%',
    minWidth: columnWidth.l,
    component: (
      <el-input-number
        class="mx-4"
        min={0}
        max={99.99}
        precision={2}
        controls={false}
        style="width:100%;margin:0;textAlign:left"
        placeholder="请输入税率"
      />
    ),
    rules: {
      required: true,
      message: '请输入税率'
    }
  },
  {
    field: 'packageFlag',
    label: '是否含包装',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.packageFlag}
          style="width: 90px"
          dictType={DICT_TYPE.CONFIRM_TYPE}
          clearable={false}
          onChangeEmit={() => {
            row.purchasePackagingPrice.amount = ''
          }}
        />
      )
    }
  },
  {
    field: 'packagingPrice',
    label: '包装价',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          min={0}
          precision={moneyPrecision}
          v-model={(row.packagingPrice ??= {}).amount}
          disabled={row.packageFlag}
        />
      )
    }
  },
  {
    field: 'packageType',
    label: '包装方式',
    width: columnWidth.l,
    component: (
      <eplus-input-search-select
        api={packageTypeList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        clearable={false}
        multiple="true"
      />
    ),
    rules: { required: true, message: '请选择包装方式' }
  },
  {
    field: 'freightFlag',
    label: '是否含运费',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.freightFlag}
          style="width: 90px"
          dictType={DICT_TYPE.CONFIRM_TYPE}
          clearable={false}
        />
      )
    }
  },
  {
    field: 'shippingPrice',
    label: '运费单价',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          class="!w-100%"
          controls={false}
          precision={moneyPrecision}
          v-model={(row.shippingPrice ??= {}).amount}
          disabled={row.freightFlag}
        />
      )
    }
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.l,
    component: (
      <el-input-number
        class="mx-4"
        min={0}
        controls={false}
        style="width:100%;margin:0;textAlign:left"
      />
    )
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l,
    component: (
      <el-input-number
        class="mx-4"
        min={0}
        controls={false}
        style="width:100%;margin:0;textAlign:left"
        placeholder="请输入外箱装量"
      />
    ),
    rules: {
      required: true,
      message: '请输入外箱装量'
    }
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.m,
    slot: (item, row: Recordable, index: number) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.m,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row.boxCount * (row.specificationList?.length || 1)}</span>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <div class="flex items-center justify-between">
          <div>{getOuterbox(row, 'grossweight')}</div>

          <Icon
            icon="ep:edit"
            class="ml5px cursor-pointer text-#409EFF"
            onClick={() => {
              UpdateOuterboxSpecRef.value.open(row, index)
            }}
          />
        </div>
      )
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <div class="flex items-center justify-between">
          <div>{getOuterbox(row, 'netweight')}</div>

          <Icon
            icon="ep:edit"
            class="ml5px cursor-pointer text-#409EFF"
            onClick={() => {
              UpdateOuterboxSpecRef.value.open(row, index)
            }}
          />
        </div>
      )
    }
  },

  {
    field: 'purchaseType',
    label: '采购类型',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.PURCHASE_TYPE, row.purchaseType)
      // let flag =
      //   props.type === 'plan' && (!props.formData?.sourceType || props.formData?.sourceType === 1)
      // return (
      //   <eplus-dict-select
      //     v-model={row.purchaseType}
      //     style="width: 150px"
      //     dictType={DICT_TYPE.PURCHASE_TYPE}
      //     disabled={!flag}
      //     clearable={true}
      //     filter={(val) => {
      //       if (
      //         (props.type === 'contract' && props?.formData?.planSourceType === 1) ||
      //         (props.type === 'plan' && !props?.formData?.sourceType)
      //       ) {
      //         return val.filter((item) => item.label !== '标准采购')
      //       } else {
      //         return val
      //       }
      //     }}
      //   />
      // )
    }
  },
  {
    field: 'invoiceStatus',
    label: '开票状态',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.INVOICE_STATUS, row?.invoiceStatus) || '-'
    }
  },
  {
    field: 'invoicedAmount',
    label: '已开票金额',
    width: columnWidth.l
  },
  {
    field: 'invoicedQuantity',
    label: '已开票数量',
    width: columnWidth.l
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
  let quantity = Number(row.quantity)
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
  let quantity = Number(currentRow.value.quantity)
  let integer = Math.floor(quantity / Number(currentRow.value.qtyPerOuterbox))
  if (type === 'ceil') {
    currentRow.value.boxCount = integer + 1
    currentRow.value.quantity = (integer + 1) * Number(currentRow.value.qtyPerOuterbox)
  } else if (type === 'floor') {
    currentRow.value.boxCount = integer
    currentRow.value.quantity = integer * Number(currentRow.value.qtyPerOuterbox)
  }
}
const emit = defineEmits(['updateTotalAmount'])
watch(
  () => tableList,
  (list: any) => {
    if (list.value) {
      //避免重复监听引起总价重复累加
      if (totalHeader.value.purchaseMoney) {
        totalHeader.value.purchaseMoney = 0
        totalHeader.value.purchaseNum = 0
      }
      totalHeader.value.giftNum = 0
      let cskuCodeList: any = []
      let giftNum = 0
      list.value.map((item, index, arr) => {
        if (item?.cskuCode) {
          cskuCodeList.push(item.cskuCode)
        }

        item.boxCount =
          typeof item.quantity === 'number' && item.qtyPerOuterbox !== 0
            ? Math.ceil(item.quantity / item.qtyPerOuterbox)
            : 0
        item.withTaxPrice.amount =
          item.quantity - item.freeQuantity > 0 ? item.withTaxPrice.amount : 0
        item.withTotalTaxPrice = formatterPrice(
          (item.quantity - item.freeQuantity) * item.withTaxPrice.amount || 0,
          moneyPrecision
        )
        totalHeader.value.giftNum += Number(item.freeQuantity)
        totalHeader.value.purchaseNum =
          (Number(item.quantity) || 0) + (totalHeader.value?.purchaseNum || 0)

        item.index = index + 1
        totalHeader.value.purchaseMoney =
          (Number(item.withTotalTaxPrice) || 0) + (Number(totalHeader.value?.purchaseMoney) || 0)

        item.purchaseType = item.purchaseType || PurchaseTypeEnum.STOCK.type
      })
      if (cskuCodeList.length) {
        cskuCodeList = [...new Set(cskuCodeList)]
        totalHeader.value.cskuListNum = cskuCodeList.length || 0
      }
      emit('updateTotalAmount', totalHeader.value)
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
      return el.id
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.id)) {
        return item
      }
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
      item.venderName = defaultQuoteitem.venderName
      item.currency = defaultQuoteitem.currency
      item.qtyPerOuterbox = defaultQuoteitem.qtyPerOuterbox
    })
    tableList.value?.push(...list)
  }
  //tableList.value=list
}
const handleSelectSure = (list) => {
  // if(list&&)
  tableList.value.forEach((item: any) => {
    if (item?.sortNum === list[0]?.value) {
      return Object.assign(item, list[1])
    }
  })
  // selectedQuote=list[0]
}
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

const updateTable = (item) => {
  let fieldList = [
    'withTaxPrice',
    'freightFlag',
    'shippingPrice',
    'packageFlag',
    'packageType',
    'packagingPrice',
    'currency',
    'taxRate',
    'splitBoxFlag',
    'qtyPerOuterbox',
    'specificationList'
  ]
  tableList.value.forEach((el: any) => {
    fieldList.forEach((key: any) => {
      el[key] = item[key]
    })
  })
}

const setCurrency = (val) => {
  tableList.value.forEach((el: any) => {
    el.currency = val
    el.withTaxPrice.currency = val
    el.packagingPrice.currency = val
    el.shippingPrice.currency = val
    el.unitPrice.currency = val
    el.totalPrice.currency = val
  })
}

defineExpose({
  selectedList,
  tableList,
  checkData,
  clearTableList,
  totalHeader,
  updateTable,
  setCurrency
})

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
<style lang="scss">
/* 全局样式 */
.el-popper.is-dark.desTooltip {
  max-width: 500px !important;
  width: auto !important;
}
</style>
