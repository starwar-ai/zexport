<template>
  <Dialog
    v-model="dialogTableVisible"
    :title="title"
    width="50%"
    append-to-body
    destroy-on-close
  >
    <div class="flex justify-between"
      ><span>分摊金额:{{ ruleForm.totalAmount }}</span>
      <span
        >分摊规则:
        <!-- <eplus-dict-select
          v-model="ruleForm.invoiceType"
          :dictType="DICT_TYPE.INVOICE_TYPE"
          @change="(val) => onChangeType(val)"
        />  -->
        <el-select
          v-model="ruleForm.invoiceType"
          placeholder="Select"
          :disabled="currStatus"
          @change="handleChange"
        >
          <el-option
            label="按数量分摊"
            value="1"
          />
          <el-option
            label="自定义"
            value="2"
          />
        </el-select> </span
    ></div>
    <div class="">
      <!-- <eplus-form-table
        ref="tableRef"
        selectionFlag
        :list="tableData"
        :defaultVal="{}"
        :schema="tableColumns"
        @selection-change="handleSelectionChange"
      /> -->
      <el-table
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column
          type="selection"
          :selectable="checkSelectable"
          width="55"
        />
        <el-table-column
          property="cskuCode"
          label="客户货号"
          show-overflow-tooltip
        />

        <el-table-column
          property="skuName"
          label="产品名称"
          show-overflow-tooltip
        />
        <el-table-column
          property="purchaseContractCode"
          label="采购合同"
          show-overflow-tooltip
        />
        <el-table-column
          property="quantity"
          label="采购数量"
        />
        <el-table-column
          label="分摊金额"
          property="itemAmount"
        >
          <template #default="scope">
            <el-input
              v-model="scope.row.itemAmount"
              placeholder="请输入金额"
            />
          </template>
        </el-table-column>
      </el-table>
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
import { TableColumn } from '@/types/table'

defineOptions({ name: 'InventoryQuantityDialog' })
const dialogTableVisible = ref(false)
const message = useMessage()
const tableRef = ref()
const tableData = ref([
  {
    id: 1,
    cskuCode: '125698',
    skuName: '分摊1',
    quantity: '1000'
  },
  {
    id: 2,
    cskuCode: '414144',
    skuName: '分摊2',
    quantity: '2000'
  },
  {
    id: 3,
    cskuCode: '16588236',
    skuName: '分摊3',
    quantity: '3000'
  }
])
const index = ref(0)
const title = ref('分摊')
const currStatus = ref('1')
const ruleForm = reactive({
  invoiceType: '1',
  totalAmount: 6000
})

let tableColumns = reactive<TableColumn[]>([
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同'
  },
  {
    field: 'quantity',
    label: '采购数量'
  },

  {
    field: 'itemAmount',
    label: '分摊金额',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <el-input-number
            controls={false}
            v-model={row.itemAmount}
            min={0.01}
            precision={3}
            class="!w-100%"
          />
        )
      }
    }
  }
])

const checkSelectable = (row) => {
  if (currStatus.value == 1) {
    return false
  } else {
    return true
  }
}

const emit = defineEmits<{
  sure: [link: string]
}>()

const selectedList = ref([])

const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleChange = (val) => {
  if (val === '1') {
    // 分摊逻辑
  }
}

const open = async (params) => {
  title.value = params.status == 1 ? '分摊' : '取消分摊'
  currStatus.value = params.status
  dialogTableVisible.value = true
}

defineExpose({ open })

const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}

const handleSure = async () => {
  let valid = await tableRef.value.validate()
  if (valid) {
  }
}

onMounted(async () => {
  try {
  } catch {}
})
</script>

<style lang="scss" scoped></style>
