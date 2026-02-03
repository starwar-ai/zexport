<template>
  <el-select
    v-model="model"
    clearable
    @change="modelChange"
    class="w-100%"
    filterable
    :filter-method="selectFilter"
  >
    <el-option
      v-for="item in filterList"
      :key="item.id"
      :label="item.name"
      :value="item.id"
    />
  </el-select>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { cloneDeep } from 'lodash-es'
import { useCountryStore } from '@/store/modules/countrylist'

const countryListData = useCountryStore().countryList

defineOptions({ name: 'EplusCountrySelect' })
const initList = ref<any[]>([])
const filterList = ref<any[]>([])
const model = defineModel<string>()
const props = withDefaults(
  defineProps<{
    multiple?: boolean
    filterUserList?: boolean
    simpleUserList?: any
    value?: string
    label?: string
  }>(),
  { multiple: false }
)
/** 查询列表 */
const getList = async () => {
  filterList.value = countryListData
  initList.value = cloneDeep(countryListData)
}

const emit = defineEmits(['change'])
const modelChange = (val) => {
  let itemObj = filterList.value.find((item) => val === item.id)
  emit('change', itemObj)
}
const selectFilter = (val: string) => {
  if (val) {
    filterList.value = initList.value.filter((item) => {
      if (item?.name) {
        return item?.name.toLowerCase().includes(val.trim().toLowerCase())
      }
    })
  } else {
    filterList.value = initList.value
  }
}
/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
