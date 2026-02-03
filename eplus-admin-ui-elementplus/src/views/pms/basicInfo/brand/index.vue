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
        @click="handleCreate"
        v-hasPermi="['pms:brand:create']"
      >
        <Icon
          icon="ep:plus"
          class="mr-5px"
        />
        新增
      </el-button>
    </template>
  </eplus-table>

  <Dialog
    v-model="dialogVisible"
    :title="title"
    width="500"
  >
    <el-form
      ref="formRef"
      :model="formData"
      label-width="100"
    >
      <el-form-item
        label="品牌类型"
        prop="brandType"
        :rules="{ required: true, message: '请选择品牌类型' }"
      >
        <eplus-dict-select
          v-model="formData.brandType"
          :dictType="DICT_TYPE.BRAND_TYPE"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item
        label="品牌名称"
        prop="name"
        :rules="{ required: true, message: '请输入品牌名称' }"
      >
        <el-input
          v-model="formData.name"
          autocomplete="off"
        />
      </el-form-item>

      <el-form-item label="简码">
        <el-input
          v-model="formData.code"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item
        label="客户名称"
        prop="custCode"
        :rules="{ required: true, message: '请选择客户名称' }"
        v-if="formData.brandType === 2"
      >
        <eplus-input-search-select
          v-model="formData.custCode"
          :api="getCustSimpleList"
          :params="{ pageSize: 100, pageNo: 1 }"
          keyword="name"
          label="name"
          value="code"
          placeholder="请选择"
          class="w-100%"
          @change-emit="
            (...e) => {
              let item = e[1].find((item) => item.code === e[0])
              formData.custId = item.id
              formData.custName = item.name
            }
          "
        />
      </el-form-item>
      <el-form-item label="logo">
        <UploadZoomPic
          v-model="formData.logo"
          :limit="1"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="handleSave"
        >
          确认
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </template>
  </Dialog>
</template>

<script setup lang="tsx">
import { columnWidth, formatDateColumn, formatDictColumn } from '@/utils/table'
import { EplusTableSchema } from '@/types/eplus'
import { reactive, ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { getCustSimpleList } from '@/api/common'
import * as brandApi from '@/api/pms/brand'
import { isValidArray } from '@/utils/is'

const message = useMessage()

const eplusListRef = ref()
defineOptions({ name: 'SkuBrand' })

const eplusSearchSchema = {
  fields: [],
  moreFields: []
}
const eplusTableSchema: EplusTableSchema = {
  getListApi: async (ps) => {
    const res = await brandApi.getBrandPage(ps)
    return {
      list: res.list,
      total: res.total
    }
  },
  delListApi: async (id) => {
    await brandApi.deleteBrand(id)
  },
  columns: [
    {
      prop: 'brandType',
      label: '品牌类型',
      formatter: formatDictColumn(DICT_TYPE.BRAND_TYPE),
      minWidth: columnWidth.m
    },
    {
      prop: 'name',
      label: '品牌名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'code',
      label: '简码',
      minWidth: columnWidth.m
    },
    {
      prop: 'custName',
      label: '客户名称',
      minWidth: columnWidth.m
    },
    {
      prop: 'createTime',
      label: '创建时间',
      formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
      minWidth: columnWidth.l
    },
    {
      prop: 'action',
      label: '操作',
      fixed: 'right',
      minWidth: columnWidth.l,
      slots: {
        default: (data) => {
          const { row } = data
          return (
            <div class="flex items-center ">
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleDetele(row.id)
                }}
                hasPermi="['pms:brand:delete']"
              >
                删除
              </el-button>
              <el-button
                link
                type="primary"
                onClick={() => {
                  handleEdit(row)
                }}
                hasPermi="['pms:brand:update']"
              >
                编辑
              </el-button>
            </div>
          )
        }
      }
    }
  ]
}
const title = ref('')
const dialogVisible = ref(false)
const formRef = ref()
let formData = reactive({
  id: undefined,
  brandType: undefined,
  custCode: undefined,
  custId: undefined,
  custName: undefined,
  logo: undefined,
  name: undefined,
  code: undefined
})
// 删除按钮操作
const handleDetele = async (id: number) => {
  await eplusListRef.value.delList(id, false)
}

const handleEdit = (row?: any) => {
  title.value = '编辑品牌'
  formData.id = row.id
  formData.name = row.name
  formData.code = row.code
  formData.brandType = row.brandType
  formData.custCode = row.custCode
  formData.custId = row.custId
  formData.custName = row.custName
  formData.logo = row.logo?.fileUrl ? [row.logo] : undefined
  dialogVisible.value = true
}

const handleCancel = () => {
  dialogVisible.value = false
  formRef.value.resetFields()
}
const handleSave = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (formData?.id) {
        brandApi
          .updateBrand({
            ...formData,
            logo: isValidArray(formData?.logo) ? formData?.logo[0] : undefined
          })
          .then((res) => {
            message.success('修改成功')
            handleRefresh()
            handleCancel()
          })
      } else {
        brandApi
          .createBrand({
            ...formData,
            logo: isValidArray(formData?.logo) ? formData?.logo[0] : undefined
          })
          .then((res) => {
            message.success('保存成功')
            handleRefresh()
            handleCancel()
          })
      }
    }
  })
}
const handleCreate = () => {
  formData.id = undefined
  formData.name = undefined
  formData.code = undefined
  formData.brandType = undefined
  formData.custCode = undefined
  formData.custId = undefined
  formData.custName = undefined
  formData.logo = undefined
  title.value = '新增品牌'
  dialogVisible.value = true
}

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}
</script>
