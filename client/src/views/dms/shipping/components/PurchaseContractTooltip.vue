<template>
  <el-tooltip
    effect="light"
    placement="top"
    :show-after="300"
    :hide-after="0"
    popper-class="purchase-contract-tooltip"
    :disabled="!contractData.length"
  >
    <template #content>
      <div class="tooltip-body">
        <el-table
          :data="contractData"
          size="small"
          style="width: 100%"
          :show-header="true"
          :max-height="200"
          class="contract-detail-table"
        >
          <el-table-column
            prop="purchaseContractType"
            label="采购类型"
            width="80"
            align="center"
          />
          <el-table-column
            prop="contractCode"
            label="合同号"
            width="120"
            align="center"
          />
          <el-table-column
            prop="supplierName"
            label="供应商名称"
            width="100"
            align="center"
          />
          <el-table-column
            prop="usedQuantity"
            label="使用数量"
            width="80"
            align="center"
          />
          <el-table-column
            prop="invoicingStatus"
            label="开票状态"
            width="80"
            align="center"
          />
          <el-table-column
            prop="invoicedQuantity"
            label="开票数量"
            width="80"
            align="center"
          />
          <el-table-column
            prop="invoicedItemName"
            label="开票品名"
            width="100"
            align="center"
            show-overflow-tooltip
          />
        </el-table>
      </div>
    </template>
    <span
      @mouseenter="handleMouseEnter"
      @mouseleave="handleMouseLeave"
    >
      {{ displayContractCodes || '-' }}
    </span>
  </el-tooltip>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'

interface ContractItem {
  purchaseContractType: string
  contractCode: string
  supplierName: string
  usedQuantity: number
  invoicingStatus: string
  invoicedQuantity: number
  invoicedItemName: string
}

interface Props {
  contractCode: string
  rowData?: any
  contractType?: 'purchase' | 'stock' // 新增参数：purchase=采购合同，stock=库存采购合同
}

const props = defineProps<Props>()

const contractData = ref<ContractItem[]>([])
const loading = ref(false)

// 计算显示的合同号（根据contractType显示不同类型的合同）
const displayContractCodes = computed(() => {
  const contracts = []

  if (props.contractType === 'purchase') {
    // 采购合同：只显示采购合同号
    if (props.contractCode) {
      contracts.push(props.contractCode)
    }
  } else if (props.contractType === 'stock') {
    // 库存采购合同：只显示库存采购合同号
    if (
      props.rowData?.stockPurchaseContractCodes &&
      Array.isArray(props.rowData.stockPurchaseContractCodes)
    ) {
      contracts.push(...props.rowData.stockPurchaseContractCodes)
    }
  } else {
    // 兼容旧逻辑：显示所有合同号
    if (props.contractCode) {
      contracts.push(...props.contractCode.split(','))
    }
    if (
      props.rowData?.stockPurchaseContractCodes &&
      Array.isArray(props.rowData.stockPurchaseContractCodes)
    ) {
      contracts.push(...props.rowData.stockPurchaseContractCodes)
    }
  }

  // 去重并返回逗号分隔的字符串
  const result = [...new Set(contracts)].join(', ')
  return result
})

const handleMouseEnter = async () => {
  if (loading.value) return
  try {
    loading.value = true
    // 直接从当前行数据中获取合同详情信息
    if (props.rowData?.contractDetails && Array.isArray(props.rowData.contractDetails)) {
      // 根据contractType过滤合同详情
      let filteredDetails = props.rowData.contractDetails

      if (props.contractType === 'purchase') {
        // 只显示采购合同（本次采购）
        filteredDetails = props.rowData.contractDetails.filter(
          (item) => item.purchaseContractType === '本次采购'
        )
      } else if (props.contractType === 'stock') {
        // 只显示库存采购合同（使用库存）
        filteredDetails = props.rowData.contractDetails.filter(
          (item) => item.purchaseContractType === '使用库存'
        )
      }
      contractData.value = filteredDetails
    }
  } catch (error) {
    ElMessage.error('获取合同详情失败')
    contractData.value = []
  } finally {
    loading.value = false
  }
}

const handleMouseLeave = () => {}
</script>

<style lang="scss">
.purchase-contract-tooltip {
  // 去掉弹框边框线

  .tooltip-body {
    padding: 0;
    margin: 0;
    background-color: white;
    border-radius: 4px;

    .contract-detail-table {
      font-size: 12px;
      border: 0.5px solid #dcdfe6; // 添加表格边框，更细
      width: 100%;

      .el-table__header {
        th {
          background-color: white;
          color: #000000; // 设置表头文字颜色为黑色
          font-weight: 500;
          font-size: 12px;
          border: 0.5px solid #dcdfe6; // 添加表头边框，更细
          border-bottom: 0.5px solid #dcdfe6;
        }
      }

      .el-table__body {
        td {
          font-size: 12px;
          border: 0.5px solid #dcdfe6; // 添加单元格边框，更细
          color: #000000; // 设置文字颜色为黑色
        }

        tr:last-child td {
          border-bottom: 0.5px solid #dcdfe6; // 最后一行也显示底部边框，更细
        }
      }

      .el-table__border {
        border: none;
      }

      .el-table__inner-wrapper {
        border: none;
      }

      .el-table__cell {
        border: none;
      }
    }
  }
}
</style>
