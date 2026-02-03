<template>
  <div
    class="mb15px"
    v-if="!props.readonly"
  >
    <el-button
      type="primary"
      @click="handleAdd"
      >选择产品</el-button
    >
  </div>
  <el-table
    :data="tableList"
    :span-method="objectSpanMethod"
    border
    style="width: 100%"
  >
    <el-table-column
      type="index"
      label="序号"
      width="80"
      align="center"
    />
    <el-table-column
      prop="skuName"
      label="图片"
      align="center"
    >
      <template #default="{ row }">
        <EplusImgEnlarge :mainPicture="row?.mainPicture" />
      </template>
    </el-table-column>
    <el-table-column
      prop="skuName"
      label="产品名称"
      align="center"
    />
    <el-table-column
      prop="quantity"
      label="加工量"
      align="center"
    >
      <template #default="scope">
        <el-input-number
          v-if="!props.readonly"
          style="width: 100%"
          v-model="scope.row.quantity"
          :min="1"
          :precision="0"
          :controls="false"
          @change="(...$event) => numChange($event, scope)"
        />
        <span v-else>{{ scope.row.quantity }}</span>
      </template>
    </el-table-column>
    <el-table-column
      prop="son_skuName"
      label="子产品名称"
      align="center"
    />
    <el-table-column
      prop="son_ratio"
      label="数量配比"
      align="center"
    />
    <el-table-column
      prop="son_quantity"
      label="子产品加工量"
      align="center"
    >
      <template #default="{ row }">
        <LockCom :row="row" />
      </template>
    </el-table-column>
    <el-table-column
      label="操作"
      align="center"
      v-if="!props.readonly"
    >
      <template #default="scope">
        <el-button
          type="success"
          link
          @click="delRow(scope)"
          >移除</el-button
        >
      </template>
    </el-table-column>
  </el-table>
  <!-- <el-button @click="checkData">保存</el-button> -->
  <ProductDialog
    ref="ProductDialogRef"
    @sure="handleSure"
    :selectionFlag="true"
    :defaultVal="{
      skuType: 2
    }"
    :schema="productColumns"
    :isShowTabs="true"
  />
</template>
<script setup lang="tsx">
import { ProductDialog } from '@/components/ProductDialog'
import { TableColumn } from '@/types/table'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import LockCom from './LockCom.vue'

defineOptions({ name: 'SkuList' })
const props = defineProps<{
  formData?: any
  readonly?: boolean
}>()
const message = useMessage()
const ProductDialogRef = ref()
const tableList = ref([])

const objectSpanMethod = ({ row, column, rowIndex, columnIndex }) => {
  if (columnIndex < 4) {
    if (row.span) {
      return {
        rowspan: row.span,
        colspan: 1
      }
    } else {
      return {
        rowspan: 0,
        colspan: 0
      }
    }
  } else if (columnIndex == 7) {
    if (row.span) {
      return {
        rowspan: row.span,
        colspan: 1
      }
    } else {
      return {
        rowspan: 0,
        colspan: 0
      }
    }
  }
}

const numChange = (val, scope) => {
  for (let i = 0; i < scope.row.span; i++) {
    let item = tableList.value[scope.$index + i]
    item.son_quantity = val[0] * item.son_ratio
  }
}

let productColumns = reactive<TableColumn[]>([
  {
    field: 'mainPicture',
    label: '图片',
    width: '150px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.thumbnail}
        />
      )
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '150px'
  },
  {
    field: 'code',
    label: '产品编码',
    width: '150px'
  },
  {
    field: 'skuName',
    label: '中文名称',
    width: '150px'
  },
  {
    field: 'nameEng',
    label: '英文名称',
    width: '150px'
  },
  {
    field: 'hsCode',
    label: '海关编码',
    width: '150px'
  },
  {
    field: 'taxRefundRate',
    label: '退税率',
    width: '150px'
  }
])

const handleAdd = async () => {
  if (props.formData?.custId && props.formData?.custCode) {
    ProductDialogRef.value.open([], props.formData?.custCode)
  } else {
    message.error('未选择客户，不可选择产品信息！')
  }
}

const delRow = (scope) => {
  tableList.value.splice(scope.$index, scope.row.span)
}

const handleSure = (list) => {
  tableList.value = []
  list.forEach((item) => {
    item.sonSkuList.forEach((sonItem, n) => {
      tableList.value.push({
        skuId: item.id,
        skuCode: item.code,
        cskuCode: item.cskuCode,
        skuName: item.skuName,
        quantity: item.quantity,
        mainPicture: item.mainPicture,
        son_skuId: sonItem.id,
        son_skuCode: sonItem.code,
        son_cskuCode: sonItem.cskuCode,
        son_skuName: sonItem.skuName,
        son_quantity: sonItem.quantity || sonItem.sonSkuCount,
        son_mainPicture: sonItem.mainPicture,
        son_ratio: sonItem.sonSkuCount,
        span: n > 0 ? undefined : item.sonSkuList.length
      })
    })
  })
}

const checkData = async () => {
  let list = cloneDeep(tableList.value),
    arr: any = []
  list.forEach((item, index) => {
    if (item.span) {
      arr.push({
        skuId: item.skuId,
        skuCode: item.skuCode,
        cskuCode: item.cskuCode,
        skuName: item.skuName,
        quantity: item.quantity,
        mainPicture: item.mainPicture,
        skuItemList: getSonList(list, index, item.span)
      })
    }
  })
  console.log(arr)
  return arr
}

const getSonList = (list, index, span) => {
  let arr: any = []
  for (let i = 0; i < span; i++) {
    arr.push({
      skuId: list[index + i].son_skuId,
      skuCode: list[index + i].son_skuCode,
      cskuCode: list[index + i].son_cskuCode,
      skuName: list[index + i].son_skuName,
      quantity: list[index + i].son_quantity,
      mainPicture: list[index + i].son_mainPicture,
      ratio: list[index + i].son_ratio,
      id: list[index + i].son_id || undefined,
      manufactureId: list[index + i].son_manufactureId || undefined
    })
  }
  return arr
}

defineExpose({ checkData })

watchEffect(() => {
  if (isValidArray(props.formData?.children)) {
    let list = props.formData?.children
    tableList.value = []
    list.forEach((item) => {
      item.skuItemList.forEach((sonItem, n) => {
        tableList.value.push({
          ...item,
          son_skuId: sonItem.skuId,
          son_skuCode: sonItem.skuCode,
          son_cskuCode: sonItem.cskuCode,
          son_skuName: sonItem.skuName,
          son_quantity: sonItem.quantity || sonItem.ratio,
          son_mainPicture: sonItem.mainPicture,
          son_ratio: sonItem.ratio,
          son_id: sonItem.id,
          son_manufactureId: sonItem.manufactureId,
          span: n > 0 ? undefined : item.skuItemList.length,
          son_stockList: sonItem.stockList || []
        })
      })
    })
  }
})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: #000;
}

.total-header {
  width: 100%;
}
</style>
