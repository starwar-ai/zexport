<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="skuApi.approveSkuChange"
    :rejectApi="skuApi.rejectSkuChange"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'pms:auxiliary-sku-change:submit',
      handler: () => {}
    }"
    :approve="{
      permi: 'pms:auxiliary-sku-change:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
      oldChangeField="oldData"
    />
    <eplus-description
      title=""
      :data="pageInfo"
      :items="basic2Info"
    >
      <template #annex>
        <UploadList
          :fileList="pageInfo.annex"
          disabled
        />
      </template>
      <template #picture>
        <!-- <UploadPic
          :pictureList="pageInfo.picture"
          disabled
        /> -->

        <UploadZoomPic
          v-model="pageInfo.picture"
          disabled
        />

        <div class="old-picture">
          <div class="old-mask">已删除</div>
          <UploadZoomPic
            v-model="pageInfo.oldCust.picture"
            disabled
          />
        </div>
      </template>
    </eplus-description>
    <eplus-description
      title="采购记录"
      :data="pageInfo"
      :items="purchaseInfo"
    >
      <template #purchaseInfo>
        <el-input
          v-model="inputRef"
          placeholder="请输入"
          clearable
          style="width: 350px"
          @change="inputChange"
        >
          <template #prepend>
            <el-select
              v-model="selectRef"
              @change="handleSelect"
              style="width: 120px"
            >
              <el-option
                v-for="field in purchaseFields"
                :key="field.name"
                :label="field.label"
                :value="field.name"
              />
            </el-select>
          </template>
        </el-input>
      </template>
      <template #purchaseTable>
        <Table
          :columns="purchaseTableColumns"
          headerAlign="center"
          align="center"
          :data="purchaseTable"
        />
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as skuApi from '@/api/pms/main/index'
import * as auxiliaryApi from '@/api/pms/auxiliary/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { EplusSearchSubFieldSchema } from '@/components/EplusSearch/types'

const message = useMessage()
const pageInfo = ref<typeObj>({})

let purchaseTable = reactive([])
const inputRef = ref('')
const selectRef = ref('cskuCode')
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()
defineOptions({ name: 'MainDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'name',
    label: '中文品名',
    isCompare: true
  },
  {
    field: 'nameEng',
    label: '英文品名',
    isCompare: true
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    dictType: DICT_TYPE.MEASURE_UNIT
  }
]

const basic2Info: EplusDescriptionItemSchema[] = [
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    slotName: 'picture',
    field: 'picture',
    label: '图片',
    span: 24,
    isCompare: true
  }
]
const purchaseInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'purchaseInfo',
    field: 'purchaseInfo',
    label: '',
    span: 24
  },
  { slotName: 'purchaseTable', field: 'purchaseTable', label: '', span: 24 }
]
const purchaseTableColumns = [
  {
    field: 'sortNum',
    label: '序号',
    width: '80px'
  },
  {
    field: 'purchaseModel',
    label: '采购类型',
    minWidth: '80px'
  },
  {
    field: 'skuName',
    label: '包材名称',
    minWidth: '80px'
  },
  {
    field: 'skuUnit',
    label: '计量单位',
    minWidth: '80px'
  },
  {
    field: 'specRemark',
    label: '规格描述',
    minWidth: '80px'
  },
  {
    field: 'mainPicture',
    label: '图片',
    minWidth: '80px'
  },
  {
    field: 'planQuantity',
    label: '计划采购量',
    minWidth: '80px'
  },
  {
    field: 'quantity',
    label: '实际采购量',
    minWidth: '80px'
  },
  {
    field: 'venderName',
    label: '供应商名称',
    minWidth: '80px'
  },
  {
    field: 'unitPrice',
    label: '供应商报价',
    minWidth: '80px'
  },
  {
    field: 'saleContractCode',
    label: '销售合同',
    minWidth: '80px'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同',
    minWidth: '80px'
  },
  {
    field: 'auxiliarySkuCode',
    label: '产品编号',
    minWidth: '80px'
  },
  {
    field: 'auxiliarySkuName',
    label: '产品品名',
    minWidth: '80px'
  },
  {
    field: 'auxiliaryCskuCode',
    label: '客户货号',
    minWidth: '80px'
  },
  {
    field: 'purchaseModel',
    label: '采购时间',
    minWidth: '80px'
  }
]
//采购信息搜索逻辑
const purchaseFields: EplusSearchSubFieldSchema[] = [
  {
    name: 'cskuCode',
    label: '客户货号'
  },
  {
    name: 'purchaseContractCode',
    label: '采购合同'
  },
  {
    name: 'saleContractCode',
    label: '销售合同'
  },
  {
    name: 'specRemark',
    label: '规格描述'
  }
]
const inputChange = async (val) => {
  try {
    const tempKey = selectRef.value
    purchaseTable = await auxiliaryApi.getPurchaseInfo({ [tempKey]: val })
  } catch {}
}
const handleSelect = (val) => {
  inputRef.value = ''
}
const getPageInfo = async () => {
  loading.value = true
  try {
    await refreshInfo()
  } finally {
    loading.value = false
  }
}

const refreshInfo = async () => {
  pageInfo.value = await auxiliaryApi.getSkuChangeInfo({ id: props.id })
  pageInfo.value.changeFlag = false
  pageInfo.value.hsdataCode = pageInfo.value.hsdata?.code
  pageInfo.value.hsdataUnit = pageInfo.value.hsdata?.unit
  pageInfo.value.hsdataTaxRefundRate = pageInfo.value.hsdata?.taxRefundRate
}
const handleUpdate = async () => {
  await refreshInfo()
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getPageInfo()
})
</script>
<style lang="scss" scoped>
.old-picture {
  position: ralative;

  .old-mask {
    position: absolute;
    left: 0;
    top: 55%;
    width: 100%;
    height: 100%;
    // opacity: 0.1;
    z-index: 9999;
  }
}
</style>
