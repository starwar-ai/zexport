<template>
  <div>
    <el-dropdown-item
      v-if="buttonVisible"
      :key="otherKey"
      :command="otherKey"
      >{{ props.label || '其他' }}</el-dropdown-item
    >
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineProps, ref } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { useUserStore } from '@/store/modules/user'

const user = useUserStore().getUser
const permissions = useUserStore().getPermissions
const props = defineProps({
  id: propTypes.number,
  isShow: propTypes.bool,
  otherKey: propTypes.string,
  auditStatus: propTypes.number,
  permi: propTypes.string || Array<string>,
  label: propTypes.string,
  createUser: propTypes.object,
  checkAuditStatus: Array<number>
})
const emit = defineEmits(['hideItem'])
const buttonVisible = ref(false)
const containsArray = (arr: Array<any>, val: any) => {
  return val.some((item) => arr.includes(item))
}
const ifPermi = () => {
  if (Array.isArray(props.permi)) {
    return containsArray(permissions, props.permi)
  } else {
    return props.permi && permissions.includes(props.permi)
  }
}
/** 初始化 **/
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
    props.isShow &&
    (!props.permi || ifPermi()) &&
    (!props.checkAuditStatus ||
      (props.checkAuditStatus && props.checkAuditStatus?.includes(props.auditStatus)))
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
    type: props.otherKey
  })
}
</script>
