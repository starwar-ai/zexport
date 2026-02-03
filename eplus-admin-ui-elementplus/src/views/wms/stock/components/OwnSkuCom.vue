<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
  />

  <eplus-dialog
    ref="eplusDialogRef"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <StockDetail
        :id="key"
        type="own"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import StockDetail from '../StockDetail.vue'
import * as StockApi from '@/api/wms/stock'
import LockCom from '../LockCom.vue'
import { isValidArray } from '@/utils/is'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { columnWidth, formatDateColumn, formatMoneyColumn } from '@/utils/table'
import { getOuterbox } from '@/utils/outboxSpec'

const eplusDialogRef = ref()
const eplusListRef = ref()

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
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
      name: 'skuName',
      label: '产品名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'oskuCode',
      label: '自营产品货号'
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
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaseUserId',
      label: '采购员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择采购员部门"></eplus-dept-select>,
      name: 'purchaseUserDeptId',
      label: '采购员部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'receiptTime',
          label: '入库时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await StockApi.getStockPage({
      ...ps,
      ownBrandFlag: 1,
      custProFlag: 0
    })
    res.list = setExtendData(res.list)
    return {
      list: res.list,
      total: res.total
    }
  },
  showTabs: true,
  tabs: [
    {
      label: '批次',
      isTree: true,
      columns: [
        // {
        //   prop: 'companyName',
        //   label: '公司名称',
        //   parent: true,
        //   minWidth: columnWidth.m
        // },
        {
          prop: 'skuCode',
          label: '产品编号',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'oskuCode',
          label: '自营产品货号',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'skuName',
          label: '产品名称',
          parent: true,
          minWidth: columnWidth.xxl
        },
        {
          prop: 'oskuCode',
          label: '自营产品货号',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'qtyPerInnerbox',
          label: '内盒装量',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'qtyPerOuterbox',
          label: '外箱装量',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'companyName',
          label: '公司名称',
          minWidth: columnWidth.m
        },
        {
          label: '产品图片',
          prop: 'mainPicture',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              if (row.thumbnail || row.mainPicture) {
                return (
                  <EplusImgEnlarge
                    mainPicture={row.mainPicture}
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
          prop: 'outerbox',
          label: '产品规格',
          minWidth: columnWidth.xl,
          formatter: (row) => {
            return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
          }
        },
        {
          prop: 'warehouseName',
          label: '仓库名称',
          minWidth: columnWidth.l
        },
        {
          prop: 'saleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUserDeptName',
          label: '采购员部门',
          minWidth: columnWidth.m
        },
        {
          prop: 'createTime',
          label: '添加日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'batchCode',
          label: '批次编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同',
          minWidth: columnWidth.m
        },

        {
          prop: 'position',
          label: '位置',
          minWidth: columnWidth.m
        },
        {
          prop: 'initQuantity',
          label: '入库数量',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.initQuantity.toString()
          }
        },
        {
          prop: 'usedQuantity',
          label: '出库数量',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.usedQuantity.toString()
          }
        },
        {
          prop: 'lockQuantity',
          label: '锁定数量',
          minWidth: columnWidth.m,
          slots: {
            default: (row) => {
              return (
                <LockCom
                  row={row}
                  isTree={true}
                />
              )
            }
          }
        },
        {
          prop: 'producingQuantity',
          label: '在制数量',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row?.producingQuantity
          }
        },
        {
          prop: 'availableQuantity',
          label: '已入库数量',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.availableQuantity.toString()
          }
        },
        // {
        //   prop: 'remainderQuantity',
        //   label: '剩余库存',
        //   minWidth:columnWidth.m,
        //   formatter: (row) => {
        //     return row.remainderQuantity.toString()
        //   }
        // },
        {
          prop: 'price',
          label: '单价',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'totalAmount',
          label: '入库总金额',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'remainTotalAmount',
          label: '剩余总金额',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'venderName',
          label: '供应商名称',
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
                <div class="flex items-center">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.skuId)
                    }}
                    hasPermi="['wms:stock:query']"
                  >
                    详情
                  </el-button>
                </div>
              )
            }
          }
        }
      ]
    },
    {
      label: '产品',
      isDefault: true,
      columns: [
        {
          label: '产品图片',
          prop: 'mainPicture',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              if (isValidArray(row.children)) {
                return (
                  <EplusImgEnlarge
                    mainPicture={row.children[0]?.mainPicture}
                    thumbnail={row.children[0]?.thumbnail}
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
          minWidth: columnWidth.l
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.l
        },
        {
          prop: 'oskuCode',
          label: '自营产品货号',
          minWidth: columnWidth.l
        },
        {
          prop: 'skuName',
          label: '产品名称',
          minWidth: columnWidth.l
        },
        {
          prop: 'oskuCode',
          label: '自营产品货号',
          minWidth: columnWidth.m
        },
        {
          prop: 'totalLockQuantity',
          label: '锁定数量',
          minWidth: columnWidth.m,
          slots: {
            default: (row) => {
              return (
                <LockCom
                  row={row}
                  isTree={false}
                />
              )
            }
          }
        },
        {
          prop: 'totalProducingQuantity',
          label: '在制数量',
          minWidth: columnWidth.m
        },
        {
          prop: 'extendWHAvailableQuantity',
          label: '已入泛太仓数量',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.extendWHAvailableQuantity ? row.extendWHAvailableQuantity.toString() : '0'
          }
        },
        {
          prop: 'extendVenderAvailableQuantity',
          label: '已入供应商仓数量',
          minWidth: columnWidth.xl,
          formatter: (row) => {
            return row.extendVenderAvailableQuantity
              ? row.extendVenderAvailableQuantity.toString()
              : '0'
          }
        },
        {
          prop: 'totalAvailableQuantity',
          label: '已入库数量',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.totalAvailableQuantity ? row.totalAvailableQuantity.toString() : '0'
          }
        },
        {
          prop: 'totalInitQuantity',
          label: '总入库数量',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.totalInitQuantity ? row.totalInitQuantity.toString() : '0'
          },
          extend: [
            {
              prop: 'extendWHInitQuantity',
              label: '仓库',
              minWidth: columnWidth.m
            },
            {
              prop: 'extendVenderInitQuantity',
              label: '供应商',
              minWidth: columnWidth.m
            }
          ]
        },
        {
          prop: 'totalUsedQuantity',
          label: '出库数量',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.totalUsedQuantity ? row.totalUsedQuantity.toString() : '0'
          },
          extend: [
            {
              prop: 'extendWHOutQuantity',
              label: '仓库',
              minWidth: columnWidth.m
            },
            {
              prop: 'extendVenderOutQuantity',
              label: '供应商',
              minWidth: columnWidth.m
            }
          ]
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
                      handleDetail(row.skuId)
                    }}
                    hasPermi="['wms:stock:query']"
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
  ]
}

