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
      v-if="currType == 1"
    >
      <el-table
        border
        :data="tableData"
        row-key="code"
        ref="tableRef"
        v-loading="loading"
      >
        <el-table-column
          prop="name"
          label="任务名称"
        />

        <el-table-column
          prop="specialPermissionFlag"
          label="是否紧急"
          width="140"
          ><template #default="scope">
            {{ scope.row.specialPermissionFlag == 1 ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="applyDesignerName"
          label="申请人"
          width="160"
        />

        <el-table-column
          align="center"
          label="计划开始日期"
          prop="planStartDate"
          ><template #default="scope">
            <el-date-picker
              v-model="scope.row.planStartDate"
              type="date"
              value-format="x"
              placeholder="选择计划开始日期"
              style="width: 160px"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="计划完成日期"
          prop="planCompleteDate"
          ><template #default="scope">
            <el-date-picker
              v-model="scope.row.planCompleteDate"
              type="date"
              value-format="x"
              placeholder="选择计划完成日期"
              :size="size"
              style="width: 160px"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="认领人"
          prop="skuCode"
          ><template #default="scope">
            <el-select
              v-model="scope.row.designerName"
              placeholder="Select"
              style="width: 160px"
              filterable
              @change="handleChange(scope.row.designerName, scope.$index)"
            >
              <el-option
                v-for="item in userList"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-if="currType == 2"
    >
      <!-- 变更认领人 -->
      <el-form-item
        label="变更认领人"
        prop="resource"
      >
        <el-select
          v-model="formData.claimee"
          filterable
          placeholder="Select"
          style="width: 240px"
        >
          <el-option
            v-for="item in claimeeData"
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
import * as DesignTaskApi from '@/api/dtms/designTask' // 设计任务单接口集合
import * as UserApi from '@/api/system/user'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'SelectProductDialog' })
const message = useMessage()
const title = ref('任务认领')
const currType = ref(1)
const radio = ref('1') // 完成提示

const UploadPicRef = ref()

const formRules = reactive({
  reworkInspectionTime: [{ required: true, message: '计划排验时间不能为空', trigger: 'blur' }],
  reworkDesc: [{ required: true, message: '请输入返工说明', trigger: 'blur' }],
  acceptDesc: [{ required: true, message: '请输入接受说明', trigger: 'blur' }],

  picture: [{ required: true, message: '状态不能为空', trigger: 'blur' }],
  reworkPicture: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})

const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const userList = ref<UserApi.UserVO[]>([])

const loading = ref(false)

const formData = reactive({
  resource: '1',
  trackUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  freight: { amount: '', currency: 'RMB' },
  otherCost: { amount: '', currency: 'RMB' },
  portName: ''
})
const claimeeData = reactive([
  {
    value: 'Option1',
    label: 'Option1'
  },
  {
    value: 'Option2',
    label: 'Option2'
  },
  {
    value: 'Option3',
    label: 'Option3'
  },
  {
    value: 'Option4',
    label: 'Option4'
  },
  {
    value: 'Option5',
    label: 'Option5'
  }
])

let inspectionMap = reactive({
  1: '待验货',
  2: '成功',
  3: '失败',
  4: '待定',
  5: '放行通过'
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

const getList = async () => {
  userList.value = await UserApi.getSimpleUserList()
  // userList.value.forEach((e) => {})
  // let tempList = cloneDeep(userList.value)
  // tempList.map((item, index, arr) => {
  //   if (tempList[index] && tempList[index]?.deptName !== tempList[index - 1]?.deptName) {
  //     index !== 0 && (tempList[index - 1].endDept = 1)
  //     tempList[index].firstDept = 1
  //   }
  //   userList.value = tempList
  // })
  // userList.value = tempList

  // filterList.value = tempList
  //
  // initList.value = cloneDeep(tempList)
}

const open = async (type, data) => {
  currType.value = type
  switch (type) {
    case 1:
      title.value = '任务认领'
      break
    case 2:
      title.value = '变更认领人'
      break
  }
  dialogTableVisible.value = true
  tableData.value = data
  let userData = useUserStore().getUser
  tableData.value.forEach((e) => {
    e.designerName = userData.nickname
    e.designerId = userData.id
    e.designerDeptId = userData.deptId
    e.designerDeptName = userData.deptName
    e.planStartDate = Date()
    e.planCompleteDate = e.expectCompleteDate
  })
  getList()
}
const handleChange = (val, index) => {
  let userInfo = userList.value.find((item) => item.id === val)
  tableData.value[index].designerId = userInfo.id
  tableData.value[index].designerName = userInfo.nickname
  tableData.value[index].designerDeptId = userInfo.deptId
  tableData.value[index].designerDeptName = userInfo.deptName
}

const selectedDiaData = ref([])

const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}
const handleSure = async () => {
  let len = tableData.value?.length

  let designItemSaveReqVO = []
  if (len > 0) {
    tableData.value.forEach((e, i) => {
      designItemSaveReqVO.push({
        designerId: e.designerId,
        designerName: e.designerName,
        designerDeptId: e.designerDeptId,
        designerDeptName: e.designerDeptName,
        designId: e.id,
        itemType: '',
        planStartDate: e.planStartDate,
        planCompleteDate: e.planCompleteDate
      })
    })

    await DesignTaskApi.createDesignTaskClaim(designItemSaveReqVO)
      .then(() => {
        message.success('提交成功')
        emit('handleSuccess', 'success')
      })
      .catch(() => {
        message.error('提交失败')
      })
    handleCancel()
  } else {
    message.warning('请选择数据')
  }
}
defineExpose({ open })
</script>
<style lang="scss">
.header_input {
  border-bottom: 1px solid #eee;
  padding: 5px 10px 10px;
}

.empty_box {
  color: #666;
  text-align: center;
  line-height: 50px;
}

.option_box {
  overflow: auto;
  max-height: 200px;
}

.eplus-popper {
  min-width: 200px;
}
/* stylelint-disable-next-line rule-empty-line-before */
.deptName {
  padding-left: 12px;
  display: block;
  width: 100%;
  box-sizing: border-box;
  line-height: 30px;
  font-size: 14px;
}
/* stylelint-disable-next-line rule-empty-line-before */
.line {
  width: 100%;
  height: 1px;
  background-color: #eee;
  margin: 5px 0;
}
</style>
