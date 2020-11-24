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
    println(makeString(this, "+"))
    println(makeStringV2(this, "+"))
}

fun <T> makeString(list: List<T>, delim: String): String = when {
    list.isEmpty() -> ""
    list.tail().isEmpty() -> "${list.head()}${makeString(list.tail(), delim)}"
    else -> "${list.head()}$delim${makeString(list.tail(), delim)}"
}

fun <T> makeStringV2(list: List<T>, delim: String): String {
    tailrec fun makeStringTail(list: List<T>, acc: String): String {
        return if (list.tail().isEmpty()) "${acc}${list.head()}"
        else makeStringTail(list.tail(), "${acc}${list.head()}${delim}")
    }

    return makeStringTail(list, "")
}
