<template>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    :maxHeight="maxHeight"
    :selectable="
      (row) => {
        return row.convertedFlag !== 1
      }
    "
    @selection-change="handleSelectionChange"
  />
  <div
    v-if="isValidArray(twoLevelList)"
    class="!w-100%"
  >
    <p>二级拆分单</p>
    <eplus-form-table
      ref="twoLevelListRef"
      :list="twoLevelList"
      :defaultVal="{}"
      :maxHeight="maxHeight"
      :schema="twoTableColumns"
    />
  </div>
  <div
    v-if="isValidArray(threeLevelList)"
    class="!w-100%"
  >
    <p>三级拆分单</p>
    <eplus-form-table
      ref="threeLevelListRef"
      :list="threeLevelList"
      :defaultVal="{}"
      :maxHeight="maxHeight"
      :schema="combineTableColumns"
    />
  </div>
  <SelectVenderQuoteDialog
    ref="SelectVenderQuoteRef"
    @sure="handleSelectSure"
  />

  <StockLockCreateDia
    ref="StockLockCreateDiaRef"
    @sure="handleSureStockLockCreat"
  />

  <SelectCompany
    ref="SelectCompanyRef"
    @sure="handleSureSelectCompany"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { PurchaseSourceTypeEnum, SkuTypeEnum } from '@/utils/constants'
import SelectVenderQuoteDialog from '../../components/SelectVenderQuote.vue'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'
import { getCompanyIdList } from '@/utils/companyPathUtils'
import StockLockCreateDia from './StockLockCreateDia.vue'
import { isValidArray } from '@/utils/is'
import SelectCompany from './SelectCompany.vue'
// import ContractUpdateSku from '../../components/ContractUpdateSku.vue'
import UpdateSku from './UpdateSku.vue'
import { columnWidth } from '@/utils/table'
import { moneyPrecision } from '@/utils/config'
import { EplusNumInput } from '@/components/EplusMoney'
import { formatterPrice } from '@/utils'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import { useUserStore } from '@/store/modules/user'

const SelectVenderQuoteRef = ref()
const TableRef = ref()
const twoLevelListRef = ref()
const threeLevelListRef = ref()
const props = defineProps<{
  id?: number
  maxHeight?: number
  formData
}>()
// const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
//   updateDialogActions: (...args: any[]) => void
//   clearDialogActions: () => void
// }
// const { close } = inject('dialogEmits') as {
//   close: () => void
// }

const message = useMessage()
const tableList = ref([])
const twoLevelList = ref([])
const threeLevelList = ref([])

const generateUniqueNumber = () => {
  let str1 = (Math.random() * 10000000).toString().slice(0, 4)
  let str2 = (Math.random() * 10000000).toString().slice(0, 4)
  return Number(`${str1}${str2}`)
}

const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleOpenSelect = (row, index) => {
  SelectVenderQuoteRef.value?.open(row, index)
}
const handleSelectSure = (dataObj) => {
  if (dataObj?.row.levels == 1) {
    updateRow(tableList.value, dataObj?.row?.id, dataObj.data, dataObj.index)
  } else if (dataObj?.row.levels == 2) {
    updateRow(twoLevelList.value, dataObj?.row?.id, dataObj.data, dataObj.index)
  } else if (dataObj?.row.levels == 3) {
    updateRow(threeLevelList.value, dataObj?.row?.id, dataObj.data, dataObj.index)
  }
}

const updateRow = (list, rowId, newRow, rowIndex) => {
  if (rowId) {
    list.forEach((item: any) => {
      if (item?.id === rowId) {
        item = Object.assign(item, {
          ...newRow,
          id: rowId,
          unitPrice: newRow.unitPrice?.amount,
          withTaxPrice: newRow.withTaxPrice?.amount
        })
      }
    })
  } else {
    list[rowIndex] = Object.assign(list[rowIndex], {
      ...newRow,
      id: rowId,
      unitPrice: newRow.unitPrice?.amount,
      withTaxPrice: newRow.withTaxPrice?.amount
    })
  }
}

