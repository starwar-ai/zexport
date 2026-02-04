<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button @click="handleBatchDel()"> 批量删除 </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <SettlementFormDetail :id="key" />
    </template>
    <template #edit="{ key }">
      <SettlementFormMain
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
import * as SettlementFormApi from '@/api/dms/settlementForm/index'
import { getcompanySimpleList } from '@/api/common/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import SettlementFormDetail from './SettlementFormDetail.vue'
import SettlementFormMain from './SettlementFormMain.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { isValidArray } from '@/utils/is'
import { useAppStore } from '@/store/modules/app'
import router from '@/router'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const message = useMessage()
const eplusListRef = ref()
const exportFileName = ref() // 导出附件的名称，如果是空着，是因为当前页面没有导出，所以暂且空着，后续有了在加上

defineOptions({ name: 'SettlementForm' })

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
      label: '结汇单号'
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
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input></el-input>,
      name: 'custPo',
      label: '客合同户'
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await SettlementFormApi.getSettlementFormPage(ps)
    return {
      list: res?.list || [],
      total: res?.total || 0
    }
  },
  delListApi: async (id) => {
    await SettlementFormApi.delSettlement(id)
  },

  showTabs: true,
  tabs: [
    {
      label: '产品',
      isTree: true,
      selection: true,
      columns: [
        {
          prop: 'code',
          label: '结汇单号',
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
          prop: 'shipmentCode',
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
              return `${row.tradeCountryName}-${row.tradeCountryArea}`
            }
          }
        },
        {
          prop: 'departurePortName',
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
          prop: 'custPo',
          label: '客户合同',
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
                    hasPermi="['dms:forwarder-company-info:update']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: true,
                        otherKey: 'SettlementFormUpdate',
                        label: '编辑',
                        permi: 'dms:settlement-form:update',
                        handler: async (row) => {
                          await handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'dmsSettlementDelete',
                        label: '删除',
                        permi: 'dms:settlement-form:delete',
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
      selection: true,
      columns: [
        {
          prop: 'invoiceCode',
          label: '发票号',
          minWidth: columnWidth.m
        },
        {
          prop: 'code',
          label: '结汇单号',
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
                    hasPermi="['dms:forwarder-company-info:update']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: true,
                        otherKey: 'SettlementFormUpdate',
                        label: '编辑',
                        permi: 'dms:settlement-form:update',
                        handler: async (row) => {
                          await handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'dmsSettlementDelete',
                        label: '删除',
                        permi: 'dms:settlement-form:delete',
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

const appStore = useAppStore()
const handleDetail = (id) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/dms/shipping-orders/settlementFormDetail/${id}`
    })
  }
}

const handleUpdate = (id) => {
  eplusDialogRef.value?.openEdit(id)
}
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleBatchDel = async () => {
  let list = eplusListRef.value?.checkedItems
  if (isValidArray(list)) {
    let ids = list.map((item) => {
      if (!isValidArray(item.children)) {
        return item.parent?.id
      } else {
        return item.id
      }
    })
    await message.confirm('确认删除所选数据吗?')
    await SettlementFormApi.delSettlement(ids)
    message.success('删除成功')
    handleRefresh()
  } else {
    message.error('请先选择需要删除的数据')
    return
  }
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
