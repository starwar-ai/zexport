<template>
  <el-dialog
    v-model="dialogTableVisible"
    title="关联收款银行"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <el-table
        border
        :data="tableData"
        @select="handleSelectionChange"
        @select-all="handleSelectionAllChange"
        row-key="code"
        ref="tableRef"
        :max-height="400"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="公司名称"
          prop="companyName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="银行名称"
          prop="bankName"
          show-overflow-tooltip
        />

        <el-table-column
          align="center"
          label="银行账号"
          prop="bankCode"
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
  </el-dialog>
</template>
<script setup lang="tsx">
import { companyBanklist } from '@/api/crm/cust'

import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'CollectionAccountDia' })
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

const emit = defineEmits<{
  sure: [link: string]
}>()

const getList = async () => {
  loading.value = true
  try {
    let res = await companyBanklist()
    tableData.value = res
    // pageForm.total = res.total
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
    let codeList = selectedDiaData.value.map((item: any) => item.id)
    let rowIndex = codeList.findIndex((item) => item === row.id)
    if (codeList.includes(row.id)) {
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

const handleCancel = () => {
  tableData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  if (len > 0) {
    let uniqueIds = [...new Set(selectedDiaData.value.map((obj) => obj.id))]
    if (len != uniqueIds.length) {
      message.warning('所选择数据中同一公司名称不能重复选择')
    } else {
      emit(
        'sure',
        selectedDiaData.value.map((item) => {
          return {
            ...item,
            companyBankId: item.id
          }
        })
      )
      handleCancel()
    }
  } else {
    message.warning('请选择银行信息')
  }
}
defineExpose({ open })
</script>
