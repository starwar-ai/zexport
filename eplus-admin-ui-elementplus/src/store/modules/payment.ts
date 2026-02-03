import { defineStore } from 'pinia'
import { store } from '@/store'

export interface paymentListState {
  paymentList: { list }
  isSetPaymentList
}

export const usePaymentStore = defineStore('payment', {
  state: (): paymentListState => {
    return {
      paymentList: { list: [] },
      isSetPaymentList: false
    }
  },
  actions: {
    setPaymentList(data) {
      this.paymentList = data
      this.isSetPaymentList = true
    }
  }
})

export const usePaymentStoreWithOut = () => {
  return usePaymentStore(store)
}
