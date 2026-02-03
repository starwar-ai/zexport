<template>
  <div class="mb15px flex">
    <el-button @click="handleSelect"> 选择产品 </el-button>
    <!-- <el-button
      type="primary"
      @click="handleAdd"
      >添加行
    </el-button> -->
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{
      withTaxPrice: { amount: 0, currency: '' },
      quotation: { amount: 0, currency: '' }
    }"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
    :max-height="430"
  />
  <ProductDialog
    ref="ProductDialogRef"
    @sure="handleSure"
    :selectionFlag="true"
    :defaultVal="{}"
    :schema="productColumns"
    :isShowTabs="true"
    :commonTabFlag="true"
  />

  <MoqDia
    ref="MoqDiaRef"
    @sure="updateRow"
  />

  <SelectVenderQuote
    ref="SelectVenderQuoteRef"
    @sure="updateQuete"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { ProductDialog } from '@/components/ProductDialog'
import { TableColumn } from '@/types/table'
import { columnWidth } from '@/utils/table'
import { formatDate } from '@/utils/formatTime'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import { UploadPic } from '@/components/UploadPic'
import * as ConfigApi from '@/api/infra/config'
import { volPrecision, VolumeUnit } from '@/utils/config'
import { formatNum } from '@/utils/index'
import MoqDia from './MoqDia.vue'
import SelectVenderQuote from '@/views/scm/purchase/components/SelectVenderQuote.vue'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'
// 统一设置报价
import { useRateStore } from '@/store/modules/rate'
import { EplusMoney, EplusNumInput } from '@/components/EplusMoney'

defineOptions({ name: 'PurchaseProducts' })
const props = defineProps<{
  formData
}>()
const TableRef = ref()
const message = useMessage()
const btnText = ref('')
const ProductDialogRef = ref()
const tableList = ref([])
//20尺柜
const twentyFootCabinetFeeConfigValue = ref(0)
//散货
const bulkHandlingFeeConfigValue = ref(0)
//散货起始价格
const bulkHandlingStartFeeConfigValue = ref(0)
const totalHeader = ref({
  purchaseNum: 0,
  giftNum: 0,
  purchaseMoney: 0,
  cskuListNum: 0
})
const unitRadio = ref('metric')

