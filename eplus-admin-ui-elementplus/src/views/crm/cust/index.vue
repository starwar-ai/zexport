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
      <!-- <div
        style="font-size: small"
        @click="handleCreate()"
      >
        发现暂存数据，点击恢复！
      </div> -->
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #detail="{ key }"> <CustDetail :id="key" /></template>

    <template #edit="{ key }">
      <CustForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #change="{ key }">
      <CustForm
        :id="key"
        mode="change"
        :changeEdit="false"
      />
    </template>

    <template #create>
      <CustForm mode="create" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import CustDetail from './CustDetail.vue'
import CustForm from './CustForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as CustApi from '@/api/crm/cust'
import * as UserApi from '@/api/system/user'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { useCountryStore } from '@/store/modules/countrylist'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import * as DeptApi from '@/api/system/dept'
import { getSourceId, removeSourceId } from '@/utils/auth'
import { EplusCountrySelect } from '@/components/EplusSelect'

const countryStore = useCountryStore()
const countryData = JSON.parse(JSON.stringify(countryStore.countryList))
const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const message = useMessage()
const eplusListRef = ref()
const exportFileName = ref('正式客户管理.xlsx')

defineOptions({
  name: 'FormalCust'
})

const handleDialogFailure = () => {}
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
          class="!w-100%"
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
  return result[0]?.[column.property == 'countryId' ? 'name' : 'regionName']
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await CustApi.getCustPage({ stageType: 2, ...ps })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await CustApi.deleteCust(id)
  },
  exportListApi: async (ps) => {
    return await CustApi.exportCust({ stageType: 2, ...ps })
  },
  columns: [
    {
      prop: 'code',
      label: '客户编号',
      minWidth: columnWidth.l,
      isHyperlink: true
      // slots: {
      //   default: (data) => {
      //     const { row } = data
      //     return (
      //       <div>
      //         <span>{row?.code}</span>
      //         {row?.changeStatus === 2 && (
      //           <el-tag
      //             type="warning"
      //             effect="plain"
      //             size="small"
      //             class="ml-2"
      //           >
      //             变更中
      //           </el-tag>
      //         )}
      //       </div>
      //     )
      //   }
      // }
    },
    {
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
                hasPermi="['crm:cust:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  permi: 'crm:cust:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  permi: 'crm:cust:delete',
                  handler: async (row) => {
                    await handleDelete(row.id).then(() => {
                      {
                        /* api.getCustPage({page}) */
                      }
                    })
                  }
                }}
                otherItems={[
                  {
                    isShow: row.changeStatus == 2 ? false : true,
                    otherKey: 'custChange',
                    label: '变更',
                    permi: 'crm:cust:change',
                    checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    handler: (row) => {
                      handleChange(row.id)
                    }
                  },
                  {
                    isShow: row.enableFlag ? false : true,
                    otherKey: 'custEnable',
                    label: '启用',
                    permi: 'crm:cust:enable',
                    checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    handler: async (row) => {
                      ElMessageBox.confirm('是否确认启用该客户？', '提示', {
                        confirmButtonText: '确认',
                        cancelButtonText: '取消',
                        type: 'warning'
                      })
                        .then(() => {
                          CustApi.enableCust(row.id).then(() => {
                            message.success('启用成功')
                            handleRefresh()
                          })
                        })
                        .catch(() => {})
                    }
                  },
                  {
                    isShow: row.enableFlag ? true : false,
                    otherKey: 'custDisable',
                    label: '停用',
                    permi: 'crm:cust:disable',
                    checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    handler: async (row) => {
                      ElMessageBox.confirm('是否确认停用该客户？', '提示', {
                        confirmButtonText: '确认',
                        cancelButtonText: '取消',
                        type: 'warning'
                      })
                        .then(() => {
                          CustApi.disEnableCust(row.id).then(() => {
                            message.success('停用成功')
                            handleRefresh()
                          })
                        })
                        .catch(() => {})
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
  return await eplusListRef.value.exportList('正式客户管理.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await CustApi.submitCust(data.id)
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

const handleChange = (id: number) => {
  eplusDialogRef.value?.openChange(id, '客户')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('客户')
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const pagePath = useRoute().path
onMounted(() => {
  var localData = localStorage.getItem(pagePath)
  if (localData) {
  }
})

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
