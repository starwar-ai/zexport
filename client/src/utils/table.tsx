import { TableColumn } from '@/components/Table/src/types'
import { formatDate } from '@/utils/formatTime'
import { formatCurrency } from '@/utils/formatNumber'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { getDictLabel } from './dict'
import { formatterPrice } from '@/utils/index'

export const formatDictColumn = (dictType: string, isParse?: boolean) => {
  // @ts-ignore
  return (row: Recordable, column: TableColumn, value: any) => {
    // if (!value) {
    //   return ''
    // }
    const valueList = isParse ? JSON.parse(value) : value
    if (Array.isArray(valueList)) {
      return valueList.map((c) => {
        return getDictLabel(dictType, c)
      })
    }
    return getDictLabel(dictType, valueList)
  }
}

// 数字格式化
export const formatNumColumn = (precision: number = 2, keepTrailingZeros: boolean = false) => {
  // @ts-ignore
  return (row: Recordable, column: TableColumn, value: any) => {
    if (value) {
      return formatterPrice(value, precision, keepTrailingZeros)
        .toString()
        .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')
    } else {
      return 0
    }
  }
}

// 金额格式化
export const formatMoneyColumn = (precision?: number, keepTrailingZeros?: boolean) => {
  // @ts-ignore
  return (row: Recordable, column: TableColumn, value: any) => {
    if (precision !== undefined) {
      return keepTrailingZeros !== undefined ? (
        <EplusMoneyLabel val={value} precision={precision} keepTrailingZeros={keepTrailingZeros} />
      ) : (
        <EplusMoneyLabel val={value} precision={precision} />
      )
    }
    return <EplusMoneyLabel val={value} />
  }
}
// 重量格式化
export const formatWeightColumn = (onlyVal = false) => {
  // @ts-ignore
  return (row: Recordable, column: TableColumn, value: any) => {
    if (onlyVal) {
      return value?.weight
        ? `${formatterPrice(value?.weight, 2)
            .toString()
            .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')}`
        : '-'
    } else {
      return value?.weight
        ? `${formatterPrice(value?.weight, 2)
            .toString()
            .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')} ${value?.unit}`
        : '-'
    }
  }
}
/**
 * element plus 的时间 Formatter 实现，使用 YYYY-MM-DD HH:mm:ss 格式
 *
 * @param row 行数据
 * @param column 字段
 * @param cellValue 字段值
 */
// @ts-ignore
export const formatDateColumn = (format?: string = 'YYYY-MM-DD') => {
  // @ts-ignore
  return (row: Recordable, column: TableColumn, value: any) => {
    // if (!value) {
    //   return ''
    // }
    if (value) {
      return formatDate(value, format)
    } else {
      return '-'
    }
  }
}

/**
 * element plus 的时间 Formatter 实现，使用 YYYY-MM-DD HH:mm:ss 格式
 *
 * @param row 行数据
 * @param column 字段
 * @param cellValue 字段值
 */
// @ts-ignore
export const formatDateTimeColumn = (format?: string) => {
  // @ts-ignore
  return (row: Recordable, column: TableColumn, value: any) => {
    if (!value) {
      return ''
    }
    return formatDate(value, format)
  }
}

export const formatCurrColumn = (format?: string) => {
  // @ts-ignore
  return (row: Recordable, column: TableColumn, value: any) => {
    if (!value) {
      return ''
    }
    return formatCurrency(value, format)
  }
}

//表格长度配置
export const columnWidth = {
  xs: '50px',
  s: '60px',
  m: '80px',
  l: '120px',
  xl: '150px',
  xxl: '200px'
}
