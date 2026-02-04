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
    ref="eplusTableRef"
    @refresh="handleRefresh"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <div
        class="w-100% flex justify-between"
        style="height: 32px"
      >
        <!-- 页面按钮 -->
        <div class="flex-1 overflow-x-auto">
          <!-- 左侧，使用flex-1自动填充剩余空间 -->
          <span
            v-for="item in btnSchemas?.filter((schema) => !schema.align)"
            :key="item?.index"
            class="mr-2"
          >
            <component
              v-if="item?.component"
              :is="item?.component"
            />
          </span>
        </div>
      </div> </template
  ></eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <FeesDetail
        :id="key"
        @success="handleRefresh"
    /></template>
  </eplus-dialog>
  <PayDialog
    ref="payDialogRef"
    @success="handleRefresh"
  />
  <PaymentConfirm
    ref="paymentConfirmRef"
    @success="handleRefresh"
  />

  <BatchDirect
    ref="batchDirectRef"
    @success="handleRefresh"
  />

  <BatchPlan
    ref="BatchPlanRef"
    @success="handleRefresh"
  />

  <BatchPaymentConfirm
    ref="BatchPaymentConfirmRef"
    @success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import FeesDetail from './feesDetail.vue'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as FeesApi from '@/api/finance/fees'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import PayDialog from './components/PayDialog.vue'
import { PaymentAuditStatus } from '@/utils/constants'
import PaymentConfirm from './components/PaymentConfirm.vue'
import BatchDirect from './components/BatchDirect.vue'
import BatchPlan from './components/BatchPlan.vue'
import BatchPaymentConfirm from './components/BatchPaymentConfirm.vue'
import * as UserApi from '@/api/system/user'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'
import { isValidArray } from '@/utils/is'
import { currencyJsonAnalysis } from '@/utils'

const name = 'FeesPayment'
defineOptions({
  name
})

const tabNameList = [
  { name: '2', label: '待计划' },
  { name: '10', label: '已计划' },
  { name: '11', label: '已支付' },
  { name: '12', label: '已作废' },
  { name: 'all', label: '全部' }
]
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const eplusTableRef = ref()
const message = useMessage()

const activeName = ref('2')
const tabIndex = ref('0')
const exportFileName = computed(() => {
  return checkPermi(['scm:purchase-plan:export']) ? '财务付款管理.xlsx' : ''
})

const batchDirectRef = ref()
const BatchPlanRef = ref()

const handleBatchPlan = () => {
  let list = eplusTableRef.value.checkedItems
  if (isValidArray(list)) {
    let companyIds = list.map((item) => item.companyId)
    if (!companyIds.every((e) => e === companyIds[0])) {
      message.warning('请选择同一付款单位下的付款单')
      return
    }
    BatchPlanRef.value.open(list)
  } else {
    message.warning('请选择操作的数据')
  }
}
const handleBatchDirect = () => {
  let list = eplusTableRef.value.checkedItems
  if (isValidArray(list)) {
    let companyIds = list.map((item) => item.companyId)
    if (!companyIds.every((e) => e === companyIds[0])) {
      message.warning('请选择同一付款单位下的付款单')
      return
    }
    batchDirectRef.value.open(list)
  } else {
    message.warning('请选择操作的数据')
  }

  // paymentConfirmRef.value.batchPay(rows, payMethod)
  // batchDirectRef.value.open()
}
const BatchPaymentConfirmRef = ref()
const openPaymentConfirm = (rowsList?, payMethod, editFlag) => {
  if (eplusTableRef.value.checkedItems.length === 0 && rowsList && rowsList?.length === 0) {
    message.error('请选择需要确认的数据')
    return
  } else if (rowsList) {
    paymentConfirmRef.value.open(rowsList, payMethod, editFlag)
  } else {
    // let companyIds = eplusTableRef.value.checkedItems.map((item) => item.companyId)
    // let payMethods = eplusTableRef.value.checkedItems.map((item) => item.payMethod)
    // let banks = eplusTableRef.value.checkedItems.map((item) => item.bank)
    // if (!companyIds.every((e) => e === companyIds[0])) {
    //   message.warning('请选择同一付款单位下的付款单')
    //   return
    // }
    // else if (!payMethods.every((e) => e === payMethods[0])) {
    //   message.warning('请选择同一付款方式的付款单')
    //   return
    // } else if (!banks.every((e) => e === banks[0])) {
    //   message.warning('请选择同一付款银行下的付款单')
    //   return
    // }
    // let rows = eplusTableRef.value.checkedItems.map((item) => {
    //   return {
    //     amount: item.amount,
    //     currency: item.amount?.currency,
    //     id: item.id,
    //     inputUser: item.inputUser,
    //     companyId: item.companyId
    //   }
    // })
    let list = eplusTableRef.value.checkedItems
    if (isValidArray(list)) {
      let companyIds = list.map((item) => item.companyId)
      if (!companyIds.every((e) => e === companyIds[0])) {
        message.warning('请选择同一付款单位下的付款单')
        return
      }
      BatchPaymentConfirmRef.value.open(list)
    } else {
      message.warning('请选择操作的数据')
    }
  }
}
//全部状态下的按钮
const allBtnSchemas = [
  {
    component: (
      <el-button
        type="primary"
        plain
        onClick={() => openPaymentConfirm()}
        v-hasPermi="['scm:purchase-plan:create']"
      >
        批量确认付款
      </el-button>
    )
  }
]
const PendingPlanBtnSchemas = [
  {
    component: (
      <el-button
        type="primary"
        plain
        onClick={() => handleBatchPlan()}
        v-hasPermi="['fms:payment:plan-payment']"
      >
        批量计划付款
      </el-button>
    )
  },
  {
    component: (
      <el-button
        type="primary"
        plain
        onClick={() => handleBatchDirect()}
        v-hasPermi="['fms:payment:direct-payment']"
      >
        批量直接付款
      </el-button>
    )
  }
]
let btnSchemas: any = reactive([])
const btnSchemasFormat = (data) => {
  switch (data) {
    case '0':
      btnSchemas = PendingPlanBtnSchemas
      break
    case '1':
      btnSchemas = allBtnSchemas
      break
    default:
      btnSchemas = []
      break
  }
}
const handleTabsClick = (val) => {
  tabIndex.value = val.index
  btnSchemasFormat(val.index)
}

