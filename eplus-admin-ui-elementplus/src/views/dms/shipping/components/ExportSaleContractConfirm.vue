<template>
  <el-tabs
    v-model="activeTabName"
    class="demo-tabs"
    @tab-click="handleTabsClick"
  >
    <el-tab-pane
      label="客户信息"
      name="0"
    >
      <CustChangeDetail :saleId="props.id"
    /></el-tab-pane>

    <el-tab-pane
      label="产品信息"
      name="1"
    />
    <el-tab-pane
      label="供应商信息"
      name="2"
    />
  </el-tabs>
</template>
<script setup lang="tsx">
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import { DICT_TYPE } from '@/utils/dict'
import CustChangeDetail from '@/views/crm/custChange/CustChangeDetail.vue'

import { EplusDetail } from '@/components/EplusTemplate'
// import ImageList from './components/ImageList.vue'

const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()
const props = defineProps<{
  title?: string
  id?: number
}>()
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const activeName = ref('first')
const activeTabName = ref('0')
const exportSaleContractDetail = ref({})

const custDeatail = ref() // 详情

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()

const ImageRef = ref()

const handleTabsClick = (val) => {
  console.log(val.props.name)
  switch (val.props.name) {
    case '0':
      // 设置客户信息
      getInfo()
      break
    case '1':
      // 设置产品信息
      custDeatail.value = {}
      break
    case '2':
      // 设置产品信息
      custDeatail.value = {}
      break
    default:
      break
  }
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
        return <span>{scope.row.name}</span>
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
    label: '联系人'
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
    field: 'email',
    label: '邮箱'
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
    field: 'nameEng',
    label: '英文名称'
  },
  {
    field: 'shortname',
    label: '简称'
  },
  {
    field: 'phone',
    label: '联系电话'
  },
  {
    field: 'email',
    label: '邮箱地址'
  },
  {
    field: 'address',
    label: '营业地址'
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
  { field: 'managerList', label: '业务员', slotName: 'managerList' },
  { field: 'customerTypes', label: '客户类型', dictType: DICT_TYPE.CUSTOM_TYPE },
  { slotName: 'custLink', field: 'custLink', label: '关联客户' },
  { field: 'enableFlag', label: '启用状态', dictType: DICT_TYPE.ENABLE_FLAG },
  { field: 'remark', label: '备注', xl: 8, lg: 12 }
]

const financeSchemas = [
  {
    field: 'currency',
    label: '结算币种',
    dictType: DICT_TYPE.CURRENCY_TYPE
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
    label: '中信保',
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
    field: 'pictureList',
    label: '',
    span: 24,
    slotName: 'pictureList'
  }
]

const loading = ref(true)
const getPurchaseContractDetail = () => {}
const handleUpdate = async () => {
  await getPurchaseContractDetail()
}
if (query?.id) {
  showProcessInstanceTaskListFlag.value = false
  outDialogFlag.value = true
}
if (props.id) {
  showProcessInstanceTaskListFlag.value = true
  outDialogFlag.value = false
}

const getInfo = async () => {
  loading.value = true
  try {
    // let obj = await ExportSaleContractApi.getConfirmSource({ id: props?.id })
    // custDeatail.value = obj.cust
  } finally {
    loading.value = false
  }
}

const handleChangeConfirm = async () => {
  await ExportSaleContractApi.changeConfirm(props.id)
    .then(() => {
      message.success('确认成功')
      close()
      emit('handleSuccess', 'success')
    })
    .catch(() => {
      message.error('确认失败')
    })
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('外销合同ID不能为空')
    if (!props.id) {
      close()
    }
  }
  await getInfo()
  updateDialogActions(
    <el-button
      onClick={async () => {
        handleChangeConfirm()
      }}
      key="changeConfirm"
    >
      确认
    </el-button>
  )
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
  background-color: #fff;
}
</style>
