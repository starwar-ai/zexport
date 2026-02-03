<template>
  <Dialog
    v-model="dialogTableVisible"
    title="追加出运明细"
    width="1000"
    append-to-body
    align-center
    destroy-on-close
  >
    <div class="mb10px">
      <eplus-search
        :fields="eplusSearchSchema.fields"
        :moreFields="eplusSearchSchema.moreFields"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>
    <Table
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :pagination="{
        total: tableObject.total
      }"
      :columns="dialogColumns"
      headerAlign="center"
      align="center"
      :data="tableObject.tableList"
      @row-click="getRow"
      style="height: 350px"
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
import Table from '@/components/Table/src/Table.vue'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatDictColumn } from '@/utils/table'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as ShipmentApi from '@/api/dms/shipment/index'
import * as ShippingPlanApi from '@/api/dms/shippingPlan/index'

const message = useMessage()
defineOptions({ name: 'AppendDia' })
const rowData = ref({})
const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await ShipmentApi.getShipmentPage({
      ...ps,
      companyId: rowData.value?.foreignTradeCompanyId,
      autoFlag: 0
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  defaultParams: {
    companyId: rowData.value?.foreignTradeCompanyId,
    statusList: [1, 2],
    autoFlag: 0
  }
})

const { getList, setSearchParams } = tableMethods

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '出运单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'invoiceCode',
      label: '发票号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'saleContractCode',
      label: '销售合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custPo',
      label: '客户PO'
    }
  ],
  moreFields: []
}

const open = async (row) => {
  dialogTableVisible.value = true
  rowData.value = row
  await getList()
}

const handleSearch = (model) => {
  setSearchParams(
    {
      ...model,
      companyId: rowData.value?.foreignTradeCompanyId,
      statusList: [1, 2]
    },
    []
  )
}
const handleReset = () => {
  setSearchParams({ companyId: rowData.value?.foreignTradeCompanyId, statusList: [1, 2] }, [])
}

const dialogColumns = reactive([
  {
    label: '选择',
    width: '60px',
    slots: {
      default: (data) => {
        const { row, $index } = data
        return (
          <el-radio
            v-model={radioobj.val}
            label={row.id}
          >
            &nbsp;
          </el-radio>
        )
      }
    }
  },
  {
    field: 'code',
    label: '出运单号',
    minWidth: columnWidth.m
  },
  {
    field: 'invoiceCode',
    label: '发票号',
    minWidth: columnWidth.m
  },
  {
    field: 'foreignTradeCompanyName',
    label: '归属公司',
    minWidth: columnWidth.m
  },
  {
    field: 'saleContractCode',
    label: '销售合同号',
    minWidth: columnWidth.m
  },
  {
    field: 'custPo',
    label: '客户PO',
    minWidth: columnWidth.m
  },
  {
    field: 'custName',
    label: '客户名称',
    minWidth: columnWidth.m
  },
  {
    field: 'departurePortName',
    label: '出运口岸',
    minWidth: columnWidth.m
  },
  {
    field: 'destinationPortName',
    label: '目的口岸',
    minWidth: columnWidth.m
  },
  {
    field: 'transportType',
    label: '运输方式',
    minWidth: columnWidth.m,
    formatter: formatDictColumn(DICT_TYPE.TRANSPORT_TYPE)
  }
])

const dialogTableVisible = ref(false)

let radioobj = reactive({
  val: undefined
})
const getRow = (row: any) => {
  radioobj.val = row.id
  selectedDiaData.value = row
}

const selectedDiaData = ref()

const emit = defineEmits(['sure'])
const handleSure = async () => {
  if (selectedDiaData.value?.id) {
    await ShippingPlanApi.handleAppend({
      shipmentId: selectedDiaData.value.id,
      shipmentPlanIds: rowData.value?.children.map((item) => item.id)
    })
    emit('sure')
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  radioobj.val = undefined
  selectedDiaData.value = []
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
