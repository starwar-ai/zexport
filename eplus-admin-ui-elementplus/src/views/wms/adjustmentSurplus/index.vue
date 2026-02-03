<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  />

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <AdjustmentSurplusDetail :id="key" /></template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn } from '@/utils/table'
import * as StocktakeApi from '@/api/wms/stocktake/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import AdjustmentSurplusDetail from './AdjustmentSurplusDetail.vue'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上
const eplusListRef = ref()
defineOptions({ name: 'AdjustmentSurplus' })

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '盘盈单号'
    },
    {
      component: <el-input></el-input>,
      name: 'warehouseName',
      label: '盘点仓库'
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await StocktakeApi.adjustmentPage({
      ...ps,
      adjustmentType: 1
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  columns: [
    {
      prop: 'code',
      label: '盘盈单号',
      minWidth: columnWidth.m
    },
    {
      prop: 'stocktakeUserName',
      label: '盘点人',
      minWidth: columnWidth.m
    },

    {
      prop: 'adjustmentDate',
      label: '盘点日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn()
    },
    {
      prop: 'warehouseName',
      label: '盘点仓库',
      minWidth: columnWidth.m
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: columnWidth.l
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
                hasPermi="['pms:sku:query']"
                class="mr10px"
              >
                详情
              </el-button>
            </div>
          )
        }
      }
    }
  ]
}

/// 打开详情
const handleDetail = async (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
