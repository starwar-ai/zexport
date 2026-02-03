<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'pms:auxiliary-sku:create',
      handler: () => saveForm(false, 0)
    }"
    :submitAction="{
      permi: 'pms:auxiliary-sku-change:submit',
      handler: () => saveForm(false, 1)
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="产品信息"
      :formSchemas="basicSchemas"
    />

    <eplus-form-items
      title=""
      :formSchemas="basic2Schemas"
    >
      <template #annex>
        <UploadList
          ref="UploadListRef"
          :fileList="formData.annex"
          @success="getFileList"
        />
      </template>
      <template #picture>
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.picture"
          @success="getPicList"
          mainFlag
        />
      </template>
    </eplus-form-items>
  </eplus-form>
  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as UserApi from '@/api/system/user'
import { DICT_TYPE } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import * as skuApi from '@/api/pms/auxiliary/index'
import { cloneDeep } from 'lodash-es'
import ChangeTips from '@/components/ChangeTips/index.vue'

const message = useMessage()
const formRef = ref()
const formData: EplusAuditable = reactive({
  measureUnit: '',
  description: '',
  descriptionEng: '',
  annex: [],
  picture: [],
  accFlag: undefined
})
const submitData = ref()
const changeTipsRef = ref()
const simpleUserList = ref([])
provide('formData', formData)
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}

const { close } = inject('dialogEmits') as {
  close: () => void
}
const loading = ref(true)
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'name',
    label: '中文品名',
    span: 8,
    component: <el-input placeholder="请输入中文品名"></el-input>,
    rules: {
      required: true,
      message: '请输入中文品名'
    }
  },
  {
    field: 'nameEng',
    label: '英文品名',
    span: 8,
    component: <el-input placeholder="请输入英文品名"></el-input>,
    rules: {
      required: true,
      message: '请输入英文品名'
    }
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    span: 8,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.MEASURE_UNIT}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择计量单位'
    }
  }
])
const basic2Schemas: EplusFormSchema[] = reactive([
  {
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    field: 'picture',
    label: '图片',
    span: 24,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let item = formData.picture.find((item) => item.mainFlag == 1)
        if (!formData.picture) {
          callback(new Error(`请上传图片`))
        } else if (!item) {
          callback(new Error(`请设置主图`))
        } else {
          callback()
        }
      }
    }
  }
])
const emit = defineEmits(['handleSuccess'])
const { t } = useI18n()

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)
  return { ...params, skuType: 4 }
}
const saveForm = async (isChange: boolean, submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (isChange) {
      submitData.value = { ...paramsResult, submitFlag: submitFlag }
      changeTipsRef.value.open(submitData.value, 'sku')
    } else {
      if (submitFlag) {
        await custSkuApi.submitSkuChange(paramsResult.id)
      } else {
        await skuApi.updateChangeSku({ ...paramsResult, submitFlag: submitFlag })
      }
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

const submitChange = async () => {
  await skuApi
    .auxiliaryChange({ ...submitData.value })
    .then((res) => {
      message.success('提交成功')
      close()
    })
    .catch((err) => {
      message.error('提交失败')
    })

  message.success('提交成功')
  close()
}

const getFileList = (params) => {
  formData.annex = params
}
const getPicList = (params) => {
  formData.picture = params
}
onMounted(async () => {
  try {
    loading.value = true
    formRef.value.resetForm()
    simpleUserList.value = await UserApi.getSimpleUserList()

    let res = await skuApi.getSkuInfo({ id: props.id })
    Object.assign(formData, res)
    if (res.auditStatus === 2) {
      clearDialogActions()
      updateDialogActions(
        <el-button
          onClick={() => {
            saveForm(true, 0)
          }}
          key="changeSku"
          type="primary"
          hasPermi="pms:auxiliary-sku:change"
        >
          保存
        </el-button>,
        <el-button
          onClick={() => {
            saveForm(true, 1)
          }}
          key="changeSubmitSku"
          hasPermi="pms:auxiliary-sku-change:submit"
        >
          提交
        </el-button>
      )
    }
    await nextTick()
    setTimeout(() => {
      loading.value = false
    }, 100)
  } finally {
    // loading的隐藏已移到setTimeout中
  }
})
</script>
<style lang="scss" scoped></style>
