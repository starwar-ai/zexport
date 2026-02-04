<template>
  <div class="import-result-page">
    <div class="page-header">
      <h2>库存导入结果分析</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <!-- 统计信息 -->
    <div class="summary-section">
      <el-row :gutter="0">
        <el-col
          :span="14"
          :md="8"
          :sm="12"
          :xs="24"
          class="no-padding-col"
        >
          <el-card class="summary-card success">
            <div class="summary-content">
              <div class="summary-number">{{ successList.length }}</div>
              <div class="summary-label">数据正常可以导入</div>
              <el-button
                class="confirm-btn"
                type="primary"
                size="small"
                round
                :loading="loading"
                :disabled="!successList.length || loading"
                @click="handleConfirmImport"
              >
                <el-icon style="margin-right: 4px"><CircleCheck /></el-icon>
                确认导入
              </el-button>
            </div>
          </el-card>
        </el-col>
        <el-col
          :span="8"
          :md="8"
          :sm="12"
          :xs="24"
          class="no-padding-col"
        >
          <el-card class="summary-card error">
            <div class="summary-content">
              <div class="summary-number">{{ errorData.length }}</div>
              <div class="summary-label">数据异常不能导入</div>
            </div>
          </el-card>
        </el-col>
        <el-col
          :span="8"
          :md="8"
          :sm="12"
          :xs="24"
          class="no-padding-col"
        >
          <el-card class="summary-card total">
            <div class="summary-content">
              <div class="summary-number">{{ totalCount }}</div>
              <div class="summary-label">总计</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <!-- 成功数据表格 -->
    <div class="result-section table-scroll">
      <h3 class="section-title success">
        <el-icon><CircleCheck /></el-icon>
        成功导入数据 ({{ successList.length }})
      </h3>
      <div class="table-inner">
        <el-table
          :data="successList"
          stripe
          style="min-width: 1200px"
          v-loading="loading"
        >
          <el-table-column
            label="状态"
            min-width="100"
          >
            <template #default>
              <el-tag type="success">成功</el-tag>
            </template>
          </el-table-column>
          <!-- <el-table-column
            prop="importCode"
            label="导入批次号"
            min-width="160"
          /> -->
          <el-table-column
            prop="skuCode"
            label="SKU编号"
            min-width="160"
          />
          <el-table-column
            prop="skuName"
            label="产品中文名称"
            min-width="180"
          />
          <el-table-column
            prop="cskuCode"
            label="客户货号"
            min-width="160"
          />
          <!-- <el-table-column
            prop="splitBoxFlag"
            label="是否分箱"
            min-width="120"
          /> -->
          <el-table-column
            prop="cabinetQuantity"
            label="转拉柜数量"
            min-width="140"
          />
          -->
          <el-table-column
            prop="basicSkuCode"
            label="基础产品编号"
            min-width="160"
          />
          <!-- <el-table-column
            prop="batchCode"
            label="批次号"
            min-width="160"
          /> -->
          <el-table-column
            prop="warehouseName"
            label="仓库名称"
            min-width="160"
          />
          <el-table-column
            prop="position"
            label="存放位置"
            min-width="160"
          />
          <el-table-column
            prop="initQuantity"
            label="入库库存"
            min-width="120"
          />
          <!-- <el-table-column
            prop="usedQuantity"
            label="出库数量"
            min-width="120"
          />
          <el-table-column
            prop="lockQuantity"
            label="锁定数量"
            min-width="120"
          />
          <el-table-column
            prop="availableQuantity"
            label="可用数量"
            min-width="120"
          />
          <el-table-column
            prop="qtyPerOuterbox"
            label="外箱装量"
            min-width="120"
          />
          <el-table-column
            prop="qtyPerInnerbox"
            label="内盒装量"
            min-width="120"
          /> -->
          <el-table-column
            prop="price"
            label="价格"
            min-width="140"
          >
            <template #default="{ row }">{{ formatAmount(row.price) }}</template>
          </el-table-column>
          <el-table-column
            prop="totalAmount"
            label="总金额"
            min-width="160"
          >
            <template #default="{ row }">{{ formatAmount(row.totalAmount) }}</template>
          </el-table-column>
          <el-table-column
            prop="venderName"
            label="供应商名称"
            min-width="180"
          />
          <el-table-column
            prop="custName"
            label="客户名称"
            min-width="180"
          />
          <el-table-column
            prop="custPo"
            label="客户PO号"
            min-width="160"
          />
          <!-- <el-table-column
            prop="outerboxLength"
            label="外箱长(cm)"
            min-width="140"
          />
          <el-table-column
            prop="outerboxWidth"
            label="外箱宽(cm)"
            min-width="140"
          />
          <el-table-column
            prop="outerboxHeight"
            label="外箱高(cm)"
            min-width="140"
          />
          <el-table-column
            prop="outerboxVolume"
            label="单箱体积"
            min-width="140"
          />
          <el-table-column
            prop="outerboxGrossweight"
            label="单箱毛重"
            min-width="160"
          >
            <template #default="{ row }">{{ display(row.outerboxGrossweight) }}</template>
          </el-table-column> -->
          <el-table-column
            prop="totalVolume"
            label="总体积"
            min-width="140"
          />
          <el-table-column
            prop="totalWeight"
            label="总毛重"
            min-width="160"
          >
            <template #default="{ row }">{{ display(row.totalWeight) }}</template>
          </el-table-column>
          <!-- <el-table-column
            prop="palletVolume"
            label="单托体积"
            min-width="140"
          />
          <el-table-column
            prop="palletWeight"
            label="单托毛重"
            min-width="160"
          >
            <template #default="{ row }">{{ display(row.palletWeight) }}</template>
          </el-table-column> -->
          <el-table-column
            prop="remark"
            label="备注"
            min-width="200"
          />
          <!-- <el-table-column
            prop="createTime"
            label="创建时间"
            min-width="180"
          /> -->
        </el-table>
      </div>
    </div>

    <!-- 错误数据表格 -->
    <div class="result-section table-scroll">
      <h3 class="section-title error">
        <el-icon><CircleClose /></el-icon>
        导入失败数据 ({{ errorData.length }})
      </h3>
      <div class="table-inner">
        <el-table
          :data="errorData"
          stripe
          style="min-width: 1200px"
          v-loading="loading"
        >
          <el-table-column
            label="状态"
            min-width="100"
          >
            <template #default>
              <el-tag type="danger">失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="errorRemark"
            label="失败原因"
            min-width="220"
          />
          <!-- <el-table-column
            prop="importCode"
            label="导入批次号"
            min-width="160"
          /> -->
          <el-table-column
            prop="skuCode"
            label="SKU编号"
            min-width="160"
          />
          <el-table-column
            prop="skuName"
            label="产品中文名称"
            min-width="180"
          />
          <el-table-column
            prop="cskuCode"
            label="客户货号"
            min-width="160"
          />
          <!-- <el-table-column
            prop="splitBoxFlag"
            label="是否分箱"
            min-width="120"
          /> -->
          <el-table-column
            label="规格"
            min-width="200"
          >
            <template #default="{ row }">{{ formatSpec(row) }}</template>
          </el-table-column>
          <!-- <el-table-column
            prop="cabinetQuantity"
            label="转拉柜数量"
            min-width="140"
          /> -->
          <el-table-column
            prop="basicSkuCode"
            label="基础产品编号"
            min-width="160"
          />
          <!-- <el-table-column
            prop="batchCode"
            label="批次号"
            min-width="160"
          /> -->
          <el-table-column
            prop="warehouseName"
            label="仓库名称"
            min-width="160"
          />
          <el-table-column
            prop="position"
            label="存放位置"
            min-width="160"
          />
          <el-table-column
            prop="initQuantity"
            label="入库库存"
            min-width="120"
          />
          <!-- <el-table-column
            prop="usedQuantity"
            label="出库数量"
            min-width="120"
          />
          <el-table-column
            prop="lockQuantity"
            label="锁定数量"
            min-width="120"
          />
          <el-table-column
            prop="availableQuantity"
            label="可用数量"
            min-width="120"
          />
          <el-table-column
            prop="qtyPerOuterbox"
            label="外箱装量"
            min-width="120"
          />
          <el-table-column
            prop="qtyPerInnerbox"
            label="内盒装量"
            min-width="120"
          /> -->
          <el-table-column
            prop="price"
            label="价格"
            min-width="140"
          >
            <template #default="{ row }">{{ formatAmount(row.price) }}</template>
          </el-table-column>
          <el-table-column
            prop="totalAmount"
            label="总金额"
            min-width="160"
          >
            <template #default="{ row }">{{ formatAmount(row.totalAmount) }}</template>
          </el-table-column>
          <el-table-column
            prop="venderName"
            label="供应商名称"
            min-width="180"
          />
          <el-table-column
            prop="custName"
            label="客户名称"
            min-width="180"
          />
          <el-table-column
            prop="custPo"
            label="客户PO号"
            min-width="160"
          />
          <!-- <el-table-column
            prop="outerboxLength"
            label="外箱长(cm)"
            min-width="140"
          />
          <el-table-column
            prop="outerboxWidth"
            label="外箱宽(cm)"
            min-width="140"
          />
          <el-table-column
            prop="outerboxHeight"
            label="外箱高(cm)"
            min-width="140"
          /> -->
          <!-- <el-table-column
            prop="outerboxVolume"
            label="单箱体积"
            min-width="140"
          />
          <el-table-column
            prop="outerboxGrossweight"
            label="单箱毛重"
            min-width="160"
          >
            <template #default="{ row }">{{ display(row.outerboxGrossweight) }}</template>
          </el-table-column> -->
          <el-table-column
            prop="totalVolume"
            label="总体积"
            min-width="140"
          />
          <el-table-column
            prop="totalWeight"
            label="总毛重"
            min-width="160"
          >
            <template #default="{ row }">{{ display(row.totalWeight) }}</template>
          </el-table-column>
          <!-- <el-table-column
            prop="palletVolume"
            label="单托体积"
            min-width="140"
          />
          <el-table-column
            prop="palletWeight"
            label="单托毛重"
            min-width="160"
          >
            <template #default="{ row }">{{ display(row.palletWeight) }}</template>
          </el-table-column> -->

          <el-table-column
            prop="remark"
            label="备注"
            min-width="200"
          />
          <!-- <el-table-column
            prop="createTime"
            label="创建时间"
            min-width="180"
          /> -->
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import * as stockApi from '@/api/wms/stock/index'

