<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :showIndexPlus="props.formData.splitBoxFlag == 1"
    :defaultVal="{}"
    :schema="tableColumns"
  />
  <!-- <el-button @click="checkData">保存</el-button> -->
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { cloneDeep } from 'lodash-es'
// import { getPurchaseContractSimpleList } from '@/api/scm/purchaseContract'
import { isValidArray } from '@/utils/is'
import {
  LengthUnit,
  lenPrecision,
  SplitKey,
  volPrecision,
  VolumeUnit,
  weightPrecision,
  weightUnit
} from '@/utils/config'
import EplusNumInput from '@/components/EplusMoney/src/EplusNumInput.vue'
import SpecCom from './SpecCom.vue'
import { formatterPrice } from '@/utils'

defineOptions({ name: 'OuterboxSpec' })
const props = defineProps<{
  formData
}>()
const TableRef = ref()
const message = useMessage()
const tableList = ref([])

let tableColumns = reactive<TableColumn[]>([
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 50
  },
  {
    field: 'outerboxSpec',
    label: `外箱规格${LengthUnit}`,
    component: <SpecCom />,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value) {
          callback(new Error('请输入外箱规格'))
        } else {
          let [l, w, h] = value.split(SplitKey)
          if (l <= 0) {
            callback(new Error('请输入外箱规格长'))
          } else if (w <= 0) {
            callback(new Error('请输入外箱规格宽'))
          } else if (h <= 0) {
            callback(new Error('请输入外箱规格高'))
          } else {
            callback()
          }
        }
      }
    }
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积${VolumeUnit}`,
    minWidth: 280,
    formatter: (item, row: Recordable, index: number) => {
      if (row.outerboxSpec) {
        let [l, w, h] = row.outerboxSpec.split(SplitKey)
        if (l > 0 && w > 0 && h > 0) {
          row.outerboxLength = formatterPrice(l, lenPrecision)
          row.outerboxWidth = formatterPrice(w, lenPrecision)
          row.outerboxHeight = formatterPrice(h, lenPrecision)
          row.outerboxVolume = formatterPrice(l * w * h, volPrecision)
          return formatterPrice((l * w * h) / 1000000, volPrecision)
        } else {
          return 0
        }
      } else {
        return 0
      }
    }
  },
  {
    field: 'outerboxGrossweightVal',
    label: `外箱毛重${weightUnit}`,
    minWidth: 150,
    component: (
      <EplusNumInput
        precision={weightPrecision}
        min={0}
      />
    ),
    rules: {
      required: true,
      message: '请输入外箱毛重'
    }
  },
  {
    field: 'outerboxNetweightVal',
    label: `外箱净重${weightUnit}`,
    minWidth: 150,
    component: (
      <EplusNumInput
        precision={weightPrecision}
        min={0}
      />
    ),
    rules: {
      required: true,
      message: '请输入外箱净重'
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '80px',
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            await delRow(index)
          }}
          disabled={tableList.value.length === 1}
        >
          移除
        </el-button>
      )
    }
  }
])

const delRow = (index) => {
  tableList.value.splice(index, 1)
}

const checkData = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    let list = cloneDeep(tableList.value)
    for (let i = 0; i < list.length; i++) {
      const el: any = list[i]
      if (el.outerboxNetweightVal > el.outerboxGrossweightVal) {
        message.warning('外箱净重需小于等于外箱毛重')
        return false
      }
    }

    return list.map((item: any) => {
      return {
        ...item,
        outerboxGrossweight: {
          weight: item.outerboxGrossweightVal,
          unit: weightUnit
        },
        outerboxNetweight: {
          weight: item.outerboxNetweightVal,
          unit: weightUnit
        },
        outerboxUnit: LengthUnit
      }
    })
  } else {
    return false
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  if (isValidArray(props.formData.specificationList)) {
    let tableData = cloneDeep(props.formData.specificationList)
    tableList.value = tableData.map((item, index) => {
      return {
        ...item,
        outerboxSpec: `${item.outerboxLength}${SplitKey}${item.outerboxWidth}${SplitKey}${item.outerboxHeight}`,
        outerboxGrossweightVal: item.outerboxGrossweight?.weight,
        outerboxNetweightVal: item.outerboxNetweight?.weight
      }
    })
  } else {
    tableList.value = []
  }
})
onMounted(async () => {})
</script>
<style lang="scss" scoped></style>
