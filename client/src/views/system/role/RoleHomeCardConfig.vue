<template>
  <div class="box">
    <div class="box-header">
      <div class="box-header-time">
        <div class="time-bar">
          <el-button
            type="primary"
            @click="handleAdd()"
            class="add-btn"
            :icon="CirclePlus"
            >添加新卡片</el-button
          >
        </div>
      </div>
    </div>
    <!-- <div class="box-footer-tabs">
      <FooterTabs @tabs-plan="tabsPlan" />
    </div> -->

    <canvas
      ref="canvas"
      class="canvas"
      width="4000"
      height="3000"
      v-show="canvasFlag"
    ></canvas>
    <grid-layout
      class="grid-config"
      ref="child"
      :layout="layout"
      :col-num="12"
      :row-height="30"
      :is-draggable="true"
      :is-resizable="true"
      :is-mirrored="false"
      :vertical-compact="true"
      :margin="[10, 10]"
      :use-css-transforms="true"
      @layout-updated="layoutUpdated"
    >
      <grid-item
        v-for="(item, index) in layout"
        class="item"
        :x="item.x"
        :y="item.y"
        :w="item.w"
        :h="item.h"
        :i="item.id"
        :minH="8"
        :minW="4"
        :key="index"
        @move="moveEvent"
        @moved="movedEvent"
        @resize="resizeEvent"
        @resized="resizedEvent"
        @mouseup="handleMouseUp"
        :isResizable="true"
      >
        <div class="card">
          <div class="card-header">
            <div>
              <el-input
                v-if="item.titleFlag"
                ref="inputRef"
                v-model="item.title"
                @blur="changeTitle(item)"
                placeholder="Please input"
              />
              <span
                v-else
                class="card-title"
                >{{ item.title }}</span
              >
            </div>

            <el-dropdown trigger="click">
              <span class="card-more">
                <el-icon color="#c2c8cc"> <MoreFilled /> </el-icon
              ></span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    command="2"
                    @click="handleCommand(item, index, 2)"
                    >重命名</el-dropdown-item
                  >
                  <el-dropdown-item
                    command="3"
                    @click="handleCommand(item, index, 3)"
                    >删除卡片</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="card-content">
            <div class="chart-box">
              <component
                :is="item.currentComponent"
                :key="index"
                :options="item.option"
                :index="index"
                @accept-add="acceptAdd"
              />
            </div>
          </div>
        </div>
      </grid-item>
    </grid-layout>
  </div>

  <RightDrawer
    ref="drawerRef"
    @config-card="configCard"
    :id="roleId"
  />
</template>

<script lang="ts" setup>
import { drawGridLines } from './components/utils'
import { computed, onMounted, ref, watch } from 'vue'
import { CirclePlus, MoreFilled } from '@element-plus/icons-vue'
import RightDrawer from './components/RightDrawer.vue'

import * as HomeApi from '@/api/home'
import FapiaoGadget from '@/views/Home/components/FapiaoGadget/Table.vue'
import EChart from '@/views/Home/components/EChart/EChart.vue'
import Number from '@/views/Home/components/NumberCard/Number.vue'
import TaskGadget from '@/views/Home/components/TaskGadget/index.vue'
import { useUserStore } from '@/store/modules/user'

const message = useMessage()

const props = defineProps<{
  id?: number
  mode: EplusFormMode
  params?: Object
}>()

const roleId: string = (props.params.id || '') as string
const generalDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const enterDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const travelDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const currentComponent = ref()
const refresh = ref(0)
const cardItem = ref(false)
const components = {
  FapiaoGadget,
  EChart,
  Number,
  TaskGadget
}

const child = ref()
const drawerRef = ref(null)
const configRef = ref(null)
const canvasFlag = ref(false)
const currTabId = ref(0)

const inputRef = ref(null)
const winWidth = ref(null)
const winHeight = ref(null)

const dialogOption = reactive({ item: {}, diaTitle: '新增', visible: false })
const layout = reactive([
  // { x: 0, y: 2, w: 2, h: 4, i: '0', type: 'lineChart', option: cardChartOption },
])

