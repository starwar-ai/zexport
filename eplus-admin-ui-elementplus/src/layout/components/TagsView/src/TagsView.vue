<script lang="ts" setup>
import { computed, nextTick, onMounted, ref, unref, watch } from 'vue'
import type { RouteLocationNormalizedLoaded, RouterLinkProps } from 'vue-router'
import { useRouter } from 'vue-router'
import { usePermissionStore } from '@/store/modules/permission'
import { useTagsViewStore } from '@/store/modules/tagsView'
import { useAppStore } from '@/store/modules/app'
import { useI18n } from '@/hooks/web/useI18n'
import { filterAffixTags } from './helper'
import { ContextMenu, ContextMenuExpose } from '@/layout/components/ContextMenu'
import { useDesign } from '@/hooks/web/useDesign'
import { useTemplateRefsList } from '@vueuse/core'
import { ElScrollbar } from 'element-plus'
import { useScrollTo } from '@/hooks/event/useScrollTo'
import { Sortable } from 'sortablejs-vue3'
import { isValidArray } from '@/utils/is'

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('tags-view')

const { t } = useI18n()

const { currentRoute, push, replace } = useRouter()

const permissionStore = usePermissionStore()

const routers = computed(() => permissionStore.getRouters)

const tagsViewStore = useTagsViewStore()
const indexView = computed(() => tagsViewStore.getVisitedViews.find((v) => v.path == '/index'))
// const visitedViews = computed(() => )
const visitedViews = computed({
  get: () => tagsViewStore.getVisitedViews.filter((v) => v.path != '/index'),
  set: (val) => {
    tagsViewStore.setVisitedView(val)
  }
})

// const indexView = computed(() => tagsViewStore.getVisitedViews)

const affixTagArr = ref<RouteLocationNormalizedLoaded[]>([])

// 初始化tag
const initTags = () => {
  affixTagArr.value = filterAffixTags(unref(routers))
  for (const tag of unref(affixTagArr)) {
    // Must have tag name
    if (tag.name) {
      tagsViewStore.addVisitedView(tag)
    }
  }
}

const selectedTag = ref<RouteLocationNormalizedLoaded>()

// 新增tag
const addTags = () => {
  const { name } = unref(currentRoute)
  if (name) {
    selectedTag.value = unref(currentRoute)
    tagsViewStore.addView(unref(currentRoute))
  }
  return false
}

// 关闭选中的tag
const closeSelectedTag = (view: RouteLocationNormalizedLoaded) => {
  if (view?.meta?.affix) return
  tagsViewStore.delView(view)
  tagList.value = tagList.value.filter((item) => item.path !== view.path)
  if (isActive(view)) {
    toLastView()
  }
  tagsChange()
}

// 关闭全部
const closeAllTags = () => {
  tagsViewStore.delAllViews()
  toLastView(true)
  tagsChange()
}

// 关闭其它
const closeOthersTags = () => {
  tagsViewStore.delOthersViews(unref(selectedTag) as RouteLocationNormalizedLoaded)
  tagsChange()
}

// 重新加载
const refreshSelectedTag = async (view?: RouteLocationNormalizedLoaded) => {
  if (!view) return
  tagsViewStore.delCachedView()
  const { path, query } = view
  await nextTick()
  replace({
    path: '/redirect' + path,
    query: query
  })
}

// 关闭左侧
const closeLeftTags = () => {
  tagsViewStore.delLeftViews(unref(selectedTag) as RouteLocationNormalizedLoaded)
  tagsChange()
}

// 关闭右侧
const closeRightTags = () => {
  tagsViewStore.delRightViews(unref(selectedTag) as RouteLocationNormalizedLoaded)
  tagsChange()
}

// 跳转到最后一个
const toLastView = (allFlag = false) => {
  const visitedViews = tagList.value
  const latestView = visitedViews.slice(-1)[0]
  if (allFlag) {
    push('/')
    return
  }
  if (latestView) {
    push(latestView)
  } else {
    if (
      unref(currentRoute).path === permissionStore.getAddRouters[0].path ||
      unref(currentRoute).path === permissionStore.getAddRouters[0].redirect
    ) {
      addTags()
      return
    }
    // TODO: You can set another route
    push('/')
  }
}

