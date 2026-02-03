import { cloneDeep, omit, toNumber, uniq } from 'lodash-es'
import { isValidArray } from './is'
import { moneyTotalPrecision, weightPrecision } from './config'

/**
 *
 * @param component 需要注册的组件
 * @param alias 组件别名
 * @returns any
 */
export const withInstall = <T>(component: T, alias?: string) => {
  const comp = component as any
  comp.install = (app: any) => {
    app.component(comp.name || comp.displayName, component)
    if (alias) {
      app.config.globalProperties[alias] = component
    }
  }
  return component as T & Plugin
}

/**
 * @param str 需要转下划线的驼峰字符串
 * @returns 字符串下划线
 */
export const humpToUnderline = (str: string): string => {
  return str.replace(/([A-Z])/g, '-$1').toLowerCase()
}

/**
 * @param str 需要转驼峰的下划线字符串
 * @returns 字符串驼峰
 */
export const underlineToHump = (str: string): string => {
  if (!str) return ''
  return str.replace(/\-(\w)/g, (_, letter: string) => {
    return letter.toUpperCase()
  })
}

/**
 * 驼峰转横杠
 */
export const humpToDash = (str: string): string => {
  return str.replace(/([A-Z])/g, '-$1').toLowerCase()
}

export const setCssVar = (prop: string, val: any, dom = document.documentElement) => {
  dom.style.setProperty(prop, val)
}

/**
 * 查找数组对象的某个下标
 * @param {Array} ary 查找的数组
 * @param {Functon} fn 判断的方法
 */
// eslint-disable-next-line
export const findIndex = <T = Recordable>(ary: Array<T>, fn: Fn): number => {
  if (ary.findIndex) {
    return ary.findIndex(fn)
  }
  let index = -1
  ary.some((item: T, i: number, ary: Array<T>) => {
    const ret: T = fn(item, i, ary)
    if (ret) {
      index = i
      return ret
    }
  })
  return index
}

export const trim = (str: string) => {
  return str.replace(/(^\s*)|(\s*$)/g, '')
}

/**
 * @param {Date | number | string} time 需要转换的时间
 * @param {String} fmt 需要转换的格式 如 yyyy-MM-dd、yyyy-MM-dd HH:mm:ss
 */
export function formatTime(time: Date | number | string, fmt: string) {
  if (!time) return ''
  else {
    const date = new Date(time)
    const o = {
      'M+': date.getMonth() + 1,
      'd+': date.getDate(),
      'H+': date.getHours(),
      'm+': date.getMinutes(),
      's+': date.getSeconds(),
      'q+': Math.floor((date.getMonth() + 3) / 3),
      S: date.getMilliseconds()
    }
    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
    }
    for (const k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {
        fmt = fmt.replace(
          RegExp.$1,
          RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
        )
      }
    }
    return fmt
  }
}

const currencyConfig = [
  { key: 'USD', symbol: '$' },
  { key: 'EUR', symbol: '€' },
  { key: 'CZK', symbol: 'Kč' },
  { key: 'GBP', symbol: '£' },
  { key: 'CAD', symbol: 'C$' },
  { key: 'AUD', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'NZD', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'JPY', symbol: 'Ұ' },
  { key: 'RMB', symbol: '￥' },
  { key: 'CNY', symbol: '￥' },
  { key: 'HKD', symbol: 'HK$' },
  { key: 'MOP', symbol: 'MOP$' },
  { key: 'TWD', symbol: 'NT$' }, // 注意：实际为新台币
  { key: 'INR', symbol: '₹' },
  { key: 'KRW', symbol: '₩' },
  { key: 'THB', symbol: '฿' },
  { key: 'SGD', symbol: 'S$' },
  { key: 'VND', symbol: '₫' },
  { key: 'MYR', symbol: 'RM' },
  { key: 'PHP', symbol: '₱' },
  { key: 'IDR', symbol: 'Rp' },
  { key: 'MXN', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'BRL', symbol: 'R$' },
  { key: 'ZAR', symbol: 'R' }, // 注意：可能与巴西雷亚尔等混淆，需结合上下文
  { key: 'ARS', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'COP', symbol: '$' }, // 注意：可能与美元等混淆，需结合上下文
  { key: 'PEN', symbol: 'S/' }, // 注意：秘鲁索尔的符号后通常跟一个数值
  { key: 'PLN', symbol: 'zł' },
  { key: 'RUB', symbol: '₽' }
]
export function getCurrency(val: string) {
  let amountSymbol = ''
  const str = val.replace(/[^a-zA-Z]/g, '')
  currencyConfig.forEach((item) => {
    if (item.key === str) {
      amountSymbol = item.symbol
    }
  })
  return amountSymbol
}

