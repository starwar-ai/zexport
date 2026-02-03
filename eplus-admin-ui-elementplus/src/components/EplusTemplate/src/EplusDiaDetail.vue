<template>
  <div
    class="pageBox flex flex-col"
    :style="props.scrollFlag ? 'height: 100%; overflow: hidden' : ''"
    ref="pageRef"
  >
    <div class="flex-1">
      <div
        v-if="props.pageTopTabsFlag"
        style="height: 54px"
      >
        <slot name="pageTopTabs"></slot>
      </div>
      <slot></slot>
      <div
        ref="pageTopRef"
        :style="
          topHeight > 0 && props.pageTopTabsFlag
            ? { height: `${topHeight - 54}px` }
            : topHeight > 0
              ? { height: `${topHeight}px` }
              : {}
        "
      >
        <el-scrollbar always>
          <slot name="pageTop"></slot>
        </el-scrollbar>
      </div>
      <div
        v-if="topHeight > 0"
        style="height: 54px"
      >
        <slot name="pageBottomTabs"></slot>
      </div>
      <div
        v-if="topHeight > 0"
        :style="{
          height: `${bottomHeight - 54}px` || '50%'
        }"
      >
        <slot name="pageBottom"></slot>
      </div>
      <el-button
        class="approveBtn"
        v-if="auditable?.processInstanceId && showProcessInstanceTaskList && scrollFlag"
        @click="dialogVisible = true"
      >
        查看审批记录
      </el-button>
      <ProcessInstanceTaskList
        v-if="auditable?.processInstanceId && showProcessInstanceTaskList && !scrollFlag"
        :loading="false"
        :tasks="tasks"
      />
    </div>
  </div>

  <Dialog
    v-model="dialogVisible"
    title="审批记录"
    width="800"
    append-to-body
    destroy-on-close
  >
    <ProcessInstanceTaskList
      :loading="false"
      :tasks="tasks"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </div>
    </template>
  </Dialog>

  <eplus-audit-form
    v-if="props.auditable"
    ref="auditEidtFormRef"
    @success="handleAuditCallback"
    :approve="approveApi"
    :reject="rejectApi"
  />
</template>

<script setup lang="tsx">
// import EplusAuditForm from '@/components/EplusAuditForm.vue'
import { defineExpose, defineProps, inject } from 'vue'
import { EplusAuditable } from '@/types/eplus'
import {
  EplusApproveButton,
  EplusCancelButton,
  EplusDelegateButton,
  EplusEditButton,
  EplusSubmitButton
} from '@/components/EplusButton'
import { JSX } from 'vue/jsx-runtime'
import * as TaskApi from '@/api/bpm/task'
import { useUserStore } from '@/store/modules/user'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import ProcessInstanceTaskList from '@/views/bpm/processInstance/detail/ProcessInstanceTaskList.vue'
import { ElMessageBox } from 'element-plus'

const message = useMessage()
const { t } = useI18n()
const auditUserIds = ref<number[]>([])
const tasks = ref<any[]>([]) // 任务列表
const taskId = ref('')
const loading = ref(true)
const userId = useUserStore().getUser.id
const auditEidtFormRef = ref<ComponentRef<typeof auditEidtFormRef>>()

defineOptions({ name: 'EplusDiaDetail' })

const props = withDefaults(
  defineProps<{
    submit?:
      | { permi: string; user?: string; handler: (auditable: EplusAuditable) => void | any }
      | boolean
    cancel?: {
      permi: string | string[]
      handler: (auditable: EplusAuditable) => void
      user?: string
    }
    edit?: { permi: string; handler: (auditable: EplusAuditable) => void; user?: string }
    approve?: { permi: string; handler: (auditable: EplusAuditable) => void }
    revertAudit?: { permi: string; handler: (auditable: EplusAuditable) => void }
    showProcessInstanceTaskList: boolean
    auditable: EplusAuditable
    outDialog: boolean // 不在dialog中
    approveApi?: any
    rejectApi?: any
    startUserId?: string
    scrollFlag?: boolean
    pageTopTabsFlag?: boolean
    mainRatio?: number
  }>(),
  {
    showProcessInstanceTaskList: false,
    outDialog: false,
    scrollFlag: false,
    pageTopTabsFlag: false,
    mainRatio: 5
  }
)

const dialogVisible = ref(false)

const { close } = props.outDialog
  ? { close: () => {} }
  : (inject('dialogEmits') as {
      close: () => void
    })
const { updateDialogActions, clearDialogActions } = props.outDialog
  ? { updateDialogActions: (...args: any[]) => {}, clearDialogActions: (val) => {} }
  : (inject('dialogActions') as {
      dialogActions: JSX.Element[]
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: (val) => void
    })
const emit = defineEmits(['update', 'getTasks'])

