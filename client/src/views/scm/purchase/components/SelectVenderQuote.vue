<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择供应商报价"
    width="80%"
    append-to-body
    destroy-on-close
  >
    <el-tabs
      v-if="props.channel !== 'exportSale'"
      v-model="activeName"
      @tab-click="handleTabClick"
    >
      <el-tab-pane
        label="已有报价"
        name="have"
      />
      <el-tab-pane
        label="新增报价"
        name="add"
      />
    </el-tabs>
    <div
      class="pb50px"
      v-if="activeName === 'have'"
    >
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
          prop="withTaxPrice"
          show-overflow-tooltip
          width="130px"
        >
          <template #default="{ row }">
            <EplusMoneyLabel :val="row.withTaxPrice" />
          </template>
        </el-table-column>
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
        <!-- <el-table-column
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
        /> -->
        <!-- <el-table-column
          align="center"
          label="币种"
          prop="currency"
          show-overflow-tooltip
        /> -->
        <el-table-column
          align="center"
          label="内盒装量"
          prop="qtyPerInnerbox"
        />
        <el-table-column
          align="center"
          label="外箱装量"
          prop="qtyPerOuterbox"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="外箱规格"
          prop="outerbox"
          show-overflow-tooltip
          width="200px"
        >
          <template #default="{ row }">
            {{ getOuterbox(row, 'spec') }}
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-if="activeName === 'add'">
      <CreateQuete
        ref="CreateQueteRef"
        :defaultData="defaultData"
      />
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
  </Dialog>
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
// import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiInput } from '@/components/EplusSearch'
// import { formatDictValue } from '@/utils/table'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import CreateQuete from './CreateQuete.vue'
import { createQuote, getVenderQuoteList } from '@/api/scm/purchasePlan'
import { getOuterbox } from '@/utils/outboxSpec'

defineOptions({ name: 'SelectVenderQuote' })
const props = defineProps<{
  channel?: string
}>()
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
const loading = ref(false)
const allDataRef = ref()
const CreateQueteRef = ref()

const activeName = ref('have')
let radioobj = reactive({
  val: undefined
})
let searchForm = reactive({})

const handleTabClick = () => {
  // radioobj.val = row?.id
}

const handleClick = (row: any) => {
  radioobj.val = row?.id
  if (row?.venderId) {
    selectedDiaData.value = [{ venderId: row?.venderId, ...row }]
  } else {
    return false
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
      component: <EplusSearchMultiInput />,
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

const tableIndex = ref(0)
const rowData = ref()
let defaultData = ref()
const open = async (row, index) => {
  rowData.value = row
  tableData.value = await getVenderQuoteList(row.skuId)
  defaultData.value = tableData.value.find((item: any) => item.defaultFlag === 1)
  tableIndex.value = index
  searchForm = {}
  activeName.value = 'have'
  dialogTableVisible.value = true
}

const selectedDiaData = ref<any>([])

const getRow = (row) => {
  handleClick(row)
}
const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}
const emit = defineEmits<{
  sure
}>()
const handleSure = () => {
  if (activeName.value === 'have') {
    let len = selectedDiaData.value?.length
    if (len > 0) {
      emit('sure', {
        data: selectedDiaData.value[0],
        index: tableIndex.value,
        row: rowData.value
      })
      handleCancel()
    } else {
      message.warning('请选择供应商报价')
    }
  } else {
    handleCreateSure()
  }
}
const handleCreateSure = async () => {
  let valid = await CreateQueteRef.value.saveForm()
  if (valid) {
    let params = await CreateQueteRef.value.getParams()
    await createQuote({
      ...params,
      skuCode: rowData.value.skuCode,
      skuId: rowData.value.skuId
    })
    emit('sure', {
      data: params,
      index: tableIndex.value,
      row: rowData.value
    })
    handleCancel()
  } else {
    message.warning('请完善报价信息')
  }
}

defineExpose({ open })
onMounted(async () => {})
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
