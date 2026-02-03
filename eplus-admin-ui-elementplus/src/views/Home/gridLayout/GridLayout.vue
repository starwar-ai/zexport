<template>
  <div class="box">
    <div
      class="version-info"
      style="translate: no"
      translate="no"
      >v{{ version }}</div
    >
    <div class="box-header">
      <div class="box-header-time">
        <div class="time-bar">
          <HeaderTimeBar />
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
    <div class="box-footer-tabs">
      <FooterTabs @tabs-plan="tabsPlan" />
    </div>

    <div
      class="grid-background"
      v-show="canvasFlag"
      :style="gridBackgroundStyle"
    ></div>
    <grid-layout
      class="grid-layout"
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
      :prevent-collision="false"
      @layout-updated="layoutUpdated"
      @breakpoint-changed="breakpointChangedEvent"
    >
      <grid-item
        v-for="(item, index) in layout"
        class="item"
        :x="item.x"
        :y="item.y"
        :w="item.w"
        :h="item.h"
        :i="item.id"
        :minH="item.type == 'number' ? 6 : 10"
        :minW="item.type == 'number' ? 3 : 4"
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
                  <!-- <el-dropdown-item
                    command="1"
                    :icon="Setting"
                    @click="handleCommand(item, index, 1)"
                    >配置</el-dropdown-item
                  > -->
                  <el-dropdown-item
                    command="2"
                    :icon="EditPen"
                    @click="handleCommand(item, index, 2)"
                    >重命名</el-dropdown-item
                  >
                  <el-dropdown-item
                    command="3"
                    :icon="Delete"
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

  <Drawer
    ref="drawerRef"
    @config-card="configCard"
  />
  <!-- <ConfigDialog
    ref="configRef"
    @config-card="configCard"
  /> -->
  <TableDialog
    ref="tableDialogRef"
    @general-expense="generalExpense"
    :visible="dialogOption.visible"
    :item="dialogOption.item"
    :dialogOption="dialogOption"
    :key="new Date()"
  />
  <!-- oa一般费用报销 -->
  <eplus-dialog ref="generalDialogRef">
    <template #create>
      <general-expense-form
        mode="create"
        :homeCard="cardItem"
      />
    </template>
  </eplus-dialog>
  <!-- oa招待费用报销 -->
  <eplus-dialog ref="enterDialogRef">
    <template #create>
      <entertainment-expenses-form
        mode="create"
        :homeCard="cardItem"
      />
    </template>
  </eplus-dialog>
  <!-- oa差旅费用报销 -->
  <eplus-dialog ref="travelDialogRef">
    <template #create>
      <travel-reimb-form
        mode="create"
        :homeCard="cardItem"
      />
    </template>
  </eplus-dialog>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue'
import { CirclePlus, Delete, EditPen, MoreFilled } from '@element-plus/icons-vue'
import Drawer from './Drawer.vue'
// import ConfigDialog from './ConfigDialog.vue'
import FooterTabs from './FooterTabs.vue'
import TableDialog from '../components/FapiaoGadget/TableDialog.vue'
import * as HomeApi from '@/api/home'
import FapiaoGadget from '../components/FapiaoGadget/Table.vue'
import EChart from '../components/EChart/EChart.vue'
import Number from '../components/NumberCard/Number.vue'
import TaskGadget from '../components/TaskGadget/index.vue'
import HeaderTimeBar from './HeaderTimeBar.vue'

import GeneralExpenseForm from '@/views/oa/generalExpense/GeneralExpenseForm.vue'
import EntertainmentExpensesForm from '@/views/oa/entertainmentExpenses/EntertainmentExpensesForm.vue'
import TravelReimbForm from '@/views/oa/travelreimb/TravelReimbForm.vue'
import { getLastVersion } from '@/api/infra/version'

const message = useMessage()
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