defineOptions({ name: 'ImportResult' })

const router = useRouter()
const route = useRoute()
const message = useMessage()

const loading = ref(false)
const successList = ref<any[]>([])
const errorData = ref<any[]>([])
const importCode = ref('')

// 计算总数
const totalCount = computed(() => successList.value.length + errorData.value.length)

// 返回上一页
const goBack = () => {
  router.back()
}

// 处理导入结果数据
const processImportData = (data: any) => {
  try {
    // 兼容 stock-import/import-excel 返回格式：{ successList: [], errorList: [] }
    // 统一取数工具

    // 新增：处理importCode
    if (data.importCode) {
      importCode.value = data.importCode
    }

    console.log(importCode.value)
    const pickArray = (obj: any, keys: string[]): any[] | null => {
      for (const key of keys) {
        const val = obj?.[key]
        if (Array.isArray(val)) return val
        // 常见后端分页包装 {records: []} / {list: []}
        if (val && (Array.isArray(val.records) || Array.isArray(val.list))) {
          return val.records || val.list
        }
      }
      return null
    }

    // 成功列表
    const suc = pickArray(data, ['successList', 'success', 'successes', 'okList', 'data'])
    if (Array.isArray(suc)) {
      const mapped = suc.map((item: any) => {
        if (typeof item === 'string') {
          return { username: item, status: '成功', message: '导入成功' }
        }
        // 保持后端对象原样，尽量不要丢字段
        return { ...item }
      })
      successList.value = [...successList.value, ...mapped]
    }

    // 失败列表
    const err = pickArray(data, ['errorList', 'failList', 'errors', 'failed', 'error'])
    if (Array.isArray(err)) {
      errorData.value = err.map((item: any) => {
        if (typeof item === 'string') {
          return { username: '-', status: '导入失败', errorRemark: item }
        }
        // 保持对象字段，同时规范失败原因字段
        const reason = item.errorRemark ?? item.message ?? item.msg ?? item.error
        return { ...item, errorRemark: reason }
      })
    }

    // 兼容之前用户导入的结构：createUsernames/updateUsernames/failureUsernames
    if (data.createUsernames && Array.isArray(data.createUsernames)) {
      const created = data.createUsernames.map((username: string) => ({
        username,
        status: '创建成功',
        message: '数据创建成功'
      }))
      successList.value = [...successList.value, ...created]
    }

    if (data.updateUsernames && Array.isArray(data.updateUsernames)) {
      const updated = data.updateUsernames.map((username: string) => ({
        username,
        status: '更新成功',
        message: '数据更新成功'
      }))
      successList.value = [...successList.value, ...updated]
    }

    if (data.failureUsernames && typeof data.failureUsernames === 'object') {
      const failures = Object.entries(data.failureUsernames).map(([username, errorMsg]) => ({
        username,
        status: '导入失败',
        message: errorMsg as string
      }))
      errorData.value = [...errorData.value, ...failures]
    }
    if (successList.value.length === 0 && errorData.value.length === 0) {
      console.warn('导入结果解析为空，原始数据为:', data)
      message.warning('未解析到导入结果，请联系管理员或检查接口返回结构')
    }
  } catch (error) {
    console.error('处理导入数据失败:', error)
    message.error('处理导入数据失败')
  }
}

