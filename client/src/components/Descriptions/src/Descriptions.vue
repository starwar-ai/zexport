<script lang="tsx">
import { ElCol, ElCollapseTransition, ElRow, ElTooltip } from 'element-plus'
import { useDesign } from '@/hooks/web/useDesign'
import { propTypes } from '@/utils/propTypes'
import { computed, defineComponent, PropType, ref, unref } from 'vue'
import { useAppStore } from '@/store/modules/app'
import { Icon } from '@/components/Icon'
import { get } from 'lodash-es'
import { DescriptionsSchema } from '@/types/descriptions'

import dayjs from 'dayjs'
import { DictTag } from '@/components/DictTag'

const appStore = useAppStore()

const mobile = computed(() => appStore.getMobile)

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('descriptions')

const defaultData = '-'

export default defineComponent({
  name: 'Descriptions',
  props: {
    title: propTypes.string.def(''),
    message: propTypes.string.def(''),
    collapse: propTypes.bool.def(true),
    border: propTypes.bool.def(true),
    column: propTypes.number.def(2),
    padding: propTypes.number.def(20),
    size: propTypes.oneOf(['large', 'default', 'small']).def('default'),
    direction: propTypes.oneOf(['horizontal', 'vertical']).def('horizontal'),
    extra: propTypes.string.def(''),
    schema: {
      type: Array as PropType<DescriptionsSchema[]>,
      default: () => []
    },
    data: {
      type: Object as PropType<any>,
      default: () => ({})
    }
  },
  setup(props, { attrs, slots }) {
    const getBindValue = computed((): any => {
      const delArr: string[] = ['title', 'message', 'collapse', 'schema', 'data', 'class']
      const obj = { ...attrs, ...props }
      for (const key in obj) {
        if (delArr.indexOf(key) !== -1) {
          delete obj[key]
        }
      }
      if (unref(mobile)) {
        obj.direction = 'vertical'
      }
      return obj
    })

    const getBindItemValue = (item: DescriptionsSchema) => {
      const delArr: string[] = ['field']
      const obj = { ...item }
      for (const key in obj) {
        if (delArr.indexOf(key) !== -1) {
          delete obj[key]
        }
      }
      return {
        labelClassName: `${prefixCls}-label`,
        ...obj
      }
    }

    // 折叠
    const show = ref(true)

    const _self = getCurrentInstance()

    const toggleClick = () => {
      if (props.collapse) {
        show.value = !unref(show)
      }
    }
    const renderSlot = (n) => {
      let s = slots[n]
      if (s) {
        return s({ _self, props })
      }
    }
    const renderLable = (item: DescriptionsSchema) => {
      return [
        item.slots?.label ? item.slots?.label(item) : item.label,
        appStore.layout == 'mini' && /:$/.test(item.label || '') ? '' : ':'
      ]
    }
    const renderSlots = (item: DescriptionsSchema) => {
      if (item.dateFormat) {
        return props.data[item.field] !== null
          ? dayjs(props.data[item.field]).format(item.dateFormat)
          : ''
      } else if (item.dictType) {
        return (
          <DictTag
            type={item.dictType}
            value={props.data[item.field] + ''}
          />
        )
      } else
        return item.slots?.default
          ? item.slots?.default(props.data)
          : (get(props.data, item.field) ?? defaultData)
    }

    return () => {
      return (
        <div
          class={[
            prefixCls,
            'bg-[var(--el-color-white)] dark:bg-[var(--el-bg-color)] dark:border-[var(--el-border-color)] dark:border-1px',
            !props.border ? '!border-0' : ''
          ]}
        >
          {props.title ? (
            <div
              class={[
                `${prefixCls}-header`,
                'relative h-50px flex justify-between items-center layout-border__bottom px-10px cursor-pointer'
              ]}
              onClick={toggleClick}
            >
              <div class={[`${prefixCls}-header__title`, 'relative font-18px font-bold ml-10px']}>
                <div class="flex items-center">
                  {props.title}
                  {props.message ? (
                    <ElTooltip
                      content={props.message}
                      placement="right"
                    >
                      <Icon
                        icon="bi:question-circle-fill"
                        class="ml-5px"
                        size={14}
                      />
                    </ElTooltip>
                  ) : null}
                  {renderSlot('header-left')}
                </div>
              </div>
              {props.collapse ? (
                <Icon icon={show.value ? 'ep:arrow-down' : 'ep:arrow-up'} />
              ) : (
                renderSlot('header-right')
              )}
            </div>
          ) : null}

          <ElCollapseTransition>
            <div
              v-show={unref(show)}
              class={[`${prefixCls}-content`, `p-${props.padding}px`]}
            >
              <ElRow
                gutter={0}
                {...unref(getBindValue)}
                class={[
                  'outline-1px outline-[var(--el-border-color-lighter)]',
                  props.border ? 'outline-solid' : 'outline-none'
                ]}
              >
                {props.schema.map((item) => {
                  if (/hidden/.test(item.labelClassName || '')) {
                    return renderSlots(item)
                  }
                  return (
                    <ElCol
                      key={item.field}
                      span={item.span || 24 / props.column}
                      class="flex items-stretch"
                    >
                      {unref(getBindValue).direction === 'horizontal' ? (
                        <div
                          class={[
                            'flex items-stretch flex-1',
                            {
                              'bg-[var(--el-fill-color-light)] outline-1px outline-[var(--el-border-color-lighter)] outline-solid':
                                appStore.layout != 'mini',
                              'outline-none border-none': !props.border
                            }
                          ]}
                        >
                          <div
                            {...getBindItemValue(item)}
                            class={[
                              'w-120px px-8px py-11px font-700 color-[var(--el-text-color-regular)]',
                              appStore.layout == 'mini'
                                ? 'text-right'
                                : 'text-left border-r-1px border-r-[var(--el-border-color-lighter)] border-r-solid',
                              item.labelClassName,
                              {
                                'outline-none border-none':
                                  !props.border || appStore.layout == 'mini',
                                'lh-[var(--el-component-size)]': appStore.currentSize == 'default',
                                'lh-[var(--el-component-size-large)]':
                                  appStore.currentSize == 'large',
                                'lh-[var(--el-component-size-small)]':
                                  appStore.currentSize == 'small'
                              }
                            ]}
                          >
                            {renderLable(item)}
                          </div>
                          <div
                            class={[
                              'flex-1 px-8px py-11px bg-[var(--el-bg-color)] color-[var(--el-text-color-primary)] text-size-14px',
                              item.className,
                              {
                                'lh-[var(--el-component-size)]': appStore.currentSize == 'default',
                                'lh-[var(--el-component-size-large)]':
                                  appStore.currentSize == 'large',
                                'lh-[var(--el-component-size-small)]':
                                  appStore.currentSize == 'small'
                              }
                            ]}
                          >
                            {renderSlots(item)}
                          </div>
                        </div>
                      ) : (
                        <div
                          class={[
                            'bg-[var(--el-fill-color-light)] outline-1px outline-[var(--el-border-color-lighter)] flex-1',
                            !props.border ? 'outline-none border-none' : 'outline-solid'
                          ]}
                        >
                          <div
                            {...getBindItemValue(item)}
                            class={[
                              'text-left px-8px py-11px font-700 color-[var(--el-text-color-regular)] border-b-1px border-b-[var(--el-border-color-lighter)] border-b-solid',
                              item.labelClassName,
                              {
                                'outline-none border-none':
                                  !props.border || appStore.layout == 'mini',
                                'lh-[var(--el-component-size)]': appStore.currentSize == 'default',
                                'lh-[var(--el-component-size-large)]':
                                  appStore.currentSize == 'large',
                                'lh-[var(--el-component-size-small)]':
                                  appStore.currentSize == 'small'
                              }
                            ]}
                          >
                            {renderLable(item)}
                          </div>
                          <div
                            class={[
                              'flex-1 px-8px py-11px bg-[var(--el-bg-color)] color-[var(--el-text-color-primary)] text-size-14px',
                              item.className
                            ]}
                          >
                            {renderSlots(item)}
                          </div>
                        </div>
                      )}
                    </ElCol>
                  )
                })}
              </ElRow>
              {renderSlot('footer')}
            </div>
          </ElCollapseTransition>
        </div>
      )
    }
  }
})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-descriptions;

.#{$prefix-cls}-header {
  &__title {
    &::after {
      position: absolute;
      top: 3px;
      left: -10px;
      width: 4px;
      height: 70%;
      background: var(--el-color-primary);
      content: '';
    }
  }
}

.#{$prefix-cls}-content {
  :deep(.#{$elNamespace}-descriptions__cell) {
    width: 0;
  }
}
</style>
