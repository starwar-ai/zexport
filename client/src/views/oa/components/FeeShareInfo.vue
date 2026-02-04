<template>
  <div v-if="isValidArray(list) && isAll">
    <div
      v-for="item in list"
      :key="item"
      class="li"
    >
      <span>{{ item }}</span>
    </div>
  </div>
  <div v-else-if="isValidArray(list) && list.length < 4 && !isAll">
    <div
      v-for="(item, index) in list"
      :key="item"
      class="li"
    >
      <span v-if="index < 3">{{ item }}</span>
    </div>
  </div>
  <div v-else-if="isValidArray(list) && list.length > 3 && !isAll">
    <el-popover
      placement="right"
      :width="400"
      trigger="hover"
    >
      <template #reference>
        <div>
          {{ list[0] }} <br />
          {{ list[1] }} <br />
          {{ list[2] }}...
        </div>
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
  <div v-else>暂不归集</div>
</template>
<script setup lang="tsx">
import { isValidArray } from '@/utils/is'

const props = withDefaults(
  defineProps<{
    info: any
    isAll?: boolean
    detailList?: any
  }>(),
  { isAll: false }
)

const list: any = ref([])

watchEffect(() => {
  list.value = []
  if (props.info) {
    if (isValidArray(props.info)) {
      props.info.forEach((item: any) => {
        list.value.push(...item.feeShareDetail.split(','))
      })
    } else {
      list.value.push(...props.info.split(','))
    }
  } else if (props?.detailList) {
    list.value.push(...props.detailList)
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
