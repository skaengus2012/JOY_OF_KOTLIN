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

package com.nlab.joyofkotlin.chapter3

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Practice9KtTest {

    @Test fun testFunc() {
        assertEquals(func(1,2,3,4), curried<Int, Int, Int, Int>()(1)(2)(3)(4))
    }

}