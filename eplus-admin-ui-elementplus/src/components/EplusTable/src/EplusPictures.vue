<template>
  <div
    ref="eplusPicturesRef"
    class="eplusPictures"
    :style="{ height: `${props.height}px` }"
  >
    <el-card
      v-for="item in list"
      :key="item.id"
      shadow="hover"
      class="box-card"
      :style="{
        width: props.size?.width || '188px',
        height: props.size?.height || '268px',
        marginRight: pictureBtn === 'big' ? '35px' : pictureBtn === 'middle' ? '17px' : '10px',
        marginBottom: '10px'
      }"
      @click="() => handlerClick(item)"
      :body-style="{ padding: '12px !important' }"
    >
      <!-- Discontinued badge -->
      <div
        v-if="item.onshelfFlag === 0"
        class="discontinued-badge"
      >
        已停售
      </div>
      <div class="header mb-2">
        <!-- <span class="des">{{ columns?.[0]?.label }}</span>： -->
        <div class="flex items-center justify-between !w-100%"
          >{{ item?.[columns?.[0]?.prop] ? item?.[columns?.[0]?.prop] : '-' }}
          <ElTooltip
            placement="top"
            content="复制"
          >
            <ElIcon
              class="tip"
              style="margin-left: 10px"
              @click.stop="
                async () => {
                  await copyToClipboard(item?.[columns?.[0]?.prop])
                }
              "
            >
              <DocumentCopy />
            </ElIcon>
          </ElTooltip>
        </div>
      </div>
      <component :is="renderImg(item)" />
      <div class="footer mt-2">
        <!-- <span class="des">{{ columns?.[1]?.label }}</span>： -->
        <div class="flex items-center justify-between !w-100%">
          <!-- {{ item?.[columns?.[1]?.prop] ? item?.[columns?.[1]?.prop] : '-' }} -->
          <div>{{ item?.[columns?.[1]?.prop] ? item?.[columns?.[1]?.prop] : '-' }}</div>
          <ElTooltip
            placement="top"
            content="复制"
          >
            <ElIcon
              class="tip"
              style="margin-left: 10px"
              @click.stop="
                async () => {
                  await copyToClipboard(item?.[columns?.[1]?.prop])
                }
              "
            >
              <DocumentCopy />
            </ElIcon>
          </ElTooltip>
        </div>
      </div>
    </el-card>
  </div>
  <eplus-dialog ref="mainDetailRef">
    <template #detail="{ key }"> <main-detail :id="key" /></template>
    <template #edit="{ key }">
      <main-form
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import MainDetail from '@/views/pms/product/main/MainDetail.vue'
import MainForm from '@/views/pms/product/main/MainForm.vue'
import { ElIcon, ElTooltip } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

const message = useMessage() //顶部tip提示框
const mainDetailRef = ref()
const props = defineProps<{
  list: any
  columns: any
  size: any
  pictureBtn: any
  height: any
}>()
const emit = defineEmits(['click-emit'])
const handlerClick = (val) => {
  emit('click-emit', val)
  // mainDetailRef.value?.openDetail(val?.id)
}
const renderImg = (item) => {
  if (item?.mainPicture || item?.thumbnail) {
    return (
      <EplusImgEnlarge
        mainPicture={item?.mainPicture}
        thumbnail={item?.thumbnail}
        width="100%"
        height={props.size?.imgHeight}
        disabled={true}
      />
    )
  } else {
    return (
      <img
        src="/empty-img.jpg"
        alt=""
        class={`w-100% h-${props.size?.imgHeight}`}
        style={{ height: props.size?.imgHeight }}
      />
    )
  }
}
const copyToClipboard = async (text) => {
  const textarea = document.createElement('textarea')
  textarea.value = text
  document.body.appendChild(textarea)
  textarea.select()
  try {
    const success = document.execCommand('copy')
    // await navigator.clipboard.writeText(text)
    message.success('复制成功！')
  } catch (err) {
    message.error('失败！' + err)
  }
  document.body.removeChild(textarea)
}
const eplusPicturesRef = ref()
watch(
  () => props.list,
  (newVal) => {
    if (eplusPicturesRef.value) {
      eplusPicturesRef.value.scrollTop = 0
    }
  }
)
</script>
<style scoped lang="scss">
.eplusPictures {
  display: flex;
  flex-wrap: wrap;
  overflow: auto;
  align-content: flex-start;
}

.box-card {
  // width: 235px;
  // height: 335px;
  position: relative;
  cursor: pointer;
  margin-bottom: 10px;
  margin-right: 10px;
}
/* stylelint-disable-next-line rule-empty-line-before */
.tip {
  visibility: hidden;
}

.box-card:hover {
  .tip {
    visibility: visible;
  }
}

.header,
.footer {
  font-size: 14px;

  .overflow {
    white-space: nowrap; /* 防止文字换行 */
    overflow: hidden; /* 隐藏溢出的内容 */
    text-overflow: ellipsis; /* 显示省略号 */
    width: 200px; /* 设置容器的宽度 */
  }

  // .tip {
  //   position: absolute;
  //   right: 0;
  // }

  .des {
    font-size: 16px;
    font-weight: bold;
    color: #aaa;
  }
}

.discontinued-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #ff0000;
  color: white;
  padding: 4px 10px;
  border-radius: 0 0 0 8px;
  font-size: 12px;
  font-weight: bold;
  z-index: 100;
  pointer-events: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
</style>
