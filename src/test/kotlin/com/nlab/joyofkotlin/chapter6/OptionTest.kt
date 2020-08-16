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

package com.nlab.joyofkotlin.chapter6

import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chpater6.Option
import com.nlab.joyofkotlin.chpater6.lift
import com.nlab.joyofkotlin.chpater6.map2
import com.nlab.joyofkotlin.chpater6.variance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author Doohyun
 */
class OptionTest {

    @Test fun testGetOrElse() {
        assertEquals(1, Option<Int>().getOrElse { 1 })
        assertEquals(5, Option(5).getOrElse { 1 })
    }

    @Test fun testMap() {
        assertEquals(Option("Hello - Optional : 5"), Option(5).map { "Hello - Optional : $it" })
    }

    @Test fun testFlatMap() {
        assertEquals(
            Option("Hello - Optional : 5"),
            Option(5).flatMap { Option("Hello - Optional : $it") }
        )
    }

    @Test fun testOrElse() {
        assertEquals(
            Option(5),
            Option<Int>().orElse { Option(5) }
        )

        assertEquals(
            Option<Int>(),
            Option<Int>().orElse { Option() }
        )
    }

    @Test fun testFilter() {
        assertEquals(Option<Int>(), Option(10).filter { it > 10 })
        assertEquals(Option(15), Option(15).filter { it > 10 })
    }

    @Test fun testVariance() {
        assertEquals(Option(0.0), variance(List(1.0, 1.0, 1.0, 1.0, 1.0)))
    }

    @Test fun testLift() {
        assertEquals(Option("HELLO"), lift (String::toUpperCase) (Option("hello")))
    }

    @Test fun testMap2() {
        assertEquals(
            Option("1 + 1.0"),
            map2(Option(1), Option(1.0)) { x: Int -> { y: Double -> "$x + $y" } }
        )
    }

    @Test fun testSequence() {
        assertEquals(
            Option(List(1, 2, 3)).toString(),
            com.nlab.joyofkotlin.chpater6.sequence(List(Option(1), Option(2), Option(3))).toString()
        )

        assertEquals(
            Option<Int>(),
            com.nlab.joyofkotlin.chpater6.sequence(List(Option(1), Option(2), Option(), Option(3)))
        )
    }

}