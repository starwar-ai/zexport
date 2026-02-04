<template>
  <div
    class="mb20px flex"
    v-if="props.feeType"
  >
    <div class="mr40px"> 归集金额 <EplusMoneyLabel :val="props.feeShareAmount" /> </div>
    <div>
      已归集金额
      <EplusMoneyLabel
        :val="{
          amount: feeSharedAmount,
          currency: props.feeShareAmount?.currency
        }"
      />
    </div>
  </div>
  <el-form
    ref="formRef"
    :model="formData"
    label-width="130px"
    label-position="left"
    require-asterisk-position="right"
    class="fee-share-form"
  >
    <el-row
      v-for="(item, index) in formData.list"
      :key="index"
    >
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
      >
        <el-form-item
          :prop="`list.${index}.orderType`"
          label="订单类型"
          :rules="{
            required: true,
            message: '请选择订单类型',
            trigger: 'change'
          }"
        >
          <eplus-dict-select
            v-model="item.orderType"
            :dictType="DICT_TYPE.ORDER_TYPE"
            class="w-100%"
            @change-emit="dataChange(item, 'orderType')"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        v-if="isShow(item, 'feeShareType')"
      >
        <el-form-item
          :prop="`list.${index}.feeShareType`"
          label="归属项"
          :rules="{
            required: true,
            message: '请选择归属项',
            trigger: 'change'
          }"
        >
          <eplus-dict-select
            v-model="item.feeShareType"
            :dictType="DICT_TYPE.FEE_SHARE_TYPE"
            class="w-100%"
            @change-emit="dataChange(item, 'feeShareType')"
            :disabled="props.initData?.type"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        v-if="isShow(item, 'relationType')"
      >
        <el-form-item
          :prop="`list.${index}.relationType`"
          label="相关方类别"
          :rules="{
            required: true,
            message: '请选择相关方类别',
            trigger: 'change'
          }"
        >
          <eplus-dict-select
            v-if="isShow(item, 'NO_ORDER_BUS_TYPE')"
            v-model="item.relationType"
            :dictType="DICT_TYPE.NO_ORDER_BUS_TYPE"
            class="w-100%"
            @change-emit="dataChange(item, 'relationType1')"
            :disabled="props.initData?.type"
          />
          <eplus-dict-select
            v-if="isShow(item, 'COMPANY_OPERATE_TYPE')"
            v-model="item.relationType"
            :dictType="DICT_TYPE.COMPANY_OPERATE_TYPE"
            class="w-100%"
            @change-emit="dataChange(item, 'relationType2')"
            :disabled="props.initData?.type"
          />
          <eplus-dict-select
            v-if="isShow(item, 'MARKET_EXPANSION_TYPE')"
            v-model="item.relationType"
            :dictType="DICT_TYPE.MARKET_EXPANSION_TYPE"
            class="w-100%"
            @change-emit="dataChange(item, 'relationType3')"
            :disabled="props.initData?.type"
          />
          <eplus-dict-select
            v-if="isShow(item, 'PROJECT_MANAGE_TYPE')"
            v-model="item.relationType"
            :dictType="DICT_TYPE.PROJECT_MANAGE_TYPE"
            class="w-100%"
            @change-emit="dataChange(item, 'relationType4')"
            :disabled="props.initData?.type"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        v-if="isShow(item, 'project')"
      >
        <el-form-item
          :prop="`list.${index}.projectId`"
          label="项目名称"
          :rules="{
            required: true,
            message: '请选择项目名称',
            trigger: 'change'
          }"
        >
          <eplus-input-search-select
            v-model="item.projectId"
            :api="FeeShareApi.projectList"
            :params="{ pageSize: 100, pageNo: 1 }"
            keyword="name"
            label="name"
            value="id"
            class="!w-100%"
            placeholder="请选择"
            @change-emit="(...$event) => getName($event, 1, item)"
            :defaultObj="{
              id: item.projectId || undefined,
              name: item.projectName || undefined
            }"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        v-if="isShow(item, 'exhibition')"
      >
        <el-form-item
          :prop="`list.${index}.exhibitionId`"
          label="展会名称"
          :rules="{
            required: true,
            message: '请选择展会名称',
            trigger: 'change'
          }"
        >
          <eplus-input-search-select
            v-model="item.exhibitionId"
            :api="FeeShareApi.exhibitionList"
            :params="{ pageSize: 100, pageNo: 1 }"
            keyword="name"
            label="name"
            value="id"
            class="!w-100%"
            placeholder="请选择"
            @change-emit="(...$event) => getName($event, 1, item)"
            :defaultObj="{
              id: item.exhibitionId || undefined,
              name: item.exhibitionName || undefined
            }"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        v-if="isShow(item, 'brand')"
      >
        <el-form-item label="品牌名称">
          <eplus-dict-select
            v-model="item.brandType"
            :dictType="DICT_TYPE.FEE_BRAND_TYPE"
            class="w-100%"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        v-if="isShow(item, 'descId')"
      >
        <el-form-item
          :prop="`list.${index}.descId`"
          label="费用标签"
          :rules="{
            required: true,
            message: '请选择费用标签',
            trigger: 'change'
          }"
        >
          <DetailCom
            :feeShareType="item.feeShareType"
            :relationType="item.relationType"
            v-model="item.descId"
            @change="(...$event) => getName($event, 3, item)"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        v-if="isShow(item, 'descId') && props.feeType == 1"
      >
        <el-form-item
          :prop="`list.${index}.amount`"
          label="分摊金额"
          :rules="{
            required: true,
            message: '请输入分摊金额',
            trigger: 'blur'
          }"
        >
          <EplusMoney
            v-model:amount="item.amount.amount"
            v-model:currency="item.amount.currency"
            :currencyDisabled="true"
            @change="getTotalAmount(item.amount.amount, `other${index}`)"
          />
        </el-form-item>
      </el-col>
      <el-col
        :xl="4"
        :lg="6"
        :md="8"
        :sm="12"
        :xs="24"
        style="padding-left: 20px"
      >
        <el-button
          v-if="formData.list.length !== 1"
          type="primary"
          @click="handleDel(index)"
          ><Icon icon="ep:delete" />
        </el-button>
        <el-button
          v-if="index === formData.list.length - 1"
          type="primary"
          @click="handleAdd"
          ><Icon icon="ep:plus" />
        </el-button>
      </el-col>
      <el-col
        :span="24"
        v-if="isShow(item, 'crmChildren')"
      >
        <el-form-item
          :prop="`list.${index}.crmChildren`"
          label="客户列表"
        >
          <CrmTable
            v-if="props.initData?.type"
            :formData="item"
          />
          <CrmList
            v-else
            :index="item.hashId"
            :feeType="props.feeType"
            :currency="props.feeShareAmount?.currency"
            :formData="item"
            :ref="(el) => setRef('CrmListRef', item.hashId, el)"
            @change="getTotalAmount"
          />
        </el-form-item>
      </el-col>
      <el-col
        :span="24"
        v-if="isShow(item, 'venderChildren')"
      >
        <el-form-item
          :prop="`list.${index}.venderChildren`"
          label="供应商列表"
        >
          <VenderTable
            v-if="props.initData?.type"
            :formData="item"
          />
          <VenderList
            v-else
            :index="item.hashId"
            :feeType="props.feeType"
            :currency="props.feeShareAmount?.currency"
            :formData="item"
            :ref="(el) => setRef('VenderRef', item.hashId, el)"
            @change="getTotalAmount"
          />
        </el-form-item>
      </el-col>
      <el-col
        :span="24"
        v-if="isShow(item, 'smsChildren')"
      >
        <el-form-item
          :prop="`list.${index}.smsChildren`"
          label="销售订单"
        >
          <SmsList
            :index="item.hashId"
            :feeType="props.feeType"
            :currency="props.feeShareAmount?.currency"
            :formData="item"
            :ref="(el) => setRef('SmsListRef', item.hashId, el)"
            @change="getTotalAmount"
          />
        </el-form-item>
      </el-col>
      <el-col
        :span="24"
        v-if="isShow(item, 'purchaseChildren')"
      >
        <el-form-item
          :prop="`list.${index}.purchaseChildren`"
          label="采购订单"
        >
          <PurchaseList
            :index="item.hashId"
            :feeType="props.feeType"
            :currency="props.feeShareAmount?.currency"
            :formData="item"
            :ref="(el) => setRef('PurchaseListRef', item.hashId, el)"
            @change="getTotalAmount"
          />
        </el-form-item>
      </el-col>
      <el-col
        :span="24"
        v-if="isShow(item, 'deptList')"
      >
        <el-form-item
          :prop="`list.${index}.deptList`"
          label="部门"
        >
          <UserList
            :index="item.hashId"
            :feeType="props.feeType"
            :currency="props.feeShareAmount?.currency"
            :formData="item"
            :ref="(el) => setRef('UserListRef', item.hashId, el)"
            @change="getTotalAmount"
          />
        </el-form-item>
      </el-col>
      <!-- <el-col
        :span="24"
        v-if="isShow(item, 'userList')"
      >
        <el-form-item
          :prop="`list.${index}.deptList`"
          label="个人"
        >
          <UserList
            :index="index"
            :feeType="props.feeType"
            :currency="props.feeShareAmount?.currency"
            :formData="item"
            :ref="(el) => setRef('UserListRef', index, el)"
            @change="getTotalAmount"
          />
        </el-form-item>
      </el-col> -->

      <el-divider />
    </el-row>
  </el-form>
