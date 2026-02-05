<template>
  <div class="h-45px bg-[var(--left-menu-bg-color)] pl-15px pr-15px">
    <el-tabs
      v-model="activeName"
      @tab-click="handleTabsClick"
    >
      <el-tab-pane
        v-for="item in tabNameList"
        :label="item.label"
        :name="item.name"
        :key="item.name"
      >
        <template
          #label
          v-if="item.name === 'nine'"
        >
          <el-badge
            :value="changeCount"
            :hidden="changeCount <= 0"
          >
            <span>{{ item.label }}</span>
          </el-badge>
        </template>
      </el-tab-pane>
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
      <!-- 页面按钮 -->
      <div class="flex-1 overflow-x-auto">
        <el-button
          v-if="activeName == 'first'"
          type="primary"
          @click="handleCreate()"
          v-hasPermi="[`${permiPrefix}:create`]"
        >
          新增
        </el-button>

        <el-button
          v-if="activeName == 'fifth' && [1, 3].includes(contractType)"
          @click="handleToPurchasePlan('')"
          v-hasPermi="[`${permiPrefix}:to-purchase-plan`]"
        >
          批量下推采购计划
        </el-button>

        <el-button
          v-if="activeName == 'sixth' && [1, 3].includes(contractType)"
          @click="
            handleCreateShipmentPlan(
              null,
              eplusListRef?.currentTabIndex === 1 ? QUERY_MODE.PRODUCT : QUERY_MODE.DOCUMENT
            )
          "
          v-hasPermi="[`${permiPrefix}:to-shipment-plan`]"
        >
          转出运计划
        </el-button>
      </div>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <ExportSaleContractForm
        mode="create"
        :contractType="contractType"
        @handle-success="handleRefresh"
      />
    </template>
    <template #copy="{ key, param }">
      <ExportSaleContractForm
        :type="param"
        :id="key"
        mode="copy"
        :contractType="contractType"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key, param }">
      <ExportSaleContractForm
        :type="param"
        :id="key"
        mode="edit"
        :contractType="contractType"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <ExportSaleContractDetail
        :id="key"
        :type="contractType"
        :lineCode="true"
        @create-shipment-plan="createShipmentPlan"
      />
    </template>
    <template #confirm="{ key }">
      <ExportSaleContractConfirm
        :id="key"
        mode="confirm"
        :row="confirmRow"
        :type="1"
        @handle-success="handleRefresh"
        @success="backChange"
      />
    </template>
    <template #change="{ key, param }">
      <!-- <ExportSaleContractChangeForm
        mode="change"
        :id="key"
      /> -->
      <ExportSaleContractForm
        :type="param"
        :id="key"
        mode="change"
        :contractType="contractType"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>

  <copy-from-quotation-dialog ref="copyFromQuotationDialogRef" />

  <eplus-dialog
    ref="planDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <ShippingPlanForm
        :params="planParams"
        mode="create"
      />
    </template>
  </eplus-dialog>

  <SignBackForm
    ref="signBackRef"
    @success="handleRefresh"
    :type="contractType"
  />

  <TtnoticeDia
    ref="TtnoticeDiaRef"
    @success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel, getDictValue, getDictOptions } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { EplusCountrySelect } from '@/components/EplusSelect'
import ExportSaleContractForm from './ExportSaleContractForm.vue'
import ShippingPlanForm from '@/views/dms/shippingPlan/ShippingPlanForm.vue'
import ExportSaleContractDetail from './ExportSaleContractDetail.vue'
import ExportSaleContractConfirm from './ExportSaleContractConfirm.vue'
import CopyFromQuotationDialog from './CopyFromQuotationDialog.vue'
import * as LoanAppApi from '@/api/oa/loanapp'
import { isAmount, isValidArray } from '@/utils/is'
import SignBackForm from './SignBackForm.vue'
import { SaleContractStatusEnum } from '@/utils/constants'
import * as DomesticSaleContractApi from '@/api/sms/saleContract/domestic'
import * as FactorySaleContractApi from '@/api/sms/saleContract/factory'
import LockCom from './components/LockCom.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { globalStore } from '@/store/modules/globalVariable'
import { useCountryStore } from '@/store/modules/countrylist'
import TtnoticeDia from './components/TtnoticeDia.vue'
import { useAppStore } from '@/store/modules/app'
import { useRateStore } from '@/store/modules/rate'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { currencyJsonAnalysis, formatNum } from '@/utils'
import { EplusSkuName } from '@/components/EplusSkuName'
import { checkPermi } from '@/utils/permission'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

defineOptions({ name: 'SaleContractPage' })

// 查询模式常量：1=单据模式，2=产品模式
const QUERY_MODE = {
  DOCUMENT: 1,
  PRODUCT: 2
}

const props = withDefaults(
  defineProps<{
    type?: number
  }>(),
  { type: 1 }
)

