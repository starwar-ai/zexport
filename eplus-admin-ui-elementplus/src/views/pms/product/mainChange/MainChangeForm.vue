<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'pms:sku:create',
      handler: () => submitForm(false, 0)
    }"
    :submitAction="{
      permi: ['pms:sku:submit'],
      handler: () => submitForm(false, 1)
    }"
  >
    <eplus-form-items
      title="产品信息"
      :formSchemas="basicSchemas"
    >
      <template #categoryId>
        <el-tree-select
          v-model="formData.categoryId"
          :data="classTree"
          :props="defaultProps"
          node-key="id"
          placeholder="请选择"
          class="!w-100%"
        />
      </template>

      <template #afterCode>
        <div class="flex !w-100%">
          <el-input
            v-model="formData.preCode"
            placeholder=""
            disabled
          />
          <span>-</span>
          <el-input
            v-model="formData.afterCode"
            placeholder="请输入后缀"
          />
          <!-- <el-button
            v-if="props.mode === 'create'"
            type="primary"
            @click="() => generateCode()"
            >生成编号</el-button
          > -->
        </div>
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
    />
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
import * as HsdataApi from '@/api/system/hsdata'
import UploadList from '@/components/UploadList/index.vue'
// import UploadPic from '@/components/UploadPic/index.vue'
import { defaultProps, handleTree } from '@/utils/tree'
import * as classApi from '@/api/pms/class'
import QuoteItemCom from '../main/components/QuoteItemCom.vue'
import AccessoriesList from '../main/components/AccessoriesList.vue'
import SubProductList from '../main/components/SubProductList.vue'
import AdornInput from '../main/components/AdornInput.vue'
import { cloneDeep } from 'lodash-es'
import { isValidArray } from '@/utils/is'
import ChangeTips from '@/components/ChangeTips/index.vue'

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const fromPage = 'basicPage'
const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'LoanAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(true)
const quoteitemFlag = ref(true)
const formData: EplusAuditable = reactive({
  auditStatus: 0,
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
    field: 'name',
    label: '中文品名',
    component: <el-input placeholder="请输入中文品名" />,
    rules: {
      required: true,
      message: '请输入中文品名'
    }
  },
  {
    field: 'nameEng',
    label: '英文品名',
    component: <el-input placeholder="请输入英文品名" />,
    rules: {
      required: true,
      message: '请输入英文品名'
    }
  },
  {
    field: 'categoryId',
    label: '产品分类',
    rules: {
      required: true,
      message: '请选择产品分类'
    }
  },
  {
    field: 'afterCode',
    label: '产品编码'
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
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SKU_TYPE}
        style="width:100%"
        filter={skuTypeFilter}
      />
    ),
    rules: {
      required: true,
      message: '请输入产品分类'
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
    label: '产品中文说明',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入产品说明"
      ></el-input>
    ),
    rules: {
      required: true,
      message: '请输入产品说明'
    }
  },
  {
    field: 'descriptionEng',
    label: '产品英文说明',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="Please enter the English product description..."
      ></el-input>
    ),
    rules: {
      required: true,
      message: 'Please enter the English product description...'
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
const quoteitemListSchemas: EplusFormSchema[] = reactive([
  {
    field: 'quoteitemList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])

const hsdataCodeChange = (val, list) => {
  let item = list.find((item) => item.id === val)
  formData.hsdata = item || {}
  formData.hsdataUnit = item?.unit || ''
  formData.hsdataTaxRefundRate = item?.taxRefundRate | ''
}
const hsdataSchemas: EplusFormSchema[] = reactive([
  {
    field: 'hsCodeId',
    label: '报关HSCODE',
    component: (
      <eplus-input-search-select
        api={HsdataApi.getHsdataPage}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="code"
        label="code"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={hsdataCodeChange}
      />
    ),
    rules: {
      required: true,
      message: '请选择报关HSCODE'
    }
  },
  {
    field: 'hsdataUnit',
    label: '报关单位',
    component: <el-input disabled></el-input>
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

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

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

  if (QuoteItemComRef.value) {
    let result = await QuoteItemComRef.value?.saveForm()
    if (result) {
      params.quoteitemList = result
    } else {
      params.quoteitemList = []
      return false
    }
  }
  return { ...params, custProFlag: 0, code: formData.preCode + (formData.afterCode ?? '') }
}
const submitForm = async (isChange: boolean, submitFlag) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false

    if (isChange) {
      submitData.value = { ...paramsResult, submitFlag: submitFlag }
      changeTipsRef.value.open(submitData.value, 'sku')
    } else {
      if (submitFlag) {
        await skuApi.submitSkuChange(paramsResult.id)
      } else {
        await skuApi.skuChangeUpdate({ ...paramsResult, submitFlag: submitFlag })
      }

      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
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
  try {
    loading.value = true
    formRef.value.resetForm()
    let item = basicSchemas.find((item) => item.field === 'code')
    // if (props.mode === 'edit') {
    let res = await skuApi.getSkuInfo({ id: props.id })
    // UploadPicRef.value.fileData(res.picture)
    const codeList = res.code?.split('-')
    let obj = {
      ...res,
      hsdataUnit: res.hsdata?.unit || '',
      hsdataTaxRefundRate: res.hsdata?.taxRefundRate || '',
      preCode: codeList[0],
      afterCode: codeList?.length === 2 ? codeList[1] : ''
    }
    Object.assign(formData, obj)
    quoteitemFlag.value = false
    if (item) {
      item.disabled = false
    }
    if (res.auditStatus === 2) {
      clearDialogActions()
      updateDialogActions(
        <el-button
          onClick={() => {
            submitForm(true, 0)
          }}
          key="changeSku"
          type="primary"
          hasPermi="pms:sku-change:update"
          // hasPermi="scm:vender:update"
        >
          保存
        </el-button>,
        <el-button
          onClick={() => {
            submitForm(true, 1)
          }}
          key="changeSubmitSku"
          hasPermi="pms:sku-change:submit"
          // hasPermi="scm:vender:update"
        >
          提交
        </el-button>
      )
    }
    // } else {
    //   if (item) {
    //     item.disabled = true
    //   }
    //   quoteitemFlag.value = false
    // }
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
