<template>
  <el-input v-model="inputVal">
    <template
      v-if="props.prependVal"
      #prepend
      >{{ props.prependVal }}</template
    >
    <template
      v-if="props.appendVal"
      #append
      >{{ props.appendVal }}</template
    >
  </el-input>
</template>

<script setup lang="tsx">
import { defineProps } from 'vue'

const inputVal = defineModel<string | number>('')

const props = defineProps<{
  prependVal?: string
  appendVal?: string
}>()
</script>