// 通用展示函数：将对象/数组友好显示在表格中
const display = (value: any): string => {
  if (value === null || value === undefined) return '-'
  if (Array.isArray(value)) {
    return value.map((item) => display(item)).join(' / ')
  }
  if (typeof value === 'object') {
    // 金额
    if ('amount' in value && 'currencyUnitName' in value) {
      // @ts-ignore 动态结构
      return `${value.amount} ${value.currencyUnitName}`
    }
    // 重量
    if ('weight' in value && 'unit' in value) {
      // @ts-ignore 动态结构
      return `${value.weight} ${value.unit}`
    }
    // 一般对象
    try {
      return JSON.stringify(value)
    } catch (_e) {
      return String(value)
    }
  }
  return String(value)
}

// 规格格式化：优先展示 outerbox 长宽高/体积/重量；无则回退到 specificationList
const formatSpec = (row: any): string => {
  const length = row?.outerboxLength
  const width = row?.outerboxWidth
  const height = row?.outerboxHeight
  const volume = row?.outerboxVolume
  const gross = row?.outerboxGrossweight
  const net = row?.outerboxNetweight

  const parts: string[] = []

  if (length || width || height) {
    const l = length ?? '-'
    const w = width ?? '-'
    const h = height ?? '-'
    parts.push(`${l}×${w}×${h} cm`)
  }

  if (volume) {
    parts.push(`${volume} cm³`)
  }

  if (gross && typeof gross === 'object' && 'weight' in gross && 'unit' in gross) {
    // @ts-ignore
    parts.push(`毛重 ${gross.weight}${gross.unit}`)
  }

  if (net && typeof net === 'object' && 'weight' in net && 'unit' in net) {
    // @ts-ignore
    parts.push(`净重 ${net.weight}${net.unit}`)
  }

  if (parts.length > 0) return parts.join(' / ')

  // 回退到 specificationList 的通用展示
  return display(row?.specificationList)
}

