<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineChangeApprove"
    :rejectApi="examineChangeReject"
    :auditable="venderDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'scm:vender-change:submit',
      handler: () => {}
    }"
    :approve="{
      permi: 'scm:vender-change:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="venderDetail"
      :items="BasicInfoSchema"
      oldChangeField="oldVender"
    >
      <template #buyerList>
        <el-tag
          type="primary"
          v-for="(item, index) in venderDetail.buyerList"
          :key="index"
        >
          {{ item.name }}
        </el-tag>
      </template>

      <template #qualificationlink>
        <el-tag
          class="mb5px mr5px"
          type="primary"
          v-for="item in venderDetail.qualificationlink"
          :key="item.id"
        >
          {{ item.name }}
        </el-tag>
      </template>
      <template #businessScopeList>
        {{ venderDetail.businessScopeList?.map((item) => item.name).join(',') || '-' }}
      </template>

      <template #venderlink>
        <el-tag
          class="mb5px mr5px"
          type="primary"
          v-for="item in venderDetail.venderlink"
          :key="item.id"
        >
          {{ item.name }}
        </el-tag>
      </template>

      <template #factoryAreaName>
        <div
          v-if="
            venderDetail?.oldVender?.completeFactoryAddress !== venderDetail?.completeFactoryAddress
          "
        >
          <span class="new-info">
            {{ venderDetail.completeFactoryAddress }}
          </span>
          <span class="old-info">
            {{
              venderDetail.oldVender.completeFactoryAddress == `null`
                ? ' '
                : venderDetail.oldVender.completeFactoryAddress
            }}
          </span>
        </div>
        <span v-else>
          {{
            venderDetail.completeFactoryAddress == `null`
              ? '-'
              : venderDetail.completeFactoryAddress
          }}
        </span>
      </template>
      <template #deliveryAreaName>
        <div
          v-if="
            venderDetail?.oldVender?.completeDeliveryAddress !==
            venderDetail?.completeDeliveryAddress
          "
        >
          <span class="new-info">
            {{ venderDetail.completeDeliveryAddress }}
          </span>
          <span class="old-info">
            {{
              venderDetail.oldVender.completeDeliveryAddress == `null`
                ? ' '
                : venderDetail.oldVender.completeDeliveryAddress
            }}
          </span>
        </div>
        <span v-else>
          {{
            venderDetail.completeDeliveryAddress == `null`
              ? '-'
              : venderDetail.completeDeliveryAddress
          }}
        </span>
      </template>
      <template #companyAreaName>
        <div
          v-if="
            venderDetail?.oldVender?.completeCompanyAddress !== venderDetail?.completeCompanyAddress
          "
        >
          <span class="new-info">
            {{ venderDetail.completeCompanyAddress }}
          </span>
          <span class="old-info">
            {{
              venderDetail.oldVender.completeCompanyAddress == `null`
                ? ''
                : venderDetail.oldVender.completeCompanyAddress
            }}
          </span>
        </div>
        <span v-else>
          {{
            venderDetail.completeCompanyAddress == `null`
              ? '-'
              : venderDetail.completeCompanyAddress
          }}
        </span>
      </template>
    </eplus-description>
    <eplus-description
      title="财务信息"
      :data="venderDetail"
      :items="financeSchemas"
      oldChangeField="oldVender"
    >
      <template #taxMsg>
        <Table
          :columns="taxMsgColumns"
          headerAlign="center"
          align="center"
          :data="venderDetail?.taxMsg"
        />
      </template>
    </eplus-description>
    <eplus-form-items
      title="付款方式信息"
      :formSchemas="paymentListSchemas"
      v-if="!venderType"
    >
      <template #paymentList>
        <Table
          :columns="paymentColumns"
          headerAlign="center"
          align="center"
          :data="venderDetail?.paymentList"
        />
      </template>
    </eplus-form-items>

    <eplus-form-items
      title="联系人信息"
      :formSchemas="pocListSchemas"
    >
      <template #pocList>
        <Table
          :columns="pocColumns"
          headerAlign="center"
          align="center"
          :data="venderDetail?.pocList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="银行账户信息"
      :formSchemas="bankAccountSchemas"
    >
      <template #bankaccountList>
        <Table
          :columns="bankColumns"
          headerAlign="center"
          align="center"
          :data="venderDetail?.bankaccountList"
        />
      </template>
    </eplus-form-items>
    <eplus-description
      title="附件信息"
      :data="venderDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <!-- <el-tag
          type="primary"
          v-for="item in venderDetail.annex"
          :key="item.id"
        >
          <span
            style="cursor: pointer; color: #333"
            @click="handleDownload(item)"
            >{{ item.name }}</span
          >
        </el-tag> -->
        <span
          v-for="item in venderDetail.annex"
          :key="item?.id"
          class="mb5px mr5px"
        >
          <el-tag
            v-if="item.info == 'new' && item.name"
            type="warning"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <el-tag
            v-else-if="item.info == 'old' && item.name"
            type="info"
            class="old-tags"
            style="cursor: pointer"
            @click="handleDownload(item)"
            >{{ item.name }}
          </el-tag>
          <span v-else>
            <el-tag
              v-if="item.name"
              style="cursor: pointer"
              @click="handleDownload(item)"
              >{{ item.name }}</el-tag
            >
          </span>
        </span>
      </template>
    </eplus-description>
  </eplus-detail>
  <eplus-dialog
    ref="toFormalDialogRef"
    @success="handleRefresh"
    :destroyOnClose="true"
  >
    <template #create="{ key }">
      <FormalVenderForm
        channel="clue"
        :id="key"
        mode="create"
        @handle-success="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { examineChangeApprove, examineChangeReject } from '@/api/audit/vender'
