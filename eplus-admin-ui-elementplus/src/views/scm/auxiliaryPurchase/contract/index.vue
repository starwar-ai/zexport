<template>
  <div class="h-45px bg-[var(--left-menu-bg-color)] pl-15px pr-15px flex items-center">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        :name="0"
      />
      <el-tab-pane
        label="上游变更"
        :name="99"
      />
      <el-tab-pane
        label="待提交"
        :name="1"
      />
      <el-tab-pane
        label="待审批"
        :name="2"
      />
      <el-tab-pane
        label="已驳回"
        :name="3"
      />
      <el-tab-pane
        label="待回签"
        :name="4"
      />
      <el-tab-pane
        label="待出运"
        :name="5"
      />
      <el-tab-pane
        label="已完成"
        :name="6"
      />
      <el-tab-pane
        label="已作废"
        :name="7"
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
      <div class="flex justify-between w-100%">
        <!-- 页面按钮 -->
        <div class="flex-1 overflow-x-auto">
          <!-- 左侧，使用flex-1自动填充剩余空间 -->
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
      <ContractForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key }">
      <ContractForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #change="{ key }">
      <ContractForm
        :id="key"
        mode="change"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <ContractDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
    <template #confirm="{ key }">
      <ExportSaleContractConfirm
        :id="key"
        mode="confirm"
        :row="confirmRow"
        :type="4"
        @handle-success="handleRefresh"
        @success="backChange"
      />
    </template>
  </eplus-dialog>

  <Dialog
    v-model="dialogVisible"
    title="返工重购"
    width="500"
    :before-close="cancelChange"
    :destroy-on-close="true"
  >
    <el-form :model="form">
      <el-form-item label="包材采购合同:">
        {{ form?.code ? form?.code : '-' }}
      </el-form-item>
      <el-form-item label="返工重购原因:">
        <el-input
          type="textarea"
          v-model="form.reason"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="confirmChange"
          >生成新的采购计划</el-button
        >
        <el-button @click="cancelChange"> 取消 </el-button>
      </div>
    </template>
  </Dialog>

  <SignBackForm
    ref="signBackRef"
    @success="handleRefresh"
  />

  <eplus-dialog
    ref="paymentRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <PaymentForm
        mode="create"
        :feeShareFlag="0"
        :contractParams="contractParams"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as PurchaseContractApi from '@/api/scm/auxiliaryPurchaseContract'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { propTypes } from '@/utils/propTypes'
import { columnWidth, formatDateColumn, formatMoneyColumn } from '@/utils/table'
import ContractForm from './ContractForm.vue'
import SignBackForm from '../components/SignBackForm.vue'
import PaymentForm from '../../../oa/corporatePayments/CorporatePaymentsForm.vue'
import ContractDetail from './ContractDetail.vue'
import { PurchaseContractStatusEnum, SkuTypeEnum } from '@/utils/constants'
import { checkPermi } from '@/utils/permission'
import ExportSaleContractConfirm from '@/views/sms/sale/exportSaleContract/ExportSaleContractConfirm.vue'
import { getcompanySimpleList } from '@/api/common'
import * as UserApi from '@/api/system/user'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { isValidArray } from '@/utils/is'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { currencyJsonAnalysis, uniqueItems } from '@/utils'

defineOptions({ name: 'AuxiliaryPurchaseContract' })

const form = reactive({
  id: undefined,
  code: '',
  reason: ''
})
const handleDialogFailure = () => {}
const eplusListRef = ref()
const activeName = ref(0)
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const dialogVisible = ref(false)
const contractParams = ref()
const confirmChange = async () => {
  if (!form.reason) {
    message.error('请输入返工重购原因！')
  } else {
    await PurchaseContractApi.rePurchaseContract({ id: form.id, rePurchaseDesc: form.reason })
    message.success('返工重购成功')
    dialogVisible.value = false
    form.reason = ''
    handleRefresh()
  }
}
const cancelChange = () => {
  dialogVisible.value = false
}
const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const batchHandleToPayment = async () => {
  // 获取选中的多行
  let checkedItems: any[] = []
  eplusListRef.value?.checkedItems?.forEach((item) => {
    if (isValidArray(item.children)) {
      checkedItems = [...checkedItems, item]
    } else {
      checkedItems = [...checkedItems, item.parent]
    }
  })
  checkedItems = uniqueItems(checkedItems)

  if (!checkedItems.length) {
    message.warning('请先选择要付款的合同')
    return
  }

  //判断同一个供应商 同一个主体
  const checkItem = checkedItems[0]
  for (const item of checkedItems) {
    if (item.companyId !== checkItem.companyId || item.venderCode !== checkItem.venderCode) {
      message.warning('采购主体或者供应商不一致，请单独下推')
      return
    }
    if (item.auxiliaryPaymentFlag === 1) {
      message.warning(`编号为${item.code}的合同已转对公付款，请勿重复操作`)
      return
    }
  }

  const codes = [...new Set(checkedItems.map((row) => row.code))]

  contractParams.value = {
    relationType: 4,
    relationCode: codes,
    paymentAmount: {
      amount: checkedItems.reduce((sum, row) => {
        const amount = row.totalAmount?.amount || 0
        return sum + amount
      }, 0),
      currency: checkItem.totalAmount?.currency
    },
    companyId: checkItem.companyId,
    venderCode: checkItem.venderCode,
    venderName: checkItem.venderName
  }
  paymentRef.value.openCreate('费用支付（单位）')
}

