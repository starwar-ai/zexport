<script lang="tsx">
import { propTypes } from '@/utils/propTypes'
import { useDesign } from '@/hooks/web/useDesign'
import { ElCard, ElCollapseTransition, ElTooltip } from 'element-plus'
import { Icon } from '@/components/Icon'

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('content-wrap')

export default defineComponent({
  name: 'ContentWrap',
  props: {
    title: propTypes.string.def(''),
    message: propTypes.string.def(''),
    collapse: propTypes.bool.def(false)
  },
  setup(props, { slots }) {
    // 折叠
    const show = ref(true)

    const toggleClick = () => {
      if (props.collapse) {
        show.value = !show.value
      }
    }
    const rslots: Recordable = {
      default: () => {
        return !props.collapse ? (
          slots.default?.()
        ) : (
          <>
            <ElCollapseTransition class="w-100%">
              <div
                v-show={unref(show)}
                class="w-100%"
              >
                {slots.default?.()}
              </div>
            </ElCollapseTransition>
          </>
        )
      }
    }
    if (props.title) {
      rslots.header = () => {
        return (
          <div
            class="flex items-center justify-between"
            click={toggleClick}
          >
            <div>
              <span class="text-16px font-700">{props.title}</span>
              <ElTooltip
                v-if="message"
                effect="dark"
                placement="right"
              >
                {{
                  content: () => {
                    return <div class="max-w-200px">{props.message}</div>
                  },
                  default: () => {
                    return (
                      <Icon
                        size={14}
                        class="ml-5px"
                        icon="ep:question-filled"
                      />
                    )
                  }
                }}
              </ElTooltip>
            </div>
            {props.collapse ? <Icon icon={show ? 'ep:arrow-down' : 'ep:arrow-up'} /> : null}
          </div>
        )
      }
    }

    return () => {
      return (
        <ElCard
          class={[prefixCls, 'mb-15px']}
          shadow="never"
        >
          {rslots}
        </ElCard>
      )
    }
  }
})
</script>
