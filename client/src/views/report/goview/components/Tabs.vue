<template>
  <div class="report">
    <span class="report-title"> 数据报表 </span>
    <el-divider
      direction="vertical"
      border-style="solid"
      style="margin-top: 7px"
    />
    <div
      class="tabs-container"
      ref="tabsContainer"
    >
      <el-icon
        v-if="btnShow"
        color="#409efc"
        @click="scrollLeft"
        class="no-inherit"
      >
        <ArrowLeftBold />
      </el-icon>
      <div
        class="tabs-scroll-wrapper"
        ref="tabsScrollWrapper"
      >
        <div
          class="tab"
          v-for="(tab, index) in tabs"
          :key="index"
          @click.stop="setActiveTab(tab, index)"
          :class="{ active: activeIndex === index }"
        >
          {{ tab.name }}
          <span
            class="tabsClose"
            @click.stop="handleClose(tab)"
          >
            <el-icon style="width: 14px; height: 14px; margin-right: 1px">
              <Close />
            </el-icon>
          </span>
        </div>
        <el-button
          type="type"
          v-if="rolesFlag"
          @click="handledialog"
          >+</el-button
        >
      </div>

      <el-icon
        v-if="btnShow"
        color="#409efc"
        @click="scrollRight"
        class="no-inherit"
      >
        <ArrowRightBold />
      </el-icon>
    </div>
    <!-- <button @click="addTab">添加Tab</button> -->
    <EplusTableSetting
      :columnSettings="syncedColumnSettings"
      :parentColumnSettings="syncedParentColumnSettings"
      :key="Math.random()"
      @reset-tabs="resetTabs"
    />
  </div>
  <Dialog
    v-model="dialogFormVisible"
    title="添加报表"
    width="500"
  >
    <el-form :model="form">
      <el-form-item
        label="报表名称"
        :label-width="formLabelWidth"
      >
        <el-input
          v-model="form.name"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item
        label="报表id"
        :label-width="formLabelWidth"
      >
        <el-input
          v-model="form.componentId"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item
        label="报表类型"
        :label-width="formLabelWidth"
      >
        <el-select
          v-model="form.type"
          placeholder="报表类型"
        >
          <el-option
            label="图片类型"
            value="1"
          />
          <el-option
            label="表格类型"
            value="2"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSave"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import EplusTableSetting from './TableSetting.vue'
import { ArrowLeftBold, ArrowRightBold, Close } from '@element-plus/icons-vue'
import * as JMReportApi from '@/api/report/jimu'
import { useUserStore } from '@/store/modules/user'

const message = useMessage()
const tabs = ref([])
const activeIndex = ref(0)
const tabsContainer = ref(null)
const tabsScrollWrapper = ref(null)
const syncedParentColumnSettings = ref([])
const btnShow = ref(false)
const dialogFormVisible = ref(false)
const syncedColumnSettings = ref([])
const userRoles = useUserStore().getRoles
const rolesFlag = userRoles.some((roles) => roles.code == 'super_admin')

const form = reactive({
  name: '',
  componentId: '',
  type: ''
})
const emit = defineEmits(['item'])
const setActiveTab = (tab, index) => {
  activeIndex.value = index
  emit('item', tab)
  scrollToActiveTab()
}
const handledialog = () => {
  dialogFormVisible.value = true
}

const handleSave = async () => {
  const res = await JMReportApi.createReport(form)
  getTabsList()
  message.success('新增成功')
  dialogFormVisible.value = false
}

const handleClose = async (data) => {
  ElMessageBox.confirm('是否删除当前报表？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await JMReportApi.deleteReportData(data.id)
    message.success('删除成功')
    getTabsList()
    activeIndex.value = 0
  })
}

const scrollLeft = () => {
  if (activeIndex.value > 0) {
    activeIndex.value = 0
    scrollToActiveTab()
  }
}

const scrollRight = () => {
  if (activeIndex.value < tabs.value.length - 1) {
    activeIndex.value = tabs.value.length - 1
    scrollToActiveTab()
  }
}

