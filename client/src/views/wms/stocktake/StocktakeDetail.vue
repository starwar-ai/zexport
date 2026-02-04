<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="StocktakeApi.approveStocktake"
    :rejectApi="StocktakeApi.rejectStocktake"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
    :approve="{
      permi: 'wms:stocktake:audit',
      handler: () => {}
    }"
  >
    <eplus-description
      title="基础信息"
      :data="pageInfo"
      :items="basicInfo"
    >
      <!-- <template #applyerName>
        {{ pageInfo?.applyer?.nickname }}
      </template>
      <template #amount> {{ pageInfo?.amount.amount }} {{ pageInfo?.amount.currency }} </template> -->
    </eplus-description>
    <eplus-description
      title="产品信息"
      :data="pageInfo"
      :items="proInfo"
    >
      <template #stocktakeItemRespVOList>
        <Table
          :columns="batchColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.stocktakeItemRespVOList"
        />
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import * as StocktakeApi from '@/api/wms/stocktake/index'
import { formatMoneyColumn } from '@/utils/table'
import { checkPermi } from '@/utils/permission'
import download from '@/utils/download'

const message = useMessage()
const pageInfo = ref({})
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
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
    pageInfo.value = await StocktakeApi.getStocktake({ id: props.id })
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
    field: 'planDate',
    label: '预计盘点日期',
    type: 'date'
  },
  {
    field: 'stocktakeUserName',
    label: '盘点人'
  },
  {
    field: 'auditStatus',
    label: '审核状态',
    dictType: DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT
  },
  {
    field: 'stocktakeStatus',
    label: '盘点状态',
    dictType: DICT_TYPE.STOCK_TAKE
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
    slotName: 'stocktakeItemRespVOList',
    field: 'stocktakeItemRespVOList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]

const batchColumns = reactive([
  {
    field: 'companyName',
    label: '归属公司'
  },
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
    label: '基础产品编号',
    minWidth: '150px'
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
    field: 'remark',
    label: '备注'
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
  }
])
const { updateDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
    })
  : { updateDialogActions: () => {} }
const handleUpdate = async () => {
  await getPageInfo()
}
const setPageBtns = () => {
  if (checkPermi(['wms:stocktake:export'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          exportDetail(pageInfo.value)
        }}
        key="detailExport"
      >
        导出
      </el-button>
    )
  }
}
const exportDetail = async (row) => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    const data = await StocktakeApi.exportDetail({
      id: row.id,
      reportCode: 'wms-stocktake-export'
    })
    if (data && data.size) {
      download.excel(data, `盘点单${row.code}.xlsx`)
    }
  } catch {}
  return
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getPageInfo()
  setPageBtns()
})
</script>
<style lang="scss" scoped></style>
