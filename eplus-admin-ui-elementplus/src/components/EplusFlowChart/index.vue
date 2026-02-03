<template>
  <div class="flow-content">
    <div
      id="container"
      style="width: 5000px; height: 600px"
    >
    </div>
  </div>
</template>

<script setup lang="ts">
import { Graph } from '@antv/x6'
import { onMounted, ref } from 'vue'

interface NodeData {
  id: string
  name: string
  code: string
  parentCode: string
  children?: NodeData[]
  level?: number
}

interface Props {
  orderData: NodeData[]
}

const props = defineProps<Props>()

const data = ref<{
  nodes: any[]
  edges: any[]
}>({
  nodes: [],
  edges: []
})

let xAxis = 0
let yAxis = 0

const getNewTree = (nodeData: NodeData[] | null, level = 0) => {
  if (!nodeData) return null

  nodeData.map((node, index) => {
    node.level = level
    data.value.nodes.push({
      id: node.id,
      x: 40 + xAxis,
      y: 40 + level * 100,
      width: 100,
      height: 50,
      shape: 'rect',
      attrs: {
        body: {
          fill: '#fff',
          stroke: '#333',
          strokeWidth: 1,
          rx: 8,
          ry: 8
        },
        label: {
          text: `${node.name}\n${node.code}`,
          fill: '#333',
          fontSize: 12,
          refX: 0.5,
          refY: 0.5,
          textAnchor: 'middle',
          textVerticalAnchor: 'middle'
        }
      },
      code: node.code,
      parentCode: node.parentCode
    })

    xAxis += 150

    if (node.children && node.children.length > 0) {
      getNewTree(node.children, level + 1)
    }
  })

  return data.value.nodes
}

let newData = getNewTree(props.orderData)

const setData = (setData) => {
  data.value.edges = []
  newData.forEach((e, i) => {
    newData.forEach((item, index) => {
      if (e.code == item.parentCode) {
        data.value.edges.push({
          source: e.id,
          target: item.id
        })
      }
    })
  })
  return data.value.edges
}

setData(setData)

const init = () => {
  const graph = new Graph({
    container: document.getElementById('container')
  })

  graph.fromJSON(data.value)
}

onMounted(() => {
  // init()
})

defineExpose({ init })
</script>

<style scoped>
.flow-content {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
