<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        plain
        @click="handlePayment('list')"
      >
        批量对公付款
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #create>
      <corporate-payments-form
        :forwarderFeeInfo="forwarderFeeInfo"
        :feeShareFlag="0"
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as forwarderFeeApi from '@/api/dms/forwarderFee/index'
import { getcompanySimpleList, getVenderSimpleList } from '@/api/common'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as UserApi from '@/api/system/user'
import CorporatePaymentsForm from '@/views/oa/corporatePayments/CorporatePaymentsForm.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'ForwarderFee' })

const searchVenderName = ref('')
const getVenderName = (e) => {
  let item = e[1].find((item) => item.code === e[0])
  searchVenderName.value = item.name
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: (
        <eplus-custom-select
          api={getcompanySimpleList}
          label="name"
          value="id"
          class="!w-150px"
        />
      ),
      name: 'companyId',
      label: '公司名称',
      formatter: async (args: any[]) => {
        let list = await getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, administrationVenderType: 2 }}
          keyword="name"
          label="name"
          value="code"
          onChangeEmit={(...$event) => getVenderName($event)}
          class="!w-150px"
        />
      ),
      name: 'venderCode',
      label: '船代公司',
      formatter: async () => {
        return searchVenderName.value
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'applyerId',
      label: '申请人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await forwarderFeeApi.getForwarderFeeApplyPage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async () => {},
  exportListApi: async () => {},
  selection: true,
  columns: [
    {
      prop: 'invoiceCode',
      label: '发票号',
      minWidth: columnWidth.m
    },
    {
      prop: 'companyName',
      label: '公司名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'applyer',
      label: '申请人',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return `${row.applyer.nickname}`
        }
      }
    },
    {
      prop: 'venderName',
      label: '船代公司',
      minWidth: columnWidth.m
    },
    {
      prop: 'dictSubjectName',
      label: '费用名称',
      minWidth: columnWidth.m,
      slots: {
        default: () => {
          return `单证费用`
        }
      }
    },
    {
      prop: 'amount',
      label: '费用金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn()
    },
    {
      prop: 'payStatus',
      label: '支付状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.APPLY_FEE_STATUS)
    },
    {
      prop: 'paymentAppCode',
      label: '对公付款单号',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <el-button
              link
              type="primary"
              onClick={() => {
                toPayment(row)
              }}
            >
              {row.paymentAppCode}
            </el-button>
          )
        }
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
                  handlePayment('row', row)
                }}
                disabled={row.payStatus != 1}
              >
                申请对公付款
              </el-button>
            </div>
          )
        }
      }
    }
  ]
}
const router = useRouter()
const toPayment = (row) => {
  setSourceId(row.paymentAppId)
  if (checkPermi(['oa:payment-app:query']) && checkPermi(['oa:payment-app:detail'])) {
    router.push({ path: '/oa/payment/corporate-payments-administration' })
  } else {
    ElMessage.error('暂无权限查看详情')
  }
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
let forwarderFeeInfo = reactive({})
const handlePayment = async (type, row = {}) => {
  if (type === 'row') {
    forwarderFeeInfo = { ...row, relationType: 3, codeList: row?.codeList.split(',') }
    eplusDialogRef.value?.openCreate()
  } else if (type === 'list') {
    let checkeArr = eplusListRef.value.checkedItems
    if (!checkeArr.length) {
      message.warning('请勾选操作数据')
      return false
    }
    let isCheck = true
    let amountTotal = 0
    let codeList = []
    const companyName = checkeArr[0].companyName
    const venderName = checkeArr[0].venderName
    const currency = checkeArr[0].amount.currency

    checkeArr.forEach((item) => {
      amountTotal += item.amount.amount
      codeList = codeList.concat(item?.codeList.split(','))
      if (
        item.companyName !== companyName ||
        item.venderName !== venderName ||
        item.amount.currency !== currency ||
        item.payStatus != 1
      ) {
        isCheck = false
      }
    })
    if (!isCheck) {
      message.warning('请选择同公司同船代同币种且支付状态为未申请的数据进行操作')
      return false
    }
    forwarderFeeInfo = {
      ...checkeArr[0],
      relationType: 3,
      codeList: codeList,
      amount: { amount: amountTotal, currency: currency }
    }
    eplusDialogRef.value?.openCreate()
  }
}
</script>
<style></style>
