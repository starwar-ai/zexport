<template>
  <!-- <div v-if="isValidArray(list)">
    <div
      v-for="item in list"
      :key="item"
      class="li"
    >
      <span>{{ item }}</span>
    </div>
  </div> -->
  <div v-if="isValidArray(list) && list.length < 2">
    <div
      v-for="(item, index) in list"
      :key="item"
      class="li"
    >
      <span v-if="index < 3">{{ item }}</span>
    </div>
  </div>
  <div v-else-if="isValidArray(list) && list.length > 1">
    <el-popover
      placement="right"
      :width="200"
      trigger="hover"
    >
      <template #reference>
        <div> {{ list[0] }} </div>
      </template>
      <div
        v-for="item in list"
        :key="item"
        class="li"
      >
        {{ item }}
      </div>
    </el-popover>
  </div>
  <div v-else>-</div>
</template>
<script setup lang="tsx">
import { isValidArray } from '@/utils/is'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'

const props = withDefaults(
  defineProps<{
    info: any
    isAll?: boolean
    detailList?: any
    currency?: string
  }>(),
  { isAll: false }
)

const list = ref([])

watchEffect(() => {
  list.value = []
  if (props.info) {
    props.info.forEach((item: any) => {
      let typeVal = getDictLabel(DICT_TYPE.DIFFERENCE_TYPE, item.differenceType)
      if (item.remark) {
        list.value.push(`${typeVal}(${item.differenceAmount})/备注：${item.remark}`)
      } else {
        list.value.push(`${typeVal}(${item.differenceAmount})`)
      }
    })
  } else {
    list.value = []
  }
})
</script>
<style lang="scss" scoped>
.li {
  line-height: 30px;
}
</style>
