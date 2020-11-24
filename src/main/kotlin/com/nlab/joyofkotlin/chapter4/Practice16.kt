/*
 * Copyright (C) 2018 The N's lab Open Source Project
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
    println(iterate(5, f = { it + 1}, n = 6))
}

fun <T> iterate(seed: T, f: (T) -> T, n: Int): List<T> {
    tailrec fun iterateTail(seed: T, acc: List<T>): List<T> {
        return if (acc.size == n) acc else iterateTail(f(seed), acc + seed)
    }

    return iterateTail(seed, emptyList())
}