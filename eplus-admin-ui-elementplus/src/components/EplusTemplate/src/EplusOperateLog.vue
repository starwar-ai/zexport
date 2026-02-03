<template>
  <Table
    style="width: 100%"
    :columns="operateLogColumns"
    headerAlign="center"
    align="center"
    :data="props.logList"
  />

  <el-dialog
    v-model="dialogVisible"
    :align-center="true"
    :append-to-body="true"
    width="700px"
    :show-close="false"
  >
    <template #header="{ close }">
      <div
        class="my-header"
        style="text-align: right"
      >
        <el-button
          link
          @click="close"
        >
          <Icon
            icon="ep:circle-close-filled"
            style="font-size: 24px; color: #409eff"
          />
        </el-button>
      </div>
    </template>

    <div style="text-align: center">
      <img
        style="max-width: 100%"
        w-full
        :src="dialogImageUrl"
        alt="Preview Image"
      />
    </div>
  </el-dialog>
</template>

<script lang="tsx" setup>
import { ref } from 'vue'
import { formatDateColumn } from '@/utils/table'

const props = defineProps<{
  logList?: any
}>()
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const handlePreview = (val) => {
  dialogImageUrl.value = val
  dialogVisible.value = true
}

const operateLogColumns = [
  {
    label: '操作时间',
    field: 'startTime',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
    width: 180
  },
  {
    label: '操作人',
    field: 'userNickName',
    width: 180
  },
  {
    label: '操作类型',
    field: 'type',
    width: 180
  },
  {
    label: '操作详情',
    field: 'content',
    align: 'left',
    slots: {
      default: (data) => {
        const { row } = data
        return <div v-html={row.content}></div>
      }
    }
    // slots: {
    //   default: (data) => {
    //     const { row } = data
    //     if (isValidArray(row.content)) {
    //       return row.content.map((item) => {
    //         if (item.operateType == 2) {
    //           return <p>{`${item.fieldName}`}</p>
    //         } else {
    //           if (!item.pictureFlag) {
    //             return <p>{`字段【${item.fieldName}】: ${item.oldValue} 修改为 ${item.value}`}</p>
    //           } else {
    //             let imgUrl = ''
    //             if (item.oldValue) {
    //               imgUrl = `${import.meta.env.VITE_BASE_URL}/admin-api${item.oldValue}`
    //             } else {
    //               imgUrl = `${import.meta.env.VITE_BASE_URL}/admin-api${item.value}`
    //             }
    //             return (
    //               <p onClick={() => handlePreview(imgUrl)}>
    //                 {`${item.fieldName}: `}
    //                 <img
    //                   src={imgUrl}
    //                   width="30"
    //                   height="30"
    //                 />
    //               </p>
    //             )
    //           }
    //         }
    //       })
    //     } else {
    //       return '-'
    //     }
    //   }
    // }
  }
]
</script>
