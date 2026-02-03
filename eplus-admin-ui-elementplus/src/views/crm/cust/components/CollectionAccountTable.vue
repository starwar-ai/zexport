<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
    >
      关联收款银行
    </el-button>
    <el-button @click="handleRemove">移除收款银行</el-button>
  </div>
  <Table
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
    @selection-change="handleSelectionChange"
  />
  <CollectionAccountDia
    ref="CollectionAccountDiaRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import { isValidArray } from '@/utils/is'
import CollectionAccountDia from './CollectionAccountDia.vue'

defineOptions({ name: 'CollectionAccountTable' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const tableList = ref([])

const tableColumns = reactive<TableColumn[]>([
  {
    type: 'selection'
  },
  {
    field: 'companyName',
    label: '公司名称'
  },
  {
    field: 'bankName',
    label: '银行名称'
  },
  {
    field: 'bankCode',
    label: '银行账号'
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
    fixed: 'right',
    align: 'left',
    slots: {
      default: (data) => {
        const { $index } = data

        return (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={async () => {
                await delRow($index)
              }}
            >
              移除
            </el-button>
          </div>
        )
      }
    }
  }
])

const CollectionAccountDiaRef = ref()
const handleAdd = async () => {
  CollectionAccountDiaRef.value.open(tableList, [])
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el: any) => {
      return el.code
    })
    tableList.value = tableList.value.filter((item: any) => {
      if (!delArr.includes(item.code)) {
        return item
      }
    })
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  tableList.value = list
}

const emit = defineEmits(['change'])

const checkData = () => {
  if (tableList.value.length > 0) {
    return tableList.value
  } else {
    return false
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  if (isValidArray(props.formData?.collectionAccountList)) {
    tableList.value = props.formData?.collectionAccountList
  } else {
    tableList.value = []
  }
})
</script>
