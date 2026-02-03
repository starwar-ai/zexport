<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >关联申请单
    </el-button>
  </div>
  <Table
    :columns="columns"
    headerAlign="center"
    align="center"
    :data="tableData"
  />

  <Dialog
    v-model="dialogTableVisible"
    title="关联申请单"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="mb10px">
      <eplus-search
        :fields="eplusSearchSchema.fields"
        :moreFields="eplusSearchSchema.moreFields"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>
    <Table
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :pagination="{
        total: tableObject.total
      }"
      :columns="dialogColumns"
      headerAlign="center"
      align="center"
      :data="tableObject.tableList"
      @selection-change="handleSelectionChange"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { isValidArray } from '@/utils/is'
import { getApplyList } from '@/api/oa/entertainApply'
import { setSourceId } from '@/utils/auth'

const message = useMessage()
defineOptions({ name: 'TravelListCom' })
const props = defineProps<{
  formData
}>()

// 获取父组件的表单引用，用于触发验证
const parentFormRef = inject('formRef') as any
const tableData = ref<any[]>([])
const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await getApplyList(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  defaultParams: { companyId: props.formData?.companyId, applyType: 1 }
})

const { getList, setSearchParams } = tableMethods

// 触发关联申请单字段的验证
const triggerValidation = () => {
  if (parentFormRef?.value?.validateField) {
    parentFormRef.value.validateField('TravelList', (valid) => {})
  }
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '创建时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const handleSearch = (model) => {
  setSearchParams({ ...model, companyId: props.formData?.companyId, applyType: 1 }, [])
}
const handleReset = () => {
  setSearchParams({ companyId: props.formData?.companyId, applyType: 1 }, [])
}
const columns = reactive([
  {
    type: 'index',
    label: '序号',
    width: columnWidth.m
  },
  {
    field: 'code',
    label: '申请单号',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <el-button
            link
            type="primary"
            onClick={() => {
              handleDetail(row.id)
            }}
          >
            {row.code}
          </el-button>
        )
      }
    }
  },
  {
    field: 'travelType',
    label: '出差类型',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.TRAVEL_TYPE)
  },
  {
    field: 'dest',
    label: '出差地点',
    minWidth: columnWidth.m
  },

  {
    field: 'startTime',
    label: '开始时间',
    minWidth: columnWidth.m,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'endTime',
    label: '结束时间',
    minWidth: columnWidth.m,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'amount',
    label: '预估费用',
    minWidth: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'purpose',
    label: '出差事由',
    minWidth: columnWidth.m
  },
  {
    field: 'remark',
    label: '备注',
    minWidth: columnWidth.m
  },
  {
    field: 'action',
    label: '操作',
    width: '100px',
    fixed: 'right',
    align: 'center',
    slots: {
      default: (data) => {
        const { $index } = data
        return (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={async () => {
                await delRow($index)
              }}
            >
              移除
            </el-button>
          </div>
        )
      }
    }
  }
])
const dialogColumns = reactive([
  {
    label: '选择',
    type: 'selection',
    minWidth: '60px'
  },
  {
    field: 'code',
    label: '申请单号',
    width: columnWidth.l
  },
  {
    field: 'travelType',
    label: '出差类型',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.TRAVEL_TYPE)
  },
  {
    field: 'dest',
    label: '出差地点',
    minWidth: columnWidth.m
  },
  {
    field: 'startTime',
    label: '开始时间',
    minWidth: columnWidth.m,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'endTime',
    label: '结束时间',
    minWidth: columnWidth.m,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'amount',
    label: '预估费用',
    minWidth: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'purpose',
    label: '出差事由',
    minWidth: columnWidth.m
  },
  {
    field: 'remark',
    label: '备注',
    minWidth: columnWidth.m
  }
])

const dialogTableVisible = ref(false)

const handleAdd = async () => {
  dialogTableVisible.value = true
  await getList()
}

const handleSelectionChange = (val) => {
  selectedDiaData.value = val
}

const selectedDiaData = ref([])

const handleSure = () => {
  if (selectedDiaData.value.length > 0) {
    tableData.value = selectedDiaData.value
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  selectedDiaData.value = []
  dialogTableVisible.value = false
}

const delRow = (index) => {
  tableData.value.splice(index, 1)
}

const checkData = () => {
  if (isValidArray(tableData.value)) {
    return tableData.value.map((item) => item.id)
  } else {
    message.warning(`请选择申请单`)
    return false
  }
}

const init = (data) => {
  if (isValidArray(data)) {
    tableData.value = [...tableData.value, ...data]
  }
}

const router = useRouter()
const handleDetail = (id) => {
  setSourceId(id)
  router.push({ path: '/oa/apply/travelIndex' })
}

// 监听 tableData 变化，触发验证
watch(
  () => tableData.value,
  (newVal, oldVal) => {
    // 确保数据确实发生了变化
    if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
      nextTick(() => {
        triggerValidation()
      })
    }
  },
  { deep: true, immediate: false }
)

defineExpose({
  checkData,
  tableData,
  init
})
// watchEffect(() => {
//   if (props.formData?.simpleTravelAppResp) {
//     tableData.value.push(props.formData.simpleTravelAppResp)
//   }
// })
</script>
