<script lang="tsx">
import {
  ComponentSize,
  ElButton,
  ElCard,
  ElEmpty,
  ElImage,
  ElPagination,
  ElTable,
  ElTableColumn,
  ElTooltipProps
} from 'element-plus'
import {
  computed,
  CSSProperties,
  defineComponent,
  onMounted,
  PropType,
  ref,
  unref,
  watch
} from 'vue'
import { propTypes } from '@/utils/propTypes'
import { setIndex } from './helper'
import type { Pagination, TableColumn, TableProps, TableSetProps } from './types'
import { get, set } from 'lodash-es'
import { getSlot } from '@/utils/tsxHelper'
import TableActions from './components/TableActions.vue'
import { useDesign } from '@/hooks/web/useDesign'
import { useRenderSelect } from '@/components/Form/src/components/useRenderSelect'
import { useRenderRadio } from '@/components/Form/src/components/useRenderRadio'
import { useRenderCheckbox } from '@/components/Form/src/components/useRenderCheckbox'
import { FormSchema } from '@/types/form'
import { setItemComponentSlots } from '@/components/Form/src/helper'
import AsyncValidator from 'async-validator'
import { componentMap } from '@/components/Form/src/componentMap'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'

export default defineComponent({
  name: 'Table',
  props: {
    pageSize: propTypes.number.def(10),
    currentPage: propTypes.number.def(1),
    // 是否展示表格的工具栏
    showAction: propTypes.bool.def(false),
    // 是否所有的超出隐藏，优先级低于schema中的showOverflowTooltip,
    showOverflowTooltip: propTypes.bool.def(true),
    // 表头
    columns: {
      type: Array as PropType<TableColumn[]>,
      default: () => []
    },
    // 是否展示分页
    pagination: {
      type: Object as PropType<Pagination>,
      default: (): Pagination | undefined => undefined
    },
    // 仅对 type=selection 的列有效，类型为 Boolean，为 true 则会在数据更新之后保留之前选中的数据（需指定 row-key）
    reserveSelection: propTypes.bool.def(false),
    // 加载状态
    loading: propTypes.bool.def(false),
    // 是否叠加索引
    reserveIndex: propTypes.bool.def(false),
    // 对齐方式
    align: propTypes.string
      .validate((v: string) => ['left', 'center', 'right'].includes(v))
      .def('left'),
    // 表头对齐方式
    headerAlign: propTypes.string
      .validate((v: string) => ['left', 'center', 'right'].includes(v))
      .def('left'),
    data: {
      type: Array as PropType<Recordable[]>,
      default: () => []
    },
    //是否可编辑
    editable: propTypes.bool.def(false),
    // 图片自动预览字段数组
    imagePreview: {
      type: Array as PropType<string[]>,
      default: () => []
    },
    // 视频自动预览字段数组
    videoPreview: {
      type: Array as PropType<string[]>,
      default: () => []
    },
    height: propTypes.oneOfType([Number, String]),
    maxHeight: propTypes.oneOfType([Number, String]),
    stripe: propTypes.bool.def(false),
    border: propTypes.bool.def(true),
    size: {
      type: String as PropType<ComponentSize>,
      validator: (v: ComponentSize) => ['default', 'small', 'large'].includes(v)
    },
    fit: propTypes.bool.def(true),
    showHeader: propTypes.bool.def(true),
    highlightCurrentRow: propTypes.bool.def(false),
    currentRowKey: propTypes.oneOfType([Number, String]),
    // row-class-name, 类型为 (row: Recordable, rowIndex: number) => string | string
    rowClassName: {
      type: [Function, String] as PropType<(row: Recordable, rowIndex: number) => string | string>,
      default: ''
    },
    rowStyle: {
      type: [Function, Object] as PropType<
        (row: Recordable, rowIndex: number) => Recordable | CSSProperties
      >,
      default: () => undefined
    },
    cellClassName: {
      type: [Function, String] as PropType<
        (row: Recordable, column: any, rowIndex: number) => string | string
      >,
      default: ''
    },
    cellStyle: {
      type: [Function, Object] as PropType<
        (row: Recordable, column: any, rowIndex: number) => Recordable | CSSProperties
      >,
      default: () => undefined
    },
    headerRowClassName: {
      type: [Function, String] as PropType<(row: Recordable, rowIndex: number) => string | string>,
      default: ''
    },
    headerRowStyle: {
      type: [Function, Object] as PropType<
        (row: Recordable, rowIndex: number) => Recordable | CSSProperties
      >,
      default: () => undefined
    },
    headerCellClassName: {
      type: [Function, String] as PropType<
        (row: Recordable, column: any, rowIndex: number) => string | string
      >,
      default: ''
    },
    headerCellStyle: {
      type: [Function, Object] as PropType<
        (row: Recordable, column: any, rowIndex: number) => Recordable | CSSProperties
      >,
      default: () => undefined
    },
    rowKey: propTypes.string.def('id'),
    emptyText: propTypes.string.def('暂无数据'),
    defaultExpandAll: propTypes.bool.def(false),
    expandRowKeys: {
      type: Array as PropType<string[]>,
      default: () => []
    },
    defaultSort: {
      type: Object as PropType<{ prop: string; order: string }>,
      default: () => ({})
    },
    tooltipEffect: {
      type: String as PropType<'dark' | 'light'>,
      default: 'dark'
    },
    tooltipOptions: {
      type: Object as PropType<
        Pick<
          ElTooltipProps,
          | 'effect'
          | 'enterable'
          | 'hideAfter'
          | 'offset'
          | 'placement'
          | 'popperClass'
          | 'popperOptions'
          | 'showAfter'
          | 'showArrow'
        >
      >,
      default: () => ({
        enterable: true,
        placement: 'top',
        showArrow: true,
        hideAfter: 200,
        popperOptions: { strategy: 'fixed' }
      })
    },
    showSummary: propTypes.bool.def(false),
    sumText: propTypes.string.def('Sum'),
    summaryMethod: {
      type: Function as PropType<(param: { columns: any[]; data: any[] }) => any[]>,
      default: () => undefined
    },
    spanMethod: {
      type: Function as PropType<
        (param: { row: any; column: any; rowIndex: number; columnIndex: number }) => any[]
      >,
      default: () => undefined
    },
    selectOnIndeterminate: propTypes.bool.def(true),
    indent: propTypes.number.def(16),
    lazy: propTypes.bool.def(false),
    load: {
      type: Function as PropType<(row: Recordable, treeNode: any, resolve: Function) => void>,
      default: () => undefined
    },
    treeProps: {
      type: Object as PropType<{ hasChildren?: string; children?: string; label?: string }>,
      default: () => ({ hasChildren: 'hasChildren', children: 'children', label: 'label' })
    },
    tableLayout: {
      type: String as PropType<'auto' | 'fixed'>,
      default: 'fixed'
    },
    scrollbarAlwaysOn: propTypes.bool.def(false),
    flexible: propTypes.bool.def(false),
    // 自定义内容
    customContent: propTypes.bool.def(false),
    cardBodyStyle: {
      type: Object as PropType<CSSProperties>,
      default: () => ({})
    },
    cardBodyClass: {
      type: String as PropType<string>,
      default: ''
    },
    cardWrapStyle: {
      type: Object as PropType<CSSProperties>,
      default: () => ({})
    },
    cardWrapClass: {
      type: String as PropType<string>,
      default: ''
    }
  },
  emits: ['update:pageSize', 'update:currentPage', 'register', 'refresh'],
  setup(props, { attrs, emit, slots, expose }) {
    const elTableRef = ref<ComponentRef<typeof ElTable>>()

    const { getPrefixCls } = useDesign()
    const prefixCls = getPrefixCls('table')

    const { formSchema } = attrs

    // 注册
    onMounted(() => {
      const tableRef = unref(elTableRef)
      emit('register', tableRef?.$parent, elTableRef.value)
    })

    const pageSizeRef = ref(props.pageSize)

    const currentPageRef = ref(props.currentPage)

    // useTable传入的props
    const outsideProps = ref<TableProps>({})

    const mergeProps = ref<TableProps>({})

    const getProps = computed(() => {
      const propsObj = { ...props }
      Object.assign(propsObj, unref(mergeProps))
      return propsObj
    })

    const setProps = (props: TableProps = {}) => {
      mergeProps.value = Object.assign(unref(mergeProps), props)
      outsideProps.value = { ...props } as any
    }

    const setColumn = (columnProps: TableSetProps[], columnsChildren?: TableColumn[]) => {
      const { columns } = unref(getProps)
      for (const v of columnsChildren || columns) {
        for (const item of columnProps) {
          if (v.field === item.field) {
            set(v, item.path, item.value)
          } else if (v.children?.length) {
            setColumn(columnProps, v.children)
          }
        }
      }
    }

    const addColumn = (column: TableColumn, index?: number) => {
      const { columns } = unref(getProps)
      if (index !== void 0) {
        columns.splice(index, 0, column)
      } else {
        columns.push(column)
      }
    }

    const delColumn = (field: string) => {
      const { columns } = unref(getProps)
      const index = columns.findIndex((item) => item.field === field)
      if (index > -1) {
        columns.splice(index, 1)
      }
    }

    const refresh = () => {
      emit('refresh')
    }

    const changSize = (size: ComponentSize) => {
      setProps({ size })
    }

    const confirmSetColumn = (columns: TableColumn[]) => {
      setProps({ columns })
    }

    const errors = ref([] as Recordable[])

    if (props.editable) {
      watch(
        () => {
          return props.data
        },
        () => {
          errors.value = props.data.map(() => {
            return {}
          })
        },
        {
          immediate: true,
          deep: true
        }
      )
    }
    // 校验
    const validate = (scope?: Recordable, field?: string, callback?: Function) => {
      if (!Array.isArray(formSchema)) return
      const fs = field
        ? formSchema.filter((a) => {
            return a.field == field
          })
        : formSchema
      const descriptor = {}
      const vs = {}

      const doValidate = (idx) => {
        let errs = errors.value[idx] || {}
        fs.forEach((f) => {
          if (f.formItemProps?.rules) {
            descriptor[f.field] = f.formItemProps.rules?.map((r) => {
              let _r = { ...r }
              delete _r.tragger
              return _r
            })
            vs[f.field] = props.data[idx][f.field]
            delete errs[f.field]
          }
        })
        const validator = new AsyncValidator(descriptor)
        return new Promise((res, req) => {
          try {
            validator.validate(vs, {}, (es) => {
              if (es) {
                es.forEach((e) => {
                  errs[e.field] = e.message
                })
              }
              res(es)
            })
          } catch (ex) {
            req()
          }
        })
      }
      if (scope)
        return doValidate(scope.$index).then((t) => {
          callback?.(t)
          return t
        })
      return Promise.all(
        props.data.map((_d, i) => {
          return doValidate(i)
        })
      ).then((t) => {
        let r = Array.isArray(t)
          ? t.filter((a) => {
              return a
            })
          : t
        callback?.(r.length)
        return r.length
      })
    }

    const addRow = (prop?: Recordable) => {
      const nr = { __isnew__: 1, defaultFlag: unref(getProps).data.length === 0 ? 1 : 0 }
      if (prop) Object.assign(nr, prop)
      unref(getProps).data.push(nr)
    }

    const saveRow = (scope: Recordable) => {
      validate(scope, undefined, (es) => {
        if (!es) delete scope.row.__isnew__
      })
    }

    const saveBtn = (scope: Recordable) => {
      return scope.row.__isnew__ ? (
        <ElButton
          link
          type="danger"
          onClick={() => {
            saveRow(scope)
          }}
        >
          保存
        </ElButton>
      ) : (
        []
      )
    }

    expose({
      setProps,
      setColumn,
      delColumn,
      addColumn,
      validate,
      addRow,
      saveRow,
      saveBtn,
      elTableRef
    })

    const pagination = computed(() => {
      return Object.assign(
        {
          small: false,
          background: false,
          pagerCount: 7,
          layout: 'sizes, prev, pager, next, jumper, ->, total',
          pageSizes: [10, 20, 30, 40, 50, 100],
          disabled: false,
          hideOnSinglePage: false,
          total: 10
        },
        unref(getProps).pagination
      )
    })

    watch(
      () => unref(getProps).pageSize,
      (val: number) => {
        pageSizeRef.value = val
      }
    )

    watch(
      () => unref(getProps).currentPage,
      (val: number) => {
        currentPageRef.value = val
      }
    )

    watch(
      () => pageSizeRef.value,
      (val: number) => {
        emit('update:pageSize', val)
      }
    )

    watch(
      () => currentPageRef.value,
      (val: number) => {
        emit('update:currentPage', val)
      }
    )

    const getBindValue = computed(() => {
      const bindValue: Recordable = { ...attrs, ...unref(getProps) }
      delete bindValue.columns
      delete bindValue.data
      return bindValue
    })

    const renderTreeTableColumn = (columnsChildren: TableColumn[]) => {
      const { align, headerAlign, showOverflowTooltip, imagePreview, videoPreview } =
        unref(getProps)
      return columnsChildren.map((v) => {
        if (v.hidden) return null
        const props = { ...v } as any
        if (props.children) delete props.children
        const children = v.children
        const slots = {
          default: (...args: any[]) => {
            const data = args[0]
            let isPreview = false
            isPreview =
              imagePreview.some((item) => (item as string) === v.field) ||
              videoPreview.some((item) => (item as string) === v.field)

            return children && children.length
              ? renderTreeTableColumn(children)
              : props?.slots?.default
                ? props.slots.default(...args)
                : v?.formatter
                  ? v?.formatter?.(data.row, data.column, get(data.row, v.field), data.$index)
                  : isPreview
                    ? renderPreview(get(data.row, v.field), v.field)
                    : get(data.row, v.field)
          }
        }
        if (props?.slots?.header) {
          slots['header'] = (...args: any[]) => props.slots.header(...args)
        }

        return (
          <ElTableColumn
            showOverflowTooltip={showOverflowTooltip}
            align={align}
            headerAlign={headerAlign}
            {...props}
            prop={v.field}
          >
            {slots}
          </ElTableColumn>
        )
      })
    }

    // 渲染options
    const renderOptions = (item: FormSchema) => {
      switch (item.component) {
        case 'Select':
        case 'SelectV2':
          const { renderSelectOptions } = useRenderSelect(slots)
          return renderSelectOptions(item)
        case 'Radio':
        case 'RadioButton':
          const { renderRadioOptions } = useRenderRadio()
          return renderRadioOptions(item)
        case 'Checkbox':
        case 'CheckboxButton':
          const { renderCheckboxOptions } = useRenderCheckbox()
          return renderCheckboxOptions(item)
        default:
          break
      }
    }

    /**
     * 显示编辑界面
     */
    const renderEdit = (item: Recordable, scope: Recordable) => {
      const notRenderOptions = ['SelectV2', 'Cascader', 'Transfer']
      const fs = Array.isArray(formSchema)
        ? formSchema.find((a) => {
            return a.field == item.field
          })
        : null
      let v = get(scope.row, item.field)
      if (fs) {
        const Com = componentMap[fs.component || 'Input'] as ReturnType<typeof defineComponent>
        const slotsMap: Recordable = {
          ...setItemComponentSlots(slots, fs.componentProps?.slots, item.field)
        }
        if (
          fs.component !== 'SelectV2' &&
          fs.component !== 'Cascader' &&
          fs.componentProps?.options
        ) {
          slotsMap.default = () => renderOptions(fs)
        }
        const checkRules = () => {
          validate(scope, item.field)
        }
        if (!scope.row._validate_) scope.row._validate_ = validate
        return (
          <div
            class={[
              'el-form-item w-[100%] !m0 flex-col',
              errors.value[scope.$index]?.[item.field] ? 'is-error' : ''
            ]}
          >
            <Com
              vModel={scope.row[item.field]}
              {...fs.componentProps}
              {...(notRenderOptions.includes(fs.component as string) && fs.componentProps?.options
                ? { options: fs.componentProps?.options || [] }
                : {})}
              style="width:100%"
              onBlur={checkRules}
              onFocus={checkRules}
            >
              {{ ...slotsMap }}
            </Com>
            {errors.value[scope.$index]?.[item.field] ? (
              <div class="el-form-item__error !relative">
                {errors.value[scope.$index]?.[item.field]}
              </div>
            ) : (
              []
            )}
          </div>
        )
      }

      return (
        <span>
          {item?.formatter ? item?.formatter?.(scope.row, scope.column, v, scope.$index) : v}
        </span>
      )
    }

    const renderPreview = (url: string, field: string) => {
      const { imagePreview } = unref(getProps)
      return (
        <div class="flex items-center">
          {imagePreview.includes(field) ? (
            <ElImage
              src={url}
              fit="cover"
              class="w-[100%]"
              lazy
              preview-src-list={[url]}
              preview-teleported
            />
          ) : null}
        </div>
      )
    }

    const renderTableColumn = (columnsChildren?: TableColumn[]) => {
      const {
        columns,
        reserveIndex,
        pageSize,
        currentPage,
        align,
        headerAlign,
        showOverflowTooltip,
        reserveSelection,
        imagePreview,
        videoPreview,
        editable,
        data
      } = unref(getProps)

      return (columnsChildren || columns).map((v) => {
        if (v.hidden) return null
        if (v.type === 'index') {
          return (
            <ElTableColumn
              type="index"
              index={
                v.index ? v.index : (index) => setIndex(reserveIndex, index, pageSize, currentPage)
              }
              align={v.align || align}
              headerAlign={v.headerAlign || headerAlign}
              label={v.label}
              fixed={v.fixed}
              width="65px"
              min-width={v.minWidth || '100px'}
            ></ElTableColumn>
          )
        } else if (v.type === 'selection') {
          return (
            <ElTableColumn
              type="selection"
              reserveSelection={reserveSelection}
              align={align}
              fixed={v.fixed}
              headerAlign={headerAlign}
              selectable={v.selectable}
              width="50"
              min-width={v.minWidth || '100px'}
            ></ElTableColumn>
          )
        } else if (v.type === 'changeField') {
          return (
            <ElTableColumn
              {...props}
              prop={v.field}
              min-width={v.minWidth || '100px'}
            >
              <EplusFieldComparison
                filed={v.field}
                oldChangeField="oldData"
                formatter={v?.formatter}
              />
            </ElTableColumn>
          )
        } else {
          const props = { ...v } as any
          if (props.children) delete props.children

          const children = v.children

          const slots = {
            default: (...args: any[]) => {
              const data = args[0]

              let isPreview = false
              isPreview =
                imagePreview.some((item) => (item as string) === v.field) ||
                videoPreview.some((item) => (item as string) === v.field)

              return children && children.length
                ? renderTreeTableColumn(children)
                : props?.slots?.default
                  ? props.slots.default(...args)
                  : editable && !props.notEdit
                    ? renderEdit(props, data)
                    : v?.formatter
                      ? v?.formatter?.(data.row, data.column, get(data.row, v.field), data.$index)
                      : isPreview
                        ? renderPreview(get(data.row, v.field), v.field)
                        : get(data.row, v.field)
                          ? get(data.row, v.field)
                          : '-'
            }
          }
          if (props?.slots?.header) {
            slots['header'] = (...args: any[]) => props.slots.header(...args)
          }

          return (
            <ElTableColumn
              showOverflowTooltip={showOverflowTooltip}
              align={align}
              headerAlign={headerAlign}
              {...props}
              prop={v.field}
              min-width={v.minWidth || '100px'}
            >
              {slots}
            </ElTableColumn>
          )
        }
      })
    }

    const renderTableScroll = () => {
      const { columns } = unref(getProps)
      if (elTableRef.value && elTableRef.value?.$el.offsetWidth) {
        let totalWidth = 0
        columns.forEach((item: any) => {
          totalWidth += parseInt(item.width) || parseInt(item.minWidth) || 100
        })

        if (totalWidth > elTableRef.value?.$el.offsetWidth) {
          return <div style="height: 10px"></div>
        } else {
          return null
        }
      }
    }

    return () => {
      const tableSlots = {}
      const actionSlots = {}

      if (getSlot(slots, 'empty')) {
        tableSlots['empty'] = (...args: any[]) => getSlot(slots, 'empty', args)
      }
      if (getSlot(slots, 'append')) {
        tableSlots['append'] = (...args: any[]) => getSlot(slots, 'append', args)
      }

      if (getSlot(slots, 'actions')) {
        actionSlots['default'] = (...args: any[]) => getSlot(slots, 'actions', args)
      }

      if (getSlot(slots, 'actionsLeft')) {
        actionSlots['left'] = (...args: any[]) => getSlot(slots, 'actionsLeft', args)
      }

      return (
        <div
          v-loading={unref(getProps).loading}
          class={prefixCls}
          style="width:100%"
        >
          {unref(getProps).customContent ? (
            <div class="flex flex-wrap">
              {unref(getProps)?.data?.length ? (
                unref(getProps)?.data.map((item) => {
                  const cardSlots = {
                    default: () => {
                      return getSlot(slots, 'content', item)
                    }
                  }
                  if (getSlot(slots, 'content-header')) {
                    cardSlots['header'] = () => {
                      return getSlot(slots, 'content-header', item)
                    }
                  }
                  if (getSlot(slots, 'content-footer')) {
                    cardSlots['footer'] = () => {
                      return getSlot(slots, 'content-footer', item)
                    }
                  }
                  return (
                    <ElCard
                      shadow="hover"
                      class={unref(getProps).cardWrapClass}
                      style={unref(getProps).cardWrapStyle}
                      bodyClass={unref(getProps).cardBodyClass}
                      bodyStyle={unref(getProps).cardBodyStyle}
                    >
                      {cardSlots}
                    </ElCard>
                  )
                })
              ) : (
                <div class="flex flex-1 justify-center">
                  <ElEmpty description="暂无数据" />
                </div>
              )}
            </div>
          ) : (
            <>
              {}
              {unref(getProps).showAction && !unref(getProps).customContent ? (
                <TableActions
                  columns={unref(getProps).columns}
                  onChangSize={changSize}
                  onRefresh={refresh}
                  onConfirm={confirmSetColumn}
                >
                  {{ ...actionSlots }}
                </TableActions>
              ) : null}
              <ElTable
                ref={elTableRef}
                data={unref(getProps).data}
                {...unref(getBindValue)}
                style="font-size:14px"
              >
                {{
                  default: () => renderTableColumn(),
                  ...tableSlots,
                  append: () => renderTableScroll()
                }}
              </ElTable>
            </>
          )}
          {unref(getProps).pagination ? (
            <ElPagination
              v-model:pageSize={pageSizeRef.value}
              v-model:currentPage={currentPageRef.value}
              class="mt-10px"
              {...unref(pagination)}
            ></ElPagination>
          ) : undefined}
        </div>
      )
    }
  }
})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-table;

.#{$prefix-cls} {
  flex: 1 1 auto;
  display: flex;
  flex-direction: column;

  :deep(.el-table--layout-fixed) {
    display: flex;
    flex-direction: column;
    flex: 1 1 auto;
  }

  :deep(.el-table__inner-wrapper) {
    flex: 1 1 auto;
  }

  :deep(.el-table__body-wrapper) {
    display: flex;
    flex-direction: column;
    flex: 1 1 auto;
  }

  :deep(.el-popper.is-dark) {
    max-width: 500px !important;
    width: auto !important;
  }
}
</style>
