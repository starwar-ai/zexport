<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="purchasePlanDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'scm:purchase-plan:create',
      handler: async () => {
        await PurchasePlanApi.submitPurchasePlan(purchasePlanDetail.id)
        message.success('提交成功')
        close()
      }
    }"
    :cancel="{
      permi: 'scm:purchase-plan:submit',
      handler: () => {},
      user: purchasePlanDetail?.creator
    }"
    :edit="{
      permi: 'scm:purchase-plan:update',
      handler: () => goEdit('采购计划'),
      user: purchasePlanDetail?.creator
    }"
    :approve="{
      permi: 'scm:purchase-plan:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="采购计划信息"
      :data="purchasePlanDetail"
      :items="puechasePlanSchemas"
    >
      <template #companyCurrencyList>
        {{ getDictLabels(DICT_TYPE.CURRENCY_TYPE, purchasePlanDetail?.companyCurrencyList) }}
      </template>
    </eplus-description>

    <eplus-description
      title="附件信息"
      :data="purchasePlanDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <el-tag
          type="primary"
          v-for="item in purchasePlanDetail.annex"
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
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="产品信息"
        name="first"
      >
        <Table
          :columns="productTableColumns"
          headerAlign="center"
          align="center"
          :data="purchasePlanDetail?.children"
        />
      </el-tab-pane>
      <el-tab-pane
        label="关联单据"
        name="second"
      >
        <!-- <Table
          :columns="relateTableColumns"
          headerAlign="center"
          align="center"
          :data="purchasePlanDetail?.contractList"
        /> -->
        <RelateTable :data="purchasePlanDetail" />
      </el-tab-pane>
      <el-tab-pane
        label="操作记录"
        name="third"
      >
        <eplus-operate-log :logList="purchasePlanDetail?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/purchase-plan'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { DICT_TYPE, getDictLabels } from '@/utils/dict'
import { columnWidth, formatDictColumn, formatMoneyColumn, formatNumColumn } from '@/utils/table'
import { PurchasePlanStatusEnum, RelatedOrdersTypeEnum } from '@/utils/constants'
import RelateTable from '@/components/RelateTable/src/RelateTable.vue'
import { checkPermi } from '@/utils/permission'
import { isValidArray } from '@/utils/is'
import { formatNum } from '@/utils'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'

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

const emit = defineEmits(['createContract'])
const activeName = ref('first')
const purchasePlanDetail = ref({})
//采购计划信息
const puechasePlanSchemas = reactive([
  {
    field: 'code',
    label: '计划编号'
  },
  {
    field: 'companyName',
    label: '采购主体'
  },
  {
    slotName: 'companyCurrencyList',
    field: 'companyCurrencyList',
    label: '可用币种'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'planStatus',
    label: '单据状态',
    dictType: DICT_TYPE.PURCHASE_PLAN_STATUS
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
    field: 'creatorName',
    label: '创建人'
  },
  {
    field: 'creatorDeptName',
    label: '创建人部门'
  },
  {
    field: 'planDate',
    label: '创建日期',
    type: 'date'
  },
  {
    field: 'estDeliveryDate',
    label: '预计交期',
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
    label: '',
    slotName: 'annex',
    span: 1
  }
]
const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}
const exportSaleColumns = reactive([
  {
    prop: 'code',
    label: '销售合同',
    parent: true
  },
  {
    prop: 'companyName',
    label: '下单主体',
    parent: true
  },
  {
    prop: 'custName',
    label: '客户名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'skuCode',
    label: '产品编码',
    minWidth: '180px'
  },
  {
    prop: 'name',
    label: '中文名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'nameEng',
    label: '英文名称',
    minWidth: columnWidth.l
  },
  {
    prop: 'mainPicture',
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
    prop: 'quantity',
    label: '数量',
    minWidth: columnWidth.l
  },
  {
    prop: 'inventoryQuantity',
    label: '库存',
    minWidth: columnWidth.l
  },
  {
    prop: 'purchaseQuantity',
    label: '应采购',
    minWidth: columnWidth.l
  }
])
const PurcahseContractColumns = reactive([
  {
    prop: 'code',
    label: '采购合同单号',
    isCopy: true,
    parent: true,
    minWidth: '220px'
  },
  {
    prop: 'companyName',
    label: '采购主体',
    parent: true,
    minWidth: '220px'
  },
  {
    prop: 'custName',
    label: '客户名称',
    parent: true,
    minWidth: columnWidth.l,
    formatter: (row) => {
      return row.custName == null ? '' : row.custName
    }
  },
  {
    prop: 'mainPicture',
    label: '图片',
    // fixed: 'left',
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
    prop: 'skuCode',
    label: '产品编号',
    isCopy: true,
    minWidth: '180px'
  },
  {
    prop: 'skuName',
    label: '品名',
    minWidth: columnWidth.l
  },
  {
    prop: 'venderProdCode',
    label: '工厂货号',
    isCopy: true,
    minWidth: columnWidth.l
  },
  {
    prop: 'quantity',
    label: '采购数量',
    summary: true,
    minWidth: columnWidth.l
  }
])
//关联单据table
let relateList = {
  beforeList: [
    {
      title: RelatedOrdersTypeEnum.SALES_CONTRACT.name,
      relatedType: RelatedOrdersTypeEnum.SALES_CONTRACT.status,
      schemas: exportSaleColumns,
      type: 'before',
      name: 'exportSale'
    }
  ],
  afterList: [
    {
      title: RelatedOrdersTypeEnum.PURCHASE_CONTRACT.name,
      relatedType: RelatedOrdersTypeEnum.PURCHASE_CONTRACT.status,
      schemas: PurcahseContractColumns,
      type: 'after',
      name: 'purcahseContract',
      changeFlag: true
    }
  ],
  params: () => {
    return {
      linkCode: purchasePlanDetail.value?.linkCode
    }
  }
}

