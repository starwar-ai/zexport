declare module 'browser-image-compression' {
  interface Options {
    maxSizeMB?: number
    maxWidthOrHeight?: number
    useWebWorker?: boolean
    maxIteration?: number
    exifOrientation?: number
    fileType?: string
    initialQuality?: number
    quality?: number
    alwaysKeepResolution?: boolean
    signal?: AbortSignal
    onProgress?: (progress: number) => void
  }

  interface Result {
    size: number
    type: string
  }

  function imageCompression(file: File | Blob, options?: Options): Promise<File>

  namespace imageCompression {
    function getDataUrlFromFile(file: File | Blob): Promise<string>
    function loadImage(src: string): Promise<HTMLImageElement>
    function drawImageInCanvas(img: HTMLImageElement): HTMLCanvasElement
    function getFilefromDataUrl(
      dataUrl: string,
      filename: string,
      lastModified?: number
    ): Promise<File>
    function downloadFile(file: File | Blob, filename: string): void
  }

  export default imageCompression
}
