<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :key="tableKey"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        v-hasPermi="['dtms:design:create']"
        @click="handleClaim(1)"
        >认领
      </el-button>
    </template>
  </eplus-table>

  <DesignTaskDialog
    ref="designTaskDialogRef"
    :key="Date.now()"
    @handle-success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as DesignTaskApi from '@/api/dtms/designTask' // 设计任务单接口集合
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import DesignTaskDialog from './components/DesignTaskDialog.vue' // 弹框
import { useUserStore } from '@/store/modules/user'
import * as UserApi from '@/api/system/user'
// 打开详情
import { setSourceId } from '@/utils/auth'

defineOptions({ name: 'DesignTaskClaim' })

const eplusListRef = ref()

const { push } = useRouter()
const tableKey = ref('0')

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const message = useMessage()

// 完成提示/任务进度/作废/评价
const designTaskDialogRef = ref()
//

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '设计名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '设计任务单'
    },

    {
      component: <eplus-dict-select dictType={DICT_TYPE.APPROVED_TYPE}></eplus-dict-select>,
      name: 'specialPermissionFlag',
      label: '紧急程度'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'inspectorId',
      label: '设计师',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '销售合同'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}

// 认领/变更认领人
const handleClaim = (type, row?) => {
  let userId = useUserStore().getUser.id + ''
  if (row) {
    let specificDesignerIdArr = row.specificDesigners?.map((it) => {
      return it.userId.toString()
    })
    if (specificDesignerIdArr?.length > 0 && !specificDesignerIdArr.includes(userId.toString())) {
      message.error(`该条任务单有指定认领人，您不可认领！`)
      return false
    }
    let designerIdArr = row.designerIds?.split(',')
    if (designerIdArr != undefined && designerIdArr.includes(userId)) {
      message.error(`该条任务单您已经认领，不能再次认领！`)
      return false
    }
    designTaskDialogRef.value?.open(type, [row])
  } else {
    let checkeArr = eplusListRef.value.checkedItems
    if (checkeArr.length) {
      for (let i = 0; i < checkeArr.length; i++) {
        let specificDesignerIdArr = checkeArr[i].specificDesigners?.map((it) => {
          return it.userId.toString()
        })
        if (
          specificDesignerIdArr?.length > 0 &&
          !specificDesignerIdArr.includes(userId.toString())
        ) {
          message.error(`已勾选的第${i + 1}条任务单有指定认领人，您不可认领！`)
          return false
        }
        let designerIdArr = checkeArr[i].designerIds?.split(',')
        if (designerIdArr != undefined && designerIdArr.includes(userId)) {
          message.error(`已勾选的第${i + 1}条任务单您已经认领，不能再次认领！`)
          return false
        }
      }
      designTaskDialogRef.value?.open(type, eplusListRef.value.checkedItems)
    } else {
      message.error('请勾选任务单')
    }
  }
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    let params = { ...ps, designStatus: 3 }
    const res = await DesignTaskApi.getDesignTaskClaimPage({ ...params })
    res.list.forEach((e) => {
      // 指定认领人
      if (e.specificDesigners?.length > 0) {
        let name = ''
        e.specificDesigners.forEach((sd) => {
          if (name == '') {
            name = sd.nickname
          } else {
            name = name + ',' + sd.nickname
          }
        })
        e.specificDesignerNames = name
      }
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  selection: true,
  columns: [
    {
      prop: 'code',
      label: '设计任务单号',
      minWidth: columnWidth.m,
      isHyperlink: true
    },
    {
      prop: 'designStatus',
      label: '任务状态',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.DESIGN_STATUS, row?.designStatus)
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'name',
      label: '设计名称',
      minWidth: columnWidth.m
    },

    {
      prop: 'specialPermissionFlag',
      label: '紧急程度',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.APPROVED_TYPE)
    },
    {
      prop: 'specialPermissionReason',
      label: '特批原因',
      minWidth: columnWidth.m
    },

    {
      prop: 'designType',
      label: '任务类型',
      minWidth: columnWidth.m,
      formatter: (row) => {
        let data = row?.designType.split(',')
        let str = ''
        data.forEach((e) => {
          str += getDictLabel(DICT_TYPE.DESIGN_TYPE, e) + ' '
        })
        return str
      }
    },
    {
      prop: 'code',
      label: '进度',
      minWidth: columnWidth.m
    },
    {
      prop: 'expectCompleteDate',
      label: '希望完成日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'completeDate',
      label: '实际完成日期',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'specificDesignerNames',
      label: '指定设计师',
      minWidth: columnWidth.m
    },
    {
      prop: 'applyDesignerName',
      label: '申请人',
      minWidth: columnWidth.m
    },
    {
      prop: 'designerNames',
      label: '设计师',
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
          let userId = useUserStore().getUser.id + ''
          if (row.designerIds && row.designerIds.indexOf(userId) >= 0) {
            return (
              <div class="flex items-center">
                <el-button
                  link
                  type="primary"
                  onClick={() => {
                    handleCancle(row.id)
                  }}
                >
                  取消认领
                </el-button>
              </div>
            )
          } else {
            return (
              <div class="flex items-center">
                <el-button
                  link
                  type="primary"
                  onClick={() => {
                    handleClaim(1, row)
                  }}
                >
                  认领
                </el-button>
              </div>
            )
          }
        }
      }
    }
  ]
}

const handleDetail = (id) => {
  setSourceId(id)
  push({ path: '/oa/design-manage/designTask' })
}

const handleCancle = async (id: number) => {
  try {
    ElMessageBox.confirm('确认取消认领该任务单么？', '提示', {
      cancelButtonText: '取消',
      confirmButtonText: '确认',
      type: 'warning'
    })
      .then(async () => {
        await DesignTaskApi.cancleClaim(id)
        message.success('操作成功')
        handleRefresh()
      })
      .catch(() => {})
  } catch {}
}
</script>
