<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="title"
    width="1100"
    append-to-body
    destroy-on-close
    align-center
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
        height="350px"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="图片"
          prop="code"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <EplusImgEnlarge
              :mainPicture="row.mainPicture"
              :thumbnail="row.thumbnail"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="中文品名"
          prop="name"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="产品编号"
          prop="code"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="计量单位"
          prop="measureUnit"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ getDictLabel(DICT_TYPE.MEASURE_UNIT, row.measureUnit) }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="单品规格/净重"
          prop="code"
          min-width="160"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{
              `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/${row.singleNetweight.weight} ${row.singleNetweight.unit}`
            }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="采购成本"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span v-if="row.withTaxPrice?.amount">
              <EplusMoneyLabel :val="row.withTaxPrice" />
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
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
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as skuApi from '@/api/pms/main/index.ts'
import { cloneDeep } from 'lodash-es'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { LengthUnit } from '@/utils/config'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { getVenderSimpleList } from '@/api/common'

defineOptions({ name: 'SelectProductDialog' })
const message = useMessage()
const title = ref('')
const skuType = ref()
const excludeSkuCodeVal = ref()
const ownBrendFlagVal = ref()
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
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '产品编码'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '中文品名'
    },
    {
      component: (
        <eplus-input-search-select
          api={getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1 }}
          keyword="name"
          label="name"
          value="id"
          multiple={true}
          class="!w-100%"
          placeholder="请选择"
          onChangeEmit={(...$event) => getVenderName($event)}
        />
      ),
      name: 'venderIdList',
      label: '供应商',
      formatter: async (args: any[]) => {
        return venderNameStr.value
      }
    }
  ],
  moreFields: []
}
const venderNameStr = ref('')
const getVenderName = (e) => {
  let nameList = []
  e[1].forEach((item) => {
    e[0].forEach((el) => {
      if (item.id === el) {
        nameList.push(item.name)
      }
    })
  })
  venderNameStr.value = nameList.join(',')
}

const getImgUrl = (row) => {
  return row.picture.find((item) => item.mainFlag == 1)?.fileUrl
}

const handleSearch = (model) => {
  searchForm = model
  pageForm.pageNo = 1
  getList()
}
const handleReset = () => {
  searchForm = {}
  pageForm.pageNo = 1
  getList()
}

const emit = defineEmits<{
  sure: [link: string]
}>()

const getList = async () => {
  loading.value = true
  try {
    let res = await skuApi.getSkuPage({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      skuTypeInList: skuType.value,
      auditStatus: 2,
      onshelfFlag: 1,
      custProFlag: 0,
      ownBrendFlag: ownBrendFlagVal.value,
      excludeSkuCode: excludeSkuCodeVal.value,
      ...searchForm
    })
    tableData.value = res?.list || []
    pageForm.total = res?.total || 0
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  }
}

const open = async (list, titlestr, type, ownBrendFlag, excludeSkuCode) => {
  ownBrendFlagVal.value = ownBrendFlag || undefined
  excludeSkuCodeVal.value = excludeSkuCode
  skuType.value = type
  title.value = titlestr

  parentList.value = cloneDeep(list.value)
  selectedDiaData.value = cloneDeep(list.value)
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item) => {
      parentList.value.forEach((el) => {
        if (item.code == el.code) {
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
const handleCancel = () => {
  tableData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  if (len > 0) {
    let remainder = 100 % len,
      integer = Math.floor(100 / len)
    emit(
      'sure',
      selectedDiaData.value.map((item, index) => {
        return {
          ...item,
          skuCode: item.code,
          ratio: index + 1 == len ? integer + remainder : integer
        }
      })
    )
    handleCancel()
  } else {
    message.warning('请选择数据')
  }
}
defineExpose({ open })
</script>
