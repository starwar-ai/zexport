import { BpmProcessInstanceResultEnum } from './bpm.d'
import { TableColumn } from '@types/table.d'
import { SearchSchema } from '@types/search.d'

export type EplusAuditable = {
  id?: number
  processInstanceId?: string
  creatorName?: string
  applyerId?: number
  auditStatus?: BpmProcessInstanceResultEnum
} & Recordable

export type EplusRow = { id: string | number } & Record<string, any>

export type EplusColumn = {
  prop: string
  label?: string
  minWidth?: string | number
  align?: string
  fixed?: 'left' | 'right' | boolean
  disable?: boolean
  sortable?: boolean | string
  parent?: boolean
  isCopy?: boolean
  summary?: boolean
  collapse?: boolean
  hide?: boolean
  isTooltip?: boolean
  items?: EplusColumn[]
  direction?: string
  tooltip?: string
  extend?: Extend[]
  subcols?: EplusColumn[]
  slots?: {
    default?: (...args: any[]) => JSX.Element | JSX.Element[] | null
    header?: (...args: any[]) => JSX.Element | null
  }
  formatter?: (row: EplusRow, col: EplusColumn, value: any) => string
}

export type EplusColumnConfig = {
  prop: string
  parent?: boolean
  label: string
  fixed?: 'left' | 'right' | boolean
  minWidth?: string | number
  hide?: boolean
  disable?: boolean
}

export type EplusDialogType = 'create' | 'edit' | 'detail' | 'change' | 'confirm' | 'copy'

export type EplusButtonSchema = {
  permi: string
  user: object
  handler: (auditable?: EplusAuditable) => void
}

export type EplusCancelButtonSchema = {
  permi: string
}

export type TabObj = {
  label: string
  isTree?: boolean
  isDefault?: boolean
  columns: TableColumn[]
  selection?: boolean
  summary?: boolean
  picture?: boolean
}

export type EplusTableSchema = {
  getListApi: (searchParam: any) => Promise<any>
  delListApi: (id: number) => Promise<any>
  exportListApi?: (params: any) => Promise<any>
  selection: Boolean | string
  columns?: TableColumn[]
  summary: Boolean
  showTabs: Boolean
  tabs?: TabObj[]
  spanMethod?: (params: any) => { rowspan: number; colspan: number }
  oldChangeField?: string
  showPictures?: boolean
  showSettings?: Boolean
}

export type EplusSearchSchema = {
  searchSchemas: SearchSchema[]
  expenderField: string
}