const handleDelegate = async () => {
  auditEidtFormRef.value.open({
    id: tasks.value[0].id,
    createName: tasks.value[0]?.processInstance?.startUserNickname,
    title: '转派',
    delegateFlag: true
  })
}
const handleOpen = async () => {
  let res = await props.approve?.handler()
  if (res || res == undefined) {
    auditEidtFormRef.value.open({
      id: taskId.value,
      createName: tasks.value[0]?.processInstance?.startUserNickname
    })
  }
}
//反审核
const handleRevertAudit = async () => {
  ElMessageBox.confirm('反审核之后将该单据的状态变为待提交', '反审核', {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel')
  })
    .then(() => {
      props.revertAudit
        ?.handler(props.auditable)
        .then(() => {
          message.success('反审核成功')
          clearDialogActions('revertAudit')
        })
        .catch(() => {
          console.info('操作取消')
        })
    })
    .catch(() => console.info('操作取消'))
}
const handleCancel = async () => {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消流程', {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/, // 判断非空，且非空格
    inputErrorMessage: '取消原因不能为空'
  })

  const data = {
    id: props.auditable?.processInstanceId!,
    reason: value
  }
  await ProcessInstanceApi.cancelProcessInstance(data)
  message.success('取消成功')
  handleAuditCallback()
}
const updateActions = () => {
  updateDialogActions(
    <EplusEditButton
      isShow={props.edit ? true : false}
      permi={props.edit?.permi}
      onClick={props.edit?.handler}
      auditable={props.auditable}
      createUser={props.edit?.user || ''}
    />,
    <EplusCancelButton
      isShow={props.cancel ? true : false}
      permi={props.cancel?.permi}
      auditable={props.auditable}
      startUserId={tasks.value[0]?.processInstance?.startUserId}
      onClick={handleCancel}
      key="cancel"
    />,
    <EplusApproveButton
      isShow={props.approve ? true : false}
      permi={props.approve?.permi}
      onClick={handleOpen}
      auditable={props.auditable}
      auditUserIds={auditUserIds.value}
      key="approve"
    />,
    <EplusDelegateButton
      isShow={props.approve ? true : false}
      onClick={handleDelegate}
      auditable={props.auditable}
      key="delegate"
    />,
    // <EplusRevertAuditButton
    //   isShow={props.revertAudit ? true : false}
    //   permi={props.revertAudit?.permi}
    //   onClick={handleRevertAudit}
    //   auditable={props.auditable}
    //   key="revertAudit"
    // />,
    <EplusSubmitButton
      isShow={props.submit ? true : false}
      permi={props.submit?.permi}
      onClick={props.submit?.handler}
      auditable={props.auditable}
      key="submit"
      createUser={props.submit?.user || ''}
    />
  )
}

const handleAuditCallback = async () => {
  clearDialogActions('approve', 'cancel')
  await getTaskList(props.auditable?.processInstanceId)
  // emit('update')
  close()
}
/** 加载任务列表 */
const getTaskList = async (processInstanceId: string) => {
  try {
    // 获得未取消的任务
    loading.value = true
    const data = await TaskApi.getTaskListByProcessInstanceId(processInstanceId)
    tasks.value = data
    // 1.1 移除已取消的审批
    // data.forEach((task) => {
    //   if (task.result !== 4) {
    //     tasks.value.push(task)
    //   }
    // })
    // 1.2 排序，将未完成的排在前面，已完成的排在后面；
    // tasks.value.sort((a, b) => {
    //   // 有已完成的情况，按照完成时间倒序
    //   if (a.endTime && b.endTime) {
    //     return b.endTime - a.endTime
    //   } else if (a.endTime) {
    //     return 1
    //   } else if (b.endTime) {
    //     return -1
    //     // 都是未完成，按照创建时间倒序
    //   } else {
    //     return b.createTime - a.createTime
    //   }
    // })
    emit('getTasks', tasks.value[0])
    auditUserIds.value = tasks.value
      .filter((it) => {
        return it.result == 1
      })
      .map((it) => it.assigneeUser.id)
    var currTasks = tasks.value.filter((it) => {
      return it.result == 1 && it.assigneeUser.id == userId
    })
    if (currTasks && currTasks.length > 0) {
      taskId.value = currTasks[0].id
    }
  } finally {
    loading.value = false
  }
}

const pageRef = ref()
const pageTopRef = ref()
const topHeight = ref(0)
const bottomHeight = ref<number>(0)
const getPageHeight = () => {
  return pageRef.value?.clientHeight || 0
}

const setPageHeight = () => {
  nextTick(() => {
    setTimeout(() => {
      const pageTopHeight = pageTopRef.value?.clientHeight
      const pageHeight = pageRef.value?.clientHeight
      if (pageTopHeight >= Math.ceil((pageHeight / 10) * props.mainRatio)) {
        topHeight.value = Math.ceil((pageHeight / 10) * props.mainRatio)
      } else {
        topHeight.value = pageTopHeight
      }
      bottomHeight.value = pageHeight - topHeight.value
    })
  })
}

defineExpose({ getPageHeight, bottomHeight, close })

onMounted(async () => {
  if (props.auditable?.processInstanceId && props.showProcessInstanceTaskList) {
    await getTaskList(props.auditable?.processInstanceId)
  }

  setPageHeight()
  setTimeout(() => {
    updateActions()
  }, 100)
  // nextTick(() => {
  //   console.dir(pageTopRef.value?.clientHeight)
  // })
})
</script>

<style scoped lang="scss">
.pageBox {
  position: relative;
}

.approveBtn {
  position: absolute;
  right: 15px;
  top: 0;
}
</style>
