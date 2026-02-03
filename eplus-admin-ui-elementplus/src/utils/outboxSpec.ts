import { lengthComposeConvert, volConvert, weightConvert } from './config'
import { isValidArray } from './is'

export const getOuterbox = (row, type, unitRadio = 'metric') => {
  if (!isValidArray(row.specificationList)) return false
  if (type === 'spec') {
    const arr: any[] = []
    for (let i = 0; i < row.specificationList.length; i++) {
      const el = row.specificationList[i]
      arr.push(
        lengthComposeConvert(el.outerboxLength, el.outerboxWidth, el.outerboxHeight, unitRadio)
      )
    }
    return arr.join('\n')
  } else if (type === 'vol') {
    return volConvert(
      row.specificationList.reduce((prev, cur) => prev + Number(cur.outerboxVolume), 0),
      unitRadio
    )
  } else if (type === 'grossweight') {
    return weightConvert(
      {
        weight: row.specificationList.reduce(
          (prev, cur) => prev + Number(cur.outerboxGrossweight.weight),
          0
        ),
        unit: row.specificationList[0].outerboxGrossweight.unit
      },
      unitRadio
    )
  } else if (type === 'netweight') {
    return weightConvert(
      {
        weight: row.specificationList.reduce(
          (prev, cur) => prev + Number(cur.outerboxNetweight.weight),
          0
        ),
        unit: row.specificationList[0].outerboxNetweight.unit
      },
      unitRadio
    )
  }
}

export const getOuterboxVal = (row, type) => {
  if (isValidArray(row.specificationList)) {
    if (type === 'vol') {
      return row.specificationList.reduce((prev, cur) => prev + Number(cur.outerboxVolume), 0)
    } else if (type === 'grossweight') {
      return row.specificationList.reduce(
        (prev, cur) => prev + Number(cur.outerboxGrossweight.weight),
        0
      )
    } else if (type === 'netweight') {
      return row.specificationList.reduce(
        (prev, cur) => prev + Number(cur.outerboxNetweight.weight),
        0
      )
    }
  }
}
