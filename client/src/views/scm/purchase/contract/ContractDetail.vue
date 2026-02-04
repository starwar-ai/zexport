<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :page="pageFlag"
    :approveApi="PurchaseContractApi.contractApprove"
    :rejectApi="PurchaseContractApi.contractReject"
    :auditable="puechaseContractDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'scm:purchase-contract:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'scm:purchase-contract:update',
      handler: () => goEdit()
    }"
    :approve="{
      permi: 'scm:purchase-contract:audit',
      handler: () => {}
    }"
    @update="pageRefresh"
    backUrl="/scm/purchase/contract"
  >
    <eplus-description
      title="采购合同信息"
      :data="puechaseContractDetail"
      :items="puechaseContractSchemas"
    >
      <template #code>
        <span>
          {{ puechaseContractDetail.code }}
          <el-button
            v-if="
              [
                PurchaseContractStatusEnum.READY_TO_SUBMIT.status,
                PurchaseContractStatusEnum.AWAITING_APPROVAL.status
              ].includes(puechaseContractDetail.contractStatus)
            "
            v-hasPermi="['scm:purchase-contract:update-code']"
            @click="updateContractCode"
            >修改单号
          </el-button>
        </span>
      </template>
      <template #managerNickName>
        <span>
          {{
            puechaseContractDetail.manager?.nickname ? puechaseContractDetail.manager.nickname : '-'
          }}
        </span>
      </template>
      <template #managerDeptName>
        <span>
          {{
            puechaseContractDetail.manager?.deptName ? puechaseContractDetail.manager.deptName : '-'
          }}
        </span>
      </template>
      <template #taxType>
        {{ taxTypeList.find((item) => item.key === puechaseContractDetail.taxType)?.label || '-' }}
      </template>
    </eplus-description>
    <eplus-description
      title="附件信息"
      :data="puechaseContractDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <UploadList
          :fileList="puechaseContractDetail.annex"
          disabled
        />
      </template>
      <template #designDraftList>
        <UploadList
          :fileList="puechaseContractDetail.designDraftList"
          @success="
            (data) => {
              puechaseContractDetail.designDraftList = data
              saveDesignDraft()
            }
          "
        />

        <!-- <el-button
          size="small"
          type="primary"
          @click="saveDesignDraft"
          >保存出片文件
        </el-button> -->
      </template>
      <template #signBackAnnexList>
        <UploadList
          :fileList="puechaseContractDetail.signBackAnnexList"
          disabled
        />
      </template>
    </eplus-description>
    <el-tabs v-model="activeName">
      <el-tab-pane
        label="采购合同信息"
        name="first"
      >
        <div class="d-block total-header mb5px flex justify-between">
          <el-radio-group v-model="unitRadio">
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
          <span style="font-size: 12px"> 合计&nbsp;&nbsp;&nbsp;&nbsp;{{ totalDes }} </span>
        </div>

        <Table
          :columns="productTableColumns"
          headerAlign="center"
          align="center"
          :data="puechaseContractDetail?.children"
        />
      </el-tab-pane>
      <el-tab-pane
        label="关联单据"
        name="second"
      >
        <RelateTable :data="puechaseContractDetail" />
      </el-tab-pane>
      <el-tab-pane
        label="包材分摊"
        name="sixth"
      >
        <!-- <Table
          :columns="auxiliaryShareTableColumns"
          headerAlign="center"
          align="center"
          :data="puechaseContractDetail?.allocationList"
        /> -->
        <AllocationList
          :info="puechaseContractDetail"
          :actionFlag="outDialogFlag"
          @get-info="getInfo"
        />
      </el-tab-pane>
      <el-tab-pane
        label="加减项"
        name="forth"
      >
        <Table
          :columns="addSubItemListSchemas"
          headerAlign="center"
          align="center"
          :data="puechaseContractDetail?.purchaseAddSubTermList"
        />
      </el-tab-pane>
      <el-tab-pane
        label="付款计划信息"
        name="fifth"
      >
        <el-button
          v-hasPermi="['scm:purchase-contract:update-payment-plan']"
          @click="handleUpdatePaymentPlan"
          class="mb10px"
          >修改控制
        </el-button>
        <Table
          :columns="paymentPlanListSchemas"
          headerAlign="center"
          align="center"
          :data="puechaseContractDetail?.purchasePaymentPlanList"
        />
      </el-tab-pane>
      <el-tab-pane
        label="操作记录"
        name="third"
      >
        <eplus-operate-log :logList="puechaseContractDetail?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
    <template #otherAction>
      <component
        v-for="(item, index) in btnList"
        :key="index"
        :is="item"
      />
    </template>
  </eplus-detail>

  <OtherActionCom
    ref="OtherActionComRef"
    @success="pageRefresh"
  />

  <UpdateContractCodeCom
    ref="updateContractCodeRef"
    @success="pageRefresh"
  />

  <UpdatePaymentPlan
    ref="updatePaymentPlanRef"
    @success="pageRefresh"
  />
