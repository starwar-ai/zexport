<template>
  <eplus-button
    v-show="buttonVisible"
    v-hasPermi="['bpm:task:transfer']"
    >转派
  </eplus-button>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import { EplusAuditable } from '@/types/eplus'

const props = defineProps<{
  permi?: string
  auditable?: EplusAuditable
  isShow: boolean
}>()

const buttonVisible = ref(false)

watchEffect(() => {
  updateButtonVisibility()
})

function updateButtonVisibility() {
  if (props.isShow) {
    if (props.auditable && props.auditable.auditStatus >= 0) {
      if (props.auditable.auditStatus == BpmProcessInstanceResultEnum.PROCESS.status) {
        buttonVisible.value = true
      } else {
        buttonVisible.value = false
      }
    } else {
      buttonVisible.value = true
    }
  } else {
    buttonVisible.value = false
  }
}
</script>

<style></style>
