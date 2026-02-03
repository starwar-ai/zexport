<template>
  <Dialog
    v-model="dialogVisible"
    title="导出"
    width="500"
    append-to-body
    destroy-on-close
    @close="handleCancel"
  >
    <el-form
      ref="formRef"
      :model="form"
      label-width="100"
    >
      <el-form-item
        label="客户名称"
        prop="custCode"
        :rules="[
          {
            required: true,
            message: '请选择客户名称'
          }
        ]"
      >
        <el-select
          v-model="form.custCode"
          placeholder="请选择"
          style="width: 300px"
        >
          <el-option
            v-for="item in custList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
import * as ShipmentApi from '@/api/dms/shipment/index'
import download from '@/utils/download'

defineOptions({ name: 'ExportDia' })
const message = useMessage()
const formRef = ref()
const dialogVisible = ref(false)
const form = reactive({
  id: undefined,
  custCode: undefined,
  reportCode: 'dms_shipment'
})

const uniqueArr = (arr) => {
  return Array.from(new Set(arr.map((item) => JSON.stringify(item)))).map((item) =>
    JSON.parse(item)
  )
}
const custList = ref([])
const pageInfo = ref({})
const open = async (row) => {
  custList.value = []
  pageInfo.value = cloneDeep(row)
  form.id = pageInfo.value.id

  nextTick(() => {
    custList.value = uniqueArr(
      pageInfo.value?.children.map((item) => {
        return {
          label: item.custName,
          value: item.custCode
        }
      })
    )
    if (custList.value.length > 1) {
      dialogVisible.value = true
    } else {
      form.custCode = custList.value[0].value
      handleExport()
      return
    }
  })
}

const handleCancel = () => {
  formRef.value.resetFields()
  dialogVisible.value = false
}

const emit = defineEmits(['sure'])
const handleSure = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      handleExport()
      handleCancel()
    }
  })
}

const handleExport = async () => {
  let params = { ...form, exportType: 'bookingNote' }
  let res = await ShipmentApi.exportShipmentDetail(params)
  let exportName = pageInfo.value?.custPo || pageInfo.value?.saleContractCodes || ''
  download.excel(res, `托单${exportName}.xlsx`)
  message.success('导出成功')
  emit('sure')
}
defineExpose({ open })
</script>
