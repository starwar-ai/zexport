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
        plain
        @click="handleCreate()"
        v-hasPermi="['oa:payment-app:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <corporate-payments-detail :id="key" /></template>
    <template #edit="{ key }">
      <corporate-payments-form
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #create>
      <corporate-payments-form
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import * as corporatePaymentsApi from '@/api/oa/corporatePayments'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchAmountRange, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import CorporatePaymentsForm from './CorporatePaymentsForm.vue'
import CorporatePaymentsDetail from './CorporatePaymentsDetail.vue'
import FeeShareInfo from '@/views/oa/components/FeeShareInfo.vue'
import { checkPermi } from '@/utils/permission'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { currencyJsonAnalysis } from '@/utils/index'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { isValidArray } from '@/utils/is'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'PaymentPage' })

const exportFileName = computed(() => {
  return checkPermi(['oa:payment-app:export']) ? '对公转账列表.xlsx' : ''
})

const handleDialogFailure = () => {}
const pagePath = useRoute().path
const businessSubjectTypeFilter = (arr) => {
  if (pagePath === '/oa/payment/corporate-payments-administration') {
    return arr.filter((item) => item.value != 1 && item.value < 4)
  } else if (pagePath === '/oa/payment/corporate-payments-business') {
    return arr.filter((item) => item.value != 2 && item.value < 4)
  } else if (pagePath === '/oa/payment/corporate-payments') {
    return arr.filter((item) => item.value < 4)
  }
}

const searchMethods = ref({})

provide('callSearchMethods', {
  registerMethod: (name, method) => {
    searchMethods.value[name] = method
  },
  callMethod: (name, ...args) => {
    if (searchMethods.value[name]) {
      return searchMethods.value[name](...args)
    }
  }
})

const businessSubjectTypeChange = () => {
  searchMethods.value?.handleCloseTag('businessSubjectCode')
}
let eplusSearchSchema: EplusSearchSchema = reactive({
  fields: [
    {
      component: <el-input />,
      name: 'code',
      label: '费用单号'
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'payAmount', label: '支付金额', formatter: '从{0}到{1}' }]
    },
    {
      component: (
        <eplus-custom-select
          api={TravelReimbApi.getcompanySimpleList}
          label="name"
          value="id"
        />
      ),
      name: 'companyId',
      label: '费用主体',
      formatter: async (args: any[]) => {
        let list = await TravelReimbApi.getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: (
        <eplus-dict-select
          dictType={DICT_TYPE.BUSINESS_SUBJECT_TYPE}
          filter={businessSubjectTypeFilter}
          onChangeEmit={() => businessSubjectTypeChange()}
        ></eplus-dict-select>
      ),
      name: 'businessSubjectType',
      label: '支付对象类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BUSINESS_SUBJECT_TYPE, args[0])
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={corporatePaymentsApi.getCustList}
          params={{ pageSize: 100, pageNo: 1 }}
          keyword="custName"
          label="name"
          value="code"
          placeholder="请选择"
          onChangeEmit={(...$event) => getName($event)}
        />
      ),
      disabled: true,
      name: 'businessSubjectCode',
      label: '支付对象',
      formatter: async (args: any[]) => {
        return businessSubjectName.value
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={corporatePaymentsApi.getVenderList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1 }}
          keyword="name"
          label="name"
          value="code"
          placeholder="请选择"
          onChangeEmit={(...$event) => getName($event)}
        />
      ),
      disabled: true,
      name: 'businessSubjectCode',
      label: '支付对象',
      formatter: async (args: any[]) => {
        return businessSubjectName.value
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={corporatePaymentsApi.getVenderList}
          params={{ pageSize: 100, pageNo: 1, venderType: 2 }}
          keyword="name"
          label="name"
          value="code"
          placeholder="请选择"
          onChangeEmit={(...$event) => getName($event)}
        />
      ),
      disabled: true,
      name: 'businessSubjectCode',
      label: '支付对象',
      formatter: async (args: any[]) => {
        return businessSubjectName.value
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.PAYMENT_APPLY_TYPE} />,
      name: 'prepaidFlag',
      label: '单据类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.PAYMENT_APPLY_TYPE, args[0])
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
      component: <eplus-dict-select dictType={DICT_TYPE.CURRENCY_TYPE}></eplus-dict-select>,
      name: 'currency',
      label: '支付币种',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.CURRENCY_TYPE, args[0])
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'creator',
      label: '录入人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'deptId',
      label: '归属部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT} />,
      name: 'auditStatus',
      label: '审核状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.PAYMENT_STATUS} />,
      name: 'paymentStatus',
      label: '支付状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.PAYMENT_STATUS, args[0])
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.IS_INT} />,
      name: 'linkFlag',
      label: '是否被冲账',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.IS_INT, args[0])
      }
    },
    {
      component: <el-input />,
      name: 'paymentAppCode',
      label: '被冲账单据编号'
    },
    {
      component: <el-input />,
      name: 'reason',
      label: '支付事由'
    },
    {
      component: <el-input />,
      name: 'remark',
      label: '备注'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'paymentDate',
          label: '支付日期',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
})
const businessSubjectName = ref()
const getName = (e) => {
  businessSubjectName.value = e[1].find((item) => item.code == e[0]).name
}

