<template>
  <el-popover
    ref="popoverRef"
    :show-arrow="false"
    :visible="visible"
    :offset="0"
    width="700"
    virtual-triggerin
  >
    <template #reference>
      <el-button
        link
        ref="buttonRef"
        :icon="Setting"
        @click="visible = true"
        v-click-outside="onClickOutside"
      />
    </template>
    <div class="flex flex-col">
      <div class="flex flex-col">
        <el-row style="margin-top: 30px">
          <el-col :span="16">
            <div class="checkbox-header">
              <div class="checkbox-header-title"> 报表数据 </div>
              <el-button
                round
                size="small"
                class="no-border-button"
                @click="checkedAllClick(`children`)"
              >
                {{ checkAllLabel }}
              </el-button>
            </div>
            <el-input
              v-model="searchLeftValue"
              style="width: 240px"
              size="small"
              placeholder="名称搜索"
              :suffix-icon="Search"
              @input="handleSearch(searchLeftValue)"
            />
            <div
              v-for="(item, i) in groupedItems"
              :key="i"
            >
              类型{{ i }}
              <el-checkbox-group
                v-model="checkedColumnLabels"
                @change="handleChange"
              >
                <el-row>
                  <el-col
                    v-for="col in item"
                    :key="col.prop"
                    :span="8"
                  >
                    <div>
                      <el-checkbox
                        :checked="!col.hide"
                        :label="col.label"
                        :disabled="col.disable || false"
                      />
                    </div>
                  </el-col>
                </el-row>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col
            :span="8"
            class="bl1 pl10px"
          >
            <column-config-table mode="children" />
          </el-col>
        </el-row>
      </div>

      <div
        class="flex flex-row justify-between pt10px"
        style="border-top: 1px solid #eee"
      >
        <div>
          <el-button @click="handleReset">恢复默认</el-button>
        </div>
        <div>
          <el-button @click="handleCancel">取消</el-button>
          <el-button @click="handleSave(true)">保存并应用</el-button>
        </div>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="tsx">
import { CircleClose, Search, Setting, Upload } from '@element-plus/icons-vue'
import { ref, unref, watch } from 'vue'
import {
  ClickOutside as vClickOutside,
  ElButton,
  ElImage,
  ElInput,
  ElTooltip,
  ScrollbarInstance
} from 'element-plus'
import { EplusColumnConfig } from '../index'
import { Sortable } from 'sortablejs-vue3'
import { cloneDeep } from 'lodash-es'
import fixSvg from '@/assets/svgs/fix.svg'
import unFixSvg from '@/assets/svgs/unfix.svg'
import * as JMReportApi from '@/api/report/jimu'

const pageKey = useRoute().path

/**点击外部关闭弹窗 */
const onClickOutside = (e: any) => {
  if (visible.value && !unref(popoverRef).popperRef.contentRef.contains(e.target)) {
    visible.value = false
  }
}
type Mode = 'parent' | 'children' | undefined
const mode = ref<Mode>()
const buttonRef = ref()
const popoverRef = ref()
const visible = ref(false)
const checkAllParentLabel = ref('取消全选')
const checkAllLabel = ref('取消全选')
const searchValue = ref('')
const searchLeftValue = ref('')
const fixedSortableRef = ref()
const unDisableFixedSortableRef = ref()
//滚动条
const scrollbarRef = ref<ScrollbarInstance>()
const props = defineProps({
  columnSettings: {
    type: Array as PropType<EplusColumnConfig[]>,
    required: true
  },
  parentColumnSettings: {
    type: Array as PropType<EplusColumnConfig[]>,
    required: true
  },
  isTree: {
    type: Boolean,
    required: true
  }
})

const columnSetData = ref(JSON.parse(JSON.stringify(props.columnSettings)))
const leftCheckData = ref()
const groupedItems = ref()
const emit = defineEmits(['reset', 'save', 'resetTabs'])
const checkedParentColumnLabels = ref<string[]>([])
const checkedColumnLabels = ref<string[]>([])
var checkedDefaultParentColumnLabels: string[] = []
var checkedDefaultColumnLabels: string[] = []
let parentColumnOrders = []
let columnOrders = []

columnSetData.value.for

