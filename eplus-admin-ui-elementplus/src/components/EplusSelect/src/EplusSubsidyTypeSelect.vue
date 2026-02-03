<template>
  <el-select
    v-model="model"
    clearable
    class="!w-60%"
  >
    <el-option
      v-for="item in list"
      :key="item[props.label]"
      :label="item[props.label]"
      :value="item[props.value]"
    />
  </el-select>
  <span
    v-show="model && props.label == 'subsidyType'"
    class="ml5px"
  >
    <EplusMoneyLabel :val="{ amount: model, currency: 'RMB' }" />
  </span>
</template>

<script setup lang="tsx">
const model = defineModel<string | number>()

const props = defineProps<{
  api: any
  label?: string
  value?: string
  class?: string
}>()
const list = ref([])
const getList = async () => {
  try {
    list.value = await props.api()
  } finally {
  }
}
onMounted(() => {
  getList()
})
</script>
