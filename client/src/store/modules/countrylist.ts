import { defineStore } from 'pinia'
import { store } from '@/store'

export interface countryListState {
  countryList: string[]
  isSetCountryList
}

export const useCountryStore = defineStore('country', {
  state: (): countryListState => {
    return {
      countryList: [],
      isSetCountryList: false
    }
  },
  actions: {
    setCountryList(data) {
      this.countryList = data
      this.isSetCountryList = true
    }
  }
})

export const useCountryStoreWithOut = () => {
  return useCountryStore(store)
}
