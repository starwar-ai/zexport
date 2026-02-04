<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :localSaveAction="{
      handler: () => localSaveForm()
    }"
    :saveAction="{
      permi: 'pms:auxiliary-sku:create',
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: 'pms:auxiliary-sku:submit',
      handler: () => saveForm(1)
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="产品信息"
      :formSchemas="basicSchemas"
    >
      <!-- <template #price>
        <EplusMoney
          v-model:amount="formData.price.amount"
          v-model:currency="formData.price.currency"
        />
      </template> -->
      <!-- <template #categoryId>
        <el-tree-select
          v-model="formData.categoryId"
          :data="classTree"
          :props="defaultProps"
          node-key="id"
          placeholder="请选择"
        />
      </template> -->
    </eplus-form-items>
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
      <!-- <template #picture>
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.picture"
          @success="getPicList"
          mainFlag
        />
      </template> -->
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
import * as classApi from '@/api/pms/class'
import { handleTree } from '@/utils/tree'

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}

const message = useMessage()
const formRef = ref()
const formData: EplusAuditable = reactive({
  measureUnit: '',
  description: '',
  descriptionEng: '',
  annex: [],
  picture: [],
  accFlag: undefined,
  categoryId: null
})
const simpleUserList = ref([])
provide('formData', formData)
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  changeEdit?: Boolean
}>()
const { close } = inject('dialogEmits') as {
  close: () => void
}

const submitData = ref()
const changeTipsRef = ref()

const classTree = ref<Tree[]>([]) // 树形结构
const getClassTreeData = async () => {
  classTree.value = handleTree(await classApi.getClassTree())
}

const loading = ref(false)
const pagePath = useRoute().path

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
  // {
  //   field: 'categoryId',
  //   label: '产品分类'
  // }
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
    component: <UploadZoomPic mainFlag />,
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

//获取本地数据
const getLocalParams = async () => {
  let params = cloneDeep(formData)
  return params
}

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)
  return { ...params, skuType: 4 }
}

//表单暂存
const localSaveForm = async () => {
  try {
    let paramsResult = await getLocalParams()
    if (!paramsResult) return
    localStorage.setItem(pagePath, JSON.stringify({ pagePath: pagePath, data: paramsResult }))
    message.success('暂存成功')
    close()
  } catch (e) {
    console.log(e)
  }
}

const saveForm = async (submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await skuApi.createSku({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await skuApi.updateSku({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
    var localData = localStorage.getItem(pagePath)
    if (localData && props.mode == 'create') {
      localStorage.removeItem(pagePath)
    }
  }
}

// 变更
const auxChangeSubmit = async (isChange: boolean, submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (isChange && props.changeEdit) {
      if (submitFlag) {
        await skuApi.submitSkuChange(paramsResult.id)
      } else {
        await skuApi.updateChangeSku({ ...paramsResult, submitFlag: submitFlag })
      }
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      submitData.value = { ...paramsResult, submitFlag: submitFlag }
      changeTipsRef.value.open(submitData.value, 'sku')
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
  formRef.value.resetForm()
  simpleUserList.value = await UserApi.getSimpleUserList()
  if (props.mode === 'edit' || props.mode === 'change') {
    let res = await skuApi.getSkuInfo({ id: props.id })
    Object.assign(formData, res)
    if (res.auditStatus === 2) {
      clearDialogActions()
      updateDialogActions(
        <el-button
          onClick={() => {
            auxChangeSubmit(true, 0)
          }}
          key="changeSku"
          type="primary"
          hasPermi="pms:auxiliary-sku:change"
        >
          保存
        </el-button>,
        <el-button
          onClick={() => {
            auxChangeSubmit(true, 1)
          }}
          key="changeSubmitSku"
          hasPermi="pms:auxiliary-sku-change:submit"
        >
          提交
        </el-button>
      )
    }
  }
  getClassTreeData()

  //增加判断是否加载本地数据
  var localData = localStorage.getItem(pagePath)
  if (localData && props.mode == 'create') {
    ElMessageBox.confirm('检测到上次有未提交的数据，是否加载？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        var localJsonData = JSON.parse(localData!!)
        Object.assign(formData, localJsonData.data)
        return
      })
      .catch(() => {
        localStorage.removeItem(pagePath)
      })
  }
})
</script>
<style lang="scss" scoped></style>
