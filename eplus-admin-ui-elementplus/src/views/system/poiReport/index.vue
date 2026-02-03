<template>
  <div class="h-45px bg-[var(--left-menu-bg-color)] pl-15px pr-15px">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleTabsClick"
    >
      <el-tab-pane
        v-for="item in tabNameList"
        v-bind="{ label: item.label, name: item.name }"
        :key="item.label"
      />
    </el-tabs>
  </div>
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
        v-hasPermi="['system:poi-report:create']"
        @click="handleCreate()"
      >
        新增标准模板
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
      <BasicModelForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #edit="{ key, param }">
      <BasicModelForm
        :id="key"
        :param="param"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #change="{ key, param }">
      <BasicModelForm
        :id="key"
        :param="param"
        mode="change"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <BasicModelDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="eplusDialog2Ref"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #edit="{ key, param }">
      <BasicModelForm
        :id="key"
        :param="param"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <DIYModelDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="eplusDialog3Ref"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #edit="{ key, param }">
      <BasicModelForm
        :id="key"
        :param="param"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <SpecificModelDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="eplusDialog4Ref"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #edit="{ key, param }">
      <BasicModelForm
        :id="key"
        :param="param"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <CompanyModelDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import { BpmProcessInstanceResultEnum, ReportTypeEnum } from '@/utils/constants'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as ReportApi from '@/api/system/poiReport'
import BasicModelForm from './BasicModelForm.vue'
import BasicModelDetail from './BasicModelDetail.vue'
import DIYModelDetail from './DIYModelDetail.vue'
import SpecificModelDetail from './SpecificModelDetail.vue'
import CompanyModelDetail from './CompanyModelDetail.vue'
import { checkPermi } from '@/utils/permission'

defineOptions({
  name: 'PoiReport'
})

const tabNameList = [
  { name: 'first', label: '标准模板' },
  { name: 'second', label: '公司特定模板' },
  { name: 'third', label: '外部模板' },
  { name: 'forth', label: '可选模板' }
]
const activeName = ref('first')
const tabIndex = ref('0')
const eplusTableRef = ref()
const message = useMessage()
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const eplusDialog2Ref = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const eplusDialog3Ref = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const eplusDialog4Ref = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const exportFileName = computed(() => {
  return checkPermi(['system:poi-report:export']) ? '模板管理.xlsx' : ''
})

const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
const handleDialogFailure = () => {}

