import { EplusDetailExpose } from '@/components/EplusTemplate'

export const useEplusDetail = () => {
  const myRef = ref<EplusDetailExpose>()

  const register = (ref: EplusDetailExpose) => {
    console.info('register')
    myRef.value = ref
  }

  const getValue = async () => {
    await nextTick()
    const eplusDetail = unref(myRef)
    if (!eplusDetail) {
      console.error(
        'The Eplus Detail is not registered. Please use the register method to register'
      )
    }
    return eplusDetail
  }
  const methods = {
    getActions: async () => {
      console.log('zkzkzk')
      const eplusDetail = await getValue()
      console.log(eplusDetail)
      return eplusDetail?.getActions()
    }
  }
  return {
    register,
    methods
  }
}
