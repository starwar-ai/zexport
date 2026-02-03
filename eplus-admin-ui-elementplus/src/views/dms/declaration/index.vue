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
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <DeclarationDetail :id="key" />
    </template>
    <template #edit="{ key }">
      <DeclarationForm
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
import * as DeclarationApi from '@/api/dms/declaration/index'
import { getcompanySimpleList } from '@/api/common/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import DeclarationDetail from './DeclarationDetail.vue'
import DeclarationForm from './DeclarationForm.vue'
import download from '@/utils/download'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { useAppStore } from '@/store/modules/app'
import router from '@/router'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const eplusListRef = ref()
const exportFileName = ref() // 导出附件的名称，因为当前页面没有导出，所以名称暂且空着，后续有了在加上
defineOptions({ name: 'Declaration' })
const message = useMessage()

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'invoiceCode',
      label: '发票号'
    },
    {
      component: <el-input></el-input>,
      name: 'declarationName',
      label: '报关中文名称'
    },
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '报关单号'
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
      label: '客户合同'
    },
    {
      component: <el-input></el-input>,
      name: 'customsDeclarationNameEng',
      label: '报关英文名称'
    },
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
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await DeclarationApi.getDeclarationPage(ps)
    return {
      list: res?.list || [],
      total: res?.total || 0
    }
  },
  delListApi: async (id) => {
    await DeclarationApi.deleteDeclaration(id)
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
          prop: 'containerQuantity',
          label: '集装箱数量',
          minWidth: columnWidth.m,
          parent: true,
          slots: {
            default: (row) => {
              return row.twentyFootCabinetNum + row.fortyFootCabinetNum + row.fortyFootContainerNum
            }
          }
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
          prop: 'declarationName',
          label: '报关中文名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'customsDeclarationNameEng',
          label: '报关英文名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'custPo',
          label: '客户合同',
          minWidth: columnWidth.m
        },
        {
          prop: 'cskuCode',
          label: '客户货号',
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
          prop: 'shippingQuantity',
          label: '出运数量',
          minWidth: columnWidth.m
        },
        {
          prop: 'declarationQuantity',
          label: '报关数量',
          minWidth: columnWidth.m
        },
        {
          prop: 'declarationUnitPrice',
          label: '报关单价',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.declarationUnitPrice?.amount && row.declarationUnitPrice ? (
                <EplusMoneyLabel
                  val={{
                    amount: row.declarationUnitPrice?.amount || 0,
                    currency: row.declarationUnitPrice?.currency
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
                    hasPermi="['dms:declaration:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: true,
                        otherKey: 'DeclarationUpdate',
                        label: '编辑',
                        permi: 'dms:declaration:update',
                        handler: async (row) => {
                          await handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'DeclarationDelete',
                        label: '删除',
                        permi: 'dms:declaration:delete',
                        handler: async (row) => {
                          handleDelete(row?.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'DeclarationENSExport',
                        label: 'ENS导出',
                        permi: 'dms:declaration:export',
                        handler: async (row) => {
                          exportExcelENS(row.id, row.custPo, row.saleContractCodes)
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
          label: '报关单号',
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '归属公司',
          minWidth: columnWidth.m
        },
        {
          prop: 'protocolCode',
          label: '报关协议号',
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
          prop: 'shipDate',
          label: '出运日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn()
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
                    hasPermi="['dms:declaration:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: true,
                        otherKey: 'DeclarationUpdate',
                        label: '编辑',
                        permi: 'dms:declaration:update',
                        handler: async (row) => {
                          await handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'DeclarationDelete',
                        label: '删除',
                        permi: 'dms:declaration:delete',
                        handler: async (row) => {
                          handleDelete(row?.id)
                        }
                      },
                      {
                        isShow: true,
                        otherKey: 'DeclarationENSExport',
                        label: 'ENS导出',
                        permi: 'dms:declaration:export',
                        handler: async (row) => {
                          exportExcelENS(row.id, row.custPo, row.saleContractCodes)
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

// const handleExport = async () => {
//   return await eplusListRef.value.exportList('船代公司.xlsx')
// }

// 打开详情
const appStore = useAppStore()
const handleDetail = (id) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/dms/shipping-orders/declarationDetail/${id}`
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
const exportExcelENS = async (id, custPo, saleContractCodes) => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await DeclarationApi.exportDeclaration({
      id: id,
      reportCode: 'declaration-form-export-ens',
      exportType: 'ens'
    })
    let exportName = custPo || saleContractCodes || ''
    if (data && data.size) {
      download.excel(data, `报关单(ENS)${exportName}.xlsx`)
    }
  } catch {}
  return
}
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
