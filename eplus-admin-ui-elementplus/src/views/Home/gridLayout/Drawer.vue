<template>
  <el-drawer
    v-model="visible"
    :show-close="false"
    :modal="true"
  >
    <template #header="{ close, titleId, titleClass }">
      <h4
        :id="titleId"
        :class="titleClass"
        class="addCard"
        >小组件列表</h4
      >
      <el-icon
        class="el-icon--left"
        @click="close"
      >
        <Close />
      </el-icon>
    </template>
    <div class="card-template">
      <div
        class="card-item"
        v-for="(card, index) in cardList"
        :key="index"
      >
        <div
          class="card-img-box"
          @click="handleAddCard(card)"
        >
          <img
            class="card-img"
            :src="card?.picture?.fileUrl ? prefixUrl + card.picture.fileUrl : card6"
          />
        </div>

        <p class="card-title">{{ card.title }}</p>
        <p class="card-description">{{ card.description }}</p>
      </div>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { ElDrawer, ElMessageBox } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import card6 from '@/assets/imgs/home/card6.png'
import * as HomeApi from '@/api/home'
import { useUserStore } from '@/store/modules/user'

const visible = ref(false)
const message = useMessage()
const userRoles = useUserStore().getRoles
const prefixUrl = `${import.meta.env.VITE_BASE_URL}/admin-api`

const props = defineProps<{}>()
const emit = defineEmits(['addCard'])
const ruleFormRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  comp: [{ required: true, message: '请输入组件名称', trigger: 'blur' }],
  title: [{ required: true, message: '请输入卡片名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入卡片描述', trigger: 'blur' }]
})
const form = reactive({
  title: '',
  description: '',
  picture: {
    id: 0,
    name: '',
    fileUrl: '',
    mainFlag: 0
  },
  status: 0,
  uniqueCode: Math.floor(Date.now() / 1000),
  tabId: 9999, // 设置一个页签id，当前页签数据用于右侧模板展示
  x: 0,
  y: 4,
  h: 10,
  w: 4,
  i: 1,
  filterCondition: '',
  type: '', // 类型
  comp: '', // 组件
  titleFlag: false
})

const cardList = ref([])

const handleAddCard = (data) => {
  ElMessageBox.alert(`确认当前页面中新增  ${data.title}  组件？`, '提示', {
    confirmButtonText: '是',
    callback: (action: Action) => {
      emit('configCard', data)
      visible.value = false
    }
  })
}

const openDrawer = () => {
  visible.value = true
}

const getCardList = async () => {
  const ids = userRoles.map((obj) => obj.id)
  cardList.value = await HomeApi.getHomeBaseList({ ids: ids.join(',') })
}

// 监听变化动态计算高度和绘制网格线
// watch([col, rowH, gutter, layoutdata], () => {
//   calcWidth()
//   if (!isDraging.value) calcLayoutHeight() // 拖拽中不使用layoutdata计算高度
//   drawGrid()
// })

onMounted(() => {
  getCardList()
})

defineExpose({
  openDrawer
})
</script>

<style scoped lang="scss">
::v-deep .el-drawer__header {
  margin-bottom: 10px !important;
}

::v-deep .el-overlay {
  background-color: #fff !important;
}

.card-template {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;

  .card-item {
    width: 42%;

    .card-img-box {
      padding: 5px;
      height: 100px;
      width: 90%;
      border: 1px solid #ccc;
      border-radius: 6px;

      .card-img {
        width: 100%;
        height: 100%;
        // object-fit: cover;
      }
    }

    .card-title {
      font-weight: 900;
      line-height: 3px;
      font-size: 14px;
    }

    .card-description {
      // line-height: 3px;
      font-size: 13px;
      color: #686b6d;
    }
  }
}

.addCard {
  color: #409eff;
  cursor: pointer;
}
</style>
