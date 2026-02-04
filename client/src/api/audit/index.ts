import request from '@/config/axios'

export type Task = {
  id: string
  name: string
}

export type ProcessInstanceVO = {
  pageNo: number
  pageSize: number
  id: number
  name: string
  stageType: []
  auditStage: []
  auditStatus: []
  startUserId: number
  startTime: string
  auditName: string
}

export type ProcessInstanceCancelVO = {
  id: string
  processInstanceId: string
  reason: string
}

export const cancelProcessInstance = async (data: ProcessInstanceCancelVO) => {
  return await request.delete({ url: '/bpm/process-instance/cancel', data: data })
}

export const getProcessInstance = async (id: number) => {
  return await request.get({ url: '/bpm/process-instance/get?id=' + id })
}
