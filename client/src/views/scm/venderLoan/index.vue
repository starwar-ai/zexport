<template>
  <div class="h-45px bg-[var(--left-menu-bg-color)] pl-15px pr-15px">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleTabsClick"
    >
      <el-tab-pane
        v-for="item in tabNameList"
        :label="item.label"
        :name="item.name"
        :key="item.label"
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
      <el-button
        type="primary"
        plain
        @click="handleCreate()"
        v-hasPermi="['scm:vender-loan:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
      <el-button
        type="success"
        plain
        @click="handleExport()"
        v-hasPermi="['scm:vender-loan:export']"
      >
        <Icon
          icon="ep:download"
          class="mr-5px"
        />
        导出
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <VenderLoanAppDetail :id="key" /></template>
    <template #edit="{ key }">
      <VenderLoanAppForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #create>
      <VenderLoanAppForm mode="create" />
    </template>
  </eplus-dialog>
  <!-- <eplus-dialog
    ref="repayDialogRef"
    @success="handleRefresh"
  >
    <template #create>
      <repay-app-form
        mode="create"
        :row="repayRow"
      />
    </template>
  </eplus-dialog> -->
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import VenderLoanAppDetail from './VenderLoanAppDetail.vue'
import VenderLoanAppForm from './VenderLoanAppForm.vue'
// import RepayAppForm from '../repayapp/RepayAppForm.vue'
import { columnWidth, formatCurrColumn, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as VenderLoanApi from '@/api/scm/venderLoan'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { BpmProcessInstanceResultEnum, PaymentStatusEnum } from '@/utils/constants'

const name = 'VenderLoan'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()
const activeName = ref('first')
const tabNameList = [
  { name: 'first', label: '全部' },
  { name: 'second', label: '待提交' },
  { name: 'third', label: '待审批' },
  { name: 'forth', label: '已审批' },
  { name: 'fifth', label: '已驳回' }
  // { name: 'six', label: '已作废' }
]
const handleTabsClick = (val) => {
  activeName.value = val.props.name
}
const eplusListRef = ref()
defineOptions({
  name
})
const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <eplus-dict-select dictType={DICT_TYPE.VENDER_LOAN_TYPE}></eplus-dict-select>,
      name: 'loanType',
      label: '欠款类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.VENDER_LOAN_TYPE, args[0])
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'venderName',
      label: '供应商名称'
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    var status = ''
    var currentTab = tabNameList.filter((item) => item.name == activeName.value)
    if (currentTab?.length) {
      status = getDictValue(DICT_TYPE.VENDER_LOAN_STATUS, currentTab[0].label)
    }
    const params = {
      loanStatus: status,
      ...ps
    }
    const res = await VenderLoanApi.getVenderLoanPage(params)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await VenderLoanApi.deleteVenderLoan(id)
  },
  exportListApi: async (ps) => {
    return await VenderLoanApi.exportVenderLoan(ps)
  },
  columns: [
    {
      prop: 'code',
      label: '单据编号',
      minWidth: columnWidth.m
    },
    {
      prop: 'companyName',
      label: '公司主体',
      minWidth: columnWidth.l
    },
    {
      prop: 'companyName',
      label: '借款方式',
      minWidth: columnWidth.m
    },
    {
      prop: 'companyName',
      label: '借款类型',
      minWidth: columnWidth.m
    },
    {
      prop: 'companyName',
      label: '供应商名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'purpose',
      label: '借款事由',
      minWidth: columnWidth.m
    },
    // {
    //   prop: 'loanType',
    //   label: '借款类型',
    //   minWidth: '100px',
    //   formatter: formatDictColumn(DICT_TYPE.LOAN_TYPE)
    // },
    {
      prop: 'amount',
      label: '借款金额',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return `${row.amount.amount} ${row.amount.currency} `
        }
      }
    },
    // {
    //   prop: 'transferAmount',
    //   label: '已转金额',
    //   formatter: formatCurrColumn(),
    //   width: '100px'
    // },
    {
      prop: 'repayAmount',
      label: '已还款金额',
      formatter: formatCurrColumn(),
      minWidth: columnWidth.m
    },
    // {
    //   prop: 'repayDate',
    //   label: '实际还款日期',
    //   formatter: formatDateColumn(),
    //   width: '120px'
    // },
    {
      prop: 'loanDate',
      label: '借款日期',
      formatter: formatDateColumn(),
      minWidth: columnWidth.m
    },
    {
      prop: 'loanDate',
      label: '借款截止日',
      formatter: formatDateColumn(),
      minWidth: columnWidth.m
    },
    {
      prop: 'loanDate',
      label: '还款状态',
      formatter: formatDateColumn(),
      minWidth: columnWidth.m
    },
    // {
    //   prop: 'createTime',
    //   label: '创建时间',
    //   formatter: formatDateColumn(),
    //   width: '120px'
    // },
    {
      prop: 'auditStatus',
      label: '借款单状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
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
                hasPermi="['scm:vender-loan:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  user: row.applyer,
                  permi: 'scm:vender-loan:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                editItem={{
                  user: row.applyer,
                  permi: 'scm:vender-loan:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  user: row.applyer,
                  permi: 'scm:vender-loan:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: row?.paymentStatus === PaymentStatusEnum.SUCCESS.status,
                    otherKey: 'loan-app-repay',
                    label: '去还款',
                    permi: 'scm:vender-loan:repay',
                    checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    handler: async (row) => {
                      await handleToRepay(row)
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

const toRepayBtn = (row) => {
  if (row?.paymentStatus === 1 && row?.auditStatus === 2) {
    return {
      label: '去还款',
      permi: 'scm:vender-loan:repay',
      command: 'loan-app-repay',
      handler: async () => {
        await handleToRepay(row)
      },
      disabled: false,
      id: row.id,
      auditable: row
    }
  } else {
    return {
      command: 'loan-app-repay',
      disabled: true,
      id: row.id,
      auditable: row
    }
  }
}
const repayDialogRef = ref()
const repayRow = ref()
const handleToRepay = (row) => {
  repayRow.value = row
  repayDialogRef.value?.openCreate('还款单')
}

const exportFileName = ref('借款管理.xlsx')
const handleExport = async () => {
  return await eplusListRef.value.exportList('借款管理.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await VenderLoanApi.submitVenderLoan(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const detailClose = () => {
  eplusDialogRef.value?.close()
  handleRefresh()
}

onMounted(() => {})

// onActivated(() => {
//   eplusDialogRef.value?.close()
// })
</script>
