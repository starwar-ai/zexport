<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择产品"
    width="1000"
    append-to-body
    align-center
    destroy-on-close
  >
    <el-tabs
      v-model="activeName"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="客户产品"
        name="cust"
      />
      <el-tab-pane
        label="自营产品"
        name="own"
      />
    </el-tabs>
    <div class="mb10px">
      <eplus-search
        v-if="activeName === 'cust'"
        :fields="eplusSearchSchema.fields"
        :moreFields="eplusSearchSchema.moreFields"
        @search="handleSearch"
        @reset="handleReset"
      />
      <eplus-search
        v-if="activeName === 'own'"
        :fields="ownSearchSchema.fields"
        :moreFields="ownSearchSchema.moreFields"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>
    <div style="overflow: hidden">
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
          label="基础产品编号"
          prop="basicSkuCode"
          show-overflow-tooltip
        />
        <el-table-column
          v-if="activeName === 'cust'"
          key="cust"
          align="center"
          label="客户货号"
          prop="cskuCode"
          show-overflow-tooltip
        />
        <el-table-column
          v-if="activeName === 'own'"
          key="own"
          align="center"
          label="自营产品货号"
          prop="oskuCode"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          prop="picture"
          label="图片"
          width="60px"
        >
          <template #default="{ row }">
            <EplusImgEnlarge
              :mainPicture="row.mainPicture"
              :thumbnail="row.thumbnail"
              width="40"
              height="40"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="中文名称"
          prop="skuName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="分类"
          prop="categoryName"
          show-overflow-tooltip
        />
        <el-table-column
          v-if="activeName === 'cust'"
          key="cust"
          align="center"
          label="客户编号"
          prop="custCode"
          show-overflow-tooltip
        />
        <el-table-column
          v-if="activeName === 'cust'"
          key="cust"
          align="center"
          label="客户名称"
          prop="custName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="产品类型"
          prop="skuType"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType) }}
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
      />
    </div>
    <!-- <Table
      ref="tableRef"
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      @select="handleSelectionChange"
      @select-all="handleSelectionAllChange"
      row-key="code"
      :pagination="{
        total: tableObject.total
      }"
      :columns="dialogColumns"
      headerAlign="center"
      align="center"
      :data="tableObject.tableList"
      style="height: 350px"
    /> -->

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
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { isValidArray } from '@/utils/is'
import { ownSkuList, simpleSkuList } from '@/api/pms/main'
import * as classApi from '@/api/pms/class'
import { cloneDeep } from 'lodash-es'

const message = useMessage()
defineOptions({ name: 'RenoticeSelectSku' })

const activeName = ref('cust')
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
let searchForm = reactive({})
const tableData = ref([])
const tableRef = ref()
const parentList = ref([])

const handleClick = async (val) => {
  activeName.value = val.props.name
  handleReset()
}

const getList = async () => {
  let req = activeName.value === 'cust' ? simpleSkuList : ownSkuList
  const res = await req({
    pageSize: pageForm.pageSize,
    pageNo: pageForm.pageNo,
    ...searchForm,
    ownBrandFlag: activeName.value === 'cust' ? undefined : 1,
    custProFlag: activeName.value === 'cust' ? 1 : 0
  })
  tableData.value = res.list
  pageForm.total = res.total
  toggleSelection()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item: any) => {
      parentList.value.forEach((el: any) => {
        if (item?.code == el?.code) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}

const treeList = ref([])

const eplusSearchSchema: EplusSearchSchema = reactive({
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '中文名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: (
        <eplus-tree-select
          checkStrictly={false}
          placeholder={'产品分类'}
          treeList={treeList}
          filterable
        />
      ),
      name: 'categoryId',
      label: '产品分类',
      formatter: (args: any[]) => {
        return findCategoryName(args)
      }
    }
  ],
  moreFields: []
})

const ownSearchSchema: EplusSearchSchema = reactive({
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '中文名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'oskuCode',
      label: '自营产品货号'
    },
    {
      component: (
        <eplus-tree-select
          checkStrictly={false}
          treeList={treeList}
          filterable
        />
      ),
      name: 'categoryId',
      label: '产品分类',
      formatter: (args: any[]) => {
        return findCategoryName(args)
      }
    }
  ],
  moreFields: []
})

const findCategoryName = (val) => {
  if (isValidArray(val)) {
    let tempList: any = cloneDeep(treeList.value)
    let selectList = []
    selectList = tempList.find((item) => item.id === val[0])
    return selectList.name || ''
  }
}
const open = async (list) => {
  let treeListTemp = await classApi.getClassTree()
  treeList.value = cloneDeep(treeListTemp)
  parentList.value = cloneDeep(list)
  selectedDiaData.value = cloneDeep(list || [])
  activeName.value = 'cust'
  dialogTableVisible.value = true
  await getList()
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

const dialogTableVisible = ref(false)

const selectedDiaData = ref()

const handleSelectionChange = (selection, row) => {
  if (row) {
    if (parentList.value?.length > 0) {
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
}

const handleSelectionAllChange = (selection) => {
  selection.forEach((item) => {
    handleSelectionChange(selection, item)
  })
}

const emit = defineEmits(['sure'])
const handleSure = async () => {
  if (isValidArray(selectedDiaData.value)) {
    await emit(
      'sure',
      selectedDiaData.value.map((item) => {
        let defaultQuote =
          item.quoteitemList.find((item) => item.defaultFlag === 1) || item.quoteitemList[0]
        return {
          ...item,
          ...defaultQuote
        }
      })
    )
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  searchForm = {}
  parentList.value = []
  selectedDiaData.value = []
  tableData.value = []
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
