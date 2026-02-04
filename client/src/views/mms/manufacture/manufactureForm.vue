<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'mms:manufacture:create',
      handler: () => saveForm(0)
    }"
  >
    <eplus-form-items
      title="基础信息"
      :formSchemas="basicSchemas"
    />
    <eplus-form-items
      title="加工产品"
      :formSchemas="skuSchemas"
    >
      <template #children>
        <SkuList
          :formData="formData"
          ref="SkuListRef"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as ManufactureApi from '@/api/mms/manufacture/index'
import { getcompanySimpleList, getCustSimpleList, getStockList } from '@/api/common'
import { cloneDeep } from 'lodash-es'
import SkuList from './components/SkuList.vue'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'manufactureForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)
const formData: EplusAuditable = reactive({
  companyId: '',
  companyName: '',
  stockId: '',
  stockName: '',
  custId: '',
  custName: '',
  custCode: ''
})

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const getName = async (val, type) => {
  if (type == 1) {
    formData.companyName = val[0].name
  } else if (type == 2) {
    formData.stockName = val[0].name
  } else if (type == 3) {
    let item = val[1].find((item) => item.id === val[0])
    formData.custName = item.name
    formData.custCode = item.code
  }
}

const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '加工主体',
    component: (
      <eplus-custom-select
        api={getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择"
        style="width:100%"
        onChange={(...$event) => getName($event, 1)}
      />
    ),
    rules: {
      required: true,
      message: '请选择加工主体'
    }
  },
  {
    field: 'stockId',
    label: '加工仓库',
    component: (
      <eplus-custom-select
        api={getStockList}
        label="name"
        value="id"
        placeholder="请选择"
        style="width:100%"
        onChange={(...$event) => getName($event, 2)}
      />
    ),
    rules: {
      required: true,
      message: '请选择加工主体'
    }
  },
  {
    field: 'custId',
    label: '客户名称',
    component: (
      <eplus-input-search-select
        api={getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="custName"
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={(...$event) => getName($event, 3)}
        defaultObj={{
          custCode: formData.custCode || undefined,
          custName: formData.custName || undefined,
          id: formData.custId || undefined
        }}
      />
    ),
    rules: {
      required: true,
      message: '请选择客户名称'
    }
  }
])

const skuSchemas: EplusFormSchema[] = reactive([
  {
    field: 'children',
    label: '',
    labelWidth: '0',
    span: 24
  }
])

const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const SkuListRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  if (SkuListRef.value) {
    let result = await SkuListRef.value?.checkData()
    if (result) {
      params.children = result
    } else {
      params.children = []
      return false
    }
  } else {
    params.children = []
    return false
  }
  return params
}
const saveForm = async (submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await ManufactureApi.createManufacture(paramsResult)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess')
    } else {
      await ManufactureApi.updateManufacture(paramsResult)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess')
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  formRef.value.resetForm()
  if (props.mode === 'edit') {
    let res = await ManufactureApi.getManufactureInfo({ id: props.id })
    Object.assign(formData, res)
  } else {
  }
})
</script>
