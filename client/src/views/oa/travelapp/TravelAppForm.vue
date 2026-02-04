<template>
  <Dialog
    :title="dialogTitle"
    v-model="dialogVisible"
  >
    <Form
      @register="formRegister"
      :schema="formSchema"
      :v-loading="formLoading"
    >
      <template #duration-append>
        <span>天</span>
      </template>
    </Form>
    <template #footer>
      <el-button
        v-if="formType != 'detail'"
        @click="submitForm"
        type="primary"
        :disabled="formLoading"
        >确 定</el-button
      >
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import * as TravelApi from '@/api/oa/travelapp'
import { useForm } from '@/hooks/web/useForm'
import { useCountryStore } from '@/store/modules/countrylist'
import { convertToFormSchema } from '@/utils/converDescSchema'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改

const countryListData = useCountryStore()
const countryData = JSON.parse(JSON.stringify(countryListData.countryList))
const queryCountry = (queryString: string, cb: any) => {
  const results = queryString ? countryData.filter(createFilter(queryString)) : countryData
  // call callback function to return suggestions
  cb(
    results.map((r: any) => {
      return { ...r, value: r.name }
    })
  )
}
const createFilter = (queryString: string) => {
  return (restaurant) => {
    return restaurant.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0
  }
}

const editScheam = [
  {
    f: 'purpose',
    l: '出差事由',
    c: '!w-[100%]',
    rules: [{ required: true }]
  },
  {
    f: 'dest',
    l: '出差地点',
    c: '!w-[100%]',
    fs: queryCountry,
    rules: [{ required: true }]
  },
  {
    showDetail: true,
    f: 'result',
    l: '结果',
    p: '暂无结果',
    c: '!w-[100%]'
  },
  {
    f: 'duration',
    n: 'Input',
    l: '出差时长',
    p: '请输入出差时长 0.5天为最小单位',
    rules: [{ required: true }],
    cp: {
      type: 'number',
      step: '0.5',
      min: '0',
      slots: {
        append: 1
      }
    }
  },
  {
    f: 'transportationType',
    l: '交通方式',
    n: 'Select',
    c: '!w-[100%]',
    //TODO 交通方式字典 getDictOptions(DICT_TYPE.OA_TRANSPORTATION_TYPE),
    o: [
      { label: '飞机', value: '1' },
      { label: '高铁', value: '2' },
      { label: '动车', value: '3' },
      { label: '铁路', value: '4' },
      { label: '轮船', value: '5' },
      { label: '自驾', value: '6' }
    ],
    cp: {
      multiple: true,
      filterable: true
    },
    rules: [{ required: true }]
  },
  {
    f: 'companions',
    l: '随行人员',
    c: '!w-[100%]'
  },
  {
    f: 'startTime',
    l: '开始时间',
    n: 'DatePicker',
    c: '!w-[100%]',
    cp: {
      format: 'YYYY-MM-DD',
      valueFormat: 'x',
      type: 'date'
    },
    rules: [{ required: true }]
  },
  {
    f: 'endTime',
    l: '结束时间',
    n: 'DatePicker',
    c: '!w-[100%]',
    cp: {
      format: 'YYYY-MM-DD',
      valueFormat: 'x',
      type: 'date'
    },
    rules: [{ required: true }]
  },
  {
    showDetail: true,
    f: 'createTime',
    l: '创建时间',
    n: 'DatePicker',
    c: '!w-[100%]',
    cp: {
      format: 'YYYY-MM-DD',
      valueFormat: 'x',
      type: 'date'
    }
  }
]

const formSchema = ref(
  convertToFormSchema(
    editScheam.filter((s) => {
      return !s.showDetail
    })
  )
)

const {
  register: formRegister,
  elFormRef,
  methods: { setValues, getFormData, getElFormExpose }
} = useForm()
/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  formSchema.value = convertToFormSchema(
    editScheam.filter((s) => {
      return type == 'detail' ? true : !s.showDetail
    })
  ).map((s) => {
    if (s.componentProps && s.formItemProps) {
      if (type == 'detail') {
        s.formItemProps.rules = []
        s.componentProps[s.componentProps.multiple ? 'disabled' : 'readonly'] = true
      } else {
        s.componentProps.readonly = false
        s.componentProps.disabled = false
      }
    }
    return s
  })
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await TravelApi.getTravelApp(id)
      if (typeof data.transportationType == 'string')
        data.transportationType = data.transportationType.split(',')
      setValues(data)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await elFormRef?.value?.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = (await getFormData()) as unknown as TravelApi.TravelAppVO
    if (Array.isArray(data.transportationType))
      data.transportationType = data.transportationType.join(',')
    if (formType.value === 'create') {
      await TravelApi.createTravelApp(data)
      message.success(t('common.createSuccess'))
    } else {
      await TravelApi.updateTravelApp(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = async () => {
  const el = await getElFormExpose()
  el?.resetFields()
}
</script>
