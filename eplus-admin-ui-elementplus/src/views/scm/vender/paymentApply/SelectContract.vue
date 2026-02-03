<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择合同"
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
      style="height: 500px"
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
import { getPurchaseContractSimpleList } from '@/api/scm/purchaseContract'
import { EplusSearchSchema } from '@/components/EplusSearch/types'

const message = useMessage()
defineOptions({ name: 'SelectContract' })

const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await getPurchaseContractSimpleList({
      ...ps
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  defaultParams: {}
})

const { getList, setSearchParams } = tableMethods

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'purchaseContractCode',
      label: '合同编号'
    }
  ],
  moreFields: []
}
const rowIndex = ref(0)
const open = async (obj, index) => {
  rowIndex.value = index
  dialogTableVisible.value = true
  await getList()
}

const handleSearch = (model) => {
  setSearchParams({ ...model }, [])
}
const handleReset = () => {
  setSearchParams({}, [])
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
            label={row.code}
          >
            &nbsp;
          </el-radio>
        )
      }
    }
  },
  {
    field: 'code',
    label: '合同编号',
    minWidth: '100px'
  },
  {
    field: 'companyName',
    label: '公司主体',
    minWidth: '100px'
  },
  {
    field: 'custName',
    label: '客户名称',
    minWidth: '100px'
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    minWidth: '100px'
  },
  {
    field: 'venderName',
    label: '供应商名称',
    minWidth: '100px'
  }
])

const dialogTableVisible = ref(false)

let radioobj = reactive({
  val: undefined
})
const getRow = (row: any) => {
  radioobj.val = row.code
  selectedDiaData.value = row
}

const selectedDiaData = ref()

const emit = defineEmits(['sure'])
const handleSure = () => {
  if (radioobj.val) {
    emit('sure', { index: rowIndex.value, code: radioobj.val })
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  radioobj.val = undefined
  selectedDiaData.value = []
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