export function convertNum(str: string) {
  return Number(str.replace(/,/g, ''))
}

export interface CurrencySummaryItem {
  currency?: string
  amount?: number | string | null
}



export function currencyJsonAnalysis(
  list: CurrencySummaryItem[] | null | undefined,
  joiner = ', '
): string {
  if (!isValidArray(list)) {
    return ''
  }
  const normalized = list
    .map((item) => {
      if (!item || !item.currency) return null
      const amountNumber = Number(item.amount || 0)
      if (Number.isNaN(amountNumber)) {
        return null
      }
      return {
        currency: String(item.currency).toUpperCase(),
        amount: amountNumber
      }
    })
    .filter((item): item is { currency: string; amount: number } => item !== null)

  if (normalized.length === 0) {
    return ''
  }

  const nonZeroItems = normalized.filter((item) => item.amount !== 0)
  if (nonZeroItems.length === 0) {
    return '0'
  }
  return nonZeroItems
    .map((item) => {
      const symbol = getCurrency(item.currency)
      const formattedAmount = formatNum(item.amount, moneyTotalPrecision)
      return `${symbol || item.currency} ${formattedAmount}`
    })
    .join(joiner)
}

export function formatNum(num: number | string, decimal = 3, keepTrailingZeros = false) {
  return formatterPrice(num, decimal, keepTrailingZeros)
    .toString()
    .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')
}

export function formatWeight(val: any, onlyVal = false) {
  if (onlyVal) {
    return val?.weight
      ? `${formatterPrice(val?.weight, weightPrecision)
          .toString()
          .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')} `
      : '-'
  } else {
    return val?.weight
      ? `${formatterPrice(val?.weight, weightPrecision)
          .toString()
          .replace(/(\d)(?=(\d{3})+(?!\d))(?<!\.\d*)/g, '$1,')} ${val?.unit}`
      : '-'
  }
}

/**
 * 生成随机字符串
 */
export function toAnyString() {
  const str: string = 'xxxxx-xxxxx-4xxxx-yxxxx-xxxxx'.replace(/[xy]/g, (c: string) => {
    const r: number = (Math.random() * 16) | 0
    const v: number = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString()
  })
  return str
}

/**
 * 首字母大写
 */
export function firstUpperCase(str: string) {
  return str.toLowerCase().replace(/( |^)[a-z]/g, (L) => L.toUpperCase())
}

export const generateUUID = () => {
  if (typeof crypto === 'object') {
    if (typeof crypto.randomUUID === 'function') {
      return crypto.randomUUID()
    }
    if (typeof crypto.getRandomValues === 'function' && typeof Uint8Array === 'function') {
      const callback = (c: any) => {
        const num = Number(c)
        return (num ^ (crypto.getRandomValues(new Uint8Array(1))[0] & (15 >> (num / 4)))).toString(
          16
        )
      }
      return '10000000-1000-4000-8000-100000000000'.replace(/[018]/g, callback)
    }
  }
  let timestamp = new Date().getTime()
  let performanceNow =
    (typeof performance !== 'undefined' && performance.now && performance.now() * 1000) || 0
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    let random = Math.random() * 16
    if (timestamp > 0) {
      random = ((timestamp + random) % 16) | 0
      timestamp = Math.floor(timestamp / 16)
    } else {
      random = ((performanceNow + random) % 16) | 0
      performanceNow = Math.floor(performanceNow / 16)
    }
    return (c === 'x' ? random : (random & 0x3) | 0x8).toString(16)
  })
}

