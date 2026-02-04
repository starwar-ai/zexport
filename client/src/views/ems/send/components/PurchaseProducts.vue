<template>
  <div class="mb15px flex">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加产品</el-button
    >
    <el-button
      @click="handleRow"
      class="mr12px"
      >添加行
    </el-button>
    <UploadPics
      ref="uploadPicRef"
      @success="handleUploadSuccess"
    />
    <!-- <el-button @click="handleRemove">移除</el-button> -->
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
    :commonTabFlag="true"
    :auxiliaryTabFlag="true"
  />
  <SelectVenderQuoteDialog
    ref="SelectVenderQuoteRef"
    @sure="handleSelectSure"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import { ProductDialog } from '@/components/ProductDialog'
import SelectVenderQuoteDialog from './SelectVenderQuote.vue'
import { TableColumn } from '@/types/table'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { SkuTypeEnum } from '@/utils/constants'
import * as UserApi from '@/api/system/user'
import UploadPic from './UploadPic.vue'
import UploadPics from './UploadPics.vue'

defineOptions({ name: 'CostAttrsCom' })
const props = defineProps<{
  formData
  type: string
}>()
const simpleUserList = ref([])
const simpleEmsList = ref([])
const TableRef = ref()
const numberFormat = (num, fixNum?) => {
  if (num) {
    if (!num === num) {
      return 0
    } else if (fixNum) {
      return Number(num).toFixed(fixNum)
    } else {
      return Number(num)
    }
  } else return 0
}
const message = useMessage()
const btnText = ref('')
const ProductDialogRef = ref()
const SelectVenderQuoteRef = ref()
const tableList = ref([])
const totalHeader = ref({
  purchaseNum: 0,
  giftNum: 0,
  purchaseMoney: 0,
  cskuListNum: 0
})
const formatLength = (length, width, height) => {
  if (length && width && height) {
    return (
      <span>{length.toFixed(2) + '*' + width.toFixed(2) + '*' + height.toFixed(2) + ' cm'}</span>
    )
  } else {
    return '-'
  }
}
const handleOpenSelect = (row) => {
  try {
    if (row?.venderLists) {
      SelectVenderQuoteRef.value?.open(row, [])
    } else {
      PurchasePlanApi.getVenderQuoteList(row?.skuId).then((res) => {
        let venderList: any = []
        res.map((item) => {
          venderList.push({
            id: item?.id,
            venderId: item?.venderId,
            venderCode: item?.venderCode,
            venderName: item?.venderName,
            unitPrice: item?.unitPrice,
            taxRate: item?.taxRate,
            packageFlag: item?.packageFlag,
            freeFlag: item?.freeFlag,
            freightFlag: item?.freightFlag,
            packageLength: item?.packageLength,
            packageWidth: item?.packageWidth,
            packageHeight: item?.packageHeight,
            outerboxLength: item?.outerboxLength,
            outerboxWidth: item?.outerboxWidth,
            outerboxHeight: item?.outerboxHeight,
            qtyPerInnerbox: item?.qtyPerInnerbox,
            currency: item?.currency
            // paymentId: item?.paymentId,
            // taxType: item?.taxType
          })
        })
        row.venderLists = venderList
        SelectVenderQuoteRef.value?.open(row, [])
      })
    }
  } catch {}
}
//弹框的字段
let productColumns = reactive<TableColumn[]>([
  {
    field: 'code',
    label: '产品编号',
    width: '175px'
  },
  {
    field: 'mainPicture',
    label: '图片',
    width: '150px',
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
    field: 'material',
    label: '产品材质',
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
      return <el-tag type="info">{getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType)}</el-tag>
    }
  }
])
let tableColumns = reactive<TableColumn[]>([
  {
    label: '序号',
    field: 'sortNum',
    fixed: 'left',
    width: 80
  },
  {
    field: 'goodsSource',
    label: '物件来源',
    width: '300px',
    showOverflowTooltip: false,
    component: (
      <eplus-dict-select
        style="width: 100px"
        dictType={DICT_TYPE.GOODS_SOURCE}
        clearable={true}
      />
    ),
    rules: [{ required: true, message: '请选择采购类型' }]
  },

  {
    field: 'skuName',
    label: '产品名称',
    width: '300px',
    component: <el-input placeholder="请输入产品名称" />,
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
    },
    rules: [{ required: true, message: '产品名称' }]
  },
  {
    label: '产品编号',
    field: 'skuCode',
    component: <el-input placeholder="请输入产品编号" />
  },
  {
    field: 'mainPicture',
    label: '图片',
    width: '150px',
    slot: (item, row: Recordable, index: number) => {
      if (row.editPic) {
        return (
          <div class="h-50px flex items-center">
            <UploadPic
              class="row-pic"
              ref="UploadPicRef"
              mainFlag
              onSuccess={(val) => getFilePic(val, row)}
            />
          </div>
        )
      } else {
        return (
          <EplusImgEnlarge
            mainPicture={row?.mainPicture}
            thumbnail={row?.thumbnail}
          />
        )
      }
    },
    rules: { required: true, message: '请上传图片' }
  },
  {
    field: 'quantity',
    label: '数量',
    // width: '150px',
    component: (
      <el-input-number
        style="width: 150px"
        precision={0}
        min={1}
        controls={false}
        clearable={true}
      />
    ),
    rules: [{ required: true, message: '请输入数量' }]
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
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
const spliceColumns = () => {
  if (props?.type === 'plan') {
    const filterList = ['deliveryDate', 'syncQuoteFlag', 'taxType', 'paymentId', 'quoteSelect']
    tableColumns = tableColumns.filter((item, index) => {
      if (filterList.includes(item.field) || (index > 10 && item.field !== 'action')) {
        return false
      } else {
        return true
      }
    })
  } else if (props?.type === 'contract') {
    tableColumns = tableColumns.filter((item) => {
      if (item.field === 'purchaseUserName' || item.field === 'venderName') {
        return false
      } else {
        return true
      }
    })
  }
}
spliceColumns()
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
      list.value.map((item, index, arr) => {
        if (item?.cskuCode) {
          cskuCodeList.push(item.cskuCode)
        }
        if (item?.unitPrice) {
          if (item?.unitPrice?.amount) {
            item.withTaxPrice = item?.unitPrice.amount * (1 + item?.taxRate / 100)
          } else {
            item.withTaxPrice = item?.unitPrice * (1 + item?.taxRate / 100)
          }
        }
        item.withTotalTaxPrice = (item.quantity * item.withTaxPrice || 0).toFixed(3)
        // if ((item.boxCount || isNaN(item.boxCount)) && item.qtyPerInnerbox) {
        //   item.boxCount = Math.ceil(item.quantity / item.qtyPerInnerbox)
        // }
        item.boxCount =
          typeof item.quantity === 'number' && item.qtyPerInnerbox !== 0
            ? Math.ceil(item.quantity / item.qtyPerInnerbox)
            : 0
        item.index = index + 1
        totalHeader.value.purchaseMoney =
          (Number(item.withTotalTaxPrice) || 0) + (Number(totalHeader.value?.purchaseMoney) || 0)
        totalHeader.value.purchaseNum =
          (Number(item.quantity) || 0) + (totalHeader.value?.purchaseNum || 0)
      })
      if (cskuCodeList.length) {
        cskuCodeList = [...new Set(cskuCodeList)]
        totalHeader.value.cskuListNum = cskuCodeList.length || 0
      }
    }
  },
  { immediate: true, deep: true }
)
const fileUrlList = ref<any>([])
const handleUploadSuccess = async (val) => {
  //等到异步emit全部传过来之后，再取值
  let asnycRes = JSON.parse(JSON.stringify(await Promise.allSettled(await val())))
  if (asnycRes?.length) {
    let idList = []
    asnycRes?.map((item, index) => {
      if (item.value.status === 'success') {
        idList.push(item.value.response?.data?.id)
      }
    })
    const uniqueIds = new Set(idList)
    const uniqueList = Array.from(uniqueIds).map((id) =>
      asnycRes.find((item) => item.value.response?.data?.id === id)
    )
    //将包装好的数据放到tablelist
    uniqueList?.map((item) => {
      const { id, name, fileUrl } = item.value.response?.data || {}
      tableList.value?.push({
        sortNum: tableList.value?.length + 1,
        mainPicture: { id, name, fileUrl }
      })
    })
  }
}
watch(
  () => props.formData.sendRegion,
  (val) => {
    tableColumns.forEach((el) => {
      if (el.field === 'mainPicture') {
        el.rules.required = val == 2
      }
    })
  },
  {
    immediate: true,
    deep: true
  }
)
const handleAdd = async () => {
  if (!props.formData.receiveType) {
    message.error('请先选择收件方类型！')
    return
  }
  if (props.formData.receiveType == getDictValue(DICT_TYPE.EMS_RECEIVE_TYPE, '客户')) {
    if (!props.formData.receiveCode) {
      message.error('请先选择收件方')
      return
    }
    ProductDialogRef.value.open([], props.formData?.receiveCode)
  } else {
    ProductDialogRef.value.open([])
  }
  // if (props.formData?.custId && props.formData?.custCode) {
  //   ProductDialogRef.value.open([], props.formData?.custCode)
  // } else {
  //   ProductDialogRef.value.open([])
  // }
}
const handleRow = async () => {
  var index = tableList.value.length == 0 ? 1 : tableList.value.length + 1
  const newRecord = {
    sortNum: index,
    editPic: true,
    goodsSource: 1,
    skuCode: '',
    skuName: '',
    picture: {
      id: 0,
      name: '',
      fileUrl: '',
      mainFlag: 1
    },
    quantity: 0,
    mainPicture: {
      id: 0,
      name: '',
      fileUrl: '',
      mainFlag: 1
    }
  }
  tableList.value?.push(newRecord)
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

const getFilePic = (params, row) => {
  row.mainPicture = params[0]
  row.mainPicture.mainFlag = 1
  row.editPic = false
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
  tableList.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
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
    })
    tableList.value?.push(...list)
    console.log(tableList.value, 'tableList.value')
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
defineExpose({ selectedList, tableList, checkData, clearTableList })

onMounted(async () => {
  simpleUserList.value = await UserApi.getSimpleUserList()
})
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

.row-pic {
  height: 40px;
}

.uploadImg {
  height: 40px;
}
</style>
