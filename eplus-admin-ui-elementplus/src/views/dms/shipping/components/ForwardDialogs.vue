<template>
  <Dialog
    v-model="dialogTableVisible"
    v-if="dialogTableVisible"
    :title="modelName"
    width="1200"
    append-to-body
    destroy-on-close
  >
    <el-form
      v-if="tableType === 'requestTable'"
      ref="requestTableFormRef"
      :model="requestTableForm"
      label-width="120px"
    >
      <el-form-item
        label="预计拉柜日期"
        prop="inboundDate"
        :rules="[
          {
            required: true,
            message: '请选择预计拉柜日期',
            trigger: 'change'
          }
        ]"
      >
        <el-date-picker
          type="date"
          v-model="requestTableForm.inboundDate"
          placeholder="请选择预计拉柜日期"
          value-format="x"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item
        label="出运方式"
        prop="shipmentType"
        :rules="{
          required: true,
          message: '请选择出运方式'
        }"
      >
        <EplusDictRadio
          v-model="requestTableForm.shipmentType"
          :dictType="DICT_TYPE.SHIPMENT_TYPE"
        />
      </el-form-item>
      <el-form-item
        label="提单号/进仓编号"
        prop="referenceNumber"
        :rules="[
          {
            required: true,
            message: '请输入提单号/进仓编号'
          }
        ]"
      >
        <el-input
          v-model="requestTableForm.referenceNumber"
          class="!w-220px"
        />
      </el-form-item>
    </el-form>
    <!-- 转拉柜通知单 -->
    <eplus-table
      v-if="tableType === 'requestTable'"
      :eplusTableSchema="eplusTableSchema"
      ref="eplusTableRef"
      key="eplusTable"
      :height="400"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          {{ modelName }}
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import * as ContainerNoticeApi from '@/api/dms/containerNotice'
import { formatDateColumn, formatDictColumn } from '@/utils/table'
import { DICT_TYPE } from '@/utils/dict'

const message = useMessage()
defineOptions({ name: 'ForwardDialogs' })
const dialogTableVisible = ref(false)
const dialogProcessVisible = ref(false)
const tableType = ref()
const modelName = ref()
const eplusTableRef = ref()
const tableRef = ref()
const emit = defineEmits<{
  sure
}>()
const props = defineProps<{
  detailInfo: any
}>()

const oneLevelList = ref([])
const twoLevelList = ref([])
const threeLevelList = ref([])

const iterationCount = ref(0)
const parentData = ref()
//拉柜通知单的请求响应数据
const asyncTableData = ref()
const requestTableFormRef = ref()
const requestTableForm = reactive({
  inboundDate: Date.now(),
  shipmentType: 1,
  referenceNumber: ''
})
let eplusTableSchema = reactive({
  getListApi: async () => {
    if (tableType.value === 'requestTable') {
      const res = await ContainerNoticeApi.getMidContainer({
        ids: parentData.value.join(','),
        shippedAddress: factoryOutboundFlag.value ? 1 : 0
      })
      asyncTableData.value = res
      asyncTableData.value?.children.forEach((e) => {
        let totalCabinetQuantity = 0
        if (e.purchaseModel == 2 && e.skuType == 2) {
          if (e.children && e.children.length) {
            e.children.forEach((item) => {
              totalCabinetQuantity += item.availableCabinetQuantity
            })
          }
        }
        e.processFlag = e.shippingQuantity - totalCabinetQuantity > 0 ? true : false
      })
      requestTableForm.referenceNumber = res.referenceNumber
      iterationCount.value++
      let resList = res.children
      //本次使用数量计算 如果：可用数量和 <= 出运数量 本次使用数量 = 可用数量
      //           如果：可用数量和 > 出运数量 只有一条库存记录 本次使用数量 = 出运数量
      resList.forEach((item) => {
        if (item.children?.length > 0) {
          let shippingQuantity = item.shippingQuantity
          let availableCabinetQuantitSum = 0
          item.children?.forEach((s) => {
            if (s.availableCabinetQuantity > 0) {
              availableCabinetQuantitSum += s.availableCabinetQuantity
            }
          })
          if (availableCabinetQuantitSum <= shippingQuantity) {
            item.children?.forEach((s) => {
              s.usedQuantity = s.availableCabinetQuantity
            })
          } else {
            if (item.children?.length == 1) {
              item.children[0].usedQuantity = shippingQuantity
            }
          }
        }
      })
      return {
        list: resList,
        total: resList?.length
      }
    }
  },
  hideChildCheckBox: true,
  selection: true,
  showSettings: false,
  columns: [
    {
      prop: 'saleContractCode',
      label: '外销合同号',
      minWidth: '100px',
      parent: true,
      fixed: 'left'
    },
    {
      prop: 'skuName',
      label: '报关中文名称',
      parent: true,
      minWidth: '100px'
    },
    {
      prop: 'skuNameEng',
      label: '报关英文名称',
      minWidth: '100px',
      parent: true
    },

    {
      prop: 'shippingQuantity',
      label: '出运数量',
      minWidth: '100px',
      parent: true
    },
    // {
    //   prop: 'estPickupTime',
    //   label: '预计拉柜日期',
    //   minWidth: '100px',
    //   parent: true,
    //   formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    // },
    {
      prop: 'batchCode',
      label: '批次号',
      minWidth: '100px'
    },
    {
      prop: 'companyName',
      label: '公司名称',
      minWidth: '100px'
    },
    {
      prop: 'stockMethod',
      label: '库存类型',
      formatter: formatDictColumn(DICT_TYPE.STOCK_METHOD),
      minWidth: '100px'
    },
    {
      prop: 'inboundDate',
      label: '入库日期',
      minWidth: '100px',
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    },
    {
      prop: 'shippedAddress',
      label: '发货地点',
      minWidth: '100px',
      formatter: formatDictColumn(DICT_TYPE.SHIPPED_ADDRESS)
    },
    {
      prop: 'availableCabinetQuantity',
      label: '可用数量',
      minWidth: '100px'
    },
    {
      prop: 'usedQuantity',
      label: '本次使用数量',
      minWidth: '100px'
      // slots: {
      //   default: (data) => {
      //     const { row } = data
      //     return (
      //       <el-input-number
      //         v-model={row.usedQuantity}
      //         min={0}
      //         max={row.availableCabinetQuantity}
      //         controls={false}
      //       />
      //     )
      //   }
      // }
    },
    {
      prop: 'purchaseContractCode',
      label: '采购合同',
      minWidth: '100px'
    },
    {
      prop: 'venderName',
      label: '供应商',
      minWidth: '200px'
    }
  ]
})

