<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="formLoading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'scm:vender:create',
      handler: () => saveForm(false, 'save')
    }"
    :submitAction="{
      permi: ['scm:vender:create', 'scm:vender:update'],
      handler: () => submitForm(false, 'submit')
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
      <template #name>
        <el-autocomplete
          v-model="formData.name"
          :fetch-suggestions="remoteMethod"
          :trigger-on-focus="false"
          class="!w-100%"
          clearable
          placeholder="请输入供应商名称"
        />
      </template>

      <template #businessScope>
        <eplus-input-select
          v-model="formData.businessScope"
          :dataList="classData"
          label="name"
          value="id"
          multiple
          class="!w-100%"
        />
      </template>
    </eplus-form-items>
    <!-- 工厂地址信息 -->
    <eplus-form-items
      title=""
      :formSchemas="factoryAreaSchemas"
      class="factory"
    >
      <template #factoryAreaIdList>
        <el-cascader
          v-model="formData.factoryAreaIdList"
          :options="areaTreeList"
          :fit-input-width="true"
          popper-class="cascaderPopper"
          class="w-1/1"
          clearable
          placeholder="请选择工厂所在地区"
        />
      </template>
      <template #deliveryAreaIdList>
        <el-cascader
          v-model="formData.deliveryAreaIdList"
          :options="areaTreeList"
          :fit-input-width="true"
          popper-class="cascaderPopper"
          class="w-1/1"
          clearable
          placeholder="请选择快递地址所在地区"
        />
      </template>
      <template #companyAreaIdList>
        <el-cascader
          v-model="formData.companyAreaIdList"
          :options="areaTreeList"
          :fit-input-width="true"
          popper-class="cascaderPopper"
          class="w-1/1"
          clearable
          placeholder="请选择公司所在地区"
        />
      </template>
    </eplus-form-items>
    <!-- 快递地址信息 -->
    <!-- <eplus-form-items
      title=""
      :formSchemas="deliveryAddressSchemas"
      class="factory"
    >
      <template #deliveryAreaIdList>
        <el-cascader
          v-model="formData.deliveryAreaIdList"
          :options="areaTreeList"
          :fit-input-width="true"
          popper-class="cascaderPopper"
          class="w-1/1"
          clearable
          placeholder="请选择快递地址所在地区"
        />
      </template>
    </eplus-form-items> -->
    <!-- 公司地址信息 -->
    <!-- <eplus-form-items
      title=""
      :formSchemas="companyAreaSchemas"
      class="factory"
    >
      <template #companyAreaIdList>
        <el-cascader
          v-model="formData.companyAreaIdList"
          :options="areaTreeList"
          :fit-input-width="true"
          popper-class="cascaderPopper"
          class="w-1/1"
          clearable
          placeholder="请选择公司所在地区"
        />
      </template>
    </eplus-form-items> -->

    <eplus-form-items
      title=""
      :formSchemas="remarkSchema"
    />
    <eplus-form-items
      title="财务信息"
      :formSchemas="financeSchemas"
    >
      <!-- <template #paymentItem>
        <el-select
          v-model="formData.paymentItem"
          clearable
          style="width: 100%"
          placeholder="请选择付款方式"
          :validate-event="false"
        >
          <el-option
            v-for="dict in paymentListData.paymentList.list"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          />
        </el-select>
      </template> -->
      <!-- <template #taxType>
        <eplus-input-select
          v-model="formData.taxType"
          :dataList="taxTypeList"
          label="label"
          value="key"
          @change-emit="
            (...e) => {
              formData.taxRate = e[1].find((item) => item.key === e[0]).value
            }
          "
          class="!w-100%"
        />
      </template> -->
      <!-- <template #taxRate>
        <el-input-number
          class="mx-4"
          v-model="formData.taxRate"
          :min="0"
          :max="99.99"
          :precision="2"
          :controls="false"
          style="width: 100%; margin: 0; text-align: left"
          placeholder="请输入税率"
        />
      </template> -->

      <template #taxMsg>
        <TaxMsgCom
          v-if="editFileShow"
          :formData="formData"
          ref="TaxMsgComRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="付款方式信息"
      :formSchemas="paymentListSchemas"
    >
      <template #paymentList>
        <PaymentTable
          :formData="formData"
          ref="paymentListRef"
          :required="true"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="联系人信息"
      desc="*邮箱，QQ， 微信至少填写一项"
      desc-color="#f56c6c"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <PocTable
          type="formal"
          :formData="formData"
          ref="pocListRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="银行账户信息"
      :formSchemas="bankAccountSchemas"
    >
      <template #bankaccountList>
        <BankTable
          :formData="formData"
          ref="bankaccountListRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      v-if="editFileShow"
      title="附件信息"
      :formSchemas="annexSchemas"
    >
      <template #annex>
        <UploadList
          v-if="formData.annex"
          ref="UploadListRef"
          :fileList="formData.annex"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />
