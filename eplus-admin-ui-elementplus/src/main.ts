// 引入unocss css
import '@/plugins/unocss'

// 导入全局的svg图标
import '@/plugins/svgIcon'

// 引入状态管理
import { setupStore } from '@/store'

// 全局组件
import { setupGlobalComponents } from '@/components'

// 引入 element-plus
import { setupElementPlus } from '@/plugins/elementPlus'

// 引入 form-create
import { setupFormCreate } from '@/plugins/formCreate'

// 引入全局样式
import '@/styles/index.scss'

// 引入动画
import '@/plugins/animate.css'

// 路由
import router, { setupRouter } from '@/router'

// 权限
import { setupAuth } from '@/directives'

import { createApp } from 'vue'

import App from './App.vue'

import './permission'

import '@/plugins/tongji' // 百度统计
import Logger from '@/utils/Logger'
import gridLayout from 'vue-grid-layout'
import VueDOMPurifyHTML from 'vue-dompurify-html' // 解决v-html 的安全隐患
//引入mockjs
import '@/mock'

// 创建实例
const setupAll = async () => {
  const app = createApp(App)
  if(import.meta.env.MODE == 'production'){
    if('__VUE_DEVTOOLS_GLOBAL_HOOK__' in window){
      // @ts-ignore
      window.__VUE_DEVTOOLS_GLOBAL_HOOK__.vue = app
    }
  }

  //错误边界监听
  app.config.errorHandler = (err, vm, info) => {
    console.log(err, vm, info)
  }
  setupStore(app)

  setupGlobalComponents(app)

  setupElementPlus(app)

  setupFormCreate(app)

  setupRouter(app)

  setupAuth(app)

  await router.isReady()

  app.use(VueDOMPurifyHTML)
  app.use(gridLayout)
  app.mount('#app')
}

setupAll()

Logger.prettyPrimary(`欢迎使用`, import.meta.env.VITE_APP_TITLE)
