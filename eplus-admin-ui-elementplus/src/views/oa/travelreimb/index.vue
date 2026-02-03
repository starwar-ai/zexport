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
        v-hasPermi="['oa:travel-reimb:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }">
      <travel-reimb-detail
        :pageInfo="pageInfo"
        :id="key"
    /></template>

    <template #edit="{ key }">
      <travel-reimb-form
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <travel-reimb-form mode="create" />
    </template>
  </eplus-dialog>

  <!-- 费用归集 -->
  <eplus-dialog
    ref="FeeShareRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #create="{ key }">
      <FeeShareFormCom
        :getApi="api.getTravelReimb"
        :updateApi="api.updateTravelReimb"
        :id="key"
        mode="create"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as api from '@/api/oa/travelreimb'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchAmountRange, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as DeptApi from '@/api/system/dept'
import TravelReimbForm from './TravelReimbForm.vue'
import TravelReimbDetail from './TravelReimbDetail.vue'
import FeeShareFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'
import FeeShareInfo from '@/views/oa/components/FeeShareInfo.vue'
import { checkPermi } from '@/utils/permission'
import * as UserApi from '@/api/system/user'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'TravelReimb' })

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '报销单号'
    },
    {
      component: (
        <eplus-custom-select
          api={api.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="公司主体"
          class="!w-150px"
        />
      ),
      name: 'companyId',
      label: '公司主体',
      formatter: async (args: any[]) => {
        let list = await api.getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择报销部门"></eplus-dept-select>,
      name: 'reimbUserDeptId',
      label: '报销部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.IS_INT}></eplus-dict-select>,
      name: 'repayFlag',
      label: '是否还款',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.IS_INT, args[0])
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
      component: <eplus-user-select></eplus-user-select>,
      name: 'reimbUserId',
      label: '报销人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'totalAmount', label: '报销金额', formatter: '从{0}到{1}' }]
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.CURRENCY_TYPE}></eplus-dict-select>,
      name: 'currency',
      label: '币种',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.CURRENCY_TYPE, args[0])
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
  selection: false,
  summary: false,
  showTabs: false,
  getListApi: async (ps) => {
    const { totalAmount, ...params } = ps || {}
    const res = await api.getTravelReimbPage({ ...params, reimbType: 1 })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await api.deleteTravelReimb(id)
  },
  exportListApi: async (ps) => {
    const { totalAmount, ...params } = ps || {}
    return await api.exportTravelReimb({ ...params, reimbType: 1 })
  },
  columns: [
    {
      prop: 'code',
      label: '报销单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '费用主体',
      minWidth: columnWidth.m,
      showOverflowTooltip: true
    },
    {
      prop: 'reimbUserDeptName',
      label: '报销人',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return `${row.reimbUser.nickname}(${row.reimbUser.deptName})`
        }
      }
    },
    {
      prop: 'expenseUseToOccur',
      label: '申请事由',
      minWidth: columnWidth.l
    },

    {
      prop: 'totalAmountList',
      label: '本次报销金额',
      minWidth: columnWidth.l,
      formatter: formatMoneyColumn()
    },
    {
      prop: 'repayFlag',
      label: '是否还款',
      minWidth: columnWidth.m,
      formatter: (row) => {
        const val = getDictLabel(DICT_TYPE.IS_INT, row.repayFlag)
        return val == '是' ? '是' : ''
      }
    },

    {
      prop: 'feeShareDetail',
      label: '费用归集',
      minWidth: columnWidth.xxl,
      slots: {
        default: (data) => {
          const { row } = data
          return <FeeShareInfo info={row.feeShare} />
        }
      }
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
      prop: 'paymentStatus',
      label: '支付状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.PAYMENT_STATUS)
    },
    {
      prop: 'paymentDate',
      label: '支付日期',
      minWidth: columnWidth.m,
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
                hasPermi="['oa:travel-reimb:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  user: row.reimbUser,
                  permi: 'oa:travel-reimb:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: [0, 3, 4].includes(row.auditStatus),
                    user: row.reimbUser,
                    otherKey: 'travelReimbDelete',
                    label: '删除',
                    permi: 'oa:travel-reimb:delete',
                    handler: async (row) => {
                      await handleDelete(row.id)
                    }
                  },
                  {
                    isShow: [2].includes(row.auditStatus),
                    otherKey: 'travelReimbClose',
                    label: '作废',
                    permi: 'oa:travel-reimb:close',
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

const exportFileName = computed(() => {
  return checkPermi(['oa:travel-reimb:export']) ? '差旅费管理.xlsx' : ''
})

const handleSubmit = async (data) => {
  // 提交请求
  let res = await api.submitTravelReimb(data.id)
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
  eplusDialogRef.value?.openEdit(id)
}
const pageInfo = ref({})
/// 打开详情
const handleDetail = async (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}
const FeeShareRef = ref()
const handleUpdateFeeShare = async (id: number) => {
  FeeShareRef.value?.openCreate('费用归集', id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const handleClose = async (id) => {
  const { value } = await ElMessageBox.prompt('请输入作废原因', '作废', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/, // 判断非空，且非空格
    inputErrorMessage: '作废原因不能为空'
  })
  const data = {
    id: id,
    cancelReason: value
  }
  await api.closeTravel(data)
  message.success('作废成功')
  handleRefresh()
}
</script>