</template>
<script setup lang="tsx">
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { ConvertInvoiceFlagEnum, PurchaseContractStatusEnum } from '@/utils/constants'
import UpdatePaymentPlan from '../components/UpdatePaymentPlan.vue'
import {
  columnWidth,
  formatDateColumn,
  formatDictColumn,
  formatMoneyColumn,
  formatNumColumn
} from '@/utils/table'
import { formatNum, openPdf } from '@/utils/index'
import RelateTable from '@/components/RelateTable/src/RelateTable.vue'
import UploadList from '@/components/UploadList/index.vue'
import AllocationList from './AllocationList.vue'
import { checkPermi } from '@/utils/permission'
import { useRateStore } from '@/store/modules/rate'
import download from '@/utils/download'
import ReceivedLogCom from './ReceivedLogCom.vue'
import { getConfigJson } from '@/api/common'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getActualRate } from '../components/getActualRate'
import OtherActionCom from './OtherActionCom.vue'
import UpdateContractCodeCom from './UpdateContractCode.vue'
import { EplusDesc, EplusImgEnlarge } from '@/components/EplusTemplate'
import { EplusCSkuCode } from '@/components/EplusSkuName'

const pageId = ref()
const pageFlag = ref(false)
const btnList = ref<any[]>([])
const rateList = useRateStore().rateList

