<template>
  <el-row>
    <el-col :span="24">
      <div
        class="page_title"
        v-if="title"
        >{{ title }}</div
      >
    </el-col>
    <template
      v-for="item in items"
      :key="item.field"
    >
      <el-col
        :xl="item.xl || item.span || 4"
        :lg="item.lg || item.span || 6"
        :md="item.md || item.span || 8"
        :sm="item.sm || item.span || 12"
        :xs="item.xs || item.span || 24"
        v-if="!item.disabled"
        class="item_col"
      >
        <div
          class="flex"
          style="margin-bottom: 5px; align-items: flex-start"
        >
          <div
            class="item_key"
            v-if="item.label"
          >
            {{ item.label }} :
          </div>
          <div class="item_val">
            <slot
              v-if="item.slotName"
              :name="item.slotName"
            ></slot>
            <dict-tag
              v-else-if="item.dictType && data[item.field] !== null && data[item.field] !== ''"
              :type="item.dictType"
              :value="data[item.field]"
            />

            <eplus-date-field
              v-else-if="item.dateFormat"
              :value="data[item.field]"
              :format="item.dateFormat"
            />

            <span v-else-if="item.type == 'time'">
              {{ data[item.field] ? formatTimeVal(data[item.field]) : defaultData }}
            </span>
            <span v-else-if="item.type == 'date'">
              {{ data[item.field] ? formatDateVal(data[item.field]) : defaultData }}
            </span>
            <span v-else-if="item.type == 'money'">
              <EplusMoneyLabel
                :val="data[item.field]"
                :precision="item.precision"
              />
            </span>
            <span v-else-if="item.type == 'weight'">
              {{ data[item.field] ? formatWeightVal(data[item.field]) : defaultData }}
            </span>
            <span v-else-if="item.type == 'num'">
              {{ data[item.field] ? formatNumVal(data[item.field]) : 0 }}
            </span>
            <span v-else-if="item.type == 'volume'">
              {{ data[item.field] ? formatVolumeVal(data[item.field]) : 0 }}
            </span>
            <span v-else-if="item.type == 'rate'">
              {{ data[item.field] ? formatRateVal(data[item.field]) : 0 }}
            </span>
            <span v-else-if="item.type == 'img'">
              <UploadZoomPic
                :modelValue="data[item.field]"
                disabled
              />
            </span>
            <span v-else-if="item.isCompare">
              <EplusFieldComparison
                v-if="data?.[item.field] || data?.[item.field] == 0"
                :item="data"
                :filed="item.field"
                :formatter="item.formatter"
                :oldChangeField="props?.oldChangeField"
              />
            </span>
            <span v-else-if="item.formatter">
              {{ item.formatter(data[item.field]) }}
            </span>
            <span v-else-if="data[item.field] === 0">{{ data[item.field] }}</span>
            <span v-else>{{ data[item.field] ? data[item.field] : defaultData }}</span>
          </div>
        </div>
      </el-col>
    </template>
  </el-row>
</template>

<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '../index'
import { EplusAuditable } from '@/types/eplus'
import { DictTag } from '@/components/DictTag'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { EplusDateField } from '@/components/EplusField' // 日期组件
import { EplusMoneyLabel } from '@/components/EplusMoney' //金额格式化
import { UploadZoomPic } from '@/components/UploadPic' // 上传图片
import { formatterPrice, formatTime } from '@/utils/index'
import { volPrecision, VolumeUnit } from '@/utils/config'

const props = withDefaults(
  defineProps<{
    title?: string
    column?: number
    data: EplusAuditable
    items: EplusDescriptionItemSchema[]
    oldChangeField?: string
  }>(),
  { column: 4 }
)

const formatTimeVal = (val) => {
  return formatTime(val, 'yyyy-MM-dd HH:mm:ss')
}
const formatDateVal = (val) => {
  return formatTime(val, 'yyyy-MM-dd')
}

const formatNumVal = (val, decimal = 2) => {
  return formatterPrice(val, decimal)
    .toString()
    .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')
}

const formatWeightVal = (val) => {
  return formatNumVal(val?.weight) + ' ' + val?.unit
}

const formatVolumeVal = (val) => {
  return formatNumVal(val / 1000000, volPrecision) + ' ' + VolumeUnit
}

const formatRateVal = (val) => {
  return Number(val * 100).toFixed(2) + ' ' + '%'
}

const defaultData = '-'
</script>
<style lang="scss" scoped>
.page_title {
  font-size: 14px;
  font-weight: 600;
  color: #000000;
  height: 20px;
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.page_title::before {
  content: '';
  display: inline-block;
  width: 5px;
  height: 15px;
  border-radius: 3px;
  background-color: var(--el-color-primary);
  margin-right: 5px;
}

.item_key {
  width: 130px;
  text-align: right;
  font-weight: 600;
  line-height: 23px;
  font-size: 14px;
  margin-right: 16px;
  color: #000000;
}

.item_val {
  flex: 1;
  font-weight: 400;
  line-height: 23px;
  font-size: 14px;
  // color: var(--el-text-color-regular);
  color: #1e1e1e;
  text-align: left;
  overflow-y: auto;
  word-wrap: break-word;
}
</style>
