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
  const columnSpacing = 6
  const rowsSpacing = 20
  const columns = Math.ceil(width / colWidth + Number(gutter))
  const rows = Math.ceil(height / rowH + Number(gutter))
  const paddingTop = 10
  const paddingLeft = -40
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
