<template>
  <el-input
    v-model="searchVal"
    :placeholder="placeholder"
    clearable
    style="width: 350px"
    @change="updateValue"
  >
    <template #prepend>
      <el-select
        v-model="selectedName"
        @change="handleSelect"
        :style="{ width: selectWidth }"
      >
        <el-option
          v-for="field in fields"
          :key="field.name"
          :label="field.label"
          :value="field.name"
        />
      </el-select>
    </template>
    <template #append>
      <el-popover
        placement="bottom-end"
        :width="280"
        :visible="visible"
      >
        <template #reference>
          <el-button @click="visible = true">
            <Icon icon="ep:operation" />
          </el-button>
        </template>
        <template #default>
          <el-input
            class="search_input"
            v-model="searchText"
            type="textarea"
            placeholder="精确搜索，一行一项，最多200项"
          />
          <div class="btn_box">
            <el-button @click="handleClear">清空</el-button>
            <div>
              <el-button @click="visible = false">关闭</el-button>
              <el-button
                @click="handleSearch"
                type="primary"
                plain
              >
                搜索
              </el-button>
            </div>
          </div>
        </template>
      </el-popover>
    </template>
  </el-input>
</template>
<script lang="tsx" setup>
import { PropType } from 'vue'

const selectWidth = ref('')
const valueNum = (val) => {
  if (val) {
    let valueLength = val.split('').length
    if (valueLength < 4) {
      return '100px'
    } else {
      return valueLength * 25 + 'px'
    }
  }
}
const placeholder = computed(
  () =>
    props.fields.find((item) => item.name === selectedName.value)?.placeholder ||
    props?.placeholder ||
    '请输入搜索内容'
)

const props = defineProps({
  fields: {
    type: Array as PropType<{ name: string; label: string; placeholder?: string }[]>,
    required: true
  },
  placeholder: String
})
const model = defineModel<Recordable>({ required: true })
const visible = ref(false)
const searchText = ref()
let selectedName = ref(props.fields[0].name)
let selectedNameList = ref(props.fields[0]?.nameList)
const searchVal = ref('')
watchEffect(() => {
  if (!model.value[selectedName.value]) {
    searchVal.value = ''
  }
  if (!model.value[selectedNameList.value]) {
    searchText.value = ''
  }
})

const emit = defineEmits(['select', 'update:modelValue', 'change', 'update'])

const computeWidth = () => {
  const selectLabel = props.fields.find((item) => item?.name === selectedName.value)
  selectLabel && (selectWidth.value = valueNum(selectLabel.label))
}
computeWidth()
const handleSelect = () => {
  props.fields.forEach((field) => {
    delete model.value[field.name]
    if (field.nameList) {
      delete model.value[field.nameList]
    }
    if (field.name === selectedName.value && field.nameList) {
      selectedNameList.value = field.nameList
    }
  })

  computeWidth()
  emit('select')
}

const handleClear = () => {
  searchText.value = ''
  emit('update', selectedNameList.value, '')
}

const handleSearch = () => {
  let searchTextList = searchText.value.split('\n').filter((item) => item.trim())
  searchVal.value = ''
  emit('update', selectedName.value, '')
  emit('update', selectedNameList.value, `${searchTextList.join()}`)
  emit('change')
  visible.value = false
}
function updateValue() {
  handleClear()
  visible.value = false
  emit('update', selectedName.value, searchVal.value ? `${searchVal.value}` : '')
  emit('change')
}
</script>
<style lang="scss" scoped>
.search_input {
  width: 100%;
  height: 300px;
}

.search_input :deep(.el-textarea__inner) {
  width: 100%;
  height: 100%;
  box-shadow: none;
}

.btn_box {
  margin-top: 10px;
  border-top: 1px solid #eee;
  padding-top: 10px;
  display: flex;
  justify-content: space-between;
}
</style>
