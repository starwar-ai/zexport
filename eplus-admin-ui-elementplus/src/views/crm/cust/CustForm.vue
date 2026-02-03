<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="formLoading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: ['crm:cust:create', 'crm:cust:update'],
      handler: () => saveForm(false, 'save')
    }"
    :submitAction="{
      permi: ['crm:cust:create', 'crm:cust:update'],
      handler: () => submitForm(false, 'submit')
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
      <!-- <template #countryName>
        <el-autocomplete
          v-model="formData.countryName"
          :fetch-suggestions="queryCountry"
          clearable
          class="inline-input"
          style="width: 100%"
          @select="
            (item) => {
              formData.countryId = item.id
              formData.countryName = item.name
              formData.regionName = item.regionName
            }
          "
          placeholder="请选择国家/地区"
        />
      </template> -->
      <template #custLinkCode>
        <eplus-input-search-select
          v-model="formData.custLinkCode"
          :api="getCustSimpleList"
          :params="{ pageSize: 100, pageNo: 1 }"
          keyword="name"
          label="name"
          value="code"
          :multiple="true"
          class="!w-100%"
          placeholder="请选择"
          :defaultObj="formData.custLink"
        />
      </template>

      <template #exmsEventCategoryId>
        <eplus-input-select
          v-model="formData.exmsEventCategoryId"
          :dataList="eventCategorySelectList"
          label="name"
          value="id"
          class="w-full"
          @change-emit="(...$event) => eventCategoryIdChange($event)"
        />
      </template>

      <template #exmsExhibitionId>
        <EplusYearSelect
          ref="exmsExhibitionIdRef"
          v-model="formData.exmsExhibitionId"
          :api="getSimpleExhibitionPage"
          label="name"
          value="id"
          keyword="planStartDate"
          :showDate="false"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title=""
      :formSchemas="remarkSchema"
    />
    <eplus-form-items
      title="财务信息"
      :formSchemas="financeSchemas"
    >
      <template #creditLimit>
        <el-input
          class="creditLimit"
          v-model="formData.creditLimit.amount"
          :validate-event="false"
          :disabled="!formData.creditFlag"
        >
          <template #append>
            <eplus-dict-select
              :disabled="!formData.creditFlag"
              v-model="formData.creditLimit.currency"
              :dictType="DICT_TYPE.CURRENCY_TYPE"
              defaultValue="USD"
            />
          </template>
        </el-input>
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="订单路径信息"
      :formSchemas="custCompanyPathListSchemas"
      v-if="formData.internalFlag === 0"
    >
      <template #custCompanyPathList>
        <CompanyPathTable
          :formData="formData"
          ref="custCompanyPathListRef"
          :required="true"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="收款方式信息"
      :formSchemas="settlementListSchemas"
    >
      <template #settlementList>
        <SettlementTable
          :formData="formData"
          ref="settlementListRef"
          :required="true"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="联系人信息"
      desc="邮箱，座机，微信，QQ至少填写一项"
      descColor="#d00"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <PocTable
          :formData="formData"
          ref="pocListRef"
          :required="true"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="收件地址信息"
      :formSchemas="addressShippingSchemas"
    >
      <template #addressShipping>
        <ShippingTable
          :formData="formData"
          ref="addressShippingRef"
          :required="true"
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
          :required="false"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="收款默认银行帐号"
      :formSchemas="collectionAccountSchemas"
    >
      <template #collectionAccountList>
        <CollectionAccountTable
          :formData="formData"
          ref="collectionAccountListRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
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
    <eplus-form-items
      title="图片信息"
      :formSchemas="pictureListSchemas"
    >
      <template #picture>
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.picture"
          @success="getPicList"
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
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { getSimpleUserList } from '@/api/system/user'
import { useCountryStore } from '@/store/modules/countrylist'
import UploadList from '@/components/UploadList/index.vue'
import SettlementTable from './components/SettlementTable.vue'
import CompanyPathTable from './components/CompanyPathTable.vue'
import PocTable from './components/PocTable.vue'
import BankTable from './components/BankTable.vue'
import CollectionAccountTable from './components/CollectionAccountTable.vue'
import ShippingTable from './components/ShippingTable.vue'
import { useUserStore } from '@/store/modules/user'
import * as CustApi from '@/api/crm/cust'
import { getCustSimpleList } from '@/api/common'
import { DICT_TYPE } from '@/utils/dict'
import { cloneDeep, omit } from 'lodash-es'
import { setTableFirstRowArr } from '@/utils/index'
import { getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import ChangeTips from '@/components/ChangeTips/index.vue'
import { getcompanySimpleList } from '@/api/common/index'
import { getSimpleExhibitionPage } from '@/api/exms/exhibition'
import EplusYearSelect from '@/components/EplusSelect/src/EplusYearSelect.vue'
import { ElMessageBox } from 'element-plus'
import { defaultProps, handleTree } from '@/utils/tree'
import { getEventCategory } from '@/api/exms/eventCategory'
import { EplusCountrySelect } from '@/components/EplusSelect'

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}

