<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择发票"
    width="800"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <el-table
        border
        :data="tableData"
        @select="handleSelectionChange"
        @select-all="handleSelectionAllChange"
        row-key="id"
        ref="tableRef"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="发票编号"
          prop="invoiceCode"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="发票名称"
          prop="invoiceName"
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
        <el-table-column
          align="center"
          label="发票类型"
          prop="invoiceType"
          show-overflow-tooltip
        >
          <template #default="{ row }">{{
            getDictLabel(DICT_TYPE.INVOICE_TYPE, row.invoiceType)
          }}</template>
        </el-table-column>
        <el-table-column
          align="center"
          label="发票金额"
          prop="invoiceAmount"
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
import { invoiceHolderPage } from '@/api/home/index'
import { cloneDeep } from 'lodash-es'
import { formatTime } from '@/utils/index'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'InvoiceDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const index = ref(0)
const loading = ref(false)
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})

const emit = defineEmits<{
  sure
}>()
let idList = ref<any[]>([])
const getList = async () => {
  loading.value = true
  try {
    let res = await invoiceHolderPage({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      status: 0,
      idList: idList.value
    })
    tableData.value = res.list
    pageForm.total = res.total
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  }
}

const open = async (list, num) => {
  idList.value = []
  list.forEach((item: any) => {
    if (item.sourceId) {
      idList.value.push(item.sourceId)
    }
  })
  index.value = num
  parentList.value = cloneDeep(list || [])
  selectedDiaData.value = cloneDeep(list.filter((item) => item.sourceId) || [])
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item: any) => {
      parentList.value.forEach((el: any) => {
        if (item.id == el.sourceId) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}

const selectedDiaData = ref<any[]>([])
const handleSelectionChange = (selection, row, isAll = false) => {
  if (parentList.value.length > 0) {
    let idList: any[] = []
    selectedDiaData.value.forEach((item) => {
      idList.push(item.sourceId || item.id)
    })
    let rowIndex = idList.findIndex((item) => item === row.id)
    if (isAll) {
      if (!idList.includes(row.id)) {
        selectedDiaData.value.push(row)
      }
    } else {
      if (idList.includes(row.id)) {
        selectedDiaData.value.splice(rowIndex, 1)
      } else {
        selectedDiaData.value.push(row)
      }
    }
  } else {
    selectedDiaData.value = selection
  }
}

const handleSelectionAllChange = (selection) => {
  if (isValidArray(selection)) {
    selection.forEach((item) => {
      handleSelectionChange(selection, item, true)
    })
  } else {
    selectedDiaData.value = []
  }
}

const handleCancel = () => {
  tableData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}

const getMaxItemByProperty = (arr: any[], property: string): any => {
  if (!arr || arr.length === 0) {
    return null
  }

  return arr.reduce((maxItem, currentItem) => {
    const currentValue = Number(currentItem[property])
    const maxValue = Number(maxItem?.[property])

    // 处理无效数值的情况
    if (isNaN(currentValue)) return maxItem
    if (maxItem === null || isNaN(maxValue)) return currentItem

    return currentValue > maxValue ? currentItem : maxItem
  }, null)
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  if (len > 0) {
    let list: any[] = []
    let amountToTal = 0
    // let ids = parentList.value.map((item: any) => item.id)
    let maxItem = getMaxItemByProperty(selectedDiaData.value, 'invoiceAmount')
    selectedDiaData.value.forEach((item) => {
      amountToTal += Number(item.invoiceAmount)
      if (item.sourceId) {
        list.push(item)
      } else {
        list.push({
          ...item.invoice[0],
          sourceId: item.id
        })
      }
    })
    let otherList = parentList.value.filter((item: any) => item.sourceId == null)
    list = [...list, ...otherList]
    emit('sure', list, amountToTal, index.value, maxItem.dictSubjectId)
    handleCancel()
  } else {
    message.warning('请选择发票')
  }
}
defineExpose({ open })
</script>
