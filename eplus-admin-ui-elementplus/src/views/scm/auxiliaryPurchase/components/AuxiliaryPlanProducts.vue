<template>
  <div class="d-block mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加包材
    </el-button>
    <el-button @click="handleRemove">移除</el-button>
  </div>

  <div class="d-block total-header flex justify-between">
    <span>合计</span>
    <span>
      产品种类 :{{ totalHeader.cskuListNum }}&nbsp;采购总量 :
      {{ numberFormat(totalHeader.purchaseNum) }}
    </span>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{ withTaxPrice: { amount: undefined, currency: undefined } }"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
    :max-height="430"
  />
  <AuxiliaryDialog
    ref="ProductDialogRef"
    @sure="handleSure"
  />
  <SaleContractDialog
    ref="SaleContractDialogRef"
    @sure="handleSaleSure"
  />
  <PurchaseContractDialog
    ref="PurchaseContractDialogRef"
    @sure="handlePurchaseSure"
  />
  <SelectVenderQuoteDialog
    ref="SelectVenderQuoteRef"
    @sure="handleSelectSure"
  />
</template>
<script setup lang="tsx">
import AuxiliaryDialog from './AuxiliaryDialog.vue'
import SaleContractDialog from './SaleContractDialog.vue'
import SelectVenderQuoteDialog from './SelectVenderQuote.vue'
import PurchaseContractDialog from './PurchaseContractDialog.vue'
import { TableColumn } from '@/types/table'
import * as UserApi from '@/api/system/user'
import UploadList from '@/components/UploadList/index.vue'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatNum, toAnyString } from '@/utils/index'
import { getConfigKey } from '@/api/common'
import { columnWidth } from '@/utils/table'

defineOptions({ name: 'AuxiliaryProducts' })
const props = defineProps<{
  formData
  type: string
}>()
const simpleUserList = ref([])
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
const SaleContractDialogRef = ref()
const PurchaseContractDialogRef = ref()
const SelectVenderQuoteRef = ref()
const tableList = ref([])
const totalHeader = ref({
  purchaseNum: 0,
  giftNum: 0,
  purchaseMoney: 0,
  cskuListNum: 0
})
const getFileList = (params, row) => {
  row.annex = params
}

