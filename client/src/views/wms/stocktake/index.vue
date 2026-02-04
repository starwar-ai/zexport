<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        plain
        @click="handleCreate()"
        v-hasPermi="['wms:stocktake:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <StocktakeDetail :id="key" /></template>

    <template #edit="{ key }">
      <StocktakeForm
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <StocktakeForm mode="create" />
    </template>
    <template v-if="AdjustFlag">
      <AdjustForm :id="AdjustId" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
// import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as StocktakeApi from '@/api/wms/stocktake/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import StocktakeDetail from './StocktakeDetail.vue'
import StocktakeForm from './StocktakeForm.vue'
import AdjustForm from './AdjustForm.vue'
import { getSourceId, removeSourceId } from '@/utils/auth'
// import * as UserApi from '@/api/system/user'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const AdjustFlag = ref(false)
const AdjustId = ref('')
const message = useMessage()
const eplusListRef = ref()
defineOptions({ name: 'StocktakeIndex' })

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '盘点单号'
    },
    {
      component: <el-input></el-input>,
      name: 'warehouseName',
      label: '盘点仓库'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.STOCK_TAKE}></eplus-dict-select>,
      name: 'stocktakeStatus',
      label: '盘点状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.STOCK_TAKE, args[0])
      }
    },
    {
      component: (
        <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}></eplus-dict-select>
      ),
      name: 'auditStatus',
      label: '审核状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
      }
    }
  ],
  moreFields: []
}
const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await StocktakeApi.getStocktakePage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await StocktakeApi.deleteStocktake(id)
  },
  exportListApi: async (ps) => {
    return await StocktakeApi.exportStocktake(ps)
  },
  columns: [
    {
      prop: 'code',
      label: '盘点单号',
      minWidth: columnWidth.l,
      isHyperlink: true
    },
    {
      prop: 'warehouseName',
      label: '盘点仓库',
      minWidth: columnWidth.l
    },
    {
      prop: 'planDate',
      label: '预计盘点日期',
      minWidth: columnWidth.l,
      formatter: formatDateColumn()
    },
    {
      prop: 'stocktakeStatus',
      label: '盘点状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.STOCK_TAKE)
    },
    {
      prop: 'stocktakeUserName',
      label: '盘点人',
      minWidth: columnWidth.m
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: columnWidth.l
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
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
            <div class="flex items-center">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="wms:stocktake:query"
                class="mr10px"
              >
                详情
              </el-button>
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: row.auditStatus == 0 && row.stocktakeStatus == 1,
                    otherKey: 'stocktakeCounting',
                    label: '开始盘点',
                    permi: 'wms:stocktake:counting',
                    handler: async (row) => {
                      await handleCounting(row.id)
                    }
                  },
                  {
                    isShow: row.auditStatus == 0 && row.stocktakeStatus == 2,
                    otherKey: 'stocktakeComplete',
                    label: '盘点录入',
                    permi: 'wms:stocktake:complete',
                    handler: async (row) => {
                      await handleComplete(row.id)
                    }
                  },
                  {
                    isShow: row.auditStatus == 0 && row.stocktakeStatus == 2,
                    otherKey: 'stocktakeSubmit',
                    label: '完成盘点',
                    permi: 'wms:stocktake:submit',
                    handler: async (row) => {
                      await handleSubmit(row)
                    }
                  },
                  {
                    isShow: row.auditStatus == 0 && row.stocktakeStatus == 1,
                    otherKey: 'stocktakeUpdate',
                    label: '编辑',
                    permi: 'wms:stocktake:update',
                    handler: async (row) => {
                      handleUpdate(row.id)
                    }
                  },
                  {
                    isShow: row.auditStatus == 0 && row.stocktakeStatus == 1,
                    otherKey: 'stocktakeDelete',
                    label: '删除',
                    permi: 'wms:stocktake:delete',
                    handler: async (row) => {
                      await handleDelete(row.id)
                    }
                  }
                ]}
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    }
  ]
}

const exportFileName = ref('产品管理.xlsx')
const handleExport = async () => {
  return await eplusListRef.value.exportList('产品管理.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await StocktakeApi.submitStocktake(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  AdjustId.value = ''
  AdjustFlag.value = false
  eplusDialogRef.value?.openEdit(id)
}
/// 打开详情
const handleDetail = async (id: number) => {
  AdjustId.value = ''
  AdjustFlag.value = false
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  AdjustId.value = ''
  AdjustFlag.value = false
  eplusDialogRef.value?.openCreate()
}

const handleCounting = (id) => {
  ElMessageBox.confirm('确认开始盘点吗？', '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确认',
    type: 'warning'
  })
    .then(async () => {
      await StocktakeApi.stocktakeCounting({ id: id })
      message.success('操作成功')
      eplusListRef.value.handleRefresh()
    })
    .catch(() => {})
  //
}

const handleComplete = (id) => {
  AdjustId.value = id
  AdjustFlag.value = true
  eplusDialogRef.value?.open('盘点结果录入', id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
