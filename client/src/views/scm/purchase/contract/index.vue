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
        key="8"
      />
      <el-tab-pane
        name="first"
        key="0"
      >
        <template #label>
          <el-badge
            :value="changeCount"
            :hidden="changeCount <= 0"
          >
            <span>上游变更</span>
          </el-badge>
        </template>
      </el-tab-pane>
      <el-tab-pane
        label="待提交"
        name="1"
        key="1"
      />
      <el-tab-pane
        label="待审批"
        name="2"
        key="2"
      />
      <el-tab-pane
        label="已驳回"
        name="3"
        key="3"
      />
      <el-tab-pane
        label="待回签"
        name="4"
        key="4"
      />
      <el-tab-pane
        label="待出运"
        name="5"
        key="5"
      />
      <el-tab-pane
        label="已完成"
        name="6"
        key="6"
      />

      <el-tab-pane
        label="已作废"
        name="7"
        key="7"
      />
      <el-tab-pane
        label="内部流通"
        name="auto"
        key="9"
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
      <div class="w-100% flex justify-between">
        <div class="flex-1 overflow-x-auto">
          <span
            v-for="item in btnSchemas.filter((schema) => !schema.align)"
            :key="item.index"
            class="mr-2"
          >
            <component
              v-if="item.component"
              :is="item.component"
            />
          </span>
        </div>

        <div class="flex flex-col items-end pr-4">
          <span
            v-for="item in btnSchemas.filter((schema) => schema.align === 'right')"
            :key="item.index"
          >
            <component
              v-if="item.component"
              :is="item.component"
              class="ml-2"
            />
          </span>
        </div>
        <!-- <div class="flex flex-col items-end pr-4">
          <el-button
            v-if="['4', '5'].includes(activeName)"
            type="primary"
            plain
            @click="handleToAuxiliaryPlan"
            v-hasPermi="['scm:purchase-contract:auxiliary']"
          >
            下推包材采购计划
          </el-button>
        </div> -->
      </div>
    </template>

    <!-- <template #tableActionsFixRight>
      <el-button
        type="primary"
        plain
        @click="jumpToPurchaseBoard"
        v-hasPermi="['scm:purchase-contract:board']"
      >
        采购合同看板
      </el-button>
    </template> -->
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <ContractForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key, param }">
      <ContractForm
        :type="param"
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <ContractDetail
        ref="ContractDetailRef"
        type="purchase"
        :id="key"
        @handle-success="handleRefresh"
        @handle="detailAction"
      />
    </template>
    <template #confirm="{ key }">
      <ExportSaleContractConfirm
        :id="key"
        mode="confirm"
        :row="confirmRow"
        :type="2"
        @handle-success="handleRefresh"
        @success="backChange"
      />
    </template>
  </eplus-dialog>

  <!-- <eplus-dialog
    ref="changeDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create="{ key }">
      <ChangeForm
        mode="create"
        :id="key"
      />
    </template>
  </eplus-dialog> -->
  <ToWareHouseDialog
    ref="ToWareHouseDialogRef"
    @sure="handleRefresh"
  />
  <ToInspectDialog
    ref="ToInspectDialogRef"
    @sure="handleRefresh"
  />
  <ToPaymentApplyDialog
    ref="ToPaymentApplyDialogRef"
    @sure="handleRefresh"
  />
  <SignBackForm
    ref="signBackRef"
    @success="handleRefresh"
  />
  <ToOrderNotice
    ref="ToOrderNoticeRef"
    @success="handleRefresh"
  />

  <ToAuxiliaryPlan
    ref="ToAuxiliaryPlanRef"
    @success="handleRefresh"
  />

  <ProducedDia
    ref="ProducedDiaRef"
    @success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
// import { ElImage } from 'element-plus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchAmountRange, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import {
  ConvertInvoiceFlagEnum,
  PurchaseContractStatusEnum,
  PurchaseTypeEnum
} from '@/utils/constants'
import { propTypes } from '@/utils/propTypes'
import {
  columnWidth,
  formatDateColumn,
  formatDictColumn,
  formatMoneyColumn,
  formatNumColumn
} from '@/utils/table'
// import { useCountryStore } from '@/store/modules/countrylist'
import ContractForm from './ContractForm.vue'
// import ChangeForm from '../change/ChangeForm.vue'
import ContractDetail from './ContractDetail.vue'
import ToWareHouseDialog from './ToWareHouseDialog.vue'
import ToInspectDialog from './ToInspectDialog.vue'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import ToPaymentApplyDialog from '../components/ToPaymentApplyDialog.vue'
import SignBackForm from '../components/SignBackForm.vue'
import ExportSaleContractConfirm from '@/views/sms/sale/exportSaleContract/ExportSaleContractConfirm.vue'
import ToOrderNotice from '../components/ToOrderNotice.vue'
import ToAuxiliaryPlan from './ToAuxiliaryPlan.vue'
import ProducedDia from './ProducedDia.vue'
import { isValidArray } from '@/utils/is'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { usePaymentStore } from '@/store/modules/payment'
import download from '@/utils/download'
import { useAppStore } from '@/store/modules/app'
import { getcompanySimpleList } from '@/api/common'
import { currencyJsonAnalysis, formatNum, formatterPrice, openPdf } from '@/utils/index'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { moneyTotalPrecision } from '@/utils/config'
import { EplusCSkuCode } from '@/components/EplusSkuName'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { EplusSkuName } from '@/components/EplusSkuName'

