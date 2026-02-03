<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        @click="handleCreate()"
        v-hasPermi="['scm:vender-clue:create']"
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
      <VenderDetail
        type="clue"
        title="潜在供应商"
        :id="key"
        @success="detailClose"
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
  </eplus-dialog>

  <eplus-dialog
    ref="toFormalDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
    :destroyOnClose="true"
  >
    <template #create="{ key }">
      <FormalVenderForm
        v-if="rowVenderType === 1"
        channel="clue"
        :id="key"
        mode="create"
        @handle-success="handleRefresh"
      />

      <AdministrationForm
        v-if="rowVenderType === 2"
        channel="clue"
        :id="key"
        mode="create"
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
import FormalVenderForm from '../formal/VenderForm.vue'
import AdministrationForm from '../administration/AdministrationForm.vue'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as VenderApi from '@/api/scm/vender'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const toFormalDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const message = useMessage()

const eplusListRef = ref()
defineOptions({
  name: 'VenderClue'
})

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '供应商编号'
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
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.VENDER_TYPE}></eplus-dict-select>,
      name: 'venderType',
      label: '供应商类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.VENDER_TYPE, args[0])
      }
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
    const res = await VenderApi.getVenderPage({ ...ps, stageType: 1 })
    return {
      list: res.list,
      total: res.total
    }
  },
  exportListApi: async (ps) => {
    return await VenderApi.exportVender({ ...ps, stageType: 1 })
  },
  columns: [
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
    },
    {
      prop: 'venderType',
      label: '供应商类型',
      minWidth: columnWidth.l,
      formatter: formatDictColumn(DICT_TYPE.VENDER_TYPE)
    },
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
      prop: 'factoryAreaName',
      label: '省份城市',
      minWidth: columnWidth.l
    },
    {
      prop: 'phone',
      label: '供应商电话',
      minWidth: columnWidth.l
    },
    {
      prop: 'licenseNo',
      label: '营业执照号',
      minWidth: columnWidth.l
    },
    // {
    //   prop: 'currency',
    //   label: '币种',
    //   width: '100px',
    //   formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE)
    // },
    // {
    //   prop: 'taxRate',
    //   label: '税率%',
    //   minWidth: '120px'
    // },
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
                hasPermi="['scm:vender-clue:detail']"
              >
                详情
              </el-button>
              <eplus-dropdown
                editItem={{
                  permi: 'scm:vender-clue:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: true,
                    label: '转正式',
                    permi: 'scm:vender:convert',
                    otherKey: 'toVender',
                    handler: () => {
                      handleToFormal(row)
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
  ]
}

const handleExport = async () => {
  return await eplusListRef.value.exportList('潜在供应商管理.xlsx')
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '潜在供应商')
}
/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate('潜在供应商')
}

const rowVenderType = ref(1)
const handleToFormal = (row) => {
  rowVenderType.value = row.venderType
  toFormalDialogRef.value?.openCreate(
    `${row.venderType === 1 ? '业务供应商' : '行政供应商'}`,
    row.id
  )
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
const detailClose = () => {
  eplusDialogRef.value?.close()
  handleRefresh()
}

const timestampToTime = (timestamp) => {
  if (timestamp || timestamp > 0) {
    let hours = Math.floor(timestamp / 3600)
    let minutes = Math.floor((timestamp % 3600) / 60)
    // let seconds = Math.floor((timestamp % 60));
    return hours > 0 ? +'小时' : '' + minutes + '分钟'
  }
  return ''
}

onMounted(() => {
  // console.log(timestampToTime(5136))
})

// onActivated(() => {
//   eplusDialogRef.value?.close()
// })
</script>
