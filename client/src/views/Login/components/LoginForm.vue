<template>
  <el-form
    v-show="getShow"
    ref="formLogin"
    :model="loginType == 'phone' ? loginData.mobileForm : loginData.loginForm"
    :rules="loginType == 'phone' ? smsRules : LoginRules"
    class="login-form !p-0"
    label-position="top"
    label-width="120px"
    size="large"
  >
    <!-- <el-form-item>
      <div class="w-[100%] flex items-center justify-around">
        <span
          v-for="item in LOGIN_TYPE_OPTIONS"
          :key="item.value"
          :class="[
            item.value == loginType ? 'b-b-solid b-b-2px text-blue' : '',
            'pl-20px pr-20px text-16px'
          ]"
          @click="loginType = item.value"
          >{{ item.label }}
        </span>
      </div>
    </el-form-item> -->
    <div v-if="loginType == 'phone'">
      <el-form-item prop="mobile">
        <el-input
          v-model="loginData.mobileForm.mobile"
          :placeholder="t('login.mobileNumberPlaceholder')"
          :prefix-icon="iconMobile"
          maxlength="11"
          clearable
        />
      </el-form-item>
      <el-form-item prop="code">
        <el-row
          justify="space-between"
          style="width: 100%"
        >
          <el-input
            class="!w-240px"
            v-model="loginData.mobileForm.code"
            :placeholder="t('login.codePlaceholder')"
            :prefix-icon="iconCode"
            maxlength="4"
          />
          <el-button @click="sendSmsCode">{{ t('login.getSmsCode') }}</el-button>
        </el-row>
      </el-form-item>
    </div>
    <div v-else>
      <el-form-item prop="username">
        <el-input
          v-model="loginData.loginForm.username"
          :placeholder="t('login.usernamePlaceholder')"
          :prefix-icon="iconAvatar"
          clearable
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginData.loginForm.password"
          :placeholder="t('login.passwordPlaceholder')"
          :prefix-icon="iconLock"
          show-password
          type="password"
        />
      </el-form-item>
    </div>
    <!-- <el-form-item>
      <el-row
        justify="space-between"
        style="width: 100%"
      >
        <el-col :span="6">
          <el-checkbox v-model="loginData.rememberMe">
            {{ t('login.remember') }}
          </el-checkbox>
        </el-col>
        <el-col
          :offset="6"
          :span="12"
        >
          <el-link
            style="float: right"
            type="primary"
            @click="backSwitch"
            >{{ t('login.forgetPassword') }}</el-link
          >
        </el-col>
      </el-row>
    </el-form-item> -->
    <el-form-item>
      <XButton
        :loading="loginLoading"
        :title="t('login.login')"
        class="w-[100%] login-btn"
        type=""
        @click="handleLogin"
        @keydown.enter="keyDown"
      />
    </el-form-item>

    <el-divider content-position="center"
      ><span class="color-[#999999]">{{ t('login.otherLogin') }}</span></el-divider
    >
    <el-col
      :span="24"
      style="padding-right: 10px; padding-left: 10px"
    >
      <el-form-item>
        <div class="w-[100%] flex justify-between">
          <Icon
            v-for="(item, key) in socialList"
            :key="key"
            :icon="item.icon"
            :size="30"
            class="anticon cursor-pointer"
            color="#999"
            @click="doSocialLogin(item.type)"
          />
        </div>
      </el-form-item>
    </el-col>
  </el-form>
</template>
<script lang="ts" setup>
import { ElLoading, ElMessage } from 'element-plus'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { useIcon } from '@/hooks/web/useIcon'
import * as authUtil from '@/utils/auth'
import * as LoginApi from '@/api/login'
import { LoginStateEnum, useFormValid, useLoginState } from './useLogin'
import router from '@/router'
import { useCache } from '@/hooks/web/useCache'
// import * as ww from '@wecom/jssdk'
// import { generateUUID } from '@/utils'

// 生成组件唯一id
// const uuid = ref('id-' + generateUUID())
defineOptions({ name: 'LoginForm' })

const { t } = useI18n()
const iconAvatar = useIcon({ icon: 'ep:avatar' })
const iconLock = useIcon({ icon: 'ep:lock' })
const iconMobile = useIcon({ icon: 'ep-iphone' })
const iconCode = useIcon({ icon: 'ep-message' })
const formLogin = ref()
const { validForm } = useFormValid(formLogin)
const { getLoginState } = useLoginState()
const { currentRoute } = useRouter()
const redirect = ref<string>('')
const loginLoading = ref(false)
const getShow = computed(() => unref(getLoginState) === LoginStateEnum.LOGIN)
const socialList = [{ icon: 'ant-design:wechat-work-outlined', type: 30 }]
const LoginRules = {
  username: [required],
  password: [required]
}
const smsRules = {
  mobile: [
    required,
    {
      pattern: /^((1[3,5,8][0-9])|(14[5,7])|(17[0,5,6,7,8])|(19[7]))\d{8}$/,
      message: '请检查手机号格式是否正确'
    }
  ],
  code: [required]
}
// const wwLogin = ww.createWWLoginPanel({
//   el: '#app',
//   params: {
//     login_type: 'CorpApp',
//     appid: 'ww6c36dbda65f99ac0',
//     agentid: '1000005',
//     redirect_uri: 'http://www.sh-syj.top/social-login',
//     state: uuid,
//     redirect_type: 'callback'
//   },
//   onCheckWeComLogin({ isWeComLogin }) {
//     console.log(isWeComLogin)
//   },
//   onLoginSuccess({ code }) {
//     console.log({ code })
//   },
//   onLoginFail(err) {
//     console.log(err)
//   }
// })
const { wsCacheSession } = useCache()
// // 卸载
// wwLogin.unmount()
const message = useMessage()
// 社交登录
const doSocialLogin = async (type: number) => {
  if (type === 0) {
    message.error('此方式未配置')
  } else {
    loginLoading.value = true
    // 计算 redirectUri
    // tricky: type、redirect需要先encode一次，否则钉钉回调会丢失。
    // 配合 Login/SocialLogin.vue#getUrlValue() 使用
    const redirectUri = 'http://www.sh-syj.top/login?' + `type=${type}`
    // 进行跳转
    const res = await LoginApi.socialAuthRedirect(type, encodeURIComponent(redirectUri))
    window.location.href = res
  }
}
const loginData = reactive({
  isShowPassword: false,
  loginForm: {
    username: '',
    password: ''
  },
  rememberMe: false,
  mobileForm: {
    mobile: '',
    code: ''
  }
})
// 记住我
const getCookie = () => {
  const loginForm = authUtil.getLoginForm()
  if (loginForm) {
    loginData.loginForm = {
      ...loginData.loginForm,
      username: loginForm.username ? loginForm.username : loginData.loginForm.username,
      password: loginForm.password ? loginForm.password : loginData.loginForm.password
    }
    if (loginForm.mobile) loginData.mobileForm.mobile = loginForm.mobile
    loginData.rememberMe = loginForm.rememberMe ? true : false
  }
}
const loading = ref() // ElLoading.service 返回的实例

