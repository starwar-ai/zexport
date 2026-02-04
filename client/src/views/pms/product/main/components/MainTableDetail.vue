<template>
  <eplus-description
    title="配件信息"
    :data="pageInfo"
    :items="accessoriesListInfo"
    v-if="isValidArray(pageInfo?.accessoriesList)"
  >
    <template #accessoriesList>
      <Table
        style="width: 100%"
        :columns="accessoriesListColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.accessoriesList"
      />
    </template>
  </eplus-description>

  <eplus-description
    title="组合产品信息"
    :data="pageInfo"
    :items="subProductListInfo"
    v-if="!SubProductFlag && isValidArray(pageInfo?.subProductList)"
  >
    <template #singleProcessFee> {{ pageInfo.singleProcessFee }} RMB </template>
    <template #subProductList>
      <Table
        style="width: 100%"
        :columns="subProductListColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.subProductList"
      />
    </template>
  </eplus-description>
  <eplus-description
    title="包材配比"
    :data="pageInfo"
    :items="auxiliaryInfo"
    v-if="!AuxiliaryFlag"
  >
    <template #skuAuxiliaryList>
      <Table
        v-if="isValidArray(pageInfo?.skuAuxiliaryList)"
        style="width: 100%"
        :columns="skuAuxiliaryColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.skuAuxiliaryList"
      />
      <div v-else>暂无数据</div>
    </template>
  </eplus-description>

  <!-- <eplus-description
    title="供应商报价"
    :data="pageInfo"
    :items="quoteitemListInfo"
  >
    <template #quoteitemList>
      <Table
        style="width: 100%"
        :columns="quoteitemListColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.quoteitemDTOList"
      />
    </template>
  </eplus-description>
  <eplus-description
    title="海关信息"
    :data="pageInfo"
    :items="hsdataInfo"
  >
    <template #hsdataTaxRefundRate>
      <span v-if="pageInfo.hsdataTaxRefundRate">{{ pageInfo.hsdataTaxRefundRate }} % </span>
      <span v-else>-</span>
    </template>
  </eplus-description> -->
</template>
<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import {
  formatDateColumn,
  formatDictColumn,
  formatMoneyColumn,
  formatWeightColumn
} from '@/utils/table'
import { isValidArray } from '@/utils/is'
import { LengthUnit } from '@/utils/config'
import ContractUpdateSku from '@/views/scm/purchase/components/ContractUpdateSku.vue'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

const props = defineProps<{
  info?: any
  AuxiliaryFlag?: boolean
  SubProductFlag?: boolean
}>()

const pageInfo = computed(() => props.info)
const subProductListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'singleProcessFee',
    field: 'singleProcessFee',
    label: '单位加工费'
  },
  {
    field: 'processRemark',
    label: '加工备注'
  },
  {
    slotName: 'subProductList',
    field: 'subProductList',
    label: '',
    span: 24
  }
]
const accessoriesListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'accessoriesList',
    field: 'accessoriesList',
    label: '',
    span: 24
  }
]
const quoteitemListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'quoteitemList',
    field: 'quoteitemList',
    label: '',
    span: 24
  }
]

const hsdataInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'hsdataCode',
    label: '报关HSCODE'
  },
  {
    field: 'hsdataUnit',
    label: '报关单位'
  },
  {
    slotName: 'hsdataTaxRefundRate',
    field: 'hsdataTaxRefundRate',
    label: '退税率'
  }
]
const auxiliaryInfo = [
  {
    slotName: 'skuAuxiliaryList',
    field: 'skuAuxiliaryList',
    label: '',
    labelWidth: 0,
    span: 24
  }
]

