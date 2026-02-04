<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="params"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item
        label="卡片名称"
        prop="name"
      >
        <el-input
          v-model="params.title"
          class="!w-240px"
          clearable
          placeholder="请输入卡片名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">
          <Icon
            class="mr-5px"
            icon="ep:search"
          />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon
            class="mr-5px"
            icon="ep:refresh"
          />
          重置
        </el-button>
        <el-button
          v-hasPermi="['system:menu:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon
            class="mr-5px"
            icon="ep:plus"
          />
          新增
        </el-button>

        <el-button
          plain
          @click="refreshMenu"
        >
          <Icon
            class="mr-5px"
            icon="ep:refresh"
          />
          刷新列表缓存
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      row-key="id"
    >
      <el-table-column
        label="序号"
        type="index"
        width="100"
      />
      <el-table-column
        :show-overflow-tooltip="true"
        label="卡片名称"
        prop="title"
      />
      <el-table-column
        :show-overflow-tooltip="true"
        label="组件名称"
        prop="currentComponent"
      />
      <el-table-column
        label="图标"
        prop="icon"
      >
        <template #default="scope">
          <EplusImgEnlarge :mainPicture="scope.row?.picture" />
        </template>
      </el-table-column>
      <el-table-column
        :show-overflow-tooltip="true"
        label="卡片描述"
        prop="description"
      />

      <el-table-column
        align="center"
        label="操作"
        width="300"
      >
        <template #default="scope">
          <el-button
            v-hasPermi="['system:menu:update']"
            link
            type="primary"
            @click="openForm('update', scope.row)"
          >
            修改
          </el-button>

          <el-button
            v-hasPermi="['system:menu:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :page-size="10"
      layout="total, prev, pager, next"
      class="pagination"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <CardForm
    ref="formRef"
    @success="getList"
  />
</template>
<script lang="ts" setup>
import CardForm from './CardForm.vue'
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'
import * as HomeApi from '@/api/home'

defineOptions({ name: 'HomeCard' })

const { wsCacheSession } = useCache()
const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref<any>([]) // 列表的数据

const params = reactive({
  pageNo: 1,
  pageSize: 10,
  basicFlag: 1
})
const queryFormRef = ref() // 搜索的表单
const isExpandAll = ref(false) // 是否展开，默认全部折叠
const refreshTable = ref(true) // 重新渲染表格状态
const total = ref(0)

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const res = await HomeApi.getAllCard(params)
    total.value = res.total

    list.value = res.list
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {}
const handleCurrentChange = (val: number) => {
  params.pageNo = val
  getList()
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type, row) => {
  formRef.value.open(type, row)
}

/** 刷新菜单缓存按钮操作 */
const refreshMenu = async () => {
  try {
    await message.confirm('即将更新缓存刷新浏览器！', '刷新菜单缓存')
    // 清空，从而触发刷新
    wsCacheSession.delete(CACHE_KEY.USER)
    wsCacheSession.delete(CACHE_KEY.ROLE_ROUTERS)
    // 刷新浏览器
    location.reload()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await HomeApi.deleteHemoCard(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
<style scoped lang="scss">
.table-box {
  width: 100%;
  display: flex;
  justify-content: space-between;

  .left-table {
    width: 100%;

    .demo-tabs {
      // margin-top: 10px;
    }
  }
}

.pagination {
  float: right;
  margin-top: 10px;
}
</style>
