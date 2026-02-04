<template>
  <Dialog
    v-model="dialogTableVisible"
    title="转结汇单"
    width="1175"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <!-- <el-form-item label="归属公司">
        <el-select v-model="companyId">
          <el-option
            v-for="i in companyPathListRef"
            :key="i.id"
            :value="i.id"
            :label="i.name"
          />
        </el-select>
      </el-form-item> -->
      <eplus-form-table
        ref="TableRef"
        selectionFlag
        :list="tableList"
        :defaultVal="{}"
        :schema="basiColumns"
      />
    </div>
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
import * as ContainerNoticeApi from '@/api/dms/containerNotice'
import { isValidArray } from '@/utils/is'
import { getDictLabel } from '@/utils/dict'
import { getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import { columnWidth } from '@/utils/table'

defineOptions({ name: 'SettlementFormDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
const TableRef = ref()
const companyId = ref()
const tableList = ref([])
const companyPathListRef = ref([])
const basiColumns = reactive([
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: columnWidth.l,
    sortable: true
  },
  // {
  //   field: 'inboundDate',
  //   label: '实际拉柜/进仓日期',
  //   width: '150px',
  //   slot: (item, row, index) => {
  //     if (row.inboundDate) {
  //       return <span>{formatDate(row.inboundDate, 'YYYY-MM-DD')}</span>
  //     } else {
  //       return <span></span>
  //     }
  //   }
  // },
  {
    field: 'shippedAddress',
    label: '发货地点',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.SHIPPED_ADDRESS, row.shippedAddress)
    }
  },
  {
    field: 'companyPathName',
    label: '订单路径',
    width: columnWidth.l
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '150px'
  },
  {
    field: 'custPo',
    label: '客户PO号',
    width: '150px',
    sortable: true
  },
  // {
  //   field: 'skuCode',
  //   label: '产品编号',
  //   width: columnWidth.l
  // },
  {
    field: 'cskuCode',
    label: '客户货号',
    sortable: true,
    width: columnWidth.l
  },
  {
    field: 'nameEng',
    label: '英文品名',
    width: columnWidth.xxl
  },
  {
    field: 'name',
    label: '中文品名',
    width: columnWidth.xxl
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l
  },
  {
    field: 'quantity',
    label: '结汇数量',
    width: columnWidth.l
  },
  {
    field: 'settlementName',
    label: '结汇方式',
    width: columnWidth.l
  }
  // {
  //   field: 'companyPathName',
  //   label: '订单路径',
  //   width: columnWidth.l
  // }
])
const open = async (row) => {
  tableList.value = []
  companyPathListRef.value = []
  companyId.value = null
  let childrenList = isValidArray(row) ? row : row.children
  childrenList.forEach((item) => {
    // setCompanyPathListRef(item)
    tableList.value.push({
      ...item,
      inboundDate: row.inboundDate,
      quantity: item.shippingQuantity,
      companyPathName: item.companyPath ? getCompanyPathNameFromObj(item.companyPath) : '-'
    })
  })
  if (isValidArray(tableList.value)) {
    dialogTableVisible.value = true
    nextTick(() => {
      TableRef.value?.handleAllToggleRowSelection()
    })
  } else {
    message.warning('暂无可转结汇的记录')
  }
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

const handleSure = async () => {
  // if (!companyId.value) {
  //   message.warning('请选择归属公司')
  //   return
  // }
  let paramsList = TableRef.value.getSelectedRows()
  if (paramsList.length == 0) {
    message.warning('请选择需要转结汇的记录')
    return
  }
  let res = await ContainerNoticeApi.toSettlement({
    settlementFormList: paramsList.map((item) => {
      return {
        id: item.id,
        quantity: item.quantity
      }
    })
  })
  // message.success('操作成功')
  message.notifyPushSuccess('转结汇单成功', res, 'shipment-settlement-form')
  handleCancel()
  emit('sure')
}

const setCompanyPathListRef = (item) => {
  if (item.companyPath) {
    let nextItem = item.companyPath
    while (nextItem) {
      let obj = {
        id: nextItem.id,
        name: nextItem.name
      }
      let includesArr = companyPathListRef?.value.filter((it) => {
        return it.id == nextItem.id
      })
      if (!includesArr.length) {
        companyPathListRef.value.push(obj)
      }
      nextItem = nextItem.next
    }
  }
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
