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

package com.nlab.joyofkotlin.chapter10

import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter5.concatViaFoldLeft
import com.nlab.joyofkotlin.chapter5.concatViaFoldRight
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.floor
import kotlin.math.log2

/**
 * @author Doohyun
 */
class TreeTest {

    @Test
    fun testContains() {
        assertTrue(4 in Tree(2, 4, 10, 5))
        assertFalse(11 in Tree(2, 4, 10, 5))
    }

    @Test
    fun testSize() {
        assertEquals(5, Tree(2, 3, 10, 5, 6).size)
        assertEquals(5, Tree(5, 3, 10, 2, 6).size)
        assertEquals(0, Tree<Int>().size)
    }

    @Test
    fun testHeight() {
        assertEquals(2, Tree(3, 2, 4, 1).height)
        assertEquals(5, Tree(5, 6, 7, 8, 9, 10).height)
        assertEquals(0, Tree(11).height)
        assertEquals(-1, Tree<Int>().height)
    }

    @Test
    fun testMax() {
        assertEquals(4, Tree(3, 2, 4, 1).max().getOrElse { -1 })
        assertEquals(10, Tree(5, 6, 7, 8, 9, 10).max().getOrElse { -1 })
        assertEquals(11, Tree(11).max().getOrElse { -1 })
        assertEquals(-1, Tree<Int>().max().getOrElse { -1 })
    }

    @Test
    fun testMin() {
        assertEquals(1, Tree(3, 2, 4, 1).min().getOrElse { -1 })
        assertEquals(5, Tree(5, 6, 7, 8, 9, 10).min().getOrElse { -1 })
        assertEquals(11, Tree(11).min().getOrElse { -1 })
        assertEquals(-1, Tree<Int>().min().getOrElse { -1 })
    }

    @Test
    fun testRemove() = with(Tree(7, 2, 20, 1, 5, 15, 24, 30, 13, 16, 21)) {
        assertEquals(Tree(20, 15, 24, 13, 2, 1, 5, 16, 21, 30).toString(), remove(7).toString())
        assertEquals(Tree(7, 2, 24, 1, 5, 21, 30, 15, 13, 16).toString(), remove(20).toString())
        assertEquals(Tree(7, 2, 20, 5, 15, 24, 30, 13, 16, 21).toString(), remove(1).toString())
        assertEquals(Tree(7, 2, 20, 1, 5, 15, 24, 13, 16, 21).toString(), remove(30).toString())
    }

    @Test
    fun testMerge() {
        assertEquals(
            Tree(5, 1, 4, 3, 9, 7).toString(),
            Tree(5, 1, 9).merge(Tree(4, 3, 7)).toString()
        )

        assertEquals(
            Tree(5, 1, 4, 2, 3, 10, 6, 7, 8).toString(),
            Tree(5, 1, 4, 10, 6).merge(Tree(7, 2, 3, 8)).toString()
        )
    }

    @Test
    fun testFold() = with(Tree(4, 2, 6, 1, 3, 5, 7)) {
        assertEquals(
            28,
            foldLeft(
                identity = 0,
                f = { acc -> { num -> acc + num } },
                g = { result1 -> { result2 -> result1 + result2 } }
            )
        )

        assertEquals(
            28,
            foldRight(
                identity = 0,
                f = { num -> { acc -> acc + num } },
                g = { result1 -> { result2 -> result1 + result2 } }
            )
        )

        assertEquals(
            List(7, 6, 5, 4, 3, 2, 1).toString(),
            foldLeft(
                identity = List<Int>(),
                f = { acc -> { num -> acc.construct(num) } },
                g = { result1 -> { result2 -> concatViaFoldLeft(result1, result2) } }
            ).toString()
        )

        assertEquals(
            List(4, 2, 1, 3, 6, 5, 7).toString(),
            foldRight(
                identity = List<Int>(),
                f = { num: Int -> { acc -> acc.construct(num) } },
                g = { result1 -> { result2 -> concatViaFoldRight(result1, result2) } }
            ).toString()
        )
    }

    @Test fun testFoldByOrder() = with(Tree(4, 2, 6, 1, 3, 5, 7)) {
        assertEquals(
            List(1, 2, 3, 4, 5, 6, 7).toString(),
            foldInOrder(List<Int>()) { result1 ->
                { num -> { result2 -> concatViaFoldRight(result1, result2.construct(num)) } }
            }.toString()
        )

        assertEquals(
            List(4, 2, 1, 3, 6, 5, 7).toString(),
            foldPreOrder(List<Int>()) { num ->
                { result1 -> { result2 -> concatViaFoldRight(result1, result2).construct(num) } }
            }.toString()
        )

        assertEquals(
            List(1, 3, 2, 5, 7, 6, 4).toString(),
            foldPostOrder(List<Int>()) { result1 ->
                { result2 -> { num -> concatViaFoldRight(result1, result2.reverse().construct(num).reverse()) } }
            }.toString()
        )
    }

    @Test fun testIdentity() = with(Tree(4, 2, 6, 1, 3, 5, 7)) {
        assertEquals(
            toString(),
            foldInOrder(Tree<Int>()) { result1 -> { num -> { result2 -> Tree(result1, num, result2) } } }.toString()
        )

        assertEquals(
            toString(),
            foldPreOrder(Tree<Int>()) { num -> { result1 -> { result2 -> Tree(result1, num, result2) } } }.toString()
        )

        assertEquals(
            toString(),
            foldPostOrder(Tree<Int>()) { result1 -> { result2 -> { num -> Tree(result1, num, result2) } } }.toString()
        )
    }

    @Test fun testMap() = with(Tree(4, 2, 6, 1, 3, 5, 7)) {
        assertEquals(
            Tree(6, 4, 8, 3, 5, 7, 9).toString(),
            map { it + 2 }.toString()
        )

        assertEquals(
            Tree(-4, -2, -6, -1, -3, -5, -7).toString(),
            map { it * -1 }.toString()
        )

        assertEquals(
            Tree(0).toString(),
            map { it * 0 }.toString()
        )
    }

    @Test fun testToList() = with(Tree(4, 2, 6, 1, 3, 5, 7)) {
        assertEquals(List(7, 6, 5, 4, 3, 2, 1).toString(), toListByDesc().toString())
        assertEquals(List(1, 2, 3, 4, 5, 6, 7).toString(), toListByAsc().toString())
    }

    @Test fun testBalance() {

        with(Tree(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
            assertEquals(
                floor(log2(size.toFloat())).toInt(),
                Tree.balance(this).height
            )
        }

        with(Tree(1, 2, 3, 4, 5, 6, 7, 21, 9, 10, 11, 12, 13, 14, 16, 20)) {
            assertEquals(
                floor(log2(size.toFloat())).toInt(),
                Tree.balance(this).height
            )
        }

        with(Tree<Int>()) {
            assertEquals(
                -1,
                Tree.balance(this).height
            )
        }

    }

}