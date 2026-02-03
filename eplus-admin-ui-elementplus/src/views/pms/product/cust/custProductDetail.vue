<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :page="pageFlag"
    backUrl="/base/product-manage/csku"
    :approveApi="custSkuApi.approveCsku"
    :rejectApi="custSkuApi.rejectCsku"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'pms:csku:submit'
    }"
    :edit="{
      permi: 'pms:csku:update',
      handler: () => goEdit()
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
    >
      <template #singleNetweight>
        {{ pageInfo?.singleNetweight.weight }} {{ pageInfo?.singleNetweight.unit }}
      </template>

      <template #onshelfFlag>
        {{ getDictLabel(DICT_TYPE.ONSHELF_FLAG, pageInfo.onshelfFlag) }}
        <el-button
          size="small"
          @click="updateVal('onshelfFlag')"
          v-if="!props.readonly"
          v-hasPermi="['pms:csku:onshelfFlag']"
          >修改状态
        </el-button>
      </template>
      <template #companyPrice>
        <span v-if="pageInfo.companyPrice.amount">
          {{ pageInfo.companyPrice.amount }} {{ pageInfo.companyPrice.currency }}
        </span>
        <span v-else> - </span>
        <el-button
          class="ml10px"
          size="small"
          @click="setPrice"
          v-if="!props.readonly"
          v-hasPermi="['pms:csku:pricing']"
          >设置定价差价
        </el-button>
      </template>
      <template #advantageFlag>
        {{ getDictLabel(DICT_TYPE.IS_INT, pageInfo.advantageFlag) }}
        <el-button
          size="small"
          @click="updateVal('advantageFlag')"
          v-if="!props.readonly"
          v-hasPermi="['pms:csku:advantage']"
          >{{ pageInfo.advantageFlag === 1 ? '取消优势产品' : '设为优势产品' }}
        </el-button>
      </template>
    </eplus-description>

    <eplus-description
      title=""
      :data="pageInfo"
      :items="basic2Info"
    >
      <template #description>
        <SkuDescriptionCom :text="pageInfo.description" />
      </template>
      <template #descriptionEng>
        <SkuDescriptionCom :text="pageInfo.descriptionEng" />
      </template>
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
      </template>
    </eplus-description>

    <MainTableDetail :info="pageInfo" />
    <QueteDetail :info="pageInfo" />
    <HsdataDetail :info="pageInfo" />
    <OtherDetail :info="pageInfo" />

    <template #otherAction>
      <component
        v-for="(item, index) in btnList"
        :key="index"
        :is="item"
      />
    </template>
  </eplus-detail>
  <SetPriceCom
    ref="SetPriceComRef"
    @sure="handleUpdate"
  />

  <OtherActionCom
    ref="OtherActionComRef"
    @success="handleUpdate"
  />
</template>
<script setup lang="tsx">
import { ElMessageBox } from 'element-plus'
import * as custSkuApi from '@/api/pms/cust/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import SetPriceCom from '../main/components/SetPriceCom.vue'
import OtherDetail from '../main/components/OtherDetail.vue'
import MainTableDetail from '../main/components/MainTableDetail.vue'
import QueteDetail from '../main/components/QueteDetail.vue'
import HsdataDetail from '../main/components/HsdataDetail.vue'
import { LengthUnit } from '@/utils/config'
import { checkPermi } from '@/utils/permission'
import SkuDescriptionCom from '../components/skuDescriptionCom.vue'
import OtherActionCom from './OtherActionCom.vue'
import { getSkuInfoByCode } from '@/api/common'

const message = useMessage()
const pageInfo = ref({})
const pageId = ref()
const pageFlag = ref(false)
const btnList = ref([])
const eplusDetailRef = ref()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()
defineOptions({ name: 'CustProductDetail' })

const { updateDialogActions, clearDialogActions } =
  props.id && !useRoute().params.id
    ? (inject('dialogActions') as {
        updateDialogActions: (...args: any[]) => void
        clearDialogActions: () => void
      })
    : useRoute().params.id
      ? {
          updateDialogActions: (...args: any[]) => {
            btnList.value.push(...args)
          },
          clearDialogActions: () => {
            btnList.value.splice(0, btnList.value.length)
          }
        }
      : { updateDialogActions: () => {}, clearDialogActions: () => {} }

//定义edit事件
// const { close, goEdit, goChange } = props.id
//   ? (inject('dialogEmits') as {
//       close: () => void
//       goEdit: (val) => void
//       goChange: (val) => void
//     })
//   : { close: () => {}, goEdit: () => {}, goChange: () => {} }

