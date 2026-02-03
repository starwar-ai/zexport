<template>
  <div v-if="sameFlag">
    <span>{{ newVal }}</span>
  </div>
  <div v-else>
    <span class="newVal mr5">{{ newVal }}</span>
    <span class="oldVal">{{ oldVal }}</span>
  </div>
</template>

<script setup lang="tsx">
const props = defineProps<{
  item: object
  filed: string
  type?: any
  formatter?: any
  newObj?: any
  oldObj?: any
}>()

let sameFlag = ref(false),
  newVal: any = ref(),
  oldVal: any = ref()
watchEffect(() => {
  let newStr = props.newObj[props.filed],
    oldStr = props.oldObj[props.filed]
  console.log(newStr, 'newStr', oldStr, 'oldStr')
  let type = typeof newStr
  console.log(type, 'type')
  if (type === 'object') {
    if (newStr?.currency) {
      if (
        oldStr != null &&
        (newStr?.amount != oldStr?.amount || newStr?.currency != oldStr?.currency)
      ) {
        sameFlag.value = false
        newVal = `${newStr?.amount} ${newStr?.currency}`
        oldVal = `${oldStr?.amount} ${oldStr?.currency}`
      } else {
        sameFlag.value = true
        newVal = `${newStr?.amount} ${newStr?.currency}`
      }
    } else if (newStr?.unit) {
      if (oldStr != null && (newStr?.weight != oldStr?.weight || newStr?.unit != oldStr?.unit)) {
        sameFlag.value = false
        newVal = `${newStr?.weight} ${newStr?.unit}`
        oldVal = `${oldStr?.weight} ${oldStr?.unit}`
      } else {
        sameFlag.value = true
        newVal = `${newStr?.weight} ${newStr?.unit}`
      }
    } else {
      sameFlag.value = true
      newVal = props.formatter ? props.formatter(newStr) : newStr
    }
  } else if (type === 'string' || type === 'number') {
    if (oldStr != null && newStr == oldStr) {
      sameFlag.value = true
      newVal = props.formatter ? props.formatter(newStr) : newStr
    } else {
      sameFlag.value = false
      newVal = props.formatter ? props.formatter(newStr) : newStr
      oldVal = props.formatter ? props.formatter(oldStr) : oldStr
    }
  } else {
    sameFlag.value = true
    newVal = props.formatter ? props.formatter(newStr) : newStr
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
