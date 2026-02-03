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
    @refresh="handleRefresh"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <div class="w-100% flex justify-between">
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
    <!-- <template #tableActionsFixRight>
      <el-button
        type="success"
        plain
        @click="jumpToPurchaseBoard"
        v-hasPermi="['scm:purchase-plan:board']"
      >
        采购计划看板
      </el-button>
    </template> -->
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
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key }">
      <PlanForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <PlanDetail
        :id="key"
        @handle-success="handleRefresh"
        @create-contract="handleCreateContract"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="toContractDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #edit="{ key }">
      <ToContractForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>

  <StockLockCreateDia
    ref="StockLockCreateDiaRef"
    @sure="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import PlanForm from './PlanForm.vue'
import PlanDetail from './PlanDetail.vue'
import ToContractForm from './ToContractForm.vue'
import { columnWidth, formatDateColumn, formatMoneyColumn, formatNumColumn } from '@/utils/table'
import { BpmProcessInstanceResultEnum, PurchasePlanStatusEnum } from '@/utils/constants'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { getVenderSimpleList } from '@/api/common'
import StockLockCreateDia from '../components/StockLockCreateDia.vue'
import { getCompanyIdList } from '@/utils/companyPathUtils'
import { ElMessageBox } from 'element-plus'
import { formatStringConcat } from '@/utils/formatStringConcat'
// onActivated(() => {
//   eplusDialogRef.value?.close()
// })
import { getSourceId, removeSourceId } from '@/utils/auth'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'PurchasePlan' })

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
const handleDialogFailure = () => {}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('采购计划')
}
const exportFileName = ref('采购计划管理.xlsx')
const handleExport = async () => {
  return await eplusTableRef.value.exportList('采购计划管理.xlsx')
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
  eplusDialogRef.value?.openEdit(id, '采购计划')
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
const jumpToPurchaseBoard = () => {
  push({
    name: 'PlanBoard'
  })
}
const finishPlanBtn = async (row) => {
  // const checkedItems = eplusTableRef.value?.checkedItems || []
  // if (!checkedItems.length) {
  //   message.warning('请选择要作废的采购计划')
  //   return
  // }
  // const ids = checkedItems.map((item) => item.id || 0)
  // try {
  //   await PurchasePlanApi.finishPurchasePlan(ids)
  //   message.success('批量作废成功')
  //   handleRefresh()
  // } catch {
  //   message.error('批量作废失败')
  // }
  // console.log(row)
  ElMessageBox.confirm('是否确认作废？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    PurchasePlanApi.finishPurchasePlan([row.id]).then(() => {
      message.success('作废成功')
      handleRefresh()
    })
  })
}
const handleListTocontract = async () => {
  const checkedItems = eplusTableRef.value?.checkedItems || []
  if (!checkedItems.length) {
    message.warning('请选择批量操作的采购计划明细')
    return
  }
  const ids = checkedItems.map((item) => item.id || 0).join(',')
  try {
    ElMessageBox.confirm('确认将选中数据转采购合同吗?', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        await PurchasePlanApi.toContract(ids)
        message.success('转采购合同成功')
      })
      .catch(() => {})
    // toContractDialogRef.value?.openEdit(ids, '转采购单')
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
        v-hasPermi="['scm:purchase-plan:create']"
      >
        新增
      </el-button>
    )
  }
]
let btnSchemas: any = reactive(allBtnSchemas)
//待采购状态下的按钮
const purchaseBtnSchemas = [
  // {
  //   component: (
  //     <el-button
  //       type="primary"
  //       onClick={handleListTocontract}
  //       v-hasPermi="['scm:purchase-plan:contract']"
  //     >
  //       批量转采购合同
  //     </el-button>
  //   )
  // }
  // {
  //   component: (
  //     <el-button
  //       plain
  //       onClick={finishPlanBtn}
  //       v-hasPermi="['scm:purchase-plan:create']"
  //     >
  //       作废
  //     </el-button>
  //   )
  // }
]
//其他tab默认展示采购看板
const defaultBtnSchemas = []
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

