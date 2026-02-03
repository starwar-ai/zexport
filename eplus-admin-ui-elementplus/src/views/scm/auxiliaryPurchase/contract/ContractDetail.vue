<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="puechaseContractDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'scm:auxiliary-purchase-contract:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'scm:auxiliary-purchase-contract:update',
      handler: () => goEdit('采购合同')
    }"
    :approve="{
      permi: 'scm:auxiliary-purchase-contract:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="采购合同信息"
      :data="puechaseContractDetail"
      :items="puechaseContractSchemas"
    >
      <template #freight>
        <span>{{ amountFormat(puechaseContractDetail.freight) }}</span>
      </template>
      <template #otherCost>
        <span>{{ amountFormat(puechaseContractDetail.otherCost) }}</span>
      </template>
      <template #payedAmount>
        <span>{{ amountFormat(puechaseContractDetail.payedAmount) }}</span>
      </template>
      <template #prepayAmount>
        <span>{{ prepayAmountSum }}</span>
      </template>
      <template #totalAmount>
        <span>{{ amountFormat(puechaseContractDetail.totalAmount) }}</span>
      </template>
      <template #venderPoc>
        <span>{{
          puechaseContractDetail?.venderPoc?.name ? puechaseContractDetail?.venderPoc?.name : ''
        }}</span>
      </template>
      <template #venderMobile>
        <span>{{
          puechaseContractDetail?.venderPoc?.mobile ? puechaseContractDetail?.venderPoc?.mobile : ''
        }}</span>
      </template>
    </eplus-description>
    <eplus-description
      title="附件信息"
      :data="puechaseContractDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <el-tag
          type="primary"
          v-for="item in puechaseContractDetail.annex"
          :key="item.id"
        >
          <span
            style="cursor: pointer; color: #333"
            @click="handleDownload(item)"
            >{{ item.name }}</span
          >
        </el-tag>
      </template>
    </eplus-description>
    <FeeShareDetail :info="puechaseContractDetail.feeShare" />
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="采购合同信息"
        name="first"
      >
        <Table
          :columns="productTableColumns"
          headerAlign="center"
          align="center"
          :data="puechaseContractDetail?.children"
          :showSummary="true"
          :summaryMethod="getProductSummary"
          :max-height="400"
        />
      </el-tab-pane>
      <el-tab-pane
        label="关联单据"
        name="second"
      >
        <el-tabs
          v-model="activeNameChild"
          class="demo-tabs"
          @tab-click="handleClick"
        >
          <el-tab-pane
            label="采购计划"
            name="first-child"
          >
            <Table
              :columns="planTableColumns"
              headerAlign="center"
              align="center"
              :data="puechaseContractDetail?.purchasePlanList"
            />
          </el-tab-pane>
          <el-tab-pane
            label="变更单"
            name="second-child"
          >
            <Table
              :columns="changeTableColumns"
              headerAlign="center"
              align="center"
              :data="puechaseContractDetail?.relateTable"
            />
          </el-tab-pane>
          <el-tab-pane
            label="入库单"
            name="third-child"
          >
            <Table
              :columns="stockTableColumns"
              headerAlign="center"
              align="center"
              :data="puechaseContractDetail?.relateTable"
            />
          </el-tab-pane>
          <el-tab-pane
            label="验货单"
            name="forth-child"
          >
            <Table
              :columns="checkTableColumns"
              headerAlign="center"
              align="center"
              :data="puechaseContractDetail?.relateTable"
            />
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
      <el-tab-pane
        label="操作记录"
        name="third"
      >
        <eplus-operate-log :logList="puechaseContractDetail?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/purchase-plan'
