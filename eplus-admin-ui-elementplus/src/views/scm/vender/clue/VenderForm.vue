<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="formLoading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'scm:vender:create',
      handler: () => submitForm()
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
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
      <template #payableVenderCode>
        <el-select
          v-model="formData.payableVenderCode"
          filterable
          placeholder="请输入应付供应商"
          style="width: 100%"
          @change="payableChange"
          :filter-method="payableInput"
          clearable
        >
          <el-option
            v-for="item in venderList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          />
        </el-select>
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
    </eplus-form-items>
    <!-- 快递地址信息 -->
    <eplus-form-items
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
    </eplus-form-items>
    <!-- 公司地址信息 -->
    <eplus-form-items
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
    </eplus-form-items>
    <eplus-form-items
      title=""
      :formSchemas="remarkSchema"
    />

    <eplus-form-items
      title="财务信息"
      :formSchemas="financeSchemas"
    >
      <template #taxMsg>
        <TaxMsgCom
          v-if="editFileShow"
          :formData="formData"
          ref="TaxMsgComRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="联系人信息"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <PocTable
          type="clue"
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
          :isRequired="false"
          :formData="formData"
          ref="bankaccountListRef"
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
  </eplus-form>
</template>
<script setup lang="tsx">
import * as VenderApi from '@/api/scm/vender'
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
// import { useCountryStore } from '@/store/modules/countrylist'
import { getSimpleUserList } from '@/api/system/user'
// import { useCountryStore } from '@/store/modules/countrylist'
import UploadList from '../components/uploadList/index.vue'
import PocTable from '../components/PocTable.vue'
import BankTable from '../components/BankTable.vue'
import TaxMsgCom from '../components/TaxMsgCom.vue'
import { useAreaTreeStore } from '@/store/modules/areaTree'
import { useUserStore } from '@/store/modules/user'
import { usePaymentStore } from '@/store/modules/payment'
import { DICT_TYPE } from '@/utils/dict'
import * as classApi from '@/api/pms/class'
import { cloneDeep } from 'lodash-es'
import { getConfigJson } from '@/api/common'

defineOptions({
  name: 'CustCreate'
})
// const message = useMessage() //顶部tip提示框
// const countryListData = useCountryStore()
const userInfo = useUserStore()
const areaTreeStore = useAreaTreeStore() //省份/城市数据
const paymentListData = usePaymentStore() //付款方式表

const formLoading = ref(false)
const UploadRef = ref()

const pocListRef = ref()
const bankaccountListRef = ref()
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const areaTreeList = ref<any[]>([])
const { close } = inject('dialogEmits') as {
  close: () => void
}
const editFileShow = ref(false)
const venderList = ref()
const queryId: string = (props.id || '') as string
const formRef = ref() // 表单 Ref
//定义表单初始项
const formData: Recordable = ref({
  stageType: 1,
  pocList: [],
  bankaccountList: [],
  businessScope: []
})
const message = useMessage()
provide('formData', formData)

const payableChange = (val) => {
  // console.log(val, 'change')
}
const payableInput = async (val) => {
  await VenderApi.getPayableVender({ name: val }).then((res) => {
    venderList.value = res?.list
  })
}
//获取主营业务数据
let classData = ref([])
const getClassData = async () => {
  let classRes = await classApi.getClassTree()
  classData.value = classRes.filter((item) => item.parentId == 0)
}
let taxTypeList = ref<any>([])
//基本信息
const basicSchemas = [
  {
    field: 'name',
    label: '供应商名称',
    component: <el-input placeholder="请输入供应商名称" />,
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
    field: 'venderType',
    label: '供应商类型',
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
    field: 'phone',
    label: '供应商电话',
    component: <el-input placeholder="请输入供应商电话" />
  },
  {
    field: 'licenseNo',
    label: '营业执照号',
    component: (
      <el-input
        type="text"
        placeholder="请输入营业执照号"
      />
    )
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
  //   label: '应付供应商'
  // },
  {
    field: 'businessScope',
    label: '主营业务'
  },
  {
    field: 'buyerList',
    label: '采购员',
    span: 24,
    component: <EplusTagSelect isUser />
  }
]
//公司地址信息
const companyAreaSchemas = [
  {
    field: 'companyAreaIdList',
    label: '公司地址',
    span: 5
  },
  {
    field: 'companyAddress',
    label: '',
    labelWidth: '0',
    span: 5,
    component: (
      <el-input
        placeholder="请输入详细地址"
        validate-event={false}
      />
    )
  }
]
//工厂地址
const factoryAreaSchemas = [
  {
    field: 'factoryAreaIdList',
    label: '工厂地址',
    span: 5,
    rules: {
      required: true,
      message: '请选择工厂地址'
    }
  },
  {
    field: 'factoryAddress',
    label: '',
    labelWidth: '0',
    span: 5,
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
  }
]
//快递地址
const deliveryAddressSchemas = [
  {
    field: 'deliveryAreaIdList',
    label: '快递地址',
    span: 5
  },
  {
    field: 'deliveryAddress',
    label: '',
    span: 5,
    labelWidth: '0',
    component: (
      <el-input
        placeholder="请输入详细地址"
        validate-event={false}
      />
    )
  }
]
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
    )
  }
]

