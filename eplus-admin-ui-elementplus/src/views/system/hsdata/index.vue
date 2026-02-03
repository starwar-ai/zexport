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
        label="编码"
        prop="code"
      >
        <el-input
          v-model="queryParams.code"
          placeholder="请输入编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="退税率"
        prop="taxRefundRate"
      >
        <el-input
          v-model="queryParams.taxRefundRate"
          placeholder="请输入退税率"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="商品名称"
        prop="name"
      >
        <el-input
          v-model="queryParams.name"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="报关单位"
        prop="unit"
      >
        <el-input
          v-model="queryParams.unit"
          placeholder="请输入报关单位"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item
        label="商品全称"
        prop="chname"
      >
        <el-input
          v-model="queryParams.chname"
          placeholder="请输入商品全称"
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
          v-hasPermi="['pms:hsdata:create']"
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
          v-hasPermi="['pms:hsdata:export']"
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
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      height="700"
    >
      <el-table-column
        label="编号"
        align="center"
        prop="id"
      />
      <el-table-column
        label="版本号"
        align="center"
        prop="ver"
      />
      <el-table-column
        label="编码"
        align="center"
        prop="code"
      />
      <el-table-column
        label="商品名称"
        align="center"
        prop="name"
      />
      <el-table-column
        label="报关单位"
        align="center"
        prop="unit"
      />
      <el-table-column
        label="退税率"
        align="center"
        prop="taxRefundRate"
      />
      <el-table-column
        label="征税率"
        align="center"
        prop="rate"
      />
      <el-table-column
        label="备注"
        align="center"
        prop="remark"
      />
      <el-table-column
        label="商品全称"
        align="center"
        prop="chname"
      />
      <el-table-column
        label="征收率"
        align="center"
        prop="addrate"
      />
      <el-table-column
        label="第二单位"
        align="center"
        prop="code2"
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
            v-hasPermi="['pms:hsdata:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['pms:hsdata:delete']"
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
      :pageSizes="globalStore().glbPageSizes"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <HsdataForm
    ref="formRef"
    @success="getList"
  />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as HsdataApi from '@/api/system/hsdata'
import HsdataForm from './HsdataForm.vue'
import { globalStore } from '@/store/modules/globalVariable'

defineOptions({ name: 'Hsdata' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: globalStore().glbPageSize,
  ver: undefined,
  code: undefined,
  name: undefined,
  unit: undefined,
  taxRefundRate: undefined,
  rate: undefined,
  remark: undefined,
  chname: undefined,
  addrate: undefined,
  code2: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await HsdataApi.getHsdataList(queryParams)
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
    await HsdataApi.deleteHsdata(id)
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
    const data = await HsdataApi.exportHsdata(queryParams)
    download.excel(data, '海关编码.xls')
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
