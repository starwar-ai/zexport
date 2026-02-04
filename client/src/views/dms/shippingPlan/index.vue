<template>
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-change="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        :name="4"
      />
      <el-tab-pane
        label="待提交"
        :name="0"
      />
      <el-tab-pane
        label="待转明细"
        :name="1"
      />
      <el-tab-pane
        label="已转明细"
        :name="2"
      />
      <el-tab-pane
        label="已作废"
        :name="3"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <!-- <el-button
        v-if="activeName == 1"
        type="primary"
        plain
        v-hasPermi="['dms:shipment:transform']"
        @click="handleTransformShipment(null)"
      >
        转出运明细
      </el-button> -->
      <el-button
        v-if="activeName == 1"
        type="primary"
        plain
        v-hasPermi="['dms:shipment:transform']"
        @click="batchMergeTransformShipment"
      >
        批量合并下推
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <ShippingPlanDetail :id="key" />
    </template>
    <template #edit="{ key }">
      <ShippingPlanForm
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <ShippingPlanForm mode="create" />
    </template>
  </eplus-dialog>

  <CloseDia
    ref="CloseDiaRef"
    @sure="handleRefresh"
  />

  <AppendDia
    ref="AppendDiaRef"
    @sure="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatNumColumn } from '@/utils/table'
