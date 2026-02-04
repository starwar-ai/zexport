<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :mode="mode"
    :loading="loading"
    v-model="formData"
    :saveAction="{
      permi: 'exms:exhibition:create',
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: ['exms:exhibition:submit'],
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

      <template #exmsEventCategoryId>
        <eplus-input-select
          v-if="!loading"
          v-model="formData.exmsEventCategoryId"
          :dataList="eventCategorySelectList"
          label="name"
          value="id"
          @change-emit="(...$event) => getEventCategoryName($event)"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as ExhibitionApi from '@/api/exms/exhibition/index'
import { useCountryStore } from '@/store/modules/countrylist'
import { useUserStore } from '@/store/modules/user'
import { getcompanySimpleList } from '@/api/common'
import { cloneDeep } from 'lodash-es'
import { getEventCategory } from '@/api/exms/eventCategory'
import { isValidArray } from '@/utils/is'
import { EplusCountrySelect } from '@/components/EplusSelect'

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
  } else if (type == 2) {
    formData.countryName = val.name
    formData.exmsEventCategoryId = ''
    if (formData.countryName.toLowerCase() === 'china') {
      eventCategorySelectList.value = eventCategoryList.value.filter(
        (item) => item.isDomestic === 1
      )
    } else {
      eventCategorySelectList.value = eventCategoryList.value.filter(
        (item) => item.isDomestic === 0
      )
    }
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

const eventCategoryList = ref([])
let eventCategorySelectList = ref([])
const getEventCategoryList = async () => {
  let res = await getEventCategory({ pageSize: 100, pageNo: 1 })
  eventCategoryList.value = res.list
  eventCategorySelectList.value = eventCategoryList.value
}

const getEventCategoryName = async (val) => {
  if (isValidArray(val)) {
    let item = val[1].find((item) => item.id === val[0]) || {}
    formData.exmsEventCategoryName = item?.name || ''
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
    label: '展会名称',
    component: <el-input></el-input>,
    rules: {
      required: true,
      message: '请输入展会名称'
    }
  },
  {
    field: 'deptList',
    label: '费用承担部门',
    component: (
      <eplus-dept-select
        multipleFlag={true}
        placeholder="请选择归属部门"
        class="w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择费用承担部门'
    }
  },
  {
    field: 'budget',
    label: '展会预算',
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
    field: 'theme',
    label: '展会主题',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.EXMS_EXHIBITION_THEME}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择展会主题'
    }
  },
  {
    field: 'stallThemeList',
    label: '摊位主题',
    component: (
      <eplus-dict-select
        multiple
        dictType={DICT_TYPE.EXMS_STALL_THEME}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择摊位主题'
    }
  },
  {
    field: 'countryId',
    label: '国家/地区',
    component: (
      <EplusCountrySelect
        onChange={(item) => getName(item, 2)}
        class="!w-100%"
      />
    )
  },
  {
    field: 'cityName',
    label: '城市',
    component: <el-input></el-input>
  },
  {
    field: 'exmsEventCategoryId',
    label: '展会系列',
    rules: {
      required: true,
      message: '请选择展会系列'
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
    field: 'stallArea',
    label: '摊位面积',
    component: <el-input></el-input>
  },
  {
    field: 'ownerUserId',
    label: '展会负责人',
    component: <eplus-user-select onChange={(...$event) => getName($event, 3)}></eplus-user-select>
  },
  {
    field: 'ownerDeptName',
    label: '展会负责人部门',
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
      await ExhibitionApi.createExhibition({ ...paramsResult, submitFlag })
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess')
    } else {
      await ExhibitionApi.updateExhibition({ ...paramsResult, submitFlag })
      message.success('操作成功')
      close()
      emit('handleSuccess')
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  formRef.value.resetForm()
  await getEventCategoryList()
  if (props.mode === 'edit') {
    let res = await ExhibitionApi.getExhibitionInfo({ id: props.id })
    Object.assign(formData, res)
    loading.value = false
  } else {
    if (userInfo.roles && userInfo.roles.length > 0 && userInfo.roles[0].code == 'super_admin') {
      return
    }
    formData.applyUserId = userInfo.user.id
    formData.applyUserName = userInfo.user.nickname
    formData.applyDeptId = userInfo.user.deptId
    formData.applyDeptName = userInfo.user.deptName
    loading.value = false
  }
})
</script>
