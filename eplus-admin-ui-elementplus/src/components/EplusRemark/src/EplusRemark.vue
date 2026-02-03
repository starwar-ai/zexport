<template>
  <!-- <div class="page_box">
   
  </div> -->
  <el-input
    class="input_box"
    v-model="model"
    type="text"
    key="text"
    :style="{ display: type === 'text' ? 'block' : 'none' }"
    @focus="handleFocus('textarea')"
  />
  <el-input
    ref="textareaRef"
    class="input_box"
    autosize
    v-model="model"
    type="textarea"
    key="textarea"
    :style="{ display: type === 'textarea' ? 'block' : 'none' }"
    @blur="handleFocus('text')"
  />
</template>

<script setup lang="tsx">
import { defineModel, defineProps, ref, withDefaults } from 'vue'

const model = defineModel<string>()

let type = ref('text')
const textareaRef = ref()
const handleFocus = (val) => {
  type.value = val
  if (val === 'textarea') {
    textareaRef.value.focus()
  }
}

const props = withDefaults(
  defineProps<{
    amountDisabled?: boolean
    currencyDisabled?: boolean
  }>(),
  { amountDisabled: false, currencyDisabled: false }
)
</script>
<style lang="scss" scoped>
.input_box {
  width: 100%;
  position: relative;
  left: 0;
  top: 0;
  z-index: 2077;
}
</style>
