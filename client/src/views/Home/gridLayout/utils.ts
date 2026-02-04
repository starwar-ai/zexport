import mitt from 'mitt'

/**
 * 计算列宽度
 * @param col 列数
 * @param gutter 边距
 * @param boxWidth 盒子宽度
 * @returns 列宽度
 */

/**
 * 计算高度
 * @param rowH 行高
 * @param gutter 边距
 * @param layout 布局
 * @returns 高度
 */

/**
 * 计算边界值
 * @param initNumber 初始数值
 * @param moveNumber 移动数值
 * @param gapNumber 间隔数值
 * @param boundaryNumber 边界数值，默认为0
 * @returns 边界值
 */
export const calcBoundary = (newplacement: number, gapNumber: number, boundaryNumber = 0) => {
  return newplacement <= 0
    ? 1
    : boundaryNumber && newplacement + gapNumber >= boundaryNumber + 1
      ? boundaryNumber + 1 - gapNumber
      : newplacement
}

/**
 * 根据 id 在布局中查找对应的索引和数据
 * @param layout 布局数组
 * @param id 要查找的 id
 * @returns 包含索引和数据的对象
 */
export const findIndexById = (layout: Layout, id: string) => {
  const index = layout.findIndex((item) => item.id === id)
  const data = layout[index]
  return { index, data }
}

/**
 * 绘制网格线
 * @param canvas canvas实列
 * @param height 高度
 * @param width 宽度
 * @param colWidth 列宽
 * @param rowH 行高
 * @param gutter 间距
 */
export const drawGridLines = (
  canvas: HTMLCanvasElement,
  height: number,
  width: number,
  colWidth: number,
  rowH: number,
  gutter: number
) => {
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  const columnSpacing = 15
  const rowsSpacing = 20
  const columns = Math.ceil(width / colWidth + Number(gutter))
  const rows = Math.ceil(height / rowH + Number(gutter))
  const paddingTop = -4
  const paddingLeft = -58
  ctx.fillStyle = '#000'
  ctx.lineWidth = 0.2 // 线条透明度
  ctx.setLineDash([20, 5]) // 虚线
  for (let i = 0; i < rows; i++) {
    for (let j = 0; j < columns; j++) {
      const data = {
        x: paddingLeft + (2 * j + 1) * 70 + j * columnSpacing,
        y: paddingTop + (2 * i + 1) * 30 + i * rowsSpacing,
        id: i * columns + j
      }
      ctx.beginPath()
      ctx.strokeRect(data.x, data.y, 136, 63)
      ctx.stroke()
    }
  }
}

/**
 * 判断两个对象是否相等
 * @param obj1 - 第一个对象
 * @param obj2 - 第二个对象
 * @returns 若两个对象相等则返回true，否则返回false
 */
export const isEqual = (obj1: any, obj2: any) => {
  if (!obj1 || !obj2) return false
  if (JSON.stringify(obj1) === JSON.stringify(obj2)) return true
  return false
}

const eventBus = mitt()
export default eventBus
