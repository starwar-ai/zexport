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
        label="基础产品"
        name="0"
      />
      <el-tab-pane
        label="自营产品"
        name="1"
        v-if="!props.ownFlag"
      />
    </el-tabs>
    <div class="mb10px">
      <eplus-search
        :fields="eplusSearchSchema.fields"
        :moreFields="eplusSearchSchema.moreFields"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>
    <Table
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :pagination="{
        total: tableObject.total
      }"
      :columns="dialogColumns"
      headerAlign="center"
      align="center"
      :data="tableObject.tableList"
      @row-click="getRow"
      style="height: 350px"
    />

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
import Table from '@/components/Table/src/Table.vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn } from '@/utils/table'
import * as skuApi from '@/api/pms/main/index.ts'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

const activeName = ref('0')
let activeKey = ref(0)

const handleClick = async (val) => {
  activeKey.value = val.props.name * 1
  await getList()
}

const message = useMessage()
defineOptions({ name: 'SelectSku' })
const props = defineProps<{
  ownFlag
}>()

const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    ps.ownBrandFlag = props.ownFlag ? 0 : activeKey.value
    let req = activeKey.value === 1 ? skuApi.ownSkuList : skuApi.simpleSkuList
    const res = await req({
      ...ps,
      ownBrandFlag: props.ownFlag ? 0 : activeKey.value,
      ownBrandonOnlyFlag: props.ownFlag ? 1 : undefined
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  defaultParams: {
    skuTypeInList: '1, 2, 3',
    custProFlag: 0,
    ownBrandFlag: props.ownFlag ? 0 : activeKey.value
  }
})

const { getList, setSearchParams } = tableMethods

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'skuName',
      label: '中文品名'
    },
    // {
    //   component: <el-input></el-input>,
    //   name: 'skuCode',
    //   label: '产品编号'
    // }
    {
      component: <el-input></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input></el-input>,
      name: 'oskuCode',
      label: '自营产品货号'
    }
  ],
  moreFields: []
}

const open = async () => {
  dialogTableVisible.value = true
  await getList()
}

const handleSearch = (model) => {
  setSearchParams(
    {
      ...model,
      skuTypeInList: '1, 2, 3',
      custProFlag: 0,
      ownBrandFlag: props.ownFlag ? 0 : activeKey.value
    },
    []
  )
}
const handleReset = () => {
  setSearchParams(
    { skuTypeInList: '1, 2, 3', custProFlag: 0, ownBrandFlag: props.ownFlag ? 0 : activeKey.value },
    []
  )
}

const dialogColumns = reactive([
  {
    label: '选择',
    width: '60px',
    slots: {
      default: (data) => {
        const { row, $index } = data
        return (
          <el-radio
            v-model={radioobj.val}
            label={row.id}
          >
            &nbsp;
          </el-radio>
        )
      }
    }
  },
  {
    label: '图片',
    field: 'picture',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusImgEnlarge
            mainPicture={row.mainPicture}
            thumbnail={row.thumbnail}
            width="40"
            height="40"
          />
        )
      }
    }
  },
  {
    field: 'skuName',
    label: '中文品名',
    minWidth: '100px'
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: '100px'
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    minWidth: '100px'
  },
  {
    field: 'skuType',
    label: '产品类型',
    minWidth: '100px',
    formatter: formatDictColumn(DICT_TYPE.SKU_TYPE)
  }
])

const dialogTableVisible = ref(false)

let radioobj = reactive({
  val: undefined
})
const getRow = (row: any) => {
  radioobj.val = row.id
  selectedDiaData.value = row
}

const selectedDiaData = ref()

const emit = defineEmits(['sure'])
const handleSure = async () => {
  if (selectedDiaData.value?.id) {
    await emit('sure', selectedDiaData.value)
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  activeName.value = '0'
  tableObject.tableList = []
  radioobj.val = undefined
  selectedDiaData.value = []
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
