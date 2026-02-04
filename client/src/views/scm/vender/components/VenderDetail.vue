<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="venderDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'scm:vender:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'scm:vender:update',
      handler: () => goEdit(props.title)
    }"
    :approve="{
      permi: 'scm:vender:audit',
      handler: () => {}
    }"
    :revertAudit="{
      permi: 'scm:vender:anti-audit',
      handler: handleRevertAudit
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="venderDetail"
      :items="BasicInfoSchema"
    >
      <template #buyerList>
        <span
          v-for="item in venderDetail.buyerList"
          :key="item.value"
          class="mb5px mr5px"
        >
          <el-tag v-if="item.defaultFlag == 1">{{ item.name }}（默认） </el-tag>
          <el-tag v-else>{{ item.name }}</el-tag>
        </span>
      </template>
      <template #qualificationlink>
        <el-tag
          class="mb5px mr5px"
          type="primary"
          v-for="item in venderDetail.qualificationlink"
          :key="item.id"
        >
          {{ item.name }}
        </el-tag>
      </template>

      <template #venderlink>
        <el-tag
          class="mb5px mr5px"
          type="primary"
          v-for="item in venderDetail.venderlink"
          :key="item.id"
        >
          {{ item.name }}
        </el-tag>
      </template>

      <template #businessScopeList>
        {{ venderDetail.businessScopeList?.map((item) => item.name).join(',') || '-' }}
      </template>

      <template #factoryAreaName>
        {{ venderDetail.factoryAreaName }} {{ venderDetail.factoryAddress }}
      </template>
      <template #deliveryAreaName>
        {{ venderDetail.deliveryAreaName }} {{ venderDetail.deliveryAddress }}
      </template>
      <template #companyAreaName>
        {{ venderDetail.companyAreaName }} {{ venderDetail.companyAddress }}
      </template>
    </eplus-description>
    <eplus-description
      title="财务信息"
      :data="venderDetail"
      :items="financeSchemas"
    >
      <!-- <template #paymentItem>
        <el-tag type="primary">
          <span>{{ venderDetail?.paymentItem?.name || '-' }}</span>
        </el-tag>
      </template>
      <template #taxType>
        {{ taxRateList.find((item) => item.key === venderDetail?.taxType)?.label || '-' }}
      </template>
      <template #taxRate>
        {{ venderDetail?.taxRate + '%' }}
      </template> -->
      <template #taxMsg>
        <Table
          :columns="taxMsgColumns"
          headerAlign="center"
          align="center"
          :data="venderDetail?.taxMsg"
        />
      </template>
    </eplus-description>
    <eplus-form-items
      title="付款方式信息"
      :formSchemas="paymentListSchemas"
    >
      <template #paymentList>
        <Table
          :columns="paymentColumns"
          headerAlign="center"
          align="center"
          :data="venderDetail?.paymentList"
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
          :data="venderDetail?.pocList"
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
          :data="venderDetail?.bankaccountList"
        />
      </template>
    </eplus-form-items>
    <eplus-description
      title="附件信息"
      :data="venderDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <el-tag
          type="primary"
          v-for="item in venderDetail.annex"
          :key="item.id"
        >
          <span
            style="cursor: pointer; color: #333"
            @click="handleDownload(item)"
            >{{ item.name }}</span
          >
        </el-tag>
      </template>
    </eplus-description>
  </eplus-detail>
  <eplus-dialog
    ref="toFormalDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #create="{ key }">
      <FormalVenderForm
        v-if="venderDetail.venderType === 1"
        channel="clue"
        :id="key"
        mode="create"
        @handle-success="handleRefresh"
      />
      <AdministrationForm
        v-if="venderDetail.venderType === 2"
        channel="clue"
        :id="key"
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/vender'
import * as VenderApi from '@/api/scm/vender'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import FormalVenderForm from '../formal/VenderForm.vue'
import AdministrationForm from '../administration/AdministrationForm.vue'
import { getConfigJson } from '@/api/common'

const message = useMessage()
const venderDetail = ref() // 借款申请单详情

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const toFormalDialogRef = ref()
const props = defineProps<{
  type: string
  title?: string
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'VenderDetail' })

//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()
const paymentColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'name',
    label: '付款方式'
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

const taxMsgColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'currency',
    label: '币种'
  },
  {
    field: 'taxType',
    label: '发票类型',
    slots: {
      default: (scope) => {
        return taxRateList.value.find((item: any) => item.key === scope.row.taxType)?.label || '-'
      }
    }
  },
  {
    field: 'taxRate',
    label: '税率(%)',
    slots: {
      default: (scope) => {
        return scope.row.taxRate || scope.row.taxRate === 0 ? scope.row.taxRate : '-'
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
    field: 'pocTypes',
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
  // {
  //   field: 'address',
  //   label: '地址'
  // },
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
    field: 'remark',
    label: '备注'
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

const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}
const getVenderDetail = async () => {
  loading.value = true
  try {
    venderDetail.value = props.id
      ? await VenderApi.getVender({ id: props.id })
      : await VenderApi.getAuditVender({ id: query?.id })

    // financeSchemas.find((item) => item.field === 'taxRate').disabled =
    //   venderDetail.value.currency !== 'RMB'
  } finally {
    loading.value = false
  }
}
/**
 * 生成明细信息
 * @param r
 */
let BasicInfoSchema = [
  {
    field: 'code',
    label: '供应商编码'
  },
  {
    field: 'name',
    label: '供应商名称'
  },
  {
    field: 'nameEng',
    label: '英文名称'
  },
  {
    field: 'nameShort',
    label: '简称'
  },
  {
    field: 'venderType',
    label: '供应商类型',
    dictType: DICT_TYPE.VENDER_TYPE
  },
  {
    field: 'phone',
    label: '供应商电话'
  },
  {
    field: 'licenseNo',
    label: '营业执照号'
  },
  {
    field: 'registeredCapital',
    label: '注册资本'
  },
  {
    field: 'legalPerson',
    label: '法定代表人'
  },
  // {
  //   field: 'venderlinkCode',
  //   label: '应付供应商',
  //   disabled: props.type === 'clue'
  // },
  {
    field: 'venderlink',
    label: '关联供应商',
    slotName: 'venderlink',
    disabled: props.type === 'clue'
  },
  {
    field: 'qualificationlink',
    label: '工厂认证',
    slotName: 'qualificationlink'
  },
  {
    slotName: 'businessScopeList',
    field: 'businessScopeList',
    label: '主营业务'
  },
  {
    field: 'fax',
    label: '传真'
  },
  {
    field: 'internalFlag',
    label: '是否内部供应商',
    dictType: DICT_TYPE.IS_INT
  },
  {
    field: 'enableFlag',
    label: '启用状态',
    dictType: DICT_TYPE.ENABLE_FLAG
  },
  {
    field: 'buyerList',
    label: '采购员',
    slotName: 'buyerList',
    span: 24
  },
  {
    slotName: 'factoryAreaName',
    field: 'factoryAreaName',
    label: '工厂地址',
    span: 8
  },
  {
    slotName: 'deliveryAreaName',
    field: 'deliveryAreaName',
    label: '快递地址',
    span: 8
  },
  {
    slotName: 'companyAreaName',
    field: 'companyAreaName',
    label: '公司地址',
    span: 8
  }
]
if (props.type === 'clue') {
  BasicInfoSchema.forEach((item, index) => {
    if (item.field === 'enableFlag') {
      BasicInfoSchema.splice(index, 1)
    }
  })
}
const financeSchemas = reactive([
  // {
  //   field: 'currency',
  //   label: '币种',
  //   dictType: DICT_TYPE.CURRENCY_TYPE
  // },
  // {
  //   slotName: 'taxType',
  //   field: 'taxType',
  //   label: '发票类型'
  // },
  // {
  //   field: 'taxRate',
  //   disabled: true,
  //   label: '税率',
  //   slotName: 'taxRate'
  // }
  {
    slotName: 'taxMsg',
    field: 'taxMsg',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])
const paymentListSchemas = [
  {
    field: 'paymentList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
//联系人信息
const pocListSchemas = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]

//银行账户信息
const bankAccountSchemas = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
//附件信息
const annexSchemas = [
  {
    field: 'annex',
    label: '',
    slotName: 'annex',
    span: 1
  }
]

const handleRevertAudit = async () => {
  await VenderApi.revertAudit(venderDetail.value?.id)
}
const handleUpdate = async () => {
  await getVenderDetail()
}
const emit = defineEmits(['success'])
const handleRefresh = () => {
  emit('success')
}
const handleToFormal = (id) => {
  toFormalDialogRef.value?.openCreate(
    venderDetail.value.venderType === 1 ? '业务供应商' : '行政供应商',
    id
  )
}

const taxRateList = ref([])
const getInvoiceRateList = async () => {
  const res = await getConfigJson({ configType: 'invoice.rate' })
  taxRateList.value = res
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('供应商ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
    if (props.type === 'clue') {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleToFormal(props.id)
          }}
          type="primary"
          key="payment"
        >
          转正式
        </el-button>
      )
    }
  }
  await getInvoiceRateList()
  await getVenderDetail()
})
</script>
<style lang="scss" scoped></style>
