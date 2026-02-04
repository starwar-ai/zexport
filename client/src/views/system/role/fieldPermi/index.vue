<template>
  <el-row :gutter="20">
    <!-- 左侧部门树 -->
    <el-col
      :span="4"
      :xs="24"
    >
      <ContentWrap class="leftContainer">
        <DeptTree
          @node-click="handleDeptNodeClick"
          ref="DeptTreeRef"
        />
      </ContentWrap>
    </el-col>
    <el-col
      :span="20"
      :xs="24"
    >
      <!-- 搜索 -->
      <ContentWrap class="searchContain">
        <el-form
          class="-mb-15px"
          :model="queryParams"
          ref="queryFormRef"
          :inline="true"
          label-width="68px"
        >
          <!-- <el-form-item label="表名称" prop="username">
            <el-input v-model="queryParams.tableName" placeholder="请输入表名称" clearable @keyup.enter="handleQuery"
              class="!w-240px" />
          </el-form-item> -->
          <!-- <el-form-item label="表描述" prop="username">
            <el-input v-model="queryParams.tableComment" placeholder="请输入表名称" clearable @keyup.enter="handleQuery"
              class="!w-240px" />
          </el-form-item> -->
          <el-form-item
            label="字段名称"
            prop="fieldName"
          >
            <el-input
              v-model="queryParams.fieldName"
              placeholder="请输入字段名称"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
          <el-form-item
            label="字段注释"
            prop="fieldComment"
          >
            <el-input
              v-model="queryParams.fieldComment"
              placeholder="请输入字段名称"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
          <!-- <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="用户状态" clearable class="!w-240px">
              <el-option v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)" :key="dict.value" :label="dict.label"
                :value="dict.value" />
            </el-select>
          </el-form-item> -->
          <!-- <el-form-item label="创建时间" prop="createTime">
            <el-date-picker v-model="queryParams.createTime" value-format="YYYY-MM-DD HH:mm:ss" type="datetimerange"
              start-placeholder="开始日期" end-placeholder="结束日期" class="!w-240px" />
          </el-form-item> -->
          <el-form-item>
            <el-button @click="handleQuery"> <Icon icon="ep:search" />搜索 </el-button>
            <el-button @click="resetQuery"> <Icon icon="ep:refresh" />重置 </el-button>
          </el-form-item>
        </el-form>
      </ContentWrap>
      <ContentWrap class="tableContain">
        <!-- <el-scrollbar height="55vh"> -->
        <el-table
          v-loading="loading"
          :data="list"
          height="60vh"
        >
          <!-- <el-table-column fixed label="字段编号" align="center" key="id" prop="id" /> -->
          <!-- <el-table-column label="表名称" align="center" prop="tableName" :show-overflow-tooltip="true" /> -->
          <!-- <el-table-column label="表描述" align="center" prop="tableComment" :show-overflow-tooltip="true" /> -->
          <el-table-column
            label="字段名称"
            align="center"
            prop="fieldName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="字段注释"
            align="center"
            prop="fieldComment"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="字段类型"
            align="center"
            prop="fieldType"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="是否字典"
            align="center"
            prop="dictFlag"
            :show-overflow-tooltip="true"
            :formatter="
              (val) => {
                return val.dictFlag ? '是' : '否'
              }
            "
          />
          <!-- <el-table-column label="字典类型" align="center" prop="dictType" :show-overflow-tooltip="true" /> -->
          <!-- <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" /> -->
          <el-table-column
            label="显示状态"
            align="center"
            width="160"
            prop="fieldPermissionFlag"
          >
            <template #default="scope">
              <el-checkbox
                v-model="scope.row.fieldPermissionFlag"
                label=""
                size="large"
              />
            </template>
          </el-table-column>
        </el-table>
        <!-- </el-scrollbar> -->
        <div class="btnContainer">
          <el-button
            type="primary"
            @click="onSubmit"
            :loading="buttonRef"
            >保存</el-button
          >
        </div>
      </ContentWrap>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import DeptTree from './DeptTree.vue'
import * as SysTableApi from '@/api/system/role/fieldPermi'

defineOptions({ name: 'SystemUser' })

// const { t } = useI18n() // 国际化
const router = useRouter() // 路由
const queryRoleId = router.currentRoute.value.query?.id
const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数
const fieldIds = ref([]) //无权限的字段id列表
const treeData = ref([])
const buttonRef = ref(false)
const queryParams = reactive({
  createTime: [],
  tableName: undefined,
  tableComment: undefined,
  fieldName: undefined,
  fieldComment: undefined
})
const currentTableName = ref('')
const queryFormRef = ref() // 搜索的表单
const DeptTreeRef = ref()
/** 查询列表 */
const getList = async (params = {}) => {
  loading.value = true
  try {
    const data = await SysTableApi.getSysFieldsList({
      tableName: params?.tableName || unref(treeData)[0].tableName,
      roleId: queryRoleId,
      ...params
    })
    for (const item of data) {
      item.fieldPermissionFlag = item.fieldPermissionFlag ? true : false
    }
    list.value = data
    message.success('查询成功！')
  } catch {
    message.error('查询失败')
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList(queryParams)
  unref(DeptTreeRef).setCurrentTreeKey(queryParams?.tableName)
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
}

/** 处理部门被点击 */
const handleDeptNodeClick = async (row) => {
  fieldIds.value = []
  currentTableName.value = row.id
  queryParams.tableName = row.id
  await getList({ tableName: row.id })
}
/** 提交表格 */
const onSubmit = async () => {
  buttonRef.value = true
  try {
    for (const item of list.value) {
      item.fieldPermissionFlag ? '' : fieldIds.value.push(item.id)
    }
    await SysTableApi.setFields2Role({ fieldIds: fieldIds.value, roleId: queryRoleId })
    fieldIds.value = []
    message.success('请求成功！')
  } catch {
    message.error('请求失败')
  } finally {
    getList({ tableName: currentTableName.value })
    buttonRef.value = false
  }
}
/** 初始化 */
onMounted(async () => {
  treeData.value = await unref(DeptTreeRef).getTree()
  queryParams.tableName = treeData.value[0].tableName
  getList()
})
</script>
<style lang="scss" scoped>
.tableContain {
  .btnContainer {
    padding-top: 20px;
    text-align: right;
  }
}
</style>
