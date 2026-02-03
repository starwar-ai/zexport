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
        :key="item.label"
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
        type="primary"
        plain
        @click="handleCreate"
        v-hasPermi="['ems:send:create']"
      >
        新增
      </el-button>
      <el-button
        v-if="String(activeName) === '5'"
        type="primary"
        plain
        @click="handleReworkRelease(null, 2)"
        v-hasPermi="['ems:send:import']"
      >
        回填费用
      </el-button>
      <el-button
        v-if="String(activeName) === '6'"
        type="primary"
        plain
        @click="
          async () => {
            await handleDownPublic()
          }
        "
        v-hasPermi="['ems:send:import']"
      >
        下推对公支付
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <!-- 新增编辑 -->
    <template #create="{ key }">
      <SendCreateForm
        :id="key"
        mode="create"
      />
    </template>

    <template #edit="{ key }">
      <SendEditForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #detail="{ key }">
      <SendDetail :id="key" />
    </template>

    <template #change="{ key }">
      <SendUpdateNumber
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>

  <design-dialog
    ref="changeDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  />

  <EmsSendDialog
    ref="emsDialogRef"
    :key="Date.now()"
    @handle-success="handleRefresh"
  />

  <ImportDia
    ref="emsImportDiaRef"
    @success="handleImportSuccess"
  />

  <!-- 下推对公 -->
  <eplus-dialog
    ref="eplusDialogOaRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #create>
      <corporate-payments-form
        mode="create"
        :feeShareFlag="0"
        :emsParams="emsParams"
        @success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <!-- 费用归集 -->
  <eplus-dialog
    ref="FeeShareDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <!-- <template #edit="{ key }">
      <FeeShareForm
        :id="key"
        mode="edit"
      />
    </template> -->
    <template #create="{ key }">
      <FeeShareForm
        :getApi="EmsListApi.getEmSDetail"
        :updateApi="EmsListApi.updateFeeShare"
        :feeShareAmount="feeShareAmount"
        :id="key"
        channel="send"
        mode="create"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'

import * as EmsListApi from '@/api/ems/emsList' // 寄件接口集合
import * as UserApi from '@/api/system/user'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { SendStatusEnum } from '@/utils/constants'
import { columnWidth, formatDateColumn, formatMoneyColumn } from '@/utils/table'
// import { useCountryStore } from '@/store/modules/countrylist'
import SendCreateForm from './sendCreateForm.vue'
import SendEditForm from './sendEditForm.vue'
import SendUpdateNumber from './sendUpdateNumber.vue'
import SendDetail from './sendDetail.vue'
import FeeShareForm from './components/FeeShare/FeeShareForm.vue'
import EmsSendDialog from './components/EmsSendDialog.vue' // 弹框
import ImportDia from './components/ImportDia.vue'
import { ElMessageBox } from 'element-plus'
import CorporatePaymentsForm from '@/views/oa/corporatePayments/CorporatePaymentsForm.vue'
import { moneyTotalPrecision } from '@/utils/config'
import { currencyJsonAnalysis, formatNum } from '@/utils'

defineOptions({ name: 'EmsSend' })

const tabNameList = [
  { name: '0', label: '全部' },
  { name: '1', label: '待提交' },
  { name: '2', label: '待审批' },
  { name: '3', label: '已驳回' },
  { name: '4', label: '待寄出' },
  { name: '5', label: '已寄出' },
  { name: '6', label: '待请款' },
  { name: '7', label: '已完成' },
  { name: '8', label: '已作废' }
]

const handleDialogFailure = () => {}
const eplusTableRef = ref()
const emsParams = ref()

const activeName = ref('0')
const tabIndex = ref(0)
const exportFileName = ref('寄件管理.xlsx')

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const { t } = useI18n()

const handleTabsClick = (val) => {
  tabIndex.value = Number(val.index)
  activeName.value = String(val.props.name)
}

const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}

const message = useMessage()

const emsDialogRef = ref()