// 滚动到选中的tag
const moveToCurrentTag = async () => {
  await nextTick()
  for (const v of unref(visitedViews)) {
    if (v.fullPath === unref(currentRoute).path) {
      // moveToTarget(v)
      if (v.fullPath !== unref(currentRoute).fullPath) {
        tagsViewStore.updateVisitedView(unref(currentRoute))
      }

      break
    }
  }
}

const tagLinksRefs = useTemplateRefsList<RouterLinkProps>()

const moveToTarget = (currentTag: RouteLocationNormalizedLoaded) => {
  const wrap$ = unref(scrollbarRef)?.wrapRef
  let firstTag: Nullable<RouterLinkProps> = null
  let lastTag: Nullable<RouterLinkProps> = null

  const tagList = unref(tagLinksRefs)
  // find first tag and last tag
  if (tagList.length > 0) {
    firstTag = tagList[0]
    lastTag = tagList[tagList.length - 1]
  }
  if ((firstTag?.to as RouteLocationNormalizedLoaded).fullPath === currentTag.fullPath) {
    // 直接滚动到0的位置
    const { start } = useScrollTo({
      el: wrap$!,
      position: 'scrollLeft',
      to: 0,
      duration: 500
    })
    start()
  } else if ((lastTag?.to as RouteLocationNormalizedLoaded).fullPath === currentTag.fullPath) {
    // 滚动到最后的位置
    const { start } = useScrollTo({
      el: wrap$!,
      position: 'scrollLeft',
      to: wrap$!.scrollWidth - wrap$!.offsetWidth,
      duration: 500
    })
    start()
  } else {
    // find preTag and nextTag
    const currentIndex: number = tagList.findIndex(
      (item) => (item?.to as RouteLocationNormalizedLoaded).fullPath === currentTag.fullPath
    )
    const tgsRefs = document.getElementsByClassName(`${prefixCls}__item`)

    const prevTag = tgsRefs[currentIndex - 1] as HTMLElement
    const nextTag = tgsRefs[currentIndex + 1] as HTMLElement

    // the tag's offsetLeft after of nextTag
    const afterNextTagOffsetLeft = nextTag?.offsetLeft + nextTag.offsetWidth + 4

    // the tag's offsetLeft before of prevTag
    const beforePrevTagOffsetLeft = prevTag.offsetLeft - 4

    if (afterNextTagOffsetLeft > unref(scrollLeftNumber) + wrap$!.offsetWidth) {
      const { start } = useScrollTo({
        el: wrap$!,
        position: 'scrollLeft',
        to: afterNextTagOffsetLeft - wrap$!.offsetWidth,
        duration: 500
      })
      start()
    } else if (beforePrevTagOffsetLeft < unref(scrollLeftNumber)) {
      const { start } = useScrollTo({
        el: wrap$!,
        position: 'scrollLeft',
        to: beforePrevTagOffsetLeft,
        duration: 500
      })
      start()
    }
  }
}

// 是否是当前tag
const isActive = (route: RouteLocationNormalizedLoaded): boolean => {
  return route.path === unref(currentRoute).path
}

// 所有右键菜单组件的元素
const itemRefs = useTemplateRefsList<ComponentRef<typeof ContextMenu & ContextMenuExpose>>()

// 右键菜单装填改变的时候
const visibleChange = (visible: boolean, tagItem: RouteLocationNormalizedLoaded) => {
  if (visible) {
    for (const v of unref(itemRefs)) {
      const elDropdownMenuRef = v.elDropdownMenuRef
      if (tagItem.fullPath !== v.tagItem.fullPath) {
        elDropdownMenuRef?.handleClose()
      }
    }
  }
}

// elscroll 实例
const scrollbarRef = ref<ComponentRef<typeof ElScrollbar>>()

// 保存滚动位置
const scrollLeftNumber = ref(0)

const scroll = ({ scrollLeft }) => {
  scrollLeftNumber.value = scrollLeft as number
}

// 移动到某个位置
const move = (to: number) => {
  const wrap$ = unref(scrollbarRef)?.wrapRef
  const { start } = useScrollTo({
    el: wrap$!,
    position: 'scrollLeft',
    to: unref(scrollLeftNumber) + to,
    duration: 500
  })
  start()
}

const appStore = useAppStore()
const currentSize = computed(() => appStore.getCurrentSize)

onMounted(() => {
  initTags()
  addTags()
  tagsChange()
})

watch(
  () => currentRoute.value,
  () => {
    addTags()
    moveToCurrentTag()
    tagsChange()
  }
)

