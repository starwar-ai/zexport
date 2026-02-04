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
            label="客户名称"
            prop="id"
            >{{ auditForm.auditName }}</el-form-item
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
      <!--详情开始-->
      <!--      <ContentWrap>-->
      <!--        &lt;!&ndash;主要信息&ndash;&gt;-->
      <!--        <el-descriptions title="主要信息">-->
      <!--          <el-descriptions-item label="企业名称:">{{ customer.name }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="客户编号:">{{ customer?.code }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="状态:">{{ customer?.auditStatus }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="创建人:">{{customer.creatorName}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="所属部门:">{{customer.terminal!}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="跟进人:">{{ customer.managerName }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="所属部门:">{{ customer.deptName }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="创建时间:">{{ formatDate(customer.createTime) }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="跟进时间:" dataformatas="dateFormatter">{{formatDate(customer.createTime)}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="客户等级:">{{ customer?.stageType }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="客户类型: ">{{customer.customerTypes}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="审核人: ">{{customer.pocName}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="审核时间:">{{ formatDate(customer.updateTime) }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="所属部门: ">{{ formatDate(customer.updateTime) }}</el-descriptions-item>-->
      <!--        </el-descriptions>-->
      <!--        &lt;!&ndash; 基础信息 &ndash;&gt;-->
      <!--        <el-descriptions title="基础信息">-->
      <!--          <el-descriptions-item label="客户编号: ">{{ customer.code }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="企业名称: ">{{ customer.name }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="公司主页: ">{{ customer.homepage }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="联系电话: ">{{customer.phone}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="邮箱地址: ">{{customer.email}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="运输方式: ">{{ customer.transportType }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="所属地区: ">{{ customer.remark }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="国家: ">{{ customer.countryId }}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="公司地址: ">{{customer.address}}</el-descriptions-item>-->
      <!--          &lt;!&ndash;      <el-descriptions-item label="主营产品: "> {{ customer.nickname }}</el-descriptions-item>&ndash;&gt;-->
      <!--          <el-descriptions-item label="客户来源:">{{customer.sourceType}}</el-descriptions-item>-->
      <!--          <el-descriptions-item label="备注信息: ">{{ customer.remark }}</el-descriptions-item>-->
      <!--        </el-descriptions>-->
      <!--        &lt;!&ndash; 联系人 &ndash;&gt;-->
      <!--        <el-descriptions title="联系人">-->
      <!--          <el-descriptions-item labelClassName="no-colon">-->
      <!--            <el-row :gutter="20">-->
      <!--              <el-col :span="24">-->
      <!--                <el-table :data="customer.pocRespList" border>-->
      <!--                  <el-table-column label="姓名" prop="name" />-->
      <!--                  <el-table-column label="岗位信息" prop="pocPosts" />-->
      <!--                  <el-table-column label="联系方式" prop="mobile" />-->
      <!--                  <el-table-column label="住宅地址" prop="address" />-->
      <!--                  <el-table-column label="邮箱地址" prop="email"  />-->
      <!--                  &lt;!&ndash;              <el-table-column label="备注信息" prop="count">{{rwo.mark}}</el-table-column>&ndash;&gt;-->
      <!--                </el-table>-->
      <!--              </el-col>-->
      <!--            </el-row>-->
      <!--          </el-descriptions-item>-->
      <!--        </el-descriptions>-->
      <!--        &lt;!&ndash; 银行信息 &ndash;&gt;-->
      <!--        <el-descriptions title="银行信息">-->
      <!--          <el-descriptions-item labelClassName="no-colon">-->
      <!--            <el-row :gutter="20">-->
      <!--              <el-col :span="24">-->
      <!--                <el-table :data="customer.bankaccountRespList" border>-->
      <!--                  <el-table-column label="创建时间" prop="createTime" />-->
      <!--                  <el-table-column label="客户ID" prop="custId" />-->
      <!--                  <el-table-column label="银行账户" prop="bankAccount" />-->
      <!--                  <el-table-column label="开户行" prop="bank" />-->
      <!--                  <el-table-column label="是否默认账户" prop="defaultFlag" />-->
      <!--                </el-table>-->
      <!--              </el-col>-->
      <!--            </el-row>-->
      <!--          </el-descriptions-item>-->
      <!--        </el-descriptions>-->
      <!--        &lt;!&ndash; 附件信息 &ndash;&gt;-->
      <!--        <el-descriptions title="附件信息">-->
      <!--          <el-descriptions-item labelClassName="no-colon">-->
      <!--            <el-row :gutter="20">-->
      <!--              <el-col :span="24">-->
      <!--                <el-table :data="customer.fileList" border>-->
      <!--                  <el-table-column label="文件编号" prop="id" />-->
      <!--                  <el-table-column label="配置编号" prop="configId" />-->
      <!--                  <el-table-column label="文件路径" prop="patch" />-->
      <!--                  <el-table-column label="name" prop="name" />-->
      <!--                  <el-table-column label="文件 URL" prop="url"  />-->
      <!--                  <el-table-column label="文件MIME类型" prop="type" />-->
      <!--                  <el-table-column label="文件大小" prop="size"  />-->
      <!--                  <el-table-column label="创建时间" prop="createTime" />-->
      <!--                </el-table>-->
      <!--              </el-col>-->
      <!--            </el-row>-->
      <!--          </el-descriptions-item>-->
      <!--        </el-descriptions>-->
      <!--        &lt;!&ndash; 图片信息 &ndash;&gt;-->
      <!--        <el-descriptions title="图片信息">-->
      <!--          <el-descriptions-item labelClassName="no-colon">-->
      <!--            <el-row :gutter="20">-->
      <!--              <el-col :span="24">-->
      <!--                <el-table :data="customer.pictureRespList" border>-->
      <!--                  <el-table-column label="客户ID" prop="custId" />-->
      <!--                  <el-table-column label="名称" prop="name" />-->
      <!--                  <el-table-column label="类型" prop="picType" />-->
      <!--                  <el-table-column label="保存位置" prop="picPath" />-->
      <!--                  <el-table-column label="创建时间" prop="createTime"  />-->
      <!--                </el-table>-->
      <!--              </el-col>-->
      <!--            </el-row>-->
      <!--          </el-descriptions-item>-->
      <!--        </el-descriptions>-->
      <!--        &lt;!&ndash; 唛头信息 &ndash;&gt;-->
      <!--        <el-descriptions title="唛头信息">-->
      <!--          <el-descriptions-item labelClassName="no-colon">-->
      <!--            <el-row :gutter="20">-->
      <!--              <el-col :span="24">-->
      <!--                <el-table :data="customer.markRespVOList" border>-->
      <!--                  <el-table-column label="id" prop="id" />-->
      <!--                  <el-table-column label="唛头名称" prop="name" />-->
      <!--                  <el-table-column label="唛头英文名称" prop="engName" />-->
      <!--                  <el-table-column label="主文字唛" prop="mainMarkText" />-->
      <!--                  <el-table-column label="主图形唛" prop="mainMarkPic"  />-->
      <!--                  <el-table-column label="主侧文字唛" prop="mainMarkTextSide" />-->
      <!--                  <el-table-column label="内主图形唛" prop="mainMarkPicIn" />-->
      <!--                </el-table>-->
      <!--              </el-col>-->
      <!--            </el-row>-->
      <!--          </el-descriptions-item>-->
      <!--        </el-descriptions>-->
      <!--      </ContentWrap>-->
      <CrmCustDetail />
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
import CrmCustDetail from '@/views/crm/cust/CustDetail.vue'
import ProcessInstanceTaskList from '@/views/audit/components/ProcessInstanceTaskList.vue'
import TaskReturnDialog from '@/views/audit/components/TaskReturnDialogForm.vue'
import * as CustApi from '@/api/crm/cust'
import { examineApprove, examineReject } from '@/api/audit/cust'

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
  id: id,
  reason: '',
  auditName: '',
  startUserId: ''
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
const cust = ref<CustApi.CustVO>({} as CustApi.CustVO) // 客户详情
const getCust = async (id: number) => {
  try {
    cust.value = await CustApi.getCustomer(id)
  } finally {
  }
}
/** 获得详情结束 */

/** 初始化 */
onMounted(() => {
  getCust(id)
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
