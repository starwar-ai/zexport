<template>
  <!-- 新增编辑 -->
  <Dialog
    v-model="dialogFormVisible"
    :title="diaTitle"
    width="40%"
    :close-on-click-modal="false"
    :show-close="false"
  >
    <!-- 详情 -->
    <el-form
      v-if="diaTitle == '详情'"
      ref="ruleFormRef"
      :model="dialogDetails"
      label-width="140"
      :rules="rules"
      class="demo-ruleForm"
      :size="formSize"
      status-icon
    >
      <el-form-item label="序号：">
        {{ dialogDetails.index }}
      </el-form-item>
      <el-form-item label="发票名称：">
        {{ dialogDetails.invoiceName }}
      </el-form-item>
      <el-form-item label="发票金额：">
        {{ dialogDetails.invoiceAmount }}
      </el-form-item>
      <el-form-item label="发票日期：">
        {{ formatTime(dialogDetails.createTime, 'yyyy-MM-dd') }}
      </el-form-item>
      <el-form-item label="发票类型：">
        {{ getDictLabel(DICT_TYPE.INVOICE_TYPE, dialogDetails.invoiceType) }}
      </el-form-item>
      <el-form-item label="发票号：">
        {{ dialogDetails.invoiceCode }}
      </el-form-item>
      <el-form-item
        label="发票附件："
        prop="region"
      >
        <UploadList
          :disabled="true"
          :fileList="dialogDetails.invoice"
        />
      </el-form-item>
    </el-form>
    <!-- 新增/编辑 -->
    <el-form
      v-else
      ref="ruleFormRef"
      label-width="140"
      :model="ruleForm"
      :rules="rules"
      class="demo-ruleForm"
      :size="formSize"
      status-icon
    >
      <el-form-item
        label="发票名称："
        prop="dictSubjectId"
      >
        <eplus-input-select
          v-model="ruleForm.dictSubjectId"
          :dataList="feeList"
          label="feeName"
          value="id"
        />
      </el-form-item>
      <el-form-item
        label="发票金额："
        prop="invoiceAmount"
      >
        <EplusNumInput
          v-model="ruleForm.invoiceAmount"
          min="0"
          :precision="moneyInputPrecision"
          class="!w-100%"
          placeholder="请输入发票金额"
        />
      </el-form-item>

      <el-form-item
        label="发票日期："
        prop="invoiceDate"
      >
        <el-date-picker
          v-model="ruleForm.invoiceDate"
          type="date"
          value-format="x"
          placeholder="请选择发票日期"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item
        label="发票类型："
        prop="invoiceType"
      >
        <eplus-dict-select
          v-model="ruleForm.invoiceType"
          :dictType="DICT_TYPE.INVOICE_TYPE"
          @change="(val) => onChangeType(val)"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item
        label="发票号："
        prop="invoiceCode"
      >
        <el-input
          v-model="ruleForm.invoiceCode"
          placeholder="请输入发票号"
        />
      </el-form-item>
      <el-form-item
        label="发票附件："
        prop="invoice"
      >
        <UploadList
          :fileList="ruleForm.invoice"
          @success="getFileList"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div
        class="dialog-footer"
        v-if="diaTitle == '详情' && dialogDetails.status == 0"
      >
        <el-button
          size="small"
          @click="handleClose"
          >关闭
        </el-button>
      </div>
      <div
        class="dialog-footer"
        v-else
      >
        <el-button
          size="small"
          @click="handleClose"
          >关闭
        </el-button>
        <el-button
          v-if="dialogDetails.status !== 1"
          type="primary"
          @click="handleSumbit(ruleFormRef)"
          size="small"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { ElButton } from 'element-plus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import * as HomeApi from '@/api/home'
import eventBus from './eventBus.ts'
import { useFeeStore } from '@/store/modules/fee'
import { formatTime } from '@/utils/index'

import { moneyInputPrecision } from '@/utils/config'

const props = defineProps<{
  dialogOption: Object
}>()

