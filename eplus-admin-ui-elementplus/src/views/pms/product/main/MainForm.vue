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
      permi: 'pms:sku:create',
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: ['pms:sku:submit'],
      handler: () => saveForm(1)
    }"
  >
    <eplus-form-items
      title="产品信息"
      :formSchemas="basicSchemas"
    >
      <template #categoryId>
        <div class="flex">
          <el-tree-select
            v-model="formData.categoryId"
            :data="classTree"
            :props="defaultProps"
            node-key="id"
            placeholder="请选择"
            class="!w-100%"
            filterable
            @change="generateCode"
          />
          <div class="ml-2 min-w-220px">{{ formData.categoryName }}</div>
        </div>
      </template>
      <template #afterCode>
        <div
          class="flex !w-100%"
          v-if="['copy', 'create'].includes(props.mode)"
        >
          <el-input
            v-model="formData.preCode"
            placeholder=""
            disabled
            class="!w-150%"
          />
          <el-input
            v-model="formData.xhCode"
            placeholder="序号"
          />
          <el-input
            v-model="formData.afterCode"
            placeholder="后缀"
          />
        </div>
        <el-input
          v-else
          v-model="formData.code"
          :disabled="!['edit'].includes(props.mode)"
        />
      </template>
      <template #code>
        <el-input
          v-model="formData.code"
          :disabled="!['edit'].includes(props.mode)"
        />
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
      <!-- <template #picture>
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.picture"
          @success="getPicList"
          mainFlag
        />
      </template> -->
    </eplus-form-items>
    <eplus-form-items
      title="关联配件"
      :formSchemas="accessoriesSchemas"
      v-if="[1, 2].includes(formData.skuType)"
    >
      <template #accessoriesList>
        <AccessoriesList
          :formData="formData"
          ref="accessoriesListRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title=" 组合产品信息"
      :formSchemas="subProductSchemas"
      v-if="formData.skuType == 2"
    >
      <template #singleProcessFee>
        <adorn-input
          v-model="formData.singleProcessFee"
          placeholder="请输入"
          appendVal="RMB"
        />
      </template>
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
        {{ formData.hsCode || '' }} <el-button @click="handleHsCode">选择HsCode</el-button>
      </template>
    </eplus-form-items>
  </eplus-form>
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
import { DICT_TYPE } from '@/utils/dict'
import * as skuApi from '@/api/pms/main/index.ts'
import UploadList from '@/components/UploadList/index.vue'
// import UploadPic from '@/components/UploadPic/index.vue'
import SpecCom from '../components/SpecCom.vue'
import { defaultProps, handleTree } from '@/utils/tree'
import * as classApi from '@/api/pms/class'
import QuoteItemCom from './components/QuoteItemCom.vue'
import AccessoriesList from './components/AccessoriesList.vue'
import SubProductList from './components/SubProductList.vue'
import AdornInput from './components/AdornInput.vue'
import SelectHsCode from '@/views/pms/product/components/SelectHsCode.vue'
import SkuAuxiliaryListCom from './components/SkuAuxiliaryListCom.vue'
import { cloneDeep } from 'lodash-es'
import { isValidArray } from '@/utils/is'
import { UploadZoomPic } from '@/components/UploadPic'
import ChangeTips from '@/components/ChangeTips/index.vue'
import { SplitKey } from '@/utils/config'
import { EplusDictRadio } from '@/components/EplusSelect'
import { formatterEngDes, getCategoryName } from '@/utils'

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const fromPage = 'basicPage'
const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'MainForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  changeEdit?: Boolean
}>()

const changeTipsRef = ref()
const submitData = ref()
const pagePath = useRoute().path

