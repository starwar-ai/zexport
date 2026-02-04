<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="QuotationDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'sms:quotation:create',
      handler: () => {}
    }"
    :cancel="{
      permi: 'sms:quotation:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'sms:quotation:update',
      handler: () => goEdit('报价单')
    }"
    :approve="{
      permi: 'sms:quotation:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="QuotationDetail"
      :items="QuotationSchemas"
    />

    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="报价单明细"
        name="first"
      >
        <el-radio-group
          v-model="unitRadio"
          class="mb10px"
          @change="handleUnitRadioChange"
        >
          <el-radio-button
            label="metric"
            value="公制"
            >公制
          </el-radio-button>
          <el-radio-button
            label="eng"
            value="英制"
            >英制
          </el-radio-button>
        </el-radio-group>
        <Table
          :columns="productTableColumns"
          headerAlign="center"
          align="center"
          :data="QuotationDetail?.children"
          :max-height="450"
        />
      </el-tab-pane>
      <el-tab-pane
        label="其他费用"
        name="second"
      >
        <Table
          :columns="otherFeeTableColumns"
          headerAlign="center"
          align="center"
          :data="QuotationDetail?.otherFeeList"
        />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
  <ExportDialog
    ref="ExportDialogRef"
    @sure="handleExportSure"
  />
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/quotation'
import * as QuotationApi from '@/api/sms/quotation'
import { DICT_TYPE } from '@/utils/dict'
import ExportDialog from './components/ExportDialog.vue'
import download from '@/utils/download'
import { LengthRatio, VolumeRatio } from '@/utils/config'
import { columnWidth, formatDateColumn, formatMoneyColumn } from '@/utils/table'
import { formatNum, openPdf } from '@/utils/index'
import * as ReportApi from '@/api/system/poiReport'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'

const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()
const props = defineProps<{
  title?: string
  id?: number
}>()
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const activeName = ref('first')
const QuotationDetail = ref({})
const ExportDialogRef = ref()

const QuotationSchemas = reactive([
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    dictType: DICT_TYPE.PRICE_TYPE
  },
  {
    field: 'custPocName',
    label: '客户联系人'
  },
  {
    field: 'currency',
    label: '币种',
    dictType: DICT_TYPE.CURRENCY_TYPE
  },
  {
    field: 'countryName',
    label: '出运国家'
  },
  {
    field: 'departurePortName',
    label: '出运口岸'
  },
  {
    field: 'validPeriod',
    label: '有效期止',
    type: 'date'
  }
])

const otherFeeTableColumns = reactive([
  {
    field: 'feeName',
    label: '费用名称'
  },
  {
    field: 'amount',
    label: '金额',
    formatter: formatMoneyColumn()
  }
])
const unitRadio = ref('metric')
const router = useRouter()
const openProductDetail = (row) => {
  setSourceId(row.skuId)
  if (row.custProFlag == 1) {
    if (checkPermi(['pms:csku:query']) && checkPermi(['pms:csku:detail'])) {
      router.push({ path: '/base/product-manage/csku' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
    if (checkPermi(['pms:own-brand-sku:query']) && checkPermi(['pms:own-brand-sku:detail'])) {
      router.push({ path: '/base/product-manage/own' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
    if (checkPermi(['pms:sku:query']) && checkPermi(['pms:sku:detail'])) {
      router.push({ path: '/base/product-manage/main' })
    } else {
      message.error('暂无权限查看详情')
    }
  }
}
//产品信息table
let productTableColumns = reactive([
  {
    field: 'skuCode',
    label: '产品编号',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <span
            onClick={() => openProductDetail(row)}
            style="color:rgb(0, 91, 245);cursor:pointer;"
          >
            {row.skuCode}
          </span>
        )
      }
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: columnWidth.l
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    label: '产品类型',
    field: 'skutype',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.custProFlag == 1) {
          return <span>客户产品</span>
        } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
          return <span>自营产品</span>
        } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
          return <span>基础产品</span>
        }
      }
    }
  },
  {
    field: 'name',
    label: '中文名称'
  },
  {
    field: 'nameEng',
    label: '产品英文名称'
  },
  {
    field: 'withTaxPrice',
    label: '工厂报价',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'profitRate',
    label: '利润率(%)',
    minWidth: columnWidth.l
  },
  {
    field: 'quotation',
    label: '产品报价',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'moq',
    label: '起订量'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'packageTypeName',
    label: '包装方式'
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    minWidth: columnWidth.l
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    minWidth: columnWidth.l
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: '100',
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    formatter: (row) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    minWidth: columnWidth.xl,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec', unitRadio.value)}</div>
    }
  },

  {
    field: 'outerboxVolume',
    label: '外箱体积',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'vol', unitRadio.value)}</div>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'grossweight', unitRadio.value)}</div>
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'netweight', unitRadio.value)}</div>
    }
  },
  {
    field: 'description',
    label: '中文描述',
    minWidth: columnWidth.l
  },
  {
    field: 'descriptionEng',
    label: '英文描述',
    minWidth: columnWidth.l
  },
  {
    field: 'hsCode',
    label: '海关编码',
    minWidth: columnWidth.l
  },
  {
    field: 'quoteDate',
    label: '交货日期',
    minWidth: columnWidth.l,
    formatter: formatDateColumn()
  }
])

