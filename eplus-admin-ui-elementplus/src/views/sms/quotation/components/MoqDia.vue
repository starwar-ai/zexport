<template>
  <Dialog
    v-model="dialogTableVisible"
    title="提示"
    width="500px"
    append-to-body
    destroy-on-close
  >
    <div class="dialog-des">{{ message }}</div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleSure('ceil')">向上取整</el-button>
        <el-button
          @click="handleSure('floor')"
          v-if="!isShowFloor"
          >向下取整
        </el-button>
        <el-button
          type="primary"
          @click="handleSure('cancel')"
        >
          取消
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
defineOptions({ name: 'MoqDia' })
const dialogTableVisible = ref(false)
const message = ref('')
const emit = defineEmits(['sure'])
const isShowFloor = ref(false)
const open = (des, floorFlag) => {
  isShowFloor.value = floorFlag === 'floor' ? true : false
  message.value = des
  dialogTableVisible.value = true
}

const handleSure = async (type) => {
  emit('sure', type)
  dialogTableVisible.value = false
}
defineExpose({ open })
</script>
<style lang="scss" scoped>
.dialog-des {
  line-height: 30px;
}
</style>
