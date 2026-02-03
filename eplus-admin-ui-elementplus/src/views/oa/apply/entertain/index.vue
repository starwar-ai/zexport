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
        v-hasPermi="['oa:entertain-apply:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
      <el-button
        @click="handleCreateEntertainmentExpenses('')"
        v-hasPermi="['oa:entertain-reimb:create']"
      >
        批量申请报销
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <entertain-detail :id="key" /></template>
    <template #edit="{ key }">
      <entertain-form
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <entertain-form mode="create" />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="entertainmentExpensesDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <EntertainmentExpensesForm
        :params="entertainmentExpensesParams"
        mode="create"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as api from '@/api/oa/entertainApply'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import EntertainForm from './entertainForm.vue'
import EntertainDetail from './EntertainDetail.vue'
import { checkPermi } from '@/utils/permission'
import { useAppStore } from '@/store/modules/app'
import EntertainmentExpensesForm from '@/views/oa/entertainmentExpenses/EntertainmentExpensesForm.vue'
import { isValidArray } from '@/utils/is'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import { getSourceId, removeSourceId } from '@/utils/auth'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'ApplyEntertain' })

const handleDialogFailure = () => {}

const exportFileName = computed(() => {
  return checkPermi(['oa:entertain-apply:export']) ? '招待费申请单.xlsx' : ''
})

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: (
        <eplus-custom-select
          api={TravelReimbApi.getcompanySimpleList}
          label="name"
          value="id"
        />
      ),
      name: 'companyId',
      label: '费用主体',
      formatter: async (args: any[]) => {
        let list = await TravelReimbApi.getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: <el-input></el-input>,
      name: 'entertainName',
      label: '招待对象名称'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.LEVEL_TYPE}></eplus-dict-select>,
      name: 'entertainLevel',
      label: '招待对象等级',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.LEVEL_TYPE, args[0])
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
    const res = await api.getEntertainApplyPage({ ...ps })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await api.deleteEntertainApply(id)
  },
  exportListApi: async (ps) => {
    //return await api.exportEntertainApplyExcel({ ...ps})
  },
  selection: true,
  columns: [
    {
      prop: 'code',
      label: '申请单号',
      minWidth: columnWidth.m,
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
        default: ({ row }) => {
          return `${row?.applyer?.nickname ? row?.applyer?.nickname : ''}(${row?.applyer?.deptName})`
        }
      }
    },
    {
      prop: 'purpose',
      label: '招待事由',
      minWidth: columnWidth.m,
      showOverflowTooltip: true
    },
    {
      prop: 'entertainName',
      label: '招待对象名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'entertainLevel',
      label: '招待对象等级',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.LEVEL_TYPE),
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
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.IS_INT)
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
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
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
                hasPermi="['oa:entertain-apply:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  user: row.applyer,
                  permi: 'oa:entertain-apply:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  user: row.applyer,
                  permi: 'oa:entertain-apply:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: row?.auditStatus == 2 && row.isApplyExpense != 1,
                    user: row.applyer,
                    otherKey: 'entertainReimbCreate',
                    label: '费用报销申请',
                    permi: 'oa:entertain-reimb:create',
                    handler: async (row) => {
                      handleCreateEntertainmentExpenses(row)
                    }
                  },
                  {
                    isShow: row.isApplyExpense != 1 && ![1, 10].includes(row.auditStatus),
                    user: row.applyer,
                    otherKey: 'entertainApplyClose',
                    label: '作废',
                    permi: 'oa:entertain-apply:close',
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

const handleClose = (id) => {
  ElMessageBox.confirm('确认对选中数据进行作废操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await api.closeEntertainApply(id)
    message.success('操作成功!')
    handleRefresh()
  })
}

const handleExport = async () => {
  return await eplusListRef.value.exportList('招待费报销单.xlsx')
}

// const handleSubmit = async (data) => {
//   // 提交请求
//   let res = await api.submitEntertain(data.id)
//   if (res) {
//     message.success('已提交审核！')
//   }
//   await eplusListRef.value.handleRefresh()
// }

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}
const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}
const pageInfo = ref({})
/// 打开详情
const appStore = useAppStore()
const router = useRouter()
const handleDetail = async (id: number) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/oa/apply/entertain/detail/${id}`
    })
  }
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const entertainmentExpensesDialogRef = ref()
const entertainmentExpensesParams = ref()
const handleCreateEntertainmentExpenses = async (row) => {
  if (row) {
    let reimbList = []
    reimbList.push(row)
    entertainmentExpensesParams.value = {
      companyId: row.companyId,
      companyName: row.companyName,
      reimbList: reimbList
    }
    entertainmentExpensesDialogRef.value?.openCreate()
  } else {
    let list = eplusListRef.value?.checkedItems
    if (isValidArray(list)) {
      let notApprovelist = eplusListRef.value?.checkedItems.filter(
        (item) => item.auditStatus != '2'
      )
      if (notApprovelist.length > 0) {
        message.error('存在未审核通过记录信息，请核对！')
        return
      }
      let isApplyExpenselist = eplusListRef.value?.checkedItems.filter(
        (item) => item.isApplyExpense == '1'
      )
      if (isApplyExpenselist.length > 0) {
        message.error('存在已申请过报销单记录信息，请核对！')
        return
      }
      let companyIdSet = new Set(list.map((item) => item.companyId))
      if (companyIdSet.size > 1) {
        ElMessage.error('请先选择同一费用主体数据进行操作')
        return
      } else {
        entertainmentExpensesParams.value = {
          companyId: list[0].companyId,
          companyName: list[0].companyName,
          reimbList: list
        }
        entertainmentExpensesDialogRef.value?.openCreate()
      }
    } else {
      ElMessage.error('请先选择需要报销费申请记录')
      return
    }
  }
}
onActivated(async () => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
