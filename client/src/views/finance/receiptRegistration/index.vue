<template>
  <eplus-table
    v-bind="{ eplusSearchSchema, eplusTableSchema }"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    key="receipt"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        plain
        v-hasPermi="['fms:bank-registration:create']"
        @click="
          () => {
            handleCreate()
          }
        "
      >
        收款登记
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <ReceiptDetail
        :id="key"
        @success="handleRefresh"
      />
    </template>
    <template #create>
      <ReceiptForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key }">
      <ReceiptForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="claimDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #edit="{ key }">
      <ReceiptClaimForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import ReceiptDetail from './ReceiptDetail.vue'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as RegistrationApi from '@/api/finance/receiptRegistration'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import ReceiptForm from './ReceiptForm.vue'
import ReceiptClaimForm from './ReceiptClaimForm.vue'
import * as CustClaimApi from '@/api/finance/custClaim'
import * as UserApi from '@/api/system/user'

const name = 'Receivables'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const claimDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusTableRef = ref()
defineOptions({
  name
})
const ClaimFormRef = ref()
const openClaimForm = (code, id) => {
  ClaimFormRef.value.open(code, id)
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'companyName',
      label: '收款主体'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'bank',
      label: '入账银行'
    },
    // {
    //   component: <el-input clearable></el-input>,
    //   name: 'nameShort',
    //   label: '简称'
    // },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'amount',
      label: '入账金额'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'companyTitle',
      label: '付款抬头'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerId',
      label: '业务员',
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
    const res = await RegistrationApi.getRegistrationPage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await RegistrationApi.deleteRegistration(id)
  },
  exportListApi: async (ps) => {
    return await RegistrationApi.exportRegistration(ps)
  },
  columns: [
    {
      prop: 'code',
      label: '收款登记编号',
      minWidth: columnWidth.l,
      isHyperlink: true
    },
    {
      prop: 'companyTitle',
      label: '付款抬头',
      minWidth: columnWidth.m
    },
    {
      prop: 'custList',
      label: '客户名称',
      minWidth: columnWidth.l,
      showOverflowTooltip: true,
      formatter: (row) => {
        return row.custList?.map((item) => item.name).join(',')
      }
    },
    {
      prop: 'custCodeList',
      label: '客户编号',
      minWidth: columnWidth.l,
      showOverflowTooltip: true,
      formatter: (row) => {
        return row.custList?.map((item) => item.code).join(',')
      }
    },
    {
      prop: 'managerList',
      label: '业务员',
      minWidth: columnWidth.l,
      showOverflowTooltip: true,
      formatter: (row) => {
        return row.managerList?.map((item) => item.nickname).join(',')
      }
    },
    {
      prop: 'companyName',
      label: '入账单位',
      minWidth: columnWidth.l
    },
    {
      prop: 'bank',
      label: '入账银行账户',
      minWidth: columnWidth.l
    },
    {
      prop: 'bankPostingDate',
      label: '银行入账日期',
      minWidth: columnWidth.l,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    // {
    //   prop: 'currency',
    //   label: '入账币别',
    //   minWidth: columnWidth.m
    // },
    {
      prop: 'amount',
      label: '入账金额',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return (
          <span>
            {
              <EplusMoneyLabel
                val={{
                  amount: row.amount,
                  currency: row.currency
                }}
              />
            }
          </span>
        )
      }
    },
    {
      prop: 'registeredByNickName',
      label: '登记人',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return row.registeredBy?.nickname
      }
    },
    {
      prop: 'registeredByDeptName',
      label: '登记人部门',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return row.registeredBy?.deptName
      }
    },
    // {
    //   prop: 'businessSubjectType',
    //   label: '收款对象类型',
    //   minWidth: columnWidth.m,
    //   formatter: formatDictColumn(DICT_TYPE.BUSINESS_SUBJECT_TYPE)
    // },
    {
      prop: 'claimStatus',
      label: '认领状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.CLAIM_STATUS)
    },
    // {
    //   prop: 'status',
    //   label: '单据状态',
    //   minWidth: columnWidth.m,
    //   formatter: formatDictColumn(DICT_TYPE.SUBMIT_STATUS)
    // },
    // {
    //   prop: 'custCode',
    //   label: '客户编号',
    //   minWidth: '100px'
    //   // formatter: formatDictColumn(DICT_TYPE.LOAN_TYPE)
    // },
    // {
    //   prop: 'custName',
    //   label: '客户名称',
    //   minWidth: '100px'
    //   // formatter: formatDictColumn(DICT_TYPE.LOAN_TYPE)
    // },
    // {
    //   prop: 'manager',
    //   label: '业务员',
    //   minWidth: '140px',
    //   formatter: (row) => {
    //     return (
    //       <span>
    //         {row.manager?.nickname
    //           ? row.manager?.nickname + '(' + (row.manager?.deptName || '-') + ')'
    //           : ''}
    //       </span>
    //     )
    //   }
    // },

    // {
    //   prop: 'claimManager',
    //   label: '认领业务员',
    //   minWidth: '140px',
    //   formatter: (row) => {
    //     return (
    //       <span>
    //         {row.claimManager?.nickname
    //           ? row.claimManager?.nickname + '(' + (row.claimManager?.deptName || '-') + ')'
    //           : ''}
    //       </span>
    //     )
    //   }
    // },

    // {
    //   prop: 'claimDate',
    //   label: '认领日期',
    //   formatter: formatDateColumn(),
    //   width: '120px'
    // },
    // {
    //   prop: 'linkSaleContractCode',
    //   label: '关联外销合同号',
    //   minWidth: '100px'
    //   // formatter: formatDictColumn(DICT_TYPE.LOAN_TYPE)
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
            <div class="flex items-center">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['fms:bank-registration:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                auditable={row}
                otherItems={[
                  {
                    isShow: row.claimStatus === 0,
                    otherKey: 'otherEdit',
                    label: '编辑',
                    permi: 'fms:bank-registration:update',
                    handler: async () => {
                      handleUpdate(row.id)
                    }
                  },
                  {
                    isShow: row.claimStatus === 0,
                    otherKey: 'otherDelete',
                    label: '删除',
                    permi: 'fms:bank-registration:delete',
                    handler: async () => {
                      await handleDelete(row.id)
                    }
                  },
                  {
                    isShow: [1, 2].includes(row.claimStatus),
                    otherKey: 'cancelClaim',
                    label: '取消认领',
                    permi: 'fms:cust-claim:cancel',
                    handler: async () => {
                      handleCancelClaim(row)
                    }
                  }
                ]}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    }
  ]
}
const exportFileName = ref('财务付款管理.xlsx')

const handleCancelClaim = async (row) => {
  ElMessageBox.confirm('是否确认取消认领？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    CustClaimApi.cancelClaim(row.id).then(() => {
      message.success('取消成功')
      handleRefresh()
    })
  })
}

const handleExport = async () => {
  return await eplusTableRef.value.exportList('财务付款管理.xlsx')
}
const handleSubmit = async (data) => {
  // 提交请求
  let res = await RegistrationApi.submitRegistration(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusTableRef.value.refresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusTableRef.value.delList(id, false)
}

const handleClaimUpdate = (id: number) => {
  claimDialogRef.value?.openEdit(id, '认领')
}
const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '财务收款')
}
claimDialogRef
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('收款登记')
}
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}

onMounted(() => {})
</script>
