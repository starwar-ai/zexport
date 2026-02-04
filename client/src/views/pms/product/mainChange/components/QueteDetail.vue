<template>
  <eplus-description
    title="供应商报价"
    :data="pageInfo"
    :items="quoteitemListInfo"
  >
    <template #quoteitemList>
      <el-radio-group v-model="unitRadio">
        <el-radio-button
          label="metric"
          value="公制"
          >公制
        </el-radio-button>
        <el-radio-button
          label="eng"
          value="英制"
          >英制
        </el-radio-button>
      </el-radio-group>
      <Table
        style="width: 100%; margin-top: 5px"
        :columns="quoteitemListColumns"
        headerAlign="center"
        align="center"
        :data="tableData"
      />
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { columnWidth } from '@/utils/table'
import { formatDate } from '@/utils/formatTime'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { getOuterbox } from '@/utils/outboxSpec'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'

const props = defineProps<{
  info?: any
}>()
const unitRadio = ref('metric')

const pageInfo = computed(() => props.info)
const tableData = ref([])
watch(
  () => unitRadio.value,
  (val) => {
    tableData.value = []
    tableData.value = pageInfo.value.quoteitemDTOList?.map((item) => {
      // 外箱规格 新旧对比数据拼接
      item.outerbox = getOuterbox(item, 'spec', unitRadio.value)
      item.outerboxVolume = getOuterbox(item, 'vol', unitRadio.value)
      item.outerboxGrossweight = getOuterbox(item, 'grossweight', unitRadio.value)
      item.outerboxNetweight = getOuterbox(item, 'netweight', unitRadio.value)

      // item.oldData.outerbox = `${item.oldData?.outerboxLength}*${item.oldData?.outerboxWidth}*${item.oldData?.outerboxHeight} ${LengthUnit}`
      // 包装规格 新旧对比数据拼接

      // item.package = item.packageLength
      //   ? `${item.packageLength}*${item.packageWidth}*${item.packageHeight} ${LengthUnit}`
      //   : ' '
      // item.oldData.package = `${item.oldData?.packageLength}*${item.oldData?.packageWidth}*${item.oldData?.packageHeight} ${LengthUnit}`

      // 是否含税/税率  新旧对比数据拼接
      item.currencyFaxFlagTaxRate =
        item.faxFlag === 0 ? `否/${item.taxRate}%` : item.faxFlag == 1 ? `是/${item.taxRate}%` : ' '
      // item.oldData.currencyFaxFlagTaxRate =
      //   item.oldData?.faxFlag === 0
      //     ? `否/${item.oldData?.taxRate}%`
      //     : item.oldData?.faxFlag == 1
      //       ? `是/${item.oldData?.taxRate}%`
      //       : ' '
      if (item.oldData) {
        item.oldData.outerbox = getOuterbox(item.oldData, 'spec')
        item.oldData.outerbox = getOuterbox(item.oldData, 'spec', unitRadio.value)
        item.oldData.outerboxVolume = getOuterbox(item.oldData, 'vol', unitRadio.value)
        item.oldData.outerboxGrossweight = getOuterbox(item.oldData, 'grossweight', unitRadio.value)
        item.oldData.outerboxNetweight = getOuterbox(item.oldData, 'netweight', unitRadio.value)

        // item.oldData.package = item.oldData?.packageLength
        //   ? `${item.oldData?.packageLength}*${item.oldData?.packageWidth}*${item.oldData?.packageHeight} ${LengthUnit}`
        //   : ' '
        item.oldData.currencyFaxFlagTaxRate =
          item.oldData.faxFlag === 0
            ? `否/${item.oldData.taxRate}%`
            : item.oldData.faxFlag == 1
              ? `是/${item.oldData.taxRate}%`
              : ' '
      }

      // item.oldData.currencyFaxFlagTaxRate = `${item.oldData.faxFlag === 0 ? '否' : item.oldData.faxFlag === 1 ? '是' : ''}/${item.oldData?.taxRate}%`
      // item.packageType = item.packageType[0]
      return {
        ...item
      }
    })
  },
  {
    immediate: true
  }
)

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
    label: '供应商名称',
    field: 'venderName',
    minWidth: '200px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="venderName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '是否含运费',
    field: 'freightFlag',
    // formatter: formatDictColumn(DICT_TYPE.IS_INT)
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="freightFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    label: '是否含包装',
    field: 'packageFlag',
    // formatter: formatDictColumn(DICT_TYPE.IS_INT)
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="packageFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    label: '包装方式',
    field: 'packageTypeName',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="packageTypeName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '币种',
    field: 'currency',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="currency"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '是否含税/税率',
    field: 'currencyFaxFlagTaxRate',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="currencyFaxFlagTaxRate"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    },
    width: '200'
  },
  {
    label: '起订量',
    field: 'moq',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="moq"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '单价',
    field: 'unitPrice',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="unitPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '含税价',
    field: 'withTaxPrice',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="withTaxPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '工厂货号',
    field: 'venderProdCode',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="venderProdCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '交期',
    field: 'delivery',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="delivery"
            oldChangeField="oldData"
            formatter={(val) => {
              return val && val !== ' ' ? `${val}天` : ''
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.m,
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    label: '单箱数量',
    field: 'qtyPerOuterbox',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="qtyPerOuterbox"
            oldChangeField="oldData"
            formatter={(val) => {
              return val && val !== ' ' ? `${val}` : ''
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    label: '外箱规格',
    field: 'outerbox',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerbox"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    label: '外箱体积',
    field: 'outerboxVolume',
    minWidth: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxVolume"
            oldChangeField="oldData"
            formatter={(val) => {
              return getOuterbox(row, 'vol', unitRadio.value)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    label: '外箱毛重',
    field: 'outerboxGrossweight',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxGrossweight"
            oldChangeField="oldData"
            formatter={(val) => {
              return getOuterbox(row, 'grossweight', unitRadio.value)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    label: '外箱净重',
    field: 'outerboxNetweight',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxNetweight"
            oldChangeField="oldData"
            formatter={(val) => {
              return getOuterbox(row, 'netweight', unitRadio.value)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    label: '报价日期',
    field: 'quoteDate',
    width: 150,
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="quoteDate"
            oldChangeField="oldData"
            formatter={(val) => {
              if (val && val !== ' ') {
                return formatDate(val, 'YYYY-MM-DD')
              } else {
                return ''
              }
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    label: '报价备注',
    field: 'remark',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="remark"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    fixed: 'right',
    width: '80',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="defaultFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  }
]
</script>
