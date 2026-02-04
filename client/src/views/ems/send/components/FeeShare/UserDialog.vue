<template>
  <Dialog
    v-model="dialogTableVisible"
    title="关联用户"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <div class="mb10px">
        <eplus-search
          :fields="eplusSearchSchema.fields"
          :moreFields="eplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
      </div>

      <el-table
        border
        :data="tableData"
        @select="handleSelectionChange"
        @select-all="handleSelectionAllChange"
        row-key="code"
        ref="tableRef"
        @row-click="getRow"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="员工编号"
          prop="code"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="用户名称"
          prop="nickname"
          show-overflow-tooltip
        />

        <el-table-column
          align="center"
          label="部门名称"
          prop="deptName"
          show-overflow-tooltip
        />
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
      />
    </div>
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
import { getUserSimplePage } from '@/api/system/user'
import { cloneDeep } from 'lodash-es'
import { EplusSearchSchema } from '@/components/EplusSearch/types'

defineOptions({ name: 'UserDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const loading = ref(false)
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input />,
      name: 'nickname',
      label: '用户名称'
    },
    {
      component: <el-input />,
      name: 'code',
      label: '员工编号'
    }
  ],
  moreFields: []
}

const handleSearch = (model) => {
  searchForm = model
  pageForm.pageNo = 1
  getList()
}
const handleReset = () => {
  searchForm = {}
  pageForm.pageNo = 1
  getList()
}

const emit = defineEmits<{
  sure: [link: string]
}>()

const getList = async () => {
  loading.value = true
  try {
    let res = await getUserSimplePage({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      ...searchForm
    })
    tableData.value = res.list
    pageForm.total = res.total
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  }
}

const open = async (list) => {
  parentList.value = cloneDeep(list.value)
  selectedDiaData.value = cloneDeep(list.value)
  searchForm = {}
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item) => {
      parentList.value.forEach((el) => {
        if (item.code == el.code) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}
// toggleRowSelection

const selectedDiaData = ref([])
const handleSelectionChange = (selection, row) => {
  if (parentList.value.length > 0) {
    let codeList = selectedDiaData.value.map((item) => item.code)
    let rowIndex = codeList.findIndex((item) => item === row.code)
    if (codeList.includes(row.code)) {
      selectedDiaData.value.splice(rowIndex, 1)
    } else {
      selectedDiaData.value.push(row)
    }
  } else {
    selectedDiaData.value = selection
  }
}

const handleSelectionAllChange = (selection) => {
  selection.forEach((item) => {
    handleSelectionChange(selection, item)
  })
}

const getRow = (row) => {
  // let codeList = selectedDiaData.value.map((item) => item.code)
  // let rowIndex = codeList.findIndex((item) => item === row.code)
  // if (codeList.includes(row.code)) {
  //   // selectedDiaData.value.splice(rowIndex, 1)
  // } else {
  //   selectedDiaData.value.push(row)
  //   tableRef.value.toggleRowSelection(row, undefined)
  // }
}

const handleCancel = () => {
  tableData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  if (len > 0) {
    emit('sure', selectedDiaData.value)
    handleCancel()
  } else {
    message.warning('请选择用户')
  }
}
defineExpose({ open })
</script>