const eplusDialogOaRef = ref()

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '单据编号'
    },
    {
      component: <eplus-simple-ems-select></eplus-simple-ems-select>,
      name: 'venderCode',
      label: '快递公司',
      formatter: async (args: any[]) => {
        let result = await EmsListApi.getSimpleList()
        let filteredArr = result.list.filter((item) => item.code == args[0])[0]
        return filteredArr.shortName || filteredArr.name
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'receiveName',
      label: '收件方'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'receiveMsg',
      label: '收件信息'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'expressCode',
      label: '物流单号'
    },
    {
      component: <eplus-user-select />,
      name: 'inputUserId',
      label: '录入人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-user-select />,
      name: 'actualUserId',
      label: '实际寄件人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.PAYMENT_STATUS} />,
      name: 'payStatus',
      label: '付款状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.PAYMENT_STATUS, args[0])
      }
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'sendTime',
          label: '寄件时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

// 新增/编辑验货单
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('寄件单')
}
const handleEdit = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '寄件单')
}

// 提交成功
const handleSubmit = async (id: number) => {
  await EmsListApi.submitSend(id)
  message.success('提交成功')
  handleRefresh()
}

// 删除
const handleDeleteEms = async (id: number) => {
  ElMessageBox.confirm(t('common.delMessage'), t('common.confirmTitle'), {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    await EmsListApi.deleteEms(id)
    message.success('删除成功')
    handleRefresh()
  })
}

// 上传单号
const handleUpdateNumber = async (id: number) => {
  eplusDialogRef.value?.openChange(id, '上传单号')
}

// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id, '')
}

// 回填
const handleReworkRelease = (id: number | null, type?: number) => {
  if (!id || type === 2) {
    emsImportDiaRef.value?.open()
  } else {
    emsDialogRef.value?.open(id, type)
  }
}
const emsImportDiaRef = ref()
const handleImportSuccess = () => {
  message.success('导入成功')
  handleRefresh()
}

