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
    println(reverse(this))
    println(reverseV2(this))
}


fun <T> reverse(list: List<T>): List<T> {
    return foldLeft(list, listOf()) { acc, element -> listOf(element) + acc }
}

fun <T> reverseV2(list: List<T>): List<T> {
    return foldLeft(list, listOf()) { acc, element -> foldLeft(acc, listOf(element)) { a, e -> a + e} }
}