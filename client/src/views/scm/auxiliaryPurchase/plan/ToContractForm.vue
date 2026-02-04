<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    v-model="formData"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="产品信息"
      :formSchemas="productSchemas"
    >
      <template #children>
        <eplus-form-table
          ref="TableRef"
          :list="tableList"
          :defaultVal="{}"
          :schema="tableColumns"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
  <SelectVenderQuoteDialog
    ref="SelectVenderQuoteRef"
    @sure="handleSelectSure"
  />
  <SaleContractDialog
    ref="SaleContractDialogRef"
    @sure="handleSaleSure"
  />
</template>
<script setup lang="tsx">
import { EplusFormSchema } from '@/components/EplusTemplate'
import { TableColumn } from '@/types/table'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import SelectVenderQuoteDialog from '../components/SelectVenderQuote.vue'
import * as PurchasePlanApi from '@/api/scm/auxiliaryPurchasePlan'
import UploadList from '@/components/UploadList/index.vue'
import { columnWidth } from '@/utils/table'
import { checkValidVender, getVenderSimpleList } from '@/api/common'
import { EplusNumInput } from '@/components/EplusMoney'
import { moneyInputPrecision } from '@/utils/config'

const formRef = ref()
const SelectVenderQuoteRef = ref()
const TableRef = ref()
const SaleContractDialogRef = ref()
const props = defineProps<{
  id?: number
  param?: any
}>()

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const { close } = inject('dialogEmits') as {
  close: () => void
}
const message = useMessage()
const queryId: string = (props.id || '') as string
const loading = ref(false)
const tableList = ref([])
const companyList = ref([])
const formData = reactive({
  purchaseUserId: '',
  purchaseUserDeptName: '',
  remark: ''
})
const productSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24
  }
]
const handleSelectSure = (list) => {
  // if(list&&)
  //可做优化减少一次遍历，sortnum-1=index
  if (list[2] === 'normalDialog') {
    tableList.value.forEach((item: any) => {
      if (item?.id === list[0]?.value) {
        //避免id覆盖
        list[1]?.id && delete list[1]?.id
        return Object.assign(item, list[1])
      }
    })
  }
  // selectedQuote=list[0]
}
const handleSaleSure = (list) => {}
const getFileList = (params, row) => {
  row.annex = params
}
let tableColumns = reactive<TableColumn[]>([
  {
    label: '序号',
    field: 'sortNum',
    fixed: 'left',
    width: 80
  },
  {
    field: 'auxiliarySkuType',
    label: '包材使用方式',
    minWidth: columnWidth.l,
    showOverflowTooltip: false,
    component: (
      <eplus-dict-select
        style="width: 150px"
        dictType={DICT_TYPE.AUXILIARY_PURCHASE_TYPE}
        clearable={true}
        disabled={true}
      />
    ),
    rules: [{ required: true, message: '请选择采购类型' }]
  },
  {
    field: 'mainPicture',
    label: '包材图片',
    width: '100px',
    // formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
    formatter: (item, row: Recordable, index: number) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.thumbnail}
        />
      )
    }
  },
  {
    field: 'skuName',
    label: '包材名称',
    minWidth: columnWidth.l
  },
  {
    field: 'skuUnit',
    label: '计量单位',
    minWidth: columnWidth.l,
    showOverflowTooltip: false,
    formatter: (item, row) => {
      return getDictLabel(DICT_TYPE.MEASURE_UNIT, row?.skuUnit)
    }
  },
  {
    field: 'auxiliarySaleContractCode',
    label: '销售合同',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return <span>{row?.auxiliarySaleContractCode ? row.auxiliarySaleContractCode : '--'}</span>
    }
  },
  {
    field: 'auxiliaryPurchaseContractCode',
    label: '采购合同',
    minWidth: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <span>{row?.auxiliaryPurchaseContractCode ? row.auxiliaryPurchaseContractCode : '--'}</span>
      )
    }
  },
  {
    field: 'auxiliarySkuName',
    label: '品名',
    minWidth: columnWidth.l
  },
  {
    field: 'auxiliaryCskuCode',
    label: '客户货号'
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-user-select
          v-model={row.purchaseUserId}
          onChange={async (val) => {
            row.venderFlag = 0
            row.venderId = ''
            row.venderCode = ''
            row.venderName = ''
            if (val) {
              row.purchaseUserDeptName = val.deptName
              row.purchaseUserName = val.nickname
              row.purchaseUserDeptId = val.deptId
            }
          }}
        ></eplus-user-select>
      )
    },
    rules: [
      {
        required: true,
        message: '请选择采购员'
      }
    ]
  },
  {
    field: 'venderCode',
    label: '供应商名称',
    width: '280px',
    slot: (item, row: Recordable, index: number) => {
      return row.venderFlag ? (
        <eplus-input-search-select
          v-model={row.venderCode}
          class="!w-100%"
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1, skuQuoteFlag: 1 }}
          keyword="name"
          label="name"
          value="code"
          defaultObj={{
            id: row.venderId,
            code: row.venderCode,
            name: row.venderName,
            currency: row.currency
          }}
          onChangeEmit={(val, list) => {
            list.map((item) => {
              if (item.code === val) {
                row.venderId = item.id
                row.venderName = item.name
                row.currency = item.currency
              }
            })
          }}
        ></eplus-input-search-select>
      ) : (
        <eplus-input
          v-model={row.venderName}
          style="width: 280px"
        />
      )
    },
    rules: {
      required: true,
      message: '请选择供应商名称'
    }
  },
  {
    field: 'withTaxPrice',
    label: '供应商报价',
    minWidth: columnWidth.m,
    component: (
      <EplusNumInput
        precision={moneyInputPrecision}
        min={0}
        controls={false}
      />
    ),
    rules: [
      {
        required: true,
        message: '请输入供应商报价'
      }
    ]
  },

  {
    field: 'needPurQuantity',
    label: '采购数量',
    minWidth: columnWidth.l
  },
  {
    field: 'specRemark',
    label: '规格描述',
    component: <el-input style="min-width: 150px" />,
    rules: [
      {
        required: true,
        message: '请输入规格描述'
      }
    ]
  },
  {
    field: 'annex',
    label: '附件',
    formatter: (item, row: Recordable, index: number) => {
      return (
        <UploadList
          ref="UploadRef"
          fileList={row?.annex}
          onSuccess={(val) => getFileList(val, row)}
        />
      )
    }
  },
  {
    field: 'remark',
    label: '备注',
    component: (
      <el-input
        style="min-width: 150px"
        clearable={true}
      />
    )
  }
])

