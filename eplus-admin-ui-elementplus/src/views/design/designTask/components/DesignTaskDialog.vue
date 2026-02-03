<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="title"
    width="1100"
    append-to-body
    destroy-on-close
  >
    <div
      class="pb50px"
      v-if="currType == 2"
    >
      <el-table
        border
        :data="tableData"
        row-key="code"
        ref="tableRef"
        v-loading="loading"
      >
        <el-table-column
          prop="designerName"
          label="认领人"
          width="180"
        />

        <el-table-column
          prop="progress"
          label="当前进度"
        >
          <template #default="scope">
            <!-- <el-input
              v-if="scope.row.current"
              v-model="scope.row.progress"
              style="width: 240px"
              placeholder="当前进度"
            />
            <span v-else>{{ scope.row.progress }}</span> -->
            <eplus-dict-select
              v-model="scope.row.progress"
              :dictType="DICT_TYPE.PROGRESS_TYPE"
              :clearable="false"
              style="width: 240px"
              :disabled="!scope.row.current"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="进度描述"
          prop="progressDesc"
          show-overflow-tooltip
          ><template #default="scope">
            <el-input
              v-if="scope.row.current"
              v-model="scope.row.progressDesc"
              style="width: 240px"
              placeholder="进度描述"
            />
            <span v-else>{{ scope.row.progressDesc }}</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="填写时间"
          prop="skuCode"
          ><template #default="scope">
            <el-date-picker
              v-model="scope.row.createTime"
              type="datetime"
              valueFormat="x"
              :size="size"
              disabled
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      label-width="120px"
      v-if="currType == 1"
    >
      <!-- 完成提示 -->

      <el-form-item
        label="完成任务类型"
        prop="resource"
      >
        <el-radio-group v-model="formData.itemType">
          <el-radio label="1">常规任务</el-radio>
          <el-radio label="2">临时任务</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        label="文件位置"
        prop="designFilePath"
        v-if="formData.itemType == 1"
        :rules="[
          {
            required: true,
            message: '请填写文件位置',
            trigger: 'blur'
          }
        ]"
      >
        <el-input v-model="formData.designFilePath" />
      </el-form-item>

      <el-form-item
        label="实际完成日期"
        prop="completeDate"
        :rules="[
          {
            type: 'date',
            required: true,
            message: '实际完成日期不能为空',
            trigger: 'change'
          }
        ]"
      >
        <el-date-picker
          v-model="formData.completeDate"
          valueFormat="x"
          type="date"
          placeholder="请选择实际完成日期"
          @change="changeDate"
        />
      </el-form-item>
      <el-form-item
        label="异常说明"
        prop="abnormalExplain"
        :rules="[
          {
            required: true,
            message: '异常说明不能为空',
            trigger: 'blur'
          }
        ]"
        v-if="abnormalFlag"
      >
        <el-input v-model="formData.abnormalExplain" />
      </el-form-item>
    </el-form>
    <el-form
      ref="formRef"
      :model="evaluateData"
      label-width="120px"
      v-if="currType == 3"
    >
      <div
        v-for="(item, index) in evaluateData.designerList"
        :key="item.id"
      >
        <span class="designerSty">{{ item.designerName }}</span>
        <el-form-item label="">
          <el-radio-group v-model="item.evaluateResult">
            <el-radio label="1">投诉</el-radio>
            <el-radio label="2">优秀</el-radio>
            <el-radio label="3">点赞</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          label="描述"
          :prop="`designerList.${index}.evaluateDesc`"
          :rules="[
            {
              required: item.evaluateResult == 2 ? false : true,
              message: '描述不能为空',
              trigger: 'blur'
            }
          ]"
        >
          <el-input
            v-model="item.evaluateDesc"
            style="width: 240px"
            :rows="2"
            type="textarea"
            placeholder="评价描述"
          />
        </el-form-item>
        <p class="lines"></p>
      </div>
    </el-form>
    <el-form
      :model="formData"
      label-width="120px"
      v-if="currType == 4"
    >
      <el-form-item
        label="作废原因"
        prop="closeReason"
      >
        <el-input
          v-model="formData.closeReason"
          :rows="2"
          type="textarea"
          placeholder="作废原因"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure()"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { useUserStore } from '@/store/modules/user'
import * as DesignTaskApi from '@/api/dtms/designTask' // 设计任务单接口集合
defineOptions({ name: 'SelectProductDialog' })
const message = useMessage()
const title = ref('')
const currType = ref(1)
const currId = ref('')
const currRow = ref({})
const radio = ref('1') // 完成提示
const formRef = ref()
const UploadPicRef = ref()

const formRules = reactive({
  // evaluateDesc: [{ required: true, message: '描述不能为空', trigger: 'blur' }]
})

const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const summaryRes = ref([])
const abnormalFlag = ref(false)
const loading = ref(false)

const formData = reactive({
  itemType: '1'
})
const evaluateData = reactive({
  itemType: '1',
  designerList: []
})

provide('formData', formData)

