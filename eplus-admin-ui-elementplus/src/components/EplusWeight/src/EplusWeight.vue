<template>
  <div class="flex">
    <EplusNumInput
      class="weightInput"
      v-model="weight"
      :precision="2"
      :min="0"
      style="max-width: 400px"
    />
    <eplus-dict-select
      class="unitSelect"
      style="width: 80px"
      v-model="unit"
      :dictType="DICT_TYPE.SINGLE_WIEGHT_UNIT"
      :clearable="false"
      :disabled="props.unitDisabled"
    />
  </div>
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDictSelect } from '@/components/EplusSelect'

const weight = defineModel<number>('weight')
const unit = defineModel<string>('unit')

const props = withDefaults(
  defineProps<{
    weightDisabled?: boolean
    unitDisabled?: boolean
  }>(),
  { weightDisabled: false, unitDisabled: true }
)

onMounted(() => {})
</script>
<style scoped>
.weightInput :deep(.el-input__wrapper) {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.unitSelect :deep(.el-input__wrapper) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
</style>
