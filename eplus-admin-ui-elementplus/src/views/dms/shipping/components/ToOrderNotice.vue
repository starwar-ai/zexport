<template>
  <Dialog
    v-model="dialogTableVisible"
    v-if="dialogTableVisible"
    title="转开票通知单"
    width="1155"
    append-to-body
    destroy-on-close
  >
    <eplus-form-table
      selectionFlag
      ref="tableRef"
      prop="addressShipping"
      :schema="tableSchema"
      :list="tableList"
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
import { columnWidth } from '@/utils/table'
import { PurchaseTypeEnum } from '@/utils/constants'
import * as ShipmentApi from '@/api/dms/shipment/index'
import { getDictLabel } from '@/utils/dict'
import { isValidArray } from '@/utils/is'

const message = useMessage()

const emit = defineEmits(['success'])
const dialogTableVisible = ref(false)
const tableList = ref([])
const parentData = ref({})
const tableRef = ref()
const open = (params) => {
  try {
    parentData.value = params
    let unNoticeList = params?.children?.filter(
      (item) =>
        item?.invoiceStatus === 0 &&
        item?.purchaseType !== PurchaseTypeEnum.STANDARD.type &&
        item?.thisPurchaseQuantity > 0
    )
    if (isValidArray(unNoticeList)) {
      tableList.value =
        unNoticeList.map((item) => {
          return {
            ...item,
            itemId: item.id,
            invoiceQuantity: item.declarationQuantity
          }
        }) || []
      dialogTableVisible.value = true
      nextTick(() => {
        tableRef.value?.handleAllToggleRowSelection()
      })
    } else {
      message.warning('无可转开票通知单数据')
    }
  } catch (e) {
    console.log(e, 'error')
  }
}
const handleCancel = () => {
  dialogTableVisible.value = false
}
const handleSure = async () => {
  try {
    let selectedItems = tableRef.value.getSelectedRows()
    if (!isValidArray(selectedItems)) {
      message.warning('请先选中操作的数据')
      return false
    }
    let res = await ShipmentApi.toInvoicNotice(
      selectedItems.map((el) => {
        return {
          itemId: el.id,
          hsMeasureUnit: el.hsMeasureUnit,
          invoiceQuantity: el.invoiceQuantity
        }
      })
    )
    dialogTableVisible.value = false
    emit('success')
    message.notifyPushSuccess('转开票通知单成功', res, 'purchase-contract-notice')
  } catch (e) {
    console.log(e, 'e')
  }
}
const tableSchema = [
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: columnWidth.l
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同',
    width: columnWidth.xl
  },
  {
    field: 'venderName',
    label: '供应商',
    width: columnWidth.xxl
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'declarationName',
    label: '开票品名',
    width: columnWidth.l
  },
  {
    field: 'purchaseTotalQuantity',
    label: '采购数量',
    width: columnWidth.l
  },
  {
    field: 'hsMeasureUnit',
    label: '计量单位',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, row.hsMeasureUnit)
    }
  },
  {
    field: 'invoiceQuantity',
    label: '开票数量',
    width: columnWidth.l,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^[1-9]\d*$/
        if (!value) {
          callback(new Error('请输入开票数量'))
        } else if (!regx.test(value)) {
          callback(new Error('请输入大于0的整数'))
        } else {
          callback()
        }
      }
    }
  }
]
defineExpose({ open })
</script>
<style scoped lang="scss"></style>
