<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        @click="handleCreate()"
        v-hasPermi="['crm:cust:create']"
      >
        新增
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <ClueDetail
        :id="key"
        @success="detailClose"
    /></template>

    <template #edit="{ key }">
      <ClueForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #create>
      <ClueForm mode="create" />
    </template>
  </eplus-dialog>
  <eplus-dialog
    ref="custDialogRef"
    @success="handleRefresh"
  >
    <template #edit="{ key }">
      <CustForm
        channel="clue"
        mode="edit"
        :id="key"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import ClueDetail from './ClueDetail.vue'
import ClueForm from './ClueForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as CustApi from '@/api/crm/cust'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { useCountryStore } from '@/store/modules/countrylist'
import CustForm from '../cust/CustForm.vue'
import * as UserApi from '@/api/system/user'
import { EplusCountrySelect } from '@/components/EplusSelect'
import * as DeptApi from '@/api/system/dept'

const countryStore = useCountryStore()
const countryData = JSON.parse(JSON.stringify(countryStore.countryList))
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const eplusListRef = ref()
const custDialogRef = ref()
const message = useMessage()

defineOptions({
  name: 'ClueCust'
})

const handleDialogFailure = () => {}
const exportFileName = ref('潜在客户管理.xlsx')

const searchCountryName = ref()
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '客户名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '客户编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'shortname',
      label: '简称'
    },
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
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerIds',
      label: '业务员',
      formatter: async (args: any[]) => {
        let user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择业务员部门"></eplus-dept-select>,
      name: 'managerDeptId',
      label: '业务员部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: (
        <EplusCountrySelect
          onChange={(item) => {
            searchCountryName.value = item.name
          }}
        />
      ),
      name: 'countryId',
      label: '国家/地区',
      formatter: () => {
        return searchCountryName.value
      }
    },
    {
      name: 'sourceType',
      label: '客户来源',
      component: <eplus-dict-select dictType={DICT_TYPE.SOURCE_TYPE}></eplus-dict-select>,
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.SOURCE_TYPE, args[0])
      }
    }
  ],
  moreFields: []
}

const counterFormatter = (row, column, __cv) => {
  let result: any = countryData.filter((item) => {
    return item.id === row.countryId
  })
  return <el-tag>{result[0]?.[column.property == 'countryId' ? 'name' : 'regionName']}</el-tag>
}
//转正式按钮 逻辑
const handleToCust = (id: number) => {
  custDialogRef.value?.openEdit(id, '转正式客户')
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await CustApi.getCustPage({ stageType: 1, ...ps })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await CustApi.deleteCust(id)
  },
  exportListApi: async (ps) => {
    return await CustApi.exportCust({ stageType: 1, ...ps })
  },
  columns: [
    {
      prop: 'code',
      label: '客户编号',
      minWidth: columnWidth.m
    },
    {
      field: 'name',
      prop: 'name',
      label: '客户名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'shortname',
      label: '简称',
      minWidth: columnWidth.m
    },
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
      prop: 'customerTypesName',
      label: '客户类型',
      minWidth: columnWidth.l,
      showOverflowTooltip: true
    },
    {
      prop: 'sourceType',
      label: '客户来源',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.SOURCE_TYPE)
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
      minWidth: columnWidth.m
    },
    {
      prop: 'managerList',
      label: '业务员',
      formatter: (row) => {
        return row.managerList?.map((item) => item.name).join(',')
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'managerDeptName',
      label: '业务员部门',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return row.managerList
          ?.map((item) => item.deptName)
          .filter((deptName) => !!deptName)
          .join(',')
      }
    },
    {
      prop: 'enableFlag',
      label: '启用状态',
      formatter: formatDictColumn(DICT_TYPE.ENABLE_FLAG),
      minWidth: columnWidth.m
    },
    {
      prop: 'createTime',
      label: '录入时间',
      formatter: formatDateColumn(),
      minWidth: columnWidth.m
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      formatter: formatDateColumn(),
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
            <div class="flex items-center justify-between">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['crm:clue:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  permi: 'crm:clue:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: true,
                    label: '转正式',
                    permi: 'crm:cust:create',
                    otherKey: 'clue',
                    handler: () => {
                      handleToCust(row.id)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'custManagerDelete',
                    label: '管理员删除',
                    permi: 'crm:cust:manager-delete',
                    handler: async () => {
                      await message.confirm('确认进行删除吗？')
                      await CustApi.manDeleteCust(row.id)
                      message.success('删除成功')
                      handleRefresh()
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

// const handleSubmit = async (data) => {
//   // 提交请求
//   let res = await CustApi.submitCust(data.id)
//   if (res) {
//     message.success('已提交审核！')
//   }
//   await eplusListRef.value.refresh()
// }
// 删除按钮操作
// const handleDelete = async (id: number) => {
//   await eplusListRef.value.delList(id, false)
// }
const router = useRouter()
const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '潜在客户')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
  // router.push({
  //   path: `/base/cust/clue-info`,
  //   query: {
  //     id
  //   }
  // })
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('潜在客户')
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const detailClose = () => {
  eplusDialogRef.value?.close()
  handleRefresh()
}
onMounted(() => {})

// onActivated(() => {
//   eplusDialogRef.value?.close()
// })
</script>