defineOptions({ name: 'PurchaseContract' })

const ToInspectDialogRef = ref()
const ToOrderNoticeRef = ref()
const ToPaymentApplyDialogRef = ref()
const handleDialogFailure = () => {}
const eplusListRef = ref()
const ToWareHouseDialogRef = ref()
const activeName = ref('all')
const { push } = useRouter()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const signBackRef = ref()
const confirmRow = ref<any>({})
const paymentListData = usePaymentStore()
const handleTabsClick = (val) => {
  activeName.value = val.props.name
  btnSchemasFormat(val.index)
  handleRefresh()
}

const handleRefresh = async () => {
  eplusDialogRef.value?.close()
  await eplusListRef.value.handleRefresh()
}

const message = useMessage()

let btnSchemas: any = reactive([])
const props = defineProps({
  type: propTypes.string.def('')
})

let companyName = ref('')
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '采购单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '中文品名'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'nameEng',
      label: '英文品名'
    },
    {
      name: 'companyId',
      label: '采购主体',
      component: (
        <eplus-custom-select
          api={getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择"
          style="width:100%"
          onChange={(item) => {
            companyName.value = item?.name || ''
          }}
        />
      ),
      formatter: async (args: any[]) => {
        return companyName.value
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'vender',
      label: '供应商'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'venderCode',
      label: '供应商编码'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaseUserId',
      label: '采购员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'purchaseUserDeptId',
      label: '采购员部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'salesUserId',
      label: '业务员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerId',
      label: '跟单员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
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
        },
        {
          name: 'purchaseTime',
          label: '采购日期',
          formatter: '从{0}到{1}'
        },
        {
          name: 'deliveryDate',
          label: '交货日期',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: (
        <eplus-dict-select
          dictType={DICT_TYPE.IS_INT}
          clearable
        />
      ),
      name: 'advantageFlag',
      label: '优势产品',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.IS_INT, args[0])
      }
    }
  ],
  moreFields: [
    {
      component: <eplus-dict-select dictType={DICT_TYPE.CURRENCY_TYPE}></eplus-dict-select>,
      name: 'currency',
      label: '币种',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.CURRENCY_TYPE, args[0])
      }
    },
    {
      component: (
        <el-select
          clearable
          ref="paymentListRef"
          style="width: 100%"
        >
          {paymentListData.paymentList.list.map((item: any, index) => {
            return (
              <el-option
                v-for={item in paymentListData.paymentList.list}
                key={item.id}
                label={item.name}
                value={item.id}
              />
            )
          })}
        </el-select>
      ),
      name: 'paymentId',
      label: '付款方式',
      formatter: async (args: any[]) => {
        return paymentListData.paymentList.list.find((item) => item.id == args[0]).name
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
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'withTaxPrice', label: '含税单价', formatter: '从{0}到{1}' }]
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'withTaxPriceRmb', label: '人民币单价', formatter: '从{0}到{1}' }]
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'totalAmountRange', label: '合同总金额', formatter: '从{0}到{1}' }]
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'totalAmountRmbRange', label: '合同总金额(人民币)', formatter: '从{0}到{1}' }]
    }
  ]
}

const exportFileName = ref('采购合同管理.xlsx')
// const handleExport = async () => {
//   return await eplusListRef.value.exportList('采购合同管理.xlsx')
// }

const getContractId = (row) => row?.contractId ?? row?.id

const batchPlaceOrder = async () => {
  let checkedData = eplusListRef.value?.checkedItems
  if (checkedData && checkedData?.length) {
    let contractIdList = checkedData.map((item) => {
      return item?.contractId ?? item?.parentId ?? item?.parent?.id ?? item?.id
    })
    let ids = [...new Set(contractIdList)].join(',')
    await PurchaseContractApi.batchOrderPurchaseContract(ids)
    message.success('批量下单成功')
  } else {
    message.error('请选择采购合同')
  }
}

const backChange = (data) => {
  handleUpdate(data.id, data.type)
}

const handleUpdate = (id: number, type?) => {
  eplusDialogRef.value?.openEdit(id, type === 'change' ? '变更' : '采购合同', type)
}

const confirmChange = (row: any) => {
  confirmRow.value = row
  confirmRow.value.status = row.contractStatus
  confirmRow.value.changeType = [4, 5].includes(row?.contractStatus)
  eplusDialogRef.value?.openConfirm(getContractId(row), '确认变更')
}

