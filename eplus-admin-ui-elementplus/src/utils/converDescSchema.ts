import { ComponentName } from '@/types/components'
import { DescriptionsSchema } from '@/types/descriptions'
import { FormSchema } from '@/types/form'
import { JSX } from 'vue/jsx-runtime'
import { getDictOptions } from './dict'
import { TableColumn } from '@/components/Table'

export interface DetailItem {
  l?: string
  f?: string
  v?: Function | JSX.Element | JSX.Element[] | string | number | any
  w?: string | number
  exts?: any
}
export interface DetailInfo {
  title: string
  list: DetailItem[]
  column?: number
  border?: boolean
}
export interface SimpleFormItem extends FormSchema {
  l?: string //label
  lm?: string //labelMessage
  f?: string //field,
  v?: string //value,
  p?: string //placeholder,
  n?: ComponentName //组件名 component ，默认 Input
  c?: string //class
  cp?: Recordable //组件详细属性  componentProps
  fp?: Recordable //formItemProps
  o?: Function | Recordable[] //options,
  dict?: string //字典类型
}
export const convertToDescSchema = (items: DetailItem[]): DescriptionsSchema[] => {
  const r = [] as DescriptionsSchema[]
  items.forEach((item) => {
    //可写原始的 DescriptionsSchema , 也可写简化版的
    const b = { ...item, field: item.f || '', label: item.l } as DescriptionsSchema
    if (item.v)
      b.slots = {
        default: (d) => {
          return typeof item.v == 'function' ? item.v(d) : item.v
        }
      }
    r.push(b)
  })
  return r
}

/**
 * 转换数据字典到FormSchema
 * @param dict 一个 object key 为字段名
 * @param fields 字段名数组 留空则为全部字段
 * @returns FormSchema
 */
export const convertDictToFormSchema = (dict: Recordable, fields: string[] = []): FormSchema[] => {
  if (!fields.length) {
    fields = Object.keys(dict)
  }
  const r = [] as Recordable[]
  fields.forEach((f) => {
    if (dict[f]) r.push({ ...dict[f], f })
  })
  return convertToFormSchema(r)
}

/**
 * 提取字典中所有字段的rule
 * @param dict 一个 object key 为字段名
 * @param fields 字段名数组 留空则为全部字段
 * @returns 提取的规则集合
 */
export const convertDictToRules = (dict: Recordable, fields: string[] = []): Recordable => {
  if (!fields.length) {
    fields = Object.keys(dict)
  }
  const r = {} as Recordable
  fields.forEach((f) => {
    if (dict[f].rules) r[f] = dict[f].rules
  })
  return r
}

export const convertToFormItemSchema = (item: Recordable): FormSchema => {
  const b = {
    // 唯一值
    field: item.field || item.f || '',
    // 标题
    label: item.label || item.l,
    // 提示
    labelMessage: item.labelMessage || item.lm,
    // col组件属性
    colProps: item.colProps,
    // 表单组件属性，slots对应的是表单组件的插槽，规则：${field}-xxx，具体可以查看element-plus文档
    componentProps: item.componentProps || item.cp,
    // formItem组件属性
    formItemProps: item.formItemProps || item.fp,
    // 渲染的组件
    component:
      item.component ||
      item.n ||
      (item.fs ? 'Autocomplete' : item.options || item.dict ? 'Select' : 'Input'),
    // 初始值
    value: item.value || item.v,
    // 是否隐藏
    hidden: item.hidden,
    ...item
  } as FormSchema & Recordable
  if (!b.componentProps) b.componentProps = {}
  if (!b.formItemProps) b.formItemProps = {}
  if (item.class) b.componentProps.class = item.class
  if (item.placeholder) b.componentProps.placeholder = item.placeholder
  if (item.options)
    b.componentProps.options = typeof item.options == 'function' ? item.options() : item.options
  if (item.maxlength) b.componentProps.maxlength = item.maxlength
  if (item.disabled) b.componentProps.disabled = item.disabled
  if (item.ro) b.componentProps.readonly = item.ro
  if (item.dict) b.componentProps.options = getDictOptions(item.dict)
  if (item.rules) b.formItemProps.rules = item.rules
  if (b.formItemProps.rules) {
    b.formItemProps.rules.forEach((r) => {
      if (!r.message) r.message = r.required ? b.label + '不能为空' : '请输入正确格式的' + b.label
    })
  }
  if (item.fs) b.componentProps.fetchSuggestions = item.fs
  if (item.slots) {
    b.formItemProps.slots = item.slots
    //自定义内容
    delete b.component
  }
  return b
}

/**
 * 转换简写schema到完整正式FormSchema
 * @param items 简写的schema
 * @returns 正式FormSchema
 */
export const convertToFormSchema = (items: Recordable[]): FormSchema[] => {
  return items.map((item) => {
    return convertToFormItemSchema({ ...item })
  })
}

export const convertToTableSchema = (items: Recordable[]): TableColumn[] => {
  const r = [] as TableColumn[]
  items.forEach((item) => {
    const b = {
      ...item,
      field: item.field || item.f || '',
      label: item.label || item.l,
      type: item.type || item.t,
      index: item.index || item.i,
      width: item.width || item.w,
      minWidth: item.minWidth || item.mw,
      align: item.align || item.a,
      headerAlign: item.headerAlign || item.ha,
      className: item.className || item.cn,
      labelClassName: item.labelClassName || item.lcn
    } as TableColumn
    'f,l,t,i,w,mw,a,ha,cn,lcn'.split(',').forEach((c) => {
      b[c] && delete b[c]
    })
    r.push(b)
  })
  return r
}
