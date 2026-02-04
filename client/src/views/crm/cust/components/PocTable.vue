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
  required: boolean
}>()
const formData = inject('formData') as EplusAuditable
const TableRef = ref()
const tableList = computed(() => {
  return formData.value.pocList
})
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'name',
    label: '联系人名称',
    component: <el-input />,
    rules: { required: props.required, message: '请输入联系人名称' }
  },
  {
    field: 'email',
    label: '邮箱',
    component: <el-input />,
    rules: [
      // bug 717
      // {
      //   required: true,
      //   validator: (rule: any, value: any, callback: any) => {
      //     let regx =
      //       /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      //     if (value && !regx.test(value)) {
      //       callback(new Error('请输入正确的邮箱格式'))
      //     } else {
      //       callback()
      //     }
      //   }
      // }
    ]
  },
  {
    field: 'pocPosts',
    label: '职位',
    component: <el-input />
  },
  {
    field: 'mobile',
    label: '手机号',
    component: <el-input />
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
    component: (
      <el-input
        type="textarea"
        rows={1}
        maxlength={350}
        input-style="min-height:32px"
      />
    )
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
    field: 'action',
    label: '操作',
    width: '80px',
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
      if (!item.email && !item.telephone && !item.wechat && !item.qq && !temporary) {
        message.error('联系人提交信息邮箱，座机，微信，QQ至少填写一项')
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
      if (formData.value?.pocList?.length) {
        TableRef.value.tableData = formData.value.pocList
      }
    }
  )
  if (TableRef.value?.tableData?.length === 1) {
    TableRef.value.tableData[0].defaultFlag = 1
  }
})
</script>