const LOGIN_TYPES = {
  account: t('login.accountTitle'),
  phone: t('login.mobileTitle'),
  email: '请输入账户关联手机号',
  wx: '企业微信登录'
}
const LOGIN_TYPE_OPTIONS = ['account', 'phone'].map((a) => {
  return { value: a, label: LOGIN_TYPES[a] }
})
const loginType = ref('account')
const sendSmsCode = async () => {
  let rule: any = smsRules.mobile[1],
    { mobile } = loginData.mobileForm
  if (rule?.pattern?.test(mobile)) {
    const res = await LoginApi.sendSmsCode({ mobile: loginData.mobileForm.mobile, scene: 1 })
    ElMessage(
      res?.data
        ? { message: '发送验证码失败！', type: 'error' }
        : { message: t('login.SmsSendMsg'), type: 'success' }
    )
  } else {
    ElMessage({ message: '请先输入正确的手机号', type: 'error' })
  }
}
const $emit = defineEmits(['switch'])
const backSwitch = () => {
  $emit('switch', 'forgot')
}
// 登录
const handleLogin = async () => {
  loginLoading.value = true
  try {
    const data = await validForm()
    if (!data) {
      return
    }
    wsCacheSession.clear()
    const res = await (
      loginType.value == 'phone'
        ? LoginApi.smsLogin(loginData.mobileForm)
        : LoginApi.login(loginData.loginForm)
    ).then((res) => {
      localStorage.setItem('loginForm.username', loginData.loginForm.username)
      localStorage.setItem('loginForm.password', loginData.loginForm.password)
      authUtil.setToken(res)
      if (!redirect.value) {
        redirect.value = '/'
      }
      router.push({
        path: '/index'
      })
    })
    // if (!res) {
    //   return
    // }
    // loading.value = ElLoading.service({
    //   lock: true,
    //   text: '正在加载系统中...',
    //   background: 'rgba(0, 0, 0, 0.7)'
    // })
    // if (loginData.rememberMe) {
    //   authUtil.setLoginForm({
    //     ...loginData.loginForm,
    //     ...loginData.mobileForm,
    //     rememberMe: loginData.rememberMe
    //   })
    // } else {
    //   authUtil.removeLoginForm()
    // }
  } catch (err) {
    console.log(err, 'loginErr')
  } finally {
    loginLoading.value = false
    loading.value?.close()
  }
}

//企微登录

// tricky: 配合LoginForm.vue中redirectUri需要对参数进行encode，需要在回调后进行decode
function getUrlValue(key: string): string {
  const url = new URL(decodeURIComponent(location.href))
  return url.searchParams.get(key) ?? ''
}
const QwhandleLogin = async (type: number, code, state) => {
  loginLoading.value = true
  try {
    const res = await LoginApi.socialLogin(type, code, state)
    if (!res) {
      return
    }
    loading.value = ElLoading.service({
      lock: true,
      text: '正在加载系统中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    authUtil.setToken(res)
    if (!redirect.value) {
      redirect.value = '/'
    }
    router.push('/')
  } finally {
    loginLoading.value = false
    loading.value?.close()
  }
}

const keyDown = (e) => {
  if (e.keyCode == 13) {
    handleLogin()
  }
}
watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string
  },
  {
    immediate: true
  }
)
onMounted(() => {
  const type = getUrlValue('type')
  const code = getUrlValue('code')
  const state = getUrlValue('state')
  if (code || state) {
    QwhandleLogin(Number(type), code, state)
  }
  window.addEventListener('keydown', keyDown)
  getCookie()

  loginData.loginForm.username = localStorage.getItem('loginForm.username') || ''
  loginData.loginForm.password = localStorage.getItem('loginForm.password') || ''
})
onUnmounted(() => {
  //销毁事件
  window.removeEventListener('keydown', keyDown, false)
})
</script>

<style lang="scss" scoped>
:deep(.anticon) {
  &:hover {
    color: var(--el-color-primary) !important;
  }
}

.login-code {
  float: right;
  width: 100%;
  height: 38px;

  img {
    width: 100%;
    height: auto;
    max-width: 100px;
    vertical-align: middle;
    cursor: pointer;
  }
}

.login-btn {
  font-weight: 650;
  font-size: 16px;
  box-shadow: 0 4px 8px #719d34;
  height: 48px;
  background-color: #8bc33c;
  color: #fff;
  border-color: #8bc33c;
}
</style>
