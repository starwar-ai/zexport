<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="reportDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :edit="{
      permi: 'system:poi-report:update',
      handler: () => goEdit('模板')
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="reportDetail"
      :items="BasicInfoSchema"
    />
    <eplus-description
      title="营业执照信息"
      :data="reportDetail"
      :items="annexSchemas"
    >
      <template #license>
        <el-tag
          v-if="reportDetail.license?.fileUrl"
          type="primary"
          :key="reportDetail.license?.id"
        >
          <span
            style="cursor: pointer; color: #333"
            @click="handleDownload(reportDetail.license)"
            >{{ reportDetail.license?.name }}</span
          >
        </el-tag>
      </template>
    </eplus-description>
    <eplus-description
      title="公章图片信息"
      :data="reportDetail"
      :items="pictureSchemas"
    >
      <template #picture>
        <UploadPicWithDicList
          :pictureList="reportDetail.picture"
          :dictType="DICT_TYPE.OFFICIAL_SEAL_TYPE"
          disabled
        />
      </template>
    </eplus-description>
    <eplus-description
      title="企业信息"
      :data="reportDetail"
      :items="companySchemas"
    >
      <template #availableCurrencyList>
        {{ getDictLabels(DICT_TYPE.CURRENCY_TYPE, reportDetail.availableCurrencyList) }}
      </template>
      <template #manager>
        {{ reportDetail.manager ? reportDetail.manager.nickname : '-' }}
      </template>
    </eplus-description>
    <eplus-description
      title="银行信息"
      :data="reportDetail"
      :items="bankSchemas"
    >
      <template #companyBankList>
        <Table
          :columns="bankTableColumns"
          headerAlign="center"
          align="left"
          :data="reportDetail?.companyBankList"
        />
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/poireport-model'
import * as AcountConfigApi from '@/api/infra/acountConfig'
import { DICT_TYPE, getDictLabels } from '@/utils/dict'

const message = useMessage()
type reportDetailType = {
  companyBankList: any
  license: any
}
let reportDetail: reportDetailType = reactive({ companyBankList: [], license: {} }) // 详情
const showProcessInstanceTaskListFlag = ref(true)
const loading = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()

const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}
const getDetail = async () => {
  loading.value = true
  try {
    let res = await AcountConfigApi.getAcountConfig({ id: props.id })
    reportDetail = res
  } finally {
    loading.value = false
  }
}
const BasicInfoSchema = [
  {
    field: 'name',
    label: '账套名称'
  },
  {
    field: 'companyNature',
    label: '企业性质',
    dictType: DICT_TYPE.COMPANY_NATURE
  }
  // {
  //   field: 'processedFlag',
  //   label: '可加工状态',
  //   dictType: DICT_TYPE.PROCESSED_FLAG
  // }
]
const bankSchemas = [
  {
    field: 'companyBankList',
    label: '',
    slotName: 'companyBankList',
    span: 24
  }
]
//附件信息
const annexSchemas = [
  {
    field: 'license',
    label: '',
    slotName: 'license',
    span: 24
  }
]
//附件信息
const pictureSchemas = [
  {
    field: 'picture',
    label: '',
    slotName: 'picture',
    span: 24
  }
]
const bankTableColumns = [
  {
    label: '序号',
    type: 'index',
    width: 60
  },
  // {
  //   label: '企业中文名称',
  //   field: 'companyName',
  //   minWidth: 200
  // },
  // {
  //   label: '企业英文名称',
  //   field: 'companyNameEng',
  //   width: 200
  // },
  {
    label: '银行中文名称',
    field: 'bankName',
    width: 300
  },
  {
    label: '银行英文名称',
    field: 'bankNameEng',
    width: 300
  },
  {
    label: '银行英文地址',
    field: 'bankAddressEng',
    width: 300
  },
  {
    label: '银行账号',
    field: 'bankCode',
    width: 300
  },
  {
    label: 'SWIFT代码(BIC)',
    field: 'swiftCode',
    width: 200
  }
]

const companySchemas = [
  {
    field: 'companyName',
    label: '企业名称',
    width: 200
  },
  {
    field: 'companyNameEng',
    label: '企业英文名称',
    width: 200
  },
  {
    field: 'licenseNo',
    label: '营业执照号',
    width: 200
  },
  {
    field: 'companyAddress',
    label: '企业地址',
    width: 200
  },
  {
    field: 'companyAddressEng',
    label: '企业英文地址',
    width: 200
  },
  {
    slotName: 'manager',
    field: 'manager',
    label: '管理员',
    width: 200
  },
  {
    field: 'managerMobile',
    label: '管理员手机号',
    width: 200
  },
  {
    field: 'managerMail',
    label: '管理员邮箱',
    width: 200
  },
  {
    field: 'companyTel',
    label: '企业电话',
    width: 200
  },
  {
    field: 'companyFax',
    label: '企业传真',
    width: 200
  },
  {
    slotName: 'availableCurrencyList',
    field: 'availableCurrencyList',
    label: '可用币种',
    width: 200
  },
  {
    field: 'taxNumb',
    label: '税号',
    width: 200
  },
  {
    field: 'customsNumber',
    label: '海关编码',
    width: 200
  }
]

//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const handleUpdate = async () => {
  await getDetail()
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('客户ID不能为空')
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
  await getDetail()
})
</script>
<style lang="scss" scoped></style>