</template>
<script setup lang="tsx">
import * as VenderApi from '@/api/scm/vender'
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
// import { useCountryStore } from '@/store/modules/countrylist'
import { getSimpleUserList } from '@/api/system/user'
// import { useCountryStore } from '@/store/modules/countrylist'
import UploadList from '@/components/UploadList/index.vue'
import PaymentTable from '../components/PaymentTable.vue'
import TaxMsgCom from '../components/TaxMsgCom.vue'
import PocTable from '../components/PocTable.vue'
import BankTable from '../components/BankTable.vue'
import { useAreaTreeStore } from '@/store/modules/areaTree'
import { useUserStore } from '@/store/modules/user'
import { usePaymentStore } from '@/store/modules/payment'
import { DICT_TYPE } from '@/utils/dict'
import { getQualificatioPage, getVenderSimpleList } from '@/api/common'
import { getcompanySimpleList } from '@/api/common/index'
import { cloneDeep } from 'lodash-es'
import * as classApi from '@/api/pms/class'
import ChangeTips from '@/components/ChangeTips/index.vue'

defineOptions({
  name: 'VenderForm'
})
// const message = useMessage() //顶部tip提示框
// const countryListData = useCountryStore()
const userInfo = useUserStore()
const areaTreeStore = useAreaTreeStore() //省份/城市数据
const paymentListData = usePaymentStore() //付款方式表

const formLoading = ref(false)
const UploadRef = ref()
const changeTipsRef = ref()

const pocListRef = ref()
const paymentListRef = ref()
const bankaccountListRef = ref()
const pagePath = useRoute().path
const props = defineProps<{
  channel?: string
  id?: number
  mode: EplusFormMode
  changeEdit?: Boolean
}>()
const areaTreeList = ref<any[]>([])
const { close } = inject('dialogEmits') as {
  close: () => void
}
const { updateDialogActions, clearDialogActions } =
  props.id || props.mode === 'create'
    ? (inject('dialogActions') as {
        updateDialogActions: (...args: any[]) => void
        clearDialogActions: () => void
      })
    : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const checkNameLoading = ref(false)
const checkNameList = ref<any[]>([])
const editFileShow = ref(false)
const venderList = ref()
const queryId: string = (props.id || '') as string
const formRef = ref() // 表单 Ref
//定义表单初始项
const formData: Recordable = ref({
  stageType: 2,
  pocList: [],
  bankaccountList: [],
  paymentList: [],
  venderType: undefined,
  internalFlag: 0,
  businessScope: []
})

const submitData = ref()

const nameInput = ref()
const message = useMessage()
provide('formData', formData)

//供应商名称查重

const remoteMethod = async (query: string, cb: any) => {
  if (query) {
    let checkRes = await VenderApi.getSearchName({ name: query })
    cb(
      checkRes.map((item) => {
        return { value: item }
      })
    )
  } else {
    cb([])
  }
}
const highlight = (label) => {
  const pattern = nameInput.value.split('').join('.*')
  const reg = new RegExp(pattern, 'gi')
  return label.replace(reg, (match) => `<span class="highlight">${match}</span>`)
}

