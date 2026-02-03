<template>
  <eplus-button v-show="buttonVisible">{{ props?.textContent || '暂存' }}</eplus-button>
</template>

<script setup lang="tsx">
import { defineProps } from 'vue'

const props = defineProps<{
  isShow: boolean
  textContent?: any
}>()

const buttonVisible = ref(false)

watchEffect(() => {
  updateButtonVisibility()
})

function updateButtonVisibility() {
  if (props.isShow) {
    buttonVisible.value = true
  } else {
    buttonVisible.value = false
  }
}
</script>

<style></style>
