import { EplusAuditable } from './../../../types/eplus.d'
import request from '@/config/axios'

export interface DictSubjectVO extends EplusAuditable {
  id: number
  subjectId: number
  subjectName: string
  subjectDescription: string
  systemDictTypeId: number
  systemDictTypeName: string
  systemDictDataId: number
  systemDictDataLabel: string
  systemDictDataValue: number
}

//新增
export const createDictSubject = async (data) => {
  return await request.post({ url: `/oa/dict-subject/create`, data })
}

// 查询类别配置分页
export const getDictSubjectPage = async (params) => {
  return await request.get({ url: `/oa/dict-subject/page`, params })
}

// 修改类别配置
export const updateDictSubject = async (data: DictSubjectVO) => {
  return await request.put({ url: `/oa/dict-subject/update`, data })
}

// 删除类别配置
export const deleteDictSubject = async (id: number) => {
  return await request.delete({ url: `/oa/dict-subject/delete?id=` + id })
}

// 导出类别配置 Excel
export const exportDictSubject = async (params) => {
  return await request.download({ url: `/oa/dict-subject/export-excel`, params })
}

// 获取科目树
export const getSubjectTree = async () => {
  return await request.get({ url: `/oa/subject/get-simple-list` })
}

// 查询类别配置
export const getDictSubject = async (id: number) => {
  return await request.get({ url: `/oa/dict-subject/detail?id=` + id })
}

// 查询类别配置
export const getFeeList = async () => {
  return await request.get({ url: `/oa/dict-subject/get-fee-list` })
}