defineOptions({
  name: 'CustCreate'
})
// const message = useMessage() //顶部tip提示框
const countryListData = useCountryStore()
const userInfo = useUserStore()
const formLoading = ref(false)
const custCompanyPathListRef = ref()
const settlementListRef = ref()
const pocListRef = ref()
const bankaccountListRef = ref()
const collectionAccountListRef = ref()
const addressShippingRef = ref()
const changeTipsRef = ref()
const submitData = ref()
const isCustChange = ref(false)
const exhibitionNameDisabled = ref(true)
const pagePath = useRoute().path
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  changeEdit?: Boolean
  channel?: string
}>()
const { close } = inject('dialogEmits') as {
  close: () => void
}
const queryId: string = (props.id || '') as string
const formRef = ref() // 表单 Ref
//定义表单初始项
const formData: Recordable = ref({
  pocList: [],
  bankaccountList: [],
  addressShipping: [],
  settlementList: [],
  custCompanyPathList: [],
  currencyList: [],
  creditLimit: {
    amount: '',
    currency: 'USD'
  },
  customerTypes: [],
  internalFlag: 0
})
const message = useMessage()
provide('formData', formData)

//客户名称查重
const findSimilarNames = async (queryString: string, fn: any) => {
  if (queryString) {
    let res = await CustApi.findSimilarNames({ name: queryString })
    fn(
      res.map((item) => {
        return { value: item }
      })
    )
  } else {
    fn([])
  }
}

// const getExhibitioName = async (val) => {
//   let item = val.find((item) => item.id === formData.value.exmsExhibitionId)
//   formData.value.exmsExhibitionName = item.name
// }

const eventCategoryList = ref([])
let eventCategorySelectList = ref([])
const getEventCategoryList = async () => {
  let res = await getEventCategory({ pageSize: 100, pageNo: 1 })
  eventCategoryList.value = res.list
  eventCategorySelectList.value = eventCategoryList.value
}
const exmsExhibitionIdRef = ref()
const eventCategoryIdChange = (val) => {
  // 清空展会名称
  formData.value.exmsExhibitionId = null
  formData.value.exmsExhibitionName = null
  // 清空展会名称的选项列表
  exmsExhibitionIdRef.value.setData([])
  // 重新设置展会名称的搜索条件
  exmsExhibitionIdRef.value.setSearchData({ exmsEventCategoryId: val[0] })
}

const sourceTypeChanged = (val, eventCategoryId) => {
  //客户来源为 国外会展 需要选择会展名称
  exhibitionNameDisabled.value = ![2, 3].includes(val)
  nextTick(() => {
    if (eventCategoryId) {
      exmsExhibitionIdRef.value &&
        exmsExhibitionIdRef.value.setSearchData({
          exmsEventCategoryId: eventCategoryId
        })
    } else {
      formData.value.exmsExhibitionName = null
      formData.value.exmsExhibitionId = null
      exmsExhibitionIdRef.value && exmsExhibitionIdRef.value.setData([])
      formData.value.exmsEventCategoryId = ''
    }
  })

  if (val === 3) {
    eventCategorySelectList.value = eventCategoryList.value.filter((item) => item.isDomestic === 1)
  } else if (val === 2) {
    eventCategorySelectList.value = eventCategoryList.value.filter((item) => item.isDomestic === 0)
  }
}

const getName = (item) => {
  formData.value.countryId = item.id
  formData.value.countryName = item.name
  formData.value.regionName = item.regionName
}

