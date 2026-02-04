<template>
  <Dialog
    v-model="dialogTableVisible"
    v-if="dialogTableVisible"
    :title="mode === 'add' ? '转开票通知单' : '编辑开票通知单'"
    width="1000"
    append-to-body
    destroy-on-close
  >
    采购合同号：<el-input
      v-model="parentData.purOrderCode"
      disabled
      class="!w-30%"
    />
    <br />
    <br />
    供应商名称：<el-input
      v-model="parentData.venderName"
      disabled
      class="!w-30%"
    />
    <br />
    <br />
    <eplus-form-table
      selectionFlag
      ref="tableRef"
      :schema="tableSchema"
      :list="parentData.children || []"
    />
    <template #footer>
      <div
        v-if="mode === 'add'"
        class="dialog-footer"
      >
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
      <div
        v-if="mode === 'edit'"
        class="dialog-footer"
      >
        <el-button @click="handleCancel">取消</el-button>
        <el-button @click="handleSave(0)"> 保存 </el-button>
        <el-button @click="handleSave(1)"> 提交 </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import * as InvoicingNoticesApi from '@/api/scm/invoicingNotices'
import { formatNum, formatterPrice } from '@/utils'
import { weightPrecision } from '@/utils/config'
import { getDictLabel } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
import { getOuterboxVal } from '@/utils/outboxSpec'

const message = useMessage()

const emit = defineEmits(['success'])
const dialogTableVisible = ref(false)
const tableList = ref([])
const parentData = ref({})
const tableRef = ref()
const mode = ref('add')
let tableSchema: any = []
const editSchema = reactive([
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '100px'
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '120px'
  },
  {
    field: 'invoicSkuName',
    label: '开票品名',
    batchEditFlag: true,
    component: <el-input />,
    width: '200px'
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    batchEditFlag: true,
    batchEditCom: (
      <eplus-dict-select
        dictType={DICT_TYPE.HS_MEASURE_UNIT}
        clearable={false}
      />
    ),
    slot: (item, row, index) => {
      return (
        <eplus-dict-select
          v-model={row.hsMeasureUnit}
          dictType={DICT_TYPE.HS_MEASURE_UNIT}
          clearable={false}
          onChangeEmit={(val) => {
            unitChange(val, row)
          }}
        />
      )
    },
    width: '150px'
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    component: <el-input />,
    width: '150px'
  },
  {
    field: 'taxRate',
    label: '退税率(%)',
    width: '100px'
  },
  {
    field: 'purchaseTotalQuantity',
    label: '采购数量',
    width: '120px',
    formatter: (item, row, index) => {
      return formatNum(row.purchaseTotalQuantity)
    }
  },
  {
    field: 'baseInvoiceQuantity',
    label: '本次开票数量',
    width: '120px',
    slot: (item, row, index) => {
      return (
        <el-input-number
          v-model={row.baseInvoiceQuantity}
          min={1}
          max={row.purchaseTotalQuantity - row.invoicedQuantity + row.oldBaseInvoiceQuantity}
          controls={false}
          precision={0}
          onChange={() => {
            unitChange(row.hsMeasureUnit, row)
          }}
        />
      )
    }
  },
  {
    field: 'invoicedQuantity',
    label: '已开票数量',
    width: '120px',
    formatter: (item, row, index) => {
      return formatNum(row.invoicedQuantity)
    }
  },
  {
    field: 'invoicNoticesQuantity',
    label: '本次开票数量(转换后)',
    width: '180px',
    formatter: (item, row, index) => {
      return formatNum(row.invoicNoticesQuantity)
    }
  }
])

