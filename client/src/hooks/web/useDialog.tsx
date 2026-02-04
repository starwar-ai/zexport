import { Dialog, DialogInitOptions } from '@/components/Dialog'
import { createVNode, mergeProps, render } from 'vue'
import { useForm } from './useForm'
import Form from '@/components/Form'
import { ElButton } from 'element-plus'
import { Icon } from '@/components/Icon'

export const useDialog = (params?: DialogInitOptions) => {
  let { props = {}, slots = {} } = params || {}

  const getParent = () => {
    const vr = document.querySelector('section.v-app-view')
    if (vr) {
      return vr
    }
    return document.body
  }
  //增加onClose事件监听
  const mprops = reactive({
    ...props,
    width: props.width || getParent().clientWidth - 40,
    maxHeight: props.maxHeight || getParent().clientHeight - 185,
    onClose: () => {
      methods.close()
    }
  })
  const mslots = reactive(slots)
  /*
  watch(
    mprops,
    (_n, __o) => {
      // methods.update()
    },
    { deep: true }
  )
  watch(
    mslots,
    (_n, __o) => {
      //   methods.update()
    },
    { deep: true }
  )
*/
  const container = document.createElement('div')

  let lastVnode: VNode | undefined

  const methods: {
    open(): void
    update(): void
    close(): void
    getProps(): Recordable
    getSlots(): Recordable
    setProps: (props: Recordable) => void
    setSlots: (slots: Recordable) => void
  } = {
    update: () => {
      lastVnode = createVNode(lastVnode || Dialog, mprops, mslots)
      render(lastVnode, container)
    },

    open: () => {
      getParent().appendChild(container)
      mprops.modelValue = true
      methods.update()
    },
    close: () => {
      render(null, container)
      getParent().removeChild(container)
    },
    getProps: () => {
      return mprops
    },
    getSlots: () => {
      return mslots
    },
    setProps: (n: Recordable) => {
      props = mergeProps(n, props)
      Object.assign(mprops, props)
    },
    setSlots: (n: Recordable) => {
      slots = mergeProps(n, slots)
      Object.assign(mslots, slots)
    }
  }
  const res: Recordable = {
    methods
  }
  if (params?.form) {
    const mform = reactive({ ...params?.form })

    const uform = useForm(mform)
    /*
    watch(
      mform.props,
      (_n, __o) => {
        uform.methods.setProps(mform.props)
      },
      { deep: true }
    )
    watch(
      mform.schema,
      (_n, __o) => {
        uform.methods.setSchema(mform.schema)
      },
      { deep: true }
    )
    watch(
      mform.values,
      (_n, __o) => {
        uform.methods.setValues(mform.values)
      },
      { deep: true }
    )
    */
    res.formMethods = uform.methods

    mslots.default = () => {
      return (
        <>
          <Form
            ref={uform.register}
            {...mform.props}
            model={mform.model}
            schema={mform.schema}
          />
        </>
      )
    }
    /**重置Form */
    res.resetForm = () => {
      const el = unref(uform.elFormRef)
      el?.resetFields?.()
      //mform.model = {...params.model}
    }
    if (params.onSubmit) {
      params._onSubmit = params.onSubmit
      params.onSubmit = async () => {
        const el = unref(uform.elFormRef)
        if (await el?.validate()) {
          await params._onSubmit(await uform.methods.getFormData())
          //提交h后关闭窗口
          methods.close()
        }
      }
    }
  }
  if (params?.btns || params?.onSubmit || params?.showReset || params?.showCancel) {
    const bs: Recordable[] = Array.isArray(params.btns)
      ? params.btns
      : params.btns
        ? [params.btns]
        : []
    if (params.showCancel) {
      bs.unshift({ icon: 'ep:close', onClick: methods.close, label: params.cancelLabel || '取消' })
    }
    if (params.showReset) {
      bs.unshift({ icon: 'ep:refresh', onClick: res.resetForm, label: params.resetLabel || '重置' })
    }
    if (params.onSubmit) {
      bs.unshift({
        icon: 'ep:finished',
        onClick: params.onSubmit,
        label: params.submitLabel || '提交'
      })
    }
    mslots.footer = () => {
      return (
        <>
          {bs.map((b) => {
            if (params.noIcon) b.icon = null
            //取消icon，直接使用Icon组件
            const bps = { ...b, icon: null }
            return (
              <ElButton {...bps}>
                {b.icon ? <Icon icon={b.icon} /> : ''}
                {(b.icon ? ' ' : '') + b.label}
              </ElButton>
            )
          })}
        </>
      )
    }
  }

  //先初始化一下不加载到body
  methods.update()

  return res
}