/**
 * element plus 的文件大小 Formatter 实现
 *
 * @param row 行数据
 * @param column 字段
 * @param cellValue 字段值
 */
// @ts-ignore
export const fileSizeFormatter = (row, column, cellValue) => {
  const fileSize = cellValue
  const unitArr = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  const srcSize = parseFloat(fileSize)
  const index = Math.floor(Math.log(srcSize) / Math.log(1024))
  const size = srcSize / Math.pow(1024, index)
  const sizeStr = size.toFixed(2) //保留的小数位数
  return sizeStr + ' ' + unitArr[index]
}

/**
 * 将值复制到目标对象，且以目标对象属性为准，例：target: {a:1} source:{a:2,b:3} 结果为：{a:2}
 * @param target 目标对象
 * @param source 源对象
 */
export const copyValueToTarget = (target, source) => {
  const newObj = Object.assign({}, target, source)
  // 删除多余属性
  Object.keys(newObj).forEach((key) => {
    // 如果不是target中的属性则删除
    if (Object.keys(target).indexOf(key) === -1) {
      delete newObj[key]
    }
  })
  // 更新目标对象值
  Object.assign(target, newObj)
}

/**
 * 将一个整数转换为分数保留两位小数
 * @param num
 */
export const formatToFraction = (num: number | string | undefined): number => {
  if (typeof num === 'undefined') return 0
  const parsedNumber = typeof num === 'string' ? parseFloat(num) : num
  return parseFloat((parsedNumber / 100).toFixed(2))
}

/**
 * 将一个数转换为 1.00 这样
 * 数据呈现的时候使用
 *
 * @param num 整数
 */
export const floatToFixed2 = (num: number | string | undefined): string => {
  let str = '0.00'
  if (typeof num === 'undefined') {
    return str
  }
  const f = formatToFraction(num)
  const decimalPart = f.toString().split('.')[1]
  const len = decimalPart ? decimalPart.length : 0
  switch (len) {
    case 0:
      str = f.toString() + '.00'
      break
    case 1:
      str = f.toString() + '0'
      break
    case 2:
      str = f.toString()
      break
  }
  return str
}

/**
 *  金钱处理
 * @param num 数字
 * @param len 小数位数
 * @param keepTrailingZeros 是否保留末尾零，默认false（移除）
 */
export const formatterPrice = (num: number | string, len = 6, keepTrailingZeros = false): string => {
  if (!num) return '0'
  const f = num.toString()
  // 判断是否为整数
  if (/^\d+(\.0+)?$/.test(f)) {
    // 是整数
    if (keepTrailingZeros && len > 0) {
      return Number(num).toFixed(len)
    }
    // 直接返回整数部分
    return f.split('.')[0]
  }
  const decimalPart = f.split('.')[1]
  const decimalLen = decimalPart ? decimalPart.length : 0
  if (decimalLen > len) {
    // 保留len位小数
    const fixed = Number(num).toFixed(len)
    return keepTrailingZeros ? fixed : fixed.replace(/(\.\d*?[1-9])0+$/g, '$1').replace(/\.0+$/, '')
  } else {
    // 小数位数不足len位
    if (keepTrailingZeros) {
      return Number(num).toFixed(len)
    }
    // 去除末尾无效0和小数点
    return f.replace(/(\.\d*?[1-9])0+$/g, '$1').replace(/\.0+$/, '')
  }
}

/**
 * 将一个分数转换为整数
 * @param num
 */
export const convertToInteger = (num: number | string | undefined): number => {
  if (typeof num === 'undefined') return 0
  const parsedNumber = typeof num === 'string' ? parseFloat(num) : num
  // TODO 分转元后还有小数则四舍五入
  return Math.round(parsedNumber * 100)
}

/**
 * 元转分
 */
