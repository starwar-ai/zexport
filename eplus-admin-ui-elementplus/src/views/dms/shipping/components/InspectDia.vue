<template>
  <Dialog
    v-model="dialogTableVisible"
    title="转商检单"
    width="1300"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <eplus-form-table
        ref="TableRef"
        selectionFlag
        :list="tableList"
        :defaultVal="{}"
        :schema="tableColumns"
        @selection-change="handleSelectionChange"
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
import { isValidArray } from '@/utils/is'
import * as ContainerNoticeApi from '@/api/dms/containerNotice'
import { getDictLabel } from '@/utils/dict'

defineOptions({ name: 'InspectDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const emit = defineEmits<{
  sure
}>()
let tableColumns = reactive([
  {
    field: 'saleContractCode',
    label: '外销合同',
    minWidth: '120px',
    sortable: true
  },
  {
    field: 'custName',
    label: '客户名称',
    minWidth: '150px'
  },
  // {
  //   field: 'skuCode',
  //   label: '产品编号',
  //   minWidth: '100px'
  // },
  {
    field: 'custPo',
    label: '客户PO号',
    width: '150px',
    sortable: true
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    sortable: true,
    minWidth: '100px'
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文品名',
    minWidth: '200px'
  },
  {
    field: 'declarationName',
    label: '报关中文品名',
    minWidth: '200px'
  },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    minWidth: '170px',
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.IS_INT, row.commodityInspectionFlag)
    }
  },
  {
    field: 'commodityInspectionType',
    label: '商检负责方',
    minWidth: '120px',
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.COMMODITY_INSPECTION_TYPE, row.commodityInspectionType)
    }
  },
  {
    field: 'venderName',
    label: '供应商名称',
    minWidth: '100px'
  }
])
const tableList = ref([])
const TableRef = ref()
const open = async (list = []) => {
  tableList.value = []
  if (isValidArray(list)) {
    list.forEach((item: any) => {
      if (!item.isToCommodityInspection && item.commodityInspectionFlag === 1) {
        tableList.value.push(item)
      }
    })
  } else {
    tableList.value = []
  }

  if (!isValidArray(tableList.value)) {
    message.warning('暂无可转商检的记录')
    return false
  }

  dialogTableVisible.value = true
  nextTick(() => {
    TableRef.value?.handleAllToggleRowSelection()
  })
}
const selectList = ref([])
const handleSelectionChange = (val) => {
  selectList.value = val
}

const handleCancel = () => {
  dialogTableVisible.value = false
}
const handleSure = async () => {
  if (selectList.value.length > 0) {
    let ids = selectList.value.map((item: any) => item.id)
    var res = await ContainerNoticeApi.toInspection({ ids: ids })
    // message.success('操作成功')
    message.notifyPushSuccess('转商检单成功', res, 'shipment-commodity-inspection')
    handleCancel()
    emit('sure')
  } else {
    message.error('请先选择数据')
  }
}
defineExpose({ open })
</script>

<style lang="scss" scoped></style>
