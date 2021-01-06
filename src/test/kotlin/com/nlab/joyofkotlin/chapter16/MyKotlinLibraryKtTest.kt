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

package com.nlab.joyofkotlin.chapter16

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * - 프로그램이 반드시 만족해야 하는 속성 집합을 생각해내야함
 * - 구현에 의존하는 일이 없게 인터페이스를 작성한 후 테스트를 작성하고 마지막으로 구현함
 * - 추상화를 통해 코드에서 신뢰해야하는 부분을 제거하여 테스트를 단순하게 해야함
 * - 테스트에 사용할 임의 값과 생성 값을 사용해 속성을 검사할 때 필요한 부가 정보를 함께 생성해주는 생성기를 작성함
 * - 수천 개의 입력 데이터를 사용하는 테스트를 설정하고 빌드할 때마다 실행함.
 *
 * @author Doohyun
 */
class MyKotlinLibraryKtTest {

    @Test
    fun testMaxMultiple() {
        assertEquals(14,  maxMultiple(2, listOf(4, 11, 8, 2, 3, 1, 14, 9, 5, 17, 6, 7)))
    }

    @Test
    fun testIsMaxMultiple() {
        forAll(Gen.positiveIntegers(), Gen.int(), Gen.int()) { multiple: Int, max: Int, value: Int ->
            isMaxMultiple(multiple)(max, value).let { result ->
                result >= value
                        && result % multiple == 0 || result == max
                        && (result % multiple == 0 && result >= value) || result % multiple != 0
            }
        }
    }
}