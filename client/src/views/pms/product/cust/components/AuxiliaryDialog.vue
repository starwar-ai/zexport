<template>
  <Dialog
    v-model="dialogTableVisible"
    title="添加包材"
    width="1200"
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
        v-loading="loading"
        @row-click="getRow"
        height="400"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="产品编号"
          prop="code"
          show-overflow-tooltip
          width="200px"
        />
        <el-table-column
          align="center"
          label="图片"
          prop="mainPicture"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <EplusImgEnlarge :mainPicture="row?.mainPicture" />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="中/英文品名"
          prop="skuName"
          show-overflow-tooltip
          width="130px"
        >
          <template #default="{ row }">
            {{ row?.skuName }}
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
        layout="total, sizes, prev, pager, next, jumper"
      />
      <!-- </div> -->
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import * as skuApi from '@/api/pms/auxiliary/index'
import { cloneDeep } from 'lodash-es'
// import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'

defineOptions({ name: 'AuxiliaryDialog' })
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
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编码'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '中文品名'
    }
  ],
  moreFields: []
}
const handleSearch = (model) => {
  searchForm = { ...model }
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
    let res = await skuApi.simpleSkuList({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      ...searchForm
    })
    tableData.value = res.list || []
    // tableData.value.categoryList = categoryList || []
    pageForm.total = res.total || 0
    loading.value = false
  } catch {
    loading.value = false
  } finally {
    loading.value = false
  }
}

const open = async (list) => {
  parentList.value = cloneDeep(list.value)
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList()
}
defineExpose({ open })
// toggleRowSelection

const selectedDiaData = ref([])
const handleSelectionChange = (selection, row) => {
  if (parentList.value?.length > 0) {
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
  let codeList = selectedDiaData.value.map((item) => item.code)
  let rowIndex = codeList.findIndex((item) => item === row.code)
  if (codeList.includes(row.code)) {
    selectedDiaData.value.splice(rowIndex, 1)
    tableRef.value.toggleRowSelection(row, undefined)
  } else {
    selectedDiaData.value.push(row)
    tableRef.value.toggleRowSelection(row, undefined)
  }
}

const handleCancel = () => {
  tableData.value = []
  selectedDiaData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  let result = cloneDeep(selectedDiaData.value)
  result.forEach((item) => {
    return (item.skuCode = item.code)
  })
  if (len > 0) {
    emit(
      'sure',
      // @ts-ignore
      result
    )
    handleCancel()
  } else {
    message.warning('请选择包材')
  }
}

onMounted(async () => {
  try {
  } catch {}
})
</script>

<style lang="scss" scoped>
.totalAction {
  cursor: pointer;
  width: 2000px;
}

.totalAction:hover {
  border-color: #ccc;
  box-shadow: 0 0 0 1px #eee;
}
</style>
