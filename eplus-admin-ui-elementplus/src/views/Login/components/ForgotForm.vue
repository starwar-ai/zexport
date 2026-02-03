<template>
  <el-form
    v-show="getShow"
    ref="formForgot"
    :model="forgotType == 'phone' ? loginData.mobileForm : loginData.emailForm"
    :rules="forgotType == 'phone' ? smsRules : emailRules"
    class="login-form !p-0"
    label-position="top"
    label-width="120px"
    size="large"
  >
    <el-form-item>
      <div class="w-[100%] flex items-center justify-around">
        <span
          v-for="item in LOGIN_TYPE_OPTIONS"
          :key="item.value"
          :class="[
            item.value == forgotType ? 'b-b-solid b-b-2px text-blue' : '',
            'pl-20px pr-20px text-16px'
          ]"
          @click="forgotType = item.value"
          >{{ item.label }}
        </span>
      </div>
    </el-form-item>
    <div v-if="forgotType == 'phone'">
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
            clearable
          />
          <el-button @click="sendSmsCode">{{ t('login.getSmsCode') }}</el-button>
        </el-row>
      </el-form-item>
    </div>
    <div v-else>
      <el-form-item prop="depId">
        <eplus-dept-select
          v-model="loginData.emailForm.depId"
          placeholder="账户所属部门/组织"
          class="w-[100%]"
        />
      </el-form-item>
      <el-form-item prop="email">
        <el-input
          v-model="loginData.emailForm.email"
          placeholder="账户邮箱"
          :prefix-icon="iconCode"
          clearable
        />
      </el-form-item>
    </div>
    <el-form-item>
      <el-row
        justify="space-between"
        style="width: 100%"
      >
        <el-col
          :span="15"
          class="text-red"
        >
          {{
            checkMessage ||
            (forgotType == 'phone' ? '输入手机号获取临时密码' : '输入邮箱获取临时密码')
          }}
        </el-col>
        <el-col :span="5">
          <el-link
            style="float: right"
            type="primary"
            @click="backLogin"
            >返回登录</el-link
          >
        </el-col>
      </el-row>
    </el-form-item>
    <el-form-item>
      <XButton
        :loading="loginLoading"
        title="提交"
        class="w-[100%]"
        type="primary"
        @click="handleSubmit"
      />
    </el-form-item>
  </el-form>
</template>
<script lang="ts" setup>
import { ElMessage } from 'element-plus'
import { useIcon } from '@/hooks/web/useIcon'
import * as LoginApi from '@/api/login'
import { LoginStateEnum, useFormValid, useLoginState } from './useLogin'

defineOptions({ name: 'EmailForm' })

const { t } = useI18n()
const iconMobile = useIcon({ icon: 'ep-iphone' })
const iconCode = useIcon({ icon: 'ep-message' })
const formForgot = ref()
const { validForm } = useFormValid(formForgot)
const { getLoginState } = useLoginState()
const loginLoading = ref(false)
const getShow = computed(() => unref(getLoginState) === LoginStateEnum.LOGIN)

const $emit = defineEmits(['switch'])
const backLogin = () => {
  $emit('switch', 'login')
}
const emailRules = {
  depId: [required],
  email: [required, { type: 'email', message: '请检查邮箱格式！' }]
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
const loginData = reactive({
  isShowPassword: false,
  emailForm: {
    depId: '',
    email: ''
  },
  rememberMe: false,
  mobileForm: {
    mobile: '',
    code: ''
  }
})

const loading = ref() // ElLoading.service 返回的实例

const LOGIN_TYPES = { email: '通过邮箱找回', phone: '通过手机找回' }
const LOGIN_TYPE_OPTIONS = ['email', 'phone'].map((a) => {
  return { value: a, label: LOGIN_TYPES[a] }
})
const forgotType = ref('email')
const checkMessage = ref('')
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
// 登录
const handleSubmit = async () => {
  loginLoading.value = true
  try {
    const data = await validForm()
    if (!data) {
      return
    }
    //const res = await (forgotType.value=='phone' ? LoginApi.smsLogin(loginData.mobileForm) : LoginApi.login(loginData.emailForm))
    const res = await new Promise((rec) => {
      checkMessage.value = '还未配置忘记密码接口！'
      rec(false)
    })
    if (!res) {
      return
    }
    checkMessage.value = '临时密码已发送至' + (forgotType.value == 'phone' ? '手机' : '邮箱')
  } finally {
    loginLoading.value = false
    loading.value?.close()
  }
}

onMounted(() => {})
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
</style>
