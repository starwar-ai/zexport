<template>
  <div :class="[prefixCls, 'flex flex-row align-items-center m-t-2px']">
    <el-select
      v-model="selectedName"
      @change="handleSelect"
      class="w-120px"
    >
      <el-option
        v-for="field in fields"
        :key="field.name"
        :label="field.label"
        :value="field.name"
      />
    </el-select>
    <el-date-picker
      v-model="model[selectedName]"
      clearable
      :class="`${prefixCls}__datepicker`"
      valueFormat="YYYY-MM-DD HH:mm:ss"
      type="daterange"
      @change="handleChange"
      :placeholder="placeholder"
      :shortcuts="shortcuts"
      :default-time="defaultTime"
    />
  </div>
</template>
<script lang="tsx" setup>
import { PropType } from 'vue'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('eplus-search-multi-date-picker')
const placeholder = computed(
  () =>
    props.fields.find((item) => item.name === selectedName.value)?.placeholder || '请输入搜索内容'
)

const props = defineProps({
  fields: {
    type: Array as PropType<{ name: string; label: string; placeholder?: string }[]>,
    required: true
  }
})

const emits = defineEmits(['select', 'change'])
const selectedName = ref(props.fields[0].name)

const model = defineModel<Recordable>({ required: true })
const defaultTime = ref<[Date, Date]>([
  new Date(2000, 1, 1, 0, 0, 0),
  new Date(2000, 2, 1, 23, 59, 59)
])
const shortcuts = [
  {
    text: '本年',
    value: () => {
      const year = new Date().getFullYear()
      const end = new Date()
      const start = new Date(year, 0, 1)
      return [start, end]
    }
  },
  {
    text: '本月',
    value: () => {
      const end = new Date()
      const start = new Date(end.getFullYear(), end.getMonth(), 1).getTime()
      return [start, end]
    }
  },
  {
    text: '本周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - (start.getDay() || 6) + 1) // 如果周日则减6天
      start.setHours(0, 0, 0, 0)
      return [start, end]
    }
  }
]

const handleChange = () => {
  emits('change')
}
const handleSelect = () => {
  props.fields.forEach((field) => {
    delete model.value[field.name]
  })

  emits('select')
}
</script>
<style lang="scss">
$prefix-cls: #{$namespace}-eplus-search-multi-date-picker;

.#{$prefix-cls}__datepicker {
  --el-date-editor-daterange-width: 200px;

  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.#{$prefix-cls} .el-input__wrapper {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
</style>
