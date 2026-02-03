import { DICT_TYPE } from '@/utils/dict'

export type EplusSearchFieldSchema = {
  component: any
  label?: string
  name?: string
  nameList?: string
  formatter?: EplusSearchFormatter
  subfields?: EplusSearchSubFieldSchema[]
  className?: string
  selectMultiple?: boolean
}
export type EplusSearchFormatter = string | DICT_TYPE | ((args: any[]) => any)
export type EplusSearchSubFieldSchema = {
  name: string
  nameList?: string
  label: string
  formatter?: EplusSearchFormatter
}
export interface EplusSearchFieldLayout {
  pageKey: string
  version: string
  mainFields: string[]
  moreFields: string[]
  timestamp: number
}

export interface EplusSearchCustomizationConfig {
  enableCustomization?: boolean  // Default: true
  minMainFields?: number        // Default: 1
  maxMainFields?: number        // Default: undefined (no limit)
}

export type EplusSearchSchema = {
  fields: EplusSearchFieldSchema[]
  moreFields?: EplusSearchFieldSchema[]
  columns?: []
  customization?: EplusSearchCustomizationConfig
}

export interface EplusSearchExpose {
  model: Recordable
}