const changeStore = globalStore()
const pagePath = useRoute().path
const contractType = props.type
const permiPrefix =
  contractType == 1
    ? 'sms:export-sale-contract'
    : contractType == 2
      ? 'sms:domestic-sale-contract'
      : 'sms:factory-sale-contract'

const TtnoticeDiaRef = ref()
const eplusListRef = ref()
const copyFromQuotationDialogRef = ref()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const signBackRef = ref()
const activeName = ref('first')
const rateList = useRateStore().rateList
const countryListData = useCountryStore()
const tabNameList = [
  { name: 'first', label: '全部' },
  { name: 'nine', label: '上游变更' },
  { name: 'second', label: '待提交' },
  { name: 'third', label: '待审核' },
  { name: 'forth', label: '待回签' },
  { name: 'fifth', label: '待采购' },
  { name: 'sixth', label: `${contractType === 2 ? '待出库' : '待出运'}` },
  { name: 'seventh', label: '已完成' },
  { name: 'eight', label: '已作废' },
  { name: 'auto', label: '内部流通' }
]
const handleDialogFailure = () => {}
const confirmRow = ref<any>({})
const searchCountryName = ref()

const handleRefresh = async () => {
  changeStore.setChangeType('')
  await eplusListRef.value.handleRefresh()
}
const handleTabsClick = (val) => {
  activeName.value = val.props.name
}

const exportFileName = computed(() => {
  if (checkPermi([`${permiPrefix}:export`])) {
    return contractType == 1
      ? '外销合同管理.xlsx'
      : contractType == 2
        ? '内销合同管理.xlsx'
        : '工厂直销合同管理.xlsx'
  } else {
    return ''
  }
})
const saleType = contractType

const message = useMessage()
const getContractId = (row) => row?.contractId ?? row?.id

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '合同号'
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
      label: '下单主体',
      component: (
        <eplus-custom-select
          api={LoanAppApi.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择"
          style="width:100%"
        />
      )
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custCode',
      label: '客户编码'
    },
    {
      component: (
        <eplus-dict-select
          dictType={DICT_TYPE.SOURCE_TYPE}
          clearable
        />
      ),
      name: 'custSourceType',
      label: '客户来源',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.SOURCE_TYPE, args[0])
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custPo',
      label: '客户合同号'
    },
    {
      component: (
        <EplusCountrySelect
          onChange={(item) => {
            searchCountryName.value = item.name
          }}
          class="!w-100%"
        />
      ),
      name: 'custCountryId',
      label: '客户国别',
      formatter: () => {
        return searchCountryName.value
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'salesId',
      label: '业务员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },

    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'salesDeptId',
      label: '业务员部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
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
          name: 'signBackDate',
          label: '回签日期',
          formatter: '从{0}到{1}'
        },

        {
          name: 'inputDate',
          label: '录入日期',
          formatter: '从{0}到{1}'
        },
        {
          name: 'custDeliveryDate',
          label: '客户交期',
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
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerId',
      label: '业务跟单',
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
      component: <eplus-dict-select dictType={DICT_TYPE.PRINT_FLAG}></eplus-dict-select>,
      name: 'printFlag',
      label: '打印状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.PRINT_FLAG, args[0])
      }
    }
  ]
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(
    id,
    contractType == 1 ? '外销合同' : contractType == 2 ? '内销合同' : '外币采购'
  )
}

const handleClose = async (row) => {
  await message.confirm('确定作废吗？')
  let req =
    contractType == 1
      ? ExportSaleContractApi.close
      : contractType == 2
        ? DomesticSaleContractApi.close
        : FactorySaleContractApi.close
  await req({ parentId: getContractId(row) })
  message.success('作废成功')
  handleRefresh()
}

const handleOrderDone = (row) => {
  ElMessageBox.confirm('是否确认完成单据？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ExportSaleContractApi.orderDone(getContractId(row)).then(() => {
      message.success('完成单据成功')
      handleRefresh()
    })
  })
}

const confirmChange = (row: any) => {
  confirmRow.value = row
  // INSINGBACK.status 待回签
  // INPURCHASE.status 待采购
  // INSHIPPING.status 待出运
  confirmRow.value.changeType = [
    SaleContractStatusEnum.INSINGBACK.status,
    SaleContractStatusEnum.INPURCHASE.status,
    SaleContractStatusEnum.INSHIPPING.status
  ].includes(row?.status)

  eplusDialogRef.value?.openConfirm(getContractId(row), '确认')
}

const backChange = (data) => {
  handleChange(data.id, data.type)
}

const handleChange = (id: number, type) => {
  eplusDialogRef.value?.openChange(id, '变更', type)
}

