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
          v-model="formData.plannedArrivalTime"
          clearable
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

      <template #annex>
        <!-- 附件 -->
        <UploadList
          :fileList="formData.annex"
          disabled
        />
      </template>
    </eplus-form-items>

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
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import * as QualityInspectionApi from '@/api/qms/qualityinspection' // 验货单接口集合

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const message = useMessage()
defineOptions({ name: 'ConfirmInspection' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()

const queryId: string = (props.id || '') as string
const loading = ref(false)
const simpleUserList = ref([])

const formData = reactive({
  purchaseUserId: '',
  trackUserId: '',
  purchaseUserDeptName: '',
  remark: ''
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

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
    component: <el-input disabled />,
    span: 6
  },
  {
    field: 'sourceType',
    label: '单据来源',
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.SOURCE_FLAG}
        style="width:100%"
      />
    ),
    span: 6
  },
  {
    field: 'sourceType',
    label: '验货方式',
    // editable: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.INSPECTION_TYPE}
        style="width:100%"
      />
    ),
    span: 6
  },
  {
    field: 'planInspectionTime',
    label: '期望验货时间',
    span: 6
  },
  {
    field: 'applyInspectorName',
    label: '申请验货人',
    editable: true,
    component: <el-input disabled />,
    span: 6
  },

  {
    field: 'inspectorName',
    label: '验货人',
    editable: true,
    component: <el-input disabled />,
    span: 6
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
    ),
    span: 6
  },

  {
    field: 'remark',
    label: '备注',
    editable: true,
    span: 6,
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
const hsdataCodeChange = (val, list) => {
  let item = list.find((item) => item.id === val)
  formData.venderId = item?.id || ''
  formData.venderName = item?.name | ''
}
let supplierInfo: EplusFormSchema[] = reactive([
  {
    field: 'venderName',
    label: '供应商',
    component: (
      <eplus-input-search-select
        api={TravelReimbApi.getVenderSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={hsdataCodeChange}
      />
    ),
    span: 6
  },
  {
    field: 'factoryContacter',
    label: '工厂联系人',
    editable: true,
    component: <el-input />,
    span: 6
  },
  {
    field: 'factoryTelephone',
    label: '联系电话',
    editable: true,
    component: <el-input />,
    span: 6
  },
  {
    field: 'inspectionAddress',
    label: '工厂地址',
    editable: true,
    component: <el-input />,
    span: 6
  }
])

// 验货信息
const inspectListColumns = [
  {
    label: '采购合同',
    field: 'purchaseContractCode',
    width: '200'
  },
  {
    label: '产品编号',
    field: 'skuCode'
  },
  {
    label: '客户货号',
    field: 'cskuCode'
  },
  {
    label: '产品名称',
    field: 'skuName'
  },
  {
    label: '数量',
    field: 'quantity'
  },
  {
    label: '采购员',
    field: 'purchaseUserName'
  },
  {
    label: '业务员',
    field: 'packageType'
  },
  {
    label: '跟单员',
    field: 'trackUserName'
  },

  {
    label: '销售合同',
    field: 'saleContractCode'
  },
  {
    label: '客户名称',
    field: 'custName'
  },
  {
    label: '外箱数量',
    field: 'qtyPerOuterbox'
  },

  {
    label: '计算箱数',
    field: 'boxCount'
  },
  {
    label: '备注',
    field: 'remark'
  }
]

const UploadRef = ref()
const getFileList = (params) => {
  formData.annex = params
}

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

const saveForm = async () => {
  try {
    let params = { ...formData }
    params.itemSaveReqVOList = formData.children
    params.children = []
    const res = await QualityInspectionApi.updateQualityInspection({ ...params })
    message.success('修改成功')
    close()
  } finally {
    loading.value = false
  }
}

const submit = async () => {
  await saveForm()
  await QualityInspectionApi.submitQualityInspectionApi({ id: props.id })
  message.success('提交成功')
  close()
}

onMounted(async () => {
  let res = await QualityInspectionApi.getQualityInspectionDetail(props.id)

  let obj = {
    ...res
  }
  Object.assign(formData, obj)
  updateDialogActions(
    <el-button
      onClick={() => {
        saveForm()
      }}
      type="primary"
      key="inspectSubmitUpdate"
      hasPermi="qms:quality-inspection:update"
      // key="verification"
      // hasPermi="qms:quality-inspection:verification"
      isShow={formData.qualityInspectionStatus == 7 ? true : false}
    >
      保存
    </el-button>,
    <el-button
      onClick={() => {
        submit(props.id)
      }}
      type="primary"
      key="inspectSubmit"
      hasPermi="qms:quality-inspection:submit"
      // key="verification"
      // hasPermi="qms:quality-inspection:verification"
      isShow={formData.qualityInspectionStatus == 7 ? true : false}
    >
      提交
    </el-button>
  )
})
</script>
