<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tabs> </template>
    <template #tableActions>&nbsp;</template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }">
      <Detail :id="key" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as changeApi from '@/api/scm/auxiliaryPurchaseContract'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import Detail from './detail.vue'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'AuxiliaryPurchaseChange' })
const eplusListRef = ref()

const exportFileName = ref()

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'creator',
      label: '创建人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '变更单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'contractCode',
      label: '包材采购合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'venderName',
      label: '供应商名称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaseUserId',
      label: '采购员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select></eplus-dept-select>,
      name: 'purchaseDeptId',
      label: '部门',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const dept = await DeptApi.getSimpleDept(args[0])
          return dept.name
        }
        return ''
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '包材产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'auxiliarySaleContractCode',
      label: '关联销售合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'auxiliaryPurchaseContractCode',
      label: '关联采购合同号'
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await changeApi.auxiliaryChangeList({ ...ps })
    let resultList = res.list?.map((item) => {
      item?.children
        ?.filter((item1) => item1?.changeFlag === 3)
        .forEach((item2) => {
          item2.oldChildren = item.oldData?.children.filter((item3) => item3?.id === item2?.id)
          return item2
        })
      return item
    })
    return {
      list: resultList,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await changeApi.deleteChange(id)
  },
  //**是否包含多个表格显示 */
  showTabs: false,
  oldChangeField: 'oldData',
  columns: [
    {
      prop: 'code',
      label: '变更单号',
      minWidth: columnWidth.m,
      parent: true,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '采购主体',
      parent: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'venderName',
      label: '供应商名称',
      minWidth: columnWidth.m,
      parent: true,
      formatter: (row) => {
        return (
          <>
            <EplusFieldComparison
              filed="venderName"
              oldChangeField="oldData"
              item={row}
            />
          </>
        )
      }
    },
    {
      prop: 'creatorName',
      label: '创建人',
      minWidth: columnWidth.m,
      parent: true,
      slots: {
        default: (row) => {
          return row.createUser?.nickname
        }
      }
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
      minWidth: columnWidth.m,
      parent: true,
      formatter: formatDateColumn()
    },

    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      parent: true,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'mainPicture',
      label: '图片',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.mainPicture && row.changeFlag !== 4) {
            return (
              <EplusImgEnlarge
                mainPicture={row.mainPicture}
                thumbnail={row?.thumbnail}
              />
            )
          } else {
            return '-'
          }
        }
      }
    },
    {
      prop: 'skuCode',
      label: '产品编号',
      isCopy: true,
      minWidth: columnWidth.m
    },
    {
      prop: 'purchaseType',
      label: '采购类型',
      minWidth: columnWidth.m,
      isShowChange: true,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.PURCHASE_TYPE, row)
      }
    },
    {
      prop: 'quantity',
      label: '采购数量',
      summary: true,
      minWidth: columnWidth.m,
      isShowChange: true
    },
    {
      prop: 'withTaxPrice',
      label: '采购单价',
      minWidth: columnWidth.m,
      summary: true,
      isShowChange: true
    },
    {
      prop: 'specRemark',
      label: '规格描述',
      minWidth: columnWidth.l,
      isShowChange: true
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: columnWidth.l,
      fixed: 'right',
      parent: true,
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
                hasPermi="scm:purchase-contract-change:detail"
              >
                详情
              </el-button>
            </div>
          )
        }
      }
    }
  ]
}
const message = useMessage()
const eplusDialogRef = ref()
const handleRefresh = () => {}
const handleDialogFailure = () => {}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}
const handleSubmit = async (data) => {
  // 提交请求
  let res = await changeApi.submitChange(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.handleRefresh()
}

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
