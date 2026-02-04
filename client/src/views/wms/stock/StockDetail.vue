<template>
  <eplus-detail
    ref="eplusDetailRef"
    :auditable="pageInfo"
    @update="handleUpdate"
  >
    <div class="flex">
      <div class="img w-300px">
        <p>{{ pageInfo.skuName }}</p>
        <EplusImgEnlarge
          :mainPicture="pageInfo.mainPicture"
          width="200"
          height="200"
        />
      </div>
      <eplus-description
        title="基础信息"
        :data="pageInfo"
        :items="basicInfo"
      />
    </div>
    <eplus-form-items
      title="批次信息"
      :formSchemas="batchInfo"
    >
      <template #children>
        <Table
          :columns="batchColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.children"
        />
      </template>
    </eplus-form-items>
  </eplus-detail>

  <RecordDia ref="RecordDiaRef" />
</template>
<script setup lang="tsx">
// 完全移除旧的导入语句
// 修改前
// import * as StockApi from '@/api/wms/stock'
// import * as StockApi from '@/api/wms/stock/index'

// 修改为直接导入需要的方法
import { getStockInfo } from '@/api/wms/stock/index'

import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import {
  columnWidth,
  formatDateColumn,
  formatMoneyColumn,
  formatNumColumn,
  formatWeightColumn
} from '@/utils/table'
import RecordDia from './RecordDia.vue'
import { volPrecision, VolumeUnit, weightUnit } from '@/utils/config'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import { formatNum } from '@/utils'

const message = useMessage()
const pageInfo = ref({})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const RecordDiaRef = ref()
const props = defineProps<{
  id?: number
  type: string
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'RenoticeDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    // 修改前
    // pageInfo.value = await StockApi.getStockInfo({ skuId: props.id })

    // 修改后
    pageInfo.value = await getStockInfo({ skuId: props.id })

    pageInfo.value?.children.forEach((item) => {
      item.boxCount = Math.ceil(item.initQuantity / item.qtyPerOuterbox)
      item.totalGrossweight = {
        weight: getOuterboxVal(item, 'grossweight') * item.boxCount,
        unit: weightUnit
      }
      item.totalNetweight = {
        weight: getOuterboxVal(item, 'netweight') * item.boxCount,
        unit: weightUnit
      }
    })
  } finally {
    loading.value = false
  }
}

/**
 * 生成明细信息
 * @param r
 */
const basicInfo: EplusDescriptionItemSchema[] = reactive([
  {
    field: 'custCode',
    label: '客户编码',
    disabled: true,
    span: 6
  },
  {
    field: 'custName',
    label: '客户名称',
    disabled: true,
    span: 6
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    span: 6
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    span: 6
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    span: 6
  },
  {
    field: 'totalInitQuantity',
    label: '总入库数量',
    span: 6
  },
  {
    field: 'totalUsedQuantity',
    label: '出库数量',
    span: 6
  },
  {
    field: 'totalLockQuantity',
    label: '锁定数量',
    span: 6
  },
  {
    field: 'totalAvailableQuantity',
    label: '已入库数量',
    span: 6
  }
  // {
  //   field: 'totalRemainderQuantity',
  //   label: '剩余库存'
  // }
])
const batchInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'children',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]

const batchColumns = reactive([
  {
    field: 'batchCode',
    label: '批次编号'
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量'
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量'
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
    label: '箱数',
    formatter: (row) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.l,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积${VolumeUnit}`,
    width: columnWidth.l,
    formatter: (row) => {
      return formatNum(getOuterboxVal(row, 'vol') / 1000000, volPrecision)
    }
  },
  {
    field: 'totalVolume',
    label: `总体积${VolumeUnit}`,
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return formatNum(row.totalVolume / 1000000, volPrecision)
      }
    }
  },
  // {
  //   field: 'unitPerOuterbox',
  //   label: '外箱单位',
  //   width: columnWidth.l,
  //   formatter: formatDictColumn(DICT_TYPE.UNIT_PER_OUTERBOX_TYPE)
  // },
  {
    field: 'outerboxGrossweight',
    label: `外箱毛重${weightUnit}`,
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterboxVal(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalGrossweight',
    label: `总毛重${weightUnit}`,
    width: columnWidth.l,
    formatter: formatWeightColumn(true)
  },
  {
    field: 'outerboxNetweight',
    label: `外箱净重${weightUnit}`,
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterboxVal(row, 'netweight')}</div>
    }
  },
  {
    field: 'totalNetweight',
    label: `总净重${weightUnit}`,
    width: columnWidth.l,
    formatter: formatWeightColumn(true)
  },
  {
    field: 'companyName',
    label: '公司名称'
  },
  {
    field: 'saleContractCode',
    label: '外销合同号'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'createTime',
    label: '添加日期',
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'position',
    label: '位置'
  },
  {
    field: 'warehouseName',
    label: '仓库名称'
  },
  {
    field: 'producingQuantity',
    label: '在制数量'
  },
  {
    field: 'availableQuantity',
    label: '已入库数量'
  },
  {
    field: 'lockQuantity',
    label: '锁定数量'
  },
  {
    field: 'diffQuantity',
    label: '盘盈盘亏数量',
    width: '120px',
    formatter: formatNumColumn()
  },
  {
    field: 'price',
    label: '单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'totalAmount',
    label: '入库总金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'remainTotalAmount',
    label: '剩余总金额',
    formatter: formatMoneyColumn()
  },
  // {
  //   field: 'remainderQuantity',
  //   label: '剩余库存'
  // },
  {
    field: 'initQuantity',
    label: '总入库数量'
  },
  {
    field: 'usedQuantity',
    label: '出库数量'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    slots: {
      default: (scope) => {
        return <span>{scope.row.purchaseUser ? scope.row.purchaseUser.nickname : ''}</span>
      }
    }
  },
  {
    field: 'purchaseUserDeptName',
    label: '采购员部门',
    slots: {
      default: (scope) => {
        return <span>{scope.row.purchaseUser ? scope.row.purchaseUser.deptName : ''}</span>
      }
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '120px',
    fixed: 'right',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="flex items-center justify-center">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleDetail(row.batchCode)
              }}
            >
              出入库记录
            </el-button>
          </div>
        )
      }
    }
  }
])
const handleDetail = (code) => {
  RecordDiaRef.value.open(code)
}
const handleUpdate = async () => {
  await getPageInfo()
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  basicInfo[0].disabled = props.type === 'own'
  basicInfo[1].disabled = props.type === 'own'

  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
