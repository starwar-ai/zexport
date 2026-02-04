<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="RenoticeApi.approveApi"
    :rejectApi="RenoticeApi.rejectApi"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
    :approve="{
      permi: 'wms:notice:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'wms:notice:audit'
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

    <eplus-form-items
      title="产品信息"
      :formSchemas="stockNoticeItemRespVOListSchemas"
    >
      <template #stockNoticeItemRespVOList>
        <Table
          :columns="stockNoticeItemColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.stockNoticeItemRespVOList"
        />
      </template>
    </eplus-form-items>

    <ToBillDia
      ref="ToBillDiaRef"
      @sure="handleUpdate"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as RenoticeApi from '@/api/wms/renotice'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn, formatWeightColumn } from '@/utils/table'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { formatNum } from '@/utils'
import { getOuterbox } from '@/utils/outboxSpec'
import { volPrecision, VolumeUnit } from '@/utils/config'
import { checkPermi } from '@/utils/permission'
import ToBillDia from '@/views/dms/containerNotice/ToBillDia.vue'
import { WarehouseStatusEnum } from '@/utils/constants'

const message = useMessage()
const pageInfo = ref()

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'RenoticeDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: () => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : {
      updateDialogActions: () => {},
      clearDialogActions: () => {}
    }

const loading = ref(true)
const { query } = useRoute()
const checkConvert = (row, type) => {
  let list = row.stockNoticeItemRespVOList.filter(
    (item) => item.convertBillFlag === 0 && item.shippedAddress === type
  )
  return list.length > 0
}

const ToBillDiaRef = ref()
const handleToBill = async (type) => {
  ToBillDiaRef.value.open(pageInfo.value, type)
}
const setBtn = () => {
  clearDialogActions()
  if (
    [
      WarehouseStatusEnum.UNTRANSFERRED.status,
      WarehouseStatusEnum.PARTIAL_TRANSFERRED.status
    ].includes(pageInfo.value.noticeStatus) &&
    checkConvert(pageInfo.value, 2) &&
    pageInfo.value.auditStatus == 2 &&
    checkPermi(['wms:notice-out:convert'])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await handleToBill(2)
        }}
        key="wmsNoticeOutConvert"
      >
        转出库单
      </el-button>
    )
  }
  if (
    [
      WarehouseStatusEnum.UNTRANSFERRED.status,
      WarehouseStatusEnum.PARTIAL_TRANSFERRED.status
    ].includes(pageInfo.value.noticeStatus) &&
    checkConvert(pageInfo.value, 1) &&
    pageInfo.value.auditStatus == 2 &&
    checkPermi(['wms:notice-out:factory'])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await handleToBill(1)
        }}
        key="wmsNoticeOutFactory"
      >
        工厂出库
      </el-button>
    )
  }
  if (
    [WarehouseStatusEnum.UNTRANSFERRED.status].includes(pageInfo.value.noticeStatus) &&
    [0, 3, 4].includes(pageInfo.value.auditStatus) &&
    checkPermi(['wms:notice:update'])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await goEdit()
        }}
        key="wmsNoticeUpdate"
      >
        编辑
      </el-button>
    )
  }
}

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await RenoticeApi.getNotice({ id: props.id })
      : await RenoticeApi.getAuditNotice({ id: query?.id })
    setBtn()
  } finally {
    loading.value = false
  }
}

/**
 * 生成明细信息
 * @param r
 */
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '通知单号'
  },
  {
    field: 'companyName',
    label: '归属公司'
  },
  {
    field: 'expectDate',
    label: '出库日期',
    type: 'date'
  },
  {
    field: 'printFlag',
    label: '打印状态',
    dictType: DICT_TYPE.PRINT_FLAG
  },

  {
    field: 'noticeStatus',
    label: '出库状态',
    dictType: DICT_TYPE.NOTICE_STATUS
  },
  {
    field: 'createUser',
    label: '创建人',
    formatter: (val) => {
      return `${val?.nickname}(${val?.deptName})`
    }
  },
  {
    field: 'createTime',
    label: '创建时间',
    type: 'time'
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
    field: 'warehouseName',
    label: '仓库名称'
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
    label: '中文名称'
  },
  {
    field: 'saleContractCode',
    label: '销售合同号'
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'custPo',
    label: '客户PO号'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'batchCode',
    label: '批次号'
  },
  {
    field: 'orderQuantity',
    label: '应出数量'
  },
  {
    field: 'shippedAddress',
    label: '发货地点',
    formatter: formatDictColumn(DICT_TYPE.SHIPPED_ADDRESS)
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'orderBoxQuantity',
    label: '应出箱数'
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
    field: 'outerbox',
    label: '外箱规格',
    width: '150',
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: '150',
    formatter: (row) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '单箱毛重',
    formatter: (row) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: '总体积',
    slots: {
      default: (data) => {
        const { row } = data
        return `${formatNum(row.totalVolume / 1000000, volPrecision)} ${VolumeUnit}`
      }
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    formatter: formatWeightColumn()
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
    field: 'purchaseUserName',
    label: '采购员'
  },
  {
    field: 'purchaseUserDeptName',
    label: '部门'
  },
  {
    field: 'remark',
    label: '备注',
    width: '200'
  }
])
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
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
