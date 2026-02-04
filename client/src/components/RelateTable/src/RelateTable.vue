<template>
  <span v-if="emptyFlag && !isValidArray(beforeTabList) && !isValidArray(afterTabList)">
    暂无数据
  </span>
  <div
    v-else
    class="wrap"
  >
    <div class="left_box">
      <el-tabs
        v-model="activeNameChild"
        class="demo-tabs"
        @tab-click="handleClick"
        tab-position="left"
      >
        <el-tab-pane
          label=""
          name="zero-child"
          disabled
          v-if="isValidArray(beforeTabList)"
        >
          <template #label> <div class="mytab">上游单据</div> </template>
        </el-tab-pane>
        <el-tab-pane
          v-for="item in beforeTabList"
          :label="item?.name"
          :name="item?.type"
          :key="item?.type"
        />
        <el-tab-pane
          label=""
          name="zero-child"
          disabled
          v-if="isValidArray(afterTabList)"
        >
          <template #label> <div class="mytab">下游单据</div> </template>
        </el-tab-pane>
        <el-tab-pane
          v-for="item in afterTabList"
          :label="`${item?.name}`"
          :name="item?.type"
          :key="item?.type"
        />
      </el-tabs>
    </div>
    <div class="right_box">
      <eplus-table
        v-if="showTable"
        :eplusTableSchema="eplusTableSchema"
        children="item"
        ref="eplusTableRef"
        @emit-row="emitOpenDetail"
      />
      <el-skeleton
        v-else
        :rows="5"
        animated
      />
    </div>
  </div>
</template>
<script lang="tsx" setup>
import { orderlinkList } from '@/api/common/index'
import { columnList, tabs } from './colums.tsx'
import { setSourceId } from '@/utils/auth'
import { isValidArray } from '@/utils/is'
import { checkPermi } from '@/utils/permission.js'

const props = defineProps<{
  beforeList?: any
  afterList?: any
  changeFlag?: any
  params?: any
  linkCode?: any
  data?: any
}>()
const eplusTableRef = ref()
const showTable = ref(false)
const activeNameChild = ref('first-child')
const beforeTabList = ref<any>([])
const afterTabList = ref<any>([])
const pagePath = useRoute().path
const emptyFlag = ref(false)

let eplusTableSchema = ref()
const handleClick = async (val) => {
  showTable.value = false
  if (val?.paneName) {
    eplusTableSchema.value = {
      getListApi: async () => {
        if (
          (pagePath.includes('shippingDetail') || pagePath.includes('payment-apply')) &&
          afterTabList.value.map((item) => item.type).includes(val?.paneName)
        ) {
          return {
            list: allList.value.filter(
              (item) => item?.type === val?.paneName && item?.parentCode === props?.data?.code
            ),
            total: 0
          }
        } else if (pagePath.includes('fees-list')) {
          return {
            list: allList.value.filter(
              (item) => item?.type === val?.paneName && item?.code === props?.data?.businessCode
            ),
            total: 0
          }
        } else {
          return {
            list: allList.value.filter((item) => item?.type === val?.paneName),
            total: 0
          }
        }
      },
      columns: columnList.find((item) => item.type == val?.paneName)?.colums,
      showSettings: false
    }
    nextTick(() => {
      showTable.value = true
    })
  }
}

