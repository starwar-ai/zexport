<template>
  <div :class="props.showDate ? 'flex' : 'w-full'">
    <el-select
      v-model="model"
      @change="modelChange"
      :class="props.showDate ? '' : 'w-full'"
      :style="props.showDate ? 'width: 60%' : 'width: 100%'"
    >
      <el-option
        v-for="item in list"
        :key="item[props.label]"
        :label="item[props.label]"
        :value="item[props.value]"
      />
    </el-select>
    <el-date-picker
      v-if="props.showDate"
      v-model="dateValue"
      valueFormat="YYYY"
      type="year"
      placeholder="年份"
      style="width: 40%"
      @change="handleChangeDate"
      :default-value="new Date()"
      :clearable="false"
    />
  </div>
</template>

<script setup lang="tsx">
const model = defineModel<string | number>()
import { getNowDateTime } from '@/utils/formatTime'

const props = withDefaults(
  defineProps<{
    api?: any
    label?: string
    value?: string
    params?: any
    keyword: string
    showDate?: boolean
  }>(),
  {
    showDate: false
  }
)
const list = ref<any[]>([])
const searchData = ref({})
const getList = async () => {
  try {
    let result = []
    result = await props.api({ ...searchData.value, ...props.params })
    let resultList
    if (result.total || result.total == 0) {
      resultList = result.list ? result.list : []
    } else {
      resultList = result
    }
    list.value = resultList ? resultList : []
  } finally {
  }
}

const emit = defineEmits(['change'])
const modelChange = (val) => {
  let itemObj = list.value.find((item) => val === item[props.value])
  emit('change', itemObj)
}

const dateValue = ref(getNowDateTime())
const handleChangeDate = () => {
  model.value = ''
  let params: any = {
    [props.keyword]: [`${dateValue.value}-01-01 00:00:00`, `${dateValue.value}-12-31 23:59:59`]
  }
  searchData.value = { ...searchData.value, ...params }
  getList()
}

const setSearchData = (val) => {
  searchData.value = { ...searchData.value, ...val }
  getList()
}

const setData = (data) => {
  list.value = data
}

// onMounted(() => {
//   getList()
// })

defineExpose({ setSearchData, setData })
</script>
