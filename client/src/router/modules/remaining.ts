import { Layout } from '@/utils/routerHelper'

const { t } = useI18n()
/**
 * redirect: noredirect        当设置 noredirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'          设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * meta : {
 hidden: true              当设置 true 的时候该路由不会再侧边栏出现 如404，login等页面(默认 false)

 alwaysShow: true          当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式，
 只有一个时，会将那个子路由当做根路由显示在侧边栏，
 若你想不管路由下面的 children 声明的个数都显示你的根路由，
 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，
 一直显示根路由(默认 false)

 title: 'title'            设置该路由在侧边栏和面包屑中展示的名字

 icon: 'svg-name'          设置该路由的图标

 noCache: true             如果设置为true，则不会被 <keep-alive> 缓存(默认 false)

 breadcrumb: false         如果设置为false，则不会在breadcrumb面包屑中显示(默认 true)

 affix: true               如果设置为true，则会一直固定在tag项中(默认 false)

 noTagsView: true          如果设置为true，则不会出现在tag中(默认 false)

 activeMenu: '/dashboard'  显示高亮的路由路径

 followAuth: '/dashboard'  跟随哪个路由进行权限过滤

 approachable: true               设置为true即使hidden为true，也依然可以进行路由跳转(默认 false)
 }
 **/