const router = useRouter()
const emitOpenDetail = (val) => {
  if (!val.businessId) return false
  setSourceId(val.businessId)
  if (val.type === 1) {
    //销售合同
    if (
      checkPermi(['sms:export-sale-contract:query']) &&
      checkPermi(['sms:export-sale-contract:detail'])
    ) {
      router.push({ path: '/sms/sale-orders/exportSaleContract' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 3) {
    //采购计划
    // scm:purchase-plan:query
    // scm:purchase-plan:detail
    if (checkPermi(['scm:purchase-plan:query']) && checkPermi(['scm:purchase-plan:detail'])) {
      router.push({ path: '/scm/purchase/plan' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 4) {
    //采购合同
    // scm:purchase-contract:query
    // scm:purchase-contract:detail
    if (
      checkPermi(['scm:purchase-contract:query']) &&
      checkPermi(['scm:purchase-contract:detail'])
    ) {
      router.push({ path: '/scm/purchase/contract' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 6) {
    //出运计划
    // dms:shipment-plan:query
    if (checkPermi(['dms:shipment-plan:query'])) {
      router.push({ path: '/dms/shipping-manage/shipping-plan' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 7) {
    //出运明细
    // dms:shipment:query

    if (checkPermi(['dms:shipment:query'])) {
      router.push({ path: '/dms/shipping-manage/shipping' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 11) {
    //拉柜通知单
    // dms:container-transportation:query
    // dms:container-transportation:detail
    if (
      checkPermi(['dms:container-transportation:query']) &&
      checkPermi(['dms:container-transportation:detail'])
    ) {
      router.push({ path: '/dms/ContainerNotice' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 12) {
    //报关单
    // dms:declaration:query

    if (checkPermi(['dms:declaration:query'])) {
      router.push({ path: '/dms/shipping-orders/declaration' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 13) {
    //结汇单
    // dms:settlement-form:query

    if (checkPermi(['dms:settlement-form:query'])) {
      router.push({ path: '/dms/shipping-orders/settlementForm' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 14) {
    //商检单
    // dms:commodity-inspection:query
    if (checkPermi(['dms:commodity-inspection:query'])) {
      router.push({ path: '/dms/shipping-orders/commodityInspection' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 16) {
    //验货单
    // qms:quality-inspection:query
    if (checkPermi(['qms:quality-inspection:query'])) {
      router.push({ path: '/scm/quality-inspection-manage/quality-inspection' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 17) {
    //付款单
    if (checkPermi(['fms:payment:query']) && checkPermi(['fms:payment:detail'])) {
      router.push({ path: '/finance/casher/fees-list' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 20) {
    //付款申请单
    // scm:payment-apply:query
    if (checkPermi(['scm:payment-apply:query'])) {
      router.push({ path: '/scm/vender-payment/payment-apply' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 21) {
    //开票通知
    // scm:invoicing-notices:query
    // scm:invoicing-notices:detail
    if (
      checkPermi(['scm:invoicing-notices:query']) &&
      checkPermi(['scm:invoicing-notices:detail'])
    ) {
      router.push({ path: '/scm/vender-payment/invoicingNotices' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  } else if (val.type === 23) {
    //入库通知单
    // wms:notice:query
    if (checkPermi(['wms:notice:query'])) {
      router.push({ path: '/wms/1/renotice' })
    } else {
      ElMessage.error('暂无权限查看详情')
    }
  }
}
const allList = ref([])
const init = async () => {
  let res = await orderlinkList({
    linkCodeList: props?.data?.linkCodeList.join(',')
  })
  allList.value = res
  let curTabBeforeTabList: any[] = [],
    curTabAfterTabList: any[] = []
  allList.value.forEach((item: any) => {
    if (props?.data?.code == item.code || props?.data?.sourceCode == item.code) {
      curTabBeforeTabList = tabs.find((el) => el.type == item.type)?.beforeTabList
      curTabAfterTabList = tabs.find((el) => el.type == item.type)?.afterTabList
    }
  })
  if (!isValidArray(curTabBeforeTabList) && !isValidArray(curTabAfterTabList)) {
    emptyFlag.value = true
    return false
  }
  allList.value.forEach((item: any) => {
    if (
      curTabBeforeTabList.includes(item.type) &&
      !beforeTabList.value?.map((el) => el.type).includes(item.type)
    ) {
      beforeTabList.value.push({ type: item.type, name: item.name })
    }
    if (
      curTabAfterTabList.includes(item.type) &&
      !afterTabList.value?.map((el) => el.type).includes(item.type)
    ) {
      afterTabList.value.push({ type: item.type, name: item.name })
    }
  })
  if (beforeTabList.value?.length > 0) {
    activeNameChild.value = beforeTabList.value[0].type
  } else if (afterTabList.value?.length > 0) {
    activeNameChild.value = afterTabList.value[0].type
  }
  handleClick({ paneName: activeNameChild.value })
}
onMounted(async () => {
  if (isValidArray(props?.data?.linkCodeList)) {
    init()
  } else {
    emptyFlag.value = true
  }
})

defineExpose({
  init
})
</script>
<style lang="scss" scoped>
.wrap {
  overflow: hidden;

  .left_box {
    float: left;
    width: 110px;
  }

  .right_box {
    margin-left: 110px;
    min-height: 300px;
  }
}

:deep(.is-left) {
  width: 110px;
}

.el-tabs__content {
  :deep(.el-tabs__nav) {
    z-index: 0;
  }
}

:deep(.mytab) {
  width: 110px;
  text-align: left;
  font-weight: bold;
}

:deep(.el-table) {
  height: 100% !important;

  .el-table__inner-wrapper {
    height: 100% !important;
  }
}
</style>
