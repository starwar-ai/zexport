<template>
  <SaleContractPage :type="1" />
</template>
<script setup lang="tsx">
defineOptions({ name: 'ExportSaleContract' })
import SaleContractPage from '@/views/sms/sale/exportSaleContract/SaleContractPage.vue'
</script>
