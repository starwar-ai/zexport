<template>
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
    >
      <el-tab-pane
        label="全部"
        :name="6"
      />

      <el-tab-pane
        label="待提交"
        :name="0"
      />
      <el-tab-pane
        label="待审核"
        :name="1"
      />
      <el-tab-pane
        label="待支付"
        :name="2"
      />
      <el-tab-pane
        label="已支付"
        :name="5"
      />
      <el-tab-pane
        label="已作废"
        :name="10"
      />
    </el-tabs>
  </div>
  <eplus-table
    v-bind="{ eplusSearchSchema, eplusTableSchema }"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  />
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <!-- <template #create>
      <PaymentApplyDetail
        mode="create"
        @handleSuccess="handleRefresh"
      />
    </template> -->
    <template #edit="{ key }">
      <PaymentApplyForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <PaymentApplyDetail :id="key" />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as VenderApi from '@/api/scm/vender'
import PaymentApplyDetail from './PaymentApplyDetail.vue'
import PaymentApplyForm from './PaymentApplyForm.vue'
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import { EplusSearchAmountRange } from '@/components/EplusSearch'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { moneyTotalPrecision } from '@/utils/config'
import { formatNum } from '@/utils'

defineOptions({ name: 'PaymentApply' })

const activeName = ref(6)
const eplusTableRef = ref()
const message = useMessage()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
const handleDialogFailure = () => {}

