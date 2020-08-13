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

fun main() = with((1..5).toList()) {
    println("1 + 2 + 3 + 4 + 5 = ${sum(this)}")
    println("1 + 2 + 3 + 4 + 5 = ${sumLoop(this)}")
    println("1 + 2 + 3 + 4 + 5 = ${sumCoRec(this)}")
}

fun sum(list: List<Int>): Int = if (list.isEmpty()) 0 else list.head() + sum(list.tail())

fun sumLoop(list: List<Int>): Int {
    var acc = 0
    var listDummy = list
    while (listDummy.isNotEmpty()) {
        acc += listDummy.head()
        listDummy = listDummy.tail()
    }

    return acc
}

fun sumCoRec(list: List<Int>): Int {
    tailrec fun sumTail(list: List<Int>, acc: Int): Int {
        return if (list.isEmpty()) acc else sumTail(list.tail(), acc + list.head())
    }
    return sumTail(list, 0)
}