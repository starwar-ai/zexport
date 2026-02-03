<template>
  <div class="!w-80%">
    <Dialog
      v-model="dialogVisible"
      append-to="#appView"
      :destroy-on-close="true"
      title="差异原因"
      width="50%"
      append-to-body
    >
      <Table
        :columns="BasicInfoSchema"
        ref="TableRef"
        :data="tableList"
        headerAlign="center"
        align="center"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="payAction"
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

const message = useMessage()
const TableRef = ref()
const props = defineProps({
  destroyOnClose: propTypes.bool.def(true),
  beforeClose: propTypes.string.def(''),
  appendEl
})
let tableList = reactive([])
let defaultValObj = reactive({
  isShow: true,
  name: '1',
  quoteDate: Date.now()
})
const formData = ref({})
provide('formData', formData)
const emit = defineEmits(['success', 'failure'])
const dialogVisible = ref<boolean>(false)

let BasicInfoSchema = reactive([
  {
    field: 'sortNum',
    label: '序号',
    width: '80px'
  },
  {
    field: 'saleContractCode',
    label: '外销合同号',
    width: '80px'
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '80px'
  },
  {
    field: 'cskuCode',
    label: '客户编号',
    minWidth: '150px'
  },
  {
    field: 'currency',
    label: '订单币别',
    width: '80px'
  },
  {
    field: 'sortNum',
    label: '收款方式',
    width: '80px'
  },
  {
    field: 'sourceType',
    label: '来源',
    width: '80px'
  },
  {
    field: 'sortNum',
    label: '应收金额',
    width: '80px'
  },
  {
    field: 'sortNum',
    label: '本次入账币种认领金额',
    width: '80px'
  },
  {
    field: 'sortNum',
    label: '订单币种认领金额',
    width: '80px'
  },
  {
    field: 'sortNum',
    label: '差异总金额',
    width: '80px'
  },
  {
    field: 'sortNum',
    label: '认领人',
    width: '80px'
  },
  {
    field: 'sortNum',
    label: '差异原因',
    width: '80px'
  }
])
const payAction = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    emit('success', { tableList, indexRef })
    close()
  } else {
    message.warning('请完善表单信息')
  }
}
const close = async () => {
  dialogVisible.value = false
}
const indexRef = ref()
const differenceAmount = ref()
const open = async (index, row) => {
  if (index !== indexRef.value) {
    tableList = []
  }
  differenceAmount.value = row?.differenceAmount ? row?.differenceAmount : ''
  dialogVisible.value = true
  indexRef.value = index
}
defineExpose({ open, close })
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
