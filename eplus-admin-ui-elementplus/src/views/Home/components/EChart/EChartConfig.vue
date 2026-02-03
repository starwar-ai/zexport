<template>
  <div class="table-box">
    <div class="left-table about">
      <div
        ref="chartContainer"
        class="chart-box"
        style=""
      ></div>
    </div>

    <div class="right-config about">
      <el-form
        :model="options"
        label-width="auto"
        style="max-width: 600px"
      >
        <el-form-item
          label="时间范围"
          class="form-item"
        >
          <el-date-picker
            @change="handleDate"
            v-model="value2"
            type="daterange"
            unlink-panels
            range-separator="To"
            start-placeholder="Start date"
            end-placeholder="End date"
            :shortcuts="shortcuts"
            :size="size"
          />
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts'

const chartContainer = ref(null)
const value2 = ref('')
const props = defineProps<{
  type: string
}>()
const emit = defineEmits(['setOptions'])
const size = ref<'default' | 'large' | 'small'>('default')
const selectedDates = ref(['2024-08-19', '2024-08-20', '2024-08-21', '2024-08-22', '2024-08-23'])

const handleDate = (value) => {
  if (value && value.length === 2) {
    const startDate = new Date(value[0])
    const endDate = new Date(value[1])

    // 初始化选中日期数组
    selectedDates.value = []

    // 循环添加每一天
    let currentDate = new Date(startDate)
    while (currentDate <= endDate) {
      selectedDates.value.push(new Date(currentDate).toISOString().split('T')[0]) // 转换为日期字符串，仅日期部分
      currentDate.setDate(currentDate.getDate() + 1)
    }
    selectedDates.value = Array.from(selectedDates.value)
  }
}
const shortcuts = [
  {
    text: 'Last week',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: 'Last month',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: 'Last 3 months',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]
const options = reactive({
  color: ['#80FFA5', '#00DDFF', '#37A2FF', '#FF0087', '#FFBF00'],
  title: {
    text: 'Chart'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  legend: {},

  grid: {
    left: '10%',
    top: '10%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    // type: 'category',
    // boundaryGap: false,
    data: selectedDates.value
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: props.type,
      type: props.type,
      stack: 'Total',
      smooth: true,
      barWidth: '20%',
      lineStyle: {
        width: 0
      },
      center: ['50%', '50%'],
      radius: '50%',

      showSymbol: false,
      areaStyle: {
        opacity: 0.8,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          {
            offset: 0,
            color: 'rgb(128, 255, 165)'
          },
          {
            offset: 1,
            color: 'rgb(1, 191, 236)'
          }
        ])
      },
      emphasis: {
        focus: 'series'
      },
      data: [
        { value: 1048, name: 'Search Engine' },
        { value: 735, name: 'Direct' },
        { value: 580, name: 'Email' },
        { value: 484, name: 'Union Ads' },
        { value: 300, name: 'Video Ads' }
      ]
    }
  ]
})

const handleAddCard = (type) => {
  emit('addCard', type)
  visible.value = false
}
const selectedDatestLength = computed(() => selectedDates.value.length)
watch(
  selectedDatestLength,
  (newOption, oldOption) => {
    setOption(true)
    options.xAxis.data = selectedDates.value
    emit('setOptions', options)
  },
  {
    deep: true // 如果需要深度监听对象内部属性的变化
  }
)
emit('setOptions', options)
const setOption = (type) => {
  const chartInstance = echarts.init(chartContainer.value)
  chartInstance.setOption(options)
  if (type) {
    chartInstance.setOption({
      xAxis: {
        data: selectedDates.value
      }
    })
  }
  const handleResize = () => {
    chartInstance.resize()
  }
  window.addEventListener('resize', handleResize)
}

onMounted(() => {
  setOption()
})
</script>

<style scoped lang="scss">
.table-box {
  width: 1100px;
  display: flex;
  justify-content: space-between;

  .about {
    width: 49%;

    .chart-box {
      width: 500px;
      height: 400px;
      max-width: 600px;
    }
  }

  // .left-table {
  // }

  .right-config {
    .form-item {
      margin: 5px !important;
    }

    .tips {
      font-size: 12px;
      color: #d00;
      margin-left: 80px;
    }
  }
}
</style>
