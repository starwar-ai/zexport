<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >选择借款单</el-button
    >
  </div>
  <!-- <Table
    :columns="columns"
    headerAlign="center"
    align="center"
    :data="tableData"
  /> -->

  <Dialog
    v-model="dialogTableVisible"
    title="选择借款单"
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
      @row-click="getRow"
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
import { formatDateColumn } from '@/utils/table'
import * as repayapp from '@/api/oa/repayapp'
import { useUserStore } from '@/store/modules/user'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'

const message = useMessage()
defineOptions({ name: 'TravelListCom' })
const props = defineProps<{
  formData
}>()

const tableData = ref([])
const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await repayapp.getLoanSimpleList({
      ...ps,
      applyerId: useUserStore().user.id,
      auditStatus: 2,
      repayStatus: 0,
      outstandingAmountNotZeroFlag: 1
    })
    return {
      list: res.list,
      total: res.total
    }
  }
})

const { getList, setSearchParams } = tableMethods

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '借款单号'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'loanDate',
          label: '借款日期',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const handleSearch = (model) => {
  setSearchParams({
    ...model,
    applyerId: useUserStore().user.id,
    repayStatus: 0,
    outstandingAmountNotZeroFlag: 1
  })
}
const handleReset = () => {
  setSearchParams({
    applyerId: useUserStore().user.id,
    repayStatus: 0,
    outstandingAmountNotZeroFlag: 1
  })
}
const dialogColumns = reactive([
  {
    label: '选择',
    width: '60px',
    slots: {
      default: (data) => {
        const { row, $index } = data
        return (
          <el-radio
            v-model={radioobj.val}
            label={row.id}
          >
            &nbsp;
          </el-radio>
        )
      }
    }
  },
  {
    field: 'code',
    label: '借款编号',
    width: '120px'
  },
  {
    field: 'amount',
    label: '借款金额',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return row.amount ? `${row?.amount?.amount} ${row?.amount?.currency}` : 0
      }
    }
  },
  {
    field: 'outstandingAmount',
    label: '待还金额',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return row.outstandingAmount
          ? `${row?.outstandingAmount?.amount} ${row?.outstandingAmount?.currency}`
          : 0
      }
    }
  },
  {
    field: 'repayAmount',
    label: '已还金额',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return row.repayAmount ? `${row?.repayAmount?.amount} ${row?.repayAmount?.currency}` : 0
      }
    }
  },
  {
    field: 'inRepaymentAmount',
    label: '审批中',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return row.inRepaymentAmount
          ? `${row?.inRepaymentAmount?.amount} ${row?.inRepaymentAmount?.currency}`
          : 0
      }
    }
  },
  {
    field: 'companyName',
    label: '公司主体',
    minWidth: '120px'
  },
  {
    field: 'loanDate',
    label: '借款日期',
    minWidth: '120px',
    formatter: formatDateColumn()
  }
])

const dialogTableVisible = ref(false)

const handleAdd = async () => {
  dialogTableVisible.value = true
  await getList()
}

let radioobj = reactive({
  val: undefined
})
const getRow = (row: any) => {
  radioobj.val = row.id
  selectedDiaData.value = [row]
}

const selectedDiaData = ref([])

const emit = defineEmits(['change'])

const handleSure = () => {
  if (selectedDiaData.value.length > 0) {
    tableData.value = selectedDiaData.value
    dialogTableVisible.value = false
    emit('change', tableData.value[0])
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  radioobj.val = undefined
  selectedDiaData.value = []
  dialogTableVisible.value = false
}

const checkData = () => {
  if (tableData.value.length != 1) {
    message.warning(`出差单数据有且仅有一条`)
    return false
  }
  return tableData.value[0].id
}
defineExpose({
  checkData,
  tableData
})
watchEffect(() => {
  if (props.formData.loanAppId && tableObject.tableList.length > 0) {
    tableObject.tableList.forEach((item) => {
      if (item.id === props.formData.loanAppId) {
        getRow(item)
      }
    })
  }
})
</script>
