<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'oa:travel-apply:create',
      handler: () => saveForm()
    }"
    :submitAction="{
      permi: 'oa:travel-apply:submit',
      handler: () => submitForm()
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="explainSchemas"
    >
      <template #referenceStandard>
        <el-row
          :gutter="20"
          style="font-size: 14px; width: 100%"
        >
          <el-col :span="12">
            <el-card>
              <template #header> 公出 </template>
              <div>
                1、小于8小时的外出办事(从公司出发或公办结束回公司，有一次打卡记录)<br />
                2、补贴:1.0元/公里，过路费按实际发生额计，无其他补贴。
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header> 出差 </template>
              <br />大于或等于8小时的外出办事，享受出差补贴.
            </el-card>
          </el-col>
        </el-row>
      </template>
      <template #amount>
        <EplusMoney
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
        />
      </template>
      <template #startTime>
        <el-date-picker
          v-model="formData.startTime"
          type="date"
          placeholder="选择开始日期"
          value-format="x"
          style="width: 100%"
          :disabled-date="disableStartDate"
          @change="handleStartTimeChange"
        />
      </template>
      <template #endTime>
        <el-date-picker
          v-model="formData.endTime"
          type="date"
          placeholder="选择结束日期"
          value-format="x"
          style="width: 100%"
          :disabled-date="disableEndDate"
          @change="handleEndTimeChange"
        />
      </template>
    </eplus-form-items>
    <!-- 费用归集部分 -->
    <div class="fee-share-section">
      <div class="section-title">费用归集</div>
      <div class="fee-share-content">
        <eplus-form-items
          title=""
          :formSchemas="companySchema"
        />
        <FeeShareOrderFormCom
          v-if="dataLoaded || mode === 'create'"
          ref="FeeShareOrderFormComRef"
          :feeType="0"
          :info="formData.feeShare"
          :mode="mode"
        />
      </div>
    </div>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import * as TravalApplyApi from '@/api/oa/travelApply'
import { useUserStore } from '@/store/modules/user'
import FeeShareOrderFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'
import { EplusDictRadio } from '@/components/EplusSelect'

const getCompanyName = (item) => {
  formData.companyName = item.name
}

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'EntertainmentExpensesForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  homeCard?: Object
}>()
const loading = ref(false)
const tabLoading = ref(false)
const dataLoaded = ref(false) // 添加数据加载状态
const feeShareobj = reactive({
  type: '',
  item: {}
})
const formData: EplusAuditable = reactive({
  auditStatus: 0,
  startTime: undefined as number | undefined, // 开始时间
  endTime: undefined as number | undefined, // 结束时间
  travelType: undefined,
  travelName: '',
  travelLevel: '',
  travelNum: '',
  applyerId: undefined,
  amount: {
    amount: 0,
    currency: 'RMB'
  },
  travelEntourageIds: [],
  companyId: '',
  companyName: '',
  purpose: '',
  remarks: '',
  feeShare: [] // 添加费用归集数据属性
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const explainSchemas: EplusFormSchema[] = reactive([
  {
    field: 'referenceStandard',
    label: '说明',
    span: 24
  },
  {
    field: 'travelType',
    label: '出差类型',
    xl: 8,
    lg: 12,
    component: <EplusDictRadio dictType={DICT_TYPE.TRAVEL_TYPE} />,
    rules: [
      {
        required: true
      }
    ]
  },
  {
    field: 'dest',
    label: '出差地点',
    component: <el-input placeholder="请输入出差地点" />,
    rules: {
      required: true,
      message: '请输入出差地点'
    }
  },
  {
    field: 'transportationType',
    label: '交通工具',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.TRAVAL_TRANSPORTATION_TYPE}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择交通工具'
    }
  },
  {
    field: 'startTime',
    label: '开始时间',
    rules: {
      required: true,
      message: '请选择开始时间'
    }
  },
  {
    field: 'endTime',
    label: '结束时间',
    rules: {
      required: true,
      message: '请选择结束时间'
    }
  },
  {
    field: 'duration',
    label: '出差时长（天）',
    width: '150',
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
        disabled={true}
      />
    ),
    rules: [{ required: true, message: '请输入出差时长' }]
  },
  {
    field: 'amount',
    label: '预估费用',
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          let regx = /^(\d{1,11})(\.?\d{1,3})?$/
          if (value.amount <= 0) {
            callback(new Error(`金额必须大于0`))
          } else if (!regx.test(value.amount)) {
            callback(new Error('金额只能输入正数,最多11位整数,3位小数'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'purpose',
    label: '出差事由',
    span: 12,
    editable: true,
    component: (
      <el-input
        type="textarea"
        placeholder="字数不小于10个字，标注关键信息，含客户/供应商/展会/项目等具体名称之一，或者费用产生的主要原因"
      />
    ),
    rules: {
      required: true,
      message: '请输入出差事由'
    }
  },
  {
    field: 'intendedObjectives',
    label: '拟达成目标',
    span: 12,
    editable: true,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入拟达成目标"
      />
    ),
    rules: {
      required: true,
      message: '请输入拟达成目标'
    }
  },
  {
    field: 'remarks',
    label: '备注',
    span: 24,
    editable: true,
    component: (
      <el-input
        type="textarea"
        placeholder="请输入备注"
      />
    ),
    rules: {
      required: false,
      message: '请输入备注'
    }
  }
])
const companySchema = [
  {
    field: 'companyId',
    label: '归属主体',
    editable: true,
    component: (
      <eplus-custom-select
        api={TravelReimbApi.getcompanySimpleList}
        label="name"
        value="id"
        clearable={false}
        placeholder="请选择"
        class="!w-100%"
        onChange={getCompanyName}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择归属主体'
      }
    ]
  }
]

