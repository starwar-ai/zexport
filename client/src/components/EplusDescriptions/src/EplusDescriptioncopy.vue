<template>
  <el-descriptions
    :title="title"
    :column="column || 3"
    size="small"
  >
    <el-row>
      <el-descriptions-item
        v-for="(item, $index) in items"
        :key="$index"
        :label="item.label"
      >
        <dict-tag
          v-if="item.dictType"
          :type="item.dictType"
          :value="data[item.field]"
        />

        <eplus-date-field
          v-else-if="item.dateFormat"
          :value="data[item.field]"
          :format="item.dateFormat"
        />

        <span v-else>{{ data[item.field] ?? defaultData }}</span>
      </el-descriptions-item>
    </el-row>
  </el-descriptions>
</template>

<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '../index'
import { EplusAuditable } from '@/types/eplus'
import { DictTag } from '@/components/DictTag'
import { EplusDateField } from '@/components/EplusField' // 日期组件

const props = withDefaults(
  defineProps<{
    title: string
    column: number
    data: EplusAuditable
    items: EplusDescriptionItemSchema[]
  }>(),
  { column: 4 }
)
const defaultData = '-'
</script>
