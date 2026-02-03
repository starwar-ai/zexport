<template>
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-change="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        :name="0"
      />
      <el-tab-pane
        label="待提交"
        :name="1"
      />
      <el-tab-pane
        label="待审核"
        :name="2"
      />
      <el-tab-pane
        label="已立项"
        :name="3"
      />
      <el-tab-pane
        label="已完成"
        :name="4"
      />
      <el-tab-pane
        label="已作废"
        :name="5"
      />
      <el-tab-pane
        label="已驳回"
        :name="6"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        @click="handleCreate"
        v-hasPermi="['exms:exhibition:create']"
      >
        新增
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <ExhibitionDetail :id="key" />
    </template>
    <template #edit="{ key }">
      <ExhibitionForm
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <ExhibitionForm mode="create" />
    </template>
  </eplus-dialog>

  <FinishDia
    ref="FinishDiaRef"
    @sure="handleRefresh"
  />
  <DoneDia
    ref="DoneDiaRef"
    @sure="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel, getDictLabels } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as ExhibitionApi from '@/api/exms/exhibition/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import ExhibitionDetail from './ExhibitionDetail.vue'
import ExhibitionForm from './ExhibitionForm.vue'
import FinishDia from './components/FinishDia.vue'
import DoneDia from './components/DoneDia.vue'
import * as UserApi from '@/api/system/user'
import { isValidArray } from '@/utils/is.ts'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const eplusListRef = ref()
defineOptions({ name: 'Exhibition' })
const activeName = ref(0)
const handleTabsClick = () => {
  eplusListRef.value.handleSearch()
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '编号'
    },
    {
      component: <el-input></el-input>,
      name: 'name',
      label: '展会名称'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.EXMS_EXHIBITION_THEME}></eplus-dict-select>,
      name: 'theme',
      label: '展会主题',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.EXMS_EXHIBITION_THEME, args[0])
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'ownerUserId',
      label: '展会负责人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    }
    // {
    //   component: <EplusSearchMultiDatePicker />,
    //   subfields: [
    //     {
    //       name: 'createTime',
    //       label: '展会时间',
    //       formatter: '从{0}到{1}'
    //     }
    //   ]
    // }
  ],
  moreFields: []
}
const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ExhibitionApi.getExhibitionPage({
      ...ps,
      expoStatus: activeName.value == 0 ? undefined : activeName.value
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await ExhibitionApi.deleteExhibition(id)
  },
  columns: [
    {
      prop: 'code',
      label: '编号',
      minWidth: columnWidth.m
    },
    {
      prop: 'name',
      label: '展会名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'theme',
      label: '展会主题',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.EXMS_EXHIBITION_THEME)
    },
    // {
    //   prop: 'planStartDate',
    //   label: '计划开始日期',
    //   minWidth: '100px',
    //   formatter: formatDateColumn('YYYY-MM-DD')
    // },
    // {
    //   prop: 'planEndDate',
    //   label: '计划结束日期',
    //   minWidth: '100px',
    //   formatter: formatDateColumn('YYYY-MM-DD')
    // },
    {
      prop: 'stallThemeList',
      label: '摊位主题',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.stallThemeList) {
            return getDictLabels(DICT_TYPE.EXMS_STALL_THEME, row.stallThemeList)
          } else {
            return ''
          }
        }
      }
    },
    {
      prop: 'stallArea',
      label: '摊位面积',
      minWidth: columnWidth.m
    },
    {
      prop: 'budget',
      label: '展会预算',
      minWidth: columnWidth.m,
      formatter: formatMoneyColumn()
    },
    {
      prop: 'ownerUserName',
      label: '展会负责人',
      minWidth: columnWidth.m
    },
    {
      prop: 'deptList',
      label: '费用承担部门',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          if (isValidArray(row.deptMsgList)) {
            return row?.deptMsgList.map((item) => item.name).join(',')
          } else {
            return ''
          }
        }
      }
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: columnWidth.xl
    },
    {
      prop: 'expoStatus',
      label: '状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.EXMS_EXHIBITION_STATUS)
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: columnWidth.l,
      fixed: 'right',
      slots: {
        default: (data) => {
          const { row } = data
          let statusVal = getDictLabel(DICT_TYPE.EXMS_EXHIBITION_STATUS, row.expoStatus)
          return (
            <div class="flex items-center ">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['exms:exhibition:query']"
                class="mr10px"
              >
                详情
              </el-button>

              <eplus-dropdown
                otherItems={[
                  {
                    isShow: ['待提交', '已驳回'].includes(statusVal),
                    otherKey: 'exmsExhibitionUpdate',
                    label: '编辑',
                    permi: 'exms:exhibition:update',
                    handler: async (row) => {
                      handleUpdate(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '待提交',
                    otherKey: 'exmsExhibitionSubmit',
                    label: '提交',
                    permi: 'exms:exhibition:submit',
                    handler: async (row) => {
                      handleSubmit(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '待提交',
                    otherKey: 'exmsExhibitionDelete',
                    label: '删除',
                    permi: 'exms:exhibition:delete',
                    handler: async (row) => {
                      handleDelete(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '已立项',
                    otherKey: 'exmsExhibitionDone',
                    label: '完成',
                    permi: 'exms:exhibition:done',
                    handler: async (row) => {
                      handleDone(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '已立项',
                    otherKey: 'exmsExhibitionFinish',
                    label: '作废',
                    permi: 'exms:exhibition:finish',
                    handler: async (row) => {
                      handleFinish(row.id)
                    }
                  },
                  {
                    isShow: statusVal === '已作废',
                    otherKey: 'exmsExhibitionRollbackFinish',
                    label: '反作废',
                    permi: 'exms:exhibition:rollback-finish',
                    handler: async (row) => {
                      handleRollbackFinish(row.id)
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

const handleDetail = (id) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleUpdate = (id) => {
  eplusDialogRef.value?.openEdit(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}
const handleSubmit = async (id) => {
  // 提交请求
  let res = await ExhibitionApi.submitExhibition(id)
  if (res) {
    message.success('已提交审核！')
  }
  handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const FinishDiaRef = ref()
const handleFinish = (id) => {
  FinishDiaRef.value.open('finish', id)
}

const DoneDiaRef = ref()
const handleDone = (id) => {
  DoneDiaRef.value.open('done', id)
}

const handleRollbackFinish = (id) => {
  ElMessageBox.confirm('确认将选中数据进行反作废操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await ExhibitionApi.rollbackFinishExhibition({ id })
    })
    .then(() => {
      message.success('操作成功!')
      handleRefresh()
    })
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
