<template>
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-change="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        :name="0"
      />
      <el-tab-pane
        label="处理中"
        :name="1"
      />
      <el-tab-pane
        label="已通过"
        :name="2"
      />
      <el-tab-pane
        label="已驳回"
        :name="3"
      />
      <el-tab-pane
        label="已取消"
        :name="4"
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
  />

  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <ConcessionReleaseDetail :id="key" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import * as ConcessionReleaseApi from '@/api/qms/concessionRelease/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import ConcessionReleaseDetail from './ConcessionReleaseDetail.vue'
import * as UserApi from '@/api/system/user'
import { getSourceId, removeSourceId } from '@/utils/auth'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)

const message = useMessage()

const exportFileName = ref() // 导出附件的名称，如果是空着，那因为当前页面没有导出，所以名称暂且空着，后续有了在加上

const eplusListRef = ref()
defineOptions({ name: 'ConcessionRelease' })
const activeName = ref(0)
const handleTabsClick = () => {
  eplusListRef.value.handleSearch()
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同编号'
    },
    {
      component: <el-input></el-input>,
      name: 'venderName',
      label: '供应商名称'
    },
    {
      component: <el-input></el-input>,
      name: 'skuName',
      label: '产品名称'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'inspectorId',
      label: '验货人',
      formatter: async (args: any[]) => {
        var user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    },
    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'checkTime',
          label: '验货时间',
          formatter: '从{0}到{1}'
        }
      ]
    }
  ],
  moreFields: []
}
const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ConcessionReleaseApi.getConcessionReleasePage({
      ...ps,
      auditStatus: activeName.value > 0 ? activeName.value : ''
    })
    return {
      list: res.list,
      total: res.total
    }
  },
  columns: [
    {
      prop: 'code',
      label: '验货单号',
      minWidth: '140px',
      parent: true,
      isHyperlink: true
    },
    {
      prop: 'inspectionType',
      label: '验货方式', // 1： 泛太陪验（工厂）2：泛太陪验（公司内） 3：泛太自验（工厂） 4：泛太自验（公司内） 5：客户自检 6：客户指定第三方 7：远程验货',
      parent: true,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.INSPECTION_TYPE, row?.inspectionType)
      },
      minWidth: '140px'
    },
    {
      prop: 'purchaseContractCode',
      label: '采购合同',
      minWidth: '140px',
      parent: true
    },
    {
      prop: 'qualityInspectionStatus',
      label: '验货结果',
      parent: true,
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.QUALITY_INSPECTION_STATUS, row?.qualityInspectionStatus)
      },
      minWidth: '140px'
    },
    {
      prop: 'venderName',
      label: '供应商名称',
      minWidth: '140px',
      parent: true
    },
    {
      prop: 'action',
      label: '操作',
      fixed: 'right',
      parent: true,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div class="flex items-center justify-start">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetail(row.id)
                }}
                hasPermi="['scm:concession-release:query']"
              >
                详情
              </el-button>
            </div>
          )
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
      prop: 'cskuCode',
      label: '客户货号',
      minWidth: '140px'
    },
    {
      prop: 'skuName',
      label: '产品名称',
      minWidth: '140px'
    },
    {
      prop: 'inspectionStatus',
      label: '验货状态',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.INSPECTION_STATUS, row?.inspectionStatus)
      },
      minWidth: '140px'
    },
    {
      prop: 'failDesc',
      label: '问题描述',
      minWidth: '140px'
    }
  ]
}

const handleDetail = (id) => {
  eplusDialogRef.value?.openDetail(id)
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
