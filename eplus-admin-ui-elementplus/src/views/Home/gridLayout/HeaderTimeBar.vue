<template>
  <div class="time-box">
    <div class="time-bar">
      <div
        class="time"
        v-for="item in cityTimeList"
        :key="item"
      >
        {{ item.city }}
        {{ item.date }}
        <!-- {{ item.time }} -->
        <el-icon
          @click="handleTimeClose(item.city)"
          class="time-close el-icon--right"
          ><Close
        /></el-icon>
      </div>
      <div
        @click="handleAddTime"
        class="add-time"
      >
        添加时钟<el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </div>
    </div>
  </div>
  <div
    v-if="timeTooltipFlag"
    role="tooltip"
    id="el-popover-2902"
    aria-hidden="false"
    class="el-popover el-popper global-pop ak-button-group-popover storage-popover is-arrow"
    style="
      width: 200px;
      transform-origin: center top;
      z-index: 2092;
      position: absolute;
      top: 50px;
      right: 73px;
    "
    tabindex="0"
    x-placement="bottom-start"
    ><div
      data-v-20b17930=""
      class="clock-pop"
      ><p
        data-v-20b17930=""
        class="clock-tip"
        ><span data-v-20b17930="">可选5个</span></p
      >
      <el-checkbox-group
        v-model="checkedCities"
        :min="1"
        :max="5"
      >
        <el-checkbox
          v-for="item in cities"
          :key="item.city"
          :label="item.city"
          :value="item.city"
        >
          {{ item.date }}
        </el-checkbox>
      </el-checkbox-group>
      <div
        data-v-20b17930=""
        class="ak-justify-end mamual-pop-footer"
        ><button
          type="button"
          @click="handleClose"
          class="el-button el-button--default el-button--mini"
          ><span class="text-wrap">取消</span></button
        ><button
          type="button"
          class="el-button el-button--mini el-button--primary"
          @click="handleAddCity"
          ><span class="text-wrap">确定</span></button
        ></div
      ></div
    >
  </div>
</template>

<script lang="ts" setup>
import { ArrowDown, Close } from '@element-plus/icons-vue'
import * as HomeApi from '@/api/home'

const props = defineProps<{
  refresh: any
}>()

const checkedCities = ref(['北京', '日本', '中欧'])
const cityList = ref([])
const cityTimeList = ref([])

const cities = [
  {
    city: '日本',
    date: '日本时间 UTC+09:00'
  },
  {
    city: '北京',
    date: '北京时间 UTC+08:00'
  },
  {
    city: '中欧',
    date: '中欧时间 UTC+01:00'
  },
  {
    city: '英国',
    date: '英国时间 UTC+00:00'
  },
  {
    city: '美东',
    date: '美东时间 UTC-05:00'
  },
  {
    city: '太平洋',
    date: '太平洋时间 UTC-08:00'
  }
]
const timestampToMonthDay = (timestamp) => {
  var date = new Date(timestamp)
  var month = date.getMonth() + 1 // 获取月份，月份从0开始，需要加1
  var day = date.getDate() // 获取日期
  return month + '-' + day
}

const timestampToTime = (timestamp) => {
  const date = new Date(timestamp) // 注意：JavaScript中的时间戳是以毫秒为单位的，所以需要乘以1000转换为正确的时间戳
  const hours = date.getHours().toString().padStart(2, '0') // 获取小时并补零
  const minutes = date.getMinutes().toString().padStart(2, '0') // 获取分钟并补零
  return hours + ':' + minutes // 返回时分表示
}
const state = reactive({
  differentTimeLists: [],
  timeList: [],
  timer: null
})

const timeTooltipFlag = ref(false)

const pageRoute = useRoute()

const handleAddTime = () => {
  timeTooltipFlag.value = !timeTooltipFlag.value
}

const handleTimeClose = (city) => {
  checkedCities.value = checkedCities.value.filter((item) => item !== city)
  handleAddCity()
}

const handleAddCity = async () => {
  cityList.value = []
  cities.forEach((obj1) => {
    let obj2 = checkedCities.value.find((obj) => obj === obj1.city)
    if (obj2) {
      cityList.value.push(obj1)
    }
  })
  await HomeApi.updateInternationalTime(cityList.value)
  getCityTime()
  handleClose()
}

const getCityTime = async () => {
  cityList.value = await HomeApi.getInternationalTime()
  cityList.value = cityList.value.length
    ? cityList.value
    : [
        {
          city: '北京',
          date: '北京时间 UTC+08:00'
        }
      ]
  refreshTime()
  checkedCities.value = cityList.value.map((e) => {
    return e.city
  })
}

const refreshTime = () => {
  cityTimeList.value = []
  state.timeList = [
    {
      city: '北京',
      date: `${timestampToMonthDay(Date.now())} ${timestampToTime(Date.now())}`
    },
    {
      city: '日本',
      date: `${timestampToMonthDay(Date.now() + 3600 * 1000)} ${timestampToTime(Date.now() + 3600 * 1000)}`
    },
    {
      city: '中欧',
      date: `${timestampToMonthDay(Date.now() - 3600 * 1000 * 6)} ${timestampToTime(Date.now() - 3600 * 1000 * 6)}`
    },
    {
      city: '英国',
      date: `${timestampToMonthDay(Date.now() - 3600 * 1000 * 7)} ${timestampToTime(Date.now() - 3600 * 1000 * 7)}`
    },
    {
      city: '美东',
      date: `${timestampToMonthDay(Date.now() - 3600 * 1000 * 12)} ${timestampToTime(Date.now() - 3600 * 1000 * 12)}`
    },
    {
      city: '太平洋',
      date: `${timestampToMonthDay(Date.now() - 3600 * 1000 * 15)} ${timestampToTime(Date.now() - 3600 * 1000 * 15)}`
    }
  ]

  state.timeList.forEach((obj1) => {
    let obj2 = cityList.value.find((obj) => obj.city === obj1.city)
    if (obj2) {
      cityTimeList.value.push(obj1)
    }
  })
}

state.timer = setInterval(function () {
  refreshTime()
}, 60000)

const handleClose = () => {
  timeTooltipFlag.value = false
}

watch(
  () => pageRoute,
  (newVal) => {
    // if (newVal.path == '/index') {
    //   state.timer = setInterval(function () {
    //     // refreshTime()
    //   }, 60000)
    // } else {
    //   clearInterval(state.timer)
    //   state.timer = null
    // }
  },
  { deep: true },
  { immediate: true }
)

onMounted(() => {
  getCityTime()
})
onUnmounted(() => {
  clearInterval(state.timer)
  state.timer = null
})
</script>
<style scoped lang="scss">
.time-box {
  height: 50px;
}

.time-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 50px;

  .time {
    display: flex;
    height: 28px;
    align-items: center;
    padding: 0 10px;
    background: #f2f5fa;
    border-radius: 3px;
    margin: 0 6px;
    font-size: 12px;
  }

  .time-close {
    display: none;
  }

  .time:hover {
    .time-close {
      display: block;
      cursor: pointer;
    }
  }
}

.add-time {
  display: inline-block;
  padding: 4px 6px;
  color: #2573f6;
  font-size: 14px;
  cursor: pointer;
}

.add-time:hover {
  background: #f0f5fe;
}
</style>
