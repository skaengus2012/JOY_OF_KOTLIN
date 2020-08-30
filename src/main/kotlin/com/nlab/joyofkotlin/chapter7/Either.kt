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

package com.nlab.joyofkotlin.chapter7

sealed class Either<E, out U> {

    abstract fun <V> map(f: (U) -> (V)): Either<E, V>
    abstract fun <V> flatMap(f: (U) -> Either<E, V>): Either<E, V>

    fun getOrElse(defaultValue: () -> @UnsafeVariance U) : U = when(this) {
        is Left -> defaultValue()
        is Right -> value
    }

    fun orElse(defaultValue: () -> Either<E, @UnsafeVariance U>): Either<E, U> = map { this }.getOrElse(defaultValue)

    internal class Left<E, out U>(private val value: E) : Either<E, U>() {
        override fun toString(): String {
            return "Left(value=$value)"
        }

        override fun <V> map(f: (U) -> V): Either<E, V> = left(value)

        override fun <V> flatMap(f: (U) -> Either<E, V>): Either<E, V> = left(value)
    }

    internal class Right<E, out U>(val value: U) : Either<E, U>() {
        override fun toString(): String {
            return "Right(value=$value)"
        }

        override fun <V> map(f: (U) -> V): Either<E, V> = right(f(value))

        override fun <V> flatMap(f: (U) -> Either<E, V>): Either<E, V> = f(value)
    }

    companion object {
        fun <T, U> left(value: T): Either<T, U> = Left(value)
        fun <T, U> right(value: U): Either<T, U> = Right(value)
    }

}
