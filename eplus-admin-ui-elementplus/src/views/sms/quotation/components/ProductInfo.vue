<template>
  <!-- <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <MainDetail
        v-if="pagetype === 'main'"
        :id="key"
        @handle-success="handleRefresh"
      />
      <CustProductDetail
        v-if="pagetype === 'cust'"
        :id="key"
        @handle-success="handleRefresh"
      />
      <OwnProductDetail
        v-if="pagetype === 'own'"
        :id="key"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog> -->
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
    <template #footer>
      <eplus-button @click="close">关闭</eplus-button>
    </template>
  </Dialog>
</template>

<script setup lang="tsx">
// import QuotationForm from './QuotationForm.vue'
// import QuotationFormDetail from './QuotationFormDetail.vue'
import MainDetail from '@/views/pms/product/main/MainDetail.vue'
import CustProductDetail from '@/views/pms/product/cust/custProductDetail.vue'
import OwnProductDetail from '@/views/pms/product/own/ownProductDetail.vue'

defineOptions({ name: 'ProductInfo' })
const dialogVisible = ref(false)
const handleRefresh = () => {}
const pagetype = ref('mian')
const pageId = ref()
const open = (row) => {
  if (row.custProFlag == 1) {
    pagetype.value = 'cust'
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
    pagetype.value = 'own'
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
    pagetype.value = 'main'
  }
  pageId.value = row.id
  // eplusDialogRef.value?.open(row.id)
  dialogVisible.value = true
}

const close = () => {
  dialogVisible.value = false
}

defineExpose({ open })
</script>
