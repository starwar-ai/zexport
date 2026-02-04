<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="venderDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'scm:vender:submit',
      handler: async () => {
        await VenderApi.submitVender(venderDetail.id)
        message.success('提交成功')
        close()
      }
    }"
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
    />

    <eplus-description
      title="财务信息"
      :data="venderDetail"
      :items="financeSchemas"
      v-if="props.type === 'formal'"
    >
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
    <el-tabs
      v-model="activeName"
      type="card"
      class="demo-tabs"
    >
      <!-- <el-tab-pane
        v-if="venderDetail.changeStatus === 3"
        label="关联单据"
        name="1"
      >
        <RelateTable
          v-if="venderDetail"
          :beforeList="relateList.beforeList"
          :afterList="relateList.afterList"
          :params="relateList.params"
        />
      </el-tab-pane> -->
    </el-tabs>
  </eplus-detail>
  <eplus-dialog
    ref="toFormalDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #create="{ key }">
      <FormalVenderForm
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
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { RelatedOrdersTypeEnum } from '@/utils/constants'
import { getConfigJson } from '@/api/common'
import { checkPermi } from '@/utils/permission'

const message = useMessage()
const venderDetail = ref() // 借款申请单详情
const activeName = ref('1')

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
  } finally {
    loading.value = false
  }
  if (venderDetail.value.changeStatus !== 2 && checkPermi(['scm:vender:change'])) {
    clearDialogActions()
    updateDialogActions(
      <el-button
        onClick={() => {
          goChange('供应商')
        }}
        key="venderChange"
      >
        变更
      </el-button>
    )
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
    field: 'phone',
    label: '供应商电话'
  },
  {
    field: 'administrationVenderType',
    label: '行政供应商类型',
    dictType: DICT_TYPE.ADMINISTRATION_VENDER_TYPE
  },
  {
    field: 'creatorName',
    label: '创建人'
  },
  {
    field: 'createTime',
    label: '创建时间',
    type: 'date'
  },
  {
    field: 'remark',
    label: '备注',
    xl: 8,
    lg: 12
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
const venderChange = [
  {
    prop: 'code',
    label: '供应商编码',
    minWidth: '120px'
  },
  {
    prop: 'name',
    label: '供应商名称',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    prop: 'nameEng',
    label: '供应商英文名称',
    minWidth: '130px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="nameEng"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    prop: 'nameShort',
    label: '供应商简称',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="nameShort"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
    // formatter: formatDictColumn(DICT_TYPE.LOAN_REPAY_STATUS)
  }
]
//关联单据table
let relateList = {
  beforeList: [],
  afterList: [
    {
      title: RelatedOrdersTypeEnum.SUPPLIER_CHANGE.name,
      relatedType: RelatedOrdersTypeEnum.SUPPLIER_CHANGE.status,
      schemas: venderChange,
      type: 'before',
      name: 'first-child'
    }
  ],
  params: () => {
    return { linkCode: venderDetail.value?.linkCode }
  }
}
const handleUpdate = async () => {
  await getVenderDetail()
}
const handleRevertAudit = async () => {
  await VenderApi.revertAudit(venderDetail.value?.id)
}
const emit = defineEmits(['success'])
const handleRefresh = () => {
  emit('success')
}
const handleToFormal = (id) => {
  toFormalDialogRef.value?.openCreate('正式供应商', id)
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
  }
  await getInvoiceRateList()
  await getVenderDetail()
})
</script>
<style lang="scss" scoped></style>