import * as PurchaseContractApi from '@/api/scm/auxiliaryPurchaseContract'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { SkuTypeEnum } from '@/utils/constants'
import { formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { openPdf } from '@/utils/index'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { h } from 'vue'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'

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

const activeName = ref('first')
const activeNameChild = ref('first-child')
const puechaseContractDetail = ref({})
const amountFormat = (val) => {
  if (!val) return '-'
  if (val.amount && val.currency) {
    return `${val.amount} ${val.currency}`
  } else {
    return '-'
  }
}
//采购计划信息
const puechaseContractSchemas = reactive([
  {
    field: 'code',
    label: '采购单号'
  },
  {
    field: 'companyName',
    label: '公司名称'
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
    field: 'manager',
    label: '跟单员',
    formatter: (val) => {
      return val?.nickname
    }
  },
  // {
  //   field: 'stockName',
  //   label: '采购仓库'
  // },
  {
    field: 'printFlag',
    label: '打印状态',
    dictType: DICT_TYPE.PRINT_FLAG
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
    field: 'venderPoc',
    label: '供应商联系人',
    slotName: 'venderPoc'
  },
  {
    field: 'venderMobile',
    label: '联系人电话',
    slotName: 'venderMobile'
  },
  {
    field: 'currency',
    label: '币种'
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
    field: 'totalAmount',
    label: '含税金额',
    slotName: 'totalAmount'
  },
  {
    field: 'payedAmount',
    label: '已付金额',
    slotName: 'payedAmount'
  },
  {
    field: 'prepayAmount',
    label: '预付金额',
    slotName: 'prepayAmount'
  },
  {
    field: 'freight',
    label: '运费',
    slotName: 'freight'
  },
  {
    field: 'otherCost',
    label: '其他费用',
    slotName: 'otherCost'
  },
  {
    field: 'deliveryAddress',
    label: '送货地址'
  },
  {
    field: 'minimumBaseQuantity',
    label: '最低备品比例%'
  },
  {
    field: 'restockingDeadline',
    label: '乙方补货时限t'
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
    label: '创建日期',
    type: 'date'
  },
  {
    field: 'remark',
    label: '备注'
  }
])
//采购计划附件信息
const annexSchemas = [
  {
    field: 'annex',
    label: '',
    slotName: 'annex',
    span: 24
  }
]
const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}
const formatLength = (length, width, height) => {
  if (length && width && height) {
    return (
      <span>
        <span>{length.toFixed(2) + '*' + width.toFixed(2) + '*' + height.toFixed(2) + 'cm'}</span>
      </span>
    )
  } else {
    return '-'
  }
}
//采购计划产品信息
const productSchemas = [
  {
    field: 'product',
    label: '',
    slotName: 'product',
    span: 24
  }
]
//关联单据table
const planTableColumns = reactive([
  {
    field: 'purchasePlanCode',
    label: '计划单号'
  },
  {
    field: 'skuCode',
    label: '产品编号'
  },
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'mainPicture',
    label: '图片',
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
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'unitPrice',
    label: '单价',
    formatter: (val) => {
      if (val.unitPrice) {
        return `${val.unitPrice.amount} ${val.unitPrice.currency}`
      } else {
        return '-'
      }
    }
  },
  {
    field: 'createTime',
    label: '申请时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
  },
  {
    field: 'quantity',
    label: '计划数量'
  },
  {
    field: 'convertedFlag',
    label: '转合同状态',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CONVERTED_FLAG, row?.convertedFlag) || ''
    }
  }
])
const changeTableColumns = reactive([
  {
    field: 'index',
    label: '序号'
  },
  {
    field: 'index',
    label: '变更单号'
  },
  {
    field: 'index',
    label: '客户产品名称'
  },
  {
    field: 'index',
    label: '采购单号'
  },
  {
    field: 'index',
    label: '物料编号'
  },
  {
    field: 'index',
    label: '物料名称'
  },
  {
    field: 'index',
    label: '型号'
  },
  {
    field: 'index',
    label: '规格'
  },
  {
    field: 'index',
    label: '品牌'
  },
  {
    field: 'index',
    label: '原数量'
  },
  {
    field: 'index',
    label: '新数量'
  },
  {
    field: 'index',
    label: '原单价'
  },
  {
    field: 'index',
    label: '新单价'
  }
])
const stockTableColumns = reactive([
  {
    field: 'index',
    label: '序号'
  },
  {
    field: 'index',
    label: '入库单号'
  },
  {
    field: 'index',
    label: '客户产品名称/SKU'
  },
  {
    field: 'index',
    label: '仓库'
  },
  {
    field: 'index',
    label: '供应商'
  },
  {
    field: 'purchaseUserName',
    label: '采购员'
  },
  {
    field: 'index',
    label: '采购量'
  },
  {
    field: 'index',
    label: '质检量'
  },
  {
    field: 'index',
    label: '良品量'
  },
  {
    field: 'index',
    label: '次品量'
  }
])
const checkTableColumns = reactive([
  {
    field: 'index',
    label: '序号'
  },
  {
    field: 'index',
    label: '质检单号'
  },
  {
    field: 'index',
    label: '客户产品名称/SKU'
  },
  {
    field: 'index',
    label: '仓库'
  },
  {
    field: 'venderName',
    label: '供应商'
  },
  {
    field: 'index',
    label: '质检员'
  },
  {
    field: 'purchaseUserName',
    label: '采购员'
  },
  {
    field: 'index',
    label: '采购量'
  },
  {
    field: 'index',
    label: '质检量'
  },
  {
    field: 'index',
    label: '良品量'
  },
  {
    field: 'index',
    label: '次品量'
  }
])
//产品信息table
let productTableColumns = reactive([
  {
    field: 'sortNum',
    label: '序号',
    width: '80px'
  },
  {
    field: 'auxiliarySkuType',
    label: '包材使用方式',
    width: '150px',
    sortable: true,
    formatter: formatDictColumn(DICT_TYPE.AUXILIARY_PURCHASE_TYPE)
  },
  {
    field: 'mainPicture',
    label: '包材图片',
    minWidth: '150px',
    // formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
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
    label: '包材名称',
    minWidth: '150px',
    sortable: true,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="h-50px flex items-center">
            {/* 普通产品的角标不展示 */}
            <el-badge
              class="item"
              value={getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType).split('')[0] || ''}
              type="primary"
              hidden={row?.skuType === SkuTypeEnum.COMMON.status ? true : false}
            >
              {row?.skuName}
            </el-badge>
          </div>
        )
      }
    }
    // formatter: formatDictColumn(DICT_TYPE.CUSTOM_TYPE, true)
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    width: '150px',
    formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
  },
  {
    field: 'auxiliarySaleContractCode',
    label: '销售合同',
    minWidth: '150px'
  },
  {
    field: 'auxiliaryPurchaseContractCode',
    label: '采购合同',
    minWidth: '150px'
  },
  {
    field: 'venderName',
    label: '供应商名称',
    minWidth: '150px'
  },
  {
    field: 'totalQuantity',
    label: '合同数量',
    minWidth: '150px'
  },
  {
    field: 'auxiliarySkuName',
    label: '品名',
    minWidth: '150px'
  },
  {
    field: 'auxiliaryCskuCode',
    label: '客户货号',
    minWidth: '150px'
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    minWidth: '150px'
  },
  {
    field: 'quantity',
    label: '采购数量',
    minWidth: '150px'
  },
  {
    field: 'withTaxPrice',
    label: '单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'totalAmount',
    label: '总金额',
    minWidth: '150px',
    formatter: (row) => {
      const price = row.withTaxPrice?.amount || 0
      const qty = row.quantity || 0
      const currency = row.withTaxPrice?.currency || ''
      return h(EplusMoneyLabel, { val: { amount: price * qty, currency } })
    }
  },
  {
    field: 'specRemark',
    label: '规格描述',
    minWidth: '150px'
  },
  {
    field: 'annex',
    label: '附件',
    minWidth: '150px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          row?.annex?.length &&
          row?.annex.map((item, index) => {
            return (
              <el-tag key={item.id}>
                <span
                  style="cursor: pointer; color: #333"
                  onClick={() => handleDownload(item)}
                >
                  {item.name}
                </span>
              </el-tag>
            )
          })
        )
      }
    }
  },
  {
    field: 'remark',
    label: '备注',
    minWidth: '150px'
  }
])

