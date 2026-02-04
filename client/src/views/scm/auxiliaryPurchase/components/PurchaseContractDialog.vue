<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择采购合同"
    width="1200"
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
        @row-click="getRow"
      >
        <el-table-column
          align="center"
          width="80px"
          label="选择"
          ><template #default="{ row }">
            <el-radio
              v-model="radioobj.val"
              :label="row.id"
              >&nbsp;</el-radio
            >
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="采购合同编号"
          prop="code"
          show-overflow-tooltip
          minWidth="200px"
        />
        <el-table-column
          align="center"
          label="供应商名称"
          prop="venderName"
          show-overflow-tooltip
          minWidth="200px"
        />
        <el-table-column
          align="center"
          label="客户名称"
          prop="custName"
          show-overflow-tooltip
          minWidth="200px"
        />
        <el-table-column
          align="center"
          label="下单主体"
          prop="companyName"
          show-overflow-tooltip
          minWidth="200px"
        />
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
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import * as CommonApi from '@/api/common'
import { EplusSearchSchema } from '@/components/EplusSearch/types'

defineOptions({ name: 'CostDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
const loading = ref(false)
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
let radioobj = reactive({
  val: undefined
})
const indexRef = ref('')
let searchForm = reactive({})

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input />,
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: <el-input />,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: <el-input />,
      name: 'venderName',
      label: '供应商名称'
    },
    {
      component: (
        <eplus-custom-select
          api={CommonApi.getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择下单主体"
          style="width:100%"
        />
      ),
      name: 'companyId',
      label: '下单主体',
      formatter: async (args: any[]) => {
        if (args[0]) {
          const companyList = await CommonApi.getcompanySimpleList()
          const company = companyList.find((item) => item.id === args[0])
          return company ? company.name : ''
        }
        return ''
      }
    }
  ],
  moreFields: []
}
const handleSearch = (model) => {
  searchForm = { ...model }
  pageForm.pageNo = 1
  getList()
}
const handleReset = () => {
  searchForm = {}
  pageForm.pageNo = 1
  getList()
}

const emit = defineEmits<{
  sure: [link: any]
}>()

const getList = async () => {
  loading.value = true
  try {
    let res = await PurchaseContractApi.getPurchaseContractSimpleList({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      auxiliaryFlag: 0,
      ...searchForm
    })
    tableData.value = res.list || []
    pageForm.total = res.total || 0
    loading.value = false
  } catch {
    loading.value = false
  } finally {
    loading.value = false
  }
}

const open = async (index, code) => {
  indexRef.value = index
  dialogTableVisible.value = true
  await getList()
  tableData.value.map((item: any) => {
    if (item.code == code) {
      radioobj.val = item.id
    }
  })
}
defineExpose({ open })

const getRow = (row) => {
  radioobj.val = row?.id
}

const handleCancel = () => {
  dialogTableVisible.value = false
}
const handleSure = () => {
  if (radioobj?.val) {
    const result: any = tableData.value.find((item) => item?.id === Number(radioobj.val))
    emit('sure', [result, indexRef.value])
    handleCancel()
  } else {
    message.warning('请选择采购合同')
  }
}

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
