<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <!-- <template #tableActions>
      <el-button
        type="success"
        plain
        @click="handleDetail"
      >
        详情
      </el-button>
    </template> -->
  </eplus-table>

  <eplus-dialog
    ref="eplusDialogRef"
    :destroyOnClose="true"
    @success="handleRefresh"
  >
    <template #detail="{ key }"> <YsbillDetail :id="key" /></template>

    <template #edit="{ key }">
      <YsbillForm
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { ElMessageBox } from 'element-plus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusTableSchema } from '@/types/eplus'
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import YsbillDetail from './YsbillDetail.vue'
import YsbillForm from './YsbillForm.vue'
import * as BillApi from '@/api/wms/bill'
import { EplusBadge } from '@/components/EplusBadge'
import * as UserApi from '@/api/system/user'
import * as DeptApi from '@/api/system/dept'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { getSourceId, removeSourceId } from '@/utils/auth'

defineOptions({ name: 'Ysbill' })

const message = useMessage()
const eplusDialogRef = ref()
const eplusListRef = ref()
const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'code',
      label: '单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'companyName',
      label: '公司名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'noticeCode',
      label: '通知单号'
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'createTime',
          label: '创建日期',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'billTime',
          label: '入库时间',
          formatter: '从{0}到{1}'
        }
      ]
    },
    {
      component: <el-input clearable></el-input>,
      name: 'venderName',
      label: '供应商名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'managerId',
      label: '跟单员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'purchaserId',
      label: '采购员',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <eplus-dept-select placeholder="请选择采购员部门"></eplus-dept-select>,
      name: 'purchaserDeptId',
      label: '采购员部门',
      formatter: async (args: any[]) => {
        var dept = await DeptApi.getSimpleDept(args[0])
        return dept.name
      }
    }
  ],
  moreFields: []
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await BillApi.getBillPage({ ...ps, billType: 1 })
    res.list.forEach((parent) => {
      parent.children?.forEach((item) => {
        item.billStatus = parent.billStatus
      })
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    // await BillApi.deletePurchaseContract(id)
  },
  showTabs: true,
  tabs: [
    {
      label: '产品',
      isTree: true,
      columns: [
        {
          prop: 'code',
          label: '单号',
          parent: true,
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'billStatus',
          label: '单据状态',
          parent: true,
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.STOCK_BILL_STATUS)
        },
        {
          prop: 'companyName',
          label: '公司名称',
          parent: true,
          minWidth: columnWidth.l
        },
        {
          prop: 'billTime',
          label: '入库日期',
          parent: true,
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'creatorName',
          label: '创建人',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorDeptName',
          label: '创建人部门',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'createTime',
          label: '创建日期',
          parent: true,
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: columnWidth.l,
          parent: true,
          fixed: 'right',
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="flex">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['wms:bill:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: row.billStatus == 1,
                        otherKey: 'billUpdate',
                        label: '编辑',
                        permi: 'wms:bill:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: row.billStatus == 2,
                        otherKey: 'billCancel',
                        label: '作废',
                        permi: 'wms:bill:cancel',
                        handler: async (row) => {
                          handleCancel(row.id)
                        }
                      },
                      {
                        isShow: row.billStatus == 1,
                        otherKey: 'billSubmit',
                        label: '提交',
                        permi: 'wms:bill:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      }
                    ]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        },
        {
          prop: 'code9',
          label: '图片',
          minWidth: columnWidth.s,
          slots: {
            default: (data) => {
              const { row } = data
              if (row.mainPicture) {
                return <EplusImgEnlarge mainPicture={row.mainPicture} />
              } else {
                return '-'
              }
            }
          }
        },
        {
          prop: 'skuCode',
          label: '产品编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.l
        },
        {
          prop: 'cskuCode',
          label: '客户货号',
          minWidth: columnWidth.l
        },
        {
          prop: 'skuName',
          label: '产品名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'orderQuantity',
          label: '应收数量',
          minWidth: columnWidth.m
        },
        {
          prop: 'orderBoxQuantity',
          label: '应收箱数',
          minWidth: columnWidth.m
        },
        {
          prop: 'actQuantity',
          label: '实收数量',
          minWidth: columnWidth.m
        },
        {
          prop: 'noticeItemStatus',
          label: '入库状态',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <EplusBadge
                  badgeValue={getDictLabel(DICT_TYPE.ABNORMAL_STATUS, row?.abnormalStatus)}
                  contentValue={getDictLabel(DICT_TYPE.BILL_STATUS, row?.noticeItemStatus)}
                  isBadgeShow={row?.abnormalStatus == null ? false : true}
                />
              )
            }
          }
        },
        {
          prop: 'batchCode',
          label: '批次编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'venderName',
          label: '供应商名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaserName',
          label: '采购员（部门）',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.purchaserDeptName
              ? `${row.purchaserName}(${row.purchaserDeptName})`
              : row.purchaserName
          }
        },
        {
          prop: 'manager',
          label: '跟单员（部门）',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return row.manager?.deptName
              ? `${row.manager?.nickname}(${row.manager?.deptName})`
              : row.manager?.nickname
          }
        }
      ]
    },
    {
      label: '单据',
      isDefault: true,
      columns: [
        {
          prop: 'code',
          label: '单号',
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'billStatus',
          label: '单据状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.STOCK_BILL_STATUS)
        },
        {
          prop: 'companyName',
          label: '公司名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'billTime',
          label: '入库时间',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'venderName',
          label: '供应商',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return [
              ...new Set(
                row.children.map((item) => {
                  return item.venderName
                })
              )
            ].join(',')
          }
        },
        {
          prop: 'noticeCode',
          label: '通知单号',
          minWidth: columnWidth.m
        },
        {
          prop: 'remark',
          label: '备注',
          minWidth: columnWidth.l,
          showOverflowTooltip: true
        },
        {
          prop: 'creatorName',
          label: '创建人',
          minWidth: columnWidth.m
        },
        {
          prop: 'creatorDeptName',
          label: '创建人部门',
          minWidth: columnWidth.m
        },
        {
          prop: 'createTime',
          label: '创建时间',
          minWidth: columnWidth.l,
          formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: columnWidth.l,
          fixed: 'right',
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="flex items-center">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['wms:bill:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow: row.billStatus == 1,
                        otherKey: 'billUpdate',
                        label: '编辑',
                        permi: 'wms:bill:update',
                        handler: async (row) => {
                          handleUpdate(row.id)
                        }
                      },
                      {
                        isShow: row.billStatus == 2,
                        otherKey: 'billCancel',
                        label: '作废',
                        handler: async (row) => {
                          handleCancel(row.id)
                        }
                      }
                    ]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        }
      ]
    }
  ]
}

const handleDetail = (id: number) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id, '入库单')
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

const handleCancel = (id: number) => {
  ElMessageBox.confirm('确认作废该入库单吗？', '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确认',
    type: 'warning'
  })
    .then(async () => {
      await BillApi.cancelBill(id)
      message.success('操作成功')
      eplusListRef.value.handleRefresh()
    })
    .catch(() => {})
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
