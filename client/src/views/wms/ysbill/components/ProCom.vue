<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn } from '@/utils/table'
import { formatNum, formatterPrice } from '@/utils/index'
import { volPrecision, VolumeUnit, weightUnit } from '@/utils/config'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'

defineOptions({ name: 'ProCom' })
const props = defineProps<{
  formData
}>()

const emit = defineEmits(['abnormalStatus'])

const message = useMessage()
const tableList = ref([])

let tableColumns = reactive([
  {
    field: 'sortNum',
    label: '序号',
    width: '60',
    fixed: 'left'
  },
  {
    field: 'batchCode',
    label: '批次编号',
    width: '120'
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: '150'
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: '150'
  },
  {
    field: 'skuName',
    label: '产品名称',
    width: '150'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号',
    width: '150'
  },
  {
    label: '仓库名称',
    width: '150',
    field: 'warehouseName'
  },
  {
    field: 'position',
    label: '位置',
    width: '150',
    batchEditFlag: true,
    component: <el-input />,
    rules: [{ required: true, message: '请输入位置' }]
  },
  {
    field: 'orderQuantity',
    label: '应收数量',
    width: '100'
  },
  {
    field: 'actQuantity',
    label: '实收数量',
    width: '100',
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <el-input-number
            v-model={row.actQuantity}
            min={0}
            controls={false}
            precision={0}
            class="!w-90%"
          />
        </>
      )
    },

    rules: [{ required: true, message: '请输入实收数量' }]
  },
  {
    field: 'orderBoxQuantity',
    label: '应收箱数',
    width: '100'
  },
  {
    field: 'actBoxQuantity',
    label: '实收箱数',
    width: '100',
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <el-input-number
            v-model={row.actBoxQuantity}
            min={0}
            controls={false}
            precision={0}
            class="!w-90%"
          />
        </>
      )
    }
  },
  {
    field: 'abnormalStatus',
    label: '异常情况',
    width: '150',
    batchEditFlag: true,
    batchEditCom: <eplus-dict-select dictType={DICT_TYPE.ABNORMAL_STATUS} />,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.abnormalStatus}
          dictType={DICT_TYPE.ABNORMAL_STATUS}
          style="width:100%"
          onChangeEmit={() => {
            if (row?.abnormalStatus == 1) {
              row.abnormalRemark = undefined
            } else {
              row.abnormalRemark = null
            }
          }}
        />
      )
    },
    rules: [{ required: true, message: '请选择异常情况' }]
  },
  {
    field: 'abnormalRemark',
    label: '异常描述',
    width: '150',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input
          v-model={row.abnormalRemark}
          disabled={row?.abnormalStatus == 1}
        />
      )
    },
    rules: [
      {
        validator: (rule, value, callback) => {
          if (value === undefined) {
            callback()
          } else if (value === null) {
            callback(new Error('请输入异常描述'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'qtyPerOuterbox',
    label: '装箱量',
    width: '100'
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: '150',
    formatter: (item, row, index) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },

  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: '总体积',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <div>
          {formatNum((getOuterboxVal(row, 'vol') * row.actBoxQuantity) / 1000000, volPrecision)}
          {VolumeUnit}
        </div>
      )
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <div>
          {formatNum(getOuterboxVal(row, 'grossweight') * row.actBoxQuantity)} {weightUnit}
        </div>
      )
    }
  },
  {
    field: 'purchaserName',
    label: '采购员',
    width: '100'
  },
  {
    field: 'manager',
    label: '跟单员(部门)',
    slot: (item, row: Recordable, index: number) => {
      return (
        <label>
          {row.manager.deptName
            ? `${row.manager.nickname}(${row.manager.deptName})`
            : row.manager.nickname}
        </label>
      )
    }
  },
  {
    field: 'ownBrandFlag',
    label: '自主品牌',
    formatter: formatDictColumn(DICT_TYPE.IS_INT),
    width: '100'
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: '150'
  },
  {
    field: 'custPo',
    label: '客户PO号',
    width: '150'
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '150'
  },
  {
    field: 'remark',
    label: '备注',
    width: '200'
  }
])

watch(
  () => tableList.value,
  (list) => {
    list?.forEach((item: any) => {
      item.actQuantity = item.actQuantity ? item.actQuantity : item.orderQuantity
      item.outerboxVolume = formatterPrice(
        Number(item.outerboxLength) * Number(item.outerboxWidth) * Number(item.outerboxHeight)
      )
      item.actBoxQuantity =
        item.qtyPerOuterbox == 0
          ? 0
          : Math.ceil(Number(item.actQuantity) / Number(item.qtyPerOuterbox))
      item.totalVolume = Number(item.outerboxVolume) * Number(item.actBoxQuantity)
      // let [w, u] = item..split(SplitKey)
      // w = isNaN(w) ? 0 : w
      // item.outerboxGrossweight.weight = item.outerboxGrossweightStr
      // item.outerboxGrossweight.unit = 'kg'
      // item.totalWeight.weight = Number(item.actBoxQuantity) * Number(item.outerboxGrossweightStr)
      // item.totalWeight.unit = 'kg'
      item.abnormalStatus = item.abnormalStatus ? item.abnormalStatus : 1
    })
  },
  { immediate: true, deep: true }
)

const TableRef = ref()
const checkData = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    return TableRef.value.tableData
  } else {
    message.warning('入库单提交有误')
    return false
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  let children = props.formData.children || []
  tableList.value = children.map((item) => {
    if (!item.totalWeight) {
      item.totalWeight = {
        weight: 0,
        unit: ''
      }
    }

    return item
  })

  let allAbnormalStatus = false
  for (const obj of children) {
    if (obj.abnormalStatus) {
      allAbnormalStatus = true
      break
    }
  }
  emit('abnormalStatus', allAbnormalStatus)
})
</script>
