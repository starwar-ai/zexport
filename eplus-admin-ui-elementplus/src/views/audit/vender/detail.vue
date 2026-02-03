<template>
  <ContentWrap>
    <!-- 审批信息 -->
    <el-card class="box-card">
      <template #header>
        <span class="el-icon-picture-outline">审批任务</span>
      </template>
      <el-col
        :offset="4"
        :span="16"
      >
        <el-form
          ref="form"
          label-width="100px"
          :model="auditForm"
          :rules="auditRule"
        >
          <el-form-item
            label="供应商名称"
            prop="id"
            >{{ auditForm.name }}</el-form-item
          >
          <el-form-item label="发起人">{{ userName }}</el-form-item>
          <el-form-item
            label="审批建议"
            prop="auditForm.reason"
          >
            <el-input
              placeholder="请输入审批建议"
              type="textarea"
              v-model="auditForm.reason"
            />
          </el-form-item>
          <div style="margin-bottom: 20px; margin-left: 10%; font-size: 14px">
            <el-button
              type="success"
              @click="handleAudit(true)"
            >
              <Icon icon="ep:select" />
              通过
            </el-button>
            <el-button
              type="danger"
              @click="handleAudit(false)"
            >
              <Icon icon="ep:close" />
              不通过
            </el-button>
            <el-button
              type="warning"
              @click="handleBack()"
            >
              <Icon icon="ep:back" />
              回退
            </el-button>
          </div>
        </el-form>
      </el-col>
    </el-card>
    <!-- 详细信息 -->
    <el-card class="box-card">
      <template #header>
        <span class="el-icon-document">详情信息</span>
      </template>
      <ScmVenderDetail />
      <!--详情结束-->
    </el-card>

    <!-- 审批记录 -->
    <ProcessInstanceTaskList :tasks="tasks" />
    <!-- 弹窗，回退节点 -->
    <TaskReturnDialog ref="taskReturnDialogRef" />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { useUserStore } from '@/store/modules/user'
import ScmVenderDetail from '@/views/scm/vender/components/VenderDetail.vue'
import ProcessInstanceTaskList from '@/views/audit/components/ProcessInstanceTaskList.vue'
import TaskReturnDialog from '@/views/audit/components/TaskReturnDialogForm.vue'
import * as VenderApi from '@/api/scm/vender'
import { examineApprove, examineReject } from '@/api/audit/vender'

defineOptions({ name: 'AuditDetail' })

const { query } = useRoute() // 查询参数
const message = useMessage() // 消息弹窗
// const userId = useUserStore().getUser.id // 当前登录的编号
const userName = useUserStore().getUser.nickname // 当前登录的编号
const id = query.id as unknown as number // 流程实例的编号
const taskId = query.taskId as unknown as string
const tasks = ref<any[]>([]) // 任务列表
// ========== 审批信息 ==========
const auditForm = reactive({
  ...query,
  reason: ''
}) // 审批任务的表单
const auditRule = reactive({
  reason: [{ required: true, message: '审批建议不能为空', trigger: 'blur' }]
})
// ========== 申请信息 ==========

/** 处理审批通过和不通过的操作 */
const handleAudit = async (pass) => {
  if (!auditForm.reason) {
    message.error('审批建议不能为空')
    return
  }

  const data = {
    id: taskId,
    reason: auditForm.reason
  }
  if (pass) {
    await examineApprove(data)
    message.success('审批通过成功')
  } else {
    await examineReject(data)
    message.success('审批不通过成功')
  }
}

//回退弹框组件
const taskReturnDialogRef = ref()
/** 处理审批退回的操作 */
const handleBack = async () => {
  taskReturnDialogRef.value.open(id)
}

/** 获取详情 */
const vender = ref<VenderApi.VenderVO>({} as VenderApi.VenderVO) // 客户详情
const getVender = async (id: number) => {
  try {
    vender.value = await VenderApi.getVender(id)
  } finally {
  }
}
/** 获得详情结束 */

/** 初始化 */
onMounted(() => {
  getVender(id)
})
</script>
<style lang="scss" scoped>
:deep(.el-descriptions) {
  &:not(:nth-child(1)) {
    margin-top: 20px;
  }

  .el-descriptions__title {
    display: flex;
    align-items: center;

    &::before {
      display: inline-block;
      width: 3px;
      height: 20px;
      margin-right: 10px;
      background-color: #409eff;
      content: '';
    }
  }

  .el-descriptions__body {
    margin: 5px 10px;
  }

  .el-descriptions-item__container {
    margin: 0 10px;

    .no-colon {
      margin: 0;

      &::after {
        content: '';
      }
    }
  }
}

// 时间线样式调整
:deep(.el-timeline) {
  margin: 10px 0 0 160px;

  .el-timeline-item__wrapper {
    position: relative;
    top: -20px;

    .el-timeline-item__timestamp {
      position: absolute !important;
      top: 10px;
      left: -150px;
    }
  }

  .el-timeline-right-content {
    display: flex;
    align-items: center;
    min-height: 30px;
    padding: 10px;
    background-color: #f7f8fa;

    &::before {
      position: absolute;
      top: 10px;
      left: 13px;
      /* 将伪元素水平居中 */
      border-color: transparent #f7f8fa transparent transparent;
      /* 尖角颜色，左侧朝向 */
      border-style: solid;
      border-width: 8px;
      /* 调整尖角大小 */
      content: '';
      /* 必须设置 content 属性 */
    }
  }

  .dot-node-style {
    position: absolute;
    left: -5px;
    display: flex;
    width: 20px;
    height: 20px;
    font-size: 10px;
    color: #fff;
    border-radius: 50%;
    justify-content: center;
    align-items: center;
  }
}
</style>
