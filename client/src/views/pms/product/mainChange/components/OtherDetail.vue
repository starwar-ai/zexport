<template>
  <el-tabs
    v-model="activeName"
    class="demo-tabs mb20px"
  >
    <el-tab-pane
      label="操作记录"
      name="first"
    >
      <eplus-operate-log :logList="pageInfo?.operateLogRespDTOList" />
    </el-tab-pane>
    <el-tab-pane
      label="历史成交价"
      name="second"
    >
      <HistoryTradePrice :code="pageInfo?.code" />
    </el-tab-pane>
  </el-tabs>
</template>

<script lang="tsx" setup>
import { ref } from 'vue'
import HistoryTradePrice from './HistoryTradePrice.vue'

const activeName = ref('first')

const props = defineProps<{
  info?: any
}>()

const pageInfo = computed(() => props.info)
</script>

<style scoped>
.demo-tabs > .el-tabs__content {
  /* padding: 32px; */
  color: #6b778c;
  font-size: 14px;
  margin-bottom: 20px;
  text-align: center;
}
</style>
