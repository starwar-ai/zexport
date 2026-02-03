<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
  >
    <eplus-form-items
      title="基础信息"
      :formSchemas="basicSchemas"
    />
    <eplus-form-items
      title="入库单详情"
      :formSchemas="proSchemas"
    >
      <template #billItems>
        <ProCom
          ref="ProComRef"
          :formData="formData"
          @abnormal-status="abnormalStatus"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title=""
      class="pictures"
      :formSchemas="pictureSchemas"
    >
      <template #pictures>
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.pictures"
          @success="getPicList"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as BillApi from '@/api/wms/bill'
import { formatTime } from '@/utils/index'
import ProCom from './components/ProCom.vue'
import { cloneDeep } from 'lodash-es'
import { formatStringConcat } from '@/utils/formatStringConcat'
import { isValidArray } from '@/utils/is'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'LoanAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)

const formData: EplusAuditable = reactive({})
provide('formData', formData)

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const { close } = inject('dialogEmits') as {
  close: () => void
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    readOnly: true,
    label: '入库单号'
  },
  {
    field: 'companyName',
    readOnly: true,
    label: '归属公司'
  },
  {
    field: 'billTime',
    readOnly: true,
    label: '入库日期',
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd')
    }
  },
  // {
  //   field: 'purchaseContractCode',
  //   readOnly: true,
  //   label: '采购合同号'
  // },
  // {
  //   field: 'purchaserName',
  //   readOnly: true,
  //   label: '采购员'
  // },
  // bug 463
  // {
  //   field: 'purchaserDeptName',
  //   readOnly: true,
  //   label: '部门'
  // },
  // {
  //   field: '',
  //   readOnly: true,
  //   label: '联系电话'
  // },

  {
    field: 'salesName',
    readOnly: true,
    label: '业务员'
  },
  {
    field: 'printFlag',
    readOnly: true,
    label: '打印状态',
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.PRINT_FLAG, val)
    }
  },
  {
    field: 'billStatus',
    readOnly: true,
    label: '单据状态',
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.STOCK_BILL_STATUS, val)
    }
  },
  {
    field: 'createUser',
    readOnly: true,
    label: '创建人',
    formatter: (val) => {
      return val?.nickname || ''
    }
  },
  {
    field: 'createTime',
    readOnly: true,
    label: '创建时间',
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd HH:mm:ss')
    }
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input type="textarea" />,
    span: 24
  }
])

const proSchemas = reactive([
  {
    field: 'billItems',
    label: '',
    labelWidth: '0px',
    span: 24
  }
])
const getPicList = (list) => {
  formData.pictures = list
}
const picRulesStatus = ref(false)
const pictureSchemas: EplusFormSchema[] = reactive([
  {
    field: 'pictures',
    label: '异常图片',
    span: 24,
    labelWidth: '80px'
  }
])
const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const ProComRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  // if (!formData.pictures) {
  //   message.warning('请上传库存图片')
  //   return
  // }
  let params = cloneDeep(formData)
  if (ProComRef.value) {
    let result = await ProComRef.value?.checkData()
    if (result) {
      params.billItemSaveReqVOList = result
    } else {
      params.billItemSaveReqVOList = []
      return false
    }
  }
  params.pictures = params.pictures?.map((item) => {
    return { sourceId: params?.id, ...item }
  })
  return params
}

const checkBillItemList = (list) => {
  if (isValidArray(list)) {
    let abnormalList = []
    let des = ''
    list.forEach((item) => {
      if (item.actQuantity > item.orderQuantity) {
        abnormalList.push(item)
      }
    })
    if (isValidArray(abnormalList)) {
      abnormalList.forEach((el: any) => {
        des += `基础产品编号为${el.basicSkuCode}的产品检测到未入库数量只有${el.orderQuantity}，现在入库${el.actQuantity}，`
      })
      des += '确认入库？'
      return des
    }
  } else {
    return false
  }
}

const saveForm = async (type) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    let des = checkBillItemList(paramsResult.billItemSaveReqVOList)
    if (des) {
      await message.confirm(des)
    }
    await BillApi.updateBill({
      ...paramsResult,
      billStatus: type === 'submit' ? 2 : paramsResult.billStatus
    })
    message.success(t('common.updateSuccess'))
    close()
    emit('success')
  } finally {
    loading.value = false
  }
}

const abnormalStatus = (status) => {
  picRulesStatus.value = status
}

onMounted(async () => {
  formRef.value.resetForm()
  if (props.mode === 'edit') {
    let res = await BillApi.getBill({ id: props.id })
    let salesName = ''
    res.children?.forEach((e) => {
      e.actQuantity = e.actQuantity ? e.actQuantity : e.orderQuantity
      if (e.sales.nickname) {
        salesName = formatStringConcat(salesName, e.sales.nickname)
      }
    })
    Object.assign(formData, { ...res, salesName: salesName })
    console.log(formData)
    updateDialogActions(
      <el-button
        onClick={() => {
          saveForm()
        }}
        type="primary"
        key="YsbillFormSave"
        hasPermi="wms:bill:update"
      >
        保存
      </el-button>,
      <el-button
        onClick={() => {
          saveForm('submit')
        }}
        key="YsbillFormSubmit"
        hasPermi="wms:bill:update"
      >
        提交
      </el-button>
    )
  }
})
</script>
<style lang="scss">
.pictures .el-form-item__label {
  font-weight: 600;
  margin-right: 20px;
  line-height: 40px;
  margin-bottom: 5px;
}
</style>
