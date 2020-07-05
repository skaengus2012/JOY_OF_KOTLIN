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
    println(fibo(10))
}

fun fibo(num: Long): List<BigInteger> {
    require(num > 0)

    tailrec fun fiboTail(seed: BigInteger, a: BigInteger, b: BigInteger, acc: List<BigInteger>): List<BigInteger> {
        return when(seed) {
            BigInteger.ZERO -> listOf(BigInteger.ONE)
            BigInteger.ONE -> listOf(BigInteger.ONE, BigInteger.ONE) + acc
            else -> fiboTail(seed - BigInteger.ONE, a + b, a, acc + listOf(a + b))
        }
    }

    return fiboTail(BigInteger.valueOf(num), BigInteger.ONE, BigInteger.ONE, listOf())
}