const tabsPlan = (data) => {
  currTabId.value = data?.id

  if (currTabId.value == undefined) {
    return false
  }
  homeCardList()
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

// 计算新卡片的默认位置（从左到右，从上到下）
const findNextPosition = (w: number, h: number) => {
  const colNum = 12

  if (layout.length === 0) {
    return { x: 0, y: 0 }
  }

  // 找到最大的 y + h 值来确定网格高度
  let maxY = 0
  layout.forEach((item) => {
    const bottom = item.y + item.h
    if (bottom > maxY) maxY = bottom
  })

  // 增加额外行以便找到空位
  maxY += h

  // 创建二维数组表示占用情况
  const grid: boolean[][] = []
  for (let y = 0; y < maxY; y++) {
    grid[y] = new Array(colNum).fill(false)
  }

  // 标记已占用的位置
  layout.forEach((item) => {
    for (let dy = 0; dy < item.h; dy++) {
      for (let dx = 0; dx < item.w; dx++) {
        const py = item.y + dy
        const px = item.x + dx
        if (py < maxY && px < colNum) {
          grid[py][px] = true
        }
      }
    }
  })

  // 从上到下，从左到右查找第一个可以放置的位置
  for (let y = 0; y <= maxY - h; y++) {
    for (let x = 0; x <= colNum - w; x++) {
      let canPlace = true
      // 检查该位置是否可以放置 w x h 的卡片
      for (let dy = 0; dy < h && canPlace; dy++) {
        for (let dx = 0; dx < w && canPlace; dx++) {
          if (grid[y + dy][x + dx]) {
            canPlace = false
          }
        }
      }
      if (canPlace) {
        return { x, y }
      }
    }
  }

  // 如果没找到，就放在最底部
  return { x: 0, y: maxY }
}

const setOption = (card) => {
  const h = card.type == 'number' ? 6 : 10
  const w = card.type == 'number' ? 3 : 4
  const { x, y } = findNextPosition(w, h)

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
    x: x,
    y: y,
    h: h,
    w: w,
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
    layout[layout.length - 1].id = res.data
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

const breakpointChangedEvent = (newBreakpoint, newLayout) => {}

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

// Grid 配置常量
const COL_NUM = 12
const ROW_HEIGHT = 30
const MARGIN_X = 10
const MARGIN_Y = 10

const getWindowSize = () => {
  winWidth.value = window.innerWidth
  winHeight.value = window.innerHeight
}

// 计算网格背景样式
const gridBackgroundStyle = computed(() => {
  const containerWidth = winWidth.value || window.innerWidth
  // vue-grid-layout 的列宽计算: (容器宽度 - 左右边距) / 列数 - 间距
  // 但实际上 vue-grid-layout 的计算是: (containerWidth - margin[0]) / colNum - margin[0]
  // 简化为: (containerWidth - margin[0] * (colNum + 1)) / colNum
  const colWidth = (containerWidth - MARGIN_X * (COL_NUM + 1)) / COL_NUM
  // 单元格周期（包含间距）
  const cellWidth = colWidth + MARGIN_X
  const cellHeight = ROW_HEIGHT + MARGIN_Y

  // 网格线绘制在每个格子的边界上
  // 背景起始位置对齐 vue-grid-layout 的第一个格子
  return {
    backgroundSize: `${cellWidth}px ${cellHeight}px`,
    backgroundPosition: `${MARGIN_X + colWidth}px ${MARGIN_Y + ROW_HEIGHT}px`,
    backgroundImage: `
      linear-gradient(to right, rgba(28, 159, 255, 0.35) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(28, 159, 255, 0.35) 1px, transparent 1px)
    `
  }
})

const version = ref('')

onMounted(async () => {
  getWindowSize()
  initChart()
  window.onresize = () => {
    getWindowSize()
  }
  try {
    const res = await getLastVersion()
    version.value = res?.publishVer || ''
  } catch (e) {
    version.value = ''
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
  .version-info {
    position: absolute;
    left: 20px;
    top: 22px;
    transform: none;
    font-size: 14px;
    color: #222;
    font-family: 'Arial', 'Helvetica Neue', Helvetica, sans-serif;
    font-weight: 400;
    letter-spacing: 1px;
    user-select: none;
    pointer-events: none;
    z-index: 2000;
    background: rgba(255, 255, 255, 0.95);
    padding: 4px 10px;
    border-radius: 5px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    height: 28px;
    display: flex;
    align-items: center;
  }
  .box-footer-tabs {
    position: absolute;
    bottom: 0;
    right: 10px;
    width: 100%;
    background-color: #333;
    padding-left: 10px;
    z-index: 1000;
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

/* .grid {
    position: relative;
    width: 100%;
}

.grid-layout {
    display: grid;
    place-items: center center;
    grid-template-columns: v-bind('layoutStyle.gridTemplateColumns');
    grid-auto-rows: v-bind('layoutStyle.gridAutoRows');
    gap: v-bind('layoutStyle.gap');
    height: v-bind('layoutStyle.height');
    overflow-x: hidden;
    touch-action: none;
    transition: width .2s ease, height .2s ease;
}

.dragingItem {
    width: 100%;
    height: 100%;
    background-color: #2c7ec2;
    grid-area: v-bind('dragingstyle.yStart') / v-bind('dragingstyle.xStart') / v-bind('dragingstyle.yEnd') / v-bind('dragingstyle.xEnd');
    z-index: 5;
}
*/
/* 拖动时显示的网格背景 */
.grid-background {
  position: absolute;
  top: 49px;
  left: 0;
  right: 0;
  bottom: 40px;
  pointer-events: none;
  z-index: 0;
}

.grid-layout {
  position: relative;
  margin-top: 49px;
  margin-bottom: 40px;
}

/* 限制拖拽元素不能超出grid区域 */
:deep(.vue-grid-layout) {
  min-height: calc(100vh - 89px); /* 49px header + 40px footer */
}

/* .vue-grid-item > .vue-resizable-handle {
  background: none;
}
.vue-resizable-handle {
  display: none;
} */

/* 拖动时的投影样式 - 与格子对齐 */
:deep(.vue-grid-placeholder) {
  background: rgba(28, 159, 255, 0.2) !important;
  border: 2px dashed #1c9fff !important;
  border-radius: 5px;
  transition: none !important; /* 禁用过渡动画，使投影立即吸附到格子 */
  box-sizing: border-box;
}

/* 拖动中的元素样式 */
:deep(.vue-grid-item.vue-draggable-dragging) {
  transition: none !important;
  opacity: 0.8;
  z-index: 100;
  /* 添加与placeholder相同的阴影效果，让用户看到对齐参考 */
  box-shadow: 0 0 0 2px rgba(28, 159, 255, 0.5);
}

/* 调整大小中的元素样式 */
:deep(.vue-grid-item.resizing) {
  transition: none !important;
  opacity: 0.8;
  box-shadow: 0 0 0 2px rgba(28, 159, 255, 0.5);
}
</style>
