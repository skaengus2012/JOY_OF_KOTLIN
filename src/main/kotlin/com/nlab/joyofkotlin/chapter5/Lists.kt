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

package com.nlab.joyofkotlin.chapter5

fun List<Int>.sum(): Int = foldLeft(this, 0) { x -> { acc -> acc + x } }

fun List<Double>.product(): Double = foldLeft(this, 1.0) { x -> { acc -> acc * x } }

fun <T, U> foldRight(
    list: List<T>,
    identity: U,
    f: (T) -> (U) -> (U)
): U = foldLeft(list.reverse(), identity, { acc -> { x -> f(x)(acc) }})

tailrec fun <T, U> foldLeft(
    list: List<T>,
    acc: U,
    f: (U) -> (T) -> (U)
): U = when(list) {
    is List.Nil -> acc
    is List.Cons -> foldLeft(list.tail, f(acc)(list.head), f)
}

fun <T> concatViaFoldLeft(
    list1: List<T>,
    list2: List<T>
): List<T> = foldLeft(list1.reverse(), list2) { acc -> { x -> acc.construct(x) } }

fun <T> concatViaFoldRight(
    list1: List<T>,
    list2: List<T>
): List<T> = foldRight(list1, list2) { x -> { acc -> acc.construct(x) } }

fun <T> flatten(list: List<List<T>>): List<T> = foldLeft(list, List()) { acc -> { x -> concatViaFoldLeft(acc, x) } }

inline fun <T, U> map(list: List<T>, crossinline mapper: (T) -> U): List<U> = foldLeft(list.reverse(), List()) { acc ->
    { x -> acc.construct(mapper(x)) }
}

fun triple(list: List<Int>): List<Int> = map(list) { n -> n * 3 }

fun <T> filter(
    list: List<T>,
    f: (T) -> Boolean
): List<T> = flatMap(list) { x -> if (f(x)) List(x) else List() }

fun <T, U> flatMap(
    list: List<T>,
    f: (T) -> List<U>
): List<U> = flatten(map(list, f))