let tableColumns = reactive<TableColumn[]>([
  {
    field: 'convertedFlag',
    label: '转合同状态',
    fixed: 'left',
    width: '100px',
    slot: (item, row: Recordable, index: number) => {
      if (row.purchaseModel == 2) {
        return <span></span>
      } else {
        return <span>{getDictLabel(DICT_TYPE.CONVERTED_FLAG, row.convertedFlag)}</span>
      }
    }
  },
  {
    field: 'mainPicture',
    label: '物料图片',
    minWidth: columnWidth.m,
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
    minWidth: columnWidth.l,
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
    field: 'skuCode',
    label: '产品编号',
    minWidth: '175px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <UpdateSku
          row={row}
          updateFlag={
            row.skuDeletedFlag && row.needPurQuantity > 0 && !checkSplit(row) ? true : false
          }
          onUpdate={() => handleUpdateSku(row, index)}
        />
      )
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-user-select
          disabled={row?.sourceType === PurchaseSourceTypeEnum.SALECONTRACT.type || row.editFlag}
          v-model={row.purchaseUserId}
          onChange={(val) => {
            if (val) {
              row.purchaseUserName = val.nickname
              row.purchaseUserDeptId = val.deptId
              row.purchaseUserDeptName = val.deptName
            }
          }}
          //simpleUserList={{ value: row?.purchaseUserList || [] }}
          class="!w-90%"
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
    field: 'venderName',
    label: '供应商名称',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <span>
          {row?.venderName || '-'}
          &nbsp;&nbsp;&nbsp;
          <span
            class="selectQuote"
            onClick={() => handleOpenSelect(row, index)}
          >
            选择报价
          </span>
        </span>
      )
    }
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
    field: 'saleQuantity',
    label: '销售数量',
    width: columnWidth.l
  },
  {
    field: 'availableQuantity',
    label: '库存',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (row.purchaseModel == 1 && row.purchaseType == 2) {
        if (row?.availableQuantity) {
          return (
            <span>
              {row?.availableQuantity || '-'}
              &nbsp;&nbsp;&nbsp;
              <span
                class="selectQuote"
                onClick={() => handleLock(row, index, row.levels)}
              >
                分配库存
              </span>
            </span>
          )
        } else {
          return <span>{row?.availableQuantity == 0 ? 0 : '-'}</span>
        }
      } else {
        return <span>{row?.availableQuantity == 0 ? 0 : '-'}</span>
      }
    }
  },
  {
    field: 'currentLockQuantity',
    label: '已锁库存',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row?.currentLockQuantity || 0}</span>
    }
  },
  {
    field: 'needPurQuantity',
    label: '待采购数量',
    width: columnWidth.l
  },
  {
    field: 'purchaseQuantity',
    label: '采购数量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.purchaseQuantity}
          precision={0}
          min={0}
          max={row.maxQuantity}
          controls={false}
          clearable={true}
          disabled={row.purchaseModel == 2}
        />
      )
    },
    rules: [{ required: true, message: '请输入采购数量' }]
  },
  {
    field: 'convertedQuantity',
    label: '已转合同数量',
    width: columnWidth.l
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.withTaxPrice}
          min={0}
          precision={moneyPrecision}
          class="!w-90%"
          disabled={row.editFlag}
        />
      )
    },
    rules: [{ required: true, message: '请输入含税单价' }]
  },

  {
    field: 'currency',
    label: '币种',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.currency}
          style="width: 90px"
          dictType={DICT_TYPE.CURRENCY_TYPE}
          clearable={true}
          disabled
        />
      )
    }
  },
  {
    field: 'withTotalTaxPrice',
    label: '含税总金额',
    width: columnWidth.l
  },

  {
    field: 'qtyPerOuterbox',
    label: '单箱数量',
    width: columnWidth.l
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row.boxCount * (row.specificationList?.length || 1)}</span>
    }
  },
  {
    field: 'taxRate',
    label: '税率',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return <span>{row?.taxRate}%</span>
    }
  },
  {
    field: 'packageFlag',
    label: '是否含包装',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.packageFlag}
          style="width: 90px"
          dictType={DICT_TYPE.CONFIRM_TYPE}
          clearable={false}
          disabled={row.editFlag}
        />
      )
    }
  },
  {
    field: 'freightFlag',
    label: '是否含运费',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.freightFlag}
          style="width: 90px"
          dictType={DICT_TYPE.CONFIRM_TYPE}
          clearable={false}
          disabled={row.editFlag}
        />
      )
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    minWidth: columnWidth.xl,
    formatter: (item, row: Recordable, index: number) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'purchaseModel',
    label: '采购模式',
    fixed: 'right',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (row.skuType === 2 && row.needPurQuantity > 0 && !checkSplit(row)) {
        return (
          <div class="flex items-center">
            <el-button
              link
              type="primary"
              onClick={() => handleSplit(row)}
            >
              {row.purchaseModel == 2 ? '组合采购' : '拆分采购'}
            </el-button>
          </div>
        )
      } else {
        return <div></div>
      }
    }
  }
])
const checkIsEdit = (row) => {
  if (row.skuType === 2 && row.needPurQuantity > 0 && !checkSplit(row)) {
    return row.purchaseModel == 2
  } else {
    return row.convertedFlag == 1
  }
}

