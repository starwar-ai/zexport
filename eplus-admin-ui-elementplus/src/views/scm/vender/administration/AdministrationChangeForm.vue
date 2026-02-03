<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="formLoading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'scm:vender:create',
      handler: () => submitForm(false)
    }"
    :submitAction="{
      permi: ['scm:vender:create', 'scm:vender:update'],
      handler: () => submitForm(false, 'submit')
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    />

    <eplus-form-items
      title="财务信息"
      :formSchemas="financeSchemas"
    >
      <!-- <template #paymentItem>
        <el-select
          v-model="formData.paymentItem"
          clearable
          style="width: 100%"
          placeholder="请选择付款方式"
          :validate-event="false"
        >
          <el-option
            v-for="dict in paymentListData.paymentList.list"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          />
        </el-select>
      </template> -->
      <template #taxType>
        <el-select
          v-model="formData.taxType"
          clearable
          style="width: 100%"
          placeholder="请选择"
          :validate-event="false"
        >
          <el-option
            v-for="dict in taxTypeList"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </template>

      <!-- component: (
      <eplus-dict-select
        dictType={DICT_TYPE.TAX_TYPE}
        style="width:100%"
      />
    ), -->
    </eplus-form-items>
    <eplus-form-items
      title="联系人信息"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <PocTable
          type="formal"
          :formData="formData"
          ref="pocListRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="银行账户信息"
      :formSchemas="bankAccountSchemas"
    >
      <template #bankaccountList>
        <BankTable
          :formData="formData"
          ref="bankaccountListRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      v-if="editFileShow"
      title="附件信息"
      :formSchemas="annexSchemas"
    >
      <template #annex>
        <UploadList
          v-if="formData.annex"
          ref="UploadListRef"
          :fileList="formData.annex"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />
</template>
<script setup lang="tsx">
import * as VenderApi from '@/api/scm/vender'
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
// import { useCountryStore } from '@/store/modules/countrylist'
import { getSimpleUserList } from '@/api/system/user'
// import { useCountryStore } from '@/store/modules/countrylist'
import UploadList from '@/components/UploadList/index.vue'
import PocTable from '../components/PocTable.vue'
import BankTable from '../components/BankTable.vue'
import ChangeTips from '@/components/ChangeTips/index.vue'
import { useAreaTreeStore } from '@/store/modules/areaTree'
import { useUserStore } from '@/store/modules/user'
import { usePaymentStore } from '@/store/modules/payment'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { cloneDeep } from 'lodash-es'

defineOptions({
  name: 'VenderForm'
})
const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
// const message = useMessage() //顶部tip提示框
// const countryListData = useCountryStore()
const userInfo = useUserStore()
const areaTreeStore = useAreaTreeStore() //省份/城市数据
const paymentListData = usePaymentStore() //付款方式表

const formLoading = ref(true)
const UploadRef = ref()

const changeTipsRef = ref()
const isVenderChange = ref(false)
const submitData = ref()

const pocListRef = ref()
const paymentListRef = ref()
const bankaccountListRef = ref()
const props = defineProps<{
  channel?: string
  id?: number
  mode: EplusFormMode
}>()
const areaTreeList = ref<any[]>([])
const { close } = inject('dialogEmits') as {
  close: () => void
}
const checkNameLoading = ref(true)
const checkNameList = ref<any[]>([])
const editFileShow = ref(false)
const venderList = ref()
const queryId: string = (props.id || '') as string
const formRef = ref() // 表单 Ref
//定义表单初始项
const formData: Recordable = ref({
  stageType: 2,
  pocList: [],
  bankaccountList: [],
  paymentList: [],
  venderType: undefined,
  internalFlag: 0
})
const message = useMessage()
provide('formData', formData)

let taxTypeList = ref<any>([])

//基本信息
const basicSchemas = reactive([
  {
    field: 'name',
    label: '供应商名称',
    component: <el-input placeholder="请输入供应商名称" />,
    rules: {
      required: true,
      message: '请输入供应商名称'
    }
  },
  {
    field: 'nameEng',
    label: '供应商英文名称',
    component: <el-input placeholder="请输入供应商英文名称" />
  },
  {
    field: 'nameShort',
    label: '简称',
    component: <el-input placeholder="请输入简称" />
  },
  {
    field: 'phone',
    label: '供应商电话',
    component: <el-input placeholder="请输入供应商电话" />
  },
  {
    field: 'administrationVenderType',
    label: '行政供应商类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.ADMINISTRATION_VENDER_TYPE}
        style="width:100%"
      />
    )
  },
  {
    field: 'venderType',
    label: '供应商类型',
    disabled: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.VENDER_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择供应商类型'
    }
  },
  {
    field: 'remark',
    label: '备注',
    span: 12,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入备注"
      />
    ),
    rules: {
      required: false,
      message: '请输入备注'
    }
  }
])

