<template>
  <el-dialog
    v-model="dialogVisible"
    title="字段布局配置"
    width="900px"
    :close-on-click-modal="false"
  >
    <div class="field-customizer">
      <!-- Main Fields Section (Left Panel) -->
      <div class="field-customizer__panel">
        <div class="field-customizer__section-header">
          <el-checkbox
            v-model="selectAllMain"
            :indeterminate="isMainIndeterminate"
            @change="handleSelectAllMain"
          >
            主搜索区字段 (始终可见)
          </el-checkbox>
          <span class="field-count">
            {{ selectedMainFields.length > 0 ? `已选 ${selectedMainFields.length} / ` : '' }}{{ mainFieldsList.length }} 个字段
          </span>
        </div>
        <div class="field-customizer__list">
          <Sortable
            :list="mainFieldsList"
            item-key="id"
            :options="sortableOptionsMain"
            @end="onMainFieldsSort"
          >
            <template #item="{ element }">
              <div
                class="field-item"
                :class="{ 'is-selected': selectedMainFields.includes(element.id) }"
              >
                <el-checkbox
                  :model-value="selectedMainFields.includes(element.id)"
                  @change="toggleMainFieldSelection(element.id)"
                  @click.stop
                />
                <Icon
                  icon="ep:rank"
                  class="drag-handle"
                />
                <span class="field-label">{{ element.label }}</span>
              </div>
            </template>
          </Sortable>
          <div
            v-if="mainFieldsList.length === 0"
            class="empty-state"
          >
            至少保留一个字段
          </div>
        </div>
      </div>

      <!-- Middle Transfer Buttons -->
      <div class="field-customizer__transfer-buttons">
        <el-tooltip
          :content="canMoveSelectedToMore ? `移动 ${selectedMainFields.length} 个字段到更多面板` : '至少保留一个主字段'"
          placement="top"
        >
          <el-button
            :icon="ArrowRight"
            :disabled="!canMoveSelectedToMore"
            @click="moveSelectedToMore"
            circle
          />
        </el-tooltip>
        <el-tooltip
          :content="`移动 ${selectedMoreFields.length} 个字段到主搜索区`"
          placement="bottom"
        >
          <el-button
            :icon="ArrowLeft"
            :disabled="selectedMoreFields.length === 0"
            @click="moveSelectedToMain"
            circle
          />
        </el-tooltip>
      </div>

      <!-- More Fields Section (Right Panel) -->
      <div class="field-customizer__panel">
        <div class="field-customizer__section-header">
          <el-checkbox
            v-model="selectAllMore"
            :indeterminate="isMoreIndeterminate"
            @change="handleSelectAllMore"
          >
            更多面板字段
          </el-checkbox>
          <span class="field-count">
            {{ selectedMoreFields.length > 0 ? `已选 ${selectedMoreFields.length} / ` : '' }}{{ moreFieldsList.length }} 个字段
          </span>
        </div>
        <div class="field-customizer__list">
          <Sortable
            :list="moreFieldsList"
            item-key="id"
            :options="sortableOptionsMore"
            @end="onMoreFieldsSort"
          >
            <template #item="{ element }">
              <div
                class="field-item"
                :class="{ 'is-selected': selectedMoreFields.includes(element.id) }"
              >
                <el-checkbox
                  :model-value="selectedMoreFields.includes(element.id)"
                  @change="toggleMoreFieldSelection(element.id)"
                  @click.stop
                />
                <Icon
                  icon="ep:rank"
                  class="drag-handle"
                />
                <span class="field-label">{{ element.label }}</span>
              </div>
            </template>
          </Sortable>
          <div
            v-if="moreFieldsList.length === 0"
            class="empty-state"
          >
            暂无字段
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="field-customizer__footer">
        <el-button @click="handleReset">恢复默认</el-button>
        <div>
          <el-button @click="handleCancel">取消</el-button>
          <el-button
            type="primary"
            @click="handleSave"
          >
            保存
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Sortable } from 'sortablejs-vue3'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { Icon } from '@/components/Icon'
import type { EplusSearchFieldSchema } from '../types'
import { useFieldLayout } from './useFieldLayout'

interface FieldItem {
  id: string
  label: string
  field: EplusSearchFieldSchema
  index: number
}

const props = defineProps<{
  visible: boolean
  allFields: EplusSearchFieldSchema[]
  currentMainFields: EplusSearchFieldSchema[]
  currentMoreFields: EplusSearchFieldSchema[]
  minMainFields?: number
}>()

const emit = defineEmits(['update:visible', 'save', 'reset'])

const { getFieldId } = useFieldLayout()

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const mainFieldsList = ref<FieldItem[]>([])
const moreFieldsList = ref<FieldItem[]>([])

// Selection state
const selectedMainFields = ref<string[]>([])
const selectedMoreFields = ref<string[]>([])

// Initialize field lists
const initializeLists = () => {
  mainFieldsList.value = props.currentMainFields.map((field, index) => ({
    id: getFieldId(field, index),
    label: field.label || field.name || `字段 ${index + 1}`,
    field,
    index
  }))

  moreFieldsList.value = props.currentMoreFields.map((field, index) => ({
    id: getFieldId(field, index),
    label: field.label || field.name || `字段 ${index + 1}`,
    field,
    index
  }))

  // Clear selections when re-initializing
  selectedMainFields.value = []
  selectedMoreFields.value = []
}