const emit = defineEmits(['handleSuccess'])
const formRef = ref()

const FeeShareOrderFormComRef = ref()

// 日期限制相关方法
/**
 * 禁用开始日期 - 如果已选择结束日期，则禁用大于结束日期的日期
 */
const disableStartDate = (time: Date) => {
  if (formData.endTime) {
    return time.getTime() > formData.endTime
  }
  return false
}

/**
 * 禁用结束日期 - 如果已选择开始日期，则禁用小于开始日期的日期
 */
const disableEndDate = (time: Date) => {
  if (formData.startTime) {
    return time.getTime() < formData.startTime
  }
  return false
}

/**
 * 处理开始时间变化
 */
const handleStartTimeChange = (value: number) => {
  // 如果开始时间大于结束时间，清空结束时间
  if (value && formData.endTime && value > formData.endTime) {
    formData.endTime = undefined
  }
  // 计算出差时长
  calculateDuration()
}

/**
 * 处理结束时间变化
 */
const handleEndTimeChange = (value: number) => {
  // 如果结束时间小于开始时间，清空开始时间
  if (value && formData.startTime && value < formData.startTime) {
    formData.startTime = undefined
  }
  // 计算出差时长
  calculateDuration()
}

/**
 * 计算出差时长
 * 计算逻辑：结束日期 - 开始日期 + 1
 * 例如：18-19号 = 19 - 18 + 1 = 2天
 */
const calculateDuration = () => {
  if (formData.startTime && formData.endTime) {
    const startDate = new Date(formData.startTime)
    const endDate = new Date(formData.endTime)

    // 重置时间为00:00:00，确保只计算日期差异
    startDate.setHours(0, 0, 0, 0)
    endDate.setHours(0, 0, 0, 0)

    // 计算天数差：结束日期 - 开始日期 + 1
    const diffTime = endDate.getTime() - startDate.getTime()
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1
    formData.duration = diffDays
  } else {
    formData.duration = 0
  }
}

const saveForm = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  try {
    let data = cloneDeep(formData)

    const params: any = {
      ...data,
      submitFlag: 0
    }
    let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
    if (feeShareParams) {
      params.auxiliaryType = 1
      params.feeShare = feeShareParams
    } else {
      message.warning('缺少费用归集信息！')
      return false
    }
    loading.value = true
    if (props.mode == 'create') {
      await TravalApplyApi.createTravelApply(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await TravalApplyApi.updateTravelApply(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}
const submitForm = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  try {
    let data = cloneDeep(formData)

    const params: any = {
      ...data,
      invoiceList: isValidArray(data.invoiceList) ? data.invoiceList.map((item) => item.id) : [],
      submitFlag: 1
    }
    let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
    if (feeShareParams) {
      params.auxiliaryType = 1
      params.feeShare = feeShareParams
    } else {
      return false
    }
    if (props.mode == 'create') {
      await TravalApplyApi.createTravelApply(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await TravalApplyApi.updateTravelApply(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (props.mode === 'edit') {
    tabLoading.value = true
    let res = await TravalApplyApi.getTravelApplyInfo({ id: props.id })

    Object.assign(formData, { ...res })
    // 确保费用归集数据被正确传递
    if (res.feeShare && Array.isArray(res.feeShare)) {
      formData.feeShare = res.feeShare
    }
    // 计算出差时长
    calculateDuration()
    tabLoading.value = false
    dataLoaded.value = true // 数据加载完成
  } else {
    formData.applyerId = useUserStore().user.id
    dataLoaded.value = true // 创建模式下直接设置为已加载
  }
})
</script>
<style lang="scss" scoped>
.fee-share-section {
  margin-top: 10px;
  background-color: #fff;
  width: 100%;

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 5px;
    line-height: 40px;
    padding: 0;
  }

  .fee-share-content {
    width: 100%;
    height: 100%;
    padding: 0;
  }

  :deep(.el-form) {
    .el-form-item {
      border-bottom: none;
    }
  }

  :deep(.el-table) {
    border: none;

    .el-table__header-wrapper {
      border-bottom: 1px solid #ebeef5;
    }

    .el-table__body-wrapper {
      border: none;
    }

    .el-table__header {
      th {
        border-bottom: 1px solid #ebeef5;
        border-right: 1px solid #ebeef5;

        &:last-child {
          border-right: none;
        }
      }
    }

    .el-table__body {
      td {
        border-bottom: 1px solid #ebeef5;
        border-right: 1px solid #ebeef5;

        &:last-child {
          border-right: none;
        }
      }
    }
  }

  :deep(.el-table--border) {
    border: none;

    &::after {
      display: none;
    }
  }

  :deep(.el-table--border::after) {
    display: none;
  }

  :deep(.el-table__border-line) {
    display: none;
  }
}
</style>
