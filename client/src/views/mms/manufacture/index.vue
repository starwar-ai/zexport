<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <!-- <el-button
        type="primary"
        plain
        @click="handleCreate()"
        v-hasPermi="['pms:sku:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button> -->
      <!-- <el-button
        type="success"
        plain
        @click="unifyHandle('done-batch')"
        v-hasPermi="['mms:manufacture:done']"
      >
        批量完成
      </el-button> -->
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <manufactureDetail :id="key" /></template>

    <template #edit="{ key }">
      <manufactureForm
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <manufactureForm mode="create" />
    </template>
  </eplus-dialog>

  <CloseDia
    ref="CloseDiaRef"
    @sure="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { formatDictColumn } from '@/utils/table'
import * as ManufactureApi from '@/api/mms/manufacture/index'
// import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import manufactureForm from './manufactureForm.vue'
import manufactureDetail from './manufactureDetail.vue'
import CloseDia from './components/CloseDia.vue'
import { uniq } from 'lodash-es'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()
const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const eplusListRef = ref()
defineOptions({ name: 'MmsManufacture' })

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'name',
      label: '产品名称'
    },
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '仓库名称'
    },
    {
      component: (
        <eplus-dict-select dictType={DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT}></eplus-dict-select>
      ),
      name: 'auditStatus',
      label: '单据状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT, args[0])
      }
    }
  ],
  moreFields: []
}

const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ManufactureApi.getManufacturePage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  selection: true,
  columns: [
    {
      prop: 'code',
      label: '加工单号',
      minWidth: '100px',
      parent: true,
      isHyperlink: true
    },
    {
      prop: 'companyName',
      label: '归属公司',
      parent: true,
      minWidth: '100px'
    },
    {
      prop: 'stockName',
      label: '仓库名称',
      parent: true,
      minWidth: '100px'
    },
    {
      prop: 'custName',
      label: '客户名称',
      parent: true,
      minWidth: '100px'
    },
    {
      prop: 'manufactureStatus',
      label: '状态',
      parent: true,
      formatter: formatDictColumn(DICT_TYPE.MMS_MANUFACTURE_STATUS)
    },
    {
      prop: 'action',
      label: '操作',
      width: '120px',
      fixed: 'right',
      parent: true,
      slots: {
        default: (data) => {
          const { row } = data
          let statusVal = getDictLabel(DICT_TYPE.MMS_MANUFACTURE_STATUS, row.manufactureStatus)
          return (
            <div class="flex items-center justify-between">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['mms:manufacture:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                otherItems={
                  [
                    // {
                    //   isShow: statusVal == '未完成',
                    //   otherKey: 'mmsManufactureUpdate',
                    //   label: '编辑',
                    //   permi: 'mms:manufacture:update',
                    //   handler: async (row) => {
                    //     handleUpdate(row?.id)
                    //   }
                    // },
                    // {
                    //   isShow: statusVal == '未完成',
                    //   otherKey: 'mmsManufactureDone',
                    //   label: '完成',
                    //   permi: 'mms:manufacture:done',
                    //   handler: async (row) => {
                    //     unifyHandle('done', row?.id)
                    //   }
                    // },
                    // {
                    //   isShow: statusVal == '未完成',
                    //   otherKey: 'mmsManufactureFinish',
                    //   label: '作废',
                    //   permi: 'mms:manufacture:finish',
                    //   handler: async (row) => {
                    //     handleFinish('finish', row?.id)
                    //   }
                    // },
                    // {
                    //   isShow: statusVal == '已作废',
                    //   otherKey: 'mmsManufactureRollbackFinish',
                    //   label: '反作废',
                    //   permi: 'mms:manufacture:rollback-finish',
                    //   handler: async (row) => {
                    //     unifyHandle('rollback-finish', row?.id)
                    //   }
                    // }
                  ]
                }
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    },
    {
      prop: 'skuCode',
      label: '产品编号'
    },
    {
      prop: 'skuName',
      label: '产品名称'
    },
    {
      prop: 'quantity',
      label: '加工量'
    },
    {
      prop: 'smsContractCode',
      label: '销售合同编号'
    },
    {
      prop: 'cskuCode',
      label: '客户货号'
    }
  ]
}

// const handleExport = async () => {
//   return await eplusListRef.value.exportList('产品管理.xlsx')
// }

// const handleSubmit = async (data) => {
//   // 提交请求
//   let res = await skuApi.submitSku(data.id)
//   if (res) {
//     message.success('已提交审核！')
//   }
//   await eplusListRef.value.handleRefresh()
// }

// 删除按钮操作
// const handleDelete = async (id: number) => {
//   await eplusListRef.value.delList(id, false)
// }

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}
/// 打开详情
const handleDetail = async (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const unifyHandle = (type, id?) => {
  let ids = uniq(eplusListRef.value?.checkedItems.map((item) => item.parentId))
  if (type === 'done-batch' && ids.length === 0) {
    message.warning('请选择操作数据！')
    return
  }
  let des =
    type === 'rollback-finish'
      ? '确认将选中数据进行反作废操作吗？'
      : '确认将选中数据进行完成操作吗？'
  let params = type === 'done-batch' ? { idList: ids.join(',') } : { id: id }
  ElMessageBox.confirm(des, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      ManufactureApi.handleManufacture(type, params)
    })
    .then(() => {
      message.success('操作成功!')
      handleRefresh()
    })
}

const CloseDiaRef = ref()

const handleFinish = (type, id) => {
  CloseDiaRef.value.open(type, id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
