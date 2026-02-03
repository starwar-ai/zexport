<template>
  <Dialog
    v-model="dialogTableVisible"
    title="关联销售订单"
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
          label="销售订单编号"
          prop="code"
          show-overflow-tooltip
        />

        <el-table-column
          align="center"
          label="类型"
          prop="saleType"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ getDictLabel(DICT_TYPE.SALE_TYPE, row.saleType) }}
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          label="客户名称"
          prop="custName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="业务员"
          prop="salesName"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row.sales?.nickname || '-' }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="业务员部门"
          prop="salesDeptName"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row.sales?.deptName || '-' }}
          </template>
        </el-table-column>
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
import * as emsApi from '@/api/ems/emsList/index'
import { cloneDeep } from 'lodash-es'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import { isValidArray } from '@/utils/is'
import { formatTime } from '@/utils/index'

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
const custCodeList = ref([])
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '销售订单编号'
    },
    {
      component: <el-input></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'salesId',
      label: '业务员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择部门"></eplus-dept-select>,
      name: 'salesDeptId',
      label: '部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: (
        <eplus-dict-select
          style="width:100px"
          dictType={DICT_TYPE.SALE_TYPE}
        ></eplus-dict-select>
      ),
      name: 'saleType',
      label: '销售订单类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.SALE_TYPE, args[0])
      }
    },
    // 销售订单号、客户名称、业务员、部门、销售订单类型、创建时间
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
    let res = await emsApi.contractList({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      custCodeList: custCodeList.value,
      ...searchForm
    })

    if (isValidArray(res.list)) {
      tableData.value = res.list.map((item) => {
        return {
          ...item,
          children: undefined
        }
      })
    } else {
      tableData.value = []
    }

    pageForm.total = res?.total || 0
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  }
}

const open = async (list, custCodes) => {
  custCodeList.value = custCodes
  parentList.value = cloneDeep(list.value)
  selectedDiaData.value = cloneDeep(list.value)
  searchForm = {}
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item: any) => {
      parentList.value.forEach((el: any) => {
        if (item.code == el.code) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}
// toggleRowSelection

const selectedDiaData = ref<any>([])
const handleSelectionChange = (selection, row) => {
  if (parentList.value.length > 0) {
    let codeList = selectedDiaData.value.map((item: any) => item.code)
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
