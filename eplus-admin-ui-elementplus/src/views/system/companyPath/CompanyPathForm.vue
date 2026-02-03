<template>
  <Dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="1200"
  >
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="60px"
    >
      <el-form-item
        prop="status"
        label="状态"
      >
        <el-select
          v-model="formData.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
          :disabled="statusDisabledRef"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.ENABLE_FLAG)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-row
        :gutter="10"
        v-for="(item, index) in formData.path"
        :key="index"
        class="!w-100%"
      >
        <el-col :span="10">
          <el-form-item
            :label="index > 0 ? '' : '流程'"
            :prop="`path.${index}.id`"
            :rules="{
              required: true,
              message: '此项不能为空',
              trigger: 'change'
            }"
          >
            <el-select
              v-model="item.id"
              placeholder="选择路径"
              @change="getName(item)"
            >
              <el-option
                v-for="el in companyList"
                :key="el.id"
                :label="el.name"
                :value="el.id"
              />
            </el-select>
            <el-button
              type="primary"
              :icon="ArrowUp"
              style="margin-left: 10px"
              :disabled="index === 0"
              @click="moveUp(index)"
            />
            <el-button
              type="primary"
              :icon="ArrowDown"
              style="margin-left: 10px"
              :disabled="index === pathRefLength - 1"
              @click="moveDown(index)"
            />
            <el-button
              type="primary"
              @click="handleRemove(index)"
              style="margin-left: 10px"
              v-if="index > 0"
            >
              -
            </el-button>
            <el-button
              type="primary"
              @click="handleAdd"
              style="margin-left: 10px"
              v-if="index === pathRefLength - 1"
            >
              +
            </el-button>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item
            label-width="0"
            :prop="`path.${index}.allocateType`"
            :rules="{
              required: true,
              message: '此项不能为空',
              trigger: 'change'
            }"
          >
            <eplus-dict-select
              v-model="item.allocateType"
              :dictType="DICT_TYPE.ALLOCATE_TYPE"
              :clearable="false"
              placeholder="类型"
            />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <div v-if="item.allocateType == 1"> - </div>
          <el-form-item
            v-if="item.allocateType == 2"
            label-width="100"
            label="固定利率(%)"
            :prop="`path.${index}.fixRatio`"
            :rules="[{ validator: validateNumber }]"
          >
            <el-input-number
              v-model="item.fixRatio"
              :min="0"
              :max="100"
              :controls="false"
              :precision="2"
            />
          </el-form-item>
          <el-form-item
            v-if="item.allocateType == 3"
            label-width="80"
            label="范围(%)"
            :prop="`path.${index}.rangeMinRatio`"
            :rules="validateRatio(item)"
          >
            <el-input-number
              v-model="item.rangeMinRatio"
              :min="0"
              :max="100"
              :controls="false"
              :precision="2"
              style="width: 72px"
            />-
            <el-input-number
              v-model="item.rangeMaxRatio"
              :min="0"
              :max="100"
              :controls="false"
              :precision="2"
              style="width: 72px"
              :disabled="!item.rangeMinRatio"
            />
          </el-form-item>
        </el-col>

        <el-col :span="2">
          <el-form-item
            label-width="0"
            label=""
            :prop="`path.${index}.allocateConditionType`"
            :rules="{
              required: true,
              message: '此项不能为空'
            }"
          >
            <eplus-dict-select
              style="width: 80px"
              v-model="item.allocateConditionType"
              :dictType="DICT_TYPE.ALLOCATE_CONDITION_TYPE"
              :clearable="false"
              placeholder="条件"
            />
          </el-form-item>
        </el-col>
        <el-col
          :span="3"
          v-if="item.allocateConditionType > 1"
        >
          <el-form-item
            label-width="0"
            :prop="`path.${index}.allocateConditionValue`"
            :rules="[{ validator: validateNumber }]"
          >
            <el-input-number
              v-model="item.allocateConditionValue"
              :min="0"
              :max="100"
              :controls="false"
              :precision="2"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item
        prop="description"
        label="描述"
      >
        <el-input
          v-model="formData.description"
          type="textarea"
          placeholder="请输入描述信息"
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
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as CompanyPathApi from '@/api/system/companyPath'
import { FormRules } from 'element-plus'
import * as LoanAppApi from '@/api/oa/loanapp'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { cloneDeep } from 'lodash-es'
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'

