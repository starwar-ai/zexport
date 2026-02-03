<template>
  <el-form
    v-loading="props.loading"
    :model="formData"
    label-position="left"
    require-asterisk-position="right"
    ref="formRef"
    :style="props.scrollFlag ? 'height: 100%; overflow: hidden' : ''"
  >
    <slot></slot>
    <div
      ref="pageTopRef"
      :style="topHeight > 0 ? { height: `${topHeight}px` } : {}"
    >
      <el-scrollbar always>
        <slot name="pageTop"></slot>
      </el-scrollbar>
    </div>
    <div
      v-if="topHeight > 0"
      style="height: 54px"
    >
      <slot name="pageBottomTabs"></slot>
    </div>
    <div
      v-if="topHeight > 0"
      :style="{
        height: `${bottomHeight - 54}px` || '50%'
      }"
    >
      <slot name="pageBottom"></slot>
    </div>
  </el-form>
</template>

<script setup lang="tsx">
import { EplusFormMode } from '../index'
import { EplusAuditable } from '@/types/eplus'
import { EplusLocalSaveButton, EplusSaveButton, EplusSubmitButton } from '@/components/EplusButton'
import { throttle } from 'lodash-es'

const message = useMessage()
const formRef = ref()

const formData = defineModel<EplusAuditable>()

const validate = () => {
  return formRef.value.validate((valid) => {
    if (valid) {
      return true
    } else {
      message.warning('表单信息填写信息不完整!')
      return false
    }
  })
}
const validateField = (field) => {
  return formRef.value.validateField(field, (valid) => {
    if (valid) {
      return true
    } else {
      return false
    }
  })
}
const resetForm = () => {
  formRef.value.resetFields()
}
const clearValidate = (Validate?) => {
  formRef.value.clearValidate(Validate)
}

const { updateDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
}

const props = withDefaults(
  defineProps<{
    id?: number
    mode?: EplusFormMode
    loading?: boolean
    localSaveAction?: {
      textContent?: string
      handler: (auditable: EplusAuditable) => void
    }
    saveAction?: {
      permi: string | Array<string>
      textContent?: string
      handler: (auditable: EplusAuditable) => void
    }
    submitAction?: {
      permi: string | Array<string>
      textContent?: string
      handler: (auditable: EplusAuditable) => void
    }
    closeAction?: { handler: (auditable: EplusAuditable) => void }
    scrollFlag?: boolean
    mainRatio?: number
  }>(),
  {
    loading: true,
    scrollFlag: false,
    mainRatio: 5
  }
)

updateDialogActions(
  <EplusSaveButton
    type="primary"
    isShow={props.saveAction ? true : false}
    v-hasPermi={props.saveAction?.permi}
    textContent={props.saveAction?.textContent}
    onClick={throttle(props.saveAction ? props.saveAction?.handler : () => {}, 1000, {
      leading: true
    })}
    auditable={formData}
  />,
  <EplusSubmitButton
    isShow={props.submitAction ? true : false}
    v-hasPermi={props.submitAction?.permi}
    onClick={throttle(props.submitAction ? props.submitAction?.handler : () => {}, 1000, {
      leading: true
    })}
    textContent={props.submitAction?.textContent}
    auditable={formData}
    key="formSubmit"
  />,
  <EplusLocalSaveButton
    isShow={props.localSaveAction && props.mode == 'create' ? true : false}
    textContent={props.localSaveAction?.textContent}
    onClick={throttle(props.localSaveAction ? props.localSaveAction?.handler : () => {}, 1000, {
      leading: true
    })}
  />
)

const pageTopRef = ref()
const topHeight = ref(0)
const bottomHeight = ref<number>(0)

const setPageHeight = () => {
  nextTick(() => {
    setTimeout(() => {
      const pageTopHeight = pageTopRef.value?.clientHeight
      const pageHeight = formRef.value.$el.offsetHeight
      if (pageTopHeight >= Math.ceil((pageHeight / 10) * props.mainRatio)) {
        topHeight.value = Math.ceil((pageHeight / 10) * props.mainRatio)
      } else {
        topHeight.value = pageTopHeight
      }
      bottomHeight.value = pageHeight - topHeight.value
    })
  })
}

defineExpose({
  resetForm,
  validate,
  formData,
  clearValidate,
  validateField,
  bottomHeight
})

onMounted(() => {
  setPageHeight()
})

defineOptions({ name: 'EplusForm' })
</script>
