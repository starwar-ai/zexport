<template>
  <div class="h-45px bg-[var(--left-menu-bg-color)] pl-15px pr-15px">
    <el-tabs
      v-model="activeName"
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
    v-bind="{ eplusSearchSchema, eplusTableSchema }"
    ref="eplusTableRef"
    @refresh="handleRefresh"
    key="receipt"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  />
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <InvoicingNoticesDetail
        :id="key"
        @success="handleRefresh"
      />
    </template>
  </eplus-dialog>

  <ToOrderNotice
    ref="toOrderNoticeRef"
    @success="handleRefresh"
  />
</template>
<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import * as InvoicingNoticesApi from '@/api/scm/invoicingNotices'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import InvoicingNoticesDetail from './InvoicingNoticesDetail.vue'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as UserApi from '@/api/system/user'
import { getVenderSimpleList } from '@/api/common'
import ToOrderNotice from '@/views/scm/purchase/components/ToOrderNotice.vue'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'

defineOptions({ name: 'InvoicingNotices' })

const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上
const message = useMessage()
const eplusTableRef = ref()
const eplusDialogRef = ref()
const tabNameList = [
  { name: 'all', label: '全部' },
  { name: '0', label: '待提交' },
  { name: '1', label: '待审批' },
  { name: '2', label: '通过' },
  { name: '3', label: '已驳回' },
  { name: '10', label: '已作废' }
]
const activeName = ref('all')
const handleTabsClick = (val) => {
  activeName.value = val.props.name
  handleRefresh()
}
const toOrderNoticeRef = ref()
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: (
        <eplus-custom-select
          api={InvoicingNoticesApi.getcompanySimpleList}
          label="name"
          value="id"
        />
      ),
      name: 'companyId',
      label: '付款单位',
      formatter: async (args: any[]) => {
        let list = await InvoicingNoticesApi.getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '通知编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'shipInvoiceCode',
      label: '发票号'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerId',
      label: '跟单员',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 500, pageNo: 1 }}
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择供应商"
        />
      ),
      name: 'venderCode',
      label: '供应商',
      formatter: async (args: any[]) => {
        const venderList = await getVenderSimpleList({ pageSize: 500, pageNo: 1 })
        const vender = venderList.find((item) => item.code == args[0])
        return vender?.name || ''
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purOrderCode',
      label: '采购合同号'
    },
    {
      component: (
        <eplus-dict-select
          dictType={DICT_TYPE.INVOICE_STATUS}
          clearable
        />
      ),
      name: 'status',
      label: '状态',
      formatter: async (args: any[]) => {
        return getDictLabel(DICT_TYPE.INVOICE_STATUS, args[0])
      }
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'shipDate',
          label: '出运日期',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'inputUserId',
      label: '录入人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await InvoicingNoticesApi.getInvoicingNoticesPage({
      ...ps,
      excludeAuditStatus: activeName.value === 'all' ? [10] : undefined,
      auditStatus: activeName.value === 'all' ? undefined : activeName.value
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async () => {
    // await CustClaimApi.deleteRegistration(id)
  },
  exportListApi: async () => {
    // return await CustClaimApi.exportRegistration(ps)
  },
  selection: false,
  summary: false,
  showTabs: false,
  columns: [
    {
      prop: 'code',
      label: '通知编号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'shipInvoiceCode',
      label: '发票号',
      minWidth: columnWidth.m
    },
    {
      prop: 'companyName',
      label: '付款单位',
      minWidth: columnWidth.l
    },
    {
      prop: 'shipDate',
      label: '出运日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'inputDate',
      label: '录入日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'manager',
      label: '跟单员',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          let { row } = data
          return `${row?.manager?.nickname ? row?.manager?.nickname : ''}`
        }
      }
    },
    {
      prop: 'status',
      label: '状态',
      minWidth: columnWidth.m,
      formatter: (val) => {
        return getDictLabel(DICT_TYPE.INVOICE_STATUS, val.status)
      }
    },
    {
      prop: 'registrationDate',
      label: '登票日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn()
    },
    {
      prop: 'venderName',
      label: '供应商名称',
      minWidth: columnWidth.l
    },
    {
      prop: 'inputUser',
      label: '录入人',
      minWidth: columnWidth.m,
      formatter: (args: any) => {
        return args?.inputUser?.nickname
      }
    },
    {
      prop: 'purOrderCode',
      label: '采购合同号',
      minWidth: columnWidth.l
    },
    {
      prop: 'code',
      label: '外销合同号',
      minWidth: columnWidth.l
    },
    {
      prop: 'printFlag',
      label: '打印状态',
      minWidth: columnWidth.m,
      formatter: (val) => {
        return getDictLabel(DICT_TYPE.PRINT_FLAG, val.printFlag)
      }
    },
    {
      prop: 'printDate',
      label: '打印日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn()
    },
    {
      prop: 'manuallyFlag',
      label: '是否手动生成',
      minWidth: columnWidth.l,
      formatter: (val) => {
        return getDictLabel(DICT_TYPE.CONFIRM_TYPE, val.manuallyFlag)
      }
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
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
                hasPermi="['scm:invoicing-notices:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                auditable={row}
                otherItems={[
                  {
                    isShow: [
                      BpmProcessInstanceResultEnum.UNSUBMITTED.status,
                      BpmProcessInstanceResultEnum.REJECT.status,
                      BpmProcessInstanceResultEnum.CANCEL.status
                    ].includes(row.auditStatus),
                    otherKey: 'scmInvoicingNoticesUpdate',
                    label: '编辑',
                    permi: 'scm:invoicing-notices:update',
                    handler: async (row) => {
                      toOrderNoticeRef.value.handleEdit(row)
                    }
                  },
                  {
                    isShow: ![
                      BpmProcessInstanceResultEnum.PROCESS.status,
                      BpmProcessInstanceResultEnum.CLOSE.status
                    ].includes(row.auditStatus),
                    otherKey: 'scmInvoicingNoticesClose',
                    label: '作废',
                    permi: 'scm:invoicing-notices:close',
                    handler: async (row) => {
                      await handleClose(row)
                    }
                  }
                ]}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    }
  ]
}
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleClose = async (row: any) => {
  await message.confirm('确认作废?')
  await InvoicingNoticesApi.invoicingNoticesClose({ id: row.id })
  handleRefresh()
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style scoped lang="scss"></style>
