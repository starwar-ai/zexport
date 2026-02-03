<template>
  <el-form
    ref="quoteFormRef"
    :model="quoteFormData"
    :rules="rules"
    size="small"
    label-width="130px"
  >
    <el-row>
      <el-col :span="8">
        <el-form-item
          label="供应商名称"
          prop="venderCode"
        >
          <eplus-input-search-select
            v-model="quoteFormData.venderCode"
            :api="getVenderSimpleList"
            :params="{ pageSize: 100, pageNo: 1, venderType: 1 }"
            keyword="name"
            label="name"
            value="code"
            class="!w-100%"
            placeholder="请选择"
            @change-emit="(...$event) => getVenderName($event)"
            :defaultObj="{
              code: quoteFormData.venderCode || undefined,
              name: quoteFormData.venderName || undefined,
              id: quoteFormData.venderId || undefined,
              taxRate: quoteFormData.taxRate || undefined,
              currency: quoteFormData.currency || undefined
            }"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="含税单价"
          prop="withTaxPrice"
        >
          <eplus-money
            v-model:amount="quoteFormData.withTaxPrice.amount"
            v-model:currency="quoteFormData.withTaxPrice.currency"
            :currencyDisabled="true"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="总成本单价"
          prop="totalPrice"
        >
          <eplus-money
            v-model:amount="quoteFormData.totalPrice.amount"
            v-model:currency="quoteFormData.totalPrice.currency"
            :amountDisabled="true"
            :currencyDisabled="true"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="报价备注"
          prop="remark"
        >
          <el-input
            v-model="quoteFormData.remark"
            autosize
            type="textarea"
            class="!w-100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="采购员"
          prop="purchaseUserId"
        >
          <eplus-user-select
            v-model="quoteFormData.purchaseUserId"
            @change="
              (...e) => {
                quoteFormData.purchaseUserName = e[0]?.nickname
                quoteFormData.purchaseUserDeptId = e[0]?.deptId
                quoteFormData.purchaseUserDeptName = e[0]?.deptName
              }
            "
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="是否含运费"
          prop="freightFlag"
        >
          <eplus-dict-select
            v-model="quoteFormData.freightFlag"
            :dictType="DICT_TYPE.IS_INT"
            class="!w-100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="运费"
          prop="shippingPrice"
        >
          <eplus-money
            v-model:amount="quoteFormData.shippingPrice.amount"
            v-model:currency="quoteFormData.shippingPrice.currency"
            :currencyDisabled="true"
            :amountDisabled="quoteFormData.freightFlag == 1"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="报价含包装"
          prop="packageFlag"
        >
          <eplus-dict-select
            v-model="quoteFormData.packageFlag"
            :dictType="DICT_TYPE.IS_INT"
            class="!w-100%"
            @change-emit="
              () => {
                quoteFormData.packagingPrice.amount = 0
              }
            "
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="包装方式"
          prop="packageType"
        >
          <eplus-input-search-select
            v-model="quoteFormData.packageType"
            :api="skuApi.packageTypeList"
            :params="{ pageSize: 100, pageNo: 1 }"
            keyword="name"
            label="name"
            value="id"
            multiple
            class="!w-100%"
            placeholder="请选择"
            :clearable="false"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="包装价"
          prop="packagingPrice"
        >
          <eplus-money
            v-model:amount="quoteFormData.packagingPrice.amount"
            v-model:currency="quoteFormData.packagingPrice.currency"
            :currencyDisabled="true"
            :amountDisabled="quoteFormData.packageFlag == 1"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="币种"
          prop="currency"
        >
          <eplus-dict-select
            v-model="quoteFormData.currency"
            :dictType="DICT_TYPE.CURRENCY_TYPE"
            class="!w-100%"
            :clearable="false"
          />
        </el-form-item>
      </el-col>
      <!-- <el-col
        :span="8"
        v-if="quoteFormData.venderCode"
      >
        <el-form-item
          label="是否含税"
          prop="faxFlag"
        >
          <eplus-dict-select
            v-model="quoteFormData.faxFlag"
            :dictType="DICT_TYPE.IS_INT"
            class="!w-100%"
          />
        </el-form-item>
      </el-col> -->
      <el-col :span="8">
        <el-form-item
          label="税率（%）"
          prop="taxRate"
        >
          <EplusNumInput
            v-model="quoteFormData.taxRate"
            :min="0"
            :max="100"
            :precision="2"
            class="!w-100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="起订量"
          prop="moq"
        >
          <el-input-number
            v-model="quoteFormData.moq"
            :min="0"
            :controls="false"
            :precision="0"
            class="!w-100%"
          />
        </el-form-item>
      </el-col>

      <el-col :span="8">
        <el-form-item
          label="报价日期"
          prop="quoteDate"
        >
          <el-date-picker
            v-model="quoteFormData.quoteDate"
            type="date"
            placeholder="请选择"
            value-format="x"
            class="!w-100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="工厂货号"
          prop="venderProdCode"
        >
          <el-input
            v-model="quoteFormData.venderProdCode"
            class="!w-100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="是否拆箱"
          prop="splitBoxFlag"
        >
          <eplus-dict-select
            v-model="quoteFormData.splitBoxFlag"
            :dictType="DICT_TYPE.IS_INT"
            class="!w-100%"
            :clearable="false"
            @change="
              () => {
                if (quoteFormData.splitBoxFlag) {
                  quoteFormData.qtyPerOuterbox = 1
                }
              }
            "
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="内盒装量"
          prop="qtyPerInnerbox"
        >
          <el-input-number
            v-model="quoteFormData.qtyPerInnerbox"
            :min="0"
            :controls="false"
            :precision="0"
            class="!w-100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="外箱装量"
          prop="qtyPerOuterbox"
        >
          <el-input-number
            v-model="quoteFormData.qtyPerOuterbox"
            :min="0"
            :controls="false"
            :precision="0"
            class="!w-100%"
            :disabled="quoteFormData.splitBoxFlag"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="外箱规格">
          <OuterboxSpec
            ref="OuterboxSpecRef"
            :formData="quoteFormData"
          />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script setup lang="tsx">
