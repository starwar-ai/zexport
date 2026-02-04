<template>
  <ContentWrap>
    <!-- 审批信息 -->
    <el-card
      v-for="(item, index) in runningTasks"
      :key="index"
      v-loading="processInstanceLoading"
      class="box-card"
    >
      <template #header>
        <span class="el-icon-picture-outline">审批任务【{{ item.name }}】</span>
      </template>
      <el-col
        :offset="6"
        :span="16"
      >
        <el-form
          :ref="'form' + index"
          :model="auditForms[index]"
          :rules="auditRule"
          label-width="100px"
        >
          <el-form-item
            v-if="processInstance && processInstance.name"
            label="流程名"
          >
            {{ processInstance.name }}
          </el-form-item>
          <el-form-item
            v-if="processInstance && processInstance.startUser"
            label="流程发起人"
          >
            {{ processInstance.startUser.nickname }}
            <!--            <el-tag size="small" type="info">{{ processInstance.startUser.deptName }}</el-tag>-->
          </el-form-item>
          <el-form-item
            label="是否通过"
            prop="isPass"
          >
            <el-radio-group v-model="auditForms[index].isPass">
              <el-radio :label="1">通过</el-radio>
              <el-radio :label="2">不通过</el-radio>
              <el-radio :label="3">转派</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="审批建议"
            prop="reason"
            v-if="auditForms[index].isPass === 2"
          >
            <el-input
              v-model="auditForms[index].reason"
              placeholder="请输入审批建议"
              type="textarea"
            />
          </el-form-item>
          <el-form-item
            label="审批建议"
            v-if="auditForms[index].isPass === 1"
          >
            <el-input
              v-model="auditForms[index].reason"
              placeholder="请输入审批建议"
              type="textarea"
            />
          </el-form-item>
          <el-form-item
            label="被转派人"
            prop="assigneeUserId"
            v-if="auditForms[index].isPass === 3"
          >
            <eplus-user-select v-model="auditForms[index].assigneeUserId" />
          </el-form-item>
          <el-form-item
            label="转派原因"
            prop="reason"
            v-if="auditForms[index].isPass === 3"
          >
            <el-input
              v-model="auditForms[index].reason"
              placeholder="请输入转派原因"
              type="textarea"
            />
          </el-form-item>
        </el-form>
        <div style="margin-bottom: 20px; margin-left: 10%; font-size: 14px">
          <el-button
            type="primary"
            @click="handleAudit(item, auditForms[index].isPass)"
          >
            确认
          </el-button>
          <!-- <el-button
            type="success"
            @click="handleAudit(item, true)"
          >
            <Icon icon="ep:select" />
            通过
          </el-button>
          <el-button
            type="danger"
            @click="handleAudit(item, false)"
          >
            <Icon icon="ep:close" />
            不通过
          </el-button> -->
        </div>
      </el-col>
    </el-card>
    <!-- 申请信息 -->
    <el-card
      v-loading="processInstanceLoading"
      class="box-card"
    >
      <template #header>
        <span class="el-icon-document">详细信息</span>
      </template>
      <!-- 情况一：流程表单 -->
      <!--      <el-col v-if="processInstance?.processDefinition?.formType === 10" :offset="6" :span="16">-->
      <!--        <form-create-->
      <!--          ref="fApi"-->
      <!--          v-model="detailForm.value"-->
      <!--          :option="detailForm.option"-->
      <!--          :rule="detailForm.rule"-->
      <!--        />-->
      <!--      </el-col>-->
      <!-- 情况二：业务表单 -->
      <div
        :class="{
          setH: ['scm_purchase_contract_own_brand'].includes(approveSource)
        }"
      >
        <!--        <BusinessFormComponent :id="processInstance.businessKey" />-->
        <!-- type  区分 1 外销合同 2 内销合同 -->
        <detallCompent
          ref="detallCompentRef"
          readonly
          :type="
            approveSource === 'sms_sale_contract_change'
              ? 1
              : approveSource === 'sms_domestic_sale_contract_change'
                ? 2
                : undefined
          "
        />
      </div>
    </el-card>

    <!-- 审批记录 -->
    <ProcessInstanceTaskList
      :loading="tasksLoad"
      :tasks="tasks"
    />

    <!-- 高亮流程图 -->
    <ProcessInstanceBpmnViewer
      :id="`${id}`"
      :bpmn-xml="bpmnXML"
      :loading="processInstanceLoading"
      :process-instance="processInstance"
      :tasks="tasks"
    />

    <!-- 弹窗：转派审批人 -->
    <TaskUpdateAssigneeForm
      ref="taskUpdateAssigneeFormRef"
      @success="getDetail"
    />
    <!-- 弹窗，回退节点 -->
    <TaskReturnDialog
      ref="taskReturnDialogRef"
      @success="getDetail"
    />
    <!-- 委派，将任务委派给别人处理，处理完成后，会重新回到原审批人手中-->
    <TaskDelegateForm
      ref="taskDelegateForm"
      @success="getDetail"
    />
    <!-- 加签，当前任务审批人为A，向前加签选了一个C，则需要C先审批，然后再是A审批，向后加签B，A审批完，需要B再审批完，才算完成这个任务节点 -->
    <TaskAddSignDialogForm
      ref="taskAddSignDialogForm"
      @success="getDetail"
    />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { nextTick } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { setConfAndFields2 } from '@/utils/formCreate'
