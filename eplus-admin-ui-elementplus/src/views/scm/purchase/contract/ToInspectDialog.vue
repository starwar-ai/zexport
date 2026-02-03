<template>
  <Dialog
    v-model="dialogTableVisible"
    title="验货申请单"
    width="70%"
    append-to-body
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      :inline="true"
    >
      <el-row :gutter="24">
        <el-col :span="6">
          <el-form-item
            label="公司名称"
            style="width: 100%"
          >
            <el-input
              v-model="formData.companyName"
              placeholder="公司名称"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            label="供应商名称"
            style="width: 100%"
          >
            <el-input
              v-model="formData.venderName"
              placeholder="供应商名称"
              disabled
            /> </el-form-item
        ></el-col>
        <el-col :span="6"
          ><el-form-item
            label="采购合同"
            style="width: 100%"
          >
            <el-input
              v-model="formData.code"
              placeholder="请输入采购合同"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            label="采购员"
            style="width: 100%"
          >
            <el-input
              v-model="formData.purchaseUserName"
              placeholder="请输入purchaseUserName"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            label="计划排验时间"
            prop="expectInspectionTime"
            style="width: 100%"
          >
            <el-date-picker
              class="w-100%"
              v-model="formData.expectInspectionTime"
              placeholder="计划排验时间"
              valueFormat="x"
              type="date"
              :disabledDate="
                (date) => {
                  const dateTime = new Date(date).getTime()
                  let today = new Date() // 获取当前时间
                  today.setHours(0, 0, 0, 0) // 将当前时间的小时、分钟、秒和毫秒设置为0，得到今天0点的时间
                  let todayTimestamp = today.getTime()
                  return dateTime < todayTimestamp
                }
              "
            />
          </el-form-item>
        </el-col>

        <el-col :span="6">
          <el-form-item
            label="验货方式"
            prop="inspectionType"
            style="width: 100%"
          >
            <eplus-dict-select
              class="w-100%"
              v-model="formData.inspectionType"
              :dictType="DICT_TYPE.INSPECTION_TYPE"
              @change="(val) => onChangeType(val)"
            />
          </el-form-item>
        </el-col>
        <el-col
          :span="6"
          v-if="formData.inspectionType < 5 || remoteFlag"
        >
          <el-form-item
            label="验货人"
            prop="inspectorId"
            style="width: 100%"
          >
            <eplus-user-select
              v-model="formData.inspectorId"
              @change="changeUser"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            label="验货节点"
            prop="inspectionNode"
            style="width: 100%"
          >
            <eplus-dict-select
              class="w-100%"
              v-model="formData.inspectionNode"
              :dictType="DICT_TYPE.INSPECTION_NODE"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            label="工厂联系人"
            prop="factoryContacter"
            style="width: 100%"
          >
            <el-input
              v-model="formData.factoryContacter"
              placeholder="工厂联系人"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            label="联系电话"
            prop="factoryTelephone"
            style="width: 100%"
          >
            <el-input
              v-model="formData.factoryTelephone"
              placeholder="联系电话"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="验货地址"
            prop="inspectionAddress"
            style="width: 100%"
          >
            <el-input
              v-model="formData.inspectionAddress"
              placeholder="验货地址"
            />
          </el-form-item>
        </el-col>

        <el-col :span="6">
          <el-form-item
            label="验货时间"
            prop="inspectionTime"
            style="width: 100%"
            v-if="remoteFlag"
          >
            <el-date-picker
              v-model="formData.inspectionTime"
              placeholder="验货时间"
              valueFormat="x"
              type="date"
              :disabledDate="
                (date) => {
                  const dateTime = new Date(date).getTime()
                  let today = new Date()
                  today.setHours(0, 0, 0, 0)
                  let todayTimestamp = today.getTime()
                  return dateTime < todayTimestamp
                }
              "
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="特别注意事项"
            prop="specialAttentionNotice"
            style="width: 100%"
          >
            <el-input
              v-model="formData.specialAttentionNotice"
              :rows="2"
              type="textarea"
              placeholder="特别注意事项"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="备注"
            style="width: 100%"
          >
            <el-input
              v-model="formData.remark"
              :rows="2"
              type="textarea"
              placeholder="备注"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item
        label="附件"
        prop="annex"
      >
        <UploadList
          ref="UploadRef"
          :fileList="formData?.annex"
          @success="getFileList"
        />
      </el-form-item>
    </el-form>
    <eplus-form-table
      ref="TableRef"
      :list="tableList"
      :defaultVal="{}"
      :schema="tableColumns.columns"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import { TableColumn } from '@/types/table'
