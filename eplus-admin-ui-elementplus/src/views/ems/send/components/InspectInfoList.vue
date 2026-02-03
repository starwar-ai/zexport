<template>
  <eplus-form-table
    ref="custClaimItemRef"
    :selectionFlag="false"
    :list="info"
    :defaultVal="{}"
    :schema="tableColumns"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'

const props = defineProps<{
  info?: any
}>()

let tableColumns = reactive([
  {
    field: 'goodsSource',
    label: '物件来源',
    width: '400px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.goodsSource}
          disabled={true}
          dictType={DICT_TYPE.GOODS_SOURCE}
          style="width:100%"
        />
      )
    }
  },

  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    label: '产品编号',
    field: 'skuCode'
  },
  {
    field: 'mainPicture',
    label: '图片',
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.mainPicture}
        />
      )
    }
  },
  {
    field: 'quantity',
    label: '数量'
  }
])
</script>