import type { ApiAttrs } from '@form-create/element-ui/types/config'
import * as DefinitionApi from '@/api/bpm/definition'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import * as TaskApi from '@/api/bpm/task'
import TaskUpdateAssigneeForm from './TaskUpdateAssigneeForm.vue'
import ProcessInstanceBpmnViewer from './ProcessInstanceBpmnViewer.vue'
import ProcessInstanceTaskList from './ProcessInstanceTaskList.vue'
import TaskReturnDialog from './TaskReturnDialogForm.vue'
import TaskDelegateForm from './TaskDelegateForm.vue'
import TaskAddSignDialogForm from './TaskAddSignDialogForm.vue'
import { registerComponent } from '@/utils/routerHelper'
import { isEmpty, isValidArray } from '@/utils/is'
import router from '@/router'
import { useTagsViewStore } from '@/store/modules/tagsView'

defineOptions({ name: 'BpmProcessInstanceDetail' })

const { query } = useRoute() // 查询参数
const message = useMessage() // 消息弹窗
const { proxy } = getCurrentInstance() as any
const approveSource: string = (query.processDefinitionKey as string) || ''
const dateNum = query.index as unknown as number
const detallCompent = ref()
const detallCompentRef = ref()
const { delView } = useTagsViewStore() // 视图操作
const { currentRoute } = useRouter()

const userId = useUserStore().getUser.id // 当前登录的编号
const id = query.id as unknown as number // 流程实例的编号
const processInstanceLoading = ref(false) // 流程实例的加载中
const processInstance = ref<any>({}) // 流程实例
const bpmnXML = ref('') // BPMN XML
const tasksLoad = ref(true) // 任务的加载中
const tasks = ref<any[]>([]) // 任务列表
// ========== 审批信息 ==========
const runningTasks = ref<any[]>([]) // 运行中的任务
const auditForms = ref<any[]>([]) // 审批任务的表单
const auditRule = reactive({
  reason: [{ required: true, message: '此项不能为空', trigger: 'blur' }],
  assigneeUserId: [{ required: true, message: '此项不能为空', trigger: 'change' }]
})
// ========== 申请信息 ==========
const fApi = ref<ApiAttrs>() //
const detailForm = ref({
  // 流程表单详情
  rule: [],
  option: {},
  value: {}
})

