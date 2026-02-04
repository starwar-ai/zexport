<template>
  <div :class="[prefixCls]">
    <Dialog
      ref="DialogRef"
      model-class="model-class"
      v-model="dialogVisible"
      fullscreen
      :destroy-on-close="props.destroyOnClose"
      :title="title"
      @close="close"
    >
      <slot></slot>
      <slot
        name="detail"
        :key="key"
        v-if="dialogType === 'detail'"
      ></slot>

      <slot
        name="create"
        :key="key"
        v-if="dialogType === 'create'"
      ></slot>

      <slot
        name="edit"
        :key="key"
        :param="param"
        v-if="dialogType === 'edit'"
      ></slot>

      <slot
        name="inspect"
        :key="key"
        :param="param"
        v-if="dialogType === 'inspect'"
      ></slot>

      <!-- 验货  -->
      <slot
        name="inspection"
        :key="key"
        :param="param"
        v-if="dialogType === 'inspection'"
      ></slot>
      <!-- 返工重验 -->
      <slot
        name="reworkInspection"
        :key="key"
        :param="param"
        v-if="dialogType === 'reworkInspection'"
      ></slot>

      <!-- 让步放行 -->
      <slot
        name="releaseInspection"
        :key="key"
        :param="param"
        v-if="dialogType === 'releaseInspection'"
      ></slot>

      <template #footer>
        <component
          :is="action"
          v-for="(action, index) in dialogActions"
          :key="index"
          v-show="action.props.isShow"
        />
        <eplus-button @click="close">关闭</eplus-button>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="tsx">
import { useDesign } from '@/hooks/web/useDesign'
import { EplusDialogType } from '@/types/eplus.d'
import { JSX } from 'vue/jsx-runtime'
import { propTypes } from '@/utils/propTypes'

const message = useMessage()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('eplus-dialog')
const props = defineProps({
  destroyOnClose: propTypes.bool.def(true),
  beforeClose: propTypes.string.def('')
})
const DialogRef = ref()
const title = ref('')
const key = ref<number>()
const param = ref()

const dialogType = ref<EplusDialogType>()

const dialogActions = ref<JSX.Element[]>([])

function updateDialogActions(...args: any[]) {
  dialogActions.value.unshift(...args)

  dialogActions.value.forEach((e) => {
    if (e.key == 'approve') {
      if (e.props.auditable.auditStatus == 1 && e.props.auditable.qualityInspectionStatus == 1) {
        e.props.isShow = true
      } else e.props.isShow = false
    }
  })
}
function clearDialogActions(key?) {
  if (key) {
    dialogActions.value = dialogActions.value.filter((item) => item.key !== key)
  } else {
    dialogActions.value.length = 0
  }
}
const emit = defineEmits(['success', 'failure'])
const dialogVisible = ref<boolean>(false)

const closeForm = () => {
  if (props.beforeClose === 'changed') {
    message
      .confirm('是否关闭新增页面？')
      .then(() => {
        close()
      })
      .catch(() => {})
  } else {
    close()
  }
}
const close = async () => {
  dialogVisible.value = false
  emit('success')
}
const openEdit = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  dialogType.value = 'edit'
  title.value = sectionName ? '编辑' + sectionName : '编辑'
  key.value = id
  param.value = params
  dialogVisible.value = true
}
const openDetail = (id: number, sectionName = '') => {
  clearDialogActions()
  dialogType.value = 'detail'
  title.value = sectionName ? sectionName + '详情' : '详情'
  key.value = id
  dialogVisible.value = true
}
const openCreate = (sectionName = '', id = 0) => {
  clearDialogActions()
  dialogType.value = 'create'
  title.value = sectionName ? '新增' + sectionName : '新增'
  if (id > 0) {
    key.value = id
  }
  dialogVisible.value = true
}

const open = (sectionName = '', id = 0) => {
  clearDialogActions()
  dialogType.value = undefined
  title.value = sectionName
  if (id > 0) {
    key.value = id
  }
  dialogVisible.value = true
}

// 确认验货单
const openInspect = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  dialogType.value = 'inspect'
  title.value = sectionName ? '确认' + sectionName : '确认'
  key.value = id
  param.value = params
  dialogVisible.value = true
}
// 从detail转到编辑验货单
const goInspection = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  dialogType.value = 'inspection'
  title.value = sectionName ? '验货' + sectionName : '验货'
  key.value = id
  param.value = params
  dialogVisible.value = true
}

// 从detail转到编辑确认验货单
const openConfirmInspect = (sectionName) => {
  clearDialogActions()
  title.value = sectionName ? '确认' + sectionName : '确认'
  if (key.value) dialogType.value = 'inspect'
}

// 从detail转到返工验货单
const goReworkInspection = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  title.value = sectionName ? sectionName + '验货单' : '返工'
  dialogType.value = 'reworkInspection'
  key.value = id
  param.value = params
  dialogVisible.value = true
}

// 从detail转到让步放行
const goReleaseInspection = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  title.value = sectionName ? sectionName + '' : '验货单'
  dialogType.value = 'releaseInspection'
  key.value = id
  param.value = params
  dialogVisible.value = true
}

//从detail转到编辑
const goEdit = (sectionName?) => {
  clearDialogActions()
  title.value = sectionName ? '编辑' + sectionName : '编辑'
  if (key.value) dialogType.value = 'edit'
}

provide('dialogActions', {
  dialogActions,
  updateDialogActions,
  clearDialogActions
})
provide('dialogEmits', {
  close,
  goEdit,
  openConfirmInspect,
  goInspection,
  goReworkInspection,
  goReleaseInspection
})

defineExpose({
  openDetail,
  openCreate,
  openEdit,
  close,
  open,
  openInspect,
  openConfirmInspect,
  goInspection,
  goReworkInspection,
  goReleaseInspection // 让步放行
})

onMounted(() => {
  // console.log('EplusDialog onMounted')
})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-eplus-dialog;

.#{$prefix-cls} {
  :deep(.el-overlay),
  :deep(.el-overlay-dialog) {
    position: absolute;
  }

  :deep(.el-dialog) {
    display: flex;
    flex-direction: column;
  }

  :deep(.is-fullscreen) {
    margin: 15px;
    height: calc(100% - 30px);
    width: calc(100% - 30px);
  }

  :deep(.el-dialog__body) {
    flex: 1 1 auto;
    overflow: auto;
  }
}
</style>
