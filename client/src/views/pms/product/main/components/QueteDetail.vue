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
import { formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import SplitBoxCom from './SplitBoxCom.vue'
import { lengthComposeConvert, volConvert, weightConvert } from '@/utils/config'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'

const message = useMessage()

const props = defineProps<{
  info?: any
}>()
const unitRadio = ref('metric')
/// 打开详情
const router = useRouter()
const handleDetail = (id) => {
  setSourceId(id)
  if (checkPermi(['scm:vender:query']) && checkPermi(['scm:vender:detail'])) {
    router.push({ path: '/base/vender/business' })
  } else {
    message.error('暂无权限查看详情')
  }
}
const pageInfo = computed(() => props.info)
const tableData = ref([])

watch(
  () => unitRadio.value,
  (val) => {
    tableData.value = []
    tableData.value = pageInfo.value.quoteitemDTOList?.map((item) => {
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

// const hsdataInfo: EplusDescriptionItemSchema[] = [
//   {
//     field: 'hsdataCode',
//     label: '报关HSCODE'
//   },
//   {
//     field: 'hsdataUnit',
//     label: '报关单位'
//   },
//   {
//     slotName: 'hsdataTaxRefundRate',
//     field: 'hsdataTaxRefundRate',
//     label: '退税率'
//   }
// ]

const quoteitemListColumns = [
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '报价日期',
    field: 'quoteDate',
    width: 150,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    label: '供应商编号',
    field: 'venderCode',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <span
            class="cursor-pointer my-text-primary"
            onClick={() => {
              handleDetail(row?.venderId)
            }}
          >
            {row?.venderCode}
          </span>
        )
      }
    }
  },
  {
    label: '供应商名称',
    field: 'venderName'
  },
  {
    label: '币种',
    field: 'currency'
  },
  {
    label: '含税价',
    field: 'withTaxPrice',
    formatter: formatMoneyColumn()
  },
  {
    label: '包装价',
    field: 'packagingPrice',
    formatter: formatMoneyColumn()
  },
  {
    label: '总成本单价',
    field: 'totalPrice',
    formatter: formatMoneyColumn()
  },
  {
    label: '税率(%)',
    field: 'taxRate',
    width: '120'
  },
  {
    label: '报价备注',
    field: 'remark'
  },
  {
    label: '采购员',
    field: 'purchaseUserName'
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
    label: '起订量',
    field: 'moq',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.moq}`
      }
    }
  },

  {
    label: '工厂货号',
    field: 'venderProdCode'
  },
  // {
  //   label: '交期',
  //   field: 'delivery',
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return row.delivery ? `${row.delivery} 天` : '-'
  //     }
  //   }
  // },
  {
    label: '内盒装量',
    field: 'qtyPerInnerbox'
  },
  {
    label: '外箱装量',
    field: 'qtyPerOuterbox'
  },
  {
    label: '是否分箱',
    width: 80,
    field: 'splitBoxFlag',
    slots: {
      default: (data) => {
        const { row } = data
        return <SplitBoxCom row={row} />
      }
    }
  },
  {
    label: '外箱规格',
    field: 'outerbox',
    slots: {
      default: (data) => {
        const { row } = data
        return getOuterbox(row, 'spec')
      }
    }
  },
  {
    label: '外箱体积',
    field: 'outerboxVolume',
    width: '120',
    slots: {
      default: (data) => {
        const { row } = data
        return getOuterbox(row, 'vol')
      }
    }
  },
  {
    label: '外箱毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return getOuterbox(row, 'grossweight')
      }
    }
  },
  {
    label: '外箱净重',
    slots: {
      default: (data) => {
        const { row } = data
        return getOuterbox(row, 'netweight')
      }
    }
  },
  {
    label: '20#装量',
    slots: {
      default: (data) => {
        const { row } = data
        return zlConvert(row, 29000000)
      }
    }
  },
  {
    label: '40#装量',
    slots: {
      default: (data) => {
        const { row } = data
        return zlConvert(row, 59000000)
      }
    }
  },
  {
    label: '40HQ#装量',
    slots: {
      default: (data) => {
        const { row } = data
        return zlConvert(row, 68000000)
      }
    }
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

const getOuterbox = (row, type) => {
  if (type === 'spec') {
    if (row.splitBoxFlag == 0) {
      let item = row.specificationList[0]
      return lengthComposeConvert(
        item.outerboxLength,
        item.outerboxWidth,
        item.outerboxHeight,
        unitRadio.value
      )
    } else {
      return '-'
    }
  } else if (type === 'vol') {
    return volConvert(
      row.specificationList.reduce((prev, cur) => prev + cur.outerboxVolume, 0),
      unitRadio.value
    )
  } else if (type === 'grossweight') {
    return weightConvert(
      {
        weight: row.specificationList.reduce(
          (prev, cur) => prev + cur.outerboxGrossweight.weight,
          0
        ),
        unit: row.specificationList[0].outerboxGrossweight.unit
      },
      unitRadio.value
    )
  } else if (type === 'netweight') {
    return weightConvert(
      {
        weight: row.specificationList.reduce((prev, cur) => prev + cur.outerboxNetweight.weight, 0),
        unit: row.specificationList[0].outerboxNetweight.unit
      },
      unitRadio.value
    )
  }
}
//装量计算
//20#装量（29 / 外箱体积 * 外箱装量）、40#装量（59 / 外箱体积 * 外箱装量）、40HQ#装量（68 / 外箱体积 * 外箱装量）
//注意：如果 除以外箱体积后有小数点，那么向下取整后再乘以外箱装量
const zlConvert = (row, bs) => {
  let vol = row.specificationList.reduce((prev, cur) => prev + cur.outerboxVolume, 0)
  if (row?.qtyPerOuterbox && vol) {
    return Math.floor(Number(bs) / Number(vol)) * Number(row?.qtyPerOuterbox)
  } else {
    return '-'
  }
}
</script>