import { getVenderSimpleList } from '@/api/common'
import * as skuApi from '@/api/pms/main/index.ts'
import { DICT_TYPE } from '@/utils/dict'
import { formatterPrice } from '@/utils/index'
import { useUserStore } from '@/store/modules/user'
import OuterboxSpec from '@/views/pms/product/main/components/OuterboxSpec.vue'

const userInfo = useUserStore().user
const props = defineProps<{
  defaultData?: any
}>()
defineOptions({ name: 'CreateQuete' })
const quoteFormRef = ref()
let quoteFormData = reactive({
  venderCode: undefined,
  venderName: undefined,
  venderId: undefined,
  freightFlag: 1,
  packageFlag: 1,
  packageType: [],
  shippingPrice: {
    amount: 0,
    currency: 'RMB'
  },
  packagingPrice: {
    amount: 0,
    currency: 'RMB'
  },
  purchaseUserId: undefined,
  purchaseUserName: undefined,
  purchaseUserDeptId: undefined,
  purchaseUserDeptName: undefined,
  currency: undefined,
  faxFlag: undefined,
  taxRate: undefined,
  moq: undefined,
  withTaxPrice: {
    amount: 0,
    currency: 'RMB'
  },
  unitPrice: {
    amount: 0,
    currency: 'RMB'
  },
  totalPrice: {
    amount: 0,
    currency: 'RMB'
  },
  venderProdCode: undefined,
  delivery: undefined,
  qtyPerInnerbox: undefined,
  qtyPerOuterbox: undefined,
  packageLength: undefined,
  packageWidth: undefined,
  packageHeight: undefined,
  outerboxLength: undefined,
  outerboxWidth: undefined,
  outerboxHeight: undefined,
  outerboxVolumeStr: undefined,
  outerboxVolume: undefined,
  outerboxGrossweight: {
    weight: undefined,
    unit: 'kg'
  },
  outerboxNetweight: {
    weight: undefined,
    unit: 'kg'
  },
  quoteDate: Date.now(),
  remark: undefined,
  splitBoxFlag: 0
})
const rules = reactive({
  venderCode: { required: true, message: '请选择供应商' },
  packageFlag: { required: true, message: '请选择报价含包装' },
  freightFlag: { required: true, message: '请选择是否含运费' },
  packageType: {
    required: true,
    message: '请选择包装方式'
  },
  shippingPrice: {
    validator: (rule: any, value: any, callback: any) => {
      if (quoteFormData.freightFlag == 0 && quoteFormData.shippingPrice.amount <= 0) {
        callback(new Error(`请输入运费`))
      } else {
        callback()
      }
    }
  },
  packagingPrice: {
    validator: (rule: any, value: any, callback: any) => {
      if (quoteFormData.packageFlag == 0 && quoteFormData.packagingPrice.amount <= 0) {
        callback(new Error(`请输入包装价`))
      } else {
        callback()
      }
    }
  },
  faxFlag: { required: true, message: '请选择是否含税' },
  taxRate: {
    required: true,
    message: '请输入税率'
  },
  moq: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      let regx = /^[1-9]\d*$/
      if (!value) {
        callback(new Error(`请输入起订量`))
      } else if (!regx.test(value)) {
        callback(new Error(`起订量为大于0的整数`))
      } else {
        callback()
      }
    }
  },
  withTaxPrice: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      let amount = quoteFormData.withTaxPrice?.amount
      if (!amount) {
        callback(new Error(`请输入含税单价`))
      } else if (amount <= 0) {
        callback(new Error(`含税单价的值需大于0`))
      } else {
        callback()
      }
    }
  },
  totalPrice: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      callback()
    }
  },
  venderProdCode: [{ required: false, message: '请输入工厂货号' }],
  delivery: {
    required: false,
    validator: (rule: any, value: any, callback: any) => {
      let regx = /^[1-9]\d*$/
      if (!regx.test(value) && value) {
        callback(new Error(`交期为大于0的整数`))
      } else {
        callback()
      }
    }
  },
  qtyPerInnerbox: {
    required: false,
    validator: (rule: any, value: any, callback: any) => {
      let regx = /^[1-9]\d*$/
      if (value && !regx.test(value)) {
        callback(new Error(`内盒装量为大于0的整数`))
      } else {
        callback()
      }
    }
  },
  packageLength: {
    validator: (rule: any, value: any, callback: any) => {
      if (quoteFormData.packageLength && quoteFormData.packageLength <= 0) {
        callback(new Error(`包装规格长度需大于0的值`))
      } else if (quoteFormData.packageWidth && quoteFormData.packageWidth <= 0) {
        callback(new Error('包装规格宽度需大于0的值'))
      } else if (quoteFormData.packageHeight && quoteFormData.packageHeight <= 0) {
        callback(new Error('包装规格高度需大于0的值'))
      } else {
        callback()
      }
    }
  },
  qtyPerOuterbox: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      let regx = /^[1-9]\d*$/
      if (!value) {
        callback(new Error(`请输入外箱装量`))
      } else if (!regx.test(value)) {
        callback(new Error(`外箱装量为大于0的整数`))
      } else {
        callback()
      }
    }
  },
  outerboxLength: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      if (!quoteFormData.outerboxLength) {
        callback(new Error(`请输入单品规格长`))
      } else if (!quoteFormData.outerboxWidth) {
        callback(new Error(`请输入单品规格宽`))
      } else if (!quoteFormData.outerboxHeight) {
        callback(new Error(`请输入单品规格高`))
      } else {
        callback()
      }
    }
  },
  outerboxVolumeStr: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      callback()
    }
  },
  outerboxGrossweight: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      let w = quoteFormData.outerboxGrossweight?.weight,
        u = quoteFormData.outerboxGrossweight?.unit
      if (!w) {
        callback(new Error(`请输入外箱毛重`))
      } else if (w <= 0) {
        callback(new Error(`外箱毛重的值需大于0`))
      } else if (u === 'undefined') {
        callback(new Error('请选择重量单位'))
      } else {
        callback()
      }
    }
  },
  outerboxNetweight: {
    required: true,
    validator: (rule: any, value: any, callback: any) => {
      let w = quoteFormData.outerboxNetweight?.weight,
        u = quoteFormData.outerboxNetweight?.unit
      if (!w) {
        callback(new Error(`请输入外箱净重`))
      } else if (w <= 0) {
        callback(new Error(`外箱净重的值需大于0`))
      } else if (u === 'undefined') {
        callback(new Error('请选择重量单位'))
      } else {
        callback()
      }
    }
  },
  quoteDate: { required: true, message: '请选择报价日期' }
})
const getVenderName = (e) => {
  let item = e[1].find((item) => item.code === e[0])
  if (item.currency) {
    quoteFormData.taxRate = item.taxRate
    quoteFormData.currency = item.currency
    quoteFormData.venderId = item.id
    quoteFormData.venderName = item.name
    quoteFormData.venderCode = item.code
  } else {
    quoteFormData.currency = undefined
    quoteFormData.venderCode = undefined
    quoteFormData.venderId = undefined
    quoteFormData.venderName = undefined
  }
}

