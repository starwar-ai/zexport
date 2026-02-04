<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="PurchaseChangeApi.approveChange"
    :rejectApi="PurchaseChangeApi.rejectChange"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
    :cancel="{
      permi: 'scm:purchase-contract:change',
      handler: () => {}
    }"
    :approve="{
      permi: 'scm:purchase-contract-change:audit',
      handler: () => {}
    }"
  >
    <eplus-description
      title="采购合同信息"
      :data="pageInfo"
      :items="basicInfo"
      oldChangeField="oldData"
    >
      <template #deliveryDate>
        <div v-if="pageInfo.deliveryDate == pageInfo.oldData.deliveryDate">
          {{ formatDate(pageInfo?.deliveryDate, 'YYYY-MM-DD') }}
        </div>
        <div v-else>
          <span class="newVal mr5">{{ formatDate(pageInfo?.deliveryDate, 'YYYY-MM-DD') }}</span>
          <span class="oldVal">{{ formatDate(pageInfo?.oldData.deliveryDate, 'YYYY-MM-DD') }}</span>
        </div>
      </template>
      <template #singleNetweight>
        {{ pageInfo?.singleNetweight?.weight }} {{ pageInfo?.singleNetweight?.unit }}
      </template>
      <template #freight>
        <span>{{ amountFormat(pageInfo.freight) }}</span>
      </template>
      <template #otherCost>
        <span>{{ amountFormat(pageInfo.otherCost) }}</span>
      </template>
      <template #payedAmount>
        <span>{{ amountFormat(pageInfo.payedAmount) }}</span>
      </template>
      <template #prepayAmount>
        <span>{{ amountFormat(pageInfo.prepayAmount) }}</span>
      </template>
      <!-- <template #managerNickName>
        <span>
          {{ pageInfo.manager?.nickname ? pageInfo.manager.nickname : '-' }}
        </span>
      </template> -->
    </eplus-description>
    <eplus-description
      title="附件信息"
      :data="pageInfo"
      :items="annexSchemas"
    >
      <template #annex>
        <!-- <UploadList
          :fileList="pageInfo.annex"
          disabled
        /> -->
        <span
          v-for="item in pageInfo.annex"
          :key="item?.id"
          class="mb5px mr5px"
        >
          <el-tag
            v-if="item.info == 'new' && item.name"
            type="warning"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <el-tag
            v-else-if="item.info == 'old' && item.name"
            type="info"
            class="oldVal"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <span v-else>
            <el-tag
              v-if="item.name"
              style="cursor: pointer"
              @click="handleDownload(item)"
              >{{ item.name }}</el-tag
            >
          </span>
        </span>
      </template>
      <template #designDraftList>
        <!-- <UploadList
          :fileList="pageInfo.designDraftList"
          @success="
            (data) => {
              pageInfo.designDraftList = data
            }
          "
        /> -->
        <span
          v-for="item in pageInfo.designDraftList"
          :key="item?.id"
          class="mb5px mr5px"
        >
          <el-tag
            v-if="item.info == 'new' && item.name"
            type="warning"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <el-tag
            v-else-if="item.info == 'old' && item.name"
            type="info"
            class="oldVal"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <span v-else>
            <el-tag
              v-if="item.name"
              style="cursor: pointer"
              @click="handleDownload(item)"
              >{{ item.name }}</el-tag
            >
          </span>
        </span>
      </template>
      <template #signBackAnnexList>
        <UploadList
          :fileList="pageInfo.signBackAnnexList"
          disabled
        />
      </template>
    </eplus-description>
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
    >
      <el-tab-pane
        label="采购合同信息"
        name="1"
      >
        <el-radio-group
          v-model="unitRadio"
          class="mb10px"
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
          :data="pageInfo?.children"
        />
      </el-tab-pane>
      <el-tab-pane
        label="包材分摊"
        name="2"
      >
        <AllocationList
          :info="puechaseContractDetail"
          :actionFlag="outDialogFlag"
          @get-info="getInfo"
        />
      </el-tab-pane>
      <el-tab-pane
        label="加/减项"
        name="3"
      >
        <Table
          v-if="pageInfo?.purchaseAddSubTermList"
          :columns="subItemTableColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.purchaseAddSubTermList"
        />
      </el-tab-pane>
      <el-tab-pane
        label="付款计划信息"
        name="4"
      >
        <Table
          v-if="pageInfo?.purchasePaymentPlanList?.length"
          :columns="purchasePaymentPlanColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.purchasePaymentPlanList"
        />
      </el-tab-pane>
      <el-tab-pane
        label="操作记录"
        name="5"
      >
        <eplus-operate-log :logList="pageInfo?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
    <!-- <OtherDetail :info="pageInfo" /> -->
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as PurchaseChangeApi from '@/api/scm/purchaseChange'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { columnWidth } from '@/utils/table'
// import OtherDetail from './OtherDetail.vue'
import { formatNum } from '@/utils/index'
import { formatDate } from '@/utils/formatTime'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import AllocationList from '../contract/AllocationList.vue'
import { cloneDeep } from 'lodash-es'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import { getActualRate } from '../components/getActualRate'

