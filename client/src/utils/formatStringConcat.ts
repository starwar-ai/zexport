export const formatStringConcat = (pStr, addStr) => {
  let resStr = pStr
  if (addStr && !resStr.includes(addStr)) {
    if (resStr) {
      resStr = resStr + ',' + addStr
    } else {
      resStr = addStr
    }
  }
  return resStr
}
