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
import { columnWidth, formatMoneyColumn } from '@/utils/table'
import * as EntertainApplyApi from '@/api/oa/entertainApply'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { isValidArray } from '@/utils/is'
import { setSourceId } from '@/utils/auth'

const message = useMessage()
defineOptions({ name: 'ApplyListCom' })
const props = defineProps<{
  formData: any
}>()

// 获取父组件的表单引用，用于触发验证
const parentFormRef = inject('formRef') as any
let tableData = ref([])
// 触发关联申请单字段的验证
const triggerValidation = () => {
  if (parentFormRef?.value?.validateField) {
    parentFormRef.value.validateField('applyList', (valid) => {})
  }
}
const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await EntertainApplyApi.getApplyList(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  defaultParams: { companyId: props.formData?.companyId, applyType: 2 }
})

const { setSearchParams } = tableMethods

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
  setSearchParams({ ...model, applyType: 2, companyId: props.formData.companyId }, [])
}
const handleReset = () => {
  setSearchParams({ applyType: 2, companyId: props.formData.companyId }, [])
}
const columns = reactive([
  {
    field: 'code',
    label: '申请单号',
    minWidth: columnWidth.l,
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
    field: 'applyer',
    label: '费用申请人',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return <div>{row.applyer?.nickname ? row.applyer.nickname : ''}</div>
      }
    }
  },
  {
    field: 'amount',
    label: '预估费用金额',
    minWidth: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'amountDesc',
    minWidth: columnWidth.m,
    label: '明细说明'
  },
  {
    field: 'purpose',
    label: '申请事由',
    minWidth: columnWidth.m,
    isTooltip: true
  },
  {
    field: 'remark',
    label: '备注',
    minWidth: columnWidth.m,
    isTooltip: true
  },
  {
    field: 'action',
    label: '操作',
    minWidth: columnWidth.m,
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
    minWidth: columnWidth.l
  },
  {
    field: 'applyer',
    label: '费用申请人',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return <div>{row.applyer?.nickname || ''}</div>
      }
    }
  },
  {
    field: 'amount',
    label: '预估费用金额',
    minWidth: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'amountDesc',
    minWidth: columnWidth.m,
    label: '明细说明'
  },
  {
    field: 'purpose',
    label: '申请事由',
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
  if (props.formData.companyId) {
    dialogTableVisible.value = true
    await handleReset()
  } else {
    message.warning('请先选择费用主体')
  }
}

const selectedDiaData = ref([])

const handleSelectionChange = (val) => {
  selectedDiaData.value = val
}

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

/**
 * 将数据添加到表格数据中
 *
 * @param data 要添加的数据
 */
const init = (data) => {
  tableData.value = [...tableData.value, ...data]
}

const router = useRouter()
const handleDetail = (id) => {
  setSourceId(id)
  router.push({ path: '/oa/apply/generalIndex' })
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
</script>
