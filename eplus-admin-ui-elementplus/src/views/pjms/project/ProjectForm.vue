<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'pjms:project:create',
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: ['pjms:project:submit'],
      handler: () => saveForm(1)
    }"
  >
    <eplus-form-items
      title="基础信息"
      :formSchemas="basicSchemas"
    >
      <template #budget>
        <EplusMoney
          v-model:amount="formData.budget.amount"
          v-model:currency="formData.budget.currency"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as ProjectApi from '@/api/pjms/project/index'
import { useCountryStore } from '@/store/modules/countrylist'
import { useUserStore } from '@/store/modules/user'
import { getcompanySimpleList } from '@/api/common'
import { cloneDeep } from 'lodash-es'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'ExhibitionForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)
const countryListData = useCountryStore()
const userInfo = useUserStore()
const formData: EplusAuditable = reactive({
  budget: {
    amount: undefined,
    currency: 'RMB'
  },
  countryName: '',
  applyDate: Date.now(),
  applyUserId: '',
  applyUserName: '',
  applyDeptId: '',
  applyDeptName: '',
  ownerUserId: '',
  ownerUserName: '',
  ownerDeptId: '',
  ownerDeptName: ''
})

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const getName = async (val, type) => {
  if (type == 1) {
    formData.companyName = val[0].name
  } else if (type == 3) {
    formData.ownerUserName = val[0].nickname
    formData.ownerDeptId = val[0].deptId
    formData.ownerDeptName = val[0].deptName
  } else if (type == 4) {
    formData.applyUserName = val[0].nickname
    formData.applyDeptId = val[0].deptId
    formData.applyDeptName = val[0].deptName
  }
}

const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '公司主体',
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
    field: 'name',
    label: '项目名称',
    component: <el-input></el-input>,
    rules: {
      required: true,
      message: '请输入项目名称'
    }
  },
  {
    field: 'developType',
    label: '研发类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PJMS_DEVELOP_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择研发类型'
    }
  },
  {
    field: 'budget',
    label: '项目预算',
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^(\d{1,11})(\.?\d{1,3})?$/
        if (formData.budget.amount === '') {
          callback(new Error('请输入项目预算'))
        } else if (!regx.test(formData.budget.amount)) {
          callback(new Error('金额只能输入数字,最多11位整数,3位小数'))
        } else if (!formData.budget.currency) {
          callback(new Error('请选择币种'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'planDate',
    label: '计划日期',
    component: (
      <el-date-picker
        type="daterange"
        placeholder="请选择"
        value-format="x"
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择计划日期'
    }
  },
  {
    field: 'ownerUserId',
    label: '项目负责人',
    component: <eplus-user-select onChange={(...$event) => getName($event, 3)}></eplus-user-select>
  },
  {
    field: 'ownerDeptName',
    label: '负责人部门',
    component: <el-input disabled></el-input>
  },
  {
    field: 'applyUserId',
    label: '申请人',
    component: <eplus-user-select onChange={(...$event) => getName($event, 4)}></eplus-user-select>
  },
  {
    field: 'applyDeptName',
    label: '申请人部门',
    component: <el-input disabled></el-input>
  },
  {
    field: 'applyDate',
    label: '申请日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
        class="!w-100%"
      />
    )
  },
  {
    field: 'remark',
    label: '备注',
    span: 24,
    component: <el-input type="textarea"></el-input>
  }
])

const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  return params
}
const saveForm = async (submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await ProjectApi.createProject({ ...paramsResult, submitFlag })
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess')
    } else {
      await ProjectApi.updateProject({ ...paramsResult, submitFlag })
      message.success('操作成功')
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
    let res = await ProjectApi.getProjectInfo({ id: props.id })
    Object.assign(formData, res)
  } else {
    formData.applyUserId = userInfo.user.id
    formData.applyUserName = userInfo.user.nickname
    formData.applyDeptId = userInfo.user.deptId
    formData.applyDeptName = userInfo.user.deptName
  }
})
</script>
