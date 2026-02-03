<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="diaType == 1 ? '工厂出库' : '转出库单'"
    width="1300"
    append-to-body
    destroy-on-close
  >
    <Table
      :columns="tableColumns"
      headerAlign="center"
      align="center"
      :data="tableList"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { isValidArray } from '@/utils/is'
import { columnWidth, formatDictColumn } from '@/utils/table'
import { getOuterbox } from '@/utils/outboxSpec'
import { formatNum } from '@/utils'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { volPrecision } from '@/utils/config'
import * as RenoticeApi from '@/api/wms/renotice'

defineOptions({ name: 'ToBillDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
let tableColumns = reactive([
  {
    field: 'shippedAddress',
    label: '发货地点',
    formatter: formatDictColumn(DICT_TYPE.SHIPPED_ADDRESS)
  },
  {
    field: 'warehouseName',
    label: '仓库名称'
  },
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'saleContractCode',
    label: '销售合同号'
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
    label: '供应商名称'
  },
  {
    field: 'batchCode',
    label: '批次号'
  },
  {
    field: 'orderQuantity',
    label: '应出数量'
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量'
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量'
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'orderBoxQuantity',
    label: '应出箱数',
    formatter: (row) => {
      return <span>{formatNum(row.orderBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.xl,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积m³',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '单箱毛重',
    formatter: (row) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: '总体积m³',
    slots: {
      default: (data) => {
        const { row } = data
        return formatNum(row.totalVolume / 1000000, volPrecision)
      }
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return row.totalWeight ? `${row.totalWeight.weight} ${row.totalWeight.unit}` : '-'
      }
    }
  }
])
const tableList = ref([])
const diaType = ref(1)
const rowId = ref(0)
const open = async (row, type = 1) => {
  rowId.value = row.id
  diaType.value = type
  tableList.value = []
  const childrenList = row.stockNoticeItemRespVOList || row.children
  if (isValidArray(childrenList)) {
    childrenList.forEach((item: any) => {
      if (item.shippedAddress == diaType.value) {
        tableList.value.push(item)
      }
    })
  } else {
    tableList.value = []
  }

  if (!isValidArray(tableList.value)) {
    message.warning('暂无可操作数据')
    return false
  }

  dialogTableVisible.value = true
}

const handleCancel = () => {
  dialogTableVisible.value = false
}
const handleSure = async () => {
  let req =
    diaType.value == 1 ? RenoticeApi.factoryToBill(rowId.value) : RenoticeApi.toBill(rowId.value)
  let res = await req
  let des = diaType.value == 1 ? '工厂出库成功' : '转出库单成功'
  message.notifyPushSuccess(des, res, 'ckbill')
  handleCancel()
  emit('sure')
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
