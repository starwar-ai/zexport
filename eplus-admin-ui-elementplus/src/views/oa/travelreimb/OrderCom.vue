<template>
  <div class="mb15px">
    <el-select
      v-model="val"
      placeholder="请选择"
      class="mr15px"
    >
      <el-option
        label="去关联（已录入订单）"
        value="1"
      />
      <el-option
        label="暂不关联(未录入订单，录入后在关联)"
        value="2"
      />
    </el-select>
    <template v-if="val == 1">
      <el-button
        type="primary"
        @click="handleAdd"
        >关联订单</el-button
      >
      <el-button>移除订单</el-button>
    </template>
  </div>
  <Table
    v-if="val == 1"
    :columns="columns"
    headerAlign="center"
    align="center"
    :data="tableList"
  />

  <Dialog
    v-model="dialogTableVisible"
    title="关联订单"
    width="800"
    append-to-body
  >
    <Table
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :pagination="{
        total: tableObject.total
      }"
      :columns="dialogColumns"
      headerAlign="center"
      align="center"
      :data="tableList"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogTableVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="dialogTableVisible = false"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'

defineOptions({ name: 'CostAttrsCom' })
const tableList = ref([])
const val = ref('')
const { tableObject } = useTable()
const columns = reactive<TableColumn[]>([
  {
    type: 'selection'
  },
  {
    field: 'id',
    label: '订单号'
  },
  {
    field: 'purpose',
    label: '创建时间'
  },
  {
    field: 'dest',
    label: '订单类型'
  },
  {
    field: 'companions',
    label: '客户名称'
  },
  {
    field: 'companions',
    label: '订单金额'
  },
  {
    field: 'companions',
    label: '报销费用分摊（100%）'
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
    fixed: 'right',
    align: 'left',
    slots: {
      default: (data) => {
        const { row } = data

        return (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
            >
              移除
            </el-button>
          </div>
        )
      }
    }
  }
])

const dialogColumns = reactive<TableColumn[]>([
  {
    type: 'selection'
  },
  {
    field: 'id',
    label: '订单号'
  },
  {
    field: 'purpose',
    label: '创建时间'
  },
  {
    field: 'dest',
    label: '订单类型'
  },
  {
    field: 'companions',
    label: '客户名称'
  },
  {
    field: 'companions',
    label: '金额'
  }
])

const dialogTableVisible = ref(false)

const handleAdd = () => {
  dialogTableVisible.value = true
}
onMounted(async () => {})
</script>