import { DICT_TYPE } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import { useUserStore } from '@/store/modules/user'
// import UploadPic from '@/components/UploadPic/src/UploadPic.vue'
import UploadSmallPic from '@/components/UploadPic/src/UploadSmallPic.vue'
import * as VenderApi from '@/api/scm/vender'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { columnWidth } from '@/utils/table'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getConfigKey, getDeptLeader } from '@/api/common'
import { getOuterbox } from '@/utils/outboxSpec'

const user = useUserStore().getUser

const message = useMessage()

const dialogTableVisible = ref(false)
const TableRef = ref()
const formData: any = ref({})
const tableList = ref([])
const companyList = ref([])
const stockList = ref([])

const formRef = ref() // 表单 Ref
const userList = ref<any[]>([]) // 用户列表
const pictureFlag = ref(false)

const remoteFlag = ref(false)
const UploadRef = ref()
const getFileList = (params) => {
  formData.value.annex = params
  formRef.value.validateField(['annex'], (valid) => {
    if (valid) {
      return true
    } else {
      return false
    }
  })
}

const formRules = reactive({
  expectInspectionTime: [{ required: true, message: '计划排验时间不能为空', trigger: 'blur' }],
  inspectionType: [{ required: true, message: '请选择验货方式', trigger: 'change' }],
  inspectionNode: [{ required: true, message: '请选择验货节点', trigger: 'change' }],
  factoryContacter: [{ required: true, message: '联系人不能为空', trigger: 'blur' }],
  factoryTelephone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }],
  inspectionAddress: [{ required: true, message: '验货地址不能为空', trigger: 'blur' }],
  annex: [{ required: true, message: '附件不能为空' }]
  // specialAttentionNotice: [{ required: true, message: '注意事项不能为空', trigger: 'blur' }],
})

const onChangeType = async (val) => {
  remoteFlag.value = val == 7 ? true : false // 是否远程验货
  if (remoteFlag.value) {
    tableColumns.columns = []
    formData.value.inspectionTime = new Date().getTime()
    tableList.value.forEach((e) => {
      e.inspectionStatus = 3 // 默认成功
      e.editPic = true
    })
  }
  pictureFlag.value = remoteFlag.value ? true : false
  // 远程验货，列表需要新增两列
  tableColumns.columns = remoteFlag.value ? [...tablePicColumns, ...tableColumn] : tableColumn
  if (val < 5) {
    let deptid = await getConfigKey('quality_inspection.dept')
    let leaders = await getDeptLeader({ code: deptid })
    formData.value.inspectorId = leaders[0]
  } else if (val === 7) {
    formData.value.inspectorName = user.nickname
    formData.value.inspectorId = user.id
    formData.value.inspectorDeptId = user.deptId
    formData.value.inspectorDeptName = user.deptName
  } else {
    formData.value.inspectorId = undefined
  }
}

