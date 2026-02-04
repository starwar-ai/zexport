<template>
  <el-dialog
    v-model="dialogTableVisible"
    title="选择供应商报价"
    width="80%"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <div class="mb10px">
        <eplus-search
          :fields="eplusSearchSchema.fields"
          :moreFields="eplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
      </div>

      <el-table
        border
        :data="tableData"
        row-key="code"
        ref="tableRef"
        v-loading="loading"
        @row-click="getRow"
        height="400px"
      >
        <el-table-column
          align="center"
          label=""
          prop="radio"
          show-overflow-tooltip
          fixed="left"
          width="55px"
        >
          <template #default="{ row }">
            <el-radio
              @click="() => handleClick(row)"
              v-model="radioobj.val"
              :label="row?.id"
              >&nbsp;</el-radio
            >
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="供应商名称"
          prop="venderName"
          show-overflow-tooltip
          width="200px"
        />
        <el-table-column
          align="center"
          label="供应商编号"
          prop="venderCode"
          show-overflow-tooltip
          width="200px"
        />
        <el-table-column
          align="center"
          label="单价"
          prop="unitPrice"
          show-overflow-tooltip
          width="130px"
          :formatter="
            (val) => {
              if (val.unitPrice?.amount) {
                return val.unitPrice?.amount + ' ' + val.unitPrice?.currency
              } else {
                return ''
              }
            }
          "
        />
        <el-table-column
          align="center"
          label="税率"
          prop="taxRate"
          show-overflow-tooltip
          :formatter="
            (val) => {
              if (val?.taxRate) {
                return val.taxRate + '%'
              } else {
                return ''
              }
            }
          "
        />
        <el-table-column
          align="center"
          label="是否含包装"
          prop="packageFlag"
          show-overflow-tooltip
          width="120px"
          :formatter="
            (val) => {
              if (val.packageFlag !== null && val.packageFlag !== undefined) {
                return getDictLabel(DICT_TYPE.CONFIRM_TYPE, val?.packageFlag)
              } else {
                return ''
              }
            }
          "
        />
        <el-table-column
          align="center"
          label="是否含运费"
          prop="freightFlag"
          show-overflow-tooltip
          width="120px"
          :formatter="
            (val) => {
              if (val.freightFlag !== null && val.freightFlag !== undefined) {
                return getDictLabel(DICT_TYPE.CONFIRM_TYPE, val?.freightFlag)
              } else {
                return ''
              }
            }
          "
        />
        <el-table-column
          align="center"
          label="付款方式"
          prop="paymentId"
          show-overflow-tooltip
          width="150px"
          :formatter="
            (val) => {
              if (val.paymentId !== null && val.paymentId !== undefined) {
                return paymentListData.paymentList.list.find((item) => {
                  return item.id === val.paymentId
                })?.name
              } else {
                return ''
              }
            }
          "
        />
        <el-table-column
          align="center"
          label="发票类型"
          prop="taxType"
          show-overflow-tooltip
          width="100px"
          :formatter="
            (val) => {
              if (val.taxType !== null && val.taxType !== undefined) {
                return getDictLabel(DICT_TYPE.TAX_TYPE, val?.taxType)
              } else {
                return ''
              }
            }
          "
        />
        <el-table-column
          align="center"
          label="币种"
          prop="currency"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="单箱数量"
          prop="qtyPerInnerbox"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="包装规格"
          prop="package"
          show-overflow-tooltip
          width="200px"
        >
          <template #default="{ row }">
            {{ formatLength(row?.packageLength, row?.packageWidth, row?.packageHeight) }}
          </template></el-table-column
        >
        <el-table-column
          align="center"
          label="外箱规格"
          prop="outerbox"
          show-overflow-tooltip
          width="200px"
        >
          <template #default="{ row }">
            {{ formatLength(row?.outerboxLength, row?.outerboxWidth, row?.outerboxHeight) }}
          </template></el-table-column
        >
      </el-table>
      <!-- 分页 -->
      <!-- <Pagination
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
        layout="total, sizes, prev, pager, next, jumper"
      /> -->
    </div>
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
  </el-dialog>
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
// import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiInput } from '@/components/EplusSearch'
// import { formatDictValue } from '@/utils/table'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { usePaymentStore } from '@/store/modules/payment'
import { isArray } from 'min-dash'

