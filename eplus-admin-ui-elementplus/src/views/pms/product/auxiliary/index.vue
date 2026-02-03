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
        v-hasPermi="['pms:auxiliary-sku:create']"
      >
        新增
      </el-button>
    </template>
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #detail="{ key }"> <auxiliary-detail :id="key" /></template>

    <template #edit="{ key }">
      <auxiliary-form
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <auxiliary-form mode="create" />
    </template>
    <template #change="{ key }">
      <auxiliary-form
        :id="key"
        mode="change"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDictColumn } from '@/utils/table'
import * as skuApi from '@/api/pms/auxiliary/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import AuxiliaryForm from './AuxiliaryForm.vue'
import AuxiliaryDetail from './AuxiliaryDetail.vue'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

// import AuxiliaryChangeForm from '../auxiliaryChange/AuxiliaryChangeForm.vue'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'Auxiliary' })

const handleDialogFailure = () => {}
const exportFileName = ref('包材信息管理.xlsx')

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'name',
      label: '中文品名'
    },
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '产品编号'
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
    }
  ],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await skuApi.getSkuPage({ ...ps, skuType: 4 })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await skuApi.deleteSku(id)
  },
  exportListApi: async (ps) => {
    return await skuApi.exportSku({ ...ps, skuType: 4 })
  },
  columns: [
    {
      label: '图片',
      prop: 'thumbnail',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div class="flex items-center">
              <EplusImgEnlarge
                mainPicture={row.mainPicture}
                thumbnail={row.thumbnail}
                width="40"
                height="40"
              />
              {row.changeStatus === 2 && (
                <el-tag
                  type="warning"
                  size="small"
                  class="ml-2"
                >
                  变更中
                </el-tag>
              )}
            </div>
          )
        }
      }
    },
    {
      prop: 'name',
      label: '中文品名',
      minWidth: columnWidth.m
    },
    {
      prop: 'nameEng',
      label: '英文品名',
      minWidth: columnWidth.m
    },
    {
      prop: 'code',
      label: '产品编号',
      minWidth: columnWidth.m
    },
    {
      prop: 'skuType',
      label: '产品类型',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.SKU_TYPE)
    },
    {
      prop: 'measureUnit',
      label: '计量单位',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
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
                hasPermi="['pms:auxiliary-sku:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  permi: 'pms:auxiliary-sku:submit',
                  handler: async (row) => {
                    handleUpdate(row.id)
                  }
                }}
                editItem={{
                  permi: 'pms:auxiliary-sku:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  permi: 'pms:auxiliary-sku:delete',
                  handler: async (row) => {
                    await handleDelete(row.id)
                  }
                }}
                otherItems={[
                  {
                    isShow: row.changeStatus !== 2 && row.auditStatus == 2 ? true : false,
                    otherKey: 'cskuChange',
                    label: '变更',
                    permi: 'pms:auxiliary-sku:change',
                    // checkAuditStatus: [BpmProcessInstanceResultEnum.APPROVE.status],
                    handler: (row) => {
                      handleChange(row.id)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'managerDel',
                    label: '管理员删除',
                    permi: 'pms:sku:manager-delete',
                    handler: (row) => {
                      handleManagerDel(row.id)
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
  return await eplusListRef.value.exportList('包材管理.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await skuApi.submitSku(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.refresh()
}

const handleManagerDel = async (id: number) => {
  await message.confirm('确定要删除吗？')
  await skuApi.manageDel(id)
  await eplusListRef.value.handleRefresh()
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

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

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const handleChange = (id: number) => {
  eplusDialogRef.value?.openChange(id, '包材管理变更')
}
</script>