const accessoriesListColumns = [
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '图片',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.picture.length > 0) {
          let url = row.picture.find((item) => item.mainFlag == 1)?.fileUrl
          return <EplusImgEnlarge mainPicture={url} />
        } else {
          return '-'
        }
      }
    }
  },
  {
    field: 'name',
    label: '中文品名',
    minWidth: '100px'
  },
  {
    field: 'code',
    label: '产品编号',
    minWidth: '100px'
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    minWidth: '100px',
    formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
  },
  {
    field: 'material',
    label: '产品材质',
    minWidth: '100px'
  },
  {
    field: 'totalAmountList',
    label: '单品规格/净重',
    minWidth: '180px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.singleNetweight?.weight) {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/${row.singleNetweight.weight} ${row.singleNetweight.unit}`
        } else {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/-`
        }
      }
    }
  },
  {
    label: '采购成本',
    field: 'withTaxPrice',
    minWidth: '100px',
    formatter: formatMoneyColumn()
  },
  {
    label: '单品毛重',
    minWidth: '100px',
    formatter: formatWeightColumn()
  }
]

const subProductListColumns = [
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '图片',
    minWidth: '60px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.picture.length > 0) {
          let url = row.picture.find((item) => item.mainFlag == 1)?.fileUrl
          return (
            <EplusImgEnlarge
              thumbnail={row.thumbnail}
              mainPicture={url}
            />
          )
        } else {
          return '-'
        }
      }
    }
  },
  {
    field: 'name',
    label: '中文品名',
    minWidth: '100px'
  },
  {
    field: 'code',
    label: '产品编号',
    minWidth: '240px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <ContractUpdateSku
            row={row}
            updateFlag={true}
          />
        )
      }
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: '120px'
  },
  {
    label: '数量',
    field: 'qty',
    width: 90
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    minWidth: '100px',
    formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
  },
  {
    field: 'venderName',
    label: '供应商名称',
    minWidth: '100px'
  },
  {
    field: 'totalAmountList',
    label: '单品规格/净重',
    minWidth: '180px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.singleNetweight?.weight) {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/${row.singleNetweight.weight} ${row.singleNetweight.unit}`
        } else {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/-`
        }
      }
    }
  },
  {
    label: '采购成本',
    field: 'withTaxPrice',
    minWidth: '100px',
    formatter: formatMoneyColumn()
  }
]

const handleUpdateSku = async (row) => {
  let res = await getSkuInfo(row.code)
  row = Object.assign(row, {
    ...res,
    qty: row.qty
  })
}

const quoteitemListColumns = [
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '供应商名称',
    field: 'venderName'
  },
  {
    label: '是否含运费',
    field: 'freightFlag',
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    label: '是否含包装',
    field: 'packageFlag',
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    label: '包装方式',
    field: 'packageTypeName'
  },
  {
    label: '币种',
    field: 'currency'
  },
  {
    label: '是否含税/税率',
    field: 'currencyFaxFlagTaxRate',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.faxFlag == 1 ? '是' : '否'}/${row.taxRate}%`
      }
    },
    width: '200'
  },
  {
    label: '采购量',
    field: 'moq',
    slots: {
      default: (data) => {
        const { row } = data
        return `≥ ${row.moq}`
      }
    }
  },
  {
    label: '单价',
    field: 'unitPrice',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.unitPrice?.amount} ${row.unitPrice.currency}`
      }
    }
  },
  {
    label: '含税价',
    field: 'withTaxPrice',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.withTaxPrice?.amount} ${row.withTaxPrice.currency}`
      }
    }
  },
  {
    label: '交期',
    field: 'delivery',
    slots: {
      default: (data) => {
        const { row } = data
        return row.delivery ? `${row.delivery} 天` : '-'
      }
    }
  },
  {
    label: '采购链接',
    field: 'purchaseUrl'
  },
  {
    label: '单箱数量',
    field: 'qtyPerOuterbox',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.qtyPerOuterbox} pcs`
      }
    }
  },
  {
    label: '包装规格',
    field: 'package',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.packageLength}*${row.packageWidth}*${row.packageHeight} ${LengthUnit}`
      }
    }
  },
  {
    label: '外箱规格',
    field: 'outerbox',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.outerboxLength}*${row.outerboxWidth}*${row.outerboxHeight} ${LengthUnit}`
      }
    }
  },
  {
    label: '外箱体积',
    field: 'outerboxVolume',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.outerboxVolume} m³`
      }
    }
  },
  {
    label: '外箱毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.outerboxGrossweight?.weight} ${row.outerboxGrossweight?.unit}`
      }
    }
  },
  {
    label: '单品毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.singleGrossweight?.weight} ${row.singleGrossweight?.unit}`
      }
    }
  },
  {
    label: '外箱净重',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.outerboxWeight?.weight} ${row.outerboxWeight?.unit}`
      }
    }
  },
  {
    label: '报价日期',
    field: 'quoteDate',
    width: 150,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    label: '报价备注',
    field: 'remark'
  },
  {
    field: 'defaultFlag',
    label: '默认',
    fixed: 'right',
    width: '80',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.defaultFlag == 1 ? '是' : '否'}`
      }
    }
  }
]
const skuAuxiliaryColumns = [
  {
    label: '包材名称',
    field: 'auxiliarySkuName',
    minWidth: '100px'
  },
  {
    label: '包材图片',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.auxiliarySkuPicture.id) {
          return <EplusImgEnlarge mainPicture={row.auxiliarySkuPicture} />
        } else {
          return '-'
        }
      }
    }
  },
  {
    label: '包材比例',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.skuRate} : ${row.auxiliarySkuRate}`
      }
    }
  },
  {
    label: '规格描述',
    field: 'description',
    minWidth: '100px'
  },
  {
    label: '备注',
    field: 'remark',
    minWidth: '100px'
  }
]
</script>