export const yuanToFen = (amount: string | number): number => {
  return convertToInteger(amount)
}

/**
 * 分转元
 */
export const fenToYuan = (price: string | number): number => {
  return formatToFraction(price)
}

/**
 * 计算环比
 *
 * @param value 当前数值
 * @param reference 对比数值
 */
export const calculateRelativeRate = (value?: number, reference?: number) => {
  // 防止除0
  if (!reference) return 0

  return ((100 * ((value || 0) - reference)) / reference).toFixed(0)
}

/**
 * 获取链接的参数值
 * @param key 参数键名
 * @param urlStr 链接地址，默认为当前浏览器的地址
 */
export const getUrlValue = (key: string, urlStr: string = location.href): string => {
  if (!urlStr || !key) return ''
  const url = new URL(decodeURIComponent(urlStr))
  return url.searchParams.get(key) ?? ''
}

/**
 * 获取链接的参数值（值类型）
 * @param key 参数键名
 * @param urlStr 链接地址，默认为当前浏览器的地址
 */
export const getUrlNumberValue = (key: string, urlStr: string = location.href): number => {
  return toNumber(getUrlValue(key, urlStr))
}

/**
 * 费用小项 获取总计 方法
 * @param list 表格数据
 * @param amount 需要处理金额字段 默认值 expenseAmount
 * @param currency 需要处理币种字段 默认值 expenseCurrency
 */
export const getTotal = (list: [], amount = 'expenseAmount', currency = 'expenseCurrency') => {
  if (!list) return
  const effectiveData = []
  list.map((item) => {
    if (Boolean(item[currency]) && Boolean(item[amount])) {
      effectiveData.push(item)
    }
  })
  if (effectiveData.length === 0) return 0
  const totalList: any = []
  uniq(
    effectiveData.map((item) => {
      return item[currency]
    })
  ).forEach((key) => {
    totalList.push({
      currency: key,
      amount: 0
    })
  })

  effectiveData.forEach((item) => {
    totalList.forEach((el) => {
      if (item[currency] === el.currency) {
        el.amount += Number(item[amount])
      }
    })
  })
  return totalList.map((item) => {
    return {
      ...item,
      amount: formatterPrice(item.amount, 3)
    }
  })
}
export const getTotalAmount = (list: [], key: any) => {
  if (!list) return
  const effectiveData = []
  list.map((item) => {
    if (Boolean(item[key].amount) && Boolean(item[key].currency)) {
      effectiveData.push(item)
    }
  })
  if (effectiveData.length === 0) return 0
  const totalList: any = []
  uniq(
    effectiveData.map((item: any) => {
      return item[key].currency
    })
  ).forEach((key) => {
    totalList.push({
      currency: key,
      amount: 0
    })
  })

  effectiveData.forEach((item) => {
    totalList.forEach((el) => {
      if (item[key].currency === el.currency) {
        el.amount += Number(item[key].amount)
      }
    })
  })
  return totalList
}

//获取多币种 单价*数量 结果
export const getTotalAmountSum = (list: [], key: any, countKey: any) => {
  if (!list) return []
  const effectiveData = []
  list.map((item) => {
    if (item[key] && Boolean(item[key].amount) && Boolean(item[key].currency)) {
      effectiveData.push(item)
    }
  })
  if (effectiveData.length === 0) return 0
  const totalList: any = []
  uniq(
    effectiveData.map((item: any) => {
      return item[key].currency
    })
  ).forEach((key) => {
    totalList.push({
      currency: key,
      amount: 0
    })
  })

  effectiveData.forEach((item) => {
    totalList.forEach((el) => {
      if (item[key] && item[key].currency === el.currency) {
        if (item[countKey]) {
          el.amount += Number(item[key].amount) * Number(item[countKey])
        }
      }
    })
  })
  return totalList
}

