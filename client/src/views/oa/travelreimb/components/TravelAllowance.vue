<template>
  <eplus-form-table
    v-if="defaultVal.dictSubjectId"
    ref="TableRef"
    :list="formData?.travelAllowanceList"
    :schema="tableSchema"
    :defaultVal="defaultVal"
  />
  <!-- <div class="total_box">
    <div> 总计 </div>
    <div class="pr15px"> 出差补助报销金额：<TotalLayout :list="TravelAllowanceAmountList" /> </div>
  </div> -->

  <!-- <el-button @click="saveForm">保存</el-button> -->
</template>
<script setup lang="tsx">
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { getTotal } from '@/utils/index'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import { useFeeStore } from '@/store/modules/fee'
import { columnWidth } from '@/utils/table'

const feeList = useFeeStore().feeList
const dictSubjectList = feeList.filter((item) => item.showFeeFlag != 1)
const feeDescList = feeList.filter((item) => item.showDescFlag == 1)
defineOptions({ name: 'TravelAllowance' })
const formData = inject('formData') as EplusAuditable
const message = useMessage()
const TableRef = ref()
const defaultVal = ref({
  expenseCurrency: formData.currency,
  dictSubjectId: undefined
})
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    minWidth: columnWidth.m
  },

  {
    label: '补贴标准',
    field: 'allowanceStandard',
    minWidth: columnWidth.m,
    component: (
      <eplus-subsidyType-select
        api={TravelReimbApi.getTravelStandard}
        label="subsidyType"
        value="amount"
      />
    ),
    rules: [
      { required: true, message: '请选择补贴标准' },
      {
        validator: (rule: any, value: any, callback: any) => {
          let valueArr = TableRef.value.tableData.map((item) => item.allowanceStandard)
          valueArr.splice(
            valueArr.findIndex((el) => el == value),
            1
          )
          if (valueArr.includes(value)) {
            callback(new Error('此项补贴标准已存在'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    label: '出差天数',
    field: 'allowanceDay',
    component: (
      <el-input-number
        min={1}
        max={9999}
        precision={0}
        step={1}
        class="!w-90%"
      />
    ),
    minWidth: columnWidth.m,
    rules: [{ required: true, message: '请输入出差天数' }]
  },
  {
    label: '发票名称',
    field: 'dictSubjectId',
    minWidth: columnWidth.m,
    hint: (
      <el-table
        data={feeDescList}
        size="small"
        max-height="450"
      >
        <el-table-column
          width="100"
          property="feeName"
          label="发票名称"
        />
        <el-table-column
          width="660"
          property="feeDesc"
          label="费用描述"
        />
      </el-table>
    ),
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <eplus-input-select
            v-model={row.dictSubjectId}
            dataList={dictSubjectList}
            label="feeName"
            value="id"
            class="!w-90%"
            disabled
          />
        </>
      )
    },
    rules: { required: true, message: '请选择发票名称' }
  },
  {
    label: '报销金额',
    field: 'expenseAmount',
    minWidth: columnWidth.m
  },
  {
    label: '备注',
    field: 'remark',
    width: columnWidth.xxl,
    component: (
      <el-input
        autosize
        type="textarea"
      />
    )
  },
  {
    label: '操作',
    width: columnWidth.m,
    fixed: 'right',
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => deleteAction(index)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
]

const TravelAllowanceAmountList = ref([])
watch(
  () => TableRef.value?.tableData,
  (list) => {
    list?.forEach((item) => {
      if (item.allowanceStandard && item.allowanceDay) {
        item.expenseAmount = Number(item.allowanceStandard) * Number(item.allowanceDay)
      } else {
        item.expenseAmount = 0
      }
    })
    if (list) {
      TravelAllowanceAmountList.value = getTotal(list)
    }
  },
  { immediate: true, deep: true }
)

const saveForm = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    return TableRef.value.tableData.map((item) => {
      return {
        ...item,
        expenseType: 1,
        expenseCurrency: formData.currency
      }
    })
  } else {
    message.warning('出差补贴信息提交信息有误')
    return false
  }
}

defineExpose({ saveForm, TravelAllowanceAmountList })

onMounted(async () => {
  let item: any = dictSubjectList.find((item: any) => item.feeName === '出差补贴')
  if (item?.id) {
    defaultVal.value.dictSubjectId = item.id
  } else {
    message.warning('票据项对应科目中未配置出差补贴')
  }
})
</script>
<style lang="scss" scoped>
.total_box {
  width: 100%;
  display: flex;
  justify-content: space-between;
  height: 40px;
  align-items: center;
}
</style>