const checkSplit = (row) => {
  if (row.levels == 1) {
    return twoLevelList.value
      .filter((item: any) => item.oneSplitNum == row.oneSplitNum)
      .some((el: any) => el.convertedFlag == 1)
  } else if (row.levels == 2) {
    return threeLevelList.value
      .filter((item: any) => item.oneSplitNum == row.oneSplitNum)
      .some((el: any) => el.convertedFlag == 1)
  }
}
//拆分
const SelectCompanyRef = ref()
const handleSplit = async (row) => {
  if (props.formData.saleType == 3 && props.formData.companyNature === 4) {
    message.warning('采购主体是国外企业，无法拆分采购！！！')
    return false
  }
  if (row.purchaseModel == 2) {
    if (row.levels == 1) {
      twoLevelList.value = twoLevelList.value.filter((item) => item.oneSplitNum != row.oneSplitNum)
      threeLevelList.value = threeLevelList.value.filter(
        (item) => item.oneSplitNum != row.oneSplitNum
      )
    } else if (row.levels == 2) {
      threeLevelList.value = threeLevelList.value.filter(
        (item) => item.twoSplitNum != row.twoSplitNum
      )
    }
    row.purchaseModel = row.purchaseModel == 1 ? 2 : 1
    row.editFlag = checkIsEdit(row)
  } else {
    if (row.levels == 1 && props?.formData.companyNature != 1) {
      SelectCompanyRef.value.open(row)
    } else {
      let splitFlag = await getSplitList(row)
      if (splitFlag) {
        row.purchaseModel = row.purchaseModel == 1 ? 2 : 1
        row.editFlag = checkIsEdit(row)
      }
    }
  }
}

