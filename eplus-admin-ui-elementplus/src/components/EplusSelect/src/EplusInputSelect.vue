<template>
  <el-select
    v-model="model"
    :clearable="props.clearable"
    :multiple="props.multiple"
    :class="props.class"
    @change="onChangeEmit"
    @visible-change="isShow"
    class="w-full"
  >
    <div class="header_input">
      <el-input
        v-model.trim="searchName"
        @input="searchNameChange"
        placeholder="请输入关键字"
        clearable
        ref="inputRef"
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
      <el-option
        v-for="item in list"
        :key="item[props.value]"
        :label="props.formatLabel ? props.formatLabel(item) : item[props.label]"
        :value="item[props.value]"
      />
    </div>
  </el-select>
</template>

<script setup lang="tsx">
const model = defineModel<string | number>()
const searchName = ref('')
import { cloneDeep, debounce } from 'lodash-es'

const props = withDefaults(
  defineProps<{
    label?: string
    value?: string
    class?: string
    queryVal?: string
    dataList: any
    multiple?: boolean
    formatLabel?: any
    clearable?: boolean
    lowerCaseFlag?: boolean
  }>(),
  { clearable: true }
)
const initList = ref([])
const list = ref([])
const filterList = ref([])
const inputRef = ref(null)
onMounted(() => {})
watchEffect(() => {
  let datalist = isRef(props.dataList) ? props.dataList.value : props.dataList
  initList.value = cloneDeep(datalist)
  list.value = datalist
  filterList.value = datalist
})

const searchNameChange = debounce(() => {
  filterList.value = props.dataList.filter((item) => {
    let fromatLable = props.formatLabel ? props.formatLabel(item) : item[props.label]
    if (props.lowerCaseFlag) {
      return fromatLable.trim().toLowerCase().includes(searchName.value.trim().toLowerCase())
    } else {
      return fromatLable.trim().includes(searchName.value.trim())
    }
  })
  list.value = filterList.value.length > 0 ? filterList.value : initList.value
}, 500)

const isShow = (visible) => {
  inputRef.value.focus()
  if (visible) {
    searchName.value = ''
    let datalist = isRef(props.dataList) ? props.dataList.value : props.dataList
    filterList.value = datalist
    list.value = datalist
  }
}

const emit = defineEmits(['changeEmit'])
const onChangeEmit = (val) => {
  emit('changeEmit', val, list.value)
}
</script>
<style lang="scss" scoped>
.header_input {
  border-bottom: 1px solid #eee;
  padding: 5px 10px 10px;
}

.empty_box {
  color: #666;
  text-align: center;
  line-height: 50px;
}

.option_box {
  overflow: auto;
  max-height: 200px;
}
</style>
