<template>
  <div
    :class="[prefixCls, 'flex flex-row flex-wrap align-items-center ']"
    v-if="fieldList && fieldList.length > 0"
  >
    <div
      v-for="field in fieldList"
      :key="field.name"
      class="m-b-5px m-r-5px"
    >
      <component
        v-if="field.subfields"
        :is="field.component"
        v-model="model"
        :fields="field.subfields"
        @change="async () => handleSubfieldChange(field)"
        @select="async () => handleSelect(field)"
        @update="handleUpdateModel"
      />
      <component
        v-if="field.name && !field.disabled"
        :is="field.component"
        v-model="model[field.name]"
        :model="model"
        :nameList="field.nameList || ''"
        :placeholder="field.label"
        @update="handleUpdateModel"
        @change="async () => handleChange(field)"
        @visible-change="async (visible) => handleVisibleChange(field, visible)"
        style="width: 150px"
        :class="field.className"
      />
    </div>

    <el-popover
      v-if="displayMoreFields.length > 0"
      :visible="popoverVisible"
      width="400"
      :hide-after="0"
      @click.stop
    >
      <template #reference>
        <el-button
          :icon="Filter"
          class="m-t-2px"
          @click="toggleMorePanel"
        />
      </template>

      <template #default>
        <MoreItemsPanel
          :fields="displayMoreFields"
          :model-value="model"
          :enable-customization="isCustomizationEnabled"
          @update:model-value="handleMorePanelUpdate"
          @search="handleMorePanelSearch"
          @cancel="handleMorePanelCancel"
          @open-customizer="handleOpenCustomizer"
        />
      </template>
    </el-popover>

    <eplus-button
      @click="reset"
      class="m-l-5px m-t-2px"
      >重置
    </eplus-button>
  </div>

  <div
    v-if="searchTags.length > 0"
    :class="[`${prefixCls}-tags`, 'flex flex-row justify-items-start items-center m-t-5px']"
  >
    <SearchTag
      v-for="tag in searchTags"
      :key="tag.name"
      :field="tag.name"
      :label="tag.label"
      :value="tag.value"
      class="m-r-5px"
    />
    <el-button
      v-if="searchTags.length > 1"
      class="!border-none"
      @click="() => handleClearTags()"
    >
      <Icon
        icon="ep:delete"
        :size="12"
      />
    </el-button>
  </div>

  <EplusSearchFieldCustomizer
    v-model:visible="customizerVisible"
    :all-fields="getAllFields()"
    :current-main-fields="displayMainFields"
    :current-more-fields="displayMoreFields"
    :min-main-fields="customizationConfig.minMainFields"
    @save="handleLayoutSave"
    @reset="handleLayoutReset"
  />
</template>

<script lang="tsx" setup>
import { defineProps } from 'vue'
import { Filter } from '@element-plus/icons-vue'
import type { EplusSearchFieldSchema, EplusSearchFormatter } from '../types'
import { EplusSearchExpose, EplusSearchSchema } from '../types'
import { useDesign } from '@/hooks/web/useDesign'
import { formatString } from '@/utils/formatString'
import { deleteProperties } from '@/utils/objectUtils'
import MoreItemsPanel from './MoreItemsPanel.vue'
import EplusSearchFieldCustomizer from './EplusSearchFieldCustomizer.vue'
import { useFieldLayout } from './useFieldLayout'

const prefixCls = useDesign().getPrefixCls('eplus-search')
const emit = defineEmits(['search', 'reset'])

const props = defineProps<EplusSearchSchema>()

// Initialize field layout management
const { loadLayout, saveLayout, clearLayout, getFieldId } = useFieldLayout()
const savedLayout = ref(loadLayout())
const customizerVisible = ref(false)

// Customization configuration
const isCustomizationEnabled = computed(() => {
  return props.customization?.enableCustomization !== false
})

const customizationConfig = computed(() => ({
  minMainFields: props.customization?.minMainFields || 1,
  maxMainFields: props.customization?.maxMainFields
}))

// Get all fields (main + more)
const getAllFields = () => {
  return [...props.fields, ...(props.moreFields || [])]
}

// Compute displayed fields based on saved layout
const displayMainFields = computed(() => {
  if (!savedLayout.value) {
    return props.fields
  }

  const allFields = getAllFields()
  return savedLayout.value.mainFields
    .map(id => allFields.find((f, i) => getFieldId(f, i) === id))
    .filter(Boolean) as EplusSearchFieldSchema[]
})

const displayMoreFields = computed(() => {
  if (!savedLayout.value) {
    return props.moreFields || []
  }

  const allFields = getAllFields()
  return savedLayout.value.moreFields
    .map(id => allFields.find((f, i) => getFieldId(f, i) === id))
    .filter(Boolean) as EplusSearchFieldSchema[]
})

const fieldList = computed(() => {
  return displayMainFields.value
})

const searchTags = reactive<{ name: string; label: string; value: string }[]>([])

const popoverVisible = ref(false)

// Toggle more panel visibility
const toggleMorePanel = () => {
  popoverVisible.value = !popoverVisible.value
}

const format = async (value: any, formatter?: EplusSearchFormatter) => {
  if (!value && value !== 0) return ''
  if (!formatter) return value
  if (typeof formatter === 'string') return formatString(formatter, value)
  if (typeof formatter === 'function') {
    if (formatter.constructor.name == 'AsyncFunction') {
      return await formatter(Array.isArray(value) ? value : [value])
    } else {
      return formatter(Array.isArray(value) ? value : [value])
    }
  }
}

