<template>
  <eplus-list
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
  >
    <!-- v-hasPermi="['oa:travel-reimb:create']" -->
    <template #tableActions>
      <el-button
        type="primary"
        plain
        @click="handleCreate()"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
      <el-button
        type="primary"
        plain
        @click="handleExport()"
        v-hasPermi="['oa:travel-app:export']"
      >
        <Icon
          icon="ep:download"
          class="mr-5px"
        />
        导出
      </el-button>
    </template>
  </eplus-list>

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #create>
      <travel-app-form
        mode="create"
        @handleSuccess="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictOptions } from '@/utils/dict'
import { EplusSearchSchema, EplusTableSchema } from '@/types/eplus'
import * as TravelApi from '@/api/oa/travelapp'
import { formatDateColumn, formatDictColumn } from '@/utils/table'
import { useCountryStore } from '@/store/modules/countrylist'
import TravelAppForm from './TravelAppForm.vue'

defineOptions({ name: 'TravelApp' })

const eplusListRef = ref()

// 出差地点 模糊搜索
const countryListData = useCountryStore()
const countryData = JSON.parse(JSON.stringify(countryListData.countryList))
const queryCountry = (queryString: string, cb: any) => {
  const results = queryString ? countryData.filter(createFilter(queryString)) : countryData
  // call callback function to return suggestions
  cb(
    results.map((r: any) => {
      return { ...r, value: r.name }
    })
  )
}

const eplusSearchSchema: EplusSearchSchema = {
  searchSchemas: [
    {
      field: 'purpose',
      label: '出差事由',
      component: 'Input',
      componentProps: {
        class: '!w-240px'
      }
    },
    {
      field: 'dest',
      label: '出差地点',
      component: 'Autocomplete',
      componentProps: {
        class: '!w-240px',
        fetchSuggestions: queryCountry
      }
    },
    {
      field: 'transportationType',
      label: '交通方式',
      component: 'Select',
      componentProps: {
        class: '!w-240px',
        options: getDictOptions(DICT_TYPE.OA_TRANSPORTATION_TYPE)
      },
      isTag: true
    },
    {
      field: 'result',
      label: '结果',
      component: 'Select',
      componentProps: {
        class: '!w-240px',
        //TODO 出差单结果未设置字典，应该同审核状态
        options: getDictOptions(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
        placeholder: '审核状态'
      },
      isTag: true
    },
    {
      field: 'startTime',
      label: '开始时间',
      component: 'DatePicker',
      componentProps: {
        class: '!w-220px',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        type: 'daterange',
        placeholder: '开始时间'
      },
      isTag: true
    },
    {
      field: 'endTime',
      label: '结束时间',
      component: 'DatePicker',
      componentProps: {
        class: '!w-220px',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        type: 'daterange',
        placeholder: '结束时间'
      },
      isTag: true
    },
    {
      field: 'createTime',
      label: '创建时间',
      component: 'DatePicker',
      componentProps: {
        class: '!w-220px',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        type: 'daterange',
        placeholder: '创建时间'
      },
      isTag: true
    }
  ],
  expenderField: 'transferStatus'
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await TravelApi.getTravelAppPage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await TravelApi.deleteTravelApp(id)
  },
  exportListApi: async (ps) => {
    return await TravelApi.exportTravelApp(ps)
  },
  columns: [
    {
      field: 'id',
      label: '编号',
      width: '120px'
    },
    {
      field: 'purpose',
      label: '出差事由',
      minWidth: '120px'
    },
    {
      field: 'dest',
      label: '出差地点',
      width: '120px'
    },
    {
      field: 'result',
      label: '结果',
      width: '100px',
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
    },
    {
      field: 'startTime',
      label: '开始时间',
      formatter: formatDateColumn(),
      width: '120px'
    },
    {
      field: 'endTime',
      label: '结束时间',
      formatter: formatDateColumn(),
      width: '120px'
    },
    {
      field: 'duration',
      label: '出差时长',
      width: '100px'
    },
    {
      field: 'transportationType',
      label: '交通工具',
      minWidth: '180px',
      formatter: formatDictColumn(DICT_TYPE.OA_TRANSPORTATION_TYPE)
    },
    {
      field: 'companions',
      label: '随行人员',
      minWidth: '180px'
    },
    {
      field: 'createTime',
      label: '创建时间',
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
      width: '180px'
    },
    {
      field: 'action',
      label: '操作',
      width: '150px',
      fixed: 'right',
      align: 'left',
      slots: {
        default: (data) => {
          const { row } = data

          return (
            <div class="flex items-center justify-between">
              <el-button
                link
                type="primary"
                onClick={() => {
                  openForm('detail', row.id)
                }}
                hasPermi="['oa:travel-app:detail']"
              >
                详情
              </el-button>
              {row.result ? (
                []
              ) : (
                <>
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      openForm('edit', row.id)
                    }}
                    hasPermi="['oa:travel-app:update']"
                  >
                    编辑
                  </el-button>
                  <el-button
                    link
                    type="danger"
                    onClick={async () => {
                      await handleDelete(row.id)
                    }}
                    hasPermi="['oa:travel-app:detail']"
                  >
                    删除
                  </el-button>
                </>
              )}
            </div>
          )
        }
      }
    }
  ]
}

const eplusDialogRef = ref()
const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}
const handleExport = () => {}

const handleRefresh = () => {}
const handleDialogFailure = () => {}

const openForm = (type: string, id?: number) => {
  // travelAppFormRef.value?.open(type, id)
}
const handleDelete = async (id: number) => {
  // await delList(id, false)
}
</script>
