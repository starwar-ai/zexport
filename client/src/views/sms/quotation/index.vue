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
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        @click="handleCreate()"
        v-hasPermi="['sms:quotation:create']"
      >
        新增
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #edit="{ key }">
      <quotationForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>
    <template #create>
      <QuotationForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
    <template #detail="{ key }">
      <QuotationFormDetail
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import * as quotationApi from '@/api/sms/quotation'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import QuotationForm from './QuotationForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import * as LoanAppApi from '@/api/oa/loanapp'
import QuotationFormDetail from './QuotationFormDetail.vue'
import { EplusTableSchema } from '@/types/eplus'
import { QuotationStatusEnum } from '@/utils/constants'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const handleDialogFailure = () => {}
const activeName = ref('first')
const tabNameList = [
  { name: 'first', label: '全部' },
  { name: 'second', label: '待提交' },
  { name: 'third', label: '待审核' },
  { name: 'forth', label: '已审核' },
  { name: 'five', label: '已驳回' },
  { name: 'six', label: '已作废' }
]

const handleTabsClick = (val) => {
  activeName.value = val.props.name
}
const exportFileName = ref()

const eplusListRef = ref()
defineOptions({ name: 'SmsQuotation' })

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      name: 'companyId',
      label: '公司主体',
      component: (
        <eplus-custom-select
          api={LoanAppApi.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择"
          style="width:100%"
        />
      )
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    var status = ''
    var currentTab = tabNameList.filter((item) => item.name == activeName.value)
    if (currentTab?.length) {
      status = getDictValue(DICT_TYPE.QUOTATION_STATUS, currentTab[0].label)
    }
    const params = { status: status, ...ps }
    const res = await quotationApi.getQuotationPage(params)
    if (res && res.list) {
      return {
        list: res.list,
        total: res.total
      }
    } else {
      return {
        list: [],
        total: 0
      }
    }
  },
  delListApi: async (id) => {
    await quotationApi.deleteQuotation(id)
  },
  selection: false,
  columns: [
    {
      prop: 'code',
      label: '报价单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '公司主体',
      minWidth: columnWidth.m
    },
    {
      prop: 'custName',
      label: '客户名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'isNewCust',
      label: '是否新客户',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.IS_INT, row?.isNewCust)
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'currency',
      label: '币种',
      minWidth: columnWidth.m
    },
    {
      prop: 'settlementTermType',
      label: '价格条款',
      minWidth: columnWidth.m
    },
    {
      prop: 'validPeriod',
      label: '有效期止',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'status',
      label: '状态',
      formatter: formatDictColumn(DICT_TYPE.QUOTATION_STATUS),
      minWidth: columnWidth.m
    },
    {
      prop: 'createTime',
      label: '录入时间',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'createUserName',
      label: '录入人',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return row?.createUser?.nickname
      }
    },
    {
      prop: 'createUserDeptName',
      label: '录入人部门',
      minWidth: columnWidth.m,
      formatter: (row) => {
        return row?.createUser?.deptName
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
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['sms:quotation:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  permi: 'sms:quotation:update',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                editItem={{
                  permi: 'sms:quotation:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  permi: 'sms:quotation:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: [QuotationStatusEnum.APPROVED.status].includes(row?.status),
                    otherKey: 'finish',
                    label: '作废',
                    permi: 'sms:quotation:finish',
                    handler: async (row) => {
                      await quotationApi.finish({ id: row.id })
                      message.success('操作成功')
                      handleRefresh()
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

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const message = useMessage()
const handleSubmit = async (data) => {
  try {
    await quotationApi.submitQuotation(data.id)
    message.success('提交成功')
  } catch (error) {
    message.error('提交失败' + error)
  }
  handleRefresh()
}
</script>