const loading = ref(false)
const quoteitemFlag = ref(true)
// const hsCodeList = ref([])
let formData: EplusAuditable = reactive({
  auditStatus: 0,
  name: '',
  nameEng: '',
  declarationName: '',
  customsDeclarationNameEng: '',
  categoryId: '',
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
//克隆formdata便于判断用户是否填写信息
// let formDataClone
const classTree = ref<Tree[]>([]) // 树形结构
const oldClassTree = ref([])
const getClassTreeData = async () => {
  let res = await classApi.getClassTree()
  oldClassTree.value = res
  classTree.value = handleTree(res)
}

provide('formData', formData)

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
const generateCode = async (val, isHsCode = false) => {
  formData.categoryName = await getCategoryName(val, classTree.value)
  if (formData.categoryId && ['create', 'copy'].includes(props.mode)) {
    try {
      let autoCode = await skuApi.generateCode(formData.categoryId)
      if (autoCode.length < 3) {
        message.error('自动生成序号异常，长度小于3')
        return false
      }
      formData.xhCode = autoCode.substring(autoCode.length - 3)
      formData.preCode = autoCode.substring(0, autoCode.length - 3)
      if (!isHsCode) {
        let treeData = oldClassTree.value?.find((item) => item.id === val)
        if (!treeData?.hsDataCode) {
          formData.hsdata = {}
          formData.hsCode = ''
          formData.hsCodeId = ''
          formData.hsdataUnit = ''
          formData.hsdataTaxRefundRate = ''
          return false
        }
        let hsData = await skuApi.getHscodeByCode(treeData?.hsDataCode)
        formData.hsdata = hsData || {}
        formData.hsCode = hsData?.code
        formData.hsCodeId = hsData?.id
        formData.hsdataUnit = hsData?.unit || ''
        formData.hsdataTaxRefundRate = hsData?.taxRefundRate || ''
      }
    } catch (e) {}
  }
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'afterCode',
    label: '产品编码',
    disabled: !['copy', 'create'].includes(props.mode)
  },
  {
    field: 'code',
    label: '产品编码',
    disabled: ['copy', 'create'].includes(props.mode),
    rules: {
      required: true,
      message: '请输入产品编码'
    }
  },
  {
    field: 'name',
    label: '中文品名',
    component: (
      <el-input
        placeholder="请输入中文品名"
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
    component: <el-input placeholder="请输入报关中文品名" />
  },
  {
    field: 'nameEng',
    label: '英文品名',
    component: (
      <el-input
        placeholder="请输入英文品名"
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
    component: <el-input placeholder="请输入报关英文品名" />
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
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SOURCE_FLAG}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择产品来源'
    }
  },
  {
    field: 'skuType',
    label: '产品类型',
    xl: 8,
    lg: 12,
    component: (
      <EplusDictRadio
        dictType={DICT_TYPE.SKU_TYPE}
        filter={skuTypeFilter}
        isBtn
      />
    ),
    rules: {
      required: true,
      message: '请选择产品类型'
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
    ),
    rules: {
      required: true,
      message: '请选择'
    }
  },
  // {
  //   field: 'ownBrandFlag',
  //   label: '是否自主品牌',
  //   component: (
  //     <eplus-dict-select
  //       dictType={DICT_TYPE.IS_INT}
  //       style="width:100%"
  //     />
  //   ),
  //   rules: {
  //     required: true,
  //     message: '请选择'
  //   }
  // },
  // {
  //   field: 'brandId',
  //   label: '品牌',
  //   disabled: true,
  //   component: (
  //     <eplus-custom-select
  //       api={brandApi.getBrandSimpleList}
  //       label="name"
  //       value="id"
  //       class="!w-100%"
  //       placeholder="请选择品牌"
  //     />
  //   ),
  //   rules: {
  //     required: true,
  //     message: '请选择品牌'
  //   }
  // },
  {
    field: 'material',
    label: '产品材质',
    component: <el-input placeholder="请输入产品材质" />
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.MEASURE_UNIT}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择计量单位'
    }
  },
  {
    field: 'spec',
    label: '单品规格',
    xl: 8,
    lg: 12
    // rules: {
    //   required: true,
    //   validator: (rule: any, value: any, callback: any) => {
    //     if (!formData.specLength) {
    //       callback(new Error(`请输入单品规格长`))
    //     } else if (!formData.specWidth) {
    //       callback(new Error(`请输入单品规格宽`))
    //     } else if (!formData.specHeight) {
    //       callback(new Error(`请输入单品规格高`))
    //     } else {
    //       callback()
    //     }
    //   }
    // }
  },
  {
    field: 'singleNetweight',
    label: '单品净重',
    rules: {
      required: false
      // validator: (rule: any, value: any, callback: any) => {
      //   if (!formData.singleNetweight.weight) {
      //     callback(new Error(`请输入单品净重`))
      //   } else {
      //     callback()
      //   }
      // }
    }
  }
  // {
  //   field: 'purchaseUserId',
  //   label: '采购员',
  //   component: (
  //     <eplus-user-select
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
        formatter={(value) => formatterEngDes(value)}
        rows="15"
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

