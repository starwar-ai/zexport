<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :page="pageFlag"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
    backUrl="/dms/ContainerNotice"
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
          :max-height="450"
        />
      </template>
    </eplus-form-items>
    <template #otherAction>
      <component
        v-for="(item, index) in btnList"
        :key="index"
        :is="item"
      />
    </template>
  </eplus-detail>

  <ToBillDia
    ref="ToBillDiaRef"
    @sure="getPageInfo"
  />
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/loan-app'
import * as RenoticeApi from '@/api/wms/renotice'
import * as ContainerNoticeApi from '@/api/dms/containerNotice'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatDictColumn } from '@/utils/table'
import download from '@/utils/download'
import { checkPermi } from '@/utils/permission'
import { formatNum } from '@/utils'
import { volPrecision } from '@/utils/config'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import ToBillDia from './ToBillDia.vue'

const message = useMessage()
const pageInfo = ref()
const pageId = ref()
const pageFlag = ref(false)
const btnList = ref<any[]>([])

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'RenoticeDetail' })
//定义edit事件
const { close } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
    })
  : { close: () => {} }

const { updateDialogActions, clearDialogActions } =
  props.id && !useRoute().params.id
    ? (inject('dialogActions') as {
        updateDialogActions: (...args: any[]) => void
        clearDialogActions: () => void
      })
    : useRoute().params.id
      ? {
          updateDialogActions: (...args: any[]) => {
            btnList.value.push(...args)
          },
          clearDialogActions: () => {
            btnList.value.splice(0, btnList.value.length)
          }
        }
      : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()
const checkConvert = (row, type) => {
  let list = row.stockNoticeItemRespVOList.filter(
    (item) => item.convertBillFlag === 0 && item.shippedAddress === type
  )
  return list.length > 0
}
const setBtn = () => {
  clearDialogActions()
  if (checkPermi(['dms:container-transportation:export'])) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await exportExcel()
        }}
        key="export"
      >
        导出
      </el-button>
    )
  }
  if (checkPermi(['dms:container-transportation:convert'])) {
    if ([1, 4].includes(pageInfo.value.noticeStatus) && checkConvert(pageInfo.value, 2)) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            await handleToBill(2)
          }}
          key="dmsContainerTransportationConvert"
        >
          转出库单
        </el-button>
      )
    }
    if ([1, 4].includes(pageInfo.value.noticeStatus) && checkConvert(pageInfo.value, 1)) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            await handleToBill(1)
          }}
          key="dmsContainerTransportationConvert2"
        >
          工厂出库
        </el-button>
      )
    }
  }

  if (checkPermi(['dms:container-transportation:close']) && pageInfo.value.noticeStatus !== 3) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await handleClose()
        }}
        key="close"
      >
        作废
      </el-button>
    )
  }
}

const handleClose = async () => {
  await message.confirm('确认作废该拉柜通知单吗？')
  await ContainerNoticeApi.close(pageId.value)
  message.success('操作成功')
  getPageInfo()
}

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = await RenoticeApi.getNotice({ id: pageId.value })
    setBtn()
  } finally {
    loading.value = false
  }
}

const ToBillDiaRef = ref()
const handleToBill = async (type) => {
  ToBillDiaRef.value.open(pageInfo.value, type)
}

const exportExcel = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await RenoticeApi.containerTransportationExportExcel({
      id: pageId.value,
      reportCode: 'container-transportation-form-export'
    })
    if (data && data.size) {
      download.excel(data, '拉柜通知单.xlsx')
    }
  } catch {}
  return
}

const emit = defineEmits(['success'])

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
    field: 'referenceNumber',
    label: '提单号/进仓编号'
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
    field: 'inboundDate',
    label: '拉柜日期',
    type: 'date'
  },
  {
    field: 'shipmentType',
    label: '出运方式',
    dictType: DICT_TYPE.SHIPMENT_TYPE
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
    field: 'createUserName',
    label: '创建人'
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
  // {
  //   field: 'sortNum',
  //   label: '序号'
  // },
  // {
  //   field: 'sourceSortNum',
  //   label: '来源单据明细序号'
  // },
  {
    field: 'shippedAddress',
    label: '发货地点',
    formatter: formatDictColumn(DICT_TYPE.SHIPPED_ADDRESS)
  },
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
  // {
  //   field: 'ownBrandFlag',
  //   label: '自主品牌',
  //   formatter: formatDictColumn(DICT_TYPE.IS_INT)
  // },
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
    width: columnWidth.l,
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'orderBoxQuantity',
    label: '应出箱数',
    formatter: (row) => {
      return <span>{formatNum(row.orderBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.xl,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积m³',
    width: columnWidth.l,
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
        return row.totalWeight ? `${row.totalWeight.weight} ${row.totalWeight.unit}` : '-'
      }
    }
  },
  {
    field: 'sales',
    label: '业务员',
    slots: {
      default: (data) => {
        const { row } = data
        return row.sales ? `${row.sales.nickname}` : '-'
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
  if (useRoute().params.id) {
    pageId.value = useRoute().params.id
    pageFlag.value = true
  } else {
    pageFlag.value = false
    if (!props.id && !query.id) {
      message.error('ID不能为空')
      if (!props.id) {
        close()
      }
    }
    if (query.id) {
      showProcessInstanceTaskListFlag.value = false
      outDialogFlag.value = true
      pageId.value = query.id
    }
    if (props.id) {
      showProcessInstanceTaskListFlag.value = true
      outDialogFlag.value = false
      pageId.value = props.id
    }
  }
  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
