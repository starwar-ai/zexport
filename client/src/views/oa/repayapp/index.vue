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
        v-hasPermi="['oa:repay-app:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增还款单
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }"> <RepayAppDetail :id="key" /></template>

    <template #edit="{ key }">
      <RepayAppForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #create>
      <RepayAppForm mode="create" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import RepayAppDetail from './RepayAppDetail.vue'
import RepayAppForm from './RepayAppForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as RepayAppApi from '@/api/oa/repayapp'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({
  name: 'RepayApp'
})

const handleDialogSuccess = () => {}

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '还款单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'loanAppCode',
      label: '借款单号'
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
          label: '创建日期',
          formatter: '从{0}到{1}'
        },
        {
          name: 'repayTime',
          label: '还款日期',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await RepayAppApi.getRepayAppPage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await RepayAppApi.deleteRepayApp(id)
  },
  exportListApi: async (ps) => {
    return await RepayAppApi.exportRepayApp(ps)
  },
  columns: [
    {
      prop: 'code',
      label: '还款单号',
      minWidth: columnWidth.m,
      datetype: 'input'
    },
    {
      prop: 'loanAppCode',
      label: '借款单号',
      minWidth: columnWidth.m,
      datetype: 'input'
    },
    {
      prop: 'repayerId',
      label: '还款人',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return `${row.repayer?.nickname}`
        }
      },
      datetype: 'user'
    },
    {
      prop: 'createTime',
      label: '创建时间',
      formatter: formatDateColumn(),
      width: columnWidth.m,
      datetype: 'time'
    },
    {
      prop: 'amount',
      label: '还款金额',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn()
    },
    {
      prop: 'repayTime',
      label: '还款时间',
      formatter: formatDateColumn(),
      minWidth: columnWidth.m,
      datetype: 'time'
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
      datetype: 'BPM_PROCESS_INSTANCE_RESULT'
    },
    {
      prop: 'repayStatus',
      label: '还款状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.REPAY_STATUS),
      datetype: 'REPAY_STATUS'
    },

    {
      prop: 'action',
      label: '操作',
      width: columnWidth.l,
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
                hasPermi="['oa:repay-app:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  user: row.repayer,
                  permi: 'oa:repay-app:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                editItem={{
                  user: row.repayer,
                  permi: 'oa:repay-app:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  user: row.repayer,
                  permi: 'oa:repay-app:delete',
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

const exportFileName = ref('还款单列表.xlsx')

const handleSubmit = async (data) => {
  // 提交请求
  let res = await RepayAppApi.submitRepayApp({ repayAppId: data.id })
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '还款单')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('还款单')
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

onMounted(() => {})
</script>
