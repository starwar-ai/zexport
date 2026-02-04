<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="formLoading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'crm:clue:create',
      handler: () => submitForm()
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
      <template #exmsEventCategoryId>
        <eplus-input-select
          v-model="formData.exmsEventCategoryId"
          :dataList="eventCategorySelectList"
          label="name"
          value="id"
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
      title="联系人信息"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <PocTable
          :formData="formData"
          ref="pocListRef"
          :required="false"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="地址"
      :formSchemas="addressShippingSchemas"
    >
      <template #addressShipping>
        <ShippingTable
          :formData="formData"
          ref="addressShippingRef"
          :required="false"
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
          mainFlag
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { getSimpleUserList } from '@/api/system/user'
import { useCountryStore } from '@/store/modules/countrylist'
import UploadList from '@/components/UploadList/index.vue'
import PocTable from '../cust/components/PocTable.vue'
import BankTable from '../cust/components/BankTable.vue'
import ShippingTable from '../cust/components/ShippingTable.vue'
import { useUserStore } from '@/store/modules/user'
import * as CustApi from '@/api/crm/cust'
import { DICT_TYPE } from '@/utils/dict'
import { cloneDeep } from 'lodash-es'
import { setTableFirstRowArr } from '@/utils/index'
import { getSimpleExhibitionPage } from '@/api/exms/exhibition'
import EplusYearSelect from '@/components/EplusSelect/src/EplusYearSelect.vue'
import { EplusCountrySelect, EplusTagSelect } from '@/components/EplusSelect'
import { defaultProps, handleTree } from '@/utils/tree'
import { getEventCategory } from '@/api/exms/eventCategory'

defineOptions({
  name: 'ClueCreate'
})
// const message = useMessage() //顶部tip提示框
const countryListData = useCountryStore()
const userInfo = useUserStore()
const formLoading = ref(false)
const UploadRef = ref()
const ImageRef = ref()
// const settlementListRef = ref()
const pocListRef = ref()
const bankaccountListRef = ref()
const addressShippingRef = ref()
const exhibitionNameDisabled = ref(true)
const exmsExhibitionIdRef = ref()
const props = defineProps<{
  id?: number
  mode: EplusFormMode
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
  // settlementList: [],
  creditLimit: {
    amount: '',
    currency: 'USD'
  }
})
const message = useMessage()
provide('formData', formData)
const createFilter = (queryString: string) => {
  return (restaurant) => {
    return restaurant.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0
  }
}
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
const queryCountry = (queryString: string, fn: any) => {
  const results = queryString
    ? countryListData.countryList.filter(createFilter(queryString))
    : countryListData.countryList
  // call callback function to return suggestions
  fn(
    results.map((r: any) => {
      return { ...r, value: r.name }
    })
  )
}

// 展会系列相关
const eventCategoryList = ref([])
let eventCategorySelectList = ref([])
const getEventCategoryList = async () => {
  let res = await getEventCategory({ pageSize: 100, pageNo: 1 })
  eventCategoryList.value = res.list
  eventCategorySelectList.value = eventCategoryList.value
}

const eventCategoryIdChange = (val) => {
  // 清空展会名称
  formData.value.exmsExhibitionId = null
  formData.value.exmsExhibitionName = null
  // 清空展会名称的选项列表
  exmsExhibitionIdRef.value.setData([])
  // 重新设置展会名称的搜索条件
  exmsExhibitionIdRef.value.setSearchData({ exmsEventCategoryId: val[0] })
  const checkedItem = val[1].filter((it) => it.id == val[0])
  if (checkedItem.length) {
    formData.value.exmsEventCategoryName = checkedItem[0].name
  }
}

const sourceTypeChanged = (val, eventCategoryId) => {
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
      formData.value.exmsEventCategoryName = ''
    }
  })

  if (val === 3) {
    eventCategorySelectList.value = eventCategoryList.value.filter(
      (item: any) => item.isDomestic === 1
    )
  } else if (val === 2) {
    eventCategorySelectList.value = eventCategoryList.value.filter(
      (item: any) => item.isDomestic === 0
    )
  }
}

const getName = (item) => {
  formData.value.countryId = item.id
  formData.value.countryName = item.name
  formData.value.regionName = item.regionName
}

