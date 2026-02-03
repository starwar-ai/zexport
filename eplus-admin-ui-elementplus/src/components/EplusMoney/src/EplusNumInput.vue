<template>
  <el-input
    v-model="displayValue"
    @input="handleInput"
    @blur="handleBlur"
  />
</template>

<script setup>
import { formatterPrice } from '@/utils'
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: [Number, String],
    default: ''
  },
  precision: {
    type: Number,
    default: 3
  },
  min: {
    type: Number,
    default: -Infinity
  },
  max: {
    type: Number,
    default: Infinity
  }
})

const emit = defineEmits(['update:modelValue'])

// 显示值（可能包含中间输入状态）
const displayValue = ref('')
// 存储最后有效的数值
const lastValidValue = ref(props.modelValue)

// 统一的小数位数控制函数
const formatWithPrecision = (value) => {
  if (!value && value !== 0) return ''

  const str = value.toString()
  if (str.includes('.')) {
    const parts = str.split('.')
    if (parts[1] && parts[1].length > props.precision) {
      // return parts[0] + '.' + parts[1].substring(0, props.precision)
      return formatterPrice(value, props.precision)
    }
  }
  return str
}

// 初始化显示值，应用小数位数控制
displayValue.value = formatWithPrecision(props.modelValue ?? '')

// 处理输入事件
const handleInput = (value) => {
  // 1. 过滤非法字符（只允许数字、负号和小数点）
  let filtered = value.replace(/[^-0-9.]/g, '')

  // 2. 处理多个小数点的情况（只保留第一个）
  const decimalCount = filtered.split('.').length - 1
  if (decimalCount > 1) {
    const parts = filtered.split('.')
    filtered = parts[0] + '.' + parts.slice(1).join('')
  }

  // 3. 处理负号位置（只能出现在开头）
  const minusCount = filtered.split('-').length - 1
  if (minusCount > 1 || (minusCount === 1 && !filtered.startsWith('-'))) {
    filtered = filtered.replace(/-/g, '')
    if (minusCount > 0) {
      filtered = '-' + filtered
    }
  }

  // 4. 处理只有负号没有数字的情况
  if (filtered === '-') {
    displayValue.value = '-'
    return
  }

  // 5. 处理以小数点开头的情况（自动补零）
  if (filtered.startsWith('.')) {
    filtered = '0' + filtered
  }

  // 6. 控制小数位数并更新显示值
  filtered = formatWithPrecision(filtered)
  displayValue.value = filtered
  // 7. 如果是有效数字，更新模型值
  if (/^-?\d*\.?\d*$/.test(filtered) && filtered !== '' && filtered !== '-' && filtered !== '.') {
    lastValidValue.value = filtered.replace(/^(-?)0+(\d)/, '$1$2')
    emit('update:modelValue', lastValidValue.value)
  }
}

// 处理失去焦点事件（格式化最终值）
const handleBlur = () => {
  // 空值处理
  if (displayValue.value === '' || displayValue.value === '-') {
    displayValue.value = ''
    lastValidValue.value = ''
    emit('update:modelValue', '')
    return
  }

  // 转换为数字
  const num = parseFloat(displayValue.value)

  if (!isNaN(num)) {
    // 应用范围限制
    let clampedNum = num
    if (num < props.min) {
      clampedNum = props.min
    } else if (num > props.max) {
      clampedNum = props.max
    }

    // 如果值被修正，需要更新显示
    // if (clampedNum !== num) {
    //   displayValue.value = clampedNum.toString()
    // }
    // 应用小数位数控制
    const formattedValue = formatWithPrecision(clampedNum.toString())
    displayValue.value = formattedValue
    // 更新最后有效值和模型值
    lastValidValue.value = formattedValue
    emit('update:modelValue', parseFloat(formattedValue))
  } else {
    // 恢复到最后有效值
    displayValue.value = lastValidValue.value
  }
}

// 监听外部modelValue变化
watch(
  () => props.modelValue,
  (newVal) => {
    // 转换为字符串进行比较，避免数字和字符串比较的问题
    const newValStr = (newVal ?? '').toString()
    const lastValidStr = (lastValidValue.value ?? '').toString()
    const currentDisplayStr = displayValue.value

    // 检查是否是数值相等但格式不同的情况（例如：34 vs "34."）
    const newNum = parseFloat(newVal)
    const displayNum = parseFloat(currentDisplayStr)
    const isEquivalentNumber = !isNaN(newNum) && !isNaN(displayNum) && newNum === displayNum

    // 如果新值与最后有效值不同，且不是用户正在输入的中间状态
    // 特殊处理：如果数值相等但用户正在输入小数点，不要覆盖
    if (
      newValStr !== lastValidStr &&
      newValStr !== currentDisplayStr &&
      !(isEquivalentNumber && currentDisplayStr.endsWith('.'))
    ) {
      const num = parseFloat(newVal)
      if (!isNaN(num)) {
        // 应用范围限制
        let clampedNum = num
        if (num < props.min) {
          clampedNum = props.min
        } else if (num > props.max) {
          clampedNum = props.max
        }
        // 应用小数位数控制
        const formattedValue = formatWithPrecision(clampedNum.toString())
        displayValue.value = formattedValue
        lastValidValue.value = formattedValue
      } else {
        displayValue.value = ''
        lastValidValue.value = ''
      }
    }
  }
)
</script>
