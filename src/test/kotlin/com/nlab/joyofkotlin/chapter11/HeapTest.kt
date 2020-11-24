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

package com.nlab.joyofkotlin.chapter11

import com.nlab.joyofkotlin.chapter5.List
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Doohyun
 */
class HeapTest {

    @Test fun testPlus() {
        val firstHeap = Heap(10)
        assertEquals(10, firstHeap.head.getOrElse { -1 })

        val secondHeap = firstHeap + 5
        assertEquals(5, secondHeap.head.getOrElse { -1 })
        assertEquals(10, secondHeap.left.flatMap { it.head }.getOrElse { -1 })

        val thirdHeap = secondHeap + 7
        assertEquals(5, thirdHeap.head.getOrElse { -1 })
        assertEquals(10, thirdHeap.left.flatMap { it.head }.getOrElse { -1 })
        assertEquals(7, thirdHeap.right.flatMap { it.head }.getOrElse { -1 })

        val fourthHeap = thirdHeap + 8
        assertEquals(5, fourthHeap.head.getOrElse { -1 })
        assertEquals(10, fourthHeap.left.flatMap { it.head }.getOrElse { -1 })
        assertEquals(7, fourthHeap.right.flatMap { it.head }.getOrElse { -1 })
        assertEquals(8, fourthHeap.right.flatMap { it.left }.flatMap { it.head }.getOrElse { -1 })

        val fifthHeap = fourthHeap + 2
        assertEquals(2, fifthHeap.head.getOrElse { -1 })
        assertEquals(5, fifthHeap.left.flatMap { it.head }.getOrElse { -1 })
        assertEquals(10, fifthHeap.left.flatMap { it.left }.flatMap { it.head }.getOrElse { -1 })
        assertEquals(7, fifthHeap.left.flatMap { it.right }.flatMap { it.head }.getOrElse { -1 })
    }

    @Test fun testTail() = with(Heap(10) + 5 + 8 + 7) {
        assertEquals(
            7,
            tail().flatMap { it.head }.getOrElse { -1 }
        )

        assertEquals(
            8,
            tail()
                .flatMap { it.tail() }
                .flatMap { it.head }
                .getOrElse { -1 }
        )

        assertEquals(
            10,
            tail()
                .flatMap { it.tail() }
                .flatMap { it.tail() }
                .flatMap { it.head }
                .getOrElse { -1 }
        )

        assertEquals(
            -1,
            tail()
                .flatMap { it.tail() }
                .flatMap { it.tail() }
                .flatMap { it.tail() }
                .flatMap { it.head }
                .getOrElse { -1 }
        )
    }

    @Test fun testGet() = with(Heap(10) + 5 + 8 + 7) {
        assertEquals(5, get(0).getOrElse { -1 })
        assertEquals(7, get(1).getOrElse { -1 })
        assertEquals(8, get(2).getOrElse { -1 })
        assertEquals(10, get(3).getOrElse { -1 })
        assertEquals(-1, get(4).getOrElse { -1 })
    }

    @Test fun testToList() {
        assertEquals(
            List(5, 7, 8, 10).toString(),
            (Heap(10) + 5 + 8 + 7).toList().toString()
        )
    }

}