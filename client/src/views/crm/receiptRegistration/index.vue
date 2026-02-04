<template>
  <eplus-table
    v-bind="{ eplusSearchSchema, eplusTableSchema }"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    key="receipt"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  />
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <ReceiptClaimDetail
        :id="key"
        @success="handleRefresh"
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
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import ReceiptClaimDetail from './ReceiptClaimDetail.vue'
import { columnWidth, formatDictColumn } from '@/utils/table'
import * as CustClaimApi from '@/api/finance/custClaim'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchAmountRange } from '@/components/EplusSearch'
// import { BpmProcessInstanceResultEnum, PaymentStatusEnum } from '@/utils/constants'
import ReceiptClaimForm from './ReceiptClaimForm.vue'
import * as UserApi from '@/api/system/user'
import { getcompanySimpleList, getCustSimpleList } from '@/api/common'

const name = 'ReceiptRegistration'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const claimDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()
const exportFileName = ref('财务付款管理.xlsx')

const eplusTableRef = ref()
defineOptions({
  name
})
const openClaimForm = (id) => {
  claimDialogRef.value?.openEdit(id, '收款认领')
}

// const handleCancelClaim = async (row) => {
//   ElMessageBox.confirm('是否确认取消认领？', '提示', {
//     confirmButtonText: '确认',
//     cancelButtonText: '取消',
//     type: 'warning'
//   }).then(() => {
//     CustClaimApi.cancelClaim(row.id).then(() => {
//       message.success('取消成功')
//       handleRefresh()
//     })
//   })
// }
const custName = ref('')
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'companyTitle',
      label: '付款抬头'
    },
    {
      component: (
        <eplus-input-search-select
          api={getCustSimpleList}
          params={{ pageSize: 100, pageNo: 1 }}
          keyword="custName"
          label="name"
          value="code"
          placeholder="请选择"
          onChangeEmit={(...e) => {
            custName.value = e[1].find((item) => item.code == e[0]).name
          }}
        />
      ),
      name: 'custCode',
      label: '客户名称',
      formatter: async (args: any[]) => {
        return custName.value
      },
      className: '!w-300px'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerId',
      label: '业务员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: (
        <eplus-custom-select
          api={getcompanySimpleList}
          label="name"
          value="id"
        />
      ),
      name: 'companyId',
      label: '入账单位',
      formatter: async (args: any[]) => {
        let list = await getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'amount', label: '入账金额', formatter: '从{0}到{1}' }]
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.CLAIM_STATUS}></eplus-dict-select>,
      name: 'claimStatus',
      label: '认领状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.CLAIM_STATUS, args[0])
      }
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await CustClaimApi.getCustClaimPage(ps)
    return {
      list: res.list,
      total: res.total
    }
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
      // formatter: formatDictColumn(DICT_TYPE.LOAN_REPAY_STATUS)
    },
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
    //   prop: 'auditStatus',
    //   label: '审核状态',
    //   minWidth: '120px',
    //   formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
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
                hasPermi="fms:cust-claim:query"
              >
                详情
              </el-button>
              <eplus-dropdown
                auditable={row}
                otherItems={[
                  {
                    isShow: row.claimStatus !== 2,
                    otherKey: 'claim',
                    label: '认领',
                    permi: 'fms:cust-claim:claim',
                    handler: async () => {
                      openClaimForm(row.id)
                    }
                  },
                  {
                    /* {
                    isShow: [1, 2].includes(row.claimStatus),
                    otherKey: 'cancelClaim',
                    label: '取消认领',
                    permi: 'fms:cust-claim:cancel',
                    handler: async () => {
                      handleCancelClaim(row)
                    }
                  } */
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

/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}

onMounted(() => {})
</script>