const shipmentBtnSchemas = [
  {
    component: (
      <el-button
        type="primary"
        onClick={batchHandleToPayment}
        v-hasPermi="['scm:auxiliary-purchase-contract:payment']"
      >
        批量对公付款
      </el-button>
    )
  }
]
const defaultBtnSchemas = []
let btnSchemas: any = reactive(defaultBtnSchemas)
const tabIndex = ref('0')
const handleTabsClick = (val) => {
  tabIndex.value = val.index
  btnSchemasFormat(val.index)
}

const btnSchemasFormat = (data) => {
  switch (data) {
    case '6':
      btnSchemas = shipmentBtnSchemas
      break
    default:
      btnSchemas = defaultBtnSchemas
      break
  }
}

const message = useMessage()
const props = defineProps({
  type: propTypes.string.def('')
})
let companyName = ref('')
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      name: 'companyId',
      label: '采购主体',
      component: (
        <eplus-custom-select
          api={getcompanySimpleList}
          label="name"
          value="id"
          placeholder="公司主体"
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
      name: 'venderName',
      label: '供应商名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '采购单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'auxiliarySaleContractCode',
      label: '关联销售合同'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'auxiliaryPurchaseContractCode',
      label: '关联采购合同'
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

const exportFileName = computed(() => {
  return checkPermi(['scm:auxiliary-purchase-contract:export']) ? '包材采购合同管理.xlsx' : ''
})

const confirmRow = ref({})
const backChange = (data) => {
  // handleUpdate(data.id, data.type)
}

const handleChange = (id) => {
  eplusDialogRef.value?.openChange(id, '包材采购合同变更')
}
const handleConfirm = (row) => {
  confirmRow.value = row
  confirmRow.value.status = row.contractStatus
  eplusDialogRef.value?.openConfirm(row.id)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '包材采购合同')
}

const handleSubmit = async (data) => {
  // bug 799 要求点击提交跳详情
  eplusDialogRef.value?.openEdit(data.id, '包材采购合同')
}

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const signBackRef = ref()
const handleSignBack = async (row) => {
  let obj = {
    id: row.id,
    code: row.code
  }
  signBackRef.value.open('回签', obj)
}

const paymentRef = ref()
const handleToPayment = async (row) => {
  let obj = {
    id: row.id,
    code: row.code
  }
  contractParams.value = {
    relationType: 4,
    relationCode: [row.code],
    paymentAmount: row.totalAmount,
    companyId: row.companyId,
    venderCode: row.venderCode,
    venderName: row.venderName
  }
  paymentRef.value.openCreate('费用支付（单位）')
}

