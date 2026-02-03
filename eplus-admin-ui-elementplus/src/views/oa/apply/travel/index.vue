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
        v-hasPermi="['oa:travel-apply:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
      <el-button
        @click="handleCreateTravelReimbExpenses('')"
        v-hasPermi="['oa:travel-reimb:create']"
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
    <template #detail="{ key }"> <travel-detail :id="key" /></template>
    <template #edit="{ key }">
      <travel-form
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <travel-form mode="create" />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="travelReimDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <TravelReimbForm
        :params="travelReimParams"
        mode="create"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as api from '@/api/oa/travelApply'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import TravelForm from './TravelForm.vue'
import TravelDetail from './TravelDetail.vue'
import { checkPermi } from '@/utils/permission'
import { useAppStore } from '@/store/modules/app'
import TravelReimbForm from '@/views/oa/travelreimb/TravelReimbForm.vue'
import { isValidArray } from '@/utils/is'
import * as UserApi from '@/api/system/user'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { getSourceId, removeSourceId } from '@/utils/auth'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'ApplyTravel' })

const handleDialogFailure = () => {}

const exportFileName = computed(() => {
  return checkPermi(['oa:travel-apply:export']) ? '招待费申请单.xlsx' : ''
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
      component: <eplus-user-select></eplus-user-select>,
      name: 'applierId',
      label: '申请人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'applierDeptName',
      label: '申请人部门'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purpose',
      label: '出差事由'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'startTime',
          label: '开始时间',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'endTime',
          label: '结束时间',
          formatter: '从{0}到{1}'
        }
      ]
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
    const res = await api.getTravelApplyPage({ ...ps })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await api.deleteTravelApply(id)
  },
  exportListApi: async (ps) => {
    //return await api.exportEntertainApplyExcel({ ...ps})
  },
  selection: true,
  summary: false,
  showTabs: false,
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
      label: '出差事由',
      minWidth: columnWidth.m,
      showOverflowTooltip: true
    },
    {
      prop: 'travelType',
      label: '出差类型',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.TRAVEL_TYPE)
    },
    {
      prop: 'dest',
      label: '出差地点',
      minWidth: columnWidth.m
    },
    {
      prop: 'transportationType',
      label: '交通工具',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.TRAVAL_TRANSPORTATION_TYPE)
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
                hasPermi="['oa:travel-apply:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  user: row.applyer,
                  permi: 'oa:travel-apply:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  user: row.applyer,
                  permi: 'oa:travel-apply:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: checkApplyExpense(row),
                    user: row.applyer,
                    otherKey: 'travelReimbCreate',
                    label: '费用报销申请',
                    permi: 'oa:travel-reimb:create',
                    handler: async (row) => {
                      handleCreateTravelReimbExpenses(row)
                    }
                  },
                  {
                    isShow: row.isApplyExpense != 1 && ![1, 10].includes(row.auditStatus),
                    user: row.applyer,
                    otherKey: 'travelApplyClose',
                    label: '作废',
                    permi: 'oa:travel-apply:close',
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

const checkApplyExpense = (row) => {
  if (row.travelType === 3) {
    return row?.auditStatus == 2
  } else {
    return row?.auditStatus == 2 && row.isApplyExpense != 1
  }
}
const handleClose = (row) => {
  ElMessageBox.confirm('确认对选中数据进行作废操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await api.closeTravelApply(row)
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
      path: `/oa/apply/travel/detail/${id}`
    })
  }
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const travelReimDialogRef = ref()
const travelReimParams = ref<any>()
const handleCreateTravelReimbExpenses = async (row: any) => {
  if (row) {
    let reimbList: any[] = []
    reimbList.push(row)
    travelReimParams.value = {
      companyId: row.companyId,
      companyName: row.companyName,
      reimbList: reimbList
    }
    travelReimDialogRef.value?.openCreate()
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
        travelReimParams.value = {
          companyId: list[0].companyId,
          companyName: list[0].companyName,
          reimbList: list
        }
        travelReimDialogRef.value?.openCreate()
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
