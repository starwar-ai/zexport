import { JSX } from 'vue/jsx-runtime'

type SearchFieldSchema = {
  label: string
  name: string
  component: JSX.Element
}
type SearchTableSchema = {
  label: string
  field: string
  minWidth: string
  formatter?: (row: any, column: any, cellValue: any, index: number) => VNode | string
  fixed?: string
}

export type EplusSearchDialogProps = {
  searchFieldSchema: Array<SearchFieldSchema>
  searchApi: (any) => Promise<any>
  searchTableSchema: Array<SearchTableSchema>
}