const handleClick = (val) => {}
const loading = ref(true)
const getPurchaseContractDetail = () => {}
const handleUpdate = async () => {
  await getPurchaseContractDetail()
}
if (query?.id) {
  showProcessInstanceTaskListFlag.value = false
  outDialogFlag.value = true
}
if (props.id) {
  showProcessInstanceTaskListFlag.value = true
  outDialogFlag.value = false
}
const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const getInfo = async () => {
  loading.value = true
  try {
    let obj = props.id
      ? await QuotationApi.getQuotation(props.id)
      : await QuotationApi.getAuditQuotation({ id: query?.id })
    QuotationDetail.value = obj
    QuotationDetail.value.children = obj.children.map((item) => {
      return {
        ...item,
        outerboxLengthEng: Number(item.outerboxLength) / LengthRatio,
        outerboxWidthEng: Number(item.outerboxWidth) / LengthRatio,
        outerboxHeightEng: Number(item.outerboxHeight) / LengthRatio,
        outerboxVolumeEng: Number(item.outerboxVolume) / VolumeRatio
      }
    })
    await setReportDOList()
    await setExportBtns()
  } finally {
    loading.value = false
  }
}
const reportDOListRef = ref()
let reportCode = 'quotation-form-export'
const setExportBtns = async () => {
  clearDialogActions()
  // if (checkPermi(['sms:quotation:export'])) {
  //   updateDialogActions(
  //     <el-button
  //       onClick={() => {
  //         handleExport()
  //       }}
  //       key="export"
  //     >
  //       导出
  //     </el-button>
  //   )
  // }
  if (checkPermi(['sms:quotation:print'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handlePrint(0)
        }}
      >
        打印
      </el-button>,
      <el-button
        onClick={() => {
          handlePrint(1)
        }}
      >
        打印(采购总价)
      </el-button>
    )
  }
}

const handlePrint = async (flag) => {
  let reportCode = 'sms_quotation'
  if (flag === 1) {
    reportCode = 'sms_quotation_purchase'
  }
  const url = await QuotationApi.printQuotation({
    id: props?.id,
    reportCode: reportCode,
    totalPurchaseFlag: flag,
    unit: unitRadio.value
  })
  openPdf(url)
}

const handleExport = async () => {
  if (reportDOListRef.value?.length) {
    await openExportDia()
  } else {
    await exportSingle()
  }
}

const exportSingle = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    const data = await QuotationApi.exportQuotation({
      id: props?.id,
      reportCode: reportCode,
      unit: unitRadio.value
    })
    if (data && data.size) {
      download.excel(data, '报价单.xlsx')
    }
  } catch {}
  return
}

const handleExportSure = async (reportId) => {
  try {
    const data = await QuotationApi.exportQuotation({
      id: props?.id,
      reportCode: reportCode,
      reportId: reportId,
      unit: unitRadio.value
    })
    if (data && data.size) {
      download.excel(data, '报价单.xlsx')
    }
  } catch {}
}

const openExportDia = () => {
  ExportDialogRef.value?.open(reportDOListRef.value)
}

const handleUnitRadioChange = async () => {
  await setReportDOList()
}

const setReportDOList = async () => {
  reportCode = 'quotation-form-export'
  if (unitRadio.value == 'eng') {
    reportCode = 'quotation-form-export-eng'
  }
  let params = {
    id: props?.id,
    reportCode: reportCode,
    companyId: QuotationDetail.value.companyId
  }
  reportDOListRef.value = await ReportApi.getCompanySpecificReport(params)
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('报价单id不能为空')
    if (!props.id) {
      close()
    }
  }
  await getInfo()
})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: #fff;
}
</style>