const getSplitList = async (row) => {
  let res = await PurchasePlanApi.getProductMixdetail({
    planItemSkuCode: row.skuCode,
    demergeQuantity: row.purchaseQuantity,
    planId: row.purchasePlanId,
    purchaseCompanyId: row.purchaseCompanyId
  })
  let validFlag = false
  let list = res.map((item) => {
    if (isValidArray(item.quoteitemList)) {
      let obj = {
        skuId: item.id,
        skuCode: item.code,
        // venderId: item.quoteitemList[0]?.venderId,
        // venderCode: item.quoteitemList[0]?.venderCode,
        // venderName: item.quoteitemList[0]?.venderName,
        // currency: item.quoteitemList[0]?.currency,
        // taxRate: item.quoteitemList[0]?.taxRate,
        // packageLength: item.quoteitemList[0]?.packageLength,
        // packageWidth: item.quoteitemList[0]?.packageWidth,
        // packageHeight: item.quoteitemList[0]?.packageHeight,
        // outerboxLength: item.quoteitemList[0]?.outerboxLength,
        // outerboxWidth: item.quoteitemList[0]?.outerboxWidth,
        // outerboxHeight: item.quoteitemList[0]?.outerboxHeight,
        // qtyPerOuterbox: item.quoteitemList[0]?.qtyPerOuterbox,
        // freightFlag: item.quoteitemList[0]?.freightFlag,
        // packageFlag: item.quoteitemList[0]?.packageFlag,
        ...item.quoteitemList[0],
        unitPrice: item.quoteitemList[0]?.unitPrice?.amount,
        withTaxPrice: item.quoteitemList[0]?.withTaxPrice?.amount,
        convertedFlag: 0,
        sourceOrderCode: row.sourceOrderCode,
        purchasePlanId: row.purchasePlanId,
        purchaseCompanyId: row.purchaseCompanyId || '',
        purchaseCompanyName: row.purchaseCompanyName || '',
        parentSkuName: row.skuName,
        purchaseModel: 1,
        saleContractCode: row.saleContractCode,
        saleItemUniqueCode: row.saleItemUniqueCode,
        saleContractItemId: row.saleContractItemId,
        saleContractId: row.saleContractId,
        initNeedPurQuantity: item.convertedQuantity || 0 + item.needPurQuantity,
        ...item
      }
      if (row.levels == 1) {
        return {
          ...obj,
          oneSplitNum: row.oneSplitNum,
          twoSplitNum: generateUniqueNumber(),
          levels: 2,
          id: undefined
        }
      } else if (row.levels == 2) {
        return {
          ...obj,
          oneSplitNum: row.oneSplitNum,
          twoSplitNum: row.twoSplitNum,
          threeSplitNum: generateUniqueNumber(),
          levels: 3,
          id: undefined
        }
      }
    } else {
      validFlag = true
    }
  })
  if (validFlag) {
    message.warning('拆分采购失败，拆分产品缺少供应商报价信息')
    return false
  }
  if (row.levels == 1) {
    twoLevelList.value = [...twoLevelList.value, ...list]
    return true
  } else if (row.levels == 2) {
    threeLevelList.value = [...threeLevelList.value, ...list]
    return true
  }
}
const handleSureSelectCompany = async (data) => {
  let row: any = tableList.value.find((item) => item.id === data.id)
  let splitFlag = await getSplitList(data)
  if (splitFlag) {
    row.companyId = data.purchaseCompanyId
    row.companyName = data.purchaseCompanyName
    row.splitCompanyId = data.purchaseCompanyId
    row.splitCompanyName = data.purchaseCompanyName
    row.purchaseModel = row.purchaseModel == 1 ? 2 : 1
    row.editFlag = checkIsEdit(row)
  }
}

const StockLockCreateDiaRef = ref()
const handleLock = (row, index, level) => {
  StockLockCreateDiaRef.value?.open({
    companyIdList: level > 1 ? [row.purchaseCompanyId] : row.companyIdList,
    skuCode: row.skuCode || row.code,
    sourceOrderCode: row.purchasePlanCode,
    sourceOrderType: 4,
    sourceOrderItemId: row.id,
    saleContractItemId: row.saleContractItemId,
    saleContractCode: props.formData?.saleContractCode,
    index,
    level,
    stockLockSaveReqVOList: row.stockLockSaveReqVOList,
    saleQuantity: row.saleQuantity,
    saleLockQuantity: row.saleLockQuantity
  })
}

