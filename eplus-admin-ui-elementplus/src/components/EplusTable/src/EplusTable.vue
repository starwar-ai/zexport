<script lang="tsx">
import type { TableColumnCtx } from 'element-plus'
import {
  ElButton,
  ElCheckbox,
  ElIcon,
  ElPagination,
  ElTable,
  ElTableColumn,
  ElTooltip
} from 'element-plus'
import type { VNode } from 'vue'
import { nextTick } from 'vue'
import EplusTableSetting from './EplusTableSetting.vue'
import { ArrowDownBold, DocumentCopy, Download, InfoFilled, Refresh } from '@element-plus/icons-vue'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusColumn, EplusColumnConfig, EplusTableSchema } from '@/types/eplus'
import { cloneDeep } from 'lodash-es'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import EplusPictures from './EplusPictures.vue'

import alipayArrowLeft from '@/assets/svgs/pay/icon/alipay_arrow_left.svg'
import alipayArrowRight from '@/assets/svgs/pay/icon/alipay_arrow_right.svg'
import { globalStore } from '@/store/modules/globalVariable'
import { formatNum } from '@/utils'

const message = useMessage() //顶部tip提示框
export default defineComponent({
  name: 'EplusTable',
  props: {
    eplusSearchSchema: {
      type: Object as PropType<EplusSearchSchema>,
      required: true
    },
    eplusTableSchema: {
      type: Object as PropType<EplusTableSchema>,
      required: true
    },

    children: {
      type: String as PropType<string>,
      default: 'children'
    },

    pageSize: Number as PropType<number>,
    currentPage: Number as PropType<number>,
    height: Number || String,
    exportFileName: {
      type: String,
      required: false
    }
  },
  emits: [
    'update:pageSize',
    'update:currentPage',
    'register',
    'refresh',
    'emit-open-detail',
    'emit-row'
  ],

  setup(props: any, { expose, slots, emit }) {
    // 在setup开始就获取route实例
    const route = useRoute()

    // const tableDataRef = ref<EplusRow[]>([])
    const showArrowRef = ref(false)
    const checkboxItems = ref<object>({})
    const checkAll = ref(false)
    const showPicturesRef = ref(true)
    const sizeJson = {
      small: { width: '195px', height: '264px', imgHeight: '171px' },
      middle: { width: '240px', height: '318px', imgHeight: '216px' },
      big: { width: '265px', height: '341px', imgHeight: '241px' }
    }

    const localStorageProduct = localStorage.getItem('product')
      ? localStorage.getItem('product')
      : 'small'
    const pictureSize = ref(sizeJson[localStorageProduct])
    const checkAllIndeterminate = ref(false)
    const resultList = ref([])
    const pictureBtn = ref(localStorageProduct || 'small')
    // 改成if-else语句
    const summary = ref(null)
    if (props.eplusTableSchema.showTabs) {
      if (props.eplusTableSchema.tabs.filter((item) => item.isDefault).length > 0) {
        summary.value = props.eplusTableSchema.tabs.filter((item) => item.isDefault)[0].summary
      } else if (props.eplusTableSchema.tabs.filter((item) => item.isTree).length > 0) {
        summary.value = props.eplusTableSchema.tabs.filter((item) => item.isTree)[0].summary
      } else {
        summary.value = props.eplusTableSchema.tabs[0].summary
      }
    } else {
      summary.value = props.eplusTableSchema.summary
    }

    // 改成if-else语句
    const tabPosition = ref(null)
    if (props.eplusTableSchema.showTabs) {
      if (props.eplusTableSchema.tabs.filter((item) => item.isDefault).length > 0) {
        tabPosition.value = props.eplusTableSchema.tabs.filter((item) => item.isDefault)[0].label
      } else if (props.eplusTableSchema.tabs.filter((item) => item.isTree).length > 0) {
        tabPosition.value = props.eplusTableSchema.tabs.filter((item) => item.isTree)[0].label
      } else {
        tabPosition.value = props.eplusTableSchema.tabs[0].label
      }
    } else {
      tabPosition.value = null
    }

    // 改成if-else语句
    const selectionRef = ref(null)
    if (props.eplusTableSchema.showTabs) {
      if (props.eplusTableSchema.tabs.filter((item) => item.isDefault).length > 0) {
        selectionRef.value = props.eplusTableSchema.tabs.filter(
          (item) => item.isDefault
        )[0].selection
      } else if (props.eplusTableSchema.tabs.filter((item) => item.isTree).length > 0) {
        selectionRef.value = props.eplusTableSchema.tabs.filter((item) => item.isTree)[0].selection
      } else {
        selectionRef.value = props.eplusTableSchema.tabs[0].selection
      }
    } else {
      selectionRef.value = props.eplusTableSchema.selection
    }
    const hideChildCheckBoxRef = ref(props.eplusTableSchema.hideChildCheckBox)

    // 改成if-else语句
    const columnPropRef = ref(null)
    if (props.eplusTableSchema.showTabs) {
      const defaultTabs = props.eplusTableSchema.tabs.filter((item) => item.isDefault)
      if (defaultTabs.length > 0) {
        columnPropRef.value = defaultTabs[0].columns
      } else {
        const isTreeTabs = props.eplusTableSchema.tabs.filter((item) => item.isTree)
        if (isTreeTabs.length > 0) {
          columnPropRef.value = isTreeTabs[0].columns
        } else {
          columnPropRef.value = props.eplusTableSchema.tabs[0].columns
        }
      }
    } else {
      columnPropRef.value = props.eplusTableSchema.columns
    }

    // 改成if-else语句
    const columnsRef = ref(null)
    if (props.eplusTableSchema.showTabs) {
      const defaultTabs = props.eplusTableSchema.tabs.filter((item) => item.isDefault)
      if (defaultTabs.length > 0) {
        columnsRef.value = defaultTabs[0].columns.filter((col) => !col.parent)
      } else {
        const isTreeTabs = props.eplusTableSchema.tabs.filter((item) => item.isTree)
        if (isTreeTabs.length > 0) {
          columnsRef.value = isTreeTabs[0].columns.filter((col) => !col.parent)
        } else {
          columnsRef.value = props.eplusTableSchema.tabs[0].columns.filter((col) => !col.parent)
        }
      }
    } else {
      columnsRef.value = props.eplusTableSchema.columns.filter((col) => !col.parent)
    }

    const tableRef = ref(null)

    // 改成if-else语句
    const parentColumnsRef = ref(null)
    if (props.eplusTableSchema.showTabs) {
      const defaultTabs = props.eplusTableSchema.tabs.filter((item) => item.isDefault)
      if (defaultTabs.length > 0) {
        parentColumnsRef.value = defaultTabs[0].columns.filter((col) => col.parent)
      } else {
        const isTreeTabs = props.eplusTableSchema.tabs.filter((item) => item.isTree)
        if (isTreeTabs.length > 0) {
          parentColumnsRef.value = isTreeTabs[0].columns.filter((col) => col.parent)
        } else {
          parentColumnsRef.value = props.eplusTableSchema.tabs[0].columns.filter(
            (col) => col.parent
          )
        }
      }
    } else {
      parentColumnsRef.value = props.eplusTableSchema.columns.filter((col) => col.parent)
    }

    const isTreeRef = ref<boolean>(parentColumnsRef.value.length > 0 ? true : false)

    //刷新页面元素控件
    const refreshPageItem = () => {
      checkboxItems.value = []
      checkAll.value = false
      checkAllIndeterminate.value = false
    }

    const tabIndex = ref(0)
    if (props.eplusTableSchema.showTabs) {
      props.eplusTableSchema.tabs.forEach((e, i) => {
        if (e.label === tabPosition.value) {
          tabIndex.value = i
        }
      })
    }
    watch(
      () => tabPosition.value,
      (currentTabPosition) => {
        props.eplusTableSchema.tabs.forEach((e, i) => {
          if (e.label === tabPosition.value) {
            tabIndex.value = i
          }
        })
        let tab = props.eplusTableSchema.tabs.filter((it) => it.label == currentTabPosition)[0]
        selectionRef.value = tab.selection
        summary.value = tab.summary
        columnPropRef.value = tab.columns ? tab.columns : []
        isTreeRef.value = tab.isTree ? true : false
        columnsRef.value = tab.columns.filter((col) => !col.parent)
        parentColumnsRef.value = tab.columns.filter((col) => col.parent)
        refreshPageItem()
        // 恢复当前tab的列宽度
        restoreColumnWidths()
        // 清空表格数据并重新获取,避免不同维度间的数据污染
        tableObject.tableList = []
        refresh()
      },
      { deep: true }
    )
    const checkedItems = computed(() => {
      let items: any[] = []
      for (let key of Object.keys(checkboxItems.value)) {
        if (checkboxItems.value[key].checked) {
          if (hideChildCheckBoxRef.value) {
            if (checkboxItems.value[key].isParent) {
              items.push(checkboxItems.value[key].row)
            }
          } else {
            if (!checkboxItems.value[key].isParent) {
              items.push(checkboxItems.value[key].row)
            }
          }
        }
      }
      return items
    })

    const sumRef = computed(() => {
      return tableObject.sum
    })

    //根据用户的操作，更新column的配置
    const applyColumnSettings = (columnSettings: EplusColumnConfig[]): EplusColumn[] => {
      let defaultColumns = columnPropRef.value.filter((col) => !col.parent)
      defaultColumns.forEach((col) => {
        const setting = columnSettings.find((setting) => setting.prop === col.prop)
        if (setting) {
          col.hide = setting.hide
          col.minWidth = setting.minWidth
          col.disable = setting.disable
          col.fixed = setting.fixed
        }
      })

      //根据配置的列顺序，调整显示顺序
      let updatedColumns: EplusColumn[] = []
      for (let i = 0; i < columnSettings.length; i++) {
        const setting = columnSettings[i]
        const col = defaultColumns.find((col) => col.prop === setting.prop)
        if (col) {
          updatedColumns.push(col)
        }
      }

      //将未包含在settings的列，添加到末尾
      // updatedColumns.forEach((col) => {
      //   if (!updatedColumns.includes(col)) {
      //     updatedColumns.push(col)
      //   }
      // })
      return updatedColumns
    }

    //根据用户的操作，更新column的配置
    const applyParentColumnSettings = (columnSettings: EplusColumnConfig[]) => {
      let defaultParentColumns = columnPropRef.value.filter((col) => col.parent)
      defaultParentColumns.forEach((col) => {
        const setting = columnSettings.find((setting) => setting.prop === col.prop)
        if (setting) {
          col.hide = setting.hide
          col.minWidth = setting.minWidth
          col.disable = setting.disable
          col.fixed = setting.fixed
        }
      })
      //根据配置的列顺序，调整显示顺序
      let updatedColumns: EplusColumn[] = []
      for (let i = 0; i < columnSettings.length; i++) {
        const setting = columnSettings[i]
        const col = defaultParentColumns.find((col) => col.prop === setting.prop)
        if (col) {
          updatedColumns.push(col)
        }
      }

      //将未包含在settings的列，添加到末尾
      // updatedColumns.forEach((col) => {
      //   if (!updatedColumns.includes(col)) {
      //     updatedColumns.push(col)
      //   }
      // })

      return updatedColumns
    }

    const defaultParentColumnSettings = computed(() => {
      const result: EplusColumnConfig[] = []
      columnPropRef.value?.forEach((col) => {
        if (!col.label) return

        if (col.parent) {
          let colConfig: EplusColumnConfig = {
            prop: col.prop,
            label: col.label,
            hide: col.hide,
            parent: true,
            minWidth: col.minWidth,
            fixed: col.fixed,
            disable: col.disable
          }
          result.push(colConfig)
        }
      })
      if (result && result.length > 0) {
        result[0].disable = true
      }
      return result
    })

    const defaultColumnSettings = computed(() => {
      const result: EplusColumnConfig[] = []
      columnPropRef.value?.forEach((col) => {
        if (!col.label) return

        if (!col.parent) {
          let colConfig: EplusColumnConfig = {
            prop: col.prop,
            label: col.label,
            hide: col.hide,
            minWidth: col.minWidth,
            fixed: col.fixed,
            disable: col.disable
          }
          result.push(colConfig)
        }
      })
      if (result && result.length > 0) {
        result[0].disable = true
      }
      return result
    })

    // 列宽度保存和恢复逻辑
    const getColumnWidthStorageKey = () => {
      return `eplus_table_column_width_${route.path}_${tabIndex.value}`
    }

    // 从localStorage恢复列宽度
    const restoreColumnWidths = () => {
      const storageKey = getColumnWidthStorageKey()
      const savedWidths = localStorage.getItem(storageKey)
      if (savedWidths) {
        try {
          const widthMap = JSON.parse(savedWidths)
          columnsRef.value.forEach((col) => {
            if (widthMap[col.prop]) {
              col.minWidth = widthMap[col.prop]
            }
          })
          if (parentColumnsRef.value) {
            parentColumnsRef.value.forEach((col) => {
              if (widthMap[col.prop]) {
                col.minWidth = widthMap[col.prop]
              }
            })
          }
        } catch (e) {
          console.error('Failed to restore column widths:', e)
        }
      }
    }

    // 保存列宽度到localStorage
    const saveColumnWidths = (newWidth: number, _oldWidth: number, column: any) => {
      const storageKey = getColumnWidthStorageKey()
      let widthMap = {}

      try {
        const savedWidths = localStorage.getItem(storageKey)
        if (savedWidths) {
          widthMap = JSON.parse(savedWidths)
        }
      } catch (e) {
        console.error('Failed to parse saved column widths:', e)
      }

      widthMap[column.property] = newWidth
      localStorage.setItem(storageKey, JSON.stringify(widthMap))

      // 同步更新列配置
      columnsRef.value.forEach((col) => {
        if (col.prop === column.property) {
          col.minWidth = newWidth + 'px'
        }
      })
      if (parentColumnsRef.value) {
        parentColumnsRef.value.forEach((col) => {
          if (col.prop === column.property) {
            col.minWidth = newWidth + 'px'
          }
        })
      }
    }

    const serializedColumnSettings = ref(
      defaultColumnSettings.value.filter((col) => {
        return col.fixed === true || col.fixed === 'left'
      })
    )
    const unSerializedColumnSettings = ref(
      defaultColumnSettings.value.filter((col) => {
        return col.fixed === false || col.fixed === 'right'
      })
    )
    const serializedParentColumnSettings = ref(
      defaultParentColumnSettings.value.filter((col) => {
        return col.fixed === true || col.fixed === 'left'
      })
    )
    const unSerializedParentColumnSettings = ref(
      defaultParentColumnSettings.value.filter((col) => {
        return col.fixed === false || col.fixed === 'right'
      })
    )

    //根据cache中的配置，恢复column的配置
    const restoreParentColumnSettings = (serialized: string): EplusColumnConfig[] => {
      const columnSettings = JSON.parse(serialized)
      return columnSettings
    }

    const syncedParentColumnSettings = computed(() => {
      let items: EplusColumnConfig[] = []
      if (parentColumnsRef.value.length > 0) {
        serializedParentColumnSettings.value.forEach((setting) => {
          //找到默认配置中对应的设置
          const defaultSetting = defaultParentColumnSettings.value.find(
            (defaultSetting) => defaultSetting.prop === setting.prop
          )
          //如果默认配置中没有对应的设置，说明用户删除了该列
          //则不同步该列的设置
          if (defaultSetting) {
            items.push({ ...defaultSetting, ...setting })
          }
        })

        //找到默认配置中没有的列，添加到末尾
        defaultParentColumnSettings.value.forEach((defaultSetting) => {
          const setting = items.find((setting) => setting.prop === defaultSetting.prop)

          if (!setting) {
            items.push(defaultSetting)
          }
        })
      }
      return items
    })
    parentColumnsRef.value = applyParentColumnSettings(syncedParentColumnSettings.value)

    const syncedColumnSettings = computed(() => {
      let items: EplusColumnConfig[] = []
      serializedColumnSettings.value.forEach((setting) => {
        //找到默认配置中对应的设置
        const defaultSetting = defaultColumnSettings.value.find(
          (defaultSetting) => defaultSetting.prop === setting.prop
        )

        //如果默认配置中没有对应的设置，说明用户删除了该列
        //则不同步该列的设置
        if (defaultSetting) {
          items.push({ ...defaultSetting, ...setting })
        }
      })

      //找到默认配置中没有的列，添加到末尾
      defaultColumnSettings.value.forEach((defaultSetting) => {
        const setting = items.find((setting) => setting.prop === defaultSetting.prop)
        if (!setting) {
          items.push(defaultSetting)
        }
      })
      return items
    })

    // getPreferences(useRoute().path)
    //   .then((res) => {
    //     if (res.children) {
    //       columnsRef.value = applyColumnSettings(res.children)
    //     } else {
    //       columnsRef.value = applyColumnSettings(syncedColumnSettings.value)
    //     }
    //   })
    //   .catch(() => {
    //     columnsRef.value = applyColumnSettings(syncedColumnSettings.value)
    //   })
    columnsRef.value = applyColumnSettings(syncedColumnSettings.value)

    // 在列配置应用后恢复列宽度
    restoreColumnWidths()

    const renderTable = () => {
      return (
        <>
          <ElTable
            ref="tableRef"
            v-loading={tableObject.loading}
            data={tableObject.tableList}
            span-method={spanMethod}
            row-key="id"
            class="frame-table"
            key="renderTable"
            border={true}
            header-row-class-name="headerStyle"
            default-expand-all
            tree-props={{ children: props.children ?? '' }}
            onSortChange={(data) => {
              sortChange(data, columnsRef.value)
            }}
            onHeaderDragend={saveColumnWidths}
            summary-method={getSummaries}
            show-summary={summary.value}
            row-style={rowStyle}
            style={'flex:1 1 auto'}
            height={props?.height ? props.height : tableHeight.value}
            scrollbar-always-on={true}
            empty-text={tableObject.loading ? ' ' : '暂无数据'}
          >
            {selectionRef.value ? defaultSelectionCol() : ''}
            {columnsRef.value.map((col) => {
              return defaultCol(col)
            })}
          </ElTable>
        </>
      )
    }

    const renderTableUnExpend = () => {
      return (
        <>
          <ElTable
            ref="tableRef"
            v-loading={tableObject.loading}
            class="frame-table"
            data={tableObject.tableList}
            key="renderTableUnExpend"
            border={true}
            header-row-class-name="headerStyle"
            onSortChange={(data) => {
              sortChange(data, columnsRef.value)
            }}
            onHeaderDragend={saveColumnWidths}
            summary-method={getSummaries}
            show-summary={summary.value}
            row-style={rowStyle}
            height={props?.height ? props.height : tableHeight.value}
            scrollbar-always-on={true}
            style="font-size:14px;"
            empty-text={tableObject.loading ? ' ' : '暂无数据'}
          >
            {selectionRef.value ? defaultSelectionCol() : ''}
            {columnsRef.value.map((col) => {
              return defaultCol(col)
            })}
          </ElTable>
        </>
      )
    }

    /**获取列最小宽度 */
    const getMiniWidth = (col) => {
      let colMinWidth = col.minWidth ? col.minWidth : '100px'
      if (col.extend && col.showArrowFlag) {
        colMinWidth = parseInt(col.minWidth?.toString() || '0')
        col.extend.forEach((it) => {
          colMinWidth += parseInt(it.minWidth) || 100
        })
      }
      return colMinWidth
    }

    const defaultCol = (col) => {
      const defaultFun = (item, col) => {
        if (isParent(item.row)) {
          let cellsDiv = <div></div>
          let actionDiv = <div></div>
          const actionSlots = parentColumnsRef.value.filter((pCol) => {
            return pCol.slots && pCol.prop == 'action'
          })
          let actionSlot
          if (actionSlots && actionSlots.length > 0) {
            actionSlot = actionSlots[0]
            let slotStyle = `display: flex;flex-direction: row;align-items:center;min-width:${actionSlot?.minWidth}`
            actionDiv = (
              <div style={slotStyle}>
                {actionSlot?.slots ? actionSlot?.slots.default(item) : ''}
              </div>
            )
          }

          if (item.row[item.column.property]) {
            if (item.row[item.column.property] === '(action)') {
              return actionDiv
            } else {
              if (!item.row[item.column.property].includes('(action)')) {
                actionDiv = <div></div>
              }
              let cells = item.row[item.column.property].split('&nbsp;').filter((col) => {
                return col && col != '(action)'
              })
              cellsDiv = (
                <el-row
                  type="flex"
                  class="table-row"
                >
                  <div class="row-left">
                    {cells.map((it, index) => {
                      let cellDiv
                      if (it.includes('(isCopy)')) {
                        it = it.replace('(isCopy)', '')
                        cellDiv = copyDiv(it)
                      }
                      let changeDiv
                      if (it.includes('(isChange)')) {
                        it = it.replace('(isChange)', '')
                        changeDiv = (
                          <el-tag
                            type="warning"
                            effect="plain"
                            size="small"
                            class="ml-2"
                          >
                            变更中
                          </el-tag>
                        )
                      }
                      const curentParent = parentColumnsRef.value.filter((pCol) => {
                        return pCol.label == it.split(':')[0]
                      })
                      if (curentParent && curentParent.length > 0) {
                        let title = it.split(':')[0]
                        let content = it.substring(it.indexOf(':') + 1, it.length)
                        const parentProp = parentColumnsRef.value.find(
                          (pCol) => pCol.label === title
                        )
                        const parentSlots = parentColumnsRef.value.filter((pCol) => {
                          return pCol.slots && pCol.label === title
                        })
                        if (parentSlots && parentSlots.length > 0) {
                          content = parentSlots[0]?.slots.default(item.row)
                        } else {
                          const parentFormatters = parentColumnsRef.value.filter((pCol) => {
                            return pCol.formatter && pCol.label === title
                          })
                          if (parentFormatters && parentFormatters.length > 0) {
                            content = parentFormatters[0].formatter(item.row, item.column, content)
                          }
                        }

                        const openDetail = () => {
                          emit('emit-open-detail', item.row?.id)
                          emit('emit-row', item.row)
                        }
                        let cellStyle = `display: flex;flex-direction: row;justify-content:center;align-items:center;min-width:${curentParent[0].minWidth};margin-right:10px`
                        let codeStyle = `color: #005bf5;cursor: pointer`
                        return (
                          <div style={cellStyle}>
                            <span style="color: #888c94;margin: 0 4px 0 8px;">
                              {parentProp?.prop !== 'code' ? title + ' : ' : ''}
                            </span>
                            <span
                              style={parentProp?.isHyperlink ? codeStyle : ''}
                              onclick={parentProp?.isHyperlink ? openDetail : ''}
                            >
                              {content}
                            </span>

                            <span class="copyStyle"> {cellDiv ? cellDiv : ''}</span>
                            <span>
                              {parentProp?.prop == 'code' && changeDiv && !col?.notShowChange
                                ? changeDiv
                                : ''}
                            </span>
                          </div>
                        )
                      }
                    })}
                  </div>
                  <div class="row-right"> {actionDiv}</div>
                </el-row>
              )
              return cellsDiv
            }
          }
        } else if (col.extend) {
          if (isParent(item.row)) {
            return <div>{item.row[item.column.property]}</div>
          }

          if (col.showArrowFlag) {
            let value = item.row[item.column.property]
            if (value || value == 0) {
              let columnStyle = `display: flex;flex-direction: column;align-items:right;min-width:${col.minWidth};`
              let rowStyle = `display: flex;flex-direction: row;align-items:right;text-align:canter;`
              return (
                <div style={rowStyle}>
                  <div style={columnStyle}>
                    <div>{value}</div>
                    <div>总和</div>
                  </div>
                  {col.extend.map((it) => {
                    columnStyle = `display: flex;flex-direction: column;align-items:left;min-width:${it.minWidth}`
                    return (
                      <div style={columnStyle}>
                        <div style="height:20px">{item.row[it.prop]}</div>
                        <div>{it.label}</div>
                      </div>
                    )
                  })}
                </div>
              )
            }
          } else {
            let contentHtml = ''
            col.extend.forEach((it) => {
              contentHtml += `
                            <div style="display: flex;flex-direction: row;align-items:center;justify-content:space-between;width:100px; ">
                              <div>${it.label}</div>
                              <div>${item.row[it.prop]}</div>
                            </div>
                            `
            })
            return (
              <ElTooltip
                placement="bottom"
                effect="light"
                content={contentHtml}
                raw-content
              >
                <div style="display: flex;flex-direction: row;align-items:center">
                  <div>{item.row[item.column.property]}</div>
                  <ElIcon>
                    <ArrowDownBold />
                  </ElIcon>
                </div>
              </ElTooltip>
            )
          }
        } else if (col.items) {
          let direction = 'column'
          if (col.direction) {
            direction = col.direction
          }
          let itemsStyle = `display: flex;flex-direction: ${direction};align-items:left;`
          const cellContent = (
            <div
              style={itemsStyle}
              class="children-row"
            >
              {col.items?.map((it, index) => {
                let itemStyle = ''
                if (index > 0 && direction == 'row') {
                  itemStyle = 'margin-left:10px'
                }
                const strFormat = it.formatter
                  ? it?.formatter?.(
                      item.row,
                      {
                        ...item.column,
                        property: it.prop
                      },
                      item.row[it.prop]
                    )
                  : item.row[it.prop]
                    ? typeof item.row[it.prop] == 'object'
                      ? JSON.stringify(item.row[it.prop])
                      : item.row[it.prop].toString()
                    : ''

                return (
                  <div style="display: flex;flex-direction: row;justify-content:center;align-items:center;">
                    <div style={itemStyle}>{strFormat}</div>
                    {it.isCopy
                      ? copyDiv(
                          strFormat && typeof strFormat === 'string'
                            ? strFormat
                            : it.copyFormatter
                              ? it.copyFormatter(strFormat)
                              : ''
                        )
                      : ''}
                  </div>
                )
              })}
            </div>
          )
          if (!col.isTooltip) {
            return cellContent
          } else {
            return (
              <ElTooltip
                placement="bottom"
                effect="light"
              >
                {{
                  default: () => {
                    return cellContent
                  },
                  content: () => {
                    const tooltipData = []
                    const tooltipJson = {}
                    col.items?.forEach((it) => {
                      tooltipJson[it.prop] = it.formatter
                        ? it?.formatter?.(
                            item.row,
                            {
                              ...item.column,
                              property: it.prop
                            },
                            item.row[it.prop]
                          )
                        : item.row[it.prop]
                          ? typeof item.row[it.prop] == 'object'
                            ? JSON.stringify(item.row[it.prop])
                            : item.row[it.prop].toString()
                          : ''
                    })
                    tooltipData.push(tooltipJson)
                    return (
                      <div>
                        <ElTable
                          data={tooltipData}
                          style="width: 100%"
                        >
                          {col.items?.map((it) => {
                            return (
                              <ElTableColumn
                                prop={it.prop}
                                label={it.label}
                                align="left"
                              >
                                {{
                                  default: (item) => {
                                    return <div>{tooltipJson[item.column.property]}</div>
                                  }
                                }}
                              </ElTableColumn>
                            )
                          })}
                        </ElTable>
                      </div>
                    )
                  }
                }}
              </ElTooltip>
            )
          }
        } else if (col.slots) {
          let cellStyle = `display: flex;text-align:left;align-items:center;min-width:${col.minWidth};`
          return <div style={cellStyle}>{col.slots.default(item)}</div>
        } else if (col.columns) {
          const strFormat = col.formatter
            ? col?.formatter?.(item.row, col.showProp)
            : item.row[item.column.property]
              ? typeof item.row[item.column.property] == 'object'
                ? JSON.stringify(item.row[item.column.property])
                : item.row[item.column.property].toString()
              : ''
          return (
            <el-popover
              placement="bottom"
              width="800px"
              trigger="click"
            >
              {{
                reference: () => {
                  return strFormat
                },
                default: () => {
                  const datas = item.row[col.prop]
                  const columns = col.columns
                  return (
                    <ElTable
                      data={datas}
                      max-height="300px"
                    >
                      {columns?.map((column) => {
                        return defaultCol(column)
                      })}
                    </ElTable>
                  )
                }
              }}
            </el-popover>
          )
        } else if (col?.isShowChange) {
          if (typeof props.eplusTableSchema?.oldChangeField === 'string') {
            return (
              <EplusFieldComparison
                item={item?.row}
                filed={col?.prop}
                formatter={col?.formatter}
                oldChangeField={props.eplusTableSchema?.oldChangeField}
              />
            )
          } else {
            return '-'
          }
        } else {
          if ('contractFreight' == item.column.property) {
          }
          const strFormat = col.formatter
            ? col?.formatter?.(item.row, item.column, item.row[item.column.property])
            : item.row[item.column.property]
              ? typeof item.row[item.column.property] == 'object'
                ? JSON.stringify(item.row[item.column.property])
                : item.row[item.column.property].toString()
              : ''
          let ellipsisStyle = 'overflow:hidden;white-space:nowrap;text-overflow:ellipsis;'
          const openDetail = () => {
            emit('emit-open-detail', item.row?.id)
            emit('emit-row', item.row)
          }
          let codeStyle = `color: #005bf5;cursor: pointer`
          let changeDiv = (
            <el-tag
              type="warning"
              effect="plain"
              size="small"
              class="ml-2"
            >
              变更中
            </el-tag>
          )
          return (
            <div style="display: flex;flex-direction: row;justify-content:left;align-items:left;">
              <div
                style={
                  (col.showOverflowTooltip ? ellipsisStyle : '', col.isHyperlink ? codeStyle : '')
                }
                onclick={col.isHyperlink ? openDetail : ''}
                class="overflowSty"
              >
                {strFormat}
              </div>
              <span class="childrenCopy">
                {' '}
                {col.isCopy
                  ? copyDiv(
                      strFormat && typeof strFormat === 'string'
                        ? strFormat
                        : col.copyFormatter
                          ? col.copyFormatter(strFormat)
                          : ''
                    )
                  : ''}
              </span>
              <span>
                {col?.prop == 'code' && item.row?.changeStatus === 2 && !col?.notShowChange
                  ? changeDiv
                  : ''}
              </span>
            </div>
          )
        }
      }
      const headerFun = (col) => {
        if (col.items && col.items.length > 0 && !col.label) {
          col.label = col.items.map((it) => it.label).join('/')
        }

        let headerColumnStyle = `display:inline-flex;vertical-align: middle;text-align:${col.showArrowFlag ? 'left' : 'right'};`
        return (
          <>
            <label style={headerColumnStyle}> {col.label} </label>
            {col.tooltip && (
              <ElTooltip
                placement="top"
                content={col.tooltip}
              >
                <ElIcon style="margin-left:5px;display: inline-flex;vertical-align: middle; ">
                  <InfoFilled />
                </ElIcon>
              </ElTooltip>
            )}
            {col.extend && (
              <ElIcon
                onClick={() => showArrow(col)}
                style="margin-left:5px;display: inline-flex;vertical-align: middle;"
              >
                {col.showArrowFlag ? (
                  <ElImage src={alipayArrowLeft} />
                ) : (
                  <ElImage src={alipayArrowRight} />
                )}
              </ElIcon>
            )}
          </>
        )
      }
      return (
        <ElTableColumn
          prop={col.prop}
          key={col.prop}
          label={col.label}
          formatter={col.formatter}
          min-width={getMiniWidth(col)}
          fixed={col.fixed}
          sortable={col.sortable}
          align="left"
          show-overflow-tooltip={col.showOverflowTooltip}
        >
          {{
            header: () => {
              return headerFun(col)
            },
            default: (item) => {
              return defaultFun(item, col)
            }
          }}
        </ElTableColumn>
      )
    }

    const defaultSelectionCol = () => {
      return (
        <el-table-column
          width="50"
          fixed={true}
          align="left"
        >
          {{
            header: () => {
              return (
                <ElCheckbox
                  v-model={checkAll.value}
                  indeterminate={checkAllIndeterminate.value}
                  onChange={allCheckChange}
                />
              )
            },
            default: (item) => {
              let index = 0
              if (item.$index != '-1' && currentPageObjs[tableObject.currentPage]) {
                if (isTreeRef.value) {
                  let preCount = 0
                  for (let key of Object.keys(currentPageObjs)) {
                    if (Number(key) < Number(tableObject.currentPage)) {
                      preCount = preCount + currentPageObjs[key].count
                    }
                  }
                  index = preCount + item.$index
                } else {
                  index =
                    (Number(tableObject.currentPage) - 1) * Number(tableObject.pageSize) +
                    item.$index
                }
                if (!Object.hasOwn(checkboxItems.value, index)) {
                  checkboxItems.value[index] = {
                    checked: false,
                    indeterminate: false,
                    index: index,
                    isParent: isParent(item.row),
                    id: item.row.id,
                    parentId: item.row.parentId,
                    row: item.row,
                    currentPage: tableObject.currentPage
                  }
                }
              }
              if (hideChildCheckBoxRef.value && !item?.row?.children) {
                return ''
              } else {
                return (
                  <ElCheckbox
                    v-model={checkboxItems.value[index].checked}
                    indeterminate={checkboxItems.value[index].indeterminate}
                    onChange={(value) => {
                      checkChange(value, item, index)
                    }}
                  />
                )
              }
            }
          }}
        </el-table-column>
      )
    }

    const isParent = (row) => {
      if (!isTreeRef.value) {
        return false
      }
      return row.children ? true : false
    }

    const allCheckChange = (value: boolean) => {
      // 使用本地变量存储状态
      const newValue = value

      // 使用 nextTick 延迟更新
      nextTick(() => {
        checkAllIndeterminate.value = false
        // 创建新对象而不是直接修改原对象
        const updatedItems = { ...checkboxItems.value }

        for (let key of Object.keys(updatedItems)) {
          if (updatedItems[key].currentPage == tableObject.currentPage) {
            updatedItems[key] = {
              ...updatedItems[key],
              checked: newValue,
              indeterminate: false
            }
          }
        }

        checkboxItems.value = updatedItems
      })
    }

    const checkChange = (value, item, index) => {
      // 使用本地变量存储状态，避免直接修改响应式数据
      const newValue = value
      const currentIndex = index

      // 使用 nextTick 延迟更新，避免在同一个渲染周期内多次修改数据
      nextTick(() => {
        // 创建新对象而不是直接修改原对象
        if (item && item.row) {
          item.row = { ...item.row, checked: newValue }
        }

        let currentItem = checkboxItems.value[currentIndex]
        if (isTreeRef.value) {
          if (currentItem.isParent) {
            //所有子节点跟随变化
            const updatedItems = { ...checkboxItems.value }
            for (let key of Object.keys(updatedItems)) {
              if (!updatedItems[key].isParent && updatedItems[key].parentId === currentItem.id) {
                updatedItems[key] = { ...updatedItems[key], checked: newValue }
              }
            }
            checkboxItems.value = updatedItems
          } else {
            //获取父节点
            let parentItem = Object.values(checkboxItems.value).filter((it) => {
              return it.isParent && it.id === currentItem.parentId
            })[0]
            //获取兄弟节点
            let checkedSiblingItems = Object.values(checkboxItems.value).filter((it) => {
              return !it.isParent && it.checked && it.parentId === currentItem.parentId
            })
            let unCheckedSiblingItems = Object.values(checkboxItems.value).filter((it) => {
              return !it.isParent && !it.checked && it.parentId === currentItem.parentId
            })

            const updatedItems = { ...checkboxItems.value }
            if (
              checkedSiblingItems &&
              checkedSiblingItems.length > 0 &&
              !(unCheckedSiblingItems && unCheckedSiblingItems.length > 0)
            ) {
              if (parentItem && updatedItems[parentItem.index]) {
                updatedItems[parentItem.index] = {
                  ...updatedItems[parentItem.index],
                  checked: true,
                  indeterminate: false
                }
              }
            } else if (
              unCheckedSiblingItems &&
              unCheckedSiblingItems.length > 0 &&
              !(checkedSiblingItems && checkedSiblingItems.length > 0)
            ) {
              if (parentItem && updatedItems[parentItem.index]) {
                updatedItems[parentItem.index] = {
                  ...updatedItems[parentItem.index],
                  checked: false,
                  indeterminate: false
                }
              }
            } else {
              if (parentItem && updatedItems[parentItem.index]) {
                updatedItems[parentItem.index] = {
                  ...updatedItems[parentItem.index],
                  checked: false,
                  indeterminate: true
                }
              }
            }
            checkboxItems.value = updatedItems
          }
        }
        resetCheckAllStatus()
      })
    }

    //--重置checkAll按钮状态
    const resetCheckAllStatus = () => {
      // 使用 nextTick 延迟更新
      nextTick(() => {
        let checkedItems = Object.values(checkboxItems.value).filter((it) => {
          return it.checked && it.currentPage == tableObject.currentPage
        })
        let unCheckedItems = Object.values(checkboxItems.value).filter((it) => {
          return !it.checked && it.currentPage == tableObject.currentPage
        })
        if (
          checkedItems &&
          checkedItems.length > 0 &&
          !(unCheckedItems && unCheckedItems.length > 0)
        ) {
          checkAll.value = true
          checkAllIndeterminate.value = false
        } else if (
          (unCheckedItems &&
            unCheckedItems.length > 0 &&
            !(checkedItems && checkedItems.length > 0)) ||
          (!unCheckedItems && !checkedItems)
        ) {
          checkAll.value = false
          checkAllIndeterminate.value = false
        } else {
          checkAll.value = false
          checkAllIndeterminate.value = checkedItems.length != 0
        }
      })
    }

    const showArrow = (col) => {
      col.showArrowFlag = col.showArrowFlag ? false : true
    }

    const copyDiv = (it) => {
      if (!it) {
        return
      }
      return (
        <ElTooltip
          placement="top"
          content="复制"
        >
          <ElIcon
            style="margin-left:10px"
            onClick={async () => {
              await copyToClipboard(it)
            }}
          >
            <DocumentCopy />
          </ElIcon>
        </ElTooltip>
      )
    }

    const copyToClipboard = async (text) => {
      if (text.indexOf(':') > -1) {
        text = text.substring(text.indexOf(':') + 1)
      }
      const textarea = document.createElement('textarea')
      textarea.value = text
      document.body.appendChild(textarea)
      textarea.select()
      try {
        // 尝试执行复制操作
        const success = document.execCommand('copy')
        if (success) {
          message.success('复制成功')
        } else {
          message.error('复制失败')
        }
      } catch (error) {
        message.error('复制失败:' + error)
      }
      document.body.removeChild(textarea)
    }

    const rowStyle = (data: { row: any; rowIndex: number }) => {
      if (isParent(data.row)) {
        return { background: '#f2f5fa' }
      }
      if (props.eplusTableSchema?.setRowStyle) {
        return props.eplusTableSchema.setRowStyle(data)
      }
    }

    const spanMethod = (data: {
      row: any
      rowIndex: number
      column: TableColumnCtx<any>
      columnIndex: number
    }):
      | {
          rowspan: number
          colspan: number
        }
      | undefined => {
      //包含children的父节点，合并单元格
      if (data.row[props.children]) {
        //获取所有单元格合并项
        let parentContent = getParentContent(data)
        //筛选所有子列中的固定列，因为父的固定列需要和子一样
        let fixedColumns = columnsRef.value.filter(
          (col) => col.fixed === true || col.fixed === 'left'
        )
        let parentContentList = parentContent.split('&nbsp;').filter((col) => col)
        let fixedParentColumnLength =
          fixedColumns.length < parentContentList.length
            ? fixedColumns.length
            : parentContentList.length
        let parentColumnLength =
          columnsRef.value.length < parentColumnsRef.value.length
            ? parentColumnsRef.value.length
            : columnsRef.value.length

        const sortColumnsRef = [
          ...columnsRef.value.filter((it) => it.fixed === true || it.fixed === 'left'),
          ...columnsRef.value.filter((it) => !it.fixed || it.fixed === 'right')
        ]
        for (let i = 0; i < fixedParentColumnLength; i++) {
          data.row[sortColumnsRef[i].prop] = parentContentList[i]
        }
        let endParentCell = ''
        if (parentContentList.length > fixedColumns.length) {
          for (let i = fixedColumns.length; i < parentContentList.length; i++) {
            if (endParentCell) {
              endParentCell += '&nbsp;' + parentContentList[i]
            } else {
              endParentCell += parentContentList[i]
            }
          }
          data.row[sortColumnsRef[fixedColumns.length].prop] = endParentCell
        }

        if (selectionRef.value) {
          fixedParentColumnLength += 1
          parentColumnLength += 1
        }
        if (data.columnIndex < fixedParentColumnLength) {
          return { rowspan: 1, colspan: 1 }
        } else if (data.columnIndex == fixedParentColumnLength) {
          return { rowspan: 1, colspan: parentColumnLength - fixedParentColumnLength }
        } else {
          //不显示
          return { rowspan: 0, colspan: 0 }
        }
      } else {
        return props.eplusTableSchema.spanMethod
          ? props.eplusTableSchema.spanMethod(data)
          : { rowspan: 1, colspan: 1 }
      }
    }

    const getParentContent = (data) => {
      let parentContent = ''
      parentColumnsRef.value
        .filter((it) => it.fixed === true || it.fixed === 'left')
        .forEach((col) => {
          if (col.prop != 'action') {
            let value
            if (col.prop) {
              value = data.row[col.prop]
            }
            if (!value) {
              value = ''
            }
            if (parentContent) {
              parentContent = parentContent + `&nbsp;${col.label}:${value}`
            } else {
              parentContent = `${col.label}:${value}`
            }
            if (col.isCopy && value) {
              parentContent = parentContent + '(isCopy)'
            }
            if (data.row['changeStatus'] === 2) {
              parentContent = parentContent + '(isChange)'
            }
          } else {
            parentContent = parentContent + '&nbsp;(action)'
          }
        })
      parentColumnsRef.value
        .filter((it) => !it.fixed || it.fixed === 'right')
        .forEach((col) => {
          if (col.prop != 'action') {
            let value
            if (col.prop) {
              value = data.row[col.prop]
            }
            if (!value) {
              value = ''
            }
            if (parentContent) {
              parentContent = parentContent + `&nbsp;${col.label}:${value}`
            } else {
              parentContent = `${col.label}:${value}`
            }
            if (col.isCopy && value) {
              parentContent = parentContent + '(isCopy)'
            }
            if (data.row['changeStatus'] === 2) {
              parentContent = parentContent + '(isChange)'
            }
          } else {
            parentContent = parentContent + '&nbsp;(action)'
          }
        })
      return parentContent
    }

    const sortChange = async (data: { column: any; prop: string; order: any }, columns) => {
      const currentCol = columns.filter((it) => it.prop === data.prop)[0]
      if (data.order) {
        setSearchParams(
          {
            sortingFields: JSON.stringify([
              {
                field: data.prop,
                subField: currentCol.sortField,
                order:
                  data.order === 'ascending' ? 'asc' : data.order === 'descending' ? 'desc' : ''
              }
            ])
          },
          []
        )
      }
    }

    const handleResetSettings = () => {
      parentColumnsRef.value = applyParentColumnSettings(defaultParentColumnSettings.value)
      columnsRef.value = applyColumnSettings(defaultColumnSettings.value)
    }

    const handleSaveSettings = (mode, newColumnSettings) => {
      if (mode === 'parent') {
        parentColumnsRef.value = applyParentColumnSettings(newColumnSettings)
      } else {
        columnsRef.value = applyColumnSettings(newColumnSettings)
      }
    }

    const handleReset = () => {
      setSearchParams({}, [])
      tabPosition.value = props.eplusTableSchema.showTabs
        ? props.eplusTableSchema.tabs.filter((item) => item.isDefault).length > 0
          ? props.eplusTableSchema.tabs.filter((item) => item.isDefault)[0].label
          : props.eplusTableSchema.tabs.filter((item) => item.isTree).length > 0
            ? props.eplusTableSchema.tabs.filter((item) => item.isTree)[0].label
            : props.eplusTableSchema.tabs[0].label
        : null
      // handleRefresh()
    }
    const handleSearch = (model, list) => {
      tableObject.tableList = []
      setSearchParams(model, list || [])
      refreshPageItem()
    }
    const handleRefresh = () => {
      tableObject.tableList = []
      refresh()
      refreshPageItem()
    }

    /** 表格实例 */
    const { tableRegister, tableMethods, tableObject } = useTable({
      /** 此处为列表关联api */
      getListApi: async (ps) => {
        const res = await props.eplusTableSchema.getListApi(ps, tabIndex.value)
        // for (let i = 0; i < res.list.length; i++) {
        //   const it = res.list[i]
        //   if (it.children && it.children.length > 0) {
        //     for (let j = 0; j < it.children.length; j++) {
        //       it.children[j].parentId = it.id
        //     }
        //   }
        // }
        resultList.value = res.list
        res.list?.forEach((it) => {
          if (it.children && it.children.length > 0) {
            let childs: any[] = []
            it.children.forEach((cit, index) => {
              let child = cloneDeep(cit)
              child.parent = it
              child.parentId = it.id
              //child.totalAmountList = [{ amount: 100 + index}]
              childs.push(child)
            })
            it.children = childs
          } else {
            it.children = []
          }
        })
        return {
          list: res.list,
          sum: res.sum,
          total: res.total
        }
      },
      //删除
      delListApi: async (id) => {
        await props.eplusTableSchema.delListApi(id)
      },
      //导出
      exportListApi: async (ps) => {
        ps = { ...ps, isTree: isTreeRef.value, currentTabIndex: tabIndex.value }
        return props.eplusTableSchema.exportListApi
          ? await props.eplusTableSchema.exportListApi(ps)
          : ''
      }
    })

    //--保存当前页面和tableList缓存数据
    let currentPageObjs: any[] = []

    const getPageCount = (list: any[]) => {
      let length = list.length
      if (isTreeRef.value) {
        list.forEach((item) => {
          if (item.children) {
            length = length + item.children.length
          }
        })
      }
      return length
    }
    watch(
      () => tableObject.currentPage,
      (currentPage) => {
        tableObject.tableList = []
        let newItems: any[] = []
        for (let key of Object.keys(currentPageObjs)) {
          if (Number(key) != currentPage) {
            newItems[key] = currentPageObjs[key]
          }
        }
        currentPageObjs = newItems
      },
      { deep: true }
    )

    watch(
      () => tableObject.tableList,
      (tableList) => {
        if (tableList) {
          if (!Object.hasOwn(currentPageObjs, tableObject.currentPage)) {
            currentPageObjs[tableObject.currentPage] = {
              count: getPageCount(tableList)
            }
          }
        }
        resetCheckAllStatus()
      },
      { deep: true }
    )

    const { refresh, getList, setSearchParams, delList, exportList } = tableMethods

    const handleExport = () => {
      exportList(props.exportFileName)
    }
    const screenHeight = ref(0)
    onMounted(() => {
      getList()
      // setTimeout(() => {
      //   tableDataRef.value = getList()
      // }, 1000)
      // 获取屏幕高度
      screenHeight.value = window.innerHeight
      // 监听窗口大小变化，更新屏幕高度
      window.addEventListener('resize', () => {
        screenHeight.value = window.innerHeight
      })
    })

    const tableDivRef = shallowRef<HTMLElement>()
    const pageDivRef = shallowRef<HTMLElement>()
    const tableHeight = computed(() => {
      //-- 减去table margin : 20
      let height = screenHeight.value - 20
      if (tableDivRef.value) {
        //-- 减去距离顶部高度
        height = height - tableDivRef.value?.getBoundingClientRect().top
      }
      if (pageDivRef.value) {
        //-- 减去分页高度
        height = height - pageDivRef.value?.getBoundingClientRect().height
      }
      return height
    })

    onUnmounted(() => {
      window.removeEventListener('resize', () => {
        screenHeight.value = window.innerHeight
      })
    })

    interface SummaryMethodProps<T = any> {
      columns: TableColumnCtx<T>[]
      data: T[]
    }

    const getSummaries = (param: SummaryMethodProps) => {
      const { columns, data } = param
      const sums: (string | VNode)[] = []
      if (data && data.length > 0) {
        /**当前是否有选中的行 */
        columns.forEach((column, index) => {
          /**存在checkbox列，合计显示在第二列，否则显示在第一列*/
          let index0 = selectionRef.value ? 1 : 0
          let index0Content = (
            <el-row style="font-weight: bold;">
              <el-col>合计</el-col>
            </el-row>
          )
          let isChecked = selectionRef.value && checkedItems.value.length > 0
          if (isChecked) {
            index0Content = (
              <>
                <el-row style="border-bottom: 1px solid #ebeef5;">
                  <el-col style="font-weight: bold;">已选合计</el-col>
                </el-row>
                <el-row>
                  <el-col style="font-weight: bold;">合计</el-col>
                </el-row>
              </>
            )
          }
          if (index == index0) {
            sums[index0] = index0Content
            return
          }
          let showContent = ''
          //-- 当前列 summary配置为true 的对象
          let summaryObjByColumnsRef = columnsRef.value.filter(
            (it) => it.prop == column.property && it.summary
          )
          if (summaryObjByColumnsRef && summaryObjByColumnsRef.length > 0) {
            //--是否显示toolTips提示框 true:不显示，false:显示
            const tipsDisabled = ref()
            const sumCheckedTipsDisabled = ref()
            const tabHover = (event, type) => {
              const ev = event.target
              // 实际宽度
              const ev_weight = ev.scrollWidth
              // 可视宽度
              const content_weight = ev.clientWidth
              let disabled = ev_weight == content_weight ? false : true
              if (type == 'checked') {
                sumCheckedTipsDisabled.value = disabled
              } else {
                tipsDisabled.value = disabled
              }
            }
            let spanStyle =
              'overflow: hidden;text-overflow: ellipsis;white-space: nowrap;minWidth:' +
              column.minWidth +
              'px'
            //--合计
            let sumTotal = sumRef.value ? sumRef.value[column.property] : 0
            if (isChecked) {
              const toNumberCheckedItem = checkedItems.value.map((item) =>
                Number(item[column.property])
              )
              //--已选合计
              let sumChecked
              if (toNumberCheckedItem.every((value) => !Number.isNaN(value))) {
                //--数字类型合计
                sumChecked = `${toNumberCheckedItem.reduce((prev, curr) => {
                  return prev + curr
                }, 0)}`
              } else {
                //--对象类型合计
                if (summaryObjByColumnsRef[0].summarySlots) {
                  let items = checkedItems.value.map((item) => item[column.property])
                  //--单位区分合计结果临时存放数组
                  let unitSumArr: any[] = []
                  items.forEach((item) => {
                    // 过滤空值
                    if (!item) return
                    let itemsDefault = summaryObjByColumnsRef[0].summarySlots.default(item)
                    if (
                      itemsDefault &&
                      itemsDefault.number !== null &&
                      itemsDefault.number !== undefined &&
                      !isNaN(Number(itemsDefault.number))
                    ) {
                      if (itemsDefault.unit) {
                        //--根据单位区分合计
                        unitSumArr[itemsDefault.unit] = unitSumArr[itemsDefault.unit]
                          ? Number(unitSumArr[itemsDefault.unit]) + Number(itemsDefault.number)
                          : Number(itemsDefault.number)
                      } else {
                        //--不根据单位区分合计
                        sumChecked = sumChecked
                          ? Number(sumChecked) + Number(itemsDefault.number)
                          : Number(itemsDefault.number)
                      }
                    }
                  })
                  //结果用千分号分隔
                  if (Object.keys(unitSumArr).length > 0) {
                    for (let unit of Object.keys(unitSumArr)) {
                      let arrValue = formatNum(unitSumArr[unit], 3) + ' ' + unit
                      sumChecked = sumChecked ? sumChecked + ',' + arrValue : arrValue
                    }
                  } else {
                    sumChecked = formatNum(sumChecked, 3)
                  }
                } else {
                  sumChecked = 'N/A'
                }
              }
              showContent = (
                <>
                  <el-row style="border-bottom: 1px solid #ebeef5;">
                    {sumCheckedTipsDisabled.value ? (
                      <el-tooltip
                        content={sumChecked}
                        placement="top"
                        effect="dark"
                      >
                        <el-col>
                          <span
                            style={spanStyle}
                            onMousemove={(e) => {
                              tabHover(e, 'checked')
                            }}
                          >
                            {' '}
                            {sumChecked}
                          </span>
                        </el-col>
                      </el-tooltip>
                    ) : (
                      <el-col>
                        <span
                          style={spanStyle}
                          onMousemove={(e) => {
                            tabHover(e, 'checked')
                          }}
                        >
                          {' '}
                          {sumChecked}
                        </span>
                      </el-col>
                    )}
                  </el-row>
                  <el-row>
                    {tipsDisabled.value ? (
                      <el-tooltip
                        content={sumTotal}
                        placement="top"
                        effect="dark"
                      >
                        <el-col>
                          <span
                            style={spanStyle}
                            onMousemove={(e) => {
                              tabHover(e, 'unChecked')
                            }}
                          >
                            {' '}
                            {sumTotal}
                          </span>
                        </el-col>
                      </el-tooltip>
                    ) : (
                      <el-col>
                        <span
                          style={spanStyle}
                          onMousemove={(e) => {
                            tabHover(e, 'unChecked')
                          }}
                        >
                          {' '}
                          {sumTotal}
                        </span>
                      </el-col>
                    )}
                  </el-row>
                </>
              )
            } else {
              showContent = (
                <el-row>
                  {tipsDisabled.value ? (
                    <el-tooltip
                      content={sumTotal}
                      placement="top"
                      effect="dark"
                    >
                      <el-col>
                        <span
                          style={spanStyle}
                          onMousemove={(e) => {
                            tabHover(e, 'unChecked')
                          }}
                        >
                          {' '}
                          {sumTotal}
                        </span>
                      </el-col>
                    </el-tooltip>
                  ) : (
                    <el-col>
                      <span
                        style={spanStyle}
                        onMousemove={(e) => {
                          tabHover(e, 'unChecked')
                        }}
                      >
                        {' '}
                        {sumTotal}
                      </span>
                    </el-col>
                  )}
                </el-row>
              )
            }
          } else {
            if (isChecked) {
              showContent = (
                <>
                  <el-row style="border-bottom: 1px solid #ebeef5;"></el-row>
                  <el-row></el-row>
                </>
              )
            } else {
              showContent = (
                <>
                  <el-row></el-row>
                </>
              )
            }
          }
          sums[index] = showContent
        })
      }
      return sums
    }
    const saveLocalstorage = (val) => {
      localStorage.setItem('product', val)
    }
    const handleChangePicSize = (type) => {
      pictureBtn.value = type
      showPicturesRef.value = false
      switch (type) {
        case 'small':
          pictureSize.value = sizeJson['small']
          saveLocalstorage('small')
          break
        case 'middle':
          pictureSize.value = sizeJson['middle']
          saveLocalstorage('middle')
          break
        case 'big':
          pictureSize.value = sizeJson['big']
          saveLocalstorage('big')
          break
      }
      showPicturesRef.value = true
    }

    const selectAll = () => {
      nextTick(() => {
        checkAll.value = true
        allCheckChange(checkAll.value)
      })
    }
    return () => {
      let Table
      if (isTreeRef.value) {
        Table = renderTable()
      } else {
        Table = renderTableUnExpend()
      }
      const emitData = (e) => {
        emit('emit-open-detail', e?.id)
        emit('emit-row', e)
      }
      expose({
        handleRefresh,
        delList,
        exportList,
        handleSearch,
        handleReset,
        checkedItems,
        tableRef,
        isTreeRef,
        selectAll
      })
      const pictureColumns = props?.eplusTableSchema?.tabs?.filter(
        (item) => item.picture === true
      )[0]

      return (
        <>
          <div>
            {props?.eplusSearchSchema && (
              <div class="border-b-[--el-border-color-light] border-b-solid bg-[let(--el-fill-color-blank)] px-15px pb-14px pt-8px">
                <eplus-search
                  fields={props.eplusSearchSchema.fields}
                  moreFields={props.eplusSearchSchema.moreFields}
                  columns={props.eplusTableSchema.columns}
                  onSearch={handleSearch}
                  onReset={handleReset}
                />
              </div>
            )}
          </div>
          <div class="table-pane flex flex-1 flex-col px-15px pb-15px">
            <div class="flex flex-row items-center justify-between">
              <div class="mb10px mt10px flex">
                {slots?.tableActions?.()}
                {isTreeRef.value ? slots?.tableIsTreeActions?.() : slots?.tableDocActions?.()}
                {selectionRef.value && checkedItems.value.length ? (
                  <div
                    class="flex items-center"
                    style="margin-left:10px;color:#a6abb4;min-width:105px;font-size:13px"
                  >
                    已选{checkedItems.value.length}条数据
                  </div>
                ) : (
                  <div></div>
                )}
              </div>

              <div class="flex flex-row">
                {/* <ElButton
                  type="primary"
                  click="confirm"
                >
                  确定
                </ElButton> */}
                <div class="mb10px mt10px flex">
                  {props.eplusTableSchema?.showPictures &&
                  tabPosition.value === pictureColumns?.label ? (
                    <span class="pictures_btn_box">
                      <el-button
                        round
                        onClick={() => {
                          handleChangePicSize('small')
                        }}
                        class={`picture-size-btn-small ${pictureBtn.value === 'small' ? 'picture-btn-active' : ''} `}
                      >
                        小
                      </el-button>
                      <el-button
                        round
                        onClick={() => {
                          handleChangePicSize('middle')
                        }}
                        class={`picture-size-btn-middle ${pictureBtn.value === 'middle' ? 'picture-btn-active' : ''}`}
                      >
                        中
                      </el-button>
                      <el-button
                        round
                        onClick={() => {
                          handleChangePicSize('big')
                        }}
                        class={`picture-size-btn-big ${pictureBtn.value === 'big' ? 'picture-btn-active' : ''}`}
                      >
                        大
                      </el-button>
                    </span>
                  ) : (
                    slots?.tableActionsFixRight?.()
                  )}
                </div>
                {props.eplusTableSchema.showTabs ? (
                  <div style="display: flex;align-items:center;justify-content:center;margin:5px 15px 5px 15px;">
                    <el-radio-group v-model={tabPosition.value}>
                      {props.eplusTableSchema.tabs.map((item) => {
                        return <el-radio-button label={item.label}>{item.label}</el-radio-button>
                      })}
                    </el-radio-group>
                  </div>
                ) : (
                  <></>
                )}
                {/*  导出待处理*/}
                {props.eplusTableSchema?.showSettings == false ? (
                  <></>
                ) : (
                  <div style="display: flex;align-items:center;justify-content: center">
                    {props.exportFileName ? (
                      <ElTooltip
                        placement="top"
                        content="导出"
                      >
                        <div style="margin-left: 10px;display: flex;align-items:center;justify-content: center">
                          <ElButton
                            link
                            icon={<Download />}
                            onClick={handleExport}
                          />
                        </div>
                      </ElTooltip>
                    ) : (
                      ''
                    )}

                    <ElTooltip
                      placement="top"
                      content="刷新当前页面"
                    >
                      <div style="margin-left: 10px;display: flex;align-items:center;justify-content: center">
                        <ElButton
                          link
                          icon={<Refresh />}
                          onClick={handleRefresh}
                        />
                      </div>
                    </ElTooltip>
                  </div>
                )}

                {props.eplusTableSchema?.showPictures &&
                tabPosition.value === pictureColumns?.label ? (
                  <></>
                ) : props.eplusTableSchema?.showSettings == false ? (
                  <></>
                ) : (
                  <ElTooltip
                    placement="top"
                    content="列设置"
                  >
                    <div style="margin-left: 10px;display: flex;align-items:center;justify-content: center">
                      <EplusTableSetting
                        columnSettings={syncedColumnSettings.value}
                        parentColumnSettings={syncedParentColumnSettings.value}
                        onReset={handleResetSettings}
                        onSave={handleSaveSettings}
                        key={tabPosition.value}
                        tabIndex={tabIndex.value}
                        isTree={isTreeRef.value}
                      />
                    </div>
                  </ElTooltip>
                )}
              </div>
            </div>
            <div ref={tableDivRef}>
              {props.eplusTableSchema?.showPictures &&
              tabPosition.value === pictureColumns?.label ? (
                <EplusPictures
                  v-if={showPicturesRef.value}
                  list={resultList.value}
                  columns={pictureColumns?.columns}
                  onClickEmit={emitData}
                  size={pictureSize.value}
                  pictureBtn={pictureBtn.value}
                  height={props?.height ? props.height : tableHeight.value}
                ></EplusPictures>
              ) : (
                <Table></Table>
              )}
            </div>
            <div
              class="flex flex-row"
              ref={pageDivRef}
            >
              {props.eplusTableSchema?.showSettings == false ? (
                <></>
              ) : (
                <ElPagination
                  total={tableObject.total}
                  v-model:currentPage={tableObject.currentPage}
                  v-model:pageSize={tableObject.pageSize}
                  class="mt-10px !w-100% !font-size-14px"
                  background={false}
                  layout=" ->, total,prev, pager, next,jumper"
                  pageSizes={globalStore().glbPageSizes}
                  disabled={false}
                  hideOnSinglePage={false}
                ></ElPagination>
              )}
            </div>
          </div>
        </>
      )
    }
  }
})
</script>
<style lang="scss">
:deep(.el-table__body),
:deep(.el-table__header) {
  font-size: 14px !important;
}

