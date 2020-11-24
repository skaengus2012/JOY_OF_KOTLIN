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

package com.nlab.joyofkotlin.chapter9

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter8.range

/**
 * @author Doohyun
 */
class LazyTest {

    @Test
    fun testOr() {
        val first = LazyAndInvokeCount { true }
        val second: LazyAndInvokeCount<Boolean> = LazyAndInvokeCount { throw IllegalStateException() }

        assertTrue(first.value() || second.value())
        assertTrue(or(first.value, second.value))
        assertEquals(1, first.invokeCount)
        assertEquals(0, second.invokeCount)
    }

    @Test
    fun testConstructMessage() {
        val greetings = LazyAndInvokeCount { "Hello" }
        val name1 = LazyAndInvokeCount { "Mickey" }
        val name2 = LazyAndInvokeCount { "Donald" }

        val message1 = constructMessage(greetings.value, name1.value)
        val message2 = constructMessage(greetings.value, name2.value)

        assertEquals("Hello, Mickey", message1())
        assertEquals("Hello, Donald", message2())
        assertEquals("Hello, Donald", message2())
        assertEquals("Hello, Mickey", message1())
        assertEquals(1, greetings.invokeCount)
        assertEquals(1, name1.invokeCount)
        assertEquals(1, name2.invokeCount)
    }

    private class LazyAndInvokeCount<T>(supplier: () -> T) {
        var invokeCount = 0
            private set

        val value: Lazy<T> = Lazy {
            ++invokeCount
            supplier()
        }
    }

    @Test
    fun testConstructMessageVariable() {
        val greetingMessage = constructMessage(Lazy { "Hello" })
        val message1 = greetingMessage(Lazy { "Mickey" })
        val message2 = greetingMessage(Lazy { "Donald" })

        assertEquals("Hello, Mickey", message1())
        assertEquals("Hello, Donald", message2())
        assertEquals("Hello, Donald", message2())
        assertEquals("Hello, Mickey", message1())
    }

    @Test
    fun testLift2() {
        assertEquals(
            "Hello, Mickey",
            Lazy.lift2 { a: String -> { b: String -> "$a, $b" } }(Lazy { "Hello" })(Lazy { "Mickey" })()
        )
    }

    @Test
    fun testMap() {
        val intValue = LazyAndInvokeCount { 1 }
        val strValue = intValue.value.map { number -> "Number : $number" }

        assertEquals(0, intValue.invokeCount)
        assertEquals("Number : 1", strValue())
        assertEquals("Number : 1", strValue())
        assertEquals(1, intValue.invokeCount)
    }

    @Test
    fun testFlatMap() {
        val intValue = LazyAndInvokeCount { 1 }
        val strValue = intValue.value.flatMap { number -> Lazy { "Number : $number" } }

        assertEquals(0, intValue.invokeCount)
        assertEquals("Number : 1", strValue())
        assertEquals("Number : 1", strValue())
        assertEquals(1, intValue.invokeCount)
    }

    @Test
    fun testSequence() {
        assertEquals(
            range(1, 6).toString(),
            sequence(List(
                Lazy { 1 },
                Lazy { 2 },
                Lazy { 3 },
                Lazy { 4 },
                Lazy { 5 }
            ))().toString()
        )
    }

    @Test
    fun testSequenceResult() {
        assertEquals(
            Result(range(1, 6)).toString(),
            sequenceResult(
                List(
                    Lazy { 1 },
                    Lazy { 2 },
                    Lazy { 3 },
                    Lazy { 4 },
                    Lazy { 5 }
                )
            )().toString()
        )
    }

}