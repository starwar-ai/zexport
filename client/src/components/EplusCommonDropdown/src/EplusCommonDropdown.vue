<template>
  <el-dropdown
    :placement="placement"
    :trigger="trigger || 'hover'"
    @command="handleCommand"
  >
    <el-button>{{ props?.btnText || '更多' }}</el-button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in props.dropdownList"
          :command="item.key"
          :key="item.key"
          >{{ item.name }}</el-dropdown-item
        >
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>
<script lang="tsx" setup>
const props = defineProps<{
  placement?: string
  btnText?: string
  dropdownList: any[]
  trigger?: string
}>()
const handleCommand = async (key) => {
  let res = await props.dropdownList.find((item) => item.key === key)
  if (res && res?.handler) {
    res.handler()
  }
  console.log(key)
}
</script>
<style lang="scss" scoped>
.el-dropdown {
  margin-right: 12px;
}
</style>
