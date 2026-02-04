<template>
  <Dialog
    v-model="dialogTableVisible"
    title="出入库记录"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <Table
        :columns="tableColumns"
        headerAlign="center"
        align="center"
        :data="tableList"
      />
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="handleCancel"
        >
          关闭
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
// 将错误的导入路径
// import { getRecord } from '@/api/wms/stock'

// 修改为正确的导入路径
import { getRecord } from '@/api/wms/stock/index'
import { formatDateColumn, formatDictColumn } from '@/utils/table'
import { DICT_TYPE } from '@/utils/dict'
import { formatNum } from '@/utils'

defineOptions({ name: 'RecordDia' })
const tableList = ref([])
const dialogTableVisible = ref(false)
const tableColumns = [
  {
    field: 'skuName',
    label: '产品中文名称'
  },
  {
    field: 'companyName',
    label: '公司名称'
  },
  {
    field: 'sourceType',
    label: '来源单据类型',
    formatter: formatDictColumn(DICT_TYPE.STOCK_SOURCE_STATUS)
  },
  {
    field: 'billType',
    label: '入/出库类型',
    formatter: formatDictColumn(DICT_TYPE.IN_OUT_TYPE)
  },
  {
    field: 'actQuantity',
    label: '实收/出数量'
  },
  {
    field: 'actBoxQuantity',
    label: '实收/出箱数',
    formatter: (row) => {
      return <span>{formatNum(row.actBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'createTime',
    label: '操作时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
  },
  {
    field: 'creatorName',
    label: '操作人'
  }
]
const open = async (code) => {
  tableList.value = await getRecord(code)
  dialogTableVisible.value = true
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

defineExpose({ open })
</script>

<style lang="scss" scoped></style>
