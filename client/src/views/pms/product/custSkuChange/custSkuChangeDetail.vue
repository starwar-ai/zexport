<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="custSkuApi.approveCskuChange"
    :rejectApi="custSkuApi.rejectCskuChange"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'pms:csku:submit'
    }"
    :approve="{
      permi: 'pms:csku:audit',
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
      <template #spec>
        <div v-if="pageInfo?.spec == pageInfo?.oldData?.spec">
          {{ pageInfo?.spec }} {{ LengthUnit }}
        </div>
        <div v-else>
          <span class="newVal mr5"> {{ pageInfo?.spec }} {{ LengthUnit }} </span>
          <span class="oldVal"> {{ pageInfo?.oldData.spec }} {{ LengthUnit }} </span>
        </div>
      </template>
      <template #singleNetweight>
        {{ pageInfo?.singleNetweight.weight }} {{ pageInfo?.singleNetweight.unit }}
      </template>
      <template #onshelfFlag>
        {{ getDictLabel(DICT_TYPE.ONSHELF_FLAG, pageInfo.onshelfFlag) }}
        <!-- <el-button
          size="small"
          @click="updateVal('onshelfFlag')"
          v-if="!props.readonly"
          v-hasPermi="['pms:csku:onshelfFlag']"
          >修改状态
        </el-button> -->
      </template>
      <template #companyPrice>
        <span v-if="pageInfo.companyPrice.amount">
          {{ pageInfo.companyPrice.amount }} {{ pageInfo.companyPrice.currency }}
        </span>
        <span v-else> - </span>
      </template>
      <template #advantageFlag>
        {{ getDictLabel(DICT_TYPE.IS_INT, pageInfo.advantageFlag) }}
        <!-- <el-button
          size="small"
          @click="updateVal('advantageFlag')"
          v-if="!props.readonly"
          v-hasPermi="['pms:csku:advantage']"
          >{{ pageInfo.advantageFlag === 1 ? '取消优势产品' : '设为优势产品' }}
        </el-button> -->
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
              >{{ item.name }}</el-tag
            >
          </span>
        </span>
      </template>
      <template #picture>
        <UploadZoomPic
          v-model="pageInfo.picture"
          disabled
        />
        <div v-if="isValidArray(pageInfo.delPic)">
          <div>已删除</div>
          <UploadZoomPic
            v-model="pageInfo.oldData.picture"
            disabled
          />
        </div>
      </template>
    </eplus-description>

    <MainTableDetail :info="pageInfo" />
    <QueteDetail :info="pageInfo" />
    <HsdataDetail :info="pageInfo" />
    <OtherDetail :info="pageInfo" />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { ElMessageBox } from 'element-plus'
import * as skuApi from '@/api/pms/main/index'
import * as custSkuApi from '@/api/pms/cust/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
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
defineOptions({ name: 'custSkuChangeDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()

const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}
const getPageInfo = async () => {
  loading.value = true
  try {
    const res = props.id
      ? await custSkuApi.cskuChangeDetail({ id: props.id })
      : await custSkuApi.cskuAuditChangeDetail({ id: query.id })

    pageInfo.value = res
    pageInfo.value.changeFlag = false
    pageInfo.value.hsdataCode = pageInfo.value.hsdata?.code
    pageInfo.value.hsdataUnit = pageInfo.value.hsdata?.unit
    pageInfo.value.hsdataTaxRefundRate = pageInfo.value.hsdata?.taxRefundRate

    // 规格
    pageInfo.value.spec = `${res?.specLength}*${res?.specWidth}*${res?.specHeight} ${LengthUnit}`
    pageInfo.value.oldData.spec = `${res?.oldData?.specLength}*${res?.oldData?.specWidth}*${res?.oldData?.specHeight} ${LengthUnit}`
    pageInfo.value.annex = changeData(res.annex, res.oldData.annex)
    setNewData(pageInfo.value.quoteitemDTOList, pageInfo.value.oldData.quoteitemDTOList || [])
    pageInfo.value.delPic = getDelPic(pageInfo.value.picture, pageInfo.value.oldData.picture)
  } finally {
    loading.value = false
  }
}

// 新旧数据对比添加标识
const changeData = (newData, oldData) => {
  const newArr = newData.filter((obj1) => !oldData.some((obj2) => obj1.name === obj2.name))
  newArr.forEach((obj) => (obj.info = 'new'))

  const oldArr = oldData.filter((obj2) => !newData.some((obj1) => obj1.name === obj2.name))
  oldArr.forEach((obj) => (obj.info = 'old'))
  newData = [...newArr, ...oldArr]
  return newData
}

const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'custName',
    label: '客户名称',
    isCompare: true
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    isCompare: true
  },
  {
    field: 'price',
    label: '参考单价',
    type: 'money',
    isCompare: true
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
    field: 'categoryName',
    label: '产品分类',
    isCompare: true
  },
  {
    field: 'code',
    label: '产品编码',
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
  // {
  //   field: 'ownBrandFlag',
  //   label: '是否自主品牌',
  //   dictType: DICT_TYPE.IS_INT
  // },
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
    slotName: 'onshelfFlag',
    field: 'onshelfFlag',
    label: '状态',
    isCompare: true
  },
  {
    slotName: 'companyPrice',
    field: 'companyPrice',
    label: '定价差价',
    isCompare: true
  },
  {
    slotName: 'advantageFlag',
    field: 'advantageFlag',
    label: '是否优势产品',
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
    field: 'barcode',
    label: '条形码',
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
  await getPageInfo()
}

const updateVal = (val) => {
  let des = '',
    params = {}
  if (val === 'onshelfFlag') {
    let status = pageInfo.value.onshelfFlag == 1 ? 0 : 1
    des = `确认将产品状态修改为${getDictLabel(DICT_TYPE.ONSHELF_FLAG, status)}吗？`
    params = {
      id: pageInfo.value.id,
      status: status
    }
  } else {
    if (pageInfo.value.advantageFlag == 1) {
      des = '确认该产品取消优势产品吗？'
    } else {
      des = '确认该产品设为优势产品吗？'
    }
    params = {
      id: pageInfo.value.id,
      advantageFlag: pageInfo.value.advantageFlag == 1 ? 0 : 1
    }
  }
  let req = val === 'onshelfFlag' ? skuApi.setOnshelfFlag : skuApi.setAdvantage
  ElMessageBox.confirm(des, '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确认',
    type: 'warning'
  })
    .then(async () => {
      await req(params)
      message.success('修改成功')
      getPageInfo()
    })
    .catch(() => {})
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
.newVal {
  color: #f7aa49;
}

.oldVal {
  color: #999;
  text-decoration: line-through;
}
</style>
