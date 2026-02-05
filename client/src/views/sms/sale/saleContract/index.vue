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
        <!-- <el-button
          v-if="activeName == 'first'"
          type="primary"
          @click="handleCreate()"
          v-hasPermi="[`${permiPrefix}:create`]"
        >
          新增
        </el-button> -->
        <el-button
          v-if="activeName !== 'eight'"
          type="primary"
          @click="handleBatchPrint"
          v-hasPermi="[`${permiPrefix}:print`]"
        >
          批量打印
        </el-button>
        <el-button
          v-if="activeName !== 'eight'"
          type="primary"
          @click="handleBatchExport"
          v-hasPermi="[`${permiPrefix}:export`]"
        >
          批量导出
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
    <template #detail="{ key }">
      <ExportSaleContractDetail :id="key" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel, getDictOptions, getDictValue } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as LoanAppApi from '@/api/oa/loanapp'
import LockCom from '../exportSaleContract/components/LockCom.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { useCountryStore } from '@/store/modules/countrylist'
import { useAppStore } from '@/store/modules/app'
import { currencyJsonAnalysis, formatNum, openPdf } from '@/utils'
import { EplusSkuName } from '@/components/EplusSkuName'
import { checkPermi } from '@/utils/permission'
import download from '@/utils/download'
import { useRateStore } from '@/store/modules/rate'
import ExportSaleContractDetail from '../exportSaleContract/ExportSaleContractDetail.vue'
import { EplusCountrySelect } from '@/components/EplusSelect'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

defineOptions({ name: 'SaleContract' })

const props = withDefaults(
  defineProps<{
    type?: number
  }>(),
  { type: 1 }
)

const contractType = props.type
const permiPrefix = 'sms:export-sale-contract'

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
  { name: 'sixth', label: '待出运' },
  { name: 'seventh', label: '已完成' },
  { name: 'eight', label: '已作废' },
  { name: 'auto', label: '内部流通' }
]
const handleDialogFailure = () => {}
const confirmRow = ref({})
const searchCountryName = ref()

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const handleTabsClick = (val) => {
  activeName.value = val.props.name
  let item = eplusSearchSchema.fields.find((item) => item.name === 'statusList')
  if (val.props.name === 'first') {
    if (!item) {
      eplusSearchSchema.fields.push(statusListItem)
    }
  } else {
    if (item) {
      eplusSearchSchema.fields.splice(eplusSearchSchema.fields.indexOf(item), 1)
    }
  }
}

// 获取父级数据（eplus-table 组件中子级数据有 parent 属性指向父级）
const getParentRows = (selectedRows: any[]) => {
  const parentMap = new Map()
  selectedRows.forEach((row) => {
    if (row.parent) {
      if (!parentMap.has(row.parent.id)) {
        parentMap.set(row.parent.id, row.parent)
      }
    } else if (row.children || row.saleType !== undefined) {
      if (!parentMap.has(row.id)) {
        parentMap.set(row.id, row)
      }
    }
  })
  return Array.from(parentMap.values())
}

// 校验选中数据的合同类型
const validateContractTypes = (parentRows: any[]) => {
  const saleTypes = [...new Set(parentRows.map((row) => row.saleType))]
  // 内销合同不能与其他类型合同混合选择
  if (saleTypes.includes(2)) {
    if (saleTypes.length > 1) {
      message.warning('内销合同不能与其他类型合同同时选择，请重新选择')
      return false
    }
  }
  return true
}

// 批量打印
const handleBatchPrint = async () => {
  const selectedRows = eplusListRef.value.checkedItems
  if (!selectedRows || selectedRows.length === 0) {
    message.warning('请至少选择一条数据')
    return
  }
  // 获取父级数据
  const parentRows = getParentRows(selectedRows)

  // 校验合同类型（内销合同不能与其他类型混合选择）
  if (!validateContractTypes(parentRows)) {
    return
  }

  try {
    // 提取合同ID列表
    const ids = parentRows.map((row) => row.id)

    // 判断合同类型（根据第一条数据的 saleType）
    const saleType = parentRows[0]?.saleType

    // 根据合同类型设置打印参数
    let printType = ''
    let reportCode = ''

    if (saleType === 2) {
      // 内销合同
      printType = 'domestic'
      reportCode = 'domestic_sale_contract'
    } else {
      // 外销或工厂合同
      printType = 'detail'
      reportCode = 'export-sale-contract'
    }

    const url = await ExportSaleContractApi.batchPrintSaleContract({
      ids,
      reportCode,
      printType
    })

    openPdf(url)
  } catch (error) {
    message.error('批量打印失败')
  }
}

