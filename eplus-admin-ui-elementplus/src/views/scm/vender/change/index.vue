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
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  />

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <VenderChangeDetail
        :title="pageTitle"
        :id="key"
      />
    </template>

    <template #edit="{ key }">
      <VenderForm
        :id="key"
        mode="edit"
        :changeEdit="true"
        @handle-success="handleRefresh"
      />
    </template>
    <template #change="{ key }">
      <AdministrationForm
        :id="key"
        mode="change"
        :changeEdit="true"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import VenderChangeDetail from '../components/VenderChangeDetail.vue'
import VenderForm from '../formal/VenderForm.vue'
import AdministrationForm from '../administration/AdministrationForm.vue'

import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as VenderApi from '@/api/scm/vender'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({
  name: 'VenderChange'
})
const activeName = ref('0')

const tabNameList = [
  { name: undefined, label: '全部' },
  { name: '1', label: '待审批' },
  { name: '2', label: '已处理' },
  { name: '3', label: '已驳回' }
]

const pagePath = useRoute().path
const pageTitle = ref('供应商变更')
const venderType = ref()

const handleTabsClick = (val) => {
  eplusListRef.value.handleSearch(val.index === '0' ? {} : { auditStatus: val.props.name })
}
const getPageTitle = () => {
  // if (pagePath === '/scm/vender/business') {
  //   pageTitle.value = '业务供应商'
  //   venderType.value = 1
  // } else if (pagePath === '/scm/vender/administration') {
  //   pageTitle.value = '行政供应商'
  //   venderType.value = 2
  // } else {
  //   pageTitle.value = '正式供应商'
  //   venderType.value = ''
  // }
}
const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '供应商名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'nameShort',
      label: '简称'
    },
    // {
    //   component: <eplus-dict-select dictType={DICT_TYPE.CONFIRM_TYPE}></eplus-dict-select>,
    //   name: 'abroadFlag',
    //   label: '是否境外供应商',
    //   formatter: (args: any[]) => {
    //     return getDictLabel(DICT_TYPE.CONFIRM_TYPE, args[0])
    //   }
    // },
    {
      component: (
        <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}></eplus-dict-select>
      ),
      name: 'auditStatus',
      label: '审核状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.ENABLE_FLAG}></eplus-dict-select>,
      name: 'enableFlag',
      label: '启用状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.ENABLE_FLAG, args[0])
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

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    // getPageTitle()
    const res = await VenderApi.changeVenderPage({
      ...ps,
      stageType: 2,
      venderType: venderType.value
    })
    res.list.forEach((e) => {
      e.changeFlag = false
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await VenderApi.deleteChangeVender(id)
  },
  exportListApi: async (ps) => {
    return await VenderApi.exportVender({ ...ps, stageType: 2, venderType: venderType.value })
  },
  columns: [
    {
      prop: 'code',
      label: '供应商编码',
      minWidth: columnWidth.m,
      notShowChange: true,
      isHyperlink: true
    },
    {
      prop: 'name',
      label: '供应商名称',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="name"
                oldChangeField="oldVender"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'nameEng',
      label: '供应商英文名称',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="nameEng"
                oldChangeField="oldVender"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'nameShort',
      label: '供应商简称',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="nameShort"
                oldChangeField="oldVender"
              />
            </>
          )
        }
      }
      // formatter: formatDictColumn(DICT_TYPE.LOAN_REPAY_STATUS)
    },
    // {
    //   prop: 'countryId',
    //   label: '国家'
    // },

    {
      prop: 'phone',
      label: '供应商电话',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="phone"
                oldChangeField="oldVender"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'companyAreaName',
      label: '工厂地址',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="companyAreaName"
                oldChangeField="oldVender"
              />
            </>
          )
        }
      }
    },

    {
      prop: 'licenseNo',
      label: '营业执照号',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="licenseNo"
                oldChangeField="oldVender"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'currency',
      label: '币种',
      minWidth: columnWidth.m,
      // formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE)

      slots: {
        default: (data) => {
          const { row } = data
          return currencyChange(row)
        }
      }
    },

    {
      prop: 'createTime',
      label: '变更申请时间',
      formatter: formatDateColumn(),
      minWidth: columnWidth.l
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'enableFlag',
      label: '启用状态',
      minWidth: columnWidth.m,
      // formatter: formatDictColumn(DICT_TYPE.ENABLE_FLAG)
      slots: {
        default: (data) => {
          const { row } = data
          return enableChange(row)
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
          return (
            <div class="flex items-center">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['scm:vender-change:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'scm:vender-change:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'scm:vender-change:update',
                //   handler: () => {
                //     handleUpdate(row)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'scm:vender-change:delete',
                //   handler: async (row) => {
                //     await handleDelete(row.id)
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

const enableChange = (row) => {
  let newDefault = row?.enableFlag == 1 ? '启用' : '停用'
  let oldDefault = row?.oldVender?.enableFlag == 1 ? '启用' : '停用'
  return tagsChange(newDefault, oldDefault)
}

const currencyChange = (row) => {
  let newDefault = row?.currency
  let oldDefault = row?.oldVender?.currency
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
        <el-tag>{newDefault}</el-tag>
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

const exportFileName = ref(`${pageTitle.value}管理.xlsx`)
const handleExport = async () => {
  return await eplusListRef.value.exportList(`${pageTitle.value}管理.xlsx`)
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await VenderApi.changeVenderSubmit(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (row) => {
  row.venderType == 1
    ? eplusDialogRef.value?.openEdit(row.id, '业务供应商变更')
    : eplusDialogRef.value?.openChange(row.id, '行政供应商变更')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate(pageTitle.value)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

onMounted(() => {
  getPageTitle()
})

// onActivated(() => {
//   eplusDialogRef.value?.close()
//   getPageTitle()
// })
</script>
<style>
.old-tags {
  text-decoration: line-through;
}
</style>
