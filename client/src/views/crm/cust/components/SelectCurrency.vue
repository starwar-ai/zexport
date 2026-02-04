<template>
  <div class="flex gap-2">
    <el-popover
      placement="top-start"
      :width="20"
      trigger="hover"
      v-for="(tag, index) in tagList"
      :key="tag.value"
      :disabled="tag.isDefault === 1"
    >
      <template #reference>
        <el-tag
          closable
          :disable-transitions="false"
          @close="handleClose(index)"
        >
          <span v-if="tag.isDefault === 1">{{ tag.name }}（默认） </span>
          <span v-else>{{ tag.name }}</span>
        </el-tag>
      </template>
      <template #default>
        <div style="text-align: center">
          <el-button
            link
            type="primary"
            @click="setDefault(index)"
            >设置默认
          </el-button>
        </div>
      </template>
    </el-popover>
    <eplus-dict-select
      v-model="newVal"
      v-if="dictType && visible"
      size="small"
      :dictType="dictType"
      @change-emit="handleDictChange"
    />
    <el-button
      v-else
      size="small"
      class="button-new-tag"
      @click="handleAdd"
    >
      +
    </el-button>
  </div>
</template>

<script setup lang="tsx">
import { getDictLabel } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
// const checkList = defineModel<any[]>()
const message = useMessage()
const tagList = ref([])
const visible = ref(false)
const newVal = ref('')
const props = defineProps<{
  list?: any
  dictType?: any
  modelValue: any
}>()

watchEffect(() => {
  tagList.value = props.modelValue || []
})

const handleAdd = () => {
  visible.value = true
}
const handleDictChange = (val: any) => {
  if (isValidArray(tagList.value)) {
    let valList = tagList.value.map((item) => item.value)
    if (valList.includes(val)) {
      message.warning('所选数据已存在')
      return false
    } else {
      tagList.value.push({
        name: getDictLabel(props.dictType, val),
        value: val,
        isDefault: 0
      })
    }
  } else {
    tagList.value.push({
      name: getDictLabel(props.dictType, val),
      value: val,
      isDefault: 1
    })
  }
  newVal.value = ''
  visible.value = false
}
const setDefault = (index: number) => {
  tagList.value.forEach((item) => {
    item.isDefault = 0
  })
  tagList.value[index].isDefault = 1
}
const handleClose = (index: number) => {
  if (tagList.value[index].isDefault == 1) {
    tagList.value.splice(index, 1)
    isValidArray(tagList.value) ? ((tagList.value[0] as any).isDefault = 1) : ''
  } else {
    tagList.value.splice(index, 1)
  }
}
const emit = defineEmits(['update:modelValue', 'change'])

watch(
  () => tagList.value,
  (list) => {
    console.log(11)
    emit('update:modelValue', list)
    // emit('change', list)
  },
  {
    deep: true
  }
)
</script>
<style lang="scss" scoped>
.select-item {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.tag {
  cursor: pointer;
}
</style>
