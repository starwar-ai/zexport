<template>
  <Dialog
    v-model="dialogTableVisible"
    title="转付款申请"
    width="1000"
    destroy-on-close
    @close="handleCancel"
  >
    <div>
      <el-tabs
        v-model="activeName"
        @tab-click="handleTabsClick"
        type="card"
      >
        <el-tab-pane
          label="预付"
          name="prepay"
          :disabled="isPrepaidDisabled"
        >
          <eplus-form-table
            v-if="activeName === 'prepay'"
            ref="PreTableRef"
            selectionFlag
            :list="preTableData"
            :allSelectable="allSelectFlag"
            :defaultVal="{}"
            :schema="schema"
            @selection-change="handleSelectionChangePre"
            :selectable="setSelectable"
          />
        </el-tab-pane>
        <el-tab-pane
          label="应付"
          name="payable"
        >
          <eplus-form-table
            v-if="activeName === 'payable'"
            ref="PayableTableRef"
            selectionFlag
            :list="payableTableData"
            :defaultVal="{}"
            :schema="schema"
            @selection-change="handleSelectionChangePayable"
            :selectable="setSelectable"
          />
        </el-tab-pane>
      </el-tabs>

      <!-- </div> -->
    </div>
    <br />
    <br />
    <div style="font-size: 14px; color: #409eff"
      >如果所选择的付款计划之前的付款有剩余金额，会合并到本次一起申请付款，请知晓！！</div
    >
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="openPaymentApplyDialog"
        >
          确认申请
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </template>
  </Dialog>
  <eplus-dialog
    ref="paymentApplyDialogRef"
    :destroyOnClose="true"
  >
    <template #create>
      <PaymentApplyForm
        :step="activeName"
        mode="create"
        :id="idList"
        @success="handleCancel"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { JSX } from 'vue/jsx-runtime'
import PaymentApplyForm from '@/views/scm/vender/paymentApply/PaymentApplyForm.vue'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'

const activeName = ref('prepay')
const message = useMessage()
const dialogTableVisible = ref(false)
const loading = ref(false)
const PayableTableRef = ref()
const PreTableRef = ref()
const paymentApplyDialogRef = ref()
const preSelectedList = ref([])
const payableSelectedList = ref([])
let searchForm = reactive({})
const preTableData = ref([])
const payableTableData = ref([])
const isPrepaidDisabled = computed(() => {
  // 如果预付款(step=1)已经全部支付(exeStatus=1)，则禁用预付标签页
  return preTableData.value.length > 0 && preTableData.value.every((item) => item.exeStatus == '1')
})

const identicalFlag = ref(false)
const allSelectFlag = ref(true) // 默认打开后全选
const handleTabsClick = (val) => {
  activeName.value = val.props.name
  nextTick(() => {
    if (activeName.value === 'prepay') {
      PreTableRef.value.setScorllBar()
    } else {
      PayableTableRef.value.setScorllBar()
    }
  })
}
const idList = ref<any[]>([])
const openPaymentApplyDialog = () => {
  if (preSelectedList.value?.length || payableSelectedList.value?.length) {
    let allSelectedList = [...preSelectedList.value, ...payableSelectedList.value]
    idList.value = allSelectedList.map((item: any) => item.id)

    let permissibleFlag = allSelectedList.every(
      (e: any) => e.receivableAmount?.amount > e.appliedAmount?.amount
    )
    if (identicalFlag.value) {
      message.warning('同一采购合同只允许选择付款计划的一个步骤')
      return false
    }
    if (permissibleFlag) {
      dialogTableVisible.value = false
      paymentApplyDialogRef.value?.openCreate('申请付款单')
    } else {
      message.warning('当前计划中已申请金额超过应付金额，不能申请付款')
    }
  } else {
    message.warning('请选择其中至少一个步骤')
    return
  }
}
const handleCancel = () => {
  payableTableData.value = []
  preTableData.value = []
  preSelectedList.value = []
  payableSelectedList.value = []
  activeName.value = 'prepay'
  dialogTableVisible.value = false
}

