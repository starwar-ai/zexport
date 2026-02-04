<template>
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
        @click="handleCreate()"
        v-hasPermi="['oa:general-apply:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
      <el-button
        @click="handleCreateEntertainmentExpenses('', 'general')"
        v-hasPermi="['oa:general-reimb:create']"
      >
        批量通用费用报销
      </el-button>
      <el-button
        @click="handleCreateEntertainmentExpenses('', 'other')"
        v-hasPermi="['oa:other-reimb:create']"
      >
        批量其他费用报销
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <GeneralDetail
        :title="'费用申请详情'"
        :id="key"
      />
    </template>

    <template #edit="{ key }">
      <GeneralEditForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>

    <template #create="{ key }">
      <GeneralEditForm
        mode="create"
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="generalExpensesDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <GeneralExpensesForm
        :params="expensesParams"
        mode="create"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="otherExpensesDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <OtherExpenseForm
        :params="expensesParams"
        mode="create"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import type { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as OaGeneralApplyApi from '@/api/oa/generalApply/index'
import GeneralExpensesForm from '@/views/oa/generalExpense/GeneralExpenseForm.vue'
import OtherExpenseForm from '@/views/oa/otherExpense/OtherExpenseForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import GeneralEditForm from './GeneralEditForm.vue'
import GeneralDetail from './GeneralDetail.vue'
import { useAppStore } from '@/store/modules/app'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { isValidArray } from '@/utils/is'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'ApplyGeneral' })
const handleDialogFailure = () => {}
const eplusTableRef = ref()
const expensesParams = ref()
const activeName = ref('0')
const tabIndex = ref('0')
const exportFileName = ref('费用申请管理.xlsx')
const generalExpensesDialogRef = ref()
const otherExpensesDialogRef = ref()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'applierId',
      label: '申请人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择申请人部门"></eplus-dept-select>,
      name: 'applierDeptId',
      label: '申请人部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
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
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '创建时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const appStore = useAppStore()
const router = useRouter()
const handleDetail = (id: number) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/oa/apply/general/detail/${id}`
    })
  }
}
const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}
const handleDelete = async (id: number) => {
  await eplusTableRef.value.delList(id, false)
}
const handleCreateEntertainmentExpenses = async (row, type) => {
  if (row) {
    let reimbList = []
    reimbList.push(row)
    expensesParams.value = {
      companyId: row.companyId,
      companyName: row.companyName,
      reimbList: reimbList,
      amount: row.amount,
      actualUser: row.actualUser
    }
    if (type == 'general') {
      generalExpensesDialogRef.value?.openCreate()
    } else {
      otherExpensesDialogRef.value?.openCreate()
    }
  } else {
    let list = eplusTableRef.value?.checkedItems
    if (isValidArray(list)) {
      let notApprovelist = list.filter((item) => item.auditStatus != '2')
      if (notApprovelist.length > 0) {
        ElMessage.error('存在未审核通过记录信息，请核对！')
        return
      }
      let isApplyExpenselist = list.filter((item) => item.isApplyExpense == '1')
      if (isApplyExpenselist.length > 0) {
        ElMessage.error('存在已申请过报销单记录信息，请核对！')
        return
      }
      let companyIdSet = new Set(list.map((item) => item.companyId))
      if (companyIdSet.size > 1) {
        ElMessage.error('请先选择同一费用主体数据进行操作')
        return
      }
      let currencySet = new Set(list.map((item) => item.amount.currency))
      if (currencySet.size > 1) {
        ElMessage.error('请先选择同一币种数据进行操作')
        return
      }
      let userIdSet = new Set(list.map((item) => item.actualUser.userId))
      if (userIdSet.size > 1) {
        ElMessage.error('请先选择同一费用申请人数据进行操作')
        return
      }
      expensesParams.value = {
        companyId: list[0].companyId,
        companyName: list[0].companyName,
        amount: list[0].amount,
        actualUser: list[0].actualUser,
        reimbList: list
      }
      if (type == 'general') {
        generalExpensesDialogRef.value?.openCreate()
      } else {
        otherExpensesDialogRef.value?.openCreate()
      }
    } else {
      ElMessage.error('请先选择需要报销费申请记录')
      return
    }
  }
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const params = tabIndex.value === '0' ? ps : { sendStatus: tabIndex.value, ...ps }
    const res = await OaGeneralApplyApi.getGeneralApplyPage({ ...params })

    return {
      list: res.list,
      total: res.total
    }
  },
  exportListApi: async (ps) => {
    ps.sendStatus = tabIndex.value
    return await OaGeneralApplyApi.exportGeneralApply(ps)
  },
  delListApi: async (id: number) => {
    await OaGeneralApplyApi.deleteGeneralApply(id)
  },
  selection: true,
  summary: false,
  showTabs: false,
  columns: [
    {
      prop: 'code',
      label: '单据编号',
      minWidth: columnWidth.m,
      isTooltip: true,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '费用主体',
      minWidth: columnWidth.m
    },
    {
      prop: 'applyer',
      label: '申请人',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div>
              {row.applyer?.nickname ? row.applyer.nickname : ''}
              {row.applyer?.deptName ? `(${row.applyer?.deptName})` : ''}
            </div>
          )
        }
      }
    },
    {
      prop: 'purpose',
      label: '申请事由',
      minWidth: columnWidth.m,
      isTooltip: true,
      showOverflowTooltip: true
    },
    {
      prop: 'amountDesc',
      minWidth: columnWidth.m,
      label: '明细说明',
      showOverflowTooltip: true
    },
    {
      prop: 'amount',
      label: '预估费用',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn()
    },
    {
      prop: 'isApplyExpense',
      label: '申请报销',
      formatter: formatDictColumn(DICT_TYPE.IS_INT),
      minWidth: columnWidth.m
    },
    {
      prop: 'createTime',
      label: '创建日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row?.auditStatus)
      },
      minWidth: columnWidth.m
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
                hasPermi="['oa:general-apply:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  user: row.applyer,
                  permi: 'oa:general-apply:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  user: row.applyer,
                  permi: 'oa:general-apply:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: row?.auditStatus == 2 && row.isApplyExpense != 1,
                    user: row.applyer,
                    otherKey: 'generalCreate',
                    label: '通用费用报销申请',
                    permi: 'oa:general-reimb:create',
                    handler: async (row) => {
                      handleCreateEntertainmentExpenses(row, 'general')
                    }
                  },
                  {
                    isShow: row?.auditStatus == 2 && row.isApplyExpense != 1,
                    user: row.applyer,
                    otherKey: 'otherCreate',
                    label: '其他费用报销申请',
                    permi: 'oa:other-reimb:create',
                    handler: async (row) => {
                      handleCreateEntertainmentExpenses(row, 'other')
                    }
                  },
                  {
                    isShow: row.isApplyExpense != 1 && ![1, 10].includes(row.auditStatus),
                    user: row.applyer,
                    otherKey: 'generalApplyClose',
                    label: '作废',
                    permi: 'oa:general-apply:close',
                    handler: async (row) => {
                      handleClose(row.id)
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
const message = useMessage()
const handleClose = (id) => {
  ElMessageBox.confirm('确认对选中数据进行作废操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await OaGeneralApplyApi.closeGeneralApply(id)
    message.success('操作成功!')
    handleRefresh()
  })
}
onActivated(async () => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
