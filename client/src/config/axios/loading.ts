import { ElLoading } from 'element-plus'

const loading = {
  count: 0,
  start() {
    this.count += 1 // 只要有请求 就加一
    // if (!window.DMC_MBD_LOADING) {
    // 第一次，进入
    // setTimeout(() => {
    // 没有开启loading，并且请求大于1
    if (!window.DMC_MBD_LOADING && this.count > 0) {
      // 设置已经开启loading
      this.checkLoading()
    }
    // }, 100)
    // }
  },
  close() {
    this.count -= 1
    setTimeout(() => {
      // 没有开启loading，并且请求大于1
      if (this.count <= 0) {
        this.done()
      }
    }, 100)
  },
  // 全部结束，让loading消失
  done() {
    this.count = 0
    if (window.DMC_MBD_LOADING) {
      window.DMC_MBD_LOADING.close()
      window.DMC_MBD_LOADING = null
    }
  },
  // 没有loading 就创建一个
  checkLoading() {
    // 获取 loading dom
    const el = document.querySelector('.dmc-loading')
    // 没有这个loading
    if (!window.DMC_MBD_LOADING && !el) {
      // 创建 loading
      window.DMC_MBD_LOADING = ElLoading.service({
        lock: true,
        text: '正在加载...',
        background: 'rgba(0, 0, 0, 0.7)',
        customClass: 'dmc-loading'
      })
    }
  }
}

export default loading
