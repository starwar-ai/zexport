<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'pms:csku:create',
      handler: () => saveForm(false, 0)
    }"
    :submitAction="{
      permi: 'pms:csku:submit',
      handler: () => saveForm(false, 1)
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="产品信息"
      :formSchemas="basicSchemas"
    >
      <template #price>
        <EplusMoney
          v-model:amount="formData.price.amount"
          v-model:currency="formData.price.currency"
        />
      </template>
      <!--  -->
      <template #categoryId>
        <el-tree-select
          disabled
          v-model="formData.categoryId"
          :data="classTree"
          :props="defaultProps"
          node-key="id"
          placeholder="请选择"
          class="!w-100%"
        />
      </template>

      <template #spec>
        <SpecCom
          disabled
          v-model:length="formData.specLength"
          v-model:width="formData.specWidth"
          v-model:height="formData.specHeight"
        />
      </template>
      <template #singleNetweight>
        <EplusWeight
          unitdisabled
          weightdisabled
          v-model:weight="formData.singleNetweight.weight"
          v-model:unit="formData.singleNetweight.unit"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title=""
      :formSchemas="basic2Schemas"
    >
      <template #annex>
        <UploadList
          ref="UploadListRef"
          :fileList="formData.annex"
          @success="getFileList"
        />
      </template>
      <template #picture>
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.picture"
          @success="getPicList"
          mainFlag
        />
      </template>
    </eplus-form-items>

    <MainTableDetail :info="formData" />

    <eplus-form-items
      title="供应商报价"
      :formSchemas="quoteitemListSchemas"
    >
      <template #quoteitemList>
        <QuoteItemCom
          :fromPage="fromPage"
          :formData="formData"
          ref="QuoteItemComRef"
          v-if="!quoteitemFlag"
        />
      </template>
    </eplus-form-items>
    <HsdataDetail :info="formData" />
  </eplus-form>
  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE } from '@/utils/dict'
import * as skuApi from '@/api/pms/main/index.ts'
import * as custSkuApi from '@/api/pms/cust/index'
import { EplusMoney } from '@/components/EplusMoney'
import UploadList from '@/components/UploadList/index.vue'
// import UploadPic from '@/components/UploadPic/index.vue'
import SpecCom from '../components/SpecCom.vue'
import { defaultProps, handleTree } from '@/utils/tree'
import * as classApi from '@/api/pms/class'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import MainTableDetail from '../main/components/MainTableDetail.vue'
import QuoteItemCom from '../main/components/QuoteItemCom.vue'
import HsdataDetail from '../main/components/HsdataDetail.vue'
import { cloneDeep } from 'lodash-es'
import ChangeTips from '@/components/ChangeTips/index.vue'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'LoanAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const fromPage = 'custPage'
const quoteitemFlag = ref(true)
const QuoteItemComRef = ref()
const loading = ref(true)
const formData: EplusAuditable = reactive({
  auditStatus: 0,
  custCode: '',
  cskuCode: '',
  price: {
    amount: undefined,
    currency: 'RMB'
  },
  sourceId: '',
  name: '',
  nameEng: '',
  categoryId: '',
  code: '',
  sourceFlag: '',
  skuType: '',
  ownBrandFlag: '',
  brandId: '',
  material: '',
  measureUnit: '',
  singleNetweight: {
    weight: '',
    unit: 'g'
  },
  purchaseUserId: '',
  purchaseUserDeptId: '',
  purchaseUserDeptName: '',
  purchaseUserName: '',
  description: '',
  descriptionEng: '',
  annex: [],
  picture: [],
  specLength: undefined,
  specWidth: undefined,
  specHeight: undefined,
  hsdata: {},
  quoteitemList: []
})
const submitData = ref()
const changeTipsRef = ref()
const classTree = ref<Tree[]>([]) // 树形结构
const getClassTreeData = async () => {
  classTree.value = handleTree(await classApi.getClassTree())
}

provide('formData', formData)
const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}

const { close } = inject('dialogEmits') as {
  close: () => void
}
const getUserInfo = (obj) => {
  formData.purchaseUserDeptId = obj.deptId
  formData.purchaseUserDeptName = obj.deptName
  formData.purchaseUserName = obj.nickname
}

