<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item
        label="客户名称"
        prop="name"
      >
        <el-input
          v-model="queryParams.name"
          placeholder="请输入客户名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <!--      <el-form-item label="业务员" prop="name">-->
      <!--        <el-input-->
      <!--          v-model="queryParams.name"-->
      <!--          placeholder="请输入业务员"-->
      <!--          clearable-->
      <!--          @keyup.enter="handleQuery"-->
      <!--          class="!w-240px"-->
      <!--        />-->
      <!--      </el-form-item>-->
      <el-form-item
        label="客户阶段"
        prop="category"
      >
        <el-select
          v-model="queryParams.stageType"
          placeholder="请选择客户阶段"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.CUSTOMER_STAGE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="审核状态"
        prop="auditStatus"
      >
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.CRM_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="发起时间"
        prop="startTime"
      >
        <el-date-picker
          v-model="queryParams.startTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择发起时间"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"
          ><Icon
            icon="ep:search"
            class="mr-5px"
          />
          搜索</el-button
        >
        <el-button @click="resetQuery"
          ><Icon
            icon="ep:refresh"
            class="mr-5px"
          />
          重置</el-button
        >
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
    >
      <el-table-column
        label="审核编号"
        align="center"
        prop="id"
      />
      <el-table-column
        label="客户名称"
        align="center"
        prop="name"
      />
      <el-table-column
        label="客户阶段"
        align="center"
        prop="stageType"
      >
        <!-- <template #default="scope">
          <dict-tag :type="DICT_TYPE.CURRENCY_TYPE" :value="scope.row.stageType" />
        </template> -->
      </el-table-column>
      <!--      <el-table-column label="业务员" align="center" prop="name" />-->
      <el-table-column
        label="审批阶段"
        align="center"
        prop="auditStage"
      >
        <!-- <template #default="scope">
          <dict-tag :type="DICT_TYPE.CUSTOMER_STAGE" :value="scope.row.auditStage" />
        </template> -->
      </el-table-column>
      <el-table-column
        label="发起人"
        align="center"
        prop="startUserId"
      />
      <el-table-column
        label="发起时间"
        align="center"
        prop="startTime"
      />
      <el-table-column
        label="审核状态"
        align="center"
        prop="auditStatus"
      >
        <!-- <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_STATUS" :value="scope.row.auditStatus" />
        </template> -->
      </el-table-column>
      <el-table-column
        label="审核人"
        align="center"
        prop="auditName"
      />
      <el-table-column
        label="操作"
        align="center"
      >
        <template #default="scope">
          <!--          <el-button-->
          <!--            link-->
          <!--            type="primary"-->
          <!--            v-hasPermi="['bpm:process-instance:cancel']"-->
          <!--            @click="handleDetail(scope.row.id)"-->
          <!--          >-->
          <!--            详情-->
          <!--          </el-button>-->
          <el-button
            link
            type="primary"
            @click="handleCancel(scope.row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
// import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import { getMyProcessInstancePage } from '@/api/audit/cust'

defineOptions({ name: 'Audit' })

const router = useRouter() // 路由
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  id: '',
  name: '',
  taskId: '',
  stageType: undefined,
  auditStage: undefined,
  auditStatus: undefined,
  startUserId: undefined,
  startTime: '',
  auditName: ''
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await getMyProcessInstancePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

//详情按钮
const handleCancel = (row) => {
  router.push({
    name: 'AuditDetail',
    query: {
      id: row.id,
      taskId: row.taskId,
      name: row.name,
      stageType: row.stageType,
      auditStage: row.auditStage,
      auditStatus: row.auditStatus,
      startUserId: row.startUserId,
      startTime: row.startTime,
      auditName: row.auditName
    }
  })
}

/** 查看详情 */
const handleDetail = (row) => {
  // router.push({
  //   name: 'BpmProcessInstanceDetail',
  //   query: {
  //     id: row.id
  //   }
  // })
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
