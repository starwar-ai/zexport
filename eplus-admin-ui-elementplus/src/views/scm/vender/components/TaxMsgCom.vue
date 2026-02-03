<template>
  <eplus-form-table
    ref="TableRef"
    :schema="tableSchema"
    :list="tableList"
  />
  <!-- <el-button
    type="primary"
    @click="saveForm"
    >保存
  </el-button> -->
</template>
<script setup lang="tsx">
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { getConfigJson } from '@/api/common'
import { EplusNumInput } from '@/components/EplusMoney'

defineOptions({ name: 'FinanceList' })
const props = defineProps<{
  formData?: any
  forwarderFlag?: boolean
}>()

const TableRef = ref()
const tableList = ref([])
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'currency',
    label: '币种',
    width: '250px',
    slot: (item, row) => {
      return (
        <eplus-dict-select
          v-model={row.currency}
          dictType={DICT_TYPE.CURRENCY_TYPE}
          onChangeEmit={(...e) => {
            row.taxType = undefined
            row.taxRate = undefined
          }}
        />
      )
    },
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (!value) {
            callback(new Error('请选择币种'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'taxType',
    label: '发票类型',
    width: '250px',
    slot: (item, row) => {
      return (
        <eplus-input-select
          v-model={row.taxType}
          dataList={row.currency === 'RMB' ? chinaTaxRateList : internationalTaxRateList}
          label="label"
          value="key"
          onChangeEmit={(...e) => {
            if (row.currency === 'RMB') {
              row.taxRate = e[1].find((item) => item.key === e[0]).value
            } else {
              row.taxRate = undefined
            }
          }}
          class="!w-100%"
        />
      )
    },
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (!value) {
            callback(new Error('请选择币种'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'taxRate',
    label: '税率（%）',
    width: '250px',
    slot: (item, row) => {
      return (
        <EplusNumInput
          v-model={row.taxRate}
          min={0}
          max={99.99}
          precision={2}
          controls={false}
          disabled={row.currency !== 'RMB'}
        />
      )
    },
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (value === '') {
            callback(new Error('请输入税率'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'defaultFlag',
    label: '默认',
    width: '120px',
    slot: (item, row, index) => {
      return (
        <div onClick={() => setDefault(index, tableList.value)}>
          <el-button
            style="width:10px;height:10px;"
            type={row.defaultFlag == 1 ? 'primary' : ''}
            circle
            size="small"
          ></el-button>
        </div>
      )
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '80px',

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

const message = useMessage()

const saveForm = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    return TableRef.value.tableData
  } else {
    message.warning('财务信息提交有误')
    return false
  }
}
defineExpose({ saveForm, tableList })
//设置为默认
const setDefault = (scope, d) => {
  for (let i = 0; i < d.length; i++) {
    d[i].defaultFlag = scope == i ? 1 : 0
  }
}

let chinaTaxRateList = ref<any>([])
let internationalTaxRateList = ref<any>([])

const getInvoiceRateList = async (type) => {
  const res = await getConfigJson({ configType: 'invoice.rate', type: type })
  if (res && type === 'abroad') {
    internationalTaxRateList.value = res
  } else {
    chinaTaxRateList.value = res
  }
}

onMounted(async () => {
  await getInvoiceRateList('abroad')
  await getInvoiceRateList('domestic')
  if (props.formData?.taxMsg?.length) {
    tableList.value = props.formData.taxMsg
  }

  if (tableList.value?.length === 1) {
    tableList.value[0].defaultFlag = 1
  }
})
</script>
