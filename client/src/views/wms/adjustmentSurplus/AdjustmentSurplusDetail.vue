<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :auditable="pageInfo"
  >
    <eplus-description
      title="基础信息"
      :data="pageInfo"
      :items="basicInfo"
    />
    <eplus-form-items
      title="产品信息"
      :formSchemas="proInfo"
    >
      <template #stocktakeItemRespVOList>
        <Table
          :columns="batchColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.adjustmentItems"
        />
      </template>
    </eplus-form-items>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import * as StocktakeApi from '@/api/wms/stocktake/index'
import { formatMoneyColumn } from '@/utils/table'

const message = useMessage()
const pageInfo = ref({})
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const props = defineProps<{
  id?: number
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
    pageInfo.value = await StocktakeApi.adjustmentDetail(props.id)
  } finally {
    loading.value = false
  }
}

/**
 * 生成明细信息
 * @param r
 */
const basicInfo = [
  {
    field: 'code',
    label: '盘点单号'
  },
  {
    field: 'warehouseName',
    label: '盘点仓库'
  },
  {
    field: 'adjustmentDate',
    label: '盘点日期',
    type: 'date'
  },
  {
    field: 'stocktakeUserName',
    label: '盘点人'
  },
  {
    field: 'remark',
    label: '备注',
    xl: 8,
    lg: 12
  }
]
const proInfo = [
  {
    field: 'stocktakeItemRespVOList',
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
    field: 'skuCode',
    label: '产品编号'
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号'
  },
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'stockQuantity',
    label: '库存数'
  },
  {
    field: 'stocktakeStockQuantity',
    label: '实际库存数'
  },
  {
    field: 'diffReasons',
    label: '差异原因'
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
    field: 'position',
    label: '位置'
  },
  {
    field: 'stocktakePosition',
    label: '实际位置'
  },
  {
    field: 'price',
    label: '单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'totalAmount',
    label: '总价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'saleContractCode',
    label: '销售合同号'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'remark',
    label: '备注'
  }
])

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