const changeDialogRef = ref('')

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const params =
      tabIndex.value === 0
        ? { ...ps, excludeStatus: [8] }
        : { sendStatus: String(tabIndex.value), ...ps }
    const res = await EmsListApi.getEmsPage({ ...params })
    return {
      list: res?.list || [],
      total: res?.total || 0,
      sum: {
        cost: currencyJsonAnalysis(res?.summary?.costSum)
      }
    }
  },
  exportListApi: async (ps) => {
    ps.sendStatus = tabIndex.value === 0 ? '' : String(tabIndex.value)
    return await EmsListApi.exportExcel(ps)
  },
  selection: true,
  delListApi: async (id: number) => Promise.resolve(id),
  showTabs: false,
  summary: true,
  columns: [
    {
      prop: 'code',
      label: '单据编号',
      minWidth: columnWidth.m,
      showOverflowTooltip: true,
      isHyperlink: true
    },
    {
      prop: 'sendStatus',
      label: '单据状态',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.SEND_STATUS, row?.sendStatus)
      }
    },
    {
      prop: 'venderShortName',
      label: '快递公司',
      minWidth: columnWidth.m
    },
    {
      prop: 'expressCode',
      label: '物流单号',
      minWidth: columnWidth.m,
      showOverflowTooltip: true
    },
    {
      prop: 'receiveName',
      label: '收件方',
      minWidth: columnWidth.m,
      showOverflowTooltip: true
    },
    {
      prop: 'receiveMsg',
      label: '收件信息',
      minWidth: columnWidth.xxl,
      showOverflowTooltip: true
    },
    {
      prop: 'inputUserName',
      minWidth: columnWidth.m,
      label: '录入人'
    },
    {
      prop: 'actualUser',
      minWidth: columnWidth.l,
      label: '实际寄件人',
      formatter: (row) => {
        return row.actualUser?.nickname
      }
    },
    // {
    //   prop: 'purchaseType3',
    //   label: '费用归属'
    // },
    {
      prop: 'sendTime',
      label: '寄件日期',
      formatter: formatDateColumn('YYYY-MM-DD'),
      minWidth: columnWidth.m
    },
    {
      prop: 'estCost',
      label: '预估金额',
      formatter: formatMoneyColumn(),
      minWidth: columnWidth.m
    },
    {
      prop: 'cost',
      label: '实际金额',
      formatter: formatMoneyColumn(),
      minWidth: columnWidth.m,
      summary: true,
      summarySlots: {
        default: (data) => {
          return {
            unit: data.currency,
            number: formatNum(data.amount, moneyTotalPrecision)
          }
        }
      }
    },
    {
      prop: 'belongFlag',
      label: '费用归属',
      minWidth: columnWidth.xxl,
      showOverflowTooltip: true,
      formatter: (row) => {
        if (!row.feeShare) return ''
        if (!Array.isArray(row.feeShare)) {
          return row.feeShare.feeShareDetail && row.feeShare.feeShareDetail !== '暂不归集'
            ? row.feeShare.feeShareDetail
            : ''
        }
        return row.feeShare
          .filter((item) => item.feeShareDetail && item.feeShareDetail !== '暂不归集')
          .map((item) => item.feeShareDetail)
          .join(' ')
      }
    },
    {
      prop: 'remark',
      label: '备注',
      showOverflowTooltip: true,
      minWidth: columnWidth.l
    },
    {
      prop: 'payStatus',
      label: '付款状态',
      multiple: true,
      minWidth: columnWidth.m,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.PAYMENT_STATUS, row?.payStatus)
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
                hasPermi="['ems:send:query']"
              >
                详情
              </el-button>

              <eplus-dropdown
                otherItems={[
                  {
                    isShow: [
                      SendStatusEnum.WAITING_SUBMIT.status,
                      SendStatusEnum.REJECTED.status
                    ].includes(row?.sendStatus),
                    otherKey: 'emsSendUpdate',
                    label: '编辑',
                    permi: 'ems:send:update',
                    handler: async (row) => {
                      await handleEdit(row.id)
                    }
                  },
                  {
                    isShow: [SendStatusEnum.WAITING_SUBMIT.status].includes(row?.sendStatus),
                    otherKey: 'emsSendDelete',
                    label: '删除',
                    permi: 'ems:send:delete',
                    handler: async (row) => {
                      await handleDeleteEms(row.id)
                    }
                  },
                  {
                    isShow: row.belongFlag == 0 && row.sendStatus !== SendStatusEnum.CLOSE.status,
                    otherKey: 'emsSendFeeShareUpdate',
                    label: '费用归集',
                    permi: 'ems:send:fee-share-update',
                    handler: async (row) => {
                      handleCreateFeeShare(row)
                    }
                  },
                  {
                    isShow: [
                      SendStatusEnum.WAITINGG_SEND.status //待寄出
                    ].includes(row?.sendStatus),
                    otherKey: 'updateNumber',
                    label: '上传快递单号',
                    permi: 'ems:send:upload-number',
                    handler: async (row: any) => {
                      handleUpdateNumber(row.id)
                    }
                  },
                  {
                    isShow: [
                      SendStatusEnum.SENT_OUT.status //已寄出
                    ].includes(row?.sendStatus),
                    otherKey: 'sendImport',
                    label: '回填费用',
                    permi: 'ems:send:import',
                    handler: async (row: any) => {
                      handleReworkRelease(row.id, 1)
                    }
                  },
                  {
                    isShow: [
                      SendStatusEnum.WAITINGG_SEND.status,
                      SendStatusEnum.SENT_OUT.status
                    ].includes(row?.sendStatus),
                    otherKey: 'sendClose',
                    label: '作废',
                    permi: 'ems:send:close',
                    handler: async (row: any) => {
                      await message.confirm('确定作废吗？')
                      await EmsListApi.closeSend(row.id)
                      message.success('作废成功')
                      handleRefresh()
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
}

const handleDownPublic = () => {
  let checkeArr = eplusTableRef.value.checkedItems
  if (!checkeArr.length) {
    message.error('请勾选寄件单')
    return false
  }
  if (new Set(checkeArr.map((e) => e.venderCode)).size > 1) {
    message.error('请选择相同快递公司')
    return false
  }
  let paymentAmount = 0
  let relationCodeList: string[] = []
  let currency = '',
    venderCode = ''
  checkeArr.forEach((e) => {
    if (e.cost?.amount) {
      paymentAmount += e.cost.amount * 1
      relationCodeList.push(e.code)
    }
    currency = e.cost?.currency
    venderCode = e.venderCode
  })
  emsParams.value = {
    relationType: 2,
    relationCode: relationCodeList,
    paymentAmount: { amount: paymentAmount, currency: currency },
    venderCode: venderCode
  }

  eplusDialogOaRef.value?.openCreate()
}
const FeeShareDialogRef = ref()
const feeShareAmount = ref<any>(null)
const itemId = ref('')
const handleCreateFeeShare = (row) => {
  itemId.value = row.id
  feeShareAmount.value = row.cost.amount > 0 ? row.cost : row.estCost
  FeeShareDialogRef.value?.openCreate('费用归集', row.id)
}
</script>