const handleToPurchasePlan = async (id) => {
  try {
    let res
    if (id) {
      res = await ExportSaleContractApi.toPurchasePlan({ saleContractId: id })
    } else {
      if (eplusListRef.value?.checkedItems.length == 0) {
        message.error('请选择数据')
        return
      }
      let changeStatuslist = eplusListRef.value?.checkedItems.map((item) =>
        item.parent ? item.parent.changeStatus : item.changeStatus
      )
      if (changeStatuslist && changeStatuslist.indexOf(2) >= 0) {
        message.error('存在变更中记录信息，请核对！')
        return
      }
      let list = eplusListRef.value?.checkedItems.map((item) => getContractId(item?.parent ?? item))
      res = await ExportSaleContractApi.batchToPurchasePlan({
        saleContractIdList: [...new Set(list)].join(',')
      })
    }
    // message.success('下推成功')
    message.notifyPushSuccess('下推采购计划成功', res, 'purchase-plan')
    handleRefresh()
  } catch (error) {
    message.error('下推失败' + error)
  }
}

const handleSignBack = async (row) => {
  if (row.status != SaleContractStatusEnum.INSINGBACK.status) {
    ElMessage.error('回签失败，状态错误！')
    return
  }
  let obj = {
    id: getContractId(row),
    code: row.code,
    createTime: row.createTime
  }
  signBackRef.value.open('回签', obj)
}

