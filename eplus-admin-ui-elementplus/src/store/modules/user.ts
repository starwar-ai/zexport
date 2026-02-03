import { store } from '../index'
import { defineStore } from 'pinia'
import { getAccessToken, removeToken } from '@/utils/auth'
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'
import { getInfo, loginOut } from '@/api/login'
import { getSimpleUserList } from '@/api/system/user'
import { isValidArray } from '@/utils/is'

const { wsCache, wsCacheSession } = useCache()
interface UserVO {
  id: number
  avatar: string
  nickname: string
  deptId: number
  deptName: string
  code: string
}

interface UserVO {
  id: number
  avatar: string
  nickname: string
  deptId: number
  deptName: string
}

interface UserInfoVO {
  permissions: string[]
  roles: string[]
  isSetUser: boolean
  user: UserVO
  userList: UserVO[]
}

export const useUserStore = defineStore('admin-user', {
  state: (): UserInfoVO => ({
    permissions: [],
    roles: [],
    isSetUser: false,
    user: {
      id: 0,
      avatar: '',
      nickname: ''
    },
    userList: []
  }),
  getters: {
    getPermissions(): string[] {
      return this.permissions
    },
    getRoles(): string[] {
      return this.roles
    },
    getIsSetUser(): boolean {
      return this.isSetUser
    },
    getUser(): UserVO {
      return this.user
    },
    getUserList() {
      return this.userList
    }
  },
  actions: {
    async setUserInfoAction() {
      if (!getAccessToken()) {
        this.resetState()
        return null
      }
      let userInfo = wsCacheSession.get(CACHE_KEY.USER)
      if (!userInfo) {
        userInfo = await getInfo()
      }
      this.permissions = userInfo.permissions
      this.roles = userInfo.roles
      this.user = userInfo.user
      this.isSetUser = true
      wsCacheSession.set(CACHE_KEY.USER, userInfo)
      wsCacheSession.set(CACHE_KEY.ROLE_ROUTERS, userInfo.menus)
    },
    async loginOut() {
      await loginOut()
      removeToken()
      wsCache.clear()
      wsCacheSession.clear()
      this.resetState()
    },
    resetState() {
      this.permissions = []
      this.roles = []
      this.isSetUser = false
      this.user = {
        id: 0,
        avatar: '',
        nickname: ''
      }
    },
    async setUserList() {
      if (isValidArray(this.userList)) {
        return this.userList
      } else {
        this.userList = await getSimpleUserList()
        return this.userList
      }
    }
  }
})

export const useUserStoreWithOut = () => {
  return useUserStore(store)
}
