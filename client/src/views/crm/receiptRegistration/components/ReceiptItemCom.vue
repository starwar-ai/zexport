<template>
  <eplus-form-table-quote
    v-if="!loading"
    ref="TableRef"
    :list="tableList"
    :defaultVal="defaultValObj"
    :schema="tableSchema"
  />
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
import { EplusFormMode, EplusFormTableSchema } from '@/components/EplusTemplate'
import * as CommonApi from '@/api/common'
import * as CustClaimApi from '@/api/finance/custClaim'
import { formatNum } from '@/utils'

defineOptions({ name: 'QuoteItemCom' })
type QuoteItemComSchema = EplusFormTableSchema & {
  isShow?: boolean
}
const loading = ref(true)
const props = defineProps<{
  mode: EplusFormMode
  formData
}>()
const message = useMessage()
const TableRef = ref()
let tableList = ref([])
const parentCurrency = ref()
let defaultValObj = reactive({
  isShow: false
})
const parentData: any = inject('formData')
const omitItems = (lastPayeeCode) => {
  let hasListCodes = cloneDeep(parentData.custClaimItemList)
    ?.filter((item: any) => item.custCode === lastPayeeCode)
    .map((item1: any) => {
      return item1.custCode
    })
  let result = parentData.custClaimItemList?.filter((item) => !hasListCodes.includes(item.custCode))
  return result
}
const getCustName = async (val, row) => {
  if (val && val[0] && val[1]) {
    try {
      let findCust = val[1].find((item) => item.id === val[0])
      if (findCust) {
        const { code = '', managerList, name } = findCust
        let check = parentData.custClaimItemList?.find((item) => item?.custCode === code)
        if (check) {
          message.warning('请勿重复添加')
          row.payeeName = ''
          return false
        }
        row.payeeCode = code
        row.payeeName = name
        row.currency = parentCurrency.value
        row.manager = managerList
        row.isShow = true
        let searchItem = await CustClaimApi.searchCustClaim({
          custCodeList: code,
          companyId: props.formData.companyId
        })
        searchItem &&
          searchItem.forEach((item, index) => {
            item.disabledFlag = true
            item.index = index + 1
          })
        parentData.custClaimItemList = [...searchItem, ...(omitItems(row.lastPayeeCode) || [])]
        row.lastPayeeCode = code
        getCustClaimTotalAmountObj()
      }
    } catch (err) {
      console.log(err, 'err')
    }
  } else {
    row.lastPayeeCode = ''
  }
}
const getCustClaimTotalAmountObj = () => {
  let custClaimTotalAmountObj = {}
  tableList.value?.forEach((item) => {
    if (item.payeeCode) {
      custClaimTotalAmountObj[item.payeeCode] = Number(item.claimTotalAmount) || 0
    }
  })
  let custCodeList = Object.keys(custClaimTotalAmountObj)
  parentData.custClaimItemList = parentData.custClaimItemList?.filter((item) =>
    custCodeList.includes(item.custCode)
  )
  emit('update', custClaimTotalAmountObj, tableList.value)
}
const managerFormat = (val) => {
  if (val) {
    return val?.map((item) => item.nickname).join(',')
  } else {
    return ''
  }
}
//删除逻辑
const myDeleteAction = (row, index, delfn) => {
  const { payeeCode } = row
  let newList = cloneDeep(parentData.custClaimItemList)?.filter(
    (item) => item.custCode !== payeeCode
  )
  parentData.custClaimItemList = newList
  delfn(index)
  getCustClaimTotalAmountObj()
}
const tableSchema: QuoteItemComSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '编号',
    field: 'payeeCode',
    minWidth: 180,
    isShow: false,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <>{row.payeeCode}</>
    }
  },
  {
    label: '名称',
    field: 'payeeName',
    width: 320,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <eplus-input-search-select
          v-model={row.payeeName}
          api={CommonApi.getCustSimpleList}
          params={{ pageSize: 100, pageNo: 1 }}
          keyword="name"
          label="name"
          value="id"
          class="!w-100%"
          placeholder="请选择"
          onChangeEmit={(...$event) => getCustName($event, row)}
          defaultObj={{
            custCode: row.payeeCode || undefined,
            custName: row.payeeName || undefined,
            id: row.id || undefined
          }}
        />
      )
    },
    isShow: true
  },
  {
    label: '负责员工',
    field: 'manager',
    minWidth: 220,
    isShow: false,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <>{managerFormat(row?.manager)}</>
    }
  },
  {
    label: '认领总金额',
    field: 'claimTotalAmount',
    minWidth: 150,
    isShow: false,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <>{row.claimTotalAmount || 0}</>
    }
  },
  {
    label: '币种',
    field: 'currency',
    minWidth: 150,
    isShow: false,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <>{row.currency}</>
    }
  },
  {
    label: '操作',
    width: 100,
    fixed: 'right',
    isShow: true,
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
            onClick={() => myDeleteAction(row, index, deleteAction)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
]

const saveForm = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    let tableData = cloneDeep(TableRef.value.tableData)
    if (tableData?.length === 1 && tableData[0]?.isShow === false) {
      message.warning('请添加收款对象信息')
      return false
    }
    return tableData
  } else {
    message.warning('收款对象信息必填项缺失')
    return false
  }
}
const validate = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    return true
  } else {
    return false
  }
}

const setAmount = (obj) => {
  tableList.value.forEach((item: any) => {
    item.claimTotalAmount = formatNum(obj[item.payeeCode] || 0)
  })
}
defineExpose({ saveForm, validate, setAmount })
const emit = defineEmits(['update'])
watch(
  tableList.value,
  (val) => {
    if (val?.length && val) {
      val = val.map((item: any) => {
        if (item.payeeCode && !item.payeeName) {
          parentData.custClaimItemList = omitItems(item.payeeCode)
          item.payeeCode = ''
          item.manager = []
          item.lastPayeeCode = ''
        }
        item.currency = parentData?.currency
      })
    }
  },
  {
    deep: true
  }
)
onMounted(() => {
  parentCurrency.value = props?.formData?.currency
  tableList.value = props?.formData?.payeeEntityList?.map((item) => {
    item.currency = props?.formData?.currency
    item.isShow = true
    return item
  })
  loading.value = false
  // TableRef.value.addAction()
})
</script>
