<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
  >
    <template #tableActions>
      <el-button
        type="primary"
        @click="handleImport()"
        v-hasPermi="['wms:stock:import']"
      >
        库存导入
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <StockDetail
        :id="key"
        type="cust"
      />
    </template>
  </eplus-dialog>

  <ImportDia
    ref="ImportDiaRef"
    @success="handleRefresh"
  />
</template>
<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import StockDetail from '../StockDetail.vue'
// 修改前
// import * as StockApi from '@/api/wms/stock'
// 修改后
import * as StockApi from '@/api/wms/stock/index'
import LockCom from '../LockCom.vue'
import { isValidArray } from '@/utils/is'
import { columnWidth, formatDateColumn, formatMoneyColumn } from '@/utils/table'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { getOuterbox } from '@/utils/outboxSpec'
import ImportDia from './ImportDia.vue'

const eplusDialogRef = ref()
const eplusListRef = ref()
const props = defineProps<{
  fromPage: string
}>()

const ImportDiaRef = ref()
const handleImport = () => {
  ImportDiaRef.value.open()
}

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
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    // {
    //   component: <el-input clearable></el-input>,
    //   name: 'companyName',
    //   label: '公司名称'
    // },
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

const treeColumns = reactive([
  {
    prop: 'custCode',
    label: '客户编码',
    parent: true,
    minWidth: columnWidth.m
  },
  {
    prop: 'custName',
    label: '客户名称',
    parent: true,
    minWidth: columnWidth.xxl
  },
  {
    prop: 'cskuCode',
    label: '客户货号',
    parent: true,
    minWidth: columnWidth.m
  },
  {
    prop: 'oskuCode',
    label: '自营产品货号',
    parent: true,
    minWidth: columnWidth.l
  },
  {
    prop: 'custPo',
    label: '客户PO号',
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
    prop: 'skuName',
    label: '产品名称',
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
])

const columns = reactive([
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
  // {
  //   prop: 'companyName',
  //   label: '公司名称',
  //   minWidth: columnWidth.m
  // },
  {
    prop: 'custName',
    label: '客户名称',
    minWidth: columnWidth.l,
    showOverflowTooltip: true
  },
  {
    prop: 'custCode',
    label: '客户编码',
    minWidth: columnWidth.m
  },
  {
    prop: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: columnWidth.l
  },
  {
    prop: 'skuName',
    label: '产品名称',
    minWidth: columnWidth.l
  },

  {
    prop: 'cskuCode',
    label: '客户货号',
    minWidth: columnWidth.m
  },
  {
    prop: 'oskuCode',
    label: '自营产品货号',
    minWidth: columnWidth.l
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
      return row.extendVenderAvailableQuantity ? row.extendVenderAvailableQuantity.toString() : '0'
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

  // {
  //   prop: 'outerbox',
  //   label: '产品规格',
  //   minWidth:columnWidth.m,
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return `${row.outerboxLength}*${row.outerboxWidth}*${row.outerboxHeight} ${LengthUnit}`
  //     }
  //   }
  // },
  // {
  //   prop: 'qtyPerOuterbox',
  //   label: '外箱装量',
  //   minWidth:columnWidth.m
  // },
  // {
  //   prop: 'qtyPerInnerbox',
  //   label: '内盒装量',
  //   minWidth:columnWidth.m
  // },

  // {
  //   prop: 'totalRemainderQuantity',
  //   label: '剩余库存',
  //   minWidth:columnWidth.m,
  //   formatter: (row) => {
  //     return row.totalRemainderQuantity ? row.totalRemainderQuantity.toString() : '0'
  //   }
  // },
  {
    prop: 'action',
    label: '操作',
    minWidth: columnWidth.l,
    fixed: 'right',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="flex">
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
])

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    let params = {
      ...ps
    }
    if (props.fromPage == 'cust') {
      params.custProFlag = 1
    }
    const res = await StockApi.getStockPage(params)
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
      columns:
        props.fromPage == 'cust' ? treeColumns.filter((e) => e.prop != 'oskuCode') : treeColumns
    },
    {
      label: '产品',
      isDefault: true,
      columns: props.fromPage == 'cust' ? columns.filter((e) => e.prop != 'oskuCode') : columns
    }
  ]
}

const setQuantity = (quantity) => {
  quantity = quantity == undefined ? 0 : quantity
  return quantity
}

const setExtendData = (list) => {
  list.forEach((e) => {
    // 在循环子项前初始化所有扩展属性
    e.extendVenderInitQuantity = 0 // 供应商入库数量
    e.extendWHInitQuantity = 0 // 仓库入库数量
    e.extendVenderOutQuantity = 0 // 供应商出库数量
    e.extendWHOutQuantity = 0 // 仓库出库数量
    e.extendVenderLockQuantity = 0 // 供应商锁定数量
    e.extendWHLockQuantity = 0 // 仓库锁定数量
    e.extendVenderAvailableQuantity = 0 // 供应商可用数量
    e.extendWHAvailableQuantity = 0 // 仓库可用数量
    e.extendVenderProducQuantity = 0 // 供应商在制数量
    e.extendWHProducQuantity = 0 // 仓库在制数量

    if (e.children) {
      e.children.forEach((child) => {
        if (child.purchaseUser) {
          child.purchaseUserName = child.purchaseUser.nickname
          child.purchaseUserDeptName = child.purchaseUser.deptName
        }

        // 根据venderFlag判断应该累加到哪个属性
        if (child.venderFlag) {
          e.extendVenderInitQuantity += setQuantity(child.initQuantity)
          e.extendVenderOutQuantity += setQuantity(child.usedQuantity)
          e.extendVenderLockQuantity += setQuantity(child.lockQuantity)
          e.extendVenderAvailableQuantity += setQuantity(child.availableQuantity)
          e.extendVenderProducQuantity += setQuantity(child.producingQuantity)
        } else {
          e.extendWHInitQuantity += setQuantity(child.initQuantity)
          e.extendWHOutQuantity += setQuantity(child.usedQuantity)
          e.extendWHLockQuantity += setQuantity(child.lockQuantity)
          e.extendWHAvailableQuantity += setQuantity(child.availableQuantity)
          e.extendWHProducQuantity += setQuantity(child.producingQuantity)
        }
      })
    }
  })
  return list
}

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = () => {
  eplusListRef.value.handleRefresh()
}
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
