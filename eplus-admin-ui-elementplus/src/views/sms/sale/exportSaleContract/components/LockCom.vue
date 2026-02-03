<template>
  <el-popover
    placement="top-start"
    :width="800"
    trigger="hover"
    @show="handlePopoverShow()"
    :disabled="data.realLockQuantity === 0 || data.realLockQuantity === null"
  >
    <template #reference>
      {{ data.realLockQuantity }}
    </template>
    <template #default>
      <div v-if="tableList.length > 0">
        <Table
          :columns="tableColumns"
          headerAlign="center"
          align="center"
          :data="tableList"
        />
      </div>
      <div
        v-else
        style="text-align: center"
      >
        暂无数据
      </div>
    </template>
  </el-popover>
</template>
<script setup lang="tsx">
import * as DomesticSaleContractApi from '@/api/sms/saleContract/domestic'

const props = defineProps<{
  data
}>()

let tableList = ref([])
const tableColumns = reactive([
  {
    field: 'skuCode',
    label: '产品编号',
    minWidth: '130px'
  },
  {
    field: 'skuName',
    label: '产品名称',
    width: '120px'
  },
  {
    field: 'companyName',
    label: '公司主体',
    width: '100px'
  },
  {
    field: 'warehouseName',
    label: '仓库名称',
    width: '120px'
  },
  {
    field: 'batchCode',
    label: '批次',
    width: '120px'
  },
  {
    field: 'sourceOrderLockedQuantity',
    label: '锁定数量',
    minWidth: '100px'
  }
])

// Popover表格展示之后调接口
const handlePopoverShow = async () => {
  if (
    props.data.saleContractItemId &&
    props.data.saleContractCode &&
    props.data.companyIdList &&
    props.data.skuCode
  ) {
    tableList.value = await DomesticSaleContractApi.listBatch(props.data)
  }
}
</script>
<style lang="scss" scoped></style>
