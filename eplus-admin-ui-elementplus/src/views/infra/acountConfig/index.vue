<template>
  <eplus-table
    v-bind="{ eplusSearchSchema, eplusTableSchema }"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        plain
        v-hasPermi="['system:company:create']"
        @click="handleCreate"
      >
        新增账套
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create>
      <AcountConfigForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key, param }">
      <AcountConfigForm
        :id="key"
        :param="param"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <AcountConfigDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { BpmProcessInstanceResultEnum, ReportTypeEnum } from '@/utils/constants'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as AcountConfigApi from '@/api/infra/acountConfig'
import AcountConfigForm from './AcountConfigForm.vue'
import AcountConfigDetail from './AcountConfigDetail.vue'

defineOptions({
  name: 'AcountConfig'
})

const activeName = ref('first')
const tabIndex = ref('0')
const eplusTableRef = ref()
const message = useMessage()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const exportFileName = ref('账套信息管理.xlsx')

const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
const handleDialogFailure = () => {}

const handleCreate = () => {
  eplusDialogRef.value?.openCreate('账套信息')
}
const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '账套信息')
}
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
// 删除按钮操作
const handleDelete = async (id: number) => {
  try {
    await eplusTableRef.value.delList(id, false)
  } catch (error) {
    message.error('删除失败')
  }
}
let normalColumns = [
  // {
  //   prop: 'id',
  //   label: '编码',
  //   minWidth: '120px'
  // },
  {
    prop: 'name',
    label: '账套名称',
    minWidth: '120px'
  },
  {
    prop: 'companyName',
    label: '企业名称',
    minWidth: '120px'
  },
  {
    prop: 'companyNature',
    label: '企业性质',
    minWidth: '120px',
    formatter: (row) => {
      return <el-tag>{getDictLabel(DICT_TYPE.COMPANY_NATURE, row.companyNature)}</el-tag>
    }
  },
  {
    field: 'enableFlag',
    label: '账套状态',
    minWidth: '100px',
    formatter: (row) => {
      return <el-tag>{getDictLabel(DICT_TYPE.ENABLE_FLAG, row.enableFlag)}</el-tag>
    }
  },
  // {
  //   prop: 'processedFlag',
  //   label: '可加工状态',
  //   minWidth: '80px',
  //   formatter: (row) => {
  //     return <el-tag>{getDictLabel(DICT_TYPE.PROCESSED_FLAG, row.processedFlag)}</el-tag>

  //   }
  // },
  {
    prop: 'action',
    label: '操作',
    minWidth: '65px',
    fixed: 'right',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleDetail(row.id)
              }}
              hasPermi="['system:company:detail']"
            >
              详情
            </el-button>
            <eplus-dropdown
              deleteItem={{
                permi: 'system:company:delete',
                handler: async (row) => {
                  await handleDelete(row.id)
                }
              }}
              otherItems={[
                {
                  isShow: [ReportTypeEnum.BASIC.status].includes(row?.reportType),
                  otherKey: 'handleAction',
                  label: row?.reportType ? '停用' : '启用',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:company:delete',
                  handler: async (row) => {
                    {
                      /* eplusDialogRef.value?.openEdit(row?.id, '打印模板', 'todiy') */
                    }
                    {
                      /* PurchasePlanApi.finishPurchasePlan([row.id]).then(() => {
                        message.success('作废成功')
                        handleRefresh()
                      }) */
                    }
                  }
                },
                {
                  isShow: true,
                  otherKey: 'otherEdit',
                  label: '编辑',
                  permi: 'system:company:update',
                  handler: async () => {
                    handleUpdate(row.id)
                    {
                      /* eplusDialogRef.value?.openEdit(row?.id, '打印模板', 'todiy') */
                    }
                    {
                      /* PurchasePlanApi.finishPurchasePlan([row.id]).then(() => {
                        message.success('作废成功')
                        handleRefresh()
                      }) */
                    }
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
let eplusSearchSchema: EplusSearchSchema = reactive({
  fields: [
    // {
    //   component: <el-input clearable></el-input>,
    //   name: 'id',
    //   label: '编码'
    // },
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '账套名称'
    },

    {
      component: <el-input clearable></el-input>,
      name: 'companyName',
      label: '企业名称'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.COMPANY_NATURE}></eplus-dict-select>,
      name: 'companyNature',
      label: '企业性质',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.COMPANY_NATURE, args[0])
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
})
let eplusTableSchema = reactive({
  getListApi: async (ps) => {
    const params = { reportType: tabIndex.value === '0' ? 1 : 2, ...ps }
    const res = await AcountConfigApi.getAcountConfigPage(params)
    return {
      list: res?.list || [],
      total: res?.total
    }
  },
  delListApi: async (id) => {
    await AcountConfigApi.deleteAcountConfig(id)
  },
  exportListApi: async (ps) => {
    return await AcountConfigApi.exportAcountConfig(ps)
  },
  columns: normalColumns
})
</script>
<style lang="scss" scoped></style>