// 金额格式化：支持 { amount: 615900, currency: 'RMB' } 或 { amount: 6159, currencyUnitName: 'RMB' }
const formatAmount = (val: any): string => {
  if (!val) return '-'
  // 直接数字
  if (typeof val === 'number') return Number(val).toLocaleString()

  const amountRaw = val.amount ?? val.value ?? null
  const currency = val.currency ?? val.currencyUnitName ?? ''
  if (amountRaw === null || amountRaw === undefined) return '-'

  // 兼容分/厘：若为整数且>=1000，按分转元；否则按原值
  const amountNum = Number(amountRaw)
  const normalized =
    Number.isInteger(amountNum) && Math.abs(amountNum) >= 1000 ? amountNum / 100 : amountNum

  const formatted = normalized.toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
  return currency ? `${formatted} ${currency}` : formatted
}

// 页面初始化
onMounted(() => {
  try {
    const queryData = route.query.data
    if (!queryData || queryData === 'null' || queryData === 'undefined') {
      message.warning('未找到导入结果数据')
      router.back()
      return
    }

    const raw = decodeURIComponent(queryData as string)
    if (!raw || raw === '{}' || raw === '[]') {
      message.warning('导入结果为空')
      router.back()
      return
    }

    const decodedData = JSON.parse(raw)
    if (!decodedData || typeof decodedData !== 'object') {
      message.warning('导入结果格式异常')
      router.back()
      return
    }

    // 后端约定为 { successList: StockImportRespVO[], errorList: StockImportRespVO[] }
    processImportData(decodedData)
  } catch (error) {
    console.error('解析导入数据失败:', error)
    message.error('解析导入数据失败')
    router.back()
  }
})

