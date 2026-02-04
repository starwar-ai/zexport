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
        label="发票名称"
        prop="feeName"
      >
        <el-input
          v-model="queryParams.feeName"
          placeholder="请输入发票名称"
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
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['oa:dict-subject:create']"
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
          v-hasPermi="['oa:dict-subject:export']"
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
      height="780"
    >
      <el-table-column
        label="发票名称"
        align="center"
        prop="feeName"
      />
      <el-table-column
        label="费用描述"
        align="center"
        prop="feeDesc"
      />
      <el-table-column
        label="管理科目"
        align="center"
        prop="subjectName"
      />
      <el-table-column
        label="描述展示"
        align="center"
        prop="showDescFlag"
      >
        <template #default="scope">
          {{ getDictLabel(DICT_TYPE.IS_INT, scope.row.showDescFlag) }}
        </template>
      </el-table-column>
      <el-table-column
        label="费用实际展示"
        align="center"
        prop="showFeeFlag"
      >
        <template #default="scope">
          {{ getDictLabel(DICT_TYPE.IS_INT, scope.row.showFeeFlag) }}
        </template>
      </el-table-column>
      <el-table-column
        label="关联字典"
        align="center"
        prop="systemDictTypeDescList"
      >
        <template #default="scope">
          {{ getColVal(scope.row.systemDictTypeDescList) }}
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
      >
        <template #default="scope">
          <el-button
            type="primary"
            v-hasPermi="['oa:dict-subject:update']"
            link
            @click="openForm('edit', scope.row.id)"
            >编辑
          </el-button>
          <el-button
            v-hasPermi="['oa:dict-subject:delete']"
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
      :pageSizes="globalStore().glbPageSizes"
    />
  </ContentWrap>

  <!-- 表单弹窗：新增编辑 -->
  <DictSubjectForm
    ref="detailRef"
    @success="getList"
  />
</template>
<script lang="ts" setup>
const subjectTree = ref<Tree[]>([]) // 树形结构
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import download from '@/utils/download'
import * as DictSubjectApi from '@/api/oa/dictsubject'
import DictSubjectForm from './DictSubjectForm.vue'
import { isValidArray } from '@/utils/is'
import { globalStore } from '@/store/modules/globalVariable'

defineOptions({ name: 'DictSubject' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: globalStore().glbPageSize
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const getColVal = (list) => {
  if (isValidArray(list)) {
    return list.map((item) => item.name).join(',')
  } else {
    return '-'
  }
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DictSubjectApi.getDictSubjectPage(queryParams)
    list.value = data.list
    total.value = data.total
    loading.value = false
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
    const data = await DictSubjectApi.exportDictSubject(queryParams)
    download.excel(data, '类别配置.xls')
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
    await DictSubjectApi.deleteDictSubject(id)
    // 刷新列表
    await getList()
    message.success(t('common.delSuccess'))
  } catch {}
}
/** 初始化 **/
onMounted(async () => {
  queryParams.pageNo = 1
  getList()
  subjectTree.value = await DictSubjectApi.getSubjectTree()
})
</script>
