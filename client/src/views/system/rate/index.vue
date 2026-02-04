<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <!-- <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item
        label="日期"
        prop="dailyCurrDate"
      >
        <el-date-picker
          v-model="queryParams.dailyCurrDate"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="币种"
        prop="dailyCurrName"
      >
        <el-input
          v-model="queryParams.dailyCurrName"
          placeholder="请输入币种"
          clearable
          @keyup.enter="handleQuery"
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
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['system:currencys-rate:export']"
        >
          <Icon
            icon="ep:download"
            class="mr-5px"
          />
          导出
        </el-button>
      </el-form-item>
    </el-form> -->
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
        label="日期"
        align="center"
        prop="dailyCurrDate"
      >
        <template #default="scope">
          {{ formatTime(scope.row.dailyCurrDate, 'yyyy-MM-dd') }}
        </template>
      </el-table-column>
      <el-table-column
        label="币种"
        align="center"
        prop="dailyCurrName"
      />
      <el-table-column
        label="汇率"
        align="center"
        prop="dailyCurrRate"
      />
    </el-table>
    <!-- 分页 -->
    <!-- <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    /> -->
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <CurrencysRateForm
    ref="formRef"
    @success="getList"
  />
</template>

<script setup lang="ts">
import download from '@/utils/download'
import * as CurrencysRateApi from '@/api/system/rate'
import CurrencysRateForm from './CurrencysRateForm.vue'
import { formatTime } from '@/utils'

defineOptions({ name: 'CurrencysRate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  dailyCurrDate: [],
  dailyCurrName: undefined,
  dailyCurrRate: undefined,
  dailyCurrSource: undefined,
  dailyCurrMidRate: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CurrencysRateApi.getCurrencysRatePage()
    list.value = Object.keys(data).map((item) => {
      return {
        dailyCurrDate: Date.now(),
        dailyCurrName: item,
        dailyCurrRate: data[item]
      }
    })
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

/** 格式化来源数据 */
const formatSource = (row) => {
  return row.dailyCurrSource == '1' ? '自动' : '手动'
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await CurrencysRateApi.exportCurrencysRate(queryParams)
    download.excel(data, '动态汇率.xls')
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
