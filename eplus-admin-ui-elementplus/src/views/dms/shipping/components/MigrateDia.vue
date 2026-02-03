<template>
  <Dialog
    v-model="dialogTableVisible"
    title="批量迁移"
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
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as ShipmentApi from '@/api/dms/shipment/index'
import * as UserApi from '@/api/system/user'

const message = useMessage()
defineOptions({ name: 'MigrateDia' })
const props = defineProps<{
  exceptId: number | string
}>()
const diaExceptId = ref(props.exceptId)
const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await ShipmentApi.getShipmentPage({
      ...ps,
      exceptId: diaExceptId.value
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  defaultParams: {
    exceptId: diaExceptId.value
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
      name: 'saleContractCode',
      label: '外销合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'invoiceCode',
      label: '发票号'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'inputUserId',
      label: '录入人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    }
  ],
  moreFields: []
}
const ids = ref('')
const open = async (id, exceptId) => {
  diaExceptId.value = exceptId
  ids.value = id
  dialogTableVisible.value = true
  await getList()
}

const handleSearch = (model) => {
  setSearchParams({ ...model, exceptId: diaExceptId.value }, [])
}
const handleReset = () => {
  setSearchParams({ exceptId: diaExceptId.value }, [])
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
    minWidth: columnWidth.m,
    isHyperlink: true
  },
  {
    field: 'invoiceCode',
    label: '发票号',
    minWidth: columnWidth.l
  },
  {
    field: 'shipmentPlanCode',
    label: '出运计划单号',
    minWidth: columnWidth.l
  },
  {
    field: 'parentSaleContractCode',
    label: '外销合同号',
    minWidth: columnWidth.l
  },
  {
    field: 'custName',
    label: '客户名称',
    minWidth: columnWidth.l
  },
  {
    field: 'foreignTradeCompanyName',
    label: '报关公司',
    minWidth: columnWidth.m
  },
  {
    field: 'custDeliveryDate',
    label: '客户交期',
    minWidth: columnWidth.l,
    formatter: formatDateColumn()
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
  },
  {
    field: 'inputUser',
    label: '录入人',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return `${row?.inputUser?.nickname}`
      }
    }
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
    await ShipmentApi.mergeShipmentItem({
      ids: ids.value,
      targetId: radioobj.val
    })
    message.success('迁移成功')
    await emit('sure')
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  tableObject.tableList = []
  radioobj.val = undefined
  selectedDiaData.value = []
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
