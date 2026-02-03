import request from '@/config/axios'
import { Sku } from '@/api/mall/product/spu'

export interface SpuVO {
  id: number
  name: string
  code: string
  barcode: string
  nameEng: string
  brandId: number
  categoryId: number
  spuType: string
  unitType: string
  onshelfFlag: number
  auditStatus: number
  description: string
  descriptionEng: string
  hsCode: string
  ownBrandFlag: number
}

export interface Spu {
  id?: number
  name?: string // 商品名称
  categoryId?: number | undefined // 商品分类
  keyword?: string // 关键字
  unit?: number | undefined // 单位
  picUrl?: string // 商品封面图
  sliderPicUrls?: string[] // 商品轮播图
  introduction?: string // 商品简介
  deliveryTemplateId?: number | undefined // 运费模版
  brandId?: number | undefined // 商品品牌编号
  specType?: boolean // 商品规格
  subCommissionType?: boolean // 分销类型
  skus?: Sku[] // sku数组
  description?: string // 商品详情
  sort?: number // 商品排序
  giveIntegral?: number // 赠送积分
  virtualSalesCount?: number // 虚拟销量
  recommendHot?: boolean // 是否热卖
  recommendBenefit?: boolean // 是否优惠
  recommendBest?: boolean // 是否精品
  recommendNew?: boolean // 是否新品
  recommendGood?: boolean // 是否优品
  price?: number // 商品价格
  salesCount?: number // 商品销量
  marketPrice?: number // 市场价
  costPrice?: number // 成本价
  stock?: number // 商品库存
  createTime?: Date // 商品创建时间
  status?: number // 商品状态
  activityOrders: number[] // 活动排序
}
// 查询商品分页
export const getSpuPage = async (params) => {
  return await request.get({ url: `/pms/spu/page`, params })
}

// 查询商品详情
export const getSpu = async (id: number) => {
  return await request.get({ url: `/pms/spu/get?id=` + id })
}

// 新增商品
export const createSpu = async (data: SpuVO) => {
  return await request.post({ url: `/pms/spu/create`, data })
}

// 修改商品
export const updateSpu = async (data: SpuVO) => {
  return await request.put({ url: `/pms/spu/update`, data })
}

// 删除商品
export const deleteSpu = async (id: number) => {
  return await request.delete({ url: `/pms/spu/delete?id=` + id })
}

// 导出商品 Excel
export const exportSpu = async (params) => {
  return await request.download({ url: `/pms/spu/export-excel`, params })
}