let tableColumns = reactive<TableColumn[]>([
  {
    label: '序号',
    field: 'sortNum',
    fixed: 'left',
    width: 80
  },
  {
    field: 'auxiliarySkuType',
    label: '包材使用方式',
    width: columnWidth.xl,
    showOverflowTooltip: false,
    sortable: true,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.auxiliarySkuType}
          dictType={DICT_TYPE.AUXILIARY_PURCHASE_TYPE}
          clearable={true}
          disabled={props?.type === 'contract'}
          onChangeEmit={(val) => {
            row.auxiliarySkuType = val
            Object.assign(row, {
              auxiliarySaleContractCode: '',
              remark: '',
              specRemark: '',
              quantity: '',
              purchaseUserName: '',
              auxiliaryCskuCode: '',
              auxiliarySkuName: '',
              auxiliaryPurchaseContractCode: '',
              auxiliarySkuList: []
            })
          }}
        />
      )
    },
    rules: [{ required: true, message: '请选择采购类型' }]
  },
  {
    field: 'mainPicture',
    label: '包材图片',
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
    label: '包材名称',
    width: columnWidth.l,
    sortable: true
  },
  {
    field: 'skuCode',
    label: '包材编号',
    width: columnWidth.l
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    width: columnWidth.l,
    showOverflowTooltip: false,
    formatter: (item, row) => {
      return getDictLabel(DICT_TYPE.MEASURE_UNIT, row?.measureUnit)
    }
  },
  {
    field: 'auxiliarySaleContractCode',
    label: '销售合同',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <span>
          {row?.auxiliarySaleContractCode ? row.auxiliarySaleContractCode + '  ' : ''}
          <el-button
            onClick={() => {
              SaleContractDialogRef.value.open(row?.index, row.auxiliarySaleContractCode)
            }}
            disabled={row?.auxiliarySkuType != 1 || props?.type === 'contract'}
          >
            {row?.auxiliarySaleContractCode ? '...' : '选择销售合同'}
          </el-button>
        </span>
      )
    }
    // rules: [
    //   {
    //     required: false,
    //     message: '请选择销售合同',
    //     validator: (rule: any, value: any, callback: any) => {
    //       console.log(value, 'validator')
    //     }
    //   }
    // ]
  },
  {
    field: 'auxiliaryPurchaseContractCode',
    label: '采购合同',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <span>
          {row?.auxiliaryPurchaseContractCode ? row.auxiliaryPurchaseContractCode + '  ' : ''}
          <el-button
            onClick={() => {
              PurchaseContractDialogRef.value.open(row?.index, row.auxiliaryPurchaseContractCode)
            }}
            disabled={row?.auxiliarySkuType != 2 || props?.type === 'contract'}
          >
            {row?.auxiliaryPurchaseContractCode ? '...' : '选择采购合同'}
          </el-button>
        </span>
      )
    }
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l
  },
  {
    field: 'totalQuantity',
    label: '合同数量',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return formatNum(row?.totalQuantity)
    }
  },
  {
    field: 'auxiliarySkuName',
    label: '品名',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <span>
          <el-select
            v-model={row.auxiliarySkuName}
            disabled={
              (!row.auxiliaryPurchaseContractCode && !row.auxiliarySaleContractCode) ||
              props?.type === 'contract'
            }
            placeholder="请选择品名"
            onChange={(val) => {
              if (val) {
                let selectedItem = row?.auxiliarySkuList?.find((item: any) => {
                  return item.skuCode === val
                })
                row.auxiliaryCskuCode = selectedItem?.cskuCode
                row.auxiliarySkuCode = selectedItem?.skuCode
                row.auxiliarySkuId = selectedItem?.skuId
                row.auxiliarySkuName = selectedItem?.skuName
                row.auxiliaryPurchaseContractItemId = selectedItem?.id
              }
            }}
          >
            {row?.auxiliarySkuList
              ? row?.auxiliarySkuList?.map((item: any) => {
                  return (
                    <el-option
                      key={item.skuCode}
                      label={`${item.sortNum}-${item.skuName}`}
                      value={item.skuCode}
                    />
                  )
                })
              : ''}
          </el-select>
        </span>
      )
    }
  },
  {
    field: 'auxiliaryCskuCode',
    label: '客户货号'
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    width: columnWidth.l,
    batchEditFlag: true,
    batchEditCom: <eplus-user-select></eplus-user-select>,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-user-select
          v-model={row.purchaseUserId}
          onChange={(val) => {
            if (val) {
              row.purchaseUserId = val.userId
              row.purchaseUserName = val.nickname
              row.purchaseUserDeptId = val.deptId
              row.purchaseUserDeptName = val.deptName
            }
          }}
        ></eplus-user-select>
      )
    },
    rules: [
      {
        required: true,
        message: '请选择采购员'
      }
    ]
  },
  {
    field: 'needPurQuantity',
    label: '采购数量',
    width: columnWidth.l,
    component: (
      <el-input-number
        precision={0}
        min={1}
        controls={false}
        clearable={true}
      />
    ),
    rules: [{ required: true, message: '请输入采购数量' }]
  },
  // {
  //   field: 'quantity',
  //   label: '采购数量',
  //   width: columnWidth.l,
  //   component: (
  //     <el-input-number
  //       precision={0}
  //       min={1}
  //       controls={false}
  //       clearable={true}
  //     />
  //   ),
  //   rules: [{ required: true, message: '请输入采购数量' }]
  // },
  {
    field: 'specRemark',
    label: '规格描述',
    width: columnWidth.l,
    component: <el-input />
  },
  {
    field: 'annex',
    label: '附件',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <UploadList
          ref="UploadRef"
          fileList={row?.annex}
          onSuccess={(val) => getFileList(val, row)}
        />
      )
    }
  },
  {
    field: 'remark',
    label: '备注',
    width: columnWidth.l,
    component: <el-input />
  },
  {
    field: 'action',
    label: '操作',
    width: columnWidth.l,
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <div>
          <el-button
            link
            type="primary"
            onClick={async () => {
              await delRow(index)
            }}
          >
            移除
          </el-button>
          <el-button
            link
            type="primary"
            onClick={async () => {
              await handleCopy(row)
            }}
          >
            复制
          </el-button>
        </div>
      )
    }
  }
])