const getFileList = (params) => {
  formData.annex = params
}
const getPicList = (params) => {
  formData.picture = params
}

const accessoriesSchemas: EplusFormSchema[] = reactive([
  {
    field: 'accessoriesList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])
const subProductSchemas: EplusFormSchema[] = reactive([
  {
    field: 'singleProcessFee',
    label: '单位加工费',
    component: (
      <adorn-input
        placeholder="请输入"
        appendVal="RMB"
      />
    )
  },
  {
    field: 'processRemark',
    label: '加工备注',
    component: <el-input placeholder="请输入" />
  },
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
const skuAuxiliaryListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'skuAuxiliaryList',
    label: '',
    span: 24,
    labelWidth: '0px'
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

const selectHsCodeRef = ref()

const handleHsCode = () => {
  selectHsCodeRef.value.open()
}
const hsdataCodeChange = (val) => {
  formData.hsdata = val || {}
  formData.hsCodeId = val?.id || ''
  formData.hsCode = val?.code || ''
  formData.hsdataUnit = val?.unit || ''
  formData.hsdataTaxRefundRate = val?.taxRefundRate || ''
}
const hsdataSchemas: EplusFormSchema[] = reactive([
  {
    field: 'hsCode',
    label: '报关HSCODE',
    // component: (
    //   <eplus-input-search-select
    //     v-model={formData.hsCodeId}
    //     api={HsdataApi.getHsdataPage}
    //     params={{ pageSize: 100, pageNo: 1 }}
    //     keyword="code"
    //     label="code"
    //     value="id"
    //     class="!w-100%"
    //     placeholder="请选择"
    //     onChangeEmit={hsdataCodeChange}
    //   />
    // ),
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
    label: '退税率',
    component: (
      <AdornInput
        disabled
        appendVal="%"
      />
    )
  }
])

const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const subProductListRef = ref()
const accessoriesListRef = ref()
const QuoteItemComRef = ref()
const SkuAuxiliaryListComRef = ref()
// 1-普通产品 2-组合产品 3-配件 4-包材
// watch(
//   () => formData.ownBrandFlag,
//   (val, oldVal) => {
//     const item = basicSchemas.find((item) => item.field === 'brandId')
//     item.disabled = !val == 1
//     if (oldVal) {
//       formData.brandId = ''
//     }
//   },
//   {
//     immediate: true,
//     deep: true
//   }
// )

//获取本地数据
const getLocalParams = async () => {
  let params = cloneDeep(formData)
  if (subProductListRef.value) {
    params.subProductList = await subProductListRef.value?.tableList
  }

  if (accessoriesListRef.value) {
    params.accessoriesList = await accessoriesListRef.value?.tableList
  }

  if (SkuAuxiliaryListComRef.value) {
    let tableList = await SkuAuxiliaryListComRef.value?.tableList
    if (tableList?.length > 0) {
      params.skuAuxiliaryList = tableList.map((item) => {
        return {
          ...item,
          skuRate: item.skuRateAndAuxiliarySkuRate?.split(SplitKey)[0],
          auxiliarySkuRate: item.skuRateAndAuxiliarySkuRate?.split(SplitKey)[1]
        }
      })
    } else {
      params.skuAuxiliaryList = []
    }
  }
  if (QuoteItemComRef.value) {
    params.quoteitemDTOList = await QuoteItemComRef.value?.tableList
  }
  return params
}

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  // 处理图片压缩
  // if (params.picture && params.picture.length > 0) {
  //   console.log('params.picture', params.picture)
  //   const compressedPictures = await Promise.all(
  //     params.picture.map(async (pic) => {
  //       if (pic.fileUrl && pic.mainFlag == 1) {
  //         const realUrl = `${import.meta.env.VITE_BASE_URL}/admin-api${pic.fileUrl}`
  //         console.log('realUrl', realUrl)
  //         const thumbnail = await generateThumbnail(realUrl, {
  //           width: 100,
  //           height: 100,
  //           quality: 0.1
  //         })
  //         console.log('thumbnail', thumbnail)
  //         return {
  //           ...pic,
  //           fileUrl: thumbnail
  //         }
  //       }
  //       return pic
  //     })
  //   )
  //   console.log('compressedPictures', compressedPictures)
  //   params.thumbnail = compressedPictures
  // }

  if (subProductListRef.value) {
    let result = await subProductListRef.value?.checkData()
    if (result) {
      params.subProductList = result
    } else {
      params.subProductList = []
      return false
    }
  }

  if (accessoriesListRef.value) {
    let result = await accessoriesListRef.value?.checkData()
    if (result) {
      params.accessoriesList = accessoriesListRef.value.tableList
    } else {
      params.accessoriesList = []
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

  if (QuoteItemComRef.value) {
    let result = await QuoteItemComRef.value?.saveForm()
    if (result) {
      params.quoteitemList = result
    } else {
      message.warning('请先添加供应商报价')
      params.quoteitemList = []
      return false
    }
  }
  const { afterCode, ...paramsRes } = params
  return {
    ...paramsRes,
    skuType: params?.skuType ? params.skuType : 1,
    custProFlag: 0,
    code: ['create', 'copy'].includes(props.mode)
      ? `${formData.preCode}${formData.xhCode ? formData.xhCode : ''}${formData.afterCode ? formData.afterCode : ''}`
      : params.code
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
    //判断用户是否修改formdata
    // if (isEqual(formDataClone, formData)) {
    //   message.warning('请先填写信息')
    //   return false
    // }
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create' || props.mode == 'copy') {
      await skuApi.createSku({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await skuApi.updateSku({ ...paramsResult, submitFlag: submitFlag })
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
    var localData = localStorage.getItem(pagePath)
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
        await skuApi.submitSkuChange(paramsResult.id)
      } else {
        await skuApi.skuChangeUpdate({ ...paramsResult, submitFlag: submitFlag })
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
  await skuApi.skuChange({ ...submitData.value })
  message.success('提交成功')
  close()
}

onMounted(async () => {
  formRef.value.resetForm()
  // let resHsData = await HsdataApi.getHsdataPage({ pageSize: 100, pageNo: 1, code: '' })
  // hsCodeList.value = resHsData?.list
  await getClassTreeData()

  if (props.mode === 'edit' || props.mode === 'change' || props.mode === 'copy') {
    const res = await skuApi.getSkuInfo({ id: props.id })
    let obj = {
      ...res,
      hsdataUnit: res.hsdata?.unit || '',
      hsCode: res.hsdata?.code || '',
      hsdataTaxRefundRate: res.hsdata?.taxRefundRate || '',
      hsCodeId: res.hsdata?.id || ''
    }
    Object.assign(formData, obj)
    quoteitemFlag.value = false

    if (res.auditStatus === 2 && props.mode != 'copy') {
      clearDialogActions()
      updateDialogActions(
        <el-button
          type="primary"
          onClick={() => {
            submitForm(true, 1)
          }}
          key="changeSubmitSku"
          hasPermi="pms:sku-change:submit"
        >
          提交
        </el-button>
      )
    }

    if (props.mode === 'copy') {
      await generateCode(formData.categoryId, true)
    }
  } else {
    quoteitemFlag.value = false
  }
  nextTick(() => {
    QuoteItemComRef.value.init()
  })

  //增加判断是否加载本地数据
  var localData = localStorage.getItem(pagePath)
  if (localData && props.mode == 'create') {
    ElMessageBox.confirm('检测到上次有未提交的数据，是否加载？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        var localJsonData = JSON.parse(localData!!)
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