const handleClick = (val) => {
  // console.log(val)
}
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
const getInfo = async () => {
  loading.value = true
  try {
    puechaseContractDetail.value = props.id
      ? await PurchaseContractApi.getPurchaseContract({ id: props?.id })
      : await PurchaseContractApi.getAuditpurchaseContract({ id: query?.id })
    updateDialogActions(
      <el-button
        onClick={async () => {
          await print()
        }}
        type="primary"
        key="payment"
      >
        打印
      </el-button>
    )
  } finally {
    loading.value = false
  }
}

const print = async () => {
  const url = await PurchaseContractApi.print({
    id: props?.id,
    reportCode: 'auxiliary-purchase-contract'
  })
  //打开积木报表预览页面
  // window.open(
  //   `${import.meta.env.VITE_BASE_URL}/jmreport/view/${import.meta.env.VITE_PURCHASE_CONTRACT_PRINT_CODE}/?token=` +
  //     getAccessToken() +
  //     `&id=${props?.id}`
  // )
  //poi-tl打印
  // window.open(import.meta.env.VITE_BASE_URL + '/admin-api' + url)

  // let dySrc = '/lib/pdfjs/web/compressed.tracemonkey-pldi-09.pdf'
  // let dySrc = encodeURIComponent(`${import.meta.env.VITE_BASE_URL}/admin-api${url}`)
  // //替换为实际的文档URL
  // const pdfUrl = `/lib/pdfjs/web/viewer.html?file=` + dySrc
  // //pdfUrl是当前页面的pdf文件的URL,第二个参数'_blank'表示在新窗口中打开。
  // window.open(pdfUrl, '_blank')

  openPdf(url)
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('采购合同ID不能为空')
    if (!props.id) {
      close()
    }
  }
  await getInfo()
})