.el-table__expand-icon {
  display: none !important;
}

.el-table .el-table__footer-wrapper {
  border-top: 1px solid #ebeef5;
}

.el-table .el-table__footer-wrapper tr td {
  background-color: white; /* 修改为你想要的颜色 */
  border: none !important;
  text-align: center;
}

.el-table .el-table__footer-wrapper tr td div {
  padding: 0;
}

.el-table .el-table__footer-wrapper .el-row {
  height: 35px;
  overflow: hidden;
  white-space: nowrap;
}

.el-table .el-table__footer-wrapper .el-row .el-col {
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-table__row--level-0 {
  font-size: 14px;
  height: 35px !important;
}

.el-table__row--level-1 {
  font-size: 14px;
  height: 35px;

  & .el-checkbox__input {
    position: relative;
  }
}

.el-table--default .el-table__cell {
  padding: 3px 0 !important;
}

.frame-table .el-table__cell {
  border-right: none;
}

.el-table__row .cell .el-table__placeholder {
  display: inline !important;
}

.el-table__row .cell .el-table__indent {
  padding-left: 0 !important;
}

.el-scrollbar {
  .el-scrollbar__bar.is-horizontal {
    display: flex !important;
    height: 12px; // 增加滚动条高度
  }

  // 横向滚动条
  .el-scrollbar__bar.is-horizontal .el-scrollbar__thumb {
    opacity: 1;
    height: 10px; // 增加滑块高度
    border-radius: 6px; // 增加圆角
    background-color: rgba(144, 147, 153, 0.8); // 调整背景色
    box-shadow: 0 0 6px rgba(144, 147, 153, 0.3); // 添加阴影效果
  }
}
</style>

<style lang="scss" scoped>
.table-row {
  justify-content: space-between;
  flex-wrap: nowrap;
}

.copyStyle,
.childrenCopy {
  visibility: hidden;
}

.table-row:hover {
  .copyStyle {
    visibility: visible;
  }
}

.el-table__row--level-1:hover {
  .childrenCopy {
    visibility: visible;
  }
}

.copyStyle:hover {
  color: #005bf5;
}

.childrenCopy:hover {
  color: #005bf5;
}

.row-left {
  display: flex;
  flex-wrap: wrap;
}

.row-right {
  width: 120px;
}

.el-radio-button__inner {
  background-color: #fff;
  // color: #409eff;
}

.el-radio-button__original-radio:checked + .el-radio-button__inner {
  color: #409eff !important;
  background-color: #fff !important;
}

.table-header-border .el-table__header-wrapper tr {
  border-left: 1px solid #d3dce6;
  color: #0b1019;
  font-size: 14px;
}

.headerStyle .el-table__cell .cell {
  color: #0b1019;
  font-size: 14px;
}

.headerStyle .el-table__cell:nth-of-type(n + 2) .cell {
  border-left: 2px solid #d9dbde;
}

.el-table__cell .cell {
  font-size: 14px;
}

.el-table .el-table__cell.is-center {
  // text-align: left
}

.tableUnExpend .el-table__body tr > td.el-table__cell {
  border-bottom: 1px solid #ebeef5;
}

.el-table__body tr > td.el-table__cell {
  border-bottom: 2px solid #ffff;
  border-top: 2px solid #ffff;
}

.el-table__body tr.hover-row > td.el-table__cell {
  background-color: #f2f8ff;
  border-bottom: 2px solid #ffff;
  border-top: 2px solid #ffff;
}

.row-code {
  color: #409eff;
  cursor: pointer;
}

.overflowSty {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.el-button--primary.is-link {
  font-size: 14px !important;
  margin-right: 20px;
}

.el-dropdown {
  font-size: 14px !important;
}

.picture-size-btn-middle,
.picture-size-btn-big,
.picture-size-btn-small {
  width: 30px;
  height: 30px;
  border-style: none;
  background-color: #f5f7f9;
  margin-left: 5px !important;
}

.picture-btn-active {
  color: #fff;
  background-color: #c7c7c7;
}

.pictures_btn_box .el-button:focus {
  background-color: #c7c7c7;
  color: #fff;
}

.pictures_btn_box .el-button:hover {
  background-color: #e2e2e2;
  color: #fff;
}
</style>
