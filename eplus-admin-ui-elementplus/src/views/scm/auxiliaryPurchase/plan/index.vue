<template>
  <div class="pl-15px pr-15px h-45px bg-[var(--left-menu-bg-color)]">
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
    @refresh="handleRefresh"
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
  <!-- 弹出框 -->
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <PlanForm
        mode="create"
        @handleSuccess="handleRefresh"
      />
    </template>
    <template #edit="{ key }">
      <PlanForm
        :id="key"
        mode="edit"
        @handleSuccess="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <PlanDetail
        :id="key"
        @handleSuccess="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="toContractDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #edit="{ key, param }">
      <ToContractForm
        :param="param"
        :id="key"
        @handleSuccess="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as PurchasePlanApi from '@/api/scm/auxiliaryPurchasePlan'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { propTypes } from '@/utils/propTypes'
import PlanForm from './PlanForm.vue'
import PlanDetail from './PlanDetail.vue'
import ToContractForm from './ToContractForm.vue'
import { columnWidth, formatDateColumn } from '@/utils/table'
import { BpmProcessInstanceResultEnum, PurchasePlanStatusEnum } from '@/utils/constants'
import * as UserApi from '@/api/system/user'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'AuxiliaryPurchasePlan' })
const tabNameList = [
  { name: 'first', label: '全部' },
  { name: 'second', label: '未提交' },
  { name: 'third', label: '待审批' },
  { name: 'forth', label: '待采购' },
  { name: 'fifth', label: '已驳回' },
  { name: 'sixth', label: '已完成' },
  { name: 'seventh', label: '已作废' }
]
const { push } = useRouter()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const toContractDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const eplusTableRef = ref()
const message = useMessage()
const tabIndex = ref('0')
const activeName = ref('first')
const props = defineProps({
  type: propTypes.string.def('')
})
const handleDialogFailure = () => {}
// const handleReset = () => {}
// const handleSearch = (model) => {
//   // console.log(model, 'model')
// }
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('包材采购计划')
}
const exportFileName = ref('包材采购计划管理.xlsx')
const handleExport = async () => {
  return await eplusTableRef.value.exportList('包材采购计划管理.xlsx')
}
const handleSubmit = async (data) => {
  // 提交请求
  try {
    await PurchasePlanApi.submitPurchasePlan(data.id)
    message.success('提交成功')
  } catch (error) {
    message.error('提交失败')
  }
  handleRefresh()
}
const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '包材采购计划')
}
// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
  handleRefresh()
}
// 删除按钮操作
const handleDelete = async (id: number) => {
  try {
    await eplusTableRef.value.delList(id, false)
  } catch (error) {
    message.error('删除失败')
  }
}
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
const finishPlanBtn = async () => {
  const checkedItems = eplusTableRef.value?.checkedItems || []
  if (!checkedItems.length) {
    message.warning('请选择要作废的包材采购计划')
    return
  }
  const ids = checkedItems.map((item) => item.id || 0)
  try {
    await PurchasePlanApi.finishPurchasePlan(ids)
    message.success('批量作废成功')
    handleRefresh()
  } catch {
    message.error('批量作废失败')
  }
}
const handleListTocontract = async () => {
  const checkedItems = eplusTableRef.value?.checkedItems || []
  if (!checkedItems.length) {
    message.warning('请选择批量操作的包材采购计划明细')
    return
  }
  const ids = checkedItems.map((item) => item.id || 0).join(',')
  try {
    toContractDialogRef.value?.openEdit(ids, '转采购单')
    // await PurchasePlanApi.toContract(ids)
  } catch {
    message.error('生成失败')
  }
}
//全部状态下的按钮
const allBtnSchemas = [
  {
    component: (
      <el-button
        type="primary"
        onClick={handleCreate}
        v-hasPermi="['scm:auxiliary-purchase-plan:create']"
      >
        新增包材采购计划
      </el-button>
    )
  }
]
let btnSchemas: any = reactive(allBtnSchemas)
//待采购状态下的按钮
const purchaseBtnSchemas = [
  {
    component: (
      <el-button
        type="primary"
        plain
        onClick={handleListTocontract}
        v-hasPermi="['scm:auxiliary-purchase-plan:contract']"
      >
        批量转采购合同
      </el-button>
    )
  },
  {
    component: (
      <el-button
        plain
        onClick={finishPlanBtn}
        v-hasPermi="['scm:auxiliary-purchase-plan:create']"
      >
        作废
      </el-button>
    )
  }
]
//其他tab默认展示采购看板
const defaultBtnSchemas = [
  {
    component: (
      <el-button
        type="success"
        plain
        onClick={handleExport}
        v-hasPermi="['scm:auxiliary-purchase-plan:export']"
      >
        导出
      </el-button>
    )
  }
]
const btnSchemasFormat = (data) => {
  switch (data) {
    case '0':
      btnSchemas = allBtnSchemas
      break
    case '3':
      btnSchemas = purchaseBtnSchemas
      break
    default:
      btnSchemas = defaultBtnSchemas
      break
  }
}
const handleTabsClick = (val) => {
  tabIndex.value = val.index
  btnSchemasFormat(val.index)
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '包材采购计划编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'auxiliarySaleContractCode',
      label: '销售合同'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'auxiliaryPurchaseContractCode',
      label: '采购合同'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaseUserId',
      label: '采购员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
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
let eplusTableSchema = {
  getListApi: async (ps) => {
    const params = tabIndex.value === '0' ? ps : { planStatus: tabIndex.value, ...ps }
    const res = await PurchasePlanApi.getPurchasePlanPage(params)
    return {
      list: res.list,
      total: res.total,
      sum: { needPurQuantity: res?.summary?.qtySum }
    }
  },
  delListApi: async (id) => {
    await PurchasePlanApi.deletePurchasePlan(id)
  },
  exportListApi: async (ps) => {
    return await PurchasePlanApi.exportPurchasePlan(ps)
  },
  selection: true,
  summary: true,
  columns: [
    {
      prop: 'code',
      label: '包材采购计划编号',
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
      prop: 'creatorName',
      label: '创建人',
      minWidth: columnWidth.m,
      parent: true
    },
    {
      prop: 'creatorDeptName',
      label: '创建人部门',
      minWidth: columnWidth.m,
      parent: true
    },
    {
      prop: 'createTime',
      label: '创建时间',
      minWidth: columnWidth.l,
      parent: true,
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    },
    {
      prop: 'planDate',
      label: '预计日期',
      minWidth: columnWidth.m,
      parent: true,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    // {
    //   prop: 'itemCountTotal',
    //   label: '计划采购数量',
    //   minWidth: columnWidth.m,
    //   parent: true
    // },
    // {
    //   prop: 'remark',
    //   label: '备注',
    //   minWidth: columnWidth.l,
    //   parent: true
    // },
    {
      prop: 'planStatus',
      label: '单据状态',
      minWidth: columnWidth.m,
      parent: true,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.PURCHASE_PLAN_STATUS, row?.planStatus)
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
      isCopy: true,
      copyFormatter: (data) => {
        return data.children && data.children.length > 0 ? data.children[0] : ''
      },
      minWidth: columnWidth.l
    },
    {
      prop: 'skuName',
      label: '包材名称',
      minWidth: columnWidth.l
    },
    {
      prop: 'auxiliaryPurchaseContractCode',
      label: '采购合同编号',
      minWidth: columnWidth.l
    },
    {
      prop: 'auxiliarySaleContractCode',
      label: '销售合同编号',
      minWidth: columnWidth.l
    },
    {
      prop: 'productPicUrl',
      label: '图片',
      isCopy: true,
      minWidth: columnWidth.m,
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
      prop: 'purchaseType',
      label: '采购类型',
      minWidth: columnWidth.m,
      formatter: (val) => {
        return getDictLabel(DICT_TYPE.PURCHASE_TYPE, val.purchaseType)
      }
    },
    {
      prop: 'purchaseUserName',
      label: '采购员',
      minWidth: columnWidth.m
    },
    {
      prop: 'cskuCode',
      label: '关联产品数',
      minWidth: columnWidth.m
    },
    {
      prop: 'cskuCode',
      label: '客户货号',
      minWidth: columnWidth.m
    },
    {
      prop: 'needPurQuantity',
      label: '计划采购数量',
      isCopy: true,
      minWidth: columnWidth.l,
      summary: true
    },
    {
      prop: 'remark',
      label: '规格描述',
      minWidth: columnWidth.l
    },
    {
      prop: 'convertedFlag',
      label: '转合同状态',
      minWidth: columnWidth.m,
      formatter: (val) => {
        return getDictLabel(DICT_TYPE.CONVERTED_FLAG, val.convertedFlag)
      }
    },
    // {
    //   prop: 'remark',
    //   label: '备注',
    //   isCopy: true,
    //   // fixed: 'left',
    //   minWidth: '150px'
    // },
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
                submitItem={{
                  permi: 'scm:auxiliary-purchase-plan:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                editItem={{
                  user: { userId: row?.creator },
                  permi: 'scm:auxiliary-purchase-plan:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  permi: 'scm:auxiliary-purchase-plan:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: [PurchasePlanStatusEnum.UNPURCHASE.status].includes(row?.planStatus),
                    otherKey: 'parentToContract',
                    label: '生成包材采购合同',
                    checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    permi: 'scm:auxiliary-purchase-plan:contract',
                    handler: async (row) => {
                      if (row && row?.children && row?.children.length) {
                        const ids = row?.children
                          .map((item) => {
                            return item.id
                          })
                          .join(',')
                        try {
                          toContractDialogRef.value?.openEdit(ids, '转包材采购合同', {
                            id: row?.id,
                            code: row?.code,
                            planDate: row?.planDate
                          })
                        } catch {
                          message.error('生成失败')
                        }
                      }
                    }
                  },
                  {
                    isShow: [
                      PurchasePlanStatusEnum.UNSUBMIT.status,
                      PurchasePlanStatusEnum.UNPURCHASE.status,
                      PurchasePlanStatusEnum.REJECTED.status,
                      PurchasePlanStatusEnum.COMPLETED.status
                    ].includes(row?.planStatus),
                    otherKey: 'finishPlan',
                    label: '作废',
                    permi: 'scm:auxiliary-purchase-plan:finish',
                    handler: async (row) => {
                      PurchasePlanApi.finishPurchasePlan([row.id]).then(() => {
                        message.success('作废成功')
                        handleRefresh()
                      })
                    }
                  }
                ]}
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
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
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: !row.convertedFlag,
                    otherKey: 'toContract',
                    label: '生成包材采购合同',
                    checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    permi: 'scm:auxiliary-purchase-plan:contract',
                    handler: async (row) => {
                      {
                        /* PurchasePlanApi.toContract(row?.id)
                        .then(() => {
                          message.success('生成成功')
                          handleRefresh()
                        })
                        .catch(() => {
                          message.error('生成失败')
                        }) */
                      }
                      toContractDialogRef.value?.openEdit(row?.id, '转包材采购合同')
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
// if (props.type !== '0') {
//   eplusTableSchema.columns.forEach((item, index) => {
//     if (item.prop === 'planStatus') {
//       return eplusTableSchema.columns.splice(index, 1)
//     }
//   })
// }
onMounted(() => {
  // console.log(eplusTableRef.value, 'eplusTableRef')
})

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
  right: -12px;
  top: 0px;
  color: #7cc440;
}
</style>
