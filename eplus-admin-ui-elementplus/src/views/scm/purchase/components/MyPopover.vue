<template>
  <el-popover
    placement="top-start"
    :width="425"
    trigger="hover"
    content="this is content, this is content, this is content"
  >
    <template #reference>
      <el-avatar
        shape="square"
        :src="imgurl"
      />
    </template>
    <template #default>
      <div>
        <el-avatar
          shape="square"
          :size="400"
          :src="imgurl"
          style="margin-bottom: 8px" /></div
    ></template>
  </el-popover>
</template>
<script setup lang="tsx">
const props = defineProps<{
  mainPicture
}>()

const imgurl = ref('')

watchEffect(() => {
  imgurl.value = `${import.meta.env.VITE_BASE_URL}/admin-api${props.mainPicture}`
})
</script>
<style lang="scss" scoped></style>
