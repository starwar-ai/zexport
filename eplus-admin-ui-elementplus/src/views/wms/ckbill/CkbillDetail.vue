<template>
  <eplus-detail
    ref="eplusDetailRef"
    :auditable="pageInfo"
    @update="handleUpdate"
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

      <template #sourceCode>
        <el-button
          type="primary"
          link
          @click="toPath"
        >
          {{ pageInfo?.sourceCode }}
        </el-button>
      </template>
    </eplus-description>

    <eplus-form-items
      title="出库详情"
      :formSchemas="stockNoticeItemRespVOListSchemas"
    >
      <template #stockNoticeItemRespVOList>
        <Table
          :columns="stockNoticeItemColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.children"
        />
      </template>
    </eplus-form-items>
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as BillApi from '@/api/wms/bill'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatWeightColumn } from '@/utils/table'
import { volPrecision, VolumeUnit } from '@/utils/config'
import { formatNum } from '@/utils'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission.js'
import { getOuterbox } from '@/utils/outboxSpec'

const message = useMessage()
const pageInfo = ref({})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'CkbillDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }
const { updateDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
}

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = await BillApi.getBill({ id: props.id })
    if (checkPermi(['wms:bill-out:update']) && pageInfo.value.billStatus == 1) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            goEdit('')
          }}
          key="update"
        >
          编辑
        </el-button>
      )
    }
  } finally {
    loading.value = false
  }
}

const emit = defineEmits(['success'])

/**
 * 生成明细信息
 * @param r
 */
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '出库单号'
  },
  {
    field: 'invoiceCode',
    label: '出运发票号'
  },
  {
    field: 'companyName',
    label: '归属公司'
  },
  {
    field: 'billTime',
    label: '出库日期',
    type: 'date'
  },
  {
    field: 'printFlag',
    label: '打印状态',
    dictType: DICT_TYPE.PRINT_FLAG
  },
  {
    field: 'billStatus',
    label: '单据状态',
    dictType: DICT_TYPE.STOCK_BILL_STATUS
  },
  {
    field: 'creatorName',
    label: '创建人',
    formatter: () => {
      return pageInfo.value?.createUser?.nickname || '-'
    }
  },
  {
    field: 'creatorDeptName',
    label: '创建人部门',
    formatter: () => {
      return pageInfo.value?.createUser?.deptName || '-'
    }
  },
  {
    field: 'createTime',
    label: '创建时间',
    type: 'time'
  },
  {
    field: 'createTime',
    label: '创建时间',
    type: 'time'
  },
  {
    field: 'sourceType',
    label: '来源单类型',
    dictType: DICT_TYPE.OUT_STOCK_SOURCE_TYPE
  },
  {
    slotName: 'sourceCode',
    field: 'sourceCode',
    label: '来源单编号'
  },
  {
    field: 'remark',
    label: '备注',
    span: 24
  }
]

//产品信息
const stockNoticeItemRespVOListSchemas = [
  {
    field: 'stockNoticeItemRespVOList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
const stockNoticeItemColumns = reactive([
  {
    field: 'sortNum',
    label: '序号'
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
    width: columnWidth.l
  },
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'sales',
    label: '业务员',
    slots: {
      default: (data) => {
        const { row } = data
        return row.sales?.nickname ? `${row.sales.nickname}` : '-'
      }
    }
  },
  {
    field: 'position',
    label: '位置'
  },
  {
    field: 'orderQuantity',
    label: '应出数量'
  },
  {
    field: 'actQuantity',
    label: '实出数量'
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'actBoxQuantity',
    label: '实出箱数',
    formatter: (row) => {
      return <span>{formatNum(row.actBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'qtyPerOuterbox',
    label: '装箱量'
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: '150',
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积`,
    width: '150',
    formatter: (row) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: `总体积`,
    slots: {
      default: (data) => {
        const { row } = data
        return `${formatNum(row.totalVolume / 1000000, volPrecision)} ${VolumeUnit}`
      }
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    formatter: (row) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    formatter: formatWeightColumn()
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'purchaserName',
    label: '采购员'
  },
  {
    field: 'manager',
    label: '跟单员(部门)',
    formatter: (row) => {
      return (
        <div>
          {row.manager.deptName
            ? `${row.manager.nickname}(${row.manager.deptName})`
            : row.manager.nickname}
        </div>
      )
    }
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
    field: 'warehouseName',
    label: '仓库名称'
  },
  {
    field: 'remark',
    label: '备注'
  }
])
const handleUpdate = async () => {
  await getPageInfo()
}

const router = useRouter()
const toPath = () => {
  setSourceId(pageInfo.value.sourceId)
  if (pageInfo.value.sourceType == 1) {
    if (checkPermi(['wms:stocktake:query'])) {
      router.push({ path: '/wms/stake/stocktake' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else if (pageInfo.value.sourceType == 2) {
    if (
      checkPermi(['dms:container-transportation:query']) &&
      checkPermi(['dms:container-transportation:detail'])
    ) {
      router.push({ path: '/dms/ContainerNotice' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else if (pageInfo.value.sourceType == 3) {
    if (checkPermi(['wms:notice:query'])) {
      router.push({ path: '/wms/2/ttnotice' })
    } else {
      message.error('暂无权限查看详情')
    }
  }
}

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
