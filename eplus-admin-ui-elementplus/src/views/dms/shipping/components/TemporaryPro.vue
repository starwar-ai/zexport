<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加行</el-button
    >
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{ shipmentId: formData.id || undefined }"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { formatterPrice } from '@/utils/index'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import * as HsdataApi from '@/api/system/hsdata'
import { SplitKey, VolumeUnit } from '@/utils/config'
import WeightCom from '@/views/pms/product/main/components/WeightCom.vue'
import { getCustSimpleList } from '@/api/common'

defineOptions({ name: 'TemporaryPro' })
const props = defineProps<{
  formData
  type: string
}>()
const TableRef = ref()
const message = useMessage()
const tableList = ref([])
const codeList = ref([])
const currency = ref('')

const hsdataCodeChange = (val, row) => {
  if (val && val[0] && val[1]) {
    let findItem = val[1].find((item) => item.code === val[0])
    row.declarationUnit = findItem?.unit || ''
  } else {
    row.declarationUnit = ''
  }
}
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'custName',
    label: '客户',
    width: 200,
    component: (
      <eplus-input-search-select
        api={getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="custName"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择"
      />
    )
  },
  {
    field: 'hsCode',
    label: '海关编码',
    width: 200,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-input-search-select
          api={HsdataApi.getHsdataPage}
          params={{ pageSize: 100, pageNo: 1 }}
          keyword="code"
          label="code"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          onChangeEmit={(...$event) => hsdataCodeChange($event, row)}
        />
      )
    }
  },
  {
    field: 'declarationUnit',
    label: '报关单位',
    width: 200,
    component: <el-input disabled />
  },
  {
    field: 'declarationElement',
    label: '报关要素',
    width: 200,
    component: <el-input />
  },
  {
    field: 'declarationName',
    label: '报关品名',
    width: 200,
    component: <el-input />
  },
  {
    field: 'declarationNameEng',
    label: '报关英文名',
    width: 200,
    component: <el-input />
  },
  {
    field: 'expectCount',
    label: '出货数量',
    width: 200,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    )
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    width: 200,
    component: <el-input />
  },
  {
    field: 'declarationCount',
    label: '报关数量',
    width: 200,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    )
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: 200,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    )
  },
  // {
  //   field: 'unitPerOuterbox',
  //   label: '外箱单位',
  //   component: <el-input />,
  //   rules: { required: true, message: '请输入外箱单位' }
  // },
  // {
  //   field: 'pricingMethod',
  //   label: '计价方式',
  //   component: <el-input type="number" />,
  //   rules: { required: true, message: '请输入计价方式' }
  // },
  {
    field: 'declarationTotalPriceStr',
    width: 200,
    label: `报关总价${currency.value}`,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={3}
        class="!w-90%"
      />
    )
  },
  {
    field: 'outerboxVolume',
    width: 200,
    label: `外箱体积${VolumeUnit}`,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={2}
        class="!w-90%"
      />
    )
  },
  {
    field: 'totalVolume',
    width: 200,
    label: `总体积${VolumeUnit}`
  },
  {
    field: 'outerboxGrossweightStr',
    width: 200,
    label: ' 外箱毛重',
    component: <WeightCom />
  },
  {
    field: 'totalGrossweight',
    width: 200,
    label: '总毛重',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.totalGrossweight?.weight
        ? `${row.totalGrossweight.weight} ${row.totalGrossweight.unit}`
        : '-'
    }
  },
  {
    field: 'outerboxNetweightStr',
    width: 200,
    label: '外箱净重',
    component: <WeightCom />
  },
  {
    field: 'totalNetweight',
    width: 200,
    label: '总净重 ',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.totalNetweight?.weight
        ? `${row.totalNetweight.weight} ${row.totalNetweight.unit}`
        : '-'
    }
  },

  {
    field: 'action',
    label: '操作',
    width: '150px',
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
        >
          移除
        </el-button>
      )
    }
  }
])

