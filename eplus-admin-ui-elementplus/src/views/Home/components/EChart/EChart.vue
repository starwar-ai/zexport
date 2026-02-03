<template>
  <div class="chart-box">
    <div
      :id="`chart${index}`"
      class="chart-box"
    ></div>
  </div>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts'

const props = defineProps<{
  // options: Object
  index: stying
}>()

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
    data: ['2024-08-19', '2024-08-20', '2024-08-21', '2024-08-22', '2024-08-23']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '',
      type: 'bar',
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

const setOption = () => {
  setTimeout(() => {
    let chartInstance = echarts.init(document.getElementById(`chart${props.index}`))
    chartInstance.setOption(options)
    chartInstance.resize()
    // window.addEventListener('resize', chartInstance.resize())
  })
}
onMounted(() => {
  setOption()
})
</script>

<style scoped lang="scss">
.chart-box {
  width: 100%;
  height: 100%;
}
</style>
