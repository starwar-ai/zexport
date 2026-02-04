<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="质检单信息"
      :formSchemas="purchaseSchemas"
    >
      <template #purchaseUserDeptName>
        <el-input
          v-model="formData.purchaseUserDeptName"
          disabled
        />
      </template>
      <template #purchaseType>
        <eplus-dict-select
          v-model="formData.purchaseType"
          style="width: 100%"
          :dictType="DICT_TYPE.PURCHASE_TYPE"
          clearable
        />
      </template>
      <template #equallyType>
        <eplus-dict-select
          v-model="formData.equallyType"
          style="width: 100%"
          :dictType="DICT_TYPE.EQUALLY_TYPE"
          clearable
        />
      </template>
      <template #deliveryDate>
        <el-date-picker
          v-model="formData.deliveryDate"
          clearable
          valueFormat="YYYY-MM-DD HH:mm:ss"
          type="datetime"
          style="width: 100%"
        />
      </template>
      <template #expectInspectionTime>
        <el-date-picker
          v-model="formData.expectInspectionTime"
          clearable
          disabled
          valueFormat="x"
          type="date"
          style="width: 100%"
        />
      </template>
      <template #planInspectionTime>
        <el-date-picker
          v-model="formData.planInspectionTime"
          clearable
          valueFormat="x"
          :disabled-date="disabledDate"
          type="date"
          style="width: 100%"
        />
      </template>
      <template #freight>
        <EplusMoney
          v-model:amount="formData.freight.amount"
          v-model:currency="formData.freight.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #otherCost>
        <EplusMoney
          v-model:amount="formData.otherCost.amount"
          v-model:currency="formData.otherCost.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #taxType>
        <el-select
          v-model="formData.taxType"
          clearable
          style="width: 100%"
          placeholder=" "
          :validate-event="false"
          :disabled="true"
        >
          <el-option
            v-for="dict in taxTypeList"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </template>
      <template #annex>
        <!-- 附件 -->
        <UploadList
          :fileList="formData.annex"
          disabled
        />
      </template>
      <!-- <template #venderPaymentId>
        <el-select
          v-model="formData.venderPaymentId"
          clearable
          style="width: 100%"
          placeholder=" "
          :validate-event="false"
          :disabled="true"
        >
          <el-option
            v-for="dict in paymentListData.paymentList.list"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          />
        </el-select>
      </template> -->
      <template #portName>
        <eplus-input-search-select
          v-model="formData.portName"
          :api="PurchaseContractApi.getPortList"
          :params="{ pageSize: 100, pageNo: 1, status: PortStatusStatusEnum.NORMAL.status }"
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="changePort"
        />
      </template>
    </eplus-form-items>

    <!-- <eplus-description
      title="供应商信息"
      :data="formData"
      :items="supplierInfo"
    >
      <template #quoteitemList> </template>
    </eplus-description> -->

    <eplus-form-items
      title="供应商信息"
      :formSchemas="supplierInfo"
    >
      <!-- <template #annex>
        <UploadList
          ref="UploadRef"
          :fileList="formData?.annex"
          @success="getFileList"
        />
      </template> -->
    </eplus-form-items>
  </eplus-form>
  <eplus-description
    title="明细"
    :data="formData"
    :items="quoteitemListInfo"
  >
    <template #quoteitemList>
      <Table
        style="width: 100%"
        :columns="inspectListColumns"
        headerAlign="center"
        align="center"
        :data="formData?.children"
      />
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
// import PurchaseProducts from '../components/PurchaseProducts.vue'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import { PortStatusStatusEnum } from '@/utils/constants'
import { useUserStore } from '@/store/modules/user'

import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'

import * as QualityInspectionApi from '@/api/qms/qualityinspection' // 验货单接口集合

const message = useMessage()
defineOptions({ name: 'ConfirmInspection' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()

const queryId: string = (props.id || '') as string
const loading = ref(false)
const simpleUserList = ref([])
const venderList = ref([])

const formData = reactive({
  purchaseUserId: '',
  trackUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  freight: { amount: '', currency: 'RMB' },
  otherCost: { amount: '', currency: 'RMB' },
  portName: '',
  inspectorId: '', // 当前登录的编号
  inspectorName: '' // 当前登录的编号
})

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}
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
const childrenRef = ref()

const changePort = (val) => {
  if (val) {
    formData.portId = Number(val)
  }
}
const changeTrackUser = (val) => {
  if (val) {
    formData.inspectorName = val.nickname
    formData.inspectorId = val.id
  }
}
const getCustName = (val) => {
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.custId = item.id
          formData.custName = item.name
          formData.custCode = item.code
        }
      })
    }
  }
}
let taxTypeList = ref<any>([])
taxTypeList.value = getIntDictOptions(DICT_TYPE.TAX_TYPE)

const quoteitemListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'quoteitemList',
    field: 'quoteitemList',
    label: '',
    span: 24
  }
]

