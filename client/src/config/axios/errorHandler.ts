import { AxiosError, AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox, ElNotification, MessageBoxData } from 'element-plus'
import { t } from '@/hooks/web/useI18n'
import { HTTP_STATUS, IGNORE_ERROR_MSGS } from './constants'
import errorCode from './errorCode'

/**
 * 错误处理器类
 */
export class ErrorHandler {
  /**
   * 处理响应错误
   */
  handleResponseError(response: AxiosResponse): Promise<never> {
    const { data } = response
    const code = data.code ?? HTTP_STATUS.SERVER_ERROR
    const message = data.msg || errorCode[code] || errorCode.default

    // 检查是否为忽略的错误信息
    if (IGNORE_ERROR_MSGS.includes(message)) {
      return Promise.reject(message)
    }

    // 根据不同状态码处理
    switch (code) {
      case HTTP_STATUS.UNAUTHORIZED:
        throw new Error('UNAUTHORIZED')
      case HTTP_STATUS.SERVER_ERROR:
        this.showServerError()
        break
      case HTTP_STATUS.LICENSE_ERROR:
        this.showLicenseError()
        break
      default:
        if (message !== '无效的刷新令牌') {
          this.showNotificationError(message)
        } else {
          console.log(message)
        }
        break
    }

    return Promise.reject(new Error(message))
  }

  /**
   * 处理请求错误
   */
  handleRequestError(error: AxiosError): Promise<never> {
    let message = error.message

    if (message === 'Network Error') {
      message = t('sys.api.errorMessage')
    } else if (message.includes('timeout')) {
      message = t('sys.api.apiTimeoutMessage')
    } else if (message.includes('Request failed with status code')) {
      message = t('sys.api.apiRequestFailed') + message.slice(-3)
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }

  /**
   * 处理二进制响应
   */
  async handleBlobResponse(response: AxiosResponse): Promise<any> {
    const { data } = response

    if (
      response.request.responseType === 'blob' ||
      response.request.responseType === 'arraybuffer'
    ) {
      if (response.headers['content-type']?.includes('json')) {
        // 处理 JSON 格式的错误响应
        const reader = new FileReader()
        return new Promise((resolve, reject) => {
          reader.onload = function (e: ProgressEvent<FileReader>) {
            try {
              const text = e.target?.result as string
              const json = JSON.parse(text)
              const msg = json.msg || errorCode[json.code] || errorCode.default

              if (json.code !== HTTP_STATUS.SUCCESS) {
                ElMessage.error(msg)
                reject()
              } else {
                resolve(json)
              }
            } catch (error) {
              reject(error)
            }
          }
          reader.readAsText(data)
        })
      } else {
        // 返回二进制数据
        return data
      }
    }

    return data
  }

  /**
   * 显示服务器错误
   */
  private showServerError(): void {
    ElMessage.error(t('sys.api.errMsg500'))
  }

  /**
   * 显示许可证错误
   */
  private showLicenseError(): void {
    ElMessage.error({
      offset: 300,
      dangerouslyUseHTMLString: true,
      message:
        '<div>' +
        t('sys.api.errMsg901') +
        '</div>' +
        '<div> &nbsp; </div>' +
        '<div>参考 https://doc.iocoder.cn/ 教程</div>' +
        '<div> &nbsp; </div>' +
        '<div>5 分钟搭建本地环境</div>'
    })
  }

  /**
   * 显示通知错误
   */
  private showNotificationError(message: string): void {
    ElNotification.error({
      title: message,
      offset: 20
    })
  }

  /**
   * 显示重新登录确认框
   */
  showReloginConfirm(): Promise<MessageBoxData> {
    return ElMessageBox.confirm(t('sys.api.timeoutMessage'), t('common.confirmTitle'), {
      showCancelButton: false,
      closeOnClickModal: false,
      showClose: false,
      confirmButtonText: t('login.relogin'),
      type: 'warning'
    })
  }
}



