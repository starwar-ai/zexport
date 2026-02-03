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
        @click="handleCreate"
        v-hasPermi="['wms:notice-out:create']"
      >
        手动创建
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }"> <TtnoticeDetail :id="key" /></template>

    <template #edit="{ key }">
      <TtnoticeCreateForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #create>
      <TtnoticeCreateForm mode="create" />
    </template>
  </eplus-dialog>

  <ToBillDia
    ref="toBillDiaRef"
    @sure="handleRefresh"
  />
</template>

<script setup lang="tsx">
import TtnoticeDetail from './TtnoticeDetail.vue'
import { columnWidth, formatDictColumn } from '@/utils/table'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as RenoticeApi from '@/api/wms/renotice'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import TtnoticeCreateForm from './TtnoticeCreateForm.vue'
import ToBillDia from '@/views/dms/containerNotice/ToBillDia.vue'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { formatNum } from '@/utils'
import { WarehouseStatusEnum } from '@/utils/constants'
import { volPrecision } from '@/utils/config'
import { getDictLabel } from '@/utils/dict'

const name = 'TTNotice'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({
  name
})

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '通知单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'saleContractCode',
      label: '销售合同号'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'noticeTime',
          label: '通知时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await RenoticeApi.getNoticePage({
      isContainerTransportation: 0,
      noticeType: 2,
      ...ps
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  columns: [
    {
      prop: 'code',
      label: '通知单号',
      minWidth: columnWidth.m,
      parent: true,
      isHyperlink: true
    },
    {
      prop: 'noticeStatus',
      label: '出库状态',
      minWidth: columnWidth.m,
      parent: true,
      formatter: formatDictColumn(DICT_TYPE.NOTICE_STATUS)
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      parent: true,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row.auditStatus)
      }
    },
    {
      prop: 'skuCode',
      label: '产品编号',
      minWidth: columnWidth.m
    },
    {
      prop: 'saleContractCode',
      label: '销售合同',
      minWidth: columnWidth.m
    },
    {
      prop: 'skuName',
      label: '产品名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'cskuCode',
      label: '客户货号',
      minWidth: columnWidth.m
    },
    {
      prop: 'basicSkuCode',
      label: '基础产品编号',
      minWidth: columnWidth.l
    },
    {
      prop: 'venderName',
      label: '供应商',
      minWidth: columnWidth.m
    },

    {
      prop: 'orderQuantity',
      label: '应出数量',
      minWidth: columnWidth.m
    },
    {
      prop: 'shippedAddress',
      label: '发货地点',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.SHIPPED_ADDRESS)
    },
    {
      prop: 'orderBoxQuantity',
      label: '应出箱数',
      minWidth: columnWidth.m
    },
    {
      prop: 'pendingStockQuantity',
      label: '待出库数量',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div>
              {row.pendingStockQuantity == null ? row.orderQuantity : row.pendingStockQuantity}
            </div>
          )
        }
      }
    },

    {
      prop: 'totalWeight',
      label: '总毛重kg',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return <div>{row.totalWeight?.weight ? formatNum(row.totalWeight?.weight, 2) : ''}</div>
        }
      }
    },
    {
      prop: 'totalVolume',
      label: '总体积m³',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return <div>{formatNum(row.totalVolume / 1000000, volPrecision)}</div>
        }
      }
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: columnWidth.xl,
      parent: true,
      slots: {
        default: (data) => {
          const { row } = data
          // let detail =
          return (
            <div class="flex items-center">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                v-hasPermi="wms:notice-out:query"
              >
                详情
              </el-button>
              <eplus-dropdown
                otherItems={[
                  {
                    isShow:
                      row.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status &&
                      [0, 3, 4].includes(row.auditStatus),
                    otherKey: 'wmsNoticeOutUpdate',
                    label: '编辑',
                    permi: 'wms:notice:update',
                    handler: async (row) => {
                      handleUpdate(row.id)
                    }
                  },
                  {
                    isShow:
                      (row.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status ||
                        row.noticeStatus == WarehouseStatusEnum.PARTIAL_TRANSFERRED.status) &&
                      row.auditStatus == 2 &&
                      row.pendingStockQuantity > 0 &&
                      checkConvert(row, 2),
                    otherKey: 'wmsNoticeOutConvert',
                    label: '转出库单',
                    permi: 'wms:notice-out:convert',
                    handler: async (row) => {
                      await handleToBill(row, 2)
                    }
                  },
                  {
                    isShow:
                      [
                        WarehouseStatusEnum.TRANSFERRED_IN_PROGRESS.status,
                        WarehouseStatusEnum.UNTRANSFERRED.status,
                        WarehouseStatusEnum.PARTIAL_TRANSFERRED.status
                      ].includes(row.noticeStatus) &&
                      row.auditStatus == 2 &&
                      row.pendingStockQuantity > 0 &&
                      checkConvert(row, 1),
                    otherKey: 'wmsNoticeOutFactory',
                    label: '工厂出库',
                    permi: 'wms:notice-out:factory',
                    handler: async (row) => {
                      await handleToBill(row, 1)
                    }
                  },
                  {
                    isShow:
                      row.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status &&
                      ![1, 10].includes(row.auditStatus),
                    otherKey: 'wmsNoticeOutClose',
                    label: '作废',
                    permi: 'wms:notice:close',
                    handler: async (row) => {
                      await handleClose(row.id)
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
const exportFileName = ref('')

const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const checkConvert = (row, type) => {
  let list = row.children.filter(
    (item) => item.convertBillFlag === 0 && item.shippedAddress === type
  )
  return list.length > 0
}

const toBillDiaRef = ref()
const handleToBill = async (row, type) => {
  // await message.confirm('确认该出库通知单转出库单吗？')
  // let res = await RenoticeApi.toBill(id)
  // message.notifyPushSuccess('转出库单成功', res, 'ckbill')
  // handleRefresh()
  toBillDiaRef.value.open(row, type)
}

const handleClose = async (id: number) => {
  await message.confirm('确认作废吗？')
  await RenoticeApi.closeApi(id)
  message.success('作废成功')
  handleRefresh()
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

onMounted(() => {})

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