//获取主营业务数据
let classData = ref([])
const getClassData = async () => {
  let classRes = await classApi.getClassTree()
  classData.value = classRes.filter((item) => item.parentId == 0)
}
//基本信息
const basicSchemas = reactive([
  {
    field: 'name',
    label: '供应商名称',
    rules: {
      required: true,
      message: '请输入供应商名称'
    }
  },
  {
    field: 'nameEng',
    label: '供应商英文名称',
    component: <el-input placeholder="请输入供应商英文名称" />
  },
  {
    field: 'nameShort',
    label: '简称',
    component: <el-input placeholder="请输入简称" />
  },
  {
    field: 'phone',
    label: '供应商电话',
    component: <el-input placeholder="请输入供应商电话" />,
    rules: {
      required: true,
      message: '请输入供应商电话'
    }
  },
  {
    field: 'licenseNo',
    label: '营业执照号',
    component: (
      <el-input
        type="text"
        placeholder="请输入营业执照号"
        maxlength="100"
      />
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value) {
          callback(new Error('请输入营业执照号'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'registeredCapital',
    label: '注册资本',
    component: (
      <el-input
        type="text"
        placeholder="请输入注册资本"
      />
    )
  },
  {
    field: 'legalPerson',
    label: '法定代表人',
    component: <el-input placeholder="请输入法定代表人" />,
    rules: {
      required: true,
      message: '请输入法定代表人'
    }
  },
  // {
  //   field: 'payableVenderCode',
  //   label: '应付供应商',
  //   rules: {
  //     required: false,
  //     message: '请选择应付供应商'
  //   }
  // },
  {
    field: 'venderLinkCode',
    label: '关联供应商',
    component: (
      <eplus-input-search-select
        api={getVenderSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="code"
        multiple={true}
        class="!w-100%"
        placeholder="请选择"
      />
    )
  },
  {
    field: 'qualificationIds',
    label: '工厂认证',
    component: (
      <eplus-input-search-select
        api={getQualificatioPage}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="id"
        multiple={true}
        class="!w-100%"
        placeholder="请选择"
      />
    )
  },
  {
    field: 'businessScope',
    label: '主营业务'
  },
  {
    field: 'venderType',
    label: '供应商类型',
    disabled: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.VENDER_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择供应商类型'
    }
  },
  {
    field: 'administrationVenderType',
    label: '行政供应商类型',
    disabled: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.ADMINISTRATION_VENDER_TYPE}
        style="width:100%"
      />
    )
  },
  {
    field: 'fax',
    label: '传真',
    component: <el-input placeholder="请输入传真" />
  },
  {
    field: 'internalFlag',
    label: '是否内部企业',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.IS_INT}
        style="width:100%"
      />
    )
  },
  {
    field: 'internalCompanyId',
    label: '内部企业名称',
    disabled: true,
    component: (
      <eplus-custom-select
        api={getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择"
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择内部企业名称'
    }
  },
  {
    field: 'buyerList',
    label: '采购员',
    span: 24,
    component: <EplusTagSelect isUser />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let defaultFlag = formData.value.buyerList?.some((item: any) => item.defaultFlag === 1)
        if (formData.value.buyerList.length === 0) {
          callback(new Error('请选择采购员'))
        } else if (!defaultFlag) {
          callback(new Error('请设置采购员默认值'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  }
])

watch(
  () => formData.value.internalFlag,
  (val) => {
    basicSchemas.find((item) => item.field === 'internalCompanyId').disabled = !val
  }
)
//公司地址信息
// const companyAreaSchemas = [
// {
//   field: 'companyAreaIdList',
//   label: '公司地址'
//   // span: 4
// },
// {
//   field: 'companyAddress',
//   label: '',
//   labelWidth: '0',
//   // span: 4,
//   component: (
//     <el-input
//       placeholder="请输入详细地址"
//       validate-event={false}
//     />
//   )
// }
// ]
//工厂地址
const factoryAreaSchemas = [
  {
    field: 'factoryAreaIdList',
    label: '工厂地址',
    // span: 4,
    rules: {
      required: true,
      message: '请选择工厂地址'
    }
  },
  {
    field: 'factoryAddress',
    label: '',
    labelWidth: '0',
    // span: 4,
    component: (
      <el-input
        placeholder="请输入详细地址"
        validate-event={false}
      />
    ),
    rules: {
      required: true,
      message: '请输入详细地址'
    }
  },
  {
    field: 'companyAreaIdList',
    label: '公司地址'
    // span: 4
  },
  {
    field: 'companyAddress',
    label: '',
    labelWidth: '0',
    // span: 4,
    component: (
      <el-input
        placeholder="请输入详细地址"
        validate-event={false}
      />
    )
  },
  {
    field: 'deliveryAreaIdList',
    label: '快递地址'
    // span: 4
  },
  {
    field: 'deliveryAddress',
    label: '',
    labelWidth: '0',
    // span: 4,
    component: (
      <el-input
        placeholder="请输入详细地址"
        validate-event={false}
      />
    )
  }
]
//快递地址
// const deliveryAddressSchemas = [
// {
//   field: 'deliveryAreaIdList',
//   label: '快递地址',
//   span: 4
// },
// {
//   field: 'deliveryAddress',
//   label: '',
//   labelWidth: '0',
//   span: 4,
//   component: (
//     <el-input
//       placeholder="请输入详细地址"
//       validate-event={false}
//     />
//   )
// }
// ]
const remarkSchema = [
  {
    field: 'remark',
    label: '备注',
    span: 16,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入备注"
      />
    ),
    rules: {
      required: false,
      message: '请输入备注'
    }
  }
]

// const extractNumberFromString = (s) => {
//   // 使用正则表达式匹配数字
//   const match = s.match(/\d+/)
//   // 如果找到数字，返回它（转换为整数），否则返回null
//   return match ? parseInt(match[0], 10) : null
// }
// watch(
//   () => formData.value.taxType,
//   (val) => {
//     let label = extractNumberFromString(getDictLabel(DICT_TYPE.TAX_TYPE, val))
//     if (label) {
//       formData.value.taxRate = label
//     } else {
//       formData.value.taxRate = ''
//     }
//   }
// )

// let taxTypeList = ref<any>([])
// let chinaTaxRateList = ref<any>([])
// let internationalTaxRateList = ref<any>([])

// const getInvoiceRateList = async (type) => {
//   const res = await getConfigJson({ configType: 'invoice.rate', type: type })
//   if (res && type === 'abroad') {
//     internationalTaxRateList.value = res
//   } else {
//     chinaTaxRateList.value = res
//   }
// }
// watch(
//   () => formData.value.currency,
//   (val, oldval) => {
//     let taxRateObj: any = financeSchemas.find((item) => item.field === 'taxRate')
//     if (val === 'RMB') {
//       taxTypeList.value = chinaTaxRateList.value
//       if ((props.mode === 'create' && props.channel !== 'clue') || oldval) {
//         formData.value.taxType = chinaTaxRateList.value[0].key
//       }
//       taxRateObj.disabled = false
//     } else {
//       taxTypeList.value = internationalTaxRateList.value
//       if ((props.mode === 'create' && props.channel !== 'clue') || oldval) {
//         formData.value.taxType = internationalTaxRateList.value[0].key
//       }
//       taxRateObj.disabled = true
//     }
//     if (oldval) {
//       formData.value.taxRate = ''
//     }
//   }
// )
//财务信息
const financeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'taxMsg',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
  // {
  //   field: 'currency',
  //   label: '币种',
  //   component: (
  //     <eplus-dict-select
  //       dictType={DICT_TYPE.CURRENCY_TYPE}
  //       style="width:100%"
  //       disabled={props.mode === 'change'}
  //     />
  //   ),
  //   rules: {
  //     required: true,
  //     message: '请选择币种'
  //   }
  // },
  // {
  //   field: 'taxType',
  //   label: '发票类型',
  //   rules: {
  //     required: true,
  //     message: '请选择发票类型'
  //   }
  // },
  // {
  //   field: 'taxRate',
  //   label: '税率%',
  //   disabled: true,
  //   rules: {
  //     required: true,
  //     message: '请输入税率'
  //   }
  // }
  // {
  //   field: 'paymentItem',
  //   label: '付款方式',
  //   rules: {
  //     required: false,
  //     message: '请选择付款方式'
  //   }
  // }
])
const paymentListSchemas = [
  {
    field: 'paymentList',
    label: '',
    span: 24,
    labelWidth: '0px',
    editable: true
  }
]
//联系人信息
const pocListSchemas: EplusFormSchema[] = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//银行账户信息
const bankAccountSchemas: EplusFormSchema[] = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//附件信息
const getFileList = (params) => {
  formData.value.annex = params
}
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    labelWidth: '0px'
  }
]
//用户列表
const simpleUserList: any = ref([])