import * as VenderApi from '@/api/scm/vender'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import FormalVenderForm from '../formal/VenderForm.vue'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { cloneDeep } from 'lodash-es'
import { setNewData } from '@/utils/index'
import { getConfigJson } from '@/api/common'

const message = useMessage()
const venderDetail = ref() // 借款申请单详情

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const toFormalDialogRef = ref()
const props = defineProps<{
  type: string
  title?: string
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'VenderDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const venderType = ref(false)
const { query } = useRoute()
const taxMsgColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'currency',
    label: '币种',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="currency"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'taxType',
    label: '发票类型',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="taxType"
              oldChangeField="oldVender"
              formatter={(val) => {
                return taxRateList.value.find((item: any) => item.key === val)?.label || '-'
              }}
            />
          </>
        )
      }
    }
  },
  {
    field: 'taxRate',
    label: '税率(%)',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="taxRate"
              oldChangeField="oldVender"
              formatter={(val) => {
                return val || val === 0 ? val : '-'
              }}
            />
          </>
        )
      }
    }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        return defaultChange(scope)
      }
    }
  }
])
const paymentColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'name',
    label: '付款方式',
    slots: {
      default: (data) => {
        const { row } = data

        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        // return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
        return defaultChange(scope)
      }
    }
  }
])
const pocColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'name',
    label: '联系人',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'pocTypes',
    label: '职位',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="pocTypes"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'mobile',
    label: '手机号',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="mobile"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'email',
    label: '邮箱',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="email"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  // {
  //   field: 'address',
  //   label: '地址'
  // },
  {
    field: 'telephone',
    label: '座机',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="telephone"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'wechat',
    label: '微信',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="wechat"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'qq',
    label: 'QQ',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="qq"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'remark',
    label: '备注',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="remark"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        // return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
        return defaultChange(scope)
      }
    }
  }
])
const bankColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'bank',
    label: '开户行',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="bank"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'bankAddress',
    label: '开户行地址',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="bankAddress"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'bankPoc',
    label: '账户名称',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="bankPoc"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'bankAccount',
    label: '账户号码',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="bankAccount"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'bankCode',
    label: '银行行号',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="bankCode"
              oldChangeField="oldVender"
            />
          </>
        )
      }
    }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    slots: {
      default: (scope) => {
        // return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
        return defaultChange(scope)
      }
    }
  }
])

