import Search from './src/Search.vue'
import { FormSchema, FormSetPropsType } from '@/types/form'

export type { SearchProps } from './src/types'

export interface SearchExpose {
  setValues: (data: Recordable) => void
  setProps: (props: Recordable) => void
  delSchema: (field: string) => void
  addSchema: (formSchema: FormSchema, index?: number) => void
  setSchema: (schemaProps: FormSetPropsType[]) => void
  formModel: Recordable
}

export { Search }
