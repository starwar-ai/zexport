<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'wms:stocktake:create',
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: 'wms:stocktake:submit',
      textContent: '完成盘点',
      handler: () => submitForm()
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
      <template #proList>
        <AdjustPro
          :formData="formData"
          ref="AdjustProRef"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as StocktakeApi from '@/api/wms/stocktake/index'
import { cloneDeep } from 'lodash-es'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatTime } from '@/utils/index'
// import { isValidArray } from '@/utils/is'
import AdjustPro from './components/AdjustPro.vue'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'AdjustForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)
const formData: EplusAuditable = reactive({
  auditStatus: 0
})

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    readOnly: true,
    label: '盘点单号'
  },
  {
    field: 'warehouseName',
    readOnly: true,
    label: '盘点仓库'
  },
  {
    field: 'planDate',
    readOnly: true,
    label: '预计盘点日期',
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd')
    }
  },
  {
    field: 'stocktakeUserName',
    readOnly: true,
    label: '盘点人'
  },
  {
    field: 'auditStatus',
    readOnly: true,
    label: '审核状态',
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, val)
    }
  },
  {
    field: 'stocktakeStatus',
    readOnly: true,
    label: '盘点状态',
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.STOCK_TAKE, val)
    }
  },
  {
    field: 'remark',
    readOnly: true,
    label: '备注',
    xl: 8,
    lg: 12
  }
])

const proSchemas: EplusFormSchema[] = reactive([
  {
    field: 'proList',
    label: '',
    labelWidth: '0px',
    span: 24
  }
])

const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const AdjustProRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  if (AdjustProRef.value) {
    let result = await AdjustProRef.value?.checkData()
    if (result) {
      params.itemSaveReqVOList = result
    } else {
      params.itemSaveReqVOList = []
      return false
    }
  }
  return params
}
const saveForm = async (submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    await StocktakeApi.stocktakeComplete({ ...paramsResult, submitFlag: submitFlag })
    message.success(t('common.updateSuccess'))
    close()
    emit('handleSuccess', 'success')
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  let paramsResult = await getParams()
  if (!paramsResult) return false
  await StocktakeApi.stocktakeComplete({ ...paramsResult, submitFlag: 0 })
  await StocktakeApi.submitStocktake(formData.id)
  message.success('已提交审核！')
  close()
  emit('handleSuccess', 'success')
}

onMounted(async () => {
  let res = await StocktakeApi.getStocktake({ id: props.id })
  Object.assign(formData, res)
})
</script>