const handleSelectionChangePre = (val) => {
  if (val?.length) {
    let repeatition = checkRepeat(val, 'pre')
    if (!repeatition) {
      PreTableRef.value.handleToggleRowSelection(val[val.length - 1], false)
    }
  } else {
    preSelectedList.value = []
  }
}
const handleSelectionChangePayable = (val) => {
  if (val?.length) {
    let repeatition = checkRepeat(val, 'payable')
    if (!repeatition) {
      PayableTableRef.value.handleToggleRowSelection(val[val.length - 1], false)
    }
    // 判断勾选的步骤是否有多个相同的采购合同  罗总
    identicalFlag.value = false
    for (let i = 0; i < val.length; i++) {
      for (let j = i + 1; j < val.length; j++) {
        if (val[i].contractCode === val[j].contractCode) {
          identicalFlag.value = true
          message.warning(` 同一采购合同只允许选择付款计划的一个步骤`)
          break
        }
      }
    }
  } else {
    payableSelectedList.value = []
  }
}
const checkRepeat = (val, type) => {
  if (val[0]?.exeStatus == 1) {
    message.warning(
      `该付款计划${getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, val[0]?.step)}步骤已支付，不可重复支付`
    )
    return false
  }
  // if (!val[0]?.startDate && val[0]?.dateType) {
  //   message.warning(`请先${getDictLabel(DICT_TYPE.DATE_TYPE, val[0]?.dateType).split('日')[0]}`)
  //   return false
  // }
  //注释掉
  // let allContractCodes = preSelectedList.value
  //   .concat(payableSelectedList.value)
  //   .concat(val)
  //   .map((item) => item.contractCode)
  // let uniqueCode = new Set(allContractCodes)
  // if (allContractCodes.length !== uniqueCode.size) {
  //   message.warning('同一采购合同只能选择一笔付款')
  //   return false
  // } else
  if (type === 'pre') {
    if (payableSelectedList.value?.length) {
      message.warning('只能选择同一步骤下的付款计划')
      return false
    } else {
      preSelectedList.value = val
      return true
    }
  } else if (type === 'payable') {
    if (preSelectedList.value?.length) {
      message.warning('只能选择同一步骤下的付款计划')
      return false
    } else {
      payableSelectedList.value = val
      return true
    }
  } else {
    return false
  }
}
// const handleSelectionAllChange = (selection) => {
//   selection.forEach((item) => {
//     handleSelectionChange(selection, item)
//   })
// }

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编码'
    },

    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    }
  ],
  moreFields: []
}
type EplusFormTableSchema = {
  field?: string
  label: string
  width?: number | string
  minWidth?: number | string
  component?: JSX.Element
  default?: any
  formatter?: any
  slot?: any
}
const schema: EplusFormTableSchema[] = [
  {
    field: 'contractCode',
    label: '采购合同号',
    width: '150px'
  },
  {
    field: 'step',
    label: '步骤',
    width: '150px',
    formatter: (item, row) => {
      return getDictLabel(DICT_TYPE.PAYMENT_PLAN_STEP, row?.step)
    }
  },
  {
    field: 'dateType',
    label: '起始点',
    width: '150px',
    formatter: (item, row) => {
      return getDictLabel(DICT_TYPE.DATE_TYPE, row?.dateType)
    }
  },
  {
    field: 'startDate',
    label: '起始日',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.startDate) {
        return formatDate(row.startDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'days',
    label: '天数',
    width: '150px'
  },
  {
    field: 'expectedReceiptDate',
    label: '预计付款日',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.expectedReceiptDate) {
        return formatDate(row.expectedReceiptDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'paymentRatio',
    label: '付款比例%',
    width: '150px'
  },
  {
    field: 'realPaymentRatio',
    label: '实际付款比例%',
    width: '150px'
  },
  {
    field: 'receivableAmount',
    label: '应付金额',
    width: '150px',
    formatter: (item, row, index) => {
      return <>{row?.receivableAmount?.amount || '-'}</>
    }
  },
  {
    field: 'appliedAmount',
    label: '已申请金额',
    width: '150px',
    formatter: (item, row, index) => {
      return <>{row?.appliedAmount?.amount !== null ? row?.appliedAmount?.amount : '-'}</>
    }
  },
  {
    field: 'receivedAmount',
    label: '已付金额',
    width: '150px',
    formatter: (item, row, index) => {
      return <>{row?.receivedAmount?.amount || '-'}</>
    }
  },
  {
    field: 'exeStatus',
    label: '状态',
    width: '150px',
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.PAYMENT_STATUS, row?.exeStatus)
    }
  }
]
const open = async (idList) => {
  searchForm = { idList, ...searchForm }
  let res = await PurchaseContractApi.getPaymentPlan({ contractIdList: idList?.join(',') })
  //同一笔采购合同存在申请中记录信息不可转付款申请
  let disabledCodes = res
    .filter((col) => {
      return col.exeStatus == '2'
    })
    .map((it) => it.contractCode)
  res.forEach((item) => {
    if (disabledCodes.indexOf(item.contractCode) >= 0) {
      item.selectable = false
    } else {
      item.selectable = true
    }
  })
  payableTableData.value = res?.filter((item) => item.step !== 1)
  preTableData.value = res?.filter((item) => item.step === 1)

  allSelectFlag.value = preTableData.value.some((item) => item.selectable)

  // 检查预付款(step=1)是否已经全部支付，如果已支付则禁用预付标签页
  const hasPrepaidPayment = preTableData.value.some((item) => item.exeStatus == '1') // 1表示已支付
  if (hasPrepaidPayment) {
    // 如果预付款已支付，默认切换到应付标签页
    activeName.value = 'payable'
  }

  dialogTableVisible.value = true
}
const handleClose = () => {
  dialogTableVisible.value = false
}

const setSelectable = (row) => {
  return row.selectable
}

onMounted(() => {})
defineExpose({ open })
</script>
<style lang="scss" scoped></style>