watch(
  () => tableList,
  (list: any) => {
    if (list.value) {
      list.value.map((item, index, arr) => {
        if (item?.withTaxPrice?.amount) {
          item.withTaxPrice = item.withTaxPrice.amount
        }
        item.totalPrice = (item.needPurQuantity * item.withTaxPrice || 0).toFixed(3)
        setTimeout(() => {
          if (item.venderFlag === 0) {
            item.venderFlag = 1
          }
        }, 0)
        item.index = index + 1
        item.purchaseQuantity = item.needPurQuantity
      })
    }
  },
  { immediate: true, deep: true }
)
//处理提交前的formdata
const chnageAmountFormat = (param) => {
  param.forEach((item, index) => {
    item.withTaxPrice = {
      amount: item.withTaxPrice,
      currency: item?.currency ? item?.currency : 'RMB'
    }
    item.totalPrice = {
      amount: item.totalPrice,
      currency: item?.currency ? item?.currency : 'RMB'
    }
  })
}
const getParams = async () => {
  let valid = await TableRef.value.validate()
  if (!valid) return false
  if (valid) {
    let params: any = JSON.parse(JSON.stringify({ planList: tableList.value }))
    chnageAmountFormat(params.planList)
    const venderList = [...params.planList.map((item) => item.venderCode)]
    await checkValidVender(venderList.join(','))
    return {
      id: props.param?.id,
      code: props.param?.code,
      planDate: props.param?.planDate,
      submitFlag: 1,
      auxiliaryFlag: 1,
      ...params
    }
  } else {
    return false
  }
}
const emit = defineEmits(['handleSuccess', 'sucess'])

const submitForm = async () => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (paramsResult) {
      try {
        const res = await PurchasePlanApi.purchaseToContract(paramsResult)
        message.notifyPushSuccess('转包材采购合同成功', res, 'purchase-auxiliary-contract')
        close()
        emit('handleSuccess', 'success')
      } catch (error) {
        message.error('提交失败')
      }
    }
  } finally {
    loading.value = false
  }
}
const paymentBtnShow = () => {
  if (props?.id) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          try {
            submitForm()
          } catch {
            message.error('转采购合同失败')
          }
        }}
        type="primary"
        key="toContract"
      >
        转包材采购合同
      </el-button>
    )
  }
}
onMounted(async () => {
  try {
    paymentBtnShow()
    companyList.value = await PurchasePlanApi.getProcessedCompanySimpleList()
    tableList.value = await PurchasePlanApi.getTocontractList(queryId)
    tableList.value.forEach((item) => {
      item.venderFlag = 1
    })
    provide('formData', { value: formData })
  } catch {
    console.log('err')
  }
})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: rgba(0, 0, 0, 0);
}
</style>
