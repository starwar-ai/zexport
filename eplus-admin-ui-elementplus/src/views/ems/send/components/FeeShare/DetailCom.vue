<template>
  <div class="flex">
    <eplus-input-select
      v-model="model"
      :dataList="options"
      label="name"
      value="id"
      @change-emit="modelChange"
    />
    <el-button @click="handleAdd">
      <Icon icon="ep:list" />
    </el-button>
  </div>

  <DetailDialog
    ref="DesDialogRef"
    @sure="getList"
  />
</template>

<script setup lang="tsx">
// import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
// import { EplusDictSelect } from '@/components/EplusSelect'
// const amount = defineModel<string | number>('amount')
// const currency = defineModel<string>('currency')
import * as FeeShareApi from '@/api/common/feeShare'
import DetailDialog from './DetailDialog.vue'

const model = defineModel<any>()
const options = ref([])

const props = withDefaults(
  defineProps<{
    feeShareType: any
    relationType: any
  }>(),
  {}
)

const DesDialogRef = ref()
const handleAdd = () => {
  DesDialogRef.value.open(props, options.value)
}

const getList = () => {
  FeeShareApi.descList({
    feeShareType: props.feeShareType,
    relationType: props.relationType
  }).then((res) => {
    options.value = res.list
  })
}

const emit = defineEmits<{
  change
}>()
const modelChange = (val, list) => {
  let item = list.find((item) => item.id === val)
  emit('change', item)
}

watch(
  () => [props.feeShareType, props.relationType],
  ([code, type]) => {
    if (code && type) {
      getList()
    }
  },
  { immediate: true, deep: true }
)
</script>