// 质检单信息
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '验货单号',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同',
    component: <el-input disabled />
  },
  {
    field: 'sourceType',
    label: '单据来源',
    editable: true,
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.SOURCE_FLAG}
        style="width:100%"
      />
    )
  },
  {
    field: 'inspectionNode',
    label: '验货节点',
    editable: true,
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.INSPECTION_NODE}
        style="width:100%"
      />
    )
  },
  {
    field: 'sourceType',
    label: '验货方式',
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.INSPECTION_TYPE}
        style="width:100%"
      />
    )
  },

  {
    field: 'expectInspectionTime',
    label: '期望验货时间'
  },
  {
    field: 'createTime',
    label: '创建日期',
    component: (
      <el-date-picker
        disabled
        style="width:100%"
        valueFormat="x"
        type="date"
      />
    )
  },
  {
    field: 'reinspectionFlag',
    label: '是否重验',
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.IS_INT}
        style="width:100%"
      />
    )
  },
  {
    field: 'saleContractCode',
    label: '销售合同',
    component: <el-input disabled />
  },
  {
    field: 'custName',
    label: '客户名称',
    component: <el-input disabled />
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    component: <el-input disabled />
  },
  {
    field: 'salesName',
    label: '业务员',
    component: <el-input disabled />
  },
  {
    field: 'applyInspectorName',
    label: '申请验货人',
    component: <el-input disabled />
  },
  {
    field: 'planInspectionTime',
    label: '计划验货时间',
    rules: [
      {
        required: true,
        message: '请选择计划验货时间',
        trigger: 'change'
      }
    ]
  },

  {
    field: 'inspectorName',
    label: '验货人',
    editable: true,
    component: <eplus-user-select onChange={changeTrackUser}></eplus-user-select>,
    rules: [
      {
        required: true,
        message: '请选择验货人',
        trigger: 'change'
      }
    ]
  },
  {
    field: 'specialAttentionNotice',
    label: '特别注意事项',
    editable: true,
    component: (
      <el-input
        placeholder="请输入注意事项"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    )
  },

  {
    field: 'remark',
    label: '备注',
    editable: true,
    component: (
      <el-input
        // placeholder=""
        // type="textarea"
        disabled
      />
    )
  },
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  }
])

// 供应商信息

let supplierInfo: EplusFormSchema[] = reactive([
  {
    field: 'venderName',
    label: '供应商',
    component: <el-input disabled />
  },
  {
    field: 'factoryContacter',
    label: '工厂联系人',
    component: <el-input disabled />
  },
  {
    field: 'factoryTelephone',
    label: '联系电话',
    component: <el-input disabled />
  },
  {
    field: 'inspectionAddress',
    label: '工厂地址',
    component: <el-input disabled />
  }
])

// 验货信息
const inspectListColumns = [
  {
    label: '历史出现问题',
    field: 'lastFailDesc',
    width: '200'
  },
  // {
  //   label: '验货费',
  //   field: 'totalPriceAmount'
  // },
  // {
  //   label: '采购合同',
  //   field: 'purchaseContractCode',
  //   width: '200'
  // },
  {
    label: '基础产品编号',
    field: 'basicSkuCode'
  },
  {
    label: '客户货号',
    field: 'cskuCode'
  },
  {
    label: '自营产品货号',
    field: 'oskuCode'
  },
  {
    label: '产品名称',
    field: 'skuName'
  },
  {
    label: '数量',
    field: 'quantity'
  },
  // {
  //   label: '采购员',
  //   field: 'purchaseUserName'
  // },
  // {
  //   label: '业务员',
  //   field: 'packageType'
  // },
  // {
  //   label: '跟单员',
  //   field: 'trackUserName'
  // },

  // {
  //   label: '销售合同',
  //   field: 'saleContractCode'
  // },
  // {
  //   label: '客户名称',
  //   field: 'custName'
  // },
  {
    label: '内盒装量',
    field: 'qtyPerInnerbox'
  },
  {
    label: '外箱装量',
    field: 'qtyPerOuterbox'
  },

  {
    label: '箱数',
    field: 'boxCount'
  },
  {
    label: '备注',
    field: 'remark'
  }
]

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

const submitForm = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return
  try {
    let params = { ...formData }
    params.itemSaveReqVOList = formData.children
    params.children = []
    params.qualityInspectionStatus = 3

    await QualityInspectionApi.qmsVerification({ ...params })
    message.success('提交成功')
    close()
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  updateDialogActions(
    <el-button
      onClick={() => {
        submitForm()
      }}
      key="14"
      type="primary"
      hasPermi="qms:quality-inspection:update"
      isShow={props.id ? true : false}
    >
      提交
    </el-button>
  )

  let res = await QualityInspectionApi.getQualityInspectionDetail(props.id)

  let obj = {
    ...res
  }

  obj.inspectorId = obj.inspectorId == null ? useUserStore().getUser.id : obj.inspectorId // 当前登录的编号
  obj.inspectorName =
    obj.inspectorName == null ? useUserStore().getUser.nickname : obj.inspectorName
  obj.planInspectionTime = res.reinspectionFlag ? obj.planInspectionTime : obj.expectInspectionTime // 计划验货时间要取到跟单申请时的填写的时间
  obj.purchaseUserName = obj.purchaseUser?.nickname
  obj.salesName = obj.sales?.nickname
  Object.assign(formData, obj)
})
</script>