watch(
  () => formData.value.currency,
  (val, oldval) => {
    if (val === 'RMB') {
      taxTypeList.value = getIntDictOptions(DICT_TYPE.TAX_TYPE).filter((item) => item.value != 1)
      financeSchemas[1].disabled = false
    } else {
      taxTypeList.value = getIntDictOptions(DICT_TYPE.TAX_TYPE).filter((item) => item.value == 1)
      financeSchemas[1].disabled = true
    }
    if (oldval) {
      formData.value.taxRate = ''
      formData.value.taxType = ''
    }
  }
)
//财务信息
const financeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'currency',
    label: '币种',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择币种'
    }
  },
  {
    field: 'taxRate',
    label: '税率%',
    disabled: true,
    component: (
      <el-input-number
        class="mx-4"
        min={0}
        max={99.99}
        precision={2}
        controls={false}
        style="width:100%;margin:0;textAlign:left"
        placeholder="请输入税率"
      />
    ),
    rules: {
      required: true,
      message: '请输入税率'
    }
  },
  {
    field: 'taxType',
    label: '发票类型',
    rules: {
      required: true,
      message: '请选择发票类型'
    }
  }
  // {
  //   field: 'paymentItem',
  //   label: '付款方式',
  //   rules: {
  //     required: false,
  //     message: '请选择付款方式'
  //   }
  // }
])

//联系人信息
const pocListSchemas: EplusFormSchema[] = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//银行账户信息
const bankAccountSchemas: EplusFormSchema[] = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//附件信息
const getFileList = (params) => {
  formData.value.annex = params
}
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    labelWidth: '0px'
  }
]
//用户列表
const simpleUserList: any = ref([])
const submitForm = async (isChange: boolean, type?: string) => {
  formLoading.value = true
  isVenderChange.value = isChange
  try {
    const reqList = { submitFlag: type === 'submit' ? 1 : 0, ...cloneDeep(formData.value) }
    //处理付款方式
    // reqList.paymentId = reqList.paymentItem
    //地址只传最子节点的code
    if (reqList.companyAreaIdList?.length === 3) {
      reqList.companyAreaId = reqList.companyAreaIdList[2]
    }
    if (reqList.factoryAreaIdList?.length === 3) {
      reqList.factoryAreaId = reqList.factoryAreaIdList[2]
    }
    if (reqList.deliveryAreaIdList?.length === 3) {
      reqList.deliveryAreaId = reqList.deliveryAreaIdList[2]
    }
    //处理联系人和银行信息
    if (paymentListRef.value) {
      let paymentListResult = await paymentListRef.value?.saveForm()
      if (paymentListResult) {
        reqList.paymentList = paymentListResult
      } else {
        formLoading.value = false
        return false
      }
    }
    if (pocListRef.value) {
      let pocListResult = await pocListRef.value?.saveForm()
      if (pocListResult) {
        reqList.pocList = pocListResult
      } else {
        formLoading.value = false
        return false
      }
    }
    if (bankaccountListRef.value) {
      let bankaccountListResult = await bankaccountListRef.value?.saveForm()
      if (bankaccountListResult) {
        reqList.bankaccountList = bankaccountListResult
      } else {
        formLoading.value = false
        return false
      }
    }
    formRef.value
      ?.validate()
      .then(async (res) => {
        if (res) {
          if (queryId) {
            if (isChange) {
              submitData.value = { ...reqList }
              changeTipsRef.value.open(reqList, 'vender')
            } else {
              let req = reqList.submitFlag
                ? VenderApi.changeVenderSubmit(reqList.id)
                : VenderApi.changeVenderUpdate(reqList)
              await req
                .then((res) => {
                  message.success('提交成功')
                  close()
                })
                .catch((err) => {
                  message.error('提交失败')
                })
            }
          }
        } else {
          message.error('请确认必填项')
        }
        formLoading.value = false
      })
      .catch((err) => {
        console.log(err, 'err')
        formLoading.value = false
      })
    formLoading.value = false
  } catch {
    formLoading.value = false
  }
}

