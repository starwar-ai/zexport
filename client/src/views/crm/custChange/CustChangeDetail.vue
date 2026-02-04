<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineChangeApprove"
    :rejectApi="examineChangeReject"
    :auditable="custDeatail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'crm:cust-change:submit',
      handler: () => {}
    }"
    :approve="{
      permi: 'crm:cust-change:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="custDeatail"
      :items="BasicInfoSchema"
      oldChangeField="oldCust"
    >
      <template #custLink>
        <!-- <el-tag
          class="mb5px mr5px"
          type="primary"
          v-for="item in custDeatail.custLink"
          :key="item?.id"
        >
          {{ item?.name }}
        </el-tag> -->
        <span
          v-for="item in custDeatail.custLink"
          :key="item.value"
          class="mb5px mr5px"
        >
          <span>
            <el-tag
              v-if="item.info == 'new'"
              type="warning"
              >{{ item.name }}
            </el-tag>
            <el-tag
              v-else-if="item.info == 'old'"
              type="info"
              class="old-tags"
              >{{ item.name }}
            </el-tag>

            <el-tag v-else>{{ item.name }}</el-tag>
          </span>
        </span>
      </template>
      <template #managerList>
        <span
          v-for="item in custDeatail.managerList"
          :key="item.value"
          class="mb5px mr5px"
        >
          <span v-if="item.defaultFlag && item.info !== 'old'">
            <el-tag>{{ item.name }}(默认) </el-tag>
          </span>
          <span v-else>
            <el-tag
              v-if="item.info == 'new'"
              type="warning"
              >{{ item.name }}
            </el-tag>
            <el-tag
              v-else-if="item.info == 'old'"
              type="info"
              class="old-tags"
              >{{ item.name }}
            </el-tag>

            <el-tag v-else>{{ item.name }}</el-tag>
          </span>
        </span>
      </template>

      <template #customerTypes>
        <span
          v-for="item in custDeatail?.customerTypes"
          :key="item"
        >
          <DictTag
            :type="DICT_TYPE.CUSTOM_TYPE"
            :value="item"
          />&nbsp;
        </span>
      </template>
    </eplus-description>
    <eplus-description
      title="财务信息"
      :data="custDeatail"
      :items="financeSchemas"
      oldChangeField="oldCust"
    >
      <!-- <template #creditLimit>
        {{
          custDeatail?.creditLimit?.amount
            ? custDeatail?.creditLimit?.amount + custDeatail?.creditLimit?.currency
            : '-'
        }}
      </template> -->
      <template #currencyList>
        <span
          v-for="item in custDeatail.currencyList"
          :key="item.value"
          class="mb5px mr5px"
        >
          <span v-if="item.defaultFlag && item.info !== 'old'">
            <el-tag>{{ item.name }}(默认) </el-tag>
          </span>
          <span v-else>
            <el-tag
              v-if="item.info == 'new'"
              type="warning"
              >{{ item.name }}
            </el-tag>
            <el-tag
              v-else-if="item.info == 'old'"
              type="info"
              class="old-tags"
              >{{ item.name }}
            </el-tag>

            <el-tag v-else>{{ item.name }}</el-tag>
          </span>
        </span>
      </template>
    </eplus-description>
    <eplus-form-items
      title="订单路径信息"
      :formSchemas="custCompanyPathListSchemas"
    >
      <template #companyPath>
        <Table
          :columns="custCompanyPathListColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.companyPath"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="收款方式信息"
      :formSchemas="settlementListSchemas"
    >
      <template #settlementList>
        <Table
          :columns="settlementColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.settlementList"
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
          :data="custDeatail?.pocList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="地址"
      :formSchemas="addressShippingSchemas"
    >
      <template #addressShipping>
        <Table
          :columns="addressShippingColumns"
          headerAlign="center"
          align="center"
          :data="custDeatail?.addressShipping"
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
          :data="custDeatail?.bankaccountList"
        />
      </template>
    </eplus-form-items>
    <eplus-description
      title="附件信息"
      :data="custDeatail"
      :items="annexSchemas"
    >
      <template #annex>
        <span
          v-for="item in custDeatail.annex"
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
    <eplus-description
      title="图片信息"
      :data="custDeatail"
      :items="pictureListSchemas"
    >
      <template #picture>
        <!-- <UploadPic :pictureList="custDeatail.picture" /> -->
        <UploadZoomPic
          v-model="custDeatail.picture"
          disabled
        />

        <div class="old-picture">
          <div class="old-mask">已删除</div>
          <UploadZoomPic
            v-model="custDeatail.oldCust.picture"
            disabled
          />
        </div>
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineChangeApprove, examineChangeReject } from '@/api/audit/cust'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import * as CustApi from '@/api/crm/cust'
import { getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { cloneDeep } from 'lodash-es'
import { DICT_TYPE } from '@/utils/dict'
import { setNewData } from '@/utils/index'

const message = useMessage()
const custDeatail = ref() // 详情

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const ImageRef = ref()
const props = defineProps<{
  id?: number
  saleId?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'CustDeatail' })
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()
const custCompanyPathListColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'id',
    label: '订单路径',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="pathName"
              oldChangeField="oldCust"
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
const settlementColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'settlementId',
    label: '收款方式',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldCust"
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
const pocColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'name',
    label: '联系人',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    field: 'pocPosts',
    label: '职位',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="pocPosts"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    field: 'mobile',
    label: '手机号',
    // isCompare: true
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="mobile"
              oldChangeField="oldCust"
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
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="email"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    field: 'address',
    label: '地址',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="address"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    field: 'telephone',
    label: '座机',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="telephone"
              oldChangeField="oldCust"
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
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="wechat"
              oldChangeField="oldCust"
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
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="qq"
              oldChangeField="oldCust"
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
        // return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
      }
    }
  }
])

const addressShippingColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'name',
    label: '联系人',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    field: 'address',
    label: '地址',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="address"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    field: 'postalCode',
    label: '邮编',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="postalCode"
              oldChangeField="oldCust"
            />
          </>
        )
      }
    }
  },
  {
    field: 'phone',
    label: '电话',
    slots: {
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="phone"
              oldChangeField="oldCust"
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
      default: (scope) => {
        const { row } = scope
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="email"
              oldChangeField="oldCust"
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
        // return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
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
              oldChangeField="oldCust"
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
              oldChangeField="oldCust"
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
              oldChangeField="oldCust"
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
              oldChangeField="oldCust"
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
              oldChangeField="oldCust"
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
        // return <span>{scope.row.defaultFlag ? '是' : '否'}</span>
      }
    },
    isCompare: true
  }
])

const defaultChange = (scope) => {
  let newDefault = scope.row.defaultFlag === '  ' ? '  ' : scope.row.defaultFlag ? '是' : '否'
  let oldDefault =
    scope.row?.oldCust?.defaultFlag == undefined
      ? ''
      : scope.row?.oldCust?.defaultFlag
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
const getDetail = async () => {
  loading.value = true

  try {
    let pathList = []
    let result = props?.id
      ? await CustApi.getCustChangeDetail({ id: props?.id })
      : await CustApi.getCustAuditChangeDetail({ id: query?.id })

    if (result.companyPath?.length) {
      result.companyPath.forEach((item) => {
        if (item.path) {
          item.pathName = getCompanyPathNameFromObj(item.path)
          // let obj = {
          //   companyPathId: item?.id,
          //   custCode: result.code,
          //   pathName: getCompanyPathNameFromObj(item.path),
          //   defaultFlag: item.defaultFlag
          // }
          // pathList.push(obj)
        }
      })
      // result.custCompanyPathList = pathList
    }

    if (result.oldCust.companyPath?.length) {
      result.oldCust.companyPath.forEach((item) => {
        if (item.path) {
          item.pathName = getCompanyPathNameFromObj(item.path)
        }
      })
    }

    // 新旧数据币种对比
    result.currencyList = changeData(result.currencyList, result.oldCust.currencyList)
    result.annex = changeData(result.annex, result.oldCust.annex)
    result.managerList = changeData(result.managerList, result.oldCust.managerList)
    result.custLink = changeData(result.custLink, result.oldCust.custLink)

    result.changeFlag = false

    setNewData(result?.addressShipping, result.oldCust?.addressShipping, 'oldCust')
    setNewData(result?.pocList, result.oldCust?.pocList, 'oldCust')
    setNewData(result?.bankaccountList, result.oldCust?.bankaccountList, 'oldCust')
    setNewData(result?.companyPath, result.oldCust?.companyPath, 'oldCust')
    setNewData(result?.settlementList, result.oldCust?.settlementList, 'oldCust')

    custDeatail.value = result
  } finally {
    loading.value = false
  }
}

// 新旧数据对比添加标识
const changeData = (newData, oldData) => {
  if (newData && oldData) {
    const newArr = newData?.filter((obj1) => !oldData?.some((obj2) => obj1.name === obj2.name))
    newArr?.forEach((obj) => (obj.info = 'new'))

    const oldArr = oldData?.filter((obj2) => !newData?.some((obj1) => obj1.name === obj2.name))
    oldArr?.forEach((obj) => (obj.info = 'old'))
    newData = [...newData, ...oldArr]
  }
  return newData
}

const setOldCust = (newCust, oldCust, typeStr) => {
  if (newCust != null && oldCust != null) {
    newCust.forEach((e, i) => {
      e.changeFlag = false
      oldCust.forEach((item, index) => {
        // if (newCust.length == oldCust.length) {
        if (i == index) {
          e.oldCust = item
        }
        // }
      })
    })

    const onlyInOldCust = oldCust.filter((oldObj) => {
      if (typeStr === 'name') {
        return !newCust.some((newObj) => newObj?.name === oldObj?.name)
      } else if (typeStr === 'bank') {
        return !newCust.some((newObj) => newObj?.bank === oldObj?.bank)
      } else {
        return !newCust.some((newObj) => newObj?.pathName === oldObj?.pathName)
      }
    })
    if (onlyInOldCust.length && newCust.length !== oldCust.length) {
      onlyInOldCust.forEach((obj) => {
        let cloneObj = cloneDeep(obj)
        // 对象属性值为空，则只展示删除的旧数据
        for (const key in cloneObj) {
          cloneObj[key] = '  '
        }
        cloneObj.changeFlag = false
        cloneObj.oldCust = obj
        newCust.push(cloneObj)
      })
    }
  }
}
/**
 * 生成明细信息
 * @param r
 */
const BasicInfoSchema = [
  {
    field: 'code',
    label: '客户编号'
  },
  {
    field: 'name',
    isCompare: true,
    label: '客户名称'
  },
  // {
  //   field: 'nameEng',
  //   isCompare: true,
  //   label: '英文名称'
  // },
  {
    field: 'shortname',
    isCompare: true,
    label: '简称'
  },
  {
    field: 'phone',
    label: '联系电话',
    isCompare: true
  },
  {
    field: 'email',
    label: '邮箱地址',
    isCompare: true
  },
  {
    field: 'address',
    label: '营业地址',
    isCompare: true
  },
  { field: 'homepage', label: '官网地址', isCompare: true },
  {
    field: 'countryName',
    label: '国家/地区',
    isCompare: true
  },
  {
    field: 'regionName',
    label: '所属地区',
    isCompare: true
  },

  { field: 'agentFlag', label: '是否联营', isCompare: true, dictType: DICT_TYPE.CONFIRM_TYPE },
  { field: 'stageType', label: '客户阶段', isCompare: true, dictType: DICT_TYPE.CUSTOMER_STAGE },
  { field: 'sourceType', label: '客户来源', isCompare: true, dictType: DICT_TYPE.SOURCE_TYPE },
  { field: 'managerList', label: '业务员', slotName: 'managerList' },
  { field: 'customerTypesName', label: '客户类型', isCompare: true },
  { slotName: 'custLink', field: 'custLink', label: '关联客户', isCompare: true },
  { field: 'enableFlag', label: '启用状态', isCompare: true, dictType: DICT_TYPE.ENABLE_FLAG },
  { field: 'remark', label: '备注', isCompare: true },
  { field: 'mainMark', label: '正面唛头', span: 12, isCompare: true },
  { field: 'sideMark', label: '侧面唛头', span: 12, isCompare: true }
]

const financeSchemas = [
  {
    slotName: 'currencyList',
    field: 'currencyList',
    label: '结算币种'
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    dictType: DICT_TYPE.PRICE_TYPE,
    isCompare: true
  },
  {
    field: 'creditFlag',
    label: '启用中信保',
    dictType: DICT_TYPE.CONFIRM_TYPE,
    isCompare: true
  },
  {
    field: 'creditLimit',
    label: '中信保额度',
    type: 'money',
    isCompare: true
  },
  {
    field: 'agentFlag',
    label: '是否联营',
    dictType: DICT_TYPE.CONFIRM_TYPE,
    isCompare: true
  }
]
//订单路径
const custCompanyPathListSchemas = [
  {
    field: 'companyPath',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]

//收款方式
const settlementListSchemas = [
  {
    field: 'settlementList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//联系人信息
const pocListSchemas = [
  {
    field: 'pocList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//快递地址信息
const addressShippingSchemas = [
  {
    field: 'addressShipping',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
//银行账户信息
const bankAccountSchemas = [
  {
    field: 'bankaccountList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
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
const pictureListSchemas = [
  {
    field: 'picture',
    label: '',
    span: 24,
    slotName: 'picture'
  }
]

const handleUpdate = async () => {
  // await getDetail()
}

onMounted(async () => {
  // if (!props?.id && !query?.id) {
  //   message.error('客户ID不能为空')
  //   if (!props?.id) {
  //     close()
  //   }
  // }
  if (query?.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props?.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  if (props?.saleId) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getDetail()
})
</script>
<style lang="scss" scoped>
.old-default {
  display: inline-block;
  margin-left: 10px;
  text-decoration: line-through;
}

.old-tags {
  text-decoration: line-through;
}

// .old-picture {
//   position: ralative;

//   .old-mask {
//     position: absolute;
//     left: 0;
//     top: 55%;
//     width: 100%;
//     height: 100%;
//     // opacity: 0.1;
//     z-index: 9999;
//   }
// }
</style>