const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()
const props = defineProps<{
  type: string
  title?: string
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
// const { close, goEdit } =
//   props.id && !useRoute().params.id
//     ? (inject('dialogEmits') as {
//         close: () => void
//         goEdit: (val) => void
//       })
//     : { close: () => {}, goEdit: () => {} }

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

const activeName = ref('first')
const puechaseContractDetail: any = ref({})

const taxTypeList = ref([])
const getTaxTypeList = async () => {
  taxTypeList.value = await getConfigJson({ configType: 'invoice.rate' })
}
//采购计划信息
const puechaseContractSchemas = reactive([
  {
    field: 'companyName',
    label: '采购主体'
  },
  {
    slotName: 'code',
    field: 'code',
    label: '采购单号'
  },
  {
    field: 'contractStatus',
    label: '单据状态',
    dictType: DICT_TYPE.PURCHASE_CONTRACT_STATUS
  },
  {
    field: 'purchaseUserName',
    label: '采购员'
  },
  {
    field: 'purchaseUserDeptName',
    label: '采购员部门'
  },
  {
    field: 'stockName',
    label: '采购仓库'
  },
  {
    field: 'printFlag',
    label: '打印状态',
    dictType: DICT_TYPE.PRINT_FLAG
  },
  {
    field: 'managerNickName',
    label: '跟单员',
    slotName: 'managerNickName'
  },
  {
    field: 'managerDeptName',
    label: '跟单员部门',
    slotName: 'managerDeptName'
  },
  {
    field: 'venderCode',
    label: '供应商号'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'currency',
    label: '供应商币种'
  },
  {
    field: 'taxRate',
    label: '汇率'
  },
  {
    field: 'paymentName',
    label: '付款方式'
  },
  {
    field: 'deliveryDate',
    label: '交货日期',
    type: 'date'
  },
  {
    field: 'saleContractCode',
    label: '销售合同'
  },
  {
    field: 'sales',
    label: '业务员',
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'sales',
    label: '业务员部门',
    formatter: (val) => {
      return val?.deptName
    }
  },
  {
    field: 'taxType',
    label: '发票类型',
    slotName: 'taxType'
  },
  {
    field: 'payedAmount',
    label: '已付金额',
    type: 'money'
  },
  {
    field: 'totalAmount',
    label: '应付金额',
    type: 'money'
  },
  {
    field: 'custCode',
    label: '客户编号'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'freight',
    label: '运费',
    type: 'money'
  },
  {
    field: 'otherCost',
    label: '其他费用',
    type: 'money'
  },
  {
    field: 'signBackTime',
    label: '回签日期',
    type: 'date'
  },
  {
    field: 'boxWithColor',
    label: '箱带颜色',
    dictType: DICT_TYPE.BOX_WITH_COLOR
  },
  {
    field: 'sampleCount',
    label: '样品套数'
  },
  // {
  //   field: 'paymentVenderName',
  //   label: '应付供应商'
  // },
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
    label: '创建日期',
    type: 'date'
  },
  {
    field: 'remark',
    label: '备注',
    xl: 8,
    lg: 12
  }
])
//采购计划附件信息
const annexSchemas = [
  {
    field: 'annex',
    label: '附件',
    slotName: 'annex',
    span: 24
  },
  {
    field: 'designDraftList',
    label: '出片文件',
    slotName: 'designDraftList',
    span: 24
  },
  {
    field: 'signBackAnnexList',
    label: '回签文件',
    slotName: 'signBackAnnexList',
    span: 24
  }
]
let addSubItemListSchemas = reactive([
  {
    field: 'contractCode',
    label: '采购合同',
    width: '180px',
    fixed: 'left'
  },
  {
    field: 'calculationType',
    label: '加/减项',
    formatter: formatDictColumn(DICT_TYPE.CALCULATION_TYPE)
  },
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
let paymentPlanListSchemas = reactive<any[]>([
  {
    field: 'step',
    label: '步骤',
    width: columnWidth.m,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.PAYMENT_PLAN_STEP, row?.step) || '-'
    }
  },
  {
    field: 'dateType',
    label: '起始点',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.DATE_TYPE, row?.dateType) || '-'
    }
  },
  {
    field: 'controlInvoiceFlag',
    label: '是否控制登票',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.IS_INT, row?.controlInvoiceFlag) || '-'
    }
  },
  {
    field: 'startDate',
    label: '起始日',
    formatter: formatDateColumn()
  },
  {
    field: 'days',
    label: '天数',
    formatter: formatNumColumn()
  },
  {
    field: 'expectedReceiptDate',
    label: '预计付款日',
    formatter: formatDateColumn()
  },
  {
    field: 'paymentRatio',
    label: '付款比例%',
    type: 'num'
  },
  {
    field: 'receivableAmount',
    label: '应付金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'paymentTime',
    label: '实际付款时间',
    slots: {
      default: (row) => {
        return <ReceivedLogCom row={row} />
      }
    }
  },
  {
    field: 'realPaymentRatio',
    label: '实际付款比例%',
    type: 'num'
  },
  {
    field: 'receivedAmount',
    label: '实付金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'annex',
    label: '附件',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <UploadList
            fileList={row.annex}
            disabled
          />
        )
      }
    }
  },
  {
    field: 'exeStatus',
    label: '状态',
    width: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.PAYMENT_STATUS)
  }
])

const unitRadio = ref('metric')

