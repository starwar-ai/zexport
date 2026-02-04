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
        @click="handleCreate()"
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
    <template #detail="{ key }"> <RenoticeDetail :id="key" /></template>

    <template #create>
      <RenoticeCreateForm mode="create" />
    </template>

    <template #edit="{ key }">
      <RenoticeCreateForm
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import RenoticeDetail from './RenoticeDetail.vue'
import RenoticeCreateForm from './RenoticeCreateForm.vue'
import {
  columnWidth,
  formatDateColumn,
  formatDictColumn,
  formatNumColumn,
  formatWeightColumn
} from '@/utils/table'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as RenoticeApi from '@/api/wms/renotice'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { WarehouseStatusEnum } from '@/utils/constants'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { formatNum } from '@/utils'

const name = 'Renotice'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()
const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

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
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'saleContractCode',
      label: '销售合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'venderName',
      label: '供应商名称'
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
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'expectDate',
          label: '预计到货日期',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaserId',
      label: '采购员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择采购员部门"></eplus-dept-select>,
      name: 'purchaserDeptId',
      label: '采购员部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await RenoticeApi.getNoticePage({ ...ps, noticeType: 1 })

    return {
      list: res.list,
      total: res.total,
      sum: {
        totalVolume: formatNum(res?.summary?.totalVolumeSum / 1000000, 6)
      }
    }
  },
  summary: true,
  columns: [
    {
      prop: 'companyName',
      label: '归属公司',
      minWidth: columnWidth.m
    },
    {
      prop: 'code',
      label: '通知单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'purchaseContractCode',
      label: '采购单号',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return [...new Set(row.children.map((e) => e.purchaseContractCode))].join(',')
        }
      }
    },
    {
      prop: 'purchaseUserName',
      label: '采购员',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return [...new Set(row.children.map((e) => e.purchaseUserName))].join(',')
        }
      }
    },
    {
      prop: 'purchaseUserDeptName',
      label: '采购员部门',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return [...new Set(row.children.map((e) => e.purchaseUserDeptName))].join(',')
        }
      }
    },
    {
      prop: 'manager',
      label: '跟单员（部门）',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return row.stockNoticeItemRespVOList
          ?.map((item) => {
            if (item.manager?.deptName && item.manager?.nickname) {
              return `${item.manager.nickname}(${item.manager.deptName})`
            } else if (item.sales?.nickname) {
              return item.sales?.nickname
            } else {
              return ''
            }
          })
          .join(',')
      }
    },
    {
      prop: 'venderName',
      label: '供应商',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return [
          ...new Set(
            row.children.map((item) => {
              return item.venderName
            })
          )
        ].join(',')
      }
    },
    {
      prop: 'expectDate',
      label: '预计到货日期',
      minWidth: columnWidth.l,
      formatter: formatDateColumn()
    },

    {
      prop: 'totalWeight',
      label: '总毛重',
      minWidth: columnWidth.m,
      formatter: formatWeightColumn()
    },
    {
      prop: 'totalVolume',
      label: '总体积(m³)',
      minWidth: columnWidth.l,
      summary: true,
      formatter: (row) => {
        return formatNum(row.totalVolume / 1000000, 6)
      }
    },
    {
      prop: 'noticeStatus',
      label: '入库状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.NOTICE_STATUS)
    },
    {
      prop: 'transformStockQuantity',
      label: '已入库数量',
      minWidth: columnWidth.l,
      formatter: formatNumColumn()
    },
    // {
    //   prop: 'purchaseContractCode',
    //   label: '采购合同号',
    //   minWidth: '120px'
    // },
    // {
    //   prop: 'purchaserUserName',
    //   label: '采购员',
    //   minWidth: '120px'
    // },
    {
      prop: 'noticeTime',
      label: '通知时间',
      minWidth: columnWidth.l,
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
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
                hasPermi="['wms:notice:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                otherItems={[
                  {
                    isShow:
                      row.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status &&
                      [0, 3, 4].includes(row.auditStatus),
                    otherKey: 'noticeUpdate',
                    label: '编辑',
                    permi: 'wms:notice:update',
                    handler: async (row) => {
                      handleUpdate(row.id)
                    }
                  },
                  {
                    isShow:
                      row.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status &&
                      row.auditStatus == 2,
                    otherKey: 'noticeChange',
                    label: '转入库单',
                    permi: 'wms:notice:convert',
                    handler: async (row) => {
                      handleToBill(row.id)
                    }
                  },
                  {
                    isShow:
                      row.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status &&
                      ![1, 10].includes(row.auditStatus),
                    otherKey: 'noticeClose',
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

const handleCreate = () => {
  eplusDialogRef.value?.openCreate('手动创建通知单')
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '入库通知单')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleToBill = async (id: number) => {
  await message.confirm('确认转入库单吗？')
  let res = await RenoticeApi.toBill(id)
  message.notifyPushSuccess('转入库单成功', res, 'ysbill')
  handleRefresh()
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

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
