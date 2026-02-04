<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="custDeatail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'crm:cust:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'crm:cust:update',
      handler: () => goEdit('客户')
    }"
    :approve="{
      permi: 'crm:cust:audit',
      handler: () => {}
    }"
    :revertAudit="{
      permi: 'crm:cust:anti-audit',
      handler: handleRevertAudit
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="custDeatail"
      :items="BasicInfoSchema"
    >
      <template #custLink>
        <el-tag
          class="mb5px mr5px"
          type="primary"
          v-for="item in custDeatail.custLink"
          :key="item?.id"
        >
          {{ item?.name }}
        </el-tag>
      </template>
      <template #managerList>
        <span
          v-for="item in custDeatail.managerList"
          :key="item.value"
          class="mb5px mr5px"
        >
          <el-tag v-if="item.defaultFlag == 1">{{ item.name }}（默认） </el-tag>
          <el-tag v-else>{{ item.name }}</el-tag>
        </span>
      </template>
    </eplus-description>
    <eplus-description
      title="财务信息"
      :data="custDeatail"
      :items="financeSchemas"
    >
      <template #currencyList>
        <span
          v-for="item in custDeatail.currencyList"
          :key="item.value"
          class="mb5px mr5px"
        >
          <el-tag v-if="item.defaultFlag == 1">{{ item.name }}（默认） </el-tag>
          <el-tag v-else>{{ item.name }}</el-tag>
        </span>
      </template>
      <template #creditLimit>
        {{
          custDeatail?.creditLimit?.amount
            ? custDeatail?.creditLimit?.amount + custDeatail?.creditLimit?.currency
            : '-'
        }}
      </template>
    </eplus-description>
    <eplus-form-items
      title="订单路径信息"
      :formSchemas="custCompanyPathListSchemas"
    >
      <template #custCompanyPathList>
        <Table
          :columns="custCompanyPathListColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.custCompanyPathList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="收款方式信息"
      :formSchemas="settlementListSchemas"
    >
      <template #settlementList>
        <Table
          :columns="settlementColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.settlementList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="联系人信息"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <Table
          :columns="pocColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.pocList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="收件地址信息"
      :formSchemas="addressShippingSchemas"
    >
      <template #addressShipping>
        <Table
          :columns="addressShippingColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.addressShipping"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="银行账户信息"
      :formSchemas="bankAccountSchemas"
    >
      <template #bankaccountList>
        <Table
          :columns="bankColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.bankaccountList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="收款默认银行帐号"
      :formSchemas="collectionAccountSchemas"
    >
      <template #collectionAccountList>
        <Table
          v-if="isValidArray(custDeatail?.collectionAccountList)"
          :columns="collectionAccountColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.collectionAccountList"
        />
        <span v-else>无</span>
      </template>
    </eplus-form-items>
    <eplus-description
      title="附件信息"
      :data="custDeatail"
      :items="annexSchemas"
    >
      <template #annex>
        <UploadList
          :fileList="custDeatail?.annex"
          disabled
        />
      </template>
    </eplus-description>
    <eplus-description
      title="图片信息"
      :data="custDeatail"
      :items="pictureListSchemas"
    >
      <template #picture>
        <UploadZoomPic
          v-model="custDeatail.picture"
          disabled
        />
      </template>
    </eplus-description>
    <!-- <el-tabs
      v-model="activeName"
      type="card"
      class="demo-tabs"
    >
      <el-tab-pane
        v-if="custDeatail.changeStatus === 3"
        label="关联单据"
        name="1"
      >
        <RelateTable
          v-if="custDeatail"
          :beforeList="relateList.beforeList"
          :afterList="relateList.afterList"
          :params="relateList.params"
        />
      </el-tab-pane>
    </el-tabs> -->
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/cust'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import * as CustApi from '@/api/crm/cust'
import { getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import { DICT_TYPE } from '@/utils/dict'
import { useCountryStore } from '@/store/modules/countrylist'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { formatDictColumn } from '@/utils/table'
import { RelatedOrdersTypeEnum } from '@/utils/constants'
import { isValidArray } from '@/utils/is'

const activeName = '1'
const message = useMessage()
const custDeatail = ref() // 详情

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const ImageRef = ref()
const countryListData = useCountryStore()
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'CustDeatail' })
//定义edit事件
const { close, goEdit, goChange } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
      goChange: (val) => void
    })
  : { close: () => {}, goEdit: () => {}, goChange: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()
const isInternal = ref(false)
const handleRevertAudit = async () => {
  await CustApi.revertAudit(custDeatail.value?.id)
}
const custCompanyPathListColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'companyPathId',
    label: '订单路径',
    slots: {
      default: (scope) => {
        return <span>{scope.row.pathName}</span>
      }
    }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
      }
    }
  }
])
const settlementColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'settlementId',
    label: '收款方式',
    slots: {
      default: (scope) => {
        return <span>{scope.row.nameEng}</span>
      }
    }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
      }
    }
  }
])
const pocColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'name',
    label: '联系人名称'
  },
  {
    field: 'email',
    label: '邮箱'
  },
  {
    field: 'pocPosts',
    label: '职位'
  },
  {
    field: 'mobile',
    label: '手机号'
  },
  {
    field: 'address',
    label: '地址'
  },
  {
    field: 'telephone',
    label: '座机'
  },
  {
    field: 'wechat',
    label: '微信'
  },
  {
    field: 'qq',
    label: 'QQ'
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
      }
    }
  }
])

const addressShippingColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'name',
    label: '联系人'
  },
  {
    field: 'address',
    label: '地址'
  },
  {
    field: 'postalCode',
    label: '邮编'
  },
  {
    field: 'phone',
    label: '电话'
  },
  {
    field: 'email',
    label: '邮箱'
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
      }
    }
  }
])

const collectionAccountColumns = reactive([
  {
    field: 'companyName',
    label: '公司名称'
  },
  {
    field: 'bankName',
    label: '银行名称'
  },
  {
    field: 'bankCode',
    label: '银行账号'
  }
])

const bankColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'bank',
    label: '开户行'
  },
  {
    field: 'bankAddress',
    label: '开户行地址'
  },
  {
    field: 'bankPoc',
    label: '账户名称'
  },
  {
    field: 'bankAccount',
    label: '账户号码'
  },
  {
    field: 'bankCode',
    label: '银行行号'
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
      }
    }
  }
])

const getDetail = async () => {
  loading.value = true
  try {
    let result = props?.id
      ? await CustApi.getCustomer({ id: props?.id })
      : await CustApi.getAuditCustomer({ id: query?.id })
    let pathList = []
    if (result.companyPath?.length) {
      result.companyPath.forEach((item) => {
        if (item.path) {
          let obj = {
            companyPathId: item?.id,
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
    isInternal.value = result.internalFlag ? true : false
    custDeatail.value = result
  } finally {
    loading.value = false
  }
}
/**
 * 生成明细信息
 * @param r
 */
const BasicInfoSchema = [
  {
    field: 'code',
    label: '客户编号'
  },
  {
    field: 'name',
    label: '客户名称'
  },
  {
    field: 'shortname',
    label: '简称'
  },
  {
    field: 'phone',
    label: '公司电话'
  },
  {
    field: 'email',
    label: '公司邮箱'
  },
  {
    field: 'address',
    label: '公司地址'
  },
  { field: 'homepage', label: '官网地址' },
  {
    field: 'countryName',
    label: '国家/地区'
  },
  {
    field: 'regionName',
    label: '所属地区'
  },
  { field: 'agentFlag', label: '是否联营', dictType: DICT_TYPE.CONFIRM_TYPE },
  { field: 'stageType', label: '客户阶段', dictType: DICT_TYPE.CUSTOMER_STAGE },
  { field: 'sourceType', label: '客户来源', dictType: DICT_TYPE.SOURCE_TYPE },
  { field: 'exmsEventCategoryName', label: '展会系列' },
  { field: 'exmsExhibitionName', label: '展会名称' },

  { field: 'customerTypesName', label: '客户类型' },
  { slotName: 'custLink', field: 'custLink', label: '关联客户' },
  { field: 'enableFlag', label: '启用状态', dictType: DICT_TYPE.ENABLE_FLAG },
  { field: 'internalFlag', label: '是否内部企业', dictType: DICT_TYPE.IS_INT },
  {
    field: 'internalCompanyName',
    label: '内部企业名称',
    disabled: isInternal.value
  },
  { field: 'managerList', label: '业务员', slotName: 'managerList', span: 24 },
  { field: 'receivePerson', label: '收货人', span: 12 },
  { field: 'notifyPerson', label: '通知人', span: 12 },
  { field: 'mainMark', label: '正面唛头', span: 12 },
  { field: 'sideMark', label: '侧面唛头', span: 12 },
  { field: 'remark', label: '备注', span: 24 }
]

const financeSchemas = [
  {
    slotName: 'currencyList',
    field: 'currencyList',
    label: '结算币种'
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    dictType: DICT_TYPE.PRICE_TYPE
  },
  {
    field: 'creditFlag',
    label: '启用中信保',
    dictType: DICT_TYPE.CONFIRM_TYPE
  },
  {
    field: 'creditLimit',
    label: '中信保额度',
    slotName: 'creditLimit'
  },
  {
    field: 'agentFlag',
    label: '是否联营',
    dictType: DICT_TYPE.CONFIRM_TYPE
  }
]
//订单路径
const custCompanyPathListSchemas = [
  {
    field: 'custCompanyPathList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]

//收款方式
const settlementListSchemas = [
  {
    field: 'settlementList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//联系人信息
const pocListSchemas = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//快递地址信息
const addressShippingSchemas = [
  {
    field: 'addressShipping',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//银行账户信息
const bankAccountSchemas = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]

const collectionAccountSchemas = [
  {
    field: 'collectionAccountList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
//附件信息
const annexSchemas = [
  {
    field: 'annex',
    label: '',
    slotName: 'annex',
    span: 24
  }
]
const pictureListSchemas = [
  {
    field: 'picture',
    label: '',
    span: 24,
    slotName: 'picture'
  }
]
const tagsChange = (newDefault, oldDefault) => {
  let tags = ''
  if (newDefault == oldDefault) {
    tags = <el-tag>{newDefault}</el-tag>
  } else {
    tags = (
      <>
        {' '}
        <el-tag type="warning">{newDefault}</el-tag>
        <el-tag
          type="info"
          class="old-tags"
        >
          {oldDefault}
        </el-tag>
      </>
    )
  }
  return tags
}
const tlementTermType = (row) => {
  let newDefault = row?.settlementTermType
  let oldDefault = row?.oldCust?.settlementTermType
  return tagsChange(newDefault, oldDefault)
}
const defaultChange = (scope) => {
  let newDefault = scope.agentFlag ? '是' : '否'
  let oldDefault = scope?.oldCust?.agentFlag ? '是' : '否'
  return tagsChange(newDefault, oldDefault)
}
const custChange = [
  {
    prop: 'code',
    label: '客户编号',
    minWidth: '120px'
  },
  {
    prop: 'name',
    label: '客户名称',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    prop: 'shortname',
    label: '简称',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="shortname"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    prop: 'agentFlag',
    label: '是否联营',
    // formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE),
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return defaultChange(row)
      }
    }
  },
  {
    prop: 'sourceType',
    label: '客户来源',
    minWidth: '120px',
    formatter: formatDictColumn(DICT_TYPE.SOURCE_TYPE)
  },
  {
    prop: 'settlementTermType',
    label: '价格条款',
    // formatter: formatDictColumn(DICT_TYPE.PRICE_TYPE),
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return tlementTermType(row)
      }
    }
  }
]
//关联单据table
let relateList = {
  beforeList: [],
  afterList: [
    {
      title: RelatedOrdersTypeEnum.CUSTOMER_CHANGE.name,
      relatedType: RelatedOrdersTypeEnum.CUSTOMER_CHANGE.status,
      schemas: custChange,
      type: 'before',
      name: 'first-child'
    }
  ],
  params: () => {
    return { linkCode: custDeatail.value?.linkCode }
  }
}
const handleUpdate = async () => {
  await getDetail()
}

onMounted(async () => {
  if (!props?.id && !query?.id) {
    message.error('客户ID不能为空')
    if (!props?.id) {
      close()
    }
  }
  if (query?.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props?.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getDetail()

  if (custDeatail.value.status === 2) {
    updateDialogActions(
      <el-button
        onClick={() => {
          goChange('客户')
        }}
        key="changeCust"
        type="primary"
        hasPermi="crm:cust:change"
      >
        变更
      </el-button>
    )
  }
})
</script>
<style lang="scss" scoped></style>
