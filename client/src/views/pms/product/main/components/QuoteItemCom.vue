<template>
  <el-button
    type="primary"
    @click="handleAdd"
    >新增报价
  </el-button>
  <Table
    style="width: 100%; margin-top: 5px"
    :columns="quoteitemListColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
  />
  <QuoteDia
    ref="QuoteDiaRef"
    @sure="setTable"
  />
</template>
<script setup lang="tsx">
import { formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { lengthComposeConvert, volConvert, weightConvert } from '@/utils/config'
import { cloneDeep } from 'lodash-es'
import SplitBoxCom from './SplitBoxCom.vue'
import QuoteDia from './QuoteDia.vue'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'QuoteItemCom' })
const props = defineProps<{
  formData?
  fromPage?: String
}>()
const message = useMessage()
const QuoteDiaRef = ref()

let tableList = ref([])
const unitRadio = ref('metric')
const setTable = (data) => {
  let row = cloneDeep(data.item)
  if (data.type === 'add') {
    tableList.value.push({
      ...row,
      defaultFlag: tableList.value.find((item) => item.defaultFlag == 1)?.venderCode ? 0 : 1
    })
  } else {
    tableList.value[data.index] = row
  }
}

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
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    label: '供应商名称',
    field: 'venderName'
  },
  {
    label: '币种',
    width: 80,
    field: 'currency'
  },
  {
    label: '含税价',
    field: 'withTaxPrice',
    width: 80,
    formatter: formatMoneyColumn()
  },
  {
    label: '包装价',
    field: 'packagingPrice',
    formatter: formatMoneyColumn()
  },
  {
    label: '总成本单价',
    width: 100,
    field: 'totalPrice',
    formatter: formatMoneyColumn()
  },
  {
    label: '税率(%)',
    width: 80,
    field: 'taxRate'
  },
  {
    label: '报价备注',
    width: 80,
    field: 'remark'
  },
  {
    label: '采购员',
    width: 80,
    field: 'purchaseUserName'
  },
  {
    label: '是否含运费',
    width: 100,
    field: 'freightFlag',
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    label: '运费',
    width: 80,
    field: 'shippingPrice',
    formatter: formatMoneyColumn()
  },
  {
    label: '是否含包装',
    width: 100,
    field: 'packageFlag',
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    label: '包装方式',
    width: 80,
    field: 'packageTypeName'
  },
  {
    label: '起订量',
    width: 80,
    field: 'moq'
  },
  {
    label: '工厂货号',
    width: 80,
    field: 'venderProdCode'
  },
  {
    label: '内盒装量',
    width: 80,
    field: 'qtyPerInnerbox'
  },
  {
    label: '外箱装量',
    width: 80,
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
    width: 80,
    field: 'outerboxVolume',
    slots: {
      default: (data) => {
        const { row } = data
        return getOuterbox(row, 'vol')
      }
    }
  },
  {
    label: '外箱毛重',
    width: 80,
    slots: {
      default: (data) => {
        const { row } = data
        return getOuterbox(row, 'grossweight')
      }
    }
  },
  {
    label: '外箱净重',
    width: 80,
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
    width: '80',
    fixed: 'right',
    slots: {
      default: (data) => {
        const { $index, row } = data
        return row.defaultFlag == 1 ? (
          '是'
        ) : (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                setDefault($index)
              }}
            >
              设置默认
            </el-button>
          </div>
        )
      }
    }
  },
  {
    field: 'operate',
    label: '操作',
    width: '80',
    fixed: 'right',
    slots: {
      default: (data) => {
        const { $index, row } = data
        if (props.fromPage === 'basicPage') {
          return (
            <div class="flex items-center justify-center">
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: true,
                    otherKey: 'vender-edit',
                    label: '编辑',
                    permi: 'pms:sku:vender-edit',
                    handler: () => {
                      handleEdit($index)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-copy',
                    label: '复制',
                    permi: 'pms:sku:vender-copy',
                    handler: () => {
                      handleCopy(row)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-delete',
                    label: '移除',
                    permi: 'pms:sku:vender-delete',
                    handler: () => {
                      handleDel($index)
                    }
                  }
                ]}
                auditable={row}
              />
            </div>
          )
        } else if (props.fromPage === 'custPage') {
          return (
            <div class="flex items-center justify-center">
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: true,
                    otherKey: 'vender-edit',
                    label: '编辑',
                    permi: 'pms:csku:vender-edit',
                    handler: () => {
                      handleEdit($index)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-copy',
                    label: '复制',
                    permi: 'pms:csku:vender-copy',
                    handler: () => {
                      handleCopy(row)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-delete',
                    label: '移除',
                    permi: 'pms:csku:vender-delete',
                    handler: () => {
                      handleDel($index)
                    }
                  }
                ]}
                auditable={row}
              />
            </div>
          )
        } else if (props.fromPage === 'ownPage') {
          return (
            <div class="flex items-center justify-center">
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: true,
                    otherKey: 'vender-edit',
                    label: '编辑',
                    permi: 'pms:own-brand:vender-edit',
                    handler: () => {
                      handleEdit($index)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-copy',
                    label: '复制',
                    permi: 'pms:own-brand:vender-copy',
                    handler: () => {
                      handleCopy(row)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-delete',
                    label: '移除',
                    permi: 'pms:own-brand:vender-delete',
                    handler: () => {
                      handleDel($index)
                    }
                  }
                ]}
                auditable={row}
              />
            </div>
          )
        } else if (props.fromPage === 'agentPage') {
          return (
            <div class="flex items-center justify-center">
              <eplus-dropdown
                otherItems={[
                  {
                    isShow: true,
                    otherKey: 'vender-edit',
                    label: '编辑',
                    permi: 'pms:agent-sku:vender-edit',
                    handler: () => {
                      handleEdit($index)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-copy',
                    label: '复制',
                    permi: 'pms:agent-sku:vender-copy',
                    handler: () => {
                      handleCopy(row)
                    }
                  },
                  {
                    isShow: true,
                    otherKey: 'vender-delete',
                    label: '移除',
                    permi: 'pms:agent-sku:vender-delete',
                    handler: () => {
                      handleDel($index)
                    }
                  }
                ]}
                auditable={row}
              />
            </div>
          )
        }
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
      row.specificationList.reduce((prev, cur) => prev + Number(cur.outerboxVolume), 0),
      unitRadio.value
    )
  } else if (type === 'grossweight') {
    return weightConvert(
      {
        weight: row.specificationList.reduce(
          (prev, cur) => prev + Number(cur.outerboxGrossweight.weight),
          0
        ),
        unit: row.specificationList[0].outerboxGrossweight.unit
      },
      unitRadio.value
    )
  } else if (type === 'netweight') {
    return weightConvert(
      {
        weight: row.specificationList.reduce(
          (prev, cur) => prev + Number(cur.outerboxNetweight.weight),
          0
        ),
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
  let vol = row.specificationList.reduce((prev, cur) => prev + Number(cur.outerboxVolume), 0)
  if (row?.qtyPerOuterbox && vol) {
    return Math.floor(Number(bs) / Number(vol)) * Number(row?.qtyPerOuterbox)
  } else {
    return '-'
  }
}
const handleAdd = () => {
  QuoteDiaRef.value.open('add', {}, tableList.value.length)
}
const handleEdit = (index) => {
  QuoteDiaRef.value.open('edit', tableList.value[index], index)
}
const handleCopy = (row) => {
  let data = cloneDeep(row)
  tableList.value.push({ ...data, id: undefined, defaultFlag: 0 })
}
const handleDel = (index) => {
  if (tableList.value[index].defaultFlag === 1) {
    tableList.value.splice(index, 1)
    tableList.value[0].defaultFlag = 1
  } else {
    tableList.value.splice(index, 1)
  }
}
const setDefault = (index) => {
  tableList.value.forEach((item) => (item.defaultFlag = 0))
  tableList.value[index].defaultFlag = 1
}

const init = () => {
  tableList.value = []
  if (isValidArray(props.formData.quoteitemDTOList)) {
    props.formData.quoteitemDTOList.forEach((item) => {
      tableList.value.push(cloneDeep(item))
    })
  }
}

onMounted(() => {
  tableList.value = []
  if (isValidArray(props.formData.quoteitemDTOList)) {
    props.formData.quoteitemDTOList.forEach((item) => {
      tableList.value.push(cloneDeep(item))
    })
  }
})

const saveForm = async () => {
  if (isValidArray(tableList.value)) {
    return tableList.value.map((item) => {
      return {
        ...item,
        shippingPrice: {
          amount: item.shippingPrice?.amount || 0,
          currency: item.shippingPrice?.currency || item.currency
        },
        packagingPrice: {
          amount: item.packagingPrice?.amount || 0,
          currency: item.packagingPrice?.currency || item.currency
        }
      }
    })
  } else {
    message.warning('缺少供应商报价信息')
    return false
  }
}

defineExpose({ saveForm, init, tableList })
</script>