watch(
  () => iterationCount.value,
  (val, old) => {
    if (val === 1) {
      // handleSureProcess()
    } else {
      iterationCount.value = 0
    }
  }
  // { immediate: true, deep: true }
)

//工厂出库标识
const factoryOutboundFlag = ref(0)
const open = async (param: any, type = 0) => {
  try {
    //将param传参提成响应式数据
    parentData.value = param
    tableType.value = 'requestTable'
    factoryOutboundFlag.value = type
    modelName.value = type == 1 ? '工厂出库' : '转拉柜/进仓通知单'
    dialogTableVisible.value = true
    if (tableType.value === 'requestTable') {
      nextTick(() => {
        eplusTableRef.value.selectAll()
      })
    }
  } catch (e) {
    console.log(e)
  }
}

const handleCancel = () => {
  dialogTableVisible.value = false
  tableType.value = undefined
}
function replacer(key, value) {
  if (key === 'parent') return undefined // 忽略 parent 属性
  return value
}

// 转拉柜通知单校验
const checkItemsContainer = async (param) => {
  if (!param?.length) {
    message.warning('请选择要进行转单的数据')
    return false
  } else {
    return stockValidate(param)
    //库存校验
    // if (!stockValidate(param)) {
    //   return false
    // }

    //是否需要自动生产
    // let needAutoComplete =
    //   param.filter((item) => {
    //     return item.producingQuantity > 0
    //   })?.length > 0
    // const shippingQuantitySkuMap = new Map()
    // const usedQuantitySkuMap = new Map()
    // param.forEach((pa) => {
    //   let sumShippingQuantity = shippingQuantitySkuMap[pa.skuCode]
    //   if (!sumShippingQuantity) {
    //     sumShippingQuantity = pa.shippingQuantity
    //   } else {
    //     sumShippingQuantity += pa.shippingQuantity
    //   }
    //   shippingQuantitySkuMap[pa.skuCode] = sumShippingQuantity
    //   let sumUsedQuantity = usedQuantitySkuMap[pa.skuCode]
    //   pa.children?.forEach((cl) => {
    //     if (!sumUsedQuantity) {
    //       sumUsedQuantity = cl.usedQuantity
    //     } else {
    //       sumUsedQuantity += cl.usedQuantity
    //     }
    //   })
    //   usedQuantitySkuMap[pa.skuCode] = sumUsedQuantity
    // })
    // //是否需要组装
    // let needAutoAssemble =
    //   param.filter((item) => {
    //     return shippingQuantitySkuMap[item.skuCode] > usedQuantitySkuMap[item.skuCode]
    //   })?.length > 0

    // if (!needAutoComplete && !needAutoAssemble) {
    //   return true
    // }

    // //组合产品拆分采购才可以自动加工，否则报错
    // let canNotAutoAssembletItems = param.filter((item) => {
    //   return (
    //     shippingQuantitySkuMap[item.skuCode] > usedQuantitySkuMap[item.skuCode] &&
    //     item.manufactureFlag == getDictValue(DICT_TYPE.IS_INT, '否')
    //   )
    // })
    // if (canNotAutoAssembletItems.length > 0) {
    //   let skuName = canNotAutoAssembletItems[0].skuName
    //   message.error(skuName + ',库存不足！')
    //   return false
    // }

    // let msg = ''
    // if (needAutoComplete && needAutoAssemble) {
    //   msg =
    //     '存在未生产完成和库存数量不足记录信息，点击确定会对相应记录进行自动完成和加工，是否继续？'
    // } else if (needAutoComplete) {
    //   msg = '存在未生产完成记录，点击确定会对相应记录进行自动完成，是否继续？'
    // } else if (needAutoAssemble) {
    //   msg = '存在库存数量不足记录信息，点击确定会对相应记录进行加工，是否继续？'
    // }
    // return new Promise((resolve, reject) => {
    //   ElMessageBox.confirm(msg, '提示', {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //     type: 'warning'
    //   })
    //     .then(async () => {
    //       resolve(true)
    //     })
    //     .catch(() => {
    //       reject(false)
    //     })
    // })
  }
}