//处理表单数据
const handleLocalFormData = async () => {
  formLoading.value = true
  const reqList = cloneDeep(formData.value)
  //处理付款方式
  // reqList.paymentId = reqList.paymentItem
  //地址只传最子节点的code
  if (reqList.companyAreaIdList?.length === 3) {
    reqList.companyAreaId = reqList.companyAreaIdList[2]
  }
  if (reqList.factoryAreaIdList?.length === 3) {
    reqList.factoryAreaId = reqList.factoryAreaIdList[2]
  }
  if (reqList.deliveryAreaIdList?.length === 3) {
    reqList.deliveryAreaId = reqList.deliveryAreaIdList[2]
  }
  //处理联系人和银行信息
  if (paymentListRef.value) {
    let paymentListResult = await paymentListRef.value?.tableList
    if (paymentListResult) {
      reqList.paymentList = paymentListResult
    } else {
      reqList.paymentList = []
    }
  }
  if (pocListRef.value) {
    let pocListResult = await pocListRef.value?.tableList
    if (pocListResult) {
      reqList.pocList = pocListResult
    } else {
      reqList.pocList = []
    }
  }
  if (bankaccountListRef.value) {
    let bankaccountListResult = await bankaccountListRef.value?.tableList
    if (bankaccountListResult) {
      reqList.bankaccountList = bankaccountListResult
    } else {
      reqList.bankaccountList = []
    }
  }
  formLoading.value = false
  return reqList
}
const TaxMsgComRef = ref()
//处理表单数据
const handleFormData = async (isChange, type?) => {
  let validRes = await formRef.value?.validate()
  if (!validRes) return false
  formLoading.value = true
  const reqList = {
    submitFlag: type === 'submit' ? 1 : 0,
    ...cloneDeep(formData.value),
    name: formData.value.name.trim()
  }
  //处理付款方式
  // reqList.paymentId = reqList.paymentItem
  //地址只传最子节点的code
  if (reqList.companyAreaIdList?.length === 3) {
    reqList.companyAreaId = reqList.companyAreaIdList[2]
  }
  if (reqList.factoryAreaIdList?.length === 3) {
    reqList.factoryAreaId = reqList.factoryAreaIdList[2]
  }
  if (reqList.deliveryAreaIdList?.length === 3) {
    reqList.deliveryAreaId = reqList.deliveryAreaIdList[2]
  }
  //处理联系人和银行信息
  if (paymentListRef.value) {
    let paymentListResult = await paymentListRef.value?.saveForm()
    if (paymentListResult) {
      reqList.paymentList = paymentListResult
    } else {
      formLoading.value = false
      return false
    }
  }
  if (pocListRef.value) {
    let pocListResult = await pocListRef.value?.saveForm()
    if (pocListResult) {
      reqList.pocList = pocListResult
    } else {
      formLoading.value = false
      return false
    }
  }
  if (bankaccountListRef.value) {
    let bankaccountListResult = await bankaccountListRef.value?.saveForm()
    if (bankaccountListResult) {
      reqList.bankaccountList = bankaccountListResult
    } else {
      formLoading.value = false
      return false
    }
  }

  if (TaxMsgComRef.value) {
    let taxMsgComResult = await TaxMsgComRef.value?.saveForm()
    if (taxMsgComResult) {
      reqList.taxMsg = taxMsgComResult
    } else {
      formLoading.value = false
      return false
    }
  }
  return reqList
}

