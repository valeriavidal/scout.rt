/*
 * Copyright (c) 2014-2018 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
import {objects} from '../index';
import {strings} from '../index';



/**
 * Ensures the given parameter is an array
 * @memberOf scout.arrays
 */
export function ensure(array) {
  if (array === undefined || array === null) {
    return [];
  }
  if (!Array.isArray(array)) {
    return [array];
  }
  return array;
}

/**
 * Creates an array with the given length and initializes each value with the given initValue.
 */
export function init(length, initValue) {
  var array = [];
  for (var i = 0; i < length; i++) {
    array[i] = initValue;
  }
  return array;
}

/**
 * Removes the first occurrence of the specified element from the array,
 * if it is present (optional operation).  If the array does not contain
 * the element, it is unchanged.
 *
 * @return true if the array contained the specified element
 */
export function remove(arr, element) {
  if (arr) {
    var index = arr.indexOf(element);
    if (index !== -1) {
      arr.splice(index, 1);
      return true;
    }
  }
  return false;
}

/**
 * Removes every given element from the array
 *
 * @return true if the array contained at least one of the specified elements
 */
export function removeAll(arr, elements) {
  var modified = false;
  if (!elements || elements.length === 0) {
    return false;
  }
  for (var i = arr.length - 1; i >= 0; i--) {
    if (elements.indexOf(arr[i]) > -1) {
      arr.splice(i, 1);
      modified = true;
    }
  }
  return modified;
}

/**
 * @return the index of the replaced element
 */
export function replace(arr, element, replacement) {
  var index = arr.indexOf(element);
  if (index !== -1) {
    arr[index] = replacement;
  }
  return index;
}

/**
 * Inserts the given element at the specified index.
 * <p>
 * This function uses insertAll() which relies on Array.prototype.splice(). Check its js-doc for details.
 */
export function insert(arr, element, index) {
  insertAll(arr, [element], index);
}

/**
 * Inserts all elements of the given array at the specified index.
 * <p>
 * This function is based on Array.prototype.splice().
 * Thus, if the 'index' is greater than the length of the array, 'elements' will be added to the end of the array 'arr'.
 * This may cause unexpected behavior on accessing arr[index] after insertion.
 *
 * The caller must ensure the size of the array.
 */
export function insertAll(arr, elements, index) {
  elements = ensure(elements);
  arr.splice(...[index, 0].concat(elements));
}

/**
 * @deprecated use insertAll(arr, elements, index) instead
 */
export function insertArray(arr, elements, index) {
  return insertAll(arr, elements, index);
}

/**
 * Inserts the given element into the array according to the sort order indicated by the given comparison function.
 *
 * All arguments are mandatory.
 */
export function insertSorted(arr, element, compareFunc) {
  // https://en.wikipedia.org/wiki/Binary_search_algorithm
  var left = 0;
  var right = arr.length - 1;
  while (left <= right) {
    var middle = left + Math.floor((right - left) / 2);
    var c = compareFunc(arr[middle], element);
    if (c < 0) {
      // Search in right half
      left = middle + 1;
    } else if (c > 0) {
      // Search in left half
      right = middle - 1;
    } else {
      // Found an exact match.
      // The insertion point index is equal to the last index starting from "middle" that matches
      // the element. This ensures a stable insertion order (because of the device-and-conquer
      // method, "middle" might be any of the elements with the same value).
      left = middle + 1;
      while (left < arr.length && compareFunc(arr[left], element) === 0) {
        left++;
      }
      break;
    }
  }
  // "left" now contains the index to insert the element
  arr.splice(left, 0, element);
}

/**
 * This function uses insert() which relies on Array.prototype.splice(). Check its js-doc for details.
 */
export function move(arr, fromIndex, toIndex) {
  var element = arr.splice(fromIndex, 1)[0];
  insert(arr, element, toIndex);
}

export function containsAny(haystack, needles) {
  haystack = ensure(haystack);
  needles = ensure(needles);
  return needles.some(function contains(element) {
    return haystack.indexOf(element) >= 0;
  });
}

