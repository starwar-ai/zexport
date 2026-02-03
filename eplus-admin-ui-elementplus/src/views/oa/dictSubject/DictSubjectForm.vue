<template>
  <Dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="500"
  >
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-if="dialogVisible"
    >
      <el-form-item
        prop="feeName"
        label="发票名称"
      >
        <el-input
          v-model="formData.feeName"
          placeholder="请输入发票名称"
        />
      </el-form-item>
      <el-form-item
        prop="feeDesc"
        label="费用描述"
      >
        <el-input
          v-model="formData.feeDesc"
          type="textarea"
          placeholder="请输入费用描述"
        />
      </el-form-item>
      <el-form-item
        prop="subjectId"
        label="管理科目"
      >
        <el-tree-select
          v-if="subjectTree"
          class="!w-100%"
          v-model="formData.subjectId"
          :data="subjectTree"
          :props="defaultProps"
          node-key="id"
          placeholder="请选择管理科目"
          @change="getSubjectName"
        />
        <el-select
          v-else
          class="!w-100%"
        />
      </el-form-item>
      <el-form-item
        prop="showDescFlag"
        label="提醒展示"
      >
        <eplus-dict-select
          v-model="formData.showDescFlag"
          :dictType="DICT_TYPE.IS_INT"
          :clearable="false"
        />
      </el-form-item>
      <el-form-item
        prop="showFeeFlag"
        label="费用实际展示"
      >
        <eplus-dict-select
          v-model="formData.showFeeFlag"
          :dictType="DICT_TYPE.IS_INT"
          :clearable="false"
        />
      </el-form-item>
      <el-form-item
        prop="systemDictTypeList"
        label="字典类型"
      >
        <eplus-input-search-select
          v-model="formData.systemDictTypeList"
          :api="DictTypeApi.getDictTypeSimpleList"
          :params="{
            pageSize: 200,
            pageNo: 1
          }"
          keyword="name"
          label="name"
          value="type"
          class="!w-100%"
          placeholder="请选择"
          :clearable="false"
          multiple
          :disabled="dictTypeDisabled"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        :disabled="formLoading"
        type="primary"
        @click="submitForm"
        >确 定</el-button
      >
      <el-button @click="handleCancel">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { FormRules } from 'element-plus'
import * as DictTypeApi from '@/api/system/dict/dict.type'
import * as DictSubjectApi from '@/api/oa/dictsubject'
import { defaultProps } from '@/utils/tree'

defineOptions({ name: 'DictSubjectForm' })
const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const dictTypeRef = ref('')
const dictTypeDisabled = ref(false)
const subjectTree = ref<Tree[]>([]) // 树形结构
let formData = reactive({
  feeName: '',
  feeDesc: '',
  subjectId: '',
  showDescFlag: '',
  showFeeFlag: '',
  systemDictTypeList: []
})

const formRules = reactive<FormRules>({
  feeName: [{ required: true, message: '发票名称不能为空', trigger: 'blur' }],
  feeDesc: [{ required: true, message: '费用描述不能为空', trigger: 'blur' }],
  subjectId: [{ required: false, message: '科目不能为空', trigger: 'change' }],
  showDescFlag: [{ required: true, message: '提醒展示不能为空', trigger: 'change' }],
  showFeeFlag: [{ required: true, message: '费用实际展示不能为空', trigger: 'change' }],
  systemDictTypeId: [{ required: true, message: '字典类型不能为空', trigger: 'blur' }],
  systemDictDataId: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogTitle.value = t('action.' + type)
  subjectTree.value = await DictSubjectApi.getSubjectTree()
  formType.value = type
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      let res = await DictSubjectApi.getDictSubject(id)
      Object.assign(formData, res)
      // Object.assign(formData, {
      //   ...res,
      //   showDescFlag: res.showDescFlag === null ? 0 : res.showDescFlag,
      //   showFeeFlag: res.showFeeFlag === null ? 0 : res.showFeeFlag
      // })
    } finally {
      formLoading.value = false
    }
  }
  dialogVisible.value = true
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const getDictTypeName = (val) => {
  dictTypeRef.value = ''
  formData.systemDictDataLabel = ''
  formData.systemDictDataId = ''
  formData.systemDictDataValue = ''
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.systemDictTypeId = item.id
          formData.systemDictTypeName = item.name
          formData.systemDictType = item.type
          dictTypeRef.value = item.type
        }
      })
    }
  }
}

const getDictDataName = (val) => {
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.systemDictDataId = item.id
          formData.systemDictDataLabel = item.label
          formData.systemDictDataValue = item.value
        }
      })
    }
  }
}

const getSubjectName = (val) => {
  subjectTree.value.forEach((item) => {
    if (item.id == val) {
      formData.subjectId = item.id
      formData.subjectName = item.name
      formData.subjectDescription = item.description
    }
  })
}

const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData as unknown as DictSubjectApi.DictSubjectVO
    if (formType.value === 'create') {
      await DictSubjectApi.createDictSubject(data)
      message.success(t('common.createSuccess'))
    } else {
      await DictSubjectApi.updateDictSubject(data)
      message.success(t('common.updateSuccess'))
    }
    handleCancel()
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const handleCancel = () => {
  resetForm()
  dialogVisible.value = false
}
/** 重置表单 */
const resetForm = () => {
  // formData = {
  //   formData.feeName: '',
  //   formData.feeDesc: '',
  //   formData.subjectId: '',
  //   formData.showDescFlag: '',
  //   formData.showFeeFlag: '',
  //   formData.systemDictTypeList: []
  // }
  formRef.value?.resetFields()
}
</script>
