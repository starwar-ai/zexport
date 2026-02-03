<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        @click="handleCreate()"
        v-hasPermi="['oa:loan-app:create']"
      >
        新增
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }">
      <loan-app-detail
        :id="key"
        @success="detailClose"
    /></template>
    <template #edit="{ key }">
      <loan-app-form
        :id="key"
        mode="edit"
      />
    </template>

    <template #create>
      <loan-app-form mode="create" />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="repayDialogRef"
    @success="handleRefresh"
  >
    <template #create>
      <repay-app-form
        mode="create"
        :row="repayRow"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import LoanAppDetail from './LoanAppDetail.vue'
import LoanAppForm from './LoanAppForm.vue'
import RepayAppForm from '../repayapp/RepayAppForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as api from '@/api/oa/loanapp'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import { BpmProcessInstanceResultEnum, PaymentStatusEnum } from '@/utils/constants'
import { currencyJsonAnalysis, formatNum } from '@/utils'
import { moneyTotalPrecision } from '@/utils/config'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({
  name: 'LoanApp'
})

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input />,
      name: 'code',
      label: '借款编号'
    },
    {
      component: <eplus-dept-select placeholder="请选择借款部门"></eplus-dept-select>,
      name: 'loanDeptId',
      label: '借款部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.REPAY_STATUS}></eplus-dict-select>,
      name: 'repayStatus',
      label: '还款状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.REPAY_STATUS, args[0])
      }
    },
    {
      component: (
        <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}></eplus-dict-select>
      ),
      name: 'auditStatus',
      label: '审核状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'applyerId',
      label: '借款人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.PAYMENT_STATUS}></eplus-dict-select>,
      name: 'paymentStatus',
      label: '支付状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.PAYMENT_STATUS, args[0])
      }
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'loanDate',
          label: '借款日期',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await api.getLoanAppPage(ps)
    return {
      list: res.list,
      total: res.total,
      sum: {
        amount: currencyJsonAnalysis(res.summary?.amountSum),
        repayAmount: currencyJsonAnalysis(res.summary?.repayAmountSum),
        inRepaymentAmount: currencyJsonAnalysis(res.summary?.inRepaymentAmountSum),
        outstandingAmount: currencyJsonAnalysis(res.summary?.outAmountSum)
      }
    }
  },
  delListApi: async (id) => {
    await api.deleteLoanApp(id)
  },
  exportListApi: async (ps) => {
    return await api.exportLoanApp(ps)
  },
  summary: true,
  columns: [
    {
      prop: 'code',
      label: '借款编号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'applyerName',
      label: '借款人',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.applyer.deptName) {
            return `${row.applyer.nickname}（${row.applyer.deptName}）`
          } else {
            return `${row.applyer.nickname}`
          }
        }
      }
    },
    {
      prop: 'amount',
      label: '借款金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn(),
      summary: true,
      summarySlots: {
        default: (data) => {
          return {
            unit: data.currency,
            number: formatNum(data.amount, moneyTotalPrecision)
          }
        }
      }
    },
    {
      prop: 'amountRMB',
      label: '借款金额(RMB)',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.amount.amount && row.exchangeRate) {
            return (Number(row.amount.amount) * Number(row.exchangeRate)).toFixed(2)
          }
        }
      }
    },
    {
      prop: 'companyName',
      label: '公司主体',
      minWidth: columnWidth.m
    },
    {
      prop: 'repayAmount',
      label: '已还金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn(),
      summary: true,
      summarySlots: {
        default: (data) => {
          return {
            unit: data.currency,
            number: formatNum(data.amount, moneyTotalPrecision)
          }
        }
      }
    },
    {
      prop: 'inRepaymentAmount',
      label: '还款中金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn(),
      summary: true,
      summarySlots: {
        default: (data) => {
          return {
            unit: data.currency,
            number: formatNum(data.amount, moneyTotalPrecision)
          }
        }
      }
    },
    {
      prop: 'outstandingAmount',
      label: '未还金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn(),
      summary: true,
      summarySlots: {
        default: (data) => {
          return {
            unit: data.currency,
            number: formatNum(data.amount, moneyTotalPrecision)
          }
        }
      }
    },

    // {
    //   prop: 'transferAmount',
    //   label: '已转金额',
    //   formatter: formatCurrColumn(),
    //   width: '100px'
    // },
    // {
    //   prop: 'repayAmount',
    //   label: '已还金额',
    //   formatter: formatCurrColumn(),
    //   width: '100px'
    // },
    // {
    //   prop: 'bank',
    //   label: '开户行',
    //   minWidth: '120px'
    // },
    // {
    //   prop: 'bankAccount',
    //   label: '银行账户',
    //   minWidth: '120px'
    // },
    // {
    //   prop: 'bankPoc',
    //   label: '账户名称',
    //   minWidth: '100px'
    // },
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
    // {
    //   prop: 'createTime',
    //   label: '创建时间',
    //   formatter: formatDateColumn(),
    //   width: '150px'
    // },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'paymentStatus',
      label: '支付状态',
      minWidth: columnWidth.m,
      formatter: (row) => {
        // 审核状态为不通过(3)或已取消(4)时，显示 "/"
        if (row.auditStatus === 3 || row.auditStatus === 4) {
          return '/'
        }
        return getDictLabel(DICT_TYPE.PAYMENT_STATUS, row.paymentStatus)
      }
    },
    {
      prop: 'purpose',
      label: '借款事由',
      minWidth: columnWidth.xxl
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
                hasPermi="['oa:loan-app:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  user: row.applyer,
                  permi: 'oa:loan-app:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                editItem={{
                  user: row.applyer,
                  permi: 'oa:loan-app:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  user: row.applyer,
                  permi: 'oa:loan-app:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow:
                      row?.paymentStatus === PaymentStatusEnum.SUCCESS.status &&
                      row.outstandingAmount?.amount > 0,
                    user: row.applyer,
                    otherKey: 'loan-app-repay',
                    label: '去还款',
                    permi: 'oa:loan-app:repay',
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
  let res = await api.submitLoanApp(data.id)
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

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const detailClose = () => {
  eplusDialogRef.value?.close()
  handleRefresh()
}

onMounted(() => {})
</script>
