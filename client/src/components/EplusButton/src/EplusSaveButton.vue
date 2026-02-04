<template>
  <eplus-button v-show="buttonVisible">{{ props?.textContent || '保存' }}</eplus-button>
</template>

<script setup lang="tsx">
import { defineProps, ref } from 'vue'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import { EplusAuditable } from '@/types/eplus'

const props = defineProps<{
  auditable?: EplusAuditable
  isShow: boolean
  textContent?: any
}>()

const buttonVisible = ref(false)

/** 初始化 **/
// onMounted(() => {
//   updateButtonVisibility()
// })

watchEffect(() => {
  updateButtonVisibility()
})

function updateButtonVisibility() {
  if (props.isShow) {
    if (props.auditable && props.auditable.auditStatus >= 0) {
      if (props.auditable.auditStatus == BpmProcessInstanceResultEnum.UNSUBMITTED.status) {
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
