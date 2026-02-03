import { getDictLabel } from '@/utils/dict'
import { useRateStore } from '@/store/modules/rate'

const rateList = useRateStore().rateList

export const getActualRate = (
  saleContractItemDto,
  currencySaleContract,
  withTaxPrice,
  quantity
) => {
  //毛利润=收入-成本
  //其中收入=货值合计*汇率+加项金额+退税金额
  //其中成本=采购合计+包材+配件+总运费+验货费+平台费+减项金额+保险费+中信保费
  //采购合计=采购总金额*对人民币汇率，需要看采购币种
  let rate = 0
  if (currencySaleContract) {
    rate = rateList[currencySaleContract]
  }
  //收入
  let addAmount = 0
  if (saleContractItemDto.totalSaleAmount?.amount) {
    addAmount = Number(saleContractItemDto.totalSaleAmount.amount) * Number(rate)
  }
  //退税金额
  if (saleContractItemDto.taxRefundPrice?.amount) {
    addAmount = Number(addAmount) + Number(saleContractItemDto.taxRefundPrice.amount)
  }
  //成本
  let subAmount = 0
  //采购合计
  if (withTaxPrice?.amount) {
    const ratePurchase = rateList[withTaxPrice?.currency]
    subAmount = Number(withTaxPrice?.amount) * Number(ratePurchase) * Number(quantity)
  }
  //包材
  if (saleContractItemDto.purchasePackagingPrice?.amount) {
    subAmount =
      Number(subAmount) +
      Number(saleContractItemDto.purchasePackagingPrice?.amount) *
        Number(saleContractItemDto.quantity)
  }
  //配件
  if (saleContractItemDto.purchaseWithTaxPrice?.amount && saleContractItemDto.skuType) {
    if (getDictLabel(DICT_TYPE.SKU_TYPE, saleContractItemDto.skuType) == '配件') {
      subAmount =
        Number(subAmount) +
        Number(saleContractItemDto.purchaseWithTaxPrice?.amount) *
          Number(saleContractItemDto.quantity)
    }
  }
  //总运费
  if (saleContractItemDto.forecastTotalCost?.amount) {
    subAmount = Number(subAmount) + Number(saleContractItemDto.forecastTotalCost.amount)
  }
  //验货费
  if (saleContractItemDto.inspectionFee?.amount) {
    subAmount = Number(subAmount) + Number(saleContractItemDto.inspectionFee.amount)
  }
  //平台费
  if (saleContractItemDto.platformFeeComputed) {
    subAmount = Number(subAmount) + Number(saleContractItemDto.platformFeeComputed)
  }
  //保险费
  if (saleContractItemDto.insuranceFee?.amount) {
    subAmount = Number(subAmount) + Number(saleContractItemDto.insuranceFee?.amount)
  }
  //中信保费用
  if (saleContractItemDto.sinosureFee?.amount) {
    subAmount = Number(subAmount) + Number(saleContractItemDto.sinosureFee?.amount)
  }
  const orderGrossProfitSum = addAmount - subAmount
  //毛利率=毛利润/收入
  if (orderGrossProfitSum == 0) {
    return 0
  } else if (orderGrossProfitSum && addAmount) {
    const orderGrossProfitRate = Number(orderGrossProfitSum) / Number(addAmount)
    return Number(orderGrossProfitRate * 100).toFixed(2)
  }
}