// const message = useMessage()
const pageInfo = ref<typeObj>({})
const activeName = ref('1')
const outDialogFlag = ref(false)
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)

const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()
defineOptions({ name: 'ChangeDetail' })
//定义edit事件
// const { close, goEdit } = props.id
//   ? (inject('dialogEmits') as {
//       close: () => void
//       goEdit: (val) => void
//     })
//   : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()

const unitRadio = ref('metric')

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await PurchaseChangeApi.getChangeInfo({ id: props.id })
      : await PurchaseChangeApi.getChangeAuditInfo({ id: query?.id })
    setOldSku(pageInfo.value.children, pageInfo.value.oldData.children, 'skuName')
    pageInfo.value.annex = changeData(pageInfo.value.annex, pageInfo.value.oldData.annex)
    pageInfo.value.designDraftList = changeData(
      pageInfo.value?.designDraftList,
      pageInfo.value.oldData?.designDraftList
    )
    console.log(pageInfo.value)
  } finally {
    loading.value = false
  }
}
const changeData = (newData, oldData) => {
  if (newData && oldData) {
    const newArr = newData?.filter((obj1) => !oldData?.some((obj2) => obj1.name === obj2.name))
    newArr?.forEach((obj) => (obj.info = 'new'))

    const oldArr = oldData?.filter((obj2) => !newData?.some((obj1) => obj1.name === obj2.name))
    oldArr?.forEach((obj) => (obj.info = 'old'))
    newData = [...newData, ...oldArr]
  }
  return newData
}

const setOldSku = (newSku, oldData, typeStr) => {
  if (newSku != null && oldData != null) {
    newSku.forEach((e, i) => {
      e.changeFlag = false
      // 新的外箱规格
      e.outerboxSpec = getOuterbox(e, 'spec', unitRadio.value)

      e.actualRate = `${getActualRate(
        e.saleContractItemSaveDTO,
        e.currencySaleContract,
        e.withTaxPrice,
        e.quantity
      )} %`

      e.invoicedAmount = {
        amount: e.withTaxPrice.amount * e.invoicedQuantity,
        currency: e.withTaxPrice.currency
      }

      e.withTotalTaxPrice = {
        amount: e.quantity * e.withTaxPrice?.amount || 0,
        currency: e.withTaxPrice?.currency
      }

      oldData.forEach((item, index) => {
        // 旧的外箱规格
        item.outerboxSpec = getOuterbox(item, 'spec', unitRadio.value)
        item.actualRate = `${getActualRate(
          item.saleContractItemSaveDTO,
          item.currencySaleContract,
          item.withTaxPrice,
          item.quantity
        )} %`
        item.invoicedAmount = {
          amount: item.withTaxPrice.amount * item.invoicedQuantity,
          currency: item.withTaxPrice.currency
        }
        item.withTotalTaxPrice = {
          amount: item.quantity * item.withTaxPrice?.amount || 0,
          currency: item.withTaxPrice?.currency
        }
        if (i == index) {
          e.oldData = item
        }
      })
    })
    const onlyInOldSku = oldData.filter((oldObj) => {
      if (typeStr === 'skuName') {
        return !newSku.some((newObj) => newObj?.name === oldObj?.name)
      }
    })
    if (onlyInOldSku.length) {
      onlyInOldSku.forEach((obj) => {
        let cloneObj = cloneDeep(obj)
        // 对象属性值为空，则只展示删除的旧数据
        for (const key in cloneObj) {
          cloneObj[key] = '  '
        }
        cloneObj.changeFlag = false
        cloneObj.oldData = obj
        newSku.push(cloneObj)
      })
    }
  }
}

const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}

