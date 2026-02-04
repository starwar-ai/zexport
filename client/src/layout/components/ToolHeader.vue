<script lang="tsx">
import { computed, defineComponent } from 'vue'
import { Message } from '@/layout/components//Message'
import { UserInfo } from '@/layout/components/UserInfo'
import { Screenfull } from '@/layout/components/Screenfull'
import RouterSearch from '@/components/RouterSearch/index.vue'
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('tool-header')

const appStore = useAppStore()

// 全屏图标
const screenfull = computed(() => appStore.getScreenfull)

// 搜索图片
const search = computed(() => appStore.search)

// 消息图标
const message = computed(() => appStore.getMessage)

export default defineComponent({
  name: 'ToolHeader',
  setup() {
    return () => (
      <div
        class={[
          prefixCls,
          'h-[var(--top-tool-height)] relative px-[var(--top-tool-p-x)] flex items-center justify-between'
        ]}
        style="--color:#fff;width:185px"
      >
        <div class="h-full flex items-center">
          {screenfull.value ? (
            <Screenfull
              class="custom-hover"
              color="var(--top-header-text-color)"
            ></Screenfull>
          ) : undefined}
          {search.value ? <RouterSearch isModal={false} /> : undefined}
          {message.value ? (
            <Message
              class="custom-hover"
              color="var(--top-header-text-color)"
            ></Message>
          ) : undefined}
          <UserInfo></UserInfo>
        </div>
      </div>
    )
  }
})
</script>

<style lang="scss" scoped>
.custom-hover {
  &:hover {
    background-color: #4e4f4f;
  }
}
</style>
