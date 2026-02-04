<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="pageType === 'add' ? '新增报价' : '编辑报价'"
    width="80%"
    append-to-body
    destroy-on-close
  >
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
              :params="{ pageSize: 100, pageNo: 1, venderType: 1, skuQuoteFlag: 1 }"
              keyword="name"
              label="name"
              value="code"
              class="!w-100%"
              placeholder="请选择"
              @change-emit="(...$event) => getVenderName($event)"
              :defaultObj="venderDefault"
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
              @change-emit="
                () => {
                  quoteFormData.shippingPrice.amount = 0
                }
              "
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
              class="!w-100%"
              placeholder="请选择"
              :clearable="false"
              :multiple="true"
              @change-emit="
                (...e) => {
                  let nameList = []
                  e[1].forEach((item) => {
                    e[0].forEach((el) => {
                      if (item.id === el) {
                        nameList.push(item.name)
                      }
                    })
                  })
                  quoteFormData.packageTypeName = nameList.join(',')
                }
              "
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
              :filter="
                (list) => {
                  if(isValidArray(taxMsg)){
                    let taxMsgList = taxMsg.map((item:any) => item.currency)
                  return list.filter((item) => {
                    return taxMsgList.includes(item.value)
                  })
                  }else{
                    return list
                  }
                 
                }
              "
            />
          </el-form-item>
        </el-col>
        <!-- <el-col
          :span="8"
          
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
              @change="() => splitBoxFlagChange()"
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

        <!-- <el-col
          :span="8"
          
        >
          <el-form-item
            label="包装规格"
            prop="packageLength"
          >
            <SpecCom
              v-model:length="quoteFormData.packageLength"
              v-model:width="quoteFormData.packageWidth"
              v-model:height="quoteFormData.packageHeight"
            />
          </el-form-item>
        </el-col> -->
        <!-- <el-col
          :span="8"
          
        >
          <el-form-item
            label="外箱装量"
            prop="qtyPerOuterbox"
          >
            <AdornInput
              v-model="quoteFormData.qtyPerOuterbox"
              appendVal="pcs"
              class="!w-100%"
            />
          </el-form-item>
        </el-col>
        <el-col
          :span="8"
          
        >
          <el-form-item
            label="外箱规格"
            prop="outerboxLength"
          >
            <SpecCom
              v-model:length="quoteFormData.outerboxLength"
              v-model:width="quoteFormData.outerboxWidth"
              v-model:height="quoteFormData.outerboxHeight"
            />
          </el-form-item>
        </el-col>
        <el-col
          :span="8"
          
        >
          <el-form-item
            label="外箱体积"
            prop="outerboxVolumeStr"
          >
            <AdornInput
              v-model="quoteFormData.outerboxVolumeStr"
              disabled
              :appendVal="VolumeUnit"
              class="!w-100%"
            />
          </el-form-item>
        </el-col>
        <el-col
          :span="8"
          
        >
          <el-form-item
            label="外箱毛重"
            prop="outerboxGrossweight"
          >
            <EplusWeight
              v-model:weight="quoteFormData.outerboxGrossweight.weight"
              v-model:unit="quoteFormData.outerboxGrossweight.unit"
            />
          </el-form-item>
        </el-col>
        <el-col
          :span="8"
          
        >
          <el-form-item
            label="外箱净重"
            prop="outerboxNetweight"
          >
            <EplusWeight
              v-model:weight="quoteFormData.outerboxNetweight.weight"
              v-model:unit="quoteFormData.outerboxNetweight.unit"
            />
          </el-form-item>
        </el-col> -->
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { getVenderSimpleList } from '@/api/common'
import * as skuApi from '@/api/pms/main/index.ts'
import OuterboxSpec from './OuterboxSpec.vue'
import { DICT_TYPE } from '@/utils/dict'
import { useUserStore } from '@/store/modules/user'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

const userInfo = useUserStore().user