const handleFinish = async (row) => {
  await message.confirm('确认作废该合同吗？')
  await PurchaseContractApi.batchFinishPurchaseContract([row.id])
  message.success('作废成功')
  handleRefresh()
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await PurchaseContractApi.getPurchaseContractPage({
      contractStatus:
        activeName.value == 0 || activeName.value == 99 ? undefined : activeName.value,
      confirmFlag: activeName.value == 99 ? 0 : undefined,
      ...ps
    })
    return {
      list: res.list,
      total: res.total,
      sum: {
        quantity: res?.summary?.qtySum,
        amount: currencyJsonAnalysis(res?.summary?.totalSum),
        totalQuantity: res?.summary?.qtySum
      }
    }
  },
  delListApi: async (id) => {
    await PurchaseContractApi.deletePurchaseContract(id)
  },
  exportListApi: async (ps) => {
    return await PurchaseContractApi.exportPurchaseContract(ps)
  },
  summary: true,
  selection: true,
  showTabs: true,
  tabs: [
    {
      label: '单据',
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'code',
          label: '采购合同单号',
          isCopy: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'companyName',
          label: '采购主体',
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
          prop: 'venderName',
          label: '供应商',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.m
        },
        {
          prop: 'manager',
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
          minWidth: columnWidth.m
        },
        {
          prop: 'totalAmount',
          label: '采购金额',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'createTime',
          label: '创建时间',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
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
                    hasPermi="['scm:auxiliary-purchase-contract:detail']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      // {
                      //   isShow: [
                      //    PurchaseContractStatusEnum.AWAITING_ORDER.status //待下单
                      //   ].includes(row?.contractStatus),
                      //  otherKey: 'orderContract',
                      //  label: '下单',
                      //   permi: 'scm:auxiliary-purchase-contract:order',
                      //   handler: async (row) => {
                      //     PurchaseContractApi.placeOrderPurchaseContract([row.id]).then(() => {
                      //      message.success('下单成功')
                      //     handleRefresh()
                      //   })
                      // }
                      //},
                      //{
                      //isShow: [
                      //PurchaseContractStatusEnum.AWAITING_ORDER.status, //待下单
                      //PurchaseContractStatusEnum.EXPECTING_DELIVERY.status, //待到货
                      //PurchaseContractStatusEnum.FINISHED.status //已完成
                      //].includes(row?.contractStatus),
                      //  otherKey: 'payContract',
                      //  label: '请款',
                      //permi: 'scm:auxiliary-purchase-contract:pay',
                      //  handler: async (row) => {
                      //  PurchaseContractApi.payPurchaseContract([row.id]).then(() => {
                      //      message.success('请款成功')
                      //      handleRefresh()
                      //    })
                      //  }
                      //},
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.READY_TO_SUBMIT.status //待提交
                          ].includes(row?.contractStatus) && row.confirmFlag !== 0,
                        otherKey: 'auxiliarypurchasecontractsubmit',
                        label: '提交',
                        permi: 'scm:auxiliary-purchase-contract:submit',
                        handler: async (row) => {
                          handleSubmit(row)
                        }
                      },
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.READY_TO_SUBMIT.status //待提交
                          ].includes(row?.contractStatus) && row.confirmFlag !== 0,
                        otherKey: 'auxiliarypurchasecontractupdate',
                        label: '编辑',
                        permi: 'scm:auxiliary-purchase-contract:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      },
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.READY_TO_SUBMIT.status, //待提交
                            PurchaseContractStatusEnum.AWAITING_ORDER.status, //待下单
                            PurchaseContractStatusEnum.REJECTED.status, //已驳回
                            PurchaseContractStatusEnum.EXPECTING_DELIVERY.status, //待到货
                            PurchaseContractStatusEnum.FINISHED.status //已完成
                          ].includes(row?.contractStatus) && row.confirmFlag !== 0,
                        otherKey: 'finishContract',
                        label: '作废',
                        permi: 'scm:auxiliary-purchase-contract:finish',
                        handler: async (row) => {
                          handleFinish(row)
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
                        permi: 'scm:auxiliary-purchase-contract:change',
                        handler: async (row) => {
                          handleChange(row.id)
                        }
                      },
                      {
                        isShow: row.confirmFlag == 0,
                        otherKey: 'confirm',
                        label: '确认变更',
                        permi: 'scm:auxiliary-purchase-contract:confirm',
                        handler: async (row) => {
                          handleConfirm(row)
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
      ]
    },
    {
      label: '产品',
      isTree: true,
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'code',
          label: '包材采购合同编号',
          isCopy: true,
          minWidth: columnWidth.m,
          parent: true,
          isHyperlink: true
        },
        {
          prop: 'companyName',
          label: '采购主体',
          minWidth: columnWidth.l,
          parent: true
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'manager',
          label: '跟单员',
          minWidth: columnWidth.m,
          parent: true,
          formatter: (row) => {
            return row.manager?.nickname
          }
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'contractStatus',
          label: '单据状态',
          minWidth: columnWidth.m,
          parent: true,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.PURCHASE_CONTRACT_STATUS, row?.contractStatus)
          }
        },
        {
          prop: 'auditStatus',
          label: '审核状态',
          minWidth: columnWidth.m,
          parent: true,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row.auditStatus)
          }
        },
        {
          prop: 'sortNum',
          label: '序号',
          minWidth: columnWidth.m
        },
        {
          prop: 'skuCode',
          label: '产品编号',
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="relative">
                  {/* 普通产品的角标不展示 */}
                  {row?.skuCode}
                  {row?.skuType === SkuTypeEnum.COMMON.status ? (
                    ''
                  ) : (
                    <span id="badge">
                      {getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType).split('')[0] || ''}
                    </span>
                  )}
                </div>
              )
            }
          }
        },
        {
          prop: 'skuName',
          label: '包材名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'productPicUrl',
          label: '图片',
          minWidth: columnWidth.s,
          isCopy: true,
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
          prop: 'quantity',
          label: '采购数量',
          minWidth: columnWidth.m
        },
        {
          prop: 'specRemark',
          label: '规格描述',
          minWidth: columnWidth.l
        },
        {
          prop: 'withTaxPrice',
          label: '单价',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'totalAmount',
          label: '总金额',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return (
              <EplusMoneyLabel
                val={{
                  amount: row.quantity * row.withTaxPrice.amount,
                  currency: row.withTaxPrice.currency
                }}
              />
            )
          }
        },
        {
          prop: 'payedAmount',
          label: '已付款金额',
          minWidth: columnWidth.m,
          formatter: (row) => {
            //如果付款金额等于总金额，则明细显示计算的总金额为已付金额
            if (
              row.parent.payedAmount != null &&
              row.parent.payedAmount.amount == row.parent.totalAmount.amount
            ) {
              return (
                <EplusMoneyLabel
                  val={{
                    amount: row.quantity * row.withTaxPrice.amount,
                    currency: row.withTaxPrice.currency
                  }}
                />
              )
            }
          }
        },
        {
          prop: 'auxiliarySaleContractCode',
          label: '关联销售合同',
          minWidth: columnWidth.l
        },
        {
          prop: 'auxiliaryPurchaseContractCode',
          label: '关联采购合同',
          minWidth: columnWidth.l
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: columnWidth.l,
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
                    hasPermi="['scm:auxiliary-purchase-plan:detail']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.READY_TO_SUBMIT.status //待提交
                          ].includes(row?.contractStatus) && row.confirmFlag !== 0,
                        otherKey: 'auxiliarypurchasecontractsubmit',
                        label: '提交',
                        permi: 'scm:auxiliary-purchase-contract:submit',
                        handler: async (row) => {
                          handleSubmit(row)
                        }
                      },
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.READY_TO_SUBMIT.status //待提交
                          ].includes(row?.contractStatus) && row.confirmFlag !== 0,
                        otherKey: 'auxiliarypurchasecontractupdate',
                        label: '编辑',
                        permi: 'scm:auxiliary-purchase-contract:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      },
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.FINISHED.status //已完成
                          ].includes(row?.contractStatus) && row.confirmFlag !== 0,
                        otherKey: 'rebackPurchase',
                        label: '返工重购',
                        permi: 'scm:auxiliary-purchase-contract:order',
                        handler: async (row) => {
                          form.code = row?.code
                          form.id = row?.id
                          dialogVisible.value = true
                        }
                      },
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.AWAITING_ORDER.status //待下单
                          ].includes(row?.contractStatus) &&
                          row.signBackFlag === 0 &&
                          row.confirmFlag !== 0,
                        otherKey: 'signBackContract',
                        label: '回签',
                        permi: 'scm:auxiliary-purchase-contract:sign-back',
                        handler: async (row) => {
                          handleSignBack(row)
                        }
                      },
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.EXPECTING_DELIVERY.status //待出运
                          ].includes(row?.contractStatus) && row.auxiliaryPaymentFlag === 0,
                        otherKey: 'toPayment',
                        label: '转对公付款',
                        permi: 'scm:auxiliary-purchase-contract:payment',
                        handler: async (row) => {
                          handleToPayment(row)
                        }
                      },
                      {
                        isShow:
                          [
                            PurchaseContractStatusEnum.READY_TO_SUBMIT.status, //待提交
                            PurchaseContractStatusEnum.AWAITING_ORDER.status, //待下单
                            PurchaseContractStatusEnum.REJECTED.status, //已驳回
                            PurchaseContractStatusEnum.EXPECTING_DELIVERY.status, //待到货
                            PurchaseContractStatusEnum.FINISHED.status //已完成
                          ].includes(row?.contractStatus) && row.confirmFlag !== 0,
                        otherKey: 'finishContract',
                        label: '作废',
                        permi: 'scm:auxiliary-purchase-contract:finish',
                        handler: async (row) => {
                          handleFinish(row)
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
                        permi: 'scm:auxiliary-purchase-contract:change',
                        handler: async (row) => {
                          handleChange(row.id)
                        }
                      },

                      {
                        isShow: row.confirmFlag == 0,
                        otherKey: 'confirm',
                        label: '确认变更',
                        permi: 'scm:auxiliary-purchase-contract:confirm',
                        handler: async (row) => {
                          handleConfirm(row)
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

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style lang="scss">
#badge {
  position: absolute;
  right: -14px;
  top: -5px;
  color: #7cc440;
  font-size: 12px;
}
</style>
