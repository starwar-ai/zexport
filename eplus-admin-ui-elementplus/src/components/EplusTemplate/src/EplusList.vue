<template>
  <div
    class="border-b-[--el-border-color-light] border-b-solid bg-[var(--el-fill-color-blank)] px-15px pb-12px pt-12px"
  >
    <eplus-search
      :fields="eplusSearchSchema.fields"
      :moreFields="eplusSearchSchema.moreFields"
      :columns="eplusTableSchema.columns"
      @search="handleSearch"
      @reset="handleReset"
    />
    <!-- <search
      :schema="eplusSearchSchema.searchSchemas"
      :is-col="false"
      :show-tag="true"
      layout="inline"
      button-position="left"
      expand
      :expandField="eplusSearchSchema.expenderField"
      @search="handleSearch"
      @reset="handleSearch"
      @register="searchRegister"
    /> -->
  </div>

  <div class="table-pane flex flex-1 flex-col px-15px pb-15px">
    <Table
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      showAction
      :columns="eplusTableSchema.columns"
      headerAlign="left"
      align="left"
      :border="true"
      header-row-class-name="headerStyle"
      :data="tableObject.tableList"
      :loading="tableObject.loading"
      :pagination="{
        total: tableObject.total
      }"
      :scrollbar-always-on="true"
      @register="tableRegister"
      @refresh="tableMethods.refresh"
      height="250"
      :show-overflow-tooltip="true"
      @header-dragend="tableMethods.dragHeader"
    >
      <template #actionsLeft>
        <slot name="tableActions"></slot>
      </template>
    </Table>
  </div>
</template>

<script setup lang="tsx">
import { PropType } from 'vue'
// import { EplusSearchSchema } from '@/types/eplus'
// import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'

const props = defineProps({
  eplusSearchSchema: {
    type: Object as PropType<EplusSearchSchema>,
    required: true
  },
  eplusTableSchema: {
    type: Object as PropType<EplusTableSchema>,
    required: true
  }
})

// const { searchRegister } = useSearch()
/** 表格实例 */
const { tableRegister, tableMethods, tableObject } = useTable({
  /** 此处为列表关联api */
  getListApi: async (ps) => {
    const res = await props.eplusTableSchema.getListApi(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  //删除
  delListApi: async (id) => {
    await props.eplusTableSchema.delListApi(id)
  },
  //导出
  exportListApi: async (ps) => {
    return await props.eplusTableSchema.exportListApi(ps)
  }
})

const { refresh, getList, setSearchParams, delList, exportList } = tableMethods
console.log(tableMethods)
defineExpose({
  refresh,
  delList,
  exportList
})

const handleReset = () => {
  setSearchParams({})
}
const handleSearch = (model) => {
  setSearchParams(model)
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.table-pane {
  :deep(.el-scrollbar__bar.is-horizontal) {
    height: 10px;
  }

  :deep(.el-scrollbar) {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  :deep(.el-scrollbar__wrap) {
    flex: 1;
  }

  :deep(.el-table--border .el-table__cell) {
    border: none;
  }

  :deep(.el-table--border .el-table__cell .cell) {
    color: #0b1019;
    font-size: 12px;
  }
}
</style>
<style lang="scss">
.headerStyle .el-table__cell:nth-of-type(n + 2) .cell {
  border-left: 1px solid #d9dbde;
}
</style>
