<script lang="ts" setup>
import { useTagsViewStore } from '@/store/modules/tagsView'
import { useAppStore } from '@/store/modules/app'
import { Footer } from '@/layout/components/Footer'

defineOptions({ name: 'AppView' })

const appStore = useAppStore()

const footer = computed(() => appStore.getFooter)

const tagsViewStore = useTagsViewStore()

const getCaches = computed((): string[] => {
  return tagsViewStore.getCachedViews
})
</script>

<template>
  <section
    class="flex flex-col items-stretch bg-[var(--app-content-bg-color)] p-0 !min-h-[100%]"
    style="min-width: 1280px"
  >
    <router-view>
      <template #default="{ Component, route }">
        <!-- max 限制缓存组件数量，防止内存溢出 -->
        <keep-alive :include="getCaches" :max="20">
          <component
            :is="Component"
            :key="route.fullPath"
          />
        </keep-alive>
      </template>
    </router-view>
  </section>
  <Footer v-if="footer" />
</template>

<style lang="scss" scoped></style>
