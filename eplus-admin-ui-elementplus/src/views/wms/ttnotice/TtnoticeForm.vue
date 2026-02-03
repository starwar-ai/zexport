<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'wms:notice-out:update',
      handler: saveForm
    }"
  >
    <eplus-form-items
      title="基础信息"
      :formSchemas="basicSchemas"
    />
    <eplus-form-items
      title="产品信息"
      :formSchemas="proSchemas"
    >
      <template #stockNoticeItemRespVOList>
        <ProCom
          ref="ProComRef"
          :formData="formData"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as RenoticeApi from '@/api/wms/renotice'
import { formatTime } from '@/utils/index'
import ProCom from './components/ProCom.vue'
import { cloneDeep } from 'lodash-es'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'LoanAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)

const formData: EplusAuditable = reactive({})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    readOnly: true,
    label: '通知单号'
  },
  {
    field: 'companyName',
    readOnly: true,
    label: '归属公司'
  },
  {
    field: 'expectDate',
    label: '出库日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
      />
    )
  },
  // {
  //   field: 'purchaseContractCode',
  //   readOnly: true,
  //   label: '采购合同号'
  // },
  // {
  //   field: 'purchaserUserName',
  //   readOnly: true,
  //   label: '采购员'
  // },
  {
    field: 'purchaserDeptName',
    readOnly: true,
    label: '部门'
  },
  {
    field: 'purchaserMobile',
    readOnly: true,
    label: '联系电话'
  },
  {
    field: 'salesName',
    readOnly: true,
    label: '业务员'
  },
  {
    field: 'printFlag',
    readOnly: true,
    label: '打印状态',
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.PRINT_FLAG, val)
    }
  },
  {
    field: 'warehouseName',
    readOnly: true,
    label: '仓库名称'
  },
  {
    field: 'noticeStatus',
    readOnly: true,
    label: '出库状态',
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.NOTICE_STATUS, val)
    }
  },
  {
    field: 'createUserName',
    readOnly: true,
    label: '创建人'
  },
  {
    field: 'createTime',
    readOnly: true,
    label: '创建时间',
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd HH:mm:ss')
    }
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input type="textarea" />,
    span: 24
  }
])

const proSchemas = reactive([
  {
    field: 'stockNoticeItemRespVOList',
    label: '',
    labelWidth: '0px',
    span: 24
  }
])
const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const ProComRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  if (ProComRef.value) {
    let result = await ProComRef.value?.checkData()
    if (result) {
      params.noticeItems = result
    } else {
      params.noticeItems = []
      return false
    }
  }
  return params
}
const saveForm = async () => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    await RenoticeApi.updateNotice(paramsResult)
    message.success(t('common.updateSuccess'))
    close()
    emit('success')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  formRef.value.resetForm()
  if (props.mode === 'edit') {
    let res = await RenoticeApi.getNotice({ id: props.id })
    Object.assign(formData, res)
  }
})
</script>
