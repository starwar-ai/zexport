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
    <template #detail="{ key }"> <AgentChangeDetail :id="key" /></template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as agentApi from '@/api/pms/agent/index'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import AgentChangeDetail from './AgentChangeDetail.vue'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { getVenderSimpleList } from '@/api/common'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'AgentSkuChange' })

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
    const res = await agentApi.agentSkuChangePage({ ...ps })
    res.list.forEach((e) => {
      e.changeFlag = false
    })
    return {
      list: res.list,
      total: res.total
    }
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
      prop: 'code',
      label: '产品编号',
      minWidth: columnWidth.xl,
      notShowChange: true,
      isHyperlink: true
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
                hasPermi="['pms:agent-sku-change:detail']"
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

const exportFileName = ref('')

/// 打开详情
const handleDetail = async (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
