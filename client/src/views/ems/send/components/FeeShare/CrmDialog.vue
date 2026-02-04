<template>
  <Dialog
    v-model="dialogTableVisible"
    title="关联客户"
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
          label="客户编号"
          prop="code"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="客户名称"
          prop="name"
          show-overflow-tooltip
        />

        <el-table-column
          align="center"
          label="业务员"
          prop="managerList"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row.managerList?.map((item) => item.nickname).join(',') }}
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          label="类型"
          prop="customerTypesName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="创建时间"
          prop="createTime"
          show-overflow-tooltip
        >
          <template #default="{ row }">{{ formatTime(row.createTime, 'yyyy-MM-dd') }}</template>
        </el-table-column>
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
import * as travelreimbApi from '@/api/oa/travelreimb'
import { cloneDeep } from 'lodash-es'
import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as UserApi from '@/api/system/user'
import { handleTree } from '@/utils/tree'
import * as CustApi from '@/api/crm/cust'

defineOptions({ name: 'CrmDialog' })
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
let classTree = ref<Tree[]>([]) // 树
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '客户编号'
    },
    {
      component: <el-input></el-input>,
      name: 'name',
      label: '客户名称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerIds',
      label: '业务员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: (
        <eplus-tree-select
          checkStrictly={false}
          treeList={classTree}
        />
      ),
      name: 'customerTypes',
      label: '客户类型'
    },
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
    let res = await travelreimbApi.getCustSimpleList({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      ...searchForm
    })
    tableData.value = res?.list || []
    pageForm.total = res?.total || 0
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  }
}

const getClassTreeData = async () => {
  let res = await CustApi.getCustCategoryTree()
  classTree.value = handleTree(res)
}

const open = async (list) => {
  parentList.value = cloneDeep(list.value)
  selectedDiaData.value = cloneDeep(list.value)
  searchForm = {}
  pageForm.pageNo = 1
  await getClassTreeData()
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
    let remainder = 100 % len,
      integer = Math.floor(100 / len)
    emit(
      'sure',
      selectedDiaData.value.map((item, index) => {
        return {
          ...item,
          ratio: index + 1 == len ? integer + remainder : integer
        }
      })
    )
    handleCancel()
  } else {
    message.warning('请选择客户')
  }
}
defineExpose({ open })
</script>
