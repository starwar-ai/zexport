<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: ['wms:notice-out:create', 'wms:notice-out:update'],
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: ['wms:notice-out:create', 'wms:notice-out:update'],
      handler: () => saveForm(1)
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
        <CreateProCom
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
import * as RenoticeApi from '@/api/wms/renotice'
import CreateProCom from './components/CreateProCom.vue'
import { cloneDeep } from 'lodash-es'
import { getcompanySimpleList } from '@/api/common'

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
    field: 'companyId',
    label: '归属公司',
    component: (
      <eplus-custom-select
        api={getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择公司"
        onChange={(val) => {
          formData.companyName = val?.name
        }}
      />
    ),
    rules: { required: true, message: '请选择归属公司' }
  },
  {
    field: 'expectDate',
    label: '预计出货日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
      />
    ),
    rules: { required: true, message: '请选择预计到货日期' }
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input type="textarea" />,
    span: 12
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
const emit = defineEmits(['success'])
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
  return {
    ...params,
    noticeType: 2,
    manualFlag: 1
  }
}
const saveForm = async (type) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode === 'edit') {
      await RenoticeApi.updateNotice({ ...paramsResult, submitFlag: type })
    } else {
      await RenoticeApi.createNotice({ ...paramsResult, submitFlag: type })
    }
    message.success('提交成功')
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
    Object.assign(formData, {
      ...res
    })
  }
})
</script>
