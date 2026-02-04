<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
  >
    <eplus-form-items
      title=""
      :formSchemas="purchaseSchemas"
    >
      <template #isSupplementOrder>
        <div style="width: 100%">
          <el-radio-group
            v-model="formData.isSupplementOrder"
            @change="handleSupplementOrder"
          >
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </div>
      </template>

      <!-- <template #designType>
        <eplus-dict-select
          v-model="formData.designType"
          style="width: 100%"
          multiple
          :dictType="DICT_TYPE.DESIGN_TYPE"
        />
      </template> -->

      <template #contractCode>
        {{ formData.contractCode }}

        <el-button
          @click="selectContractCode"
          class="ml15px"
          >选择合同编号
        </el-button>
      </template>

      <template #expectCompleteDate>
        <el-date-picker
          v-model="formData.expectCompleteDate"
          clearable
          valueFormat="x"
          type="date"
          style="width: 100%"
        />
      </template>

      <template #annex>
        <!-- 附件 -->
        <UploadList
          :fileList="formData.annex"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
  </eplus-form>

  <SmsDialog
    ref="SmsDialogRef"
    @sure="
      (code) => {
        formData.contractCode = code
      }
    "
  />
  <PurchaseDialog
    ref="PurchaseDialogRef"
    @sure="
      (code) => {
        formData.contractCode = code
      }
    "
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import * as DesignTaskApi from '@/api/dtms/designTask' // 设计任务单接口集合
import { useUserStore } from '@/store/modules/user'
import SmsDialog from './components/SmsDialog.vue'
import PurchaseDialog from './components/PurchaseDialog.vue'

defineOptions({ name: 'DesignTaskForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}
const message = useMessage()

const loading = ref(false)

const isSupplementOrderFlag = ref(false)
const specificDesignerFlag = ref(true)
const reasonFlag = ref(true)
const contractCodeList = ref([])

const formData = reactive({
  isSupplementOrder: 0,
  specialPermissionFlag: 1,
  remark: '',
  submitFlag: '', // 1提交 0 暂存
  applyDesignerId: useUserStore().getUser.id,
  applyDesignerName: useUserStore().getUser.nickname,
  applyDesignerDeptId: useUserStore().getUser.deptId,
  applyDesignerDeptName: useUserStore().getUser.deptName,
  contractType: 1,
  specificDesignerIds: [],
  specificDesigners: [],
  specialPermissionReason: ''
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const handleSupplementOrder = (val) => {
  isSupplementOrderFlag.value = val == 1 ? true : false
  specificDesignerFlag.value = !isSupplementOrderFlag.value
}

const SmsDialogRef = ref()
const PurchaseDialogRef = ref()

const selectContractCode = () => {
  if (formData.contractType === 1) {
    SmsDialogRef.value.open()
  } else if (formData.contractType === 2) {
    PurchaseDialogRef.value.open()
  } else {
    message.warning('请先选择合同类型')
  }
}

const contractTypeChange = async (type, code?) => {
  formData.contractCode = ''
}

const getSpecificDesignersList = (val: any) => {
  formData.specificDesigners = val
}

const specialPermissionFlagChange = (val) => {
  formData.specialPermissionReason = ''
  reasonFlag.value = getDictLabel(DICT_TYPE.APPROVED_TYPE, val) == '特批' ? false : true
}

let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'name',
    label: '设计名称',
    editable: true,
    component: <el-input />,
    rules: {
      trigger: 'blur',
      required: true,
      message: '请填写设计名称'
    }
  },
  {
    field: 'specialPermissionFlag',
    label: '紧急程度',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.APPROVED_TYPE}
        style="width:100%"
        onChangeEmit={(val) => specialPermissionFlagChange(val)}
      />
    ),
    rules: {
      required: true,
      trigger: 'change',
      message: '请选择紧急程度'
    }
  },
  {
    field: 'specialPermissionReason',
    label: '特批原因',
    disabled: reasonFlag,
    component: <el-input />,
    rules: {
      trigger: 'blur',
      required: true,
      message: '请填写特批原因'
    }
  },
  {
    field: 'designType',
    label: '任务类型',
    component: (
      <eplus-dict-select
        multiple
        dictType={DICT_TYPE.DESIGN_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      trigger: 'blur',
      required: true,
      message: '请选择任务类型'
    }
  },
  {
    field: 'contractType',
    label: '合同类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CONTRACT_TYPE}
        style="width:100%"
        onChange={(val) => {
          if (val) {
            contractTypeChange(val)
          } else {
            contractCodeList.value = []
          }
        }}
      />
    )
  },
  {
    field: 'contractCode',
    label: '合同编号',
    editable: true
  },
  {
    field: 'expectCompleteDate',
    label: '希望完成日期',
    rules: {
      trigger: 'blur',
      required: true,
      message: '请选择希望完成日期'
    }
  },
  {
    field: 'isSupplementOrder',
    label: '是否补单',
    rules: {
      required: true,
      trigger: 'change',
      message: '请选择是否补单'
    }
  },
  {
    field: 'specificDesignerIds',
    label: '指定设计师',
    editable: true,
    disabled: specificDesignerFlag,
    component: (
      <eplus-user-select
        class="!w-100%"
        multiple={true}
        onChange={getSpecificDesignersList}
      ></eplus-user-select>
    ),
    rules: {
      trigger: 'blur',
      required: isSupplementOrderFlag,
      message: '请选择指定设计师'
    }
  },
  {
    field: 'designRequirement',
    label: '设计要求',
    editable: true,
    component: (
      <el-input
        placeholder="请输入设计要求"
        type="textarea"
        maxlength={500}
        show-word-limit
      />
    ),
    span: 24,
    rules: {
      trigger: 'blur',
      required: true,
      message: '请填写设计要求'
    }
  },
  {
    field: 'materialDesc',
    label: '素材说明',
    editable: true,
    component: (
      <el-input
        placeholder="请输入素材说明"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    ),
    span: 24,
    rules: {
      trigger: 'blur',
      required: true,
      message: '请填写素材说明'
    }
  },
  {
    field: 'remark',
    label: '备注',
    editable: true,
    component: (
      <el-input
        placeholder="请输入备注"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    ),
    span: 24
  },

  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  }
])