const getFilePic = (params, row) => {
  row.picture = params
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
  tableList.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
}
let tableColumns = reactive<TableColumn[]>({
  columns: []
})
const tablePicColumns = reactive<TableColumn[]>([
  {
    field: 'picture',
    label: '图片',
    minWidth: '250px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <UploadSmallPic
          limit={5}
          ref="UploadPicRef"
          onSuccess={(val) => getFilePic(val, row)}
        />
      )
    },
    rules: [
      {
        required: pictureFlag,
        message: '请上传图片'
      }
    ]
  },
  {
    field: 'inspectionStatus',
    label: '验货结果',
    width: columnWidth.xl,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.INSPECT_RESULT_TYPE}
        clearable={false}
      />
    )
  }
])
const tableColumn = reactive<TableColumn[]>([
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.xl
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.xl
  },
  {
    field: 'skuName',
    label: '产品名称',
    width: columnWidth.xl
  },
  {
    field: 'purchaseQuantity',
    label: '采购数量',
    width: columnWidth.l
  },
  {
    field: 'quantity',
    label: '验货数量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.quantity}
          controls={false}
          precision={0}
          min={1}
          max={row.purchaseQuantity}
        />
      )
    }
  },
  {
    field: 'totalPrice',
    label: '产品总价',
    width: columnWidth.xl,
    slot: (item, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.totalPrice} />
    }
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.m
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.m
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.m,
    slot: (item, row: Recordable, index: number) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.m,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row.boxCount * (row.specificationList?.length || 1)}</span>
    }
  },
  {
    field: 'boxCount',
    label: '外箱规格',
    width: columnWidth.xl,
    slot: (item, row: Recordable, index: number) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.m
  },
  {
    field: 'remark',
    label: '备注',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <el-input v-model={row.remark} />
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '80px',
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            await delRow(index)
          }}
        >
          移除
        </el-button>
      )
    }
  }
])
const emit = defineEmits<{
  sure
}>()
const open = async (id) => {
  tableColumns.columns = tableColumn
  try {
    const res = await PurchaseContractApi.getPurchaseContract({ id: id })
    stockList.value = await PurchaseContractApi.getStockList()
    const Vender = await VenderApi.getVender({ id: res.venderId })

    formData.value = res
    formData.value.factoryContacter = res.venderPoc?.name
    formData.value.factoryTelephone = res.venderPoc?.mobile
    formData.value.inspectionAddress = res.venderPoc?.address
    formData.value.inspectionAddress = Vender.factoryAreaName + Vender.factoryAddress
    formData.value.annex = res.auxiliaryAnnex || []
    tableList.value = res?.children

    tableList.value.forEach((e: any) => {
      e.purchaseQuantity = e.quantity
      e.totalPriceAmount = e.totalPrice.amount.toFixed(3) + ' ' + e.totalPrice.currency
      e.editPic = true
      e.handleFlag = undefined
      e.inspectionStatus = undefined
    })
  } catch {}
  dialogTableVisible.value = true
}

const handleCancel = () => {
  dialogTableVisible.value = false
}

const changeUser = (val) => {
  if (val) {
    formData.value.inspectorName = val.nickname
    formData.value.inspectorId = val.id
    formData.value.inspectorDeptId = val.deptId
    formData.value.inspectorDeptName = val.deptName
  }
}

const handleSure = async () => {
  let valid = await formRef.value.validate()
  let validList = await TableRef.value.validate()

  if (valid && validList) {
    const {
      companyName,
      companyId,
      venderName,
      venderCode,
      venderId,
      purchaseUserName,
      code,
      id,
      expectInspectionTime,
      inspectionType,
      inspectionNode,
      warehouseName,
      warehouseId,
      factoryContacter,
      factoryTelephone,
      inspectionAddress,
      specialAttentionNotice,
      remark,
      annex,
      inspectionTime,
      inspectorName,
      inspectorId,
      inspectorDeptId,
      inspectorDeptName,
      linkCodeList,
      purchaseUserId,
      sales,
      mainPicture
    } = formData.value
    let formDataParam = {
      expectInspectionTime,
      purchaseContractId: id, //合同主键
      purchaseContractCode: code, // 合同编码
      companyId,
      companyName,
      remark,
      venderName,
      venderCode,
      venderId,
      purchaseUserName,
      inspectionType,
      inspectionNode,
      warehouseName,
      warehouseId,
      factoryContacter,
      factoryTelephone,
      inspectionAddress,
      specialAttentionNotice,
      annex,
      inspectionTime,
      inspectorName,
      inspectorId,
      inspectorDeptId,
      inspectorDeptName,
      itemSaveReqVOList: tableList.value,
      linkCodeList,
      purchaseUserId,
      sales,
      mainPicture
    }

    let res = await PurchaseContractApi.createQualityInspection({ ...formDataParam })
    // message.success('转验货单成功')
    message.notifyPushSuccess('转验货单成功', res, 'quality-inspection')
    emit('sure')
    dialogTableVisible.value = false
  } else {
    message.warning('请核验信息')
  }
}
defineExpose({ open })
</script>
<style scoped lang="scss">
:deep(.item_key) {
  line-height: 32px;
}
// :deep(.item_val) {
//   line-height: 32px;
// }
</style>
