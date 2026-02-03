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
        label="借款事由"
        prop="purpose"
      >
        <el-input
          v-model="queryParams.purpose"
          placeholder="请输入借款事由"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="借款部门"
        prop="loanDeptId"
      >
        <el-input
          v-model="queryParams.loanDeptId"
          placeholder="请输入借款部门"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="还款状态"
        prop="repayStatus"
      >
        <el-select
          v-model="queryParams.repayStatus"
          placeholder="请选择还款状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LOAN_REPAY_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="计划还款日期"
        prop="repayDate"
      >
        <el-date-picker
          v-model="queryParams.repayDate"
          value-format="YYYY-MM-DD"
          type="date"
          placeholder="选择计划还款日期"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="借款金额"
        prop="amount"
      >
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入借款金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="支付状态"
        prop="transferStatus"
      >
        <el-select
          v-model="queryParams.transferStatus"
          placeholder="请选择支付状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LOAN_TRANSFER_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="借款日期"
        prop="loanDate"
      >
        <el-date-picker
          v-model="queryParams.loanDate"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="已转金额"
        prop="transferAmount"
      >
        <el-input
          v-model="queryParams.transferAmount"
          placeholder="请输入已转金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="已还金额"
        prop="repayAmount"
      >
        <el-input
          v-model="queryParams.repayAmount"
          placeholder="请输入已还金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="开户行"
        prop="bank"
      >
        <el-input
          v-model="queryParams.bank"
          placeholder="请输入开户行"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="银行账户"
        prop="bankaccount"
      >
        <el-input
          v-model="queryParams.bankaccount"
          placeholder="请输入银行账户"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="账户名称"
        prop="accountname"
      >
        <el-input
          v-model="queryParams.accountname"
          placeholder="请输入账户名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="创建时间"
        prop="createTime"
      >
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
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
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['oa:loan-app:create']"
        >
          <Icon
            icon="ep:plus"
            class="mr-5px"
          />
          新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['oa:loan-app:export']"
        >
          <Icon
            icon="ep:download"
            class="mr-5px"
          />
          导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
    >
      <el-table-column
        label="编号"
        align="center"
        prop="id"
      />
      <el-table-column
        label="借款事由"
        align="center"
        prop="purpose"
      />
      <el-table-column
        label="借款部门"
        align="center"
        prop="loanDeptId"
      />
      <el-table-column
        label="还款状态"
        align="center"
        prop="repayStatus"
      >
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.LOAN_REPAY_STATUS"
            :value="scope.row.repayStatus"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="计划还款日期"
        align="center"
        prop="repayDate"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="借款金额"
        align="center"
        prop="amount"
      />
      <el-table-column
        label="支付状态"
        align="center"
        prop="transferStatus"
      >
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.LOAN_TRANSFER_STATUS"
            :value="scope.row.transferStatus"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="借款日期"
        align="center"
        prop="loanDate"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="已转金额"
        align="center"
        prop="transferAmount"
      />
      <el-table-column
        label="已还金额"
        align="center"
        prop="repayAmount"
      />
      <el-table-column
        label="开户行"
        align="center"
        prop="bank"
      />
      <el-table-column
        label="银行账户"
        align="center"
        prop="bankaccount"
      />
      <el-table-column
        label="账户名称"
        align="center"
        prop="accountname"
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="操作"
        align="center"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['oa:loan-app:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['oa:loan-app:delete']"
          >
            删除
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

  <!-- 表单弹窗：添加/修改 -->
  <!-- <LoanAppForm ref="formRef" @success="getList" /> -->
</template>

<script setup lang="ts">
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as LoanAppApi from '@/api/oa/loanapp'
// import LoanAppForm from './LoanAppForm.vue'

defineOptions({ name: 'LoanApp' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  purpose: undefined,
  loanDeptId: undefined,
  repayStatus: undefined,
  repayDate: undefined,
  amount: undefined,
  transferStatus: undefined,
  loanDate: undefined,
  transferAmount: undefined,
  repayAmount: undefined,
  bank: undefined,
  bankaccount: undefined,
  accountname: undefined,
  createTime: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await LoanAppApi.getLoanAppPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await LoanAppApi.deleteLoanApp(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await LoanAppApi.exportLoanApp(queryParams)
    download.excel(data, '借款申请单.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
