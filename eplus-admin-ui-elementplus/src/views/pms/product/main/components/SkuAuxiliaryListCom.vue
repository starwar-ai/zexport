<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >关联包材</el-button
    >
    <el-button @click="handleRemove">移除包材</el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />

  <AuxiliaryDialog
    ref="AuxiliaryDialogRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import AuxiliaryDialog from './AuxiliaryDialog.vue'
import SkuRateCom from './SkuRateCom.vue'
import { SplitKey } from '@/utils/config'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'SkuAuxiliaryListCom' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const AuxiliaryDialogRef = ref()
const tableList = ref([])

let tableColumns = reactive([
  {
    field: 'auxiliarySkuName',
    label: '包材名称',
    minWidth: '100px'
  },
  {
    label: '包材图片',
    minWidth: '100px',
    field: 'picture',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (row.auxiliarySkuPicture?.id) {
        return <EplusImgEnlarge mainPicture={row.auxiliarySkuPicture} />
      } else {
        return '-'
      }
    }
  },
  {
    field: 'skuRateAndAuxiliarySkuRate',
    label: '包材比例',
    width: '200px',
    component: <SkuRateCom />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let [skuRate, auxiliarySkuRate] = value ? value.split(SplitKey) : []
        if (!value || !skuRate || auxiliarySkuRate === 'undefined') {
          callback(new Error(`请输入包材比例`))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'description',
    label: '规格描述',
    component: (
      <el-input
        autosize
        type="textarea"
        class="!w-90%"
      />
    ),
    minWidth: '200px'
  },
  {
    field: 'remark',
    label: '备注',
    component: (
      <el-input
        autosize
        type="textarea"
        class="!w-90%"
      />
    ),
    minWidth: '200px'
  },
  {
    label: '操作',
    width: 100,
    fixed: 'right',
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => delRow(index)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
])

const handleAdd = async () => {
  AuxiliaryDialogRef.value.open(
    tableList.value.map((item) => {
      return {
        ...item,
        code: item.auxiliarySkuCode
      }
    })
  )
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.code
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.code)) {
        return item
      }
    })
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  tableList.value = list.map((item) => {
    return {
      auxiliarySkuName: item.auxiliarySkuName || item.skuName,
      auxiliarySkuCode: item.auxiliarySkuCode || item.skuCode,
      auxiliarySkuPicture: item.auxiliarySkuPicture || item.mainPicture,
      skuRate: item.skuRate || 1,
      auxiliarySkuRate: item.auxiliarySkuRate || undefined,
      skuRateAndAuxiliarySkuRate:
        item.skuRate && item.auxiliarySkuRate
          ? `${item.skuRate}${SplitKey}${item.auxiliarySkuRate}`
          : `1${SplitKey}undefined`,
      description: item.description || undefined,
      remark: item.remark || undefined
    }
  })
}
const TableRef = ref()
const checkData = async () => {
  if (tableList.value?.length > 0) {
    let valid = await TableRef.value.validate()
    if (valid) {
      return TableRef.value.tableData.map((item) => {
        return {
          ...item,
          skuRate: item.skuRateAndAuxiliarySkuRate?.split(SplitKey)[0],
          auxiliarySkuRate: item.skuRateAndAuxiliarySkuRate?.split(SplitKey)[1]
        }
      })
    } else {
      message.warning('包材配比提交信息有误')
      return false
    }
  } else {
    return []
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  if (isValidArray(props.formData?.skuAuxiliaryList)) {
    tableList.value =
      props.formData.skuAuxiliaryList.map((item) => {
        return {
          ...item,
          skuRateAndAuxiliarySkuRate: `${item.skuRate}${SplitKey}${item.auxiliarySkuRate}`
        }
      }) || []
  } else {
    tableList.value = []
  }
})
</script>
