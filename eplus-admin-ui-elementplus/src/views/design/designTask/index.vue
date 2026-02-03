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
      <el-button
        type="primary"
        @click="handleCreate"
        v-hasPermi="['dtms:design:create']"
      >
        创建设计任务
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create="{ key }">
      <DesignCreateForm
        mode="create"
        :id="key"
      />
    </template>

    <template #edit="{ key }">
      <DesignCreateForm
        mode="edit"
        :id="key"
      />
    </template>
    <template #detail="{ key }">
      <DesignTaskDetail :id="key" />
    </template>
  </eplus-dialog>

  <DesignTaskDialog
    ref="designTaskDialogRef"
    :key="Date.now()"
    @handle-success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
// import DesignDialog from './components/DesignDialog.vue' // 私有弹框
import { EplusDialog } from '@/components/EplusDialog'
import * as DesignTaskApi from '@/api/dtms/designTask' // 设计任务单接口集合
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { DesignTaskStatusEnum } from '@/utils/constants'
import { columnWidth, formatDateColumn } from '@/utils/table'
import DesignCreateForm from './DesignCreateForm.vue'
import DesignTaskDetail from './DesignTaskDetail.vue'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
// import TestPrint from './testPrint.vue'
import DesignTaskDialog from './components/DesignTaskDialog.vue' // 弹框
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'DesignTask' })

const tabNameList = [
  { name: '0', label: '全部' },
  { name: '1', label: '待提交' },
  { name: '2', label: '待审批' },
  { name: '3', label: '待完成' },
  { name: '4', label: '待评价' },
  { name: '5', label: '已完成' },
  { name: '6', label: '已作废' },
  { name: '7', label: '已驳回' }
]

const handleDialogFailure = () => {}
const eplusListRef = ref()
const activeName = ref('0')
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const tableKey = ref('0')
const { t } = useI18n()

const exportFileName = ref('设计管理.xlsx')

const currEvalStatus = ref('')

const handleTabsClick = (val) => {
  eplusListRef.value.handleSearch(val.index === '0' ? {} : { designStatus: val.props.name })
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const message = useMessage()

// 审批
/** 添加/修改操作 */
const formRef = ref()

// 完成提示/任务进度/作废/评价
const designTaskDialogRef = ref()
//

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '设计单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '设计名称'
    },

    {
      component: <eplus-dict-select dictType={DICT_TYPE.APPROVED_TYPE}></eplus-dict-select>,
      name: 'specialPermissionFlag',
      label: '紧急程度'
    },
    // {
    //   component: <eplus-user-select></eplus-user-select>,
    //   name: 'inspectorId',
    //   label: '设计师',
    //   formatter: async (args: any[]) => {
    //     var user = await UserApi.getSimpleUser(args[0])
    //     return user.nickname
    //   }
    // },
    {
      component: <el-input clearable></el-input>,
      name: 'saleContractCode',
      label: '销售合同'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '申请时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
    // {
    //   component: (
    //     <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}></eplus-dict-select>
    //   ),
    //   name: 'auditStatus',
    //   label: '状态',
    //   formatter: (args: any[]) => {
    //     return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
    //   }
    // }
  ],
  moreFields: []
}

// 新增/编辑验货单
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('设计任务单')
}
const handleEdit = (id: number, type) => {
  eplusDialogRef.value?.openEdit(id, '设计任务单')
}

// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id, '')
}

// 完成提示/任务进度/作废/评价
const handleReworkRelease = (id: number, type, row?) => {
  designTaskDialogRef.value?.open(id, type, row)
}

