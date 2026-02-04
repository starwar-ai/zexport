<template>
  <div class="number-box">
    <div class="left-number about">
      <div class="left-number-card">{{ options.number }}</div>
    </div>

    <div class="right-config about">
      <el-form
        :model="options"
        label-width="auto"
        style="max-width: 600px"
      >
        <el-form-item
          label="数据"
          prop="number"
          class="form-item"
          :rules="[{ required: true, message: '数据不能为空' }]"
        >
          <el-input
            v-model="options.number"
            placeholder="请输入数据"
            @blur="setNumber(options.number)"
          />
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
const props = defineProps<{}>()
const emit = defineEmits(['setOptions'])
const options = reactive({
  number: 0
})

const setNumber = (number) => {
  if (number == false) {
    options.number = 0
  }
  emit('setOptions', options.number)
}
emit('setOptions', options.number)

watch(options, (newOption, oldOption) => {}, {
  deep: true // 如果需要深度监听对象内部属性的变化
})

onMounted(() => {})
</script>

<style scoped lang="scss">
.number-box {
  width: 1100px;
  display: flex;
  justify-content: space-between;

  .about {
    width: 49%;
  }

  .left-number {
    display: flex;
    justify-content: center;
    align-items: center;
    border-right: 1px solid #ccc;

    .left-number-card {
      font-size: 120px;
      font-weight: 700;
      color: #1c9fff;
    }
  }

  .right-config {
    margin-top: 50px;
  }
}
</style>