// 确认导入
const handleConfirmImport = async () => {
  try {
    if (!successList.value.length) {
      message.warning('没有可导入的数据')
      return
    }
    loading.value = true
    // 优先使用处理后的数据中的importCode，如果没有则尝试从路由参数获取
    const importCodeValue = importCode.value || (route.query.importCode as string)

    if (!importCodeValue) {
      message.error('缺少导入编号，请重新导入')
      loading.value = false
      return
    }

    // 根据后端接口要求，只需要传递importCode作为请求参数
    const params = {
      importCode: importCodeValue
    }

    console.log('提交导入数据:', params)
    // 调用后端接口，传递importCode作为参数
    await stockApi.confirmImportStock(params)
    message.success('导入成功')
    // 修改：不使用back()而是使用push()并添加刷新参数
    router.push({
      path: '/wms/stock', // 正确的库存列表页面路径
      query: { refresh: 'true' } // 添加刷新参数
    })
  } catch (e: any) {
    console.error('确认导入失败:', e)
    message.error(e?.message || '导入失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.import-result-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  h2 {
    margin: 0;
    color: #303133;
    font-size: 24px;
  }
}

.result-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .section-title {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 16px;
    font-size: 18px;
    font-weight: 600;

    .el-icon {
      margin-right: 8px;
      font-size: 20px;
    }

    &.success {
      color: #67c23a;
    }

    &.error {
      color: #f56c6c;
    }
  }

  // 表格细节优化
  :deep(.el-table) {
    border-radius: 6px;
    overflow: hidden;

    // 表头样式优化
    .el-table__header-wrapper {
      background-color: #f7f8fa;

      th {
        background-color: #f7f8fa !important;
        color: #606266;
        font-weight: 600;
        border-bottom: 1px solid #ebeef5;
      }
    }

    // 表格内容样式
    .el-table__body {
      td {
        border-bottom: 1px solid #ebeef5;
        transition: background-color 0.2s ease;

        // 行悬停效果
        &:hover {
          background-color: #f5f7fa;
        }
      }
    }

    // 单元格内边距调整
    .cell {
      padding: 10px 12px;
      font-size: 14px;
    }

    // 斑马纹样式优化 - 修复错误：使用完整选择器替代&--striped
    &.el-table--striped {
      .el-table__body {
        tr.el-table__row--striped {
          td {
            background-color: #fafafa;
          }
        }
      }
    }
  }
}

.table-scroll {
  overflow-x: auto;
  padding-bottom: 10px; // 为横向滚动条预留空间

  // 自定义滚动条（WebKit）
  &::-webkit-scrollbar {
    height: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #f0f2f5;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #c0c4cc;
    border-radius: 4px;
    transition: background-color 0.2s ease;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: #a6a9ad;
  }

  // 适配 Firefox
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc #f0f2f5;
}

.table-inner {
  min-width: 100%;
}