//表单暂存
const localSaveForm = async () => {
  try {
    let res = await handleLocalFormData()
    localStorage.setItem(pagePath, JSON.stringify({ pagePath: pagePath, data: res }))
    message.success('暂存成功')
    close()
  } catch (e) {
    console.log(e)
  }
}

//表单暂存
const saveForm = async (isChange, type?) => {
  try {
    let res = await handleFormData(isChange, type)
    if (!res) return
    queryId
      ? props.channel === 'clue'
        ? await VenderApi.toFormalVender(res)
        : await VenderApi.updateVender(res)
      : await VenderApi.createVender(res)
    if (props.channel === 'clue' && res.venderType == 1) {
      message.notifyPushSuccess(
        '转业务供应商成功',
        [{ code: res.code, id: res.id }],
        'vender-business',
        '操作成功'
      )
    } else if (props.channel === 'clue' && res.venderType == 2) {
      message.notifyPushSuccess(
        '转行政供应商成功',
        [{ code: res.code, id: res.id }],
        'vender-administration',
        '操作成功'
      )
    } else {
      message.success('保存成功')
    }
    close()
  } catch (e) {
    console.log(e, 'e')
  } finally {
    formLoading.value = false
  }
}
const submitForm = async (isChange: boolean, type?: string) => {
  try {
    let res = await handleFormData(isChange, type)

    let validRes = await formRef.value?.validate()
    if (type && validRes) {
      queryId
        ? props.channel === 'clue'
          ? await VenderApi.toFormalVender(res)
          : await VenderApi.updateVender(res)
        : await VenderApi.createVender(res)
      if (props.channel === 'clue' && res.venderType == 1) {
        message.notifyPushSuccess(
          '转业务供应商成功',
          [{ code: res.code, id: res.id }],
          'vender-business',
          '操作成功'
        )
      } else if (props.channel === 'clue' && res.venderType == 2) {
        message.notifyPushSuccess(
          '转行政供应商成功',
          [{ code: res.code, id: res.id }],
          'vender-administration',
          '操作成功'
        )
      } else {
        message.success('提交成功')
      }
      close()
    } else {
      message.warning('请确认信息填写完整')
    }
  } catch (e) {
    console.log(e)
  } finally {
    formLoading.value = false
    let localData = localStorage.getItem(pagePath)
    if (localData && props.mode == 'create' && !isChange) {
      localStorage.removeItem(pagePath)
    }
  }
}