const remainingRouter: AppRouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layout,
    name: 'Redirect',
    children: [
      {
        path: '/redirect/:path(.*)',
        name: 'Redirect2',
        component: () => import('@/views/Redirect/Redirect.vue'),
        meta: {}
      }
    ],
    meta: {
      hidden: true,
      noTagsView: true
    }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/index',
    name: 'Home',
    meta: { hidden: true },
    children: [
      {
        path: 'index',
        component: () => import('@/views/Home/Index.vue'),
        name: 'Index',
        meta: {
          title: t('router.home'),
          icon: 'ep:home-filled',
          noCache: false,
          affix: true,
          hidden: true
        }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    name: 'UserInfo',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'profile',
        component: () => import('@/views/Profile/Index.vue'),
        name: 'Profile',
        meta: {
          approachable: true,
          hidden: true,
          noTagsView: false,
          icon: 'ep:user',
          title: t('common.profile')
        }
      },
      {
        path: 'notify-message',
        component: () => import('@/views/system/notify/my/index.vue'),
        name: 'MyNotifyMessage',
        meta: {
          approachable: true,
          hidden: true,
          noTagsView: false,
          icon: 'ep:message',
          title: '我的站内信'
        }
      }
    ]
  },
  //系统模块页面
  {
    path: '/system',
    component: Layout,
    name: 'System',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index.vue'),
        name: 'SystemUser',
        meta: {
          approachable: true,
          hidden: true,
          noTagsView: false,
          icon: 'ep:user',
          title: '用户管理'
        }
      }
    ]
  },

  {
    path: '/dict',
    component: Layout,
    name: 'dict',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'type/data/:dictType',
        component: () => import('@/views/system/dict/data/index.vue'),
        name: 'SystemDictData',
        meta: {
          title: '字典数据',
          noCache: true,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/system/dict'
        }
      }
    ]
  },

  {
    path: '/codegen',
    component: Layout,
    name: 'CodegenEdit',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'edit',
        component: () => import('@/views/infra/codegen/EditTable.vue'),
        name: 'InfraCodegenEditTable',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          icon: 'ep:edit',
          title: '修改生成配置',
          activeMenu: 'infra/codegen/index'
        }
      }
    ]
  },
  {
    path: '/job',
    component: Layout,
    name: 'JobL',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'job-log',
        component: () => import('@/views/infra/job/logger/index.vue'),
        name: 'InfraJobLog',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          icon: 'ep:edit',
          title: '调度日志',
          activeMenu: 'infra/job/index'
        }
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    name: 'Login',
    meta: {
      hidden: true,
      title: t('router.login'),
      noTagsView: true
    }
  },
  // {
  //   path: '/sso',
  //   component: () => import('@/views/Login/Login.vue'),
  //   name: 'SSOLogin',
  //   meta: {
  //     hidden: true,
  //     title: t('router.login'),
  //     noTagsView: true
  //   }
  // },
  {
    path: '/403',
    component: () => import('@/views/Error/403.vue'),
    name: 'NoAccess',
    meta: {
      hidden: true,
      title: '403',
      noTagsView: true
    }
  },
  {
    path: '/404',
    component: () => import('@/views/Error/404.vue'),
    name: 'NoFound',
    meta: {
      hidden: true,
      title: '404',
      noTagsView: true
    }
  },
  {
    path: '/500',
    component: () => import('@/views/Error/500.vue'),
    name: 'Error',
    meta: {
      hidden: true,
      title: '500',
      noTagsView: true
    }
  },
  {
    path: '/bpm',
    component: Layout,
    name: 'bpm',
    meta: {
      hidden: true
    },
    children: [
      {
        path: '/manager/form/edit',
        component: () => import('@/views/bpm/form/editor/index.vue'),
        name: 'BpmFormEditor',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '设计流程表单',
          activeMenu: '/bpm/manager/form'
        }
      },
      {
        path: '/manager/model/edit',
        component: () => import('@/views/bpm/model/editor/index.vue'),
        name: 'BpmModelEditor',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '设计流程',
          activeMenu: '/bpm/manager/model'
        }
      },
      {
        path: '/manager/definition',
        component: () => import('@/views/bpm/definition/index.vue'),
        name: 'BpmProcessDefinition',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '流程定义',
          activeMenu: '/bpm/manager/model'
        }
      },
      {
        path: '/manager/task-assign-rule',
        component: () => import('@/views/bpm/taskAssignRule/index.vue'),
        name: 'BpmTaskAssignRuleList',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '任务分配规则'
        }
      },
      {
        path: '/process-instance/create',
        component: () => import('@/views/bpm/processInstance/create/index.vue'),
        name: 'BpmProcessInstanceCreate',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '发起流程',
          activeMenu: 'bpm/processInstance/create'
        }
      },
      {
        path: '/process-instance/detail',
        component: () => import('@/views/bpm/processInstance/detail/index.vue'),
        name: 'BpmProcessInstanceDetail',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '流程详情',
          activeMenu: 'bpm/processInstance/detail'
        }
      },
      {
        path: '/bpm/oa/leave/create',
        component: () => import('@/views/bpm/oa/leave/create.vue'),
        name: 'OALeaveCreate',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '发起 OA 请假',
          activeMenu: '/bpm/oa/leave'
        }
      },
      {
        path: '/bpm/oa/leave/detail',
        component: () => import('@/views/bpm/oa/leave/detail.vue'),
        name: 'OALeaveDetail',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '查看 OA 请假',
          activeMenu: '/bpm/oa/leave'
        }
      }
    ]
  },

  //  OA
  {
    path: '/oa',
    name: 'oa',
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: '/loan-app/detail',
        component: () => import('@/views/oa/loanapp/LoanAppDetail.vue'),
        name: 'LoanAppDetail',
        meta: {
          noCache: true,
          hidden: true,
          approachable: true,
          title: '查看详情',
          activeMenu: '/oa/loan-app'
        }
      }
      // {
      //   path: '/apply/entertain/detail/:id',
      //   component: () => import('@/views/oa/apply/entertain/EntertainDetail.vue'),
      //   name: 'EntertainDetail',
      //   meta: {
      //     noCache: true,
      //     hidden: true,
      //     approachable: true,
      //     title: '查看详情',
      //     activeMenu: '/oa/apply/entertainIndex'
      //   }
      // }
    ]
  },

  //审核页面开发用
  {
    path: '/',
    component: Layout,
    name: 'crm',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'audit',
        component: () => import('@/views/audit/cust/index.vue'),
        name: 'Audit',
        meta: {
          hidden: true,
          title: '审核列表',
          noTagsView: true
        }
      },
      {
        path: 'audit-detail',
        component: () => import('@/views/audit/cust/detail.vue'),
        name: 'AuditDetail',
        meta: {
          hidden: true,
          title: '审核详情',
          noTagsView: true
        }
      }
    ]
  },

  //数据权限页面test
  {
    path: '/',
    component: Layout,
    name: 'premi',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'fieldPremi',
        component: () => import('@/views/system/role/fieldPermi/index.vue'),
        name: 'FieldPremi',
        meta: {
          hidden: true,
          title: '字段权限test'
        }
      }
    ]
  },
  //销售合同详情
  {
    path: '/sms',
    component: Layout,
    name: 'sms',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'sale-orders/ExportSaleContractDetail/:id',
        component: () => import('@/views/sms/sale/exportSaleContract/ExportSaleContractDetail.vue'),
        name: 'ExportSaleContractDetail',
        meta: {
          title: '销售合同详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/sms/sale-orders/exportSaleContract'
        }
      },
      {
        path: 'sale-orders/SaleContractDetail/:id',
        component: () => import('@/views/sms/sale/saleContract/SaleContractDetail.vue'),
        name: 'SaleContractDetail',
        meta: {
          title: '销售合同详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/sms/sale-orders/saleContract'
        }
      }
    ]
  },
  //采购合同详情
  {
    path: '/scm',
    component: Layout,
    name: 'scm',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'purchase/ContractDetail/:id',
        component: () => import('@/views/scm/purchase/contract/ContractDetail.vue'),
        name: 'ContractDetail',
        meta: {
          title: '采购合同详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/scm/purchase/contract'
        }
      }
    ]
  },
  // 出运明细详情
  {
    path: '/dms',
    component: Layout,
    name: 'dms',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'shipping-manage/shippingDetail/:id',
        component: () => import('@/views/dms/shipping/ShippingDetail.vue'),
        name: 'ShippingDetail',
        meta: {
          title: '出运明细详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/dms/shipping-manage/shipping'
        }
      },
      {
        path: 'containerNoticeDetail/:id',
        component: () => import('@/views/dms/containerNotice/ContainerNoticeDetail.vue'),
        name: 'ContainerNoticeDetail',
        meta: {
          title: '拉柜通知单详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/dms/ContainerNotice'
        }
      },
      {
        path: 'shipping-orders/commodityInspectionDetail/:id',
        component: () => import('@/views/dms/commodityInspection/CommodityInspectionDetail.vue'),
        name: 'CommodityInspectionDetail',
        meta: {
          title: '商检单详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/dms/shipping-orders/commodityInspection'
        }
      },
      {
        path: 'shipping-orders/declarationDetail/:id',
        component: () => import('@/views/dms/declaration/DeclarationDetail.vue'),
        name: 'DeclarationDetail',
        meta: {
          title: '报关单详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/dms/shipping-orders/declaration'
        }
      },
      {
        path: 'shipping-orders/settlementFormDetail/:id',
        component: () => import('@/views/dms/settlementForm/SettlementFormDetail.vue'),
        name: 'SettlementFormDetail',
        meta: {
          title: '结汇单详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/dms/shipping-orders/settlementForm'
        }
      }
    ]
  },
  // oa
  {
    path: '/oa',
    component: Layout,
    name: 'oa',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'apply/entertain/detail/:id',
        component: () => import('@/views/oa/apply/entertain/EntertainDetail.vue'),
        name: 'EntertainDetail',
        meta: {
          title: '招待费申请详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/oa/apply/entertainIndex'
        }
      },
      {
        path: 'apply/travel/detail/:id',
        component: () => import('@/views/oa/apply/travel/TravelDetail.vue'),
        name: 'TravelDetail',
        meta: {
          noCache: false,
          hidden: true,
          approachable: true,
          title: '出差费用申请详情',
          activeMenu: '/oa/apply/travelIndex'
        }
      },
      {
        path: 'apply/general/detail/:id',
        component: () => import('@/views/oa/apply/general/GeneralDetail.vue'),
        name: 'GeneralDetail',
        meta: {
          noCache: false,
          hidden: true,
          approachable: true,
          title: '费用申请详情',
          activeMenu: '/oa/apply/generalIndex'
        }
      }
    ]
  },
  {
    path: '/base',
    component: Layout,
    name: 'base',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'product-manage/mainDetail/:id',
        component: () => import('@/views/pms/product/main/MainDetail.vue'),
        name: 'SkuMainDetail',
        meta: {
          title: '基础产品详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/base/product-manage/main'
        }
      },
      {
        path: 'product-manage/cskuDetail/:id',
        component: () => import('@/views/pms/product/cust/custProductDetail.vue'),
        name: 'CustProductDetail',
        meta: {
          title: '客户产品详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/base/product-manage/csku'
        }
      },
      {
        path: 'product-manage/ownDetail/:id',
        component: () => import('@/views/pms/product/own/ownProductDetail.vue'),
        name: 'OwnProductDetail',
        meta: {
          title: '自营产品详情',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/base/product-manage/own'
        }
      }
    ]
  },
  //测试页面(已移除)
  // WMS 库存管理
  {
    path: '/wms',
    component: Layout,
    name: 'wms',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'import-result',
        component: () => import('@/views/wms/stock/components/ImportResult.vue'),
        name: 'StockImportResult',
        meta: {
          title: '库存导入结果',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/wms/stock'
        }
      }
    ]
  },
  // EMS 寄件导入结果
  {
    path: '/ems',
    component: Layout,
    name: 'ems',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'import-result',
        component: () => import('@/views/ems/send/components/ImportResult.vue'),
        name: 'EmsImportResult',
        meta: {
          title: '寄件导入结果',
          noCache: false,
          hidden: true,
          approachable: true,
          icon: '',
          activeMenu: '/ems/send'
        }
      }
    ]
  }
]

export default remainingRouter
