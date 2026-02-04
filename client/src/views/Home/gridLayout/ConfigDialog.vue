<template>
  <el-dialog
    v-model="dialogFormVisible"
    class="config-dialog"
    width="60%"
  >
    <template #header="{ titleId, titleClass }">
      <div class="my-header">
        <div v-if="titleFlag">
          <span
            :id="titleId"
            :class="titleClass"
            >{{ cardTitle }}</span
          ><el-icon
            color="#409efc"
            @click="setTitle"
            ><EditPen
          /></el-icon>
        </div>

        <el-input
          ref="inputRef"
          v-model="cardTitle"
          style="width: 240px"
          placeholder="请输入标题"
          @blur="changeTitle"
          v-else
        />
      </div>
    </template>

    <div class="config-content">
      <component
        :is="currentComponent"
        :type="currCard.type"
        :key="currCard.type"
        @set-options="setOptions"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleConfirm"
        >
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ElButton } from 'element-plus'
import { EditPen } from '@element-plus/icons-vue'

import TableConfig from '../components/FapiaoGadget/TableConfig.vue'
import NumberConfig from '../components/NumberCard/NumberConfig.vue'
import EChartConfig from '../components/EChart/EChartConfig.vue'

const dialogFormVisible = ref(false)
const currCard = ref({})
const titleFlag = ref(true)
const cardTitle = ref('卡片标题')
const inputRef = ref(null)
const props = defineProps<{}>()
const emit = defineEmits(['configCard'])
const cardOption = ref({})

// 定义当前展示的子组件 值为对子组件的引用
const currentComponent = ref(NumberConfig)

const components = {
  TableConfig,
  NumberConfig,
  EChartConfig
}

const openConfig = (card) => {
  currCard.value = card
  dialogFormVisible.value = true
  cardTitle.value = '卡片标题'
  switch (card.type) {
    case 'number':
      currentComponent.value = NumberConfig
      break
    case 'line':
      currentComponent.value = EChartConfig
      break
    case 'pie':
      currentComponent.value = EChartConfig
      break
    case 'bar':
      currentComponent.value = EChartConfig
      break
    case 'table':
      cardTitle.value = '票夹子'
      currentComponent.value = TableConfig
      break
    default:
      currentComponent.value = TableConfig
      break
  }
}

const setTitle = () => {
  titleFlag.value = !titleFlag.value
  titleFlag.value == true
    ? ''
    : nextTick(() => {
        inputRef.value.focus()
      })
}

const changeTitle = () => {
  titleFlag.value = true
}

const handleConfirm = () => {
  let option = cardOption.value
  emit('configCard', currCard.value, cardTitle.value, option)
  dialogFormVisible.value = false
}

const setOptions = (options) => {
  cardOption.value = options
}

onMounted(() => {})

defineExpose({
  openConfig
})
</script>

<style scoped lang="scss">
.config-dialog {
  .config-content {
    height: 400px;
    border-top: 1px solid #ccc;
    display: flex;
    justify-content: space-between;

    .chart-box {
      width: 49%;
      height: 90%;
      // background: #ccc;
    }

    .config {
      border-left: 1px solid #ccc;
    }
  }
}

::v-deep .el-dialog__body {
  padding: 0 20px !important;
}
</style>
