<template>
  <div
    class="more-items-panel"
    @click.stop
  >
    <div class="more-items-panel__header">
      <span class="more-items-panel__title">筛选条件</span>
      <el-button
        v-if="enableCustomization"
        link
        :icon="Setting"
        @click="openCustomizer"
      >
        配置
      </el-button>
    </div>

    <el-descriptions
      column="1"
      class="more-items-panel__content"
    >
      <el-descriptions-item
        v-for="(field, index) in fields"
        :key="getFieldKey(field, index)"
        :label="field.label"
        class="more-items-panel__item"
      >
        <component
          v-if="field.subfields"
          :is="field.component"
          :model-value="modelValue"
          :fields="field.subfields"
          @update="handleUpdate"
          @update:modelValue="handleUpdateModelValue"
        />
        <component
          v-else-if="field.name && !field.disabled"
          :is="field.component"
          :model-value="modelValue[field.name]"
          :model="modelValue"
          :nameList="field.nameList || ''"
          :placeholder="field.label"
          :class="field.className"
          @update="handleUpdate"
          @update:modelValue="(value) => handleUpdateField(field.name, value)"
          style="width: 150px"
        />
      </el-descriptions-item>
    </el-descriptions>

    <div class="more-items-panel__footer">
      <eplus-button
        @click="handleSearch"
        is-primary
      >
        检索
      </eplus-button>
      <eplus-button @click="handleCancel">取消</eplus-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Setting } from '@element-plus/icons-vue'
import type { EplusSearchFieldSchema } from '../types'

const props = defineProps<{
  fields: EplusSearchFieldSchema[]
  modelValue: Recordable
  enableCustomization?: boolean
}>()

const emit = defineEmits([
  'search',
  'cancel',
  'openCustomizer',
  'update:modelValue'
])

const getFieldKey = (field: EplusSearchFieldSchema, index: number) => {
  return field.name || field.label || `field-${index}`
}

const handleSearch = () => {
  emit('search')
}

const handleCancel = () => {
  emit('cancel')
}

const openCustomizer = () => {
  emit('openCustomizer')
}

// Handle the @update event from components (like in main search area)
const handleUpdate = (key: string, value: any) => {
  const updatedModel = { ...props.modelValue, [key]: value }
  emit('update:modelValue', updatedModel)
}

// Handle standard v-model update event for subfields
const handleUpdateModelValue = (value: any) => {
  emit('update:modelValue', value)
}

// Handle standard v-model update event for single field
const handleUpdateField = (fieldName: string, value: any) => {
  const updatedModel = { ...props.modelValue, [fieldName]: value }
  emit('update:modelValue', updatedModel)
}
</script>

<style lang="scss" scoped>
.more-items-panel {
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  &__title {
    font-weight: 500;
    font-size: 14px;
  }

  &__content {
    margin-bottom: 16px;
  }

  &__item {
    padding-right: 10px;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}
</style>
