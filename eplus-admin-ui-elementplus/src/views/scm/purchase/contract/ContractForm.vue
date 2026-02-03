<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="采购信息"
      :formSchemas="purchaseSchemas"
    >
      <template #purchaseUserDeptName>
        <el-input
          v-model="formData.purchaseUserDeptName"
          disabled
        />
      </template>
      <!-- <template #payableVenderLink>
        <el-select
          v-model="formData.paymentVenderName"
          clearable
          style="width: 100%"
          placeholder=" "
          :validate-event="false"
          @change="payableVenderChange"
        >
          <el-option
            v-for="item in formData.payableVenderLink"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          />
        </el-select>
      </template> -->
      <template #purchaseType>
        <eplus-dict-select
          v-model="formData.purchaseType"
          style="width: 100%"
          :dictType="DICT_TYPE.PURCHASE_TYPE"
          clearable
        />
      </template>
      <template #equallyType>
        <eplus-dict-select
          v-model="formData.equallyType"
          style="width: 100%"
          :dictType="DICT_TYPE.EQUALLY_TYPE"
          clearable
        />
      </template>
      <template #paymentId>
        <el-select
          v-model="formData.paymentId"
          style="width: 100%"
          clearable
          @change="changePayment"
        >
          <el-option
            v-for="item in formData.paymentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </template>
      <template #deliveryDate>
        <el-date-picker
          v-model="formData.deliveryDate"
          clearable
          valueFormat="x"
          type="date"
          style="width: 100%"
          :disabled-date="
            (time) => {
              if (formData.initDeliveryDate) {
                return time.getTime() > new Date(formData.initDeliveryDate).getTime()
              }
            }
          "
        />
      </template>
      <template #freight>
        <EplusMoney
          v-model:amount="formData.freight.amount"
          v-model:currency="formData.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #otherCost>
        <EplusMoney
          v-model:amount="formData.otherCost.amount"
          v-model:currency="formData.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #taxType>
        <el-select
          v-model="formData.taxType"
          clearable
          style="width: 100%"
          placeholder=" "
          :validate-event="false"
        >
          <el-option
            v-for="dict in taxTypeList"
            :key="dict.key"
            :label="dict.label"
            :value="dict.key"
          />
        </el-select>
      </template>
      <!-- <template #portName>
        <eplus-input-search-select
          v-model="formData.portName"
          :api="PurchaseContractApi.getPortList"
          :params="{ pageSize: 100, pageNo: 1, status: PortStatusStatusEnum.NORMAL.status }"
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="changePort"
        />
      </template> -->

      <template #venderCode>
        <eplus-input-select
          v-model="formData.venderCode"
          :dataList="venderList"
          label="venderName"
          value="venderCode"
          :clearable="false"
          @change-emit="venderChange"
          class="!w-100%"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="附件信息"
      :formSchemas="annexSchemas"
    >
      <template #annex>
        <UploadList
          ref="UploadRef"
          :fileList="formData?.annex"
          @success="getFileList"
        />
      </template>

      <template #designDraftList>
        <UploadList
          :fileList="formData.designDraftList"
          @success="
            (data) => {
              formData.designDraftList = data
            }
          "
        />
      </template>
    </eplus-form-items>
    <el-tabs
      v-model="activeName"
      class="demo-tabs mb20px"
      @tab-change="handleTabChange"
    >
      <el-tab-pane
        label="采购明细"
        name="first"
      >
        <eplus-form-items
          title=""
          :formSchemas="collectionSchemas"
        >
          <template #children>
            <PurchaseProducts
              :formData="formData"
              ref="childrenRef"
              type="contract"
              :changeFlag="props?.type === 'change' ? true : false"
              @update-total-amount="(val) => setPaymentPlan(val)"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
      <el-tab-pane
        label="加减项"
        name="second"
      >
        <eplus-form-items
          title=""
          :formSchemas="addSubItemListSchemas"
        >
          <template #purchaseAddSubTermList>
            <AddSubItemTable
              :formData="formData"
              ref="addSubItemListRef"
              :required="true"
              type="contract"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
      <el-tab-pane
        label="付款计划"
        name="third"
      >
        <eplus-form-items
          title=""
          :formSchemas="paymentPlanListSchemas"
        >
          <template #purchasePaymentPlanList>
            <PaymentPlanTable
              :formData="formData"
              ref="paymentPlanListRef"
              :required="true"
              type="contract"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
    </el-tabs>
  </eplus-form>
  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import PurchaseProducts from '../components/ContractPurchaseProducts.vue'