const venderNameStr = ref('')
const getVenderName = (e) => {
  e[1].forEach((item) => {
    if (item.code === e[0]) {
      venderNameStr.value = item.name
    }
  })
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
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
      component: <el-input></el-input>,
      name: 'saleContractCode',
      label: '外销合同号'
    },
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
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '采购计划编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '品名'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'creator',
      label: '录入人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'creatorDeptId',
      label: '归属部门',
      formatter: async (args: any[]) => {
        const dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1, skuQuoteFlag: 1 }}
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          onChangeEmit={(...$event) => getVenderName($event)}
        />
      ),
      name: 'venderCode',
      label: '供应商',
      formatter: async (args: any[]) => {
        return venderNameStr.value
      }
    }
    // creatorDeptId
  ],
  moreFields: []
}
let eplusTableSchema = {
  getListApi: async (ps) => {
    const params = tabIndex.value === '0' ? ps : { planStatus: tabIndex.value, ...ps }
    const res = await PurchasePlanApi.getPurchasePlanPage(params)
    res.list?.forEach((item) => {
      let purchaseUserNameAll = ''
      item.children?.forEach((childItem) => {
        if (childItem.purchaseUserName) {
          purchaseUserNameAll = formatStringConcat(purchaseUserNameAll, childItem.purchaseUserName)
        }
      })
      item.purchaseUserName = purchaseUserNameAll
    })
    return {
      list: res.list,
      total: res.total,
      sum: { quantity: res?.summary?.qtySum }
    }
  },
  delListApi: async (id) => {
    await PurchasePlanApi.deletePurchasePlan(id)
  },
  exportListApi: async (ps) => {
    return await PurchasePlanApi.exportPurchasePlan(ps)
  },
  showTabs: true,

  tabs: [
    {
      label: '单据',
      isDefault: true,
      columns: [
        {
          prop: 'code',
          label: '采购计划编号',
          isCopy: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'saleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'companyName',
          label: '采购主体',
          minWidth: columnWidth.l
        },
        {
          prop: 'custName',
          label: '客户',
          minWidth: columnWidth.l
        },
        {
          prop: 'sales',
          label: '业务员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.sales?.nickname
          }
        },
        {
          prop: 'saleDeptName',
          label: '业务员部门',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.sales?.deptName
          }
        },
        {
          prop: 'planStatus',
          label: '单据状态',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.PURCHASE_PLAN_STATUS, row?.planStatus)
          }
        },
        {
          prop: 'auditStatus',
          label: '审核状态',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row.auditStatus)
          }
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorName',
          label: '创建人',
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorDeptName',
          label: '创建人部门',
          minWidth: columnWidth.m
        },
        {
          prop: 'planDate',
          label: '创建日期',
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
                    hasPermi="['scm:purchase-plan:detail']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    submitItem={{
                      permi: 'scm:purchase-plan:submit',
                      handler: async (row) => {
                        await handleSubmit(row)
                      }
                    }}
                    editItem={{
                      user: { userId: row?.creator },
                      permi: 'scm:purchase-plan:update',
                      handler: () => {
                        handleUpdate(row.id)
                      }
                    }}
                    deleteItem={{
                      permi: 'scm:purchase-plan:delete',
                      handler: async (row) => {
                        await handleDelete(row.id)
                      }
                    }}
                    otherItems={[
                      {
                        isShow:
                          [PurchasePlanStatusEnum.UNPURCHASE.status].includes(row?.planStatus) &&
                          checkCreateContract(row),
                        otherKey: 'updatePlan',
                        label: '生成采购合同',
                        checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                        permi: 'scm:purchase-plan:contract',
                        handler: async (row) => {
                          try {
                            handleCreateContract(row)
                          } catch {
                            message.error('修改失败')
                          }
                        }
                      },
                      {
                        isShow: [
                          PurchasePlanStatusEnum.UNPURCHASE.status,
                          PurchasePlanStatusEnum.REJECTED.status,
                          PurchasePlanStatusEnum.COMPLETED.status
                        ].includes(row?.planStatus),
                        otherKey: 'scmPurchasePlanFinish',
                        label: '作废',
                        permi: 'scm:purchase-plan:finish',
                        handler: (row) => {
                          finishPlanBtn(row)
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
    },
    {
      label: '产品',
      summary: true,
      isTree: true,
      columns: [
        {
          prop: 'code',
          label: '采购计划编号',
          isCopy: true,
          isHyperlink: true,
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'saleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'companyName',
          label: '采购主体',
          minWidth: columnWidth.l,
          parent: true
        },
        {
          prop: 'custName',
          label: '客户',
          minWidth: columnWidth.l,
          parent: true
        },
        {
          prop: 'sales',
          label: '业务员',
          minWidth: columnWidth.m,
          parent: true,
          formatter: (row) => {
            return row.sales.nickname
          }
        },
        {
          prop: 'saleDeptName',
          label: '业务员部门',
          minWidth: columnWidth.m,
          parent: true,
          formatter: (row) => {
            return row.sales?.deptName
          }
        },
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
          prop: 'creatorName',
          label: '创建人',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorDeptName',
          label: '创建人部门',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'planDate',
          label: '创建日期',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        // {
        //   prop: 'sortNum',
        //   label: '序号',
        //   minWidth: '50px'
        // },
        {
          prop: 'skuCode',
          label: '产品编号',
          isCopy: true,
          copyFormatter: (data) => {
            return data.children && data.children.length > 0 ? data.children[0] : ''
          },
          minWidth: columnWidth.l,
          align: 'left'
        },
        {
          prop: 'cskuCode',
          label: '客户货号',
          minWidth: columnWidth.l
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
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
          prop: 'skuName',
          label: '品名',
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <EplusSkuName
                  name={row.skuName}
                  type={row.skuType}
                />
              )
            }
          }
        },
        {
          prop: 'purchaseType',
          label: '计划类型',
          minWidth: columnWidth.m,
          formatter: (val) => {
            return getDictLabel(DICT_TYPE.PURCHASE_TYPE, val.purchaseType)
          }
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUserDeptName',
          label: '采购员部门',
          minWidth: columnWidth.m
        },
        {
          prop: 'withTaxPrice',
          label: '含税单价',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'saleQuantity',
          label: '销售数量',
          isCopy: true,
          minWidth: columnWidth.m,
          summary: true,
          formatter: formatNumColumn()
        },
        // 12.10 孙斌让注释掉
        // {
        //   prop: 'availableQuantity',
        //   label: '库存',
        //   minWidth: columnWidth.m,
        //   slots: {
        //     default: (data) => {
        //       const { row } = data
        //       if (activeName.value === 'forth') {
        //         return (
        //           <div class="flex items-center">
        //             <span class="mr-5">{row?.availableQuantity || 0}</span>
        //             <el-button
        //               link
        //               type="primary"
        //               onClick={() => handleLock(row)}
        //             >
        //               分配库存
        //             </el-button>
        //           </div>
        //         )
        //       } else {
        //         return row?.availableQuantity || 0
        //       }
        //     }
        //   }
        // },
        // {
        //   prop: 'currentLockQuantity',
        //   label: '已锁库存',
        //   minWidth: columnWidth.m,
        //   slots: {
        //     default: (data) => {
        //       const { row } = data
        //       return row?.currentLockQuantity || 0
        //     }
        //   }
        // },
        {
          prop: 'needPurQuantity',
          label: '待采购',
          minWidth: columnWidth.m,
          formatter: formatNumColumn()
        },
        {
          prop: 'convertedQuantity',
          label: '已转合同数量',
          minWidth: columnWidth.l,
          formatter: formatNumColumn()
        },
        // {
        //   prop: 'estDeliveryDate',
        //   label: '预计日期',
        //   isCopy: true,
        //   // fixed: 'left',
        //   minWidth: '150px',
        //   formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
        // },
        {
          prop: 'convertedFlag',
          label: '转合同状态',
          minWidth: columnWidth.m,
          formatter: (val) => {
            return getDictLabel(DICT_TYPE.CONVERTED_FLAG, val.convertedFlag)
          }
        },
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
                    hasPermi="['scm:purchase-plan:detail']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    submitItem={{
                      permi: 'scm:purchase-plan:submit',
                      handler: async (row) => {
                        await handleSubmit(row)
                      }
                    }}
                    editItem={{
                      user: { userId: row?.creator },
                      permi: 'scm:purchase-plan:update',
                      handler: () => {
                        handleUpdate(row.id)
                      }
                    }}
                    deleteItem={{
                      permi: 'scm:purchase-plan:delete',
                      handler: async (row) => {
                        await handleDelete(row.id)
                      }
                    }}
                    otherItems={[
                      {
                        isShow:
                          [PurchasePlanStatusEnum.UNPURCHASE.status].includes(row?.planStatus) &&
                          checkCreateContract(row),
                        otherKey: 'updatePlan',
                        label: '生成采购合同',
                        checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                        permi: 'scm:purchase-plan:contract',
                        handler: async (row) => {
                          try {
                            handleCreateContract(row)
                            // toContractDialogRef.value?.openEdit(row.id, '生成采购合同')
                          } catch {
                            message.error('修改失败')
                          }
                        }
                      },
                      {
                        isShow: [
                          PurchasePlanStatusEnum.UNPURCHASE.status,
                          PurchasePlanStatusEnum.REJECTED.status,
                          PurchasePlanStatusEnum.COMPLETED.status
                        ].includes(row?.planStatus),
                        otherKey: 'scmPurchasePlanFinish',
                        label: '作废',
                        permi: 'scm:purchase-plan:finish',
                        handler: (row) => {
                          finishPlanBtn(row)
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
  ]
}

const checkCreateContract = (row) => {
  return isValidArray(row.children.filter((item) => item.needPurQuantity > item.convertedQuantity))
}
const handleCreateContract = async (row) => {
  toContractDialogRef.value?.openEdit(row.id, '生成采购合同')
}

const handleRevertAudit = async (id) => {
  let res = await PurchasePlanApi.planRevertAudit(id)
  if (res) {
    message.success('返审核成功！')
  }
  handleRefresh()
}

const StockLockCreateDiaRef = ref()
const handleLock = (row) => {
  StockLockCreateDiaRef.value.open({
    ...row,
    companyIdList: getCompanyIdList(row?.parent?.companyPath)
  })
}
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
