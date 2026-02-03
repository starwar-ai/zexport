<template>
  <eplus-form
    ref="formRef"
    v-bind="{ id: id, loading: loading, mode: mode }"
    v-model="formData"
    :saveAction="{
      permi: 'scm:purchase-plan:create',
      handler: () => submitForm()
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="productSchemas"
    />
    <eplus-form-items
      title="营业执照信息"
      :formSchemas="annexSchemas"
    >
      <template #license>
        <UploadList
          v-if="formData.license"
          v-bind="{
            ref: 'UploadRef',
            fileList: formData?.license
          }"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>

    <eplus-form-items
      title="公章图片信息"
      :formSchemas="pictureSchemas"
    >
      <template #picture>
        <UploadPicWithDicList
          ref="UploadPicRef"
          :dictType="DICT_TYPE.OFFICIAL_SEAL_TYPE"
          :pictureList="formData.picture"
          @success="getPicList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="企业信息"
      :formSchemas="companySchemas"
    />
    <eplus-form-items
      title="银行信息"
      :formSchemas="bankSchemas"
    >
      <template #companyBanks>
        <eplus-form-table
          ref="TableRef"
          :list="formData.companyBankList"
          :schema="tableSchema"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE } from '@/utils/dict'
import * as AcountConfigApi from '@/api/infra/acountConfig'
import { cloneDeep } from 'lodash-es'

// import { ReportTypeEnum } from '@/utils/constants'
const message = useMessage()
const formRef = ref()
const TableRef = ref()
const loading = ref(false)

const props = defineProps<{
  id?: number
  mode: EplusFormMode
  param?
}>()
const { close } = inject('dialogEmits') as {
  close: () => void
}
const queryId: string = (props.id || '') as string
interface FormData {
  license: any | null
  companyBankList: Array<[]>
  picture: Array<[]>
}
let formData = reactive<FormData>({
  license: [],
  companyBankList: [],
  picture: []
})
let managerReact = reactive({})
const { query } = useRoute()
const { updateDialogActions = () => {}, clearDialogActions = () => {} } = query.id
  ? {}
  : (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
provide('formData', formData)

const getFileList = (params) => {
  formData.license = params
}
const toggleSchemaDisabled = async (val) => {}
let productSchemas: EplusFormSchema[] = reactive([
  {
    field: 'name',
    label: '账套名称',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入账套名称'
      }
    ]
  },
  {
    field: 'companyNature',
    label: '企业性质',
    editable: true,
    span: 6,
    component: (
      <eplus-dict-select
        style="width: 100%"
        dictType={DICT_TYPE.COMPANY_NATURE}
        clearable={true}
        onChangeEmit={(val) => {
          toggleSchemaDisabled(val)
        }}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择企业性质',
        trigger: 'click'
      }
    ]
  }
  // {
  //   field: 'processedFlag',
  //   label: '可加工状态',
  //   editable: true,
  //   span: 6,
  //   component: (
  //     <eplus-dict-select
  //       style="width: 100%"
  //       dictType={DICT_TYPE.PROCESSED_FLAG}
  //       clearable={true}
  //       onChangeEmit={(val) => {
  //         toggleSchemaDisabled(val)
  //       }}
  //     />
  //   ),
  //   rules: [
  //     {
  //       required: true,
  //       message: '请选择可加工状态',
  //       trigger: 'click'
  //     }
  //   ]
  // },
  // {
  //   field: 'isInnerCustomer',
  //   label: '是否内部客户',
  //   editable: true,
  //   span: 6,
  //   component: (
  //     <eplus-dict-select
  //       style="width: 100%"
  //       dictType={DICT_TYPE.IS_INT}
  //       clearable={true}
  //     />
  //   ),
  //   rules: [
  //     {
  //       required: true,
  //       message: '请选择是否内部客户',
  //       trigger: 'click'
  //     }
  //   ]
  // }
])
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'license',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: [
      {
        required: false,
        message: '请上传营业执照'
      }
    ]
  }
]

