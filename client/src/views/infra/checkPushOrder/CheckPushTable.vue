<template>
  <el-row>
    <el-col
      :span="16"
      class="flex"
    >
      <el-col :span="8">
        <eplus-dict-select
          :dictType="DICT_TYPE.CUSTOM_TYPE"
          style="width: 100%"
        />
      </el-col>
      <el-col :span="6">
        <eplus-dict-select
          :dictType="DICT_TYPE.CUSTOM_TYPE"
          style="width: 100%"
        />
      </el-col>
      <el-col :span="10">
        <el-input
          style="width: 100%"
          v-model="keyWord"
        />
      </el-col>
    </el-col>
    <el-col :span="8">
      <!-- <el-col : -->
    </el-col>
  </el-row>
  <!-- <eplus-table
    :eplusTableSchema="eplusTableSchema"
    ref="eplusTableRef"
    @refresh="handleRefresh"
  /> -->
</template>
<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'

const keyWord = ref('')
const eplusTableRef = ref()
const handleRefresh = async () => {
  await eplusTableRef.value.handleRefresh()
}
</script>
<style lang="scss" scoped></style>
