import test from 'node:test';
import assert from 'node:assert/strict';
import { fpsText, chooseImageSrc } from '../src/utils.js';

test('fpsText formats correctly', () => {
  assert.equal(fpsText(0, 640, 480), 'FPS: 0 | 640x480');
  assert.equal(fpsText(15, 1280, 720), 'FPS: 15 | 1280x720');
});

test('chooseImageSrc prefers frame.png', () => {
  const fallback = 'data:image/png;base64,AAA';
  assert.equal(chooseImageSrc(true, fallback), './frame.png');
  assert.equal(chooseImageSrc(false, fallback), fallback);
});