defineOptions({ name: 'QuoteDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
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
    amount: undefined,
    currency: 'RMB'
  },
  totalPrice: {
    amount: undefined,
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
    required: true
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
        u = quoteFormData.outerboxGrossweight?.unit,
        w2 = quoteFormData.outerboxNetweight?.weight
      if (!w) {
        callback(new Error(`请输入外箱毛重`))
      } else if (w <= 0) {
        callback(new Error(`外箱毛重的值需大于0`))
      } else if (w2 && w < w2) {
        callback(new Error(`外箱毛重的值需大于等于外箱净重的值`))
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
  quoteDate: { required: true, message: '请选择日期' }
})
let taxMsg = ref([])
const getVenderName = (e) => {
  let item = e[1].find((item) => item.code === e[0])
  if (item.currency) {
    quoteFormData.faxFlag = 1
    quoteFormData.taxRate = quoteFormData.taxRate || item.taxRate
    quoteFormData.currency = item.currency
    quoteFormData.venderId = item.id
    quoteFormData.venderName = item.name
    taxMsg.value = item.taxMsg
  } else {
    quoteFormData.currency = undefined
    quoteFormData.taxRate = undefined
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

    //外箱体积计算
    // let [l, w, h] = [
    //   quoteFormData.outerboxLength,
    //   quoteFormData.outerboxWidth,
    //   quoteFormData.outerboxHeight
    // ]
    // if (l > 0 && w > 0 && h > 0) {
    //   quoteFormData.outerboxVolumeStr = formatterPrice((l * w * h) / 1000000)
    //   quoteFormData.outerboxVolume = formatterPrice(l * w * h)
    // }
  },
  {
    immediate: true,
    deep: true
  }
)
// watch(
//   () => quoteFormData.outerboxGrossweight.unit,
//   (val) => {
//     quoteFormData.outerboxNetweight.unit = val
//   }
// )
// watch(
//   () => quoteFormData.outerboxNetweight.unit,
//   (val) => {
//     quoteFormData.outerboxGrossweight.unit = val
//   }
// )

const emit = defineEmits(['sure'])
const editIndex = ref(0)
const pageType = ref('add')
const venderDefault = ref({})
const open = async (type, obj, index = 0) => {
  editIndex.value = index
  pageType.value = type
  let row = cloneDeep(obj)
  if (type === 'edit') {
    let res = await getVenderSimpleList({
      pageSize: 1,
      pageNo: 1,
      venderType: 1,
      skuQuoteFlag: 1,
      code: row.venderCode
    })
    venderDefault.value = res?.list[0] || {}
    taxMsg.value = venderDefault.value.taxMsg || []
    Object.assign(quoteFormData, {
      ...row,
      shippingPrice: row.shippingPrice
        ? row.shippingPrice
        : {
            amount: undefined,
            currency: row.currency
          },
      packagingPrice: row.packagingPrice
        ? row.packagingPrice
        : {
            amount: undefined,
            currency: row.currency
          }
    })
  } else {
    Object.assign(quoteFormData, {
      venderCode: undefined,
      venderName: undefined,
      venderId: undefined,
      freightFlag: 1,
      packageFlag: 1,
      packageType: [],
      shippingPrice: {
        amount: undefined,
        currency: 'RMB'
      },
      packagingPrice: {
        amount: undefined,
        currency: 'RMB'
      },
      purchaseUserId: userInfo.id,
      purchaseUserName: userInfo.nickname,
      purchaseUserDeptId: userInfo.deptId,
      purchaseUserDeptName: userInfo.deptName,
      currency: undefined,
      faxFlag: undefined,
      taxRate: undefined,
      moq: undefined,
      withTaxPrice: {
        amount: undefined,
        currency: 'RMB'
      },
      totalPrice: {
        amount: undefined,
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
    quoteFormRef.value?.resetFields()
    venderDefault.value = {}
  }

  dialogTableVisible.value = true
}

const handleCancel = () => {
  dialogTableVisible.value = false
}
const OuterboxSpecRef = ref()

const splitBoxFlagChange = () => {
  if (!quoteFormData.splitBoxFlag && OuterboxSpecRef.value.tableList.length > 1) {
    message.warning('检测到有多条规格，是否拆箱不可以修改为否')
    quoteFormData.splitBoxFlag = 1
    return false
  }
  if (quoteFormData.splitBoxFlag) {
    quoteFormData.qtyPerOuterbox = 1
  }
}

const handleSure = async () => {
  let formValid = await quoteFormRef.value.validate()
  let specValid = await OuterboxSpecRef.value.checkData()
  if (formValid && specValid) {
    message.success('保存成功')
    emit('sure', {
      type: pageType.value,
      item: {
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
      },
      index: editIndex.value
    })
    handleCancel()
  }
}
defineExpose({ open })
</script>
