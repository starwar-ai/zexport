<template>
  <el-select
    v-model="model"
    :clearable="props.clearable"
    :class="props.class"
    @change="onChangeEmit"
    @visible-change="isShow"
    style="width: 200px"
  >
    <div class="header_input">
      <el-input
        v-model="searchName"
        @input="searchNameChange"
        placeholder="请输入关键字"
        clearable
      />
    </div>
    <div
      class="empty_box"
      v-show="filterList.length === 0"
    >
      无数据
    </div>
    <div
      class="option_box"
      v-show="filterList.length > 0"
    >
      <template v-if="list.length > 0">
        <el-option
          v-for="item in list"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </template>
    </div>
  </el-select>
</template>

<script setup lang="tsx">
import * as ReportApi from '@/api/system/poiReport'

const model = defineModel<string | number>()
const searchName = ref('')
const props = withDefaults(
  defineProps<{
    api?: any
    params?: any
    keyword?: string
    label?: string
    value?: string
    class?: string
    queryVal?: string
    dataList?: any
    clearable?: boolean
    defaultObj?: any
  }>(),
  {
    clearable: true
  }
)
const initList = ref([])
const list = ref([])
const filterList = ref([])

const searchNameChange = () => {
  getList(searchName.value, false)
}

const isShow = (visible) => {
  if (visible) {
    searchName.value = ''
    list.value = initList.value
  }
}

const emit = defineEmits(['changeEmit'])
const onChangeEmit = (val) => {
  emit('changeEmit', val, list.value)
}

const getList = async (val? = '', isInit) => {
  let params = {
    pageSize: 100,
    pageNo: 1,
    name: val == '' ? undefined : val
  }
  let result =
    // (await props?.api({ ...props.params, [props.keyword]: val })) ||
    await ReportApi.getVenderList({ ...params })
  let resultList = result.total ? result.list : result
  if (!resultList?.length) {
    return
  }
  filterList.value = resultList ? resultList : []
  if (isInit) {
    let initData = resultList ? resultList : []
    let codes = initData.map((item) => item[props.value])

    if (
      props.defaultObj &&
      props.defaultObj[props.value] &&
      !codes.includes(props.defaultObj[props.value])
    ) {
      initData.unshift(props.defaultObj)
    }
    initList.value = initData
  }
  if (filterList.value.length > 0) {
    list.value = filterList.value
  } else {
    list.value = initList.value
  }
}
onMounted(async () => {
  await getList('', true)
})
</script>
<style lang="scss" scoped>
.header_input {
  border-bottom: 1px solid #eee;
  padding: 5px 10px 10px;
}
/* stylelint-disable-next-line rule-empty-line-before */
.empty_box {
  color: #666;
  text-align: center;
  line-height: 50px;
}
/* stylelint-disable-next-line rule-empty-line-before */
.option_box {
  overflow: auto;
  max-height: 200px;
}
</style>
