<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="ownSkuApi.changeApproveOsku"
    :rejectApi="ownSkuApi.changeRejectOsku"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'pms:sku-change:submit',
      handler: () => {}
    }"
    :approve="{
      permi: 'pms:own-brand-change:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
      oldChangeField="oldData"
    >
      <!-- <template #spec>
        <div v-if="pageInfo?.spec == pageInfo?.oldData?.spec">
          {{ pageInfo?.spec }}{{ LengthUnit }}
        </div>
        <div v-else>
          <span class="newVal mr5"> {{ pageInfo?.spec }}{{ LengthUnit }} </span>
          <span class="oldVal"> {{ pageInfo?.oldData.spec }}{{ LengthUnit }} </span>
        </div>
      </template> -->
      <template #companyPrice>
        <span v-if="pageInfo.companyPrice.amount">
          {{ pageInfo.companyPrice.amount }} {{ pageInfo.companyPrice.currency }}
        </span>
        <span v-else> - </span>
      </template>
    </eplus-description>

    <eplus-description
      title=""
      :data="pageInfo"
      :items="basic2Info"
      oldChangeField="oldData"
    >
      <template #annex>
        <span
          v-for="item in pageInfo.annex"
          :key="item?.id"
          class="mb5px mr5px"
        >
          <el-tag
            v-if="item.info == 'new' && item.name"
            type="warning"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <el-tag
            v-else-if="item.info == 'old' && item.name"
            type="info"
            class="oldVal"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <span v-else>
            <el-tag
              v-if="item.name"
              style="cursor: pointer"
              @click="handleDownload(item)"
              >{{ item.name }}
            </el-tag>
          </span>
        </span>
      </template>
      <template #picture>
        <UploadZoomPic
          v-model="pageInfo.picture"
          disabled
        />
        <div
          class="old-picture"
          v-if="isValidArray(pageInfo.delPic)"
        >
          <div class="old-mask">已删除</div>
          <UploadZoomPic
            v-model="pageInfo.delPic"
            disabled
          />
        </div>
      </template>
    </eplus-description>

    <MainTableDetail :info="pageInfo" />
    <QueteDetail :info="pageInfo" />
    <HsdataDetail :info="pageInfo" />
    <OtherDetail :info="pageInfo" />

    <SetPriceCom
      ref="SetPriceComRef"
      @sure="handleUpdate"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as ownSkuApi from '@/api/pms/own/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import SetPriceCom from '../mainChange/components/SetPriceCom.vue'
import OtherDetail from '../mainChange/components/OtherDetail.vue'
import MainTableDetail from '../mainChange/components/MainTableDetail.vue'
import QueteDetail from '../mainChange/components/QueteDetail.vue'
import HsdataDetail from '../mainChange/components/HsdataDetail.vue'
import { LengthUnit } from '@/utils/config'
import { getDelPic, setNewData } from '@/utils/index'
import { isValidArray } from '@/utils/is'

const message = useMessage()
const pageInfo = ref<typeObj>({})

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

const getPageInfo = async () => {
  loading.value = true
  try {
    await refreshInfo()
  } finally {
    loading.value = false
  }
}

const refreshInfo = async () => {
  const res = await ownSkuApi.getOwnSkuChangeDetail({ id: props.id })
  pageInfo.value = res
  pageInfo.value.changeFlag = false

  pageInfo.value.hsdataCode = pageInfo.value.hsdata?.code
  pageInfo.value.hsdataUnit = pageInfo.value.hsdata?.unit
  pageInfo.value.hsdataTaxRefundRate = pageInfo.value.hsdata?.taxRefundRate
  pageInfo.value.annex = changeData(res?.annex || [], res?.oldData?.annex || [])

  // 规格
  pageInfo.value.spec = `${res?.specLength}*${res?.specWidth}*${res?.specHeight} ${LengthUnit}`
  pageInfo.value.oldData.spec = `${res?.oldData?.specLength}*${res?.oldData?.specWidth}*${res?.oldData?.specHeight} ${LengthUnit}`
  setNewData(pageInfo.value.quoteitemDTOList, pageInfo.value.oldData.quoteitemDTOList)
  pageInfo.value.delPic = getDelPic(pageInfo.value.picture, pageInfo.value.oldData.picture)
}

// 新旧数据对比添加标识
const changeData = (newData, oldData) => {
  const newArr = newData.filter((obj1) => !oldData.some((obj2) => obj1.name === obj2.name))
  newArr.forEach((obj) => (obj.info = 'new'))

  const oldArr = oldData.filter((obj2) => !newData.some((obj1) => obj1.name === obj2.name))
  oldArr.forEach((obj) => (obj.info = 'old'))
  newData = [...newData, ...oldArr]
  return newData
}

const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}

const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '产品编码'
  },
  {
    field: 'sourceCode',
    label: '基础产品编号'
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    isCompare: true
  },
  {
    field: 'skuName',
    label: '基础产品名称'
  },
  {
    field: 'name',
    label: '中文品名',
    isCompare: true
  },
  {
    field: 'declarationName',
    label: '报关中文品名',
    isCompare: true
  },
  {
    field: 'nameEng',
    label: '英文品名',
    isCompare: true
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文品名',
    isCompare: true
  },
  {
    field: 'price',
    label: '参考单价',
    isCompare: true
  },
  {
    field: 'categoryName',
    label: '产品分类',
    isCompare: true
  },

  {
    field: 'sourceFlag',
    label: '产品来源',
    dictType: DICT_TYPE.SOURCE_FLAG
  },
  {
    field: 'skuType',
    label: '产品类型',
    dictType: DICT_TYPE.SKU_TYPE
  },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    dictType: DICT_TYPE.IS_INT
  },
  {
    field: 'brandName',
    label: '品牌',
    isCompare: true
  },
  {
    field: 'material',
    label: '产品材质',
    isCompare: true
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    dictType: DICT_TYPE.MEASURE_UNIT
  },
  {
    field: 'spec',
    label: '单品规格',
    isCompare: true
  },
  {
    field: 'singleNetweight',
    label: '单品净重',
    isCompare: true
  },
  // {
  //   field: 'purchaseUserName',
  //   label: '采购员'
  // },

  {
    // slotName: 'onshelfFlag',
    field: 'onshelfFlag',
    label: '状态',
    dictType: DICT_TYPE.ONSHELF_FLAG,
    isCompare: true
  },
  {
    slotName: 'companyPrice',
    field: 'companyPrice',
    label: '内部核算单价',
    isCompare: true
  },
  {
    // slotName: 'advantageFlag',
    field: 'advantageFlag',
    label: '是否优势产品',
    dictType: DICT_TYPE.IS_INT,
    isCompare: true
  },
  {
    field: 'barcode',
    label: '条形码',
    isCompare: true
  }
]
const basic2Info: EplusDescriptionItemSchema[] = [
  {
    field: 'description',
    label: '中文描述',
    span: 12,
    isCompare: true
  },
  {
    field: 'descriptionEng',
    label: '英文描述',
    span: 12,
    isCompare: true
  },
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
    span: 24
  }
]

const handleUpdate = async () => {
  await refreshInfo()
}

const SetPriceComRef = ref()

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
.newVal {
  color: #f7aa49;
}

.oldVal {
  color: #999;
  text-decoration: line-through;
}
</style>
