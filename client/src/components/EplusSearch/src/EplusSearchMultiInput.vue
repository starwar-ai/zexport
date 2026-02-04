<template>
  <el-input
    v-model.trim="model[selectedName]"
    :placeholder="placeholder"
    clearable
    style="width: 350px"
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

const emits = defineEmits(['select'])
const selectedName = ref(props.fields[0].name)
const model = defineModel<Recordable>({ required: true })
const computeWidth = () => {
  const selectLabel = props.fields.find((item) => item?.name === selectedName.value)
  selectLabel && (selectWidth.value = valueNum(selectLabel.label))
}
computeWidth()
const handleSelect = () => {
  props.fields.forEach((field) => {
    delete model.value[field.name]
  })
  computeWidth()
  emits('select')
}
</script>