// 变更
const changeSubmit = async (isChange: boolean, type?: string) => {
  try {
    let res = await handleFormData(isChange, type)

    let validRes = await formRef.value?.validate()
    if (validRes && queryId) {
      if (isChange && props.changeEdit) {
        let reqPath = res.submitFlag
          ? VenderApi.changeVenderSubmit(res.id)
          : VenderApi.changeVenderUpdate(res)
        await reqPath
          .then((res) => {
            message.success('提交成功')
            close()
          })
          .catch((err) => {
            message.error('提交失败')
          })
      } else {
        submitData.value = { ...res }
        changeTipsRef.value.open(res, 'vender')
      }
    } else {
      message.warning('请确认信息填写完整')
    }
  } catch (e) {
    console.log(e)
  } finally {
    formLoading.value = false
  }
}

const submitChange = async () => {
  if (queryId) {
    await VenderApi.changeVender(submitData.value)
      .then((res) => {
        message.success('提交成功')
        close()
      })
      .catch((err) => {
        message.error('提交失败')
      })
  }
  message.success('提交成功')
  close()
}

const treeListFormat = (params: any[]) => {
  params.map((item, index, arr) => {
    item.value = item.id
    item.label = item.name
    if (item?.children) {
      treeListFormat(item?.children)
    }
  })
  return params
}
const setVenderType = () => {
  const index = basicSchemas.findIndex((item) => item.field === 'venderType')

  if (pagePath === '/base/vender/business') {
    formData.value.venderType = 1
    basicSchemas[index].disabled = true
  } else if (pagePath === '/base/vender/administration') {
    formData.value.venderType = 2
    basicSchemas[index].disabled = true
  } else {
    basicSchemas[index].disabled = false
  }
}

watch(
  () => formData.value.venderType,
  async (val) => {
    const index = basicSchemas.findIndex((item) => item.field === 'administrationVenderType')
    basicSchemas[index].disabled = !(val === 2)
  },
  { immediate: true }
)

