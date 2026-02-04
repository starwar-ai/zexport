<template>
  <div class="tab-pane">
    <el-popover
      placement="bottom"
      :width="70"
      trigger="click"
    >
      <template #reference>
        <el-button
          class="m-2"
          :icon="Monitor"
          >{{ currTabName }}</el-button
        >
      </template>
      <div class="pane">
        <el-button
          type="text"
          @click="handleAddTab()"
        >
          <el-icon
            :size="size"
            :color="color"
          >
            <Plus />
          </el-icon>
        </el-button>

        <div
          v-for="(item, index) in tabsList"
          :key="item.id"
          class="pane-box"
          style="
            height: 30px;
            display: flex;
            align-items: center;
            padding-left: 10px;
            cursor: pointer;
          "
          @click="handlePane(item, index)"
          :class="{ active: activeIndex === index }"
        >
          <Monitor style="width: 1em; height: 1em; margin-right: 1px" />

          <span class="pane-title">{{ item.name }}</span>

          <el-dropdown
            trigger="click"
            v-show="item.moreSty"
          >
            <span class="card-more">
              <el-icon color="#c2c8cc">
                <MoreFilled style="width: 2em; height: 1em; margin-right: 1px" /> </el-icon
            ></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  command="1"
                  :icon="Setting"
                  @click="handleTabsSort(item)"
                  >设为首个标签页</el-dropdown-item
                >
                <el-dropdown-item
                  command="2"
                  :icon="EditPen"
                  @click="handleTabsUpdate(item)"
                  >重命名</el-dropdown-item
                >
                <el-dropdown-item
                  command="3"
                  :disabled="item.sort == 0"
                  :icon="Delete"
                  @click="handleTabDel(item)"
                  >删除仪表盘</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script lang="ts" setup>
import { ElButton } from 'element-plus'
import { Monitor, MoreFilled, Plus } from '@element-plus/icons-vue'
import * as HomeApi from '@/api/home'
import { useUserStore } from '@/store/modules/user'

const message = useMessage()
const visible = ref(false)
const activeIndex = ref(0)
const props = defineProps<{}>()
const emit = defineEmits(['addCard'])
const paneFlag = ref(false)
const currTabName = ref('')
const tabsForm = reactive({
  id: undefined,
  userId: useUserStore().getUser.id,
  roleId: useUserStore().getRoles[0]?.id,
  name: '',
  sort: 0
})

const tabsList = ref([{ id: 1, name: '默认仪表盘', moreSty: false }])

const getHomeTabs = async () => {
  let params = {
    userId: tabsForm.userId
    // roleId: tabsForm.roleId
  }
  const res = await HomeApi.getHomeTabList(params)
  tabsList.value = res
  if (!tabsList.value.length) {
    tabsForm.name = '默认仪表盘'
    createHomeTabs(tabsForm)
  }
  currTabName.value = tabsList.value[0].name
  emit('tabsPlan', res[0])
}

// 新增仪表盘
const handleAddTab = () => {
  ElMessageBox.prompt('仪表盘名称', '新建仪表盘', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPattern: /[^\s]/,
    inputErrorMessage: '名称不能为空'
  })
    .then(({ value }) => {
      tabsForm.name = value
      tabsForm.sort = tabsList.value.length + 1
      createHomeTabs(tabsForm)
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消'
      })
    })
}

const createHomeTabs = async (tabsForm) => {
  await HomeApi.createHomeTabs(tabsForm).then(() => {
    getHomeTabs()
    activeIndex.value = tabsList.value.length
  })
}

const handleTabsSort = async (data) => {
  let tabIds = []
  tabsList.value.forEach((e) => {
    if (data !== e) {
      tabIds.push(e.id)
    }
  })
  tabIds.unshift(data.id)
  await HomeApi.homeTabsSort({ tabIds: tabIds }).then(() => {
    getHomeTabs()
    activeIndex.value = 0
  })
}

const handleTabsUpdate = async (data) => {
  ElMessageBox.prompt('仪表盘名称', '重命名仪表盘', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPattern: /[^\s]/,
    inputErrorMessage: '名称不能为空'
  })
    .then(({ value }) => {
      data.name = value
      tabsUpdate(data)
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消'
      })
    })
}

const handleTabDel = async (data) => {
  ElMessageBox.confirm('是否删除当前仪表盘？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await HomeApi.deleteHemoTab(data.id)
    message.success('删除成功')
    getHomeTabs()
    activeIndex.value = 0
  })
}

const tabsUpdate = async (data) => {
  await HomeApi.homeTabUpdate({ ...data }).then(() => {
    getHomeTabs()
    activeIndex.value = 0
  })
}

// const tabsDel = async (data) => {
//   await HomeApi.deleteHemoTab({ ...data }).then(() => {
//     getHomeTabs()
//     activeIndex.value = 0
//   })
// }

const handlePane = (item, index) => {
  currTabName.value = item.name
  activeIndex.value = index
  tabsList.value.forEach((e) => {
    e.moreSty = e == item ? true : false
  })
  emit('tabsPlan', item)
}

const openDrawer = () => {
  visible.value = true
}

const handleTab = () => {
  // paneFlag.value = !paneFlag.value
}

onMounted(() => {
  getHomeTabs()
})

defineExpose({
  openDrawer
})
</script>

<style scoped lang="scss">
.tab-pane {
  position: reactive;
  background: #ffff;

  .footer-tab {
    height: 50px;
    padding: 0 10px;
    line-height: 50px;
  }

  .pane {
    position: absolute;
    left: 10px;
    bottom: 60px;
    width: 160px;
    background: #ffff;
    border-radius: 5px;
    box-shadow: 0 1px 2px #8888;
    padding: 10px;
  }

  .pane-box {
    display: flex;
    align-items: center;
  }

  .pane-box:hover {
    background: #e8f9ff;
    color: #1c9fff;
  }

  ::v-deep .el-divider--vertical {
    margin: 0 !important;
  }
}

.pane-box.active {
  background-color: #e8f9ff;
}

.card-more {
  margin-left: 5px;
}
</style>
