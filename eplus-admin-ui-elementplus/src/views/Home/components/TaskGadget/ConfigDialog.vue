<template>
  <div></div>
</template>

<script lang="ts" setup>
const props = defineProps<{}>()
const emit = defineEmits(['setOptions'])
const options = reactive({})

watch(options, (newOption, oldOption) => {}, {
  deep: true // 如果需要深度监听对象内部属性的变化
})

onMounted(() => {})
</script>

<style scoped lang="scss"></style>
