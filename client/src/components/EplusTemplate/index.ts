import EplusList from './src/EplusList.vue'
import EplusDetail from './src/EplusDetail.vue'
import EplusForm from './src/EplusForm.vue'
import EplusFormItems from './src/EplusFormItems.vue'
import { FormItemRule } from 'element-plus/es/components/form'
import EplusFormTable from './src/EplusFormTable.vue'
import EplusFormTableQuote from './src/EplusFormTableQuote.vue'
import EplusOperateLog from './src/EplusOperateLog.vue'
import EplusImgEnlarge from './src/EplusImgEnlarge.vue'
import EplusPageDetail from './src/EplusPageDetail.vue'
import EplusDesc from './src/EplusDesc.vue'

import { JSX } from 'vue/jsx-runtime'

interface EplusDetailExpose {
  getActions: () => any // Promise<JSX.Element[]>
}

type Arrayable<T> = T | T[]
type EplusFormMode = 'create' | 'edit' | 'change' | 'copy'
type EplusFormSchema = {
  field: string
  label: string
  dictType?: string
  dateFormat?: string
  editable?: boolean
  span?: number
  component?: any
  rules?: Arrayable<FormItemRule>
  disabled?: boolean
  readOnly?: boolean
  labelWidth?: number | string
  formatter?: any
  hint?: string
}

type EplusFormTableSchema = {
  field?: string
  label?: string
  width?: number | string
  minWidth?: number | string
  component?: JSX.Element
  default?: any
  formatter?: any
  slot?: (
    item: EplusFormTableSchema,
    row: Recordable,
    index: number,
    deleteAction: (index: number) => void
  ) => JSX.Element
  rules?: Arrayable<FormItemRule>
  fixed?: 'left' | 'right'
  disabled?: boolean
  hint?: string
}

export {
  EplusList,
  EplusDetail,
  EplusDetailExpose,
  EplusFormSchema,
  EplusFormTableSchema,
  EplusFormMode,
  EplusForm,
  EplusFormItems,
  EplusFormTable,
  EplusFormTableQuote,
  EplusOperateLog,
  EplusImgEnlarge,
  EplusPageDetail,
  EplusDesc
}