</template>
<script setup lang="tsx">
import { EplusFormMode } from '@/components/EplusTemplate'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import CrmList from './CrmList.vue'
import VenderList from './VenderList.vue'
import CrmTable from './CrmTable.vue'
import VenderTable from './VenderTable.vue'
import SmsList from './SmsList.vue'
import PurchaseList from './PurchaseList.vue'
import DetailCom from './DetailCom.vue'
import UserList from './UserList.vue'
import * as FeeShareApi from '@/api/common/feeShare'
import { cloneDeep } from 'lodash-es'
import { isValidArray } from '@/utils/is'
import { formatterPrice, generateUUID } from '@/utils'
import { moneyTotalPrecision } from '@/utils/config'

const message = useMessage()

defineOptions({ name: 'FeeShareForm' })

const props = withDefaults(
  defineProps<{
    id?: number
    mode: EplusFormMode
    info?: any
    getApi?: any
    updateApi?: any
    channel?: any
    businessType?: any
    businessId?: any
    businessCode?: any
    initData?: any
    feeShareAmount?: any
    feeType?: any
  }>(),
  {
    feeType: 1
  }
)

const isShow = (item, type) => {
  if (type === 'feeShareType') {
    return getDictLabel(DICT_TYPE.ORDER_TYPE, item.orderType) === '无订单'
  } else if (type === 'relationType') {
    return item.feeShareType && getDictLabel(DICT_TYPE.ORDER_TYPE, item.orderType) === '无订单'
  } else if (type === 'NO_ORDER_BUS_TYPE') {
    return getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '部门费用'
  } else if (type === 'COMPANY_OPERATE_TYPE') {
    return getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '公司运营'
  } else if (type === 'MARKET_EXPANSION_TYPE') {
    return getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '市场拓展'
  } else if (type === 'PROJECT_MANAGE_TYPE') {
    return getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '项目管理'
  } else if (type === 'descId') {
    let label = getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType)
    if (label === '项目管理') {
      return true
    } else if (['市场拓展', '公司运营'].includes(label)) {
      return item.relationType
    } else {
      return false
    }
  } else if (type === 'crmChildren') {
    return (
      getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '部门费用' &&
      getDictLabel(DICT_TYPE.NO_ORDER_BUS_TYPE, item.relationType) === '核心客户'
    )
  } else if (type === 'venderChildren') {
    return (
      getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '部门费用' &&
      getDictLabel(DICT_TYPE.NO_ORDER_BUS_TYPE, item.relationType) === '核心供应商'
    )
  } else if (type === 'smsChildren') {
    return getDictLabel(DICT_TYPE.ORDER_TYPE, item.orderType) === '销售合同'
  } else if (type === 'purchaseChildren') {
    return getDictLabel(DICT_TYPE.ORDER_TYPE, item.orderType) === '采购合同'
  } else if (type === 'deptList') {
    return (
      getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '部门费用' &&
      getDictLabel(DICT_TYPE.NO_ORDER_BUS_TYPE, item.relationType) === '部门'
    )
  } else if (type === 'userList') {
    return (
      getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '部门费用' &&
      getDictLabel(DICT_TYPE.NO_ORDER_BUS_TYPE, item.relationType) === '个人'
    )
  } else if (type === 'project') {
    return (
      getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '项目管理' &&
      getDictLabel(DICT_TYPE.PROJECT_MANAGE_TYPE, item.relationType) === '研发项目'
    )
  } else if (type === 'exhibition') {
    return (
      getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '项目管理' &&
      getDictLabel(DICT_TYPE.PROJECT_MANAGE_TYPE, item.relationType) === '展会项目'
    )
  } else if (type === 'brand') {
    return (
      getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, item.feeShareType) === '项目管理' &&
      getDictLabel(DICT_TYPE.PROJECT_MANAGE_TYPE, item.relationType) === '品牌项目'
    )
  }
}

