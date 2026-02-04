<template>
  <div class="h-55px bg-[var(--left-menu-bg-color)] pl-15px pr-15px pt-10px">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        name="first"
      />
      <el-tab-pane
        label="待审批"
        name="second"
      />
      <el-tab-pane
        label="已处理"
        name="third"
      />
      <el-tab-pane
        label="已驳回"
        name="fourth"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableActions>
      <el-button
        type="primary"
        plain
        @click="handleCreate()"
        v-hasPermi="['crm:cust:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增客户
      </el-button>
      <el-button
        type="success"
        plain
        @click="handleExport()"
        v-hasPermi="['crm:cust:export']"
      >
        <Icon
          icon="ep:download"
          class="mr-5px"
        />
        导出
      </el-button>
      <el-dropdown @command="handleCommand">
        <el-button
          type="primary"
          style="margin-left: 10px"
        >
          更多<Icon :icon="'ep:arrow-down'" />
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="dict in getIntDictOptions(DICT_TYPE.SCM_STATUS_TYPE)"
              :command="dict.label"
              :key="dict.value"
              >{{ dict.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </template>
    <template #tableActionsFixRight>
      <el-button
        type="primary"
        plain
        @click="handleSee()"
        v-hasPermi="['crm:cust:create']"
      >
        采购看板
      </el-button>
    </template>
  </eplus-table>
</template>

<script setup lang="tsx">
import { EplusTableSchema } from '@/types/eplus'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as api from '@/api/crm/cust'
import { useCountryStore } from '@/store/modules/countrylist'
import { formatDateColumn, formatDictColumn } from '@/utils/table'
import { Icon } from '@/components/Icon'
import { ElImage } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

const eplusListRef = ref()
const activeName = ref('first')
const handleTabsClick = () => {}
const handleCommand = (command) => {
  console.log('已选择：' + command)
}

const exportFileName = ref('test.xlsx') // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

// 获取国家列表
const countryStore = useCountryStore()
const countryData = JSON.parse(JSON.stringify(countryStore.countryList))

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'name',
      label: '客户名称',
      className: '!w-200px'
    }
  ],
  moreFields: []
}

const counterFormatter = (row, column) => {
  let result: any = countryData.filter((item) => {
    return item.id === row.countryId
  })
  return <el-tag>{result[0]?.[column.property == 'countryId' ? 'name' : 'regionName']}</el-tag>
}

const quantityFormatter = (formatValue) => {
  return formatValue + 'dd'
}

const itemListFormatter = (row, showProp) => {
  // let content: any = row.children?.map((item) => {
  //   return  <span style="margin-right:5px">{item[showProp]}</span>
  // })
  // return <div>{content}</div>
  const url =
    'https://image.distributetop.com/erp-vue/90136230241538560/20240422/155c61ffef7a44b4b02652aac85ef592.jpeg'
  return (
    <ElImage
      src={url}
      style="width:30px;height:30px"
    ></ElImage>
  )
}

