<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :localSaveAction="{
      handler: () => localSaveForm()
    }"
    :saveAction="{
      permi: 'pms:csku:create',
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: 'pms:csku:submit',
      handler: () => saveForm(1)
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
          :precision="moneyPrecision"
        />
      </template>
      <template #sourceId>
        {{ formData.skuName }}
        <el-button
          v-if="props.mode !== 'edit' && props.mode !== 'change'"
          class="ml10px"
          type="primary"
          @click="selectSku"
          >选择产品
        </el-button>
      </template>
      <!--  -->
      <template #categoryId>
        <div class="flex">
          <el-tree-select
            v-model="formData.categoryId"
            :data="classTree"
            :props="defaultProps"
            node-key="id"
            placeholder="请选择"
            filterable
            class="!w-100%"
            @change="categoryChange"
          />
          <div class="ml-2 min-w-220px">{{ formData.categoryName }}</div>
        </div>
      </template>

      <template #spec>
        <SpecCom
          v-model:length="formData.specLength"
          v-model:width="formData.specWidth"
          v-model:height="formData.specHeight"
        />
      </template>
      <template #singleNetweight>
        <EplusWeight
          v-model:weight="formData.singleNetweight.weight"
          v-model:unit="formData.singleNetweight.unit"
          class="!w-100%"
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
    </eplus-form-items>

    <MainTableDetail
      :info="formData"
      AuxiliaryFlag
      SubProductFlag
    />
    <eplus-form-items
      title=" 组合产品信息"
      :formSchemas="subProductSchemas"
      v-if="formData.skuType == 2"
    >
      <template #subProductList>
        <SubProductList
          :formData="formData"
          ref="subProductListRef"
        />
      </template>
    </eplus-form-items>

    <eplus-form-items
      title="包材配比"
      :formSchemas="skuAuxiliaryListSchemas"
    >
      <template #skuAuxiliaryList>
        <SkuAuxiliaryListCom
          :formData="formData"
          ref="SkuAuxiliaryListComRef"
        />
      </template>
    </eplus-form-items>

    <eplus-form-items
      title="供应商报价"
      :formSchemas="quoteitemListSchemas"
    >
      <template #quoteitemList>
        <QuoteItemCom
          :formData="formData"
          ref="QuoteItemComRef"
          v-if="!quoteitemFlag"
          :fromPage="fromPage"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="报关信息"
      :formSchemas="hsdataSchemas"
    >
      <template #hsCode>
        {{ formData.hsCode || formData.hsdata?.code || '' }}
        <el-button
          @click="
            () => {
              selectHsCodeRef.open()
            }
          "
          >选择HsCode</el-button
        >
      </template>
    </eplus-form-items>
    <!-- <HsdataDetail :info="formData" /> -->
  </eplus-form>

  <SelectSkuCom
    :ownFlag="true"
    ref="SelectSkuComRef"
    @sure="skuSure"
  />
  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />
  <SelectHsCode
    ref="selectHsCodeRef"
    @sure="hsdataCodeChange"
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as skuApi from '@/api/pms/main/index.ts'
import * as ownSkuApi from '@/api/pms/own/index.ts'
import { EplusMoney } from '@/components/EplusMoney'
import UploadList from '@/components/UploadList/index.vue'
// import UploadPic from '@/components/UploadPic/index.vue'
import SpecCom from '../components/SpecCom.vue'
import SelectSkuCom from '../components/SelectSku.vue'
import { defaultProps, handleTree } from '@/utils/tree'
import * as classApi from '@/api/pms/class'
import * as brandApi from '@/api/pms/brand'
import SkuAuxiliaryListCom from '../main/components/SkuAuxiliaryListCom.vue'
import MainTableDetail from '../main/components/MainTableDetail.vue'
import QuoteItemCom from '../main/components/QuoteItemCom.vue'
import SelectHsCode from '@/views/pms/product/components/SelectHsCode.vue'
import { cloneDeep } from 'lodash-es'
import ChangeTips from '@/components/ChangeTips/index.vue'
import { UploadZoomPic } from '@/components/UploadPic'
import { isValidArray } from '@/utils/is'
import SubProductList from '../main/components/SubProductList.vue'
import { formatterEngDes, getCategoryName } from '@/utils'
import { moneyPrecision } from '@/utils/config'
import { useUserStore } from '@/store/modules/user'

