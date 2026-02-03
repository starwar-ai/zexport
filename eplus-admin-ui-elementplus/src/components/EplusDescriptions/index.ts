import EplusDescriptions from './src/EplusDescriptions.vue'
type EplusDescriptionItemSchema = {
  precision?: number | undefined
  field: string
  label: string
  dictType?: string
  dateFormat?: string
  span?: number
  slotName?: string
  disabled?: boolean
  isCompare?: boolean | string
  type?: string
  formatter?: any
}

export { EplusDescriptionItemSchema, EplusDescriptions }
