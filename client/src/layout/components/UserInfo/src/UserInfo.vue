<script lang="ts" setup>
import { ElMessageBox } from 'element-plus'

import { CACHE_KEY, useCache } from '@/hooks/web/useCache'
import { useDesign } from '@/hooks/web/useDesign'
import { useUserStore } from '@/store/modules/user'
import { useTagsViewStore } from '@/store/modules/tagsView'

defineOptions({ name: 'UserInfo' })

const { t } = useI18n()

const { wsCache, wsCacheSession } = useCache()

const { push, replace } = useRouter()

const userStore = useUserStore()

const tagsViewStore = useTagsViewStore()

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('user-info')

const user = wsCacheSession.get(CACHE_KEY.USER)

const userName = user.user.nickname ? user.user.nickname : 'Admin'
const avatar = user.user.avatar

const loginOut = () => {
  ElMessageBox.confirm(t('common.loginOutMessage'), t('common.reminder'), {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  })
    .then(async () => {
      await userStore.loginOut()
      tagsViewStore.delAllViews()
      location.reload()
      // replace('/login?redirect=/index')
    })
    .catch(() => {})
}
const toProfile = async () => {
  push('/user/profile')
}
</script>

<template>
  <ElDropdown
    class="custom-hover"
    :class="prefixCls"
    trigger="click"
  >
    <div class="flex items-center text-14px">
      <img
        v-if="avatar"
        :src="avatar"
        class="m-5px h-18px w-18px rd-[50%]"
      />
      <img
        v-else
        src="@/assets/imgs/avatar.jpg"
        class="m-5px h-18px w-18px rd-[50%]"
      />
      <span class="pr-[5px] text-[var(--top-header-text-color)] <lg:hidden">
        {{ userName }}
      </span>
      <Icon
        icon="ep:caret-bottom"
        :size="14"
      />
    </div>
    <template #dropdown>
      <ElDropdownMenu>
        <ElDropdownItem>
          <Icon icon="ep:tools" />
          <div @click="toProfile">{{ t('common.profile') }}</div>
        </ElDropdownItem>
        <ElDropdownItem
          divided
          @click="loginOut"
        >
          <Icon icon="ep:switch-button" />
          <div>{{ t('common.loginOut') }}</div>
        </ElDropdownItem>
      </ElDropdownMenu>
    </template>
  </ElDropdown>
</template>