const custDetailsUrl = 'crm/cust/CustDetail'
const venderDetailsUrl = 'scm/vender/components/VenderDetail'
const loanAppDetailsUrl = 'oa/loanapp/LoanAppDetail'
const fmsPaymentDetailsUrl = 'finance/fees/feesDetail'
const fmsReceiptDetailsUrl = 'finance/receipt/receiptDetail'
const paymentAppDetailsUrl = 'oa/corporatePayments/CorporatePaymentsDetail'
const travelreimbDetailsUrl = 'oa/travelreimb/TravelReimbDetail'
const repayAppDetailsUrl = 'oa/repayapp/RepayAppDetail'
const entertainDetailsUrl = 'oa/entertainmentExpenses/EntertainmentExpensesDetail'
const generalDetailsUrl = 'oa/generalExpense/GeneralReimbDetail'
const skuDetailsUrl = 'pms/product/main/MainDetail'
const purchasePlanDetailsUrl = 'scm/purchase/plan/PlanDetail'
const purchaseContractDetailsUrl = 'scm/purchase/contract/ContractDetail'
const custSkuDetailsUrl = 'pms/product/cust/custProductDetail'
const poiReportDetailsUrl = 'system/poiReport/BasicModelDetail'
const AuxiliaryDetailsUrl = 'pms/product/auxiliary/AuxiliaryDetail'
const AuxiliaryPlanDetailsUrl = 'pms/product/auxiliary/PlanDetail'
const ExhibitionDetailUrl = 'exms/exhibition/ExhibitionDetail'
const ProjectDetailUrl = 'pjms/project/ProjectDetail'
const dtmsDesignUrl = 'design/designTask/DesignTaskDetail'
const exportSaleContact = 'sms/sale/exportSaleContract/ExportSaleContractDetail'
const mainChangeDetailsUrl = 'pms/product/mainChange/MainChangeDetail'
const shippingChangeDetailsUrl = 'dms/shippingChange/ChangeDetail'
const custSkuChangeDetailsUrl = 'pms/product/custSkuChange/custSkuChangeDetail'
const ownSkuChangeDetailsUrl = 'pms/product/ownBrandSkuChange/ownSkuChangeDetail'
const custChangeDetailUrl = 'crm/custChange/CustChangeDetail'
const venderChangeDetailUrl = 'scm/vender/components/VenderChangeDetail'
const paymentApplyDetailUrl = 'scm/vender/paymentApply/PaymentApplyDetail'
const quotationDetailUrl = 'sms/quotation/QuotationFormDetail'
const AdministrationDetailUrl = 'scm/vender/administration/AdministrationDetail'
const AuxiliaryPlanUrl = 'scm/auxiliaryPurchase/plan/PlanDetail'
const PurchaseContractChangeUrl = 'scm/purchase/change/ChangeDetail'
const ExportSaleContractChangeDetailUrl =
  'sms/sale/exportSaleContractChange/ExportSaleContractChangeDetail'
const SendDetailUrl = 'ems/send/sendDetail'
const InvoicingNoticesDetailUrl = 'scm/vender/invoicingNotices/InvoicingNoticesDetail'
const ConcessionReleaseDetailUrl = 'qms/concessionRelease/ConcessionReleaseDetail'
const OwnProductDetailUrl = 'pms/product/own/ownProductDetail'
const RegistrationDetailUrl = 'scm/purchase/registration/RegistrationDetail'
const EntertainApplyDetailUrl = 'oa/apply/entertain/EntertainDetail'
const TravelApplyDetailUrl = 'oa/apply/travel/TravelDetail'
const GeneralApplyDetailUrl = 'oa/apply/general/GeneralDetail'
const OtherExpenseDetailUrl = 'oa/otherExpense/OtherExpenseDetail'
const VenderChangeDetailUrl = 'scm/vender/components/VenderChangeDetail'
const CostCollectionDetailUrl = '/oa/costCollection/CostCollectionDetail'
const PurchaseContractAuxiliaryDetailUrl = 'scm/auxiliaryPurchase/contract/ContractDetail'
const wmsOutStockNoticeDetailUrl = '/wms/ttnotice/TtnoticeDetail'
const wmsInStockNoticeDetailUrl = '/wms/renotice/RenoticeDetail'
// wms_out_stock_notice
// wms_in_stock_notice
// oa_fee_share
// pms_change_auxiliary_sku
// scm_purchase_contract_auxiliary

