import { computed } from 'vue'
import { useAppStore } from '@/store/modules/app'
import { TabMenu } from '@/layout/components/TabMenu'
import AppView from './AppView.vue'
import ToolHeader from './ToolHeader.vue'
import { ElScrollbar } from 'element-plus'
import { useDesign } from '@/hooks/web/useDesign'
import { TagsView } from '@/layout/components/TagsView'

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('layout')

const appStore = useAppStore()

const pageLoading = computed(() => appStore.getPageLoading)

// 标签页
const tagsView = computed(() => appStore.getTagsView)

// 菜单折叠
const collapse = computed(() => appStore.getCollapse)

// 固定头部
const fixedHeader = computed(() => appStore.getFixedHeader)

// 固定菜单

export const renderLayout = () => {
  return (
    <>
      <div class="relative h-100% w-100%">
        <TabMenu class="h-100%" />
        <div
          class={[
            'absolute flex flex-col top-0 right-0 bottom-0  items-stretch',
            collapse.value ? 'left-[var(--tab-menu-min-width)]' : 'left-[var(--tab-menu-max-width)]'
          ]}
        >
          {/* 顶部工具栏  */}
          <div class="relative h-[var(--top-header-bg-height)] flex items-center bg-[var(--top-header-bg-color)]">
            {tagsView.value ? <TagsView style="width:calc(100% - 190px)"></TagsView> : undefined}
            <ToolHeader></ToolHeader>
          </div>
          <div class={[`${prefixCls}-content`, 'flex-1', 'overflow-hidden']}>
            <ElScrollbar
              v-loading={pageLoading.value}
              class={[
                `${prefixCls}-content-scrollbar`,
                {
                  '!h-[calc(100%-var(--tags-view-height))] mt-[calc(var(--tags-view-height))]':
                    fixedHeader.value && tagsView.value
                }
              ]}
            >
              <AppView id="appView"></AppView>
            </ElScrollbar>
          </div>
        </div>
      </div>
    </>
  )
}
