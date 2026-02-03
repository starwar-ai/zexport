<template>
  <!-- noticeStatus -->
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      @tab-change="handleRefresh"
    >
      <el-tab-pane
        label="全部"
        :name="1"
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
    ref="eplusTableRef"
    @refresh="handleRefresh"
    key="transferOrder"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  />
  <eplus-dialog
    ref="eplusDialogRef"
    @primary="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <ContainerNoticeDetail
        :id="key"
        @success="handleRefresh"
      />
    </template>
  </eplus-dialog>

  <ToBillDia
    ref="ToBillDiaRef"
    @sure="handleRefresh"
  />
</template>
<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as ContainerNoticeApi from '@/api/dms/containerNotice'
import ContainerNoticeDetail from './ContainerNoticeDetail.vue'
import { formatNum } from '@/utils'
import ToBillDia from './ToBillDia.vue'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { volPrecision, weightPrecision } from '@/utils/config'
import { useAppStore } from '@/store/modules/app'
import router from '@/router'
import * as DeptApi from '@/api/system/dept'

defineOptions({ name: 'ContainerNotice' })

const message = useMessage()
const eplusTableRef = ref<any>(null)
const exportFileName = ref()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | null>(null)
const activeName = ref(1)
//EplusTable组件的搜索表单配置
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'planDate',
          label: '创建日期',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <el-input clearable></el-input>,
      name: 'saleContractCode',
      label: '外销合同号'
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
      name: 'invoiceCode',
      label: '出运发票号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custPo',
      label: '客户合同号'
    },
    {
      component: <eplus-dept-select placeholder="请选择业务部门"></eplus-dept-select>,
      name: 'salesDeptId',
      label: '业务部门',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const dept = await DeptApi.getSimpleDept(args[0])
          return dept.name
        }
        return ''
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择采购部门"></eplus-dept-select>,
      name: 'purchaserDeptId',
      label: '采购部门',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const dept = await DeptApi.getSimpleDept(args[0])
          return dept.name
        }
        return ''
      }
    }
  ],
  moreFields: []
}

//EplusTable组件的表格配置
let eplusTableSchema: any = {
  getListApi: async (ps) => {
    const res = await ContainerNoticeApi.getContainerPage({
      isContainerTransportation: 1,
      noticeStatus: activeName.value === 1 ? undefined : activeName.value,
      ...ps
    })
    return {
      list: res.list,
      total: res.total,
      sum: { quantity: res?.summary?.qtySum }
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
      prop: 'referenceNumber',
      label: '提单号/进仓编号',
      minWidth: columnWidth.m,
      parent: true
    },
    {
      prop: 'invoiceCode',
      label: '出运发票号',
      minWidth: columnWidth.m,
      parent: true
    },
    {
      prop: 'containerTransportationDate',
      label: '预计拉柜日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD'),
      parent: true
    },
    {
      prop: 'saleContractCode',
      label: '销售合同',
      minWidth: columnWidth.m
    },
    {
      prop: 'skuCode',
      label: '产品编号',
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
      prop: 'purchaseContractCode',
      label: '采购合同编号',
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
      prop: 'sales',
      label: '业务员',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div>
              {row.sales?.nickname || ''}
            </div>
          )
        }
      }
    },
    {
      prop: 'salesDeptName',
      label: '业务部门',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div>
              {row.sales?.deptName || ''}
            </div>
          )
        }
      }
    },
    {
      prop: 'purchaseUserName',
      label: '采购员',
      minWidth: columnWidth.m
    },
    {
      prop: 'purchaseUserDeptName',
      label: '采购部门',
      minWidth: columnWidth.m
    },
    {
      prop: 'orderBoxQuantity',
      label: '应出箱数',
      minWidth: columnWidth.m
    },
    {
      prop: 'orderQuantity',
      label: '应出数量',
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
      prop: 'noticeStatus',
      label: '出库状态',
      minWidth: columnWidth.m,
      parent: true,
      formatter: formatDictColumn(DICT_TYPE.NOTICE_STATUS)
    },

    {
      prop: 'totalWeight',
      label: '总毛重kg',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div>
              {row.totalWeight?.weight ? formatNum(row.totalWeight?.weight, weightPrecision) : ''}
            </div>
          )
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
                v-hasPermi="dms:container-transportation:query"
              >
                详情
              </el-button>
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: [1, 4].includes(row.noticeStatus) && checkConvert(row, 2),
                    otherKey: 'dmsContainerTransportationConvert',
                    label: '转出库单',
                    permi: 'dms:container-transportation:convert',
                    handler: async (row) => {
                      await handleToBill(row, 2)
                    }
                  },
                  {
                    isShow: [1, 4].includes(row.noticeStatus) && checkConvert(row, 1),
                    otherKey: 'dmsContainerTransportationConvert2',
                    label: '工厂出库',
                    permi: 'dms:container-transportation:convert',
                    handler: async (row) => {
                      await handleToBill(row, 1)
                    }
                  },

                  {
                    isShow: row.noticeStatus !== 3,
                    otherKey: 'transportationClose',
                    label: '作废',
                    permi: 'dms:container-transportation:close',
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

const checkConvert = (row, type) => {
  let list = row.children.filter(
    (item) => item.convertBillFlag === 0 && item.shippedAddress === type
  )
  return list.length > 0
}

const ToBillDiaRef = ref()
const handleToBill = async (row, type) => {
  ToBillDiaRef.value.open(row, type)
}

const handleClose = async (id) => {
  await message.confirm('确认作废该拉柜通知单吗？')
  await ContainerNoticeApi.close(id)
  message.success('操作成功')
  handleRefresh()
}

//刷新表格
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
//弹窗操作失败
const handleDialogFailure = () => {}
// 打开详情
const appStore = useAppStore()
const handleDetail = (id) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/dms/containerNoticeDetail/${id}`
    })
  }
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style lang="scss" scoped></style>