defineOptions({ name: 'InfraFileConfigForm' })
const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const companyList = ref<any[]>([])
const formData = ref({
  id: undefined,
  path: [
    {
      id: undefined,
      allocateType: 1,
      rangeMinRatio: 0,
      rangeMaxRatio: 0,
      fixRatio: 0,
      allocateConditionType: 1,
      allocateConditionValue: 0
    }
  ],
  status: 1,
  description: ''
})

const getName = (item) => {
  const customer = companyList.value.filter((it) => it.id == item.id)
  item.name = customer[0].name
  item.processedFlag = customer[0].processedFlag
}

const validateRatio = (item) => {
  return [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (item.rangeMinRatio <= 0 || item.rangeMaxRatio <= 0) {
          callback(new Error('数值必须大于0'))
        } else if (item.rangeMinRatio >= item.rangeMaxRatio) {
          callback(new Error('范围数据不正确'))
        } else {
          callback()
        }
      }
    }
  ]
}
const validateNumber = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('此项不能为空'))
  } else if (value <= 0) {
    callback(new Error('数值必须大于0'))
  } else {
    callback()
  }
}

const formRules = reactive<FormRules>({
  path: [{ required: true, message: '公司路径不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const statusDisabledRef = ref(true) // 表单 Ref

const pathRefLength = computed(() => {
  return formData.value.path.length
})

//添加订单路径
const handleAdd = () => {
  let obj = {
    id: '',
    allocateType: 1,
    rangeMinRatio: 0,
    rangeMaxRatio: 0,
    fixRatio: 0,
    allocateConditionType: 1,
    allocateConditionValue: 0
  }
  formData.value.path.push(obj)
}

const handleRemove = (index) => {
  formData.value.path.splice(index, 1)
}

/**上移 */
const moveUp = (index) => {
  var arr = formData.value.path
  if (index > 0) {
    let upData = arr[index - 1]
    arr.splice(index - 1, 1)
    arr.splice(index, 0, upData)
  } else {
    alert('已经是第一条，不可上移')
  }
  formData.value.path = arr
}

/**下移 */
const moveDown = (index) => {
  var arr = formData.value.path
  if (index < arr.length - 1) {
    let downData = arr[index + 1]
    arr.splice(index + 1, 1)
    arr.splice(index, 0, downData)
  } else {
    alert('已经是最后一条，不可下移')
  }
  formData.value.path = arr
}

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  // var res = await LoanAppApi.getUnProcessCompanyList()   //查询不包含生产工厂的公司列表
  var res = await LoanAppApi.getcompanySimpleList() //查询包含生产工厂的公司列表
  companyList.value = JSON.parse(JSON.stringify(res || []))
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      let res = await CompanyPathApi.getCompanyPath(id)
      // var path = formData.value.path
      let arr = []
      if (res.path) {
        let obj = {
          ...res.path,
          id: Number(res.path.id)
        }
        arr.push(obj)
        let nextObj = res.path.next
        while (nextObj) {
          let obj = {
            ...nextObj,
            id: Number(nextObj.id)
          }
          arr.push(obj)
          nextObj = nextObj.next
        }
      }
      formData.value = {
        ...res,
        path: arr
      }
      statusDisabledRef.value = false
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = cloneDeep(formData.value)
    data.path = data.path.map((item, index) => {
      return {
        ...item,
        level: index
      }
    })
    data.path = data.path.map((item, index) => {
      return {
        ...item,
        next: data.path[index + 1]
      }
    })
    if (formType.value === 'create') {
      await CompanyPathApi.createCompanyPath(data)
      message.success(t('common.createSuccess'))
    } else {
      await CompanyPathApi.updateCompanyPath(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    path: [
      {
        id: undefined,
        allocateType: 1,
        rangeMinRatio: 0,
        rangeMaxRatio: 0,
        fixRatio: 0,
        allocateConditionType: 1,
        allocateConditionValue: 0
      }
    ],
    status: 1,
    description: ''
  }
  formRef.value?.resetFields()
}
</script>