const setQuantity = (quantity) => {
  quantity = quantity == undefined ? 0 : quantity
  return quantity
}

const setExtendData = (list) => {
  list.forEach((e) => {
    if (e.children) {
      e.children.forEach((child) => {
        if (child.purchaseUser) {
          child.purchaseUserName = child.purchaseUser.nickname
          child.purchaseUserDeptName = child.purchaseUser.deptName
        }
        e.extendVenderInitQuantity = setQuantity(e.extendVenderInitQuantity) // 供应商入库数量
        e.extendWHInitQuantity = setQuantity(e.extendWHInitQuantity) // 仓库入库数量

        e.extendVenderOutQuantity = setQuantity(e.extendVenderOutQuantity) // 供应商出库数量
        e.extendWHOutQuantity = setQuantity(e.extendWHOutQuantity) // 仓库出库数量

        e.extendVenderLockQuantity = setQuantity(e.extendVenderLockQuantity) // 供应商锁定数量
        e.extendWHLockQuantity = setQuantity(e.extendWHLockQuantity) // 仓库锁定数量

        e.extendVenderAvailableQuantity = setQuantity(e.extendVenderAvailableQuantity) // 供应商可用数量
        e.extendWHAvailableQuantity = setQuantity(e.extendWHAvailableQuantity) // 仓库可用数量

        e.extendVenderProducQuantity = setQuantity(e.extendVenderProducQuantity) // 供应商在制数量
        e.extendWHProducQuantity = setQuantity(e.extendWHProducQuantity) // 仓库在制数量

        if (child.venderFlag) {
          e.extendVenderInitQuantity += child.initQuantity
          e.extendVenderOutQuantity += child.usedQuantity
          e.extendVenderLockQuantity += child.lockQuantity
          e.extendVenderAvailableQuantity += child.availableQuantity
          e.extendVenderProducQuantity += child.producingQuantity
        } else {
          e.extendWHInitQuantity += child.initQuantity
          e.extendWHOutQuantity += child.usedQuantity
          e.extendWHLockQuantity += child.lockQuantity
          e.extendWHAvailableQuantity += child.availableQuantity
          e.extendWHProducQuantity += child.producingQuantity
        }
      })
    }
  })
  return list
}

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
