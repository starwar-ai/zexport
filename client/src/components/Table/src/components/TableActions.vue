<script lang="tsx">
import { computed, defineComponent, PropType, ref, unref } from 'vue'
import { ComponentSize, ElDropdown, ElDropdownItem, ElDropdownMenu } from 'element-plus'
import { Icon } from '@/components/Icon'
import { useI18n } from '@/hooks/web/useI18n'
import { useAppStore } from '@/store/modules/app'
import { TableColumn } from '../types'
import ColumnSetting from './ColumnSetting.vue'

const appStore = useAppStore()
const sizeMap = computed(() => appStore.sizeMap)

const { t } = useI18n()

export default defineComponent({
  name: 'TableActions',
  components: {
    ColumnSetting
  },
  props: {
    columns: {
      type: Array as PropType<TableColumn[]>,
      default: () => []
    }
  },
  emits: ['refresh', 'changSize', 'confirm'],
  setup(props, { emit, slots }) {
    const showSetting = ref(false)

    const refresh = () => {
      emit('refresh')
    }

    const changSize = (size: ComponentSize) => {
      emit('changSize', size)
    }

    const confirm = (columns: TableColumn[]) => {
      emit('confirm', columns)
    }

    const showColumnSetting = () => {
      showSetting.value = true
    }

    const renderRight = () => {
      return (
        <>
          {slots?.default?.()}
          <div
            title="刷新"
            class="h-20px w-30px flex items-center justify-end"
            onClick={refresh}
          >
            <Icon
              icon="ant-design:sync-outlined"
              class="cursor-pointer"
              hover-color="var(--el-color-primary)"
            />
          </div>

          <ElDropdown
            trigger="click"
            onCommand={changSize}
          >
            {{
              default: () => {
                return (
                  <div
                    title="尺寸"
                    class="h-20px w-30px flex items-center justify-end"
                  >
                    <Icon
                      icon="ant-design:column-height-outlined"
                      class="cursor-pointer"
                      hover-color="var(--el-color-primary)"
                    />
                  </div>
                )
              },
              dropdown: () => {
                return (
                  <ElDropdownMenu>
                    {{
                      default: () => {
                        return unref(sizeMap).map((v) => {
                          return (
                            <ElDropdownItem
                              key={v}
                              command={v}
                            >
                              {t(`size.${v}`)}
                            </ElDropdownItem>
                          )
                        })
                      }
                    }}
                  </ElDropdownMenu>
                )
              }
            }}
          </ElDropdown>

          <div
            title="列设置"
            class="h-20px w-30px flex items-center justify-end"
            onClick={showColumnSetting}
          >
            <Icon
              icon="ant-design:setting-outlined"
              class="cursor-pointer"
              hover-color="var(--el-color-primary)"
            />
          </div>
        </>
      )
    }
    return () => (
      <>
        <div
          class={[
            'flex items-center',
            {
              'h-28px justify-end text-right': !slots?.left,
              'h-50px justify-between': slots?.left
            }
          ]}
        >
          {slots?.left ? (
            <>
              <div class="h-[100%] flex items-center justify-start">{slots.left()}</div>
              <div class="h-[100%] flex items-center justify-end text-right">{renderRight()}</div>
            </>
          ) : (
            renderRight()
          )}
        </div>
        <ColumnSetting
          v-model={showSetting.value}
          columns={props.columns}
          onConfirm={confirm}
        />
      </>
    )
  }
})
</script>