const classTree = ref<Tree[]>([]) // 树
const getClassTreeData = async () => {
  let res = await CustApi.getCustCategoryTree()
  classTree.value = handleTree(res)
}
//基本信息
let basicSchemas = reactive([
  {
    field: 'code',
    label: '客户编号',
    component: <el-input disabled />
  },
  {
    field: 'name',
    label: '企业名称',
    component: (
      <el-autocomplete
        fetch-suggestions={findSimilarNames}
        clearable
        class="!w-100%"
        placeholder="请输入企业名称"
      />
    ),
    rules: {
      required: true,
      message: '请输入企业名称'
    }
  },
  {
    field: 'shortname',
    label: '简称',
    component: <el-input placeholder="请输入简称" />
  },
  {
    field: 'phone',
    label: '公司电话',
    component: <el-input placeholder="请输入公司电话" />
  },
  {
    field: 'email',
    label: '公司邮箱',
    component: (
      <el-input
        type="text"
        placeholder="请输入公司邮箱"
      />
    )
    // rules: [
    //   {
    //     required: true,
    //     message: '请输入邮箱地址',
    //     trigger: 'click'
    //   },
    //   {
    //     pattern:
    //       /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    //     message: '请输入正确的邮箱格式'
    //   }
    // ]
  },
  {
    field: 'address',
    label: '公司地址',
    component: (
      <el-input
        type="text"
        placeholder="请输入公司地址"
      />
    ),
    rules: {
      required: true,
      message: '请输入公司地址'
    }
  },
  {
    field: 'homepage',
    label: '官网',
    component: <el-input placeholder="请输入官网" />
  },
  {
    field: 'countryId',
    label: '国家/地区',
    component: (
      <EplusCountrySelect
        onChange={(item) => getName(item)}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择国家'
    }
  },
  {
    field: 'regionName',
    label: '所属地区',
    component: (
      <el-input
        placeholder="请输入所属地区"
        disabled
      />
    )
  },
  // {
  //   field: 'stageType',
  //   label: '客户阶段',
  //   component: (
  //     <eplus-dict-select
  //       dictType={DICT_TYPE.CUSTOMER_STAGE}
  //       style="width:100%"
  //     />
  //   ),
  //   rules: {
  //     required:  true,
  //     message: '请选择客户阶段'
  //   }
  // },
  {
    field: 'sourceType',
    label: '客户来源',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SOURCE_TYPE}
        style="width:100%"
        onChange={(val) => {
          sourceTypeChanged(val, '')
        }}
      />
    ),
    rules: {
      required: true,
      message: '请选择客户来源'
    }
  },
  {
    field: 'exmsEventCategoryId',
    label: '展会系列',
    disabled: exhibitionNameDisabled,
    rules: {
      required: true,
      message: '请选择展会系列'
    }
  },
  {
    field: 'exmsExhibitionId',
    label: '展会名称',
    disabled: exhibitionNameDisabled,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!formData.value.exmsExhibitionId) {
          callback(new Error('请选择展会名称'))
        } else {
          callback()
        }
      },
      trigger: 'click'
    }
  },
  {
    field: 'customerTypes',
    label: '客户类型',
    component: (
      <el-tree-select
        data={classTree}
        props={defaultProps}
        node-key="id"
        multiple
        placeholder="请选择"
        class="!w-100%"
        default-expand-all
      />
    ),
    rules: {
      required: true,
      message: '请选择客户类型'
    }
  },
  {
    field: 'custLinkCode',
    label: '关联客户'
  },
  {
    field: 'managerList',
    label: '业务员',
    span: 24,
    component: <EplusTagSelect isUser />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let defaultFlag = formData.value.managerList?.some((item: any) => item.defaultFlag === 1)
        if (!formData.value.managerList) {
          callback(new Error('请选择业务员'))
        } else if (!defaultFlag) {
          callback(new Error('请设置业务员默认值'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'internalFlag',
    label: '是否内部企业',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.IS_INT}
        style="width:100%"
        clearable={false}
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
    )
  }
])
watch(
  () => formData.value.internalFlag,
  (val) => {
    formData.value.internalCompanyId = null
    basicSchemas[basicSchemas.length - 1].disabled = !val
  }
)
if (!queryId) {
  basicSchemas.splice(0, 1)
}
const remarkSchema = [
  {
    field: 'receivePerson',
    label: '收货人',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入收货人"
      />
    )
  },
  {
    field: 'notifyPerson',
    label: '通知人',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入通知人"
      />
    )
  },
  {
    field: 'mainMark',
    label: '正面唛头',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入正面唛头"
      />
    )
  },
  {
    field: 'sideMark',
    label: '侧面唛头',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入侧面唛头"
      />
    )
  },
  {
    field: 'remark',
    label: '备注',
    span: 12,
    editable: true,
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
//财务信息
const financeSchemas: EplusFormSchema[] = [
  {
    field: 'currencyList',
    label: '结算币种',
    component: <EplusTagSelect dictType={DICT_TYPE.CURRENCY_TYPE} />,
    span: 24,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let defaultFlag = formData.value.currencyList?.some((item: any) => item.defaultFlag === 1)
        if (formData.value.currencyList.length === 0) {
          callback(new Error('请选择结算币种'))
        } else if (!defaultFlag) {
          callback(new Error('请设置结算币种默认值'))
        } else {
          callback()
        }
      },
      trigger: 'click'
    }
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PRICE_TYPE}
        style="width:100%"
      />
    )
  },
  {
    field: 'agentFlag',
    label: '是否联营',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CONFIRM_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择是否联营'
    }
  },
  {
    field: 'creditFlag',
    label: '启用中信保',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CONFIRM_TYPE}
        style="width:100%"
        defaultValue={0}
      />
    ),
    rules: {
      required: true,
      message: '请选择中信保'
    }
  },
  {
    field: 'creditLimit',
    label: '中信保额度'
  }
]

