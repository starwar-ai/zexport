<template>
  <Dialog
    v-model="dialogTableVisible"
    title="关联采购订单"
    width="900"
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
          label="采购订单编号"
          prop="code"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="供应商名称"
          prop="venderName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="采购员名称"
          prop="purchaseUserName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="采购员部门"
          prop="purchaseUserDeptName"
          show-overflow-tooltip
        />

        <el-table-column
          align="center"
          label="创建时间"
          prop="purchaseTime"
          show-overflow-tooltip
        >
          <template #default="{ row }">{{
            formatTime(row.purchaseTime, 'yyyy-MM-dd HH:mm:ss')
          }}</template>
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
import * as emsApi from '@/api/ems/emsList/index'
import { cloneDeep } from 'lodash-es'
import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { getDictLabel } from '@/utils/dict'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { isValidArray } from '@/utils/is'
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'

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
let searchForm = reactive({})
const venderCodeList = ref([])
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '采购订单编号'
    },
    {
      component: <el-input></el-input>,
      name: 'venderName',
      label: '供应商名称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaseUserId',
      label: '采购员员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择部门"></eplus-dept-select>,
      name: 'purchaseUserDeptId',
      label: '部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    // {
    //   component: <el-input></el-input>,
    //   name: 'purchaseUserName',
    //   label: '采购员名称'
    // },
    // {
    //   component: <el-input></el-input>,
    //   name: 'purchaseUserDeptName',
    //   label: '采购员部门'
    // },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'purchaseTime',
          label: '创建时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const dictValue = (dictType, value) => {
  if (value) {
    let list = JSON.parse(value)
    if (Array.isArray(list)) {
      let valList = list.map((c) => {
        return getDictLabel(dictType, c)
      })
      return valList.join(',')
    }
  }
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
    let res = await emsApi.purchaseList({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      venderCodeList: venderCodeList.value,
      ...searchForm
    })
    if (isValidArray(res.list)) {
      tableData.value = res.list.map((item) => {
        return {
          ...item,
          children: undefined
        }
      })
      pageForm.total = res.total
    } else {
      tableData.value = []
      pageForm.total = 0
    }

    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  }
}

const open = async (list, codes) => {
  venderCodeList.value = codes
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
    let remainder = 100 % len,
      integer = Math.floor(100 / len)
    let list = selectedDiaData.value.map((item, index) => {
      return {
        ...item,
        ratio: index + 1 == len ? integer + remainder : integer
      }
    })
    console.log(list)
    emit('sure', list)
    handleCancel()
  } else {
    message.warning('请选择采购订单')
  }
}
defineExpose({ open })
</script>
