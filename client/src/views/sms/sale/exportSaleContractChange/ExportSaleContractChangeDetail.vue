<template>
  <el-tabs v-model="activeName">
    <el-tab-pane
      label="销售合同"
      name="Contract"
    >
      <ContractPage
        :id="id"
        :isFromComfirmPage="isFromComfirmPage"
        @handle-success="handleCreatePlan"
        @create-plan="backCreatePlanParams"
        :type="type"
      />
    </el-tab-pane>
    <el-tab-pane
      label="订单费用"
      name="Cost"
    >
      <CostPage
        :id="id"
        :type="type"
      />
    </el-tab-pane>
  </el-tabs>
</template>
<script setup lang="tsx">
import ContractPage from './components/ContractPage.vue'
import CostPage from './components/CostPage.vue'

const props = defineProps<{
  title?: string
  id?: number
  isFromComfirmPage?: boolean
  type?: number
}>()
const planParams = ref()
// 接收销售合同详情组件返回的数据，以免在订单费用组件下转出运没参数
const backCreatePlanParams = (data) => {
  planParams.value = data
}
const emit = defineEmits(['createShipmentPlan'])
const handleCreatePlan = () => {
  emit('createShipmentPlan', planParams.value)
  close()
}

const activeName = ref('Contract')
</script>
<style lang="scss" scoped>
.con_box {
  height: calc(100% - 60px);
  overflow: auto;
}
</style>
