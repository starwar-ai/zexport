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
        label="状态"
        prop="status"
      >
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.ENABLE_FLAG)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['company:path:create']"
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
          v-hasPermi="['company:path:export']"
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
    >
      <el-table-column
        label="流程"
        align="center"
        prop="pathName"
      />
      <el-table-column
        label="描述"
        align="center"
        prop="description"
      />
      <el-table-column
        label="状态"
        prop="status"
      >
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.ENABLE_FLAG"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
      >
        <template #default="scope">
          <el-button
            type="primary"
            v-hasPermi="['company:path:update']"
            link
            @click="openForm('edit', scope.row.id)"
            >编辑</el-button
          >
          <el-button
            v-hasPermi="['company:path:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
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

  <!-- 表单弹窗：新增编辑 -->
  <CompanyPathForm
    ref="detailRef"
    @success="getList"
  />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import download from '@/utils/download'
import * as CompanyPathApi from '@/api/system/companyPath'
import CompanyPathForm from './CompanyPathForm.vue'
import * as LoanAppApi from '@/api/oa/loanapp'

defineOptions({ name: 'CompanyPath' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const companyList = ref<any[]>([])
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CompanyPathApi.getCompanyPathPage(queryParams)
    data.list.forEach((item) => {
      if (item.path) {
        item.pathName = item.path.name
        let nextObj = item.path.next
        while (nextObj) {
          item.pathName += '->' + nextObj.name
          nextObj = nextObj.next
        }
      }
    })
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

/** 详情操作 */
const detailRef = ref()

const openForm = (type: string, id?: number) => {
  detailRef.value.open(type, id)
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await CompanyPathApi.exportCompanyPath(queryParams)
    download.excel(data, '订单路径.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}
/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await CompanyPathApi.deleteCompanyPath(id)
    // 刷新列表
    await getList()
    message.success(t('common.delSuccess'))
  } catch {}
}
/** 初始化 **/
onMounted(async () => {
  var res = await LoanAppApi.getcompanySimpleList()
  companyList.value = JSON.parse(JSON.stringify(res || []))
  getList()
})
</script>