// 统计信息区域样式优化
.summary-section {
  margin-bottom: 24px;
  padding: 0;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #ebeef5;
  height: 200px;
  background-color: #ffffff;
  display: flex;
  gap: 10px; // 设置卡片间隔为10像素
  padding: 10px; // 添加内边距以确保间隔生效
  width: 100%;

  // 移除列的内边距
  .no-padding-col {
    padding: 0 !important;
    margin: 0 !important;
    width: calc(33.333% - 6.666px) !important; // 宽度为行的1/3，减去间隔的影响
    min-width: calc(33.333% - 6.666px) !important;
    max-width: calc(33.333% - 6.666px) !important;
  }

  .summary-card {
    border: none;
    box-shadow: none;
    height: 100%;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border-radius: 8px !important; // 为每个卡片设置圆角
    position: relative;
    overflow: hidden;
    width: 100%;
    min-width: 0;
    flex-shrink: 0; // 防止卡片被压缩

    // 为卡片添加装饰元素
    &::before {
      content: '';
      position: absolute;
      top: 0;
      right: 0;
      width: 80px;
      height: 80px;
      border-radius: 50%;
      opacity: 0.15;
      transform: translate(50%, -50%);
      transition: transform 0.3s ease;
    }

    // 成功卡片样式
    &.success {
      background: linear-gradient(135deg, #f0f9ea 0%, #e1f3d8 100%);
      &::before {
        background-color: #67c23a;
      }
    }

    // 错误卡片样式
    &.error {
      background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
      &::before {
        background-color: #f56c6c;
      }
    }

    // 总计卡片样式
    &.total {
      background: linear-gradient(135deg, #ecf5ff 0%, #ebefff 100%);
      &::before {
        background-color: #409eff;
      }
    }

    // 卡片悬停效果
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
      z-index: 10;
      &::before {
        transform: translate(40%, -40%) scale(1.2);
      }
    }

    // 卡片内容样式
    .summary-content {
      padding: 20px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 12px;
      height: 100%;
      position: relative;
      z-index: 1;

      .summary-number {
        font-size: 36px;
        font-weight: 700;
        margin-bottom: 4px;
        transition: transform 0.3s ease;
        line-height: 1;
        // 添加数字动画
        animation: fadeInUp 0.6s ease-out;
      }

      .summary-label {
        font-size: 14px;
        color: #606266;
        font-weight: 500;
        // 添加标签动画
        animation: fadeInUp 0.6s ease-out 0.1s both;
        text-align: center;
      }

      // 卡片悬停效果
      &:hover .summary-number {
        transform: scale(1.05);
      }
    }
  }

  // 确保覆盖Element Plus默认样式
  :deep(.el-row) {
    display: flex !important;
    width: 100% !important;
    height: 100% !important;
    margin: 0 !important;
    gap: 10px !important;
    &::before,
    &::after {
      display: none !important;
    }
  }

  :deep(.el-col) {
    display: block !important;
    width: calc(33.333% - 6.666px) !important; // 固定宽度为行的1/3
    min-width: calc(33.333% - 6.666px) !important;
    max-width: calc(33.333% - 6.666px) !important;
    padding: 0 !important;
    margin: 0 !important;
    height: 100% !important;
    flex-shrink: 0 !important; // 防止被压缩
  }
}

// 确认按钮样式优化
.confirm-btn {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  padding: 6px 16px;
  font-weight: 500;
  // 添加按钮动画
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.confirm-btn:disabled {
  opacity: 0.6;
  transform: none !important;
  box-shadow: none !important;
}

.confirm-btn:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(103, 194, 58, 0.3);
}

.confirm-btn:not(:disabled):active {
  transform: translateY(0);
}

// 添加动画效果
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计优化
@media screen and (max-width: 768px) {
  .import-result-page {
    padding: 10px;
  }

  .page-header {
    padding: 15px;
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;

    h2 {
      font-size: 20px;
    }
  }

  .result-section {
    padding: 15px;

    .section-title {
      font-size: 16px;
    }
  }

  .summary-section {
    height: auto !important;
    flex-direction: column !important;
    gap: 10px !important;
    padding: 10px !important;
  }

  .summary-card,
  .no-padding-col,
  :deep(.el-col) {
    width: 100% !important;
    min-width: 100% !important;
    max-width: 100% !important;
    min-height: 120px !important;
  }
}
</style>