const UploadListRef = ref()
const UploadPicRef = ref()
const skuTypeFilter = (arr) => {
  return arr.filter((item) => item.value < 4)
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'custCode',
    label: '客户名称',
    component: (
      <eplus-input-search-select
        api={TravelReimbApi.getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择"
        disabled
      />
    )
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    component: <el-input placeholder="请输入" />,
    rules: {
      required: true,
      message: '请输入客户货号'
    }
  },
  {
    field: 'price',
    label: '销售单价',
    rules: {
      required: true,
      message: '请输入销售单价'
    }
  },
  {
    field: 'sourceId',
    label: '产品名称',
    component: (
      <eplus-input-search-select
        api={skuApi.simpleSkuList}
        params={{ pageSize: 100, pageNo: 1, custProFlag: 0, skuTypeInList: '1, 2, 3' }}
        keyword="skuName"
        label="skuName"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        disabled
      />
    ),
    rules: {
      required: !(props.mode === 'edit'),
      message: '请选择产品'
    }
  },
  {
    field: 'name',
    label: '中文品名',
    component: <el-input placeholder="请输入" />,
    rules: {
      required: true,
      message: '请输入中文品名'
    }
  },
  {
    field: 'nameEng',
    label: '英文品名',
    component: <el-input placeholder="请输入" />,
    rules: {
      required: true,
      message: '请输入英文品名'
    }
  },
  {
    field: 'categoryId',
    label: '产品分类'
  },
  {
    field: 'code',
    label: '产品编码',
    component: (
      <el-input
        disabled
        placeholder="请输入"
      />
    )
  },
  {
    field: 'sourceFlag',
    label: '产品来源',
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.SOURCE_FLAG}
        style="width:100%"
      />
    )
  },
  {
    field: 'skuType',
    label: '产品类型',
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.SKU_TYPE}
        style="width:100%"
        filter={skuTypeFilter}
      />
    )
  },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.IS_INT}
        style="width:100%"
        disabled
      />
    )
  },
  // {
  //   field: 'ownBrandFlag',
  //   label: '是否自主品牌',
  //   component: (
  //     <eplus-dict-select
  //       disabled
  //       dictType={DICT_TYPE.IS_INT}
  //       style="width:100%"
  //     />
  //   )
  // },
  // {
  //   field: 'brandId',
  //   label: '品牌',
  //   disabled: true,
  //   component: (
  //     <eplus-custom-select
  //       disabled
  //       api={brandApi.getBrandSimpleList}
  //       label="name"
  //       value="id"
  //       class="!w-100%"
  //       placeholder="请选择"
  //     />
  //   )
  // },
  {
    field: 'material',
    label: '产品材质',
    component: (
      <el-input
        disabled
        placeholder="请输入"
      />
    )
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.MEASURE_UNIT}
        style="width:100%"
      />
    )
  },
  {
    field: 'spec',
    label: '单品规格'
  },
  {
    field: 'singleNetweight',
    label: '单品净重'
    // component: <el-input placeholder="请输入" />
  }
  // {
  //   field: 'purchaseUserId',
  //   label: '采购员',
  //   component: (
  //     <eplus-user-select
  //       disabled
  //       class="!w-100%"
  //       onChange={getUserInfo}
  //     ></eplus-user-select>
  //   ),
  //   rules: {
  //     required: true,
  //     message: '请选择'
  //   }
  // }
])

const basic2Schemas: EplusFormSchema[] = reactive([
  {
    field: 'description',
    label: '产品说明',
    span: 12,
    component: <el-input type="textarea"></el-input>,
    rules: {
      required: true,
      message: '请输入产品说明'
    }
  },
  {
    field: 'descriptionEng',
    label: '',
    labelWidth: '30px',
    span: 12,
    component: <el-input type="textarea"></el-input>,
    rules: {
      required: true,
      message: '请输入产品说明英文版'
    }
  },
  {
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    field: 'picture',
    label: '图片',
    span: 24
    // rules: {
    //   required: true,
    //   validator: (rule: any, value: any, callback: any) => {
    //     let item = formData.picture.find((item) => item.mainFlag == 1)
    //     if (!formData.picture) {
    //       callback(new Error(`请上传图片`))
    //     } else if (!item) {
    //       callback(new Error(`请设置主图`))
    //     } else {
    //       callback()
    //     }
    //   }
    // }
  }
])
const quoteitemListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'quoteitemList',
    label: '',
    span: 24
  }
])

