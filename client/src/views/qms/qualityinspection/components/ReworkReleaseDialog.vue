<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="title"
    width="1100"
    append-to-body
    destroy-on-close
    align-center
  >
    <div class="pb50px">
      <eplus-form-table
        ref="tableRef"
        :list="tableData"
        :schema="currType == 1 ? ReworkTableSchema : tableSchema"
        :defaultVal="{}"
        selectionFlag
        @selection-change="handleSelectionChange"
      />
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-if="currType == 1"
    >
      <!-- <el-form-item
        label="返工证据"
        prop="reworkPicture"
      >
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.reworkPicture"
          @success="getPicList"
        />
      </el-form-item> -->

      <!-- <el-form-item
        label="返工说明"
        prop="reworkDesc"
      >
        <el-input
          v-model="formData.reworkDesc"
          :rows="2"
          type="textarea"
          placeholder="请输入返工说明"
        />
      </el-form-item> -->
      <el-form-item
        label="期望重验日期"
        prop="reworkInspectionTime"
      >
        <el-date-picker
          v-model="formData.reworkInspectionTime"
          value-format="x"
          type="date"
          placeholder="请选择期望重验日期"
          :disabled-date="disabledDate"
        />
      </el-form-item>
    </el-form>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-else
    >
      <el-form-item
        label="是否工厂保函"
        prop="annexFlag"
      >
        <EplusDictRadio
          v-model="formData.annexFlag"
          :dictType="DICT_TYPE.IS_INT"
        />
      </el-form-item>
      <el-form-item
        label="接受说明"
        prop="description"
      >
        <el-input
          v-model="formData.description"
          :rows="2"
          type="textarea"
          placeholder="请输入接受说明"
        />
      </el-form-item>
      <el-form-item
        label="工厂保函"
        prop="factoryFile"
        v-if="formData.annexFlag == 1"
      >
        <UploadList
          ref="UploadRef"
          :fileList="formData?.factoryFile"
          @success="getFileList"
        />
      </el-form-item>
      <el-form-item
        label="图片"
        prop="picture"
      >
        <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.picture"
          @success="
            (list) => {
              formData.picture = list
            }
          "
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          v-if="currType == 1"
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
        <el-button
          v-if="currType == 2"
          type="primary"
          @click="handleRelease"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as QualityInspectionApi from '@/api/qms/qualityinspection' // 验货单接口集合
import UploadList from '@/components/UploadList/index.vue'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'ReworkReleaseDialog' })
const message = useMessage()
const title = ref('返工重验')
const currType = ref(1)
const formRef = ref()

const formRules = reactive({
  reworkInspectionTime: [
    { type: 'date', required: true, message: '期望重验日期不能为空', trigger: 'change' }
  ],
  reworkDesc: [{ required: true, message: '请输入返工说明', trigger: 'blur' }],

  annexFlag: [{ required: true, message: '是否工厂保函不能为空' }],
  description: [{ required: true, message: '请输入接受说明', trigger: 'blur' }],
  factoryFile: [{ required: true, message: '工厂保函不能为空' }]
})

const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
const parentList = ref([])
const loading = ref(false)
const formData = reactive({})

provide('formData', formData)

const emit = defineEmits(['handleSuccess'])

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}

const getList = async (row) => {
  loading.value = true
  if (isValidArray(row.children)) {
    tableData.value = row.children.filter(
      (item) => [1, 2].includes(item?.inspectionStatus) && item?.handleFlag === 0
    )
  } else {
    tableData.value = [row]
  }
  loading.value = false
}
// const toggleSelection = () => {
//   tableData.value.forEach((item) => {
//     tableRef.value.toggleRowSelection(item, undefined)
//   })
// }

const tableSchema = reactive([
  {
    label: '验货结果',
    field: 'inspectionStatus',
    minWidth: 100,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, row.inspectionStatus)
    }
  },
  {
    label: '问题描述',
    field: 'failDesc',
    minWidth: 150
  },
  {
    label: '采购合同',
    field: 'purchaseContractCode',
    minWidth: 150,
    formatter: (item, row, index) => {
      return row?.parent?.purchaseContractCode
    }
  },
  {
    label: '产品编号',
    field: 'skuCode',
    minWidth: 150
  },
  {
    label: '客户货号',
    field: 'cskuCode',
    minWidth: 150
  },
  {
    label: '产品名称',
    field: 'skuName',
    minWidth: 150
  }
])
const ReworkTableSchema = reactive([
  ...tableSchema,
  {
    label: '返工说明',
    field: 'reworkDesc',
    component: (
      <el-input
        autosize
        type="textarea"
      />
    ),
    minWidth: 150,
    rules: { required: true, message: '请输入返工说明' }
  }
])

const open = async (row, type) => {
  currType.value = type
  title.value = type == 1 ? '返工重验' : '让步放行'
  dialogTableVisible.value = true
  await getList(row)
  if (type == 2) {
    formData.annexFlag = 1
  }
  nextTick(() => {
    tableRef.value?.handleAllToggleRowSelection()
  })
}

// const getPicList = (params) => {
//   formData.reworkPicture = params
// }

const getFileList = (params) => {
  formData.factoryFile = params
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleCancel = () => {
  tableData.value = []
  formData.description = ''
  formData.factoryFile = []
  formData.picture = []
  selectedList.value = []
  dialogTableVisible.value = false
}
const handleSure = async () => {
  let tableValid = await tableRef.value.validate()
  if (!tableValid) {
    message.warning('请填写返工说明')
    return false
  }
  let valid = await formRef.value.validate()
  if (!valid) return false
  if (!isValidArray(selectedList.value)) {
    message.warning('请选择至少一条数据')
    return false
  }
  QualityInspectionApi.qmsRework({
    ...formData,
    itemList: tableData.value.map((e: any) => {
      return { itemId: e.id, reworkDesc: e.reworkDesc }
    })
  })
    .then(() => {
      message.success('提交成功')
      handleCancel()
      emit('handleSuccess')
    })
    .catch(() => {
      message.error('提交失败')
    })
}

const handleRelease = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  // formData.annex = formData.factoryFile
  if (!isValidArray(selectedList.value)) {
    message.warning('请选择至少一条数据')
    return false
  }
  var res = await QualityInspectionApi.qmsRelease({
    ...formData,
    submitFlag: 1,
    qualityInspectionItemIdList: selectedList.value.map((e) => e.id)
  })
    .then(() => {
      // message.success('提交成功')
      message.notifyPushSuccess('成功让步放行', res, 'purchase-release')
      handleCancel()
      emit('handleSuccess')
    })
    .catch(() => {
      message.error('提交失败')
    })
}
defineExpose({ open })
</script>
