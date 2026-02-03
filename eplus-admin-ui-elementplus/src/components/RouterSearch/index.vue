<template>
  <ElDialog
    v-if="isModal"
    v-model="showSearch"
    :show-close="false"
    title="菜单搜索"
  >
    <el-select
      filterable
      :reserve-keyword="false"
      remote
      placeholder="请输入菜单内容"
      :remote-method="remoteMethod"
      style="width: 100%"
      @change="handleChange"
    >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </ElDialog>
  <div
    v-else
    class="flex cursor-pointer items-center justify-center"
    @click.stop="searchClick"
    style="height: 64px"
  >
    <div
      v-if="!showTopSearch"
      class="flex items-center justify-center"
      style="flex-direction: column"
    >
      <Icon
        icon="ep:search"
        style="height: 20px; color: #bfcbd9"
      />
      <div
        v-show="!showTopSearch"
        style="color: #bfcbd9; font-size: 14px; margin-top: 5px; margin-bottom: 14px"
        >搜索
      </div>
    </div>
    <div
      v-else
      class="flex cursor-pointer items-center justify-center"
    >
      <Icon
        icon="ep:search"
        style="color: #bfcbd9"
      />
      <el-select
        filterable
        :reserve-keyword="false"
        remote
        placeholder="请输入菜单内容"
        :remote-method="remoteMethod"
        class="w-140px ml2 overflow-hidden transition-all-600"
        @change="handleChange"
      >
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/store/modules/app'

const props = defineProps({
  isModal: {
    type: Boolean,
    default: true
  }
})
const router = useRouter() // 路由对象
const showSearch = ref(false) // 是否显示弹框
const showTopSearch = ref(false) // 是否显示顶部搜索框
const value: Ref = ref('') // 用户输入的值

const routers = router.getRoutes()
const options = computed(() => {
  // 提示选项
  if (!value.value) {
    return []
  }
  const list = routers.filter((item: any) => {
    if (item.meta.title?.indexOf(value.value) > -1 && !item.meta.filterFlag) {
      return true
    }
  })
  return list.map((item) => {
    return {
      label: `${item.meta.title}`,
      value: item.path
    }
  })
})

function remoteMethod(data) {
  // 这里可以执行相应的操作（例如打开搜索框等）
  value.value = data
}

function handleChange(path) {
  router.push({ path })
  hiddenTopSearch()
}

function hiddenTopSearch() {
  // showTopSearch.value = false
}

onMounted(() => {
  window.addEventListener('keydown', listenKey)
  window.addEventListener('click', hiddenTopSearch)
})

onUnmounted(() => {
  window.removeEventListener('keydown', listenKey)
  window.removeEventListener('click', hiddenTopSearch)
})

const appStore = useAppStore()
watch(
  () => appStore.collapse,
  (val) => {
    showTopSearch.value = !val
  },
  {
    immediate: true
  }
)
const emit = defineEmits(['updateCollapse'])
const searchClick = () => {
  showTopSearch.value = !showTopSearch.value
  emit('updateCollapse', !showTopSearch.value)
}
// 监听 ctrl + k
function listenKey(event) {
  if ((event.ctrlKey || event.metaKey) && event.key === 'm') {
    showSearch.value = !showSearch.value
    // 这里可以执行相应的操作（例如打开搜索框等）
  }
}

defineExpose({
  openSearch: () => {
    showSearch.value = true
  }
})
</script>
