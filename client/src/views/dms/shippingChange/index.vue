<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tabs> </template>
    <template #tableActions>&nbsp;</template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <!-- <template #edit="{ key }">
      <change-form
        :id="key"
        mode="edit"
      />
    </template> -->
    <template #detail="{ key }">
      <change-detail :id="key" />
      <!-- {{ key }} -->
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as ShipmentApi from '@/api/dms/shipment/index'
import { getcompanySimpleList } from '@/api/common/index'
import { formatDate } from '@/utils/formatTime'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import ChangeDetail from './ChangeDetail.vue'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import * as UserApi from '@/api/system/user/index'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'ShippingChange' })

const eplusListRef = ref()
const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      name: 'companyId',
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
      component: <el-input clearable></el-input>,
      name: 'invoiceCode',
      label: '发票号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'orderManagerId',
      label: '单证员',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const user = await UserApi.getSimpleUser(args[0])
          return user.nickname
        }
        return ''
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '产品名称'
    },
    {
      component: <el-input></el-input>,
      name: 'shipmentPlanCode',
      label: '计划单号'
    },
    {
      component: <el-input></el-input>,
      name: 'saleContractCode',
      label: '外销合同号'
    },
    {
      component: <el-input></el-input>,
      name: 'custContractCode',
      label: '客户合同'
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
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ShipmentApi.getChangeList(ps)
    let resultList = res.list?.map((item) => {
      item.children
        .filter((item1) => item1?.changeFlag === 3)
        .forEach((item2) => {
          item2.oldChildren = item.oldData.children.filter((item3) => item3?.id === item2?.id)
          return item2
        })
      return item
    })
    return {
      list: resultList,
      total: res.total
    }
  },
  delListApi: async () => {},
  //**是否包含多个表格显示 */
  showTabs: false,
  oldChangeField: 'oldData',
  selection: false,
  summary: false,
  columns: [
    {
      prop: 'shipmentPlanCode',
      label: '计划单号',
      minWidth: columnWidth.m,
      parent: true
    },
    {
      prop: 'invoiceCode',
      label: '出运发票号',
      minWidth: columnWidth.m,
      parent: true,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="invoiceCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'companyName',
      label: '归属公司',
      parent: true,
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="companyName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'estShipDate',
      label: '客户交期',
      minWidth: columnWidth.m,
      // formatter: formatDateColumn(),
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="estShipDate"
              oldChangeField="oldData"
              formatter={(val) => {
                return formatDate(val, 'YYYY-MM-DD')
              }}
              item={row}
            />
          </>
        )
      },
      parent: true
    },
    {
      prop: 'departurePortName',
      label: '出运口岸',
      minWidth: columnWidth.m,
      parent: true,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="departurePortName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'destinationPortName',
      label: '目的口岸',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="destinationPortName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      parent: true
    },
    {
      prop: 'transportType',
      label: '运输方式',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="transportType"
              oldChangeField="oldData"
              formatter={(val) => {
                return getDictLabel(DICT_TYPE.TRANSPORT_TYPE, val)
              }}
              item={row}
            />
          </>
        )
      },
      parent: true
    },
    {
      prop: 'inputUser',
      label: '录入人',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="inputUser"
              oldChangeField="oldData"
              item={row}
              formatter={(val) => {
                return `${val?.nickname}`
              }}
            />
          </>
        )
      },
      parent: true
      // formatter: (row) => {
      //   return `${row?.inputUser?.nickname}`
      // }
    },
    {
      prop: 'inputDate',
      label: '录入日期',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="inputDate"
              oldChangeField="oldData"
              item={row}
              formatter={(val) => {
                return formatDate(val, 'YYYY-MM-DD')
              }}
            />
          </>
        )
      },
      parent: true
      // formatter: formatDateColumn()
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
      parent: true,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'auditStatus',
      label: '审批状态',
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
      minWidth: columnWidth.m,
      parent: true
    },
    // {
    //   prop: 'changeStatus',
    //   label: '变更标识',
    //   formatter: formatDictColumn(DICT_TYPE.CHANGE_TYPE),
    //   minWidth: columnWidth.m
    // },
    {
      prop: 'saleContractCode',
      label: '外销合同号',
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="saleContractCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'custName',
      label: '客户名称',
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="custName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      minWidth: columnWidth.l
    },
    {
      prop: 'cskuCode',
      label: '客户货号',
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="cskuCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'basicSkuCode',
      label: '基础产品编号',
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="basicSkuCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'name',
      label: '产品名称',
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="name"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'skuCode',
      label: '产品编号',
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="skuCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'declarationName',
      label: '报关中文品名',
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="declarationName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'hsMeasureUnit',
      label: '海关计量单位',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="hsMeasureUnit"
              oldChangeField="oldData"
              formatter={(val) => {
                return getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, val)
              }}
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'deliveryDate',
      label: '交货日期',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="deliveryDate"
              oldChangeField="oldData"
              formatter={(val) => {
                return formatDate(val, 'YYYY-MM-DD')
              }}
              item={row}
            />
          </>
        )
      }
      // formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'hsCode',
      label: 'HS编码',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="hsCode"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
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
                  handleDetail(row.id)
                }}
                hasPermi="scm:purchase-contract-change:detail"
              >
                详情
              </el-button>
              <eplus-dropdown
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'scm:purchase-contract-change:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'scm:purchase-contract-change:update',
                //   handler: () => {
                //     handleUpdate(row.id)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'scm:purchase-contract-change:delete',
                //   handler: async (row) => {
                //     await handleDelete(row.id)
                //   }
                // }}
                otherItems={[]}
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    }
  ]
}
const eplusDialogRef = ref()
const handleRefresh = () => {}
const handleDialogFailure = () => {}

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
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