const stockValidate = (param) => {
  let result = true
  let item: any = {}
  param.forEach((pi) => {
    if (pi.children?.length > 0) {
      pi.children.forEach((rc) => {
        if (!(Number(rc.availableCabinetQuantity) >= Number(rc.usedQuantity))) {
          item = rc
          result = false
        }
      })
    }
  })
  if (!result) {
    message.warning(`存在异常数据，批次号${item.batchCode}本次使用数量大于可用数量`)
    return false
  } else {
    return true
  }
  // let batchCodes = allChildrens?.map((item) => {
  //   return item.batchCode + ',' + item.saleContractCode
  // })
  // const uniqueBatchCodes = batchCodes?.reduce((acc, current) => {
  //   return acc.includes(current) ? acc : [...acc, current]
  // }, [])
  // uniqueBatchCodes?.forEach((it) => {
  //   let stockArrays = allChildrens.filter((item) => {
  //     return item.batchCode + ',' + item.saleContractCode === it
  //   })
  //   //可用数量
  //   let availableCabinetQuantity = stockArrays[0].availableCabinetQuantity
  //   //使用数量
  //   let usedQuantity = 0
  //   stockArrays.forEach((it) => {
  //     if (it.usedQuantity && Number(it.usedQuantity) > 0) {
  //       usedQuantity += it.usedQuantity
  //     }
  //   })
  //   if (usedQuantity > availableCabinetQuantity) {
  //     message.warning('同一批次总使用数量不可大于可用数量')
  //     result = false
  //     return
  //   }
  // })
  // return result
}

const submitItemContainerFn = async (param) => {
  const { children, ...result } = asyncTableData.value
  result.inboundDate = requestTableForm.inboundDate
  result.shipmentType = requestTableForm.shipmentType
  result.referenceNumber = requestTableForm.referenceNumber
  let requestParams = JSON.parse(JSON.stringify({ ...result, children: param }, replacer))
  requestParams.shipmentItemIdList = param.map((item) => item.shipmentItemId)
  requestParams.factoryOutboundFlag = factoryOutboundFlag.value ? 1 : undefined
  const res = await ContainerNoticeApi.createContainer(requestParams)
  message.notifyPushSuccess('转拉柜通知单成功', res, 'shipment-container-notice')
  // 发出 sure 事件，通知父组件刷新页面
  emit('sure')
}

const handleSure = async () => {
  try {
    if (tableType.value === 'requestTable') {
      // if (!estPickupTimeRef.value) {
      //   message.warning('请选择预计拉柜日期')
      //   return
      // }
      let valid = await requestTableFormRef.value?.validate()
      if (!valid) return false
      let checkedItems = eplusTableRef.value.checkedItems
      let realItems = []
      checkedItems.forEach((cItem) => {
        let realObj = { ...cItem }
        if (realObj.children?.length > 0) {
          realObj.children = realObj.children.filter((it) => {
            return it.usedQuantity > 0
          })
        }
        realItems.push(realObj)
      })
      let checkResult = await checkItemsContainer(realItems)
      if (checkResult) {
        await submitItemContainerFn(realItems)
        handleCancel()
      } else {
        return false
      }
    }
  } catch (e) {
    console.log(e, 'e')
  }
}
defineExpose({ open })
</script>

<style lang="scss" scoped>
.tip {
  color: red;
  margin-left: 15px;
  margin-right: 3px;
}

.input-referenceNumber {
  margin-left: 45px;
}
</style>
