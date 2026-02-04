<template>
  <Dialog
    ref="dialogRef"
    v-model="dialogVisible"
    destroy-on-close
  >
    <el-form
      :model="searchFormData"
      inline
    >
      <el-form-item
        v-for="field in searchFieldSchema"
        :key="field.name"
        :label="field.label"
      >
        <component
          v-if="field.component"
          :is="field.component"
          v-model="searchFormData[field.name]"
        />
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          @click="handleSearch"
        >
          搜索
        </el-button>
      </el-form-item>
    </el-form>

    <el-table
      :border="true"
      :data="searchTableData"
    >
      <el-table-column type="selection" />
      <el-table-column
        v-for="column in searchTableSchema"
        @selection-change="handleSelectionChange"
        :key="column.field"
        :prop="column.field"
        :label="column.label"
        :min-width="column.minWidth"
        :formatter="column.formatter"
        :fixed="column.fixed"
      />
    </el-table>

    <div class="flex flex-row justify-between items-center pt-2">
      <el-pagination
        small
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="total"
      />

      <div>
        <el-button
          type="primary"
          @click="handleConfirm"
          >确定</el-button
        >
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </div>
  </Dialog>
</template>
<script setup lang="tsx">
import { useDesign } from '@/hooks/web/useDesign'
import type { EplusSearchDialogProps } from './types'

const { getPrefixCls } = useDesign()
const emit = defineEmits<{
  confirm: Array<any>
  cancel
}>()
const props = defineProps<EplusSearchDialogProps>()
const dialogVisible = ref(false)
const currentPage = ref(1) // Define currentPage
const pageSize = ref(10) // Define pageSize
const total = ref(0) // Define total
const dialogRef = ref()
const prefixCls = getPrefixCls('eplus-search-dialog')

const searchTableData = ref<Array<{ id: number }>>([]) // Define searchResult
const searchFormData = ref({})
const search = async () => {
  const result = await props.searchApi({
    currentPage: currentPage.value,
    pageSize: pageSize.value,
    ...searchFormData
  })
  searchTableData.value = result.list
  total.value = result.total
}

const open = async () => {
  dialogVisible.value = true
  clear()
  await search()
}

defineExpose({
  open
})
const selection = ref<Array<{ id: number }>>([])
const handleSelectionChange = (newSelection) => {
  selection.value = newSelection
}

const handleSearch = () => {
  search()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  search()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  search()
}

const handleConfirm = () => {
  clear()
  emit('confirm', selection)
  dialogVisible.value = false
}

const handleCancel = () => {
  // Implement cancel logic here
  dialogVisible.value = false
  clear()
  emit('cancel')
}

const clear = () => {
  searchTableData.value = []
  searchFormData.value = {}
}
</script>
<style lang="scss">
$prefix-cls: #{$namespace}-eplus-search-dialog;
</style>
