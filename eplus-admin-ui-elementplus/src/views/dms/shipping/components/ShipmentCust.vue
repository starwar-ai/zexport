<template>
  <el-form
    ref="formRef"
    style="width: 100%"
    :model="custForm"
    label-width="130px"
    label-position="left"
    require-asterisk-position="right"
  >
    <el-row
      v-for="(item, index) in custForm.list"
      :key="item.custId"
      :gutter="20"
    >
      <!-- <el-col :span="24">
        <el-form-item
          label="客户名称"
          :prop="'list.' + index + '.custName'"
          :rules="{
            required: true
          }"
        >
          <el-input
            v-model="item.custName"
            disabled
            style="width: 300px"
          />
        </el-form-item>
      </el-col> -->
      <el-col :span="8">
        <el-form-item
          label="收货人"
          :prop="'list.' + index + '.receivePerson'"
          :rules="{
            required: true,
            message: '请填写收货人',
            trigger: 'blur'
          }"
          label-width="80px"
        >
          <el-input
            type="textarea"
            v-model="item.receivePerson"
            :rows="2"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="通知人"
          :prop="'list.' + index + '.notifyPerson'"
          :rules="{
            required: true,
            message: '请填写通知人',
            trigger: 'blur'
          }"
          label-width="80px"
        >
          <el-input
            type="textarea"
            v-model="item.notifyPerson"
            :rows="2"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item
          label="正面唛头"
          :prop="'list.' + index + '.frontShippingMark'"
          :rules="{
            required: true,
            message: '请填写正面唛头',
            trigger: 'blur'
          }"
          label-width="100px"
        >
          <el-input
            v-model="item.frontShippingMark"
            type="textarea"
            :rows="2"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <!-- <el-col
        :xl="8"
        :lg="12"
      >
        <el-form-item
          label="侧面唛头"
          :prop="'list.' + index + '.sideShippingMark'"
          :rules="{
            required: true,
            message: '请填写反面唛头',
            trigger: 'blur'
          }"
        >
          <el-input
            v-model="item.sideShippingMark"
            type="textarea"
          />
        </el-form-item>
      </el-col> -->
    </el-row>
  </el-form>
  <!-- <el-button @click="checkData">保存</el-button> -->
</template>
<script setup lang="tsx">
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'ShipmentCust' })

const message = useMessage()
const props = defineProps<{
  formData
  type?: string
}>()
const custForm = reactive({
  list: []
})
const formRef = ref()
const checkData = async () => {
  let valid = await formRef.value.validate()
  if (valid) {
    return true
  } else {
    message.error('出货信息填写不完整')
    return false
  }
}
defineExpose({ checkData, custForm })
watchEffect(() => {
  if (isValidArray(props.formData.shipmentCustList)) {
    custForm.list = props.formData.shipmentCustList.map((item: any) => {
      return {
        custId: item.id || item.custId,
        custCode: item.code || item.custCode,
        custName: item.name || item.custName,
        frontShippingMark: item.frontShippingMark || '',
        sideShippingMark: item.sideShippingMark || '',
        receivePerson: item.receivePerson || '',
        notifyPerson: item.notifyPerson || ''
      }
    })
  } else {
    custForm.list = []
  }
})
</script>

<style lang="scss" scoped>
.el-col {
  min-height: 50px;
}
:deep(.el-form-item__label) {
  padding-right: 2px !important;
}
</style>
