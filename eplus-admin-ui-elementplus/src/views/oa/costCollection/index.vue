<template>
  <div class="h-45px bg-[var(--left-menu-bg-color)] pl-15px pr-15px">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        name="all"
        key="0"
      />
      <el-tab-pane
        label="未归集"
        name="0"
        key="1"
      />
      <el-tab-pane
        label="处理中"
        name="1"
        key="2"
      />
      <el-tab-pane
        label="已归集"
        name="2"
        key="3"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    key="transferOrder"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <div class="w-100% flex justify-between">
        <!-- 页面按钮 -->
      </div>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="FeeShareDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <CostCollectionDetail
        :rowId="rowIdRef"
        :param="processInstanceIdRef"
        :status="statusRef"
        :businessType="businessTypeRef"
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key, param }">
      <FeeShareForm
        :getApi="selectApiRef"
        :updateApi="
          channelRef === 'send' && rowStatus === 0
            ? EmsListApi.updateFeeShare
            : FeeShareApi.updateFeeShare
        "
        :channel="channelRef"
        :businessType="businessTypeRef"
        :id="key"
        :businessCode="businessCodeRef"
        :businessId="businessIdRef"
        mode="edit"
        :feeShareAmount="param"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as TransferOrderApi from '@/api/wms/transferOrder'
import { BpmProcessInstanceResultEnum, CostTypes } from '@/utils/constants'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import * as EmsListApi from '@/api/ems/emsList' // 寄件接口集合
import * as GeneralApi from '@/api/oa/generalExpense' //一般费用报销接口
import * as EntertainApi from '@/api/oa/entertain'
import * as corporatePaymentsApi from '@/api/oa/corporatePayments'
import * as OtherApi from '@/api/oa/otherExpense'
import FeeShareForm from '../../ems/send/components/FeeShare/FeeShareForm.vue'
import * as FeeShareApi from '@/api/oa/feeShare'
import { forwarderFeeDetail } from '@/api/dms/forwarderCompanyInfo'
import FeeShareInfo from '@/views/oa/components/FeeShareInfo.vue'
import CostCollectionDetail from './CostCollectionDetail.vue'

defineOptions({ name: 'CostCollection' })

const message = useMessage()
const activeName = ref('all')
const eplusTableRef = ref<any>(null)
const channelRef = ref<any>(null)
const businessIdRef = ref<any>(null)
const businessCodeRef = ref<any>(null)
const businessTypeRef = ref<any>(null)
//不同费用类型对应的弹窗
const FeeShareDialogRef = ref<InstanceType<typeof EplusDialog> | null>(null)

const processInstanceIdRef = ref()
const statusRef = ref()
const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const selectApi = async (type: number) => {
  switch (type) {
    case CostTypes.TRAVELREIMB.status:
      selectApiRef.value = TravelReimbApi.getTravelReimb
      channelRef.value = 'travelReimb'
      break
    case CostTypes.GENERAL.status:
      selectApiRef.value = GeneralApi.getGeneralReimb
      channelRef.value = 'generalReimb'
      break
    case CostTypes.ENTERTAINMENT.status:
      selectApiRef.value = EntertainApi.getEntertainInfo
      channelRef.value = 'entertain'
      break
    case CostTypes.CORPORATE.status:
      selectApiRef.value = corporatePaymentsApi.getPaymentAppInfo
      channelRef.value = 'corporatePayments'
      break
    case CostTypes.SEND.status:
      selectApiRef.value = EmsListApi.getEmSDetail
      channelRef.value = 'send'
      break
    case CostTypes.FORWARDER.status:
      selectApiRef.value = forwarderFeeDetail
      channelRef.value = 'forwarder'
      break
    case CostTypes.OTHER.status:
      selectApiRef.value = OtherApi.getOtherReimb
      channelRef.value = 'other'
      break
    default:
      break
  }
  businessTypeRef.value = type
}
const selectApiRef: any = ref()
//EplusTable组件的搜索表单配置
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      name: 'businessCode',
      label: '来源单号',
      component: <el-input />
    },
    {
      component: (
        <eplus-custom-select
          api={TravelReimbApi.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="公司主体"
          class="!w-150px"
        />
      ),
      name: 'companyId',
      label: '公司主体',
      formatter: async (args: any[]) => {
        let list = await TravelReimbApi.getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: (
        <eplus-dict-select
          dictType={DICT_TYPE.FEE_SHARE_BUSINESS_TYPE}
          clearable
        ></eplus-dict-select>
      ),
      name: 'businessType',
      label: '费用类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.FEE_SHARE_BUSINESS_TYPE, args[0])
      }
    },
    {
      component: (
        <eplus-dict-select
          dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}
          clearable
        ></eplus-dict-select>
      ),
      name: 'auditStatus',
      label: '审批状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'inputUserId',
      label: '申请人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'inputDeptId',
      label: '申请人部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },

    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'planDate',
          label: '创建日期',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}
