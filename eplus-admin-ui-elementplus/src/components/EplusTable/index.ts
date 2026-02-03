import EplusTable from './src/EplusTable.vue'
type EplusRow = { id: string | number } & Record<string, any>

type EplusColumn = {
  prop: string
  label?: string
  width?: string | number
  align?: string
  fixed?: 'left' | 'right'
  sortable?: boolean
  parent?: boolean
  collapse?: boolean
  hide?: boolean
  items?: EplusColumn[]
  direction?: string
  tooltip?: string
  extend?: Extend[]

  subcols?: EplusColumn[]
  formatter?: (row: EplusRow, col: EplusColumn) => string
}

type Extend = {
  id?: string
  prop: string
  label?: string
}

type EplusColumnConfig = {
  prop: string
  parent?: boolean
  label: string
  fixed?: 'left' | 'right' | boolean
  width?: string | number
  hide?: boolean
  isSearched?: boolean
  disable?: boolean
  element?: any
}

type EplusCheckbox = {
  index: number
  checked: boolean
}

export { EplusTable, EplusRow, EplusColumn, EplusColumnConfig, EplusCheckbox }