//弹框的字段
let productColumns = reactive<TableColumn[]>([
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
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
  // 报价单选择基础产品，客户货号和品牌不用显示
  // {
  //   field: 'cskuCode',
  //   label: '客户货号',
  //   width: columnWidth.l
  // },
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
  // {
  //   field: 'brandName',
  //   label: '品牌',
  //   width: columnWidth.l
  // },
  {
    field: 'skuType',
    label: '产品类型',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <el-tag type="info">{getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType)}</el-tag>
    }
  }
])
const router = useRouter()
const openProductDetail = (row) => {
  setSourceId(row.skuId)
  if (row.custProFlag == 1) {
    if (checkPermi(['pms:csku:query']) && checkPermi(['pms:csku:detail'])) {
      router.push({ path: '/base/product-manage/csku' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
    if (checkPermi(['pms:own-brand-sku:query']) && checkPermi(['pms:own-brand-sku:detail'])) {
      router.push({ path: '/base/product-manage/own' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
    if (checkPermi(['pms:sku:query']) && checkPermi(['pms:sku:detail'])) {
      router.push({ path: '/base/product-manage/main' })
    } else {
      message.error('暂无权限查看详情')
    }
  }
}

let tableColumns = reactive<TableColumn[]>([
  {
    label: '产品编号',
    field: 'skuCode',
    width: columnWidth.xl,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        if (row.skuCode) {
          return (
            <span
              onClick={() => openProductDetail(row)}
              style="color:rgb(0, 91, 245);cursor:pointer;"
            >
              {row.skuCode}
            </span>
          )
        } else {
          return <span>-</span>
        }
      } else {
        return <el-input v-model={row.skuCode} />
      }
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    label: '产品类型',
    field: 'skutype',
    width: columnWidth.m,
    slot: (item, row: Recordable, index: number) => {
      if (row.custProFlag == 1) {
        return <span>客户产品</span>
      } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
        return <span>自营产品</span>
      } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
        return <span>基础产品</span>
      }
    }
  },
  {
    field: 'mainPicture',
    label: '产品图片',
    width: columnWidth.m,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        return (
          <EplusImgEnlarge
            mainPicture={row?.mainPicture}
            thumbnail={row?.thumbnail}
          />
        )
      } else {
        return (
          <UploadPic
            ref="UploadPicRef"
            fileList={row?.UploadPicRef}
            onSuccess={(val) => getPicList(val, row)}
            limit="1"
          />
        )
      }
    }
  },
  {
    field: 'name',
    label: '产品中文名称',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        if (row.name) {
          return <span>{row.name}</span>
        } else {
          return <span>-</span>
        }
      } else {
        return <el-input v-model={row.name} />
      }
    }
  },
  {
    field: 'nameEng',
    label: '产品英文名称',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        if (row.nameEng) {
          return <span>{row.nameEng}</span>
        } else {
          return <span>-</span>
        }
      } else {
        return <el-input v-model={row.nameEng} />
      }
    }
  },
  {
    field: 'withTaxPrice',
    label: '工厂报价',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        if (row.skuCode) {
          return <span>{<EplusMoneyLabel val={row.withTaxPrice} />}</span>
        } else {
          return <span>-</span>
        }
      } else {
        return (
          <el-input-number
            precision={3}
            min={1}
            controls={false}
            clearable={true}
            onChange={(value) => {
              row.withTaxPrice = {
                amount: value,
                currency: props.formData.currency
              }
            }}
          />
        )
      }
    }
  },
  {
    field: 'profitRate',
    label: '利润率(%)',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.profitRate}
          style="width: 150px"
          precision={2}
          min={0}
          clearable={true}
          onChange={(value) => {
            row.quotation = getQuotation(row.withTaxPrice, row.profitRate)
            row.quotationAmount = row.quotation?.amount
            setQuotation()
          }}
        />
      )
    },
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value) {
          callback(new Error('请输利润率'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'quotation',
    label: '产品报价',
    width: columnWidth.xxl,
    slot: (item, row: Recordable, index: number) => {
      if (row.skuCode) {
        return (
          <EplusMoney
            v-model:amount={row.quotation.amount}
            v-model:currency={row.quotation.currency}
            currencyDisabled={true}
            onChange={() => {
              getProfitRate(row)
            }}
          />
        )
      } else {
        return <span>-</span>
      }
    },
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.amount) {
          callback(new Error('请输入产品报价'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'moq',
    label: '起订量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.moq}
          min={0}
          controls={false}
          precision={0}
          style="width: 150px"
          onBlur={(...$event) => moqChange($event, row)}
        />
      )
    },
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^[1-9]\d*$/
        if (!regx.test(value)) {
          callback(new Error('请输入大于0的整数'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        if (row.venderName) {
          return (
            <span>
              {row.venderName}
              <el-button
                link
                type="primary"
                onClick={() => {
                  selectVenderQuote(row, index)
                }}
              >
                选择报价
              </el-button>
            </span>
          )
        } else {
          return <span>-</span>
        }
      } else {
        return <el-input v-model={row.venderName} />
      }
    }
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.l
  },
  {
    field: 'freightFlag',
    label: '是否含运费',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{getDictLabel(DICT_TYPE.IS_INT, row.freightFlag)}</span>
    }
  },
  {
    field: 'shippingPrice',
    label: '运费',
    width: columnWidth.xxl,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusMoney
          v-model:amount={row.shippingPrice.amount}
          v-model:currency={row.shippingPrice.currency}
          currencyDisabled={true}
          amountDisabled={row.freightFlag === 1}
        />
      )
    }
  },
  // {
  //   field: 'qtyPerInnerbox',
  //   label: '内盒装量',
  //   width: columnWidth.l,
  //   slot: (item, row: Recordable, index: number) => {
  //     if (!row.isHandleAdd) {
  //       if (row.qtyPerInnerbox) {
  //         return <span>{row.qtyPerInnerbox}</span>
  //       } else {
  //         return <span>-</span>
  //       }
  //     } else {
  //       return <el-input v-model={row.qtyPerInnerbox} />
  //     }
  //   }
  // },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.l
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        if (row.qtyPerOuterbox) {
          return <span>{row.qtyPerOuterbox}</span>
        } else {
          return <span>-</span>
        }
      } else {
        return <el-input v-model={row.qtyPerOuterbox} />
      }
    }
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: '100',
    formatter: (item, row, index) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.xl,
    formatter: (item, row, index) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜',
    width: columnWidth.m
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜',
    width: columnWidth.m
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜',
    width: columnWidth.m
  },
  {
    field: 'bulkHandlingVolume',
    label: '散货',
    width: columnWidth.m,
    slot: (item, row: Recordable, index: number) => {
      return (
        <span>
          {formatNum(row.bulkHandlingVolume / 1000000, volPrecision)} {VolumeUnit}
        </span>
      )
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'netweight')}</div>
    }
  },
  {
    field: 'hsCode',
    label: '海关编码',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        if (row.hsCode) {
          return <span>{row.hsCode}</span>
        } else {
          return <span>-</span>
        }
      } else {
        return <el-input v-model={row.hsCode} />
      }
    }
  },
  {
    field: 'quoteDate',
    label: '交货日期',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (!row.isHandleAdd) {
        return <span>{formatDate(row.quoteDate, 'YYYY-MM-DD')}</span>
      } else {
        return (
          <el-date-picker
            v-model={row.quoteDate}
            clearable
            valueFormat="x"
            type="date"
            style="width: 100%"
          />
        )
      }
    }
  },
  {
    field: 'action',
    label: '操作',
    width: columnWidth.l,
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            await delRow(index)
          }}
        >
          移除
        </el-button>
      )
    }
  }
])

