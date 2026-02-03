<template>
  <el-select
    v-model="model"
    :clearable="props.clearable"
    :class="props.class"
    @change="onChangeEmit"
    @visible-change="isShow"
    :fit-input-width="true"
    :multiple="props.multiple"
    :disabled="props.disabled"
    :placeholder="props.placeholder ? props.placeholder : '请选择'"
  >
    <div class="header_input">
      <el-input
        v-model.trim="searchName"
        ref="inputRef"
        @input="searchNameChange"
        placeholder="请输入关键字"
        clearable
      />
    </div>
    <div
      class="empty_box"
      v-show="filterList?.length === 0"
    >
      无数据
    </div>
    <div
      class="option_box"
      v-show="filterList?.length > 0"
    >
      <template v-if="list?.length > 0">
        <el-option
          v-for="item in list"
          :key="item[props.value]"
          :label="props.formatLabel ? props.formatLabel(item) : item[props.label]"
          :value="item[props.value]"
        />
      </template>
    </div>
  </el-select>
</template>

<script setup lang="tsx">
import { isValidArray } from '@/utils/is'
import { debounce } from 'lodash-es'

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
    multiple?: boolean
    disabled?: boolean
    placeholder: string
    formatLabel?: any
  }>(),
  {
    clearable: true,
    multiple: false
  }
)
const initList = ref([])
const list = ref([])
const filterList = ref([])
const inputRef = ref()

const searchNameChange = debounce(() => {
  getList(searchName.value, false)
}, 500)

const isShow = (visible) => {
  if (visible) {
    inputRef.value.focus()
    searchName.value = ''
    if (model.value) {
      filterList.value = list.value
    } else {
      list.value = initList.value
      filterList.value = list.value
    }
  }
}

const emit = defineEmits(['changeEmit'])
const onChangeEmit = (val) => {
  emit('changeEmit', val, list.value)
}

// const getInitList = async (val? = '') => {
//   let result = await props.api({ ...props.params, [props.keyword]: val })
//   let resultList = result.total ? result.list : result
//   initList.value = resultList ? resultList : []
// }
// const formData = inject('formData')
const defaultFormatterList = (codes, val, initData) => {
  let result
  if (isValidArray(val) && props.defaultObj) {
    let defaultCodes = val.map((item) => {
      return item[props.value]
    })
    codes.map((item) => {
      if (!defaultCodes.includes(item)) {
        result = [...val, ...initData]
      }
    })
  } else if (!isValidArray(val) && props.defaultObj) {
    if (props.defaultObj[props.value] && !codes.includes(props.defaultObj[props.value])) {
      result = [props.defaultObj, ...initData]
    } else {
      result = initData
    }
  } else {
    result = initData
  }
  return result
}
const getList = async (val = '', isInit) => {
  let result = await props.api({ ...props.params, [props.keyword]: val })
  let resultList = result?.total ? result.list : result
  if (!resultList?.length) {
    filterList.value = []
    return
  }
  filterList.value = resultList ? resultList : []
  if (isInit) {
    let initData = resultList ? resultList : []
    let codes = initData.map((item) => item[props.value])
    initList.value = defaultFormatterList(codes, props?.defaultObj, initData)
    list.value = initList.value
    filterList.value = initList.value
  }
  if (filterList.value?.length > 0) {
    // let filterSelf = cloneDeep(filterList.value)
    // list.value = filterSelf.filter((item) => {
    //   return item?.code !== formData?.value?.code
    // })
    list.value = filterList.value
  } else {
    list.value = initList.value
  }
}
defineExpose({ getList })
onMounted(async () => {
  if (props) {
    await getList(searchName.value, true)
    if (model.value) {
      emit('changeEmit', model.value, list.value)
    }
  }
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
