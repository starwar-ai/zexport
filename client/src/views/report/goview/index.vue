<template>
  <HeaderTabs @item="handleItem" />
  <ContentWrap>
    <IFrame :src="src" />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { getAccessToken } from '@/utils/auth'
import HeaderTabs from './components/Tabs.vue'

const BASE_URL = import.meta.env.VITE_BASE_URL
const itemId = ref()
const src = ref()
const handleItem = (item) => {
  itemId.value = item.componentId
  viewReport()
}
const viewReport = () => {
  if (itemId.value) {
    src.value = BASE_URL + `/jmreport/view/${itemId.value}?token=` + getAccessToken()
  }
}

onMounted(() => {
  viewReport()
})
defineOptions({ name: 'JimuReport' })
</script>