//产品信息table
let productTableColumns = reactive([
  // {
  //   field: 'sortNum',
  //   label: '序号'
  // },
  {
    field: 'mainPicture',
    label: '物料图片',
    minWidth: columnWidth.m,
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
    label: '产品名称',
    minWidth: columnWidth.xl,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusSkuName
            name={row.skuName}
            type={row.skuType}
          />
        )
      }
    }
  },
  // {
  //   field: 'planArriveDate',
  //   label: '到料日期',
  //   minWidth: columnWidth.m,
  //   formatter: formatDateColumn('YYYY-MM-DD')
  // },
  {
    field: 'cskuCode',
    label: '客户货号',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return <EplusCSkuCode row={row} />
      }
    }
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    minWidth: columnWidth.l
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: columnWidth.xl
  },
  // {
  //   field: 'venderProdCode',
  //   label: '工厂货号',
  //   minWidth: '175px'
  // },
  {
    field: 'quantity',
    label: '采购数量',
    minWidth: columnWidth.m,
    formatter: formatNumColumn()
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    minWidth: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'withTotalTaxPrice',
    label: '采购总金额',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusMoneyLabel
            val={{
              amount: row.withTaxPrice.amount * row.quantity,
              currency: row.withTaxPrice.currency
            }}
          />
        )
      }
    }
  },
  {
    field: 'taxRate',
    label: '税率',
    minWidth: columnWidth.m,
    formatter: (val) => {
      if (val?.taxRate || val?.taxRate === 0) {
        return val.taxRate + '%'
      } else return ''
    }
  },
  {
    field: 'outerboxLength',
    label: '外箱规格',
    minWidth: columnWidth.xl,
    slots: {
      default: (data) => {
        const { row } = data
        return <div style="white-space: pre-line;">{getOuterbox(row, 'spec', unitRadio.value)}</div>
      }
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div style="white-space: pre-line;">
            {getOuterbox(row, 'grossweight', unitRadio.value)}
          </div>
        )
      }
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div style="white-space: pre-line;">{getOuterbox(row, 'netweight', unitRadio.value)}</div>
        )
      }
    }
  },
  {
    field: 'description',
    label: '中文描述',
    minWidth: columnWidth.l,
    showOverflowTooltip: false,
    formatter: (row) => {
      return <EplusDesc info={row.description} />
    }
  },
  {
    field: 'freeFlag',
    label: '是否含赠品',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  },
  {
    field: 'freeQuantity',
    label: '赠品数量',
    minWidth: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'syncQuoteFlag',
    label: '是否同步供应商报价',
    width: columnWidth.xl,
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  },

  {
    field: 'packageFlag',
    label: '是否含包装',
    minWidth: columnWidth.ll,
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  },
  {
    field: 'packagingPrice',
    label: '包装价',
    width: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.m
  },
  {
    field: 'freightFlag',
    label: '是否含运费',
    minWidth: columnWidth.ll,
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  },
  {
    field: 'shippingPrice',
    label: '运费单价',
    width: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    minWidth: columnWidth.m,
    formatter: formatNumColumn()
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    minWidth: columnWidth.m,
    formatter: formatNumColumn()
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.m,
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.m,
    formatter: (row) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'purchaseType',
    label: '采购类型',
    minWidth: columnWidth.m,
    showOverflowTooltip: false,
    formatter: formatDictColumn(DICT_TYPE.PURCHASE_TYPE)
  },
  {
    field: 'billStatus',
    label: '入库地点',
    formatter: formatDictColumn(DICT_TYPE.SALE_ITEM_BILL_STATUS),
    minWidth: columnWidth.l
  },
  {
    field: 'unNoticedquantity',
    label: '待转入库通知数量',
    formatter: (row) => {
      let val = Number(row.quantity) - Number(row.noticedQuantity) || 0
      return val > 0 ? formatNum(val) : 0
    },
    minWidth: '140px'
  },
  {
    field: 'billQuantity',
    label: '实际入库数量',
    formatter: formatNumColumn(),
    minWidth: columnWidth.l
  },
  {
    field: 'checkStatus',
    label: '验货结果',
    formatter: formatDictColumn(DICT_TYPE.INSPECT_RESULT_TYPE),
    minWidth: columnWidth.l
  },
  {
    field: 'actualRate',
    label: '实际利润率',
    minWidth: columnWidth.l
  },
  {
    field: 'invoiceStatus',
    label: '开票状态',
    width: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.INVOICE_STATUS)
  },
  {
    field: 'invoicedAmount',
    label: '已开票金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusMoneyLabel
            val={{
              amount: row.withTaxPrice.amount * row.invoicedQuantity,
              currency: row.withTaxPrice.currency
            }}
          />
        )
      }
    }
  },
  {
    field: 'invoicedQuantity',
    label: '已开票数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  }
])

const loading = ref(true)

const getInfo = async () => {
  loading.value = true
  try {
    let result =
      props.id || pageFlag.value
        ? await PurchaseContractApi.getPurchaseContract({ id: pageId.value })
        : await PurchaseContractApi.getAuditpurchaseContract({ id: pageId.value })
    result.children.forEach((item) => {
      if (item.saleContractItemSaveDTO) {
        item.actualRate =
          getActualRate(
            item.saleContractItemSaveDTO,
            item.currencySaleContract,
            item.withTaxPrice,
            item.quantity
          ) +
          ' ' +
          '%'
      }
    })
    puechaseContractDetail.value = result
  } finally {
    loading.value = false
  }
}

const print = async () => {
  const url = await PurchaseContractApi.print({ id: pageId.value, reportCode: 'purchase-contract' })
  openPdf(url)
}

