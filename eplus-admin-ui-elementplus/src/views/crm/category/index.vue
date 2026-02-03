<template>
  <el-row>
    <ContentWrap class="!w100%">
      <el-button
        type="primary"
        @click="handleCreate"
        v-hasPermi="['crm:category:create']"
      >
        新增
      </el-button>
    </ContentWrap>
  </el-row>
  <el-row :gutter="20">
    <el-col
      :span="4"
      :xs="24"
    >
      <ContentWrap class="h-1/1">
        <Tree
          ref="categoryTreeRef"
          @node-click="handleDeptNodeClick"
        />
      </ContentWrap>
    </el-col>
    <el-col
      :span="20"
      :xs="24"
    >
      <ContentWrap>
        <div
          class="flex"
          v-if="selectedNodeInfo?.id"
        >
          <p class="mr40px">
            <span class="font-600">本级分类名称：</span>
            {{ selectedNodeInfo?.name }}
          </p>
          <p class="mr40px">
            <span class="font-600">简码：</span>
            {{ selectedNodeInfo?.code || '-' }}
          </p>
          <p class="mr40px">
            <el-button
              link
              type="primary"
              @click="handleEdit(selectedNodeInfo)"
              v-hasPermi="['crm:category:update']"
            >
              编辑
            </el-button>
          </p>
        </div>
        <div
          class="flex"
          v-else
        >
          <p>本级分类名称：全部分类</p>
        </div>
      </ContentWrap>
      <ContentWrap>
        <div class="mb20px font-600">
          {{ selectedNodeInfo?.id ? '下级分类列表' : '全部分类列表' }}
        </div>
        <el-table :data="list">
          <el-table-column
            label="上级分类"
            align="center"
            prop="parentName"
          />
          <el-table-column
            label="分类名称"
            align="center"
            prop="name"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="简码"
            align="center"
            prop="code"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="创建时间"
            align="center"
            prop="createTime"
            :formatter="dateFormatter"
            width="180"
          />
          <el-table-column
            label="操作"
            align="center"
            width="160"
          >
            <template #default="scope">
              <div class="flex items-center">
                <el-button
                  link
                  type="primary"
                  @click="handleDelete(scope.row.id)"
                  v-hasPermi="['crm:category:delete']"
                >
                  删除
                </el-button>
                <el-button
                  link
                  type="primary"
                  @click="handleEdit(scope.row)"
                  v-hasPermi="['crm:category:update']"
                >
                  编辑
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <Pagination
          :total="total"
          v-model:page="queryParams.pageNo"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </ContentWrap>
    </el-col>
  </el-row>

  <Form
    ref="createFormRef"
    @success="handleSuccess"
  />
</template>
<script setup lang="tsx">
import { ref } from 'vue'
import Tree from './Tree.vue'
import Form from './Form.vue'
import * as CategoryApi from '@/api/crm/category'
import { dateFormatter } from '@/utils/formatTime'

const message = useMessage()
const categoryTreeRef = ref()
const createFormRef = ref()
const selectedNode = ref()
const selectedNodeInfo = ref()

defineOptions({
  name: 'CustCategory'
})

const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  parentId: ''
})

const getInfo = async () => {
  if (selectedNode.value?.id && selectedNode.value?.id != 0) {
    selectedNodeInfo.value = await CategoryApi.getCategoryInfo(selectedNode.value?.id)
  } else {
    selectedNodeInfo.value = {}
  }
}
const getList = async () => {
  let res = await CategoryApi.getCategoryPage(queryParams)
  list.value = res?.list || []
  total.value = res?.total || 0
}

const handleEdit = (row) => {
  createFormRef.value.show({ ...row, type: 'edit' })
}
const handleDelete = async (id: number) => {
  ElMessageBox.confirm('是否删除所选中数据?', '系统提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await CategoryApi.deleteCategory(id)
    message.success('删除成功')
    handleSuccess()
  })
}
const handleCreate = () => {
  if (selectedNode.value) {
    if (selectedNode.value.grade === 3) {
      createFormRef.value.show({ type: 'create', parentId: selectedNode.value.parentId })
    } else {
      createFormRef.value.show({
        type: 'create',
        parentId: selectedNode.value.id == 0 ? '' : selectedNode.value.id
      })
    }
  } else {
    createFormRef.value.show({ type: 'create', parentId: '' })
  }
}
const handleSuccess = async () => {
  await getInfo()
  await getList()
  categoryTreeRef.value.getTree()
}

const handleDeptNodeClick = (row) => {
  selectedNode.value = row
  queryParams.parentId = row.id == 0 ? '' : row.id
  getInfo()
  getList()
}

onMounted(() => {
  getList()
})
</script>
