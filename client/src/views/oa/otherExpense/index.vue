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
        v-hasPermi="['oa:other-reimb:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
      <el-button
        type="primary"
        plain
        @click="handleExport()"
        v-hasPermi="['oa:other-reimb:export']"
      >
        <Icon
          icon="ep:download"
          class="mr-5px"
        />
        导出
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }">
      <OtherExpenseDetail
        :pageInfo="pageInfo"
        :id="key"
    /></template>

    <template #edit="{ key }">
      <OtherExpenseForm
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <OtherExpenseForm mode="create" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as OtherApi from '@/api/oa/otherExpense'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchAmountRange, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as DeptApi from '@/api/system/dept'
import OtherExpenseForm from './OtherExpenseForm.vue'
import OtherExpenseDetail from './OtherExpenseDetail.vue'
import FeeShareInfo from '@/views/oa/components/FeeShareInfo.vue'
import { getSimpleUser } from '@/api/system/user'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'OtherExpense' })

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
          api={OtherApi.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="费用主体"
          class="!w-150px"
        />
      ),
      name: 'companyId',
      label: '费用主体',
      formatter: async (args: any[]) => {
        let list = await OtherApi.getcompanySimpleList()
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
      component: <eplus-user-select></eplus-user-select>,
      name: 'reimbUserId',
      label: '报销人',
      formatter: async (args: any[]) => {
        const user = await getSimpleUser(args[0])
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
    // {
    //   component: <eplus-dept-select placeholder="请选择报销部门"></eplus-dept-select>,
    //   name: 'reimbUserDeptId',
    //   label: '报销部门',
    //   formatter: async (args: any[]) => {
    //     var dept = await DeptApi.getSimpleDept(args[0])
    //     return dept.name
    //   }
    // },
    // {
    //   component: <eplus-dict-select dictType={DICT_TYPE.IS_INT}></eplus-dict-select>,
    //   name: 'repayFlag',
    //   label: '是否还款',
    //   formatter: (args: any[]) => {
    //     return getDictLabel(DICT_TYPE.IS_INT, args[0])
    //   }
    // },
    // {
    //   component: (
    //     <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}></eplus-dict-select>
    //   ),
    //   name: 'auditStatus',
    //   label: '审核状态',
    //   formatter: (args: any[]) => {
    //     return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
    //   }
    // },
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await OtherApi.getOtherReimbPage({ reimbType: 4, ...ps })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await OtherApi.deleteOtherReimb(id)
  },
  exportListApi: async (ps) => {
    return await OtherApi.exportOtherReimb(ps)
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
      minWidth: columnWidth.m
    },
    {
      prop: 'reimbUserName',
      label: '报销人',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return row.reimbUser?.nickname + '(' + row.reimbUser?.deptName + ')'
        }
      }
    },
    {
      prop: 'totalAmountList',
      label: '本次报销金额',
      minWidth: columnWidth.m,
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
                hasPermi="['oa:other-reimb:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  user: row.reimbUser,
                  permi: 'oa:other-reimb:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: [0, 3, 4].includes(row.auditStatus),
                    user: row.reimbUser,
                    otherKey: 'otherReimbDelete',
                    label: '删除',
                    permi: 'oa:other-reimb:delete',
                    handler: async (row) => {
                      await handleDelete(row.id)
                    }
                  },
                  {
                    isShow: [2].includes(row.auditStatus),
                    otherKey: 'otherReimbClose',
                    label: '作废',
                    permi: 'oa:other-reimb:close',
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
  await OtherApi.closeOther(data)
  message.success('作废成功')
  handleRefresh()
}

const exportFileName = ref('一般费用管理.xlsx')
const handleExport = async () => {
  return await eplusListRef.value.exportList('一般费用管理.xlsx')
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

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