const dataChange = (item, type) => {
  if (type === 'orderType') {
    item.feeShareType = ''
    let orderTypeLabel = getDictLabel(DICT_TYPE.ORDER_TYPE, item.orderType)
    let orderTypeList = formData.list.filter((el) => el.orderType === item.orderType)
    if (
      (orderTypeLabel === '销售合同' || orderTypeLabel === '采购合同') &&
      orderTypeList.length > 1
    ) {
      item.orderType = ''
      message.warning(`${orderTypeLabel}订单类型不能同时存在`)
    }
  } else if (type === 'feeShareType') {
    item.relationType = ''
  } else if (type === 'relationType1') {
    let relationTypeLabel = getDictLabel(DICT_TYPE.NO_ORDER_BUS_TYPE, item.relationType)
    let relationTypeList = formData.list
      .map((el) => {
        if (getDictLabel(DICT_TYPE.FEE_SHARE_TYPE, el.feeShareType) === '部门费用') {
          return el.relationType
        }
      })
      .filter((el2) => el2 == item.relationType)
    if (relationTypeList.length > 1) {
      item.relationType = ''
      message.warning(`${relationTypeLabel}相关方类别不能同时存在`)
    }
  }
  item.descId = ''
  item.descName = ''
  item.amount = {
    amount: 0,
    currency: props.feeShareAmount?.currency || ''
  }
  getTotalAmount()
}

