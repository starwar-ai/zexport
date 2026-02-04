<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="title"
    width="600"
    append-to-body
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <!-- 批量回填费用 -->
      <el-form-item
        label=""
        prop="resource"
        v-if="currType == 2"
      >
        <uploadFile
          ref="UploadRef"
          :fileList="formData?.annex"
          :uploadUrl="EmsListApi.sendBillImport"
          @success="getFileList"
        />
      </el-form-item>
      <!--单个 回填费用 -->
      <el-form-item
        label=""
        prop="resource"
        v-if="currType == 1"
      >
        <EplusMoney
          v-model:amount="formData.cost.amount"
          v-model:currency="formData.cost.currency"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          提交
        </el-button>
      </div>
    </template>
    <!-- 回填费用列表 -->
    <el-dialog
      v-model="dialogVisible"
      title="确认回填运费"
      width="1000"
      :before-close="handleClose"
      append-to-body
    >
      <el-table
        :data="tableData"
        style="width: 100%"
        height="400"
        border
      >
        <el-table-column
          prop="sendCode"
          label="寄件编号"
        />

        <el-table-column
          prop="inputUserName"
          label="录入人姓名"
        />

        <el-table-column
          prop="receiveMsg"
          label="收件人信息"
          show-overflow-tooltip
        />
        <el-table-column
          prop="venderName"
          label="快递公司"
        />
        <el-table-column
          prop="emsCode"
          label="物流单号"
        />
        <el-table-column
          prop="cost"
          label="导入费用"
        />
        <el-table-column
          prop="remark"
          label="备注"
        />
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleBackFill()"
          >
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>
  </Dialog>
</template>
<script setup lang="tsx">
import uploadFile from './uploadFile.vue'

import * as EmsListApi from '@/api/ems/emsList' // 寄件接口集合

const dialogVisible = ref(false)

defineOptions({ name: 'SelectProductDialog' })
const message = useMessage()
const title = ref('回填费用')
const currType = ref(1)
const currId = ref('')
const currRow = ref({})
const formRules = reactive({})
const dialogTableVisible = ref(false)
const tableData = ref([])

const formData = reactive({
  cost: {
    amount: 0,
    currency: 'RMB'
  }
})

const UploadRef = ref()
const getFileList = (params) => {
  tableData.value = params.sendBillList
}

provide('formData', formData)

const emit = defineEmits<{
  sure: [link: string]
}>()

//回填费用-批量
const handleBackFill = async () => {
  let sendBillRespVOList = tableData.value
  await EmsListApi.backfillImport(sendBillRespVOList)
    .then(() => {
      message.success('提交成功')
      dialogVisible.value = false
      dialogTableVisible.value = false
      emit('success')
    })
    .catch(() => {
      message.error('提交失败')
    })
}

// 回填费用 -单条
const handleUpdateCose = async () => {
  let data = {
    id: currId.value,
    cost: formData.cost
  }

  await EmsListApi.updateCost({ ...data })
    .then(() => {
      message.success('提交成功')
      dialogTableVisible.value = false
      emit('success')
    })
    .catch(() => {
      message.error('提交失败')
    })
}

const open = async (id?: number, type?: number, row?: any) => {
  currType.value = type
  currId.value = id
  currRow.value = row
  dialogTableVisible.value = true
  switch (type) {
    case 1:
      title.value = '回填费用'
      break
    case 2:
      title.value = '快递信息更新'
      break
  }
}

const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}
const handleSure = async () => {
  switch (currType.value) {
    case 1:
      // title.value = '回填费用'
      handleUpdateCose()

      break
    case 2:
      dialogVisible.value = true
      break
  }
}

defineExpose({ open })
</script>
<style scoped lang="scss">
.designerSty {
  display: inline-block;
  margin-left: 50px;
}

.lines {
  width: 100%;
  height: 0;
  border-top: 0.5px solid #ebedf0;
}

.uploadExcelContent {
  display: flex;
  flex-direction: row;
}
.excel-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  .comp {
    width: 200px;
    font-size: 12px;
  }
}
</style>
