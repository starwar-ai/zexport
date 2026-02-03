<template>
  <Dialog
    v-model="dialogTableVisible"
    title="下推包材采购计划"
    width="1100"
    append-to-body
    destroy-on-close
  >
    <eplus-form-table
      ref="TableRef"
      :list="tableList"
      :schema="tableSchema"
      :defaultVal="{}"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { auxiliaryList, toAuxiliary } from '@/api/scm/purchaseContract'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'

const message = useMessage()
defineOptions({ name: 'ToAuxiliaryPlan' })

const tableSchema: EplusFormTableSchema[] = [
  {
    label: '包材使用方式',
    field: 'auxiliarySkuType',
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.AUXILIARY_PURCHASE_TYPE, row.auxiliarySkuType)
    },
    width: 100
  },
  {
    label: '采购合同',
    field: 'purchaseContractCode',
    width: 100
  },
  {
    label: '产品名称',
    field: 'skuName',
    width: 120
  },
  {
    label: '客户货号',
    field: 'cskuCode',
    width: 120
  },
  {
    label: '包材名称',
    field: 'auxiliarySkuName',
    width: 120
  },
  {
    label: '包材计量单位',
    field: 'auxiliarySkuUnit',
    width: 120,
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.MEASURE_UNIT, row.auxiliarySkuUnit)
    }
  },
  {
    label: '图片',
    field: 'auxiliarySkuPicture',
    width: 60,
    formatter: (item, row: Recordable, index: number) => {
      return <EplusImgEnlarge mainPicture={row?.auxiliarySkuPicture} />
    }
  },
  {
    label: '产品采购数量',
    field: 'skuQuantity',
    width: 100
  },
  {
    label: '包材采购数量',
    field: 'auxiliaryQuantity',
    width: 100,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: {
      required: true,
      message: '请输入包材采购数量'
    }
  },
  {
    label: '规格描述',
    field: 'auxiliaryDescription',
    width: 120,
    component: (
      <el-input
        autosize
        type="textarea"
      />
    ),
    rules: {
      required: true,
      message: '请输入规格描述'
    }
  }
]

const tableList = ref<any[]>([])
const open = async (ids) => {
  tableList.value = await auxiliaryList({ ids: ids })
  dialogTableVisible.value = true
}

const dialogTableVisible = ref(false)
const TableRef = ref()
const emit = defineEmits(['success'])
const handleSure = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    var res = await toAuxiliary(tableList.value)
    // message.success('操作成功')
    message.notifyPushSuccess('转包材采购计划成功', res, 'purchase-auxiliary-plan')
    dialogTableVisible.value = false
    emit('success')
  } else {
    message.warning('表单信息填写信息不完整!')
  }
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