const loading = ref(false)
const feeSharedAmount = ref(0)
let formData = reactive({
  list: [] as any[]
})
provide('formData', formData)

const handleAdd = () => {
  let obj = {
    orderType: '',
    feeShareType: null,
    relationType: null,
    descId: null,
    descName: null,
    crmChildren: [],
    venderChildren: [],
    smsChildren: [],
    purchaseChildren: [],
    amount: {
      amount: '',
      currency: props.feeShareAmount?.currency || ''
    },
    hashId: generateUUID()
  }
  formData.list.push(obj)
}

const handleDel = (index) => {
  formData.list.splice(index, 1)
  getTotalAmount()
}

const { close } = inject('dialogEmits') as {
  close: () => void
}
const feeShareRefList = ref({})
const setRef = (name, index, el) => {
  feeShareRefList.value[`${name}${index}`] = el
}

// const CrmListRef = ref()
// const VenderListRef = ref()
// const SmsListRef = ref()
// const PurchaseListRef = ref()
// const smsFilter = (code) => {
//   SmsListRef.value.handleFilter(code)
// }

// const purchaseFilter = (code) => {
//   PurchaseListRef.value.handleFilter(code)
// }
// const venderCodes = ref([])
// const setVenderCode = (list) => {
//   venderCodes.value = list.map((item) => item.code)
// }
// const custCodes = ref([])
// const setCustCode = (list) => {
//   custCodes.value = list.map((item) => item.code)
// }
// const relationTypeChange = () => {
//   formData.descId = null
//   formData.descName = null
// }