const moreList = ref<RouteLocationNormalizedLoaded[]>([])
const tagList = ref<RouteLocationNormalizedLoaded[]>([])
const isShow = ref(false)
const tagListRef = ref()
const SortableRef = ref()

const sortArrayByOrder = (orderArray, targetArray, key) => {
  const orderMap = new Map<string, number>()
  orderArray.forEach((item, index) => {
    orderMap.set(item, index)
  })

  return [...targetArray].sort((a, b) => {
    return (orderMap.get(a[key]) || 0) - (orderMap.get(b[key]) || 0)
  })
}
const handleUpdate = () => {
  tagList.value = sortArrayByOrder(SortableRef.value?.sortable?.toArray(), tagList.value, 'path')
  const indexViewValue = indexView.value ? [indexView.value] : []
  visitedViews.value = [...indexViewValue, ...tagList.value, ...moreList.value]
}
const tagsChange = () => {
  let tagListWidth = tagListRef.value?.clientWidth
  let showListWidth = visitedViews.value.length * 117 + 200
  let num = Math.floor(tagListWidth / 117) - 1
  if (tagListWidth < showListWidth) {
    if (num < visitedViews.value.length) {
      tagList.value = visitedViews.value.slice(visitedViews.value.length - num + 1)
      moreList.value = visitedViews.value.slice(0, visitedViews.value.length - num + 1)
    } else {
      tagList.value = visitedViews.value
      moreList.value = []
    }

    isShow.value = isValidArray(moreList.value)
  } else {
    isShow.value = false
    tagList.value = visitedViews.value
    moreList.value = []
  }
  const index = moreList.value?.findIndex((item) => item.fullPath === currentRoute.value.fullPath)
  if (index >= 0) {
    let lastItem = tagList.value[tagList.value?.length - 1]
    tagList.value[tagList.value?.length - 1] = currentRoute.value
    moreList.value.splice(index, 1)
    moreList.value.push(lastItem)
  }
}
</script>

