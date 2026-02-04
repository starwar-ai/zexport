<template>
  <el-tabs
    v-model="activeName"
    type="card"
    class="demo-tabs"
  >
    <el-tab-pane
      label="全部产品"
      name="first"
    >
      <Table
        style="width: 100%"
        :columns="productColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo.children"
      />
    </el-tab-pane>
    <el-tab-pane
      label="操作记录"
      name="second"
    >
      <Table
        style="width: 100%"
        :columns="operateLogColumns"
        headerAlign="center"
        align="center"
        :data="[]"
      />
    </el-tab-pane>
  </el-tabs>
</template>

<script lang="tsx" setup>
import { ref } from 'vue'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatTime } from '@/utils/index'

import { formatDateColumn, formatDictColumn } from '@/utils/table'
import { LengthUnit } from '@/utils/config'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'

const activeName = ref('first')

const props = defineProps<{
  info?: any
}>()

const pageInfo = computed(() => props.info)
let productColumns = [
  {
    field: 'mainPicture',
    label: '物料图片',
    minWidth: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusImgEnlarge
            mainPicture={row?.mainPicture}
            thumbnail={row?.thumbnail}
          />
        )
      }
    }
  },
  {
    field: 'skuName',
    label: '物料名称'
  },
  {
    field: 'deliveryDate',
    label: '到料日期',
    width: '220px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="deliveryDate"
              formatter={(val) => {
                return formatTime(val, 'yyyy-MM-dd')
              }}
            />
          </>
        )
      }
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '175px'
  },
  {
    field: 'purchaseType',
    label: '采购类型',
    minWidth: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="purchaseType"
              formatter={(val) => {
                return getDictLabel(DICT_TYPE.PURCHASE_TYPE, val)
              }}
            />
          </>
        )
      }
    }
  },
  {
    field: 'quantity',
    label: '采购数量',
    width: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="quantity"
            />
          </>
        )
      }
    }
  },
  {
    field: 'unitPrice',
    label: '单价',
    minWidth: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.unitPrice?.amount} ${row.unitPrice?.currency}`
      }
    }
  },
  {
    field: 'freeFlag',
    label: '是否赠品',
    width: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="freeFlag"
              formatter={(val) => {
                return getDictLabel(DICT_TYPE.CONFIRM_TYPE, val)
              }}
            />
          </>
        )
      }
    }
  },
  {
    field: 'taxRate',
    label: '税率(%)',
    minWidth: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="taxRate"
            />
          </>
        )
      }
    }
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    width: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.withTaxPrice?.amount} ${row.withTaxPrice?.currency}`
      }
    }
  },
  {
    field: 'totalPrice',
    label: '含税总金额',
    width: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.totalPrice?.amount} ${row.totalPrice?.currency}`
      }
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="boxCount"
            />
          </>
        )
      }
    }
  },
  {
    field: 'packageFlag',
    label: '是否含包装',
    width: '120px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="packageFlag"
              formatter={(val) => {
                return getDictLabel(DICT_TYPE.CONFIRM_TYPE, val)
              }}
            />
          </>
        )
      }
    }
  },
  {
    field: 'freightFlag',
    label: '是否含运费',
    width: '120px',
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE),
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="freightFlag"
              formatter={(val) => {
                return getDictLabel(DICT_TYPE.CONFIRM_TYPE, val)
              }}
            />
          </>
        )
      }
    }
  },
  {
    field: 'qtyPerInnerbox',
    label: '单箱数量'
  },
  {
    field: 'package',
    label: '包装规格',
    minWidth: '180px',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.packageLength}*${row.packageWidth}*${row.packageHeight} ${LengthUnit}`
      }
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    minWidth: '180px',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.outerboxLength}*${row.outerboxWidth}*${row.outerboxHeight} ${LengthUnit}`
      }
    }
  }
]

const operateLogColumns = [
  {
    label: '操作时间',
    field: 'startTime',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
    width: 180
  },
  {
    label: '操作人',
    field: 'userNickName',
    width: 180
  },
  {
    label: '操作类型',
    field: 'userType',
    width: 180,
    formatter: formatDictColumn(DICT_TYPE.SYSTEM_OPERATE_TYPE)
  },
  {
    label: '操作详情',
    field: 'content'
  }
]
</script>

<style>
.demo-tabs > .el-tabs__content {
  /* padding: 32px; */
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 20px;
}
</style>
