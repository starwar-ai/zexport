<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'scm:purchase-contract-change:cancel',
      handler: () => {}
    }"
    :approve="{
      permi: 'scm:purchase-contract-change:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="采购合同信息"
      :data="pageInfo"
      :items="puechaseContractSchemas"
      oldChangeField="oldData"
    />
    <eplus-description
      title="附件信息"
      :data="pageInfo"
      :items="annexSchemas"
    >
      <template #annex>
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
    </eplus-description>
    <eplus-description
      title="产品信息"
      :data="pageInfo"
      :items="childrenSchemas"
    >
      <template #children>
        <Table
          :columns="productTableColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.children"
        />
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/purchase-plan'
import * as PurchaseContractApi from '@/api/scm/auxiliaryPurchaseContract'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { SkuTypeEnum } from '@/utils/constants'
import { columnWidth, formatDictColumn } from '@/utils/table'
import { setNewData } from '@/utils/index'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'

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
const { close } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
    })
  : { close: () => {} }

const pageInfo = ref({})

const puechaseContractSchemas = reactive([
  {
    field: 'code',
    label: '采购合同编号'
  },
  {
    field: 'companyName',
    label: '公司名称'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'purchaseUserName',
    label: '采购员'
  },
  {
    field: 'deliveryDate',
    label: '交货日期',
    type: 'date',
    isCompare: true
  },
  {
    field: 'paymentName',
    label: '付款方式',
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
    field: 'remark',
    label: '备注',
    isCompare: true
  }
])

const annexSchemas = [
  {
    field: 'annex',
    label: '',
    slotName: 'annex',
    span: 24
  }
]

const childrenSchemas = [
  {
    field: 'children',
    label: '',
    slotName: 'children',
    span: 24
  }
]

//产品信息table
let productTableColumns = reactive([
  {
    field: 'sortNum',
    label: '序号',
    width: '60px'
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
    minWidth: columnWidth.l,
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
    minWidth: columnWidth.l,
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
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
  },
  {
    field: 'auxiliarySaleContractCode',
    label: '销售合同',
    minWidth: columnWidth.l
  },
  {
    field: 'auxiliaryPurchaseContractCode',
    label: '采购合同',
    minWidth: columnWidth.l
  },
  {
    field: 'auxiliarySkuName',
    label: '品名',
    minWidth: columnWidth.l
  },
  {
    field: 'auxiliaryCskuCode',
    label: '客户货号',
    minWidth: columnWidth.l
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchaseUserName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'quantity',
    label: '采购数量',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="quantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'withTaxPrice',
    label: '单价',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="withTaxPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },

  {
    field: 'specRemark',
    label: '规格描述',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="specRemark"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'annex',
    label: '附件',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return 22
      }
    }
  },
  {
    field: 'remark',
    label: '备注',
    minWidth: columnWidth.l
  }
])

const loading = ref(true)
const handleUpdate = async () => {
  await getInfo()
}

const getInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = await PurchaseContractApi.auxiliaryChangeInfo(props?.id)
    pageInfo.value.annex = changeData(pageInfo.value.annex, pageInfo.value.oldData.annex)
    setNewData(pageInfo.value.children, pageInfo.value.oldData.children)
  } finally {
    loading.value = false
  }
}
const changeData = (newData, oldData) => {
  const newArr = newData.filter((obj1) => !oldData.some((obj2) => obj1.name === obj2.name))
  newArr.forEach((obj) => (obj.info = 'new'))

  const oldArr = oldData.filter((obj2) => !newData.some((obj1) => obj1.name === obj2.name))
  oldArr.forEach((obj) => (obj.info = 'old'))
  newData = [...newData, ...oldArr]
  return newData
}

const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('采购合同ID不能为空')
    close()
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
