export function fpsText(fps: number, width: number, height: number): string {
  return `FPS: ${fps} | ${width}x${height}`;
}

export function chooseImageSrc(hasFramePng: boolean, fallbackBase64: string): string {
  return hasFramePng ? './frame.png' : fallbackBase64;
}