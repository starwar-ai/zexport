<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #edit="{ key }">
      <GeneralEditForm
        :id="key"
        mode="edit"
      />
    </template>

    <template #change="{ key }">
      <GeneralEditForm
        :id="key"
        mode="change"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import GeneralEditForm from './GeneralEditForm.vue'

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
