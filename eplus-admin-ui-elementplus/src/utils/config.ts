import { formatNum, formatterPrice } from '@/utils/index'

// 长度单位
export const LengthUnit = 'cm'
export const EngLengthUnit = 'inch'
export const LengthRatio = 2.54
export const lenPrecision = 1
export const weightPrecision = 2
export const moneyInputPrecision = 6
export const moneyPrecision = 3
export const moneyTotalPrecision = 2
export const ratePrecision = 4
export const volPrecision = 3

export const lengthComposeConvert = (length, width, height, type) => {
  if (length && width && height) {
    if (type === 'eng') {
      return `${formatterPrice(Number(length) / LengthRatio, 2)}*${formatterPrice(Number(width) / LengthRatio, 2)}*${formatterPrice(Number(height) / LengthRatio, 2)} ${EngLengthUnit}`
    } else {
      return `${formatNum(length, lenPrecision)}*${formatNum(width, lenPrecision)}*${formatNum(height, lenPrecision)} ${LengthUnit}`
    }
  } else {
    return '-'
  }
}

// 体积单位
export const VolumeUnit = 'm³'
export const EngVolumeUnit = 'cu ft'
export const VolumeRatio = 0.0000353

export const volConvert = (val, type) => {
  if (val > 0) {
    if (type === 'eng') {
      return `${formatNum(val / VolumeRatio, volPrecision)} ${EngVolumeUnit}`
    } else {
      return `${formatNum(val / 1000000, volPrecision)} ${VolumeUnit}`
    }
  } else {
    return '-'
  }
}

//重量单位
export const weightUnit = 'kg'
export const EngWeightUnit = 'lb'
export const WeightRatio = 2.20462

export const weightConvert = (val, type) => {
  if (val?.weight) {
    if (type === 'eng') {
      if (val.unit === 'kg') {
        return `${formatterPrice(val.weight * WeightRatio, weightPrecision)} ${EngWeightUnit}`
      } else {
        return `${formatterPrice((val.weight / 1000) * WeightRatio, weightPrecision)} ${EngWeightUnit}`
      }
    } else {
      return `${val.weight} ${val.unit}`
    }
  } else {
    return '-'
  }
}

//字段分割符
export const SplitKey = '&$&'

//查询条件
export const inputOptions = [
  {
    label: '包含',
    value: 'like'
  },
  {
    label: '不包含',
    value: 'nlike'
  },
  {
    label: '等于',
    value: '$eq'
  },
  {
    label: '不等于',
    value: '$ne'
  }
]

export const selectOptions = [
  {
    label: '等于',
    value: '$eq'
  },
  {
    label: '不等于',
    value: '$ne'
  }
]

export const timeOptions = [
  {
    label: '早于',
    value: '$gt'
  },
  {
    label: '晚于',
    value: '$lt'
  },
  {
    label: '介于',
    value: '$in'
  }
]

// "$eq" -> "等于"
// "$ne" -> "不等于"
// "$gt" -> "大于"
//  "$gte" ->"大于等于"
//  "$lt"->"小于"
// "$lte"->"小于等于"
// "$in"->"介于"
// "$nin"-> "不存在于"
// "like"-> "包含"
//     NLIKE("nlike", "不包含");
