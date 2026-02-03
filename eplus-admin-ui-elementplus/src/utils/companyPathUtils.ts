// copy to vben-admin

export const getCompanyPathNameFromObj = (item) => {
  let pathName = item.name
  let nextObj = item.next
  while (nextObj) {
    pathName += '->' + nextObj.name
    nextObj = nextObj.next
  }
  return pathName
}
const isSubset = <T>(subset: T[], superset: T[]): boolean => {
  return subset.every((element) => superset.includes(element))
}

const checkInnerCustPath = (pathItem, innerCustIdList) => {
  const arr: number[] = [pathItem.id]
  let nextObj = pathItem.next
  while (nextObj) {
    arr.push(nextObj.id)
    nextObj = nextObj.next
  }
  return isSubset(arr, innerCustIdList)
}
export const getInnerCustPath = (pathList, innerCustIdList) => {
  const innerCustPath: any[] = []
  pathList.forEach((item: any) => {
    if (item?.path && checkInnerCustPath(item?.path, innerCustIdList)) {
      innerCustPath.push(item)
    }
  })
  return innerCustPath
}

export const getCompanyIdList = (item) => {
  if (item) {
    const list = [item.id]
    let nextObj = item.next
    while (nextObj) {
      list.push(nextObj.id)
      nextObj = nextObj.next
    }
    // if (list.length > 2) {
    //   return [list[0], list[list.length - 1]]
    // } else {
    //   return list
    // }
    return [list[list.length - 1]]
  }
}
