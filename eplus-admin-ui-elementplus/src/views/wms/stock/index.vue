<template>
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-change="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        name="0"
      />
      <el-tab-pane
        label="客户产品"
        name="1"
      />
      <el-tab-pane
        label="自营产品"
        name="2"
      />
    </el-tabs>
  </div>
  <CustSkuCom
    v-if="activeName === '0'"
    :fromPage="`all`"
  />
  <CustSkuCom
    v-if="activeName === '1'"
    :fromPage="`cust`"
  />
  <OwnSkuCom v-if="activeName === '2'" />
</template>
<script setup lang="tsx">
import CustSkuCom from './components/CustSkuCom.vue'
import OwnSkuCom from './components/OwnSkuCom.vue'

defineOptions({ name: 'Stock' })
const activeName = ref('0')

const handleTabsClick = (name) => {
  // eplusListRef.value.handleSearch()
}
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