const payDialogRef = ref()
const paymentConfirmRef = ref()
const openPayDialog = (row) => {
  payDialogRef.value.open(row)
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable />,
      name: 'code',
      label: '付款单号'
    },
    {
      component: (
        <eplus-custom-select
          api={FeesApi.getcompanySimpleList}
          label="name"
          value="id"
        />
      ),
      name: 'companyId',
      label: '付款主体',
      formatter: async (args: any[]) => {
        let list = await FeesApi.getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: <el-input clearable />,
      name: 'applyAmount',
      label: '申请总金额'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'applierId',
      label: '申请人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
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
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '申请时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const params = activeName.value === 'all' ? ps : { auditStatus: activeName.value, ...ps }
    if (activeName.value === '2') {
      btnSchemas = PendingPlanBtnSchemas
    }
    const res = await FeesApi.getFeesPage(params)
    return {
      list: res.list,
      total: res.total,
      sum: {
        applyAmount: currencyJsonAnalysis(res?.summary?.amountSum)
      }
    }
  },
  delListApi: async (id) => {
    await FeesApi.deleteFees(id)
  },
  exportListApi: async (ps) => {
    return await FeesApi.exportFees(ps)
  },
  selection: true,
  summary: true,
  columns: [
    {
      prop: 'code',
      label: '付款单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '付款单位',
      minWidth: columnWidth.m
    },
    // {
    //   prop: 'businessSubjectName',
    //   label: '支付对象',
    //   minWidth: '130px',
    //   formatter: (row) => {
    //     return (
    //       <span>
    //         {row.businessSubjectType === 3
    //           ? JSON.parse(row.businessSubjectName).nickname +
    //             '(' +
    //             JSON.parse(row.businessSubjectName)?.deptName +
    //             ')'
    //           : row?.businessSubjectName}
    //       </span>
    //     )
    //   }
    // },
    // {
    //   prop: 'bankAccount',
    //   label: '账号',
    //   minWidth: '120px'
    //   // formatter: formatDictColumn(DICT_TYPE.LOAN_REPAY_STATUS)
    // },
    // {
    //   prop: 'countryId',
    //   label: '国家'
    // },
    {
      prop: 'paymentBank',
      label: '付款银行',
      minWidth: columnWidth.m
    },
    {
      prop: 'paymentMethod',
      label: '支付方式',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.LOAN_TYPE)
    },
    {
      prop: 'applyAmount',
      label: '申请总金额',
      minWidth: columnWidth.l,
      summary: true,
      summarySlots: {
        default: (data) => {
          //-- unit:单位 number:值
          return {
            unit: data.currency,
            number: data.amount.toFixed(3)
          }
        }
      },
      formatter: formatMoneyColumn()
    },
    {
      prop: 'businessSubjectType',
      label: '付款对象类型',
      minWidth: columnWidth.l,
      formatter: formatDictColumn(DICT_TYPE.BUSINESS_SUBJECT_TYPE)
    },
    {
      prop: 'businessSubjectName',
      label: '付款对象名称',
      minWidth: columnWidth.l
    },
    {
      prop: 'businessType',
      label: '来源类型',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BUSINESS_TYPE)
    },
    {
      prop: 'businessCode',
      label: '来源编码',
      minWidth: columnWidth.m
    },
    {
      prop: 'applyer',
      label: '申请人（部门）',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return (
          <span>
            {row.applyer ? row.applyer?.nickname + '(' + row.applyer?.deptName + ')' : ''}
          </span>
        )
      }
    },
    {
      prop: 'inputUser',
      label: '录入人（部门）',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return (
          <span>
            {row.applyer ? row.inputUser?.nickname + '(' + row.inputUser?.deptName + ')' : ''}
          </span>
        )
      }
    },
    {
      prop: 'createTime',
      label: '添加日期',
      formatter: formatDateColumn(),
      minWidth: columnWidth.l
    },
    {
      prop: 'applyPaymentDate',
      label: '申请付款日',
      formatter: formatDateColumn(),
      minWidth: columnWidth.l
    },
    {
      prop: 'date',
      label: '付款日期',
      formatter: formatDateColumn(),
      minWidth: columnWidth.l
    },
    {
      prop: 'cashier',
      label: '出纳员',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return <span>{row.cashier?.nickname || '-'}</span>
      }
    },
    {
      prop: 'status',
      label: '支付状态',
      minWidth: columnWidth.m,
      formatter: (row) => {
        // 审核状态为不通过(3)或已取消(4)，或单据类型为冲账(3)时，显示 "/"
        if (row.auditStatus === 3 || row.auditStatus === 4 || row.prepaidFlag === 3) {
          return '/'
        }
        return getDictLabel(DICT_TYPE.PAYMENT_STATUS, row.status)
      }
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
                hasPermi="['fms:payment:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  permi: 'fms:payment:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                otherItems={[
                  {
                    isShow: [0, 3].includes(row.status),
                    otherKey: 'planPayment',
                    label: '计划付款',
                    permi: 'fms:payment:plan-payment',
                    checkAuditStatus: [
                      PaymentAuditStatus.APPROVE.status,
                      PaymentAuditStatus.PART_PAID.status,
                      PaymentAuditStatus.PART_PLANNED.status
                    ],
                    handler: (row) => {
                      openPayDialog(row)
                    }
                  },
                  {
                    isShow: [0, 3].includes(row.status),
                    otherKey: 'directPayment',
                    label: '直接付款',
                    permi: 'fms:payment:direct-payment',
                    checkAuditStatus: [
                      PaymentAuditStatus.APPROVE.status,
                      PaymentAuditStatus.PART_PAID.status,
                      PaymentAuditStatus.PART_PLANNED.status
                    ],
                    handler: (row) => {
                      openPaymentConfirm(
                        {
                          applyAmount: row.applyAmount,
                          amount: {
                            amount: row.applyAmount[0]?.amount - row.paidAmount?.amount || 0,
                            currency: row.applyAmount[0]?.currency
                          },
                          paidAmount: row.paidAmount,
                          currency: row.applyAmount[0]?.currency,
                          id: row.id,
                          inputUser: row.inputUser,
                          companyId: row.companyId,
                          acceptanceDays: row.acceptanceDays,
                          bank: row.paymentBank,
                          bankAccount: row.paymentBackAccount
                        },
                        row.paymentMethod,
                        true
                      )
                    }
                  },
                  {
                    isShow: row.auditStatus === PaymentAuditStatus.PLANNED.status,
                    otherKey: 'paymentConfirm',
                    label: '确认',
                    permi: 'fms:payment:confirm',
                    checkAuditStatus: [PaymentAuditStatus.PLANNED.status],
                    handler: (row) => {
                      openPaymentConfirm(
                        {
                          applyAmount: row.applyAmount,
                          amount: row.amount,
                          paidAmount: row.paidAmount,
                          currency: row.applyAmount[0]?.currency,
                          id: row.id,
                          inputUser: row.inputUser,
                          companyId: row.companyId,
                          acceptanceDays: row.acceptanceDays,
                          bank: row.paymentBank,
                          bankAccount: row.paymentBackAccount
                        },
                        row.paymentMethod,
                        false
                      )
                    }
                  },
                  {
                    isShow: row.auditStatus === PaymentAuditStatus.PLANNED.status,
                    otherKey: 'paymentCancel',
                    label: '取消计划付款',
                    permi: 'fms:payment:plan-payment',
                    handler: async () => {
                      handleCancelPayment(row)
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

const handleSubmit = async (data) => {
  // 提交请求
  let res = await FeesApi.submitFees(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusTableRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusTableRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '供应商')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}

const handleCancelPayment = async (row) => {
  try {
    await message.confirm('确定取消计划付款？')
    await FeesApi.cancelPlanPaymentApi(row.id)
    message.success('取消成功')
    handleRefresh()
  } catch {}
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
