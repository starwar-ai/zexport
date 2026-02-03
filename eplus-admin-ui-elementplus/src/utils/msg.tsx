import router from '@/router'
import { ElNotification } from 'element-plus'
import { setSourceId } from '@/utils/auth'

export const createMsg = (des: string, codeList: any[], type: string) => {
  return h('span', null, [
    h('span', null, des),
    ...codeList.map((item, index) => {
      if (index === codeList.length - 1) {
        return h(
          'i',
          {
            style: 'color: teal; cursor: pointer',
            onClick: () => nodeClick(item.id, type)
          },
          item.code
        )
      } else {
        return h(
          'i',
          {
            style: 'color: teal; cursor: pointer',
            onClick: () => nodeClick(item.id, type)
          },
          item.code + '，'
        )
      }
    })
  ])
}

const routers = [
  {
    key: 'quality-inspection',
    url: '/scm/quality-inspection-manage/quality-inspection',
    permi: ['qms:quality-inspection:query']
  },
  {
    key: 'purchase-contract-notice',
    url: '/scm/vender-payment/invoicingNotices',
    permi: []
  },
  {
    key: 'purchase-plan',
    url: '/scm/purchase/plan',
    permi: []
  },
  {
    key: 'purchase-release',
    url: '/scm/quality-inspection-manage/concessionRelease',
    permi: []
  },
  {
    key: 'purchase-auxiliary-plan',
    url: '/scm/auxiliary-purchase/auxiliary-plan',
    permi: []
  },
  {
    key: 'purchase-payment-apply',
    url: '/scm/vender-payment/payment-apply',
    permi: []
  },
  {
    key: 'shipment-plan',
    url: '/dms/shipping-manage/shipping-plan',
    permi: []
  },
  {
    key: 'shipment',
    url: '/dms/shipping-manage/shipping',
    permi: []
  },
  {
    key: 'purchase-auxiliary-contract',
    url: '/scm/auxiliary-purchase/auxiliary-contract',
    permi: []
  },
  {
    key: 'shipment-commodity-inspection',
    url: '/dms/shipping-orders/commodityInspection',
    permi: []
  },
  {
    key: 'shipment-declaration',
    url: '/dms/shipping-orders/declaration',
    permi: []
  },
  {
    key: 'shipment-settlement-form',
    url: '/dms/shipping-orders/settlementForm',
    permi: []
  },
  {
    key: 'shipment-container-notice',
    url: '/dms/ContainerNotice',
    permi: []
  },
  {
    key: 'shipment-manufacture',
    url: '/wms/manufacture/manufacture-index',
    permi: []
  },
  {
    key: 'export-change',
    url: '/sms/sale-orders/exportSaleContractChange',
    permi: []
  },
  {
    key: 'domestic-change',
    url: '/sms/sale-orders/domesticSaleContractChange',
    permi: []
  },
  {
    key: 'factory-change',
    url: '/sms/sale-orders/factorySaleContractChange',
    permi: []
  },
  {
    key: 'purchase-change',
    url: '/scm/purchase/purchaseChange',
    permi: []
  },
  {
    key: 'shipment-change',
    url: '/dms/shipping-manage/shipment-change',
    permi: []
  },
  {
    key: 'scm-purchase-contract',
    url: '/scm/purchase/contract',
    permi: []
  },
  {
    key: 'scm-purchase-warehouse',
    url: '/wms/1/renotice',
    permi: []
  },
  {
    key: 'auxiliary-change',
    url: '/scm/auxiliary-purchase/auxiliary-change',
    permi: []
  },
  {
    key: 'cust-formal',
    url: '/base/cust/formal-index',
    permi: []
  },
  {
    key: 'vender-business',
    url: '/base/vender/business',
    permi: []
  },
  {
    key: 'vender-administration',
    url: '/base/vender/administration',
    permi: []
  },
  {
    key: 'ysbill',
    url: '/wms/1/ysbill',
    permi: []
  },
  {
    key: 'ckbill',
    url: '/wms/2/ckbill',
    permi: []
  },
  {
    key: 'payments',
    url: '/oa/payment/corporate-payments',
    permi: []
  },
  {
    key: 'ttnotice',
    url: '/wms/2/ttnotice',
    permi: []
  }
]
const nodeClick = (id: string, type: string) => {
  setSourceId(id)
  const path = routers.find((item) => item.key === type)?.url
  router.push({ path: path })
  ElNotification.closeAll()
}
