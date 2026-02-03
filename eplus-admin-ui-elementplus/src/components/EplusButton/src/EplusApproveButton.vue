<template>
  <eplus-button
    v-show="buttonVisible"
    v-hasPermi="[props.permi]"
    >审核
  </eplus-button>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import { EplusAuditable } from '@/types/eplus'
import { useUserStore } from '@/store/modules/user'

const userId = useUserStore().getUser.id

const props = defineProps<{
  permi?: string
  auditable?: EplusAuditable
  isShow: boolean
  auditUserIds: number[]
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
      if (
        props.auditable.auditStatus == BpmProcessInstanceResultEnum.PROCESS.status &&
        props.auditUserIds.includes(userId)
      ) {
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