const groupBy = (array, key) => {
  return array.reduce((result, currentItem) => {
    // 使用 key 的值作为分组的键
    const groupKey = currentItem[key]

    // 如果 result 中不存在这个键，则创建一个数组
    if (!result[groupKey]) {
      result[groupKey] = []
    }

    // 将当前项推入对应的分组数组中
    result[groupKey].push(currentItem)

    return result
  }, {})
}

groupedItems.value = groupBy(columnSetData.value, 'type')

var parentColumnSettings = cloneDeep(
  props.parentColumnSettings
    ? props.parentColumnSettings?.filter((col) => {
        return col.prop != 'action'
      })
    : []
)

let columnSettings = cloneDeep(
  props.columnSettings.filter((col) => {
    return col.prop != 'action'
  })
)
var parentActionColumnSettings = cloneDeep(
  props.parentColumnSettings
    ? props.parentColumnSettings?.filter((col) => {
        return col.prop === 'action'
      })
    : []
)
var actionColumnSettings = cloneDeep(
  props.columnSettings.filter((col) => {
    return col.prop === 'action'
  })
)

const handleChange = (val) => {}

const handleSearch = (query) => {
  if (query) {
    let data = columnSetData.value.filter((item) => item.label.includes(query))
    groupedItems.value = groupBy(data, 'type')

    return groupedItems.value
  } else {
    return (groupedItems.value = groupBy(columnSetData.value, 'type'))
  }
  // groupedItems.value = groupBy(columnSetData.value, 'type')

  // return checkedColumnLabels.value.filter((item) => item.includes(query))
}
// parentColumnSettings &&
//   parentColumnSettings.forEach((col) => {
//     if (!col.hide) {
//       checkedParentColumnLabels.value.push(col.label)
//     }
//   })
// checkedDefaultParentColumnLabels = checkedParentColumnLabels.value

//查询当前页面配置项
// getPreferences(pageKey).then((res) => {
//   if (res.children) {
//     let selectedCols = res.children.map((item) => item.prop)
//     columnSettings.forEach((item) => {
//       if (!selectedCols.includes(item.prop)) {
//         checkedColumnLabels.value.splice(checkedColumnLabels.value.indexOf(item.label), 1)
//       }
//     })
//   }
//   if (res.parent) {
//     let parentSelectedCols = res.parent.map((item) => item.prop)
//     parentColumnSettings.forEach((item) => {
//       if (!parentSelectedCols.includes(item.prop)) {
//         checkedParentColumnLabels.value.splice(
//           checkedParentColumnLabels.value.indexOf(item.label),
//           1
//         )
//       }
//     })
//   }
//   handleSave(false)
// })