const syncSearchTags = async (fields: EplusSearchFieldSchema[]) => {
  fields.forEach(async (field) => {
    if (field.subfields) {
      field.subfields.forEach(async (subfield) => {
        await syncSearchTagCore(subfield.name, subfield.label, subfield.formatter)
        if (subfield.nameList) {
          await syncSearchTagCore(subfield.nameList, subfield.label, subfield.formatter)
        }
      })
    } else {
      if (field.name) {
        await syncSearchTagCore(field.name, field.label || field.name, field.formatter)
      }
      if (field.nameList) {
        await syncSearchTagCore(field.nameList, field.label, field.formatter)
      }
    }
  })
}
const syncSearchTagCore = async (
  name: string,
  label: string | undefined,
  formatter?: EplusSearchFormatter
) => {
  //有值
  if (model[name] || model[name] === 0) {
    let index = searchTags.findIndex((tag) => tag.name === name)
    if (index === -1) {
      searchTags.push({
        name: name,
        label: label,
        value: await format(model[name], formatter)
      })
    } else {
      searchTags[index].value = await format(model[name], formatter)
    }
  } else {
    //无值
    const index = searchTags.findIndex((tag) => tag.name === name)
    if (index !== -1) {
      searchTags.splice(index, 1)
    }
  }
}

const clearFields = (fields: EplusSearchFieldSchema[]) => {
  fields.forEach((field) => {
    if (field.subfields) {
      field.subfields.forEach((subfield) => {
        delete model[subfield.name]
      })
    } else if (field.name) {
      delete model[field.name]
    }
  })
}

// Handler functions for MoreItemsPanel
const handleMorePanelUpdate = (updatedModel: Recordable) => {
  // Update model without triggering search or closing popover
  Object.assign(model, updatedModel)
}

const handleMorePanelSearch = async () => {
  await syncSearchTags(displayMoreFields.value)
  popoverVisible.value = false
  search()
}

const handleMorePanelCancel = async () => {
  clearFields(displayMoreFields.value)
  await syncSearchTags(displayMoreFields.value)
  search()
  popoverVisible.value = false
}

const handleOpenCustomizer = () => {
  customizerVisible.value = true
}

// Handler functions for Field Customizer
const handleLayoutSave = (mainFieldIds: string[], moreFieldIds: string[]) => {
  saveLayout({ mainFields: mainFieldIds, moreFields: moreFieldIds })
  savedLayout.value = loadLayout()
}

const handleLayoutReset = () => {
  clearLayout()
  savedLayout.value = null
}

const handleClearTags = () => {
  searchTags.forEach((tag) => {
    delete model[tag.name]
  })
  searchTags.length = 0
  search()
}

const handleCloseTag = (name) => {
  const index = searchTags.findIndex((item) => item.name === name)
  searchTags.splice(index, 1)
  delete model[name]
  search()
}
const SearchTag = (props) => {
  return (
    <>
      <el-tag
        closable
        onClose={() => handleCloseTag(props.field)}
      >
        <span>{props.label}:</span>
        <span>{formatTagValue(props.value)}</span>
      </el-tag>
    </>
  )
}

const formatTagValue = (value) => {
  let valList = value.split(',')
  if (valList.length > 2) {
    return `${valList[0]},${valList[1]}等${valList.length - 2}项`
  } else {
    return value
  }
}

const model = reactive<Recordable>({})

const handleSelect = async (field: EplusSearchFieldSchema) => {
  await syncSearchTags([field])
}

const handleUpdateModel = async (key, value) => {
  // await syncSearchTags([field])
  // model = { ...model, ...modelObj }
  model[key] = value
}

// const handleUpdateModel2 = async (key, value, field) => {
//   console.log()
//   await syncSearchTags([field])
//   model[key] = value
// }

const handleSubfieldChange = async (field: EplusSearchFieldSchema) => {
  await syncSearchTags([field])
  search()
}

const handleChange = async (field: EplusSearchFieldSchema) => {
  if (!field.selectMultiple) {
    await syncSearchTags([field])
    search()
  }
}
const handleVisibleChange = async (field: EplusSearchFieldSchema, visible: boolean) => {
  if (!visible && field.selectMultiple) {
    await syncSearchTags([field])
    search()
  }
}

const search = () => {
  // 遍历 model，去除字符串前后空格
  Object.keys(model).forEach((key) => {
    if (typeof model[key] === 'string') {
      model[key] = model[key].trim()
    }
    if (model[key] === undefined || model[key] === null || model[key] === '') {
      delete model[key]
    }
  })
  emit('search', model)
}

const reset = () => {
  // reactive定义的变量需遍历属性
  deleteProperties(model)
  searchTags.length = 0
  emit('reset', model)
}

if (inject('callSearchMethods')) {
  const { registerMethod } = inject('callSearchMethods')
  registerMethod('handleCloseTag', handleCloseTag)
}

defineExpose<EplusSearchExpose>({
  model
})

onBeforeUnmount(() => {
  if (inject('callSearchMethods')) {
    registerMethod('handleCloseTag', null)
  }
})
</script>

<style lang="scss">
$prefix-cls: #{$namespace}-eplus-search;

.#{$prefix-cls}-popup__item {
  padding-right: 10px;
}

.el-input__inner::placeholder {
  color: var(--el-input-text-color);
  opacity: 1;
}
</style>