const userInfo = useUserStore().getUser
const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const fromPage = 'ownPage'
const subProductListRef = ref()
const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'OwnProductForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  changeEdit?: Boolean
}>()
const changeTipsRef = ref()
const submitData = ref()
const quoteitemFlag = ref(true)
const SkuAuxiliaryListComRef = ref()
const QuoteItemComRef = ref()
const loading = ref(false)
const pagePath = useRoute().path
const formData: EplusAuditable = reactive({
  auditStatus: 0,
  // custCode: '',
  // cskuCode: '',
  price: {
    amount: undefined,
    currency: 'USD'
  },
  skuName: '',
  sourceCode: '',
  sourceId: '',
  name: '',
  nameEng: '',
  categoryId: '',
  code: '',
  sourceFlag: '',
  skuType: '',
  ownBrandFlag: 1,
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
  quoteitemList: [],
  belongingDeptId: '',
  belongingDeptName: ''
})
const classTree = ref<Tree[]>([]) // 树形结构
const getClassTreeData = async () => {
  classTree.value = handleTree(await classApi.getClassTree())
}

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
// const getUserInfo = (obj) => {
//   formData.purchaseUserDeptId = obj.deptId
//   formData.purchaseUserDeptName = obj.deptName
//   formData.purchaseUserName = obj.nickname
// }

const UploadListRef = ref()

const categoryChange = async (val) => {
  formData.categoryName = await getCategoryName(val, classTree.value)
}

const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'basicSkuCode',
    label: '基础产品编码',
    readOnly: true
  },
  {
    field: 'sourceId',
    label: '产品名称',
    rules: {
      required: !(props.mode === 'edit'),
      message: '请选择产品'
    }
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    component: <el-input placeholder="请输入" />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入自营产品货号'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'price',
    label: '销售价格',
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.amount) {
          callback(new Error('请输入销售价格'))
        } else {
          callback()
        }
      }
    }
  },

  {
    field: 'name',
    label: '中文品名',
    component: (
      <el-input
        placeholder="请输入"
        onBlur={(e) => {
          const val = e.target.value
          if (val && !formData.declarationName) {
            formData.declarationName = val
          }
        }}
      />
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入中文品名'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'declarationName',
    label: '报关中文品名',
    component: <el-input placeholder="请输入" />
  },
  {
    field: 'nameEng',
    label: '英文品名',
    component: (
      <el-input
        placeholder="请输入"
        onBlur={(e) => {
          const val = e.target.value
          if (val && !formData.customsDeclarationNameEng) {
            formData.customsDeclarationNameEng = val
          }
        }}
      />
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入英文品名'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文品名',
    component: <el-input placeholder="请输入" />
  },
  {
    field: 'categoryId',
    label: '产品分类',
    xl: 8,
    lg: 12,
    rules: {
      required: true,
      message: '请选择产品分类'
    }
  },
  {
    field: 'sourceFlag',
    label: '产品来源',
    readOnly: true,
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.SOURCE_FLAG, val)
    }
  },
  {
    field: 'skuType',
    label: '产品类型',
    readOnly: true,
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.SKU_TYPE, val)
    }
  },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.IS_INT}
        style="width:100%"
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
  {
    field: 'brandId',
    label: '品牌',
    component: (
      <eplus-custom-select
        api={brandApi.getBrandSimpleList}
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
      />
    ),
    rules: {
      required: true,
      message: '请选择品牌'
    }
  },
  {
    field: 'material',
    label: '产品材质',
    readOnly: true
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    readOnly: true,
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.MEASURE_UNIT, val)
    }
  },
  {
    field: 'spec',
    xl: 8,
    lg: 12,
    label: '单品规格'
  },
  {
    field: 'singleNetweight',
    label: '单品净重'
  },
  {
    field: 'belongingDeptId',
    label: '归属部门',
    component: (
      <eplus-dept-select
        onChange={(item) => {
          formData.belongingDeptName = item?.name || ''
        }}
      />
    ),
    rules: {
      required: true,
      message: '请选择归属部门'
    }
  },
  {
    field: 'barcode',
    label: '条形码',
    component: <el-input placeholder="请输入" />
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
    label: '中文描述',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入产品中文描述"
        rows="15"
      ></el-input>
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('请输入产品中文描述'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'descriptionEng',
    label: '英文描述',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="Please enter the English product description..."
        rows="15"
        formatter={(value) => formatterEngDes(value)}
      ></el-input>
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.trim()) {
          callback(new Error('Please enter the English product description...'))
        } else {
          callback()
        }
      }
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
    span: 24,
    component: <UploadZoomPic mainFlag />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let item = formData.picture.find((item) => item.mainFlag == 1)
        if (!formData.picture) {
          callback(new Error(`请上传图片`))
        } else if (!item) {
          callback(new Error(`请设置主图`))
        } else {
          callback()
        }
      }
    }
  }
])
const skuAuxiliaryListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'skuAuxiliaryList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])

