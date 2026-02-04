<template>
  <Table
    :columns="auxiliaryShareTableColumns"
    headerAlign="center"
    align="center"
    :data="tableData"
  />

  <ShareDia
    ref="ShareDiaRef"
    @sure="getInfo"
  />
</template>
<script setup lang="tsx">
import { formatDictColumn, formatMoneyColumn } from '@/utils/table'
import ShareDia from './ShareDia.vue'

const ShareDiaRef = ref()
const message = useMessage()
const props = defineProps<{
  info?: any
  actionFlag?: boolean
  title?: string
  id?: number
}>()
const tableData = ref(props.info?.allocationList || [])

// const { close, goEdit } = props.id
//   ? (inject('dialogEmits') as {
//       close: () => void
//       goEdit: (val) => void
//     })
//   : { close: () => {}, goEdit: () => {} }

// const auxiliaryPurchaseItemList = ref([])
// const skuList = ref([])
// 包材分摊
const auxiliaryShareTableColumns = reactive([
  {
    field: 'skuCode',
    label: '包材编号'
  },
  {
    field: 'skuName',
    label: '包材名称'
  },
  {
    field: 'unit',
    label: '计量单位',
    formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
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
    field: 'contractCode',
    label: '包材采购合同'
  },
  {
    field: 'quantity',
    label: '包材采购数量'
  },
  {
    field: 'withTaxPrice',
    label: '包材采购单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'isAllocation',
    label: '是否分摊',
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
    // formatter: (row) => {
    //   return getDictLabel(DICT_TYPE.CALCULATION_TYPE, row?.shareFlag) || '-'
    // }
  },
  {
    field: 'allocationAmount',
    label: '分摊金额',
    formatter: formatMoneyColumn()
  },

  {
    field: 'action',
    label: '操作',
    width: '120px',
    fixed: 'right',
    disabled: props.actionFlag,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleShare(row)
              }}
            >
              {row.isAllocation == 1 ? '取消分摊' : '分摊'}
            </el-button>
          </div>
        )
      }
    }
  }
])

const handleShare = (row) => {
  if (row.isAllocation == 1) {
    ShareDiaRef.value.open(
      {
        auxiliaryContractItemId: row.purchaseContractItemId,
        contractId: props.info?.id,
        allocationFlag: row.isAllocation
      },
      'cancel',
      row.allocationAmount
    )
  } else {
    ShareDiaRef.value.open(
      {
        auxiliaryContractItemId: row.purchaseContractItemId,
        contractId: props.info?.id,
        allocationFlag: row.isAllocation
      },
      'add',
      row.allocationAmount
    )
  }
}
const emit = defineEmits(['getInfo'])
const getInfo = (val) => {
  emit('getInfo')
}
</script>
<style lang="scss" scoped>
.tab_item {
  width: 120px;
  text-align: center;
  cursor: pointer;
}

.tab_item.on {
  color: #409eff;
}
</style>
