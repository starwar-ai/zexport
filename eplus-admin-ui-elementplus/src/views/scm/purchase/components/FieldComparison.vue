<template>
  <div v-if="sameFlag">
    <span>{{ newVal }}</span>
  </div>
  <div v-else>
    <span class="mr5 newVal">{{ newVal }}</span>
    <span class="oldVal">{{ oldVal }}</span>
  </div>
</template>

<script setup lang="tsx">
const props = defineProps<{
  item: object
  filed: string
  type?: string
}>()

let sameFlag = ref(false),
  newVal = ref(),
  oldVal = ref()

watchEffect(() => {
  let newStr = props.filed,
    oldStr = `old_${props.filed}`
  let type = typeof props.item[newStr]
  if (type === 'object') {
    if (props.item[newStr]?.amount && props.item[newStr]?.currency) {
      if (
        props.item[newStr]?.amount != props.item[oldStr]?.amount ||
        props.item[newStr]?.currency != props.item[oldStr]?.currency
      ) {
        sameFlag.value = false
        newVal = `${props.item[newStr]?.amount} ${props.item[newStr]?.currency}`
        oldVal = `${props.item[oldStr]?.amount} ${props.item[oldStr]?.currency}`
      } else {
        sameFlag.value = true
        newVal = `${props.item[newStr]?.amount} ${props.item[newStr]?.currency}`
      }
    } else {
      sameFlag.value = true
      newVal = props.item[newStr]
    }
  } else if (type === 'string' || type === 'number') {
    if (props.item[newStr] == props.item[oldStr]) {
      sameFlag.value = true
      newVal = props.item[newStr]
    } else {
      sameFlag.value = false
      newVal = props.item[newStr]
      oldVal = props.item[oldStr]
    }
  } else {
    sameFlag.value = true
    newVal = props.item[newStr]
  }
})

// const moneyCompare = () => {
//   return props.item[props.filed] - props.item[`old_${props.filed}`]
// }
</script>
<style lang="scss" scoped>
.newVal {
  color: #f7aa49;
}

.oldVal {
  color: #999;
  text-decoration: line-through;
}
</style>