const getFileList = (params) => {
  formData.annex = params
}
const getPicList = (params) => {
  formData.picture = params
}

const emit = defineEmits(['handleSuccess'])
const formRef = ref()

// 1-普通产品 2-组合产品 3-配件 4-包材
watch(
  () => formData.ownBrandFlag,
  (val, oldVal) => {
    const item = basicSchemas.find((item) => item.field === 'brandId')
    item.disabled = !val == 1
    if (oldVal) {
      formData.brandId = ''
    }
  },
  {
    immediate: true,
    deep: true
  }
)
watch(
  () => formData.sourceId,
  (val, oldVal) => {
    if (props.mode === 'create') {
      getBasicInfo(val)
    } else {
      if (oldVal && val) {
        getBasicInfo(val)
      }
    }
  }
)

const getBasicInfo = async (sourceId) => {
  let res = await skuApi.getSkuInfo({ id: sourceId })
  //获取到的客户产品信息合并到formdata时，需要排除id等字段，否则会覆盖掉formdata中的对应字段
  const { id, auditStatus, code, ...result } = res
  let obj = {
    ...result,
    hsdataCode: result.hsdata?.code || '',
    hsdataUnit: result.hsdata?.unit || '',
    hsdataTaxRefundRate: result.hsdata?.taxRefundRate || '',
    custCode: formData.custCode,
    cskuCode: formData.cskuCode,
    price: {
      amount: formData.price.amount,
      currency: formData.price.currency
    },
    sourceId: sourceId
  }
  Object.assign(formData, obj)
  quoteitemFlag.value = false
}

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)
  if (QuoteItemComRef.value) {
    let result = await QuoteItemComRef.value?.saveForm()
    if (result) {
      params.quoteitemList = result
    } else {
      params.quoteitemList = []
      return false
    }
  }

  return {
    ...params,
    custProFlag: 1
  }
}
const saveForm = async (isChange: boolean, submitFlag) => {
  let paramsResult = await getParams()
  if (!paramsResult) return false
  if (isChange) {
    submitData.value = { ...paramsResult, submitFlag: submitFlag }
    changeTipsRef.value.open(submitData.value, 'sku')
  } else {
    if (submitFlag) {
      await custSkuApi.submitCskuChange(paramsResult.id)
    } else {
      await skuApi.skuChangeUpdate({ ...paramsResult, submitFlag: submitFlag })
    }
    message.success(t('common.updateSuccess'))
    close()
    emit('handleSuccess', 'success')
  }
}

const submitChange = async () => {
  await custSkuApi.cskuChange({ ...submitData.value })
  message.success('提交成功')
  close()
}

onMounted(async () => {
  try {
    loading.value = true
    formRef.value.resetForm()

    let res = await skuApi.getSkuInfo({ id: props.id })
    let obj = {
      ...res,
      hsdataCode: res.hsdata?.code || '',
      hsdataUnit: res.hsdata?.unit || '',
      hsdataTaxRefundRate: res.hsdata?.taxRefundRate || ''
    }
    Object.assign(formData, obj)
    quoteitemFlag.value = false
    if (res.auditStatus === 2) {
      clearDialogActions()
      updateDialogActions(
        <el-button
          onClick={() => {
            saveForm(true, 0)
          }}
          key="changeSku"
          type="primary"
          hasPermi="pms:csku-change:update"
        >
          保存
        </el-button>,
        <el-button
          onClick={() => {
            saveForm(true, 1)
          }}
          key="changeSubmitSku"
          hasPermi="pms:csku-change:submit"
        >
          提交
        </el-button>
      )
    }
    await getClassTreeData()
    await nextTick()
    setTimeout(() => {
      loading.value = false
    }, 100)
  } finally {
    // loading的隐藏已移到setTimeout中
  }
})
</script>
