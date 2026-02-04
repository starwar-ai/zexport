<template>
  <div class="table-box">
    <div class="left-table about">
      <el-tabs
        v-model="activeName"
        class="demo-tabs"
        @tab-click="handleClick"
      >
        <el-tab-pane
          v-for="item in options.editableTabs"
          :key="item.name"
          :label="item.title"
          :name="item.name"
        />
      </el-tabs>
      <el-button
        type="primary"
        style="float: right"
        size="small"
        >新增</el-button
      >
      <el-table
        :data="options.tableData"
        style="width: 100vw"
      >
        <el-table-column
          v-for="(item, index) in options.column"
          :key="index"
          :prop="item.prop"
          :label="item.label"
          :width="item.width ?? ''"
          show-overflow-tooltip
        />
        <!-- 操作 -->
        <el-table-column
          fixed="right"
          label="操作"
          min-width="40"
        >
          <template #default>
            <el-button
              link
              type="primary"
              size="small"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="right-config about">
      <el-form
        :model="options"
        label-width="auto"
        style="max-width: 600px"
      >
        <el-form-item
          label="Tabs标签"
          class="form-item"
        >
          <el-input
            v-model="options.tabs"
            placeholder=" 未报销/已报销"
            @blur="setTabs(options.tabs)"
          />
        </el-form-item>
        <span class="tips"
          >提示: 如果需要tabs页签,则使用/分隔,不需要则置空。例如: 未报销/已报销
        </span>
        <el-form-item
          label="表头"
          class="form-item"
        >
          <el-input
            v-model="options.columnLabel"
            placeholder=" 序号/名称/日期"
            @blur="setColumnLabel(options.columnLabel)"
          />
        </el-form-item>
        <span class="tips">提示: 请使用/分隔。例如: 序号/名称/日期 </span>
        <el-form-item
          label="字段"
          class="form-item"
        >
          <el-input
            v-model="options.prop"
            placeholder=" index/name/date"
            @blur="setColumnProp(options.prop)"
          />
        </el-form-item>
        <span class="tips"
          >提示: 请使用/分隔，注意字段和表头内容相对应。例如: index/name/date
        </span>
        <el-form-item
          label="新增按钮"
          class="form-item"
        >
          <el-radio-group v-model="options.add">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="操作按钮"
          class="form-item"
        >
          <el-radio-group v-model="options.operation">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ElButton } from 'element-plus'

const visible = ref(false)
const form = reactive({})

const props = defineProps<{}>()
const emit = defineEmits(['addCard'])
const options = reactive({
  tabs: '',
  columnLabel: '',
  prop: '',
  add: true,
  operation: true,

  tableData: [],
  column: [
    {
      prop: 'invoiceAmount',
      label: '报销金额'
    },
    {
      prop: 'reimbType',
      label: '报销类型'
    },
    {
      prop: '',
      label: '发票凭证'
    }
  ],
  editableTabs: [
    {
      title: '未报销',
      name: '1'
    },
    {
      title: '已报销',
      name: '2'
    }
  ]
})

const setTabs = (tabs) => {
  options.editableTabs = []
  let tabList = tabs.split('/')
  if (tabList.length >= 2) {
    tabList.forEach((e, i) => {
      options.editableTabs.push({ title: e, name: i })
    })
  }
}

const setColumnLabel = (column) => {
  options.column = []
  let columnList = column.split('/')
  if (columnList.length) {
    columnList.forEach((e, i) => {
      options.column.push({ prop: '', label: e })
    })
  }
}

const setColumnProp = (column) => {
  let columnList = column.split('/')
  if (columnList.length) {
    options.column.forEach((item, index) => {
      columnList.forEach((prop, i) => {
        if (index == i) {
          item.prop = prop
        }
      })
    })
  }
}

const handleAddCard = (type) => {
  emit('addCard', type)
  visible.value = false
}

watch(
  options,
  (newOption, oldOption) => {
    emit('setOptions', options)
  },
  {
    deep: true // 如果需要深度监听对象内部属性的变化
  }
)
// 监听变化
// watch([col, rowH, gutter, layoutdata], () => {
//   calcWidth()
//   if (!isDraging.value) calcLayoutHeight() // 拖拽中不使用layoutdata计算高度
//   drawGrid()
// })
onMounted(() => {})

defineExpose({
  // openDrawer
})
</script>

<style scoped lang="scss">
.table-box {
  width: 1100px;
  display: flex;
  justify-content: space-between;

  .about {
    width: 46%;
  }

  .left-table {
    .demo-tabs {
      margin-top: 10px;
    }
  }

  .right-config {
    .form-item {
      margin: 5px !important;
    }

    .tips {
      font-size: 12px;
      color: #d00;
      margin-left: 80px;
    }
  }
}
</style>