const getName = (e, type, item) => {
  if (type == 1) {
    // let item = e[1].find((item) => item.code === e[0])
    // item.descName = item.name
  } else if (type == 3) {
    item.descName = e[0].name
  }
}
const getTotalAmount = async () => {
  // feeSharedAmount.value = 0
  // amountObj[type] = Number(val)
  // Object.values(amountObj).forEach((item: number) => {
  //   feeSharedAmount.value += item
  // })

  let params: any = cloneDeep(formData.list)
  let total = 0
  for (let index = 0; index < params.length; index++) {
    const item = params[index]
    total += Number(item.amount?.amount) || 0
    if (
      Object.hasOwn(feeShareRefList.value, `CrmListRef${item.hashId}`) &&
      feeShareRefList.value[`CrmListRef${item.hashId}`]
    ) {
      item.crmChildren = feeShareRefList.value[`CrmListRef${item.hashId}`]?.tableList
      total += feeShareRefList.value[`CrmListRef${item.hashId}`]?.totalAmount || 0
    }
    if (
      Object.hasOwn(feeShareRefList.value, `VenderRef${item.hashId}`) &&
      feeShareRefList.value[`VenderRef${item.hashId}`]
    ) {
      item.venderChildren = feeShareRefList.value[`VenderRef${item.hashId}`]?.tableList
      total += feeShareRefList.value[`VenderRef${item.hashId}`]?.totalAmount || 0
    }
    if (
      Object.hasOwn(feeShareRefList.value, `SmsListRef${item.hashId}`) &&
      feeShareRefList.value[`SmsListRef${item.hashId}`]
    ) {
      item.smsChildren = feeShareRefList.value[`SmsListRef${item.hashId}`]?.tableList
      total += feeShareRefList.value[`SmsListRef${item.hashId}`]?.totalAmount || 0
    }
    if (
      Object.hasOwn(feeShareRefList.value, `PurchaseListRef${item.hashId}`) &&
      feeShareRefList.value[`PurchaseListRef${item.hashId}`]
    ) {
      item.purchaseChildren = feeShareRefList.value[`PurchaseListRef${item.hashId}`]?.tableList
      total += feeShareRefList.value[`PurchaseListRef${item.hashId}`]?.totalAmount || 0
    }

    if (
      Object.hasOwn(feeShareRefList.value, `UserListRef${item.hashId}`) &&
      feeShareRefList.value[`UserListRef${item.hashId}`]
    ) {
      item.userChildren = feeShareRefList.value[`UserListRef${item.hashId}`]?.tableList
      total += feeShareRefList.value[`UserListRef${item.hashId}`]?.totalAmount || 0
    }
  }
  feeSharedAmount.value = total
  formData.list = params
}

const formRef = ref()

const getParams = async (flag = true) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params: any = cloneDeep(formData.list)
  let total = 0
  for (let index = 0; index < params.length; index++) {
    const item = params[index]
    total += Number(item.amount?.amount) || 0
    if (
      Object.hasOwn(feeShareRefList.value, `CrmListRef${item.hashId}`) &&
      feeShareRefList.value[`CrmListRef${item.hashId}`]
    ) {
      let result = await feeShareRefList.value[`CrmListRef${item.hashId}`]?.checkData()
      if (result) {
        item.crmChildren = result
        total += feeShareRefList.value[`CrmListRef${item.hashId}`]?.totalAmount || 0
      } else {
        item.crmChildren = []
        return false
      }
    } else {
      item.crmChildren = []
    }
    if (
      Object.hasOwn(feeShareRefList.value, `VenderRef${item.hashId}`) &&
      feeShareRefList.value[`VenderRef${item.hashId}`]
    ) {
      let result = await feeShareRefList.value[`VenderRef${item.hashId}`]?.checkData()
      if (result) {
        item.venderChildren = result
        total += feeShareRefList.value[`VenderRef${item.hashId}`]?.totalAmount || 0
      } else {
        item.venderChildren = []
        return false
      }
    } else {
      item.venderChildren = []
    }
    if (
      Object.hasOwn(feeShareRefList.value, `SmsListRef${item.hashId}`) &&
      feeShareRefList.value[`SmsListRef${item.hashId}`]
    ) {
      let result = await feeShareRefList.value[`SmsListRef${item.hashId}`]?.checkData()
      if (result) {
        item.smsChildren = result
        total += feeShareRefList.value[`SmsListRef${item.hashId}`]?.totalAmount || 0
      } else {
        item.smsChildren = []
        return false
      }
    } else {
      item.smsChildren = []
    }
    if (
      Object.hasOwn(feeShareRefList.value, `PurchaseListRef${item.hashId}`) &&
      feeShareRefList.value[`PurchaseListRef${item.hashId}`]
    ) {
      let result = await feeShareRefList.value[`PurchaseListRef${item.hashId}`]?.checkData()
      if (result) {
        item.purchaseChildren = result
        total += feeShareRefList.value[`PurchaseListRef${item.hashId}`]?.totalAmount || 0
      } else {
        item.purchaseChildren = []
        return false
      }
    } else {
      item.purchaseChildren = []
    }
    if (
      Object.hasOwn(feeShareRefList.value, `DeptListRef${item.hashId}`) &&
      feeShareRefList.value[`DeptListRef${item.hashId}`]
    ) {
      let result = await feeShareRefList.value[`DeptListRef${item.hashId}`]?.checkData()
      if (result) {
        item.deptChildren = result
        total += feeShareRefList.value[`DeptListRef${item.hashId}`]?.totalAmount || 0
      } else {
        item.deptChildren = []
        return false
      }
    } else {
      item.deptChildren = []
    }

    if (
      Object.hasOwn(feeShareRefList.value, `UserListRef${item.hashId}`) &&
      feeShareRefList.value[`UserListRef${item.hashId}`]
    ) {
      let result = await feeShareRefList.value[`UserListRef${item.hashId}`]?.checkData()
      if (result) {
        item.userChildren = result
        total += feeShareRefList.value[`UserListRef${item.hashId}`]?.totalAmount || 0
      } else {
        item.userChildren = []
        return false
      }
    } else {
      item.userChildren = []
    }
  }
  feeSharedAmount.value = total
  if (flag && props.feeShareAmount) {
    if (
      Number(formatterPrice(props.feeShareAmount.amount, moneyTotalPrecision)) !==
      Number(formatterPrice(feeSharedAmount.value, moneyTotalPrecision))
    ) {
      message.warning('请确认已归集金额是否与归集金额相等')
      return false
    }
  }
  return params
}

const { updateDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
}
const emit = defineEmits(['save', 'success'])

const submitForm = async () => {
  let paramsResult = await getParams()
  if (!paramsResult) return false
  emit('save', paramsResult)
  close()
}
const initParams = ref({})
const updateForm = async () => {
  let paramsResult = await getParams()
  if (!paramsResult) return false
  initParams.value.auxiliaryType = 1
  initParams.value.feeShare = paramsResult
  let params = {}
  if (['send'].includes(props?.channel)) {
    params = {
      sendId: props?.businessId ? props?.businessId : props.id,
      feeShare: paramsResult
    }
  } else {
    params = {
      businessType: props?.businessType,
      businessId: props?.businessId,
      businessCode: props?.businessCode,
      submitFlag: 1,
      feeShare: paramsResult
    }
  }

  await props.updateApi(params)

  message.success('保存成功')
  close()
}

// 监听 props.info 的变化，确保异步数据加载完成后能正确更新
watch(
  () => props.info,
  (newInfo) => {
    if (isValidArray(newInfo)) {
      formData.list = newInfo
      getParams(false)
    }
  },
  { immediate: true, deep: true }
)

defineExpose({ getParams })
onMounted(async () => {
  if (props.feeType == 0 && !isValidArray(props.info)) {
    handleAdd()
  } else {
    //编辑页面展示
    if (!isValidArray(props.info) && !props?.info?.id && !props.id && !props.initData?.type) {
      if (isValidArray(props.initData)) {
        formData.list = [
          ...props.initData.map((item) => {
            return {
              ...item,
              hashId: generateUUID(),
              amount: {
                amount: item.amount?.amount,
                currency: props.feeShareAmount?.currency
              }
            }
          })
        ]
      } else {
        handleAdd()
      }

      updateDialogActions(
        <el-button
          type="primary"
          onClick={submitForm}
          key="submit"
        >
          提交
        </el-button>
      )
    }

    // if (props.initData?.type && props.mode == 'create') {
    //   updateDialogActions(
    //     <el-button
    //       type="primary"
    //       onClick={submitForm}
    //       key="createSubmit"
    //     >
    //       提交
    //     </el-button>
    //   )
    // }

    if (props.id) {
      let res = ['forwarder', 'entertain', 'corporatePayments'].includes(props?.channel)
        ? await props.getApi({ id: props?.businessId || props.id })
        : await props.getApi(props?.businessId || props.id)
      initParams.value = cloneDeep({ ...res, businessCode: props.businessCode })
      getParams(false)
      if (isValidArray(initParams.value?.feeShare)) {
        formData.list = initParams.value?.feeShare.map((item) => {
          return {
            ...item,
            hashId: generateUUID()
          }
        })
      } else {
        handleAdd()
      }

      updateDialogActions(
        <el-button
          type="primary"
          onClick={updateForm}
          key="createSave"
        >
          保存
        </el-button>
      )

      // if (['ems'].includes(props?.channel)) {
      //   updateDialogActions(
      //     <el-button
      //       type="primary"
      //       onClick={submitForm}
      //       key="createSubmit"
      //     >
      //       提交
      //     </el-button>
      //   )
      // } else {

      // }
    }
  }
})
</script>

<style lang="scss" scoped>
.fee-share-form {
  width: 100%;
}

.fee-share-form :deep(.el-form-item__label) {
  padding-left: 20px !important;
  font-weight: 600;
}
</style>
