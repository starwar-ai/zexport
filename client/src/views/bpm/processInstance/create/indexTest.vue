<template>
  <ContentWrap>
    <el-card class="box-card">
      <div class="clearfix">
        <span class="el-icon-document">申请信息【{{ selectProcessInstance?.name || '-' }}】</span>
        <!-- <el-button
          style="float: right"
          type="primary"
          @click="selectProcessInstance = undefined"
        >
          <Icon icon="ep:delete" /> 选择其它流程
        </el-button> -->
      </div>
      <el-col
        :span="16"
        :offset="6"
        style="margin-top: 20px"
      >
        <form-create
          :rule="detailForm.rule"
          v-model:api="fApi"
          :option="detailForm.option"
          @submit="submitForm"
        />
      </el-col>
    </el-card>
    <!-- 流程图预览 -->
    <!-- <ProcessInstanceBpmnViewer :bpmn-xml="bpmnXML as any" /> -->
  </ContentWrap>
</template>
<script lang="tsx" setup>
import * as DefinitionApi from '@/api/bpm/definition'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import { setConfAndFields2 } from '@/utils/formCreate'
import type { ApiAttrs } from '@form-create/element-ui/types/config'

defineOptions({ name: 'BpmProcessInstanceCreate' })
const { query } = useRoute()
const router = useRouter() // 路由
const message = useMessage() // 消息

// ========== 表单相关 ==========
const bpmnXML = ref(null) // BPMN 数据
const fApi = ref<ApiAttrs>()
const detailForm = ref({
  // 流程表单详情
  rule: [],
  option: {}
})
const selectProcessInstance = ref() // 选择的流程实例

/** 处理选择流程的按钮操作 **/
const handleSelect = async (row) => {
  // 设置选择的流程
  selectProcessInstance.value = row

  // 情况一：流程表单
  if (row.formType == 10) {
    // 设置表单
    setConfAndFields2(detailForm, row.formConf, row.formFields)
    // 加载流程图
    bpmnXML.value = await DefinitionApi.getProcessDefinitionBpmnXMLTest('oa_test')
    // 情况二：业务表单
  } else if (row.formCustomCreatePath) {
    await router.push({
      path: row.formCustomCreatePath
    })
    // 这里暂时无需加载流程图，因为跳出到另外个 Tab；
  }
}

/** 提交按钮 */
const submitForm = async (formData) => {
  if (!fApi.value || !selectProcessInstance.value) {
    return
  }
  // 提交请求
  fApi.value.btn.loading(true)
  try {
    await ProcessInstanceApi.createProcessInstance({
      processDefinitionId: selectProcessInstance.value.id,
      variables: formData,
      auditAbleId: query?.id ? Number(query?.id) : '',
      bussinessKey: query?.bussinessKey as string
    })
    // 提示
    message.success('发起流程成功')
    router.go(-1)
  } finally {
    fApi.value.btn.loading(false)
  }
}
/** 初始化 */
onMounted(async () => {
  const processDefinitionKey = query.processDefinitionKey as string
  let res = await DefinitionApi.getProcessDefinitionBpmnXMLTest(processDefinitionKey)
  console.log(res, 'res')
  selectProcessInstance.value = res
  // 情况一：流程表单
  if (res.formType == 10) {
    // 设置表单
    setConfAndFields2(detailForm, res.formConf, res.formFields)
    // 加载流程图
    // bpmnXML.value = await DefinitionApi.getProcessDefinitionBpmnXMLTest('oa_test')
    // 情况二：业务表单
  } else if (res.formCustomCreatePath) {
    await router.push({
      path: res.formCustomCreatePath
    })
    // 这里暂时无需加载流程图，因为跳出到另外个 Tab；
  }
})
</script>
