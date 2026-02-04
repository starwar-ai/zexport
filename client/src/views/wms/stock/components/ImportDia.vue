<template>
  <Dialog
    v-model="dialogVisible"
    title="库存导入"
    width="400"
  >
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :action="importUrl + '?updateSupport=' + updateSupport"
      :auto-upload="false"
      :disabled="formLoading"
      :headers="uploadHeaders"
      :limit="1"
      :on-error="submitFormError"
      :on-exceed="handleExceed"
      :on-success="submitFormSuccess"
      accept=".xlsx, .xls"
      drag
    >
      <Icon icon="ep:upload" />
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <span>仅允许导入 xls、xlsx 格式文件。</span>
          <el-link
            :underline="false"
            style="font-size: 12px; vertical-align: baseline"
            type="primary"
            @click="importTemplate"
          >
            下载模板
          </el-link>
        </div>
      </template>
    </el-upload>
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
// 修改导入路径为正确的index文件
import * as stockApi from '@/api/wms/stock/index'
import { getAccessToken } from '@/utils/auth'
import { useRouter } from 'vue-router'

defineOptions({ name: 'SystemUserImportForm' })

const message = useMessage() // 消息弹窗
const router = useRouter() // 路由实例

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const uploadRef = ref()
const importUrl =
  import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/wms/stock-import/import-excel'
const uploadHeaders = ref() // 上传 Header 头
const fileList = ref([]) // 文件列表
const updateSupport = ref(0) // 是否更新已经存在的用户数据

/** 打开弹窗 */
const open = () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const submitForm = async () => {
  console.log('开始提交表单') // 调试信息

  if (fileList.value.length == 0) {
    message.error('请上传文件')
    return
  }

  console.log('文件列表:', fileList.value) // 调试信息

  // 提交请求
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken()
    // 'tenant-id': getTenantId()
  }

  console.log('上传头信息:', uploadHeaders.value) // 调试信息
  formLoading.value = true
  uploadRef.value!.submit()
}

/** 文件上传成功 */
const emits = defineEmits(['success'])
const submitFormSuccess = (response: any) => {
  console.log('上传成功响应:', response) // 调试信息

  if (response.code !== 0) {
    message.error(response.msg)
    formLoading.value = false
    return
  }

  // 关闭弹窗
  dialogVisible.value = false
  formLoading.value = false

  // 跳转到结果页面，传递导入结果数据
  console.log('准备跳转到结果页面，数据:', response.data) // 调试信息
  router
    .push({
      path: '/wms/import-result',
      query: {
        data: encodeURIComponent(JSON.stringify(response.data))
      }
    })
    .then(() => {
      console.log('路由跳转成功') // 调试信息
    })
    .catch((error) => {
      console.error('路由跳转失败:', error) // 调试信息
      message.error('页面跳转失败，请手动访问结果页面')
    })

  // 发送操作成功的事件
  emits('success')
}

/** 上传错误提示 */
const submitFormError = (): void => {
  message.error('上传失败，请您重新上传！')
  formLoading.value = false
}

/** 重置表单 */
const resetForm = () => {
  // 重置上传状态和文件
  formLoading.value = false
  uploadRef.value?.clearFiles()
}

/** 文件数超出提示 */
const handleExceed = (): void => {
  message.error('最多只能上传一个文件！')
}

/** 下载模板操作 - 完全修复版本 */
const importTemplate = async () => {
  try {
    formLoading.value = true

    // 获取Blob数据
    const blobData = await stockApi.importStockTemplate()

    // 创建下载链接
    const blob = new Blob([blobData], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })

    // 创建临时URL
    const url = window.URL.createObjectURL(blob)

    // 创建a标签并触发下载
    const link = document.createElement('a')
    link.href = url
    link.download = '库存导入模版.xlsx'
    document.body.appendChild(link)
    link.click()

    // 清理
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    message.success('模板下载成功')
  } catch (error: any) {
    console.error('下载模板失败:', error)

    // 提供详细的错误信息
    let errorMsg = '下载模板失败'
    if (error?.response?.data?.msg) {
      errorMsg = error.response.data.msg
    } else if (error?.message) {
      errorMsg = error.message
    }

    message.error(errorMsg)
  } finally {
    formLoading.value = false
  }
}
</script>
