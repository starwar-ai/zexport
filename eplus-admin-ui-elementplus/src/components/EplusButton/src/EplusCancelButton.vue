<template>
  <eplus-button v-show="buttonVisible">取消申请</eplus-button>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import { useUserStore } from '@/store/modules/user'
import { EplusAuditable } from '@/types/eplus'
import { isString } from '@/utils/is'

const { t } = useI18n()
const message = useMessage()
const user = useUserStore().getUser
const props = defineProps<{
  permi?: string
  auditable?: EplusAuditable
  isShow: boolean
  startUserId: number | string
}>()
let permiList = ref<string[]>([])

const buttonVisible = ref(false)

/** 初始化 **/
// onMounted(async () => {
//   await nextTick()
//   updateButtonVisibility()
// })

watchEffect(() => {
  if (isString(props.permi)) {
    permiList.value = [props.permi]
  } else {
    permiList.value = props.permi || []
  }
  updateButtonVisibility()
})

function updateButtonVisibility() {
  if (props.isShow) {
    if (props.auditable && props.auditable.auditStatus >= 0) {
      if (
        props.auditable.auditStatus == BpmProcessInstanceResultEnum.PROCESS.status &&
        user.id == props.startUserId &&
        props.auditable.confirmFlag !== 0
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