if (parentColumnSettings && parentColumnSettings.length > 0) {
  mode.value = 'parent'
}
const checkedParentColumnOrderSettings = computed(() => {
  let result: any[] = []
  checkedParentColumnLabels.value.forEach((label) => {
    result.push(
      parentColumnSettings.filter((col) => {
        return label == col.label
      })[0]
    )
  })
  result = [
    ...result.filter((col) => {
      if (col) {
        return col.disable || col.fixed === true || col.fixed === 'left'
      }
    }),
    ...result.filter((col) => {
      if (col) {
        return !(col.disable || col.fixed === true || col.fixed === 'left')
      }
    })
  ]
  return result
})
const checkedColumnOrderSettings = computed(() => {
  let result: any[] = []
  checkedColumnLabels.value.forEach((label) => {
    result.push(
      columnSettings.filter((col) => {
        return label == col.label
      })[0]
    )
  })
  result = [
    ...result.filter((col) => {
      if (col) {
        return col.disable || col.fixed === true || col.fixed === 'left'
      }
    }),
    ...result.filter((col) => {
      if (col) {
        return !(col.disable || col.fixed === true || col.fixed === 'left')
      }
    })
  ]
  return result
})
const checkedParentColumnSettings = computed(() => {
  return parentColumnSettings.filter((col) => {
    return checkedParentColumnLabels.value.includes(col.label)
  })
})
const checkedColumnSettings = computed(() => {
  return columnSettings.filter((col) => {
    return checkedColumnLabels.value.includes(col.label)
  })
})
const checkedWithDisableParentColumnSettings = computed(() => {
  return checkedParentColumnOrderSettings.value.filter((col) => {
    return col.disable
  })
})
const checkedWithFixedParentColumnSettings = computed(() => {
  return checkedParentColumnOrderSettings.value.filter((col) => {
    return !col.disable && (col.fixed === true || col.fixed === 'left')
  })
})
const checkedWithDisableFixedParentColumnSettings = computed(() => {
  return checkedParentColumnOrderSettings.value.filter((col) => {
    return col.disable || col.fixed === true || col.fixed === 'left'
  })
})
const checkedWithOutDisableFixedParentColumnSettings = computed(() => {
  return checkedParentColumnOrderSettings.value.filter((col) => {
    return !(col.disable || col.fixed === true || col.fixed === 'left')
  })
})
const checkedWithDisableColumnSettings = computed(() => {
  return checkedColumnOrderSettings.value.filter((col) => {
    return col.disable
  })
})
const checkedWithFixedColumnSettings = computed(() => {
  return checkedColumnOrderSettings.value.filter((col) => {
    return !col.disable && (col.fixed === true || col.fixed === 'left')
  })
})
const checkedWithDisableFixedColumnSettings = computed(() => {
  return checkedColumnOrderSettings.value.filter((col) => {
    return col.disable || col.fixed === true || col.fixed === 'left'
  })
})
const checkedWithOutDisableFixedColumnSettings = computed(() => {
  return checkedColumnOrderSettings.value.filter((col) => {
    return !(col.disable || col.fixed === true || col.fixed === 'left')
  })
})
const checkedDefaultWithDisableParentColumnLabels = computed(() => {
  return checkedParentColumnOrderSettings.value
    .filter((col) => {
      return col.disable
    })
    .map((it) => it.label)
})
const checkedDefaultWithFixedParentColumnLabels = computed(() => {
  return checkedParentColumnOrderSettings.value
    .filter((col) => {
      return !col.disable && (col.fixed === true || col.fixed === 'left')
    })
    .map((it) => it.label)
})
const checkedDefaultWithDisableFixedParentColumnLabels = computed(() => {
  return checkedParentColumnOrderSettings.value
    .filter((col) => {
      return col.disable || col.fixed === true || col.fixed === 'left'
    })
    .map((it) => it.label)
})
const checkedDefaultWithOutDisableFixedParentColumnLabels = computed(() => {
  return checkedParentColumnOrderSettings.value
    .filter((col) => {
      return !(col.disable || col.fixed === true || col.fixed === 'left')
    })
    .map((it) => it.label)
})
const checkedDefaultWithDisableColumnLabels = computed(() => {
  return checkedColumnOrderSettings.value
    .filter((col) => {
      return col.disable
    })
    .map((it) => it.label)
})
const checkedDefaultWithFixedColumnLabels = computed(() => {
  return checkedColumnOrderSettings.value
    .filter((col) => {
      return !col.disable && (col.fixed === true || col.fixed === 'left')
    })
    .map((it) => it.label)
})
const checkedDefaultWithDisableFixedColumnLabels = computed(() => {
  return checkedColumnOrderSettings.value
    .filter((col) => {
      return col.disable || col.fixed === true || col.fixed === 'left'
    })
    .map((it) => it.label)
})
const checkedDefaultWithOutDisableFixedColumnLabels = computed(() => {
  return checkedColumnOrderSettings.value
    .filter((col) => {
      return !(col.disable || col.fixed === true || col.fixed === 'left')
    })
    .map((it) => it.label)
})
const checkedWithDisableFixedParentColumnNum = ref(0)
const checkedWithDisableFixedColumnNum = ref(0)

const handleReset = () => {
  // console.log(props.columnSettings)
  checkedParentColumnLabels.value = []
  checkedParentColumnLabels.value = props.parentColumnSettings.map((item: any) => {
    if (item.prop != 'action') {
      return item.label
    }
  })

  checkedColumnLabels.value = []
  checkedColumnLabels.value = props.columnSettings.map((item: any) => {
    if (item.prop != 'action') {
      return item.label
    }
  })

  parentColumnSettings = cloneDeep(
    props.parentColumnSettings.filter((col) => {
      return col.prop != 'action'
    })
  )
  columnSettings = cloneDeep(
    props.columnSettings.filter((col) => {
      return col.prop != 'action'
    })
  )
  handleSave(true)
  // emit('reset')
  // visible.value = false
}

