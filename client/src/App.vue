<script lang="ts" setup>
import routerSearch from '@/components/RouterSearch/index.vue'
import { onErrorCaptured } from 'vue'

defineOptions({ name: 'App' })

onErrorCaptured((error, instance) => {
  console.error('An error occurred:', error)
  console.error('The error was caught in:', instance)
})
</script>
<template>
  <ConfigGlobal size="small">
    <RouterView />
    <routerSearch />
  </ConfigGlobal>
</template>
<style lang="scss">
$prefix-cls: #{$namespace}-app;

.size {
  width: 100%;
  height: 100%;
}

html,
body {
  @extend .size;

  padding: 0 !important;
  margin: 0;
  overflow: hidden;

  #app {
    @extend .size;
  }
}

.dmc-loading {
  z-index: 9999 !important;
}
</style>
