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
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as RenoticeApi from '@/api/wms/renotice'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { WarehouseStatusEnum } from '@/utils/constants'
import { checkPermi } from '@/utils/permission'
import { formatNum, openPdf } from '@/utils/index'
import { formatStringConcat } from '@/utils/formatStringConcat'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import { volPrecision } from '@/utils/config'

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
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    let res = props.id
      ? await RenoticeApi.getNotice({ id: props.id })
      : await RenoticeApi.getAuditNotice({ id: query?.id })
    let salesName = ''
    let saleDeptNames = ''
    let saleMobiles = ''
    res.stockNoticeItemRespVOList.forEach((item) => {
      if (item.sales.nickname) {
        salesName = formatStringConcat(salesName, item.sales.nickname)
      }
      if (item.sales.deptName) {
        saleDeptNames = formatStringConcat(saleDeptNames, item.sales.deptName)
      }
      if (item.sales.mobile) {
        saleMobiles = formatStringConcat(saleMobiles, item.sales.mobile)
      }
    })
    pageInfo.value = {
      ...res,
      salesName: salesName,
      saleDeptNames: saleDeptNames,
      saleMobiles: saleMobiles
    }
  } finally {
    loading.value = false
  }
}

const emit = defineEmits(['success'])
const handleRefresh = () => {
  emit('success')
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
    label: '预计到货日期',
    type: 'date'
  },
  {
    field: 'printFlag',
    label: '打印状态',
    dictType: DICT_TYPE.PRINT_FLAG
  },
  {
    field: 'noticeStatus',
    label: '入库状态',
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
    field: 'skuCode',
    label: '产品编号'
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: '150'
  },
  {
    field: 'skuName',
    label: '中文名称'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'purchaseUserName',
    label: '采购员'
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
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'warehouseName',
    label: '仓库名称'
  },
  {
    field: 'orderQuantity',
    label: '总入库数量'
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
    field: 'orderBoxQuantity',
    label: '总入库箱数',
    formatter: (row) => {
      return <span>{formatNum(row.orderBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: 120,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
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
    label: '总体积m³',
    slots: {
      default: (data) => {
        const { row } = data
        return formatNum(row.totalVolume / 1000000, volPrecision)
      }
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return row.totalWeight?.weight
          ? `${formatNum(row.totalWeight.weight)} ${row.totalWeight.unit}`
          : '-'
      }
    }
  },
  {
    field: 'realBillQuantity',
    label: '实际入库数量',
    width: '120',
    slots: {
      default: (data) => {
        const { row } = data
        return formatNum(row.realBillQuantity) || 0
      }
    }
  },
  {
    field: 'transformStockQuantity',
    label: '已入库数量',
    slots: {
      default: (data) => {
        const { row } = data
        return formatNum(Number(row.orderQuantity) - Number(row.pendingStockQuantity || 0)) || 0
      }
    }
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

const handleToBill = async () => {
  await message.confirm('确认转入库单吗？')
  let res = await RenoticeApi.toBill(pageInfo.value?.id)
  message.notifyPushSuccess('转入库单成功', res, 'ysbill')
  emit('success')
  close()
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

  if (
    pageInfo.value?.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status &&
    pageInfo.value?.auditStatus == 2
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await handleToBill()
        }}
        key="noticeChange"
      >
        转入库单
      </el-button>
    )
  }
  if (
    pageInfo.value?.noticeStatus == WarehouseStatusEnum.UNTRANSFERRED.status &&
    [0, 3, 4].includes(pageInfo.value?.auditStatus)
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await goEdit()
        }}
        key="noticeUpdate"
      >
        编辑
      </el-button>
    )
  }

  if (checkPermi(['wms:notice:print'])) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await print()
        }}
        key="payment"
      >
        打印
      </el-button>
    )
  }
})
const print = async () => {
  const url = await RenoticeApi.print({ id: props?.id, reportCode: 'renotice-print' })
  openPdf(url)
}
</script>
<style lang="scss" scoped></style>
