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
class MapTest {

    @Test fun testSize() {
        assertTrue(Map<String, String>().isEmpty())
        assertEquals(2, (Map<String, String>() + ("Hello" to "Kotlin") + ("Hello-Test" to "Kotlin")).size())
    }

    @Test fun testPlus() {
        assertTrue((Map<String, String>() + ("Hello" to "Kotlin")).contains("Hello"))
        assertFalse((Map<String, String>() + ("Hello-Test" to "Kotlin")).contains("Hello"))
    }

    @Test fun testMinus() {
        assertFalse((Map<String, String>() + ("Hello" to "Kotlin") - "Hello").contains("Hello"))
    }

    @Test fun testValues() {
        assertEquals(
            List(1, 3, 6, 7, 8).toString(),
            (Map<Int, Int>() + (3 to 3) + (7 to 7) + (6 to 6) + (8 to 8) + (1 to 1)).values().toString()
        )
    }

}