<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    exportFileName=""
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { columnWidth } from '@/utils/table'
import * as CustApi from '@/api/crm/cust'
import * as UserApi from '@/api/system/user'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { useCountryStore } from '@/store/modules/countrylist'
import * as DeptApi from '@/api/system/dept'
import { EplusCountrySelect } from '@/components/EplusSelect'

const countryStore = useCountryStore()
const countryData = JSON.parse(JSON.stringify(countryStore.countryList))
const eplusListRef = ref()

defineOptions({
  name: 'CustRepeatingCheck'
})

const searchCountryName = ref()
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '客户名称'
    },
    {
      component: (
        <EplusCountrySelect
          onChange={(item) => {
            searchCountryName.value = item.name
          }}
          class="!w-100%"
        />
      ),
      name: 'countryId',
      label: '国家/地区',
      formatter: () => {
        return searchCountryName.value
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'address',
      label: '地址'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerIds',
      label: '业务员',
      formatter: async (args: any[]) => {
        let user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择业务员部门"></eplus-dept-select>,
      name: 'managerDeptId',
      label: '业务员部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    },
    {
      component: <eplus-dict-select dictType={DICT_TYPE.CUSTOMER_STAGE}></eplus-dict-select>,
      name: 'stageType',
      label: '客户阶段',
      formatter: (args: any[]) => {
        return getDictLabel(DICT_TYPE.CUSTOMER_STAGE, args[0])
      }
    }
  ],
  moreFields: []
}

const counterFormatter = (row, column, __cv) => {
  let result: any = countryData.filter((item) => {
    return item.id === row.countryId
  })
  return result[0]?.[column.property == 'countryId' ? 'name' : 'regionName']
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await CustApi.custDuplicateCheck({ ...ps })
    return {
      list: res.list,
      total: res.total
    }
  },
  columns: [
    {
      prop: 'name',
      label: '客户名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'countryId',
      label: '国家/地区',
      formatter: counterFormatter,
      minWidth: columnWidth.m
    },
    {
      prop: 'address',
      label: '地址',
      showOverflowTooltip: true,
      minWidth: columnWidth.l
    },
    {
      prop: 'managerList',
      label: '业务员',
      formatter: (row) => {
        return row.managerList?.map((item) => item.name).join(',')
      },
      minWidth: columnWidth.m
    },
    {
      prop: 'managerDeptName',
      label: '业务员部门',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return row.managerList
          ?.map((item) => item.deptName)
          .filter((deptName) => !!deptName)
          .join(',')
      }
    },
    {
      prop: 'stageType',
      label: '客户阶段',
      minWidth: columnWidth.l,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.CUSTOMER_STAGE, row.stageType)
      }
    }
  ]
}

onMounted(() => {})
</script>
