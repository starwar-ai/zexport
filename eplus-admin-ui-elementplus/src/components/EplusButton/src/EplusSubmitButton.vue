<template>
  <eplus-button v-show="buttonVisible">{{ textContent || '提交' }}</eplus-button>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import { EplusAuditable } from '@/types/eplus'

const props = defineProps<{
  auditable?: EplusAuditable
  textContent?: string
  isShow: boolean
  createUser?: string | number
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
      if (props.createUser) {
        userVisibility()
      } else {
        basisVisibility()
      }
    } else {
      buttonVisible.value = true
    }
  } else {
    buttonVisible.value = false
  }
}

function basisVisibility() {
  if (
    (props.auditable.auditStatus == BpmProcessInstanceResultEnum.UNSUBMITTED.status ||
      props.auditable.auditStatus == BpmProcessInstanceResultEnum.CANCEL.status ||
      props.auditable.auditStatus == BpmProcessInstanceResultEnum.REJECT.status) &&
    props.auditable.confirmFlag !== 0
  ) {
    buttonVisible.value = true
  } else {
    buttonVisible.value = false
  }
}

function userVisibility() {
  if (
    (props.auditable.auditStatus == BpmProcessInstanceResultEnum.UNSUBMITTED.status ||
      props.auditable.auditStatus == BpmProcessInstanceResultEnum.CANCEL.status ||
      props.auditable.auditStatus == BpmProcessInstanceResultEnum.REJECT.status) &&
    user.id == props.createUser &&
    props.auditable.confirmFlag !== 0
  ) {
    buttonVisible.value = true
  } else {
    buttonVisible.value = false
  }
}

// function updateButtonVisibility() {
//   if (props.isShow) {
//     if (props.auditable && props.auditable.auditStatus >= 0) {
//       if (
//         props.auditable.auditStatus == BpmProcessInstanceResultEnum.UNSUBMITTED.status ||
//         props.auditable.auditStatus == BpmProcessInstanceResultEnum.CANCEL.status ||
//         props.auditable.auditStatus == BpmProcessInstanceResultEnum.REJECT.status
//       ) {
//         buttonVisible.value = true
//       } else {
//         buttonVisible.value = false
//       }
//     } else {
//       buttonVisible.value = true
//     }
//   } else {
//     buttonVisible.value = false
//   }
// }
</script>

<style></style>
