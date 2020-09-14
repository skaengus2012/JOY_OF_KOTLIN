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

package com.nlab.joyofkotlin.chapter9

import com.nhaarman.mockitokotlin2.*
import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter8.range
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StreamTest {

    @Test
    fun testTakeAMost() {
        assertEquals(15, Stream.repeat { 3 }.takeAtMost(5).sum())
    }

    @Test
    fun testDropAtMost() {
        assertEquals(9, Stream.repeat { 3 }
            .takeAtMost(8)
            .dropAtMost(5)
            .sum()
        )

        assertEquals(15, Stream.repeat { 3 }
            .dropAtMost(8)
            .takeAtMost(5)
            .sum()
        )

        assertEquals(0, Stream.repeat { 3 }
            .takeAtMost(3)
            .dropAtMost(10)
            .sum()
        )
    }

    @Test
    fun testToList() {
        assertEquals(List(3, 3, 3).toString(), Stream.repeat { 3 }.takeAtMost(3).toList().toString())
    }

    @Test
    fun testIterate() {
        assertEquals(9, Stream.iterate(1) { n -> n + 1 }
            .dropAtMost(3)
            .takeAtMost(2)
            .sum())
    }

    @Test
    fun testTakeWhile() {
        assertEquals(10, Stream.iterate(1) { n -> n + 1 }.takeWhile { it < 5 }.sum())
    }

    @Test
    fun testDropWhile() {
        assertEquals(7, Stream.iterate(1) { n -> n + 1 }.dropWhile { it < 3 }.takeAtMost(2).sum())
    }

    @Test
    fun testExists() {
        assertEquals(
            true,
            Stream.iterate(1) { n -> n * 2 }.takeAtMost(5).exists { it > 8 }
        )

        assertEquals(
            false,
            Stream.iterate(1) { n -> n * 2 }.takeAtMost(5).exists { it < 0 }
        )
    }

    @Test
    fun testTakeWhileViaFoldRight() {
        assertEquals(10, Stream.iterate(1) { n -> n + 1 }.takeWhileViaFoldRight { it < 5 }.sum())
    }

    @Test
    fun testHeadSafeViaFoldRight() {
        assertEquals(
            1, Stream.cons(head = Lazy { 1 }, tail = Lazy { Stream<Int>() })
                .headSafeViaFoldRight()
                .getOrElse(-1)
        )

        assertEquals(
            -1, Stream<Int>()
                .headSafeViaFoldRight()
                .getOrElse(-1)
        )
    }

    @Test
    fun testMap() {
        val effect: () -> Unit = mock()

        val numbers = Stream.iterate(1) { n -> n + 1 }.map { n ->
            effect()
            n * 2
        }
        verify(effect, never())()

        numbers.takeAtMost(5).sum()
        verify(effect, times(5))()
    }

    @Test
    fun testFilter() {
        val effect: () -> Unit = mock()

        assertEquals(12, Stream.iterate(1)  { n -> n + 1 }
            .filter { n ->
                effect()
                n % 2 == 0
            }
            .takeAtMost(3)
            .sum())
        verify(effect, times(8))()
    }

    @Test
    fun testAppend() {
        assertEquals(
            range(1, 8).toString(),
            Stream.iterate(1) { n -> n + 1 }
                .takeAtMost(5)
                .append(Lazy { Stream.iterate(6) { n -> n + 1}.takeAtMost(2) })
                .toList()
                .toString()
        )
    }

    @Test
    fun testFlatMap() {
        assertEquals(
            List(1, 1, 1, 2, 2, 2).toString(),
            Stream.iterate(1) { n -> n + 1 }
                .takeAtMost(2)
                .flatMap { n -> Stream.repeat { n }.takeAtMost(3) }
                .toList()
                .toString()
        )
    }

    @Test
    fun testFind() {
        assertEquals(11, Stream.iterate(1) { n -> n + 1 }
            .find { n -> n > 10 }
            .getOrElse(-1)
        )

        assertEquals(-1, Stream.iterate(1) { n -> n + 1 }
            .takeAtMost(10)
            .find { n -> n > 100 }
            .getOrElse(-1)
        )
    }

    private fun Stream<Int>.sum(): Int {
        tailrec fun sum(acc: Int, stream: Stream<Int>): Int {
            return when {
                stream.isEmpty() -> acc
                else -> sum(acc + stream.head().getOrElse(0), stream.tail().getOrElse(Stream()))
            }
        }
        return sum(0, this)
    }

    @Test
    fun testFibs() {
        assertEquals(
            List(1, 1, 2, 3, 5, 8, 13, 21).toString(),
            Stream.unFold(Pair(1, 1)) { (a, b) -> Result(Pair(a, Pair(b, a + b))) }
                .takeWhile { it < 30 }
                .toList()
                .toString()
        )
    }

    @Test
    fun testFilter2() {
        val effect: () -> Unit = mock()

        assertEquals(12, Stream.iterate(1)  { n -> n + 1 }
            .filter2 { n ->
                effect()
                n % 2 == 0
            }
            .takeAtMost(3)
            .sum())
        verify(effect, times(8))()
    }

}