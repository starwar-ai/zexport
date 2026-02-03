<template>
  <div :class="[prefixCls, 'flex flex-row align-items-center m-t-2px']">
    <!-- <el-input :model-value="" readonly :style="{ width: labelWidth }" /> -->
    <el-input-number
      v-model="minValue"
      :placeholder="`${labelText}最小值`"
      @update:modelValue="handleInput"
      @change="handleChange"
      @blur="handleChange"
      :controls="false"
      :style="{ width: labelWidth }"
    />
    <span>-</span>
    <el-input-number
      v-model="maxValue"
      :placeholder="`${labelText}最大值`"
      @update:modelValue="handleInput"
      @change="handleChange"
      @blur="handleChange"
      :controls="false"
      :style="{ width: labelWidth }"
    />
  </div>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { useDesign } from '@/hooks/web/useDesign'
import { ElMessage } from 'element-plus'

const prefixCls = useDesign().getPrefixCls('eplus-search-amount-range')

const props = defineProps({
  // 传入 1 个字段（如 totalAmount，标签展示依赖 formatter），或 2 个字段（min/max）
  fields: {
    type: Array as PropType<{ name: string; label?: string }[]>,
    required: true
  }
})

const emits = defineEmits(['change'])

// 外层通过 v-model 传入整体搜索模型对象
const model = defineModel<Recordable>({ required: true })

// 添加一个标志来防止在删除操作时重新创建 bundle 字段
const isDeleting = ref(false)

const isBundled = computed(() => props.fields.length === 1)
const bundleName = computed(() => (isBundled.value ? props.fields[0].name : ''))
const minField = computed(() =>
  isBundled.value ? `${bundleName.value}Min` : props.fields?.[0]?.name || ''
)
const maxField = computed(() =>
  isBundled.value ? `${bundleName.value}Max` : props.fields?.[1]?.name || ''
)

const labelText = computed(() => props.fields?.[0]?.label || '金额')
const labelWidth = computed(() => {
  const text = String(labelText.value || '')
  // 估算字符宽度：中文大约 16px，最小 80px
  const px = (text.length + 3) * 17
  return `${px}px`
})

const minValue = computed({
  get: () => model.value[minField.value],
  set: (val: any) => {
    const n = normalizeNumber(val)
    model.value[minField.value] = n
    if (isBundled.value) syncBundle()
  }
})

const maxValue = computed({
  get: () => model.value[maxField.value],
  set: (val: any) => {
    const n = normalizeNumber(val)
    model.value[maxField.value] = n
    if (isBundled.value) syncBundle()
  }
})

// 单字段模式下，将 [min,max] 写入 bundle 字段，供标签 formatter 使用
const syncBundle = () => {
  if (!isBundled.value) return
  const min = model.value[minField.value]
  const max = model.value[maxField.value]
  if (min === undefined && max === undefined) {
    delete model.value[bundleName.value]
  } else {
    model.value[bundleName.value] = [min ?? '', max ?? '']
  }
}

const normalizeNumber = (val: any) => {
  if (val === '' || val === null || val === undefined) return undefined
  const num = Number(val)
  return Number.isNaN(num) ? undefined : num
}

const handleInput = () => {
  if (isBundled.value) syncBundle()
}

const handleChange = () => {
  const min = model.value[minField.value]
  const max = model.value[maxField.value]
  if (!min && !max) return false
  if (min !== undefined && max !== undefined && Number(max) < Number(min)) {
    ElMessage.error('最大值不能小于最小值')
    model.value[maxField.value] = undefined
    if (isBundled.value) syncBundle()
    return
  }
  if (isBundled.value) syncBundle()
  emits('change')
}

// 监听 bundle 字段的变化，当 bundle 字段被删除时，清空 min 和 max 字段
watch(
  () => model.value[bundleName.value],
  (newVal) => {
    if (isBundled.value && newVal === undefined) {
      // 设置删除标志，防止重新创建 bundle 字段
      isDeleting.value = true
      // 当 bundle 字段被删除时，清空 min 和 max 字段
      model.value[minField.value] = undefined
      model.value[maxField.value] = undefined
      // 延迟重置标志，确保所有操作完成
      nextTick(() => {
        isDeleting.value = false
      })
    }
  },
  { immediate: true }
)
</script>

<style lang="scss"></style>
