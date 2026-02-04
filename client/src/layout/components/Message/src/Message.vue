<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import * as NotifyMessageApi from '@/api/system/notify/message'

defineOptions({ name: 'Message' })

const { push } = useRouter()
const activeName = ref('notice')
const unreadCount = ref(0) // 未读消息数量
const list = ref<any[]>([]) // 消息列表

// 定时刷新器
let refreshTimer: NodeJS.Timeout | null = null

// 获得消息列表
const getList = async () => {
  list.value = await NotifyMessageApi.getUnreadNotifyMessageList()
  // 强制设置 unreadCount 为 0，避免小红点因为轮询太慢，不消除
  unreadCount.value = 0
}

// 获得未读消息数
const getUnreadCount = async () => {
  NotifyMessageApi.getUnreadNotifyMessageCount().then((data) => {
    unreadCount.value = data
  })
}

// 处理通知点击
const handleNotifyClick = async (item: any) => {
  // 标记为已读
  try {
    await NotifyMessageApi.updateNotifyMessageRead([item.id])
    // 从列表中移除
    const index = list.value.findIndex((i) => i.id === item.id)
    if (index > -1) {
      list.value.splice(index, 1)
    }
  } catch (e) {
    console.error('标记已读失败', e)
  }

  // 根据业务类型跳转到对应页面
  if (item.businessType === 'bpm_task' && item.businessParams?.processInstanceId) {
    push({
      name: 'BpmProcessInstanceDetail',
      query: {
        id: item.businessParams.processInstanceId
      }
    })
  } else {
    // 默认跳转到站内信详情页
    goMyList()
  }
}

// 跳转我的站内信
const goMyList = () => {
  push({
    name: 'MyNotifyMessage'
  })
}

// 开始定时刷新未读消息数量
const startAutoRefresh = () => {
  // 清除可能存在的旧定时器
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  // 每2分钟刷新一次未读消息数量
  refreshTimer = setInterval(() => {
    getUnreadCount()
  }, 1000 * 60 * 2)
}

// 停止定时刷新
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// ========== 初始化 =========
onMounted(() => {
  // 首次加载小红点
  getUnreadCount()
  // 启动定时刷新
  startAutoRefresh()
})

onUnmounted(() => {
  // 组件卸载时停止定时刷新
  stopAutoRefresh()
})
</script>
<template>
  <div class="message">
    <ElPopover
      :width="400"
      placement="bottom"
      trigger="click"
    >
      <template #reference>
        <ElBadge
          :is-dot="unreadCount > 0"
          class="item items-center !flex"
        >
          <Icon
            :size="18"
            class="cursor-pointer"
            icon="ep:bell"
            @click="getList"
          />
        </ElBadge>
      </template>
      <ElTabs v-model="activeName">
        <ElTabPane
          label="我的站内信"
          name="notice"
        >
          <el-scrollbar class="message-list">
            <template v-if="list.length > 0">
              <template
                v-for="item in list"
                :key="item.id"
              >
                <div
                  class="message-item"
                  :class="{ 'message-item-clickable': item.businessType }"
                  @click="handleNotifyClick(item)"
                >
                  <img
                    alt=""
                    class="message-icon"
                    src="@/assets/imgs/avatar.gif"
                  />
                  <div class="message-content">
                    <span class="message-title">
                      {{ item.templateNickname }}：{{ item.templateContent }}
                    </span>
                    <span class="message-date">
                      {{ formatDate(item.createTime) }}
                    </span>
                  </div>
                  <div
                    v-if="item.businessType"
                    class="message-action"
                  >
                    <Icon
                      icon="ep:arrow-right"
                      :size="14"
                    />
                  </div>
                </div>
              </template>
            </template>
            <template v-else>
              <div class="message-empty">
                <Icon
                  icon="ep:message"
                  :size="48"
                  color="#c0c4cc"
                />
                <span>暂无新消息</span>
              </div>
            </template>
          </el-scrollbar>
        </ElTabPane>
      </ElTabs>
      <!-- 更多 -->
      <div style="margin-top: 10px; text-align: right">
        <XButton
          preIcon="ep:view"
          title="查看全部"
          type="primary"
          @click="goMyList"
        />
      </div>
    </ElPopover>
  </div>
</template>
<style lang="scss" scoped>
.message-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 260px;
  line-height: 45px;
  color: var(--el-text-color-secondary);
}

.message-list {
  display: flex;
  height: 400px;
  flex-direction: column;

  .message-item {
    display: flex;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-light);
    transition: background-color 0.2s;

    &:last-child {
      border: none;
    }

    &.message-item-clickable {
      cursor: pointer;

      &:hover {
        background-color: var(--el-fill-color-light);
      }
    }

    .message-icon {
      width: 40px;
      height: 40px;
      margin: 0 20px 0 5px;
    }

    .message-content {
      display: flex;
      flex-direction: column;
      flex: 1;

      .message-title {
        margin-bottom: 5px;
      }

      .message-date {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }

    .message-action {
      padding: 0 10px;
      color: var(--el-text-color-secondary);
    }
  }
}
</style>
