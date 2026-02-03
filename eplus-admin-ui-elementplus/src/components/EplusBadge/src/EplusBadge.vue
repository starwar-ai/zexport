<template>
  <div class="h-50px flex items-center">
    <el-badge
      v-if="props?.isBadgeShow"
      class="item"
      :value="badgeValue || ''"
      type="primary"
    >
      {{ props?.contentValue }}
    </el-badge>
    <span v-else>{{ props?.contentValue }}</span>
  </div>
</template>
<script lang="tsx" setup>
import { propTypes } from '@/utils/propTypes'

const props = defineProps({
  badgeValue: propTypes.string.def(''),
  contentValue: propTypes.string.def(''),
  isBadgeShow: propTypes.bool.def(false)
})
</script>
<style scoped lang="scss">
:deep(.el-badge__content) {
  margin-top: -4px;
}
</style>