const loading = ref(true)
const { query } = useRoute()
const setBtn = () => {
  clearDialogActions()
  if (
    pageInfo.value.changeStatus !== 2 &&
    pageInfo.value.auditStatus === 2 &&
    checkPermi(['pms:csku:change'])
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          goChange()
        }}
        key="changeSku"
      >
        变更
      </el-button>
    )
  }
  if (
    pageInfo.value.changeStatus !== 2 &&
    pageInfo.value.auditStatus === 0 &&
    checkPermi(['pms:csku:delete'])
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleDel()
        }}
        key="delete"
      >
        删除
      </el-button>
    )
  }
  updateDialogActions(
    <el-button
      onClick={() => {
        goCopy()
      }}
      key="copySku"
    >
      复制
    </el-button>
  )
}

const setPageInfo = () => {
  pageInfo.value.hsdataCode = pageInfo.value.hsdata?.code
  pageInfo.value.hsdataUnit = pageInfo.value.hsdata?.unit
  pageInfo.value.hsdataTaxRefundRate = pageInfo.value.hsdata?.taxRefundRate

  pageInfo.value.spec = `${pageInfo.value?.specLength}*${pageInfo.value?.specWidth}*${pageInfo.value?.specHeight} ${LengthUnit}`
}

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value =
      props.id || pageFlag.value
        ? await custSkuApi.getCustSkuInfo({ id: pageId.value })
        : await custSkuApi.getCustSkuAuditInfo({ id: pageId.value })
    setPageInfo()
    setBtn()
  } finally {
    loading.value = false
  }
}

const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '产品编号'
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号'
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'price',
    label: '销售价格',
    type: 'money'
  },
  {
    field: 'lastDealPrice',
    label: '最近历史成交价',
    type: 'money'
  },
  {
    field: 'name',
    label: '中文品名'
  },
  {
    field: 'declarationName',
    label: '报关中文品名'
  },
  {
    field: 'nameEng',
    label: '英文品名'
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文品名'
  },
  {
    field: 'categoryName',
    label: '产品分类'
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
    label: '品牌'
  },
  {
    field: 'material',
    label: '产品材质'
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    dictType: DICT_TYPE.MEASURE_UNIT
  },
  {
    field: 'spec',
    label: '单品规格'
  },
  {
    field: 'singleNetweight',
    label: '单品净重',
    type: 'weight'
  },
  // {
  //   field: 'purchaseUserName',
  //   label: '采购员'
  // },

  {
    slotName: 'onshelfFlag',
    field: 'onshelfFlag',
    label: '状态'
  },
  {
    slotName: 'companyPrice',
    field: 'companyPrice',
    label: '定价差价'
  },
  {
    slotName: 'advantageFlag',
    field: 'advantageFlag',
    label: '是否优势产品'
  },
  {
    field: 'barcode',
    label: '条形码'
  },
  {
    field: 'creatorName',
    label: '录入人'
  },
  {
    field: 'createTime',
    label: '创建日期',
    type: 'date'
  },
  {
    field: 'updateTime',
    label: '更新日期',
    type: 'date'
  }
]
const basic2Info: EplusDescriptionItemSchema[] = [
  {
    slotName: 'description',
    field: 'description',
    label: '中文描述',
    span: 12
  },
  {
    slotName: 'descriptionEng',
    field: 'descriptionEng',
    label: '英文描述',
    span: 12
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
  loading.value = true
  try {
    pageInfo.value = await getSkuInfoByCode(pageInfo.value.code)
    setPageInfo()
    setBtn()
  } finally {
    loading.value = false
  }
}

const handleDel = async () => {
  await message.confirm('确认删除吗？')
  await custSkuApi.deleteCustSku(pageInfo.value?.id)
  message.success('删除成功')
  eplusDetailRef.value.close()
}

const OtherActionComRef = ref()
const goEdit = () => {
  OtherActionComRef.value.handleUpdate(pageInfo.value.id)
}

const goChange = () => {
  OtherActionComRef.value.handleChange(pageInfo.value.id)
}

const goCopy = () => {
  OtherActionComRef.value.handleCopy(pageInfo.value.id)
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
  let req = val === 'onshelfFlag' ? custSkuApi.setOnshelfFlag : custSkuApi.setAdvantage
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

const SetPriceComRef = ref()

const setPrice = () => {
  SetPriceComRef.value.open(pageInfo.value)
}

onMounted(async () => {
  if (useRoute().params.id) {
    pageId.value = useRoute().params.id
    pageFlag.value = true
  } else {
    pageFlag.value = false
    if (query.id) {
      showProcessInstanceTaskListFlag.value = false
      outDialogFlag.value = true
      pageId.value = query.id
    }
    if (props.id) {
      showProcessInstanceTaskListFlag.value = true
      outDialogFlag.value = false
      pageId.value = props.id
    }
  }

  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
