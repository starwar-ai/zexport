<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #edit="{ key }">
      <TravelForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #change="{ key }">
      <TravelForm
        :id="key"
        mode="change"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import TravelForm from './TravelForm.vue'

const emit = defineEmits(['success'])
const eplusDialogRef = ref()
const handleRefresh = () => {
  emit('success')
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

const handleChange = (id: number) => {
  eplusDialogRef.value?.openChange(id, '招待费申请变更')
}

defineExpose({
  handleUpdate,
  handleChange
})
</script>