/** 处理审批通过和不通过的操作 */
const handleAudit = async (task, pass) => {
  // 1.1 获得对应表单
  const index = runningTasks.value.indexOf(task)
  const auditFormRef = proxy.$refs['form' + index][0]
  // 1.2 校验表单
  const elForm = unref(auditFormRef)
  if (!elForm) return
  const valid = await elForm.validate()
  if (!valid) return

  // 其他费用报销审核通过校验方法
  if (
    pass == 1 &&
    approveSource &&
    [
      'oa_payment_app',
      'oa_travel_reimb',
      'oa_entertain_reimb',
      'oa_general_reimb',
      'oa_other_reimb'
    ].includes(approveSource)
  ) {
    // 等待组件加载完成
    await nextTick()
    try {
      const validateResult = await detallCompentRef.value.handleApprove()
      if (validateResult === false) {
        return
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : '校验失败，请检查数据'
      message.error(errorMessage)
      return
    }
  }

  // 2.1 提交审批
  const data = {
    id: task.id,
    reason: auditForms.value[index].reason,
    assigneeUserId: auditForms.value[index].assigneeUserId
  }
  if (pass === 1) {
    await TaskApi.approveTask('/bpm/task/approve', data)
    message.success('审批通过成功')
    handleNext()
  } else if (pass === 2) {
    await TaskApi.approveTask('/bpm/task/reject', data)
    message.success('审批不通过成功')
    handleNext()
  } else if (pass === 3) {
    await TaskApi.delegateTask(data)
    message.success('转派成功')
    handleNext()
  }
}

const handleNext = async () => {
  let data: any = []
  let firstData: any = []

  if (dateNum == 1) {
    data = await TaskApi.getTodoTaskPage({
      pageNo: dateNum,
      pageSize: 1
    })
    firstData = []
  } else {
    data = await TaskApi.getTodoTaskPage({
      pageNo: dateNum,
      pageSize: 1
    })
    firstData = await TaskApi.getTodoTaskPage({
      pageNo: 1,
      pageSize: 1
    })
  }

  if (isValidArray(data?.list)) {
    toNext(data)
  } else if (isValidArray(firstData?.list)) {
    toNext(firstData)
  } else {
    delView(unref(currentRoute))
    router.push({
      path: '/bpm/task/todo'
    })
  }
}

const toNext = (data) => {
  const row = data.list[0]
  router.push({
    path: '/process-instance/detail',
    query: {
      id: row.processInstance.id,
      processDefinitionKey: row.processInstance.processDefinitionKey,
      index: dateNum
    }
  })
  nextTick(() => {
    getDetail()
  })
}

/** 转派审批人 */
const taskUpdateAssigneeFormRef = ref()
const openTaskUpdateAssigneeForm = (id: string) => {
  taskUpdateAssigneeFormRef.value.open(id)
}

const taskDelegateForm = ref()
/** 处理审批退回的操作 */
const handleDelegate = async (task) => {
  taskDelegateForm.value.open(task.id)
}

//回退弹框组件
const taskReturnDialogRef = ref()
/** 处理审批退回的操作 */
const handleBack = async (task) => {
  taskReturnDialogRef.value.open(task.id)
}

const taskAddSignDialogForm = ref()
/** 处理审批加签的操作 */
const handleSign = async (task) => {
  taskAddSignDialogForm.value.open(task.id)
}

/** 获得详情 */
const getDetail = () => {
  if (approveSource === 'oa_cust') {
    detallCompent.value = registerComponent(custDetailsUrl)
  } else if (approveSource === 'oa_business_vender') {
    detallCompent.value = registerComponent(venderDetailsUrl)
  } else if (approveSource === 'oa_loan_app') {
    detallCompent.value = registerComponent(loanAppDetailsUrl)
  } else if (approveSource === 'fms_payment') {
    detallCompent.value = registerComponent(fmsPaymentDetailsUrl)
  } else if (approveSource === 'fms_receipt') {
    detallCompent.value = registerComponent(fmsReceiptDetailsUrl)
  } else if (approveSource === 'oa_payment_app') {
    detallCompent.value = registerComponent(paymentAppDetailsUrl)
  } else if (approveSource === 'oa_travel_reimb') {
    detallCompent.value = registerComponent(travelreimbDetailsUrl)
  } else if (approveSource === 'oa_repay_app') {
    detallCompent.value = registerComponent(repayAppDetailsUrl)
  } else if (approveSource === 'oa_entertain_reimb') {
    detallCompent.value = registerComponent(entertainDetailsUrl)
  } else if (approveSource === 'oa_general_reimb') {
    detallCompent.value = registerComponent(generalDetailsUrl)
  } else if (approveSource === 'pms_sku') {
    detallCompent.value = registerComponent(skuDetailsUrl)
  } else if (approveSource === 'scm_purchase_plan') {
    detallCompent.value = registerComponent(purchasePlanDetailsUrl)
  } else if (approveSource === 'pms_csku') {
    detallCompent.value = registerComponent(custSkuDetailsUrl)
  } else if (
    approveSource === 'scm_purchase_contract' ||
    approveSource === 'scm_purchase_contract_own_brand'
  ) {
    detallCompent.value = registerComponent(purchaseContractDetailsUrl)
  } else if (approveSource === 'system_poi_report') {
    detallCompent.value = registerComponent(poiReportDetailsUrl)
  } else if (approveSource === 'pms_auxiliary_sku') {
    detallCompent.value = registerComponent(AuxiliaryDetailsUrl)
  } else if (approveSource === 'scm_auxiliary_purchase_plan') {
    detallCompent.value = registerComponent(AuxiliaryPlanDetailsUrl)
  } else if (approveSource === 'exms_exhibition') {
    detallCompent.value = registerComponent(ExhibitionDetailUrl)
  } else if (approveSource === 'pjms_project') {
    detallCompent.value = registerComponent(ProjectDetailUrl)
  } else if (approveSource === 'dtms_design') {
    detallCompent.value = registerComponent(dtmsDesignUrl)
  } else if (
    approveSource === 'sms_export_sale_contact' ||
    approveSource === 'sms_domestic_sale_contact' ||
    approveSource === 'sms_factory_sale_contact'
  ) {
    detallCompent.value = registerComponent(exportSaleContact)
  } else if (approveSource === 'pms_change_sku') {
    detallCompent.value = registerComponent(mainChangeDetailsUrl)
  } else if (
    approveSource === 'dms_shipment_change_business' ||
    approveSource === 'dms_shipment_change'
  ) {
    detallCompent.value = registerComponent(shippingChangeDetailsUrl)
  } else if (approveSource === 'pms_change_csku') {
    detallCompent.value = registerComponent(custSkuChangeDetailsUrl)
  } else if (approveSource === 'pms_change_own_brand') {
    detallCompent.value = registerComponent(ownSkuChangeDetailsUrl)
  } else if (approveSource === 'oa_change_cust') {
    detallCompent.value = registerComponent(custChangeDetailUrl)
  } else if (approveSource === 'oa_change_vender') {
    detallCompent.value = registerComponent(venderChangeDetailUrl)
  } else if (approveSource === 'scm_payment_apply') {
    detallCompent.value = registerComponent(paymentApplyDetailUrl)
  } else if (approveSource === 'sms_quotation') {
    detallCompent.value = registerComponent(quotationDetailUrl)
  } else if (approveSource === 'oa_vender') {
    detallCompent.value = registerComponent(AdministrationDetailUrl)
  } else if (approveSource === 'scm_purchase_plan_auxiliary') {
    detallCompent.value = registerComponent(AuxiliaryPlanUrl)
  } else if (
    approveSource === 'sms_sale_contract_change' ||
    approveSource === 'sms_domestic_sale_contract_change'
  ) {
    detallCompent.value = registerComponent(ExportSaleContractChangeDetailUrl)
  } else if (approveSource === 'scm_purchase_contract_change') {
    detallCompent.value = registerComponent(PurchaseContractChangeUrl)
  } else if (approveSource === 'ems_send') {
    detallCompent.value = registerComponent(SendDetailUrl)
  } else if (approveSource === 'scm_invoicing_notices') {
    detallCompent.value = registerComponent(InvoicingNoticesDetailUrl)
  } else if (approveSource === 'scm_concession_release') {
    detallCompent.value = registerComponent(ConcessionReleaseDetailUrl)
  } else if (approveSource === 'pms_own_brand') {
    detallCompent.value = registerComponent(OwnProductDetailUrl)
  } else if (approveSource === 'purchase_registration') {
    detallCompent.value = registerComponent(RegistrationDetailUrl)
  } else if (approveSource === 'oa_apply_entertain') {
    detallCompent.value = registerComponent(EntertainApplyDetailUrl)
  } else if (approveSource === 'oa_apply_travel') {
    detallCompent.value = registerComponent(TravelApplyDetailUrl)
  } else if (approveSource === 'oa_apply_general') {
    detallCompent.value = registerComponent(GeneralApplyDetailUrl)
  } else if (approveSource === 'oa_other_reimb') {
    detallCompent.value = registerComponent(OtherExpenseDetailUrl)
  } else if (approveSource === 'oa_change_business_vender') {
    detallCompent.value = registerComponent(VenderChangeDetailUrl)
  } else if (approveSource === 'oa_fee_share') {
    detallCompent.value = registerComponent(CostCollectionDetailUrl)
  } else if (approveSource === 'scm_purchase_contract_auxiliary') {
    detallCompent.value = registerComponent(PurchaseContractAuxiliaryDetailUrl)
  } else if (approveSource === 'wms_out_stock_notice') {
    detallCompent.value = registerComponent(wmsOutStockNoticeDetailUrl)
  } else if (approveSource === 'wms_in_stock_notice') {
    detallCompent.value = registerComponent(wmsInStockNoticeDetailUrl)
  }

  // 1. 获得流程实例相关
  getProcessInstance()
  // 2. 获得流程任务列表（审批记录）
  getTaskList()
}

/** 加载流程实例 */
const BusinessFormComponent = ref(null) // 异步组件
const getProcessInstance = async () => {
  try {
    processInstanceLoading.value = true
    const data = await ProcessInstanceApi.getProcessInstance(id)
    if (!data) {
      // message.error('查询不到流程信息！')
      return
    }
    processInstance.value = data

    // 设置表单信息
    const processDefinition = data.processDefinition
    if (processDefinition.formType === 10) {
      setConfAndFields2(
        detailForm,
        processDefinition.formConf,
        processDefinition.formFields,
        data.formVariables
      )
      nextTick().then(() => {
        fApi.value?.fapi?.btn.show(false)
        fApi.value?.fapi?.resetBtn.show(false)
        fApi.value?.fapi?.disabled(true)
      })
    } else {
      BusinessFormComponent.value = registerComponent(data.processDefinition.formCustomViewPath)
    }

    // 加载流程图
    bpmnXML.value = await DefinitionApi.getProcessDefinitionBpmnXML(processDefinition.id as number)
  } finally {
    processInstanceLoading.value = false
  }
}

/** 加载任务列表 */
const getTaskList = async () => {
  try {
    // 获得未取消的任务
    tasksLoad.value = true
    const data = await TaskApi.getTaskListByProcessInstanceId(id)
    tasks.value = []
    // 1.1 移除已取消的审批
    data.forEach((task) => {
      if (task.result !== 4) {
        tasks.value.push(task)
      }
    })
    // 1.2 排序，将未完成的排在前面，已完成的排在后面；
    tasks.value.sort((a, b) => {
      // 有已完成的情况，按照完成时间倒序
      if (a.endTime && b.endTime) {
        return b.endTime - a.endTime
      } else if (a.endTime) {
        return 1
      } else if (b.endTime) {
        return -1
        // 都是未完成，按照创建时间倒序
      } else {
        return b.createTime - a.createTime
      }
    })

    // 获得需要自己审批的任务
    runningTasks.value = []
    auditForms.value = []
    loadRunningTask(tasks.value)
  } finally {
    tasksLoad.value = false
  }
}

/**
 * 设置 runningTasks 中的任务
 */
const loadRunningTask = (tasks) => {
  tasks.forEach((task) => {
    if (!isEmpty(task.children)) {
      loadRunningTask(task.children)
    }
    // 2.1 只有待处理才需要
    if (task.result !== 1 && task.result !== 6) {
      return
    }
    // 2.2 自己不是处理人
    if (!task.assigneeUser || task.assigneeUser.id !== userId) {
      return
    }
    // 2.3 添加到处理任务
    runningTasks.value.push({ ...task })
    auditForms.value.push({
      isPass: 1,
      assigneeUserId: '',
      reason: ''
    })
  })

  // console.log('loadRunningTask', tasks, runningTasks)
}

/** 初始化 */
onMounted(() => {
  if (approveSource === 'oa_cust') {
    detallCompent.value = registerComponent(custDetailsUrl)
  } else if (approveSource === 'oa_business_vender') {
    detallCompent.value = registerComponent(venderDetailsUrl)
  } else if (approveSource === 'oa_loan_app') {
    detallCompent.value = registerComponent(loanAppDetailsUrl)
  } else if (approveSource === 'fms_payment') {
    detallCompent.value = registerComponent(fmsPaymentDetailsUrl)
  } else if (approveSource === 'fms_receipt') {
    detallCompent.value = registerComponent(fmsReceiptDetailsUrl)
  } else if (approveSource === 'oa_payment_app') {
    detallCompent.value = registerComponent(paymentAppDetailsUrl)
  } else if (approveSource === 'oa_travel_reimb') {
    detallCompent.value = registerComponent(travelreimbDetailsUrl)
  } else if (approveSource === 'oa_repay_app') {
    detallCompent.value = registerComponent(repayAppDetailsUrl)
  } else if (approveSource === 'oa_entertain_reimb') {
    detallCompent.value = registerComponent(entertainDetailsUrl)
  } else if (approveSource === 'oa_general_reimb') {
    detallCompent.value = registerComponent(generalDetailsUrl)
  } else if (approveSource === 'pms_sku') {
    detallCompent.value = registerComponent(skuDetailsUrl)
  } else if (approveSource === 'scm_purchase_plan') {
    detallCompent.value = registerComponent(purchasePlanDetailsUrl)
  } else if (approveSource === 'pms_csku') {
    detallCompent.value = registerComponent(custSkuDetailsUrl)
  } else if (
    approveSource === 'scm_purchase_contract' ||
    approveSource === 'scm_purchase_contract_own_brand'
  ) {
    detallCompent.value = registerComponent(purchaseContractDetailsUrl)
  } else if (approveSource === 'system_poi_report') {
    detallCompent.value = registerComponent(poiReportDetailsUrl)
  } else if (approveSource === 'pms_auxiliary_sku') {
    detallCompent.value = registerComponent(AuxiliaryDetailsUrl)
  } else if (approveSource === 'scm_auxiliary_purchase_plan') {
    detallCompent.value = registerComponent(AuxiliaryPlanDetailsUrl)
  } else if (approveSource === 'exms_exhibition') {
    detallCompent.value = registerComponent(ExhibitionDetailUrl)
  } else if (approveSource === 'pjms_project') {
    detallCompent.value = registerComponent(ProjectDetailUrl)
  } else if (approveSource === 'dtms_design') {
    detallCompent.value = registerComponent(dtmsDesignUrl)
  } else if (
    approveSource === 'sms_export_sale_contact' ||
    approveSource === 'sms_domestic_sale_contact' ||
    approveSource === 'sms_factory_sale_contact'
  ) {
    detallCompent.value = registerComponent(exportSaleContact)
  } else if (approveSource === 'pms_change_sku') {
    detallCompent.value = registerComponent(mainChangeDetailsUrl)
  } else if (
    approveSource === 'dms_shipment_change_business' ||
    approveSource === 'dms_shipment_change'
  ) {
    detallCompent.value = registerComponent(shippingChangeDetailsUrl)
  } else if (approveSource === 'pms_change_csku') {
    detallCompent.value = registerComponent(custSkuChangeDetailsUrl)
  } else if (approveSource === 'pms_change_own_brand') {
    detallCompent.value = registerComponent(ownSkuChangeDetailsUrl)
  } else if (approveSource === 'oa_change_cust') {
    detallCompent.value = registerComponent(custChangeDetailUrl)
  } else if (approveSource === 'oa_change_vender') {
    detallCompent.value = registerComponent(venderChangeDetailUrl)
  } else if (approveSource === 'scm_payment_apply') {
    detallCompent.value = registerComponent(paymentApplyDetailUrl)
  } else if (approveSource === 'sms_quotation') {
    detallCompent.value = registerComponent(quotationDetailUrl)
  } else if (approveSource === 'oa_vender') {
    detallCompent.value = registerComponent(AdministrationDetailUrl)
  } else if (approveSource === 'scm_purchase_plan_auxiliary') {
    detallCompent.value = registerComponent(AuxiliaryPlanUrl)
  } else if (
    approveSource === 'sms_sale_contract_change' ||
    approveSource === 'sms_domestic_sale_contract_change'
  ) {
    detallCompent.value = registerComponent(ExportSaleContractChangeDetailUrl)
  } else if (approveSource === 'scm_purchase_contract_change') {
    detallCompent.value = registerComponent(PurchaseContractChangeUrl)
  } else if (approveSource === 'ems_send') {
    detallCompent.value = registerComponent(SendDetailUrl)
  } else if (approveSource === 'scm_invoicing_notices') {
    detallCompent.value = registerComponent(InvoicingNoticesDetailUrl)
  } else if (approveSource === 'scm_concession_release') {
    detallCompent.value = registerComponent(ConcessionReleaseDetailUrl)
  } else if (approveSource === 'pms_own_brand') {
    detallCompent.value = registerComponent(OwnProductDetailUrl)
  } else if (approveSource === 'purchase_registration') {
    detallCompent.value = registerComponent(RegistrationDetailUrl)
  } else if (approveSource === 'oa_apply_entertain') {
    detallCompent.value = registerComponent(EntertainApplyDetailUrl)
  } else if (approveSource === 'oa_apply_travel') {
    detallCompent.value = registerComponent(TravelApplyDetailUrl)
  } else if (approveSource === 'oa_apply_general') {
    detallCompent.value = registerComponent(GeneralApplyDetailUrl)
  } else if (approveSource === 'oa_other_reimb') {
    detallCompent.value = registerComponent(OtherExpenseDetailUrl)
  } else if (approveSource === 'oa_change_business_vender') {
    detallCompent.value = registerComponent(VenderChangeDetailUrl)
  } else if (approveSource === 'oa_fee_share') {
    detallCompent.value = registerComponent(CostCollectionDetailUrl)
  } else if (approveSource === 'scm_purchase_contract_auxiliary') {
    detallCompent.value = registerComponent(PurchaseContractAuxiliaryDetailUrl)
  } else if (approveSource === 'wms_out_stock_notice') {
    detallCompent.value = registerComponent(wmsOutStockNoticeDetailUrl)
  } else if (approveSource === 'wms_in_stock_notice') {
    detallCompent.value = registerComponent(wmsInStockNoticeDetailUrl)
  }
  getDetail()
})
</script>
<style lang="scss">
.setH {
  height: 80vh;
}
</style>
