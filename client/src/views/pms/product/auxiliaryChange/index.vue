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
  >
    <template #detail="{ key }"> <AuxiliaryChangeDetail :id="key" /></template>

    <template #edit="{ key }">
      <AuxiliaryForm
        :id="key"
        mode="edit"
        :changeEdit="true"
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
// import AuxiliaryChangeForm from './AuxiliaryChangeForm.vue'
import AuxiliaryChangeDetail from './AuxiliaryChangeDetail.vue'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import AuxiliaryForm from '../auxiliary/AuxiliaryForm.vue'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'AuxiliaryChange' })

const handleDialogFailure = () => {}

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
    const res = await skuApi.getSkuChangePage({ ...ps, skuType: 4 })
    res.list.forEach((e) => {
      e.changeFlag = false
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await skuApi.deleteChangeSku(id)
  },
  exportListApi: async (ps) => {
    return await skuApi.exportSku({ ...ps, skuType: 4 })
  },
  columns: [
    {
      label: '图片',
      prop: 'thumbnail',
      minWidth: columnWidth.s,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <EplusImgEnlarge
              mainPicture={row.mainPicture}
              thumbnail={row.thumbnail}
              width="40"
              height="40"
            />
          )
        }
      }
    },
    {
      prop: 'name',
      label: '中文品名',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="name"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'nameEng',
      label: '英文品名',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="nameEng"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
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
            <div class="flex items-center justify-between">
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
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'pms:auxiliary-sku:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'pms:auxiliary-sku:update',
                //   handler: () => {
                //     handleUpdate(row.id)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'pms:auxiliary-sku:delete',
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

const handleExport = async () => {
  return await eplusListRef.value.exportList('辅料管理.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await skuApi.submitSkuChange(data.id)
  if (res) {
    message.success('已提交审核！')
  }
  await eplusListRef.value.refresh()
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
  await eplusListRef.value.refresh()
}
</script>