const setSearchFields = (ps) => {
  if (ps.businessSubjectType == 1) {
    eplusSearchSchema.fields[2].disabled = true
    eplusSearchSchema.fields[3].disabled = false
    eplusSearchSchema.fields[4].disabled = true
  } else if (ps.businessSubjectType == 2) {
    eplusSearchSchema.fields[2].disabled = true
    eplusSearchSchema.fields[3].disabled = true
    eplusSearchSchema.fields[4].disabled = false
  } else if (ps.businessSubjectType == 3) {
    eplusSearchSchema.fields[2].disabled = false
    eplusSearchSchema.fields[3].disabled = true
    eplusSearchSchema.fields[4].disabled = true
  } else {
    eplusSearchSchema.fields[2].disabled = true
    eplusSearchSchema.fields[3].disabled = true
    eplusSearchSchema.fields[4].disabled = true
  }
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    setSearchFields(ps)
    let typeList = '1,2,3'
    if (pagePath === '/oa/payment/corporate-payments-administration') {
      typeList = '2,3'
    } else if (pagePath === '/oa/payment/corporate-payments-business') {
      typeList = '1,3'
    } else if (pagePath === '/oa/payment/corporate-payments') {
      typeList = '1,2,3'
    }

    const res = await corporatePaymentsApi.getPaymentAppPage({
      ...ps,
      businessSubjectTypeList: typeList
    })
    return {
      list: res?.list || [],
      total: res?.total || 0,
      sum: {
        amount: currencyJsonAnalysis(res?.summary?.amountSum),
        invoiceAmount: currencyJsonAnalysis(res?.summary?.invoiceAmountSum)
      }
    }
  },
  delListApi: async (id) => {
    await corporatePaymentsApi.deletePaymentApp(id)
  },
  exportListApi: async (ps) => {
    return await corporatePaymentsApi.exportPaymentApp(ps)
  },
  selection: true,
  summary: true,
  columns: [
    {
      prop: 'code',
      label: '费用单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'amount',
      label: '支付金额',
      minWidth: columnWidth.m,
      summary: true,
      summarySlots: {
        default: (data) => {
          const amount = Number(data?.amount || 0)
          const currency = data?.currency || ''
          return {
            unit: currency,
            number: amount.toFixed(3)
          }
        }
      },
      formatter: formatMoneyColumn()
    },
    {
      prop: 'invoiceAmount',
      label: '发票金额',
      minWidth: columnWidth.m,
      summary: true,
      summarySlots: {
        default: (data) => {
          const amount = Number(data?.amount || 0)
          const currency = data?.currency || ''
          return {
            unit: currency,
            number: amount.toFixed(3)
          }
        }
      },
      formatter: formatMoneyColumn()
    },
    {
      prop: 'companyName',
      label: '费用主体',
      minWidth: columnWidth.m
    },
    {
      prop: 'businessSubjectType',
      label: '支付对象类型',
      minWidth: columnWidth.l,
      formatter: formatDictColumn(DICT_TYPE.BUSINESS_SUBJECT_TYPE)
    },
    {
      prop: 'businessSubjectName',
      label: '支付对象',
      minWidth: columnWidth.m
    },
    {
      prop: 'prepaidFlag',
      label: '单据类型',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.PAYMENT_APPLY_TYPE)
    },
    {
      prop: 'createTime',
      label: '创建日期',
      minWidth: columnWidth.xl,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'createUserName',
      label: '录入人',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return `${row.createUser.nickname}（${row.createUser.deptName}）`
      }
    },
    {
      prop: 'feeShareDetail',
      label: '费用归集',
      minWidth: columnWidth.xxl,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.prepaidFlag < 3) {
            return <FeeShareInfo info={row.feeShare} />
          } else {
            return '/'
          }
        }
      }
    },
    {
      prop: 'linkFlag',
      label: '是否被冲账',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.prepaidFlag == 1) {
            return getDictLabel(DICT_TYPE.IS_INT, row.linkFlag)
          } else {
            return ''
          }
        }
      }
    },
    {
      prop: 'paymentAppList',
      label: '被冲账单据编号',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          if (isValidArray(row.paymentAppDTOList)) {
            return (
              <div class="flex flex-wrap !w-100%">
                {row.paymentAppDTOList.map((item) => {
                  return (
                    <div>
                      <el-button
                        link
                        type="primary"
                        onClick={() => {
                          handleDetail(item.id)
                        }}
                      >
                        {item.code}
                      </el-button>
                    </div>
                  )
                })}
              </div>
            )
          } else {
            return ''
          }
        }
      }
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'reason',
      label: '支付事由',
      minWidth: columnWidth.l
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: columnWidth.l
    },
    {
      prop: 'paymentStatus',
      label: '支付状态',
      minWidth: columnWidth.m,
      formatter: (row) => {
        // 审核状态为不通过(3)或已取消(4)，或单据类型为冲账(3)时，显示 "/"
        if (row.auditStatus === 3 || row.auditStatus === 4 || row.prepaidFlag === 3) {
          return '/'
        }
        return getDictLabel(DICT_TYPE.PAYMENT_STATUS, row.paymentStatus)
      }
    },
    {
      prop: 'paymentDate',
      label: '支付时间',
      minWidth: columnWidth.l,
      formatter: formatDateColumn('YYYY-MM-DD')
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
                hasPermi="['oa:payment-app:detail']"
                class="mr-10px"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  user: row.applyer,
                  permi: 'oa:payment-app:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  user: row.applyer,
                  permi: 'oa:payment-app:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
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

// const handleSubmit = async (data) => {
//   // 提交请求
//   let res = await corporatePaymentsApi.submitPaymentApp(data.id)
//   if (res) {
//     message.success('已提交审核！')
//   }
//   await eplusListRef.value.handleRefresh()
// }

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

onMounted(() => {
  // eplusSearchSchema.fields = [...searchFields, ...eplusSearchSchema.fields]
})

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
