export function deleteProperties(obj) {
  const keys = Object.keys(obj)
  keys.forEach((item) => {
    delete obj[item]
  })
}
