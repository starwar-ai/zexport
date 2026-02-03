<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    @emit-open-detail="handleDetail"
  />
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }"> <CustChangeDetail :id="key" /></template>

    <template #edit="{ key }">
      <CustForm
        :id="key"
        mode="edit"
        :changeEdit="true"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import CustChangeDetail from './CustChangeDetail.vue'
// import CustChangeForm from './CustChangeForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as CustApi from '@/api/crm/cust'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { useCountryStore } from '@/store/modules/countrylist'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import CustForm from '../cust/CustForm.vue'

const countryStore = useCountryStore()
const countryData = JSON.parse(JSON.stringify(countryStore.countryList))
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const message = useMessage()
const eplusListRef = ref()
const name = 'CustChange'

defineOptions({
  name
})

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '客户名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'shortname',
      label: '简称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'changeId',
      label: '变更人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser()
        return user.nickname
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

const currencyChange = (row) => {
  let newDefault = row?.currency
  let oldDefault = row?.oldCust?.currency
  return tagsChange(newDefault, oldDefault)
}

const tlementTermType = (row) => {
  let newDefault = row?.settlementTermType
  let oldDefault = row?.oldCust?.settlementTermType
  return tagsChange(newDefault, oldDefault)
}

const defaultChange = (scope) => {
  let newDefault = scope.agentFlag ? '是' : '否'
  let oldDefault = scope?.oldCust?.agentFlag ? '是' : '否'
  return tagsChange(newDefault, oldDefault)
}

const tagsChange = (newDefault, oldDefault) => {
  let tags = ''
  if (newDefault == oldDefault) {
    tags = <el-tag>{newDefault}</el-tag>
  } else {
    tags = (
      <>
        {' '}
        <el-tag type="warning">{newDefault}</el-tag>
        <el-tag
          type="info"
          class="old-tags"
        >
          {oldDefault}
        </el-tag>
      </>
    )
  }
  return tags
}

const counterFormatter = (row, column, __cv) => {
  let result: any = countryData.filter((item) => {
    return item.id === row.countryId
  })
  let oldResult: any = countryData.filter((item) => {
    return item.id === row?.oldCust?.countryId
  })

  let tags = ''
  let oldName = oldResult[0]?.[column.property == 'countryId' ? 'name' : 'regionName']
  if (result[0]?.id == oldResult[0]?.id || oldName == undefined) {
    tags = <el-tag>{result[0]?.[column.property == 'countryId' ? 'name' : 'regionName']}</el-tag>
  } else {
    tags = (
      <>
        {' '}
        <el-tag type="warning">
          {result[0]?.[column.property == 'countryId' ? 'name' : 'regionName']}
        </el-tag>
        <el-tag
          type="info"
          class="old-tags"
        >
          {oldName}
        </el-tag>
      </>
    )
  }
  return tags
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await CustApi.getCustChangePage({ ...ps })

    res.list.forEach((e) => {
      e.changeFlag = false
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await CustApi.deleteChangeCust(id)
  },
  exportListApi: async (ps) => {
    return await CustApi.exportCust({ stageType: 2, ...ps })
  },
  columns: [
    {
      prop: 'code',
      label: '客户编号',
      minWidth: columnWidth.m,
      notShowChange: true,
      isHyperlink: true
    },
    {
      prop: 'name',
      label: '客户名称',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="name"
                oldChangeField="oldCust"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'shortname',
      label: '简称',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="shortname"
                oldChangeField="oldCust"
              />
            </>
          )
        }
      }
    },
    // {
    //   field: 'phone',
    //   label: '联系人电话',
    //   minWidth: '120px',
    //   slots: {
    //     default: (data) => {
    //       const { row } = data
    //       return (
    //         <>
    //           <EplusFieldComparison
    //             item={row}
    //             filed="phone"
    //             oldChangeField="oldCust"
    //           />
    //         </>
    //       )
    //     }
    //   }
    // },
    {
      prop: 'countryId',
      label: '国家/地区',
      formatter: counterFormatter,
      minWidth: columnWidth.m
    },
    {
      prop: 'regionName',
      label: '所属地区',
      formatter: counterFormatter,
      minWidth: columnWidth.m
    },
    {
      prop: 'customerTypes',
      label: '客户类型',
      formatter: formatDictColumn(DICT_TYPE.CUSTOM_TYPE),
      minWidth: columnWidth.m
    },
    // {
    //   field: 'agentFlag',
    //   label: '是否联营',
    //   // formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE),
    //   minWidth: '100px',
    //   slots: {
    //     default: (data) => {
    //       const { row } = data
    //       return defaultChange(row)
    //     }
    //   }
    // },
    {
      prop: 'sourceType',
      label: '客户来源',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.SOURCE_TYPE)
    },
    // {
    //   field: 'currency',
    //   label: '币种',
    //   // formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE),
    //   minWidth: '80px',
    //   slots: {
    //     default: (data) => {
    //       const { row } = data
    //       return currencyChange(row)
    //     }
    //   }
    // },
    // {
    //   field: 'creditFlag',
    //   label: '信用额度',
    //   formatter: (_, __, cv) => {
    //     return <el-tag>{cv ? '开启' : '关闭'}</el-tag>
    //   },
    //   minWidth: '100px'
    // },
    // {
    //   field: 'settlementTermType',
    //   label: '价格条款',
    //   // formatter: formatDictColumn(DICT_TYPE.PRICE_TYPE),
    //   minWidth: '100px',
    //   slots: {
    //     default: (data) => {
    //       const { row } = data
    //       return tlementTermType(row)
    //     }
    //   }
    // },
    // {
    //   field: 'email',
    //   label: '联系人邮箱',
    //   minWidth: '180px',
    //   slots: {
    //     default: (data) => {
    //       const { row } = data
    //       return (
    //         <>
    //           <EplusFieldComparison
    //             item={row}
    //             filed="email"
    //             oldChangeField="oldCust"
    //           />
    //         </>
    //       )
    //     }
    //   }
    // },
    {
      prop: 'auditStatus',
      label: '审核状态',
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
      minWidth: columnWidth.m
    },

    {
      prop: 'enableFlag',
      label: '启用状态',
      formatter: formatDictColumn(DICT_TYPE.ENABLE_FLAG),
      minWidth: columnWidth.m
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
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
                hasPermi="['crm:cust-change:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'crm:cust-change:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'crm:cust-change:update',
                //   handler: () => {
                //     handleUpdate(row.id)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'crm:cust-change:delete',
                //   handler: async (row) => {
                //     await handleDelete(row.id).then(() => {
                //       {
                //         /* api.getCustPage({page}) */
                //       }
                //     })
                //   }
                // }}
                otherItems={[]}
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    }
  ]
}
const handleExport = async () => {
  return await eplusListRef.value.exportList('变更客户管理.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await CustApi.changeSubmit(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '客户')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

onMounted(() => {})

// onActivated(() => {
//   eplusDialogRef.value?.close()
// })
</script>
<style>
.old-tags {
  text-decoration: line-through;
}
</style>
