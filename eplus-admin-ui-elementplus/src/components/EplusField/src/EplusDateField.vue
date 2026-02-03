<template>
  <span>{{ formated }}</span>
</template>

<script setup lang="tsx">
const props = defineProps({
  format: {
    type: String as PropType<string>,
    default: 'YYYY-MM-DD'
  },
  value: {
    type: [String, Number, Date] as PropType<string | number | Date>
  }
})
import dayjs from 'dayjs'

const formated = ref()

formated.value = dayjs(props.value).format(props.format)
</script>