// 批量导出
const handleBatchExport = async () => {
  const selectedRows = eplusListRef.value?.checkedItems
  if (!selectedRows || selectedRows.length === 0) {
    message.warning('请至少选择一条数据')
    return
  }

  // 获取父级数据（只调用一次）
  const parentRows = getParentRows(selectedRows)

  // 校验合同类型（内销合同不能与其他类型混合选择）
  if (!validateContractTypes(parentRows)) {
    return
  }

  try {
    // 提取合同ID列表
    const ids = parentRows.map((row) => row.id)

    // 判断合同类型（根据第一条数据的 saleType）
    const saleType = parentRows[0]?.saleType

    // 根据合同类型设置导出参数
    let exportType = ''
    let reportCode = ''

    if (saleType === 2) {
      // 内销合同
      exportType = 'domestic'
      reportCode = 'domestic-sale-contract-detail'
    } else {
      // 外销或工厂合同
      exportType = 'detail'
      reportCode = 'export-sale-contract-detail'
    }

    // 调用批量导出接口
    const data = await ExportSaleContractApi.batchExportSaleContract({
      ids,
      reportCode,
      exportType
    })

    if (data && data.size) {
      download.excel(data, '批量销售合同' + '.xlsx')
    }
  } catch (error) {
    message.error('批量导出失败')
  }
}

const exportFileName = computed(() => {
  if (checkPermi([`${permiPrefix}:export`])) {
    return '销售合同.xlsx'
  } else {
    return ''
  }
})

const message = useMessage()
const statusListItem = {
  name: 'statusList',
  label: '合同状态',
  component: (
    <el-select multiple>
      {getDictOptions(DICT_TYPE.SALE_CONTRACT_STATUS).map((item) => {
        return (
          <el-option
            label={item?.label}
            value={item?.value}
            key={item?.value}
          />
        )
      })}
    </el-select>
  )
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '合同号'
    },
    // {
    //   component: <el-input clearable></el-input>,
    //   name: 'skuCode',
    //   label: '产品编号'
    // },
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
        }
      ]
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'signBackDate',
          label: '回签日期',
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
    },
    {
      name: 'statusList',
      label: '合同状态',
      selectMultiple: true,
      component: (
        <el-select
          multiple
          class="!w-250px"
        >
          {getDictOptions(DICT_TYPE.SALE_CONTRACT_STATUS).map((item) => {
            return (
              <el-option
                label={item?.label}
                value={item?.value}
                key={item?.value}
              />
            )
          })}
        </el-select>
      ),
      formatter: async (args: any[]) => {
        return args.map((item) => getDictLabel(DICT_TYPE.SALE_CONTRACT_STATUS, item)).join(',')
      }
    }
  ],
  moreFields: []
}

