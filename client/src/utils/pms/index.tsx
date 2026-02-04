const router = useRouter()
import { setSourceId } from '@/utils/auth'

export const openProductDetail = (row) => {
  setSourceId(row.skuId)
  if (row.custProFlag == 1) {
    router.push({ path: '/base/product-manage/csku' })
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
    router.push({ path: '/base/product-manage/own' })
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
    router.push({ path: '/base/product-manage/main' })
  }
}

export const formatSkuCodeColumn = (row) => {
  return (
    <span
      onClick={() => openProductDetail(row)}
      style="color:rgb(0, 91, 245);cursor:pointer;"
    >
      {row.skuCode}
    </span>
  )
}

export const formatProductTypeColumn = (row) => {
  if (row.custProFlag == 1) {
    return <span>客户产品</span>
  } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
    return <span>自有品牌</span>
  } else {
    return <span>其他</span>
  }
}
