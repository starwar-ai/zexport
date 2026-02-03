<template>
  <EplusPageDetail
    v-if="props.page"
    v-bind="props"
    ref="EplusPageDetailRef"
  >
    <slot></slot>
    <template #pageTopTabs><slot name="pageTopTabs"></slot></template>
    <template #pageTop><slot name="pageTop"></slot></template>
    <template #pageBottomTabs><slot name="pageBottomTabs"></slot></template>
    <template #pageBottom><slot name="pageBottom"></slot></template>
    <template #otherAction><slot name="otherAction"></slot></template>
  </EplusPageDetail>
  <EplusDiaDetail
    v-else
    v-bind="props"
    ref="EplusDiaDetailRef"
  >
    <slot></slot>
    <template #pageTopTabs><slot name="pageTopTabs"></slot></template>
    <template #pageTop><slot name="pageTop"></slot></template>
    <template #pageBottomTabs><slot name="pageBottomTabs"></slot></template>
    <template #pageBottom><slot name="pageBottom"></slot></template>
  </EplusDiaDetail>
</template>

<script setup lang="tsx">
import EplusDiaDetail from './EplusDiaDetail.vue'
import EplusPageDetail from './EplusPageDetail.vue'

const props = withDefaults(
  defineProps<{
    submit?: { permi: string; handler: (auditable: EplusAuditable) => void | any } | boolean
    cancel?: {
      permi: string | string[]
      handler: (auditable: EplusAuditable) => void
      user?: string
    }
    edit?: { permi: string; handler: (auditable: EplusAuditable) => void; user?: string }
    approve?: { permi: string; handler: (auditable: EplusAuditable) => void }
    revertAudit?: { permi: string; handler: (auditable: EplusAuditable) => void }
    showProcessInstanceTaskList: boolean
    auditable: EplusAuditable
    outDialog: boolean // 不在dialog中
    approveApi?: any
    rejectApi?: any
    startUserId?: string
    scrollFlag?: boolean
    page?: boolean
    pageTopTabsFlag?: boolean
  }>(),
  { showProcessInstanceTaskList: false, outDialog: false, scrollFlag: false, page: false }
)
const EplusPageDetailRef = ref()
const EplusDiaDetailRef = ref()
const bottomHeight = ref(0)
setTimeout(() => {
  if (props?.page) {
    bottomHeight.value = EplusPageDetailRef.value.bottomHeight
  } else {
    bottomHeight.value = EplusDiaDetailRef.value.bottomHeight
  }
}, 500)

const close = () => {
  if (props?.page) {
    EplusPageDetailRef.value.goBack()
  } else {
    EplusDiaDetailRef.value.close()
  }
}

defineExpose({ bottomHeight, close })
</script>

<style scoped lang="scss"></style>