const addSchema = reactive([
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '100px'
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '120px'
  },
  {
    field: 'skuName',
    label: '开票品名',
    batchEditFlag: true,
    component: <el-input />,
    width: '200px'
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    batchEditFlag: true,
    batchEditCom: (
      <eplus-dict-select
        dictType={DICT_TYPE.HS_MEASURE_UNIT}
        clearable={false}
      />
    ),
    slot: (item, row, index) => {
      return (
        <eplus-dict-select
          v-model={row.hsMeasureUnit}
          dictType={DICT_TYPE.HS_MEASURE_UNIT}
          clearable={false}
          onChangeEmit={(val) => {
            unitChange(val, row)
          }}
        />
      )
    },
    width: '150px'
  },

  {
    field: 'purchaseTotalQuantity',
    label: '采购数量',
    width: '120px',
    formatter: (item, row, index) => {
      return formatNum(row.purchaseTotalQuantity)
    }
  },
  {
    field: 'baseInvoiceQuantity',
    label: '本次开票数量',
    width: '120px',
    slot: (item, row, index) => {
      return (
        <el-input-number
          v-model={row.baseInvoiceQuantity}
          min={1}
          max={row.purchaseTotalQuantity - row.invoicedQuantity}
          controls={false}
          precision={0}
          onChange={() => {
            unitChange(row.hsMeasureUnit, row)
          }}
        />
      )
    }
  },
  {
    field: 'invoiceStatus',
    label: '开票状态',
    width: '120px',
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.INVOICE_STATUS, row.invoiceStatus)
    }
  },

  {
    field: 'invoicedQuantity',
    label: '已开票数量',
    width: '120px',
    formatter: (item, row, index) => {
      return formatNum(row.invoicedQuantity)
    }
  },
  {
    field: 'invoicNoticesQuantity',
    label: '本次开票数量(转换后)',
    width: '180px',
    formatter: (item, row, index) => {
      return formatNum(row.invoicNoticesQuantity)
    }
  }
])
const open = async (params) => {
  try {
    tableSchema = [...addSchema]
    mode.value = 'add'
    parentData.value = await PurchaseContractApi.toInvoiceNoticeDetail(params.id)
    handleData()
  } catch (e) {
    console.log(e, 'error')
  }
}
const handleEdit = async (row) => {
  try {
    tableSchema = [...editSchema]
    parentData.value = await InvoicingNoticesApi.getInvoicingNoticesDetail({ id: row.id })
    parentData.value.children.forEach((item) => {
      item.oldBaseInvoiceQuantity = item.baseInvoiceQuantity
      item.skuUnit = item.hsMeasureUnit
      unitChange(item.hsMeasureUnit, item)
    })
    mode.value = 'edit'
    dialogTableVisible.value = true
    nextTick(() => {
      tableRef.value?.handleAllToggleRowSelection()
    })
  } catch (e) {
    console.log(e, 'error')
  }
}

const handleData = () => {
  parentData.value.children.forEach((item) => {
    item.baseInvoiceQuantity = item.purchaseTotalQuantity - item.invoicedQuantity
    unitChange(item.hsMeasureUnit, item)
  })
  parentData.value.children = parentData.value.children.filter(
    (item) => item.baseInvoiceQuantity > 0
  )
  dialogTableVisible.value = true
  nextTick(() => {
    tableRef.value?.handleAllToggleRowSelection()
  })
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

const handleSure = async () => {
  try {
    let selectedItems = tableRef.value.getSelectedRows()
    if (isValidArray(selectedItems)) {
      for (let index = 0; index < selectedItems.length; index++) {
        const item = selectedItems[index]
        if (!(item.hsMeasureUnit && item.skuName)) {
          message.warning('请完善选中数据的开票品名和海关计量单位')
          return false
        }
      }
    } else {
      message.warning('请先选中操作的数据')
      return false
    }

    let res = await PurchaseContractApi.toInvoiceNotice({
      ...parentData.value,
      children: selectedItems.map((item) => {
        return { ...item, skuNoticeName: item.skuName }
      })
    })
    dialogTableVisible.value = false
    emit('success')
    // message.success('转开票通知单成功')
    message.notifyPushSuccess('转开票通知单成功', res, 'purchase-contract-notice')
  } catch (e) {
    console.log(e, 'e')
  }
}
const handleSave = async (type) => {
  try {
    let selectedItems = tableRef.value.getSelectedRows()
    if (isValidArray(selectedItems)) {
      for (let index = 0; index < selectedItems.length; index++) {
        const item = selectedItems[index]
        if (!(item.hsMeasureUnit && item.invoicSkuName)) {
          message.warning('请完善选中数据的开票品名和海关计量单位')
          return false
        }
      }
    } else {
      message.warning('请先选中操作的数据')
      return false
    }
    await InvoicingNoticesApi.invoicingNoticesUpdate({
      ...parentData.value,
      children: selectedItems,
      submitFlag: type
    })
    dialogTableVisible.value = false
    emit('success')
    message.success('操作成功')
  } catch (e) {
    console.log(e, 'e')
  }
}

const unitChange = (val, row) => {
  let unitLabel = getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, val)
  if (unitLabel === '克') {
    // if (row.skuUnit === '千克') {
    //   row.invoicNoticesQuantity = row.baseInvoiceQuantity * 1000
    // } else if (row.skuUnit === '克') {
    //   row.invoicNoticesQuantity = row.baseInvoiceQuantity
    // } else {

    // }
    let w = getOuterboxVal(row, 'netweight') * 1000
    row.invoicNoticesQuantity = formatterPrice(
      Math.ceil(row.baseInvoiceQuantity / row.qtyPerOuterbox) * w,
      weightPrecision
    )
  } else if (unitLabel === '千克') {
    // if (row.skuUnit === '克') {
    //   row.invoicNoticesQuantity = row.baseInvoiceQuantity / 1000
    // } else if (row.skuUnit === '千克') {
    //   row.invoicNoticesQuantity = row.baseInvoiceQuantity
    // } else {

    // }
    let w = getOuterboxVal(row, 'netweight')
    row.invoicNoticesQuantity = formatterPrice(
      Math.ceil(row.baseInvoiceQuantity / row.qtyPerOuterbox) * w,
      weightPrecision
    )
  } else {
    row.invoicNoticesQuantity = row.baseInvoiceQuantity
  }
}

defineExpose({ open, handleEdit })
</script>
<style scoped lang="scss"></style>
