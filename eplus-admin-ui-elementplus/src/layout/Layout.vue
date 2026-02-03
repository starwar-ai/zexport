<script lang="tsx">
import { defineComponent } from 'vue'
import { Backtop } from '@/components/Backtop'
import { renderLayout } from './components/useRenderLayout'
import { useDesign } from '@/hooks/web/useDesign'

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('layout')

export default defineComponent({
  name: 'Layout',
  setup() {
    return () => (
      <section class={[prefixCls, 'w-[100%] h-[100%] relative']}>
        {renderLayout()}

        <Backtop></Backtop>
      </section>
    )
  }
})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-layout;

.#{$prefix-cls} {
  background-color: var(--app-content-bg-color);
  :deep(.#{$elNamespace}-scrollbar__view) {
    height: 100% !important;
  }
}
</style>
