<template>
  <div class="!w-100%">
    <Dialog
      v-model="dialogVisible"
      append-to="#appView"
      :destroy-on-close="true"
      title="确认数据"
      width="80%"
    >
      <Table
        :columns="tableColumns"
        headerAlign="center"
        align="center"
        :data="resultRef"
      />
      <!-- <div><span>本次认领金额总计：</span><span></span></div> -->
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="payAction"
            v-hasPermi="['fms:cust-claim:create']"
          >
            确认
          </el-button>
          <el-button @click="close">取消</el-button>
        </div>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="tsx">
import { propTypes } from '@/utils/propTypes'
import { formatNumColumn } from '@/utils/table'

const props = defineProps({
  destroyOnClose: propTypes.bool.def(true),
  beforeClose: propTypes.string.def('')
})
let tableList = reactive([])
const formData = ref({})
provide('formData', formData)
const emit = defineEmits(['success', 'failure'])
const dialogVisible = ref<boolean>(false)

const formatManager = (val) => {
  if (val && val?.length > 0) {
    return val.map((item) => item.nickname).join(',')
  }
}

const tableColumns = [
  {
    label: '编号',
    field: 'payeeCode',
    minWidth: 180
  },
  {
    label: '名称',
    field: 'payeeName',
    width: 320
  },
  {
    label: '负责员工',
    field: 'manager',
    minWidth: 220,
    slots: {
      default: (data) => {
        const { row } = data
        return formatManager(row.manager)
      }
    }
  },
  {
    label: '认领总金额',
    field: 'claimTotalAmount',
    minWidth: 150,
    formatter: formatNumColumn()
  },
  {
    label: '币种',
    field: 'currency',
    minWidth: 150
  }
]

const payAction = async () => {
  emit('success')
}
const close = async () => {
  dialogVisible.value = false
}
const resultRef = ref()
const open = async (result) => {
  resultRef.value = result
  dialogVisible.value = true
}
defineExpose({ open, close })

onMounted(() => {})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-eplus-dialog;

.#{$prefix-cls} {
  :deep(.el-overlay),
  :deep(.el-overlay-dialog) {
    position: absolute;
  }

  :deep(.el-dialog) {
    display: flex;
    flex-direction: column;
  }

  :deep(.el-dialog__body) {
    flex: 1 1 auto;
    overflow: auto;
  }
  .el-form {
    padding-top: 15px;
  }
}
</style>