const scrollToActiveTab = () => {
  if (tabsScrollWrapper.value && tabsContainer.value && tabs.value) {
    const tabWidth = tabsScrollWrapper.value.offsetWidth / tabs.value.length
    const scrollPosition = activeIndex.value * tabWidth
    tabsScrollWrapper.value.scrollLeft = scrollPosition
  }
}

const scrollable = computed(() => {
  let comp = false

  if (tabsContainer.value && tabsScrollWrapper.value) {
    let tabsW = tabsContainer.value.clientWidth
    let planW = tabsScrollWrapper.value.clientWidth
    comp = tabsW - planW < 10 ? true : false
  }

  return true
})

const getTabsList = async () => {
  const res = await JMReportApi.getJMReportList()
  tabs.value = res
  if (res == null) {
    return false
  }
  tabs.value.forEach((e, i) => {
    if (e.sort == 0) {
      emit('item', e)
    }
  })

  setTimeout(function () {
    btnShow.value =
      tabsContainer.value.clientWidth - tabsScrollWrapper.value.clientWidth < 10 ? true : false
  }, 100)
}

const resetTabs = () => {
  getTabsList()
}

const getTabsRoleList = async () => {
  const res = await JMReportApi.getJMReportRoleList()
  if (res == null) {
    return false
  }

  res.forEach((e, i) => {
    syncedColumnSettings.value.push({
      disable: false,
      fixed: undefined,
      hide: undefined,
      label: e.name,
      roleId: e.roleId,
      sort: e.sort,
      // minWidth: '150px',
      prop: 'code' + i,
      type: e.type,
      userId: e.userId,
      id: e.id,
      componentId: e.componentId
    })
  })
}

onMounted(() => {
  if (tabs.value.length > 0) {
    scrollToActiveTab()
  }
  getTabsRoleList()
  getTabsList()
})

watch(tabs, () => {
  // if (tabs.value.length > 0) {
  scrollToActiveTab()
  // }
})
watch(tabsScrollWrapper.value, (newVal) => {})
</script>
<style scoped>
.report {
  display: flex;
  justify-content: space-between;
  padding: 1px 10px;
}

.report-title {
  display: inline-block;
  margin: 4px 3px 0;
  font-weight: 900;
}

.tabs-container {
  position: relative;
  width: calc(100% - 120px);
  overflow: hidden;
  display: flex;
  /* justify-content: space-between; */
}

.no-inherit {
  margin: 5px 6px 0;
  font-size: 20px;
}

.tabs-scroll-wrapper {
  display: flex;
  overflow-x: hidden;
  scroll-snap-type: x mandatory;
  -webkit-overflow-scrolling: touch; /* 平滑滚动（可选） */
}

.hide-overflow-x {
  overflow-x: hidden; /* 隐藏水平滚动条 */
  scrollbar-width: none; /* 对于支持的浏览器隐藏滚动条宽度 */
}

.tab {
  scroll-snap-align: start;
  min-width: 100px; /* 每个Tab的宽度 */
  padding: 3px 5px 3px 10px;
  border: 1px solid #d9ecff;
  background-color: #fff;
  cursor: pointer;
  text-align: center;
  margin: 0 2px;
  flex-shrink: 0; /* 防止Tab在滚动时缩小 */
}

.tab.active {
  background-color: #5b9cff;
  /* border-bottom: 1px solid #d00; */
  color: #fff;
}

.scroll-btn {
  /* position: absolute;
  top: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.1);
  border: none;
  cursor: pointer;
  z-index: 1000; */
}

.tabsClose {
  display: inline-block;
  margin-left: 6px;
}

/* 定制滚动条轨道 */
/* ::-webkit-scrollbar-track {
  background-color: #f1f1f1;
} */

/* 定制滚动条的按钮 */
/* ::-webkit-scrollbar-thumb {
  background-color: #f5f7f9;
  border-radius: 10px;
  height: 3px !important;
} */

/* 设置滚动条的宽度 */
/* ::-webkit-scrollbar {
  width: 3px; 
} */
</style>
