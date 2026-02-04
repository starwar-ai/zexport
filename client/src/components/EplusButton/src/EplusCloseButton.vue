<template>
  <eplus-button>关闭</eplus-button>
</template>

<script setup lang="tsx">
import { defineProps, ref } from 'vue'
import { EplusAuditable } from '@/types/eplus'

const props = defineProps<{
  auditable: EplusAuditable
}>()

const buttonVisible = ref(true)

/** 初始化 **/
// onMounted(() => {
//   updateButtonVisibility()
// })

watchEffect(() => {
  updateButtonVisibility()
})

function updateButtonVisibility() {}
</script>

<style></style>
