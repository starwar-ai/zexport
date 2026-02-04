<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    exportFileName=""
    @emit-open-detail="handleDetail"
  />

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
    :destroyOnClose="true"
  >
    <template #detail="{ key }">
      <VenderDetail
        type="repeatingCheck"
        title="供应商"
        :id="key"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import VenderDetail from '../components/VenderDetail.vue'
import * as UserApi from '@/api/system/user'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import * as VenderApi from '@/api/scm/vender'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import * as DeptApi from '@/api/system/dept'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const eplusListRef = ref()
defineOptions({
  name: 'VenderRepeatingCheck'
})

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '供应商编码'
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
    // {
    //   component: <eplus-dict-select dictType={DICT_TYPE.CONFIRM_TYPE}></eplus-dict-select>,
    //   name: 'abroadFlag',
    //   label: '是否境外供应商',
    //   formatter: (args: any[]) => {
    //     return getDictLabel(DICT_TYPE.CONFIRM_TYPE, args[0])
    //   }
    // },
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
      component: <eplus-dict-select dictType={DICT_TYPE.ENABLE_FLAG}></eplus-dict-select>,
      name: 'enableFlag',
      label: '启用状态',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.ENABLE_FLAG, args[0])
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
    }
  ],
  moreFields: []
}

const tableColumns = reactive([
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
    // formatter: formatDictColumn(DICT_TYPE.LOAN_REPAY_STATUS)
  },
  {
    prop: 'venderType',
    label: '供应商类型',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.VENDER_TYPE)
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
    prop: 'phone',
    label: '供应商电话',
    minWidth: columnWidth.l
    // formatter: formatDictColumn(DICT_TYPE.LOAN_TRANSFER_STATUS)
  },
  // {
  //   prop: 'countryId',
  //   label: '国家'
  // },
  {
    prop: 'factoryAreaName',
    label: '工厂地址',
    minWidth: columnWidth.xl
  },
  {
    prop: 'licenseNo',
    label: '营业执照号',
    minWidth: columnWidth.l
  },
  {
    prop: 'currency',
    label: '币种',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE)
  },
  // {
  //   prop: 'taxRate',
  //   label: '税率%',
  //   minWidth: '120px'
  // },
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
    prop: 'auditStatus',
    label: '审核状态',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT)
  },
  {
    prop: 'enableFlag',
    label: '启用状态',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.ENABLE_FLAG)
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
              hasPermi="['scm:vender:detail']"
            >
              详情
            </el-button>
          </div>
        )
      }
    }
  }
])

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await VenderApi.getVenderPage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  columns: tableColumns
}

/// 打开详情
const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
