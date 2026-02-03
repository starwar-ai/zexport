<template>
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
  >
    <template #detail="{ key }"> <main-change-detail :id="key" /></template>

    <template #edit="{ key }">
      <main-form
        :id="key"
        mode="edit"
        :changeEdit="true"
      />
    </template>
    <!-- <template #change="{ key }">
      <main-form
        :id="key"
        mode="change"
        :changeEdit="true"
      />
    </template> -->
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as skuApi from '@/api/pms/main/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
// import MainChangeForm from './MainChangeForm.vue'
import MainForm from '../main/MainForm.vue'
import MainChangeDetail from './MainChangeDetail.vue'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { getVenderSimpleList } from '@/api/common'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'MainChange' })

const handleDialogFailure = () => {}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <eplus-sku-code-search></eplus-sku-code-search>,
      name: 'name',
      nameList: 'nameListStr',
      label: '中文品名',
      className: '!w-200px'
    },
    {
      component: <eplus-sku-code-search></eplus-sku-code-search>,
      name: 'code',
      nameList: 'codeListStr',
      label: '产品编号',
      className: '!w-200px'
    },
    {
      component: <el-input></el-input>,
      name: 'nameEng',
      label: '英文名称'
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.IS_INT}></eplus-dict-select>,
      name: 'advantageFlag',
      label: '优势产品',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.IS_INT, args[0])
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.SKU_TYPE}></eplus-dict-select>,
      name: 'skuType',
      label: '产品类型',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.SKU_TYPE, args[0])
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
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1 }}
          keyword="name"
          label="name"
          value="id"
          multiple={true}
          class="!w-100%"
          placeholder="请选择"
          onChangeEmit={(...$event) => getVenderName($event)}
        />
      ),
      name: 'venderIdList',
      label: '供应商',
      formatter: async (args: any[]) => {
        return venderNameStr.value
      }
    }
  ],
  moreFields: []
}

const venderNameStr = ref('')
const getVenderName = (e) => {
  let nameList = []
  e[1].forEach((item) => {
    e[0].forEach((el) => {
      if (item.id === el) {
        nameList.push(item.name)
      }
    })
  })
  venderNameStr.value = nameList.join(',')
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await skuApi.skuChangePage({ ...ps })
    res.list.forEach((e) => {
      e.changeFlag = false
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await skuApi.deleteSku(id)
  },
  exportListApi: async (ps) => {
    return await skuApi.exportSku({ ...ps, custProFlag: 0 })
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
      prop: 'code',
      label: '产品编号',
      minWidth: columnWidth.l,
      notShowChange: true,
      isHyperlink: true
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
      prop: 'material',
      label: '产品材质',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="material"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },

    {
      label: '供应商名称',
      prop: 'venderName',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="venderName"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      label: '采购成本',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="withTaxPrice"
                oldChangeField="oldData"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
      minWidth: columnWidth.m,
      formatter: formatDateColumn('YYYY-MM-DD')
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
                hasPermi="['pms:sku-change:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'pms:sku-change:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'pms:sku-change:update',
                //   handler: () => {
                //     handleUpdate(row.id)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'pms:sku-change:delete',
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

const singleWeight = (row) => {
  let newDefault = row?.singleGrossweight?.weight
  let oldDefault = row?.oldData?.singleGrossweight?.weight
  let unit = row?.singleGrossweight?.unit
  return tagsChange(newDefault, oldDefault, unit)
}

const costPrice = (row) => {
  let newDefault = row?.withTaxPrice?.amount
  let oldDefault = row?.oldData?.withTaxPrice?.amount
  let unit = row?.withTaxPrice?.currency
  return tagsChange(newDefault, oldDefault, unit)
}

const deliveryTime = (row) => {
  let newDefault = row?.delivery
  let oldDefault = row?.oldData?.delivery
  return tagsChange(newDefault, oldDefault, '天')
}

const tagsChange = (newDefault, oldDefault, unit) => {
  let tags = ''
  if (newDefault == oldDefault) {
    tags = newDefault + unit
  } else {
    tags = (
      <>
        {' '}
        <span class="new-text">{newDefault}</span>
        <span class="old-text">{oldDefault}</span>
        <span>{unit}</span>
      </>
    )
  }
  return tags
}

const exportFileName = ref('产品管理.xlsx')
const handleExport = async () => {
  return await eplusListRef.value.exportList('产品管理.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await skuApi.submitSkuChange(data.id)
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
  eplusDialogRef.value?.openEdit(id)
}
/// 打开详情
const handleDetail = async (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
<style lang="scss" scoped>
.new-text {
  color: #f7aa49;
}

.old-text {
  color: #999;
  text-decoration: line-through;
}
</style>