import AddSubItemTable from '../components/AddSubItemTable.vue'
import PaymentPlanTable from '../components/PaymentPlanTable.vue'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { DICT_TYPE } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { getConfigJson } from '@/api/common'
import * as PaymentApi from '@/api/system/payment'
import { formatterPrice, formatTime, getCurrency } from '@/utils/index'
import { cloneDeep } from 'lodash-es'
import * as PurchaseChangeApi from '@/api/scm/purchaseChange'
import ChangeTips from '@/components/ChangeTips/index.vue'
import { checkPermi } from '@/utils/permission'
import { isValidArray } from '@/utils/is'
import { getDateRate } from '@/api/finance/custClaim'

const message = useMessage()
const activeName = ref('first')
defineOptions({ name: 'ContractForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  type?: string
}>()
const queryId: string = (props.id || '') as string
const loading = ref(true)
const oldPurchaseContractInfoRespVO = ref<any>({})

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const changeTipsRef = ref()

const formData = reactive({
  purchaseUserId: '',
  trackUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  freight: { amount: '', currency: 'RMB' },
  otherCost: { amount: '', currency: 'RMB' },
  portName: '',
  payableVenderLink: [],
  paymentVenderCode: '',
  paymentVenderName: '',
  paymentVenderId: '',
  trackUserName: '',
  custId: '',
  custName: '',
  custCode: '',
  purchaseUserDeptId: '',
  purchaseUserName: '',
  portId: 0,
  currency: '',
  purchasePaymentPlanList: [],
  purchaseAddSubTermList: [],
  paymentId: 0,
  paymentList: [],
  designDraftList: [],
  manager: {}
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const childrenRef = ref()
const paymentPlanListRef = ref()
const addSubItemListRef = ref()
const handleTabChange = (val) => {
  if (val === 'third') {
    paymentPlanListRef.value.setTotalHeader(childrenRef.value.totalHeader)
  }
}

const setPaymentPlan = (val) => {
  paymentPlanListRef.value.setTotalHeader(val)
}

const taxMsgList = ref([])

const venderChange = (val, list, updatePlanFlag = true) => {
  let item = list.find((el) => el.venderCode === val)
  formData.venderId = item.venderId
  formData.venderName = item.venderName
  formData.paymentList = item.paymentList
  taxMsgList.value = item.taxMsg
  let payItem = item.paymentList.find((el) => el.defaultFlag === 1) || item.paymentList[0]

  if (updatePlanFlag) {
    formData.paymentId = payItem?.id
    formData.paymentName = payItem?.name
    formData.purchasePaymentPlanList = payItem?.systemPaymentPlanDTOList || []
  }
  formData.taxType = item.taxType
  nextTick(() => {
    paymentPlanListRef.value.setTotalHeader(childrenRef.value.totalHeader)
  })
  // childrenRef.value.updateTable(item)
}
const changeUser = (val) => {
  if (val) {
    formData.purchaseUserDeptName = val.deptName
    formData.purchaseUserName = val.nickname
    formData.purchaseUserDeptId = val.deptId
  }
}
const payableVenderChange = (val) => {
  formData.payableVenderLink.map((item: any) => {
    if (item.code === val) {
      formData.paymentVenderId = item.id
      formData.paymentVenderName = item.name
      formData.paymentVenderCode = item.code
    }
  })
}
const changeManager = (val) => {
  if (val) {
    const manager = {
      userId: val.id,
      uerCode: val.code,
      ...val
    }
    formData.manager = manager
  }
}
const getCustName = (val) => {
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.custId = item.id
          formData.custName = item.name
          formData.custCode = item.code
        }
      })
    }
  }
}
let taxTypeList = ref<any>([])
const getTaxTypeList = async () => {
  taxTypeList.value = await getConfigJson({ configType: 'invoice.rate' })
}

