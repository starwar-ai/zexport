<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #edit="{ key }">
      <SettlementFormMain
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import SettlementFormMain from './SettlementFormMain.vue'

const emit = defineEmits(['success'])
const eplusDialogRef = ref()
const handleRefresh = () => {
  emit('success')
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

defineExpose({
  handleUpdate
})
</script>
