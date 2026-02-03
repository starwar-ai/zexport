<template>
  <Dialog
    ref="DialogRef"
    model-class="model-class"
    v-model="dialogVisible"
    :close-on-click-modal="false"
    fullscreen
    :destroy-on-close="true"
    title="产品详情"
    @close="close"
  >
    <MainDetail
      v-if="pagetype === 'main'"
      :id="pageId"
      @handle-success="handleRefresh"
    />
    <CustProductDetail
      v-if="pagetype === 'cust'"
      :id="pageId"
      @handle-success="handleRefresh"
    />
    <OwnProductDetail
      v-if="pagetype === 'own'"
      :id="pageId"
      @handle-success="handleRefresh"
    />
    <ExportSaleContractDetail
      v-if="pagetype === 'saleContract'"
      :id="pageId"
      :lineCode="false"
      @handle-success="handleRefresh"
    />
    <template #footer>
      <eplus-button @click="close">关闭</eplus-button>
    </template>
  </Dialog>
</template>

<script setup lang="tsx">
import MainDetail from '@/views/pms/product/main/MainDetail.vue'
import CustProductDetail from '@/views/pms/product/cust/custProductDetail.vue'
import OwnProductDetail from '@/views/pms/product/own/ownProductDetail.vue'
import ExportSaleContractDetail from '@/views/sms/sale/exportSaleContract/ExportSaleContractDetail.vue'

defineOptions({ name: 'ProductInfo' })
const eplusDialogRef = ref()
const dialogVisible = ref(false)
const handleRefresh = () => {}
const pagetype = ref('mian')
const pageId = ref()
const open = (row, type?) => {
  console.log(row)
  if (type == 2) {
    // 外销合同
    pagetype.value = 'saleContract'
    pageId.value = row.saleContractItemId
  } else {
    if (row.custProFlag == 1) {
      pagetype.value = 'cust'
    } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
      pagetype.value = 'own'
    } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
      pagetype.value = 'main'
    } else {
      pagetype.value = 'cust'
    }
    pageId.value = row.skuId
  }

  dialogVisible.value = true
}

const close = () => {
  dialogVisible.value = false
}

defineExpose({ open })
</script>
