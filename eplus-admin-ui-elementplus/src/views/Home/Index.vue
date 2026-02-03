<template>
  <div>
    <GridLayout />
  </div>
</template>
<script lang="ts" setup>
import { useUserStore } from '@/store/modules/user'
import GridLayout from './gridLayout/GridLayout.vue'

defineOptions({ name: 'Home' })
const components = {
  // TimeBar
}

const { t } = useI18n()
const userStore = useUserStore()
const loading = ref(true)
const username = userStore.getUser.nickname
const getAllApi = async () => {
  await Promise.all([])
  loading.value = false
}

getAllApi()
</script>
