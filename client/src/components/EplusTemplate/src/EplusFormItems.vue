<template>
  <el-row>
    <el-col
      :span="24"
      v-if="title || desc"
      class="flex"
    >
      <span class="title">{{ title }}</span>
      <span
        class="desc"
        :style="{ color: props?.descColor || '#606266' }"
        >{{ desc }}</span
      >
    </el-col>
    <template
      v-for="item in formSchemas"
      :key="item.field"
    >
      <el-col
        v-if="!item.disabled"
        :xl="item.xl || item.span || 4"
        :lg="item.lg || item.span || 6"
        :md="item.md || item.span || 8"
        :sm="item.sm || item.span || 12"
        :xs="item.xs || item.span || 24"
      >
        <el-form-item
          class="form-item"
          :label="item.label"
          :prop="item.field"
          :rules="item.rules"
          :label-width="item.labelWidth || '130px'"
        >
          <template
            #label
            v-if="item.hint"
          >
            <span>{{ item.label }}</span>
            <el-popover
              placement="right"
              :width="800"
              trigger="hover"
            >
              <template #reference>
                <Icon
                  icon="ep:question-filled"
                  class="relative top-1px ml-1px text-#409EFF"
                />
              </template>
              <template #default>
                <component :is="item.hint" />
              </template>
            </el-popover>
          </template>
          <component
            v-if="item.component"
            :is="item.component"
            v-model="formData[item.field]"
          />
          <span v-else-if="item.readOnly">{{
            item.formatter ? item.formatter(formData[item.field]) : formData[item.field] || '-'
          }}</span>
          <slot :name="item.field"></slot>
        </el-form-item>
      </el-col>
    </template>
  </el-row>
</template>

<script setup lang="tsx">
import { EplusFormSchema } from '../index'
import { EplusAuditable } from '@/types/eplus'

defineOptions({ name: 'EplusFormItems' })

const formData = inject('formData') as EplusAuditable
const props = withDefaults(
  defineProps<{
    desc?: string
    title?: string
    column?: number
    formSchemas: EplusFormSchema[]
    descColor?: any
  }>(),
  { column: 4 }
)
</script>

<style lang="scss" scoped>
:deep(.el-form-item__label) {
  padding-left: 20px !important;
  font-weight: 600;
}

.title {
  font-weight: 600;
  margin-right: 20px;
  line-height: 40px;
  margin-bottom: 5px;
}

.desc {
  font-size: 12px;
  line-height: 40px;
}

.form-item {
  margin-bottom: 16px !important;
}
</style>