//将两个多币种合并为一个对象并返回
export const getTotalAmountListSum = (list: [], addList: []) => {
  if (!list && !addList) return []
  if (!list && addList) return addList
  //所有币种集合
  const allCurrency = uniq(
    list.map((item) => {
      return item.currency
    })
  )
  addList &&
    addList.map((item) => {
      if (allCurrency.indexOf(item.currency) < 0) {
        allCurrency.push(item.currency)
      }
    })
  const totalList: any = []
  allCurrency.forEach((key) => {
    totalList.push({
      currency: key,
      amount: 0
    })
  })
  totalList.forEach((item) => {
    list.forEach((li) => {
      if (li.currency === item.currency) {
        item.amount += Number(li.amount)
      }
    })
    addList.forEach((ali) => {
      if (ali.currency === item.currency) {
        item.amount += Number(ali.amount)
      }
    })
  })
  return totalList
}

//多币合计计算(人民币)
export const getAmountListSumRmb = (list, rateList) => {
  if (!list && list != 0) return 0
  let amountSum = 0
  list.forEach((lp) => {
    if (lp.currency && lp.amount) {
      const rate = rateList[lp.currency]
      if (rate) {
        amountSum = amountSum + Number(lp.amount) * Number(rate)
      }
    }
  })
  return amountSum
}

export const amountListFormat = (val) => {
  if (!val) return '-'
  let valueFormat
  val.forEach((lp) => {
    if (!valueFormat) {
      valueFormat = Number(lp.amount).toFixed(3) + ' ' + lp.currency
    } else {
      valueFormat += ',' + Number(lp.amount).toFixed(3) + ' ' + lp.currency
    }
  })
  if (valueFormat) {
    return `${valueFormat}`
  } else {
    return '-'
  }
}

export const checkeAmountFormat = (val) => {
  if (!val) return '-'
  if (val.checkAmount && val.currency) {
    return `${val.checkAmount.toFixed(2)} ${val.currency}`
  } else {
    return '-'
  }
}
/**
 * table组件如果是非必填且 第一行默认行中每个属性为空 则置为空数组 方法
 * @param reqlist 请求参数 类型object
 * @param keyArr 需要处理的数组合集 eg: ['pocList','bankList']
 * @param omitArr 筛选时需要排除掉的属性
 */
const checkValues = (data) => {
  return data === ''
}
export const setTableFirstRowArr = (reqlist, keyArr, omitArr?) => {
  if (reqlist && keyArr && keyArr.length) {
    keyArr.map((item, index, arr) => {
      if (reqlist[item] && reqlist[item].length) {
        //获取筛选后的对象
        const newObj = omit(reqlist[item][0], omitArr)
        //判断是否对象中的每个value都为空
        if (Object.values(newObj).every(checkValues)) {
          return (reqlist[item] = [])
        }
        //如果table默认行中未填数据 则置空
        else if (Object.keys(reqlist[item][0]).length === 1) {
          return (reqlist[item] = [])
        }
      }
      //处理后台返回的null字段 并添加默认行 eg: pocList=null
      else {
        reqlist[item] = [{ defaultFlag: 1 }]
      }
    })
  }
  return reqlist
}

export const openPdf = (url) => {
  const dySrc = encodeURIComponent(`${import.meta.env.VITE_BASE_URL}/admin-api${url}`)
  //替换为实际的文档URL
  const pdfUrl = `/lib/pdfjs/web/viewer.html?file=` + dySrc
  //pdfUrl是当前页面的pdf文件的URL,第二个参数'_blank'表示在新窗口中打开。
  window.open(pdfUrl, '_blank')
}

export const maskBankAccount = (accountNumber, showLastFour = 4) => {
  return accountNumber
  // // 将账号转换为字符串，以便操作
  // const accountStr = accountNumber.toString()

  // // 计算需要显示的星号数量
  // const starCount = accountStr.length - showLastFour

  // // 使用数组的map方法生成占位符数组
  // const starArray = new Array(starCount).fill('*')

  // // 将数组转换为字符串，并与账号的最后四位数字连接
  // const mask = starArray.join('').concat(accountStr.slice(-showLastFour))

  // return mask
}