//财务信息
const financeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'taxMsg',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])
//联系人信息
const pocListSchemas: EplusFormSchema[] = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
//银行账户信息
const bankAccountSchemas: EplusFormSchema[] = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    labelWidth: '0px'
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
const TaxMsgComRef = ref()

const handleFormData = async (type?: string) => {
  let valid = await formRef.value?.validate()
  if (!valid) return false
  const reqList = {
    submitFlag: type === 'submit' ? 1 : 0,
    ...cloneDeep(formData.value),
    name: formData.value.name.trim()
  }

  //主营业务处理
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
  if (pocListRef.value) {
    let pocListResult = await pocListRef.value?.saveForm('temporary')
    if (pocListResult) {
      reqList.pocList = pocListResult
    } else {
      return false
    }
  }
  if (bankaccountListRef.value) {
    let bankaccountListResult = await bankaccountListRef.value?.saveForm('temporary')
    if (bankaccountListResult) {
      reqList.bankaccountList = bankaccountListResult
    } else {
      return false
    }
  }

  if (TaxMsgComRef.value) {
    let taxMsgComResult = await TaxMsgComRef.value?.saveForm()
    if (taxMsgComResult) {
      reqList.taxMsg = taxMsgComResult
    } else {
      return false
    }
  }

  return reqList
}
const submitForm = async (type?: string) => {
  try {
    let reqList = await handleFormData(type)
    if (!reqList) return false
    formLoading.value = true
    try {
      queryId ? await VenderApi.updateVender(reqList) : await VenderApi.createVender(reqList)
      message.success('提交成功')
      close()
    } catch {
      message.error('提交失败')
    }
  } finally {
    formLoading.value = false
  }
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

let chinaTaxRateList = ref<any>([])
let internationalTaxRateList = ref<any>([])

const getInvoiceRateList = async (type) => {
  const res = await getConfigJson({ configType: 'invoice.rate', type: type })
  if (res && type === 'abroad') {
    internationalTaxRateList.value = res
  } else {
    chinaTaxRateList.value = res
  }
}

onMounted(async () => {
  await getInvoiceRateList('abroad')
  await getInvoiceRateList('domestic')
  await VenderApi.getPayableVender({ pageNo: 1, pageSize: 100, name: '' }).then((res) => {
    venderList.value = res?.list
  })
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

    result.paymentIdList = result.paymentList ? result.paymentList[0].code : ''
    formData.value = result

    if (!formData.value.annex) {
      formData.value.annex = []
    }
    provide(formData, 'formData')
    // await getCustDetail(queryId)
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
  }
  if (!formData.value.bankaccountList) {
    formData.value.bankaccountList = [{ defaultFlag: 1 }]
  }
  getClassData()
  nextTick(() => {
    editFileShow.value = true
    if (formData.value.pocList?.length < 1) {
      // refs.pocList?.addRow()
    }
    if (!userIdCheck && !queryId) {
      formData.value.buyerIds = []
    }
  })
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

:deep(.el-input__inner) {
  text-align: left !important;
}

.factory {
  padding-left: 10px;

  :deep(.is-guttered) {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
}
</style>