// const handleSubmit = async (data) => {
//   try {
//     await PurchaseContractApi.submitPurchaseContract(data.id)
//     message.success('提交成功')
//   } catch (error) {
//     message.error('提交失败' + error)
//   }
//   handleRefresh()
// }
const appStore = useAppStore()
const handleDetail = (id) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    push({
      path: `/scm/purchase/ContractDetail/${id}`
    })
  }
}
// const jumpToPurchaseBoard = () => {
//   push({
//     name: 'ContractBoard'
//   })
// }

const allBtnSchemas = []

const preBuyBtnSchemas = [
  {
    // component: (
    //   <el-button
    //     type="primary"
    //     plain
    //     onClick={batchPlaceOrder}
    //     v-hasPermi="['scm:purchase-contract:export']"
    //   >
    //     批量下单
    //   </el-button>
    // )
  }
]
const ContractDetailRef = ref()
const detailAction = async (data) => {
  if (data.type === 'toPaymentApply') {
    ToPaymentApplyDialogRef.value.open(data.params)
  } else if (data.type === 'toWareHouse') {
    ToWareHouseDialogRef.value.open(data.params?.map((item) => item?.id))
  } else if (data.type === 'toAuxiliaryPlan') {
    ToAuxiliaryPlanRef.value.open(data.params?.map((item) => item?.id).join(','))
  } else if (data.type === 'signBackContract') {
    handleSignBack(data.params)
  } else if (data.type === 'produceCompleted') {
    handleProduced(data.params)
  } else if (data.type === 'checkContract') {
    ToInspectDialogRef.value.open(data.params?.id)
  } else if (data.type === 'exchangeContract') {
    handleExchange(data.params)
  } else if (data.type === 'toOrderNotice') {
    ToOrderNoticeRef.value.open(data.params)
  } else if (data.type === 'changeContract') {
    handleUpdate(data.params.id, 'change')
  } else if (data.type === 'scmPurchaseContractFinish') {
    handleClose(data.params)
  }
}

const toPaymentApply = () => {
  let checkedData = eplusListRef.value?.checkedItems
  if (checkedData && checkedData?.length) {
    let contractIdList = checkedData.map((item) => {
      return item?.contractId ?? item?.parentId ?? item?.parent?.id ?? item?.id
    })
    ToPaymentApplyDialogRef.value.open(contractIdList)
  } else {
    message.error('请选择采购合同')
  }
}

const handleToWareHouse = () => {
  let checkedItems: any[] = []
  eplusListRef.value?.checkedItems?.forEach((item) => {
    if (isValidArray(item.children) && item.produceCompleted === 1) {
      checkedItems = [...checkedItems, ...item.children]
    } else {
      if (item?.parent?.produceCompleted === 1) {
        checkedItems = [...checkedItems, item]
      }
    }
  })
  if (!checkedItems?.length) {
    message.warning('请选择生产完成的数据进行操作')
    return
  }
  for (let i = 0; i < checkedItems.length; i++) {
    let item = checkedItems[i]
    if (item?.warehousingType !== 0) {
      message.warning(`选中的第${i + 1}条明细已转入库通知单`)
      return false
    }
  }
  ToWareHouseDialogRef.value.open(checkedItems.map((item) => item.id))
}

const ToAuxiliaryPlanRef = ref()
const handleToAuxiliaryPlan = () => {
  let ids = eplusListRef.value?.checkedItems
    .map((item) => {
      if (isValidArray(item.children)) {
        return item.children.map((el) => el.id)
      } else {
        return item.id
      }
    })
    .join(',')
  if (ids) {
    ToAuxiliaryPlanRef.value.open(ids)
  } else {
    message.warning('请选择要操作的数据')
  }
}
const awaitinOrderBtnSchemas = [
  {
    component: (
      <el-button
        type="primary"
        plain
        onClick={toPaymentApply}
        v-hasPermi="['scm:payment-apply:create']"
      >
        转付款申请单
      </el-button>
    )
  },
  {
    component: (
      <el-button
        type="primary"
        plain
        onClick={handleToWareHouse}
        v-hasPermi="['scm:purchase-contract:warehousing']"
      >
        批量转入库通知单
      </el-button>
    )
  },
  {
    component: (
      <el-button
        type="primary"
        plain
        onClick={handleToAuxiliaryPlan}
        v-hasPermi="['scm:purchase-contract:auxiliary']"
      >
        下推包材采购计划
      </el-button>
    )
  },
  ...allBtnSchemas
]
const btnSchemasFormat = (data) => {
  switch (data) {
    case '5':
      btnSchemas = preBuyBtnSchemas
      break
    case '6':
    case '7':
      btnSchemas = awaitinOrderBtnSchemas
      break
    default:
      btnSchemas = allBtnSchemas
      break
  }
}

