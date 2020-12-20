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

package com.nlab.joyofkotlin.chapter12

import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter9.Lazy
import com.nlab.joyofkotlin.chapter9.Stream

/**
 * @author Doohyun
 */
class IO<out T> private constructor(
    private val f: () -> T
) {
    operator fun invoke(): T = f()

    fun <U> map(g: (T) -> U): IO<U> = IO{ g(this()) }

    fun <U> flatMap(g: (T) -> IO<U>): IO<U> = IO {
        g(this())()
    }

    companion object {
        val Empty: IO<Unit> = IO {}

        operator fun <T> invoke(f: () -> T): IO<T> = IO(f)
        operator fun <T> invoke(t: T): IO<T> = IO { t }

        private fun <T, U, V> map2(ioa: IO<T>, iob: IO<U>, f: (T) -> (U) -> V): IO<V> = ioa.flatMap { t ->
            iob.map { u -> f(t)(u) }
        }

        fun <T> repeat(n: Int, io: IO<T>): IO<List<T>> = Stream.fill(n, Lazy { io })
            .foldRight(Lazy { IO { List() } }) { ioT ->
                { ioAcc -> map2(ioT, ioAcc()) { t -> { acc -> acc.construct(t) } } }
            }

        fun <T, U> forever(iot: IO<T>): IO<U> = iot.flatMap { forever(iot) }
    }

}