import * as ShippingPlanApi from '@/api/dms/shippingPlan/index'
import { getcompanySimpleList } from '@/api/common/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import ShippingPlanDetail from './ShippingPlanDetail.vue'
import ShippingPlanForm from './ShippingPlanForm.vue'
import CloseDia from './components/CloseDia.vue'
import AppendDia from './components/AppendDia.vue'
import { ShipmentPlanStatusEnum } from '@/utils/constants'
import download from '@/utils/download'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { isValidArray } from '@/utils/is'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const eplusListRef = ref()
defineOptions({ name: 'ShippingPlan' })
const activeName = ref(4)
const handleTabsClick = () => {
  eplusListRef.value.handleSearch()
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      name: 'foreignTradeCompanyId',
      label: '归属公司',
      component: (
        <eplus-custom-select
          api={getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择"
          style="width:100%"
        />
      )
    },
    {
      component: <el-input></el-input>,
      name: 'saleContractCode',
      label: '外销合同号'
    },
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '计划单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    }
  ],
  moreFields: []
}
const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ShippingPlanApi.getShipmentPlanPage({
      ...ps,
      status: activeName.value == 4 ? undefined : activeName.value
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  selection: true,
  showTabs: true,
  tabs: [
    {
      label: '产品',
      selection: true,
      isTree: true,
      columns: [
        {
          prop: 'code',
          label: '计划单号',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '归属公司',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'custDeliveryDate',
          label: '客户交期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn(),
          parent: true
        },
        {
          prop: 'departurePortName',
          label: '出运口岸',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'destinationPortName',
          label: '目的口岸',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'status',
          label: '单据状态',
          minWidth: columnWidth.m,
          parent: true,
          formatter: (row) => {
            return Object.values(ShipmentPlanStatusEnum).find((item) => {
              return row.status == item.status
            })?.name
          }
        },
        {
          prop: 'inputUser',
          label: '录入人',
          minWidth: columnWidth.m,
          parent: true,
          slots: {
            default: (row) => {
              return `${row?.inputUser?.nickname}`
            }
          }
        },

        {
          prop: 'inputDate',
          label: '录入日期',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDateColumn()
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: columnWidth.l,
          fixed: 'right',
          parent: true,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="flex items-center justify-start">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['dms:shipment-plan:query']"
                  >
                    详情
                  </el-button>

                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: row.status == ShipmentPlanStatusEnum.WAITING_PROCESS.status,
                        otherKey: 'ShipmentTransform',
                        label: '转出运明细',
                        permi: 'dms:shipment:transform',
                        handler: async (row) => {
                          handleTransformShipment(row?.children.map((item) => item.id).join(','))
                        }
                      },
                      {
                        isShow: row.status == ShipmentPlanStatusEnum.WAITING_PROCESS.status,
                        otherKey: 'append',
                        label: '追加',
                        permi: 'dms:shipment:transform',
                        handler: async (row) => {
                          handleAppend(row)
                        }
                      },
                      {
                        isShow: row.status == ShipmentPlanStatusEnum.WAITING_SUBMIT.status,
                        otherKey: 'shipmentPlanUpdate',
                        label: '编辑',
                        permi: 'dms:shipment-plan:update',
                        handler: async (row) => {
                          handleUpdate(row?.id)
                        }
                      },
                      {
                        isShow: [
                          ShipmentPlanStatusEnum.WAITING_PROCESS.status,
                          ShipmentPlanStatusEnum.WAITING_SUBMIT.status
                        ].includes(row.status),
                        otherKey: 'shipmentPlanClose',
                        label: '作废',
                        permi: 'dms:shipment-plan:close',
                        handler: async (row) => {
                          handleClose({ parentId: row.id })
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentPlanExport',
                        label: '明细导出',
                        permi: 'dms:shipment-plan:detail-export',
                        handler: async (row) => {
                          exportDetail(row)
                        }
                      }
                    ]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        },
        {
          prop: 'saleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'custPo',
          label: '客户po',
          minWidth: columnWidth.m
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'name',
          label: '产品名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'skuCode',
          label: '产品编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'cskuCode',
          label: '客户货号',
          minWidth: columnWidth.m
        },
        {
          prop: 'shippingQuantity',
          label: '出运数量',
          minWidth: columnWidth.m,
          formatter: formatNumColumn()
        },
        {
          prop: 'sales',
          label: '业务员',
          minWidth: columnWidth.m,
          slots: {
            default: ({ row }) => {
              return `${row?.sales?.nickname ? row?.sales?.nickname : ''}`
            }
          }
        },
        {
          prop: 'currency',
          label: '币种/汇率',
          minWidth: columnWidth.m
        },
        {
          prop: 'settlementName',
          label: '收款方式',
          minWidth: columnWidth.m
        },
        {
          prop: 'status',
          label: '状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.SHIPPING_PLAN_STATUS)
        },
        {
          prop: 'transformShipmentFlag',
          label: '是否已转出运',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.IS_INT)
        }
        // {
        //   prop: 'action',
        //   label: '操作',
        //   minWidth: columnWidth.l,
        //   fixed: 'right',
        //   slots: {
        //     default: (data) => {
        //       const { row } = data
        //       return row.status == ShipmentPlanStatusEnum.WAITING_PROCESS.status ? (
        //         <div class="flex items-center">
        //           <el-button
        //             link
        //             type="primary"
        //             onClick={() => {
        //               handleTransformShipment(row.id)
        //             }}
        //             hasPermi="['dms:forwarder-company-info:update']"
        //           >
        //             转出运明细
        //           </el-button>
        //           <el-button
        //             link
        //             type="primary"
        //             onClick={() => {
        //               handleClose({ itemId: row.id })
        //             }}
        //             hasPermi="['dms:forwarder-company-info:close']"
        //           >
        //             作废
        //           </el-button>
        //         </div>
        //       ) : (
        //         <div></div>
        //       )
        //     }
        //   }
        // }
      ]
    },
    {
      label: '单据',
      selection: true,
      isDefault: true,
      columns: [
        {
          prop: 'code',
          label: '计划单号',
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '归属公司',
          minWidth: columnWidth.m
        },
        {
          prop: 'saleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'custPo',
          label: '客户po',
          minWidth: columnWidth.m
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'custDeliveryDate',
          label: '客户交期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn()
        },
        {
          prop: 'departurePortName',
          label: '出运口岸',
          minWidth: columnWidth.m
        },
        {
          prop: 'destinationPortName',
          label: '目的口岸',
          minWidth: columnWidth.m
        },
        {
          prop: 'status',
          label: '单据状态',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return Object.values(ShipmentPlanStatusEnum).find((item) => {
              return row.status == item.status
            })?.name
          }
        },
        {
          prop: 'inputUser',
          label: '录入人',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return `${row?.inputUser?.nickname}`
            }
          }
        },
        {
          prop: 'inputDate',
          label: '录入日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn()
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
                      handleDetail(row.id)
                    }}
                    hasPermi="['dms:shipment-plan:query']"
                  >
                    详情
                  </el-button>

                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: row.status == ShipmentPlanStatusEnum.WAITING_PROCESS.status,
                        otherKey: 'ShipmentTransform',
                        label: '转出运明细',
                        permi: 'dms:shipment:transform',
                        handler: async (row) => {
                          handleTransformShipment(row?.children.map((item) => item.id).join(','))
                        }
                      },
                      {
                        isShow: row.status == ShipmentPlanStatusEnum.WAITING_PROCESS.status,
                        otherKey: 'append',
                        label: '追加',
                        permi: 'dms:shipment:transform',
                        handler: async (row) => {
                          handleAppend(row)
                        }
                      },
                      {
                        isShow: row.status == ShipmentPlanStatusEnum.WAITING_SUBMIT.status,
                        otherKey: 'shipmentPlanUpdate',
                        label: '编辑',
                        permi: 'dms:shipment-plan:update',
                        handler: async (row) => {
                          handleUpdate(row?.id)
                        }
                      },
                      {
                        isShow: [
                          ShipmentPlanStatusEnum.WAITING_PROCESS.status,
                          ShipmentPlanStatusEnum.WAITING_SUBMIT.status
                        ].includes(row.status),
                        otherKey: 'shipmentPlanClose',
                        label: '作废',
                        permi: 'dms:shipment-plan:close',
                        handler: async (row) => {
                          handleClose({ parentId: row.id })
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentPlanExport',
                        label: '明细导出',
                        permi: 'dms:shipment-plan:detail-export',
                        handler: async (row) => {
                          exportDetail(row)
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
  ]
}
const AppendDiaRef = ref()
const handleAppend = (row) => {
  AppendDiaRef.value.open(row)
}

const handleDetail = (id) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleUpdate = (id) => {
  eplusDialogRef.value?.openEdit(id)
}

const CloseDiaRef = ref()
const exportDetail = async (row) => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    const data = await ShippingPlanApi.exportShipmentPlanDetail({
      id: row.id,
      reportCode: 'dms-shipment-plan-detail',
      exportType: 'detail'
    })
    if (data && data.size) {
      download.excel(data, `明细${row.code}.xlsx`)
    }
  } catch {}
  return
}
const handleClose = async (obj) => {
  await message.confirm('确认将选中数据作废吗?')
  await ShippingPlanApi.closeShipmentPlan({
    ...obj,
    remark: '作废操作'
  })
  message.success('作废成功!')
  await handleRefresh()
  // CloseDiaRef.value.open(obj)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const batchMergeTransformShipment = async () => {
  let params = {}
  let list = eplusListRef.value?.checkedItems
  if (list.length > 0) {
    params = {
      ids: list
        .map((item) => {
          if (isValidArray(item.children)) {
            return item.children.map((el) => el.id)
          } else {
            return item.id
          }
        })
        .join(',')
    }
  } else {
    message.warning('请选择数据!')
    return
  }
  await message.confirm('确认将选中数据批量合并转出运明细吗?')
  let res = await ShippingPlanApi.batchMerge(params)
  message.notifyPushSuccess('转出运明细成功', res, 'shipment')
  await handleRefresh()
}

const handleTransformShipment = async (id) => {
  let params = {}
  if (id) {
    params = { ids: id }
  } else {
    let list = eplusListRef.value?.checkedItems
    if (list.length > 0) {
      params = {
        ids: list
          .map((item) => {
            if (isValidArray(item.children)) {
              return item.children.map((el) => el.id)
            } else {
              return item.id
            }
          })
          .join(',')
      }
    } else {
      message.warning('请选择数据!')
      return
    }
  }
  await message.confirm('确认将选中数据转出运明细吗?')
  let res = await ShippingPlanApi.transformShipment(params)
  message.notifyPushSuccess('转出运明细成功', res, 'shipment')
  await handleRefresh()
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
