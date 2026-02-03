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
        :key="item.name"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusTableRef"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        v-if="activeName == '1'"
        type="primary"
        plain
        @click="handleCreate"
        v-hasPermi="['scm:purchase-registration:create']"
        >新增
      </el-button>
      <!-- <el-button
        type="primary"
        plain
        @click="() => handleReview(null)"
        v-hasPermi="['scm:purchase-registration:batch-review']"
      >
        批量复核
      </el-button> -->
      <el-button
        v-if="activeName == '1'"
        type="primary"
        plain
        @click="async () => await handleAudit()"
        v-hasPermi="['scm:purchase-registration:audit']"
      >
        批量审核
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <RegistrationForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key }">
      <RegistrationUpdateForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <RegistrationDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>

  <ReviewDia
    ref="ReviewDiaRef"
    @success="handleRefresh"
  />

  <eplus-audit-form
    ref="auditEidtFormRef"
    :approve="examineRegistrationApprove"
    :reject="examineRegistrationReject"
    @success="handleRefresh"
  />
</template>
<script lang="tsx" setup>
import { EplusTableSchema } from '@/types/eplus'
import RegistrationForm from './RegistrationForm.vue'
import RegistrationUpdateForm from './RegistrationUpdateForm.vue'
import RegistrationDetail from './RegistrationDetail.vue'
import * as RegistrationApi from '@/api/scm/registration'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { formatNum } from '@/utils/index'
import ReviewDia from './ReviewDia.vue'
import { isValidArray } from '@/utils/is'
import { formatStringConcat } from '@/utils/formatStringConcat'
import { getVenderSimpleList } from '@/api/common'
import * as UserApi from '@/api/system/user'
import * as TaskApi from '@/api/bpm/task'
import { useUserStore } from '@/store/modules/user'
import { BpmProcessInstanceResultEnum, PurchaseRegistrationStatusEnum } from '@/utils/constants'
import { examineRegistrationApprove, examineRegistrationReject } from '@/api/audit/purchase-plan'

defineOptions({ name: 'PurchaseRegistration' })

const userId = useUserStore().getUser.id
const activeName = ref('1')
const auditEidtFormRef = ref<ComponentRef<typeof auditEidtFormRef>>()
const tabNameList = [
  { name: '1', label: '未审批' },
  { name: '2', label: '已审批' },
  { name: '3', label: '已作废' },
  { name: '0', label: '全部' }
]
const handleTabsClick = (val) => {
  activeName.value = val.props.name
}

