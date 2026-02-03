<template>
  <span v-if="props.list.length > 0">
    <span
      v-for="(item, index) in props.list"
      :key="item.index"
    >
      <span class="item_val"> {{ item.amount }}</span>
      <span class="item_unit">{{ item.currency }}</span>
      <span v-if="index < props.list.length - 1">;</span>
    </span>
  </span>
  <span
    v-else
    class="item_val"
    >0
  </span>
</template>

<script setup lang="ts">
defineOptions({ name: 'TotalLayout' })

const props = defineProps({
  list: {
    type: Array,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.item_val {
  font-size: 16px;
  font-weight: 600;
  padding-right: 5px;
  margin-left: 10px;
}
</style>
