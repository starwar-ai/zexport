import { useRoute } from 'vue-router'
import type { EplusSearchFieldSchema, EplusSearchFieldLayout } from '../types'

const STORAGE_VERSION = '1.0'

export const useFieldLayout = () => {
  const route = useRoute()

  const getStorageKey = (): string => {
    return `eplus-search-layout-${route.path}`
  }

  const getFieldId = (field: EplusSearchFieldSchema, index: number): string => {
    return field.name || `field-${index}`
  }

  const loadLayout = (): EplusSearchFieldLayout | null => {
    try {
      const storageKey = getStorageKey()
      const saved = localStorage.getItem(storageKey)
      if (!saved) return null

      const layout: EplusSearchFieldLayout = JSON.parse(saved)

      // Version check for future migrations
      if (layout.version !== STORAGE_VERSION) {
        console.warn('Layout version mismatch, ignoring saved layout')
        return null
      }

      return layout
    } catch (error) {
      console.error('Failed to load field layout:', error)
      return null
    }
  }

  const saveLayout = (layout: Omit<EplusSearchFieldLayout, 'pageKey' | 'version' | 'timestamp'>): void => {
    try {
      const storageKey = getStorageKey()
      const fullLayout: EplusSearchFieldLayout = {
        ...layout,
        pageKey: route.path,
        version: STORAGE_VERSION,
        timestamp: Date.now()
      }
      localStorage.setItem(storageKey, JSON.stringify(fullLayout))
    } catch (error) {
      console.error('Failed to save field layout:', error)
      // Handle quota exceeded
      if (error.name === 'QuotaExceededError') {
        console.error('localStorage quota exceeded')
      }
    }
  }

  const clearLayout = (): void => {
    try {
      const storageKey = getStorageKey()
      localStorage.removeItem(storageKey)
    } catch (error) {
      console.error('Failed to clear field layout:', error)
    }
  }

  return {
    getStorageKey,
    getFieldId,
    loadLayout,
    saveLayout,
    clearLayout
  }
}
