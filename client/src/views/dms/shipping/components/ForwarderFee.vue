<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加行</el-button
    >
    <el-button @click="handleRemove">移除</el-button>
    <el-button
      v-if="props.channel == 'detail'"
      @click="handleSave"
      type="primary"
    >
      保存
    </el-button>
    <el-button
      v-if="props.channel == 'detail'"
      @click="handleCancel"
      type="primary"
    >
      退出
    </el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="defaultVal"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { TableColumn } from '@/types/table'
import { formatterPrice } from '@/utils/index'
import { useFeeStore } from '@/store/modules/fee'
import { getVenderSimpleList } from '@/api/common'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

const feeList = useFeeStore().feeList
const dictSubjectList = feeList?.filter((item) => item.showFeeFlag != 1)
// const feeDescList = feeList.filter((item) => item.showDescFlag == 1)
defineOptions({ name: 'ForwarderFeeCom' })
const props = defineProps<{
  formData
  type: string
  channel?: string
}>()
const TableRef = ref()
const message = useMessage()
const tableList = ref([])

const defaultVal = {
  // dictSubjectId: dictSubjectList?.find((item) => item.feeName === '单证费用').id,
  dictSubjectName: '国内费用',
  currency: 'RMB'
}
const getVenderName = (e, row) => {
  let item = e[1]?.find((item) => item.code === e[0])
  row.venderCode = item.code || undefined
  row.venderId = item.id || undefined
  row.venderName = item.name || undefined
}

let tableColumns = reactive<TableColumn[]>([
  {
    field: 'venderCode',
    label: '船代公司',
    isShow: true,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <eplus-input-search-select
            v-model={row.venderCode}
            api={getVenderSimpleList}
            params={{ pageSize: 100, pageNo: 1, administrationVenderType: 2 }}
            keyword="name"
            label="name"
            value="code"
            class="!w-100%"
            placeholder="请选择"
            clearable={false}
            onChangeEmit={(...$event) => getVenderName($event, row)}
            defaultObj={{
              code: row.venderCode || undefined,
              name: row.venderName || undefined,
              id: row.venderId || undefined
            }}
            disabled={row.payStatus > 1}
          />
        </>
      )
    },
    rules: {
      required: true,
      message: '请选择供应商'
    },
    width: 300
  },
  {
    field: 'dictSubjectName',
    label: '费用名称',
    component: <el-input />,
    rules: { required: true, message: '请输入费用名称' }
  },
  // {
  //   field: 'feeType',
  //   label: '费用类型',
  //   slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
  //     return (
  //       <>
  //         <eplus-dict-select
  //           v-model={row.feeType}
  //           class="!w-90%"
  //           dictType={DICT_TYPE.FORWARDER_FEE_TYPE}
  //           disabled={row.payStatus > 1}
  //         />
  //       </>
  //     )
  //   },
  //   rules: { required: true, message: '请输入费用类型' }
  // },
  {
    label: '金额',
    field: 'amountVal',
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <EplusMoney
            v-model:amount={row.amountVal}
            v-model:currency={row.currency}
            class="!w-90%"
            amountDisabled={row.payStatus > 1}
            currencyDisabled={row.payStatus > 1}
          />
        </>
      )
    },
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^(\d{1,11})(\.?\d{1,3})?$/
        if (!regx.test(value)) {
          callback(new Error('金额只能输入正数,最多11位整数,3位小数'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'payStatus',
    label: '付款状态',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (row.payStatus) {
        return (
          <el-tag type="primary">{getDictLabel(DICT_TYPE.APPLY_FEE_STATUS, row.payStatus)}</el-tag>
        )
      } else {
        return '-'
      }
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            await delRow(index)
          }}
          disabled={row.payStatus > 1}
        >
          移除
        </el-button>
      )
    }
  }
])

const handleAdd = async () => {
  const newRecord = {
    sortNum: tableList.value.length > 0 ? tableList.value.length - 1 : 0,
    ...defaultVal
  }
  tableList.value.push(newRecord)
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.sortNum
    })
    tableList.value = tableList.value?.filter((item, index) => {
      if (!delArr.includes(item.sortNum)) {
        return item
      }
    })
    tableList.value.forEach((item, index) => {
      return (item.sortNum = index + 1)
    })
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
  tableList.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const checkData = async () => {
  if (tableList.value.length) {
    let valid = await TableRef.value.validate()
    if (valid) {
      let tableData = cloneDeep(TableRef.value.tableData)
      let arr = tableData.map((item, index) => {
        return {
          ...item,
          amount: { amount: formatterPrice(item.amountVal), currency: item.currency }
        }
      })
      return arr
    } else {
      message.warning('单证费用提交有误')
      return false
    }
  } else {
    return []
  }
}
defineExpose({ selectedList, tableList, checkData })

const emit = defineEmits(['save', 'cancel'])
const handleSave = async () => {
  let data = await checkData()
  if (data) {
    emit('save', {
      purchaseUserList: props.formData.purchaseUserList,
      managerList: props.formData.managerList,
      shipmentId: props.formData.id,
      forwarderFeeList: data
    })
  }
}
const handleCancel = () => {
  emit('cancel')
}
watchEffect(() => {
  if (isValidArray(props.formData.forwarderFeeList)) {
    let tableData = cloneDeep(props.formData.forwarderFeeList)
    tableList.value = tableData.map((item) => {
      return {
        ...item,
        amountVal: item.amount?.amount,
        currency: item.amount?.currency
      }
    })
  } else {
    tableList.value = []
  }
})
</script>
