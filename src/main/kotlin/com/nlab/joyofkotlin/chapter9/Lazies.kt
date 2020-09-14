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

package com.nlab.joyofkotlin.chapter9

import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter5.map
import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter8.sequence as sequence8

fun or(a: Lazy<Boolean>, b: Lazy<Boolean>): Boolean = if (a()) true else b()

fun constructMessage(a: Lazy<String>, b: Lazy<String>): Lazy<String> = Lazy { "${a()}, ${b()}" }

val constructMessage: (Lazy<String>) -> (Lazy<String>) -> Lazy<String> = { a ->
    { b ->
        Lazy { "${a()}, ${b()}" }
    }
}

fun <T> sequence(lst: List<Lazy<T>>): Lazy<List<T>> = Lazy { map(lst) { it() } }

fun <T> sequenceResult(lst: List<Lazy<T>>): Lazy<Result<List<T>>> = Lazy {
    sequence8(map(lst) { Result(it()) })
}