const dialogFormVisible = ref(false)
const formSize = ref<ComponentSize>('default')
const ruleFormRef = ref<FormInstance>()
const message = useMessage()
const diaTitle = ref('新增')
const FileList = ref([])
const dialogDetails = ref()
const refresh = ref(false)
const feeList = useFeeStore().feeList

const emit = defineEmits(['close', 'generalExpense', ''])
let ruleForm = reactive<RuleForm>({
  invoiceType: 1,
  invoiceAmount: '',
  invoiceCode: '',
  invoice: []
})

const params = ref({
  pageSize: 5,
  pageNo: 1
})

const rules = reactive<FormRules<RuleForm>>({
  invoiceAmount: [{ required: true, message: '输入发票金额', trigger: 'blur' }],
  invoiceDate: [{ required: true, message: '请选择日期', trigger: 'blur' }],
  dictSubjectId: [
    {
      required: true,
      message: '选择发票名称',
      trigger: 'change'
    }
  ],
  invoiceType: [
    {
      required: true,
      message: '选择发票类型',
      trigger: 'change'
    }
  ],
  invoiceCode: [{ required: true, message: '填写发票号：', trigger: 'blur' }],
  invoice: [{ required: true, message: '发票附件不能为空', trigger: 'blur' }]
})

const setDate = (timestamp) => {
  let date = new Date(timestamp)
  return date.toLocaleDateString('zh-CN')
}

const UploadRef = ref()
const getFileList = (params) => {
  ruleForm.invoice = params
}
const onChangeType = (val) => {
  ruleForm.reimbType = val
}

// 新增
const handleSumbit = (formEl: FormInstance | undefined) => {
  if (!formEl) return

  formEl.validate((valid) => {
    if (valid) {
      let changeUrl =
        diaTitle.value == '编辑'
          ? HomeApi.updateInvoice(ruleForm)
          : HomeApi.createInvoiceHolder(ruleForm)
      changeUrl.then(() => {
        message.success('提交成功')
        handleClose(true)
      })
    }
  })
}

// 编辑
const handleEdit = () => {
  diaTitle.value = '编辑'
  ruleForm = reactive({ ...dialogDetails.value })
}

// 删除
const handleDelete = (item) => {
  ElMessageBox.confirm('是否删除当前数据？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await HomeApi.deleteHemoInvoice(item.id)
    message.success('删除成功')
    handleClose(true)
  })
}

// 报销
const handleReimburse = (item) => {
  emit('generalExpense', item)
  handleClose()
}

// 关闭
const handleClose = (refresh?) => {
  let refreshFlag = refresh ? true : false
  // diaTitle.value = '新增'
  dialogFormVisible.value = false
  ruleForm = reactive({
    reimbType: 1,
    invoiceAmount: '',
    reimbAmount: '',
    reimbItem: '',
    invoice: []
  })
  let option = {
    refresh: refreshFlag
  }
  eventBus.emit('closeDialog', option)
  // emit('close', false)
}

watch(
  () => props.dialogOption,
  (newValue, oldValue) => {
    console.log(`1 ${oldValue} to ${newValue}`)
  }
)
let unsubscribe
onMounted(() => {
  unsubscribe = eventBus.on('addCard', (data) => {
    dialogFormVisible.value = data.visible
    diaTitle.value = data.diaTitle
    dialogDetails.value = data.item
  })
})
onUnmounted(() => {
  // unsubscribe()
})
</script>

<style scoped lang="scss">
.table-box {
  width: 100%;
  display: flex;
  justify-content: space-between;

  .left-table {
    width: 100%;

    .demo-tabs {
      // margin-top: 10px;
    }
  }
}

// .el-tabs__header {
//   margin: 0 !important;
// }

// ::v-deep .el-dialog__body {
//   padding: 0 !important;
// }

// ::v-deep .el-form-item,
// .el-form-item--default {
//   margin-bottom: 0 !important;
// }

// ::v-deep .el-dialog {
//   margin: 1px 2px !important;
// }
</style>