const paymentListData = usePaymentStore() //付款方式表

defineOptions({ name: 'CostDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const loading = ref(false)
const checkId = ref(0)
const allDataRef = ref()
// const pageForm = reactive({
//   pageSize: 10,
//   pageNo: 1,
//   total: 0
// })
let radioobj = reactive({
  val: undefined
})
const formatLength = (length, width, height) => {
  if (length && width && height) {
    return length.toFixed(2) + '*' + width.toFixed(2) + '*' + height.toFixed(2) + ' cm'
  } else {
    return '-'
  }
}
const handleClick = (row: any) => {
  radioobj.val = row?.id
  if (row?.venderId) {
    selectedDiaData.value = [{ venderId: row?.venderId, ...row }]
  }
}
let searchForm = reactive({})

const loopSearchParam = (list, searchParam) => {
  if (isArray(list)) {
    if (list.length === 0) {
      return loopSearchParam([], {})
    } else {
    }
  }
}
const handleSearch = (model) => {
  if (Object.keys(model).length !== 0) {
    if (Object.values(model).every((e) => e === '')) {
      tableData.value = allDataRef.value?.venderLists
    } else {
      let tempList = cloneDeep(allDataRef.value?.venderLists)
      Object.keys(model).map((it) => {
        tempList = tempList.filter((f) => f[it].includes(model[it]))
      })
      tableData.value = tempList
    }
  }
  searchForm = { ...model }
  // pageForm.pageNo = 1
}
const handleReset = () => {
  searchForm = {}
  tableData.value = allDataRef.value?.venderLists
  // pageForm.pageNo = 1
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <EplusSearchMultiInput placeholder="请输入客户产品名称" />,
      subfields: [
        {
          name: 'venderName',
          label: '供应商名称'
        },
        {
          name: 'venderCode',
          label: '供应商编号'
        }
      ]
    }
  ],
  moreFields: []
}

const open = async (allData, dialogType) => {
  console.log(allData, 'allData')
  tableData.value = allData?.venderLists
  allDataRef.value = { dialogType: dialogType, ...allData }
  checkId.value = allData?.id
  searchForm = {}
  // pageForm.pageNo = 1
  dialogTableVisible.value = true
}

const selectedDiaData = ref([])

const getRow = (row) => {
  // let codeList = selectedDiaData.value.map((item) => item.code)
  // let rowIndex = codeList.findIndex((item) => item === row.code)
  // if (codeList.includes(row.code)) {
  //   // selectedDiaData.value.splice(rowIndex, 1)
  // } else {
  //   selectedDiaData.value.push(row)
  //   tableRef.value.toggleRowSelection(row, undefined)
  // }
}
const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}
const emit = defineEmits<{
  sure: [link: string]
}>()
const handleSure = () => {
  let len = selectedDiaData.value?.length
  const normalData = { skuId: allDataRef.value?.skuId, ...selectedDiaData.value[0] }
  const combineData = {
    skuId: allDataRef.value?.id,
    sourceId: allDataRef.value?.sourceId,
    ...selectedDiaData.value[0]
  }
  if (len > 0) {
    emit(
      'sure',
      // @ts-ignore
      [
        checkId,
        allDataRef.value?.dialogType === 'normalDialog' ? normalData : combineData,
        allDataRef.value?.dialogType
      ]
    )
    handleCancel()
  } else {
    message.warning('请选择供应商报价')
  }
}
defineExpose({ open })
onMounted(async () => {
  try {
  } catch {}
})
</script>

<style lang="scss" scoped>
.totalAction {
  cursor: pointer;
  width: 2000px;
}
.totalAction:hover {
  border-color: #ccc;
  box-shadow: 0 0 0 1px #eee;
}
</style>