const handleCancel = () => {
  visible.value = false
  searchValue.value = ''
}

const syncColumnOrder = (checkedColumnSettings: EplusColumnConfig[], orderedProps: string[]) => {
  const orderedColumnSettings = checkedColumnSettings.sort((a, b) => {
    return orderedProps.indexOf(a.prop) - orderedProps.indexOf(b.prop)
  })
  return orderedColumnSettings
}

const handleSave = async (flag = true) => {
  let orderedParentOrderColumnSettings = syncColumnOrder(
    checkedParentColumnOrderSettings.value,
    parentColumnOrders
  )
  orderedParentOrderColumnSettings = [
    ...orderedParentOrderColumnSettings,
    ...parentActionColumnSettings
  ]
  let orderedColumnOrderSettings = syncColumnOrder(checkedColumnOrderSettings.value, columnOrders)
  orderedColumnOrderSettings = [...orderedColumnOrderSettings, ...actionColumnSettings]

  visible.value = false
  searchValue.value = ''

  emit('save', 'parent', orderedParentOrderColumnSettings)
  emit('save', 'children', orderedColumnOrderSettings)
  if (flag) {
    let reportIds = orderedColumnOrderSettings.map((obj) => obj.id)
    await JMReportApi.batchCreate({ reportIds: reportIds })
    emit('resetTabs')
  }
}
const handleTabClick = () => {
  searchValue.value = ''
}

const checkedAllClick = (value) => {
  var checkeLabels: string[] = []

  //--子全选
  if (checkedColumnLabels.value.length < checkedDefaultColumnLabels.length) {
    checkAllLabel.value = '取消全选'
    checkeLabels = columnSettings.map((it) => it.label)
  } else {
    checkAllLabel.value = '全选'
    checkeLabels = columnSettings
      .filter((item) => {
        return item.disable
      })
      .map((it) => it.label)
  }
  checkedColumnLabels.value = checkeLabels
}

