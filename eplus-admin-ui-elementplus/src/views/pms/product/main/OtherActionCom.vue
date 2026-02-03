<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #edit="{ key }">
      <main-form
        :id="key"
        mode="edit"
      />
    </template>

    <template #change="{ key }">
      <main-form
        :id="key"
        mode="change"
      />
    </template>

    <template #copy="{ key }">
      <main-form
        :id="key"
        mode="copy"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import MainForm from './MainForm.vue'

const emit = defineEmits(['success'])
const eplusDialogRef = ref()
const handleRefresh = () => {
  emit('success')
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

const handleChange = (id: number) => {
  eplusDialogRef.value?.openChange(id, '基础产品变更')
}
const handleCopy = (id: number) => {
  eplusDialogRef.value?.openCopy(id, '基础产品复制')
}

defineExpose({
  handleUpdate,
  handleChange,
  handleCopy
})
</script>
