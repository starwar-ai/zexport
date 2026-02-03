<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
  >
    <eplus-form-items
      title="基础信息"
      :formSchemas="basicSchemas"
    />
    <eplus-form-items
      title="出库详情"
      :formSchemas="proSchemas"
    >
      <template #billItems>
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
import * as BillApi from '@/api/wms/bill'
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

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const { close } = inject('dialogEmits') as {
  close: () => void
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    readOnly: true,
    label: '出库单号'
  },
  {
    field: 'invoiceCode',
    readOnly: true,
    label: '出运发票号'
  },
  {
    field: 'companyName',
    readOnly: true,
    label: '归属公司'
  },
  {
    field: 'billTime',
    readOnly: true,
    label: '出库日期',
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd')
    }
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
    field: 'billStatus',
    readOnly: true,
    label: '单据状态',
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.STOCK_BILL_STATUS, val)
    }
  },
  {
    field: 'creatorName',
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
    field: 'billItems',
    label: '',
    labelWidth: 0,
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
      params.billItemSaveReqVOList = result
    } else {
      params.billItemSaveReqVOList = []
      return false
    }
  }
  return params
}
const saveForm = async (type) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    await BillApi.updateBill({
      ...paramsResult,
      billStatus: type === 'submit' ? 2 : paramsResult.billStatus
    })
    message.success(t('common.updateSuccess'))
    close()
    // emit('success')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  formRef.value.resetForm()
  if (props.mode === 'edit') {
    let res = await BillApi.getBill({ id: props.id })
    Object.assign(formData, res)
    formData.children?.forEach((e) => {
      e.actQuantity = e.actQuantity ? e.actQuantity : e.orderQuantity
    })
    updateDialogActions(
      <el-button
        onClick={() => {
          saveForm()
        }}
        type="primary"
        key="YsbillFormSave"
        hasPermi="wms:bill-out:update"
      >
        保存
      </el-button>,
      <el-button
        onClick={() => {
          saveForm('submit')
        }}
        key="YsbillFormSubmit"
        hasPermi="wms:bill-out:update"
      >
        提交
      </el-button>
    )
  }
})
</script>
