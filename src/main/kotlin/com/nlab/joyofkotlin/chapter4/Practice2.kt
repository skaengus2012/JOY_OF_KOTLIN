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
    println("5! = ${Factorial.valueOf(5)}")
}

object Factorial {
    private val factorial: (BigInteger) -> BigInteger by lazy { { n: BigInteger ->
        (if (n <= BigInteger.ONE) n else n * factorial(n - BigInteger.ONE))
    }}

    fun valueOf(n: Long): BigInteger = factorial(BigInteger.valueOf(n))
}