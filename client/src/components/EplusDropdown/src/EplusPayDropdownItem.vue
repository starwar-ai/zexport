<template>
  <div>
    <el-dropdown-item
      v-if="buttonVisible"
      key="pay"
      command="pay"
      v-hasPermi="[permi]"
      >支付</el-dropdown-item
    >
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineProps, onMounted, ref } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { useUserStore } from '@/store/modules/user'

const user = useUserStore().getUser
const permissions = useUserStore().getPermissions
const props = defineProps({
  id: propTypes.number,
  auditStatus: propTypes.number,
  permi: propTypes.string,
  status: propTypes.number,
  createUser: propTypes.object
})

const emit = defineEmits(['hideItem'])

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
    props.auditStatus === 2 &&
    props.status === 0
    // props.auditStatus == BpmProcessInstanceResultEnum.UNSUBMITTED.status ||
    // props.auditStatus == BpmProcessInstanceResultEnum.CANCEL.status ||
    // props.auditStatus == BpmProcessInstanceResultEnum.REJECT.status
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
    type: 'pay'
  })
}
</script>