const categoryTree = ref<Tree[]>([]) // 树
const getCategoryTreeData = async () => {
  let res = await CustApi.getCustCategoryTree()
  categoryTree.value = handleTree(res)
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
    label: '客户名称',
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
      message: '请输入客户名称'
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
    ),
    rules: [
      {
        required: false,
        message: '请输入公司邮箱'
      },
      {
        pattern:
          /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
        message: '请输入正确的邮箱格式'
      }
    ]
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
      required: false,
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
    label: '国家',
    component: <EplusCountrySelect onChange={(item) => getName(item)} />,
    rules: {
      required: true,
      message: '请选择国家',
      trigger: 'change'
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
      message: '请选择客户来源',
      trigger: 'click'
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
      trigger: 'change'
    }
  },
  {
    field: 'customerTypes',
    label: '客户类型',
    component: (
      <el-tree-select
        data={categoryTree}
        props={defaultProps}
        node-key="id"
        multiple
        placeholder="请选择"
        default-expand-all
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择客户类型'
    }
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
        if (formData.value.managerList.length === 0) {
          callback(new Error('请选择业务员'))
        } else if (!defaultFlag) {
          callback(new Error('请设置业务员默认值'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  }
])
if (!queryId) {
  basicSchemas.splice(0, 1)
}
const remarkSchema = [
  {
    field: 'remark',
    label: '备注',
    span: 16,
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
// const financeSchemas: EplusFormSchema[] = [
//   {
//     field: 'currency',
//     label: '结算币种',
//     component: (
//       <eplus-dict-select
//         dictType={DICT_TYPE.CURRENCY_TYPE}
//         style="width:100%"
//       />
//     ),
//     rules: {
//       required: false,
//       message: '请选择币种'
//     }
//   },
//   {
//     field: 'settlementTermType',
//     label: '价格条款',
//     component: (
//       <eplus-dict-select
//         dictType={DICT_TYPE.PRICE_TYPE}
//         style="width:100%"
//       />
//     )
//   },
//   {
//     field: 'creditFlag',
//     label: '启用信用额度',
//     component: (
//       <eplus-dict-select
//         dictType={DICT_TYPE.CONFIRM_TYPE}
//         style="width:100%"
//         defaultValue={0}
//       />
//     ),
//     rules: {
//       required: false,
//       message: '请选择信用额度'
//     }
//   },
//   {
//     field: 'creditLimit',
//     label: '信用额度'
//   }
// ]
//收款方式信息
// const settlementListSchemas: EplusFormSchema[] = [
//   {
//     field: 'settlementList',
//     label: '',
//     span: 24,
//     editable: true,
//     labelWidth: '0px',
//     rules: {
//       required: false
//     }
//   }
// ]
//联系人信息
const pocListSchemas: EplusFormSchema[] = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: false
    }
  }
]
const addressShippingSchemas: EplusFormSchema[] = [
  {
    field: 'addressShipping',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: false
    }
  }
]
//银行账户信息
const bankAccountSchemas: EplusFormSchema[] = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: false
    }
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
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
const pictureListSchemas: EplusFormSchema[] = [
  {
    field: 'picture',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
//用户列表
const simpleUserList: any = ref([])
const submitForm = async (type?: string) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  formLoading.value = true
  let reqList = {
    submitFlag: type === 'submit' ? 1 : 0,
    ...cloneDeep(formData.value),
    name: formData.value.name.trim()
  }
  //地址只传最子节点的code
  reqList.creditFlag ? '' : (reqList.creditLimit = {})
  //处理联系人和银行信息
  if (pocListRef.value) {
    let pocListResult = await pocListRef.value?.saveForm('temporary')
    if (pocListResult) {
      reqList.pocList = pocListResult
    } else {
      formLoading.value = false
      return false
    }
  }
  if (bankaccountListRef.value) {
    let bankaccountListResult = await bankaccountListRef.value?.saveForm('temporary')
    if (bankaccountListResult) {
      reqList.bankaccountList = bankaccountListResult
    } else {
      formLoading.value = false
      return false
    }
  }
  if (addressShippingRef.value) {
    let addressShippingListResult = await addressShippingRef.value?.saveForm('temporary')
    if (addressShippingListResult) {
      reqList.addressShipping = addressShippingListResult
    } else {
      formLoading.value = false
      return false
    }
  }
  reqList = setTableFirstRowArr(
    reqList,
    ['addressShipping', 'pocList', 'bankaccountList'],
    ['index', 'action', 'defaultFlag', 'addressType', 'isHandleAdd']
  )
  if (queryId) {
    await CustApi.updateCust({ stageType: 1, ...reqList })
    message.success('提交成功')
    close()
  } else if (!queryId) {
    await CustApi.createCust({ stageType: 1, ...reqList })
    message.success('提交成功')
    close()
  }
  formLoading.value = false
}

onMounted(async () => {
  formRef.value.resetForm()
  // venderList.value = await VenderApi.getPayableVender({ venderName: '' })
  simpleUserList.value = await getSimpleUserList()
  await getCategoryTreeData()
  await getEventCategoryList()
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
  if (queryId) {
    let result = await CustApi.getCustomer({ id: props.id })
    if (result.sourceType) {
      sourceTypeChanged(result.sourceType, result.exmsEventCategoryId)
    }
    countryListData.countryList.find((item: any) => {
      if (item?.id === result?.countryId) {
        result.countryName = item.name
        result.regionName = item.regionName
      }
    })
    setTableFirstRowArr(result, ['addressShipping', 'pocList', 'bankaccountList'])
    result.paymentIdList = result.paymentList ? result.paymentList[0].code : ''
    !result.creditLimit && (result.creditLimit = { amount: '', currency: 'USD' })
    formData.value = result
    if (!formData.value.annex) {
      formData.value.annex = []
    }
    if (!formData.value.picture) {
      formData.value.picture = []
    }
    provide(formData, 'formData')
    // await getCustDetail(queryId)
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
  }
  nextTick(() => {
    if (!userIdCheck && !queryId) {
      formData.value.managerList = []
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
</style>
