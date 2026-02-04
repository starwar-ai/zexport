<template>
  <div class="flex items-center">
    <p class="font-600">费用详情展示</p>
    <el-divider direction="vertical" />
    <div class="flex items-center">
      <p
        class="tab_item"
        :class="{ on: feeActive === 'auxiliary' }"
        @click="feeActive = 'auxiliary'"
      >
        包材
      </p>
    </div>
  </div>
  <Table
    :columns="auxiliaryShareTableColumns"
    headerAlign="center"
    align="center"
    :data="auxiliaryPurchaseItemList"
  />

  <ShareDia
    ref="ShareDiaRef"
    @sure="getInfo"
  />
</template>
<script setup lang="tsx">
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import { formatMoneyColumn } from '@/utils/table'
import ShareDia from './ShareDia.vue'

const ShareDiaRef = ref()
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

const feeActive = ref('auxiliary')
const auxiliaryPurchaseItemList = ref([])
const skuList = ref([])
const { query } = useRoute()
// 包材分摊
const auxiliaryShareTableColumns = reactive([
  {
    field: 'contractCode',
    label: '包材采购合同'
  },
  {
    field: 'skuCode',
    label: '包材编号'
  },
  {
    field: 'skuName',
    label: '包材名称'
  },
  {
    field: 'venderName',
    label: '供应商'
  },
  {
    field: 'quantity',
    label: '包材采购数量'
  },
  {
    field: 'currency',
    label: '币种'
  },
  {
    field: 'unitPrice',
    label: '包材单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'totalAmount',
    label: '包材采购总额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'action',
    label: '操作',
    minWidth: '120px',
    fixed: 'right',
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
              {row.allocationFlag == 1 ? '取消分摊' : '分摊'}
            </el-button>
          </div>
        )
      }
    }
  }
])
const getInfo = async () => {
  const id = props.id || query.id
  if (!id) return
  let res = await ExportSaleContractApi.saleContractDetailFee(id)
  if (res && res.auxiliaryPurchaseItemList) {
    auxiliaryPurchaseItemList.value = res.auxiliaryPurchaseItemList.map((row) => {
      return {
        ...row,
        totalAmount: {
          amount: Number(row.unitPrice.amount) * Number(row.quantity),
          currency: row.currency
        }
      }
    })
  } else {
    auxiliaryPurchaseItemList.value = []
  }
  skuList.value = res?.children || []
}

const handleShare = (row) => {
  if (row.allocationFlag == 1) {
    // ElMessageBox.confirm('确认取消分摊吗?', '提示', {
    //   confirmButtonText: '确认',
    //   cancelButtonText: '取消',
    //   type: 'warning'
    // }).then(async () => {
    //   await ExportSaleContractApi.cancelAllocation(row.contractItemId)
    //   message.success('取消成功')
    //   await getInfo()
    // })
    ShareDiaRef.value.open(
      {
        saleId: props.id,
        purchaseItemId: row.contractItemId
      },
      'cancel'
    )
  } else {
    ShareDiaRef.value.open(
      {
        list: skuList.value.map((item) => {
          return {
            cskuCode: item.cskuCode,
            name: item.name,
            quantity: item.quantity,
            allocationAmount: '',
            saleContractItemId: item.id
          }
        }),
        saleContractId: props.id,
        auxiliaryPurchaseContractItemId: row.contractItemId,
        totalAmount: row.totalAmount
      },
      'add'
    )
  }
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    return false
  }
  await getInfo()
})
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