const subProductSchemas: EplusFormSchema[] = reactive([
  {
    field: 'subProductList',
    label: '包含产品',
    span: 24,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!isValidArray(subProductListRef.value.tableList)) {
          callback(new Error(`请选择产品`))
        } else {
          callback()
        }
      }
    }
  }
])

const quoteitemListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'quoteitemList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])
const hsdataSchemas: EplusFormSchema[] = reactive([
  {
    field: 'hsCode',
    label: '报关HSCODE',
    rules: {
      required: true,
      message: '请选择报关HSCODE'
    }
  },
  {
    field: 'hsdataUnit',
    label: '报关单位',
    component: <el-input disabled />
  },
  {
    field: 'hsdataTaxRefundRate',
    label: '退税率（%）',
    component: <el-input disabled />
  }
])

const selectHsCodeRef = ref()
const hsdataCodeChange = (val) => {
  formData.hsdata = val || {}
  formData.hsCodeId = val?.id || ''
  formData.hsCode = val?.code || ''
  formData.hsdataUnit = val?.unit || ''
  formData.hsdataTaxRefundRate = val?.taxRefundRate || ''
}

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
const SelectSkuComRef = ref()
const selectSku = () => {
  SelectSkuComRef.value?.open()
}

const skuSure = (data) => {
  formData.sourceId = data.id
  formData.sourceCode = data.code || ''
  formData.skuName = data.skuName
}

const getBasicInfo = async (sourceId) => {
  let res = await skuApi.getSkuInfo({ id: sourceId })
  //获取到的客户产品信息合并到formdata时，需要排除id等字段，否则会覆盖掉formdata中的对应字段
  const { id, auditStatus, code, ...result } = res
  result?.subProductList?.forEach((e) => {
    e.skuCode = e.code
  })
  let obj = {
    ...result,
    hsCode: result.hsdata?.code || '',
    hsdataUnit: result.hsdata?.unit || '',
    hsdataTaxRefundRate: result.hsdata?.taxRefundRate || '',
    // custCode: formData.custCode,
    // cskuCode: formData.cskuCode,
    price: {
      amount: formData.price.amount,
      currency: formData.price.currency
    },
    sourceId: sourceId,
    sourceCode: code,
    skuName: result.name,
    brandId: formData.brandId || result.brandId
  }
  Object.assign(formData, obj)
  formData.belongingDeptId = userInfo.deptId
  formData.belongingDeptName = userInfo.deptName
  nextTick(() => {
    quoteitemFlag.value = false
    QuoteItemComRef.value.init()
  })
}