const SelectVenderQuoteRef = ref()
const selectVenderQuote = (row, index) => {
  SelectVenderQuoteRef.value?.open(row, index)
}
const updateQuete = (dataObj) => {
  Object.assign(tableList.value[dataObj.index], dataObj.data)
}

const getPicList = (params, row) => {
  if (params?.length > 0) {
    row.mainPicture = params[0]
  }
}

// 箱数计算
const MoqDiaRef = ref()
const currentRow = ref()
const moqChange = (e, row) => {
  currentRow.value = row
  let remainder = Number(row.moq) % Number(row.qtyPerOuterbox),
    integer = Math.floor(Number(row.moq) / Number(row.qtyPerOuterbox))
  if (remainder > 0) {
    row.boxCount = integer + 1
    let des = `输入的起订量计算得出箱数为${integer},余数为${remainder}。您可以选择向下取整起订量为${integer * Number(row.qtyPerOuterbox)}箱数为${integer}，或者向上取整箱数起订量为${(integer + 1) * Number(row.qtyPerOuterbox)}为${integer + 1}`
    MoqDiaRef.value.open(des)
  } else {
    row.boxCount = Number(row.moq) / Number(row.qtyPerOuterbox)
    getCabinet(row)
  }
}
const updateRow = (type) => {
  let integer = Math.floor(Number(currentRow.value.moq) / Number(currentRow.value.qtyPerOuterbox))
  if (type === 'ceil') {
    currentRow.value.boxCount = integer + 1
    currentRow.value.moq = (integer + 1) * Number(currentRow.value.qtyPerOuterbox)
  } else if (type === 'floor') {
    currentRow.value.boxCount = integer
    currentRow.value.moq = integer * Number(currentRow.value.qtyPerOuterbox)
  }
  getCabinet(currentRow.value)
}

const rateList = useRateStore().rateList
const setQuotation = () => {
  if (props.formData.currency) {
    tableList.value.forEach((row: any) => {
      let rateExchange = rateList[row.withTaxPrice.currency]
      let formDataRate = rateList[props.formData.currency]
      if (formDataRate && rateExchange) {
        row.quotation.amount = (
          ((Number(row.withTaxPrice.amount) * Number(rateExchange)) / Number(formDataRate)) *
          Number(1 + row.profitRate * 0.01)
        ).toFixed(2)
        row.quotation.currency = props.formData.currency
      }
    })
  }
}

//计算利润率
const getProfitRate = (row) => {
  let rateExchange = rateList[row.quotation.currency]
  let rowRate = rateList[row.withTaxPrice.currency]
  row.profitRate = (
    ((Number(row.quotation.amount) * Number(rateExchange)) /
      Number(rowRate) /
      Number(row.withTaxPrice?.amount) -
      1) *
    100
  ).toFixed(3)
}
//计算报价
const getQuotation = (unitPrice, profitRate) => {
  //-** 产品报价 = 工厂报价*(1+利润率)
  var qoValue = '0'
  if (unitPrice.amount && profitRate) {
    var rate = Number(1 + profitRate * 0.01)
    if (unitPrice?.amount) {
      qoValue = Number(unitPrice?.amount * rate).toFixed(3)
    }
  }
  return { amount: qoValue, currency: unitPrice.currency ? unitPrice.currency : null }
}