const handleSureStockLockCreat = (obj) => {
  if (obj.level == 1) {
    let item: any = tableList.value[obj.index]
    item.currentLockQuantity = obj.lockQuantityTotal
    item.availableQuantity = obj.availableQuantityTotal
    item.stockLockSaveReqVOList = obj.list
  } else if (obj.level == 2) {
    let item: any = twoLevelList.value[obj.index]
    item.currentLockQuantity = obj.lockQuantityTotal
    item.availableQuantity = obj.availableQuantityTotal
    item.stockLockSaveReqVOList = obj.list
  } else if (obj.level == 3) {
    let item: any = threeLevelList.value[obj.index]
    item.currentLockQuantity = obj.lockQuantityTotal
    item.availableQuantity = obj.availableQuantityTotal
    item.stockLockSaveReqVOList = obj.list
  }
}
const getNeedPurQuantity = (list) => {
  list.forEach((item: any) => {
    if (item.saleQuantity > 0) {
      let val = Number(item.initNeedPurQuantity) - Number(item.currentLockQuantity || 0)
      item.needPurQuantity = val < 0 ? 0 : val
      item.maxQuantity = item.needPurQuantity - item.convertedQuantity || 0
      if (item.purchaseQuantity > item.maxQuantity) {
        item.purchaseQuantity = item.maxQuantity
      }
    }
  })
}
const tableFormat = (list) => {
  if (isValidArray(list)) {
    let wainingFlag = false
    list.forEach((item, index) => {
      if (item?.unitPrice?.amount) {
        item.unitPrice = item.unitPrice.amount
      }
      if (Object.hasOwn(item?.withTaxPrice, 'amount')) {
        item.withTaxPrice = item.withTaxPrice.amount
      }
      // item.withTaxPrice = item?.unitPrice * (1 + item?.taxRate / 100)
      item.unitPrice = (item?.withTaxPrice / (1 + item?.taxRate / 100)).toFixed(3)
      //是赠品含税总金额=0

      item.withTotalTaxPrice = formatterPrice(
        item.needPurQuantity * (Number(item.withTaxPrice) || 0),
        moneyPrecision
      )
      item.boxCount =
        typeof item.needPurQuantity === 'number' && item.qtyPerOuterbox !== 0
          ? Math.ceil(item.needPurQuantity / item.qtyPerOuterbox)
          : 0
      item.index = index + 1
      if (item.needPurQuantity === 0) {
        wainingFlag = true
      }
    })
    emit('setwaining', wainingFlag)
  }
}
watch(
  () => [tableList.value, twoLevelList.value, threeLevelList.value],
  ([list1, list2, list3]) => {
    getNeedPurQuantity(list1)
    getNeedPurQuantity(list2)
    getNeedPurQuantity(list3)
    tableFormat(list1)
    tableFormat(list2)
    tableFormat(list3)
  },
  {
    immediate: true,
    deep: true
  }
)

