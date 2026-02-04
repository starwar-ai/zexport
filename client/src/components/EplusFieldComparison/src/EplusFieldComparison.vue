<template>
  <div v-if="sameFlag">
    <span v-if="valType === 'money'">
      <EplusMoneyLabel :val="newVal" />
    </span>
    <span v-else>{{ newVal }}</span>
  </div>
  <div v-else>
    <template v-if="valType === 'money'">
      <span
        class="newVal"
        v-if="newVal != ' '"
      >
        <EplusMoneyLabel :val="newVal" />
      </span>
      <br v-if="newVal != ' '" />
      <span
        class="oldVal"
        v-if="oldVal != ' '"
      >
        <EplusMoneyLabel :val="oldVal" />
      </span>
    </template>
    <template v-else>
      <span
        class="newVal"
        v-if="newVal && newVal !== ' '"
        >{{ newVal }}</span
      >
      <br v-if="newVal && newVal !== ' '" />
      <span class="oldVal">{{ oldVal }}</span>
    </template>
  </div>
</template>

<script setup lang="tsx">
import { EplusMoneyLabel } from '@/components/EplusMoney'

const props = defineProps<{
  item: object | any
  filed: string
  type?: any
  formatter?: any
  oldChangeField?: any
  oldChildren?: any
}>()

let sameFlag = ref(false),
  newVal: any = ref(),
  oldVal: any = ref(),
  valType = ref('')

