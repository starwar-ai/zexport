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
    :destroyOnClose="true"
    @success="handleRefresh"
  >
    <template #detail="{ key }"> <CkbillDetail :id="key" /></template>

    <template #edit="{ key }">
      <CkbillForm
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { ElMessageBox } from 'element-plus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { DICT_TYPE } from '@/utils/dict'
import CkbillDetail from './CkbillDetail.vue'
import CkbillForm from './CkbillForm.vue'
import * as BillApi from '@/api/wms/bill'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

defineOptions({
  name: 'CKBill'
})
const message = useMessage()
const eplusDialogRef = ref()
const eplusListRef = ref()
const exportFileName = ref()
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'companyName',
      label: '公司名称'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '创建日期',
          formatter: '从{0}到{1}'
        }
      ]
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
      name: 'purchaseContractCode',
      label: '采购合同号'
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await BillApi.getBillPage({ ...ps, billType: 2 })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    // await BillApi.deletePurchaseContract(id)
  },
  showTabs: true,
  tabs: [
    {
      label: '产品',
      isTree: true,
      columns: [
        {
          prop: 'code',
          label: '单号',
          parent: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'billStatus',
          label: '单据状态',
          parent: true,
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.STOCK_BILL_STATUS)
        },
        {
          prop: 'companyName',
          label: '公司名称',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'billTime',
          label: '出库日期',
          parent: true,
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        // {
        //   prop: 'purchaseContractCode',
        //   label: '采购合同号',
        //   parent: true,
        //   minWidth: '220px'
        // },
        // {
        //   prop: 'purchaserName',
        //   label: '采购员',
        //   parent: true,
        //   minWidth: '220px'
        // },
        // {
        //   prop: 'purchaserDeptName',
        //   label: '所属部门',
        //   parent: true,
        //   minWidth: '220px'
        // },
        {
          prop: 'remark',
          label: '备注',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'creatorName',
          label: '创建人',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'createTime',
          label: '创建日期',
          parent: true,
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: columnWidth.l,
          parent: true,
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
                    hasPermi="wms:bill-out:query"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: row.billStatus == 1,
                        otherKey: 'billUpdate',
                        label: '编辑',
                        permi: 'wms:bill-out:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      },
                      {
                        /* {
                        isShow: row.billStatus == 2,
                        otherKey: 'billCancel',
                        label: '作废',
                        permi: 'wms:bill:cancel',
                        handler: async (row) => {
                          handleCancel(row.id)
                        }
                      } */
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
          prop: 'thumbnail',
          label: '图片',
          minWidth: columnWidth.s,
          slots: {
            default: (data) => {
              const { row } = data
              if (row.mainPicture) {
                return (
                  <EplusImgEnlarge
                    mainPicture={row?.mainPicture}
                    thumbnail={row.thumbnail}
                  />
                )
              } else {
                return '-'
              }
            }
          }
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
          prop: 'purchaseContractCode',
          label: '采购合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'skuName',
          label: '产品名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'orderQuantity',
          label: '应出数量',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.orderQuantity == 0 ? 0 : row.orderQuantity ? row.orderQuantity : '-'
            }
          }
        },
        {
          prop: 'actQuantity',
          label: '实出数量',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.actQuantity == 0 ? 0 : row.actQuantity ? row.actQuantity : '-'
            }
          }
        },
        {
          prop: 'batchCode',
          label: '批次编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          minWidth: columnWidth.m
        }
      ]
    },
    {
      label: '单据',
      columns: [
        {
          prop: 'code',
          label: '单号',
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'billStatus',
          label: '单据状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.STOCK_BILL_STATUS)
        },
        {
          prop: 'companyName',
          label: '公司名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'billTime',
          label: '出库时间',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同号',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return [...new Set(row.children?.map((item) => item.purchaseContractCode))].join(',')
          }
        },
        // {
        //   prop: 'purchaserName',
        //   label: '采购员',
        //   minWidth: '220px'
        // },
        // {
        //   prop: 'purchaserDeptName',
        //   label: '所属部门',
        //   minWidth: '220px'
        // },
        {
          prop: 'warehouseName',
          label: '仓库名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'remark',
          label: '备注',
          minWidth: columnWidth.l,
          showOverflowTooltip: true
        },
        {
          prop: 'creatorName',
          label: '创建人',
          minWidth: columnWidth.m
        },
        {
          prop: 'createTime',
          label: '创建时间',
          minWidth: columnWidth.l,
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
                <div class="flex items-center">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['wms:bill-out:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: row.billStatus == 1,
                        otherKey: 'billUpdate',
                        label: '编辑',
                        permi: 'wms:bill-out:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      },
                      {
                        /* {
                        isShow: row.billStatus == 2,
                        otherKey: 'billCancel',
                        label: '作废',
                        handler: async (row) => {
                          handleCancel(row.id)
                        }
                      } */
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

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '出库单')
}

const handleCancel = (id: number) => {
  ElMessageBox.confirm('确认作废该出库单吗？', '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确认',
    type: 'warning'
  })
    .then(async () => {
      await BillApi.cancelBill(id)
      message.success('操作成功')
      handleRefresh()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  eplusListRef.value.handleRefresh()
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