let combineTableColumns = reactive<TableColumn[]>([
  {
    field: 'convertedFlag',
    label: '转合同状态',
    fixed: 'left',
    width: '100px',
    slot: (item, row: Recordable, index: number) => {
      if (row.purchaseModel == 2) {
        return <span></span>
      } else {
        return <span> {getDictLabel(DICT_TYPE.CONVERTED_FLAG, row.convertedFlag)}</span>
      }
    }
  },
  {
    label: '采购主体',
    field: 'purchaseCompanyName',
    width: 150
  },
  {
    label: '父物料名称',
    field: 'parentSkuName',
    width: 150
  },
  {
    field: 'mainPicture',
    label: '物料图片',
    minWidth: columnWidth.m,
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
    minWidth: columnWidth.l,
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
    field: 'skuCode',
    label: '产品编号',
    minWidth: '175px',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <UpdateSku
          row={row}
          updateFlag={
            row.skuDeletedFlag && row.needPurQuantity > 0 && !checkSplit(row) ? true : false
          }
          onUpdate={() => handleUpdateSku(row, index)}
        />
      )
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      row?.purchaseUserList?.forEach((item) => {
        item.id = item.userId
      })
      return (
        <eplus-user-select
          disabled={row?.sourceType === PurchaseSourceTypeEnum.SALECONTRACT.type || row.editFlag}
          v-model={row.purchaseUserId}
          onChange={(val) => {
            if (val) {
              row.purchaseUserName = val.nickname
              row.purchaseUserDeptId = val.deptId
              row.purchaseUserDeptName = val.deptName
            }
          }}
          //simpleUserList={{ value: row?.purchaseUserList || [] }}
          class="!w-90%"
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
    field: 'venderName',
    label: '供应商名称',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <span>
          {row?.venderName || '-'}
          &nbsp;&nbsp;&nbsp;
          <span
            class="selectQuote"
            onClick={() => handleOpenSelect(row, index)}
          >
            选择报价
          </span>
        </span>
      )
    }
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
    field: 'venderProdCode',
    label: '工厂货号',
    width: columnWidth.l
  },
  {
    field: 'purchaseType',
    label: '采购类型',
    width: columnWidth.l,
    showOverflowTooltip: false,
    component: (
      <eplus-dict-select
        style="width: 150px"
        dictType={DICT_TYPE.PURCHASE_TYPE}
        clearable={true}
        disabled
      />
    ),
    rules: [{ required: true, message: '请选择采购类型' }]
  },

  // {
  //   field: 'freeFlag',
  //   label: '是否赠品',
  //   width: columnWidth.l,
  //   slot: (item, row: Recordable, index: number) => {
  //     return (
  //       <eplus-dict-select
  //         v-model={row.freeFlag}
  //         dictType={DICT_TYPE.CONFIRM_TYPE}
  //         class="!w-90%"
  //         disabled={row.editFlag}
  //       />
  //     )
  //   },
  //   rules: [{ required: true, message: '请选择是否赠品' }]
  // },
  {
    field: 'availableQuantity',
    label: '库存',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (row.purchaseModel == 1 && row.purchaseType == 2 && row?.availableQuantity > 0) {
        return (
          <span>
            {row?.availableQuantity || '-'}
            &nbsp;&nbsp;&nbsp;
            <span
              class="selectQuote"
              onClick={() => handleLock(row, index, row.levels)}
            >
              分配库存
            </span>
          </span>
        )
      } else {
        return <span>{row?.availableQuantity || '-'}</span>
      }
    }
  },
  {
    field: 'currentLockQuantity',
    label: '已锁库存',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row?.currentLockQuantity || 0}</span>
    }
  },
  {
    field: 'needPurQuantity',
    label: '待采购',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row?.needPurQuantity || 0}</span>
    },
    rules: [{ required: props.formData?.sourceType !== 2, message: '请输入待采购数量' }]
  },
  {
    field: 'purchaseQuantity',
    label: '采购数量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row?.purchaseQuantity || 0}</span>
    }
  },

  {
    field: 'sonSkuCount',
    label: '配件数量',
    width: columnWidth.l
  },
  {
    field: 'qtyPerOuterbox',
    label: '单箱数量',
    width: columnWidth.l
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row.boxCount * (row.specificationList?.length || 1)}</span>
    }
  },

  {
    field: 'taxRate',
    label: '税率',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return <span>{row?.taxRate}%</span>
    }
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.withTaxPrice}
          min={0}
          controls={false}
          precision={moneyPrecision}
          class="!w-90%"
          disabled={row.editFlag}
        />
      )
    },
    rules: [{ required: true, message: '请输入单价' }]
  },
  {
    field: 'withTotalTaxPrice',
    label: '含税总金额',
    width: columnWidth.l
  },
  {
    field: 'currency',
    label: '币种',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.currency}
          style="width: 90px"
          dictType={DICT_TYPE.CURRENCY_TYPE}
          clearable={true}
          disabled
        />
      )
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
          disabled={row.editFlag}
        />
      )
    }
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
          disabled={row.editFlag}
        />
      )
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    minWidth: columnWidth.xl,
    formatter: (item, row: Recordable, index: number) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  }
])

let twoTableColumns = reactive([
  ...combineTableColumns,
  {
    field: 'purchaseModel',
    label: '采购模式',
    fixed: 'right',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (row.skuType === 2 && row.needPurQuantity > 0 && !checkSplit(row)) {
        return (
          <div class="flex items-center">
            <el-button
              link
              type="primary"
              onClick={() => handleSplit(row)}
            >
              {row.purchaseModel == 2 ? '组合采购' : '拆分采购'}
            </el-button>
          </div>
        )
      } else {
        return <div></div>
      }
    }
  }
])

const emit = defineEmits(['handleSuccess', 'sucess', 'setwaining'])

// const getQuantity = async (companyIdList, list) => {
//   let data = []
//   if (isValidArray(list)) {
//     list.forEach((item) => {
//       data.push({
//         companyIdList: companyIdList,
//         skuCode: item.skuCode,
//         sourceOrderCode: item.purchasePlanCode,
//         saleContractCode: item.saleContractCode || undefined
//       })
//     })
//     return await DomesticSaleContractApi.queryTotalStock(data)
//   } else {
//     return {}
//   }
// }