watch(
  () => tableList,
  (list: any) => {
    if (list.value) {
      //避免重复监听引起总价重复累加
      if (totalHeader.value.purchaseNum) {
        totalHeader.value.purchaseNum = 0
      }
      let skuCodeList = []

      list.value.map((item, index, arr) => {
        skuCodeList.push(item.skuCode)
        item.index = index + 1
        if (props?.type === 'contract') {
          totalHeader.value.purchaseNum =
            (Number(item.quantity) || 0) + (totalHeader.value?.purchaseNum || 0)
        } else {
          totalHeader.value.purchaseNum =
            (Number(item.needPurQuantity) || 0) + (totalHeader.value?.purchaseNum || 0)
        }
      })
      totalHeader.value.cskuListNum = [...new Set(skuCodeList)].length || 0
    }
  },
  { immediate: true, deep: true }
)
const handleAdd = async () => {
  ProductDialogRef.value.open([])
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
  tableList.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
}
const handleCopy = (row) => {
  let obj = { ...row, id: undefined, sortNum: tableList.value.length + 1 }
  tableList.value.push(obj)
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
      item.uid = toAnyString()
      item.purchaseUserId = defaultPurchaseUserId.value || undefined
    })
    tableList.value?.push(...list)
  }
}
const handleSaleSure = (list) => {
  tableList.value?.forEach((item: any) => {
    if (item.sortNum === list[1]) {
      item.auxiliarySaleContractCode = list[0]?.code
      item.totalQuantity = list[0]?.totalQuantity
      // 当选择销售合同时，获取销售合同的产品列表
      if (list[0]?.children && list[0].children.length > 0) {
        list[0].children.forEach((item) => {
          item.skuName = item.name
        })
        item.auxiliarySkuList = list[0]?.children
      }
    }
  })
}
const handlePurchaseSure = (list) => {
  tableList.value?.forEach((item: any) => {
    if (item.sortNum === list[1]) {
      item.auxiliaryPurchaseContractCode = list[0]?.code
      item.totalQuantity = list[0]?.totalQuantity
      item.auxiliarySkuName = ''
      item.auxiliaryCskuCode = ''
      item.auxiliarySkuList = list[0]?.children
      item.venderName = list[0]?.venderName
      item.venderCode = list[0]?.venderCode
      item.venderId = list[0]?.venderId
    }
  })
}
const handleSelectSure = (list) => {
  tableList.value.forEach((item: any) => {
    if (item?.sortNum === list[0]?.value) {
      return Object.assign(item, list[1])
    }
  })
}
const checkData = async () => {
  if (tableList.value.length == 0) {
    message.warning(`请选择${btnText.value}`)
    return false
  } else {
    let valid = await TableRef.value.validate()
    if (valid) {
      console.log(1)
      return TableRef.value.tableData
    } else {
      message.warning('产品提交信息有误')
      return false
    }
  }
}
defineExpose({ selectedList, tableList, checkData })

const defaultPurchaseUserId = ref()
const getDefaultPurchaseUserId = async () => {
  let userCode = await getConfigKey('auxiliary.purchase.user.default')
  let userList = await UserApi.getSimpleUserList()
  if (userCode) {
    defaultPurchaseUserId.value = userList.find((item) => item.code === userCode)?.id
  }
}

onMounted(async () => {
  // simpleUserList.value = await UserApi.getSimpleUserList()
  await getDefaultPurchaseUserId()
  if (props?.type === 'contract') {
    tableColumns = tableColumns.filter((item) => {
      if (['needPurQuantity', 'purchaseUserName'].includes(item.field)) {
        return false
      } else {
        return true
      }
    })
  } else {
    tableColumns = tableColumns.filter((item) => {
      if (['quantity'].includes(item.field)) {
        return false
      } else {
        return true
      }
    })
  }
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
</style>