onMounted(async () => {
  // await getInvoiceRateList('abroad')
  // await getInvoiceRateList('domestic')
  await getClassData()
  await setVenderType()
  simpleUserList.value = await getSimpleUserList()
  //验证当前用户是否存在于业务员列表中
  const userIdCheck = simpleUserList.value.find((val) => {
    if (val.id === userInfo.user.id) return true
  })
  //采购员部门展示
  simpleUserList.value.map((item, index, arr) => {
    if (item.deptName !== arr[index - 1]?.deptName) {
      index !== 0 && (arr[index - 1].endDept = 1)
      item.firstDept = 1
    } else {
    }
  })
  areaTreeList.value = treeListFormat(cloneDeep(areaTreeStore.areaTree))
  // areaTreeList.value = handleTree(areaTreeStore.areaTree, 'id')
  if (queryId) {
    let result = await VenderApi.getVender({ id: props.id })

    if (result.auditStatus === 2 || props.changeEdit) {
      clearDialogActions()
      updateDialogActions(
        // <el-button
        //   onClick={() => {
        //     changeSubmit(true)
        //   }}
        //   key="changeCust"
        //   type="primary"
        //   hasPermi="crm:cust:change"
        // >
        //   保存
        // </el-button>,
        <el-button
          type="primary"
          onClick={() => {
            changeSubmit(true, 'submit')
          }}
          key="changeSubmitCust"
          v-hasPermi="crm:cust:change"
        >
          提交
        </el-button>
      )
    }

    // result.paymentItem = result.paymentItem?.id
    formData.value = result
    // if (result.businessScope.length) {
    //   formData.value.businessScope = result.businessScope?.split(',').map((item) => {
    //     return parseInt(item)
    //   })
    // }

    //付款方式传参字段调整
    if (!formData.value.paymentList) {
      formData.value.paymentList = [{ defaultFlag: 1 }]
    } else {
      formData.value.paymentList.forEach((value, index, arr) => {
        const { id: paymentId } = value
        const newObj = { paymentId, ...value }
        arr[index] = newObj
      })
    }
    if (!formData.value.annex) {
      formData.value.annex = []
    }
    provide(formData, 'formData')
  } else {
    formData.value.buyerList = [
      {
        value: userInfo.user?.id,
        name: userInfo.user?.nickname,
        defaultFlag: 1,
        deptId: userInfo.user?.deptId,
        deptName: userInfo.user?.deptName
      }
    ]
    formData.value.annex = []
    if (props.channel !== 'clue' && props.mode === 'create') {
      updateDialogActions(
        <el-button
          onClick={() => {
            localSaveForm()
          }}
          key="localSave"
        >
          暂存
        </el-button>
      )
    }
  }
  if (!formData.value.bankaccountList) {
    formData.value.bankaccountList = [{ defaultFlag: 1 }]
  }
  if (!formData.value.pocList) {
    formData.value.pocList = [{ defaultFlag: 1 }]
  }

  nextTick(() => {
    editFileShow.value = true
    if (!userIdCheck && !queryId) {
      formData.value.buyerList = []
    }
  })

  //增加判断是否加载本地数据
  let localData = localStorage.getItem(pagePath)
  if (localData && props.mode == 'create') {
    ElMessageBox.confirm('检测到上次有未提交的数据，是否加载？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        let localJsonData = JSON.parse(localData!!)
        formData.value = localJsonData.data
        return
      })
      .catch(() => {
        localStorage.removeItem(pagePath)
      })
  }
})
onUnmounted(() => {})
</script>
<style lang="scss" scoped>
.el-form-item--default {
  margin-bottom: 0;
}

.el-table__inner-wrapper {
  width: 100%;
}

.buttonContainer {
  width: 100%;
  margin-top: 50px;
}

:deep(.highlight) {
  color: #409eff;
}

:deep(.el-input__inner) {
  text-align: left !important;
}

.factory {
  // padding-left: 10px;

  :deep(.is-guttered) {
    padding-right: 0 !important;
  }

  :deep(.is-guttered):nth-child(2n) {
    padding-left: 0 !important;
  }
}
</style>
