<template>
  <div>测试Eplus Search</div>
  <eplus-search
    :schema="seachSchema"
    @search="handleSearch"
  />
</template>

<script setup lang="tsx">
import { EplusSearchSchema } from '@/components/EplusSearch/types'

const seachSchema: EplusSearchSchema = {
  items: [
    {
      component: <el-input placeholder="请输入参数"></el-input>,
      field: 'field'
    }
  ],
  moreItems: [
    {
      component: <el-input placeholder="请输入参数2"></el-input>,
      field: 'field2'
    },
    {
      component: <el-input placeholder="请输入参数2"></el-input>,
      field: 'field3'
    },
    {
      component: <el-input placeholder="请输入参数2"></el-input>,
      field: 'field4'
    }
  ]
}

const handleSearch = (model) => {
  // console.log(JSON.stringify(model))
}
</script>
