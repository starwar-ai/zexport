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
      <template #taxMsg>
        <TaxMsgCom
          :formData="formData"
          ref="TaxMsgComRef"
          v-if="editFileShow"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="联系人信息"
      desc="*邮箱，QQ， 微信至少填写一项"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <PocTable
          :forwarderFlag="props.forwarderFlag"
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
// import { useCountryStore } from '@/store/modules/countrylist'
import UploadList from '@/components/UploadList/index.vue'
import PocTable from '../components/PocTable.vue'
import BankTable from '../components/BankTable.vue'
import TaxMsgCom from '../components/TaxMsgCom.vue'
import { useAreaTreeStore } from '@/store/modules/areaTree'
import { useUserStore } from '@/store/modules/user'
import { DICT_TYPE } from '@/utils/dict'
import { cloneDeep } from 'lodash-es'
import ChangeTips from '@/components/ChangeTips/index.vue'

defineOptions({
  name: 'AdministrationForm'
})

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
// const message = useMessage() //顶部tip提示框
// const countryListData = useCountryStore()
const userInfo = useUserStore()
const areaTreeStore = useAreaTreeStore() //省份/城市数据

const formLoading = ref(false)

const pocListRef = ref()
const paymentListRef = ref()
const bankaccountListRef = ref()
const pagePath = useRoute().path
const props = defineProps<{
  channel?: string
  id?: number
  mode: EplusFormMode
  changeEdit?: Boolean
  forwarderFlag?: Boolean
}>()
const areaTreeList = ref<any[]>([])
const { close } = inject('dialogEmits') as {
  close: () => void
}

const changeTipsRef = ref()
const submitData = ref()
const editFileShow = ref(false)
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
    disabled: props.forwarderFlag,
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

//财务信息
const financeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'taxMsg',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
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
const TaxMsgComRef = ref()
//处理表单数据
const handleLocalFormData = async () => {
  formLoading.value = true
  const reqList = cloneDeep(formData.value)
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
    let paymentListResult = await paymentListRef.value?.tableList
    if (paymentListResult) {
      reqList.paymentList = paymentListResult
    } else {
      reqList.paymentList = []
    }
  }
  if (pocListRef.value) {
    let pocListResult = await pocListRef.value?.tableList
    if (pocListResult) {
      reqList.pocList = pocListResult
    } else {
      reqList.pocList = []
    }
  }
  if (bankaccountListRef.value) {
    let bankaccountListResult = await bankaccountListRef.value?.tableList
    if (bankaccountListResult) {
      reqList.bankaccountList = bankaccountListResult
    } else {
      reqList.bankaccountList = []
    }
  }
  if (TaxMsgComRef.value) {
    let taxMsgComResult = await TaxMsgComRef.value?.saveForm()
    if (taxMsgComResult) {
      reqList.taxMsg = taxMsgComResult
    } else {
      formLoading.value = false
      return false
    }
  }
  formLoading.value = false
  return reqList
}

//表单暂存
const localSaveForm = async () => {
  try {
    let res = await handleLocalFormData()
    localStorage.setItem(pagePath, JSON.stringify({ pagePath: pagePath, data: res }))
    message.success('暂存成功')
    close()
  } catch (e) {
    console.log(e)
  }
}

const submitForm = async (isChange: boolean, type?: string) => {
  formLoading.value = true
  try {
    let pageForm = await handleLocalFormData()
    const reqList = { submitFlag: type === 'submit' ? 1 : 0, ...pageForm }

    let validateRes = await formRef.value?.validate()
    if (!validateRes) {
      message.error('请确认必填项')
      return
    }
    let req
    if (queryId) {
      if (isChange && props.changeEdit) {
        req = reqList.submitFlag
          ? VenderApi.changeVenderSubmit(reqList.id)
          : VenderApi.changeVenderUpdate(reqList)
        await req
        message.success('提交成功')
        close()
      } else if (isChange && !props.changeEdit) {
        submitData.value = { ...reqList }
        changeTipsRef.value.open(reqList, 'vender')
      } else {
        req = props.channel === 'clue' ? VenderApi.toFormalVender : VenderApi.updateVender
        await req(reqList)
        message.success('提交成功')
        close()
      }
    } else {
      await VenderApi.createVender(reqList)
      message.success('提交成功')
      close()
    }
  } catch (e) {
    console.log(e)
  } finally {
    formLoading.value = false
    let localData = localStorage.getItem(pagePath)
    if (localData && props.mode == 'create' && !isChange) {
      localStorage.removeItem(pagePath)
    }
  }
}

const submitChange = async () => {
  if (queryId) {
    await VenderApi.changeVender(submitData.value)
    message.success('提交成功')
    close()
  }
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
const setVenderType = () => {
  formData.value.venderType = 2
}
onMounted(async () => {
  setVenderType()

  areaTreeList.value = treeListFormat(cloneDeep(areaTreeStore.areaTree))
  // areaTreeList.value = handleTree(areaTreeStore.areaTree, 'id')
  if (queryId) {
    let result = await VenderApi.getVender({ id: props.id })
    result.buyerIds = result.buyerList?.map((item) => {
      return (item = item.userId)
    })
    // result.paymentItem = result.paymentItem?.id
    formData.value = result
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

    if (result.auditStatus === 2 || props.changeEdit) {
      clearDialogActions()
      updateDialogActions(
        <el-button
          onClick={() => {
            submitForm(true, 'submit')
          }}
          key="changeSubmitVender"
          hasPermi="scm:vender-change:submit"
        >
          提交
        </el-button>
      )
    }
  } else {
    formData.value.buyerIds = [userInfo.user?.id]
    formData.value.annex = []
  }
  if (props.forwarderFlag && props.mode === 'create') {
    formData.value.administrationVenderType = 2
    formData.value.taxMsg = [{ currency: 'RMB', taxType: 2, taxRate: 0, defaultFlag: 1 }]
  }
  if (!formData.value.bankaccountList) {
    formData.value.bankaccountList = [{ defaultFlag: 1 }]
  }
  if (!formData.value.pocList) {
    formData.value.pocList = [{ defaultFlag: 1 }]
  }
  nextTick(() => {
    editFileShow.value = true
  })

  if (props.channel !== 'clue' && props.mode === 'create') {
    updateDialogActions(
      <el-button
        onClick={() => {
          localSaveForm()
        }}
        key="localSave"
      >
        暂存
      </el-button>
    )
  }

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
        formData.value = localJsonData.data
        return
      })
      .catch(() => {
        localStorage.removeItem(pagePath)
      })
  }
})
onUnmounted(() => {})
</script>
