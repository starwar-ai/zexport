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
    <template #detail="{ key }">
      <export-sale-contract-change-detail
        :id="key"
        :type="2"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as DomesticSaleContractChangeApi from '@/api/sms/saleContract/domestic/change'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import * as CommonApi from '@/api/common'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import ExportSaleContractChangeDetail from '../exportSaleContractChange/ExportSaleContractChangeDetail.vue'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { DICT_TYPE } from '@/utils/dict'

defineOptions({ name: 'DomesticSaleContractChange' })
const eplusListRef = ref()
const exportFileName = ref()

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'creator',
      label: '创建人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
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
      component: <EplusSearchMultiInput />,
      subfields: [
        {
          name: 'code',
          label: '变更单号'
        },
        {
          name: 'contractCode',
          label: '内销单号'
        }
      ]
    },
    {
      component: <el-input clearable></el-input>,
      name: 'sourceCode',
      label: '销售合同号'
    },
    {
      component: <EplusSearchMultiInput />,
      subfields: [
        {
          name: 'custName',
          label: '客户名称'
        },
        {
          name: 'custCode',
          label: '客户编号'
        }
      ]
    },
    {
      component: (
        <eplus-custom-select
          api={CommonApi.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择下单主体"
          style="width:100%"
        />
      ),
      name: 'companyId',
      label: '下单主体',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const companyList = await CommonApi.getcompanySimpleList()
          const company = companyList.find((item) => item.id === args[0])
          return company ? company.name : ''
        }
        return ''
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'salesUserId',
      label: '业务员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'salesDeptId',
      label: '部门',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const dept = await DeptApi.getSimpleDept(args[0])
          return dept.name
        }
        return ''
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <EplusSearchMultiInput />,
      subfields: [
        {
          name: 'name',
          label: '中文品名'
        },
        {
          name: 'nameEng',
          label: '英文品名'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await DomesticSaleContractChangeApi.getChangePage({ ...ps, saleType: 2 })
    let resultList = res.list?.map((item) => {
      item.changeFlag = false
      // item.oldData?.children?.forEach((e) => {
      //   e.unitPrice.amount = e.unitPrice?.amount.toFixed(2)
      // })
      item.children
        ?.filter((item1) => item1?.changeFlag === 3)
        ?.forEach((item2) => {
          item2.oldChildren = item.oldData?.children?.filter((item3) => item3?.id === item2?.id)
          // item2.unitPrice.amount = item2.unitPrice?.amount.toFixed(2)
          return item2
        })
      return item
    })
    return {
      list: resultList,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await DomesticSaleContractChangeApi.deleteChange(id)
  },
  //**是否包含多个表格显示 */
  showTabs: false,
  columns: [
    {
      prop: 'code',
      label: '合同编号',
      isCopy: true,
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'companyName',
      label: '下单主体',
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'custCode',
      label: '客户编号',
      isCopy: true,
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'custName',
      label: '客户名称',
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'skuCode',
      label: '产品编码',
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="skuCode"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'inputDate',
      label: '录入日期',
      parent: true,
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'status',
      label: '合同状态',
      parent: true,
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.SALE_CONTRACT_STATUS)
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
      parent: true,
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'mainPicture',
      label: '图片',
      // minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <EplusImgEnlarge
              mainPicture={row?.mainPicture}
              thumbnail={row?.thumbnail}
            />
          )
        }
      }
    },
    {
      prop: 'name',
      label: '中文名称',
      // minWidth: columnWidth.m,

      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="name"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'nameEng',
      label: '英文名',
      // minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="nameEng"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'quantity',
      label: '数量',
      // minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="quantity"
                oldChangeField="oldData"
              />
            </>
          )
        }
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
                hasPermi="sms:domestic-sale-contract:detail"
              >
                详情
              </el-button>
              <eplus-dropdown
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
const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const handleDialogFailure = () => {}

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
