<template>
  <Dialog
    v-model="dialogTableVisible"
    title="添加采购产品"
    width="1000"
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
        @select="handleSelectionChange"
        @select-all="handleSelectionAllChange"
        row-key="code"
        ref="tableRef"
        v-loading="loading"
        @row-click="getRow"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
          fixed="left"
        />
        <el-table-column
          align="center"
          label="产品编号"
          prop="code"
          show-overflow-tooltip
          fixed="left"
        />
        <el-table-column
          align="center"
          label="图片"
          prop="mainPicture"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <EplusImgEnlarge
              :mainPicture="row?.mainPicture"
              :thumbnail="row?.thumbnail"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="产品名称"
          prop="skuName"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row?.skuName }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="客户货号"
          prop="cskuCode"
          show-overflow-tooltip
          fixed="left"
        />
        <el-table-column
          align="center"
          label="产品材质"
          prop="material"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="分类"
          prop="categoryName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="品牌"
          prop="brandName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="状态"
          prop="skuType"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType) }}
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
        layout="total, sizes, prev, pager, next, jumper"
      />
      <!-- </div> -->
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
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { cloneDeep } from 'lodash-es'
// import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusSearchMultiInput } from '@/components/EplusSearch'
// import { formatDictValue } from '@/utils/table'

defineOptions({ name: 'CostDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const loading = ref(false)
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
const venderCodeRef = ref('')
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: (
        <eplus-tree-select
          checkStrictly={false}
          placeholder={'产品分类'}
        />
      ),
      name: 'categoryId',
      label: '产品分类'
      // formatter: (args: any[]) => {
      //   return getDictLabel(DICT_TYPE.CUSTOM_TYPE, args[0])
      // }
    },
    {
      component: <EplusSearchMultiInput placeholder="请输入产品名称" />,
      subfields: [
        {
          name: 'skuName',
          label: '产品名称'
        }
      ]
    }
  ],
  moreFields: []
}
const handleSearch = (model) => {
  searchForm = { venderCode: venderCodeRef.value, ...model }
  pageForm.pageNo = 1
  getList()
}
const handleReset = () => {
  searchForm = { venderCode: venderCodeRef.value }
  pageForm.pageNo = 1
  getList()
}

const emit = defineEmits<{
  sure: [link: string]
}>()

const getList = async () => {
  loading.value = true
  try {
    let res = await PurchasePlanApi.getSkuSimpleList({
      pageSize: pageForm.pageSize,
      custProFlag: 1,
      pageNo: pageForm.pageNo,
      ...searchForm
    })
    tableData.value = res.list || []
    // tableData.value.categoryList = categoryList || []
    pageForm.total = res.total || 0
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  } finally {
    loading.value = false
  }
}

const open = async (list, venderCode = '') => {
  parentList.value = cloneDeep(list.value)
  selectedDiaData.value = cloneDeep(list.value)
  venderCodeRef.value = venderCode
  searchForm = { venderCode: venderCode }
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item) => {
      parentList.value.forEach((el) => {
        if (item.cskuCode == el.cskuCode) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}
// toggleRowSelection

const selectedDiaData = ref([])
const handleSelectionChange = (selection, row) => {
  if (parentList.value.length > 0) {
    let codeList = selectedDiaData.value.map((item) => item.code)
    let rowIndex = codeList.findIndex((item) => item === row.code)
    if (codeList.includes(row.code)) {
      selectedDiaData.value.splice(rowIndex, 1)
    } else {
      selectedDiaData.value.push(row)
    }
  } else {
    selectedDiaData.value = selection
  }
}

const handleSelectionAllChange = (selection) => {
  selection.forEach((item) => {
    handleSelectionChange(selection, item)
  })
}

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
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  if (len > 0) {
    emit(
      'sure',
      // @ts-ignore
      selectedDiaData.value.map((item: any, index) => {
        if (!item?.withTotalTaxPrice) {
          let defaultQuoteitemList = item?.quoteitemList?.filter(
            (item) => item.defaultFlag === 1
          )[0]
          let venderList = []
          item?.quoteitemList?.map((item) => {
            venderList.push({
              venderCode: item?.venderCode,
              venderName: item?.venderName
            })
          })
          return {
            ...item,
            sortNum: index + 1,
            skuCode: item?.code,
            unitPrice: defaultQuoteitemList?.unitPrice,
            taxRate: defaultQuoteitemList?.taxRate,
            packageLength: defaultQuoteitemList?.packageLength,
            packageWidth: defaultQuoteitemList?.packageWidth,
            packageHeight: defaultQuoteitemList?.packageHeight,
            outerboxLength: defaultQuoteitemList?.outerboxLength,
            outerboxWidth: defaultQuoteitemList?.outerboxWidth,
            outerboxHeight: defaultQuoteitemList?.outerboxHeight,
            qtyPerInnerbox: defaultQuoteitemList?.qtyPerInnerbox,
            venderCode: defaultQuoteitemList?.venderCode,
            venderName: defaultQuoteitemList?.venderName,
            venderId: defaultQuoteitemList?.venderId,
            venderLists: venderList,
            skuName: item?.skuName,
            freeFlag: 0,
            freightFlag: 0,
            packageFlag: 0,
            currency: 'RMB'
          }
        } else {
          return { ...item }
        }
      })
    )
    handleCancel()
  } else {
    message.warning('请选择客户')
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
