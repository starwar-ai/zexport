<template>
  <div :class="[prefixCls]">
    <Dialog
      ref="DialogRef"
      model-class="model-class"
      v-model="dialogVisible"
      :close-on-click-modal="false"
      fullscreen
      :destroy-on-close="props.destroyOnClose"
      :title="title"
      @close="close"
      z-index="2000"
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
        name="change"
        :key="key"
        :param="param"
        v-if="dialogType === 'change'"
      ></slot>

      <slot
        name="copy"
        :key="key"
        :param="param"
        v-if="dialogType === 'copy'"
      ></slot>

      <slot
        name="confirm"
        :key="key"
        :param="param"
        v-if="dialogType === 'confirm'"
      ></slot>

      <template #footer>
        <component
          :is="action"
          v-for="(action, index) in dialogActions"
          :key="index"
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
  dialogActions.value.push(...args)
  // 去重
  // dialogActions.value = Array.from(new Set(dialogActions.value.map((item) => item.key))).map(
  //   (key) => {
  //     return dialogActions.value.find((item) => item.key === key)
  //   }
  // )
}
function clearDialogActions(...key) {
  if (key && key.length) {
    dialogActions.value = dialogActions.value.filter((item) => !key.includes(item.key))
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
  if (dialogVisible.value) {
    dialogVisible.value = false
    emit('success')
  }
}
const openEdit = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  dialogType.value = 'edit'
  title.value = sectionName ? sectionName : '编辑'
  key.value = id
  param.value = params
  dialogVisible.value = true
}
const openChange = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  dialogType.value = 'change'
  title.value = sectionName ? sectionName : '变更'
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
  title.value = sectionName ? sectionName : '新增'
  if (id > 0) {
    key.value = id
  }
  dialogVisible.value = true
}
const openConfirm = (id: number, sectionName = '') => {
  clearDialogActions()
  dialogType.value = 'confirm'
  title.value = sectionName ? sectionName : '确认'
  key.value = id
  dialogVisible.value = true
}
const openCopy = (id: number, sectionName = '', params?) => {
  clearDialogActions()
  dialogType.value = 'copy'
  title.value = sectionName ? sectionName : '复制'
  key.value = id
  param.value = params
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
//从detail转到编辑
const goEdit = (sectionName?, params?) => {
  clearDialogActions()
  title.value = sectionName ? sectionName : '编辑'
  param.value = params
  if (key.value) dialogType.value = 'edit'
}

//从detail转到变更
const goChange = (sectionName?, params?) => {
  clearDialogActions()
  title.value = sectionName ? sectionName : '变更'
  param.value = params
  if (key.value) dialogType.value = 'change'
}

//从detail转到确认
const goConfirm = (sectionName?) => {
  clearDialogActions()
  title.value = sectionName ? sectionName : ''
  if (key.value) dialogType.value = 'confirm'
}

provide('dialogActions', {
  dialogActions,
  updateDialogActions,
  clearDialogActions
})
provide('dialogEmits', { close, goEdit, goChange, goConfirm })

defineExpose({ openDetail, openCreate, openEdit, openChange, close, open, openConfirm, openCopy })

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

  :deep(.el-dialog__header) {
    font-size: 16px;
    color: #000;
  }

  :deep(.el-dialog__body) {
    flex: 1 1 auto;
    overflow: auto;
  }
}
</style>
