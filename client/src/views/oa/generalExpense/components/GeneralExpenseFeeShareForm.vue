<template>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleDialogFailure"
    @failure="handleDialogFailure"
  >
    <template #create>
      <FeeShareForm
        :feeShareAmount="feeShareAmount"
        :initData="initData"
        mode="create"
        @save="handleSave"
      />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import FeeShareForm from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

defineOptions({ name: 'GeneralExpenseFeeShareForm' })

const handleDialogFailure = () => {}
const initData = ref()
const feeShareAmount = ref()
const handleCreate = (obj, amount) => {
  initData.value = obj
  feeShareAmount.value = amount
  eplusDialogRef.value?.openCreate('费用归集')
}
const emit = defineEmits(['submit'])
const handleSave = (data) => {
  emit('submit', data)
}

defineExpose({ handleCreate })
</script>
