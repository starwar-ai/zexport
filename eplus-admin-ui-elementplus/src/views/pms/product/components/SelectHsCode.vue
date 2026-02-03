<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择海关编码"
    width="800"
    append-to-body
    align-center
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
      style="height: 350px"
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
import { formatNumColumn } from '@/utils/table'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { getHsdataPage } from '@/api/system/hsdata'

const message = useMessage()
defineOptions({ name: 'SelectHsCode' })
// const props = defineProps<{
//   ownFlag
// }>()

const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await getHsdataPage({ ...ps })
    return {
      list: res.list,
      total: res.total
    }
  }
})

const { getList, setSearchParams } = tableMethods

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '海关编码'
    },
    {
      component: <el-input></el-input>,
      name: 'name',
      label: '名称'
    }
  ],
  moreFields: []
}

const open = async () => {
  dialogTableVisible.value = true
  await getList()
}

const handleSearch = (model) => {
  setSearchParams(
    {
      ...model
    },
    []
  )
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
            label={row.id}
          >
            &nbsp;
          </el-radio>
        )
      }
    }
  },
  {
    field: 'code',
    label: '海关编码',
    minWidth: '100px'
  },
  {
    field: 'name',
    label: '名称',
    minWidth: '100px'
  },
  {
    field: 'unit',
    label: '海关计量单位',
    minWidth: '100px'
  },
  {
    field: 'taxRefundRate',
    label: '退税率',
    minWidth: '100px',
    formatter: formatNumColumn()
  }
])

const dialogTableVisible = ref(false)

let radioobj = reactive({
  val: undefined
})
const getRow = (row: any) => {
  radioobj.val = row.id
  selectedDiaData.value = row
}

const selectedDiaData = ref()

const emit = defineEmits(['sure'])
const handleSure = async () => {
  if (selectedDiaData.value?.id) {
    await emit('sure', selectedDiaData.value)
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  tableObject.tableList = []
  radioobj.val = undefined
  selectedDiaData.value = []
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