watchEffect(() => {
  let newStr = props.filed
  let type = typeof props?.item[newStr]
  let oldType = typeof props?.item?.[props.oldChangeField]?.[newStr]

  if (props.item?.changeFlag || props.item?.changeFlag === null) {
    switch (props.item?.changeFlag) {
      case 1:
        sameFlag.value = true
        if (type === 'object' && props.item?.[newStr]) {
          if (
            props.item?.[newStr]?.currency ||
            (props.item?.[newStr] &&
              Object.prototype.hasOwnProperty.call(props.item?.[newStr], 'currency'))
          ) {
            valType.value = 'money'
            newVal = props.item?.[newStr]
          } else if (props.item?.[newStr]?.unit) {
            newVal = `${props.item?.[newStr]?.weight} ${props.item?.[newStr]?.unit}`
          } else {
            newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr]
          }
        } else {
          newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr]
        }

        break
      case 2:
        sameFlag.value = false
        const newItem = props.item?.[newStr]

        if (
          (newItem && newItem?.currency) ||
          (newItem && Object.prototype.hasOwnProperty.call(newItem, 'currency'))
        ) {
          valType.value = 'money'
          newVal = newItem
          oldVal = ' '
        } else if (newItem && newItem?.unit) {
          newVal = `${newItem?.weight} ${newItem?.unit}`
          oldVal = ' '
        } else {
          // 为null 的情况
          newVal = newItem == null ? '-' : props.formatter ? props.formatter(newItem) : newItem
          oldVal = ' '
        }

        break
      case 3:
        const oldChild = props.item?.oldChildren?.[0]?.[newStr]
        const newChild = props.item?.[newStr]

        if (newChild === '' || newChild === null) {
          sameFlag.value = true
          newVal = props.formatter ? props.formatter(oldChild) : oldChild
          return null
        } else if (type === 'object' && newChild) {
          if (
            newChild?.currency ||
            (newChild && Object.prototype.hasOwnProperty.call(newChild, 'currency'))
          ) {
            valType.value = 'money'

            if (
              oldChild != null &&
              (newChild?.amount != oldChild?.amount || newChild?.currency != oldChild?.currency)
            ) {
              sameFlag.value = false
              newVal = newChild
              oldVal = oldChild
            } else {
              sameFlag.value = true
              newVal = newChild
            }
          } else if (newChild?.unit) {
            if (
              oldChild != null &&
              (newChild?.weight != oldChild?.weight || newChild?.unit != oldChild?.unit)
            ) {
              sameFlag.value = false
              newVal = `${newChild?.weight} ${newChild?.unit}`
              oldVal = `${oldChild?.weight} ${oldChild?.unit}`
            } else {
              sameFlag.value = true
              newVal = `${newChild?.weight} ${newChild?.unit}`
            }
          } else {
            sameFlag.value = true
            newVal = props.formatter ? props.formatter(newChild) : newChild
          }
        } else {
          if (newChild === oldChild) {
            sameFlag.value = true
            newVal = props.formatter ? props.formatter(newChild) : newChild
            return null
          } else {
            sameFlag.value = false
            newVal = props.formatter ? props.formatter(newChild) : newChild
            oldVal = props.formatter ? props.formatter(oldChild) : oldChild
          }
        }
        break
      case 4:
        sameFlag.value = false
        // 被删除的列表价格展示处理
        if (props.item[newStr]?.amount || props.item[newStr]?.amount === 0) {
          valType.value = 'money'
          oldVal = props.item[newStr]
        } else if (props.item[newStr]?.unit) {
          oldVal = `${props.item[newStr]?.weight} ${props.item[newStr]?.unit}`
        } else {
          oldVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr]
        }
        break
      default:
        break
    }
    return null
  } else if (
    (props.item[newStr] == null || props.item[newStr] == '' || props.item[newStr] == 'null') &&
    props.item[newStr] != 0
  ) {
    sameFlag.value = true
    newVal = props.formatter
      ? props.formatter(props?.item?.[props?.oldChangeField][newStr])
      : props.formatter == undefined
        ? ''
        : props?.item?.[props?.oldChangeField][newStr]
    return null
  } else if (
    (type === 'object' && props.item[newStr]) ||
    (oldType === 'object' && props?.item?.[props.oldChangeField]?.[newStr])
  ) {
    if (
      Object.prototype.hasOwnProperty.call(props.item[newStr], 'currency') ||
      props?.item?.[props.oldChangeField]?.[newStr]?.currency
    ) {
      valType.value = 'money'

      if (
        props.item?.[props?.oldChangeField] &&
        props.item?.[props?.oldChangeField][newStr]?.amount != props.item[newStr].amount
        // props.item?.[props?.oldChangeField][newStr]?.currency != props.item[newStr].currency
      ) {
        sameFlag.value = false
        newVal = props.item[newStr]
        oldVal = props.item?.[props?.oldChangeField][newStr]
      } else if (props.item[newStr] && props.item?.[props?.oldChangeField] == undefined) {
        // 针对新添加的数据
        sameFlag.value = false
        newVal = props.item[newStr]
        oldVal = ' '
      } else {
        sameFlag.value = true
        // newVal = `${props.item[newStr]?.amount} ${props.item[newStr]?.currency}`
        newVal = props.item[newStr]
      }
    } else if (props.item[newStr]?.unit || props?.item?.[props.oldChangeField]?.[newStr]?.unit) {
      if (
        props.item?.[props?.oldChangeField] &&
        props.item?.[props?.oldChangeField][newStr]?.weight != props.item[newStr]?.weight
        // props.item?.[props?.oldChangeField][newStr]?.unit != props.item[newStr]?.unit
      ) {
        sameFlag.value = false
        newVal = props.item[newStr]?.weight
          ? `${props.item[newStr]?.weight} ${props.item[newStr]?.unit}`
          : ' '
        oldVal = props.item?.[props?.oldChangeField][newStr]?.weight
          ? `${props.item?.[props?.oldChangeField][newStr]?.weight} ${props.item?.[props?.oldChangeField][newStr]?.unit}`
          : ' '
      } else if (props.item[newStr] && props.item?.[props?.oldChangeField] == undefined) {
        // 针对新添加的重量数据
        sameFlag.value = false
        newVal = props.item[newStr]?.weight
          ? `${props.item[newStr]?.weight} ${props.item[newStr]?.unit}`
          : ''
        oldVal = ' '
      } else {
        sameFlag.value = true
        newVal = props.item[newStr]?.weight
          ? `${props.item[newStr]?.weight} ${props.item[newStr]?.unit}`
          : '-'
      }
    } else if (props.item[newStr]?.userId) {
      if (props.item[newStr]?.userId != props.item?.[props?.oldChangeField][newStr]?.userId) {
        sameFlag.value = false
        newVal = props.formatter
          ? props.formatter(props.item[newStr])
          : props.item[newStr]?.nickname
        oldVal = props.formatter
          ? props.formatter(props.item?.[props?.oldChangeField][newStr])
          : props.item?.[props?.oldChangeField][newStr].nickname
      } else {
        sameFlag.value = true
        newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr].nickname
      }
    } else {
      sameFlag.value = true
      newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr]
    }
  } else if (type === 'string' || type === 'number') {
    if (props.item && props.item?.[props?.oldChangeField]) {
      if (
        props.item[newStr] &&
        props.item?.[props?.oldChangeField]?.[newStr] &&
        props.item[newStr] != props.item?.[props?.oldChangeField]?.[newStr]
      ) {
        sameFlag.value = false
        newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr]
        oldVal = props.formatter
          ? props.formatter(props.item?.[props?.oldChangeField][newStr])
          : props.item?.[props?.oldChangeField][newStr]
      } else {
        sameFlag.value = true
        newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr] || '-'
      }
    } else {
      sameFlag.value = false
      newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr]
      oldVal = ''
    }
  } else if (!props.item[newStr]) {
    return '-'
  } else {
    sameFlag.value = true
    newVal = props.formatter ? props.formatter(props.item[newStr]) : props.item[newStr]
  }
})

// const moneyCompare = () => {
//   return props.item[props.filed] - props.item[`old_${props.filed}`]
// }
</script>
<style lang="scss" scoped>
.newVal {
  color: #f7aa49;
}

.oldVal {
  color: #999;
  text-decoration: line-through;
}
</style>
