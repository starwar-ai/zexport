<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="auxiliaryApprove"
    :rejectApi="auxiliaryReject"
    :auditable="puechasePlanDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'scm:auxiliary-purchase-plan:create',
      handler: () => {}
    }"
    :cancel="{
      permi: 'scm:auxiliary-purchase-plan:submit',
      handler: () => {},
      user: puechasePlanDetail.creator
    }"
    :edit="{
      permi: 'scm:auxiliary-purchase-plan:update',
      handler: () => goEdit('采购计划'),
      user: puechasePlanDetail.creator
    }"
    :approve="{
      permi: 'scm:auxiliary-purchase-plan:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="采购计划信息"
      :data="puechasePlanDetail"
      :items="puechasePlanSchemas"
    />
    <eplus-description
      title="附件信息"
      :data="puechasePlanDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <el-tag
          type="primary"
          v-for="item in puechasePlanDetail.annex"
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
          :data="puechasePlanDetail?.children"
          :max-height="430"
        />
      </el-tab-pane>
      <el-tab-pane
        label="关联单据"
        name="second"
      >
        <Table
          :columns="relateTableColumns"
          headerAlign="center"
          align="center"
          :data="puechasePlanDetail?.contractList"
        />
      </el-tab-pane>
      <el-tab-pane
        label="操作记录"
        name="third"
      >
        <eplus-operate-log :logList="puechasePlanDetail?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { auxiliaryApprove, auxiliaryReject } from '@/api/audit/purchase-plan'
import * as PurchasePlanApi from '@/api/scm/auxiliaryPurchasePlan'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatDateColumn, formatDictColumn } from '@/utils/table'
import { SkuTypeEnum } from '@/utils/constants'

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
const activeName = ref('first')
const puechasePlanDetail = ref({})
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
    field: 'estDeliveryDate',
    label: '预计交期',
    type: 'date'
  },
  {
    field: 'planStatus',
    label: '单据状态',
    dictType: DICT_TYPE.PURCHASE_PLAN_STATUS
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
//关联单据table
const relateTableColumns = reactive([
  {
    field: 'code',
    label: '采购单号'
  },
  {
    field: 'mainPicture',
    label: '图片',
    minWidth: '150px',
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
    label: '客户产品名称/SKU'
  },
  {
    field: 'venderName',
    label: '供应商'
  },
  {
    field: 'stockName',
    label: '仓库'
  },
  {
    field: 'totalQuantity',
    label: '采购量'
  },
  {
    field: 'receivedQuantity',
    label: '收货量'
  },
  {
    field: 'checkedQuantity',
    label: '验货量'
  },
  {
    field: 'exchangeQuantity',
    label: '退货量'
  },
  {
    field: 'plannedArrivalTime',
    label: '计划到料时间'
  },
  {
    field: 'purchaseUserName',
    label: '采购员'
  },
  {
    field: 'createTime',
    label: '创建时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
    minWidth: '150px'
  },
  {
    field: 'remark',
    label: '备注'
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
    field: 'needPurQuantity',
    label: '采购数量',
    minWidth: '150px'
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
const getPurchasePlanDetail = () => {}
const handleUpdate = async () => {
  await getPurchasePlanDetail()
}

const getInfo = async () => {
  loading.value = true
  try {
    puechasePlanDetail.value = props.id
      ? await PurchasePlanApi.getPurchasePlan({ id: props?.id })
      : await PurchasePlanApi.getAuditpurchasePlan({ id: query?.id })
  } finally {
    loading.value = false
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
