<template>
  <eplus-form-table
    ref="TableRef"
    prop="pocList"
    :schema="tableSchema"
    :list="formData.pocList"
  />
</template>
<script setup lang="tsx">
import type { EplusAuditable } from '@/types/eplus'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'

defineOptions({ name: 'PocTable' })
const props = defineProps<{
  type: string
  forwarderFlag?: boolean
}>()
const formData = inject('formData') as EplusAuditable
const TableRef = ref()
const tableList = computed(() => {
  return formData.value.pocList
})
let maxval = ref(1)
const maxvalChange = (val) => {
  maxval.value = val === 'focus' ? 4 : 1
  console.log(maxval.value)
}
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'name',
    label: '联系人',
    component: <el-input />,
    rules: { required: !props.forwarderFlag, message: '请输入联系人' }
  },
  {
    field: 'pocTypes',
    label: '职位',
    component: <el-input />,
    rules: {
      required: props.forwarderFlag ? false : props.type === 'formal' ? true : false,
      message: '请输入职位'
    }
  },
  {
    field: 'mobile',
    label: '手机号',
    component: <el-input />,
    rules: { required: !props.forwarderFlag, message: '请输入手机号' }
  },
  {
    field: 'email',
    label: '邮箱',
    component: <el-input />,
    rules: [
      {
        validator: (rule: any, value: any, callback: any) => {
          let regx =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
          if (value && !regx.test(value)) {
            callback(new Error('请输入正确的邮箱格式'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'telephone',
    label: '座机',
    component: <el-input />
  },
  {
    field: 'wechat',
    label: '微信',
    component: <el-input />
  },
  {
    field: 'qq',
    label: 'QQ',
    component: <el-input />
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input />
  },
  {
    field: 'defaultFlag',
    label: '默认',
    width: '80px',
    fixed: 'right',
    slot: (item, row, index) => {
      return (
        <div onClick={() => setDefault(index, formData.value.pocList)}>
          <el-button
            style="width:10px;height:10px;"
            type={row.defaultFlag == 1 ? 'primary' : ''}
            circle
            size="small"
          ></el-button>
        </div>
      )
    }
  },
  {
    label: '操作',
    width: 80,
    fixed: 'right',
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => deleteAction(index)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
]

const message = useMessage()

const saveForm = async (temporary?) => {
  let valid = await TableRef.value.validate()
  if (valid || temporary) {
    for (let i = 0; i < TableRef.value.tableData.length; i++) {
      const item = TableRef.value.tableData[i]
      if (!props.forwarderFlag && !item.email && !item.wechat && !item.qq && !temporary) {
        message.warning('联系人提交信息邮箱，微信，QQ至少填写一项')
        return false
      }
    }
    return TableRef.value.tableData
  } else {
    message.warning('联系人提交信息有误')
    return false
  }
}
defineExpose({ saveForm, tableList })
//设置为默认
const setDefault = (scope, d) => {
  for (let i = 0; i < d.length; i++) {
    d[i].defaultFlag = scope == i ? 1 : 0
  }
}

onMounted(async () => {
  watch(
    () => formData.value,
    async () => {
      TableRef.value.tableData = formData.value.pocList
    }
  )
  if (TableRef.value?.tableData?.length === 1) {
    TableRef.value.tableData[0].defaultFlag = 1
  }
})
</script>
