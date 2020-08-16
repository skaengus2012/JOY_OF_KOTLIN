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

package com.nlab.joyofkotlin.chapter5

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author Doohyun
 */
class ListTest {

    @Test fun testSetHead() {
        assertEquals(List(3, 1, 2).toString(), List(0, 1, 2).setHead(3).toString())
    }

    @Test fun testDrop() {
        assertEquals(List(3).toString(), List(1, 2, 3).drop(2).toString())
    }

    @Test fun testDropWhile() {
        assertEquals(List(3, 4, 5).toString(), List(1, 2, 3, 4, 5).dropWhile { it < 3 }.toString())
    }

    @Test fun testReverse() {
        assertEquals(List<Int>().toString(), List<Int>().reverse().toString())
        assertEquals(List(1, 2, 3).toString(), List(3, 2, 1).reverse().toString())
    }

    @Test fun testInit() {
        assertEquals(List(1, 2, 3).toString(), List(1, 2, 3, 4).init().toString())
    }

    @Test fun testSum() {
        assertEquals(10, List(1,2,3,4).sum())
    }

    @Test fun testProduct() {
        assertEquals(10.0, List(2.0, 5.0).product())
    }

    @Test fun testSize() {
        assertEquals(2L, List(2.0, 5.0).size())
    }

    @Test fun testFold() {
        assertEquals(
            List(1, 2, 3, 4).toString(),
            foldLeft(List(1, 2, 3, 4), List<Int>()) { acc -> { x -> acc.construct(x) }}.reverse().toString()
        )
        assertEquals(
            List(1, 2, 3, 4).toString(),
            foldRight(List(1, 2, 3, 4), List<Int>()) { x -> { acc -> acc.construct(x) }}.toString()
        )
    }

    @Test fun testConcat() {
        assertEquals(
            List(1, 2, 3, 4).toString(),
            concatViaFoldLeft(List(1, 2), List(3, 4)).toString()
        )

        assertEquals(
            List(1, 2, 3, 4).toString(),
            concatViaFoldRight(List(1, 2), List(3, 4)).toString()
        )
    }

    @Test fun testFlatten() {
        assertEquals(
            List(1, 2, 3, 4, 5, 6).toString(),
            flatten(List(List(1), List(2, 3), List(4, 5, 6))).toString()
        )
    }

    @Test fun testTriple() {
        assertEquals(
            List(0, 3, 6, 9, 12, 15).toString(),
            triple(List(0, 1, 2, 3, 4, 5)).toString()
        )
    }

    @Test fun testFilter() {
        assertEquals(
            List(2, 4, 6, 8, 10).toString(),
            filter(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) { it % 2 == 0 }.toString()
        )
    }

    @Test fun testFlatmap() {
        assertEquals(
            List(1, -1, 2, -2, 3, -3).toString(),
            flatMap(List(1, 2, 3)) { x -> List(x, -x) }.toString()
        )
    }
}
