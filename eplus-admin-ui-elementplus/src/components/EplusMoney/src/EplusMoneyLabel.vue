<template>
  <span
    v-for="(item, index) in amountList"
    :key="index"
  >
    <span v-if="Number(item.amount) != 0 && symbolFlag">
      <span
        v-if="item.amount < 0"
        style="color: red"
      >
        {{ item.amountSymbol }} {{ item.amountVal }}
      </span>
      <span v-else> {{ item.amountSymbol }} {{ item.amountVal }} </span>
    </span>
    <span v-else> {{ item.amountVal }} </span>
    <span v-if="index < amountList.length - 1">,</span>
  </span>
</template>

<script setup lang="tsx">
import { ref, watchEffect } from 'vue'
import { formatterPrice } from '@/utils/index'
import { isValidArray } from '@/utils/is'
import { moneyTotalPrecision } from '@/utils/config'

const currencyConfig = [
  { key: 'USD', symbol: '$' },
  { key: 'EUR', symbol: '€' },
  { key: 'CZK', symbol: 'Kč' },
  { key: 'GBP', symbol: '£' },
  { key: 'CAD', symbol: 'C$' },
  { key: 'AUD', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'NZD', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'JPY', symbol: 'Ұ' },
  { key: 'RMB', symbol: '￥' },
  { key: 'CNY', symbol: '￥' },
  { key: 'HKD', symbol: 'HK$' },
  { key: 'MOP', symbol: 'MOP$' },
  { key: 'TWD', symbol: 'NT$' }, // 注意：实际为新台币
  { key: 'INR', symbol: '₹' },
  { key: 'KRW', symbol: '₩' },
  { key: 'THB', symbol: '฿' },
  { key: 'SGD', symbol: 'S$' },
  { key: 'VND', symbol: '₫' },
  { key: 'MYR', symbol: 'RM' },
  { key: 'PHP', symbol: '₱' },
  { key: 'IDR', symbol: 'Rp' },
  { key: 'MXN', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'BRL', symbol: 'R$' },
  { key: 'ZAR', symbol: 'R' }, // 注意：可能与巴西雷亚尔等混淆，需结合上下文
  { key: 'ARS', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'COP', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'PEN', symbol: 'S/' }, // 注意：秘鲁索尔的符号后通常跟一个数值
  { key: 'PLN', symbol: 'zł' },
  { key: 'RUB', symbol: '₽' }
]
// const props = defineProps<{
//   val: any
//   symbolFlag?: boolean
// }>()

const props = withDefaults(
  defineProps<{
    val: any
    symbolFlag?: boolean
    precision?: number
    keepTrailingZeros?: boolean
  }>(),
  { symbolFlag: true, precision: moneyTotalPrecision, keepTrailingZeros: false }
)
const amountList = ref([])
const formatterVal = (val) => {
  let obj = { amountSymbol: '', amount: 0, amountVal: '' }
  if (val?.currency) {
    currencyConfig.forEach((item) => {
      if (item.key === val.currency) {
        obj.amountSymbol = item.symbol
      }
    })
  } else {
    obj.amountSymbol = ''
  }
  if (val?.amount) {
    obj.amount = val.amount
    obj.amountVal = formatterPrice(val.amount, props.precision, props.keepTrailingZeros)
      .toString()
      .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')
  } else {
    obj.amountVal = '0'
  }
  amountList.value.push(obj)
}
watchEffect(() => {
  amountList.value = []
  if (isValidArray(props.val)) {
    props.val.forEach((item) => {
      formatterVal(item)
    })
  } else {
    formatterVal(props.val)
  }
})
</script>