const router = useRouter()
const appStore = useAppStore()
const handleDetail = (id: number) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/sms/sale-orders/SaleContractDetail/${id}`
    })
  }
}

const changeCount = ref(0)

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps, currentTabIndex?) => {
    let status = ''
    let currentTab = tabNameList.filter((item) => item.name == activeName.value)

    if (currentTab?.length) {
      status = getDictValue(DICT_TYPE.SALE_CONTRACT_STATUS, currentTab[0].label)
    }
    let confirmFlag = activeName.value == 'nine' ? 0 : ''
    let autoFlag = activeName.value == 'auto' ? 1 : 0
    
    // 根据当前 Tab 判断查询模式：产品 Tab (index=1) 使用产品模式 queryMode=2
    const queryMode = currentTabIndex === 1 ? 2 : 1
    
    const params = {
      status: status,
      ...ps,
      confirmFlag: confirmFlag,
      autoFlag: autoFlag,
      queryMode: queryMode
    }

    const res = await ExportSaleContractApi.getExportSaleContractPage(params)

    changeCount.value = res?.summary?.changeCount || 0

    return {
      list: res?.list || [],
      total: res?.total || 0,
      sum: {
        totalAmountThisCurrency: getCurrencySummary(res, 'sumTotalAmountThisCurrency'),
        totalAmount: getCurrencySummary(res, 'sumTotalAmount'),
        totalAmountUsd: getCurrencySummary(res, 'sumTotalAmountUsd'),
        totalSaleAmount: getCurrencySummary(res, 'sumTotalAmountThisCurrency'),
        totalSaleAmountUsd: getCurrencySummary(res, 'sumTotalAmountUsd'),
        receivedAmount: getCurrencySummary(res, 'receivedAmount'),
        unReceivedAmount: getCurrencySummary(res, 'unReceivedAmount'),
        orderGrossProfit: getCurrencySummary(res, 'orderGrossProfit')
      }
    }
  },
  delListApi: async (id) => {},
  exportListApi: async (ps) => {
    // 根据当前 Tab 判断导出模式：产品 Tab (index=1) 使用产品模式 queryMode=2
    const queryMode = ps?.currentTabIndex === 1 ? 2 : 1
    let reqPath = await ExportSaleContractApi.exportExportSaleContract({
      ...ps,
      autoFlag: activeName.value == 'auto' ? 1 : 0,
      queryMode
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
          label: '销售总金额(RMB)',
          minWidth: columnWidth.xl,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
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
          prop: 'totalAmountUsd',
          label: '销售总金额(美金)',
          minWidth: columnWidth.xl,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
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
          prop: 'orderGrossProfit',
          label: '毛利润',
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
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              if (!data || data.amount === null || data.amount === undefined) {
                return {
                  unit: data?.currency || 'USD',
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
          prop: 'unReceivedAmount',
          label: '未收金额',
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
          prop: 'custCountryName',
          label: '客户国别',
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
              return (
                <div class="flex items-center">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="[`${permiPrefix}:detail`]"
                  >
                    详情
                  </el-button>
                </div>
              )
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
          prop: 'code',
          label: '合同编号',
          isCopy: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
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
          prop: 'currency',
          label: '交易币种',
          minWidth: columnWidth.m
        },
        {
          prop: 'salesNickname',
          label: '业务员',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.sales ? `${row.sales?.nickname}` : ''
            }
          }
        },
        {
          prop: 'salesDeptName',
          label: '业务员部门',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.sales ? `${row.sales?.deptName}` : ''
            }
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
          label: '销售总金额',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
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
          prop: 'totalSaleAmountUsd',
          label: '销售总金额(USD)',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (data) => {
              //-- unit:单位 number:值
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
          field: 'purchaseTotalAmount',
          label: '采购总金额',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.purchaseWithTaxPrice?.amount && row.realPurchaseQuantity ? (
                <EplusMoneyLabel
                  val={{
                    amount: row.realPurchaseQuantity * row.purchaseWithTaxPrice?.amount || 0,
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
                    saleContractItemId: row.itemId,
                    saleContractCode: row.code,
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
          label: '待转出运数量',
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
          label: '待出运数量',
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
          prop: 'purchaseUser',
          label: '采购员',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.purchaseUser ? row.purchaseUser.nickname : ''
            }
          }
        },
        {
          prop: 'purchaseUserDeptName',
          label: '采购员部门',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return row.purchaseUser ? row.purchaseUser.deptName : ''
            }
          }
        },
        {
          prop: 'reorderFlag',
          label: '是否翻单',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row?.reorderFlag)
          }
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
          minWidth: '120px',
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
                      handleDetail(row.contractId)
                    }}
                    hasPermi="[`${permiPrefix}:detail`]"
                  >
                    详情
                  </el-button>
                </div>
              )
            }
          }
        }
      ]
    }
  ]
}

const getCurrencySummary = (res, key) => currencyJsonAnalysis(res?.summary?.[key])

onActivated(async () => {})
</script>
