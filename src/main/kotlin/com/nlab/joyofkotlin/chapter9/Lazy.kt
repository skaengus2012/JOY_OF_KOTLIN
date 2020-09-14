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

class Lazy<out T>(supplier: () -> T) : () -> T {

    private val lazySupplier: T by lazy(supplier)

    override fun invoke(): T = lazySupplier

    fun <U> map(f: (T) -> U): Lazy<U> = Lazy { f(lazySupplier) }

    fun <U> flatMap(f: (T) -> Lazy<U>): Lazy<U> = Lazy { f(lazySupplier)() }

    companion object {

        fun <T, U, V> lift2(f: (T) -> (U) -> V): (Lazy<T>) -> (Lazy<U>) -> Lazy<V> = { a ->
            { b ->
                Lazy {
                    f(a())(b())
                }
            }

        }

    }

}