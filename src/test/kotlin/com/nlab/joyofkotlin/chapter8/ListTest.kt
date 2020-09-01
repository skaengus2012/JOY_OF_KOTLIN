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

package com.nlab.joyofkotlin.chapter8

import com.nlab.joyofkotlin.chapter5.List
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ListTest {

    @Test fun testGetAt() {
        assertTrue(List(1, 2, 3, 4).getAt(3).exists { it == 4 })
    }

    @Test fun testHasSubList() {
        assertTrue(List(1, 1, 2, 3, 4, 3).hasSubList(List(3, 4, 3)))
        assertFalse(List(1, 1, 2, 3, 4, 3).hasSubList(List(1, 3, 4)))
    }

    @Test fun testRange() {
        assertEquals(List(0, 1, 2, 3, 4).toString(), range(0, 5).toString())
    }

    @Test fun testExist() {
        assertTrue(List(1, 2, 3, 4, 5).exists { it < 2 })
        assertFalse(List(1, 2, 3, 4, 5).exists { it > 10 })
    }

}