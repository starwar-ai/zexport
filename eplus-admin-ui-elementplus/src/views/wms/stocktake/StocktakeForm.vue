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
        <AddPro
          :formData="formData"
          ref="AddProRef"
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
// import { isValidArray } from '@/utils/is'
import AddPro from './components/AddPro.vue'
import { useUserStore } from '@/store/modules/user'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'StocktakeForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)
const formData: EplusAuditable = reactive({
  auditStatus: 0,
  planDate: Date.now()
})

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const disabledDate = (time: Date) => {
  return time.getTime() <= Date.now() - 1000 * 60 * 60 * 24
}
const getCompanyName = (item) => {
  formData.companyName = item.name
  AddProRef.value?.setTableData()
}
const getWarehouseName = (item) => {
  formData.warehouseName = item.name
  AddProRef.value?.setTableData()
}
const getStocktakeUserName = (item) => {
  formData.stocktakeUserName = item.nickname
}
const basicSchemas: EplusFormSchema[] = reactive([
  // {
  //   field: 'companyId',
  //   label: '归属公司',
  //   editable: true,
  //   component: (
  //     <eplus-custom-select
  //       api={getcompanySimpleList}
  //       label="name"
  //       value="id"
  //       class="!w-100%"
  //       placeholder="请选择"
  //       onChange={getCompanyName}
  //     />
  //   ),
  //   rules: [
  //     {
  //       required: true,
  //       message: '请选择归属公司'
  //     }
  //   ]
  // },
  {
    field: 'warehouseId',
    label: '盘点仓库',
    editable: true,
    component: (
      <eplus-custom-select
        api={StocktakeApi.warehouseList}
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        onChange={getWarehouseName}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择盘点仓库'
      }
    ]
  },
  {
    field: 'planDate',
    label: '预计盘点日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
        disabled-date={disabledDate}
      />
    ),
    rules: {
      required: true,
      message: '请选择预计盘点日期'
    }
  },
  {
    field: 'stocktakeUserId',
    label: '盘点人',
    component: (
      <eplus-user-select
        multiple={false}
        class="!w-100%"
        onChange={getStocktakeUserName}
      ></eplus-user-select>
    ),
    rules: {
      required: true,
      message: '请选择盘点人'
    }
  },
  {
    field: 'remark',
    label: '备注',
    span: 24,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入备注"
      />
    )
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
const AddProRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  if (AddProRef.value) {
    let result = await AddProRef.value?.checkData()
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
    if (props.mode == 'create') {
      await StocktakeApi.createStocktake({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await StocktakeApi.updateStocktake({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  formRef.value.resetForm()
  if (props.mode === 'edit') {
    let res = await StocktakeApi.getStocktake({ id: props.id })
    Object.assign(formData, res)
  } else {
    formData.stocktakeUserId = Number(useUserStore().getUser.id)
    formData.stocktakeUserName = useUserStore().getUser.nickname
  }
})
</script>
