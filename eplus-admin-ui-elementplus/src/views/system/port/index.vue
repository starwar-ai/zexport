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
        label="编号"
        prop="code"
      >
        <el-input
          v-model="queryParams.code"
          placeholder="请输入编号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="城市"
        prop="name"
      >
        <el-input
          v-model="queryParams.city"
          placeholder="请输入城市名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="名称"
        prop="name"
      >
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="英文名称"
        prop="name"
      >
        <el-input
          v-model="queryParams.nameEng"
          placeholder="请输入英文名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item
        label="国家/地区"
        prop="countryId"
      >
        <EplusCountrySelect
          v-model="queryParams.countryId"
          class="!w-100%"
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
          v-hasPermi="['system:port:create']"
        >
          <Icon
            icon="ep:plus"
            class="mr-5px"
          />
          新增
        </el-button>
        <!-- <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
        >
          <Icon
            icon="ep:download"
            class="mr-5px"
          />
          导出
        </el-button> -->
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
        prop="code"
      />
      <el-table-column
        label="国家/地区"
        align="center"
        prop="countryName"
      />
      <el-table-column
        label="城市"
        align="center"
        prop="city"
      />
      <el-table-column
        label="地址"
        align="center"
        prop="address"
      />
      <el-table-column
        label="名称"
        align="center"
        prop="name"
      />
      <el-table-column
        label="英文名称"
        align="center"
        prop="nameEng"
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="是否置顶"
        align="center"
        prop="topFlag"
        width="180px"
      >
        <template #default="{ row }"> {{ getDictLabel(DICT_TYPE.IS_INT, row.topFlag) }}</template>
      </el-table-column>
      <el-table-column
        label="状态"
        align="center"
        prop="status"
        width="180px"
      >
        <template #default="{ row }">
          {{ getDictLabel(DICT_TYPE.PORT_STATUS, row.status) }}</template
        >
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['system:port:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleTop(scope.row)"
          >
            {{ scope.row.topFlag === 1 ? '取消置顶' : '置顶' }}
          </el-button>
          <!-- <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['system:port:delete']"
          >
            删除
          </el-button> -->
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
  <PortForm
    ref="formRef"
    @success="getList"
  />
</template>

<script setup lang="ts">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as portApi from '@/api/system/port'
import PortForm from './PortForm.vue'

defineOptions({ name: 'Port' })
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  code: undefined,
  city: undefined,
  name: undefined,
  nameEng: undefined,
  auditStatus: undefined,
  dateType: undefined,
  duration: undefined,
  countryId: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await portApi.getPortPage(queryParams)
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

const handleTop = async (row) => {
  try {
    let msg = row.topFlag === 1 ? '取消置顶' : '置顶'
    await message.confirm(`确定将该口岸${msg}吗？`)
    await portApi.portTop(row.topFlag === 1 ? 'rollback-top' : 'top', { id: row.id })
    message.success('操作成功')
    getList()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await portApi.deletePort(id)
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
    const data = await portApi.exportPort(queryParams)
    download.excel(data, '口岸.xls')
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
