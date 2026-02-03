<template>
  <el-select
    v-model="model"
    :disabled="props.disabled"
    :clearable="props.clearable"
    :class="props.class"
    @change="modelChange"
  >
    <el-option
      v-for="item in list"
      :key="item[props.label]"
      :label="item[props.label]"
      :value="item[props.value]"
    />
  </el-select>
</template>

<script setup lang="tsx">
const model = defineModel<string | number>()

const props = withDefaults(
  defineProps<{
    api: any
    params?: any
    label?: string
    value?: string
    class?: string
    clearable?: boolean
    disabled?: boolean
    formatter?: any
  }>(),
  { clearable: true }
)

const list = ref([])
const getList = async () => {
  try {
    let result = await props.api(props.params)
    let resultList = result.total ? result.list : result
    if (props.formatter) {
      list.value = resultList ? props.formatter(resultList) : []
    } else {
      list.value = resultList ? resultList : []
    }
  } finally {
  }
}
const emit = defineEmits(['change'])
const modelChange = (val) => {
  let itemObj = list.value.find((item) => val === item[props.value])
  emit('change', itemObj)
}
onMounted(() => {
  getList()
})
</script>
