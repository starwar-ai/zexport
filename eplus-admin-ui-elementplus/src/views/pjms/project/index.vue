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
      <ProjectDetail :id="key" />
    </template>
    <template #edit="{ key }">
      <ProjectForm
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <ProjectForm mode="create" />
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
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDictColumn } from '@/utils/table'
import * as ProjectApi from '@/api/pjms/project/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import ProjectDetail from './ProjectDetail.vue'
import ProjectForm from './ProjectForm.vue'
import FinishDia from './components/FinishDia.vue'
import DoneDia from './components/DoneDia.vue'
import * as UserApi from '@/api/system/user'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()
const exportFileName = ref()

const eplusListRef = ref()
defineOptions({ name: 'Project' })
const activeName = ref(0)
const handleTabsClick = () => {
  // eplusListRef.value.handleSearch()
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
      label: '项目名称'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.PJMS_DEVELOP_TYPE}></eplus-dict-select>,
      name: 'developType',
      label: '研发类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.PJMS_DEVELOP_TYPE, args[0])
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'ownerUserId',
      label: '负责人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    }
  ],
  moreFields: []
}
const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ProjectApi.getProjectPage({
      ...ps,
      projectStatus: activeName.value == 0 ? undefined : activeName.value
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await ProjectApi.deleteProject(id)
  },
  columns: [
    {
      prop: 'code',
      label: '编号',
      minWidth: columnWidth.m
    },
    {
      prop: 'name',
      label: '项目名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'developType',
      label: '研发类型',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.PJMS_DEVELOP_TYPE)
    },
    {
      prop: 'budget',
      label: '项目预算',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return `${row.budget?.amount} ${row.budget?.currency}`
        }
      }
    },
    {
      prop: 'ownerUserName',
      label: '项目负责人',
      minWidth: columnWidth.m
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: columnWidth.xl
    },
    {
      prop: 'projectStatus',
      label: '状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.PJMS_PROJECT_STATUS)
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: columnWidth.l,
      fixed: 'right',
      slots: {
        default: (data) => {
          const { row } = data
          let statusVal = getDictLabel(DICT_TYPE.PJMS_PROJECT_STATUS, row.projectStatus)
          return (
            <div class="flex items-center">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['pjms:project:query']"
                class="mr10px"
              >
                详情
              </el-button>

              <eplus-dropdown
                otherItems={[
                  {
                    isShow: ['待提交', '已驳回'].includes(statusVal),
                    otherKey: 'pjmsProjectUpdate',
                    label: '编辑',
                    permi: 'pjms:project:update',
                    handler: async (row) => {
                      handleUpdate(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '待提交',
                    otherKey: 'pjmsProjectSubmit',
                    label: '提交',
                    permi: 'pjms:project:submit',
                    handler: async (row) => {
                      handleSubmit(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '待提交',
                    otherKey: 'pjmsProjectDelete',
                    label: '删除',
                    permi: 'pjms:project:delete',
                    handler: async (row) => {
                      handleDelete(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '已立项',
                    otherKey: 'pjmsProjectDone',
                    label: '完成',
                    permi: 'pjms:project:done',
                    handler: async (row) => {
                      handleDone(row?.id)
                    }
                  },
                  {
                    isShow: statusVal === '已立项',
                    otherKey: 'pjmsProjectFinish',
                    label: '作废',
                    permi: 'pjms:project:finish',
                    handler: async (row) => {
                      handleFinish(row.id)
                    }
                  },
                  {
                    isShow: statusVal === '已作废',
                    otherKey: 'pjmsProjectRollbackFinish',
                    label: '反作废',
                    permi: 'pjms:project:rollback-finish',
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
  let res = await ProjectApi.submitProject(id)
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
      await ProjectApi.rollbackFinishProject({ id })
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
