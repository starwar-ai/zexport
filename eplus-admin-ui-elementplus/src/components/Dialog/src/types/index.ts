export type DialogProps = {
  closeOnClickModal?: boolean
  modelValue?: boolean
  title?: string
  fullscreen?: boolean
  scroll?: boolean
  maxHeight?: string | number
} & Recordable

export type DialogInitOptions = {
  props?: DialogProps
  slots?: Recordable
} & Recordable
