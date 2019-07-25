/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
export function getToDocT (el) {
  // let top = el.offsetTop;
  // while (el) {
  //   top += el.offsetTop;
  //   el = el.offsetParent;
  // }
  // return top;
  let rect = el.getBoundingClientRect()
  // 获取元素距离文档顶部的距离
  let top = rect.top + (window.pageYOffset || document.documentElement.scrollTop) - (document.documentElement.clientTop || 0)
  return Math.floor(top)
}

export function getToDocL (el) {
  // let left = el.offsetLeft;
  // while (el) {
  //   left += el.offsetLeft;
  //   el = el.offsetParent;
  // }
  // return left;
  let rect = el.getBoundingClientRect()
  // 获取元素距离文档顶部的距离
  let left = rect.left + (window.pageXOffset || document.documentElement.scrollLeft) - (document.documentElement.clientLeft || 0)
  return Math.floor(left)
}

export function getDocScrollT () {
  return document.documentElement.scrollTop || document.body.scrollTop
}

export function getDocScrollL () {
  return document.documentElement.scrollLeft || document.body.scrollLeft
}

export function getWindowW () {
  return window.innerWidth
}

export function getWindowH () {
  return window.innerHeight
}

/**
 * Get bounding client rect of given element
 * @function
 * @ignore
 * @param {HTMLElement} element
 * @return {Object} client rect
 */
export function getBoundingClientRect (element) {
  let rect = element.getBoundingClientRect()
  // whether the IE version is lower than 11
  let isIE = navigator.userAgent.indexOf('MSIE') !== -1
  // fix ie document bounding top always 0 bug
  let rectTop = isIE && element.tagName === 'HTML'
    ? -element.scrollTop
    : rect.top

  return {
    left: rect.left,
    top: rectTop,
    right: rect.right,
    bottom: rect.bottom,
    width: rect.right - rect.left,
    height: rect.bottom - rectTop
  }
}
