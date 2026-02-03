import router from './router'
import type { RouteRecordRaw } from 'vue-router'
import { isRelogin } from '@/config/axios/service'
import { getAccessToken } from '@/utils/auth'
import { useTitle } from '@/hooks/web/useTitle'
import { useNProgress } from '@/hooks/web/useNProgress'
import { useDictStoreWithOut } from '@/store/modules/dict'
import { useUserStoreWithOut } from '@/store/modules/user'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { useTagsViewStoreWithOut } from '@/store/modules/tagsView'
import { useCountryStoreWithOut } from '@/store/modules/countrylist'
import { useSettlementStoreWithOut } from '@/store/modules/settlementList'
import { usePaymentStoreWithOut } from '@/store/modules/payment'
import { useRateStoreWithOut } from '@/store/modules/rate'
import { useAreaTreeStoreWithOut } from '@/store/modules/areaTree'
import { useFeeStoreWithOut } from '@/store/modules/fee'
import { useAppStoreWithOut } from '@/store/modules/app'
import * as AreaApi from '@/api/system/area'
import { getFeeList } from '@/api/oa/dictSubject'
import { getCountryInfoPage, getSettlementInfoPage } from '@/api/system/countryinfo/index'
import { getPaymentItemSimpleList } from '@/api/system/payment/index'
import { getCurrencysRatePage } from '@/api/system/rate/index'
import { getConfigKey } from '@/api/common'

const { start, done } = useNProgress()

// const { loadStart, loadDone } = usePageLoading()
// 路由不重定向白名单
const whiteList = [
  '/login',
  '/social-login',
  '/auth-redirect',
  '/bind',
  '/register',
  '/oauthLogin/gitee'
]

// 路由加载前
router.beforeEach(async (to, from, next) => {
  start()
  // loadStart()
  if (getAccessToken()) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      //记录来源
      to.meta = { ...to.meta, from: from.name }
      //设置打开方式
      const appStore = useAppStoreWithOut()
      if (!appStore.getWindowShowFlag) {
        const flag = await getConfigKey('window_show_flag')
        appStore.setWindowShowFlag(Number(flag))
      }
      //获取国家树
      const countryStore = useCountryStoreWithOut()
      if (!countryStore.isSetCountryList) {
        const countryData = await getCountryInfoPage()
        countryStore.setCountryList(countryData)
      }
      //获取费用名称list
      const feeStore = useFeeStoreWithOut()
      //首次加载不存在feeList缓存
      if (!feeStore.isSetFeeList) {
        const feeData = await getFeeList()
        feeStore.setFeeList(feeData)
      }

      //获取地区树
      const areaTreeStore = useAreaTreeStoreWithOut()
      if (!areaTreeStore.isSetAreaTree) {
        const areaTreeData = await AreaApi.getAreaTree()
        areaTreeStore.setAreaTree(areaTreeData)
      }

      // 获取收款方式
      const settlementStore = useSettlementStoreWithOut()
      if (!settlementStore.isSetSettlementList) {
        const settlementListData = await getSettlementInfoPage()
        settlementStore.setsettlementList(settlementListData)
      }
      //获取付款方式
      const paymentStore = usePaymentStoreWithOut()
      if (!paymentStore.isSetPaymentList) {
        const paymentListData = await getPaymentItemSimpleList({})
        paymentStore.setPaymentList(paymentListData)
      }

      //获取汇率
      const rateStore = useRateStoreWithOut()
      if (!rateStore.isSetRateList) {
        const rateListData = await getCurrencysRatePage()
        rateStore.setRateList(rateListData)
      }

      // 获取所有字典
      const dictStore = useDictStoreWithOut()
      const userStore = useUserStoreWithOut()
      const permissionStore = usePermissionStoreWithOut()
      const tagsViewStore = useTagsViewStoreWithOut()
      
      if (!dictStore.getIsSetDict) {
        await dictStore.setDictMap()
      }
      if (!userStore.getIsSetUser) {
        isRelogin.show = true
        await userStore.setUserInfoAction()
        isRelogin.show = false
        // 后端过滤菜单
        await permissionStore.generateRoutes()
        permissionStore.getAddRouters.forEach((route) => {
          router.addRoute(route as unknown as RouteRecordRaw) // 动态添加可访问路由表
        })
        // 恢复页面缓存（防止刷新后丢失）
        tagsViewStore.restoreCachedViews()
        const redirectPath = from.query.redirect || to.path
        const redirect = decodeURIComponent(redirectPath as string)
        const nextData = to.path === redirect ? { ...to, replace: true } : { path: redirect }
        next(nextData)
      } else {
        next()
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      // console.log(to, 'to.fullPath')
      // next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      next(`/login?redirect=index`) // 解决切换用户时会出现404
    }
  }
})

router.afterEach((to) => {
  useTitle(to?.meta?.title as string)
  done() // 结束Progress
  // loadDone()
})
