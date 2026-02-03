<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
  >
    <eplus-form-items
      v-if="props.mode === 'create'"
      title=""
      :formSchemas="basicSchemas"
    />
    <div v-else>
      <eplus-form-items
        title=""
        :formSchemas="editSchemas"
      >
        <template #mainInstanceFlag>
          <el-switch
            v-model="formData.mainInstanceFlag"
            :active-value="1"
            :inactive-value="0"
            @change="handleChange"
          />
        </template>
      </eplus-form-items>
      <eplus-form-items
        title=""
        :formSchemas="childchema"
      >
        <template #children>
          <FormChangeItemTable
            :formData="formData"
            ref="childrenRef"
            :required="true"
          />
        </template>
      </eplus-form-items>
    </div>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as formChangeApi from '@/api/system/formChange'
import { cloneDeep } from 'lodash-es'
import FormChangeItemTable from './FormChangeItemTable.vue'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'LoanAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(true)

const formData: EplusAuditable = reactive({})
provide('formData', formData)

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const { close } = inject('dialogEmits') as {
  close: () => void
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'name',
    label: '表名称',
    component: <el-input class="!w-240px" />,
    span: 24
  },
  {
    field: 'description',
    label: '描述',
    component: <el-input class="!w-240px" />,
    span: 24
  }
])

const editSchemaFalse = reactive([
  {
    field: 'mainInstanceFlag',
    label: '流程是否开启',
    editable: true,
    span: 8
  }
])

const editSchemas = reactive([
  {
    field: 'mainInstanceFlag',
    label: '流程是否开启',
    editable: true,
    span: 8
  }
  // {
  //   field: 'modelKey',
  //   label: '审批流程',
  //   editable: true,
  //   component: (
  //     <eplus-input-search-select
  //       api={ModelApi.getModelSimpleList}
  //       params={{}}
  //       keyword="name"
  //       label="name"
  //       value="key"
  //       class="!w-240px"
  //       placeholder="请选择"
  //       onChangeEmit={(...$event) => getPath($event)}
  //     />
  //   ),
  //   rules: {
  //     required: true,
  //     message: '请选择审批流程'
  //   },
  //   span: 8
  // }
])

const childchema = reactive([
  {
    field: 'children',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
])
const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const childrenRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)
  if (childrenRef.value) {
    let result = await childrenRef.value?.saveForm()
    if (result) {
      params.children = result
    } else {
      params.children = []
      return false
    }
  }
  return params
}

const getPath = (val) => {
  if (val?.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.name === val[0]) {
          formData.path = item.path
        }
      })
    }
  }
}

const handleChange = (val) => {
  if (!formData.instanceFlag) {
    formData.processInstanceId = ''
  }
  provide('formData', formData)
}

const saveForm = async () => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await formChangeApi
        .createFormChange(paramsResult)
        .then(() => {
          message.success('提交成功')
          close()
          emit('handleSuccess', 'success')
        })
        .catch(() => {})
    } else {
      await formChangeApi
        .updateFormChange(paramsResult)
        .then(() => {
          message.success('修改成功')
          close()
          emit('handleSuccess', 'success')
        })
        .catch(() => {})
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    loading.value = true
    formRef.value.resetForm()
    if (props.mode === 'edit') {
      let res = await formChangeApi.getFormChange(props.id)
      Object.assign(formData, res)
    }
    updateDialogActions(
      <el-button
        onClick={() => {
          saveForm()
        }}
        type="primary"
        key="save"
      >
        保存
      </el-button>
    )
    await nextTick()
    setTimeout(() => {
      loading.value = false
    }, 100)
  } finally {
    // loading的隐藏已移到setTimeout中
  }
})
</script>