const saveDesignDraft = async () => {
  await PurchaseContractApi.updateDesign({
    id: puechaseContractDetail.value.id,
    designDraftList: puechaseContractDetail.value.designDraftList
  })
  message.success('保存成功')
}
const OtherActionComRef = ref()
const setBtns = () => {
  clearDialogActions()
  if (puechaseContractDetail.value.confirmFlag !== 0) {
    if (
      checkPermi(['scm:purchase-contract:check']) &&
      ((puechaseContractDetail.value.autoFlag != 1 &&
        [PurchaseContractStatusEnum.EXPECTING_DELIVERY.status].includes(
          puechaseContractDetail.value.contractStatus
        )) ||
        puechaseContractDetail.value.autoFlag == 1) &&
      puechaseContractDetail.value.changeStatus != 2
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'checkContract',
              params: puechaseContractDetail.value
            })
          }}
        >
          转验货单
        </el-button>
      )
    }

    if (
      checkPermi(['scm:purchase-contract:produced']) &&
      !puechaseContractDetail.value.produceCompleted &&
      [PurchaseContractStatusEnum.EXPECTING_DELIVERY.status].includes(
        puechaseContractDetail.value.contractStatus
      ) &&
      puechaseContractDetail.value.changeStatus != 2
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'produceCompleted',
              params: puechaseContractDetail.value
            })
          }}
        >
          生产完成
        </el-button>
      )
    }

    if (puechaseContractDetail.value?.contractStatus === 5) {
      // if (checkPermi(['scm:purchase-contract:auxiliary'])) {
      //   updateDialogActions(
      //     <el-button
      //       onClick={async () => {
      //         emit('handle', {
      //           type: 'toAuxiliaryPlan',
      //           params: puechaseContractDetail.value?.children
      //         })
      //       }}
      //     >
      //       下推包材采购计划
      //     </el-button>
      //   )
      // }
      if (
        [1, 2].includes(puechaseContractDetail.value?.convertNoticeFlag) &&
        puechaseContractDetail.value?.produceCompleted &&
        checkPermi(['scm:purchase-contract:warehousing'])
      ) {
        updateDialogActions(
          <el-button
            onClick={async () => {
              OtherActionComRef.value.detailAction({
                type: 'toWareHouse',
                params: puechaseContractDetail.value?.children
              })
            }}
          >
            转入库通知单
          </el-button>
        )
      }
    }
    if (
      (puechaseContractDetail.value.autoFlag == 1 ||
        (puechaseContractDetail.value?.contractStatus === 5 &&
          puechaseContractDetail.value.autoFlag != 1)) &&
      checkPermi(['scm:payment-apply:create'])
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'toPaymentApply',
              params: [puechaseContractDetail.value?.id]
            })
          }}
        >
          转付款申请单
        </el-button>
      )
    }
    if (
      checkPermi(['scm:purchase-contract:to-invoice-notice']) &&
      (puechaseContractDetail.value.autoFlag == 1 ||
        (puechaseContractDetail.value.autoFlag != 1 &&
          [PurchaseContractStatusEnum.EXPECTING_DELIVERY.status].includes(
            puechaseContractDetail.value.contractStatus
          ))) &&
      puechaseContractDetail.value.changeStatus != 2 &&
      puechaseContractDetail.value?.invoiceStatus !== ConvertInvoiceFlagEnum.TRANSFERRED.status
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'toOrderNotice',
              params: puechaseContractDetail.value
            })
          }}
        >
          转开票通知
        </el-button>
      )
    }
    if (
      checkPermi(['scm:purchase-contract:change']) &&
      [
        PurchaseContractStatusEnum.AWAITING_ORDER.status,
        PurchaseContractStatusEnum.EXPECTING_DELIVERY.status
      ].includes(puechaseContractDetail.value.contractStatus) &&
      puechaseContractDetail.value.changeStatus != 2
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'changeContract',
              params: puechaseContractDetail.value
            })
          }}
        >
          变更
        </el-button>
      )
    }

    if (checkPermi(['scm:purchase-contract:print'])) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            await print()
          }}
          key="print"
        >
          打印
        </el-button>
      )
    }
    if (
      checkPermi(['scm:purchase-contract:finish']) &&
      [
        PurchaseContractStatusEnum.READY_TO_SUBMIT.status,
        PurchaseContractStatusEnum.REJECTED.status,
        PurchaseContractStatusEnum.AWAITING_ORDER.status,
        PurchaseContractStatusEnum.EXPECTING_DELIVERY.status
      ].includes(puechaseContractDetail.value.contractStatus) &&
      puechaseContractDetail.value.changeStatus != 2
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'scmPurchaseContractFinish',
              params: puechaseContractDetail.value
            })
          }}
        >
          作废
        </el-button>
      )
    }
    if (
      checkPermi(['scm:purchase-contract:sign-back']) &&
      puechaseContractDetail.value.signBackFlag === 0 &&
      [PurchaseContractStatusEnum.AWAITING_ORDER.status].includes(
        puechaseContractDetail.value.contractStatus
      ) &&
      puechaseContractDetail.value.changeStatus != 2
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'signBackContract',
              params: puechaseContractDetail.value
            })
          }}
        >
          回签
        </el-button>
      )
    }

    if (
      checkPermi(['scm:purchase-contract:exchange']) &&
      [PurchaseContractStatusEnum.FINISHED.status].includes(
        puechaseContractDetail.value.contractStatus
      ) &&
      puechaseContractDetail.value.changeStatus != 2 &&
      puechaseContractDetail.value.autoFlag != 1
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'exchangeContract',
              params: puechaseContractDetail.value
            })
          }}
        >
          退换货
        </el-button>
      )
    }
    if (
      puechaseContractDetail.value?.contractStatus === 5 &&
      checkPermi(['scm:purchase-contract:auxiliary'])
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            OtherActionComRef.value.detailAction({
              type: 'toAuxiliaryPlan',
              params: puechaseContractDetail.value?.children
            })
          }}
        >
          下推包材采购计划
        </el-button>
      )
    }
  }

  if (checkPermi(['scm:purchase-contract:export'])) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await exportWord()
        }}
        key="exportWord"
      >
        导出
      </el-button>
    )
  }
}
const updateContractCodeRef = ref()
const updateContractCode = () => {
  updateContractCodeRef.value.open(puechaseContractDetail.value)
}
// const exportExcel = async () => {
//   try {
//     // 导出的二次确认
//     await message.exportConfirm()
//     // 发起导出
//     const data = await PurchaseContractApi.exportPurchaseContractDetail({
//       id: pageId.value,
//       reportCode: 'purchase-contract-export'
//     })
//     if (data && data.size) {
//       download.excel(data, '采购合同.xlsx')
//     }
//   } catch {}
//   return
// }