let venderList = ref()
const getVenderList = async (rest) => {
  let list = await PurchaseContractApi.venderQuoteList(
    rest.children.map((item) => item.skuCode).join(',')
  )
  let codeList = list.map((item) => item.venderCode)
  if (codeList.includes(rest.venderCode)) {
    venderList.value = list
  } else {
    let item = [
      {
        venderCode: rest.venderCode,
        venderId: rest.venderId,
        venderName: rest.venderName,
        paymentList: rest.paymentList,
        taxType: rest.taxType
      }
    ]
    venderList.value = [...item, ...list]
  }
}

const rateList = ref({})
const currencyChange = async () => {
  if (rateList[formData.currency]) {
    formData.taxRate = rateList[formData.currency]
  } else {
    let res = await getDateRate(formatTime(formData.createTime, 'yyyy-MM-dd'))
    if (isValidArray(res)) {
      res.forEach((item) => {
        rateList[item.dailyCurrName] = item.dailyCurrRate
      })
    }
    formData.taxRate = rateList[formData.currency]
  }
  // formData.taxType = taxMsgList.value.find((item) => item.currency === formData.currency)?.taxType
  childrenRef.value.setCurrency(formData.currency)
}

let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '采购合同编号',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    editable: true,
    component: (
      <eplus-user-select
        disabled
        onChange={changeUser}
      ></eplus-user-select>
    )
  },
  {
    field: 'purchaseUserDeptName',
    label: '采购员部门',
    editable: true
  },
  {
    field: 'managerId',
    label: '跟单员',
    editable: true,
    component: <eplus-user-select onChange={changeManager}></eplus-user-select>,
    rules: [
      {
        required: true,
        message: '请选择跟单员',
        trigger: 'click'
      }
    ]
  },
  {
    field: 'venderCode',
    label: '供应商名称',
    rules: {
      required: true,
      message: '请选择供应商名称'
    }
  },
  {
    field: 'currency',
    label: '供应商币种',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        class="!w-100%"
        clearable={false}
        filter={(list) => {
          if (isValidArray(taxMsgList.value)) {
            let currencyList = taxMsgList.value.map((item: any) => item.currency)
            return list.filter((item) => {
              return currencyList.includes(item.value)
            })
          } else {
            return list
          }
        }}
        onChangeEmit={() => currencyChange()}
      />
    ),
    rules: {
      required: true,
      message: '请选择供应商币种'
    }
  },
  {
    field: 'taxRate',
    label: '汇率',
    component: <el-input disabled />
  },
  {
    field: 'paymentId',
    label: '付款方式',
    editable: true,
    rules: {
      required: true,
      message: '请选择付款方式',
      trigger: 'click'
    }
  },
  {
    field: 'taxType',
    label: '发票类型',
    rules: {
      required: true,
      message: '请选择发票类型'
    }
  },
  {
    field: 'deliveryDate',
    label: '交货日期',
    rules: {
      required: true,
      message: '请选择交货日期'
    }
  },
  {
    field: 'saleContractCode',
    label: '销售合同',
    component: <el-input disabled />,
    editable: true
  },
  {
    field: 'custId',
    label: '客户名称',
    editable: true,
    component: (
      <eplus-input-search-select
        v-if={!loading}
        api={PurchasePlanApi.getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="id"
        class="!w-100%"
        placeholder=" "
        onChangeEmit={(...$event) => getCustName($event)}
        defaultObj={{
          code: formData.custCode || undefined,
          name: formData.custName || undefined,
          id: formData.custId || undefined
        }}
        disabled
      />
    ),
    rules: {
      required: false
    }
  },
  {
    field: 'freight',
    label: '运费',
    editable: true
  },
  {
    field: 'otherCost',
    label: '其他费用',
    editable: true
  },
  {
    field: 'equallyType',
    label: '费用分配方式'
  },
  // {
  //   field: 'payableVenderLink',
  //   label: '应付供应商'
  // },
  {
    field: 'boxWithColor',
    label: '箱带颜色',
    component: <eplus-dict-select dictType={DICT_TYPE.BOX_WITH_COLOR} />
  },
  {
    field: 'sampleCount',
    label: '样品套数',
    component: (
      <el-input-number
        controls={false}
        precision={0}
        min={0}
      />
    )
  },
  {
    field: 'remark',
    label: '备注',
    editable: true,
    component: (
      <el-input
        placeholder="请输入备注"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    ),
    span: 24
  }
])
if (props.mode === 'create') {
  purchaseSchemas = purchaseSchemas.filter((item) => {
    return item.field !== 'code'
  })
}
const UploadRef = ref()
const getFileList = (params) => {
  formData.annex = params
}
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '附件',
    span: 24,
    editable: true
  },
  {
    field: 'designDraftList',
    label: '出片文件',
    span: 24,
    editable: true
  }
]
const collectionSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24,
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (childrenRef.value.tableList?.length === 0) {
            callback(new Error('请选择产品信息'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
]
//加减项
const addSubItemListSchemas: EplusFormSchema[] = [
  {
    field: 'purchaseAddSubTermList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
const changePayment = async (val) => {
  try {
    formData.paymentName = formData.paymentList.find((el) => el.id === formData.paymentId)?.name
    if (!val) {
      paymentPlanListRef.value.tableList = []
      return
    }
    let res = await PaymentApi.getPaymentItem(val)
    if (res?.systemPaymentPlanList?.length) {
      paymentPlanListRef.value.tableList = res?.systemPaymentPlanList
      nextTick(() => {
        paymentPlanListRef.value.setTotalHeader(childrenRef.value.totalHeader)
      })
    } else {
      message.warning('该付款方式未配置付款计划')
    }
  } catch (err) {
    console.log(err)
  }
}
//付款计划
const paymentPlanListSchemas: EplusFormSchema[] = [
  {
    field: 'purchasePaymentPlanList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
    // rules: [
    //   {
    //     required: true,
    //     // validator: (rule: any, value: any, callback: any) => {
    //     //   if (collectionPlanListRef.value.tableList?.length === 0) {
    //     //     callback(new Error('收款计划不可为空'))
    //     //   } else {
    //     //     callback()
    //     //   }
    //     // },
    //     trigger: 'blur'
    //   }
    // ]
  }
]
const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

//处理提交前的formdata
const getParams = async (type?) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let result = await childrenRef.value.checkData()
  let purchasePaymentPlanList = await paymentPlanListRef.value.checkData()
  let purchaseAddSubTermList = await addSubItemListRef.value.checkData()
  if (result && purchaseAddSubTermList && purchasePaymentPlanList) {
    let params: any = JSON.parse(JSON.stringify(formData))
    params.children = JSON.parse(JSON.stringify(childrenRef.value.tableList))
    params.children.forEach((item, index) => {
      item.amount = { amount: item.withTotalTaxPrice, currency: item?.currency }
      if (!item.skuId) {
        item.skuId = item.id
      }
    })
    params.purchasePaymentPlanList = purchasePaymentPlanList
    params.purchaseAddSubTermList = purchaseAddSubTermList?.map((item) => {
      return {
        ...item,
        amount: { amount: formatterPrice(item.amountFormat), currency: formData?.currency || null }
      }
    })
    if (childrenRef.value.totalHeader.purchaseMoney < Number(formData.payedAmount?.amount)) {
      message.warning(
        `已付款${formData.payedAmount?.amount}${getCurrency(formData.currency)}，变更后总付款金额为${childrenRef.value.totalHeader.purchaseMoney}${getCurrency(formData.currency)}，无法变更`
      )
      return false
    }
    params.totalAmount.amount = childrenRef.value.totalHeader.purchaseMoney
    params.totalAmountRmb.amount = childrenRef.value.totalHeader.purchaseMoney * formData.taxRate
    return { sourceType: 1, submitFlag: type === 'submit' ? 1 : 0, ...params }
  } else {
    return false
  }
}

const submitForm = async (type?) => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    loading.value = true
    let paramsResult = await getParams(type)
    if (!paramsResult) {
      message.error('提交失败')
      return false
    }
    if (props.mode == 'create') {
      await PurchaseContractApi.createPurchaseContract(paramsResult)
      message.success('提交成功')
      close()
    } else {
      if (props?.type === 'change') {
        let oldIdList = oldPurchaseContractInfoRespVO.value?.children.map((item) => {
          return item.id
        })
        paramsResult?.children.forEach((item, index) => {
          if (!oldIdList.includes(item.id)) {
            paramsResult.children[index].receiveStatus = 0
            paramsResult.children[index].id = null
          }
        })
        paramsResult = {
          purchaseContractInfoRespVO: paramsResult,
          old_purchaseContractInfoRespVO: oldPurchaseContractInfoRespVO.value
        }

        var res = await PurchaseChangeApi.createChange(paramsResult)
        if (paramsResult.purchaseContractInfoRespVO.confirmFlag === 0) {
          await PurchaseContractApi.changeConfirm(paramsResult.purchaseContractInfoRespVO.id)
        }
        // message.success('提交成功')
        message.notifyPushSuccess('成功生成采购变更单', res, 'purchase-change')
        close()
      } else {
        try {
          await PurchaseContractApi.updatePurchaseContract(paramsResult)
          message.success('提交成功')
          close()
        } catch {
          message.error('提交失败')
        }
      }
    }
  } finally {
    loading.value = false
  }
}

const handleChanhge = async () => {
  let paramsResult = await getParams('change')
  const res = await PurchaseContractApi.getContractChangeEffect(paramsResult)
  changeTipsRef.value.open(res)
}

const submitChange = () => {
  submitForm('change')
}

onMounted(async () => {
  try {
    await getTaxTypeList()

    if (props.mode === 'edit') {
      const rest = await PurchaseContractApi.getPurchaseContract({ id: queryId })
      await getVenderList(rest)
      if (props?.type === 'change') {
        oldPurchaseContractInfoRespVO.value = cloneDeep(rest)
      }

      addSubItemListRef.value.tableList = rest?.purchaseAddSubTermList || []
      childrenRef.value.tableList = rest?.children
      rest.paymentId = rest.paymentId
        ? rest.paymentId
        : rest?.paymentList?.find((item) => item?.defaultFlag === 1)?.id

      rest.freight = rest.freight ? rest.freight : { amount: '', currency: rest?.currency }
      rest.otherCost = rest.otherCost ? rest.otherCost : { amount: '', currency: rest?.currency }

      Object.assign(formData, {
        ...rest,
        managerId: rest.manager ? rest.manager.userId : '',
        payableVenderLink: rest?.venderlink ? rest?.venderlink : []
      })
      //更新供应商信息
      venderChange(rest.venderCode, venderList.value, false)
    }
    if (props?.type === 'change') {
      if (checkPermi(['scm:purchase-contract:change'])) {
        updateDialogActions(
          <el-button
            onClick={() => {
              handleChanhge()
            }}
            type="primary"
            key="save"
          >
            变更
          </el-button>
        )
      }
    } else {
      if (checkPermi(['scm:purchase-contract:create', 'scm:purchase-contract:update'])) {
        updateDialogActions(
          <el-button
            onClick={() => {
              submitForm(props?.type === 'change' ? 'change' : '')
            }}
            type="primary"
            key="save"
          >
            保存
          </el-button>,
          <el-button
            onClick={() => {
              submitForm('submit')
            }}
            key="submit"
          >
            提交
          </el-button>
        )
      }
    }
  } catch (err) {
    console.log(err, 'err')
    // close()
  } finally {
    loading.value = false
  }
})
</script>
