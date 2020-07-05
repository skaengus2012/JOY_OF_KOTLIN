/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nlab.joyofkotlin.chapter4

/**
 * @author Doohyun
 */
fun main() {
    range(1, 10).run { println(this) }
    rangeWithUnfold(1, 10).run { println(this) }
    rangeRecursive(1, 10).run { println(this) }
    rangeWithUnfoldV2(1, 10).run { println(this) }
}

fun range(start: Int, end: Int): List<Int> {
    val result = mutableListOf<Int>()

    var i = start
    while(i < end) {
        result += i
        ++i
    }

    return result
}

fun <T> unfold(seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    var elsem = seed
    while (p(elsem)) {
        result += elsem
        elsem = f(elsem)
    }

    return result
}

fun rangeWithUnfold(start: Int, end: Int): List<Int> = unfold(
    start,
    f = { num -> num + 1 },
    p = { num -> num < end }
)

fun rangeRecursive(start: Int, end: Int): List<Int> {
    return if (start >= end) emptyList() else listOf(start) + rangeRecursive(start + 1, end)
}

fun <T> unfoldV2(seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> {
    tailrec fun unfoldTail(element: T, acc: List<T>): List<T> {
        return if (p(element)) unfoldTail(f(element), acc + listOf(element)) else acc
    }

    return unfoldTail(seed, emptyList())
}

fun rangeWithUnfoldV2(start: Int, end: Int): List<Int> = unfoldV2(
    start,
    f = { num -> num + 1 },
    p = { num -> num < end }
)