const pictureSchemas: EplusFormSchema[] = [
  {
    field: 'picture',
    label: '公章图片',
    span: 24,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        var valueEmpty = formData.picture.filter((item) => {
          return !item.value
        })
        var emptyFile = formData.picture.filter((item) => {
          return !item.fileList || item.fileList.length <= 0
        })
        if (valueEmpty && valueEmpty.length > 0) {
          callback(new Error(`请选择图片类型`))
        } else if (emptyFile && emptyFile.length > 0) {
          var message = '请上传' + emptyFile[0].label + '图片'
          callback(new Error(message))
        } else {
          callback()
        }
      }
    }
  }
]
const userChange = (val) => {
  const userId = val?.id
  const { id, ...result } = val
  managerReact = { userId, ...result }
}
const getPicList = (params) => {
  formData.picture = params
  formRef.value.validateField('picture')
}
const companySchemas: EplusFormSchema[] = [
  {
    field: 'companyName',
    label: '企业名称',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入企业名称'
      }
    ]
  },
  {
    field: 'companyNameEng',
    label: '企业英文名称',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入企业英文名称'
      }
    ]
  },
  {
    field: 'licenseNo',
    label: '营业执照号',
    span: 6,
    component: <el-input maxlength="18" />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value) {
          callback(new Error('请输入营业执照号'))
        } else if (value.length !== 18) {
          callback(new Error('请输入18位有效的营业执照号'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'companyAddress',
    label: '企业地址',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入企业地址'
      }
    ]
  },
  {
    field: 'companyAddressEng',
    label: '企业英文地址',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入企业英文地址'
      }
    ]
  },
  {
    field: 'managerId',
    label: '管理员',
    editable: true,
    span: 6,
    component: <eplus-user-select onChange={userChange}></eplus-user-select>,
    rules: [
      {
        required: true,
        message: '请输入管理员'
      }
    ]
  },
  {
    field: 'managerMobile',
    label: '管理员手机号',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入管理员手机号'
      }
    ]
  },
  {
    field: 'managerMail',
    label: '管理员邮箱',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入管理员邮箱'
      }
    ]
  },
  {
    field: 'companyTel',
    label: '企业电话',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入企业电话'
      }
    ]
  },
  {
    field: 'companyFax',
    label: '企业传真',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入企业传真'
      }
    ]
  },
  {
    field: 'availableCurrencyList',
    label: '可用币种',
    span: 6,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        class="!w-100%"
        multiple
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择可用币种'
      }
    ]
  },
  {
    field: 'taxNumb',
    label: '税号',
    editable: true,
    span: 6,
    component: <el-input />,
    rules: [
      {
        required: true,
        message: '请输入税号'
      }
    ]
  },
  {
    field: 'customsNumber',
    label: '海关编码',
    editable: true,
    span: 6,
    component: <el-input />
  }
]

const tableSchema: any = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 60
  },
  // {
  //   label: '企业中文名称',
  //   field: 'companyName',
  //   minWidth: 200,
  //   component: <el-input class="!w-90%" />,
  //   rules: { required: true, message: '请输入企业中文名称' }
  // },
  // {
  //   label: '企业英文名称',
  //   field: 'companyNameEng',
  //   width: 200,
  //   component: <el-input class="!w-90%" />,
  //   rules: { required: true, message: '请输入企业英文名称' }
  // },
  {
    label: '银行中文名称',
    field: 'bankName',
    width: 300,
    component: <el-input class="!w-90%" />
  },
  {
    label: '银行中文地址',
    field: 'bankAddress',
    width: 300,
    component: <el-input class="!w-90%" />
  },
  {
    label: '银行英文名称',
    field: 'bankNameEng',
    width: 300,
    component: <el-input class="!w-90%" />
  },
  {
    label: '银行英文地址',
    field: 'bankAddressEng',
    width: 300,
    component: <el-input class="!w-90%" />
  },
  {
    label: '银行账号',
    field: 'bankCode',
    width: 300,
    component: <el-input class="!w-90%" />,
    rules: { required: true, message: '请输入银行账号' }
  },
  {
    label: 'SWIFT代码(BIC)',
    field: 'swiftCode',
    width: 200,
    component: <el-input class="!w-90%" />
    // rules: { required: true, message: '请输入SWIFT代码(BIC)' }
  },
  {
    label: '操作',
    width: 100,
    fixed: 'right',
    slot: (item, row, index: number, deleteAction: (index: number) => void) => {
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
const bankSchemas: EplusFormSchema[] = [
  {
    field: 'companyBanks',
    label: '',
    editable: true,
    labelWidth: '0px',
    span: 24
  }
]
const getResult = (val) => {
  const { manager } = val || {}
  val.license = [val.license]
  const managerId = manager?.userId
  return { managerId, ...val }
}
onMounted(async () => {
  try {
    if (props?.mode === 'edit') {
      const res = await AcountConfigApi.getAcountConfig({ id: props?.id })
      Object.assign(formData, getResult(res))

      await nextTick()
    }
  } catch {
  } finally {
  }
})
const getParams = async () => {
  let valid = await TableRef.value.validate()
  if (!valid) return false
  const cloneFormData = cloneDeep(formData)
  const { license, companyBankList, manager, ...result } = cloneFormData
  const [licenseObj] = license || []
  const resultBankList = companyBankList?.map((item: any) => {
    const { id, ...resultItem } = item
    return resultItem
  })
  return {
    manager: managerReact?.userId ? managerReact : manager,
    companyBankList: resultBankList,
    license: license?.length ? licenseObj : null,
    ...result
  }
}
const submitForm = async (type?) => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    if (valid) {
      let result: any = await getParams()
      if (result) {
        try {
          if (queryId) {
            await AcountConfigApi.updateAcountConfig({ submitFlag: 0, ...result })
          } else {
            await AcountConfigApi.createAcountConfig({ submitFlag: 0, ...result })
          }
          message.success('提交成功')
          close()
        } catch {}
      } else {
        return false
      }
    }
  } finally {
    loading.value = false
  }
}
</script>
<style scoped lang="scss"></style>
