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
    v-bind="{ eplusSearchSchema, eplusTableSchema }"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  />
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <ReceiptDetail
        :id="key"
        @success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <ReceiptDialog
    ref="ReceiptDialogRef"
    @success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import ReceiptDetail from './receiptDetail.vue'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as ReceiptApi from '@/api/finance/receipt'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import ReceiptDialog from './components/ReceiptDialog.vue'
import { BpmProcessInstanceResultEnum, PaymentStatusEnum } from '@/utils/constants'

const name = 'Receipt'
defineOptions({
  name
})

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const exportFileName = ref('收款单.xlsx')

const eplusTableRef = ref()
const activeName = ref('all')
const tabIndex = ref('0')
const tabNameList = [
  { name: '3', label: '收款登记' },
  { name: '4', label: '个人还款' },
  { name: 'all', label: '全部' }
]
const handleTabsClick = (val) => {
  tabIndex.value = val.index
}

const ReceiptDialogRef = ref()
const openReceiptDialog = (code, id) => {
  ReceiptDialogRef.value.open(code, id)
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'companyName',
      label: '收款主体'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'nameShort',
      label: '简称'
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
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '申请时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const params =
      activeName.value === 'all' ? ps : { businessSubjectType: activeName.value, ...ps }
    const res = await ReceiptApi.getReceiptPage(params)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await ReceiptApi.deleteReceipt(id)
  },
  exportListApi: async (ps) => {
    return await ReceiptApi.exportReceipt(ps)
  },
  columns: [
    {
      prop: 'code',
      label: '单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '收款主体',
      minWidth: columnWidth.m
    },
    {
      prop: 'amount',
      label: '收款金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn()
    },
    {
      prop: 'businessSubjectType',
      label: '收款对象类型',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BUSINESS_SUBJECT_TYPE)
    },
    {
      prop: 'receiptUser',
      label: '收款人（部门）',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return (
          <span>
            {row.receiptUser?.nickname
              ? row.receiptUser?.nickname + '(' + (row.receiptUser?.deptName || '-') + ')'
              : ''}
          </span>
        )
      }
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'status',
      label: '确认状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.RECEIPT_STATUS)
    },
    {
      prop: 'receiptTime',
      label: '收款时间',
      formatter: formatDateColumn(),
      minWidth: columnWidth.l
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
                hasPermi="['fms:receipt:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  permi: 'fms:receipt:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                auditable={row}
                otherItems={[
                  {
                    isShow: row?.status === PaymentStatusEnum.WAITING.status,
                    otherKey: 'receipt',
                    label: '收款确认',
                    permi: 'fms:receipt:confirm',
                    checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    handler: async (row) => {
                      await openReceiptDialog(row.businessCode, row?.id)
                    }
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

const handleSubmit = async (data) => {
  // 提交请求
  let res = await ReceiptApi.submitReceipt(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusTableRef.value.refresh()
}

/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}

onMounted(() => {})

// onActivated(() => {
//   eplusDialogRef.value?.close()
// })
</script>
