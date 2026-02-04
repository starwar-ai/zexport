<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleDialogFailure"
    @failure="handleDialogFailure"
  >
    <template #create>
      <FeeShareOrderForm
        :initData="initData"
        mode="create"
        @save="handleSave"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

defineOptions({ name: 'OrderFeeShare' })

const handleDialogFailure = () => {}
const initData = ref()
const handleCreate = (obj) => {
  console.log(obj)
  initData.value = obj
  eplusDialogRef.value?.openCreate('费用归集')
}
const emit = defineEmits(['submit'])
const handleSave = (data) => {
  emit('submit', data)
}

defineExpose({ handleCreate })
</script>