const getFileList = (params) => {
  formData.annex = params
}

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

const saveForm = async (type) => {
  formData.specialPermissionFlag = Number(formData.specialPermissionFlag)
  formData.contractType = formData.contractType ? formData.contractType : null
  const valid = await formRef.value.validate()
  if (!valid) return
  try {
    let params = { ...formData }
    params.designType = formData.designType.join(',')
    params.submitFlag = type
    if (props.mode == 'create') {
      await DesignTaskApi.createDesignTask({ ...params })
      message.success('保存成功')
    } else {
      await DesignTaskApi.designUpdate({ ...params })
      message.success('更新成功')
    }
    emit('handleSuccess', 'success')
    close()
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  updateDialogActions(
    <el-button
      onClick={() => {
        saveForm(0)
      }}
      key="saveDesign"
      type="primary"
      hasPermi="dtms:design:create"
    >
      保存
    </el-button>,
    <el-button
      onClick={() => {
        saveForm(1)
      }}
      key="submitDesign"
      hasPermi="dtms:design:submit"
    >
      提交
    </el-button>
  )
  if (props.mode === 'edit') {
    let res = await DesignTaskApi.designDetail(props.id)
    let obj = {
      ...res
    }
    obj.designType = obj.designType.split(',')
    obj.designType = obj.designType.map(Number)
    obj.contractType = obj.contractType == null ? '' : Number(obj.contractType)
    specialPermissionFlagChange(obj.specialPermissionFlag)
    handleSupplementOrder(obj.isSupplementOrder)
    if (obj.specificDesigners) {
      obj.specificDesigners.forEach((item) => {
        if (!obj.specificDesignerIds) {
          obj.specificDesignerIds = []
        }
        obj.specificDesignerIds.push(item.userId)
      })
    }
    Object.assign(formData, obj)
  }
})
</script>
<style scoped lang="scss">
.warning-tips {
  font-size: 11px;
  margin-left: -80px;
  color: #d00;
}
</style>
