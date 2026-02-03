<template>
  <Dialog
    v-model="dialogVisible"
    :title="title"
    width="500"
  >
    <el-form
      ref="formRef"
      :model="formData"
      label-width="100"
    >
      <el-form-item label="上级分类">
        <template #label>
          <Tooltip
            message="此项不填表示一级"
            title="上级分类"
          />
        </template>
        <el-tree-select
          ref="treeRef"
          v-model="formData.parentId"
          :data="classTree"
          :props="defaultProps"
          check-strictly
          node-key="id"
          placeholder="请选择"
          class="!w-100%"
        />
      </el-form-item>
      <el-form-item
        label="分类名称"
        prop="name"
        :rules="{ required: true, message: '请输入分类名称' }"
      >
        <el-input
          v-model="formData.name"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item label="简码">
        <el-input
          v-model="formData.code"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item
        label="海关编码"
        prop="hsCodeId"
      >
        <eplus-input-search-select
          v-model="formData.hsCodeId"
          :api="classApi.getHsData"
          :params="{ pageSize: 100, pageNo: 1 }"
          keyword="code"
          label="code"
          value="id"
          :multiple="false"
          class="!w-100%"
          placeholder="请选择"
          @changeEmit="hsdataCodeChange"
          :defaultObj="{
            code: formData.hsDataCode || undefined,
            id: formData.hsCodeId || undefined
          }"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="handleSave"
        >
          确认
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </template>
  </Dialog>
</template>

<script setup lang="tsx">
import { reactive, ref } from 'vue'
import * as classApi from '@/api/pms/class'
import { defaultProps, handleTree } from '@/utils/tree'
import { cloneDeep } from 'lodash-es'

const message = useMessage()
defineOptions({ name: 'ClassCreateForm' })
const treeRef = ref()
const title = ref('')
const dialogVisible = ref(false)
const formRef = ref()
let formData = reactive({
  id: undefined,
  parentId: '',
  name: undefined,
  code: undefined,
  hsCodeId: undefined,
  hsDataCode: undefined
})
const emit = defineEmits(['success'])

const handleCancel = () => {
  dialogVisible.value = false
}
const handleSave = () => {
  let nodeGrade = treeRef.value.getCurrentNode()?.grade || 0
  if (nodeGrade == 3) {
    message.warning('产品分类等级不可超过三级')
    return
  }
  formRef.value.validate((valid) => {
    if (valid) {
      let data = cloneDeep(formData)

      if (data?.id) {
        classApi
          .updateClass({
            ...data,
            grade: nodeGrade + 1,
            parentId: data.parentId ? data.parentId : 0
          })
          .then((res) => {
            message.success('修改成功')
            handleCancel()
            emit('success')
          })
      } else {
        classApi
          .createClass({
            ...data,
            grade: nodeGrade + 1,
            parentId: data.parentId ? data.parentId : 0
          })
          .then((res) => {
            message.success('保存成功')
            handleCancel()
            emit('success')
          })
      }
    }
  })
}
const hsdataCodeChange = (val, list) => {
  let selectedItem = list?.find((item) => item.id === val)
  formData.hsDataCode = selectedItem?.code || ''
}
const classTree = ref<Tree[]>([]) // 树形结构
const getTree = async () => {
  classTree.value = handleTree(await classApi.getClassTree())
}

const show = async (obj?: any) => {
  if (obj?.id) {
    formData.id = obj.id
    formData.parentId = obj.parentId == 0 ? '' : obj.parentId
    formData.name = obj.name
    formData.code = obj.code
    formData.hsCodeId = obj.hsCodeId
    formData.hsDataCode = obj.hsDataCode
    title.value = '编辑分类'
  } else {
    formData.id = undefined
    formData.parentId = obj.parentId
    formData.name = undefined
    formData.code = undefined
    formData.hsCodeId = undefined
    formData.hsDataCode = undefined
    title.value = '新增分类'
  }
  await getTree()
  dialogVisible.value = true
  nextTick(() => {
    treeRef.value.setCurrentKey(formData.parentId)
  })
}
defineExpose({ show })
</script>
