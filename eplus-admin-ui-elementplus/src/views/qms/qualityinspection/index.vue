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
    ref="eplusListRef"
    :key="tableKey"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <div class="w-100% flex justify-between">
        <div class="flex-1 overflow-x-auto">
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
        <div class="flex flex-col items-end pr-4">
          <span
            v-for="item in btnSchemas.filter((schema) => schema.align === 'right')"
            :key="item.index"
          >
            <component
              v-if="item.component"
              :is="item.component"
              class="ml-2"
            />
          </span>
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
    <template #edit="{ key }">
      <EditInspect
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <InspectDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
    <!-- 确认验货 -->
    <template #confirm="{ key }">
      <ConfirmInspection
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
    <!-- 验货 -->
    <template #change="{ key }">
      <Inspection
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>

  <!-- 返工和放行弹框 -->
  <ReworkReleaseDialog
    ref="reworkReleaseRef"
    :key="Date.now()"
    @handle-success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as QualityInspectionApi from '@/api/qms/qualityinspection' // 验货单接口集合
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { QualityInspectStatusEnum } from '@/utils/constants'
import { propTypes } from '@/utils/propTypes'
import { columnWidth, formatDateColumn } from '@/utils/table'
import EditInspect from './EditInspect.vue'
import InspectDetail from './InspectDetail.vue'
import ConfirmInspection from './ConfirmInspection.vue' // 确认验货
import Inspection from './Inspection.vue' // 验货
import ReworkReleaseDialog from './components/ReworkReleaseDialog.vue' // 返工放行弹框
import { ElMessageBox } from 'element-plus'
import * as UserApi from '@/api/system/user'
import { getVenderSimpleList } from '@/api/common'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'Qualityinspection' })

const tabNameList = [
  { name: '0', label: '全部' },
  // { name: '1', label: '待审批' },
  { name: '2', label: '待排验' },
  { name: '3', label: '待验货' },
  { name: '4', label: '验货不通过' },
  // { name: '5', label: '部分通过' },
  { name: '6', label: '验货通过(含放行)' },
  { name: '7', label: '已驳回' },
  { name: '8', label: '已作废' }
]

const handleDialogFailure = () => {}
const eplusListRef = ref()
const activeName = ref('0')
const { push } = useRouter()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const tableKey = ref('0')
const tabIndex = ref(0)
const tabName = ref('')
const handleTabsClick = (val) => {
  tabIndex.value = val.index
  tabName.value = val.props.name

  btnSchemasFormat(val.index)
  handleRefresh()
  // eplusListRef.value.handleSearch(

  // )
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const message = useMessage()

const props = defineProps({
  type: propTypes.string.def('')
})

const formData: any = reactive({
  venderId: ''
})

// 审批
/** 添加/修改操作 */
const formRef = ref()
const openForm = (row: any) => {
  formRef.value.open(row)
}

// 返工/放行
const reworkReleaseRef = ref()

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '单据编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '采购单号'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'inspectorId',
      label: '验货人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1 }}
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
        />
      ),
      name: 'venderCode',
      label: '供应商',
      formatter: async (args: any[]) => {
        const vender = await getVenderSimpleList({ venderCode: args[0] })
        return vender.name
      }
    },

    // {
    //   component: <eplus-vender-select></eplus-vender-select>,
    //   name: 'venderCode',
    //   label: '供应商名称',
    //   formatter: async (args: any[]) => {
    //     const vender = await ReportApi.getVenderList({ venderCode: args[0] })
    //     return vender.name
    //   }
    // },

    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'inspectionTime',
          label: '验货日期',
          formatter: '从{0}到{1}'
        },
        {
          name: 'createTime',
          label: '录入日期',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

const exportFileName = ref('验货报告统计表.xlsx')
const handleExport = async () => {
  return await eplusListRef.value.exportList('验货报告统计表.xlsx')
}

// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id, '')
}
// 确认验货单
const handleConfirmInspect = (id: number) => {
  eplusDialogRef.value?.openConfirm(id, '验货单')
}
// 验货
const handleInspection = (id: number) => {
  eplusDialogRef.value?.openChange(id, '验货')
}
// 返工重验/让步放行
const handleReworkRelease = (row, type) => {
  reworkReleaseRef.value?.open(row, type)
}

// 让步放行
// const handleReleaseInspec = (id: number) => {
//   eplusDialogRef.value?.goReleaseInspection(id, '让步放行')
// }
// 编辑验货单
const handleEdit = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '验货单')
}

// 全部状态下的按钮
const allBtnSchemas = []
let btnSchemas: any = reactive(allBtnSchemas)

const preBuyBtnSchemas = []

