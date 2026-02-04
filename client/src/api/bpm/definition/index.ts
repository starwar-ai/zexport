import request from '@/config/axios'

export const getProcessDefinitionBpmnXML = async (id: number) => {
  return await request.get({
    url: '/bpm/process-definition/get-bpmn-xml?id=' + id
  })
}
export const getProcessDefinitionBpmnXMLTest = async (key: string) => {
  return await request.get({
    url: '/bpm/process-definition/get-bpmn-xml-by-key?key=' + key
  })
}
export const getProcessDefinitionPage = async (params) => {
  return await request.get({
    url: '/bpm/process-definition/page',
    params
  })
}

export const getProcessDefinitionList = async (params) => {
  return await request.get({
    url: '/bpm/process-definition/list',
    params
  })
}