const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})

const emit = defineEmits<{
  sure: [link: string]
}>()

const getList = async (id) => {
  loading.value = true
  const queryParams = {
    pageNo: 1,
    pageSize: 10,
    designId: id
  }
  let currUser = []
  let otherUser = []
  let res = await DesignTaskApi.getDesignSummary(queryParams)
  res.forEach((e) => {
    if (!isNaN(Number(e.progress))) {
      e.progress = Number(e.progress)
    }
    if (e.designerId == useUserStore().getUser.id) {
      currUser.push(e)
    } else {
      otherUser.push(e)
    }
  })
  let newArr = currUser.sort((a, b) => b.createTime - a.createTime)
  const now = new Date() // 获取当前时间
  now.setHours(0) // 将时间调整为零点
  now.setMinutes(0)
  now.setSeconds(0)
  now.setMilliseconds(0)
  const zeroTimeTimestamp = now.getTime() // 当天零点的时间戳
  if (!newArr.length) {
    newArr = [
      {
        designerDeptId: useUserStore().getUser.deptId,
        designerDeptName: useUserStore().getUser.deptName,
        designerId: useUserStore().getUser.id,
        designerName: useUserStore().getUser.nickname,
        designId: currId.value,
        progress: '',
        progressDesc: '',
        createTime: new Date(),
        current: true
      }
    ]
  } else {
    if (newArr[0].createTime < zeroTimeTimestamp) {
      newArr.unshift({
        designerDeptId: useUserStore().getUser.deptId,
        designerDeptName: useUserStore().getUser.deptName,
        designerId: useUserStore().getUser.id,
        designerName: useUserStore().getUser.nickname,
        designId: currId.value,
        progress: '',
        progressDesc: '',
        createTime: new Date(),
        current: true
      })
    } else {
      newArr[0].current = true
    }
  }

  tableData.value = [...newArr, ...otherUser]

  loading.value = false
}

// const designerList = ref([])
const getDesignItemPage = async (id) => {
  let params = {
    pageNo: 1,
    pageSize: 10,
    designId: id
  }
  evaluateData.designerList = await DesignTaskApi.designItemPage(params)

  evaluateData.designerList.forEach((e) => {
    e.evaluateResult = '2'
  })
}

const open = async (id, type, row?: any) => {
  currType.value = type
  currId.value = id
  currRow.value = row
  dialogTableVisible.value = true
  switch (type) {
    case 1:
      title.value = '完成提示'
      break
    case 2:
      title.value = '任务进度'
      await getList(id)
      break
    case 3:
      title.value = '评价提示'
      await getDesignItemPage(id)
      break
    case 4:
      title.value = '作废提示'
      break
  }
}

const changeDate = (val) => {
  abnormalFlag.value = val > currRow.value?.planCompleteDate ? true : false
}

const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}
const handleSure = async () => {
  switch (currType.value) {
    case 1:
      await getCompleteDesign()
      break
    case 2:
      await progressSubmit()
      break
    case 3:
      await submitEvaluateDesign()
      break
    case 4:
      await submitDesignUpdate()
      break
  }
}

//进度提交
const progressSubmit = async () => {
  let params = tableData.value.filter((item) => item.current == true)[0]
  params.designerDeptId = useUserStore().getUser.deptId
  params.designerDeptName = useUserStore().getUser.deptName
  await DesignTaskApi.getDesignSummaryCreate(params)
    .then(() => {
      message.success('提交成功')
      emit('handleSuccess', 'success')
    })
    .catch(() => {
      message.error('提交失败')
    })

  handleCancel()
}

// 完成设计

const getCompleteDesign = async () => {
  let params = { designId: currId.value, designerId: useUserStore().getUser.id, ...formData }
  const valid = await formRef.value.validate()
  if (!valid) return

  await DesignTaskApi.completeDesign(params)
    .then(() => {
      message.success('提交成功')
      emit('handleSuccess', 'success')
    })
    .catch(() => {
      message.error('提交失败')
    })
  handleCancel()
}

// 评价提交
const submitEvaluateDesign = async () => {
  let data = []

  evaluateData.designerList.forEach((e) => {
    data.push({
      id: e.id,
      designId: e.designId,
      evaluateResult: e.evaluateResult,
      evaluateDesc: e.evaluateDesc
    })
  })
  const valid = await formRef.value.validate()
  if (!valid) return
  await DesignTaskApi.evaluateDesign(data)
    .then(() => {
      message.success('提交成功')
      emit('handleSuccess', 'success')
    })
    .catch(() => {
      message.error('提交失败')
    })
  handleCancel()
}

// 作废
const submitDesignUpdate = async () => {
  let data = {
    id: currId.value,
    designStatus: 6,
    closeReason: formData.closeReason
  }
  await DesignTaskApi.designUpdate(data)
    .then(() => {
      message.success('作废成功')
      emit('handleSuccess', 'success')
    })
    .catch(() => {
      message.error('提交失败')
    })
  handleCancel()
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
</style>
