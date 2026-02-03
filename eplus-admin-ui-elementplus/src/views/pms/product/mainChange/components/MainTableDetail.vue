<template>
  <eplus-description
    title="配件信息"
    :data="pageInfo"
    :items="accessoriesListInfo"
    v-if="isValidArray(pageInfo?.accessoriesList)"
  >
    <template #accessoriesList>
      <Table
        style="width: 100%"
        :columns="accessoriesListColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.accessoriesList"
      />
    </template>
  </eplus-description>

  <eplus-description
    title="组合产品信息"
    :data="pageInfo"
    :items="subProductListInfo"
    v-if="isValidArray(pageInfo?.subProductList)"
  >
    <template #singleProcessFee> {{ pageInfo.singleProcessFee }} RMB </template>
    <template #subProductList>
      <Table
        style="width: 100%"
        :columns="subProductListColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.subProductList"
      />
    </template>
  </eplus-description>
  <eplus-description
    title="包材配比"
    :data="pageInfo"
    :items="auxiliaryInfo"
  >
    <template #skuAuxiliaryList>
      <Table
        v-if="isValidArray(pageInfo?.skuAuxiliaryList)"
        style="width: 100%"
        :columns="skuAuxiliaryColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo?.skuAuxiliaryList"
      />
      <div v-else>暂无数据</div>
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { formatDictColumn } from '@/utils/table'
import { isValidArray } from '@/utils/is'
import { LengthUnit } from '@/utils/config'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { cloneDeep } from 'lodash-es'

const props = defineProps<{
  info?: any
}>()

const data = ref(props?.info)
// 针对于被删除的列表，这里将会之展示中划线
const setOldSku = (newSku, oldSku, typeStr) => {
  if (newSku != null && oldSku != null) {
    newSku.forEach((e, i) => {
      e.changeFlag = false
      oldSku.forEach((item, index) => {
        if (e.auxiliarySkuName == item.auxiliarySkuName) {
          e.oldSku = item
        }
      })
    })
    const onlyInOldVender = oldSku.filter((oldObj) => {
      if (typeStr === 'auxiliarySkuName') {
        return !newSku.some((newObj) => newObj?.auxiliarySkuName === oldObj?.auxiliarySkuName)
      } else {
        return !newSku.some((newObj) => newObj?.skuName === oldObj?.skuName)
      }
    })
    if (onlyInOldVender.length) {
      onlyInOldVender.forEach((obj) => {
        let cloneObj = cloneDeep(obj)
        // 对象属性值为空，则只展示删除的旧数据
        for (const key in cloneObj) {
          cloneObj[key] = '  '
        }
        cloneObj.changeFlag = false
        cloneObj.oldSku = obj
        newSku.push(cloneObj)
      })
    }
  }
}

setOldSku(data.value?.skuAuxiliaryList, data.value?.oldData?.skuAuxiliaryList, 'auxiliarySkuName')

const pageInfo = computed(() => {
  return {
    ...data.value,
    skuAuxiliaryList: data.value.skuAuxiliaryList.map((item) => {
      return {
        ...item,
        skuRateAndAuxiliarySkuRate: `${item.skuRate}:${item.auxiliarySkuRate}`,
        oldSku: {
          ...item.oldSku,
          skuRateAndAuxiliarySkuRate: `${item.oldSku?.skuRate}:${item.oldSku?.auxiliarySkuRate}`
        }
      }
    })
  }
})
const subProductListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'singleProcessFee',
    field: 'singleProcessFee',
    label: '单位加工费',
    isCompare: true
  },
  {
    field: 'processRemark',
    label: '加工备注',
    isCompare: true
  },
  {
    slotName: 'subProductList',
    field: 'subProductList',
    label: '',
    span: 24
  }
]
const accessoriesListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'accessoriesList',
    field: 'accessoriesList',
    label: '',
    span: 24
  }
]

const auxiliaryInfo = [
  {
    slotName: 'skuAuxiliaryList',
    field: 'skuAuxiliaryList',
    label: '',
    labelWidth: 0,
    span: 24
  }
]

const accessoriesListColumns = [
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '图片',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.picture.length > 0) {
          let url = row.picture.find((item) => item.mainFlag == 1)?.fileUrl
          return <EplusImgEnlarge mainPicture={url} />
        } else {
          return '-'
        }
      }
    }
  },
  {
    field: 'name',
    label: '中文品名',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="name"
              oldChangeField="oldData"
            />
          </>
        )
      }
    }
  },
  {
    field: 'code',
    label: '产品编号',
    minWidth: '100px'
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    minWidth: '100px',
    formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
  },
  {
    field: 'material',
    label: '产品材质',
    minWidth: '100px'
  },
  // {
  //   field: 'purchaseUserName',
  //   label: '采购员',
  //   minWidth: '100px'
  // },
  {
    field: 'totalAmountList',
    label: '单品规格/净重',
    minWidth: '180px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.singleNetweight?.weight) {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/${row.singleNetweight.weight} ${row.singleNetweight.unit}`
        } else {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/-`
        }
      }
    }
  },
  {
    field: 'delivery',
    label: '采购交期',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return row.delivery ? `${row.delivery}天` : '-'
      }
    }
  },
  {
    label: '采购成本',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return row.unitPrice?.amount ? `${row.unitPrice?.amount} ${row.unitPrice?.currency}` : '-'
      }
    }
  }
]

const subProductListColumns = [
  ...accessoriesListColumns,
  {
    label: '数量',
    field: 'qty',
    width: 90
  }
]

const skuAuxiliaryColumns = [
  {
    label: '包材名称',
    field: 'auxiliarySkuName',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="auxiliarySkuName"
              oldChangeField="oldSku"
            />
          </>
        )
      }
    }
  },
  {
    label: '包材图片',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.auxiliarySkuPicture.id) {
          return <EplusImgEnlarge mainPicture={row?.auxiliarySkuPicture} />
        } else {
          return '-'
        }
      }
    }
  },
  {
    label: '包材比例',
    fileld: 'skuRateAndAuxiliarySkuRate',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="skuRateAndAuxiliarySkuRate"
              oldChangeField="oldSku"
            />
          </>
        )
      }
    }
  },
  {
    label: '规格描述',
    field: 'description',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="description"
              oldChangeField="oldSku"
            />
          </>
        )
      }
    }
  },
  {
    label: '备注',
    field: 'remark',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <>
            <EplusFieldComparison
              item={row}
              filed="remark"
              oldChangeField="oldSku"
            />
          </>
        )
      }
    }
  }
]
</script>
