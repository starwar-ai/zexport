<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'oa:entertain-apply:create',
      handler: () => saveForm()
    }"
    :submitAction="{
      permi: 'oa:entertain-apply:submit',
      handler: () => submitForm()
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="explainSchemas"
    >
      <template #amount>
        <EplusMoney
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
        />
      </template>
      <template #referenceStandard>
        <el-row
          :gutter="20"
          style="font-size: 14px; width: 100%"
        >
          <el-col :span="8">
            <el-card>
              <template #header> 餐饮费 </template>
              <div>
                ①普通客户：食堂便餐或简餐150元/人以内 <br />
                ②重要客户：200元/人以内或2000元/桌以内 <br />
                ③特级客户：400元/人以内或4000元/桌以内
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <template #header> 水果食品费 </template>
              ①普通客户：50元以内 <br />
              ②重要客户：200元以内 <br />
              ③特级客户：300元以内
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <template #header> 礼品费 </template>
              ①普通客户：人均300元以内 <br />
              ②重要客户：人均500元以内 <br />
              ③特级客户：人均1000元以内
            </el-card>
          </el-col>
        </el-row>
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
import { DICT_TYPE, getDictValue } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import * as EntertainApplyApi from '@/api/oa/entertainApply'
import { useUserStore } from '@/store/modules/user'
import FeeShareOrderFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'

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
  entertainTime: '',
  entertainType: undefined,
  entertainName: '',
  entertainLevel: '',
  entertainNum: '',
  applyerId: undefined,
  amount: {
    amount: 0,
    currency: 'RMB'
  },
  entertainEntourageIds: [],
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