const ColumnConfigTable = (props: { mode?: Mode }) => {
  let list = ref<EplusColumnConfig[]>([])
  list.value =
    mode.value === 'parent'
      ? checkedParentColumnOrderSettings.value
      : checkedColumnOrderSettings.value
  let disabledList = ref<EplusColumnConfig[]>([])
  disabledList.value =
    mode.value === 'parent'
      ? checkedWithDisableParentColumnSettings.value
      : checkedWithDisableColumnSettings.value
  let fixedList = ref<EplusColumnConfig[]>([])
  fixedList.value =
    mode.value === 'parent'
      ? checkedWithFixedParentColumnSettings.value
      : checkedWithFixedColumnSettings.value
  let unDisableFixedList = ref<EplusColumnConfig[]>([])
  unDisableFixedList.value =
    mode.value === 'parent'
      ? checkedWithOutDisableFixedParentColumnSettings.value
      : checkedWithOutDisableFixedColumnSettings.value
  const handleRemove = (item: EplusColumnConfig) => {
    if (props.mode === 'parent') {
      const index = checkedParentColumnLabels.value.findIndex(
        (label) => label === item.element.label
      )
      checkedParentColumnLabels.value.splice(index, 1)
    } else {
      const index = checkedColumnLabels.value.findIndex((label) => label === item.element.label)
      checkedColumnLabels.value.splice(index, 1)
    }
  }

  const handleTop = (item: EplusColumnConfig) => {
    if (props.mode === 'parent') {
      if (item.element.fixed) {
        var checkedWithFixedParentColumnLabels = checkedParentColumnLabels.value.filter((label) => {
          return checkedDefaultWithFixedParentColumnLabels.value.includes(label)
        })
        const index = checkedWithFixedParentColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithFixedParentColumnLabels.splice(index, 1)
        checkedWithFixedParentColumnLabels.unshift(item.element.label)
        checkedParentColumnLabels.value = [
          ...checkedDefaultWithDisableParentColumnLabels.value,
          ...checkedWithFixedParentColumnLabels,
          ...checkedDefaultWithOutDisableFixedParentColumnLabels.value
        ]
      } else {
        var checkedWithOutDisableParentColumnLabels = checkedParentColumnLabels.value.filter(
          (label) => {
            return checkedDefaultWithOutDisableFixedParentColumnLabels.value.includes(label)
          }
        )
        const index = checkedWithOutDisableParentColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithOutDisableParentColumnLabels.splice(index, 1)
        checkedWithOutDisableParentColumnLabels.unshift(item.element.label)
        checkedParentColumnLabels.value = [
          ...checkedDefaultWithDisableFixedParentColumnLabels.value,
          ...checkedWithOutDisableParentColumnLabels
        ]
      }
    } else {
      if (item.element.fixed) {
        var checkedWithFixedColumnLabels = checkedColumnLabels.value.filter((label) => {
          return checkedDefaultWithFixedColumnLabels.value.includes(label)
        })
        const index = checkedWithFixedColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithFixedColumnLabels.splice(index, 1)
        checkedWithFixedColumnLabels.unshift(item.element.label)
        checkedColumnLabels.value = [
          ...checkedDefaultWithDisableColumnLabels.value,
          ...checkedWithFixedColumnLabels,
          ...checkedDefaultWithOutDisableFixedColumnLabels.value
        ]
      } else {
        var checkedWithOutDisableColumnLabels = checkedColumnLabels.value.filter((label) => {
          return checkedDefaultWithOutDisableFixedColumnLabels.value.includes(label)
        })
        const index = checkedWithOutDisableColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithOutDisableColumnLabels.splice(index, 1)
        checkedWithOutDisableColumnLabels.unshift(item.element.label)
        checkedColumnLabels.value = [
          ...checkedDefaultWithDisableFixedColumnLabels.value,
          ...checkedWithOutDisableColumnLabels
        ]
      }
    }
  }

  const handleFix = (item: EplusColumnConfig) => {
    if (props.mode === 'parent') {
      const checkedWithDisableFixedParentColumnLabels =
        checkedDefaultWithDisableFixedParentColumnLabels.value
      const checkedWithOutDisableFixedParentColumnLabels =
        checkedDefaultWithOutDisableFixedParentColumnLabels.value
      if (
        item.element.fixed === 'left' ||
        item.element.fixed === 'right' ||
        item.element.fixed === true
      ) {
        item.element.fixed = false
        const index = checkedWithDisableFixedParentColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithDisableFixedParentColumnLabels.splice(index, 1)
        checkedWithOutDisableFixedParentColumnLabels.unshift(item.element.label)
      } else {
        item.element.fixed = true
        checkedWithDisableFixedParentColumnLabels.push(item.element.label)
        const index = checkedWithOutDisableFixedParentColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithOutDisableFixedParentColumnLabels.splice(index, 1)
      }
      checkedParentColumnLabels.value = [
        ...checkedWithDisableFixedParentColumnLabels,
        ...checkedWithOutDisableFixedParentColumnLabels
      ]
      checkedWithDisableFixedParentColumnNum.value =
        checkedWithDisableFixedParentColumnLabels.length
    } else {
      const checkedWithDisableFixedColumnLabels = checkedDefaultWithDisableFixedColumnLabels.value
      const checkedWithOutDisableFixedColumnLabels =
        checkedDefaultWithOutDisableFixedColumnLabels.value
      if (
        item.element.fixed === 'left' ||
        item.element.fixed === 'right' ||
        item.element.fixed === true
      ) {
        item.element.fixed = false
        const index = checkedWithDisableFixedColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithDisableFixedColumnLabels.splice(index, 1)
        checkedWithOutDisableFixedColumnLabels.unshift(item.element.label)
      } else {
        item.element.fixed = true
        checkedWithDisableFixedColumnLabels.push(item.element.label)
        const index = checkedWithOutDisableFixedColumnLabels.findIndex(
          (label) => label === item.element.label
        )
        checkedWithOutDisableFixedColumnLabels.splice(index, 1)
      }
      checkedColumnLabels.value = [
        ...checkedWithDisableFixedColumnLabels,
        ...checkedWithOutDisableFixedColumnLabels
      ]
      checkedWithDisableFixedColumnNum.value = checkedWithDisableFixedColumnLabels.length
    }
  }

  const handleUpdate = () => {
    const fixedOrderedProps = fixedSortableRef.value?.sortable?.toArray()
    const unDisableFixedOrderedProps = unDisableFixedSortableRef.value?.sortable?.toArray()
    if (props.mode === 'parent') {
      parentColumnOrders = fixedOrderedProps.concat(unDisableFixedOrderedProps)
    } else {
      columnOrders = fixedOrderedProps.concat(unDisableFixedOrderedProps)
    }
  }

  // 左侧搜索
  const handleTypeSearch = (v) => {
    if (v.keyCode == '13') {
      let templist: any = []
      list.value.forEach((it: EplusColumnConfig) => {
        if (searchValue.value && it.label.indexOf(searchValue.value) >= 0) {
          it.isSearched = true
        } else {
          it.isSearched = false
        }
        templist.push(it)
      })
      var searched = sortTabListDom.filter((item) => {
        return item.textContent && item.textContent.indexOf(searchValue.value) >= 0
      })
      if (searched && searched.length > 0) {
        // 滚动到选中的第一项
        scrollbarRef.value?.setScrollTop(searched[0].offsetTop - 35)
      }
      list.value = templist
    }
  }

  // 右侧搜索
  const handleSearch = (v) => {
    if (v.keyCode == '13') {
      let templist: any = []
      list.value.forEach((it: EplusColumnConfig) => {
        if (searchValue.value && it.label.indexOf(searchValue.value) >= 0) {
          it.isSearched = true
        } else {
          it.isSearched = false
        }
        templist.push(it)
      })
      var searched = sortTabListDom.filter((item) => {
        return item.textContent && item.textContent.indexOf(searchValue.value) >= 0
      })
      if (searched && searched.length > 0) {
        // 滚动到选中的第一项
        scrollbarRef.value?.setScrollTop(searched[0].offsetTop - 35)
      }
      list.value = templist
    }
  }

  //

  //-- 将Sortable label节点 dom元素放到数组里面
  let sortTabListDom: HTMLInputElement[] = []
  const getSortTabList = (el) => {
    sortTabListDom.push(el)
  }

  return (
    <div class="test">
      <ElInput
        v-model={searchValue.value}
        size="small"
        placeholder="搜索"
        suffix-icon={Search}
        onKeydown={handleSearch}
        style="height:25px;"
      />
      <div style="margin-top:10px;height:100%;">
        <div style="color:#9999;">最多可固定7项</div>
        <el-scrollbar
          ref={scrollbarRef}
          style="margin-top:10px;height: 100%"
          wrap-style="overflow-x:hidden;"
        >
          <div style="max-height: 300px;margin-right:20px;">
            <Sortable
              list={disabledList.value}
              tag="div"
              itemKey="prop"
            >
              {{
                item: (item) => {
                  return (
                    <el-row
                      gutter={20}
                      data-id={item.element.prop}
                      type="flex"
                      justify="space-between"
                      style="margin-top:8px;margin-bottom:8px"
                    >
                      <el-col>
                        <div
                          ref={getSortTabList}
                          style={item.element.isSearched ? 'color:#409eff;font-weight: bold' : ''}
                        >
                          {item.element.label}
                        </div>
                      </el-col>
                    </el-row>
                  )
                }
              }}
            </Sortable>
            <Sortable
              ref={fixedSortableRef}
              list={fixedList.value}
              tag="div"
              itemKey="prop"
              onUpdate={handleUpdate}
            >
              {{
                item: (item) => {
                  return (
                    <el-row
                      gutter={20}
                      data-id={item.element.prop}
                      type="flex"
                      justify="space-between"
                      style="margin-top:8px;margin-bottom:8px"
                    >
                      <el-col
                        span={6}
                        style="width:90px"
                      >
                        <div
                          ref={getSortTabList}
                          style={item.element.isSearched ? 'color:#409eff;font-weight: bold' : ''}
                        >
                          {item.element.label}
                        </div>
                      </el-col>
                      <el-col span={6}>
                        <div>
                          {!item.element.disable ? (
                            <>
                              <ElButton
                                link
                                icon={<CircleClose />}
                                onClick={() => handleRemove(item)}
                              />
                              <ElTooltip
                                placement="top"
                                content="置顶"
                              >
                                <ElButton
                                  link
                                  icon={<Upload />}
                                  onClick={() => handleTop(item)}
                                />
                              </ElTooltip>
                              <ElTooltip
                                placement="top"
                                content="取消固定"
                              >
                                <ElImage
                                  style="width:1em;height:1em;padding:3px;vertical-align:bottom;margin-left:10px"
                                  link
                                  src={unFixSvg}
                                  onClick={() => handleFix(item)}
                                />
                              </ElTooltip>
                            </>
                          ) : (
                            ''
                          )}
                        </div>
                      </el-col>
                    </el-row>
                  )
                }
              }}
            </Sortable>
            <Sortable
              ref={unDisableFixedSortableRef}
              list={unDisableFixedList.value}
              tag="div"
              itemKey="prop"
              onUpdate={handleUpdate}
            >
              {{
                item: (item) => {
                  return (
                    <el-row
                      gutter={20}
                      data-id={item.element.prop}
                      type="flex"
                      justify="space-between"
                      style="margin-top:8px;margin-bottom:8px"
                    >
                      <el-col
                        span={6}
                        style="width:90px"
                      >
                        <div
                          ref={getSortTabList}
                          style={item.element.isSearched ? 'color:#409eff;font-weight: bold' : ''}
                        >
                          {item.element.label}
                        </div>
                      </el-col>
                      <el-col span={6}>
                        <div>
                          {!item.element.disable ? (
                            <>
                              <ElButton
                                link
                                icon={<CircleClose />}
                                onClick={() => handleRemove(item)}
                              />
                              <ElTooltip
                                placement="top"
                                content="置顶"
                              >
                                <ElButton
                                  link
                                  icon={<Upload />}
                                  onClick={() => handleTop(item)}
                                />
                              </ElTooltip>
                              <ElTooltip
                                placement="top"
                                content="固定"
                              >
                                <ElImage
                                  link
                                  style="width:1em;height:1em;padding:3px;vertical-align:bottom;margin-left:10px"
                                  src={fixSvg}
                                  disabled={
                                    props.mode === 'parent'
                                      ? checkedWithDisableFixedParentColumnNum.value >= 7
                                      : checkedWithDisableFixedColumnNum.value >= 7
                                  }
                                  onClick={() => handleFix(item)}
                                />
                              </ElTooltip>
                            </>
                          ) : (
                            ''
                          )}
                        </div>
                      </el-col>
                    </el-row>
                  )
                }
              }}
            </Sortable>
          </div>
        </el-scrollbar>
      </div>
    </div>
  )
}

