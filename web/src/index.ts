const sampleBase64 = `data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAYAAABccqhmAAAABHNCSVQICAgIfAhkiAAAABh0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwA...`;

const img = document.getElementById('frame') as HTMLImageElement;
const stats = document.getElementById('stats') as HTMLDivElement;

let fps = 0;
let last = performance.now();
function tick(){
  const now = performance.now();
  if (now - last >= 1000) {
    stats.textContent = `FPS: ${fps} | 640x480`;
    fps = 0; last = now;
  }
  fps++;
  requestAnimationFrame(tick);
}

img.src = sampleBase64; // one static frame for demo
requestAnimationFrame(tick);