const handleAdd = async () => {
  var index = tableList.value.length - 1
  const newRecord = {
    sortNum: index,
    outerboxGrossweightStr: `0${SplitKey}kg`,
    outerboxGrossweight: { weight: 0, unit: 'kg' },
    outerboxNetweightStr: `0${SplitKey}kg`,
    outerboxNetweight: { weight: 0, unit: 'kg' },
    totalNetweight: { weight: 0, unit: '' },
    totalGrossweight: { weight: 0, unit: '' },
    shipmentId: props.formData.id || undefined
  }
  tableList.value.push(newRecord)
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.sortNum
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.sortNum)) {
        return item
      }
    })
    tableList.value.forEach((item, index) => {
      return (item.sortNum = index + 1)
    })
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
  tableList.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const checkData = async () => {
  tableList.value.forEach((item: any) => {
    item?.id && delete item?.id
  })
  if (tableList.value.length) {
    let valid = await TableRef.value.validate()
    if (valid) {
      let tableData = cloneDeep(TableRef.value.tableData)
      let arr = tableData.map((item, index) => {
        return {
          ...item,
          declarationTotalPrice: item.declarationTotalPriceStr
            ? {
                amount: formatterPrice(item.declarationTotalPriceStr),
                currency: currency.value
              }
            : { amount: 0, currency: currency.value }
        }
      })
      return arr
    } else {
      message.warning('临时产品提交有误')
      return false
    }
  } else {
    return []
  }
}
const emit = defineEmits(['getCabinet'])
watch(
  () => tableList.value,
  (list) => {
    let listTotalBoxes = 0,
      listTotalVolume = 0,
      listTotalGrossweight = 0,
      listTotalWeight = 0
    list?.forEach((item: any) => {
      // 总体积
      item.totalVolume = (item.boxCount || 0) * (item.outerboxVolume || 0)
      // 总净重
      let [w1, u1] = item.outerboxNetweightStr.split(SplitKey)
      item.outerboxNetweight.weight = Number(w1)
      item.outerboxNetweight.unit = u1
      item.totalNetweight.weight = Number(w1) * (Number(item.boxCount) || 0)
      item.totalNetweight.unit = u1
      if (u1 === 'kg') {
        listTotalWeight += item.totalNetweight.weight
      } else {
        listTotalWeight += item.totalNetweight.weight / 1000
      }
      //总毛重
      let [w2, u2] = item.outerboxGrossweightStr.split(SplitKey)
      item.outerboxGrossweight.weight = Number(w2)
      item.outerboxGrossweight.unit = u2
      item.totalGrossweight.weight = Number(w2) * (Number(item.boxCount) || 0)
      item.totalGrossweight.unit = u2
      if (u2 === 'kg') {
        listTotalGrossweight += item.totalGrossweight.weight
      } else {
        listTotalGrossweight += item.totalGrossweight.weight / 1000
      }

      //总箱数
      listTotalBoxes += item.boxCount || 0
      // 总体积
      listTotalVolume += item.totalVolume * 1000000 || 0
    })
    // emit('getCabinet', {
    //   listTotalBoxes,
    //   listTotalVolume,
    //   listTotalGrossweight,
    //   listTotalWeight
    // })
  },
  { immediate: true, deep: true }
)

const init = () => {
  currency.value = props.formData?.children[0]?.currency
  if (isValidArray(props.formData.temporarySkuList)) {
    let tableData = cloneDeep(props.formData.temporarySkuList)
    tableList.value = tableData.map((item, index) => {
      return {
        ...item,
        declarationTotalPriceStr: item.declarationTotalPrice?.amount,
        outerboxGrossweightStr: `${item.outerboxGrossweight?.weight}${SplitKey}${item.outerboxGrossweight?.unit}`,
        outerboxNetweightStr: `${item.outerboxNetweight?.weight}${SplitKey}${item.outerboxNetweight?.unit}`
      }
    })
  } else {
    tableList.value = []
  }
}
defineExpose({ selectedList, tableList, checkData, init })

onMounted(async () => {})
</script>
