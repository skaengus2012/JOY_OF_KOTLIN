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

fun main() = with((1..5).toList()) {
    println("${makeStringV3(this, " + ")} = ${sumV2(this)}")
}

fun <T, U> foldLeft(list: List<T>, identity: U, f: (acc: U, element: T) -> U): U {
    tailrec fun foldLeft(list: List<T>, acc: U): U {
        return if (list.isEmpty()) acc else foldLeft(list.tail(), f(acc, list.head()))
    }

    return foldLeft(list, identity)
}

fun <T> makeStringV3(list: List<T>, delim: String): String {
    return foldLeft(list, "") { acc, element ->
        if (acc.isEmpty()) "$element" else "${acc}$delim${element}"
    }
}

fun sumV2(list: List<Int>) = foldLeft(list, 0) { acc, element -> acc + element }