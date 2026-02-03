<template>
  <el-dropdown
    @command="handleCommand"
    v-hasPermi="permissions"
    v-if="otherItemsCount > 0"
  >
    <span class="el-dropdown-link">
      更多
      <Icon :icon="'ep:arrow-down'" />
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <eplus-edit-dropdown-item
          v-if="editItem"
          :id="auditable?.id"
          :audit-status="auditable?.auditStatus"
          :permi="editItem.permi"
          :createUser="editItem.user || {}"
          @hide-item="handleItemHide"
        />
        <eplus-submit-dropdown-item
          v-if="submitItem"
          :id="auditable?.id"
          :audit-status="auditable?.auditStatus"
          :permi="submitItem.permi"
          :createUser="submitItem.user || {}"
          @hide-item="handleItemHide"
        />
        <eplus-delete-dropdown-item
          v-if="deleteItem"
          :id="auditable?.id"
          :audit-status="auditable?.auditStatus"
          :permi="deleteItem?.permi"
          :createUser="deleteItem?.user || {}"
          @hide-item="handleItemHide"
        />
        <div
          v-for="item in otherItems"
          :key="item.otherKey"
        >
          <eplus-other-dropdown-item
            :isShow="item.isShow"
            :permi="item.permi"
            :id="auditable?.id"
            :otherKey="item.otherKey"
            @hide-item="handleItemHide"
            :check-audit-status="item.checkAuditStatus"
            :audit-status="auditable?.auditStatus"
            :label="item.label"
            :createUser="item.user || {}"
          />
        </div>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import EplusEditDropdownItem from './EplusEditDropdownItem.vue'
import EplusSubmitDropdownItem from './EplusSubmitDropdownItem.vue'
import EplusDeleteDropdownItem from './EplusDeleteDropdownItem.vue'
import EplusOtherDropdownItem from './EplusOtherDropdownItem.vue'
import type { EplusAuditable, EplusButtonSchema } from '@/types/eplus'
import { defineProps } from 'vue'

interface OtherItem {
  isShow: boolean
  permi: string
  user: object
  label: string
  handler: (EplusAuditable) => void
  checkAuditStatus: Array<number>
  otherKey: string
}

// props定义
const props = defineProps<{
  otherItems: OtherItem[]
  editItem?: EplusButtonSchema
  submitItem?: EplusButtonSchema
  deleteItem?: EplusButtonSchema
  auditable?: EplusAuditable
  payItem?: EplusButtonSchema
}>()

const handleCommand = async (key) => {
  if (key === 'submit') {
    await props.submitItem?.handler(props.auditable)
    return
  }
  if (key === 'edit') {
    await props.editItem?.handler(props.auditable)
    return
  }
  if (key === 'delete') {
    await props.deleteItem?.handler(props.auditable)
    return
  }
  if (key === 'pay') {
    await props.payItem?.handler(props.auditable)
    return
  }
  const item = props.otherItems.find((item) => {
    return item.otherKey === key
  })
  if (item) {
    await item?.handler(props.auditable)
    return
  }
  return
}

// const itemCount = ref(props.otherItems.length)

const permissions: string[] = []
const otherItemsCount = ref(0)

//将otherItems的permi属性放入permissions数组
props.otherItems?.forEach((item) => {
  permissions.push(item?.permi)
  otherItemsCount.value++
})
if (props.editItem) {
  permissions.push(props.editItem?.permi)
  otherItemsCount.value++
}
if (props.submitItem) {
  permissions.push(props.submitItem?.permi)
  otherItemsCount.value++
}
if (props.deleteItem) {
  permissions.push(props.deleteItem?.permi)
  otherItemsCount.value++
}
if (props.payItem) {
  permissions.push(props.payItem?.permi)
  otherItemsCount.value++
}
const permissionsList: Array<{ id?: number; num: number }> = ref([])
permissionsList.value.push({
  id: props.auditable?.id,
  num: otherItemsCount.value
})
const isSecond = ref([])
const handleItemHide = (data) => {
  let str = `${data.id}${data.type}`
  if (!isSecond.value.includes(str)) {
    isSecond.value.push(str)
    otherItemsCount.value = --permissionsList.value.filter((item) => item.id === data.id)[0].num
  }
}
</script>

<style scoped lang="scss">
.el-dropdown-link {
  display: flex;
  color: var(--el-color-primary);
  cursor: pointer;
  align-items: center;
}
</style>