//订单路径信息
const custCompanyPathListSchemas: EplusFormSchema[] = [
  {
    field: 'custCompanyPathList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]

//收款方式信息
const settlementListSchemas: EplusFormSchema[] = [
  {
    field: 'settlementList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
//联系人信息
const pocListSchemas: EplusFormSchema[] = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
const addressShippingSchemas: EplusFormSchema[] = [
  {
    field: 'addressShipping',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
//银行账户信息
const bankAccountSchemas: EplusFormSchema[] = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]

const collectionAccountSchemas: EplusFormSchema[] = [
  {
    field: 'collectionAccountList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
//附件信息
const getFileList = (params) => {
  formData.value.annex = params
}
//图片信息
const getPicList = (params) => {
  formData.value.picture = params
}
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    span: 8,
    editable: true,
    labelWidth: '0px'
  }
]
const pictureListSchemas: EplusFormSchema[] = [
  {
    field: 'picture',
    label: '',
    span: 8,
    editable: true,
    labelWidth: '0px'
  }
]

const bankListFormatter = (list) => {
  if (Array.isArray(list) && list.length > 0) {
    list.map((item, index) => {
      if (
        Object.values(omit(item, ['ver', 'createTime', 'defaultFlag'])).every((item) => item === '')
      ) {
        if (index === 0) {
          list = []
        } else {
          list.splice(index, 1)
        }
      }
    })
  } else {
    return list
  }
  return list
}
//用户列表
const simpleUserList: any = ref([])

//处理表单数据
const handleLocalFormData = async () => {
  formLoading.value = true
  const reqList = cloneDeep(formData.value)
  //地址只传最子节点的code
  reqList.creditFlag
    ? (reqList.creditLimit = { amount: reqList.creditLimit?.amount, currency: 'USD' })
    : (reqList.creditLimit = {})
  //订单路径
  if (custCompanyPathListRef.value) {
    let custCompanyPathListResult = custCompanyPathListRef.value?.tableList
    if (custCompanyPathListResult) {
      // custCompanyPathListResult.forEach((item) => (item.custId = formData.value.id))
      reqList.custCompanyPathList = custCompanyPathListResult
    } else {
      reqList.custCompanyPathList = []
    }
  }
  //处理联系人和银行信息
  if (settlementListRef.value) {
    let settlementListResult = await settlementListRef.value?.tableList
    if (settlementListResult) {
      reqList.settlementList = settlementListResult
    } else {
      reqList.settlementList = []
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
  if (addressShippingRef.value) {
    let addressShippingListResult = await addressShippingRef.value?.tableList
    if (addressShippingListResult) {
      reqList.addressShipping = addressShippingListResult
    } else {
      reqList.addressShipping = []
    }
  }
  if (bankaccountListRef.value) {
    let bankaccountListResult = await bankaccountListRef.value?.tableList
    if (bankaccountListResult) {
      reqList.bankaccountList = bankListFormatter(bankaccountListResult)
    } else {
      reqList.bankaccountList = []
    }
  }

  if (collectionAccountListRef.value) {
    let collectionAccountListResult = await collectionAccountListRef.value?.tableList
    if (collectionAccountListResult) {
      reqList.collectionAccountList = collectionAccountListResult
    } else {
      reqList.collectionAccountList = []
    }
  }
  formLoading.value = false
  return reqList
}

//处理表单数据
const handleFormData = async (isChange, type?) => {
  formLoading.value = true
  const reqList = {
    submitFlag: type === 'submit' ? 1 : 0,
    ...cloneDeep(formData.value),
    name: formData.value.name.trim()
  }
  //地址只传最子节点的code
  reqList.creditFlag
    ? (reqList.creditLimit = { amount: reqList.creditLimit?.amount, currency: 'USD' })
    : (reqList.creditLimit = {})
  //订单路径
  if (custCompanyPathListRef.value) {
    let custCompanyPathListResult = await custCompanyPathListRef.value?.saveForm()
    if (custCompanyPathListResult) {
      custCompanyPathListResult.forEach((item) => (item.custId = formData.value.id))
      reqList.custCompanyPathList = custCompanyPathListResult
    } else {
      formLoading.value = false
      return false
    }
  }
  //处理联系人和银行信息
  if (settlementListRef.value) {
    let settlementListResult = await settlementListRef.value?.saveForm()
    if (settlementListResult) {
      reqList.settlementList = settlementListResult
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
  if (addressShippingRef.value) {
    let addressShippingListResult = await addressShippingRef.value?.saveForm()
    if (addressShippingListResult) {
      reqList.addressShipping = addressShippingListResult
    } else {
      formLoading.value = false
      return false
    }
  }
  if (bankaccountListRef.value) {
    let bankaccountListResult = await bankaccountListRef.value?.saveForm()
    if (bankaccountListResult) {
      reqList.bankaccountList = bankListFormatter(bankaccountListResult)
    } else {
      formLoading.value = false
      return false
    }
  }

  if (collectionAccountListRef.value) {
    let collectionAccountListResult = await collectionAccountListRef.value?.checkData()
    if (collectionAccountListResult) {
      reqList.collectionAccountList = collectionAccountListResult
    } else {
      reqList.collectionAccountList = []
    }
  }

  submitData.value = { stageType: 2, ...reqList }
  isCustChange.value = isChange
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

//表单保存
const saveForm = async (isChange, type) => {
  const valid = await formRef.value.validate()
  if (!valid) return
  try {
    let res = await handleFormData(isChange, type)
    if (!res) return
    if (isChange && !props.changeEdit) {
      toChangeConfirm(res)
    } else if (isChange && props.changeEdit) {
      await CustApi.changeUpdate({ stageType: 2, ...res })
      message.success('保存成功')
      close()
    } else {
      queryId
        ? await CustApi.updateCust({ stageType: 2, ...res })
        : await CustApi.createCust({ stageType: 2, ...res })
      if (props.channel === 'clue') {
        message.notifyPushSuccess(
          '转正式客户成功',
          [{ code: res.code, id: res.id }],
          'cust-formal',
          '操作成功'
        )
      } else {
        message.success('保存成功')
      }
      close()
    }
  } catch (e) {
    console.log(e)
  } finally {
    formLoading.value = false
    var localData = localStorage.getItem(pagePath)
    if (localData && props.mode == 'create' && !isChange) {
      localStorage.removeItem(pagePath)
    }
  }
}
//表单提交审批
const submitForm = async (isChange: boolean, type?: string) => {
  let validRes = await formRef.value?.validate()
  if (!validRes) return
  try {
    let res = await handleFormData(isChange, type)

    if (isChange && !props.changeEdit) {
      toChangeConfirm(res)
    } else if (isChange && props.changeEdit) {
      await CustApi.changeSubmit(res.id)
      message.success('提交成功')
      close()
      await CustApi.changeUpdate({ stageType: 2, ...res })
    } else {
      if (type && validRes && res) {
        queryId
          ? await CustApi.updateCust({ stageType: 2, ...res })
          : await CustApi.createCust({ stageType: 2, ...res })

        if (props.channel === 'clue') {
          message.notifyPushSuccess(
            '转正式客户成功',
            [{ code: res.code, id: res.id }],
            'cust-formal',
            '操作成功'
          )
        } else {
          message.success('提交成功')
        }
        close()
      } else {
        // message.warning('请确认信息填写完整')
      }
    }
  } catch (e) {
    console.log(e)
  } finally {
    formLoading.value = false
    var localData = localStorage.getItem(pagePath)
    if (localData && props.mode == 'create' && !isChange) {
      localStorage.removeItem(pagePath)
    }
  }
}
//变更跳转
const toChangeConfirm = async (reqList) => {
  changeTipsRef.value.open({ stageType: 2, ...reqList }, 'cust')
}
//变更确认提交请求
const submitChange = async () => {
  await CustApi.changeCust({ ...submitData.value })

  message.success('提交成功')
  close()
}

onMounted(async () => {
  await getEventCategoryList()
  // venderList.value = await VenderApi.getPayableVender({ venderName: '' })
  simpleUserList.value = await getSimpleUserList()
  //客户分类
  await getClassTreeData()
  //验证当前用户是否存在于业务员列表中
  const userIdCheck = simpleUserList.value.find((val) => {
    if (val.id === userInfo.user.id) return true
  })
  //采购员部门展示
  simpleUserList.value.map((item, index, arr) => {
    if (item.deptName !== arr[index - 1]?.deptName) {
      index !== 0 && (arr[index - 1].endDept = 1)
      item.firstDept = 1
    }
  })
  if (queryId) {
    let result = await CustApi.getCustomer({ id: props.id })
    if (result.sourceType) {
      sourceTypeChanged(result.sourceType, result.exmsEventCategoryId)
    }
    let pathList = []
    if (result.companyPath?.length) {
      result.companyPath.forEach((item) => {
        if (item.path) {
          let obj = {
            companyPathId: item.id,
            custCode: result.code,
            pathName: getCompanyPathNameFromObj(item.path),
            defaultFlag: item.defaultFlag
          }
          pathList.push(obj)
        }
      })
      result.custCompanyPathList = pathList
    }
    countryListData.countryList.find((item: any) => {
      if (item?.id === result?.countryId) {
        result.countryName = item.name
        result.regionName = item.regionName
      }
    })
    setTableFirstRowArr(result, [
      'addressShipping',
      'pocList',
      'bankaccountList',
      'settlementList',
      'custCompanyPathList'
    ])

    result.paymentIdList = result.paymentList ? result.paymentList[0].code : ''
    !result.creditLimit && (result.creditLimit = { amount: '', currency: 'USD' })
    //从潜在客户转正式过来之后需要判断修改stagetype
    result?.stageType === 1 ? (result.stageType = 2) : ''
    formData.value = result
    formLoading.value = false
    if (!formData.value.annex) {
      formData.value.annex = []
    }
    if (!formData.value.countryName) {
      formData.value.countryName = ''
    }
    if (!formData.value.picture) {
      formData.value.picture = []
    }
    provide(formData, 'formData')
    // await getCustDetail(queryId)
    //

    if (result.auditStatus === 2 || props.changeEdit) {
      clearDialogActions()
      updateDialogActions(
        // <el-button
        //   onClick={() => {
        //     saveForm(true, 'save')
        //   }}
        //   key="changeCust"
        //   type="primary"
        //   hasPermi="crm:cust:change"
        // >
        //   保存
        // </el-button>,
        <el-button
          onClick={() => {
            submitForm(true, 'submit')
          }}
          type="primary"
          key="changeSubmitCust"
          v-hasPermi="crm:cust:change"
        >
          提交
        </el-button>
      )
    }
  } else {
    formData.value.managerList = [
      {
        value: userInfo.user?.id,
        name: userInfo.user?.nickname,
        defaultFlag: 1,
        deptId: userInfo.user?.deptId,
        deptName: userInfo.user?.deptName
      }
    ]
    formData.value.annex = []
    formData.value.picture = []
    if (props.mode === 'create') {
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
  nextTick(() => {
    if (!userIdCheck && !queryId) {
      formData.value.managerList = []
    }
  })

  //增加判断是否加载本地数据
  const localData = localStorage.getItem(pagePath)
  if (localData && props.mode == 'create') {
    ElMessageBox.confirm('检测到上次有未提交的数据，是否加载？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        let localJsonData = JSON.parse(localData!!)
        formData.value = localJsonData.data
        // Object.assign(formData, localJsonData.data)
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

:deep(.highlight) {
  color: #409eff;
}

.el-table__inner-wrapper {
  width: 100%;
}

.buttonContainer {
  width: 100%;
  margin-top: 50px;
}

:deep(.el-input__inner) {
  text-align: left !important;
}

:deep(.creditLimit) {
  .el-select--disabled {
    background-color: #f5f7fa;
  }

  .el-select {
    background-color: #fff;
  }
}
</style>