const eplusTableRef = ref<any>(null)
const eplusDialogRef = ref<any>(null)
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
const message = useMessage()
const handleDialogFailure = () => {}
const handleCreate = () => {
  eplusDialogRef.value.openCreate('发票签收')
}
const exportFileName = ref('发票签收.xlsx') // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '发票签收')
}
const handleSubmit = async (id) => {
  // 提交请求
  try {
    await RegistrationApi.submitRegistration(id)
    message.success('提交成功')
  } catch (error) {
    message.error('提交失败')
  }
  handleRefresh()
}
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'shipInvoiceCode',
      label: '出运发票号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'invoicSkuName',
      label: '开票品名'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'saleContractCode',
      label: '销售合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1 }}
          keyword="name"
          label="name"
          value="name"
          class="!w-100%"
          placeholder="请选择"
        />
      ),
      name: 'venderName',
      label: '供应商'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.HS_MEASURE_UNIT}></eplus-dict-select>,
      name: 'hsMeasureUnit',
      label: '单位',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, args[0])
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerId',
      label: '跟单员',
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
          label: '创建日期',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const params = activeName.value === '0' ? ps : { status: activeName.value, ...ps }
    let res = await RegistrationApi.getRegistrationPage(params)
    res.list?.forEach((e) => {
      let saleContractCodes = ''
      let purchaseContractCodes = ''
      let invoicThisQuantitys = 0
      let hsMeasureUnits = ''
      let invoicSkuNames = ''
      // let taxRates = ''
      // let managers = [...new Set(e.children?.map((item) => item.manager?.nickname || ''))].join(',')
      let managers = ''
      let invoicThisPriceAmount = 0
      e.children?.forEach((item) => {
        saleContractCodes = formatStringConcat(saleContractCodes, item.saleContractCode)
        purchaseContractCodes = formatStringConcat(purchaseContractCodes, item.purchaseContractCode)
        // invoicThisQuantitys = formatStringConcat(invoicThisQuantitys, item.invoicThisQuantity) +=item.invoicThisQuantity
        invoicThisQuantitys += Number(item.invoicThisQuantity || 0)
        hsMeasureUnits = formatStringConcat(
          hsMeasureUnits,
          getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, item.hsMeasureUnit)
        )
        invoicSkuNames = formatStringConcat(invoicSkuNames, item.invoicSkuName)
        // taxRates += Number(item.taxRate || 0)
        managers = formatStringConcat(managers, item.manager?.nickname || '')
        invoicThisPriceAmount += Number(item.invoicPrice) * Number(item.invoicThisQuantity)
        item.invoicThisPrice = {
          amount: Number(item.invoicPrice) * Number(item.invoicThisQuantity),
          currency: item.purchaseCurrency
        }
      })
      e.saleContractCodes = saleContractCodes
      e.purchaseContractCodes = purchaseContractCodes
      e.invoicThisQuantitys = invoicThisQuantitys
      e.hsMeasureUnits = hsMeasureUnits
      e.invoicSkuNames = invoicSkuNames
      // e.taxRates = taxRates
      e.managers = managers
      e.invoicThisPrice = {
        amount: invoicThisPriceAmount,
        currency: e.children?.length ? e.children[0].purchaseCurrency : ''
      }
    })
    return {
      list: res.list,
      total: res.total,
      sum: {
        shippingQuantity: formatNum(res.summary?.shippingQuantityTotal, 3),
        invoicThisPrice: formatNum(res.summary?.invoicPriceTotal, 3),
        invoicThisQuantity: formatNum(res.summary?.invoicThisQuantityTotal, 3),
        invoicNoticesQuantity: formatNum(res.summary?.invoicNoticesQuantity, 3)
      }
    }
  },
  exportListApi: async (ps) => {
    if (ps.isTree) {
      exportFileName.value = '发票签收-产品.xlsx'
    } else {
      exportFileName.value = '发票签收-单据.xlsx'
    }
    const params = activeName.value === '0' ? ps : { status: activeName.value, ...ps }
    return await RegistrationApi.exportRegistration(params)
  },
  showTabs: true,
  tabs: [
    {
      label: '单据',
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'code',
          label: '发票签收编号',
          isCopy: true,
          minWidth: columnWidth.l,
          isHyperlink: true
        },
        {
          prop: 'invoiceCode',
          label: '发票编号',
          isCopy: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'invoicSkuNames',
          label: '开票品名',
          minWidth: columnWidth.m
        },
        {
          prop: 'saleContractCodes',
          label: '外销合同号',
          isCopy: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'purchaseContractCodes',
          label: '采购合同号',
          isCopy: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'hsMeasureUnits',
          label: '单位',
          minWidth: columnWidth.l
        },
        {
          prop: 'companyName',
          label: '付款单位',
          minWidth: columnWidth.l
        },
        {
          prop: 'venderCode',
          label: '供应商编号',
          minWidth: columnWidth.l
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          minWidth: columnWidth.l
        },
        // {
        //   prop: 'invoicThisQuantitys',
        //   label: '数量',
        //   isCopy: true,
        //   minWidth: columnWidth.l
        // },
        {
          prop: 'invoicThisPrice',
          label: '开票总金额',
          minWidth: columnWidth.l,
          summary: true,
          formatter: formatMoneyColumn(),
          summarySlots: {
            default: (data) => {
              return {
                unit: data.currency,
                number: data.amount
              }
            }
          }
        },
        {
          prop: 'invoicedDate',
          label: '收票日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        // {
        //   prop: 'invoiceAmount',
        //   label: '发票金额',
        //   minWidth: columnWidth.m,
        //   formatter: (row) => {
        //     return formatNum(row.invoiceAmount, 2)
        //   }
        // },
        {
          prop: 'createTime',
          label: '登记日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'invoiceCode',
          label: '登记人',
          minWidth: columnWidth.m
        },
        // {
        //   prop: 'taxRates',
        //   label: '税率'
        // },
        {
          prop: 'managers',
          label: '跟单员',
          minWidth: columnWidth.m
        },
        {
          prop: 'auditStatus',
          label: '审核状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
        },
        {
          prop: 'status',
          label: '状态',
          minWidth: columnWidth.l,
          formatter: (row) => {
            for (const key in PurchaseRegistrationStatusEnum) {
              const statusItem = PurchaseRegistrationStatusEnum[key]
              if (statusItem.status === row.status) {
                return statusItem.name
              }
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
                      handleDetail(row.id)
                    }}
                    hasPermi="['scm:purchase-registration:detail']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    submitItem={{
                      permi: 'scm:purchase-registration:submit',
                      handler: async (row) => {
                        handleSubmit(row?.id)
                      }
                    }}
                    otherItems={[
                      /* {
                        isShow: row.auditStatus === 2 && !row.invoicedDate,
                        otherKey: 'scmPurchaseRegistrationReview',
                        label: '复核',
                        permi: 'scm:purchase-registration:batch-review',
                        handler: async (row) => {
                          await handleReview(row)
                        }
                      }*/
                      {
                        isShow:
                          [PurchaseRegistrationStatusEnum.APPROVE.status].includes(row.status) ||
                          [BpmProcessInstanceResultEnum.REJECT.status].includes(row.auditStatus),
                        otherKey: 'scmPurchaseRegistrationClose',
                        label: '作废',
                        permi: 'scm:purchase-registration:close',
                        handler: async (row) => {
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
    },
    {
      label: '产品',
      isTree: true,
      isDefault: true,
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'code',
          label: '发票签收编号',
          isCopy: true,
          parent: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'invoiceCode',
          label: '发票编号',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'companyName',
          label: '付款单位',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'invoicSkuName',
          label: '开票品名',
          minWidth: columnWidth.m
        },
        {
          prop: 'saleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.l
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同号',
          minWidth: columnWidth.l
        },
        {
          prop: 'shipInvoiceCode',
          label: '出运发票号',
          minWidth: columnWidth.l
        },
        {
          prop: 'shippingQuantity',
          label: '出运数量',
          summary: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'hsMeasureUnit',
          label: '单位',
          formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
        },
        {
          prop: 'invoicNoticesQuantity',
          label: '通知开票数量',
          summary: true
        },
        {
          prop: 'invoicThisQuantity',
          label: '数量',
          summary: true
        },
        {
          prop: 'invoicThisPrice',
          label: '开票总金额',
          minWidth: columnWidth.l,
          summary: true,
          formatter: formatMoneyColumn(),
          summarySlots: {
            default: (data) => {
              return {
                unit: data.currency,
                number: data.amount
              }
            }
          }
        },
        {
          prop: 'taxRate',
          label: '税率'
        },
        {
          prop: 'manager',
          label: '跟单员',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              let { row } = data
              return `${row?.manager?.nickname ? row?.manager?.nickname : ''}`
            }
          }
        },
        {
          prop: 'auditStatus',
          label: '审核状态',
          parent: true,
          minWidth: columnWidth.l,
          formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
        },
        {
          prop: 'status',
          label: '状态',
          parent: true,
          minWidth: columnWidth.l,
          formatter: (row) => {
            for (const key in PurchaseRegistrationStatusEnum) {
              const statusItem = PurchaseRegistrationStatusEnum[key]
              if (statusItem.status === row.status) {
                return statusItem.name
              }
            }
          }
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {}
          }
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: '120px',
          fixed: 'right',
          parent: true,
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
                    hasPermi="['scm:purchase-registration:detail']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    submitItem={{
                      permi: 'scm:purchase-registration:submit',
                      handler: async (row) => {
                        handleSubmit(row?.id)
                      }
                    }}
                    otherItems={[
                      {
                        /* {
                        isShow: row.auditStatus === 2 && !row.invoicedDate,
                        otherKey: 'scmPurchaseRegistrationReview',
                        label: '复核',
                        permi: 'scm:purchase-registration:batch-review',
                        handler: async (row) => {
                          await handleReview(row)
                        }
                      } */
                      },
                      {
                        isShow: [
                          BpmProcessInstanceResultEnum.UNSUBMITTED.status,
                          BpmProcessInstanceResultEnum.REJECT.status,
                          BpmProcessInstanceResultEnum.CANCEL.status
                        ].includes(row.auditStatus),
                        otherKey: 'scmPurchaseRegistrationUpdate',
                        label: '编辑',
                        permi: 'scm:purchase-registration:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      },
                      {
                        isShow:
                          [PurchaseRegistrationStatusEnum.APPROVE.status].includes(row.status) ||
                          [BpmProcessInstanceResultEnum.REJECT.status].includes(row.auditStatus),
                        otherKey: 'scmPurchaseRegistrationClose',
                        label: '作废',
                        permi: 'scm:purchase-registration:close',
                        handler: async (row) => {
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
    }
  ]
}
const ReviewDiaRef = ref()
const handleClose = (row) => {
  ElMessageBox.confirm('确认对选中数据进行作废操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await RegistrationApi.registrationClose(row.id)
    message.success('操作成功!')
    handleRefresh()
  })
}
const handleReview = async (row?) => {
  if (row) {
    ReviewDiaRef.value?.open([row.id])
  } else {
    if (!isValidArray(eplusTableRef.value?.checkedItems)) {
      message.warning('请选择操作的数据!')
      return false
    }
    let flag = true
    let ids: number[] = []
    eplusTableRef.value?.checkedItems
      .map((item) => {
        if (item.parent) {
          return item.parent
        } else {
          return item
        }
      })
      .forEach((item) => {
        if (item.auditStatus !== 2 || item.invoicedDate) {
          flag = false
        }
        ids.push(item.id)
      })
    if (!flag) {
      message.warning('请选择审批通过且收票日期为空的数据进行复核操作!')
      return
    } else {
      ReviewDiaRef.value?.open(ids)
    }
  }
}
const tasks = ref<any[]>([]) // 任务列表
const getTaskId = async (processInstanceId: string) => {
  // 获得未取消的任务
  tasks.value = await TaskApi.getTaskListByProcessInstanceId(processInstanceId)
  tasks.value.sort((a, b) => {
    // 有已完成的情况，按照完成时间倒序
    if (a.endTime && b.endTime) {
      return b.endTime - a.endTime
    } else if (a.endTime) {
      return 1
    } else if (b.endTime) {
      return -1
      // 都是未完成，按照创建时间倒序
    } else {
      return b.createTime - a.createTime
    }
  })
  var currTasks = tasks.value.filter((it) => {
    return it.result == 1 && it.assigneeUser.id == userId
  })
  if (currTasks && currTasks.length > 0) {
    return currTasks[0].id
  }
}
const handleAudit = async () => {
  if (!isValidArray(eplusTableRef.value?.checkedItems)) {
    message.warning('请选择操作的数据!')
    return false
  }
  let flag = true
  let ids: number[] = []
  const checkedItems =
    eplusTableRef.value && eplusTableRef.value.checkedItems ? eplusTableRef.value.checkedItems : []
  for (const item of checkedItems.map((i) => i.parent || i)) {
    if (item.auditStatus == 2 && !item.processInstanceId) {
      flag = false
    }
    let taskId = await getTaskId(item.processInstanceId)
    if (!taskId) {
      flag = false
    } else {
      ids.push(taskId)
    }
  }
  if (!flag) {
    message.warning('请选择未审批并且审批人是当前登录人的记录信息!')
    return
  } else {
    auditEidtFormRef.value.open({
      ids: [...new Set(ids)],
      createName: tasks.value[0]?.processInstance?.startUserNickname
    })
  }
}
</script>
<style lang="scss" scoped></style>