const prepayAmountSum = computed(() => {
  const list = puechaseContractDetail.value?.children || []
  if (!Array.isArray(list) || list.length === 0) return '-'
  let total = 0
  let currency = ''
  list.forEach((item) => {
    const price = item.withTaxPrice?.amount || 0
    const qty = item.quantity || 0
    total += Number(price) * Number(qty)
    if (!currency && item.withTaxPrice?.currency) currency = item.withTaxPrice.currency
  })
  return currency ? `${total} ${currency}` : total
})

// 合计行方法，采购数量、单价、总金额
const getProductSummary = ({ columns, data }) => {
  const sums: (string | number | any)[] = []
  let currency = ''
  for (const row of data) {
    if (row.withTaxPrice?.currency) {
      currency = row.withTaxPrice.currency
      break
    }
  }
  columns.forEach((column, index) => {
    const field = column.property // 兼容 field/prop
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    if (field === 'quantity') {
      sums[index] = data.reduce((total, row) => total + (Number(row.quantity) || 0), 0)
    } else if (field === 'withTaxPrice') {
      // 平均单价
      const prices = data.map((row) => Number(row.withTaxPrice?.amount) || 0).filter(Boolean)
      const avg = prices.length ? prices.reduce((a, b) => a + b, 0) / prices.length : 0
      sums[index] = currency
        ? h(EplusMoneyLabel, { val: { amount: avg, currency } })
        : avg.toFixed(2)
    } else if (field === 'totalAmount') {
      let total = 0
      let totalCurrency = ''
      data.forEach((row) => {
        const price = row.withTaxPrice?.amount || 0
        const qty = row.quantity || 0
        total += Number(price) * Number(qty)
        if (!totalCurrency && row.withTaxPrice?.currency) totalCurrency = row.withTaxPrice.currency
      })
      sums[index] = totalCurrency
        ? h(EplusMoneyLabel, { val: { amount: total, currency: totalCurrency } })
        : total.toFixed(2)
    } else {
      sums[index] = ''
    }
  })
  return sums
}
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
