<template>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
  >
    <template #tableActions>
      <el-button
        type="primary"
        plain
        @click="handleCreate()"
        v-hasPermi="['system:form-change:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleDialogFailure"
  >
    <template #edit="{ key }">
      <FormChangeForm
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <FormChangeForm mode="create" />
    </template>
  </eplus-dialog>
</template>

<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import * as formChangeApi from '@/api/system/formChange'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import FormChangeForm from './FormChangeForm.vue'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const handleDialogFailure = () => {}

const eplusListRef = ref()
defineOptions({ name: 'FormChange' })

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'name',
      label: '表名称'
    },
    {
      component: <el-input></el-input>,
      name: 'description',
      label: '描述'
    }
  ],
  moreFields: []
}

const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await formChangeApi.getFormChangePage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await formChangeApi.deleteFormChange(id)
  },
  selection: false,
  columns: [
    {
      prop: 'id',
      label: '编号'
    },
    {
      prop: 'name',
      label: '表名'
    },
    {
      prop: 'description',
      label: '描述'
    },
    {
      prop: 'action',
      label: '操作',
      minWidth: '120px',
      fixed: 'right',
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div
              class="flex items-center"
              style="justify-content:center"
            >
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleUpdate(row.id)
                }}
                hasPermi="['system:form-change:update']"
              >
                设置
              </el-button>
              <el-button
                link
                type="danger"
                onClick={() => {
                  handleDelete(row.id)
                }}
                hasPermi="['system:form-change:delete']"
              >
                删除
              </el-button>
            </div>
          )
        }
      }
    }
  ]
}

// 删除按钮操作
const handleDelete = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleUpdate = (id: number) => {
  eplusDialogRef.value?.openEdit(id)
}

const handleCreate = () => {
  eplusDialogRef.value?.openCreate()
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