const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'companyName',
    label: '采购主体'
  },
  {
    field: 'sourceCode',
    label: '采购合同号'
  },

  {
    field: 'code',
    label: '变更单号'
  },
  {
    field: 'contractStatus',
    label: '单据状态',
    dictType: DICT_TYPE.PURCHASE_CONTRACT_STATUS
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    isCompare: true
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
    field: 'manager',
    label: '跟单员',
    isCompare: true
  },
  {
    field: 'venderCode',
    label: '供应商号',
    isCompare: true
  },
  {
    field: 'venderName',
    label: '供应商名称',
    isCompare: true
  },
  {
    field: 'paymentName',
    label: '付款方式',
    isCompare: true
  },
  {
    field: 'deliveryDate',
    label: '交货日期',
    slotName: 'deliveryDate'
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
    field: 'taxType',
    label: '发票类型',
    dictType: DICT_TYPE.TAX_TYPE
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
    label: '客户名称',
    isCompare: true
  },
  {
    field: 'freight',
    label: '运费',
    isCompare: true
  },
  {
    field: 'otherCost',
    label: '其他费用',
    isCompare: true
  },
  {
    field: 'signBackTime',
    label: '回签日期',
    type: 'date',
    isCompare: true
  },
  {
    field: 'boxWithColor',
    label: '箱带颜色',
    // dictType: DICT_TYPE.BOX_WITH_COLOR,
    isCompare: true,
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.BOX_WITH_COLOR, val)
    }
  },
  {
    field: 'sampleCount',
    label: '样品套数',
    isCompare: true
  },
  {
    field: 'remark',
    label: '备注',
    isCompare: true,
    xl: 8,
    lg: 12
  }
]

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
const productTableColumns = reactive([
  // {
  //   field: 'sortNum',
  //   label: '序号',
  //   minWidth: columnWidth.m
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
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="skuName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  // {
  //   field: 'planArriveDate',
  //   label: '到料日期',
  //   minWidth: columnWidth.l,
  //   formatter: formatDateColumn('YYYY-MM-DD')
  // },
  {
    field: 'cskuCode',
    label: '客户货号',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <EplusFieldComparison
          filed="cskuCode"
          oldChangeField="oldData"
          item={row}
        />
      )
    }
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    minWidth: columnWidth.xl,
    formatter: (row) => {
      return (
        <EplusFieldComparison
          filed="oskuCode"
          oldChangeField="oldData"
          item={row}
        />
      )
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <EplusFieldComparison
          filed="basicSkuCode"
          oldChangeField="oldData"
          item={row}
        />
      )
    }
  },
  // {
  //   field: 'skuCode',
  //   label: '产品编号',
  //   minWidth: columnWidth.l,
  //   formatter: (row) => {
  //     return (
  //       <>
  //         <EplusFieldComparison
  //           filed="skuCode"
  //           oldChangeField="oldData"
  //           item={row}
  //         />
  //       </>
  //     )
  //   }
  // },
  // {
  //   field: 'venderProdCode',
  //   label: '工厂货号',
  //   minWidth: '175px',
  //   formatter: (row) => {
  //     return (
  //       <>
  //         <EplusFieldComparison
  //           filed="venderProdCode"
  //           oldChangeField="oldData"
  //           item={row}
  //         />
  //       </>
  //     )
  //   }
  // },
  // {
  //   field: 'purchaseType',
  //   label: '采购类型',
  //   minWidth: columnWidth.l,
  //   showOverflowTooltip: false,
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return (
  //         <EplusFieldComparison
  //           filed="purchaseType"
  //           oldChangeField="oldData"
  //           formatter={(val) => {
  //             return getDictLabel(DICT_TYPE.PURCHASE_TYPE, val)
  //           }}
  //           item={row}
  //         />
  //       )
  //     }
  //   }
  // },
  // {
  //   field: 'freeFlag',
  //   label: '是否赠品',
  //   minWidth: columnWidth.l,
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return (
  //         <EplusFieldComparison
  //           filed="freeFlag"
  //           oldChangeField="oldData"
  //           formatter={(val) => {
  //             return getDictLabel(DICT_TYPE.IS_INT, val)
  //           }}
  //           item={row}
  //         />
  //       )
  //     }
  //   }
  // },
  {
    field: 'quantity',
    label: '采购数量',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="quantity"
            oldChangeField="oldData"
            item={row}
          />
        </>
      )
    }
  },
  {
    field: 'withTaxPrice',
    label: '含税单价',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="withTaxPrice"
            oldChangeField="oldData"
            item={row}
          />
        </>
      )
    }
  },
  {
    field: 'taxRate',
    label: '税率',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="taxRate"
            oldChangeField="oldData"
            formatter={(val) => {
              return val ? val + '%' : '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outerboxSpec',
    label: '外箱规格',
    minWidth: columnWidth.xxl,
    formatter: (row) => {
      return (
        <EplusFieldComparison
          filed="outerboxSpec"
          oldChangeField="oldData"
          item={row}
        />
      )
    }
  },
  {
    field: 'description',
    label: '中文描述',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <EplusFieldComparison
          filed="description"
          oldChangeField="oldData"
          item={row}
        />
      )
    }
  },
  {
    field: 'freeFlag',
    label: '是否含赠品',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="freeFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'freeQuantity',
    label: '赠品数量',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="freeQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'syncQuoteFlag',
    label: '是否同步产品报价',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="syncQuoteFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'packageFlag',
    label: '是否含包装',
    minWidth: columnWidth.xl,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="packageFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'packagingPrice',
    label: '包装价',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="packagingPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="packageTypeName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },

  {
    field: 'freightFlag',
    label: '是否含运费',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="freightFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'shippingPrice',
    label: '运费单价',
    width: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="shippingPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'qtyPerOuterbox',
    label: '单箱数量',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="qtyPerOuterbox"
            oldChangeField="oldData"
            item={row}
          />
        </>
      )
    }
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
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="boxCount"
            oldChangeField="oldData"
            formatter={(val) => {
              return formatNum(val * (row.specificationList?.length || 1))
            }}
            item={row}
          />
        </>
      )
    }
  },
  {
    field: 'purchaseType',
    label: '采购类型',
    minWidth: columnWidth.m,
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="purchaseType"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.PURCHASE_TYPE, val)
            }}
            item={row}
          />
        </>
      )
    }
  },
  {
    field: 'billStatus',
    label: '入库状态',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="billStatus"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.SALE_ITEM_BILL_STATUS, val)
            }}
            item={row}
          />
        </>
      )
    }
  },
  {
    field: 'unNoticedquantity',
    label: '待转入库通知数量',
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="unNoticedquantity"
            oldChangeField="oldData"
            formatter={(val) => {
              let valNum = Number(row.quantity) - Number(row.noticedQuantity) || 0
              return val > 0 ? formatNum(valNum) : 0
            }}
            item={row}
          />
        </>
      )
    },
    minWidth: columnWidth.xl
  },
  {
    field: 'billQuantity',
    label: '实际入库数量',
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="billQuantity"
            oldChangeField="oldData"
            item={row}
          />
        </>
      )
    },
    minWidth: columnWidth.l
  },
  {
    field: 'checkStatus',
    label: '验货结果',
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="checkStatus"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, val)
            }}
            item={row}
          />
        </>
      )
    },
    minWidth: columnWidth.l
  },
  {
    field: 'actualRate',
    label: '实际利润率',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <EplusFieldComparison
          filed="actualRate"
          oldChangeField="oldData"
          item={row}
        />
      )
    }
  },
  {
    field: 'invoiceStatus',
    label: '开票状态',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="invoiceStatus"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.INVOICE_STATUS, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'invoicedAmount',
    label: '已开票金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="invoicedAmount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'invoicedQuantity',
    label: '已开票数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="invoicedQuantity"
            oldChangeField="oldData"
            formatter={(val) => {
              return val === 0 ? '0' : val
            }}
            item={row}
          />
        )
      }
    }
  }
])
const subItemTableColumns = reactive([
  {
    field: 'contractCode',
    label: '采购合同',
    width: '180px',
    fixed: 'left',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="contractCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'calculationType',
    label: '加/减项',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="calculationType"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.CALCULATION_TYPE, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'feeName',
    label: '费用名称',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="feeName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'amount',
    label: '金额',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="amount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  }
])
const purchasePaymentPlanColumns = [
  {
    field: 'step',
    label: '步骤',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="step"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'dateType',
    label: '起始点',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="dateType"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.DATE_TYPE, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'startDate',
    label: '起始日',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="startDate"
            oldChangeField="oldData"
            formatter={(val) => {
              return formatDate(val, 'YYYY-MM-DD')
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'days',
    label: '天数',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="days"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'expectedReceiptDate',
    label: '预计付款日',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="expectedReceiptDate"
            oldChangeField="oldData"
            formatter={(val) => {
              return formatDate(val, 'YYYY-MM-DD')
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'collectionRatio',
    minWidth: columnWidth.l,
    label: '付款比例%',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="collectionRatio"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'receivableAmount',
    label: '应付金额',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="receivableAmount"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.amount ? `${val.amount} ${val.currency}` : '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'receivedAmount',
    label: '实收金额',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="receivedAmount"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.amount ? `${val.amount} ${val.currency}` : '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'exeStatus',
    label: '状态',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="exeStatus"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.EXECUTE_STATUS, val)
            }}
            item={row}
          />
        )
      }
    }
  }
]
const handleUpdate = async () => {
  await getPageInfo()
}

onMounted(async () => {
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
<style lang="scss" scoped>
.newVal {
  color: #f7aa49;
}

.oldVal {
  color: #999;
  text-decoration: line-through;
}
</style>
