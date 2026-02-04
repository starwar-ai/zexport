<script lang="tsx">
import { usePermissionStore } from '@/store/modules/permission'
import { useAppStore } from '@/store/modules/app'
import { computed, defineComponent, onMounted, ref, unref, watch } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { ClickOutside, ElMenu, ElScrollbar } from 'element-plus'
import { Icon } from '@/components/Icon'
import { useRouter } from 'vue-router'
import { pathResolve } from '@/utils/routerHelper'
import { cloneDeep } from 'lodash-es'
import { filterMenusPath, initTabMap, tabPathMap } from './helper'
import { useDesign } from '@/hooks/web/useDesign'
import { isUrl } from '@/utils/is'
import { useRenderMenuItem } from '../../Menu/src/components/useRenderMenuItem'
import RouterSearch from '@/components/RouterSearch/index.vue'

const { getPrefixCls, variables } = useDesign()

const prefixCls = getPrefixCls('tab-menu')

export default defineComponent({
  name: 'TabMenu',
  directives: {
    ClickOutside
  },
  setup() {
    const { push, currentRoute } = useRouter()

    const { t } = useI18n()

    const appStore = useAppStore()

    const collapse = computed(() => appStore.getCollapse)

    const fixedMenu = computed(() => appStore.getFixedMenu)

    const menuMode = computed((): 'vertical' | 'horizontal' => {
      return 'vertical'
    })

    const activeMenu = computed(() => {
      const { meta, path } = unref(currentRoute)
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu as string
      }
      return path
    })

    const permissionStore = usePermissionStore()

    const routers = computed(() => permissionStore.getRouters)

    const tabRouters = computed(() =>
      unref(routers).filter((v) => !v?.meta?.hidden && v?.name != 'Home')
    )

    const setCollapse = () => {
      appStore.setCollapse(!unref(collapse))
    }

    onMounted(() => {
      if (unref(fixedMenu)) {
        const path = `/${unref(currentRoute).path.split('/')[1]}`
        const children = unref(tabRouters).find(
          (v) =>
            (v.meta?.alwaysShow || (v?.children?.length && v?.children?.length > 1)) &&
            v.path === path
        )?.children

        tabActive.value = path
        if (children) {
          permissionStore.setMenuTabRouters(
            cloneDeep(children).map((v) => {
              v.path = pathResolve(unref(tabActive), v.path)
              return v
            })
          )
        }
      }
    })

    watch(
      () => routers.value,
      (routers: AppRouteRecordRaw[]) => {
        initTabMap(routers)
        filterMenusPath(routers, routers)
      },
      {
        immediate: true,
        deep: true
      }
    )

    const showTitle = ref(true)

    watch(
      () => currentRoute.value.path,
      () => {
        //要执行的方法
        clickOut()
      },
      { deep: true }
    )

    // 是否显示菜单
    const showMenu = ref(unref(fixedMenu) ? true : false)

    // tab高亮
    const tabActive = ref('')

    // tab 鼠标浮上

    const tabHover = (item: AppRouteRecordRaw) => {
      if (isUrl(item.path) || !item.children || !unref(showMenu)) return
      let cur = item.path
      if (tabActive.value != cur) {
        tabActive.value = cur
        permissionStore.setMenuTabRouters(
          cloneDeep(item.children).map((v) => {
            v.path = pathResolve(unref(tabActive), v.path)
            return v
          })
        )
      }
    }

    // tab点击事件
    const tabClick = (item: AppRouteRecordRaw, isSub = false) => {
      if (isUrl(item.path)) {
        window.open(item.path)
        return
      }
      const newPath = item.children ? item.path : item.path.split('/')[0]
      const oldPath = unref(tabActive)
      tabActive.value = item.children ? item.path : item.path.split('/')[0]
      if (item.children && !isSub) {
        if (newPath === oldPath || !unref(showMenu)) {
          // showMenu.value = unref(fixedMenu) ? true : !unref(showMenu)
          showMenu.value = !unref(showMenu)
        }
        if (unref(showMenu)) {
          permissionStore.setMenuTabRouters(
            cloneDeep(item.children).map((v) => {
              v.path = pathResolve(unref(tabActive), v.path)
              return v
            })
          )
        }
      } else {
        push(item.path)
        permissionStore.setMenuTabRouters([])
        showMenu.value = false
      }
    }

    // 设置高亮
    const isActive = (currentPath: string) => {
      const { path } = unref(currentRoute)
      if (tabPathMap[currentPath].includes(path)) {
        return true
      }
      return false
    }

    const clickOut = () => {
      if (!unref(fixedMenu)) {
        showMenu.value = false
      }
    }

    const menuSelect = (index: string) => {
      showMenu.value = false
      // 自定义事件
      if (isUrl(index)) {
        window.open(index)
      } else {
        push(index)
      }
    }

    const renderPop = () => {
      let r: any[] = []
      if (unref(showMenu))
        r.push(
          <div
            class="bottom-0 right-0 top-0 z-3000 bg-black opacity-30 !fixed !left-[var(--tab-menu-min-width)]"
            onClick={clickOut}
          />
        )
      let menus = permissionStore.getMenuTabRouters.map((item) => {
        return (
          <div class={['w-180px', 'pop-item']}>
            <div
              class={item.children ? 'pop-title' : 'pop-href'}
              onClick={() => {
                if (!item.children) menuSelect(item.path)
              }}
            >
              <span>{t(item.meta?.title || '')}</span>
            </div>
            {item.children
              ? item.children.map((c) => {
                  return (
                    !c?.meta?.hidden && (
                      <div
                        class="pop-item-cont"
                        onClick={() => {
                          menuSelect(pathResolve(item.path, c.path))
                        }}
                      >
                        <span>{c.meta?.title || ''}</span>
                      </div>
                    )
                  )
                })
              : []}
          </div>
        )
      })
      r.push(
        <div
          class={[
            'submenus absolute bottom-0 top-0 z-3000 bg-[var(--left-menu-bg-color)] !left-[var(--tab-menu-min-width)]',
            { 'sub-leave': !unref(showMenu), 'sub-enter': unref(showMenu) }
          ]}
        >
          {menus}
        </div>
      )
      return r
    }

    const renderMenu = () => {
      return collapse.value ? (
        unref(tabRouters).map((v) => {
          const item = (
            v.meta?.alwaysShow || (v?.children?.length && v?.children?.length > 1)
              ? v
              : {
                  ...v,
                  ...(v?.children && v?.children[0]),
                  path: pathResolve(v.path, (v?.children && v?.children[0])?.path as string)
                }
          ) as AppRouteRecordRaw
          return (
            <div
              class={[
                `${prefixCls}__item`,
                'text-center text-14px relative py-8px cursor-pointer',
                {
                  'is-active': isActive(v.path)
                }
              ]}
              onMouseover={(e) => {
                // tabHover(item)
              }}
              onClick={() => {
                tabClick(item)
              }}
            >
              {
                <div>
                  <Icon icon={item?.meta?.icon}></Icon>
                </div>
              }
              {!unref(showTitle) ? undefined : (
                <p class="mt-5px break-words px-2px">{t(item.meta?.title || '')}</p>
              )}
            </div>
          )
        })
      ) : (
        <ElMenu
          defaultActive={unref(activeMenu)}
          mode={unref(menuMode)}
          collapse={false}
          uniqueOpened={true}
          textColor="var(--tab-menu-text-color)"
          activeTextColor="var(--tab-menu-text-active-color)"
          style="--el-menu-bg-color:transparent;--el-menu-hover-bg-color: #666;"
          onSelect={menuSelect}
        >
          {{
            default: () => {
              const { renderMenuItem } = useRenderMenuItem(unref(menuMode))
              return renderMenuItem(unref(routers))
            }
          }}
        </ElMenu>
      )
    }
    return () => (
      <div
        id={`${variables.namespace}-menu`}
        v-click-outside={clickOut}
        class={[
          prefixCls,
          'relative bg-[var(--tab-menu-bg-color)]',
          {
            'w-[var(--tab-menu-min-width)]': unref(collapse),
            'w-[var(--tab-menu-max-width)]': !unref(collapse)
          }
        ]}
      >
        <div class="absolute z-1000 top-0 left-0 leftSFC w-100%">
          <div class="h-[calc(var(--logo-height))]">
            <router-link
              class="block !h-56px flex items-center justify-center"
              to="/"
            >
              <img
                class="h-28px w-28px"
                src="/logo.png"
              />
            </router-link>
            <RouterSearch
              isModal={false}
              onUpdateCollapse={(flag) => {
                appStore.setCollapse(flag)
              }}
            />
          </div>
          <ElScrollbar class="!h-[calc(100vh-var(--tab-menu-collapse-height)-var(--logo-height))]">
            {renderMenu()}
          </ElScrollbar>
          <div
            class={[
              `${prefixCls}--collapse`,
              'ml-17px',
              ' h-[var(--tab-menu-collapse-height)] leading-[var(--tab-menu-collapse-height)] cursor-pointer'
            ]}
            onClick={setCollapse}
          >
            <Icon
              class="hanburger cursor-pointer"
              icon={unref(collapse) ? 'ep:expand' : 'ep:fold'}
              size={20}
            ></Icon>
          </div>
        </div>
        {unref(collapse) ? renderPop() : []}
      </div>
    )
  }
})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-tab-menu;