const acceptAdd = (data) => {
  dialogOption.visible = data.visible
  dialogOption.item = data.item
  dialogOption.diaTitle = data.diaTitle
}

const generalExpense = (data) => {
  cardItem.value = data

  switch (data.reimbType) {
    case 1:
      generalDialogRef.value?.openCreate()
      break
    case 2:
      enterDialogRef.value?.openCreate('招待费报销')
      break
    case 3:
      travelDialogRef.value?.openCreate('差旅费报销')
      break
  }
}

const homeCardList = async () => {
  let id = currTabId.value
  const res = await HomeApi.getHomeCardList({ id: id })
  layout.length = 0
  res.forEach((e, index) => {
    e.i = e.id
    switch (e.type) {
      case 'number':
        e.currentComponent = Number
        break
      case 'FapiaoGadget':
        e.currentComponent = FapiaoGadget
        break
      case 'TaskGadget':
        e.currentComponent = TaskGadget
        break
      case 'eChart':
        e.currentComponent = EChart
        break
      default:
        e.currentComponent = FapiaoGadget
        break
    }
    layout.push(e)
  })
}

// 打开新增弹框
const handleAdd = () => {
  drawerRef.value.openDrawer()
}

const configCard = (data) => {
  let isHasCard = layout.some((obj) => obj.type === data.type)
  if (isHasCard) {
    message.warning(`当前新增组件已存在,不可重复添加`)
  } else {
    setOption(data)
  }
}

// 新版本
const initChart = () => {
  child.value.layoutUpdate()
}
const layoutLength = computed(() => layout.length)
watch(layoutLength, (newLength) => {
  setTimeout(() => {
    initChart()
  })
})

// 新版本end

const setOption = (card) => {
  let data = {
    title: card.title,
    description: card.description,
    picture: {
      id: 0,
      name: '',
      fileUrl: '',
      mainFlag: 0
    },

    status: 0,
    uniqueCode: Math.floor(Date.now() / 1000), // 十位数的时间戳
    tabId: currTabId.value,
    x: 0,
    y: 4,
    h: 10,
    w: 4,
    i: 1,
    filterCondition: '',
    type: card.type, // 类型
    comp: card.comp, // 组件
    titleFlag: false,
    basicFlag: 0
  }
  createCard(data)
}

const createCard = async (data) => {
  const res = await HomeApi.createCard(data).then(() => {
    homeCardList()
  })
}

const handleCommand = (item, index, type) => {
  switch (type) {
    case 1:
      break
    case 2:
      layout[index].titleFlag = true
      break
    case 3:
      handleDelCard(item)
      break
  }
}
const handleDelCard = async (data) => {
  const res = await HomeApi.deleteHemoCard(data.id).then(() => {
    homeCardList()
  })
}

const handleCardUpdate = async (data) => {
  data.titleFlag = false
  data.currentComponent = ''
  const res = await HomeApi.cardUpdate(data).then(() => {
    homeCardList()
  })
}

const changeTitle = (item) => {
  handleCardUpdate(item)
}

const layoutUpdated = () => {}

const moveEvent = (i, newX, newY) => {
  canvasFlag.value = true
}

const movedEvent = (i, newX, newY) => {
  setCardSize(i)
}

const resizeEvent = (i, newH, newW, newHPx, newWPx) => {
  canvasFlag.value = true
}

const resizedEvent = (i, newH, newW, newHPx, newWPx) => {
  setCardSize(i)
}

const handleMouseUp = () => {
  canvasFlag.value = false
}

// 移动伸缩
const setCardSize = async (i) => {
  let cardPositions = []
  canvasFlag.value = false
  layout.forEach((e) => {
    cardPositions.push({
      id: e.id,
      x: e.x,
      y: e.y,
      w: e.w,
      h: e.h
    })
  })
  const res = await HomeApi.cardPosition({ cardPositions: cardPositions }).then(() => {
    homeCardList()
    initChart()
  })
}
const tabsForm = reactive({
  id: undefined,
  userId: useUserStore().getUser.id,
  roleId: useUserStore().getRoles[0]?.id,
  name: '',
  sort: 0
})

