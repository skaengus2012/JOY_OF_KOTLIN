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

package com.nlab.joyofkotlin.chapter13

import com.nlab.joyofkotlin.chapter5.List
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Doohyun
 */
class ListTest {

    @Test
    fun testZipWithPosition() {
        val result = List(1, 2, 3, 4)

        assertEquals(
            List(
                Pair(1, 0),
                Pair(2, 1),
                Pair(3, 2),
                Pair(4, 3)
            ).toString(),
            result.zipWithPosition().toString()
        )

    }
}