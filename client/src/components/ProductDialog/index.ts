import ProductDialog from './src/ProductDialog.vue'
import { JSX } from 'vue/jsx-runtime'

type EplusFormTableSchema = {
  field?: string
  label: string
  width?: number | string
  minWidth?: number | string
  component?: JSX.Element
  default?: any
  formatter?: any
  slot?: any
}
export { ProductDialog, EplusFormTableSchema }
