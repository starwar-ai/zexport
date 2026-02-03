<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    key="transferOrder"
    @emit-open-detail="handleDetail"
    :exportFileName="exportFileName"
  >
    <template #tableActions>
      <div class="w-100% flex justify-between">
        <!-- 页面按钮 -->
        <div class="flex-1 overflow-x-auto">
          <!-- 左侧，使用flex-1自动填充剩余空间 -->
          <el-button
            type="primary"
            plain
            @click="handleCreate()"
            v-hasPermi="['crm:cust:create']"
          >
            <Icon
              icon="ep:plus"
              class="mr-5px"
            />
            新增调拨单
          </el-button>
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
      <transferOrderForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key }">
      <transferOrderForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <!-- <template #detail="{ key }">
      <PlanDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template> -->
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker, EplusSearchMultiInput } from '@/components/EplusSearch'
import { EplusDialog } from '@/components/EplusDialog'
import { formatDateColumn, formatDictColumn } from '@/utils/table'
import * as TransferOrderApi from '@/api/wms/transferOrder'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import transferOrderForm from './transferOrderForm.vue'

const message = useMessage()
const eplusTableRef = ref<any>(null)
const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | null>(null)
//EplusTable组件的搜索表单配置
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    // {
    //   component: <eplus-user-select></eplus-user-select>,
    //   name: 'purchaseUserId',
    //   label: '采购员',
    //   formatter: async (args: any[]) => {
    //     var user = await UserApi.getSimpleUser(args[0])
    //     return user.nickname
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
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'planDate',
          label: '创建日期',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <EplusSearchMultiInput />,
      subfields: [
        {
          name: 'code',
          label: '采购计划编号'
        }
      ]
    }
  ],
  moreFields: []
}
//EplusTable组件的表格配置
let eplusTableSchema: any = {
  getListApi: async (ps) => {
    const res = await TransferOrderApi.getTransferOrderPage(ps)
    return {
      list: res.list,
      total: res.total,
      sum: { quantity: res?.summary?.qtySum }
    }
  },
  delListApi: async (id) => {
    await TransferOrderApi.deleteTransferOrder(id)
  },
  exportListApi: async (ps) => {
    return await TransferOrderApi.exportTransferOrder(ps)
  },
  selection: true,
  summary: true,
  columns: [
    {
      prop: 'code',
      label: '调拨单号',
      minWidth: '150px'
    },
    {
      prop: 'companyName',
      label: '库存主体',
      minWidth: '150px'
    },
    {
      prop: 'transferType',
      label: '调拨类型',
      minWidth: '120px'
    },
    {
      prop: 'saleContractCode',
      label: '拨入订单号',
      minWidth: '150px'
    },
    {
      prop: 'custCode',
      label: '客户编号',
      minWidth: '150px'
    },
    {
      prop: 'custName',
      label: '客户名称',
      minWidth: '150px'
    },
    {
      prop: 'inputUser',
      label: '录入人',
      minWidth: '150px'
    },
    {
      prop: 'code',
      label: '部门',
      minWidth: '150px'
    },
    {
      prop: 'createTime',
      label: '添加日期',
      minWidth: '150px',
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: '120px',
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: '120px',
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
                otherItems={[
                  {
                    isShow: true,
                    otherKey: 'billCancel',
                    label: '作废',
                    permi: 'wms:transferOrder:cancel',
                    checkAuditStatus: [
                      BpmProcessInstanceResultEnum.APPROVE.status,
                      BpmProcessInstanceResultEnum.UNSUBMITTED.status
                    ],
                    handler: async (row) => {
                      handleCancel(row.id)
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
const handleCancel = (id: number) => {
  ElMessageBox.confirm('确认作废该入库单吗？', '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确认',
    type: 'warning'
  })
    .then(async () => {
      // await BillApi.cancelBill(id)
      message.success('操作成功')
      // eplusListRef.value.handleRefresh()
    })
    .catch(() => {})
}
//新增
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('客户')
}
//刷新表格
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
//弹窗操作失败
const handleDialogFailure = () => {}
// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
// 提交请求
const handleSubmit = async (data) => {
  let res = await TransferOrderApi.submitTransferOrder(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusTableRef.value.refresh()
}
</script>
<style lang="scss" scoped></style>