//产品信息table
let productTableColumns = reactive([
  // {
  //   field: 'sortNum',
  //   label: '序号',
  //   width: '80px'
  // },
  {
    field: 'convertedFlag',
    label: '转合同状态',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.CONVERTED_FLAG)
  },
  {
    field: 'mainPicture',
    label: '图片',
    minWidth: '80px',
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
    label: '品名',
    minWidth: columnWidth.l,
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
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '250px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.skuDeletedFlag) {
          return (
            <div>
              <span>{row.skuCode}</span>
              <el-tag
                type="info"
                effect="dark"
                size="small"
                class="ml10px"
              >
                被修改
              </el-tag>
            </div>
          )
        } else {
          return <span>{row.skuCode}</span>
        }
      }
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    width: columnWidth.xl
  },
  {
    field: 'needPurQuantity',
    label: '待采购数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'purchaseQuantity',
    label: '采购数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'convertedQuantity',
    label: '已转合同数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'withTaxPrice',
    label: '采购价',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: columnWidth.l
  },
  {
    field: 'purchaseUserDeptName',
    label: '采购员部门',
    width: columnWidth.l
  },
  {
    field: 'purchaseType',
    label: '采购类型',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.PURCHASE_TYPE)
  },
  {
    field: 'qtyPerOuterbox',
    label: '单箱数量',
    width: columnWidth.l,
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
    field: 'currentLockQuantity',
    label: '锁定库存',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return row.currentLockQuantity || 0
      }
    }
  },
  {
    field: 'levels',
    label: '拆分等级',
    width: columnWidth.l
  }
  // {
  //   field: 'venderProdCode',
  //   label: '工厂货号',
  //   width: columnWidth.l
  // },
  // {
  //   field: 'saleQuantity',
  //   label: '销售数量',
  //   width: columnWidth.l
  // },
])

const handleRevertAudit = async (id) => {
  let res = await PurchasePlanApi.planRevertAudit(id)
  if (res) {
    message.success('返审核成功！')
  }
  handleUpdate()
}

const handleCreateContract = async () => {
  close()
  emit('createContract', purchasePlanDetail.value)
}

const handleClick = (val) => {
  // console.log(val)
}
const loading = ref(true)
const handleUpdate = async () => {
  if (props.id || query?.id) {
    await getInfo()
    setBtns()
  }
}

const getInfo = async () => {
  loading.value = true
  try {
    purchasePlanDetail.value = props.id
      ? await PurchasePlanApi.getPurchasePlan({ id: props?.id })
      : await PurchasePlanApi.getAuditpurchasePlan({ id: query?.id })
    // let relateNum = await PurchasePlanApi.getPlanRelateNum({ id: props?.id })
  } finally {
    loading.value = false
  }
}

const handleFinish = async () => {
  await message.confirm('是否确认作废？')
  await PurchasePlanApi.finishPurchasePlan([purchasePlanDetail.value.id])
  message.success('作废成功！')
}

const checkCreateContract = (row) => {
  return isValidArray(row.children.filter((item) => item.needPurQuantity > item.convertedQuantity))
}

const setBtns = () => {
  if (
    [PurchasePlanStatusEnum.UNPURCHASE.status].includes(purchasePlanDetail.value?.planStatus) &&
    checkPermi(['scm:purchase-plan:contract']) &&
    checkCreateContract(purchasePlanDetail.value)
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleCreateContract()
        }}
        key="updatePlan"
      >
        生成采购合同
      </el-button>
    )
  }
  if (
    [
      PurchasePlanStatusEnum.UNPURCHASE.status,
      PurchasePlanStatusEnum.REJECTED.status,
      PurchasePlanStatusEnum.COMPLETED.status
    ].includes(purchasePlanDetail.value?.planStatus) &&
    checkPermi(['scm:purchase-plan:finish'])
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleFinish()
        }}
        key="scmPurchasePlanFinish"
      >
        作废
      </el-button>
    )
  }
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('采购计划ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query?.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getInfo()

  setBtns()
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
