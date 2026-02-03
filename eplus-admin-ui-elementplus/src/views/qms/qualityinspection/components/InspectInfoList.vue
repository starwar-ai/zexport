<template>
  <eplus-description
    title=""
    :data="pageInfo"
    :items="quoteitemListInfo"
  >
    <template #quoteitemList>
      <Table
        style="width: 100%"
        :columns="quoteitemListColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.children"
        :max-height="450"
      />
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
import UploadSmallPic from '@/components/UploadPic/src/UploadSmallPic.vue'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { columnWidth, formatDictColumn, formatNumColumn } from '@/utils/table'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { formatNum } from '@/utils'
import { getOuterbox } from '@/utils/outboxSpec'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

const props = defineProps<{
  info?: any
}>()

const pageInfo = computed(() => props.info)

const quoteitemListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'quoteitemList',
    field: 'quoteitemList',
    label: '',
    span: 24
  }
]

const quoteitemListColumns = [
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '图片',
    field: 'picture',
    formatter: (row) => {
      return (
        <UploadSmallPic
          pictureList={row.picture}
          disabled
        />
      )
    },
    minWidth: '250'
  },
  {
    label: '验货结果',
    field: 'inspectionStatus',
    formatter: formatDictColumn(DICT_TYPE.INSPECT_RESULT_TYPE)
  },
  {
    label: '问题描述',
    field: 'failDesc'
  },
  {
    label: '历史出现问题',
    field: 'lastFailDesc',
    width: '200'
  },
  // {
  //   label: '验货费',
  //   field: 'itemAmount'
  // },
  // {
  //   label: '采购合同',
  //   field: 'purchaseContractCode'
  // },
  {
    label: '产品图片',
    field: 'mainPicture',
    formatter: (row) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.mainPicture}
        />
      )
    }
  },
  {
    label: '基础产品编号',
    field: 'basicSkuCode',
    width: columnWidth.l
  },
  {
    label: '客户货号',
    field: 'cskuCode'
  },
  {
    label: '自营产品货号',
    field: 'oskuCode',
    width: '140px'
  },
  {
    label: '产品名称',
    field: 'skuName'
  },
  {
    label: '采购数量',
    field: 'purchaseQuantity',
    formatter: formatNumColumn()
  },
  {
    label: '验货数量',
    field: 'quantity',
    formatter: formatNumColumn()
  },
  {
    label: '外箱数量',
    field: 'qtyPerOuterbox'
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '计算箱数',
    formatter: (row) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.xl,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.m
  },
  {
    label: '备注',
    field: 'remark'
  },
  {
    field: 'handleFlag',
    label: '处理状态',
    formatter: formatDictColumn(DICT_TYPE.QMS_HANDLE_STATE)
  },
  {
    field: 'reworkDesc',
    label: '返工说明',
    width: '150px'
  }
]
</script>