btnSchemasFormat(props.type)
const handleSignBack = async (row, title = '回签') => {
  let obj = {
    id: getContractId(row),
    code: row.code,
    venderName: row.venderName,
    createTime: row.createTime
  }
  signBackRef.value.open(title, obj)
}
const changeCount = ref(0)
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    // 根据当前 Tab 判断查询模式：产品 Tab (index=1) 使用产品模式 queryMode=2
    const queryMode = ps?.currentTabIndex === 1 ? 2 : 1
    
    const res = await PurchaseContractApi.getPurchaseContractPage({
      ...ps,
      contractStatus: isNaN(Number(activeName.value)) ? '' : activeName.value,
      confirmFlag: activeName.value === 'first' ? 0 : '',
      autoFlag: activeName.value === 'auto' ? 1 : 0,
      queryMode: queryMode
    })

    //产品维度和单据维度 withTaxTotal 已由后端返回，无需前端计算

    changeCount.value = res?.summary?.changeCount || 0
    return {
      list: res.list,
      total: res.total,
      sum: {
        // 产品维度列
        quantity: res?.summary?.qtySum,
        withTaxTotal: currencyJsonAnalysis(res?.summary?.totalSum),
        withTaxPriceRmb: currencyJsonAnalysis(res?.summary?.totalAmountRmb),
        // 单据维度列
        totalQuantity: res?.summary?.qtySum,
        totalAmount: currencyJsonAnalysis(res?.summary?.totalSum),
        totalAmountRmb: currencyJsonAnalysis(res?.summary?.totalAmountRmb),
        paymentAmount: currencyJsonAnalysis(res?.summary?.paymentAmount),
        payedAmount: currencyJsonAnalysis(res?.summary?.payedAmount)
      }
    }
  },
  delListApi: async (id) => {
    await PurchaseContractApi.deletePurchaseContract(id)
  },
  exportListApi: async (ps) => {
    const queryMode = ps?.currentTabIndex === 1 ? 2 : 1
    return await PurchaseContractApi.exportPurchaseContract({
      ...ps,
      queryMode
    })
  },
  showTabs: true,
  tabs: [
    {
      label: '单据',
      selection: true,
      summary: true,
      isDefault: true,
      columns: [
        {
          prop: 'code',
          label: '采购合同单号',
          isCopy: true,
          minWidth: columnWidth.xl,
          isHyperlink: true
        },
        {
          prop: 'companyName',
          label: '采购主体',
          minWidth: columnWidth.l
        },
        {
          prop: 'custName',
          label: '客户',
          minWidth: columnWidth.l
        },
        {
          prop: 'contractStatus',
          label: '单据状态',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.PURCHASE_CONTRACT_STATUS, row?.contractStatus)
          }
        },
        {
          prop: 'saleContractCode',
          label: '销售合同编号',
          minWidth: columnWidth.l
        },
        {
          prop: 'venderName',
          label: '供应商',
          minWidth: columnWidth.l
        },

        {
          prop: 'paymentName',
          label: '付款方式',
          minWidth: columnWidth.l
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUserDeptName',
          label: '采购员部门',
          minWidth: columnWidth.l
        },
        {
          prop: 'managerNickName',
          label: '跟单员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.manager?.nickname
          }
        },
        {
          prop: 'totalQuantity',
          label: '采购数量',
          summary: true,
          minWidth: columnWidth.l,
          formatter: formatNumColumn()
        },
        {
          prop: 'totalAmountRmb',
          label: '采购金额(RMB)',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              if (!data || data.amount === null || data.amount === undefined) {
                return {
                  unit: data?.currency,
                  number: 0
                }
              }
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        {
          prop: 'totalAmount',
          label: '采购金额',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              if (!data || data.amount === null || data.amount === undefined) {
                return {
                  unit: data?.currency,
                  number: 0
                }
              }
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        {
          prop: 'paymentAmount',
          label: '应付金额',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              if (!data || data.amount === null || data.amount === undefined) {
                return {
                  unit: data?.currency,
                  number: 0
                }
              }
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        {
          prop: 'payedAmount',
          label: '已付金额',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              if (!data || data.amount === null || data.amount === undefined) {
                return {
                  unit: data?.currency,
                  number: 0
                }
              }
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        // {
        //   prop: 'unPaidAmount',
        //   label: '未付金额',
        //   minWidth: columnWidth.m,
        //   formatter: formatMoneyColumn()
        // },
        {
          prop: 'salesNickName',
          label: '业务员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.sales?.nickname
          }
        },
        {
          prop: 'salesDeptName',
          label: '业务员部门',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.sales?.deptName
          }
        },
        {
          prop: 'managerNickName',
          label: '跟单员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.manager?.nickname
          }
        },
        {
          prop: 'managersDeptName',
          label: '跟单员部门',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.manager?.deptName
          }
        },
        {
          prop: 'checkStatus',
          label: '验货状态',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.PURCHASE_CHECK_STATUS, row?.checkStatus)
          }
        },
        {
          prop: 'deliveryDate',
          label: '交货日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'signBackTime',
          label: '回签日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'createTime',
          label: '创建时间',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
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
              let purchaseTypeList = row?.children?.map((item) => {
                return item.purchaseType
              })

              const isAllStandard = purchaseTypeList?.every(
                (item) => item === PurchaseTypeEnum.STANDARD.type
              )
              if (activeName.value === 'auto') {
                return (
                  <div class="flex items-center">
                    <el-button
                      link
                      type="primary"
                      onClick={() => {
                        handleDetail(row.id)
                      }}
                      hasPermi="['scm:purchase-contract:detail']"
                    >
                      详情
                    </el-button>
                    <eplus-dropdown
                      otherItems={[
                        {
                          isShow: row.confirmFlag !== 0 && row?.changeStatus != 2,
                          otherKey: 'checkContract',
                          label: '转验货单',
                          permi: 'scm:purchase-contract:check',
                          handler: async (row) => {
                            ToInspectDialogRef.value.open(row?.id)
                          }
                        },
                        {
                          isShow:
                            row?.invoiceStatus !== ConvertInvoiceFlagEnum.TRANSFERRED.status &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'toOrderNotice',
                          label: '转开票通知',
                          permi: 'scm:purchase-contract:to-invoice-notice',
                          handler: async (row) => {
                            ToOrderNoticeRef.value.open(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0 && row?.changeStatus != 2,
                          otherKey: 'paymentApplyCreate',
                          label: '转付款申请',
                          permi: 'scm:payment-apply:create',
                          handler: async (row) => {
                            ToPaymentApplyDialogRef.value.open([row.id])
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0 && row?.changeStatus != 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: 'scm:purchase-contract:change',
                          handler: async (row) => {
                            handleUpdate(row.id, 'change')
                          }
                        }
                      ]}
                      auditable={{ ...row }}
                    ></eplus-dropdown>
                  </div>
                )
              } else {
                return (
                  <div class="flex items-center">
                    <el-button
                      link
                      type="primary"
                      onClick={() => {
                        handleDetail(row.id)
                      }}
                      hasPermi="['scm:purchase-contract:detail']"
                    >
                      详情
                    </el-button>
                    <eplus-dropdown
                      otherItems={[
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'checkContract',
                          label: '转验货单',
                          permi: 'scm:purchase-contract:check',
                          handler: async (row) => {
                            ToInspectDialogRef.value.open(row?.id)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.READY_TO_SUBMIT.status,
                              PurchaseContractStatusEnum.REJECTED.status
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'scmPurchaseContractUpdate',
                          label: '编辑',
                          permi: 'scm:purchase-contract:update',
                          handler: async (row) => {
                            handleUpdate(row.id)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.AWAITING_ORDER.status //待回签
                            ].includes(row?.contractStatus) &&
                            row.signBackFlag === 0 &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'signBackContract',
                          label: '回签',
                          permi: 'scm:purchase-contract:sign-back',
                          handler: async (row) => {
                            handleSignBack(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待回签
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'signBackContract2',
                          label: '重新回签',
                          permi: 'scm:purchase-contract:sign-back',
                          handler: async (row) => {
                            handleSignBack(row, '重新回签')
                          }
                        },
                        //回签完成后自动下单
                        // {
                        //  isShow:
                        //   [
                        //     PurchaseContractStatusEnum.AWAITING_ORDER.status //待下单
                        //    ].includes(row?.contractStatus) && row?.placeOrderFlag == 0,
                        //  otherKey: 'orderContract',
                        //  label: '下单',
                        //  permi: 'scm:purchase-contract:order',
                        //  handler: async (row) => {
                        //    PurchaseContractApi.placeOrderPurchaseContract(row.id).then(() => {
                        //      message.success('下单成功')
                        //      handleRefresh()
                        //   })
                        // }
                        //},
                        // 2024.11.11验货单修改，注释掉验货通知单
                        // {
                        //   isShow: [
                        //     PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                        //   ].includes(row?.contractStatus),
                        //   otherKey: 'checkContract',
                        //   label: '验货通知单',
                        //   permi: 'scm:purchase-contract:check',
                        //   handler: async (row) => {
                        //     PurchaseContractApi.checkPurchaseContract([row.id]).then(() => {
                        //       message.success('验货通知单成功')
                        //       handleRefresh()
                        //     })
                        //   }
                        // },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                            ].includes(row?.contractStatus) &&
                            !row?.produceCompleted &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'produceCompleted',
                          label: '生产完成',
                          permi: 'scm:purchase-contract:produced',
                          handler: async (row) => {
                            await handleProduced(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                            ].includes(row?.contractStatus) &&
                            (row?.convertNoticeFlag === 1 || row?.convertNoticeFlag === 2) &&
                            row?.produceCompleted &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          // row?.convertNoticeFlag => 1:未转 2.部分转，3，已转

                          otherKey: 'warehousingContract',
                          label: '转入库通知单',
                          permi: 'scm:purchase-contract:warehousing',
                          handler: async (row) => {
                            ToWareHouseDialogRef.value.open(row?.children?.map((item) => item?.id))
                          }
                        },

                        //{
                        //  isShow: [
                        //    PurchaseContractStatusEnum.AWAITING_ORDER.status, //待下单
                        //    PurchaseContractStatusEnum.EXPECTING_DELIVERY.status, //待到货
                        //    PurchaseContractStatusEnum.FINISHED.status //已完成
                        //  ].includes(row?.contractStatus),
                        //  otherKey: 'payContract',
                        //  label: '请款',
                        //  permi: 'scm:purchase-contract:pay',
                        //  handler: async (row) => {
                        //    PurchaseContractApi.payPurchaseContract([row.id]).then(() => {
                        //      message.success('请款成功')
                        //      handleRefresh()
                        //    })
                        //  }
                        //},
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.FINISHED.status //已完成
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'exchangeContract',
                          label: '退换货',
                          permi: 'scm:purchase-contract:exchange',
                          handler: async (row) => {
                            await handleExchange(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.AWAITING_ORDER.status, //待下单
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: 'scm:purchase-contract:change',
                          handler: async (row) => {
                            handleUpdate(row.id, 'change')
                          }
                        },
                        {
                          isShow: row.confirmFlag == 0,
                          otherKey: 'confirm',
                          label: '确认变更',
                          permi: 'scm:purchase-contract-change:confirm',
                          handler: async (row) => {
                            confirmChange(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row?.invoiceStatus !== ConvertInvoiceFlagEnum.TRANSFERRED.status &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'toOrderNotice',
                          label: '转开票通知',
                          permi: 'scm:purchase-contract:to-invoice-notice',
                          handler: async (row) => {
                            ToOrderNoticeRef.value.open(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'paymentApplyCreate',
                          label: '转付款申请',
                          permi: 'scm:payment-apply:create',
                          handler: async (row) => {
                            ToPaymentApplyDialogRef.value.open([row.id])
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.READY_TO_SUBMIT.status,
                              PurchaseContractStatusEnum.REJECTED.status,
                              PurchaseContractStatusEnum.AWAITING_ORDER.status,
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'scmPurchaseContractFinish',
                          label: '作废',
                          permi: 'scm:purchase-contract:finish',
                          handler: async (row) => {
                            handleClose(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'scmPurchaseContractOrderDone',
                          label: '完成单据',
                          permi: 'scm:purchase-contract:order-done',
                          handler: async (row) => {
                            handleDone(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0,
                          otherKey: 'scmPurchaseContractPrint',
                          label: '打印',
                          permi: 'scm:purchase-contract:print',
                          handler: async (row) => {
                            handlePrint(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0,
                          otherKey: 'scmPurchaseContractExport',
                          label: '导出',
                          permi: 'scm:purchase-contract:export',
                          handler: async (row) => {
                            handleExport(row)
                          }
                        }
                      ]}
                      auditable={{ ...row }}
                    ></eplus-dropdown>
                  </div>
                )
              }
            }
          }
        }
      ]
    },
    {
      label: '产品',
      isTree: false,
      selection: true,
      summary: true,
      columns: [
        // 单据信息列
        {
          prop: 'code',
          label: '采购合同单号',
          isCopy: true,
          minWidth: columnWidth.l,
          isHyperlink: true
        },
        {
          prop: 'companyName',
          label: '采购主体',
          minWidth: columnWidth.l
        },
        {
          prop: 'venderName',
          label: '供应商',
          minWidth: columnWidth.m
        },
        {
          prop: 'contractStatus',
          label: '单据状态',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.PURCHASE_CONTRACT_STATUS, row?.contractStatus)
          }
        },
        {
          prop: 'purchaseUserNickname',
          label: '采购员',
          minWidth: columnWidth.m
        },       
         {
          prop: 'managerNickname',
          label: '跟单员',
          minWidth: columnWidth.m
        },
        {
          prop: 'createTime',
          label: '采购日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        // 明细信息列
        {
          prop: 'sortNum',
          label: '序号',
          minWidth: columnWidth.m
        },
        {
          prop: 'mainPicture',
          label: '图片',
          minWidth: columnWidth.m,
          // fixed: 'left',
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
          prop: 'skuCode',
          label: '产品编号',
          isCopy: true,
          minWidth: columnWidth.l
        },

        {
          prop: 'cskuCode',
          label: '客户货号',
          isCopy: true,
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return <EplusCSkuCode row={row} />
            }
          }
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          isCopy: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'name',
          label: '品名',
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <EplusSkuName
                  name={row.name || row.skuName}
                  type={row.skuType}
                />
              )
            }
          }
        },
        {
          prop: 'venderProdCode',
          label: '工厂货号',
          isCopy: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'planArriveDate',
          label: '到料时间',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'checkStatus',
          label: '验货结果',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, row?.checkStatus)
          }
        },
        {
          prop: 'withTaxPrice',
          label: '采购单价',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'quantity',
          label: '采购数量',
          summary: true,
          minWidth: columnWidth.m,
          formatter: formatNumColumn()
        },
        {
          prop: 'billStatus',
          label: '入库地点',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.SALE_ITEM_BILL_STATUS)
        },
        {
          prop: 'unNoticedquantity',
          label: '待转入库通知数量',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return formatNum(Number(row.quantity) - Number(row.noticedQuantity) || 0)
          }
        },
        {
          prop: 'billQuantity',
          label: '实际入库数量',
          minWidth: columnWidth.l,
          formatter: formatNumColumn()
        },
        {
          prop: 'withTaxTotal',
          label: '采购金额（含税总计）',
          minWidth: columnWidth.xl,
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
              return {
                unit: data.currency,
                number: data.amount
              }
            }
          },
          formatter: (row) => {
            return row.withTaxTotal?.amount ? (
              <EplusMoneyLabel
                val={{
                  amount: row.withTaxTotal.amount,
                  currency: row.withTaxTotal.currency
                }}
              />
            ) : (
              0
            )
          }
        },
        {
          prop: 'withTaxPriceRmb',
          label: '采购金额（人民币）',
          minWidth: columnWidth.l,
          summary: true,
          summarySlots: {
            default: (data) => {
              return {
                unit: data.currency,
                number: data.amount
              }
            }
          },
          formatter: (row) => {
            return row.withTaxPriceRmb?.amount ? (
              <EplusMoneyLabel
                val={{
                  amount: row.withTaxPriceRmb.amount,
                  currency: row.withTaxPriceRmb.currency
                }}
              />
            ) : (
              0
            )
          }
        },
        {
          prop: 'purchaseType',
          label: '采购类型',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.PURCHASE_TYPE, row?.purchaseType)
          }
        },

        {
          prop: 'invoiceStatus',
          label: '开票通知状态',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.INVOICE_NOTICE_STATUS, row?.invoiceStatus)
          }
        },
        // {
        //   prop: 'checkCost',
        //   label: '验货费',
        //   minWidth: '150px',
        //   slots: {
        //     default: (data) => {
        //       const { row } = data
        //       return row.checkCost ? row.checkCost.amount + ' ' + row.checkCost.currency : ''
        //     }
        //   }
        // },
        {
          prop: 'action',
          label: '操作',
          minWidth: '120px',
          fixed: 'right',
          slots: {
            default: (data) => {
              const { row } = data
              // 产品模式使用 contractId 获取父级合同ID
              const parentId = row.contractId || row.id
              if (activeName.value === 'auto') {
                return (
                  <div class="flex items-center">
                    <el-button
                      link
                      type="primary"
                      onClick={() => {
                        handleDetail(parentId)
                      }}
                      hasPermi="['scm:purchase-contract:detail']"
                    >
                      详情
                    </el-button>
                    <eplus-dropdown
                      otherItems={[
                        {
                          isShow: row.confirmFlag !== 0 && row?.changeStatus != 2,
                          otherKey: 'checkContract',
                          label: '转验货单',
                          permi: 'scm:purchase-contract:check',
                          handler: async (row) => {
                            ToInspectDialogRef.value.open(parentId)
                          }
                        },
                        {
                          isShow:
                            row?.invoiceStatus !== ConvertInvoiceFlagEnum.TRANSFERRED.status &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'toOrderNotice',
                          label: '转开票通知',
                          permi: 'scm:purchase-contract:to-invoice-notice',
                          handler: async (row) => {
                            ToOrderNoticeRef.value.open(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0 && row?.changeStatus != 2,
                          otherKey: 'paymentApplyCreate',
                          label: '转付款申请',
                          permi: 'scm:payment-apply:create',
                          handler: async (row) => {
                            ToPaymentApplyDialogRef.value.open([parentId])
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0 && row?.changeStatus != 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: 'scm:purchase-contract:change',
                          handler: async (row) => {
                            handleUpdate(parentId, 'change')
                          }
                        }
                      ]}
                      auditable={{ ...row }}
                    ></eplus-dropdown>
                  </div>
                )
              } else {
                return (
                  <div class="flex items-center">
                    <el-button
                      link
                      type="primary"
                      onClick={() => {
                        handleDetail(parentId)
                      }}
                      hasPermi="['scm:purchase-contract:detail']"
                    >
                      详情
                    </el-button>
                    <eplus-dropdown
                      editItem={{
                        permi: 'scm:purchase-contract:update',
                        handler: () => {
                          handleUpdate(parentId)
                        }
                      }}
                      otherItems={[
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'checkContract',
                          label: '转验货单',
                          permi: 'scm:purchase-contract:check',
                          handler: async (row) => {
                            ToInspectDialogRef.value.open(parentId)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.AWAITING_ORDER.status //待回签
                            ].includes(row?.contractStatus) &&
                            row.signBackFlag === 0 &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'signBackContract',
                          label: '回签',
                          permi: 'scm:purchase-contract:sign-back',
                          handler: async (row) => {
                            handleSignBack(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'signBackContract2',
                          label: '重新回签',
                          permi: 'scm:purchase-contract:sign-back',
                          handler: async (row) => {
                            handleSignBack(row, '重新回签')
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                            ].includes(row?.contractStatus) &&
                            !row?.produceCompleted &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'produceCompleted',
                          label: '生产完成',
                          permi: 'scm:purchase-contract:produced',
                          handler: async (row) => {
                            await handleProduced(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待到货
                            ].includes(row?.contractStatus) &&
                            (row?.convertNoticeFlag === 1 || row?.convertNoticeFlag === 2) &&
                            row?.produceCompleted &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          // row?.convertNoticeFlag => 1:未转 2.部分转，3，已转
                          otherKey: 'warehousingContract',
                          label: '转入库通知单',
                          permi: 'scm:purchase-contract:warehousing',
                          handler: async (row) => {
                            ToWareHouseDialogRef.value.open([row.id])
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.FINISHED.status //已完成
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'exchangeContract',
                          label: '退换货',
                          permi: 'scm:purchase-contract:exchange',
                          handler: async (row) => {
                            await handleExchange(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.AWAITING_ORDER.status, //待回签
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: 'scm:purchase-contract:change',
                          handler: async (row) => {
                            handleUpdate(parentId, 'change')
                          }
                        },
                        {
                          isShow: row.confirmFlag == 0,
                          otherKey: 'confirm',
                          label: '确认变更',
                          permi: 'scm:purchase-contract-change:confirm',
                          handler: async (row) => {
                            confirmChange(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row?.invoiceStatus !== ConvertInvoiceFlagEnum.TRANSFERRED.status &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'toOrderNotice',
                          label: '转开票通知',
                          permi: 'scm:purchase-contract:to-invoice-notice',
                          handler: async (row) => {
                            ToOrderNoticeRef.value.open(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'paymentApplyCreate',
                          label: '转付款申请',
                          permi: 'scm:payment-apply:create',
                          handler: async (row) => {
                            ToPaymentApplyDialogRef.value.open([parentId])
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.READY_TO_SUBMIT.status,
                              PurchaseContractStatusEnum.REJECTED.status,
                              PurchaseContractStatusEnum.AWAITING_ORDER.status,
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'scmPurchaseContractFinish',
                          label: '作废',
                          permi: 'scm:purchase-contract:finish',
                          handler: async (row) => {
                            handleClose(row)
                          }
                        },
                        {
                          isShow:
                            [
                              PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                            ].includes(row?.contractStatus) &&
                            row.confirmFlag !== 0 &&
                            row?.changeStatus != 2,
                          otherKey: 'scmPurchaseContractOrderDone',
                          label: '完成单据',
                          permi: 'scm:purchase-contract:order-done',
                          handler: async (row) => {
                            handleDone(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0,
                          otherKey: 'scmPurchaseContractPrint',
                          label: '打印',
                          permi: 'scm:purchase-contract:print',
                          handler: async (row) => {
                            handlePrint(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0,
                          otherKey: 'scmPurchaseContractExport',
                          label: '导出',
                          permi: 'scm:purchase-contract:export',
                          handler: async (row) => {
                            handleExport(row)
                          }
                        }
                      ]}
                      auditable={{ ...row }}
                    ></eplus-dropdown>
                  </div>
                )
              }
            }
          }
        }
      ]
    }
  ]
}
const handleExchange = (row) => {
  const contractId = getContractId(row)
  ElMessageBox.confirm('是否确认退换货？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    PurchaseContractApi.exchangePurchaseContract([contractId]).then(() => {
      message.success('退换货成功')
      handleRefresh()
    })
  })
}

const ProducedDiaRef = ref()
const handleProduced = (row) => {
  ProducedDiaRef.value.open(row)
  // ElMessageBox.confirm('是否确认生产完成？', '提示', {
  //   confirmButtonText: '确认',
  //   cancelButtonText: '取消',
  //   type: 'warning'
  // }).then(() => {
  //   PurchaseContractApi.produceDonePurchaseContract([row.id]).then(() => {
  //     message.success('生产完成成功')
  //     handleRefresh()
  //   })
  // })
}

const handleClose = (row) => {
  const contractId = getContractId(row)
  ElMessageBox.confirm('是否确认作废？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    PurchaseContractApi.batchFinishPurchaseContract([contractId]).then(() => {
      message.success('作废成功')
      handleRefresh()
    })
  })
}

const handleDone = (row) => {
  const contractId = getContractId(row)
  ElMessageBox.confirm('是否确认完成单据？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    PurchaseContractApi.orderDonePurchaseContract(contractId).then(() => {
      message.success('完成单据成功')
      handleRefresh()
    })
  })
}

const handlePrint = async (row) => {
  const url = await PurchaseContractApi.print({ id: getContractId(row), reportCode: 'purchase-contract' })
  openPdf(url)
}

const handleExport = async (row) => {
  await message.exportConfirm()
  const data = await PurchaseContractApi.exportPurchaseContractDetailWord({
    id: getContractId(row),
    reportCode: 'purchase-contract'
  })
  if (data && data.size) {
    download.word(data, row?.code + '.docx')
  }
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
