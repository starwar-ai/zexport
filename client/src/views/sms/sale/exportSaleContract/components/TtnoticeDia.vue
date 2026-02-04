<template>
  <Dialog
    v-model="dialogTableVisible"
    title="转出库通知单"
    width="950"
    append-to-body
    destroy-on-close
  >
    <eplus-table
      :eplusTableSchema="eplusTableSchema"
      ref="eplusTableRef"
      key="eplusTable"
      :height="400"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { formatDateColumn, formatDictColumn } from '@/utils/table'
import { DICT_TYPE } from '@/utils/dict'
import { outNoticeMid, transformNotice } from '@/api/sms/saleContract/domestic/index'
import { isValidArray } from '@/utils/is'

const message = useMessage()
const eplusTableRef = ref()
const dialogTableVisible = ref(false)
const pageList = ref([])
const open = async (row) => {
  pageList.value = await outNoticeMid(row.children.map((item) => item.id).join(','))
  dialogTableVisible.value = true
  setTimeout(() => {
    eplusTableRef.value.selectAll()
  }, 100)
}

let eplusTableSchema = reactive({
  getListApi: async () => {
    let resList = pageList.value.children
    //本次使用数量计算 如果：可用数量和 <= 出运数量 本次使用数量 = 可用数量
    //           如果：可用数量和 > 出运数量 只有一条库存记录 本次使用数量 = 出运数量
    resList.forEach((item) => {
      if (item.children?.length > 0) {
        let quantity = item.quantity
        let availableCabinetQuantitSum = 0
        item.children?.forEach((s) => {
          if (s.availableCabinetQuantity > 0) {
            availableCabinetQuantitSum += s.availableCabinetQuantity
          }
        })
        if (availableCabinetQuantitSum <= quantity) {
          item.children?.forEach((s) => {
            s.usedQuantity = s.availableCabinetQuantity
          })
        } else {
          if (item.children?.length == 1) {
            item.children[0].usedQuantity = quantity
          }
        }
      }
    })
    return {
      list: resList,
      total: resList?.length
    }
  },
  hideChildCheckBox: true,
  selection: true,
  showSettings: false,
  columns: [
    {
      prop: 'saleContractCode',
      label: '销售合同号',
      minWidth: '100px',
      parent: true,
      fixed: 'left'
    },
    {
      prop: 'skuCode',
      label: '产品编号',
      minWidth: '100px',
      parent: true
    },
    {
      prop: 'skuName',
      label: '产品名称',
      minWidth: '100px',
      parent: true
    },

    {
      prop: 'quantity',
      label: '出库数量',
      minWidth: '100px',
      parent: true
    },
    {
      prop: 'batchCode',
      label: '批次号',
      minWidth: '100px'
    },
    {
      prop: 'companyName',
      label: '公司名称',
      minWidth: '100px'
    },
    {
      prop: 'stockMethod',
      label: '库存类型',
      formatter: formatDictColumn(DICT_TYPE.STOCK_METHOD),
      minWidth: '100px'
    },
    {
      prop: 'inboundDate',
      label: '入库日期',
      minWidth: '100px',
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
    },
    {
      prop: 'shippedAddress',
      label: '发货地点',
      minWidth: '120px',
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <eplus-dict-select
              class="!w-90%"
              v-model={row.shippedAddress}
              dictType={DICT_TYPE.SHIPPED_ADDRESS}
              clearable={false}
            />
          )
        }
      }
    },
    {
      prop: 'availableCabinetQuantity',
      label: '可用数量',
      minWidth: '100px'
    },
    {
      prop: 'usedQuantity',
      label: '本次使用数量',
      minWidth: '100px'
    },
    {
      prop: 'purchaseContractCode',
      label: '采购合同',
      minWidth: '100px'
    },
    {
      prop: 'venderName',
      label: '供应商',
      minWidth: '200px'
    }
  ]
})

const emit = defineEmits(['success'])
defineExpose({ open })

function replacer(key, value) {
  if (key === 'parent') return undefined // 忽略 parent 属性
  return value
}
const handleSure = async () => {
  let checkedItems = eplusTableRef.value.checkedItems
  if (!isValidArray(checkedItems)) {
    message.warning('请选择数据')
    return false
  }
  let childrenList = []
  checkedItems.forEach((item: any) => {
    childrenList.push(...item.children)
  })
  for (let i = 0; i < childrenList.length; i++) {
    const el: any = childrenList[i]
    if (!el.shippedAddress) {
      message.warning('请选择发货地点')
      return false
    }
  }
  const { children, ...result } = pageList.value
  let requestParams = JSON.parse(JSON.stringify({ ...result, children: checkedItems }, replacer))
  let res = await transformNotice(requestParams)
  message.notifyPushSuccess('转出库通知单成功', res, 'ttnotice')
  handleCancel()
  emit('success')
}
const handleCancel = () => {
  dialogTableVisible.value = false
}
</script>