const defaultChange = (scope) => {
  let newDefault = scope.row.defaultFlag === '  ' ? '  ' : scope.row.defaultFlag ? '是' : '否'
  let oldDefault =
    scope.row?.oldVender?.defaultFlag == undefined
      ? ''
      : scope.row?.oldVender?.defaultFlag
        ? '是'
        : '否'

  if (newDefault == oldDefault) {
    return <span>{newDefault}</span>
  } else {
    return (
      <>
        <span style="color:#f7aa49">{newDefault}</span>
        <span style=" text-decoration: line-through;margin:0 20px;color:#999">{oldDefault}</span>
      </>
    )
  }
}

const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}
const getChangeDetail = async () => {
  loading.value = true
  try {
    let result = props.id
      ? await VenderApi.changeVenderDetail({ id: props.id })
      : await VenderApi.auditchangeVenderDetail({ id: query.id })
    // setOldVender(result?.paymentList, result.oldVender?.paymentList, 'name')
    // setOldVender(result?.pocList, result.oldVender?.pocList, 'name')
    // setOldVender(result?.bankaccountList, result.oldVender?.bankaccountList, 'bank')
    setNewData(result?.paymentList, result.oldVender?.paymentList || [], 'oldVender')
    setNewData(result?.taxMsg, result.oldVender?.taxMsg || [], 'oldVender')
    setNewData(result?.pocList, result.oldVender?.pocList || [], 'oldVender')
    setNewData(result?.bankaccountList, result.oldVender?.bankaccountList || [], 'oldVender')
    result.annex = changeData(result.annex, result.oldVender.annex || [])

    venderDetail.value = setAddress(result)
    venderDetail.value.changeFlag = false
    venderType.value = venderDetail.value.venderType == 1 ? false : true

    if (venderType.value) {
      //行政
      BasicInfoSchema = DistrictInfoSchema
    }
  } finally {
    loading.value = false
  }
}

// 新旧数据对比添加标识
const changeData = (newData, oldData) => {
  const newArr = newData.filter((obj1) => !oldData.some((obj2) => obj1.name === obj2.name))
  newArr.forEach((obj) => (obj.info = 'new'))

  const oldArr = oldData.filter((obj2) => !newData.some((obj1) => obj1.name === obj2.name))
  oldArr.forEach((obj) => (obj.info = 'old'))
  newData = [...newData, ...oldArr]
  return newData
}

const setAddress = (data) => {
  data.completeDeliveryAddress = `${data.deliveryAreaName ? data.deliveryAreaName : ''}${data.deliveryAddress}`
  data.completeCompanyAddress = `${data.companyAreaName ? data.companyAreaName : ''}${data.companyAddress}`
  data.completeFactoryAddress = `${data.factoryAreaName ? data.factoryAreaName : ''}${data.factoryAddress}`

  data.oldVender.completeDeliveryAddress = `${data.oldVender.deliveryAreaName ? data.oldVender.deliveryAreaName : ''}${data.oldVender.deliveryAddress}`
  data.oldVender.completeCompanyAddress = `${data.oldVender.companyAreaName ? data.oldVender.companyAreaName : ''}${data.oldVender.companyAddress}`
  data.oldVender.completeFactoryAddress = `${data.oldVender.factoryAreaName ? data.oldVender.factoryAreaName : ''}${data.oldVender.factoryAddress}`

  return data
}

const setOldVender = (newVender, oldVender, typeStr) => {
  if (newVender != null && oldVender != null) {
    newVender.forEach((e, i) => {
      e.changeFlag = false
      oldVender.forEach((item, index) => {
        if (i == index) {
          e.oldVender = item
        }
      })
    })
    const onlyInOldVender = oldVender.filter((oldObj) => {
      if (typeStr === 'name') {
        return !newVender.some((newObj) => newObj?.name === oldObj?.name)
      } else {
        return !newVender.some((newObj) => newObj?.bank === oldObj?.bank)
      }
    })
    if (onlyInOldVender.length) {
      onlyInOldVender.forEach((obj) => {
        let cloneObj = cloneDeep(obj)
        // 对象属性值为空，则只展示删除的旧数据
        for (const key in cloneObj) {
          cloneObj[key] = '  '
        }
        cloneObj.changeFlag = false
        cloneObj.oldVender = obj
        newVender.push(cloneObj)
      })
    }
  }
}
/**
 * 业务供应商基本信息
 * @param r
 */
