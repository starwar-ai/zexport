<template>
  <div>
    <el-dropdown-item
      v-if="buttonVisible"
      :key="dropDownKey"
      :command="dropDownKey"
      >提交</el-dropdown-item
    >
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineProps, onMounted, ref } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'
import { useUserStore } from '@/store/modules/user'

const user = useUserStore().getUser
const permissions = useUserStore().getPermissions
const props = defineProps({
  id: propTypes.number,
  auditStatus: propTypes.number,
  permi: propTypes.string,
  createUser: propTypes.object
})

const emit = defineEmits(['hideItem'])
const dropDownKey = 'submit'

const buttonVisible = ref(false)
/** 初始化 **/
onMounted(() => {
  // updateButtonVisibility()
})

watchEffect(() => {
  updateButtonVisibility()
})

function updateButtonVisibility() {
  if (props.createUser.userId) {
    if (props.createUser.userId === user.id) {
      basisVisibility()
    } else {
      buttonHide()
    }
  } else {
    basisVisibility()
  }
}

function basisVisibility() {
  if (
    (!props.permi || (props.permi && permissions.includes(props.permi))) &&
    (!props.id ||
      props.auditStatus == BpmProcessInstanceResultEnum.UNSUBMITTED.status ||
      props.auditStatus == BpmProcessInstanceResultEnum.CANCEL.status ||
      props.auditStatus == BpmProcessInstanceResultEnum.REJECT.status)
  ) {
    buttonVisible.value = true
  } else {
    buttonHide()
  }
}

function buttonHide() {
  buttonVisible.value = false
  emit('hideItem', {
    id: props.id,
    type: dropDownKey
  })
}
</script>
