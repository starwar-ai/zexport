<template>
  <Dialog
    v-model="dialogTableVisible"
    title="转入库通知单"
    width="80%"
    append-to-body
    destroy-on-close
  >
    <eplus-form-table
      ref="TableRef"
      :list="tableList"
      :defaultVal="{}"
      :schema="tableColumns"
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
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { TableColumn } from '@/types/table'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { cloneDeep } from 'lodash-es'
import { isValidArray } from '@/utils/is'

const message = useMessage()

const dialogTableVisible = ref(false)
const TableRef = ref()
const tableList = ref([])
const companyList = ref([])
const stockList = ref([])

let tableColumns = reactive<TableColumn[]>([
  {
    field: 'purchaseContractCode',
    label: '采购合同',
    width: 170
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: 170
  },
  {
    field: 'companyName',
    label: '公司名称',
    width: 170
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: 170
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: 130
  },
  {
    field: 'skuName',
    label: '产品名称',
    width: 190
  },
  {
    field: 'quantity',
    label: '采购数量',
    width: 120
  },
  {
    field: 'unNoticedquantity',
    label: '待转入库通知数量',
    width: 140,
    formatter: (item, row, index) => {
      return Number(row.quantity) - Number(row.noticedQuantity || 0)
    }
  },
  {
    field: 'billQuantity',
    label: '实际入库数量',
    width: 120
  },
  {
    field: 'orderQuantity',
    label: '本次入库数',
    width: 120,
    // bug 660  改为不可编辑
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.orderQuantity}
          controls={false}
          precision={0}
          max={row?.quantity - (row?.noticedQuantity || 0)}
          min={1}
        />
      )
    },
    rules: [{ required: true, message: '请输入本次入库数' }]
  },
  {
    field: 'expectDate',
    label: '到货时间',
    batchEditFlag: true,
    width: 170,
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        valueFormat="x"
        class="!w-100%"
      />
    ),
    rules: [{ required: true, message: '请选择到货时间' }]
  },
  {
    field: 'warehouseId',
    label: '采购仓库',
    batchEditFlag: true,
    width: 170,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-select
          v-model={row.warehouseId}
          onChange={(val) => {
            row.warehouseName = stockList.value.find((item) => item.id == val)?.name
          }}
        >
          {stockList.value?.map((item) => {
            return (
              <el-option
                label={item?.name}
                value={item?.id}
                key={item?.id}
              />
            )
          })}
        </el-select>
      )
    },
    rules: [{ required: true, message: '请选择采购仓库' }]
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: 120
  }
])
const emit = defineEmits<{
  sure
}>()
const open = async (ids) => {
  const res = await PurchaseContractApi.batchToNoticeMid(ids?.join(','))
  companyList.value = await PurchasePlanApi.getcompanySimpleList()
  stockList.value = await PurchaseContractApi.getStockList()
  let defaultWarehouse = stockList.value.find((item: any) => item.defaultFlag == 1)
  tableList.value = res.map((item: any) => {
    return {
      ...item,
      warehouseId: defaultWarehouse?.id,
      warehouseName: defaultWarehouse?.name,
      orderQuantity: item.unOutStockQuantity
    }
  })

  if (isValidArray(tableList.value)) {
    dialogTableVisible.value = true
  } else {
    message.warning('暂无可转数据！')
  }
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

const handleSure = async () => {
  try {
    let valid = await TableRef.value.validate()
    if (valid && tableList.value?.length > 0) {
      let tempTableList = cloneDeep(tableList.value)
      tempTableList.forEach((item: any) => {
        const { uniqueCode } = item || {}
        item.sourceUniqueCode = uniqueCode
        item.orderBoxQuantity = item.boxCount
      })
      let res = await PurchaseContractApi.batchToNotice(tempTableList)
      message.notifyPushSuccess('转入库通知单成功', res, 'scm-purchase')
      // message.success('操作成功')
      emit('sure')
      dialogTableVisible.value = false
    } else {
      message.warning('请核验信息')
    }
  } catch {
  } finally {
  }
}
defineExpose({ open })
</script>
<style scoped lang="scss">
:deep(.item_key) {
  line-height: 32px;
}

.must {
  color: #d00;
  position: absolute;
  left: 52px;
  right: 0;
}

:deep(.item_val) {
  line-height: 32px;
}
</style>
