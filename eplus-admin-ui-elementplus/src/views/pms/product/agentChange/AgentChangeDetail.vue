<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="agentApi.approveCskuChange"
    :rejectApi="agentApi.rejectCskuChange"
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
    />

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
    <QueteDetail :info="pageInfo" />
    <HsdataDetail :info="pageInfo" />
    <OtherDetail :info="pageInfo" />
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as agentApi from '@/api/pms/agent/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import OtherDetail from '../mainChange/components/OtherDetail.vue'
import QueteDetail from '../mainChange/components/QueteDetail.vue'
import HsdataDetail from '../mainChange/components/HsdataDetail.vue'
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
defineOptions({ name: 'AgentChangeDetail' })
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
      ? await agentApi.getChangeAgentSkuInfo({ id: props.id })
      : await agentApi.getChangeAgentSkuAuditInfo({ id: query.id })

    pageInfo.value = res
    pageInfo.value.changeFlag = false
    pageInfo.value.hsdataCode = pageInfo.value.hsdata?.code
    pageInfo.value.hsdataUnit = pageInfo.value.hsdata?.unit
    pageInfo.value.hsdataTaxRefundRate = pageInfo.value.hsdata?.taxRefundRate

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
    field: 'code',
    label: '产品编码',
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
  await getPageInfo()
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
