<template>
  <el-select
    v-model="model"
    clearable
    :multiple="props.multiple"
    popper-class="eplus-popper"
    @change="modelChange"
    @visible-change="isShow"
    class="w-100%"
    filterable
    :filter-method="selectFilter"
  >
    <div class="option_box">
      <div
        v-for="dict in userList"
        :key="dict.userId"
      >
        <span class="deptName">{{ dict?.firstDept ? dict?.deptName : '' }}</span>
        <el-option
          style="padding-left: 20px"
          :key="dict[props?.value] || dict.userId"
          :label="
            dict[props?.label]
              ? `${dict[props?.label]}${dict.status === 1 ? '（离职）' : ''}`
              : `${dict.nickname}${dict.status === 1 ? '（离职）' : ''}`
          "
          :value="dict[props?.value] || dict.userId"
        />
        <div
          class="line"
          v-if="dict?.endDept"
        ></div>
      </div>
    </div>

    <!-- <el-option
      v-for="item in userList"
      :key="item.id"
      :label="item.nickname"
      :value="item.id"
    >
      <span style="float: left">{{ item.nickname }}</span>
      <span style="float: right; color: var(--el-text-color-secondary)">{{ item.deptName }} </span>
    </el-option> -->
  </el-select>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as UserApi from '@/api/system/user'
import { useUserStore, useUserStoreWithOut } from '@/store/modules/user'
import { cloneDeep } from 'lodash-es'

const userStore = useUserStoreWithOut()

defineOptions({ name: 'EplusUserSelect' })

interface UserItem extends UserApi.UserVO {
  firstDept?: boolean
  endDept?: boolean
  userId?: number
}

const userList = ref<UserItem[]>([])
const initList = ref<UserItem[]>([])
const filterList = ref<UserItem[]>([])
const searchName = ref('')
const model = defineModel<string>()
const props = withDefaults(
  defineProps<{
    multiple?: boolean
    filterUserList?: boolean
    simpleUserList?: any
    value?: string
    label?: string
    filterMethod?: (user: UserItem) => boolean
  }>(),
  { multiple: false }
)
/** 查询列表 */
const getList = async () => {
  const userData = (await props?.simpleUserList?.value) || (await userStore.setUserList())
  userList.value = userData as UserItem[]
  let tempList = cloneDeep(userList.value)
  if (props?.filterUserList) {
    tempList = tempList.filter((item) => item?.userId !== useUserStore().user.id)
  }
  // 应用自定义过滤方法
  if (props.filterMethod) {
    tempList = tempList.filter((item) => props.filterMethod!(item))
  }
  let deptIdList = [...new Set(tempList.map((item) => item.deptId))]
    .map((id) => {
      return {
        deptId: id,
        deptName: tempList.find((item) => item?.deptId === id)?.deptName,
        userList: tempList
          .filter((item) => item?.deptId === id)
          .map((tem, index) => {
            return { ...tem, firstDept: index === 0 ? true : false }
          })
      }
    })
    .map((item) => item?.userList)
    .flat() as UserItem[]

  userList.value = deptIdList
  filterList.value = deptIdList
  initList.value = cloneDeep(deptIdList)
}

const searchNameChange = () => {
  filterList.value = initList.value.filter((item) => {
    if (item?.nickname) {
      return item?.nickname.includes(searchName.value)
    } else if (props?.label) {
      return item[props.label].includes(searchName.value)
    }
  })
  if (filterList.value.length > 0) {
    userList.value = filterList.value
  } else {
    userList.value = initList.value
  }
}

const isShow = (visible) => {
  if (visible) {
    searchName.value = ''
    filterList.value = initList.value
    userList.value = initList.value
  }
}

const emit = defineEmits(['change'])
const modelChange = (val) => {
  if (props.multiple) {
    let itemList = []
    val.forEach((item) => {
      userList.value.forEach((el) => {
        if (item == el.userId) {
          itemList.push({ ...el })
        }
      })
    })
    emit('change', itemList)
  } else {
    let itemObj = userList.value.find((item) => val === item.userId)
    emit('change', itemObj)
  }
}
const selectFilter = (val: string) => {
  if (!val) {
    userList.value = initList.value
    return
  }
  // 使用自定义过滤方法进行搜索
  if (props.filterMethod) {
    filterList.value = initList.value.filter((item) => {
      const labelValue = props?.label ? String(item[props.label as keyof UserItem]) : ''
      const matchesSearch = item?.nickname?.includes(val.trim()) || labelValue.includes(val.trim())
      return matchesSearch && props.filterMethod!(item)
    })
  } else {
    filterList.value = initList.value.filter((item) => {
      if (item?.nickname) {
        return item?.nickname.includes(val.trim())
      } else if (props?.label) {
        const labelValue = String(item[props.label as keyof UserItem])
        return labelValue.includes(val.trim())
      }
      return false
    })
  }
  userList.value = filterList.value
}
// watchEffect(() => {
//   if (model.value) {
//     modelChange(model.value)
//   }
// })
/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style lang="scss">
.header_input {
  border-bottom: 1px solid #eee;
  padding: 5px 10px 10px;
}

.empty_box {
  color: #666;
  text-align: center;
  line-height: 50px;
}

.option_box {
  overflow: auto;
  max-height: 200px;
}

.eplus-popper {
  min-width: 200px;
}
/* stylelint-disable-next-line rule-empty-line-before */
.deptName {
  padding-left: 12px;
  display: block;
  width: 100%;
  box-sizing: border-box;
  line-height: 30px;
  font-size: 14px;
}
/* stylelint-disable-next-line rule-empty-line-before */
.line {
  width: 100%;
  height: 1px;
  background-color: #eee;
  margin: 5px 0;
}
</style>