const submitChange = async () => {
  if (queryId) {
    // let req = submitData.value.submitFlag ? VenderApi.changeVender : VenderApi.updateVender
    await VenderApi.changeVender(submitData.value)
      .then((res) => {
        message.success('提交成功')
        close()
      })
      .catch((err) => {
        message.error('提交失败')
      })
  }
  message.success('提交成功')
  close()
}

const treeListFormat = (params: any[]) => {
  params.map((item, index, arr) => {
    item.value = item.id
    item.label = item.name
    if (item?.children) {
      treeListFormat(item?.children)
    }
  })
  return params
}
const pagePath = useRoute().path
const setVenderType = () => {
  const index = basicSchemas.findIndex((item) => item.field === 'venderType')

  if (pagePath === '/scm/vender/business') {
    formData.value.venderType = 1
    basicSchemas[index].disabled = true
  } else if (pagePath === '/scm/vender/administration') {
    formData.value.venderType = 2
    basicSchemas[index].disabled = true
  } else {
    basicSchemas[index].disabled = false
  }
}
onMounted(async () => {
  try {
    formLoading.value = true
    // await VenderApi.getPayableVender({ pageNo: 1, pageSize: 100, name: '' }).then((res) => {
    //   venderList.value = res?.list
    // })
    setVenderType()
    simpleUserList.value = await getSimpleUserList()
  //验证当前用户是否存在于业务员列表中
  const userIdCheck = simpleUserList.value.find((val) => {
    if (val.id === userInfo.user.id) return true
  })
  //采购员部门展示
  simpleUserList.value.map((item, index, arr) => {
    if (item.deptName !== arr[index - 1]?.deptName) {
      index !== 0 && (arr[index - 1].endDept = 1)
      item.firstDept = 1
    } else {
    }
  })
  areaTreeList.value = treeListFormat(cloneDeep(areaTreeStore.areaTree))
  // areaTreeList.value = handleTree(areaTreeStore.areaTree, 'id')
  if (queryId) {
    let result = await VenderApi.getVender({ id: props.id })
    result.buyerIds = result.buyerList?.map((item) => {
      return (item = item.userId)
    })
    formData.value = result
    if (!formData.value.pocList) {
      formData.value.pocList = [{ defaultFlag: 1 }]
    }
    //付款方式传参字段调整
    if (!formData.value.paymentList) {
      formData.value.paymentList = [{ defaultFlag: 1 }]
    } else {
      formData.value.paymentList.forEach((value, index, arr) => {
        const { id: paymentId } = value
        const newObj = { paymentId, ...value }
        arr[index] = newObj
      })
    }
    if (!formData.value.annex) {
      formData.value.annex = []
    }
    provide(formData, 'formData')

    if (result.auditStatus === 2) {
      clearDialogActions()
      updateDialogActions(
        <el-button
          onClick={() => {
            submitForm(true)
          }}
          key="changeVender"
          type="primary"
          hasPermi="scm:vender-change:update"
          // hasPermi="scm:vender:update"
        >
          保存
        </el-button>,
        <el-button
          onClick={() => {
            submitForm(true, 'submit')
          }}
          key="changeSubmitVender"
          hasPermi="scm:vender-change:submit"
          // hasPermi="scm:vender:update"
        >
          提交
        </el-button>
      )
    }
  } else {
    formData.value.buyerIds = [userInfo.user?.id]
    formData.value.annex = []
  }
  if (!formData.value.bankaccountList) {
    formData.value.bankaccountList = [{ defaultFlag: 1 }]
  }

  await nextTick()
    editFileShow.value = true
    if (!userIdCheck && !queryId) {
      formData.value.buyerIds = []
    }
    setTimeout(() => {
      formLoading.value = false
    }, 100)
  } finally {
    // formLoading的隐藏已移到setTimeout中
  }
})
onUnmounted(() => {})
</script>
<style lang="scss" scoped>
.el-form-item--default {
  margin-bottom: 0;
}

.el-table__inner-wrapper {
  width: 100%;
}

.buttonContainer {
  width: 100%;
  margin-top: 50px;
}

:deep(.highlight) {
  color: #409eff;
}

:deep(.el-input__inner) {
  text-align: left !important;
}
</style>
