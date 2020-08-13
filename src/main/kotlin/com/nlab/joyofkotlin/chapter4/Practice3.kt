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

import java.math.BigInteger

/**
 * @author Doohyun
 */

fun main() {
    println(fibonacci(4))
    println(fibonacciV2(4))
}

fun fibonacci(n: Int): BigInteger {
    if (n == 0) return BigInteger.ONE
    if (n == 1) return BigInteger.ONE
    var a = BigInteger.ONE
    var b = BigInteger.ONE
    var i = n - 1

    while (i-- != 0) {
        val preA = a
        a += b
        b = preA
    }

    return a
}

fun fibonacciV2(n: Int): BigInteger {
    if (n == 0) return BigInteger.ONE
    if (n == 1) return BigInteger.ONE
    tailrec fun fibonacciTail(n: Int, a: BigInteger, b: BigInteger): BigInteger {
        return if (n == 0) a else fibonacciTail(n - 1, a + b, a)
    }
    return fibonacciTail(n - 1, BigInteger.ONE, BigInteger.ONE)
}