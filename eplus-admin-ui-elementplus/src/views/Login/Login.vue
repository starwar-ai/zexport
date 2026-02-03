<template>
  <div class="login-container flex relative h-[100%] lt-md:px-10px lt-sm:px-10px lt-xl:px-10px">
    <div class="left-container"></div>
    <div class="right-container">
      <div class="m-auto h-full flex flex-col items-center justify-center">
        <div class="login-logo w-440px h-auto">
          <img
            src="/login-logo.jpg"
            alt=""
            class="w-100% h-auto mb-72px"
          />
        </div>
        <div class="w-400px">
          <div class="title color-[var(--login-main-color)]">{{ t('login.login') }}</div>
          <ForgotForm
            v-if="switchTag === 'forgot'"
            @switch="handleSwitch"
          />
          <!-- 三方登录 -->
          <SSOLogin
            v-else-if="switchTag === 'ssoLogin'"
            class="m-auto h-auto p-20px lt-xl:(rounded-3xl light:bg-white)"
          />
          <!-- 账号登录 -->
          <LoginForm
            v-else
            @switch="handleSwitch"
          />
          <div
            class="login-version cursor-pointer"
            @click="handleVersionClick"
            style="pointer-events: auto; user-select: auto"
            translate="no"
          >
            v{{ version }}
          </div>
          <el-dialog
            v-model="showVersionDialog"
            title="更新日志"
            width="600px"
            :close-on-click-modal="true"
            class="version-dialog"
          >
            <div class="version-timeline-container">
              <el-timeline>
                <el-timeline-item
                  v-for="item in versionLogs"
                  :key="item.id"
                  :timestamp="`v${item.publishVer}`"
                  placement="top"
                >
                  <div style="white-space: pre-line">{{ item.publishDesc }}</div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-dialog>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { getLastVersion, getVersionPage } from '@/api/infra/version'
import { useAppStore } from '@/store/modules/app'
import { ForgotForm, LoginForm, SSOLogin } from './components'

const { t } = useI18n()

defineOptions({ name: 'Login' })

const appStore = useAppStore()
const switchTag = ref('login')
const handleSwitch = (tag) => {
  switchTag.value = tag
}

const version = ref('')
const versionLogs = ref<any[]>([])
const showVersionDialog = ref(false)

const fetchVersionLogs = async () => {
  try {
    const res = await getVersionPage({ pageNo: 1, pageSize: 20 })
    versionLogs.value = res?.list || []
  } catch (e) {
    versionLogs.value = []
  }
}

const handleVersionClick = async () => {
  await fetchVersionLogs()
  showVersionDialog.value = true
}

onMounted(async () => {
  try {
    const res = await getLastVersion()
    version.value = res?.publishVer || ''
  } catch (e) {
    version.value = ''
  }
})
</script>

<style scoped lang="scss">
.login-container {
  overflow: hidden;

  .left-container {
    flex: 0.375;
    background: url('/background-img.jpg');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    min-width: 0;
  }

  .right-container {
    flex: 0.625;

    .title {
      font-size: 32px;
      font-weight: 8;
      margin-bottom: 24px;
    }
  }
}

.login-version {
  position: absolute;
  left: 50%;
  bottom: 20px;
  transform: translateX(-50%);
  font-size: 14px;
  color: #bbb;
  font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif;
  font-weight: 400;
  letter-spacing: 1px;
  user-select: none;
  pointer-events: none;
  z-index: 2;
}

.version-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
  }
}

.version-timeline-container {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

@media (width <= 500px) {
  .login-version {
    font-size: 12px;
    bottom: 10px;
  }
}

@media screen and (width <= 864px) {
  .login-container {
    .left-container {
      display: none;
    }

    .right-container {
      flex: 1;
    }
  }
}
</style>
