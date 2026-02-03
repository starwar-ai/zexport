<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="custDeatail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :edit="{
      permi: 'crm:clue:update',
      handler: () => goEdit('潜在客户')
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="custDeatail"
      :items="BasicInfoSchema"
    >
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
      title="地址"
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
    <eplus-description
      title="附件信息"
      :data="custDeatail"
      :items="annexSchemas"
    >
      <template #annex>
        <el-tag
          type="primary"
          v-for="item in custDeatail.annex"
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
    <eplus-description
      title="图片信息"
      :data="custDeatail"
      :items="pictureListSchemas"
    >
      <template #pictureList>
        <ImageList
          :pictureList="custDeatail"
          :disabled="true"
        />
      </template>
    </eplus-description>
  </eplus-detail>
  <eplus-dialog
    ref="custDialogRef"
    @success="handleRefresh"
  >
    <template #edit="{ key }">
      <CustForm
        mode="edit"
        :id="key"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/loan-app'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import * as CustApi from '@/api/crm/cust'
import ImageList from '../cust/components/ImageList.vue'
import CustForm from '../cust/CustForm.vue'

const message = useMessage()
const custDeatail = ref() // 详情
const custDialogRef = ref()

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'CustDeatail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const emit = defineEmits(['success', 'failure'])
const handleRefresh = async () => {
  emit('success')
}
const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const handleToCust = (id) => {
  custDialogRef.value?.openEdit(id, '转正式客户')
}
const paymentBtnShow = () => {
  updateDialogActions(
    <el-button
      onClick={() => {
        handleToCust(props.id)
      }}
      type="primary"
      key="payment"
    >
      转正式
    </el-button>
  )
}
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
    label: '账户名称'
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
const getDetail = async () => {
  loading.value = true
  try {
    custDeatail.value = await CustApi.getCustomer({ id: props.id })
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
    field: 'nameEng',
    label: '英文名称'
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
  // { field: 'agentFlag', label: '是否联营', dictType: DICT_TYPE.CONFIRM_TYPE },
  // { field: 'stageType', label: '客户阶段', dictType: DICT_TYPE.CUSTOMER_STAGE },
  { field: 'sourceType', label: '客户来源', dictType: DICT_TYPE.SOURCE_TYPE },
  { field: 'exmsEventCategoryName', label: '展会系列' },
  { field: 'exmsExhibitionName', label: '展会名称' },
  { field: 'customerTypesName', label: '客户类型' },
  { field: 'createTime', label: '创建日期', type: 'date' },
  { field: 'managerList', label: '业务员', slotName: 'managerList', span: 24 },
  { field: 'remark', label: '备注', span: 24 }
]

// const financeSchemas = [
//   {
//     field: 'currency',
//     label: '结算币种',
//     dictType: DICT_TYPE.CURRENCY_TYPE
//   },
//   {
//     field: 'settlementTermType',
//     label: '价格条款',
//     dictType: DICT_TYPE.PRICE_TYPE
//   },
//   {
//     field: 'settlementList',
//     label: '收款方式',
//     slotName: 'settlementList'
//   },
//   {
//     field: 'creditFlag',
//     label: '启用信用额度',
//     dictType: DICT_TYPE.CONFIRM_TYPE
//   },
//   {
//     field: 'creditLimit',
//     label: '信用额度',
//     slotName: 'creditLimit'
//   },
//   {
//     field: 'agentFlag',
//     label: '是否联营',
//     dictType: DICT_TYPE.CONFIRM_TYPE
//   }
// ]
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
const handleUpdate = async () => {
  await getDetail()
}

onMounted(async () => {
  if (!props.id) {
    message.error('客户ID不能为空')
    close()
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getDetail().then(() => {
    paymentBtnShow()
  })
})
</script>
<style lang="scss" scoped></style>
