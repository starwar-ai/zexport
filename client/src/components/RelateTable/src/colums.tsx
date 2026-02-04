import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { columnWidth, formatDateColumn } from '@/utils/table'
import { EplusImgEnlarge } from '@/components/EplusTemplate/index'
import { formatTime } from '@/utils/index'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import DifferenceReasonInfo from '@/views/crm/receiptRegistration/components/DifferenceReasonInfo.vue'

// 采购计划3  销售合同1                         采购合同4
// 采购合同4  销售合同1、采购计划3              采购变更单5、入库通知单23、验货单16、付款申请单20、开票通知21
// 出运计划6  销售合同1、采购合同4              出运明细7
// 出运明细7  销售合同1、采购合同4、出运计划6    拉柜通知单11、报关单12、结汇单13、商检单14、开票通知21
// 外销合同1                                   销售变更单2、采购计划3、采购合同4、出运计划6、出运明细7、收款账单22、
// 付款单17   付款申请单20
// 付款申请单20                                 付款单17
export const tabs = [
  {
    name: '外销合同',
    type: 1,
    beforeTabList: [],
    afterTabList: [2, 3, 4, 6, 7, 22]
  },
  {
    name: '采购计划',
    type: 3,
    beforeTabList: [1],
    afterTabList: [4]
  },
  {
    name: '采购合同',
    type: 4,
    beforeTabList: [1, 3],
    afterTabList: [5, 16, 20, 21, 23]
  },
  {
    name: '出运计划',
    type: 6,
    beforeTabList: [1, 4],
    afterTabList: [7]
  },
  {
    name: '出运明细',
    type: 7,
    beforeTabList: [1, 4, 6],
    afterTabList: [11, 12, 13, 14, 21]
  },
  {
    name: '付款单',
    type: 17,
    beforeTabList: [20],
    afterTabList: []
  },
  {
    name: '付款申请单',
    type: 20,
    beforeTabList: [],
    afterTabList: [17]
  }
]
export const columnList = [
  {
    name: '外销合同',
    type: 1,
    colums: [
      {
        prop: 'code',
        label: '销售合同',
        parent: true,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '下单主体',
        parent: true
      },
      {
        prop: 'businessSubjectName',
        label: '客户名称',
        parent: true,
        minWidth: columnWidth.l
      },
      {
        prop: 'skuCode',
        label: '产品编码',
        minWidth: columnWidth.l
      },
      {
        prop: 'name',
        label: '中文名称',
        minWidth: columnWidth.l
      },
      {
        prop: 'nameEng',
        label: '英文名称',
        minWidth: columnWidth.l
      },
      {
        prop: 'mainPicture',
        label: '图片',
        slots: {
          default: (data) => {
            const { row } = data
            return (
              <EplusImgEnlarge
                mainPicture={row?.mainPicture}
                thumbnail={row?.thumbnail}
              />
            )
          }
        }
      },
      {
        prop: 'quantity',
        label: '数量',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row.quantity || 0
          }
        }
      }
    ]
  },
  {
    name: '采购计划',
    type: 3,
    colums: [
      {
        prop: 'code',
        label: '采购计划编号',
        minWidth: columnWidth.l,
        parent: true,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '采购主体',
        minWidth: columnWidth.l,
        parent: true
      },
      {
        prop: 'skuCode',
        label: '产品编号',
        minWidth: columnWidth.l
      },
      {
        prop: 'productPicUrl',
        label: '图片',
        slots: {
          default: (data) => {
            const { row } = data
            return (
              <EplusImgEnlarge
                mainPicture={row?.mainPicture}
                thumbnail={row?.thumbnail}
              />
            )
          }
        }
      },
      {
        prop: 'skuName',
        label: '品名',
        minWidth: '120px'
      },
      // {
      //   prop: 'availableQuantity',
      //   label: '库存',
      //   minWidth: columnWidth.l,
      //   slots: {
      //     default: (data) => {
      //       const { row } = data
      //       return row?.availableQuantity || 0
      //     }
      //   }
      // },
      {
        prop: 'currentLockQuantity',
        label: '已锁库存',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.currentLockQuantity || 0
          }
        }
      },
      {
        prop: 'needPurQuantity',
        label: '待采购',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.needPurQuantity || 0
          }
        }
      }
    ]
  },
  {
    name: '采购合同',
    type: 4,
    colums: [
      {
        prop: 'code',
        label: '采购合同单号',
        parent: true,
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '采购主体',
        parent: true,
        minWidth: columnWidth.l
      },
      {
        prop: 'businessSubjectName',
        label: '供应商',
        parent: true,
        minWidth: columnWidth.l
      },
      {
        prop: 'autoFlag',
        label: '是否自动生成',
        parent: true,
        minWidth: columnWidth.l,
        slots: {
          default: (row) => {
            return getDictLabel(DICT_TYPE.IS_INT, row?.orderMsg.autoFlag)
          }
        }
      },
      {
        prop: 'mainPicture',
        label: '图片',
        slots: {
          default: (data) => {
            const { row } = data
            return (
              <EplusImgEnlarge
                mainPicture={row?.mainPicture}
                thumbnail={row?.thumbnail}
              />
            )
          }
        }
      },
      {
        prop: 'skuCode',
        label: '产品编号',
        minWidth: columnWidth.l
      },
      {
        prop: 'skuName',
        label: '品名',
        minWidth: columnWidth.l
      },
      {
        prop: 'quantity',
        label: '采购数量',
        minWidth: columnWidth.l
      }
    ]
  },
  {
    name: '出运计划',
    type: 6,
    colums: [
      {
        prop: 'code',
        label: '计划单号',
        minWidth: columnWidth.l,
        parent: true,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '归属公司',
        parent: true,
        minWidth: columnWidth.l
      },
      {
        prop: 'estShipDate',
        label: '客户交期',
        minWidth: columnWidth.l,
        parent: true,
        slots: {
          default: (row) => {
            return formatTime(row.orderMsg?.estShipDate, 'yyyy-MM-dd')
          }
        }
      },
      {
        prop: 'departurePortName',
        label: '出运口岸',
        minWidth: columnWidth.l,
        parent: true,
        slots: {
          default: (row) => {
            return row.orderMsg?.departurePortName || '-'
          }
        }
      },
      {
        prop: 'destinationPortName',
        label: '目的口岸',
        minWidth: columnWidth.l,
        parent: true,
        slots: {
          default: (row) => {
            return row.orderMsg?.destinationPortName || '-'
          }
        }
      },
      {
        prop: 'saleContractCode',
        label: '外销合同号',
        minWidth: columnWidth.l
      },
      {
        prop: 'custName',
        label: '客户名称',
        minWidth: columnWidth.l
      },
      {
        prop: 'name',
        label: '产品名称',
        minWidth: columnWidth.l
      },
      {
        prop: 'shippingQuantity',
        label: '出运数量',
        minWidth: columnWidth.l
      },
      {
        prop: 'currency',
        label: '币种',
        minWidth: columnWidth.l
      }
    ]
  },
  {
    name: '出运明细',
    type: 7,
    colums: [
      {
        prop: 'code',
        label: '出运明细单号',
        minWidth: columnWidth.l,
        parent: true,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '归属公司',
        parent: true,
        minWidth: columnWidth.l
      },
      {
        prop: 'estShipDate',
        label: '出运日期',
        minWidth: columnWidth.l,
        parent: true,
        slots: {
          default: (row) => {
            return formatTime(row.orderMsg?.estShipDate, 'yyyy-MM-dd')
          }
        }
      },
      {
        prop: 'departurePortName',
        label: '出运口岸',
        minWidth: columnWidth.l,
        parent: true,
        slots: {
          default: (row) => {
            return row.orderMsg?.departurePortName || '-'
          }
        }
      },
      {
        prop: 'destinationPortName',
        label: '目的口岸',
        minWidth: columnWidth.l,
        parent: true,
        slots: {
          default: (row) => {
            return row.orderMsg?.destinationPortName || '-'
          }
        }
      },
      {
        prop: 'saleContractCode',
        label: '外销合同号',
        minWidth: columnWidth.l
      },
      {
        prop: 'custName',
        label: '客户名称',
        minWidth: columnWidth.l
      },
      {
        prop: 'manager',
        label: '业务员',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.manager?.nickname ? row?.manager?.nickname : '-'}`
          }
        }
      },
      {
        prop: 'name',
        label: '产品名称',
        minWidth: columnWidth.l
      },
      {
        prop: 'shippingQuantity',
        label: '出货数量',
        minWidth: columnWidth.l
      },
      {
        prop: 'currency',
        label: '币种',
        minWidth: columnWidth.l
      }
    ]
  },
  {
    name: '拉柜通知单',
    type: 11,
    colums: [
      {
        prop: 'code',
        label: '拉柜通知单号',
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'referenceNumber',
        label: '提单号',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.referenceNumber || '-'
          }
        }
      },
      {
        prop: 'containerTransportationDate',
        label: '预计拉柜日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return formatTime(row?.orderMsg?.containerTransportationDate, 'yyyy-MM-dd')
          }
        }
      }
    ]
  },
  {
    name: '报关单',
    type: 12,
    colums: [
      {
        prop: 'code',
        label: '报关单号',
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'shipmentCode',
        label: '出运明细号',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.shipmentCode ? row?.orderMsg?.shipmentCode : '-'}`
          }
        }
      },

      {
        prop: 'createTime',
        label: '制单日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.createTime ? formatTime(row.orderMsg?.createTime, 'yyyy-MM-dd') : '-'}`
          }
        }
      }
    ]
  },
  {
    name: '结汇单',
    type: 13,
    colums: [
      {
        prop: 'invoiceCode',
        label: '发票号',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.invoiceCode ? row?.orderMsg?.invoiceCode : '-'}`
          }
        }
      },
      {
        prop: 'code',
        label: '结汇单号',
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'createTime',
        label: '制单日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.createTime ? formatTime(row.orderMsg?.createTime, 'yyyy-MM-dd') : '-'}`
          }
        }
      },
      {
        prop: 'parentCode',
        label: '来源出运单号',
        minWidth: columnWidth.l
      }
    ]
  },
  {
    name: '商检单',
    type: 14,
    colums: [
      {
        prop: 'code',
        label: '商检单号',
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'inputDate',
        label: '制单日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.inputDate ? formatTime(row.orderMsg?.inputDate, 'yyyy-MM-dd') : '-'}`
          }
        }
      },
      {
        prop: 'parentCode',
        label: '来源出运单号',
        minWidth: columnWidth.l
      }
    ]
  },
  {
    name: '入库单',
    type: 15,
    colums: [
      {
        prop: 'code',
        label: '入库单号',
        parent: true,
        minWidth: columnWidth.xxl,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '下单主体',
        parent: true,
        minWidth: columnWidth.xxl
      },
      {
        prop: 'createTime',
        label: '入库日期',
        parent: true,
        minWidth: columnWidth.xxl,
        formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
      },
      {
        prop: 'billTime',
        label: '出库日期',
        parent: true,
        minWidth: columnWidth.xxl,
        formatter: formatDateColumn('YYYY-MM-DD')
      },
      {
        prop: 'purchaseContractCode',
        label: '采购合同号',
        parent: true,
        minWidth: columnWidth.xxl
      },
      {
        prop: 'skuCode',
        label: '产品编号',
        minWidth: columnWidth.xxl
      },
      {
        prop: 'skuName',
        label: '产品名称',
        minWidth: columnWidth.xxl
      },
      {
        prop: 'orderQuantity',
        label: '应收数量',
        minWidth: columnWidth.xxl
      },
      {
        prop: 'orderBoxQuantity',
        label: '应收箱数',
        minWidth: columnWidth.xxl
      },
      {
        prop: 'batchCode',
        label: '批次编号',
        minWidth: columnWidth.xxl
      },
      {
        prop: 'venderName',
        label: '供应商名称',
        minWidth: columnWidth.xxl
      }
    ]
  },
  {
    name: '验货单',
    type: 16,
    colums: [
      {
        prop: 'skuCode',
        label: '产品编号',
        minWidth: columnWidth.l
      },
      {
        prop: 'picture',
        label: '图片',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return (
              <EplusImgEnlarge
                mainPicture={row?.mainPicture}
                thumbnail={row?.thumbnail}
              />
            )
          }
        }
      },
      {
        prop: 'skuName',
        label: '产品名称',
        minWidth: columnWidth.xxl
      },

      {
        prop: 'code',
        label: '采购单号',
        minWidth: columnWidth.l,
        parent: true,
        isHyperlink: true,
        formatter: (row) => {
          return row.orderMsg?.code || '-'
        }
      },
      {
        prop: 'qualityInspectionStatus',
        label: '状态',
        parent: true,
        formatter: (row) => {
          return row?.orderMsg?.qualityInspectionStatus
            ? getDictLabel(
                DICT_TYPE.QUALITY_INSPECTION_STATUS,
                row?.orderMsg?.qualityInspectionStatus
              )
            : '-'
        },
        minWidth: columnWidth.l
      },
      {
        prop: 'inspectionType',
        label: '验货方式', // 1： 泛太陪验（工厂）2：泛太陪验（公司内） 3：泛太自验（工厂） 4：泛太自验（公司内） 5：客户自检 6：客户指定第三方 7：远程验货',
        parent: true,
        minWidth: columnWidth.l,
        slots: {
          default: (row) => {
            return `${row?.orderMsg?.inspectionType ? getDictLabel(DICT_TYPE.INSPECTION_TYPE, row?.orderMsg.inspectionType) : '-'}`
          }
        }
      },
      {
        prop: 'reinspectionFlag',
        label: '是否重验',
        parent: true,
        minWidth: columnWidth.l,
        formatter: (row) => {
          return row.orderMsg?.reinspectionFlag == 1 ? '是' : '否'
        }
      },

      {
        prop: 'inspectorName',
        label: '验货人',
        parent: true,
        minWidth: columnWidth.l,
        formatter: (row) => {
          return row.orderMsg?.inspectorName || '-'
        }
      },

      {
        prop: 'venderName',
        label: '供应商名称',
        parent: true,
        minWidth: columnWidth.xxl,
        formatter: (row) => {
          return row.orderMsg?.venderName || '-'
        }
      }
    ]
  },
  {
    name: '付款单',
    type: 17,
    colums: [
      {
        prop: 'code',
        label: '付款单号',
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '付款单位',
        minWidth: columnWidth.l
      },
      {
        prop: 'applyAmount',
        label: '申请总金额',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return <EplusMoneyLabel val={row.orderMsg?.applyAmount} />
          }
        }
      },
      {
        prop: 'applyer',
        label: '申请人（部门）',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row.orderMsg?.applyer
              ? row.orderMsg.applyer?.nickname + '(' + row.orderMsg.applyer?.deptName + ')'
              : '-'
          }
        }
      },
      {
        prop: 'applyPaymentDate',
        label: '申请付款日',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.applyPaymentDate ? formatTime(row.orderMsg?.applyPaymentDate, 'yyyy-MM-dd') : '-'}`
          }
        }
      },
      {
        prop: 'date',
        label: '付款日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.date ? formatTime(row.orderMsg?.date, 'yyyy-MM-dd') : '-'}`
          }
        }
      },
      {
        prop: 'cashier',
        label: '出纳员',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row.orderMsg?.cashier?.nickname || '-'
          }
        }
      },
      {
        prop: 'status',
        label: '付款状态',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return getDictLabel(DICT_TYPE.PAYMENT_STATUS, row?.orderMsg?.status)
          }
        }
      }
    ]
  },
  {
    name: '付款申请单',
    type: 20,
    colums: [
      {
        prop: 'code',
        label: '申请单号',
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '付款单位',
        minWidth: columnWidth.l
      },
      {
        prop: 'manager',
        label: '申请人',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg.applyer?.nickname || '-'
          }
        }
      },
      {
        prop: 'applyDate',
        label: '申请日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.applyDate ? formatTime(row.orderMsg?.applyDate, 'yyyy-MM-dd') : '-'}`
          }
        }
      },
      {
        prop: 'applyPaymentDate',
        label: '支付日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.applyPaymentDate ? formatTime(row.orderMsg?.applyPaymentDate, 'yyyy-MM-dd') : '-'}`
          }
        }
      },
      {
        prop: 'applyTotalAmount',
        label: '申请金额',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return (
              <EplusMoneyLabel
                val={{
                  amount: row?.orderMsg?.applyTotalAmount,
                  currency: row?.orderMsg?.currency
                }}
              />
            )
          }
        }
      },
      {
        prop: 'step',
        label: '付款类型',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.step ? getDictLabel(DICT_TYPE.PAYMENT_TYPE, row?.orderMsg.step) : '-'}`
          }
        }
      },
      {
        prop: 'parentCode',
        label: '关联采购合同',
        minWidth: columnWidth.l
      },
      {
        prop: 'businessSubjectName',
        label: '供应商名称',
        minWidth: columnWidth.l
      }
    ]
  },
  {
    name: '开票通知',
    type: 21,
    colums: [
      {
        prop: 'code',
        label: '通知编号',
        minWidth: columnWidth.m,
        isHyperlink: true
      },
      {
        prop: 'shipInvoiceCode',
        label: '发票号',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.shipInvoiceCode || '-'
          }
        }
      },
      {
        prop: 'companyName',
        label: '付款单位',
        minWidth: columnWidth.l
      },
      {
        prop: 'shipDate',
        label: '出运日期',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return formatTime(row.orderMsg?.shipDate, 'yyyy-MM-dd')
          }
        }
      },
      {
        prop: 'manager',
        label: '跟单员',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.manager?.nickname ? row?.orderMsg?.manager?.nickname : '-'}`
          }
        }
      },
      {
        prop: 'status',
        label: '状态',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return getDictLabel(DICT_TYPE.INVOICE_STATUS, row.orderMsg?.status)
          }
        }
      },
      {
        prop: 'venderName',
        label: '供应商名称',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return row.orderMsg?.venderName || '-'
          }
        }
      },
      {
        prop: 'inputUser',
        label: '录入人',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row?.orderMsg?.inputUser?.nickname ? row?.orderMsg?.inputUser?.nickname : '-'}`
          }
        }
      },
      {
        prop: 'purOrderCode',
        label: '采购合同号',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return row.orderMsg?.purOrderCode || '-'
          }
        }
      },
      {
        prop: 'manuallyFlag',
        label: '是否手动生成',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row.orderMsg?.manuallyFlag)
          }
        }
      }
    ]
  },
  {
    name: '收款单',
    type: 22,
    colums: [
      {
        prop: 'custName',
        label: '客户名称',
        width: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.custName || '-'
          }
        }
      },
      {
        prop: 'custCode',
        label: '客户编号',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.custCode || '-'
          }
        }
      },
      {
        prop: 'settlementName',
        label: '收款方式',
        minWidth: columnWidth.xxl,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.settlementName || '-'
          }
        }
      },
      {
        prop: 'source',
        label: '来源',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.source || '-'
          }
        }
      },
      {
        prop: 'currency',
        label: '订单币别',
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.currency || '-'
          }
        }
      },
      {
        prop: 'receivableAmount',
        label: '应收金额',
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.receivableAmount || '0'
          }
        }
      },
      {
        prop: 'receivedAmount',
        label: '已收金额',
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.receivedAmount || '0'
          }
        }
      },
      {
        prop: 'contractAmount',
        label: '认领金额',
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.contractAmount || '0'
          }
        }
      },
      {
        prop: 'differenceAmount',
        label: '差异总金额',
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.contractAmount || '0'
          }
        }
      },
      {
        prop: 'claimPerson',
        label: '认领人',
        slots: {
          default: (data) => {
            const { row } = data
            return row?.orderMsg?.claimPerson?.nickname || '-'
          }
        }
      },
      {
        prop: 'claimDate',
        label: '认领日期',
        slots: {
          default: (data) => {
            const { row } = data
            return formatTime(row.orderMsg?.claimDate, 'yyyy-MM-dd')
          }
        }
      },
      {
        prop: 'differenceReason',
        label: '差异原因',
        minWidth: '200px',
        showOverflowTooltip: false,
        slots: {
          default: (scope) => {
            return <DifferenceReasonInfo info={scope.row.differenceReason} />
          }
        }
      }
    ]
  },
  {
    name: '入库通知单',
    type: 23,
    colums: [
      {
        prop: 'code',
        label: '通知单号',
        minWidth: columnWidth.l,
        isHyperlink: true
      },
      {
        prop: 'companyName',
        label: '归属公司名称',
        minWidth: columnWidth.m
      },
      {
        prop: 'expectDate',
        label: '预计到货日期',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return formatTime(row.orderMsg?.expectDate, 'yyyy-MM-dd')
          }
        }
      },
      {
        prop: 'totalWeight',
        label: '总毛重',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row.orderMsg?.totalWeight?.weight} ${row.orderMsg?.totalWeight?.unit}`
          }
        }
      },
      {
        prop: 'totalVolume',
        label: '总体积(m³)',
        minWidth: columnWidth.m,
        slots: {
          default: (data) => {
            const { row } = data
            return `${row.orderMsg?.totalVolume}`
          }
        }
      },
      {
        prop: 'noticeTime',
        label: '通知时间',
        minWidth: columnWidth.l,
        slots: {
          default: (data) => {
            const { row } = data
            return formatTime(row.orderMsg?.noticeTime, 'yyyy-MM-dd HH:mm:ss')
          }
        }
      }
    ]
  }
]
