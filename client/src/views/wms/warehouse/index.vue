<template>
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-change="handleTabsClick"
    >
      <el-tab-pane
        label="本地仓"
        :name="0"
      />
      <el-tab-pane
        label="供应仓"
        :name="1"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
  >
    <template #tableActions>
      <el-button
        v-if="activeName == 0"
        type="primary"
        @click="handleCreate()"
        v-hasPermi="['wms:warehouse:create']"
      >
        新增
      </el-button>
    </template>
  </eplus-table>

  <WarehouseForm
    ref="WarehouseFormRef"
    @sure="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { columnWidth, formatDateColumn } from '@/utils/table'
import * as WarehouseApi from '@/api/wms/warehouse/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import WarehouseForm from './WarehouseForm.vue'

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'Warehouse' })
const activeName = ref(0)
const handleTabsClick = () => {
  eplusListRef.value.handleSearch()
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'name',
      label: '仓库名称'
    },
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '仓库编号'
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await WarehouseApi.getWarehousePage({ ...ps, venderFlag: activeName.value })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await WarehouseApi.deleteWarehouse(id)
  },
  columns: [
    {
      prop: 'code',
      label: '仓库编号',
      minWidth: columnWidth.m
    },
    {
      prop: 'name',
      label: '仓库名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'defaultFlag',
      label: '是否默认仓库',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return row.defaultFlag == 1 ? '是' : '否'
        }
      }
    },
    {
      prop: 'address',
      label: '仓库地址',
      minWidth: columnWidth.m
    },
    {
      prop: 'enableFlag',
      label: '仓库状态',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return row.enableFlag == 1 ? '启用' : '停用'
        }
      }
    },
    {
      prop: 'managerName',
      label: '仓管员',
      minWidth: columnWidth.m
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: columnWidth.xl
    },
    {
      prop: 'code',
      label: '创建人',
      minWidth: columnWidth.m
    },
    {
      prop: 'creatorName',
      label: '创建人',
      minWidth: columnWidth.m
    },
    {
      prop: 'createTime',
      label: '创建时间',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: columnWidth.l,
      fixed: 'right',
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div class="flex items-center justify-start">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleUpdate(row)
                }}
                hasPermi="['wms:warehouse:update']"
              >
                编辑
              </el-button>

              <el-button
                link
                type="primary"
                onClick={() => {
                  statusChange(row)
                }}
                hasPermi="['wms:warehouse:update']"
              >
                {row.enableFlag == 1 ? '停用' : '启用'}
              </el-button>
              <el-button
                link
                type="primary"
                onClick={() => {
                  setDefault(row)
                }}
                hasPermi="['wms:warehouse:update']"
              >
                {row.defaultFlag == 0 && row.enableFlag == 1 ? '设为默认仓库' : ''}
              </el-button>
            </div>
          )
        }
      }
    }
  ]
}
const WarehouseFormRef = ref()
const exportFileName = ref('产品管理.xlsx')
const handleExport = async () => {
  return await eplusListRef.value.exportList('产品管理.xlsx')
}

const handleUpdate = (row) => {
  WarehouseFormRef.value?.open('edit', row)
}
const handleCreate = () => {
  WarehouseFormRef.value?.open('add')
}

const setDefault = async (row) => {
  WarehouseApi.setDefaultWarehouse(row.id)
    .then(() => {
      message.success(`已设置为默认仓库`)
      handleRefresh()
    })
    .catch(() => {})
}

const statusChange = async (row) => {
  let text = row.enableFlag == 1 ? '停用' : '启用'
  let type = row.enableFlag == 1 ? 'disable' : 'enable'
  ElMessageBox.confirm(`确认${text}选中数据吗？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      WarehouseApi.updateWarehouseStatus(type, row.id).then(() => {
        message.success(`${text}成功`)
        handleRefresh()
      })
    })
    .catch(() => {})
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