<template>
  <div
    :id="prefixCls"
    :class="prefixCls"
    class="relative h-[var(--top-tool-height)] flex bg-[var(--top-header-bg-color)]"
    ref="tagListRef"
  >
    <div class="flex overflow-hidden">
      <!-- <ElScrollbar
        ref="scrollbarRef"
        class="h-full"
        @scroll="scroll"
      >
        
      </ElScrollbar> -->
      <div
        v-for="element in affixTagArr"
        :key="element.fullPath"
        :class="[
          `${prefixCls}__item`,
          element?.meta?.affix ? `${prefixCls}__item--affix` : '',
          {
            'is-active': isActive(element)
          }
        ]"
      >
        <div class="flex items-center">
          <router-link
            :ref="tagLinksRefs.set"
            :to="{ ...element }"
            custom
            v-slot="{ navigate }"
          >
            <div
              @click="navigate"
              class="tagLabel h-full !w-60px"
              style="line-height: 30px"
            >
              <span :class="`${prefixCls}__item--${currentSize || 'default'}`">
                {{ t(element?.meta?.title as string) }}</span
              >
            </div>
          </router-link>
        </div>
      </div>
      <Sortable
        ref="SortableRef"
        :list="tagList"
        tag="div"
        itemKey="path"
        @update="handleUpdate"
        class="h-full flex"
      >
        <template #item="{ element }">
          <div
            class="h-full flex"
            :key="element.path"
            :data-id="element.path"
          >
            <ContextMenu
              :ref="itemRefs.set"
              :schema="[
                {
                  icon: 'ep:refresh',
                  label: t('common.reload'),
                  disabled: selectedTag?.fullPath !== element.fullPath,
                  command: () => {
                    refreshSelectedTag(element)
                  }
                },
                {
                  icon: 'ep:close',
                  label: t('common.closeTab'),
                  disabled: !!visitedViews?.length && selectedTag?.meta.affix,
                  command: () => {
                    closeSelectedTag(element)
                  }
                },
                {
                  divided: true,
                  icon: 'ep:d-arrow-left',
                  label: t('common.closeTheLeftTab'),
                  disabled:
                    !!visitedViews?.length &&
                    (element.fullPath === visitedViews[0].fullPath ||
                      selectedTag?.fullPath !== element.fullPath),
                  command: () => {
                    closeLeftTags()
                  }
                },
                {
                  icon: 'ep:d-arrow-right',
                  label: t('common.closeTheRightTab'),
                  disabled:
                    !!visitedViews?.length &&
                    (element.fullPath === visitedViews[visitedViews.length - 1].fullPath ||
                      selectedTag?.fullPath !== element.fullPath),
                  command: () => {
                    closeRightTags()
                  }
                },
                {
                  divided: true,
                  icon: 'ep:discount',
                  label: t('common.closeOther'),
                  disabled: selectedTag?.fullPath !== element.fullPath,
                  command: () => {
                    closeOthersTags()
                  }
                },
                {
                  icon: 'ep:minus',
                  label: t('common.closeAll'),
                  command: () => {
                    closeAllTags()
                  }
                }
              ]"
              :key="element.fullPath"
              :tag-item="element"
              :class="[
                `${prefixCls}__item`,
                element?.meta?.affix ? `${prefixCls}__item--affix` : '',
                {
                  'is-active': isActive(element)
                }
              ]"
              @visible-change="visibleChange"
            >
              <div class="flex items-center">
                <router-link
                  :ref="tagLinksRefs.set"
                  :to="{ ...element }"
                  custom
                  v-slot="{ navigate }"
                >
                  <div
                    @click="navigate"
                    class="tagLabel"
                  >
                    <span :class="`${prefixCls}__item--${currentSize || 'default'}`">
                      {{ t(element?.meta?.title as string) }}</span
                    >
                    <Icon
                      :class="`${prefixCls}__item--close`"
                      color="#333"
                      id="item-close"
                      icon="ep:close"
                      :size="12"
                      @click.prevent.stop="closeSelectedTag(element)"
                    />
                  </div>
                </router-link>
              </div>
            </ContextMenu>
          </div>
        </template>
      </Sortable>
      <div v-if="isShow">
        <el-dropdown>
          <span class="moreBtn pt-1">
            更多
            <Icon :icon="'ep:arrow-down'" />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="element in moreList"
                :key="element.fullPath"
              >
                <div class="flex items-center">
                  <router-link
                    :ref="tagLinksRefs.set"
                    :to="{ ...element }"
                    custom
                    v-slot="{ navigate }"
                  >
                    <div
                      @click="navigate"
                      class="tagLabel"
                    >
                      <span :class="`${prefixCls}__item--${currentSize || 'default'}`">
                        {{ t(element?.meta?.title as string) }}</span
                      >
                      <!-- <Icon
                        v-if="!element?.meta?.affix"
                        :class="`${prefixCls}__item--close`"
                        color="#333"
                        id="item-close"
                        icon="ep:close"
                        :size="12"
                        @click.prevent.stop="closeSelectedTag(element)"
                      /> -->
                    </div>
                  </router-link>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-tags-view;

.#{$prefix-cls} {
  :deep(.#{$elNamespace}-scrollbar__view) {
    height: 100%;
  }

  &__item {
    position: relative;
    top: 6px;
    height: 32px;
    padding: 0 15px;
    color: #ccc;
    cursor: pointer;
    border: none;

    &--default {
      font-size: var(--el-font-size-small);
    }

    &--large {
      font-size: var(--el-font-size-base);
    }

    &--small {
      font-size: var(--el-font-size-extra-small);
    }

    &--close {
      position: absolute;
      top: 50%;
      right: 5px;
      transform: translate(0, -50%);
    }
  }

  &__item:not(.is-active) {
    padding: 0 25px 0 12px;

    &:hover {
      color: var(--el-color-primary);

      :deep(#item-close) {
        color: #fff !important;

        .#{$prefix-cls}__item--close {
          color: #fff !important;
        }
      }
    }
  }

  &__item.is-active {
    padding: 0 25px 0 12px;
    color: #666;
    background-color: #fff;
    border-bottom: none;
    border-radius: 6px 6px 0 0;
  }

  &__item--affix.is-active {
    padding: 0 25px;
  }
}

.tagLabel {
  width: 80px;
  text-align: center;
  white-space: nowrap; /* 确保文本不会换行 */
  overflow: hidden; /* 超出容器部分的文本会被隐藏 */
  text-overflow: ellipsis; /* 超出的文本显示为省略号 */
}

.moreBtn {
  width: 80px;
  text-align: center;
  color: #ccc;
  font-size: 14px;
  padding-top: 6px;
  line-height: 32px;
}
</style>