const btnSchemasFormat = (data) => {
  switch (data) {
    case '':
      btnSchemas = allBtnSchemas
      break
    case '0':
      btnSchemas = allBtnSchemas
      break
    case '3':
      btnSchemas = preBuyBtnSchemas
      break
    default:
      btnSchemas = allBtnSchemas
      break
  }
}

btnSchemasFormat(props.type)

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await QualityInspectionApi.getQualityInspectionPage(
      tabIndex.value == 0
        ? { ...ps, qualityInspectionStatus: [1, 2, 3, 4, 5, 6, 7] }
        : tabIndex.value == 3
          ? { ...ps, qualityInspectionStatus: [4, 6] }
          : tabIndex.value == 4
            ? { ...ps, qualityInspectionStatus: [5, 6] }
            : { ...ps, qualityInspectionStatus: [tabName.value] }
    )
    return {
      list: res.list,
      total: res.total
    }
  },
  exportListApi: async (ps) => {
    return await QualityInspectionApi.exportQualityInspectionApi(ps)
  },

  showTabs: true,
  tabs: [
    {
      label: '单据',
      selection: true,
      isDefault: true,
      columns: [
        {
          prop: 'code',
          label: '单号',
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同',
          minWidth: columnWidth.m
        },
        {
          prop: 'saleContractCode',
          label: '销售合同',
          minWidth: columnWidth.m
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUser',
          label: '采购员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row?.purchaseUser?.nickname
          }
        },
        {
          prop: 'sales',
          label: '业务员',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row?.sales?.nickname
          }
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          minWidth: columnWidth.l
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.l
        },
        {
          prop: 'inspectionType',
          label: '验货方式', // 1： 泛太陪验（工厂）2：泛太陪验（公司内） 3：泛太自验（工厂） 4：泛太自验（公司内） 5：客户自检 6：客户指定第三方 7：远程验货',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.INSPECTION_TYPE, row?.inspectionType)
          }
        },
        {
          prop: 'qualityInspectionStatus',
          label: '状态',
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.QUALITY_INSPECTION_STATUS, row?.qualityInspectionStatus)
          },
          minWidth: columnWidth.m
        },
        {
          prop: 'inspectorName',
          label: '验货人',
          minWidth: columnWidth.m
        },
        {
          prop: 'reinspectionFlag',
          label: '是否重验',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.reinspectionFlag == '1' ? '是' : '否'
          }
        },

        {
          prop: 'sourceCode',
          label: '关联验货单',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.sourceCode ? row.sourceCode : ''
          }
        },
        {
          prop: 'inspectorName',
          label: '验货人',
          minWidth: columnWidth.m
        },

        {
          prop: 'planInspectionTime',
          label: '计划排验时间',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'inspectionTime',
          label: '实际验货时间',
          minWidth: columnWidth.l,
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
                <div class="flex">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    v-hasPermi="scm:purchase-contract:detail"
                  >
                    详情
                  </el-button>

                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: [
                          QualityInspectStatusEnum.WAITING_FOR_CONFIRMATION.status //上游变更
                        ].includes(row?.qualityInspectionStatus),

                        otherKey: 'verification',
                        label: '确认排验',
                        permi: 'qms:quality-inspection:verification',
                        handler: async (row) => {
                          handleConfirmInspect(row.id)
                        }
                      },
                      // {
                      //   isShow: [
                      //     QualityInspectStatusEnum.WAITING_FOR_CONFIRMATION.status //打印验货单
                      //   ].includes(row?.qualityInspectionStatus),
                      //   // isShow: false,
                      //   otherKey: 'signBackContract',
                      //   label: '打印验货单',
                      //   permi: 'scm:purchase-contract:sign-back',
                      //   handler: async (row) => {
                      //     // PurchaseContractApi.signBackPurchaseContract([row.id]).then(() => {
                      //     //   message.success('回签成功')
                      //     //   handleRefresh()
                      //     // })
                      //   }
                      // },
                      {
                        isShow: [
                          QualityInspectStatusEnum.WAITING_FOR_INSPECTION.status //待验货
                        ].includes(row?.qualityInspectionStatus),
                        otherKey: 'uploadInspection',
                        label: '验货',
                        permi: 'qms:quality-inspection:uploadInspection',
                        handler: async (row) => {
                          handleInspection(row.id)
                        }
                      },
                      {
                        isShow: row?.children.some(
                          (item) =>
                            [1, 2].includes(item?.inspectionStatus) && item?.handleFlag === 0
                        ),
                        otherKey: 'reworkInspect',
                        label: '返工重验',
                        permi: 'qms:quality-inspection:rework',
                        handler: async (row) => {
                          handleReworkRelease(row, 1)
                        }
                      },
                      {
                        isShow: row?.children.some(
                          (item) =>
                            [1, 2].includes(item?.inspectionStatus) && item?.handleFlag === 0
                        ),
                        otherKey: 'releaseInspect',
                        label: '让步放行',
                        permi: 'qms:quality-inspection:release',
                        handler: async (row) => {
                          handleReworkRelease(row, 2)
                        }
                      },
                      {
                        isShow:
                          (getDictLabel(DICT_TYPE.INSPECTION_TYPE, row?.inspectionType) ===
                            '远程验货' &&
                            [QualityInspectStatusEnum.WAITING_PASS.status].includes(
                              row?.qualityInspectionStatus
                            )) ||
                          [
                            QualityInspectStatusEnum.WAITING_FAILED.status, //验货不通过
                            QualityInspectStatusEnum.COMPLETED.status, //部分通过
                            QualityInspectStatusEnum.WAITING_FOR_INSPECTION.status,
                            QualityInspectStatusEnum.WAITING_FOR_CONFIRMATION.status
                          ].includes(row?.qualityInspectionStatus),
                        otherKey: 'closeCaseInspect',
                        label: '作废',
                        permi: 'qms:quality-inspection:close',
                        handler: async (row) => {
                          await message.confirm('是否确认作废？')
                          await QualityInspectionApi.closeQualityInspection(row.id)
                          message.success('作废成功')
                          handleRefresh()
                        }
                      },

                      {
                        isShow: [
                          QualityInspectStatusEnum.REJECTED.status //已驳回，需要编辑
                        ].includes(row?.qualityInspectionStatus),
                        otherKey: 'updateInspect',
                        label: '编辑',
                        permi: 'qms:quality-inspection:update',
                        handler: async (row) => {
                          handleEdit(row.id)
                        }
                      }
                    ]}
                    auditable={{ ...row }}
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
      selection: true,
      isTree: true,
      columns: [
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'cskuCode',
          label: '客户货号',
          minWidth: columnWidth.m
        },
        {
          prop: 'mainPicture',
          label: '图片',
          minWidth: columnWidth.s,
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
          label: '产品名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'inspectionStatus',
          label: '验货结果',
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, row?.inspectionStatus)
          },
          minWidth: columnWidth.m
        },
        {
          prop: 'handleFlag',
          label: '处理状态',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.QMS_HANDLE_STATE, row?.handleFlag)
          }
        },

        {
          prop: 'code',
          label: '验货单编号',
          isCopy: true,
          parent: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同',
          isCopy: true,
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'saleContractCode',
          label: '销售合同',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'custName',
          label: '客户名称',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUser',
          label: '采购员',
          parent: true,
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row?.purchaseUser?.nickname
          }
        },
        {
          prop: 'sales',
          label: '业务员',
          parent: true,
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row?.sales?.nickname
          }
        },
        // {
        //   prop: 'qualityInspectionStatus',
        //   label: '合同状态',
        //   parent: true,
        //   formatter: (row) => {
        //     return getDictLabel(DICT_TYPE.PURCHASE_CONTRACT_STATUS, row?.qualityInspectionStatus)
        //   },
        //   minWidth: '140px'
        // },
        // {
        //   prop: 'auditStatus',
        //   label: '审核状态',
        //   parent: true,
        //   formatter: (row) => {
        //     return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row?.auditStatus)
        //   },
        //   minWidth: columnWidth.m
        // },
        {
          prop: 'venderName',
          label: '供应商名称',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'inspectionType',
          label: '验货方式', // 1： 泛太陪验（工厂）2：泛太陪验（公司内） 3：泛太自验（工厂） 4：泛太自验（公司内） 5：客户自检 6：客户指定第三方 7：远程验货',
          parent: true,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.INSPECTION_TYPE, row?.inspectionType)
          },
          minWidth: columnWidth.m
        },
        {
          prop: 'qualityInspectionStatus',
          label: '状态',
          parent: true,
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.QUALITY_INSPECTION_STATUS, row?.qualityInspectionStatus)
          },
          minWidth: columnWidth.m
        },

        {
          prop: 'reinspectionFlag',
          label: '是否重验',
          parent: true,
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.reinspectionFlag == '1' ? '是' : '否'
          }
        },

        {
          prop: 'sourceCode',
          label: '关联验货单',
          parent: true,
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.sourceCode ? row.sourceCode : ''
          }
        },
        // {
        //   prop: 'createTime',
        //   label: '录入日期',
        //   minWidth: '200px',
        //   formatter: formatDateColumn('YYYY-MM-DD')
        // },

        {
          prop: 'inspectorName',
          label: '验货人',
          parent: true,
          minWidth: columnWidth.m
        },

        {
          prop: 'planInspectionTime',
          label: '计划排验时间',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'inspectionTime',
          label: '实际验货时间',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDateColumn('YYYY-MM-DD')
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
                <div class="flex">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['scm:purchase-contract:detail']"
                  >
                    详情
                  </el-button>

                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: [
                          QualityInspectStatusEnum.WAITING_FOR_CONFIRMATION.status //上游变更
                        ].includes(row?.qualityInspectionStatus),

                        otherKey: 'verification',
                        label: '确认排验',
                        permi: 'qms:quality-inspection:verification',
                        handler: async (row) => {
                          handleConfirmInspect(row.id)
                        }
                      },
                      {
                        isShow: row?.children.some(
                          (item) =>
                            [1, 2].includes(item?.inspectionStatus) && item?.handleFlag === 0
                        ),
                        otherKey: 'reworkInspect',
                        label: '返工重验',
                        permi: 'qms:quality-inspection:rework',
                        handler: async (row) => {
                          handleReworkRelease(row, 1)
                        }
                      },
                      {
                        isShow: row?.children.some(
                          (item) =>
                            [1, 2].includes(item?.inspectionStatus) && item?.handleFlag === 0
                        ),
                        otherKey: 'releaseInspect',
                        label: '让步放行',
                        permi: 'qms:quality-inspection:release',
                        handler: async (row) => {
                          handleReworkRelease(row, 2)
                        }
                      },
                      // {
                      //   isShow: [
                      //     QualityInspectStatusEnum.WAITING_FOR_CONFIRMATION.status //打印验货单
                      //   ].includes(row?.qualityInspectionStatus),
                      //   // isShow: false,
                      //   otherKey: 'signBackContract',
                      //   label: '打印验货单',
                      //   permi: 'scm:purchase-contract:sign-back',
                      //   handler: async (row) => {
                      //     // PurchaseContractApi.signBackPurchaseContract([row.id]).then(() => {
                      //     //   message.success('回签成功')
                      //     //   handleRefresh()
                      //     // })
                      //   }
                      // },
                      {
                        isShow: [
                          QualityInspectStatusEnum.WAITING_FOR_INSPECTION.status //待验货
                        ].includes(row?.qualityInspectionStatus),
                        otherKey: 'uploadInspection',
                        label: '验货',
                        permi: 'qms:quality-inspection:uploadInspection',
                        handler: async (row) => {
                          handleInspection(row.id)
                        }
                      },
                      {
                        isShow: [
                          QualityInspectStatusEnum.WAITING_FAILED.status, //验货不通过
                          QualityInspectStatusEnum.COMPLETED.status //部分通过
                        ].includes(row?.qualityInspectionStatus),
                        otherKey: 'closeCaseInspect',
                        label: '作废',
                        permi: 'qms:quality-inspection:closeCase',
                        handler: async (row) => {
                          ElMessageBox.confirm('是否确认作废？', '提示', {
                            confirmButtonText: '确认',
                            cancelButtonText: '取消',
                            type: 'warning'
                          })
                            .then(() => {
                              QualityInspectionApi.updateQualityInspection({
                                id: row.id,
                                qualityInspectionStatus: 8
                              }).then(() => {
                                message.success('作废成功')
                                handleRefresh()
                              })
                            })
                            .catch(() => {})
                        }
                      },

                      {
                        isShow: [
                          QualityInspectStatusEnum.REJECTED.status //已驳回，需要编辑
                        ].includes(row?.qualityInspectionStatus),
                        otherKey: 'updateInspect',
                        label: '编辑',
                        permi: 'qms:quality-inspection:update',
                        handler: async (row) => {
                          handleEdit(row.id)
                        }
                      }
                      // {
                      //   isShow: [
                      //     QualityInspectStatusEnum.FINISH.status //反作废
                      //   ].includes(row?.qualityInspectionStatus),
                      //   otherKey: 'reverseClose',
                      //   label: '反作废',
                      //   permi: 'qms:quality-inspection:reverse',
                      //   handler: async (row) => {
                      //     await QualityInspectionApi.updateQualityInspection({
                      //       id: row.id,
                      //       qualityInspectionStatus: 2
                      //     }).then(() => {
                      //       message.success('反作废成功')
                      //       handleRefresh()
                      //     })
                      //   }
                      // }
                    ]}
                    auditable={{ ...row }}
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
              if ([1, 2].includes(row?.inspectionStatus) && row?.handleFlag === 0) {
                return (
                  <div class="flex items-center">
                    <el-button
                      type="primary"
                      link
                      onClick={() => handleReworkRelease(row, 1)}
                      v-hasPermi="qms:quality-inspection:rework"
                    >
                      返工重验
                    </el-button>
                    <el-button
                      type="primary"
                      link
                      onClick={() => handleReworkRelease(row, 2)}
                      v-hasPermi="qms:quality-inspection:release"
                    >
                      让步放行
                    </el-button>
                  </div>
                )
              }
            }
          }
        }
      ]
    }
  ]
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