.#{$prefix-cls} {
  .leftSFC {
    background-color: var(--tab-menu-bg-color);
  }

  &__item {
    color: var(--tab-menu-text-color);
    transition: all var(--transition-time-025);

    &:hover {
      color: var(--tab-menu-text-active-color);
      // background-color: var(--left-menu-bg-active-color);
    }
  }

  &--collapse {
    color: var(--tab-menu-text-color);
    //background-color: var(--tab-menu-bg-light-color);
  }

  .hanburger {
    padding: 5px;
    transition: background-color var(--transition-time-025) ease-in;

    &:hover {
      background-color: #4e4f4f;
    }
  }

  .v-tab-menu__item.is-active {
    color: var(--tab-menu-text-active-color);
    background-color: rgba(255, 255, 255, 0.2);
  }

  .el-menu-item.is-active {
    background-color: var(--tab-menu-bg-active-color);
  }

  .is-opened {
    background-color: #23262a;
  }

  .sub-enter {
    transition: all var(--transition-time-02);
    animation: drawer 0.3s;
    animation-duration: 0.3s;
    animation-timing-function: ease;
    animation-delay: 0s;
    animation-iteration-count: 1;
    animation-direction: normal;
    animation-fill-mode: forwards;
    animation-play-state: running;
    animation-name: drawer;
    animation-timeline: auto;
    animation-range-start: normal;
  }

  :deep(.el-scrollbar__wrap) {
    height: 100%;
  }

  .sub-leave {
    transition: all var(--transition-time-02);
    animation: drawer-leave 0.3s;
    animation-duration: 0.3s;
    animation-timing-function: ease;
    animation-delay: 0s;
    animation-iteration-count: 1;
    animation-direction: normal;
    animation-fill-mode: forwards;
    animation-play-state: running;
    animation-name: drawer-leave;
    animation-timeline: auto;
    animation-range-start: normal;
  }

  .submenus {
    box-shadow: 0 2px 4px #00000026;
    display: flex;
    writing-mode: vertical-lr;
    flex-wrap: wrap;
    align-content: flex-start;
    box-sizing: border-box;
    line-height: 40px;
    color: #0b1019;
    letter-spacing: 0;
    font-size: 12px;
    visibility: hidden;

    .pop-href {
      padding: 8px 16px;
      color: #0b1019;
      cursor: pointer;
    }

    .pop-title {
      padding: 8px 16px;
      color: #888c94;
      line-height: 16px;
    }

    > .pop-item {
      padding: 16px 0 0;
      float: left;
      writing-mode: horizontal-tb;
    }

    .pop-item-cont span {
      display: block;
      color: #0b1019;
      padding: 8px 16px 8px 32px;
      cursor: pointer;
      line-height: 16px;
    }
  }

  /* stylelint-disable-next-line order/order */
  @keyframes drawer {
    0% {
      visibility: visible;
      transform: translate(-80%);
      opacity: 1;
    }

    100% {
      visibility: visible;
      transform: translate(0);
      opacity: 1;
    }
  }
  /* stylelint-disable-next-line order/order */
  @keyframes drawer-leave {
    0% {
      transform: translate(0);
      visibility: visible;
      opacity: 1;
    }

    100% {
      transform: translate(-100%);
      visibility: hidden;
      opacity: 1;
    }
  }
}
</style>