onMounted(() => {})

// watch(
//   () => visible.value,
//   (value) => {
//     if (value === true) {
//       // 监听document的点击事件
//       document.addEventListener('click', clickClose)
//     } else {
//       document.removeEventListener('click', clickClose)
//     }
//   }
// )

// const clickClose = (event) => {
//   visible.value = false
// }

watch(
  () => checkedColumnLabels.value,
  (value) => {
    if (value.length === checkedDefaultColumnLabels.length) {
      checkAllLabel.value = '取消全选'
    } else {
      checkAllLabel.value = '全选'
    }
  },
  { deep: true }
)

watch(
  () => checkedParentColumnLabels.value,
  (value) => {
    if (value.length === checkedDefaultParentColumnLabels.length) {
      checkAllParentLabel.value = '取消全选'
    } else {
      checkAllParentLabel.value = '全选'
    }
  },
  { deep: true }
)
</script>
<style lang="scss" scoped>
$prefix-cls: eplus-table-settings;

.el-checkbox {
  padding-left: 10%;
  white-space: break-spaces;
  word-break: break-all;
}

.handle {
  cursor: move;
  cursor: -webkit-grabbing;
}

.#{$prefix-cls} {
  &.el-popover {
    &.el-popper {
      padding: 0;
    }
  }
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}

.bl1 {
  border-left: 1px solid #eee;
}

.el-tabs__header {
  margin: 0 !important;
}

.checkbox-header {
  display: -webkit-box;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
}

.checkbox-header-title {
  font-weight: bold;
  font-size: 14px;
  margin-right: 5px;
}

.no-border-button {
  border: none !important;
  box-shadow: none !important;
  font-size: 14px;
}

.test {
  width: 100%;
  margin: 0 auto;
}
</style>