export const setNewData = (newData = [], oldData = [], typeStr = 'oldData') => {
  // 给新数据加上oldData对象，方便对比
  if (newData != null && oldData != null) {
    if (newData?.length >= oldData?.length) {
      newData.forEach((e, i) => {
        e.changeFlag = false
        if (oldData[i]) {
          e[typeStr] = oldData[i]
        } else {
          const cloneObj = cloneDeep(e)
          for (const key in cloneObj) {
            cloneObj[key] = ' '
          }
          e[typeStr] = cloneObj
        }
      })
    } else {
      oldData.forEach((e, i) => {
        if (newData[i]) {
          newData[i] = {
            ...newData[i],
            [typeStr]: e,
            changeFlag: false
          }
        } else {
          const cloneObj = cloneDeep(e)
          for (const key in cloneObj) {
            cloneObj[key] = ' '
          }
          newData[i] = {
            ...cloneObj,
            [typeStr]: e,
            changeFlag: false
          }
        }
      })
    }
  }
}

export const getDelPic = (newData, oldData) => {
  // 给新数据加上oldData对象，方便对比
  if (newData != null && oldData != null) {
    const delPic: any[] = [],
      newIdList = newData.map((e) => e.id)

    oldData.forEach((item) => {
      if (!newIdList.includes(item.id)) {
        delPic.push(item)
      }
    })
    return delPic
  } else {
    return []
  }
}

export const formatterEngDes = (text) => {
  if (text) {
    const punctuationMap = {
      '，': ',',
      '。': '.',
      '；': ';',
      '：': ':',
      '“': '"',
      '”': '"',
      '‘': "'",
      '’': "'",
      '（': '(',
      '）': ')',
      '【': '[',
      '】': ']',
      '？': '?',
      '！': '!',
      '……': '...',
      '——': '_'
    }
    const text2 = text.replace(/(^|\n)\s*([a-z])/g, (match, p1, p2) => {
      return p1 + p2.toUpperCase()
    })
    return text2
      .split('')
      .map((char) => punctuationMap[char] || char)
      .join('')
  } else {
    return ''
  }
}

export const uniqueItems = (list, key = 'code') => {
  return list.filter((item, index, self) => index === self.findIndex((t) => t[key] === item[key]))
}

export const getDecimalPlaces = (value: number | string): number => {
  if (value === null || value === undefined) return 0

  const str = value.toString()
  const decimalIndex = str.indexOf('.')

  // 如果没有小数点，返回0
  if (decimalIndex === -1) return 0

  // 返回小数部分的长度
  return str.length - decimalIndex - 1
}

const findNodePath = (nodes, id, path = []) => {
  for (const node of nodes) {
    const currentPath = [...path, node.name]
    if (node.id === id) {
      return currentPath
    }
    if (node.children) {
      const found = findNodePath(node.children, id, currentPath)
      if (found) return found
    }
  }
  return null
}

export const getCategoryName = async (val, classTree) => {
  const pathNames = findNodePath(classTree, val)
  return pathNames.join('->')
}

/**
 * 编码文件 URL，处理文件名中的特殊字符（如 +、中文等）
 * @param fileUrl 文件 URL 路径，例如：/infra/file/18/get/VK251390C-1+VK251427C-1值班搞.png
 * @returns 编码后的 URL 路径
 */
export const encodeFileUrl = (fileUrl: string): string => {
  if (!fileUrl) return fileUrl

  // 找到最后一个斜杠的位置，分离路径和文件名
  const lastSlashIndex = fileUrl.lastIndexOf('/')
  if (lastSlashIndex === -1) {
    // 如果没有斜杠，直接编码整个字符串
    return encodeURIComponent(fileUrl)
  }

  // 分离路径部分和文件名部分
  const pathPart = fileUrl.substring(0, lastSlashIndex + 1) // 包含最后一个斜杠
  const fileName = fileUrl.substring(lastSlashIndex + 1)

  // 对文件名部分进行编码，路径部分保持不变
  const encodedFileName = encodeURIComponent(fileName)

  // 重新拼接
  return pathPart + encodedFileName
}
