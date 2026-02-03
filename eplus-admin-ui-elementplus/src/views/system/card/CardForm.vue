<template>
  <Dialog
    v-model="dialogVisible"
    :title="dialogTitle"
  >
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item
        label="组件名称"
        prop="currentComponent"
      >
        <el-input
          v-model="formData.currentComponent"
          placeholder="例如:FapiaoGadget、TaskGadget"
        />
      </el-form-item>
      <el-form-item
        label="卡片名称"
        prop="title"
      >
        <el-input
          v-model="formData.title"
          placeholder="请输入卡片名称,例如:发票夹"
        />
      </el-form-item>
      <el-form-item
        label="卡片描述"
        prop="description"
      >
        <el-input
          v-model="formData.description"
          autosize
          type="textarea"
          placeholder="请输入卡片描述,例如:此类卡片用于记录发票等..."
        />
      </el-form-item>
      <el-form-item label="组件图标">
        <UploadZoomPic
          v-model="formData.picture"
          :limit="1"
        />
        <!-- <UploadPic
          ref="UploadPicRef"
          :pictureList="formData.picture"
          @success="getPicList"
          :limit="1"
        /> -->
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        :disabled="formLoading"
        type="primary"
        @click="submitForm"
        >确 定</el-button
      >
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as HomeApi from '@/api/home'
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'SystemMenuForm' })

const { wsCacheSession } = useCache()
const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  currentComponent: '', // 组件
  title: '',
  description: '',
  picture: [],

  status: 0,
  uniqueCode: Math.floor(Date.now() / 1000), // 十位数的时间戳
  tabId: '',
  filterCondition: '',
  type: '', // 类型
  titleFlag: false,
  basicFlag: 1
})

const formRules = reactive({
  currentComponent: [{ required: true, message: '组件名称不能为空', trigger: 'blur' }],
  title: [{ required: true, message: '卡片名称不能为空', trigger: 'blur' }],
  description: [{ required: true, message: '卡片描述不能为空', trigger: 'blur' }]
  // status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, row?: Object) => {
  dialogVisible.value = true
  dialogTitle.value = type == 'create' ? '新增卡片' : '编辑卡片'
  if (type == 'create') {
    formData.value.currentComponent = ''
    formData.value.title = ''
    formData.value.description = ''
    formData.value.picture = []
  } else if (type == 'update') {
    let data = cloneDeep(row) as any
    formData.value = data
    formData.value.picture = [data.picture]
  }

  formType.value = type
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const getPicList = (params) => {
  formData.value.picture = params
  // formData.value.picture = {
  //   id: params[0].id,
  //   name: params[0].name,
  //   fileUrl: params[0].fileUrl,
  //   mainFlag: 0
  // }
}

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = cloneDeep(formData.value)
    if (formType.value === 'create') {
      data.type = data.currentComponent
      await HomeApi.createCard({
        ...data,
        picture: {
          id: data.picture[0].id,
          name: data.picture[0].name,
          fileUrl: data.picture[0].fileUrl,
          mainFlag: 0
        }
      })
      message.success(t('common.createSuccess'))
    } else {
      await HomeApi.cardUpdate({
        ...data,
        picture: {
          id: data.picture[0].id,
          name: data.picture[0].name,
          fileUrl: data.picture[0].fileUrl,
          mainFlag: 0
        }
      })
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
    wsCacheSession.delete(CACHE_KEY.ROLE_ROUTERS)
  }
}
</script>
