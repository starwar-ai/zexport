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
    />

    <eplus-form-items
      title="入库单详情"
      :formSchemas="stockNoticeItemRespVOListSchemas"
    >
      <template #stockNoticeItemRespVOList>
        <Table
          :columns="stockNoticeItemColumns"
          headerAlign="center"
          align="center"
          height="430"
          :data="pageInfo?.children"
        />
      </template>
    </eplus-form-items>
    <eplus-description
      title="库存图片"
      :data="pageInfo"
      :items="pictureInfo"
    >
      <template #pictures>
        <UploadPic
          :pictureList="pageInfo.pictures"
          disabled
        />
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as BillApi from '@/api/wms/bill'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn, formatNumColumn, formatWeightColumn } from '@/utils/table'
import { formatStringConcat } from '@/utils/formatStringConcat'
import { formatNum } from '@/utils'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import { volPrecision, VolumeUnit } from '@/utils/config'
import { checkPermi } from '@/utils/permission'

const message = useMessage()
const pageInfo = ref({})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'YsbillDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
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

const setBtns = () => {
  if (pageInfo.value.billStatus == 1 && checkPermi(['wms:bill:update'])) {
    updateDialogActions(<el-button onClick={() => goEdit('编辑')}>编辑</el-button>)
  }
}

const getPageInfo = async () => {
  loading.value = true
  try {
    let res = await BillApi.getBill({ id: props.id })
    let salesName = ''
    let salesDeptName = ''
    res.children?.forEach((e) => {
      e.actQuantity = e.actQuantity ? e.actQuantity : e.orderQuantity
      if (e.sales.nickname) {
        salesName = formatStringConcat(salesName, e.sales.nickname)
        salesDeptName = formatStringConcat(salesDeptName, e.sales.deptName)
      }
    })
    pageInfo.value = {
      ...res,
      salesName: salesName,
      salesDeptName: salesDeptName,
      creatorName: res.createUser?.nickname,
      creatorDeptName: res.createUser?.deptName
    }

    setBtns()
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
    label: '入库单号'
  },
  {
    field: 'noticeCode',
    label: '入库通知单号'
  },
  {
    field: 'companyName',
    label: '归属公司'
  },
  {
    field: 'billTime',
    label: '入库日期',
    type: 'date'
  },
  // {
  //   field: 'purchaseContractCode',
  //   label: '采购合同号'
  // },
  // {
  //   field: 'purchaserName',
  //   label: '采购员'
  // },
  // bug 463
  // {
  //   field: 'purchaserDeptName',
  //   label: '部门'
  // },
  // {
  //   field: '',
  //   label: '联系电话'
  // },

  {
    field: 'salesName',
    label: '业务员'
  },
  {
    field: 'salesDeptName',
    label: '业务员部门'
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
    label: '创建人'
  },

  {
    field: 'creatorDeptName',
    label: '创建人部门'
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
const pictureInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'pictures',
    label: '',
    span: 24,
    slotName: 'pictures'
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
    field: 'sourceType',
    label: '来源单据类型',
    formatter: formatDictColumn(DICT_TYPE.STOCK_SOURCE_STATUS),
    width: '120px'
  },
  {
    field: 'skuCode',
    label: '产品编号'
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: '150px'
  },
  {
    field: 'skuName',
    label: '产品名称'
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
    field: 'purchaserDeptName',
    label: '采购员部门'
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
    field: 'ownBrandFlag',
    label: '自主品牌',
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
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
    label: '供应商'
  },
  {
    field: 'warehouseName',
    label: '仓库名称'
  },
  {
    field: 'position',
    label: '位置'
  },
  // {
  //   field: 'venderName',
  //   label: '采购序号'
  // },
  {
    field: 'orderQuantity',
    label: '应收数量',
    formatter: formatNumColumn()
  },
  {
    field: 'abnormalStatus',
    label: '异常描述',
    formatter: formatDictColumn(DICT_TYPE.ABNORMAL_STATUS)
  },
  {
    field: 'abnormalRemark',
    label: '异常说明'
  },
  {
    field: 'orderQuantity',
    label: '装箱量',
    formatter: formatNumColumn()
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
    label: '应收箱数',
    formatter: (row) => {
      return <span>{formatNum(row.orderBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'actQuantity',
    label: '实收数量',
    formatter: formatNumColumn()
  },

  {
    field: 'actBoxQuantity',
    label: '实收箱数',
    formatter: (row) => {
      return <span>{formatNum(row.actBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: '120',
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
    field: 'totalVolume',
    label: '总体积',
    width: '150',
    formatter: (row) => {
      return (
        <div>
          {formatNum(getOuterboxVal(row, 'vol') * row.orderBoxQuantity, volPrecision)} {VolumeUnit}
        </div>
      )
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
  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
