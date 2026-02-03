<template>
  <el-button
    auto-insert-space
    :type="isPrimary ? 'primary' : 'default'"
  >
    <slot></slot>
  </el-button>
</template>

<script setup lang="tsx">
const props = defineProps({
  isPrimary: {
    type: Boolean,
    default: false
  }
})
</script>
