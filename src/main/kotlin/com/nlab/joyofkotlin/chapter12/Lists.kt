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

package com.nlab.joyofkotlin.chapter12

import com.nlab.joyofkotlin.chapter5.List

/**
 * @author Doohyun
 */

fun <T> List<T>.forEach(ef: (T) -> Unit) {
    tailrec fun forEachRec(list: List<T>, ef: (T) -> Unit) {
        when(list) {
            is List.Nil -> Unit
            is List.Cons -> {
                ef(list.head)
                forEachRec(list.tail, ef)
            }
        }
    }

    forEachRec(this, ef)
}