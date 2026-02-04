<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
    backUrl="/dms/shipping-orders/commodityInspection"
  />
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <CommodityInspectionDetail :id="key" />
    </template>
    <template #edit="{ key }">
      <CommodityInspectionForm
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as CommodityInspectionApi from '@/api/dms/commodityInspection/index'
import { getcompanySimpleList } from '@/api/common/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import CommodityInspectionDetail from './CommodityInspectionDetail.vue'
import CommodityInspectionForm from './CommodityInspectionForm.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { useAppStore } from '@/store/modules/app'
import router from '@/router'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const eplusListRef = ref()
defineOptions({ name: 'CommodityInspection' })

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      name: 'foreignTradeCompanyName',
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
      name: 'code',
      label: '商检单号'
    },
    {
      component: <el-input></el-input>,
      name: 'invoiceCode',
      label: '发票号'
    },
    {
      component: <el-input></el-input>,
      name: 'saleContractCode',
      label: '外销合同号'
    },
    {
      component: <el-input></el-input>,
      name: 'custPo',
      label: '客户PO号'
    },
    {
      component: <el-input></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input></el-input>,
      name: 'venderName',
      label: '供应名称'
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await CommodityInspectionApi.getCommodityInspectionPage(ps)
    return {
      list: res.list || [{}],
      total: res.total
    }
  },
  delListApi: async (id) => {
    await CommodityInspectionApi.deleteCommodityInspection(id)
  },
  showTabs: true,
  tabs: [
    {
      label: '产品',
      isTree: true,
      columns: [
        {
          prop: 'code',
          label: '商检单号',
          parent: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'invoiceCode',
          label: '发票号',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '归属公司',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'shipDate',
          label: '出运日期',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDateColumn()
        },
        {
          prop: 'shipDate',
          label: '出运明细单号',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'tradeCountryName',
          label: '贸易国别',
          minWidth: columnWidth.m,
          parent: true,
          slots: {
            default: (row) => {
              return `${row.tradeCountryName} ${row.tradeCountryArea}`
            }
          }
        },
        {
          prop: 'destinationPortName',
          label: '目的口岸',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'forwarderCompanyName',
          label: '船代公司',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'transportType',
          label: '运输方式',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDictColumn(DICT_TYPE.TRANSPORT_TYPE)
        },
        {
          prop: 'invoiceCode',
          label: '发票号',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'saleContractCode',
          label: '外销合同',
          minWidth: columnWidth.m
        },
        {
          prop: 'name',
          label: '产品中文名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'nameEng',
          label: '产品英文名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'custName',
          label: '客户名称',
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
          minWidth: columnWidth.m
        },
        {
          prop: 'saleAmount',
          label: '销售金额',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.saleAmount?.amount && row.saleAmount ? (
                <EplusMoneyLabel
                  val={{
                    amount: row.saleAmount?.amount || 0,
                    currency: row.saleAmount?.currency
                  }}
                />
              ) : (
                '-'
              )
            }
          }
        },
        {
          prop: 'currency',
          label: '外销币种',
          minWidth: columnWidth.m
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'hsCode',
          label: 'HS编码',
          minWidth: columnWidth.m
        },
        {
          prop: 'hsMeasureUnit',
          label: '海关计量单位',
          formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
        },
        {
          prop: 'settlementTermType',
          label: '价格条款',
          minWidth: columnWidth.m
        },
        {
          prop: 'settlementName',
          label: '收款方式',
          minWidth: columnWidth.m
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
                <div class="flex items-center ">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['dms:commodity-inspection:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: true,
                        otherKey: 'CommodityInspectionUpdate',
                        label: '编辑',
                        permi: 'dms:commodity-inspection:update',
                        handler: async (row) => {
                          await handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'CommodityInspectionDelete',
                        label: '删除',
                        permi: 'dms:commodity-inspection:delete',
                        handler: async (row) => {
                          handleDelete(row?.id)
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
    },
    {
      label: '单据',
      isDefault: true,
      columns: [
        {
          prop: 'invoiceCode',
          label: '发票号',
          minWidth: columnWidth.m
        },
        {
          prop: 'code',
          label: '商检单号',
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
          label: '外销合同',
          showOverflowTooltip: true,
          minWidth: columnWidth.m,
          formatter: (row) => {
            return [...new Set(row.children.map((item) => item.saleContractCode))].join(',')
          }
        },
        {
          prop: 'custPo',
          label: '客户合同',
          showOverflowTooltip: true,
          minWidth: columnWidth.m,
          formatter: (row) => {
            return [...new Set(row.children.map((item) => item.custPo))].join(',')
          }
        },
        {
          prop: 'custName',
          label: '客户名称',
          showOverflowTooltip: true,
          minWidth: columnWidth.m,
          formatter: (row) => {
            return [...new Set(row.children.map((item) => item.custName))].join(',')
          }
        },
        {
          prop: 'departurePortName',
          label: '目的口岸',
          minWidth: columnWidth.m
        },
        {
          prop: 'transportType',
          label: '运输方式',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.TRANSPORT_TYPE)
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
                <div class="flex items-center ">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['dms:commodity-inspection:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: true,
                        otherKey: 'CommodityInspectionUpdate',
                        label: '编辑',
                        permi: 'dms:commodity-inspection:update',
                        handler: async (row) => {
                          await handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'CommodityInspectionDelete',
                        label: '删除',
                        permi: 'dms:commodity-inspection:delete',
                        handler: async (row) => {
                          handleDelete(row?.id)
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

const exportFileName = ref()
// const handleExport = async () => {
//   return await eplusListRef.value.exportList('船代公司.xlsx')
// }

const appStore = useAppStore()
const handleDetail = (id) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/dms/shipping-orders/commodityInspectionDetail/${id}`
    })
  }
}
const handleUpdate = (id) => {
  eplusDialogRef.value?.openEdit(id)
}
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
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
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