const getCabinet = (row) => {
  let totalVolumeFormat = (Number(row.boxCount) * getOuterboxVal(row, 'vol')) / 1000000
  let fortyFootContainerNum = 0,
    fortyFootCabinetNum = 0,
    twentyFootCabinetNum = 0,
    bulkHandlingVolume = 0
  //容器计算
  let remainNum = Number(totalVolumeFormat)
  if (Number(remainNum) > 68) {
    fortyFootContainerNum = (Number(totalVolumeFormat) / 68) | 0
    remainNum = Number(remainNum) - 68 * Number(fortyFootContainerNum)
  }
  if (Number(remainNum) > 59 && Number(remainNum) <= 68) {
    fortyFootContainerNum += 1
  } else {
    if (Number(remainNum) > 49 && Number(remainNum) <= 59) {
      fortyFootCabinetNum = 1
    } else {
      if (Number(remainNum) > 29 && Number(remainNum) <= 49) {
        twentyFootCabinetNum = 1
        remainNum = Number(remainNum) - 29
      }
      if (Number(remainNum) >= 20 && Number(remainNum) <= 29) {
        twentyFootCabinetNum += 1
      } else {
        let bulkHandlingMoney = bulkHandlingStartFeeConfigValue.value
        if (remainNum > 1) {
          bulkHandlingMoney =
            Number(bulkHandlingMoney) +
            Number(bulkHandlingFeeConfigValue.value) * (Math.ceil(remainNum) - 1)
        }
        if (Number(bulkHandlingMoney - twentyFootCabinetFeeConfigValue.value) > 0) {
          twentyFootCabinetNum += 1
          bulkHandlingVolume = 0
          bulkHandlingMoney =
            Number(bulkHandlingMoney) - Number(twentyFootCabinetFeeConfigValue.value)
        } else {
          bulkHandlingVolume = remainNum * 1000000
        }
      }
    }
  }

  row.twentyFootCabinetNum = twentyFootCabinetNum
  row.fortyFootContainerNum = fortyFootContainerNum
  row.fortyFootCabinetNum = fortyFootCabinetNum
  row.bulkHandlingVolume = bulkHandlingVolume
}

const handleSelect = async () => {
  if (props.formData?.custId && props.formData?.custCode) {
    ProductDialogRef.value.open([], props.formData?.custCode)
  } else {
    ProductDialogRef.value.open([])
  }
}

const handleAdd = async () => {
  var index = tableList.value.length - 1
  const newRecord = {
    sortNum: index,
    isHandleAdd: true
  }
  tableList.value.push(newRecord)
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

const delRow = (index) => {
  tableList.value.splice(index, 1)
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  if (list && list.length > 0) {
    list.forEach((item, index) => {
      //根据产品弹窗是否多选来控制序号的递增
      item.sortNum =
        list.length === 1 ? tableList.value.length + 1 : tableList.value.length + index + 1
      //利润率默认15
      item.profitRate = 15
      item.quotation = getQuotation(item.withTaxPrice, item.profitRate)
      item.quotationAmount = item.quotation?.amount
      item.boxCount = Math.ceil(Number(item.moq) / Number(item.qtyPerOuterbox))
      item.hsMeasureUnit = item.hsdata?.unit || ''
      // item.packageLengthEng = Number(item.packageLength) / LengthRatio
      // item.packageWidthEng = Number(item.packageWidth) / LengthRatio
      // item.packageHeightEng = Number(item.packageHeight) / LengthRatio
      // item.outerboxLengthEng = Number(item.outerboxLength) / LengthRatio
      // item.outerboxWidthEng = Number(item.outerboxWidth) / LengthRatio
      // item.outerboxHeightEng = Number(item.outerboxHeight) / LengthRatio
      // item.outerboxVolumeEng = Number(item.outerboxVolume) / VolumeRatio
      getCabinet(item)
    })
    tableList.value?.push(...list)
    setQuotation()
    console.log('tableList', tableList.value)
  }
}
const checkData = async () => {
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
defineExpose({ selectedList, tableList, checkData, clearTableList, totalHeader, setQuotation })

onMounted(async () => {
  const config = await ConfigApi.getConfigSimplelist({})
  if (config?.length) {
    let twentyFootCabinetFeeConfig = config.filter(
      (item) => item.category == 'twenty_foot_cabinet_fee'
    )
    if (twentyFootCabinetFeeConfig?.length) {
      twentyFootCabinetFeeConfigValue.value = twentyFootCabinetFeeConfig[0].value
    }
    let bulkHandlingFeeConfig = config.filter((item) => item.category == 'bulk_handling_fee')
    if (bulkHandlingFeeConfig?.length) {
      bulkHandlingFeeConfigValue.value = bulkHandlingFeeConfig[0].value
    }
    let bulkHandlingStartFeeConfig = config.filter(
      (item) => item.category == 'bulk_handling_start_fee'
    )
    if (bulkHandlingStartFeeConfig?.length) {
      bulkHandlingStartFeeConfigValue.value = bulkHandlingStartFeeConfig[0].value
    }
  }
  if (isValidArray(props.formData.children)) {
    let tableData = cloneDeep(props.formData.children)
    tableList.value = tableData.map((item, index) => {
      return {
        ...item
      }
    })
    setQuotation()
  } else {
    tableList.value = []
  }
})

watchEffect(async () => {})
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