let BasicInfoSchema = [
  {
    field: 'name',
    label: '供应商名称',
    isCompare: true
  },
  {
    field: 'nameEng',
    label: '英文名称',
    isCompare: true
  },
  {
    field: 'nameShort',
    label: '简称',
    isCompare: true
  },
  {
    field: 'venderType',
    label: '供应商类型',
    dictType: DICT_TYPE.VENDER_TYPE
  },
  {
    field: 'phone',
    label: '供应商电话',
    isCompare: true
  },
  {
    field: 'licenseNo',
    label: '营业执照号',
    isCompare: true
  },
  {
    field: 'registeredCapital',
    label: '注册资本',
    isCompare: true
  },
  {
    field: 'legalPerson',
    label: '法定代表人',
    isCompare: true
  },
  // {
  //   field: 'venderlinkCode',
  //   label: '应付供应商',
  //   disabled: props.type === 'clue'
  // },
  {
    field: 'venderlink',
    label: '关联供应商',
    slotName: 'venderlink'
  },
  {
    field: 'qualificationlink',
    label: '关联资质',
    slotName: 'qualificationlink',
    isCompare: true
  },
  {
    field: 'businessScopeList',
    slotName: 'businessScopeList',
    label: '主营业务',
    isCompare: true
  },
  {
    field: 'buyerList',
    label: '采购员',
    slotName: 'buyerList',
    isCompare: true
  },
  {
    field: 'fax',
    label: '传真',
    isCompare: true
  },

  {
    slotName: 'factoryAreaName',
    field: 'factoryAreaName',
    label: '工厂地址',
    isCompare: true,
    span: 8
  },
  {
    slotName: 'deliveryAreaName',
    field: 'deliveryAreaName',
    label: '快递地址',
    isCompare: true,
    span: 8
  },
  {
    slotName: 'companyAreaName',
    field: 'companyAreaName',
    label: '公司地址',
    isCompare: true,
    span: 8
  }
]

// 行政供应商基本信息
let DistrictInfoSchema = [
  {
    field: 'name',
    label: '供应商名称',
    isCompare: true
  },
  {
    field: 'nameEng',
    label: '英文名称',
    isCompare: true
  },
  {
    field: 'nameShort',
    label: '简称',
    isCompare: true
  },
  {
    field: 'venderType',
    label: '供应商类型',
    dictType: DICT_TYPE.VENDER_TYPE
  },
  {
    field: 'phone',
    label: '供应商电话',
    isCompare: true
  },
  {
    field: 'remark',
    label: '备注',
    isCompare: true,
    span: 8
  }
]

const financeSchemas = reactive([
  {
    slotName: 'taxMsg',
    field: 'taxMsg',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])
const paymentListSchemas = [
  {
    field: 'paymentList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
//联系人信息
const pocListSchemas = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]

//银行账户信息
const bankAccountSchemas = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
//附件信息
const annexSchemas = [
  {
    field: 'annex',
    label: '',
    slotName: 'annex',
    span: 24
  }
]

const handleUpdate = async () => {
  await getChangeDetail()
}
const emit = defineEmits(['success'])
const handleRefresh = () => {
  emit('success')
}

const taxRateList = ref([])
const getInvoiceRateList = async () => {
  const res = await getConfigJson({ configType: 'invoice.rate' })
  taxRateList.value = res
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('供应商ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getInvoiceRateList()
  await getChangeDetail()
})
</script>
<style lang="scss" scoped>
.new-info {
  color: #f7aa49;
}

.old-info {
  margin-left: 10px;
  color: #999;
  text-decoration: line-through;
}
</style>
