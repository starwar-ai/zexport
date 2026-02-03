<template>
  <div class="flex !W-100%">
    <!-- <el-input-number
      class="flex-1 w-6/10 amountInput"
      v-model="amount"
      :disabled="props.amountDisabled"
      :controls="false"
      :precision="precision"
    /> -->
    <EplusNumInput
      class="amountInput w-6/10 flex-1"
      v-model="amount"
      :disabled="props.amountDisabled"
      :precision="precision"
    />

    <eplus-dict-select
      class="currencySelect w-4/10"
      v-model="currency"
      :dictType="DICT_TYPE.CURRENCY_TYPE"
      :clearable="false"
      :disabled="props.currencyDisabled"
      @change="handleCurrencyChange"
    />
  </div>
  <!-- <template #append>
      <eplus-dict-select
        style="width: 90px"
        v-model="currency"
        :dictType="DICT_TYPE.CURRENCY_TYPE"
        :clearable="false"
        :disabled="props.currencyDisabled"
      />
    </template> -->
  <!-- </el-input-number> -->
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDictSelect } from '@/components/EplusSelect'
import EplusNumInput from './EplusNumInput.vue'
import { moneyInputPrecision } from '@/utils/config'

const amount = defineModel<number>('amount')
const currency = defineModel<string>('currency')
const props = withDefaults(
  defineProps<{
    amountDisabled?: boolean
    currencyDisabled?: boolean
    precision?: number
  }>(),
  { amountDisabled: false, currencyDisabled: false, precision: moneyInputPrecision }
)

// 定义 emit 事件
const emit = defineEmits<{
  currencyChange: [currency: string]
}>()

// 处理币种变化
const handleCurrencyChange = (value: string) => {
  emit('currencyChange', value)
}

onMounted(() => {})
</script>
<style scoped lang="scss">
.amountInput :deep(.el-input__wrapper) {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.currencySelect :deep(.el-input__wrapper) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
</style>