watch(
  () => quoteFormData,
  (obj) => {
    //币种赋值
    quoteFormData.shippingPrice.currency = obj.currency
    quoteFormData.packagingPrice.currency = obj.currency
    quoteFormData.withTaxPrice.currency = obj.currency
    quoteFormData.totalPrice.currency = obj.currency
    // 单价赋值
    quoteFormData.totalPrice.amount =
      (Number(obj.packagingPrice.amount) || 0) +
      (Number(obj.withTaxPrice.amount) || 0) +
      (Number(obj.shippingPrice.amount) || 0)
    //unitPrice赋值
    quoteFormData.unitPrice.amount = formatterPrice(
      Number(obj.totalPrice.amount) / (1 + quoteFormData.taxRate / 100)
    )

    quoteFormData.unitPrice.currency = obj.currency
  },
  {
    immediate: true,
    deep: true
  }
)
const OuterboxSpecRef = ref()
const saveForm = async () => {
  let formValid = await quoteFormRef.value.validate()
  let specValid = await OuterboxSpecRef.value.checkData()
  return formValid && specValid
}

const getParams = async () => {
  let specValid = await OuterboxSpecRef.value.checkData()
  if (specValid) {
    return {
      ...quoteFormData,
      shippingPrice: quoteFormData.shippingPrice.amount
        ? quoteFormData.shippingPrice
        : {
            amount: 0,
            currency: quoteFormData.shippingPrice.currency
          },
      packagingPrice: quoteFormData.packagingPrice.amount
        ? quoteFormData.packagingPrice
        : {
            amount: 0,
            currency: quoteFormData.packagingPrice.currency
          },
      specificationList: specValid
    }
  }
}
defineExpose({ saveForm, quoteFormData, getParams })

