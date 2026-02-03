<template>
  <el-input
    v-model="searchVal"
    placeholder="产品编号"
    @change="updateValue"
  >
    <template #append>
      <el-popover
        placement="bottom-end"
        :width="280"
        :visible="visible"
      >
        <template #reference>
          <Icon
            icon="ep:operation"
            @click="visible = true"
          />
        </template>
        <template #default>
          <el-input
            class="search_input"
            v-model="searchText"
            type="textarea"
            placeholder="编号搜索，一行一项，最多200项"
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

<script setup lang="tsx">
const visible = ref(false)
const searchText = ref()

const searchVal = ref()

const props = defineProps<{
  modelValue: String
  nameList?: String
  model: any
}>()

watchEffect(() => {
  searchVal.value = props.modelValue
})
const emit = defineEmits(['update:modelValue', 'change', 'update'])

const handleClear = () => {
  searchText.value = ''
}

const handleSearch = () => {
  let searchTextList = searchText.value.split('\n').filter((item) => item.trim())
  emit('update:modelValue', '')
  emit('update', props.nameList, searchTextList.join())
  emit('change')
  visible.value = false
}

function updateValue() {
  emit('update:modelValue', searchVal.value)
  emit('update', props.nameList, '')
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