//tab切换
const handleTabsClick = (val) => {
  activeName.value = val.props.name
  handleRefresh()
}
//EplusTable组件的表格配置
let eplusTableSchema: any = {
  getListApi: async (ps) => {
    const res = await FeeShareApi.getFeeSharePage({
      ...ps,
      status: activeName.value === 'all' ? undefined : activeName.value
    })
    return {
      list: res.list,
      total: res.total,
      sum: { quantity: res?.summary?.qtySum }
    }
  },
  delListApi: async (id) => {
    await TransferOrderApi.deleteTransferOrder(id)
  },
  exportListApi: async (ps) => {
    return await TransferOrderApi.exportTransferOrder(ps)
  },
  columns: [
    {
      prop: 'businessCode',
      label: '来源单号',
      minWidth: columnWidth.m
    },
    {
      prop: 'feeShareDetail',
      label: '费用归集',
      minWidth: columnWidth.xxl,
      slots: {
        default: (data) => {
          const { row } = data
          return <FeeShareInfo detailList={row.feeShareDetailList} />
        }
      }
    },
    {
      prop: 'companyName',
      label: '公司主体',
      minWidth: columnWidth.m
    },
    {
      prop: 'businessType',
      label: '费用类型',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.FEE_SHARE_BUSINESS_TYPE)
    },
    {
      prop: 'amount',
      label: '费用金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn()
    },
    {
      prop: 'inputUser',
      label: '申请人',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return <div>{row.inputUser?.nickname ? row.inputUser.nickname : ''}</div>
        }
      }
    },
    {
      prop: 'deptName',
      label: '申请人部门',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return <div>{row.inputUser?.deptName ? row.inputUser.deptName : ''}</div>
        }
      }
    },
    {
      prop: 'createTime',
      label: '创建日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    // {
    //   prop: 'status',
    //   label: '费用是否归集',
    //   minWidth: '120px',
    //   formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
    // },
    {
      prop: 'paymentStatus',
      label: '支付状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.PAYMENT_STATUS)
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
                  handleDetail(row)
                }}
                hasPermi="['fms:receipt:detail']"
              >
                详情
              </el-button>
              {row?.auditStatus !== BpmProcessInstanceResultEnum.PROCESS.status &&
                row?.businessType !== CostTypes.FORWARDER.status && (
                  <el-button
                    style="width: 104px"
                    link
                    type="primary"
                    onClick={() => {
                      handleCreate(row)
                    }}
                    hasPermi="['fms:receipt:detail']"
                  >
                    费用归集
                  </el-button>
                )}
            </div>
          )
        }
      }
    }
  ]
}
//刷新表格
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
//弹窗操作失败
const handleDialogFailure = () => {}
//打开新增
const rowStatus = ref<any>(null)
const handleCreate = (row) => {
  selectApi(row.businessType)
  businessIdRef.value = row.businessId
  businessCodeRef.value = row.businessCode
  rowStatus.value = row.status
  // if (
  //   row.businessType === CostTypes.ENTERTAINMENT.status ||
  //   row.businessType === CostTypes.CORPORATE.status
  // ) {
  //   FeeShareDialogRef.value?.openEdit(row.id, '费用归集')
  // } else {
  FeeShareDialogRef.value?.openEdit(row.id, '费用归集', row?.amount)
  // }
}

const rowIdRef = ref('')
// 打开详情
const handleDetail = (row) => {
  processInstanceIdRef.value = row?.processInstanceId
  statusRef.value = row?.auditStatus
  const { businessType, businessId } = row
  businessTypeRef.value = businessType
  rowIdRef.value = row.id
  FeeShareDialogRef.value?.openDetail(businessId)
  // switch (businessType) {
  //   case CostTypes.TRAVELREIMB.status:
  //     TravelReimbDialogRef.value?.openDetail(businessId)
  //     break
  //   case CostTypes.GENERAL.status:
  //     GeneralDialogRef.value?.openDetail(businessId)
  //     break
  //   case CostTypes.ENTERTAINMENT.status:
  //     EntertainmentExpensesDialogRef.value?.openDetail(businessId)
  //     break
  //   case CostTypes.CORPORATE.status:
  //     CorporatePaymentsDialogRef.value?.openDetail(businessId)
  //     break
  //   case CostTypes.SEND.status:
  //     SendDialogRef.value?.openDetail(businessId)
  //     break
  //   case CostTypes.FORWARDER.status:
  //     ForwarderDialogRef.value?.openDetail(businessId)
  //     break
  //   default:
  //     break
  // }
}
</script>
<style lang="scss" scoped></style>
