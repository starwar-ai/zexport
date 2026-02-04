<template>
  <div class="left-number">
    <div class="left-number-card">{{ options.number }}</div>
  </div>
</template>

<script lang="ts" setup>
const props = defineProps<{
  // options: Object
  index: stying
}>()
const emit = defineEmits(['setOptions'])
const options = reactive({
  number: 0
})
onMounted(() => {})
</script>

<style scoped lang="scss">
.left-number {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  .left-number-card {
    font-size: 70px;
    font-weight: 700;
    color: #1c9fff;
    margin: 0;
  }
}
</style>