watch(() => props.visible, (newVal) => {
  if (newVal) {
    initializeLists()
  }
})

const minMainFields = computed(() => props.minMainFields || 1)

// Select All computed properties
const selectAllMain = computed({
  get: () => selectedMainFields.value.length === mainFieldsList.value.length && mainFieldsList.value.length > 0,
  set: () => {}
})

const selectAllMore = computed({
  get: () => selectedMoreFields.value.length === moreFieldsList.value.length && moreFieldsList.value.length > 0,
  set: () => {}
})

const isMainIndeterminate = computed(() => {
  const count = selectedMainFields.value.length
  return count > 0 && count < mainFieldsList.value.length
})

const isMoreIndeterminate = computed(() => {
  const count = selectedMoreFields.value.length
  return count > 0 && count < moreFieldsList.value.length
})

// Multi-select handlers
const handleSelectAllMain = (checked: boolean) => {
  if (checked) {
    selectedMainFields.value = mainFieldsList.value.map(f => f.id)
  } else {
    selectedMainFields.value = []
  }
}

const handleSelectAllMore = (checked: boolean) => {
  if (checked) {
    selectedMoreFields.value = moreFieldsList.value.map(f => f.id)
  } else {
    selectedMoreFields.value = []
  }
}

const toggleMainFieldSelection = (id: string) => {
  const index = selectedMainFields.value.indexOf(id)
  if (index > -1) {
    selectedMainFields.value.splice(index, 1)
  } else {
    selectedMainFields.value.push(id)
  }
}

const toggleMoreFieldSelection = (id: string) => {
  const index = selectedMoreFields.value.indexOf(id)
  if (index > -1) {
    selectedMoreFields.value.splice(index, 1)
  } else {
    selectedMoreFields.value.push(id)
  }
}

// Check if selected items can be moved
const canMoveSelectedToMore = computed(() => {
  if (selectedMainFields.value.length === 0) return false
  const remainingCount = mainFieldsList.value.length - selectedMainFields.value.length
  return remainingCount >= minMainFields.value
})

// Batch move operations
const moveSelectedToMore = () => {
  if (!canMoveSelectedToMore.value) return

  const itemsToMove = mainFieldsList.value.filter(f => selectedMainFields.value.includes(f.id))
  mainFieldsList.value = mainFieldsList.value.filter(f => !selectedMainFields.value.includes(f.id))
  moreFieldsList.value.push(...itemsToMove)

  selectedMainFields.value = []
}

const moveSelectedToMain = () => {
  if (selectedMoreFields.value.length === 0) return

  const itemsToMove = moreFieldsList.value.filter(f => selectedMoreFields.value.includes(f.id))
  moreFieldsList.value = moreFieldsList.value.filter(f => !selectedMoreFields.value.includes(f.id))
  mainFieldsList.value.push(...itemsToMove)

  selectedMoreFields.value = []
}

// Sortable options
const sortableOptionsMain = {
  animation: 150,
  handle: '.drag-handle',
  ghostClass: 'ghost',
  chosenClass: 'chosen',
  dragClass: 'drag'
}

const sortableOptionsMore = {
  ...sortableOptionsMain
}

const onMainFieldsSort = () => {
  // Sortable handles the list reordering
}

const onMoreFieldsSort = () => {
  // Sortable handles the list reordering
}

const handleSave = () => {
  const mainFieldIds = mainFieldsList.value.map(f => f.id)
  const moreFieldIds = moreFieldsList.value.map(f => f.id)

  emit('save', mainFieldIds, moreFieldIds)
  emit('update:visible', false)
}

const handleCancel = () => {
  emit('update:visible', false)
}

const handleReset = () => {
  emit('reset')
  emit('update:visible', false)
}
</script>

<style lang="scss" scoped>
.field-customizer {
  display: flex;
  gap: 16px;
  align-items: stretch;

  &__panel {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    padding: 12px;
    min-width: 0;
  }

  &__section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    font-weight: 500;
    font-size: 14px;

    :deep(.el-checkbox__label) {
      font-weight: 500;
    }
  }

  .field-count {
    font-weight: normal;
    color: var(--el-text-color-secondary);
    font-size: 12px;
    white-space: nowrap;
  }

  &__list {
    flex: 1;
    min-height: 200px;
    max-height: 400px;
    overflow-y: auto;
  }

  &__transfer-buttons {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 12px;
    padding: 0 8px;
  }

  .field-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    margin-bottom: 4px;
    background: var(--el-fill-color-light);
    border-radius: 4px;
    transition: all 0.2s;

    &:hover {
      background: var(--el-fill-color);
    }

    &.is-selected {
      background: var(--el-color-primary-light-9);
      border: 1px solid var(--el-color-primary-light-7);
    }

    &.ghost {
      opacity: 0.4;
    }

    &.chosen {
      background: var(--el-color-primary-light-8);
      border: 1px solid var(--el-color-primary-light-6);
    }

    :deep(.el-checkbox) {
      margin-right: 0;
    }

    .drag-handle {
      cursor: grab;
      color: var(--el-text-color-secondary);
      font-size: 16px;

      &:active {
        cursor: grabbing;
      }
    }

    .field-label {
      flex: 1;
      font-size: 14px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .empty-state {
    padding: 40px 20px;
    text-align: center;
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }

  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;

    > div {
      display: flex;
      gap: 8px;
    }
  }
}
</style>
