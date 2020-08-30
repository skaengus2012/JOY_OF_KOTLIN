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

package com.nlab.joyofkotlin.chapter6

import com.nlab.joyofkotlin.chapter5.*
import com.nlab.joyofkotlin.chapter5.List
import java.lang.Exception
import kotlin.math.pow

/**
 * @author Doohyun
 */
val mean: (List<Double>) -> Option<Double> = { list ->
    if (list.isEmpty()) {
        Option()
    } else {
        Option(list.sum() / list.size())
    }
}

val variance: (List<Double>) -> Option<Double> = { list ->
    mean(list).flatMap { avg -> mean(map(list) { x -> (x - avg).pow(2.0) }) }
}

fun <T, U> lift(f: (T) -> U): (Option<T>) -> Option<U> = {
    try {
        it.map(f)
    } catch (e: Exception) {
        Option()
    }
}

fun <T, U, V> map2(
    ot: Option<T>,
    ou: Option<U>,
    f: (T) -> (U) -> V
): Option<V> = ot.flatMap { t -> ou.map { u -> f(t)(u) } }

fun <T> sequence(
    list: List<Option<T>>
): Option<List<T>> = traverse(list) { it }

fun <T, U> traverse(
    list: List<T>,
    f: (T) -> Option<U>
): Option<List<U>> = foldRight(list, Option(List())) { x: T ->
    { y: Option<List<U>> ->
        map2(f(x), y) { a: U -> { b: List<U> -> b.construct(a) } }
    }
}