onMounted(() => {
  quoteFormRef.value?.resetFields()
  Object.assign(quoteFormData, {
    ...props.defaultData,
    id: undefined,
    defaultFlag: 0
  })
  console.log(quoteFormData)
  // Object.assign(quoteFormData, {
  //   venderCode: undefined,
  //   venderName: undefined,
  //   venderId: undefined,
  //   freightFlag: 1,
  //   packageFlag: 1,
  //   packageType: [],
  //   packagingPrice: {
  //     amount: 0,
  //     currency: 'RMB'
  //   },
  //   purchaseUserId: userInfo.id,
  //   purchaseUserName: userInfo.nickname,
  //   purchaseUserDeptId: userInfo.deptId,
  //   purchaseUserDeptName: userInfo.deptName,
  //   currency: undefined,
  //   faxFlag: undefined,
  //   taxRate: undefined,
  //   moq: undefined,
  //   withTaxPrice: {
  //     amount: 0,
  //     currency: 'RMB'
  //   },
  //   unitPrice: {
  //     amount: 0,
  //     currency: 'RMB'
  //   },
  //   totalPrice: {
  //     amount: 0,
  //     currency: 'RMB'
  //   },
  //   venderProdCode: undefined,
  //   delivery: undefined,
  //   qtyPerInnerbox: undefined,
  //   qtyPerOuterbox: undefined,
  //   packageLength: undefined,
  //   packageWidth: undefined,
  //   packageHeight: undefined,
  //   outerboxLength: undefined,
  //   outerboxWidth: undefined,
  //   outerboxHeight: undefined,
  //   outerboxVolumeStr: undefined,
  //   outerboxVolume: undefined,
  //   outerboxGrossweight: {
  //     weight: undefined,
  //     unit: 'kg'
  //   },
  //   outerboxNetweight: {
  //     weight: undefined,
  //     unit: 'kg'
  //   },
  //   quoteDate: Date.now(),
  //   remark: undefined,
  //   splitBoxFlag: 0
  // })
})
</script>
