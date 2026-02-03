import type { App } from 'vue'
import { Icon } from './Icon'

export const setupGlobalComponents = (app: App<Element>): void => {
  app.component('Icon', Icon)
}
