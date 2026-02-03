<template>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
</template>
<script setup lang="tsx">
import { SplitKey } from '@/utils/config'
import { isValidArray } from '@/utils/is'
import SkuRateCom from '../../main/components/SkuRateCom.vue'

defineOptions({ name: 'AccessoriesList' })
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
    field: 'auxiliarySkuPicture',
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
        if (!value || !skuRate || !auxiliarySkuRate) {
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
  }
])

const handleAdd = async () => {
  AuxiliaryDialogRef.value.open(tableList)
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
      auxiliarySkuName: item.skuName,
      auxiliarySkuCode: item.skuCode,
      mainPicture: item.mainPicture,
      skuRate: 1,
      auxiliarySkuRate: undefined
    }
  })
}
const TableRef = ref()
const checkData = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    if (selectedList.value?.length > 0) {
      return selectedList.value.map((item) => {
        return {
          ...item,
          skuRate: item.skuRateAndAuxiliarySkuRate?.split(SplitKey)[0],
          auxiliarySkuRate: item.skuRateAndAuxiliarySkuRate?.split(SplitKey)[1]
        }
      })
    } else {
      return []
    }
  } else {
    message.warning('包材配比提交信息有误')
    return false
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  if (isValidArray(props.formData?.skuAuxiliaryList)) {
    tableList.value =
      props.formData.skuAuxiliaryList.map((item) => {
        return {
          ...item,
          skuName: undefined,
          skuCode: undefined,
          skuRateAndAuxiliarySkuRate: `${item.skuRate}${SplitKey}${item.auxiliarySkuRate}`
        }
      }) || []
  } else {
    tableList.value = []
  }
})
</script>