const handleCreate = () => {
  eplusDialogRef.value?.openCreate('打印模板')
}
const handleSubmit = async (data) => {
  // 提交请求
  let res = await ReportApi.submitReport(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  handleRefresh()
}
const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '打印模板')
}
const handleUpdateCompany = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '打印模板', 'company')
}
const handleUpdateDIY = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '打印模板', 'diy')
}
const handleUpdateSpecific = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '打印模板', 'specific')
}
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleSpecificDetail = (id: number) => {
  eplusDialog3Ref.value?.openDetail(id)
}
const handleDIYDetail = (id: number) => {
  eplusDialog2Ref.value?.openDetail(id)
}
const handleCompanyDetail = (id: number) => {
  eplusDialog4Ref.value?.openDetail(id)
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
  {
    prop: 'code',
    label: '模板编码',
    minWidth: columnWidth.l
  },
  {
    prop: 'businessType',
    label: '业务类型',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return <el-tag>{getDictLabel(DICT_TYPE.REPORT_BUSINESS_TYPE, row.businessType)}</el-tag>
    }
  },
  {
    prop: 'name',
    label: '模板名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'annex',
    label: '模板路径',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return row?.annex ? row.annex[0].fileUrl : ''
    }
  },
  {
    prop: 'auditStatus',
    label: '审核状态',
    minWidth: columnWidth.m,
    formatter: (row) => {
      return <el-tag>{getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row.auditStatus)}</el-tag>
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
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleDetail(row.id)
              }}
              hasPermi="['system:poi-report:detail']"
            >
              详情
            </el-button>
            <eplus-dropdown
              submitItem={{
                permi: 'system:poi-report:submit',
                handler: async (row) => {
                  await handleSubmit(row)
                }
              }}
              editItem={{
                permi: 'system:poi-report:update',
                handler: () => {
                  handleUpdate(row.id)
                }
              }}
              deleteItem={{
                permi: 'system:poi-report:delete',
                handler: async (row) => {
                  await handleDelete(row.id)
                }
              }}
              otherItems={[
                {
                  isShow: [ReportTypeEnum.BASIC.status].includes(row?.reportType),
                  otherKey: 'ToCompanyReport',
                  label: '生成公司特定模板',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:poi-report:delete',
                  handler: async (row) => {
                    eplusDialogRef.value?.openEdit(row?.id, '打印模板', 'toCompany')
                  }
                },
                {
                  isShow: [ReportTypeEnum.BASIC.status].includes(row?.reportType),
                  otherKey: 'change',
                  label: '变更',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:poi-report:directly-update',
                  handler: async (row) => {
                    eplusDialogRef.value?.openChange(row?.id, '标准模板变更')
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
let diyColumns = [
  {
    prop: 'code',
    label: '模板编码',
    minWidth: columnWidth.l
  },
  {
    prop: 'name',
    label: '模板名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'companyName',
    label: '账套名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'annex',
    label: '模板路径',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return row?.annex ? row.annex[0].fileUrl : ''
    }
  },
  {
    prop: 'reportType',
    label: '模板类型',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.REPORT_TYPE)
  },
  {
    prop: 'sourceType',
    label: '外部模板类型',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.REPORT_SOURCE_TYPE)
  },
  {
    prop: 'sourceCode',
    label: '客户/供应商编号',
    minWidth: columnWidth.xl
  },
  {
    prop: 'sourceName',
    label: '客户/供应商名称',
    minWidth: columnWidth.xl
  },
  {
    prop: 'createTime',
    label: '创建时间',
    formatter: formatDateColumn(),
    width: columnWidth.l
  },
  {
    prop: 'auditStatus',
    label: '审核状态',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return <el-tag>{getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row.auditStatus)}</el-tag>
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
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleDIYDetail(row.id)
              }}
              hasPermi="['system:poi-report:detail']"
            >
              详情
            </el-button>
            <eplus-dropdown
              submitItem={{
                permi: 'system:poi-report:submit',
                handler: async (row) => {
                  await handleSubmit(row)
                }
              }}
              editItem={{
                permi: 'system:poi-report:update',
                handler: () => {
                  handleUpdateDIY(row.id)
                }
              }}
              deleteItem={{
                permi: 'system:poi-report:delete',
                handler: async (row) => {
                  await handleDelete(row.id)
                }
              }}
              otherItems={[
                {
                  isShow: true,
                  otherKey: 'change',
                  label: '变更',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:poi-report:directly-update',
                  handler: async (row) => {
                    eplusDialogRef.value?.openChange(row?.id, '外部模板变更', 'toDiy')
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

let companyColumns = [
  {
    prop: 'code',
    label: '模板编码',
    minWidth: columnWidth.l
  },
  {
    prop: 'name',
    label: '模板名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'companyName',
    label: '账套名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'annex',
    label: '模板路径',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return row?.annex ? row.annex[0].fileUrl : ''
    }
  },
  {
    prop: 'reportType',
    label: '模板类型',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.REPORT_TYPE)
  },
  {
    prop: 'createTime',
    label: '创建时间',
    formatter: formatDateColumn(),
    width: columnWidth.l
  },
  {
    prop: 'auditStatus',
    label: '审核状态',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return <el-tag>{getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row.auditStatus)}</el-tag>
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
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleCompanyDetail(row.id)
              }}
              hasPermi="['system:poi-report:detail']"
            >
              详情
            </el-button>
            <eplus-dropdown
              submitItem={{
                permi: 'system:poi-report:submit',
                handler: async (row) => {
                  await handleSubmit(row)
                }
              }}
              editItem={{
                permi: 'system:poi-report:update',
                handler: () => {
                  handleUpdateCompany(row.id)
                }
              }}
              deleteItem={{
                permi: 'system:poi-report:delete',
                handler: async (row) => {
                  await handleDelete(row.id)
                }
              }}
              otherItems={[
                {
                  isShow: [ReportTypeEnum.COMPANY.status].includes(row?.reportType),
                  otherKey: 'toDiyReport',
                  label: '生成外部模板',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:poi-report:create',
                  handler: async (row) => {
                    eplusDialogRef.value?.openEdit(row?.id, '打印模板', 'toDiy')
                  }
                },
                {
                  isShow: [ReportTypeEnum.COMPANY.status].includes(row?.reportType),
                  otherKey: 'ToSpecificReport',
                  label: '生成可选模板',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:poi-report:create',
                  handler: async (row) => {
                    eplusDialogRef.value?.openEdit(row?.id, '打印模板', 'toSpecific')
                  }
                },
                {
                  isShow: [ReportTypeEnum.COMPANY.status].includes(row?.reportType),
                  otherKey: 'change',
                  label: '变更',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:poi-report:directly-update',
                  handler: async (row) => {
                    eplusDialogRef.value?.openChange(row?.id, '公司特定模板变更', 'company')
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

let specificColumns = [
  {
    prop: 'code',
    label: '模板编码',
    minWidth: columnWidth.l
  },
  {
    prop: 'name',
    label: '模板名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'companyName',
    label: '账套名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'annex',
    label: '模板路径',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return row?.annex ? row.annex[0].fileUrl : ''
    }
  },
  {
    prop: 'reportType',
    label: '模板类型',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.REPORT_TYPE)
  },
  {
    prop: 'createTime',
    label: '创建时间',
    formatter: formatDateColumn(),
    width: columnWidth.l
  },
  {
    prop: 'auditStatus',
    label: '审核状态',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return <el-tag>{getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, row.auditStatus)}</el-tag>
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
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleSpecificDetail(row.id)
              }}
              hasPermi="['system:poi-report:detail']"
            >
              详情
            </el-button>
            <eplus-dropdown
              submitItem={{
                permi: 'system:poi-report:submit',
                handler: async (row) => {
                  await handleSubmit(row)
                }
              }}
              editItem={{
                permi: 'system:poi-report:update',
                handler: () => {
                  handleUpdateSpecific(row.id)
                }
              }}
              deleteItem={{
                permi: 'system:poi-report:delete',
                handler: async (row) => {
                  await handleDelete(row.id)
                }
              }}
              otherItems={[
                {
                  isShow: true,
                  otherKey: 'ToSpecificReport',
                  label: '变更',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  permi: 'system:poi-report:directly-update',
                  handler: async (row) => {
                    eplusDialogRef.value?.openChange(row?.id, '可选模板变更', 'toSpecific')
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
const defaultFileds = [
  {
    component: <el-input clearable></el-input>,
    name: 'code',
    label: '模板编码'
  },
  {
    component: <el-input clearable></el-input>,
    name: 'name',
    label: '模板名称'
  },

  {
    component: <el-input clearable></el-input>,
    name: 'sourceCode',
    label: '来源编号'
  },
  {
    component: <el-input clearable></el-input>,
    name: 'sourceName',
    label: '来源名称'
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
]
const diyFileds = [
  {
    component: <el-input clearable></el-input>,
    name: 'code',
    label: '模板编码'
  },
  {
    component: <el-input clearable></el-input>,
    name: 'name',
    label: '模板名称'
  },

  {
    component: <el-input clearable></el-input>,
    name: 'sourceName',
    label: '客户/供应商名称'
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
]
let eplusSearchSchema: EplusSearchSchema = reactive({
  fields: defaultFileds,
  moreFields: []
})
let eplusTableSchema = reactive({
  getListApi: async (ps) => {
    var reportType = ''
    var currentTab = tabNameList.filter((item) => item.name == activeName.value)
    if (currentTab?.length) {
      reportType = getDictValue(DICT_TYPE.REPORT_TYPE, currentTab[0].label)
    }
    const params = { reportType: reportType, ...ps }
    const res = await ReportApi.getReportPage(params)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await ReportApi.deleteReport(id)
  },
  exportListApi: async (ps) => {
    return await ReportApi.exportPoireport(ps)
  },
  columns: normalColumns
})

const handleTabsClick = (val) => {
  eplusSearchSchema.fields = defaultFileds
  activeName.value = val.props.name
  tabIndex.value = val.index
  if (tabIndex.value === '0') {
    eplusTableSchema.columns = normalColumns
  } else if (val.index === '1') {
    eplusTableSchema.columns = companyColumns
  } else if (val.index === '2') {
    eplusTableSchema.columns = diyColumns
    eplusSearchSchema.fields = diyFileds
  } else {
    eplusTableSchema.columns = specificColumns
  }
}
</script>
<style lang="scss" scoped></style>