const handleUpdate = (id: any) => {
  eplusDialogRef.value?.openEdit(id, '付款申请')
}
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
// 删除按钮操作
const handleDelete = async (id: number) => {
  try {
    await eplusTableRef.value.delList(id, false)
  } catch (error) {
    message.error('删除失败')
  }
}
const handleSubmit = async (data) => {
  // 提交请求
  let res = await VenderApi.submitVender(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusTableRef.value.refresh()
}

// 反提交
const handleReverseSubmit = async (processInstanceId) => {
  let res = await VenderApi.reverseSubmit(processInstanceId)
  if (res) {
    message.success('反提交成功')
  }
  await eplusTableRef.value.refresh()
}

// 反审核
const handleReverseAudit = async (id) => {
  let res = await VenderApi.reversePaymentAudit(id)
  if (res) {
    message.success('反审核成功')
  }
  await eplusTableRef.value.refresh()
}
let normalColumns = [
  {
    prop: 'code',
    label: '付款申请编号',
    minWidth: columnWidth.l,
    isHyperlink: true
  },
  {
    prop: 'companyName',
    label: '付款单位',
    minWidth: columnWidth.l
  },
  {
    prop: 'applyDate',
    label: '申请日期',
    minWidth: columnWidth.m,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    prop: 'applyPaymentDate',
    label: '申请付款日',
    minWidth: columnWidth.l,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  // {
  //   prop: 'paymentRatio',
  //   label: '计划付款比例',
  //   minWidth: '120px'
  // },
  // {
  //   prop: 'companyName',
  //   label: '申请比例付款',
  //   minWidth: '120px'
  // },
  {
    prop: 'applyTotalAmount',
    label: '申请总金额',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <EplusMoneyLabel
          val={{
            amount: row.applyTotalAmount,
            currency: row.currency
          }}
        />
      )
    }
  },
  {
    prop: 'thisApplyTotalAmount',
    label: '本次申请总金额',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <EplusMoneyLabel
          val={{
            amount: formatNum(
              row.applyerPurchaseItemList.reduce((prev, cur) => prev + cur.applyAmount, 0),
              moneyTotalPrecision
            ),
            currency: row.currency
          }}
        />
      )
    }
  },
  {
    prop: 'goodsTotalAmount',
    label: '货款金额',
    minWidth: columnWidth.m,
    formatter: (row) => {
      return (
        <EplusMoneyLabel
          val={{
            amount: row.goodsTotalAmount,
            currency: row.currency
          }}
        />
      )
    }
  },
  {
    prop: 'paymentMethod',
    label: '支付方式',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.PAY_METHOD)
  },
  // {
  //   prop: 'paymentName',
  //   label: '付款类型',
  //   minWidth: columnWidth.m
  // },
  {
    prop: 'purchaseContractCodeList',
    label: '关联采购合同',
    minWidth: columnWidth.l,
    formatter: (row) => {
      if (Array.isArray(row?.purchaseContractCodeList)) {
        return row?.purchaseContractCodeList?.join(',')
      }
    }
  },
  // {
  //   prop: 'saleContractCode',
  //   label: '关联销售合同',
  //   minWidth: '120px'
  // },
  {
    prop: 'venderName',
    label: '供应商名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'applyer',
    label: '申请人',
    minWidth: columnWidth.m,
    formatter: (row) => {
      return row?.applyer?.nickname ? row.applyer.nickname : ''
    }
  },
  {
    prop: 'applyerDept',
    label: '申请部门',
    minWidth: columnWidth.m,
    formatter: (row) => {
      return row?.applyer?.deptName ? row.applyer.deptName : ''
    }
  },
  {
    prop: 'auditStatus',
    label: '审核状态',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
  },
  {
    prop: 'printFlag',
    label: '打印状态',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.PRINT_FLAG)
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
              hasPermi="['scm:payment-apply:detail']"
            >
              详情
            </el-button>
            <eplus-dropdown
              //submitItem={{
              //  permi: 'scm:payment-apply:submit',
              //  handler: async (row) => {
              //    await handleSubmit(row)
              //  }
              //}}
              editItem={{
                permi: 'scm:payment-apply:update',
                handler: async (row) => {
                  await handleUpdate(row.id)
                }
              }}
              //deleteItem={{
              //  permi: 'scm:payment-apply:delete',
              //  handler: async (row) => {
              //    await handleDelete(row.id)
              //  }
              //}}
              otherItems={[
                {
                  isShow: row.auditStatus == 1 ? true : false,
                  otherKey: 'reverseSubmit',
                  label: '反提交',
                  permi: 'scm:payment-apply:cancel',
                  handler: (row) => {
                    handleReverseSubmit(row.processInstanceId)
                  }
                },
                {
                  isShow:
                    [0, 3, 4].includes(row.auditStatus) ||
                    (row.auditStatus === 2 && row.paymentStatus == 0),
                  otherKey: 'scmPaymentClose',
                  label: '作废',
                  permi: 'scm:payment-apply:close',
                  handler: (row) => {
                    handleClose(row)
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

const handleClose = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入作废原因', '作废', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/, // 判断非空，且非空格
    inputErrorMessage: '作废原因不能为空'
  })
  const data = {
    id: row.id,
    cancelReason: value
  }
  await VenderApi.closePayment(data)
  message.success('作废成功')
  handleRefresh()
}
let eplusSearchSchema: EplusSearchSchema = reactive({
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '付款申请编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'venderName',
      label: '供应商名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'companyName',
      label: '付款单位'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'applyerId',
      label: '申请人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择部门"></eplus-dept-select>,
      name: 'applyerDeptId',
      label: '申请人部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.PAY_METHOD}></eplus-dict-select>,
      name: 'paymentMethod',
      label: '支付方式',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.PAY_METHOD, args[0])
      }
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'applyTotalAmount', label: '申请总金额', formatter: '从{0}到{1}' }]
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
    }
  ],
  moreFields: []
})
let eplusTableSchema = reactive({
  getListApi: async (ps) => {
    const res = await VenderApi.getPaymentApplyPage({
      excludeAuditStatus: activeName.value === 6 ? [10] : undefined,
      auditStatus:
        activeName.value === 6 ? undefined : activeName.value === 5 ? 2 : activeName.value,
      paymentStatus: activeName.value === 2 ? 0 : activeName.value === 5 ? 1 : undefined,
      ...ps
    })
    return {
      list: res?.list || [],
      total: res?.total
    }
  },
  delListApi: async (id) => {
    await VenderApi.deletePaymentApply(id)
  },
  exportListApi: async (ps) => {
    return await VenderApi.exportPaymentApply(ps)
  },
  columns: normalColumns
})
onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style lang="scss" scoped>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
