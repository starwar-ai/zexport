<template>
  <div class="flex gap-2">
    <el-popover
      placement="top-start"
      :width="20"
      trigger="hover"
      v-for="(tag, index) in tagList"
      :key="tag.value"
      :disabled="tag.defaultFlag === 1"
    >
      <template #reference>
        <el-tag
          closable
          :disable-transitions="false"
          @close="handleClose(index)"
        >
          <span v-if="tag.defaultFlag === 1">{{ tag.name }}（默认） </span>
          <span v-else>{{ tag.name }}</span>
        </el-tag>
      </template>
      <template #default>
        <div style="text-align: center">
          <el-button
            link
            type="primary"
            @click="setDefault(index)"
            >设置默认
          </el-button>
        </div>
      </template>
    </el-popover>
    <eplus-dict-select
      v-model="newVal"
      v-if="dictType && visible"
      size="small"
      :dictType="dictType"
      :clearable="false"
      @change-emit="handleDictChange"
    />
    <eplus-user-select
      v-model="newVal"
      v-else-if="isUser && visible"
      size="small"
      @change="handleUserChange"
    />
    <eplus-input-select
      v-model="newVal"
      v-else-if="isSelect && visible"
      :dataList="selectList"
      label="name"
      value="id"
      @change-emit="handleSelect"
    />
    <el-button
      v-else
      size="small"
      class="button-new-tag"
      @click="handleAdd"
    >
      +
    </el-button>
  </div>
</template>

<script setup lang="tsx">
import { getDictLabel } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
// const checkList = defineModel<any[]>()
const message = useMessage()
const tagList = ref([])
const visible = ref(false)
const newVal = ref('')
const props = defineProps<{
  isUser?: boolean
  list?: any
  dictType?: any
  modelValue: any
  isSelect?: boolean
  selectList?: any
}>()

watchEffect(() => {
  tagList.value = props.modelValue || []
})

const handleAdd = () => {
  visible.value = true
}
const handleDictChange = (val: any) => {
  if (isValidArray(tagList.value)) {
    let valList = tagList.value.map((item) => item.value)
    if (valList.includes(val)) {
      message.warning('所选数据已存在')
      return false
    } else {
      tagList.value.push({
        name: getDictLabel(props.dictType, val),
        value: val,
        defaultFlag: 0
      })
    }
  } else {
    tagList.value.push({
      name: getDictLabel(props.dictType, val),
      value: val,
      defaultFlag: 1
    })
  }
  newVal.value = ''
  visible.value = false
}
const handleUserChange = (userObj: any) => {
  if (isValidArray(tagList.value)) {
    let valList = tagList.value.map((item) => item.value)
    if (valList.includes(userObj.id)) {
      message.warning('所选数据已存在')
      return false
    } else {
      tagList.value.push({
        name: userObj.nickname,
        value: userObj.userId,
        deptId: userObj.deptId,
        deptName: userObj.deptName,
        defaultFlag: 0
      })
    }
  } else {
    tagList.value.push({
      name: userObj.nickname,
      value: userObj.userId,
      deptId: userObj.deptId,
      deptName: userObj.deptName,
      defaultFlag: 1
    })
  }
  newVal.value = ''
  visible.value = false
}
const handleSelect = (val) => {
  if (isValidArray(tagList.value)) {
    let valList = tagList.value.map((item) => item.id)
    if (valList.includes(val)) {
      message.warning('所选数据已存在')
      return false
    } else {
      tagList.value.push({
        name: props?.selectList.find((item) => item.id == val).name,
        id: val,
        defaultFlag: 0
      })
    }
  } else {
    tagList.value.push({
      name: props?.selectList.find((item) => item.id == val).name,
      id: val,
      defaultFlag: 1
    })
  }

  newVal.value = ''
  visible.value = false
}
const setDefault = (index: number) => {
  tagList.value.forEach((item) => {
    item.defaultFlag = 0
  })
  tagList.value[index].defaultFlag = 1
}
const handleClose = (index: number) => {
  if (tagList.value[index].defaultFlag == 1) {
    tagList.value.splice(index, 1)
    isValidArray(tagList.value) ? ((tagList.value[0] as any).defaultFlag = 1) : ''
  } else {
    tagList.value.splice(index, 1)
  }
}
const emit = defineEmits(['update:modelValue', 'change'])

watch(
  () => tagList.value,
  (list) => {
    emit('update:modelValue', list)
    // emit('change', list)
  },
  {
    deep: true
  }
)
</script>
<style lang="scss" scoped>
.select-item {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.tag {
  cursor: pointer;
}
</style>
