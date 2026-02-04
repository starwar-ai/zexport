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
        v-hasPermi="['scm:vender:create']"
      >
        {{ `新增${pageTitle}` }}
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
      <VenderDetail
        type="formal"
        :title="pageTitle"
        :id="key"
      />
    </template>

    <template #edit="{ key }">
      <VenderForm
        :id="key"
        mode="edit"
        @handle-success="handleRefresh"
      />
    </template>

    <template #create>
      <VenderForm
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>

    <template #change="{ key }">
      <VenderForm
        :id="key"
        mode="change"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import VenderDetail from '../components/VenderDetail.vue'
import VenderForm from './VenderForm.vue'
import * as UserApi from '@/api/system/user'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as VenderApi from '@/api/scm/vender'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import * as DeptApi from '@/api/system/dept'
import { getSourceId, removeSourceId } from '@/utils/auth'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({
  name: 'VenderFormal'
})

const pagePath = useRoute().path
const pageTitle = ref('正式供应商')
const venderType = ref()
const getPageTitle = () => {
  if (pagePath === '/base/vender/business') {
    pageTitle.value = '业务供应商'
    venderType.value = 1
  } else if (pagePath === '/base/vender/administration') {
    pageTitle.value = '行政供应商'
    venderType.value = 2
  } else {
    pageTitle.value = '正式供应商'
    venderType.value = ''
  }
}
const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '供应商编码'
    },
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
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'buyerIds',
      label: '采购员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择采购员部门"></eplus-dept-select>,
      name: 'buyerDeptId',
      label: '采购员部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    }
  ],
  moreFields: []
}

const tableColumns = reactive([
  {
    prop: 'code',
    label: '供应商编码',
    minWidth: columnWidth.l,
    isHyperlink: true
  },
  {
    prop: 'name',
    label: '供应商名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'nameEng',
    label: '供应商英文名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'nameShort',
    label: '供应商简称',
    minWidth: columnWidth.l
    // formatter: formatDictColumn(DICT_TYPE.LOAN_REPAY_STATUS)
  },
  {
    prop: 'venderType',
    label: '供应商类型',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.VENDER_TYPE)
  },
  {
    prop: 'venderPocName',
    label: '联系人',
    minWidth: columnWidth.m
  },
  {
    prop: 'venderPocPhone',
    label: '联系电话',
    minWidth: columnWidth.m
  },
  {
    prop: 'phone',
    label: '供应商电话',
    minWidth: columnWidth.l
    // formatter: formatDictColumn(DICT_TYPE.LOAN_TRANSFER_STATUS)
  },
  // {
  //   prop: 'countryId',
  //   label: '国家'
  // },
  {
    prop: 'factoryAreaName',
    label: '工厂地址',
    minWidth: columnWidth.xl
  },
  {
    prop: 'licenseNo',
    label: '营业执照号',
    minWidth: columnWidth.l
  },
  {
    prop: 'currency',
    label: '币种',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE)
  },
  // {
  //   prop: 'taxRate',
  //   label: '税率%',
  //   minWidth: '120px'
  // },
  {
    prop: 'buyerList',
    label: '采购员',
    minWidth: columnWidth.m,
    formatter: (row) => {
      return row.buyerList?.map((item) => item.name).join(',')
    }
  },
  {
    prop: 'buyerDeptName',
    label: '采购员部门',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return row.buyerList
        ?.map((item) => item.deptName)
        .filter((deptName) => !!deptName)
        .join(',')
    }
  },
  {
    prop: 'createTime',
    label: '创建时间',
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
    prop: 'auditStatus',
    label: '审核状态',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
  },
  {
    prop: 'enableFlag',
    label: '启用状态',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.ENABLE_FLAG)
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
              hasPermi="['scm:vender:detail']"
            >
              详情
            </el-button>
            <eplus-dropdown
              editItem={{
                permi: 'scm:vender:update',
                handler: () => {
                  handleUpdate(row.id)
                }
              }}
              deleteItem={{
                permi: 'scm:vender:delete',
                handler: async (row) => {
                  await handleDelete(row.id)
                }
              }}
              otherItems={[
                {
                  isShow: row.changeStatus == 2 ? false : true,
                  otherKey: 'venderChange',
                  label: '变更',
                  permi: 'scm:vender:change',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  handler: (row) => {
                    handleChange(row.id)
                  }
                },
                {
                  isShow: row.enableFlag ? false : true,
                  otherKey: 'venderEnable',
                  label: '启用',
                  permi: 'scm:vender:enable',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  handler: async (row) => {
                    ElMessageBox.confirm('是否确认启用该供应商？', '提示', {
                      confirmButtonText: '确认',
                      cancelButtonText: '取消',
                      type: 'warning'
                    })
                      .then(() => {
                        VenderApi.enableVender(row.id).then(() => {
                          message.success('启用成功')
                          handleRefresh()
                        })
                      })
                      .catch(() => {})
                  }
                },
                {
                  isShow: row.enableFlag ? true : false,
                  otherKey: 'venderDisable',
                  label: '停用',
                  permi: 'scm:vender:disable',
                  checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                  handler: async (row) => {
                    ElMessageBox.confirm('是否确认停用该供应商？', '提示', {
                      confirmButtonText: '确认',
                      cancelButtonText: '取消',
                      type: 'warning'
                    })
                      .then(() => {
                        VenderApi.disEnableVender(row.id).then(() => {
                          message.success('停用成功')
                          handleRefresh()
                        })
                      })
                      .catch(() => {})
                  }
                },
                {
                  isShow: true,
                  otherKey: 'venderManagerDelete',
                  label: '管理员删除',
                  permi: 'scm:vender:manager-delete',
                  handler: async () => {
                    await message.confirm('确认进行删除吗？')
                    await VenderApi.manDeleteVender(row.id)
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
])

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    getPageTitle()
    const res = await VenderApi.getVenderPage({ ...ps, stageType: 2, venderType: venderType.value })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await VenderApi.deleteVender(id)
  },
  exportListApi: async (ps) => {
    return await VenderApi.exportVender({ ...ps, stageType: 2, venderType: venderType.value })
  },
  columns: tableColumns
}

const exportFileName = ref(`${pageTitle.value}管理.xlsx`)
const handleExport = async () => {
  return await eplusListRef.value.exportList(`${pageTitle.value}管理.xlsx`)
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await VenderApi.submitVender(data.id)
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
  eplusDialogRef.value?.openEdit(id, pageTitle.value)
}
const handleChange = (id: number) => {
  eplusDialogRef.value?.openChange(id, '供应商变更')
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

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