const handleSubmit = async (data) => {
  try {
    contractType == 1
      ? await ExportSaleContractApi.submitExportSaleContract(data.id)
      : contractType == 2
        ? await DomesticSaleContractApi.submitDomesticSaleContract(data.id)
        : await FactorySaleContractApi.submitFactoryContract(data.id)
    message.success('提交成功')
  } catch (error) {
    message.error('提交失败' + error)
  }
  handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const router = useRouter()
const appStore = useAppStore()
const handleDetail = (id: number) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/sms/sale-orders/ExportSaleContractDetail/${id}`,
      query: {
        contractType: contractType
      }
    })
  }
  // router.push({
  //   path: `/sms/sale-orders/ExportSaleContractDetail/${id}`,
  //   query: {
  //     contractType: contractType
  //   }
  // })
}

const capitalizeFirstLetter = (str) => {
  return 'real' + str.charAt(0).toUpperCase() + str.slice(1)
}

const getRealVal = (item) => {
  let realFieldList = [
    'boxCount',
    'lockQuantity',
    'packagingPrice',
    'prchaseCurrency',
    'purchaseQuantity',
    'purchaseWithTaxPrice',
    'shippingPrice',
    'specificationList',
    'splitBoxFlag',
    'taxRefundPrice',
    'taxRefundRate',
    'venderCode',
    'venderId',
    'venderName'
  ]
  realFieldList.forEach((f) => {
    if (isAmount(item[f])) {
      if (item[capitalizeFirstLetter(f)]?.amount > 0 && item[capitalizeFirstLetter(f)]?.currency) {
        item[f] = item[capitalizeFirstLetter(f)]
      } else {
        item[f] = item[f]
      }
    } else {
      item[f] = item[capitalizeFirstLetter(f)] || item[f]
    }
  })
  //库存单独处理
  item.currentLockQuantity = item.realLockQuantity || item.currentLockQuantity || 0
}

const handleList = (list) => {
  list.forEach((item) => {
    if (isValidArray(item.children)) {
      item.children.forEach((el) => {
        getRealVal(el)
      })
    }
  })
  return list
}

const changeDialogRef = ref('')
const changeCount = ref(0)

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    let status = ''
    let currentTab = tabNameList.filter((item) => item.name == activeName.value)

    if (currentTab?.length) {
      status =
        contractType == 1
          ? getDictValue(DICT_TYPE.SALE_CONTRACT_STATUS, currentTab[0].label)
          : getDictValue(DICT_TYPE.DOME_SALE_CONTRACT_STATUS, currentTab[0].label)
    }
    let confirmFlag = activeName.value == 'nine' ? 0 : ''
    let autoFlag = activeName.value == 'auto' ? 1 : 0
    // 根据当前 Tab 判断查询模式：产品 Tab (index=1) 使用产品模式 queryMode=2，其他使用单据模式 queryMode=1
    const queryMode = ps?.currentTabIndex === 1 ? 2 : 1
    const params = {
      status: status,
      ...ps,
      queryMode: queryMode,
      saleType: saleType,
      confirmFlag: confirmFlag,
      autoFlag: autoFlag
    }

    const res =
      contractType === 1
        ? await ExportSaleContractApi.getExportSaleContractPage(params)
        : contractType == 2
          ? await DomesticSaleContractApi.getDomesticSaleContractPage(params)
          : await FactorySaleContractApi.getFactoryContractPage(params)

    changeCount.value = res?.summary?.changeCount || 0
    const filteredList = handleList(res?.list) || []

    return {
      list: filteredList,
      total: res?.total || 0,
      sum: {
        totalAmountThisCurrency: getCurrencySummary(res, 'sumTotalAmountThisCurrency'),
        totalAmount: getCurrencySummary(res, 'sumTotalAmount'),
        totalAmountUsd: getCurrencySummary(res, 'sumTotalAmountUsd'),
        totalSaleAmount: getCurrencySummary(res, 'sumTotalAmountThisCurrency'),
        totalSaleAmountUsd: getCurrencySummary(res, 'sumTotalAmountUsd')
      }
    }
  },
  delListApi: async (id) => {
    contractType == 1
      ? await ExportSaleContractApi.deleteExportSaleContract(id)
      : contractType == 2
        ? await DomesticSaleContractApi.deleteDomesticSaleContract(id)
        : await FactorySaleContractApi.deleteFactoryContract(id)
  },
  exportListApi: async (ps) => {
    let reqPath =
      contractType == 1
        ? await ExportSaleContractApi.exportExportSaleContract({
            ...ps,
            saleType: saleType,
            autoFlag: activeName.value == 'auto' ? 1 : 0
          })
        : contractType == 2
          ? await DomesticSaleContractApi.exportDomesticSaleContract({
              ...ps,
              saleType: saleType,
              autoFlag: activeName.value == 'auto' ? 1 : 0
            })
          : await FactorySaleContractApi.exportFactoryContract({
              ...ps,
              saleType: saleType,
              autoFlag: activeName.value == 'auto' ? 1 : 0
            })
    return reqPath
  },
  selection: true,
  summary: true,
  showTabs: true,
  tabs: [
    {
      label: '单据',
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'code',
          label: '合同号',
          minWidth: columnWidth.xxl,
          isHyperlink: true
        },
        {
          prop: 'companyName',
          label: '下单主体',
          minWidth: columnWidth.l
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '报关主体',
          minWidth: columnWidth.l
        },
        {
          prop: 'custPo',
          label: '客户合同号',
          minWidth: columnWidth.l
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.xxl
        },
        {
          prop: 'inputDate',
          label: '录入日期',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'custDeliveryDate',
          label: '客户交期',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'currency',
          label: '交易币别',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE)
        },
        {
          prop: 'totalAmountThisCurrency',
          label: '销售总金额(原币种)',
          minWidth: columnWidth.xl,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        {
          prop: 'totalAmount',
          label: '销售总金额(RMB)',
          minWidth: columnWidth.xl,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        {
          prop: 'totalAmountUsd',
          label: '销售总金额(美金)',
          minWidth: columnWidth.xl,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        {
          prop: 'orderGrossProfit',
          label: '毛利润',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'grossProfitMargin',
          label: '毛利率(%)',
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return row?.grossProfitMargin ? Number(row.grossProfitMargin * 100).toFixed(2) : ''
            }
          }
        },
        {
          prop: 'settlementName',
          label: '收款方式',
          minWidth: columnWidth.l
        },
        {
          prop: 'receivedAmount',
          label: '已收金额',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'unReceivedAmount',
          label: '未收金额',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'custCountryName',
          label: '客户国别',
          minWidth: columnWidth.l
        },
        {
          prop: 'tradeCountryName',
          label: '贸易国别',
          minWidth: columnWidth.l
        },
        {
          prop: 'exchangeRate',
          label: '汇率',
          minWidth: columnWidth.m
        },
        {
          prop: 'salesNickName',
          label: '业务员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.sales?.nickname || ''
          }
        },
        {
          prop: 'salesDeptName',
          label: '业务员部门',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.sales?.deptName || ''
          }
        },
        {
          prop: 'managerNickName',
          label: '业务跟单',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.manager?.nickname || ''
          }
        },
        {
          prop: 'managerDeptName',
          label: '跟单部门',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.manager?.deptName || ''
          }
        },
        {
          prop: 'signBackDate',
          label: '回签日期',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'auditStatus',
          label: '审核状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
        },
        {
          prop: 'status',
          label: '合同状态',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              if (contractType == 1 || contractType == 3) {
                return getDictLabel(DICT_TYPE.SALE_CONTRACT_STATUS, row.status)
              } else {
                return getDictLabel(DICT_TYPE.DOME_SALE_CONTRACT_STATUS, row.status)
              }
            }
          }
        },
        {
          prop: 'printFlag',
          label: '打印状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.PRINT_FLAG)
        },
        {
          prop: 'creatorName',
          label: '创建人',
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorDeptName',
          label: '创建人部门',
          minWidth: columnWidth.l
        },
        {
          prop: 'createTime',
          label: '创建时间',
          minWidth: columnWidth.xl,
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
              if (activeName.value == 'auto') {
                return (
                  <div class="flex items-center">
                    <el-button
                      link
                      type="primary"
                      onClick={() => {
                        handleDetail(getContractId(row))
                      }}
                      hasPermi="[`${permiPrefix}:detail`]"
                    >
                      详情
                    </el-button>
                    <eplus-dropdown
                      otherItems={[
                        {
                          isShow: row?.confirmFlag !== 0 && row?.changeStatus !== 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: `${permiPrefix}:change`,
                          handler: async (row) => {
                            handleChange(getContractId(row), 'change')
                          }
                        }
                      ]}
                      auditable={row}
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
                        handleDetail(getContractId(row))
                      }}
                      hasPermi="[`${permiPrefix}:detail`]"
                    >
                      详情
                    </el-button>

                    <eplus-dropdown
                      editItem={{
                        permi: `${permiPrefix}:update`,
                        handler: () => {
                          handleUpdate(getContractId(row))
                        }
                      }}
                      deleteItem={{
                        permi: `${permiPrefix}:delete`,
                        handler: async (row) => {
                          await handleDelete(getContractId(row))
                        }
                      }}
                      otherItems={[
                        {
                          isShow:
                            [SaleContractStatusEnum.INSHIPPING.status].includes(row?.status) &&
                            contractType == 1 &&
                            row?.changeStatus !== 2 &&
                            row?.confirmFlag !== 0,
                          otherKey: 'toShipmentPlan',
                          label: '转出运计划',
                          permi: `${permiPrefix}:to-shipment-plan`,
                          handler: async (row) => {
                            handleCreateShipmentPlan(row, QUERY_MODE.DOCUMENT)
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INSHIPPING.status].includes(row?.status) &&
                            contractType == 2 &&
                            isValidArray(
                              row.children?.filter((item) => item.converNoticeFlag == 0)
                            ) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'Ttnotice',
                          label: '转出库通知单',
                          permi: `${permiPrefix}:to-notice`,
                          handler: async (row) => {
                            TtnoticeDiaRef.value?.open(row)
                          }
                        },
                        {
                          isShow:
                            [
                              SaleContractStatusEnum.INSINGBACK.status,
                              SaleContractStatusEnum.INPURCHASE.status,
                              SaleContractStatusEnum.INSHIPPING.status
                            ].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: `${permiPrefix}:change`,
                          handler: async (row) => {
                            handleChange(getContractId(row), 'change')
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INSINGBACK.status].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'signBack',
                          label: '回签',
                          permi: `${permiPrefix}:sign-back`,
                          handler: async (row) => {
                            handleSignBack(row)
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INPURCHASE.status].includes(row?.status) &&
                            row?.changeStatus !== 2 &&
                            row?.confirmFlag !== 0, // 内销不能下推
                          otherKey: 'toPurchasePlan',
                          label: '下推采购计划',
                          permi: `${permiPrefix}:to-purchase-plan`,
                          handler: async (row) => {
                            handleToPurchasePlan(getContractId(row))
                          }
                        },
                        {
                          isShow:
                            [
                              SaleContractStatusEnum.INSINGBACK.status,
                              SaleContractStatusEnum.INPURCHASE.status,
                              SaleContractStatusEnum.INSHIPPING.status
                            ].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'finish',
                          label: '作废',
                          permi: `${permiPrefix}:close`,
                          handler: async (row) => {
                            handleClose(row)
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INSHIPPING.status].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'order-done',
                          label: '完成单据',
                          permi: `${permiPrefix}:order-done`,
                          handler: async (row) => {
                            handleOrderDone(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag == 0,
                          otherKey: 'confirm',
                          label: '确认变更',
                          permi: `${permiPrefix}-change:confirm`,
                          handler: async (row) => {
                            confirmChange(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0,
                          otherKey: 'copy',
                          label: '复制',
                          permi: `${permiPrefix}:create`,
                          handler: async (row) => {
                            eplusDialogRef.value?.openCopy(
                              getContractId(row),
                              contractType == 1
                                ? '外销合同'
                                : contractType == 2
                                  ? '内销合同'
                                  : '外币采购'
                            )
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
        }
      ]
    },
    {
      label: '产品',
      isTree: false,
      isDefault: true,
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'custPo',
          label: '客户合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'custCountryName',
          label: '客户国别',
          minWidth: columnWidth.l
        },
        {
          prop: 'code',
          label: '合同编号',
          isCopy: true,
          minWidth: columnWidth.m,
          slots: {
            default: (scope: any) => (
              <el-link
                type="primary"
                onClick={() => handleDetail(getContractId(scope.row))}
              >
                {scope.row.code}
              </el-link>
            )
          }
        },
        {
          prop: 'settlementName',
          label: '收款方式',
          minWidth: columnWidth.l
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '报关主体',
          minWidth: columnWidth.m
        },
        {
          prop: 'reorderFlag',
          label: '是否翻单',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row?.reorderFlag)
          }
        },
        {
          prop: 'currency',
          label: '交易币种',
          minWidth: columnWidth.m
        },
        {
          prop: 'salesNickname',
          label: '业务员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.salesNickname || row.sales?.nickname || ''
          }
        },
        {
          prop: 'salesDeptName',
          label: '业务员部门',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.salesDeptName || row.sales?.deptName || ''
          }
        },
        {
          prop: 'managerNickname',
          label: '业务跟单',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.managerNickname || row.manager?.nickname || ''
          }
        },
        {
          prop: 'managerDeptName',
          label: '跟单部门',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.managerDeptName || row.manager?.deptName || ''
          }
        },
        {
          prop: 'signBackDate',
          label: '回签日期',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'exchangeRate',
          label: '汇率',
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorName',
          label: '创建人',
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorDeptName',
          label: '创建人部门',
          minWidth: columnWidth.l
        },
        {
          prop: 'createTime',
          label: '创建时间',
          minWidth: columnWidth.xl,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        // {
        //   prop: 'skuCode',
        //   label: '产品编号',
        //   minWidth: columnWidth.m
        // },
        {
          prop: 'cskuCode',
          label: '客户货号',
          minWidth: columnWidth.xxl
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.xxl
        },
        {
          prop: 'status',
          label: '合同状态',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              if (contractType == 1 || contractType == 3) {
                return getDictLabel(DICT_TYPE.SALE_CONTRACT_STATUS, row.status)
              } else {
                return getDictLabel(DICT_TYPE.DOME_SALE_CONTRACT_STATUS, row.status)
              }
            }
          }
        },
        {
          field: 'purchaseTotalAmount',
          label: '采购总金额',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              const quantity = row.realPurchaseQuantity || row.needPurQuantity
              return row.purchaseWithTaxPrice?.amount && quantity ? (
                <EplusMoneyLabel
                  val={{
                    amount: row.quantity * row.purchaseWithTaxPrice?.amount || 0,
                    currency: row.purchaseWithTaxPrice?.currency
                  }}
                />
              ) : (
                '-'
              )
            }
          }
        },
        {
          prop: 'mainPicture',
          label: '图片',
          minWidth: columnWidth.m,
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
          prop: 'name',
          label: '中文名称',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <EplusSkuName
                  name={row.name}
                  type={row.skuType}
                />
              )
            }
          }
        },
        {
          prop: 'nameEng',
          label: '英文名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'quantity',
          label: '数量',
          minWidth: columnWidth.m
        },
        {
          prop: 'unitPrice',
          label: '销售单价',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'totalSaleAmount',
          label: '销售总金额（原币种）',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        {
          prop: 'totalSaleAmountUsd',
          label: '销售总金额(USD)',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
              return {
                unit: data.currency,
                number: Number(data.amount) || 0
              }
            }
          }
        },
        // {
        //   prop: 'needPurQuantity',
        //   label: '待采购',
        //   minWidth: columnWidth.m,
        //   formatter: formatNumColumn()
        // },
        {
          prop: 'realPurchaseQuantity',
          label: '采购数量',
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return row.realPurchaseQuantity || row.needPurQuantity
            }
          }
        },
        {
          prop: 'withTaxPriceRemoveFree',
          label: '采购含税单价',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn()
        },
        // {
        //   prop: 'currentLockQuantity',
        //   label: '锁定数量',
        //   minWidth: columnWidth.m,
        //   formatter: formatNumColumn()
        // },
        {
          prop: 'realLockQuantity',
          label: '真实锁定数量',
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <LockCom
                  data={{
                    saleContractItemId: row.id,
                    saleContractCode: row.contractCode,
                    companyIdList: row.companyIdList,
                    skuCode: row.skuCode,
                    realLockQuantity: row.realLockQuantity,
                    onlyLockFlag: 1
                  }}
                />
              )
            }
          }
        },
        {
          prop: 'stockLockPrice',
          label: '锁定库存单价',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'stockLockTotalPrice',
          label: '锁定库存总金额',
          minWidth: columnWidth.l,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'billStatus',
          label: '入库地点',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.SALE_ITEM_BILL_STATUS)
        },

        {
          prop: 'transferShippedQuantity',
          label: `${contractType === 1 ? '待转出运数量' : '待转出库数量'}`,
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return formatNum(Number(row.quantity) - Number(row.transferShippedQuantity))
            }
          }
        },
        {
          prop: 'shippedQuantity',
          label: `${contractType === 1 ? '待出运数量' : '待出库数量'}`,
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return formatNum(Number(row.quantity) - Number(row.shippedQuantity))
            }
          }
        },
        {
          prop: 'venderName',
          label: '供应商',
          minWidth: columnWidth.l
        },
        {
          prop: 'purchaseUserNickname',
          label: '采购员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.purchaseUserNickname || row.purchaseUser?.nickname || ''
          }
        },
        {
          prop: 'purchaseUserDeptName',
          label: '采购员部门',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.purchaseUserDeptName || row.purchaseUser?.deptName || ''
          }
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: '120px',
          fixed: 'right',
          slots: {
            default: (data) => {
              const { row } = data
              if (activeName.value == 'auto') {
                return (
                  <div class="flex items-center">
                    <el-button
                      link
                      type="primary"
                      onClick={() => {
                        handleDetail(getContractId(row))
                      }}
                      hasPermi="[`${permiPrefix}:detail`]"
                    >
                      详情
                    </el-button>
                    <eplus-dropdown
                      otherItems={[
                        {
                          isShow: row?.confirmFlag !== 0 && row?.changeStatus !== 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: `${permiPrefix}:change`,
                          handler: async (row) => {
                            handleChange(getContractId(row), 'change')
                          }
                        }
                      ]}
                      auditable={row}
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
                        handleDetail(getContractId(row))
                      }}
                      hasPermi="[`${permiPrefix}:detail`]"
                    >
                      详情
                    </el-button>

                    <eplus-dropdown
                      editItem={{
                        permi: `${permiPrefix}:update`,
                        handler: () => {
                          handleUpdate(getContractId(row))
                        }
                      }}
                      deleteItem={{
                        permi: `${permiPrefix}:delete`,
                        handler: async (row) => {
                          await handleDelete(getContractId(row))
                        }
                      }}
                      otherItems={[
                        {
                          isShow:
                            [SaleContractStatusEnum.INSHIPPING.status].includes(row?.status) &&
                            (contractType == 1 || contractType == 3) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'toShipmentPlan',
                          label: '转出运计划',
                          permi: `${permiPrefix}:to-shipment-plan`,
                          handler: async (row) => {
                            handleCreateShipmentPlan(row, QUERY_MODE.PRODUCT)
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INSHIPPING.status].includes(row?.status) &&
                            contractType == 2 &&
                            row?.converNoticeFlag == 0 &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'Ttnotice',
                          label: '转出库通知单',
                          permi: `${permiPrefix}:to-notice`,
                          handler: async (row) => {
                            TtnoticeDiaRef.value?.open(row)
                          }
                        },
                        {
                          isShow:
                            [
                              SaleContractStatusEnum.INSINGBACK.status,
                              SaleContractStatusEnum.INPURCHASE.status,
                              SaleContractStatusEnum.INSHIPPING.status
                            ].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'changeContract',
                          label: '变更',
                          permi: `${permiPrefix}:change`,
                          handler: async (row) => {
                            handleChange(getContractId(row), 'change')
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INSINGBACK.status].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'signBack',
                          label: '回签',
                          permi: `${permiPrefix}:sign-back`,
                          handler: async (row) => {
                            handleSignBack(row)
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INPURCHASE.status].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'toPurchasePlan',
                          label: '下推采购计划',
                          permi: `${permiPrefix}:to-purchase-plan`,
                          handler: async (row) => {
                            handleToPurchasePlan(getContractId(row))
                          }
                        },
                        {
                          isShow:
                            [
                              SaleContractStatusEnum.INSINGBACK.status,
                              SaleContractStatusEnum.INPURCHASE.status,
                              SaleContractStatusEnum.INSHIPPING.status
                            ].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'finish',
                          label: '作废',
                          permi: `${permiPrefix}:close`,
                          handler: async (row) => {
                            handleClose(row)
                          }
                        },
                        {
                          isShow:
                            [SaleContractStatusEnum.INSHIPPING.status].includes(row?.status) &&
                            row?.confirmFlag !== 0 &&
                            row?.changeStatus !== 2,
                          otherKey: 'order-done',
                          label: '完成单据',
                          permi: `${permiPrefix}:order-done`,
                          handler: async (row) => {
                            handleOrderDone(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag == 0,
                          otherKey: 'confirm',
                          label: '确认变更',
                          permi: `${permiPrefix}-change:confirm`,
                          handler: async (row) => {
                            confirmChange(row)
                          }
                        },
                        {
                          isShow: row.confirmFlag !== 0,
                          otherKey: 'copy',
                          label: '复制',
                          permi: `${permiPrefix}:create`,
                          handler: async (row) => {
                            eplusDialogRef.value?.openCopy(
                              getContractId(row),
                              contractType == 1
                                ? '外销合同'
                                : contractType == 2
                                  ? '内销合同'
                                  : '外币采购'
                            )
                          }
                        },
                        {
                          /* {
                        isShow: [
                          SaleContractStatusEnum.CANCEL.status,
                        ].includes(row?.status),
                        otherKey: 'rollbackFinish',
                        label: '反作废',
                        permi: 'sms:export-sale-contract:rollback-finish',
                        handler: async (row) => {
                          ExportSaleContractApi.close({id:row.id}).then(() => {
                            message.success('反作废成功')
                            handleRefresh()
                          })
                        }
                      } */
                        }
                      ]}
                      auditable={row}
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

// const handleGenContract = async (row) => {
//   await message.confirm('确认生成购销合同?')
//   await ExportSaleContractApi.genContract(row.id)
//   message.success('操作成功')
// }
// const handleDelGenContract = async (row) => {
//   await message.confirm('确认删除购销合同?')
//   await ExportSaleContractApi.genContractDel(row.id)
//   message.success('操作成功')
// }

const handleCreate = () => {
  eplusDialogRef.value?.openCreate(
    contractType == 1 ? '外销合同' : contractType == 2 ? '内销合同' : '外币采购'
  )
}

const getCurrencySummary = (res, key) => currencyJsonAnalysis(res?.summary?.[key])

const planDialogRef = ref()
const planParams = ref()
/**
 * 转出运计划
 * @param row 行数据，批量操作时为 null
 * @param queryMode 查询模式：QUERY_MODE.DOCUMENT(1)=单据模式，QUERY_MODE.PRODUCT(2)=产品模式
 */
const handleCreateShipmentPlan = async (row, queryMode) => {
  // 批量操作模式（无row参数）
  if (!row) {
    const list = eplusListRef.value?.checkedItems
    if (!isValidArray(list)) {
      ElMessage.error('请先选择需要转出运计划的产品')
      return
    }

    const changeStatuslist = list.map((item) =>
      item.parent ? item.parent.changeStatus : item.changeStatus
    )
    if (changeStatuslist && changeStatuslist.indexOf(2) >= 0) {
      message.error('存在变更中记录信息，请核对！')
      return
    }

    const companyIdSet = new Set(list.map((item) => item.foreignTradeCompanyId))
    if (companyIdSet.size > 1) {
      ElMessage.error('请先选择同一报关主体数据进行操作')
      return
    }

    const ids: number[] = []
    list.forEach((item) => {
      if (queryMode === QUERY_MODE.PRODUCT) {
        // 单据模式：从children中筛选可出运产品
        item.children
          ?.filter(
            (obj) => (Number(obj.quantity) || 0) - (Number(obj.transferShippedQuantity) || 0) > 0
          )
          .forEach((child) => ids.push(Number(child.id)))
      } else {
        // 产品模式：直接判断当前行
        const quantity = Number(item.quantity) || 0
        const transferShippedQuantity = Number(item.transferShippedQuantity) || 0
        if (quantity - transferShippedQuantity > 0) {
          ids.push(Number(item.id))
        }
      }
    })

    if (ids.length === 0) {
      ElMessage.error('所选产品已全部转出运计划')
      return
    }

    const firstItem = list[0]
    const companyId = firstItem.foreignTradeCompanyId || firstItem.parent?.foreignTradeCompanyId
    const companyName =
      firstItem.foreignTradeCompanyName || firstItem.parent?.foreignTradeCompanyName

    planParams.value = {
      companyId,
      companyName,
      childrenIdList: ids.join(',')
    }
  }
  // 单行操作模式
  else {
    // 产品模式 (queryMode === QUERY_MODE.PRODUCT)
    if (queryMode === QUERY_MODE.PRODUCT) {
      if (Number(row.quantity) - Number(row.transferShippedQuantity) <= 0) {
        ElMessage.error('该产品已全部转出运计划')
        return
      }
      planParams.value = {
        companyId: row.foreignTradeCompanyId,
        companyName: row.foreignTradeCompanyName,
        childrenIdList: String(row.id)
      }
    }
    // 单据模式 (queryMode === QUERY_MODE.DOCUMENT)
    else {
      let children = row.children
      let foreignTradeCompanyId = row.foreignTradeCompanyId
      let foreignTradeCompanyName = row.foreignTradeCompanyName

      if (!isValidArray(children)) {
        const detail = await ExportSaleContractApi.getExportSaleContract({ id: getContractId(row) })
        children = detail?.children
        foreignTradeCompanyId = detail?.foreignTradeCompanyId
        foreignTradeCompanyName = detail?.foreignTradeCompanyName
      }

      const validChildrenIds = children
        ?.filter(
          (obj) => (Number(obj.quantity) || 0) - (Number(obj.transferShippedQuantity) || 0) > 0
        )
        .map((item) => item.id)

      if (!validChildrenIds || validChildrenIds.length === 0) {
        ElMessage.error('该合同产品已全部转出运计划')
        return
      }

      planParams.value = {
        companyId: foreignTradeCompanyId,
        companyName: foreignTradeCompanyName,
        childrenIdList: validChildrenIds.join(',')
      }
    }
  }

  await ExportSaleContractApi.checkContractStatus(planParams.value)
  planDialogRef.value?.openCreate('出运计划')
}

const createShipmentPlan = async (row, type) => {
  if (type) {
    planParams.value = {
      companyId: row.foreignTradeCompanyId,
      companyName: row.foreignTradeCompanyName,
      childrenIdList: [row.id]
    }
  } else {
    planParams.value = row
  }
  await ExportSaleContractApi.checkContractStatus(planParams.value)
  planDialogRef.value?.openCreate('出运计划')
}

//--批量回签
const handleSignBackBatch = () => {
  let list = eplusListRef.value?.checkedItems
  if (isValidArray(list)) {
    let inSignBack = list.filter((item) => item.status == SaleContractStatusEnum.INSINGBACK.status)
    if (inSignBack.length < list.length) {
      ElMessage.error('只能选择待回签状态的记录！')
      return
    } else {
      const simpleData: any[] = []
      list.forEach((item) => {
        if (item.parent) {
          let obj = {
            id: getContractId(item.parent),
            code: item.parent.code
          }
          simpleData.push(obj)
        } else {
          // 单据模式，直接使用 item 的主表 id
          let obj = {
            id: getContractId(item),
            code: item.code
          }
          simpleData.push(obj)
        }
      })
      signBackRef.value.open('回签', simpleData)
    }
  } else {
    ElMessage.error('请先选择需要回签的合同信息')
    return
  }
}

onActivated(async () => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