export function containsAll(haystack, needles) {
  haystack = ensure(haystack);
  needles = ensure(needles);
  return needles.every(function contains(element) {
    return haystack.indexOf(element) >= 0;
  });
}

export function first(arr) {
  if (Array.isArray(arr)) {
    return arr[0];
  }
  return arr;
}

export function last(arr) {
  if (Array.isArray(arr)) {
    return arr[arr.length - 1];
  }
  return arr;
}

/**
 * @returns true if the given argument is an array and has a length > 0, false in any other case.
 */
export function hasElements(arr) {
  return !empty(arr);
}

/**
 * @returns true if the given argument is not an array or the length of the array is 0, false in any other case.
 */
export function empty(arr) {
  if (Array.isArray(arr)) {
    return arr.length === 0;
  }
  return true;
}

export function pushAll(arr, arr2) {
  arr.push.apply(arr, arr2);
}

/**
 * Merges the two given arrays and removes duplicate entries in O(n).
 * If the arrays contain objects instead of primitives, it uses their id to check for equality.
 */
export function union(array1, array2) {
  var result = [],
    map = {};

  array1 = ensure(array1);
  array2 = ensure(array2);

  array1.forEach(function(entry) {
    var key = entry;
    if (typeof entry === 'object') {
      key = entry.id;
    }
    map[key] = entry;
    result.push(entry);
  });

  array2.forEach(function(entry) {
    var key = entry;
    if (typeof entry === 'object') {
      key = entry.id;
    }
    if (!(key in map)) {
      result.push(entry);
    }
  });

  return result;
}

export function equalsIgnoreOrder(arr, arr2) {
  if (arr === arr2) {
    return true;
  }
  if ((!arr || arr.length === 0) && (!arr2 || arr2.length === 0)) {
    return true;
  }
  if (!arr || !arr2) {
    return false;
  }
  if (arr.length !== arr2.length) {
    return false;
  }
  return containsAll(arr, arr2);
}

export function equals(arr, arr2) {
  if (arr === arr2) {
    return true;
  }
  if ((!arr || arr.length === 0) && (!arr2 || arr2.length === 0)) {
    return true;
  }
  if (!arr || !arr2) {
    return false;
  }
  if (arr.length !== arr2.length) {
    return false;
  }

  for (var i = 0; i < arr.length; i++) {
    if (arr[i] !== arr2[i]) {
      return false;
    }
  }
  return true;
}

export function greater(arr, arr2) {
  var arrLength = 0,
    arr2Length = 0;
  if (arr) {
    arrLength = arr.length;
  }
  if (arr2) {
    arr2Length = arr2.length;
  }
  return arrLength > arr2Length;
}

export function eachSibling(arr, element, func) {
  if (!arr || !func) {
    return;
  }
  for (var i = 0; i < arr.length; i++) {
    var elementAtI = arr[i];
    if (elementAtI !== element) {
      func(elementAtI, i);
    }
  }
}

/**
 * Alternative implementation of Array.findIndex(callback [, thisArg]), which is supported by most browsers.
 * See Array.findIndex for a detailed description.
 */
export function findIndex(arr, predicate, thisArg) {
  if (!arr || !predicate) {
    return -1;
  }
  for (var i = 0; i < arr.length; i++) {
    if (predicate.call(thisArg, arr[i], i, arr)) {
      return i;
    }
  }
  return -1;
}

export function find(arr, predicate, thisArg) {
  var index = findIndex(arr, predicate, thisArg);
  if (index === -1) {
    return null;
  }
  return arr[index];
}

export function findFrom(arr, startIndex, predicate, reverse) {
  if (reverse) {
    return findFromReverse(arr, startIndex, predicate);
  } else {
    return findFromForward(arr, startIndex, predicate);
  }
}

export function findIndexFrom(arr, startIndex, predicate, reverse) {
  if (reverse) {
    return findIndexFromReverse(arr, startIndex, predicate);
  } else {
    return findIndexFromForward(arr, startIndex, predicate);
  }
}

export function findFromForward(arr, startIndex, predicate) {
  var index = findIndexFromForward(arr, startIndex, predicate);
  if (index === -1) {
    return null;
  }
  return arr[index];
}

