<template>
  <Dialog
    :title="dialogTitle"
    v-model="dialogVisible"
    width="40%"
  >
    <div>
      <Table
        height="500"
        v-if="tipsFlag"
        :columns="pocColumns"
        headerAlign="center"
        align="center"
        :data="tableData"
      />
      <p v-else> 未检测到后继续影响流程单据，点击确认直接变更</p>
    </div>

    <template #footer>
      <el-button
        @click="submitForm"
        type="primary"
        :disabled="formLoading"
        v-if="!submitFlag"
      >
        确 定
      </el-button>
      <el-button
        @click="openConfirmMsg"
        type="primary"
        :disabled="formLoading"
        v-if="submitFlag"
      >
        提 交
      </el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn } from '@/utils/table'
import * as CustApi from '@/api/crm/cust'
import * as SkuApi from '@/api/pms/main/index'
import * as VenderApi from '@/api/scm/vender'

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('变更影响') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用

const tipsFlag = ref(false)

const tableData = ref([]) // 详情

const pocColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'effectRangeCode',
    label: '变更来源编号'
  },
  {
    field: 'effectRangeType',
    label: '变更来源类型',
    formatter: formatDictColumn(DICT_TYPE.EFFECT_RANGE)
  }
])

/** 打开弹窗 */
const open = async (data, channel = 'other') => {
  formLoading.value = true
  handleChangeEffect(data, channel)
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗
const submitFlag = ref(0)
const setData = (data) => {
  tableData.value = Array.isArray(data.effectRangeList)
    ? data.effectRangeList.slice(0, 500)
    : data.effectRangeList
  submitFlag.value = data.submitFlag
}
const handleChangeEffect = async (data, channel) => {
  if (channel === 'other') {
    setData(data)
  } else if (channel === 'cust') {
    let res = await CustApi.getCustChangeEffect(data)
    setData(res)
  } else if (channel === 'vender') {
    let res = await VenderApi.createVenderEffect(data)
    setData(res)
  } else if (channel === 'sku') {
    let res = await SkuApi.createSkuEffect(data)
    setData(res)
  }

  dialogVisible.value = true
  formLoading.value = false

  tipsFlag.value = tableData.value.length ? true : false
}

/** 提交表单 */
const emit = defineEmits(['submitChange']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  formLoading.value = true
  try {
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('submitChange')
  } finally {
    formLoading.value = false
  }
}

const openConfirmMsg = () => {
  ElMessageBox.confirm('确认提交审核吗?', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      submitForm()
    })
    .catch(() => {})
}
</script>
<style scoped lang="scss">
.code-box {
  padding-bottom: 5px;
  border-bottom: 1px solid #ccc;

  .title-tips {
    border-bottom: 1px solid #ccc;
  }

  .order-code {
    display: inline-block;
    margin: 1px 25px;
  }
}
</style>
