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
    println("4 + 5 = ${add(4, 5)}")
}

private fun inc(n: Int) = n + 1
private fun dec(n: Int) = n - 1
tailrec fun add(a: Int, b: Int): Int = if (b > 0) add(inc(a), dec(b)) else a