let obtainTabsCount = 0 // 控制条件
const getHomeTabs = async () => {
  let data = {
    roleId: roleId
  }
  const res = await HomeApi.getHomeTabList(data)
  let hasRole = res.some((obj) => obj.roleId === roleId)
  if (hasRole) {
    const defaultTab = res.filter((obj) => obj.sort == 0)
    currTabId.value = defaultTab[0]?.id
    homeCardList()
    obtainTabsCount = 0
  } else {
    let params = {
      userId: null,
      roleId: roleId,
      name: '默认仪表盘',
      sort: 0
    }

    if (obtainTabsCount < 1) {
      createTabs(params)
    }
  }
}

const createTabs = async (params) => {
  obtainTabsCount++
  await HomeApi.createHomeTabs(params).then(() => {
    getHomeTabs()
  })
}

const canvas = ref<HTMLCanvasElement>()
const isDrawGridLines = computed((): boolean => props.isDrawGridLines)

const getWindowSize = () => {
  winWidth.value = window.innerWidth
  winHeight.value = window.innerHeight
}

/**
 * 计算每个item的宽度高度
 */
const gridLayoutRef = ref<HTMLDivElement | null>(null)
// 绘制网格线
const drawGrid = () => {
  drawGridLines(canvas.value, winHeight.value, winWidth.value, 156, 60, 0)
}

onMounted(() => {
  getWindowSize()
  getHomeTabs()
  initChart()
  drawGrid()
  window.onresize = () => {
    drawGrid()
    getWindowSize()
  }
})
</script>

<style scoped lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.box {
  background: #f5f7f9;
  min-height: 100%;

  .box-footer-tabs {
    display: none;
  }

  .box-header {
    position: absolute;
    top: 0;
    right: 0;
    width: 100%;
    background-color: #333;
    // color: white;
    z-index: 1000;

    .box-header-time {
      display: flex;
      flex-direction: row-reverse;
      height: 50px;
      background-color: white;

      .time-bar {
        display: flex;
      }
    }
  }
}

.add-btn {
  margin: 9px;
  padding: 10px;
}

.item {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  // background-color: #ccc;
  border: 2px solid #fff;

  .card {
    width: 100%;
    height: 100%;
    border-radius: 5px;
    background: #fff;

    .card-header {
      display: flex;
      justify-content: space-between;
      width: 100%;
      height: 48px;
      background-color: #fff;
      padding: 10px 14px;
      border-bottom: 1px solid #e7ebef;
      border-radius: 5px 5px 0 0;

      .card-title {
        font-size: 14px;
        line-height: 30px;
      }

      .card-more {
        display: inline-block;
        height: 20px;
        width: 20px;
        border-radius: 2px;
        cursor: pointer;
        text-align: center;
      }

      .card-more:hover {
        background: #ebf0f5;
      }
    }

    .card-content {
      width: 100%;
      height: calc(100% - 48px);
      background-color: #fff;
      padding: 10px 20px;
      border-radius: 0 0 5px 5px;
      position: absolute;
      color: #686565;
      cursor: default;
      display: flex; /* 使用弹性布局 */
      justify-content: center; /* 水平居中 */
      align-items: center; /* 垂直居中 */

      .chart-box {
        width: 100%;
        height: 100%;
      }
    }
  }
}

.item:hover {
  border: 2px solid #1c9fff;
}

.canvas {
  position: absolute;
  top: 40px;
  left: 0;
}

.grid-config {
  // background: #ccc;
  // margin-top: 4px;
}

::v-deep .el-dialog__body {
  padding: 0 !important;
  background: #f5f7f9;
}

/* .vue-grid-item > .vue-resizable-handle {
  background: none;
}
.vue-resizable-handle {
  display: none;
} */
</style>