const handleUpdateSku = async (row, index) => {
  //   tableList
  // twoLevelList
  // threeLevelList
  const oldId = row?.id
  let res = await getSkuInfo(row.skuCode)
  let defaultQuoteitemList = res?.quoteitemDTOList?.filter((item) => item.defaultFlag === 1)[0]
  let defaultUser =
    defaultQuoteitemList?.buyers.find((obj) => obj.defaultFlag === 1) ||
    defaultQuoteitemList?.buyers[0]
  row = Object.assign(row, {
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

const init = () => {
  let companyIdList = []
  if (props.formData.companyPath !== undefined) {
    companyIdList =
      props.formData.companyPath.id == null
        ? [props.formData.companyId]
        : getCompanyIdList(props.formData.companyPath)
    companyIdList = companyIdList.concat(props.formData?.producedCompanyIdList)
    companyIdList = Array.from(new Set(companyIdList))
    // let res = await getQuantity(companyIdList, props.formData.planList)
    // let res2 = await getQuantity(companyIdList, props.formData?.combineList)
    props?.formData?.planList?.forEach((item) => {
      let obj = {
        ...item,
        editFlag: checkIsEdit(item),
        // availableQuantity: res[item.skuCode],
        companyIdList: companyIdList,
        levels: 1,
        oneSplitNum: item.oneSplitNum || generateUniqueNumber(),
        purchaseQuantity: Number(item.needPurQuantity) - Number(item.convertedQuantity || 0),
        initNeedPurQuantity: Number(item.needPurQuantity) + Number(item.currentLockQuantity || 0)
      }
      tableList.value.push(obj)
    })
    props?.formData?.combineList?.forEach((item) => {
      let obj = {
        ...item,
        purchaseQuantity: item.needPurQuantity,
        initNeedPurQuantity: Number(item.needPurQuantity) + Number(item.currentLockQuantity || 0),
        editFlag: checkIsEdit(item),
        // availableQuantity: res2[item.skuCode],
        companyIdList: companyIdList
      }
      if (item.levels === 2) {
        twoLevelList.value.push(obj)
      }
      if (item.levels === 3) {
        threeLevelList.value.push(obj)
      }
    })
  }
}

const currentUserId = useUserStore().getUser.id

const checkSelectedList = () => {
  if (selectedList.value.length > 0) {
    let purchaseUserIdList = [
      ...new Set(selectedList.value.map((item: any) => item.purchaseUserId))
    ]
    if (purchaseUserIdList.length === 1 && purchaseUserIdList[0] === currentUserId) {
      return true
    } else {
      message.warning('请选择采购员为本人的数据进行下推')
      return false
    }
  } else {
    message.warning('请选择产品')
    return false
  }
}

const checkData = async (type) => {
  let valid = await TableRef.value.validate()
  let valid2 = twoLevelListRef.value ? await twoLevelListRef.value.validate() : true
  let valid3 = threeLevelListRef.value ? await threeLevelListRef.value.validate() : true
  if (valid && valid2 && valid3) {
    if (type === 1) {
      return checkSelectedList()
    } else {
      return true
    }
  } else {
    message.warning('产品信息提交信息有误')
    return false
  }
}

const getTableDate = (type) => {
  const oneNumList = selectedList.value.map((item: any) => item.oneSplitNum)
  if (type === 'one') {
    return selectedList.value
  } else if (type === 'two') {
    return twoLevelList.value.filter((item: any) => oneNumList.includes(item.oneSplitNum))
  } else if (type === 'three') {
    return threeLevelList.value.filter((item: any) => oneNumList.includes(item.oneSplitNum))
  }
}

defineExpose({ tableList, twoLevelList, threeLevelList, checkData, init, getTableDate })
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

:deep(.selectQuote) {
  color: #005bf5;
  cursor: pointer;
}

:deep(.selectQuote):hover {
  text-decoration: underline;
  text-underline-offset: 2px;
  text-decoration-color: #005bf5;
}
</style>