export function findIndexFromForward(arr, startIndex, predicate) {
  if (!arr || !predicate) {
    return -1;
  }
  for (var i = startIndex; i < arr.length; i++) {
    var element = arr[i];
    if (predicate(element, i)) {
      return i;
    }
  }
  return -1;
}

export function findFromReverse(arr, startIndex, predicate) {
  var index = findIndexFromReverse(arr, startIndex, predicate);
  if (index === -1) {
    return null;
  }
  return arr[index];
}

export function findIndexFromReverse(arr, startIndex, predicate) {
  if (!arr || !predicate) {
    return -1;
  }
  for (var i = startIndex; i >= 0; i--) {
    var element = arr[i];
    if (predicate(element, i)) {
      return i;
    }
  }
  return -1;
}

/**
 * Pushes all elements to the given array that are not null or undefined.
 */
export function pushIfDefined(arr, elements) {
  elements = objects.argumentsToArray(arguments).slice(1).filter(function(element) {
    return element !== null && element !== undefined;
  });
  if (arr && elements.length) {
    arr.push(...elements);
  }
}

/**
 * Pushes the given element if it does not already exist in the array and the element is truthy. Thus the array is like a Set where every element
 * can only be added once to the collection. Note: the comparison is done with the === operator.
 */
export function pushSet(arr, element) {
  if (element && arr.indexOf(element) === -1) {
    arr.push(element);
  }
}

/**
 * @param encoded defaults to false
 */
export function format(arr, delimiter, encodeHtml) {
  if (!arr || arr.length === 0) {
    return '';
  }

  var output = '';
  for (var i = 0; i < arr.length; i++) {
    var element = arr[i];
    if (delimiter && i > 0 && i < arr.length) {
      output += delimiter;
    }
    if (encodeHtml) {
      element = strings.encode(element);
    }
    output += element;
  }
  return output;
}

export function formatEncoded(arr, delimiter) {
  return format(arr, delimiter, true);
}

export function max(arr) {
  if (arr === null || arr === undefined) {
    return Math.max.apply(Math, arr);
  }

  // Math.max() returns 0 (not null!) if arr contains only null and negative elements.
  var filtered = arr.filter(objects.isNumber);
  return Math.max.apply(Math, filtered);
}

export function min(arr) {
  if (arr === null || arr === undefined) {
    return Math.min.apply(Math, arr);
  }

  // Math.min() returns 0 (not null!) if arr contains only null and non-negative elements.
  var filtered = arr.filter(objects.isNumber);
  return Math.min.apply(Math, filtered);
}

/**
 * @returns all elements of the first array which are not in the second array
 */
export function diff(arr1, arr2) {
  var diff = arr1.slice();
  removeAll(diff, arr2);
  return diff;
}

export function flatMap(arr, func) {
  var result = [];
  arr.forEach(function(element) {
    pushAll(result, func(element));
  });
  return result;
}

//
// Use these methods if you have an array of jquery objects.
// Reason $elem1 === $elem2 does often not work because new jquery objects are created for the same html node.
// -> Html nodes need to be compared.
//

export function $indexOf(arr, $element) {
  for (var i = 0; i < arr.length; i++) {
    if (arr[i][0] === $element[0]) {
      return i;
    }
  }
}

export function $remove(arr, $element) {
  var index = $indexOf(arr, $element);
  if (index >= 0) {
    arr.splice(index, 1);
  }
}

export default {
  $indexOf,
  $remove,
  containsAll,
  containsAny,
  diff,
  eachSibling,
  empty,
  ensure,
  equals,
  equalsIgnoreOrder,
  find,
  findFrom,
  findFromForward,
  findFromReverse,
  findIndex,
  findIndexFrom,
  findIndexFromForward,
  findIndexFromReverse,
  first,
  flatMap,
  format,
  formatEncoded,
  greater,
  hasElements,
  init,
  insert,
  insertAll,
  insertArray,
  insertSorted,
  last,
  max,
  min,
  move,
  pushAll,
  pushIfDefined,
  pushSet,
  remove,
  removeAll,
  replace,
  union
};