const exportWord = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await PurchaseContractApi.exportPurchaseContractDetailWord({
      id: pageId.value,
      reportCode: 'purchase-contract'
    })
    if (data && data.size) {
      download.word(data, puechaseContractDetail.value?.code + '.docx')
    }
  } catch {}
  return
}

const updatePaymentPlanRef = ref()
const handleUpdatePaymentPlan = () => {
  updatePaymentPlanRef.value.open(puechaseContractDetail.value)
}

const pageRefresh = async () => {
  await getInfo()
  setBtns()
}

defineExpose({
  pageRefresh
})
const totalDes = ref('')
const getTotalDes = () => {
  let cskuCodeList = []
  let quantitys = 0
  let giftNum = 0
  let purchaseMoney = 0
  puechaseContractDetail.value.children.forEach((item) => {
    cskuCodeList.push(item.cskuCode)
    quantitys += item.quantity
    giftNum += Number(item.freeQuantity)
    // purchaseMoney.amount += item.quantity * item.withTaxPrice.amount
    // purchaseMoney.currency = item.withTaxPrice.currency
    purchaseMoney += (item.quantity - Number(item.freeQuantity)) * item.withTaxPrice.amount
  })

  let rowRate = rateList[puechaseContractDetail.value.children[0]?.currency]

  totalDes.value = `客户产品种类:${[...new Set(cskuCodeList)].length} 采购总量:${formatNum(quantitys)} 赠品:${giftNum} 含税总金额:${formatNum(purchaseMoney)} 采购总金额(RMB):${formatNum(purchaseMoney * rowRate)}`
}
const goEdit = () => {
  OtherActionComRef.value.handleUpdate(pageId.value, 'edit')
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
  await getTaxTypeList()
  await getInfo()
  getTotalDes()
  setBtns()
})
</script>
<style lang="scss" scoped>
.el-tabs__content {
  :deep(.el-tabs__nav) {
    z-index: 0;
  }
}

:deep(.mytab) {
  text-align: left;
  width: 110px;
  font-weight: bold;
}
</style>
