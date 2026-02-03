<template>
  <div class="flex">
    <eplus-input-search-select
      ref="selectRef"
      v-model="model"
      :api="getPortList"
      :params="params"
      keyword="nameEng"
      label="nameEng"
      value="id"
      class="!w-100%"
      placeholder="请选择"
      :clearable="false"
      @change-emit="(...$event) => modelChange($event)"
      :defaultObj="defaultObj"
    />
    <el-button @click="handleAdd">
      <Icon icon="ep:plus" />
    </el-button>
  </div>
  <PortForm
    ref="formRef"
    @success="addSuccess"
  />
</template>
<script setup lang="tsx">
import PortForm from '@/views/system/port/PortForm.vue'
import { getPortList } from '@/api/system/port'

const model = defineModel<string | number>()

const props = withDefaults(
  defineProps<{
    defaultObj?: any
    params?: any
  }>(),
  {
    params: {
      pageSize: 100,
      pageNo: 1,
      status: 1
    }
  }
)
const emit = defineEmits(['changeEmit'])
const modelChange = (...$event) => {
  emit('changeEmit', $event[0][0], $event[0][1])
}

const selectRef = ref()
const formRef = ref()
const handleAdd = () => {
  formRef.value.open('create')
}
const addSuccess = () => {
  selectRef.value.getList()
}
</script>
