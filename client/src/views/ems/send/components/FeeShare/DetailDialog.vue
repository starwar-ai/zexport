<template>
  <Dialog
    v-model="dialogTableVisible"
    title="设置选项"
    width="500"
    append-to-body
    destroy-on-close
  >
    <div>
      <el-input
        v-model="searchVal"
        placeholder="请输入搜索关键字"
        @change="handleSearch"
      />
    </div>
    <div class="mt15px">选项列表</div>
    <div class="con_box pb50px">
      <template v-for="(item, index) in pageForm.children">
        <p
          v-if="item.isShow"
          :key="item.index"
          class="flex items-center justify-between"
        >
          <Icon
            @click="handleDel(item, index)"
            icon="ep:circle-close"
            class="pointer mr5px"
          />
          <el-input
            v-model="item.name"
            placeholder="请输入内容"
          />
        </p>
      </template>

      <p
        class="pointer"
        @click="handleAdd"
      >
        <Icon
          icon="ep:circle-plus"
          class="mr5px"
        />
        添加选项
      </p>
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
// import * as travelreimbApi from '@/api/oa/travelreimb'
// import { cloneDeep } from 'lodash-es'
// import { formatTime } from '@/utils/index'
// import { EplusSearchSchema } from '@/components/EplusSearch/types'
// import { getIntDictOptions, DICT_TYPE, getDictLabel, getDictLabels } from '@/utils/dict'
// import { EplusSearchMultiInput, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
// import { formatDictValue } from '@/utils/table'
import * as FeeShareApi from '@/api/common/feeShare'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'DetailDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const searchVal = ref('')
let pageForm: any = reactive({
  feeShareType: undefined,
  relationType: undefined,
  children: []
})
const emit = defineEmits<{
  sure
}>()

const handleAdd = () => {
  pageForm.children.push({
    id: undefined,
    name: '',
    isShow: true
  })
}

const handleDel = async (item, index) => {
  await FeeShareApi.descDel(item.id)
  pageForm.children.splice(index, 1)
}

const open = async (params, list) => {
  pageForm.feeShareType = params.feeShareType
  pageForm.relationType = params.relationType
  pageForm.children = list.map((item) => {
    return {
      id: item.id,
      name: item.name,
      isShow: true
    }
  })
  if (!isValidArray(pageForm.children)) {
    handleAdd()
  }
  dialogTableVisible.value = true
}

const handleSearch = () => {
  for (let index = 0; index < pageForm.children.length; index++) {
    const item = pageForm.children[index]
    item.isShow = item.name.includes(searchVal.value)
  }
}

const handleCancel = () => {
  dialogTableVisible.value = false
}
const handleSure = async () => {
  if (!isValidArray(pageForm.children)) {
    message.warning('请填写数据')
    return false
  }
  await FeeShareApi.descCreate({
    ...pageForm,
    children: pageForm.children.map((item) => {
      if (item.name) {
        return item
      }
    })
  })
  handleCancel()
  emit('sure')
}
defineExpose({ open })
</script>
<style lang="scss" scoped>
.con_box {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
}

.pointer {
  cursor: pointer;
}
</style>
