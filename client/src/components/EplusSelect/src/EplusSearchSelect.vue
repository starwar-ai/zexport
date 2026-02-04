<template>
  <el-select
    v-model="model"
    clearable
    filterable
    remote
    :remote-method="getList"
    :class="props.class"
    @change="onChangeEmit"
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
const props = defineProps<{
  api: any
  params?: any
  label?: string
  value?: string
  class?: string
  queryVal?: string
  defaultObj?: any
  keyword?: string
}>()
const list: any = ref([])
const getList = async (query: string = '') => {
  let result = await props.api({ ...props.params, [props.keyword]: query })
  let codes = result.list.map((item) => item[props.value])
  if (props.defaultObj) {
    if (codes.includes(props.defaultObj[props.value])) {
      list.value = result.list
    } else {
      list.value = [props.defaultObj, ...result.list]
    }
  } else {
    list.value = result.list
  }

  // emit('changeEmit', model.value, list.value)
}

defineExpose({ getList })

const emit = defineEmits(['changeEmit'])
const onChangeEmit = (val) => {
  emit('changeEmit', val, list.value)
}

watchEffect(async () => {
  await getList()
  // if (model.value) {
  //   emit('changeEmit', model.value, list.value)
  // }
})

onMounted(async () => {})
</script>