// 删除
const handleDeleteDesign = (id: number) => {
  ElMessageBox.confirm(t('common.delMessage'), t('common.confirmTitle'), {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    await DesignTaskApi.deleteDesign(id)
    message.success('删除成功')
    handleRefresh()
  })
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    let userId = useUserStore().getUser.id + ''
    const res = await DesignTaskApi.getDesignTaskPage({ ...ps })
    res.list.forEach((e) => {
      let designerIdArr = e.designerIds?.split(',')
      if (e.designStatus == 3 && designerIdArr != undefined) {
        if (designerIdArr.includes(userId)) {
          e.currEvalStatus = 3
        }
      }
      // 评价和作废

      if (userId == e.applyDesignerId) {
        e.evaluate = 4
      }
      // 指定认领人
      if (e.specificDesigners?.length > 0) {
        let name = ''
        e.specificDesigners.forEach((sd) => {
          if (name == '') {
            name = sd.nickname
          } else {
            name = name + ',' + sd.nickname
          }
        })
        e.specificDesignerNames = name
      }
    })
    return {
      list: res.list,
      total: res.total
    }
  },

  selection: false,

  columns: [
    {
      prop: 'code',
      label: '设计任务单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'designStatus',
      label: '任务状态',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.DESIGN_STATUS, row?.designStatus)
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'name',
      label: '设计名称',
      minWidth: columnWidth.l
    },

    {
      prop: 'specialPermissionFlag',
      label: '紧急程度',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.APPROVED_TYPE, row?.specialPermissionFlag)
      }
    },
    {
      prop: 'designType',
      label: '任务类型',
      multiple: true,
      formatter: (row) => {
        let data = row?.designType.split(',')
        let str = ''
        data.forEach((e) => {
          str += getDictLabel(DICT_TYPE.DESIGN_TYPE, e) + ' '
        })
        return str
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'createTime',
      label: '申请时间',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'expectCompleteDate',
      label: '希望完成日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'completeDate',
      label: '实际完成日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'specificDesignerNames',
      label: '指定设计师',
      minWidth: columnWidth.m
    },
    {
      prop: 'applyDesignerName',
      label: '申请人',
      minWidth: columnWidth.m
    },
    {
      prop: 'applyDesignerDeptName',
      label: '申请人部门',
      minWidth: columnWidth.m
    },

    {
      prop: 'designerNames',
      label: '设计师',
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
                hasPermi="['dtms:design:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: [
                      DesignTaskStatusEnum.WAITING_SUBMIT.status //待提交
                    ].includes(row?.designStatus),
                    otherKey: 'dtmsDelete',
                    label: '删除',
                    permi: 'dtms:design:delete',
                    handler: async (row) => {
                      handleDeleteDesign(row.id)
                    }
                  },
                  {
                    isShow: [
                      DesignTaskStatusEnum.WAITING_SUBMIT.status, //待提交
                      DesignTaskStatusEnum.REJECTED.status //待提交
                    ].includes(row?.designStatus),
                    otherKey: 'designUpdata',
                    label: '编辑',
                    permi: 'dtms:design:update',
                    handler: async (row) => {
                      handleEdit(row.id, 'edit')
                    }
                  },

                  {
                    isShow: [
                      // DesignTaskStatusEnum.WAITING_COMPLETE.status, //待完成
                      row.currEvalStatus
                    ].includes(row?.designStatus),
                    otherKey: 'dtmsSpeed',
                    label: '填写进度',
                    permi: 'dtms:design:speed',
                    handler: async (row) => {
                      handleReworkRelease(row.id, 2)
                    }
                  },
                  {
                    isShow: [
                      // DesignTaskStatusEnum.WAITING_COMPLETE.status //待完成
                      row.currEvalStatus
                    ].includes(row?.designStatus),
                    otherKey: 'dtmsComplete',
                    label: '完成任务',
                    permi: 'dtms:design:complete',
                    handler: async (row) => {
                      handleReworkRelease(row.id, 1, row)
                    }
                  },
                  {
                    isShow: [
                      // DesignTaskStatusEnum.WAITING_EVALUATE.status //待评价
                      row.evaluate
                    ].includes(row?.designStatus),
                    otherKey: 'releaseInspect',
                    label: '评价',
                    permi: 'dtms:design:evaluate',
                    handler: async (row) => {
                      handleReworkRelease(row.id, 3)
                    }
                  },
                  {
                    isShow: [
                      // DesignTaskStatusEnum.WAITING_EVALUATE.status //待完成
                      row.evaluate
                    ].includes(row?.designStatus),
                    otherKey: 'closeCaseInspect',
                    label: '作废',
                    permi: 'dtms:design:update',
                    handler: async (row) => {
                      handleReworkRelease(row.id, 4, row)
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
}
onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
