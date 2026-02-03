<template>
  <el-card class="common-layout ml-2 mr-2 mt-2 mb-2">
    <div style="flex: 1">
      <el-container>
        <el-aside
          width="20%"
          class="el-aside"
        >
          <el-header>全部</el-header>
          <el-divider />
          <el-button
            v-for="item in leftBtnRef"
            :class="'btn' + (activeBtn === item.text ? ' active' : '')"
            :key="item.text"
            text
            @click="() => handleLeftClick(item)"
            >{{ item.text + `(${item.num})` }}</el-button
          >
        </el-aside>
        <el-divider direction="vertical" />
        <el-main>
          <el-tabs
            v-model="editableTabsValue"
            type="card"
            class="demo-tabs"
            @tab-remove="removeTab"
          >
            <el-tab-pane
              v-for="item in editableTabs"
              :key="item.name"
              :label="item.title"
              :name="item.name"
              :closable="item.name !== '1'"
            >
              <CheckPushTable />
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </div>
  </el-card>
</template>
<script lang="tsx" setup>
import CheckPushTable from './CheckPushTable.vue'

const activeBtn = ref<string>('采购合同')
const leftBtnRef = ref<any>([])
let tabIndex = 1
const editableTabsValue = ref('1')
const editableTabs = ref([
  {
    title: '采购合同',
    name: '1',
    content: 'Tab 1 content'
  }
])
const handleLeftClick = (val) => {
  activeBtn.value = val.text
  const newTabName = `${++tabIndex}`
  editableTabs.value.push({
    title: val.text,
    name: newTabName,
    content: 'New Tab content'
  })
  editableTabsValue.value = newTabName
}
const removeTab = (targetName: string) => {
  const tabs = editableTabs.value
  let activeName = editableTabsValue.value
  if (activeName === targetName) {
    tabs.forEach((tab, index) => {
      if (tab.name === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activeName = nextTab.name
        }
      }
    })
  }

  editableTabsValue.value = activeName
  editableTabs.value = tabs.filter((tab) => tab.name !== targetName)
}
onMounted(() => {
  console.log('mounted')
  leftBtnRef.value = [
    { text: '采购合同', num: 3 },
    { text: '销售合同', num: 4 },
    { text: '客户', num: 5 }
  ]
})
</script>
<style lang="scss" scoped>
.common-layout {
  flex: 1;
  display: flex;
}

:deep(.el-container) {
  flex: 1;
  display: flex;
}

.btn {
  margin-left: 0;
}

.el-aside {
  display: flex;
  flex-direction: column;
}

.active {
  color: skyblue;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
}

:deep(.el-card__body > div) {
  flex: 1;
  display: flex;
}
</style>