const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await api.getCustPage(ps)
    //--币种模拟测试数据添加
    res.list?.forEach((it, index) => {
      it.totalAmountList = [{ amount: 100.12 + index, currency: index % 2 ? 'RMB' : 'RMB' }]
      it.quantity = {
        old_quantity: '1000',
        quantity: '2000'
      }
    })
    var parent = [
      {
        id: 1,
        plan_code: 'CG0001',
        purchase_user_name: '张三',
        plan_date: '2022-01-09 11:10:00',
        children: res.list
      },
      {
        id: 2,
        plan_code: 'CG0002',
        purchase_user_name: '李四',
        plan_date: '2022-01-09 11:10:00',
        children: res.list
      }
    ]
    return {
      list: parent,
      sum: { ver: 100, totalAmountList: '100 RMB,101 RMB' },
      total: 2
    }
  },
  delListApi: async (id) => {
    await api.deleteCust(id)
  },
  exportListApi: async (ps) => {
    return await api.exportCust(ps)
  },
  selection: true,
  summary: true,
  columns: [
    {
      prop: 'plan_code',
      label: '采购计划编号',
      minWidth: '180px',
      isCopy: true,
      parent: true
    },
    {
      prop: 'plan_date',
      label: '采购计划时间',
      minWidth: '250px',
      isCopy: true,
      parent: true,
      formatter: formatDateColumn('YYYY-MM-DD')
    },
    {
      prop: 'purchase_user_name',
      label: '采购员',
      minWidth: '200px',
      isCopy: true,
      parent: true
    },
    {
      prop: 'code',
      label: '客户编号',
      isCopy: true,
      fixed: 'left',
      minWidth: '180px'
    },
    {
      prop: 'ver',
      label: '版本',
      isCopy: true,
      fixed: 'left',
      summary: true,
      minWidth: '250px'
    },
    {
      prop: 'quantity',
      label: '采购量',
      isCompare: true,
      formatter: quantityFormatter
    },
    {
      prop: 'totalAmountList',
      label: '报销金额',
      minWidth: '150px',
      sortable: 'custom',
      sortField: 'amount,currency:asc',
      summary: true,
      summarySlots: {
        default: (data) => {
          //-- unit:单位 number:值
          var obj = {
            unit: data[0].currency,
            number: data[0].amount
          }
          return obj
        }
      },
      slots: {
        default: (data) => {
          const { row } = data
          return row.totalAmountList
            ? row.totalAmountList[0].amount + ' ' + row.totalAmountList[0].currency
            : ''
        }
      }
    },
    {
      prop: 'name',
      label: '客户名称',
      minWidth: '120px',
      fixed: 'left',
      extend: [
        {
          prop: 'shortname',
          label: '简称',
          minWidth: '100px'
        },
        {
          prop: 'phone',
          label: '联系人电话',
          minWidth: '200px'
        }
      ]
    },
    {
      prop: 'counReg',
      label: '国家/地区',
      minWidth: '1000px',
      direction: 'column',
      isTooltip: true,
      items: [
        {
          prop: 'countryId',
          label: '国家',
          formatter: counterFormatter,
          isCopy: true,
          minWidth: '150px'
        },
        {
          prop: 'regionName',
          label: '所属地区',
          formatter: counterFormatter,
          isCopy: true,
          minWidth: '120px'
        }
      ]
    },
    {
      prop: 'customerTypes',
      label: '客户类型',
      formatter: formatDictColumn(DICT_TYPE.CUSTOM_TYPE),
      minWidth: '180px',
      tooltip: '地址说明地址说明地址说明地地址说明地址说明地址说明地址说明地址说明地址说明'
    },
    {
      prop: 'agentFlag',
      label: '是否联营',
      formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE),
      minWidth: '150px',
      sortable: 'custom'
    },
    {
      prop: 'sourceType',
      label: '客户来源',
      minWidth: '120px',
      formatter: formatDictColumn(DICT_TYPE.SOURCE_TYPE),
      sortable: 'custom'
    },
    {
      prop: 'creditFlag_currency',
      label: '信用额度_电话',
      minWidth: '100px',
      items: [
        {
          prop: 'creditFlag',
          label: '信用额度',
          formatter: (_, __, cv) => {
            return <el-tag>{cv ? '开启' : '关闭'}</el-tag>
          },
          isCopy: true,
          minWidth: '100px'
        },
        {
          prop: 'phone',
          label: '联系人电话',
          isCopy: true,
          minWidth: '200px'
        }
      ]
    },
    {
      prop: 'currency',
      label: '币种',
      formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE),
      minWidth: '80px'
    },
    {
      prop: 'settlementTermType',
      label: '价格条款',
      formatter: formatDictColumn(DICT_TYPE.PRICE_TYPE),
      minWidth: '100px'
    },
    {
      prop: 'auditStatus',
      label: '审核状态',
      formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
      minWidth: '100px'
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: '120px',
      fixed: 'right',
      parent: true,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div
              class="flex items-center"
              style="justify-content:space-around"
            >
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['crm:cust:detail']"
              >
                <Icon icon="ep:document" /> 详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  permi: 'crm:cust:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                editItem={{
                  permi: 'crm:cust:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  permi: 'crm:cust:delete',
                  handler: async (row) => {
                    await handleDelete(row.id).then(() => {
                      {
                        /* api.getCustPage({page}) */
                      }
                    })
                  }
                }}
                otherItems={[]}
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: '150px',
      fixed: 'right',
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div
              class="flex items-center justify-between"
              style="justify-content:space-around"
            >
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['crm:cust:detail']"
              >
                <Icon icon="ep:document" /> 明细详情
              </el-button>
              <eplus-dropdown
                submitItem={{
                  permi: 'crm:cust:submit',
                  handler: async (row) => {
                    await handleSubmit(row)
                  }
                }}
                editItem={{
                  permi: 'crm:cust:update',
                  handler: () => {
                    handleUpdate(row.id)
                  }
                }}
                deleteItem={{
                  permi: 'crm:cust:delete',
                  handler: async (row) => {
                    await handleDelete(row.id).then(() => {
                      {
                        /* api.getCustPage({page}) */
                      }
                    })
                  }
                }}
                otherItems={[]}
                auditable={row}
              ></eplus-dropdown>
            </div>
          )
        }
      }
    }
  ],
  //**是否包含多个表格显示 */
  showTabs: false,
  tabs: [
    {
      label: '单据',
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'plan_code',
          label: '采购计划编号',
          minWidth: '180px'
        },
        {
          prop: 'plan_date',
          label: '采购计划时间',
          minWidth: '250px',
          isCopy: true
        },
        {
          prop: 'purchase_user_name',
          label: '采购员',
          minWidth: '200px'
        },
        {
          prop: 'children',
          label: '采购产品',
          minWidth: '200px',
          direction: 'column',
          showProp: 'code',
          formatter: itemListFormatter,
          columns: [
            {
              prop: 'code',
              label: '客户编号',
              minWidth: '180px'
            },
            {
              prop: 'ver',
              label: '版本',
              minWidth: '100px'
            },
            {
              prop: 'totalAmountList',
              label: '报销金额',
              minWidth: '200px',
              sortable: 'custom',
              sortField: 'amount,currency:asc',
              slots: {
                default: (data) => {
                  const { row } = data
                  return row.totalAmountList
                    ? row.totalAmountList[0].amount + ' ' + row.totalAmountList[0].currency
                    : ''
                }
              }
            },
            {
              prop: 'name',
              label: '客户名称',
              minWidth: '120px'
            },
            {
              prop: 'counReg',
              label: '国家/地区',
              minWidth: '100px',
              direction: 'column',
              isTooltip: true,
              items: [
                {
                  prop: 'countryId',
                  label: '国家',
                  formatter: counterFormatter,
                  isCopy: true,
                  minWidth: '150px'
                },
                {
                  prop: 'regionName',
                  label: '所属地区',
                  formatter: counterFormatter,
                  isCopy: true,
                  minWidth: '120px'
                }
              ]
            },
            {
              prop: 'customerTypes',
              label: '客户类型',
              formatter: formatDictColumn(DICT_TYPE.CUSTOM_TYPE),
              minWidth: '180px',
              tooltip: '地址说明地址说明地址说明地地址说明地址说明地址说明地址说明地址说明地址说明'
            },
            {
              prop: 'agentFlag',
              label: '是否联营',
              formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE),
              minWidth: '150px',
              sortable: 'custom'
            },
            {
              prop: 'sourceType',
              label: '客户来源',
              minWidth: '120px',
              formatter: formatDictColumn(DICT_TYPE.SOURCE_TYPE),
              sortable: 'custom'
            },
            {
              prop: 'creditFlag_currency',
              label: '信用额度_电话',
              minWidth: '150px',
              items: [
                {
                  prop: 'creditFlag',
                  label: '信用额度',
                  formatter: (_, __, cv) => {
                    return <el-tag>{cv ? '开启' : '关闭'}</el-tag>
                  },
                  isCopy: true,
                  minWidth: '100px'
                },
                {
                  prop: 'phone',
                  label: '联系人电话',
                  isCopy: true,
                  minWidth: '200px'
                }
              ]
            },
            {
              prop: 'currency',
              label: '币种',
              formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE),
              minWidth: '80px'
            },
            {
              prop: 'settlementTermType',
              label: '价格条款',
              formatter: formatDictColumn(DICT_TYPE.PRICE_TYPE),
              minWidth: '100px'
            },
            {
              prop: 'auditStatus',
              label: '审核状态',
              formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
              minWidth: '100px'
            }
          ]
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: '150px',
          fixed: 'right',
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div
                  class="flex items-center justify-between"
                  style="justify-content:space-around"
                >
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['crm:cust:detail']"
                  >
                    <Icon icon="ep:document" /> 明细详情
                  </el-button>
                  <eplus-dropdown
                    submitItem={{
                      permi: 'crm:cust:submit',
                      handler: async (row) => {
                        await handleSubmit(row)
                      }
                    }}
                    editItem={{
                      permi: 'crm:cust:update',
                      handler: () => {
                        handleUpdate(row.id)
                      }
                    }}
                    deleteItem={{
                      permi: 'crm:cust:delete',
                      handler: async (row) => {
                        await handleDelete(row.id).then(() => {
                          {
                            /* api.getCustPage({page}) */
                          }
                        })
                      }
                    }}
                    otherItems={[]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        }
      ]
    },
    {
      label: '产品',
      isTree: true,
      selection: true,
      summary: true,
      columns: [
        {
          prop: 'plan_code',
          label: '采购计划编号',
          minWidth: '180px',
          isCopy: true,
          parent: true
        },
        {
          prop: 'plan_date',
          label: '采购计划时间',
          minWidth: '250px',
          isCopy: true,
          parent: true
        },
        {
          prop: 'purchase_user_name',
          label: '采购员',
          minWidth: '200px',
          isCopy: true,
          parent: true
        },
        {
          prop: 'code',
          label: '客户编号',
          isCopy: true,
          fixed: 'left',
          minWidth: '180px'
        },
        {
          prop: 'ver',
          label: '版本',
          isCopy: true,
          fixed: 'left',
          summary: true,
          minWidth: '250px'
        },
        {
          prop: 'totalAmountList',
          label: '报销金额',
          minWidth: '150px',
          sortable: 'custom',
          sortField: 'amount,currency:asc',
          slots: {
            default: (data) => {
              const { row } = data
              return row.totalAmountList
                ? row.totalAmountList[0].amount + ' ' + row.totalAmountList[0].currency
                : ''
            }
          }
        },
        {
          prop: 'name',
          label: '客户名称',
          minWidth: '120px',
          fixed: 'left',
          tooltip: '客户名称',
          extend: [
            {
              prop: 'shortname',
              label: '简称',
              minWidth: '100px'
            },
            {
              prop: 'phone',
              label: '联系人电话',
              minWidth: '200px'
            }
          ]
        },
        {
          prop: 'counReg',
          label: '国家/地区',
          minWidth: '1000px',
          direction: 'column',
          isTooltip: true,
          items: [
            {
              prop: 'countryId',
              label: '国家',
              formatter: counterFormatter,
              isCopy: true,
              minWidth: '150px'
            },
            {
              prop: 'regionName',
              label: '所属地区',
              formatter: counterFormatter,
              isCopy: true,
              minWidth: '120px'
            }
          ]
        },
        {
          prop: 'customerTypes',
          label: '客户类型',
          formatter: formatDictColumn(DICT_TYPE.CUSTOM_TYPE),
          minWidth: '180px',
          tooltip: '地址说明地址说明地址说明地地址说明地址说明地址说明地址说明地址说明地址说明'
        },
        {
          prop: 'agentFlag',
          label: '是否联营',
          formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE),
          minWidth: '150px',
          sortable: 'custom'
        },
        {
          prop: 'sourceType',
          label: '客户来源',
          minWidth: '120px',
          formatter: formatDictColumn(DICT_TYPE.SOURCE_TYPE),
          sortable: 'custom'
        },
        {
          prop: 'creditFlag_currency',
          label: '信用额度_电话',
          minWidth: '100px',
          items: [
            {
              prop: 'creditFlag',
              label: '信用额度',
              formatter: (_, __, cv) => {
                return <el-tag>{cv ? '开启' : '关闭'}</el-tag>
              },
              isCopy: true,
              minWidth: '100px'
            },
            {
              prop: 'phone',
              label: '联系人电话',
              isCopy: true,
              minWidth: '200px'
            }
          ]
        },
        {
          prop: 'currency',
          label: '币种',
          formatter: formatDictColumn(DICT_TYPE.CURRENCY_TYPE),
          minWidth: '80px'
        },
        {
          prop: 'settlementTermType',
          label: '价格条款',
          formatter: formatDictColumn(DICT_TYPE.PRICE_TYPE),
          minWidth: '100px'
        },
        {
          prop: 'auditStatus',
          label: '审核状态',
          formatter: formatDictColumn(DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT),
          minWidth: '100px'
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: '120px',
          fixed: 'right',
          parent: true,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div
                  class="flex items-center"
                  style="justify-content:space-around"
                >
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['crm:cust:detail']"
                  >
                    <Icon icon="ep:document" /> 详情
                  </el-button>
                  <eplus-dropdown
                    submitItem={{
                      permi: 'crm:cust:submit',
                      handler: async (row) => {
                        await handleSubmit(row)
                      }
                    }}
                    editItem={{
                      permi: 'crm:cust:update',
                      handler: () => {
                        handleUpdate(row.id)
                      }
                    }}
                    deleteItem={{
                      permi: 'crm:cust:delete',
                      handler: async (row) => {
                        await handleDelete(row.id).then(() => {
                          {
                            /* api.getCustPage({page}) */
                          }
                        })
                      }
                    }}
                    otherItems={[]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        },
        {
          prop: 'action',
          label: '操作',
          minWidth: '150px',
          fixed: 'right',
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div
                  class="flex items-center justify-between"
                  style="justify-content:space-around"
                >
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['crm:cust:detail']"
                  >
                    <Icon icon="ep:document" /> 明细详情
                  </el-button>
                  <eplus-dropdown
                    submitItem={{
                      permi: 'crm:cust:submit',
                      handler: async (row) => {
                        await handleSubmit(row)
                      }
                    }}
                    editItem={{
                      permi: 'crm:cust:update',
                      handler: () => {
                        handleUpdate(row.id)
                      }
                    }}
                    deleteItem={{
                      permi: 'crm:cust:delete',
                      handler: async (row) => {
                        await handleDelete(row.id).then(() => {
                          {
                            /* api.getCustPage({page}) */
                          }
                        })
                      }
                    }}
                    otherItems={[]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        }
      ]
    }
  ],
  spanMethod: (data) => {
    return { rowspan: 1, colspan: 1 }
  }

  // columns: [
  //   {
  //     id: 'id',
  //     prop: 'id',
  //     label: 'ID',
  //     width: '100'
  //   },
  //   {
  //     id: 'name_shortname',
  //     prop: 'name_shortname',
  //     width: '100',
  //     items: [
  //       {
  //         id: 'name',
  //         prop: 'name',
  //         label: 'Name',
  //         width: '100'
  //       },
  //       {
  //         id: 'shortname',
  //         prop: 'shortname',
  //         label: 'Shortname',
  //         width: '100'
  //       }
  //     ]
  //   },
  //   {
  //     id: 'age',
  //     prop: 'age',
  //     label: 'Age',
  //     group: 'test',
  //     width: '100',
  //     extend: [
  //       {
  //         id: 'age2',
  //         prop: 'age2',
  //         label: 'Age2'
  //       },
  //       {
  //         id: 'age3',
  //         prop: 'age3',
  //         label: 'Age3'
  //       }
  //     ]
  //   },
  //   {
  //     id: 'ckg',
  //     prop: 'ckg',
  //     label: '长宽高',
  //     width: '100',
  //     direction: 'row',
  //     tooltip: '长宽高说明长宽高说明长宽高说明长宽高说明长宽高说明',
  //     items: [
  //       {
  //         id: 'long',
  //         prop: 'long',
  //         label: 'Long',
  //         width: '100'
  //       },
  //       {
  //         id: 'width',
  //         prop: 'width',
  //         label: 'Width',
  //         width: '100'
  //       },
  //       {
  //         id: 'height',
  //         prop: 'height',
  //         label: 'Height',
  //         width: '100'
  //       }
  //     ]
  //   },
  //   {
  //     id: 'num_money',
  //     prop: 'num_money',
  //     label: '供应商报价',
  //     width: '100',
  //     direction: 'row',
  //     items: [
  //       {
  //         id: 'num',
  //         prop: 'num',
  //         label: 'Num',
  //         width: '100',
  //       },
  //       {
  //         id: 'money',
  //         prop: 'money',
  //         label: 'Money',
  //         width: '100'
  //       }
  //     ]
  //   },
  //   {
  //     id: 'address',
  //     prop: 'address',
  //     label: 'Address',
  //     width: '100',
  //     tooltip: '地址说明地址说明地址说明地地址说明地址说明地址说明地址说明地址说明地址说明'
  //   },
  //   {
  //     id: 'date',
  //     prop: 'date',
  //     label: 'Date',
  //     width: '100'
  //   },
  //   {
  //     id: 'action',
  //     prop: 'action',
  //     label: 'Action',
  //     width: '100',
  //     tooltip: 'action列'
  //   },
  //   {
  //     id: 'test',
  //     prop: 'test',
  //     label: 'test',
  //     width: '100',
  //     parent: true
  //   },
  //   {
  //     id: 'test2',
  //     prop: 'test2',
  //     label: 'test2',
  //     width: '100',
  //     parent: true
  //   }
  // ]
}

const handleCreate = () => {
  console.log('新增')
}
const handleSee = () => {
  console.log('采购看板')
}

const handleUpdate = (id: number) => {
  console.log('更新')
}

const handleDelete = async (id: number) => {
  console.log('删除')
}

const handleSubmit = (data) => {
  console.log('提交审核')
}

const handleDetail = (id: number) => {
  console.log('详情')
}

const handleExport = () => {
  console.log('导出')
}
</script>