const getEntertainEntourageList = (val: any) => {
  formData.entertainEntourage = val
}
const disabledDate = (time: Date) => {
  return time.getTime() > Date.now()
}
const getItem = async (val, list) => {
  if (val && list) {
    feeShareobj.item = list.find((item) => item.name === val)
  } else {
    let params = {
      pageSize: 100,
      pageNo: 1,
      name: formData?.entertainName
    }
    let res =
      formData?.entertainType === 1
        ? await TravelReimbApi.getCustSimpleList(params)
        : await TravelReimbApi.getVenderSimpleList(params)
    feeShareobj.item = {}
  }
}
const custDisabled = ref(true)
const venderDisabled = ref(true)
const explainSchemas: EplusFormSchema[] = reactive([
  {
    field: 'entertainTime',
    label: '招待日期',
    component: (
      <el-date-picker
        class="!w-100%"
        type="date"
        placeholder="请选择"
        value-format="x"
      />
    ),
    rules: {
      required: true,
      message: '请选择招聘日期'
    }
  },
  {
    field: 'entertainType',
    label: '招待对象类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.ENTWETAIN_TYPE}
        class="!w-100%"
        clearable={false}
        onChange={(val) => {
          formData.entertainName = ''
          custDisabled.value = val != getDictValue(DICT_TYPE.ENTWETAIN_TYPE, '客户')
          venderDisabled.value = val != getDictValue(DICT_TYPE.ENTWETAIN_TYPE, '供应商')
        }}
      />
    ),
    rules: {
      required: true,
      message: '请选择招待对象类型'
    }
  },
  {
    field: 'entertainName',
    label: `客户名称`,
    disabled: custDisabled,
    component: (
      <eplus-input-search-select
        api={TravelReimbApi.getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="name"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={getItem}
      />
    ),
    rules: {
      required: true,
      message: `请选择客户名称`
    }
  },
  {
    field: 'entertainName',
    label: `供应商名称`,
    disabled: venderDisabled,
    component: (
      <eplus-input-search-select
        api={TravelReimbApi.getVenderSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="name"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={getItem}
      />
    ),
    rules: {
      required: true,
      message: `请选择供应商名称`
    }
  },
  {
    field: 'entertainLevel',
    label: `招待对象等级`,
    component: (
      <eplus-dict-select
        class="!w-100%"
        dictType={DICT_TYPE.LEVEL_TYPE}
        clearable={false}
      />
    ),
    rules: {
      required: true,
      message: `请选择等级`
    }
  },
  {
    field: 'entertainNum',
    label: '招待人数(人)',
    component: (
      <el-input-number
        class="!w-100%"
        min={1}
        step={1}
      />
    ),
    rules: {
      required: true,
      message: '请输入招待人数'
    }
  },
  {
    field: 'entertainEntourageIds',
    label: '我司陪同人',
    component: (
      <eplus-user-select
        multiple={true}
        class="!w-100%"
        onChange={getEntertainEntourageList}
      ></eplus-user-select>
    ),
    rules: {
      required: true,
      message: '请选择陪同人'
    }
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
    label: '招待事由',
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
      message: '请输入招待事由'
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
  },
  {
    field: 'referenceStandard',
    label: '参考标准',
    span: 24
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
      return false
    }
    loading.value = true
    if (props.mode == 'create') {
      await EntertainApplyApi.createEntertainApply(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await EntertainApplyApi.updateEntertainApply(params)
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
      await EntertainApplyApi.createEntertainApply(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await EntertainApplyApi.updateEntertainApply(params)
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
    let res = await EntertainApplyApi.getEntertainApplyInfo({ id: props.id })
    feeShareobj.type = res.entertainType
    Object.assign(formData, { ...res })
    formData.entertainEntourageIds = formData.entertainEntourage.map((item) => item.userId)
    if (res.entertainType) {
      custDisabled.value = res.entertainType != getDictValue(DICT_TYPE.ENTWETAIN_TYPE, '客户')
      venderDisabled.value = res.entertainType != getDictValue(DICT_TYPE.ENTWETAIN_TYPE, '供应商')
    }
    // 确保费用归集数据被正确传递
    if (res.feeShare && Array.isArray(res.feeShare)) {
      formData.feeShare = res.feeShare
    }
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

// 参考标准样式
.reference-standard-container {
  display: flex;
  align-items: stretch;
  border: 1px solid #f56c6c;
  border-radius: 4px;
  overflow: hidden;

  .reference-standard-label {
    width: 60px;
    background-color: #fff;
    border-right: 1px solid #f56c6c;
    display: flex;
    align-items: center;
    justify-content: center;
    writing-mode: vertical-rl;
    text-orientation: mixed;
    font-size: 14px;
    color: #303133;
    font-weight: 500;
    flex-shrink: 0;
  }

  .reference-standard-content {
    flex: 1;
    display: flex;
    align-items: stretch;

    .fee-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      border-right: 1px solid #f56c6c;

      &:last-child {
        border-right: none;
      }

      .fee-title {
        height: 40px;
        background-color: #fff;
        border-bottom: 1px solid #f56c6c;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #303133;
        font-weight: 500;
        flex-shrink: 0;
      }

      .fee-input {
        flex: 1;
        padding: 10px;
        background-color: #fff;
        min-height: 80px;
        display: flex;
        align-items: center;

        :deep(.el-input-number) {
          width: 100%;
        }
      }
    }
  }
}

// 参考标准表格样式 - 一行显示
.reference-standard-table {
  width: 100%;
  border: 1px solid #f56c6c;
  border-radius: 4px;
  overflow: hidden;
  background-color: #fff;

  .reference-standard-header {
    display: flex;
    align-items: stretch;

    .reference-label {
      width: 60px;
      background-color: #fff;
      border-right: 1px solid #f56c6c;
      display: flex;
      align-items: center;
      justify-content: center;
      writing-mode: vertical-rl;
      text-orientation: mixed;
      font-size: 14px;
      color: #303133;
      font-weight: 500;
      flex-shrink: 0;
      min-height: 120px;
    }

    .fee-columns {
      flex: 1;
      display: flex;
      align-items: stretch;

      .fee-column {
        flex: 1;
        display: flex;
        flex-direction: column;
        border-right: 1px solid #f56c6c;

        &:last-child {
          border-right: none;
        }

        .fee-title {
          height: 40px;
          background-color: #fff;
          border-bottom: 1px solid #f56c6c;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          color: #303133;
          font-weight: 500;
          flex-shrink: 0;
        }

        .fee-content {
          flex: 1;
          padding: 8px;
          background-color: #fff;
          min-height: 80px;

          .standard-item {
            display: flex;
            align-items: flex-start;
            margin-bottom: 6px;
            font-size: 12px;
            line-height: 1.4;

            &:last-child {
              margin-bottom: 0;
            }

            .standard-number {
              color: #409eff;
              font-weight: 600;
              margin-right: 4px;
              flex-shrink: 0;
            }

            .standard-text {
              color: #606266;
              flex: 1;
            }
          }
        }
      }
    }
  }
}
</style>
