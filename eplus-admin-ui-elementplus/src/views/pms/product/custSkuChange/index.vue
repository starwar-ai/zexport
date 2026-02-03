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
    <template #detail="{ key }"> <custSkuChangeDetail :id="key" /></template>

    <template #edit="{ key }">
      <custProductForm
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
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as skuApi from '@/api/pms/main/index'
import * as custSkuApi from '@/api/pms/cust/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import custSkuChangeDetail from './custSkuChangeDetail.vue'
import { LengthUnit } from '@/utils/config'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import custProductForm from '../cust/custProductForm.vue'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { getVenderSimpleList } from '@/api/common'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'CustSkuChange' })

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
    const res = await custSkuApi.getCustSkuChangePage({ ...ps })
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
    return await custSkuApi.exportCustSku({ ...ps, custProFlag: 1 })
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
      prop: 'custName',
      label: '客户名称',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="custName"
                oldChangeField="oldCsku"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'cskuCode',
      label: '客户货号',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="cskuCode"
                oldChangeField="oldCsku"
              />
            </>
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
                oldChangeField="oldCsku"
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
                oldChangeField="oldCsku"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'totalAmountList',
      label: '单品规格/净重',
      minWidth: '180px',
      slots: {
        default: (data) => {
          const { row } = data
          if (row.singleNetweight.weight) {
            return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/${row.singleNetweight.weight} ${row.singleNetweight.unit}`
          } else {
            return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/-`
          }
        }
      }
    },
    {
      prop: 'venderName',
      label: '供应商',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <>
              <EplusFieldComparison
                item={row}
                filed="venderName"
                oldChangeField="oldCsku"
              />
            </>
          )
        }
      }
    },
    {
      prop: 'delivery',
      label: '采购交期',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return row.delivery ? `${row.delivery}天` : '-'
        }
      }
    },
    {
      label: '采购成本',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          return `${row.unitPrice} ${row.currency}`
        }
      }
    },
    {
      label: '单品毛重',
      minWidth: columnWidth.m,
      slots: {
        default: (data) => {
          const { row } = data
          if (row.singleGrossweight) {
            return `${row.singleGrossweight.weight} ${row.singleGrossweight.unit}`
          } else {
            return '-'
          }
        }
      }
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      minWidth: columnWidth.m,
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      prop: 'createTime',
      label: '变更申请时间',
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'action',
      label: '操作',
      width: '120px',
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
                hasPermi="['pms:csku:query']"
              >
                详情
              </el-button>
              <eplus-dropdown
                // 2025-2-5 bug#1049  让注释掉
                // submitItem={{
                //   permi: 'pms:csku:submit',
                //   handler: async (row) => {
                //     await handleSubmit(row)
                //   }
                // }}
                // editItem={{
                //   permi: 'pms:csku:update',
                //   handler: () => {
                //     handleUpdate(row.id)
                //   }
                // }}
                // deleteItem={{
                //   permi: 'pms:csku:delete',
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

const exportFileName = ref('客户产品.xlsx')

const handleExport = async () => {
  return await eplusListRef.value.exportList('客户产品.xlsx')
}

const handleSubmit = async (data) => {
  // 提交请求
  let res = await custSkuApi.submitCskuChange(data.id)
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
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