//获取本地数据
const getLocalParams = async () => {
  let params = cloneDeep(formData)
  if (SkuAuxiliaryListComRef.value) {
    params.skuAuxiliaryList = await SkuAuxiliaryListComRef.value?.tableList
  }

  if (QuoteItemComRef.value) {
    params.quoteitemDTOList = await QuoteItemComRef.value?.tableList
  }

  if (subProductListRef.value) {
    params.subProductList = await subProductListRef.value?.tableList
  }
  return params
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
  if (SkuAuxiliaryListComRef.value) {
    let result = await SkuAuxiliaryListComRef.value?.checkData()
    if (result) {
      params.skuAuxiliaryList = result
    } else {
      params.skuAuxiliaryList = []
      return false
    }
  }
  if (subProductListRef.value) {
    let result = await subProductListRef.value?.checkData()
    if (result) {
      params.subProductList = result
    } else {
      params.subProductList = []
      return false
    }
  }
  if (props.mode === 'edit' || props.mode === 'change') {
    return {
      ...params,
      ownBrandFlag: 1,
      custProFlag: 0
    }
  } else {
    return {
      ...params,
      id: undefined,
      code: null,
      ownBrandFlag: 1,
      custProFlag: 0,
      auditStatus: 0,
      createTime: undefined
    }
  }
}

//表单暂存
const localSaveForm = async () => {
  try {
    let paramsResult = await getLocalParams()
    if (!paramsResult) return
    localStorage.setItem(pagePath, JSON.stringify({ pagePath: pagePath, data: paramsResult }))
    message.success('暂存成功')
    close()
  } catch (e) {
    console.log(e)
  }
}

const saveForm = async (submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create' || props.mode == 'copy') {
      await ownSkuApi.ownSkuCreate({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await ownSkuApi.ownSkuUpdate({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
    let localData = localStorage.getItem(pagePath)
    if (localData && props.mode == 'create') {
      localStorage.removeItem(pagePath)
    }
  }
}

const submitForm = async (isChange: boolean, submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (isChange && props.changeEdit) {
      if (submitFlag) {
        await ownSkuApi.submitOwnSkuChange(paramsResult.id)
      } else {
        await ownSkuApi.updateOwnSku({ ...paramsResult, submitFlag: submitFlag })
      }

      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      submitData.value = { ...paramsResult, submitFlag: submitFlag }
      changeTipsRef.value.open(submitData.value, 'sku')
    }
  } finally {
    loading.value = false
  }
}

const submitChange = async () => {
  await ownSkuApi.ownSkuChange({ ...submitData.value })
  message.success('提交成功')
  close()
}

onMounted(async () => {
  formRef.value.resetForm()

  if (props.mode === 'edit' || props.mode === 'change' || props.mode == 'copy') {
    let res = await skuApi.getSkuInfo({ id: props.id })
    res?.subProductList?.forEach((e) => {
      e.skuCode = e.code
    })
    let obj = {
      ...res,
      hsCode: res.hsdata?.code || '',
      hsdataUnit: res.hsdata?.unit || '',
      hsdataTaxRefundRate: res.hsdata?.taxRefundRate || ''
    }
    Object.assign(formData, obj)
    quoteitemFlag.value = false

    if (res.changeStatus !== 2 && res.auditStatus == 2 && props.mode != 'copy') {
      clearDialogActions()
      updateDialogActions(
        <el-button
          type="primary"
          onClick={() => {
            submitForm(true, 1)
          }}
          key="changeSubmitSku"
          hasPermi="pms:own-brand-change:submit"
        >
          提交
        </el-button>
      )
    }
  } else {
    formData.belongingDeptId = userInfo.deptId
    formData.belongingDeptName = userInfo.deptName
  }

  getClassTreeData()

  //增加判断是否加载本地数据
  let localData = localStorage.getItem(pagePath)
  if (localData && props.mode == 'create') {
    ElMessageBox.confirm('检测到上次有未提交的数据，是否加载？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        let localJsonData = JSON.parse(localData!!)
        Object.assign(formData, localJsonData.data)
        nextTick(() => {
          QuoteItemComRef.value.init()
        })
        return
      })
      .catch(() => {
        localStorage.removeItem(pagePath)
      })
  }
})
</script>
