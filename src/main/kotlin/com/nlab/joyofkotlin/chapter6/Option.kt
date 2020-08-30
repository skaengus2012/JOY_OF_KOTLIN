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

package com.nlab.joyofkotlin.chapter6

/**
 * @author Doohyun
 */
sealed class Option<out T> {

    abstract fun isEmpty(): Boolean

    fun filter(p: (T) -> Boolean): Option<T> = flatMap { if (p(it)) this else None }

    fun <U> map(mapper: (T) -> U): Option<U> = when(this) {
        is Some -> Some(mapper(value))
        is None -> this
    }

    fun <U> flatMap(mapper: (T) -> Option<U>): Option<U> = map { mapper(it) }.getOrElse { None }

    fun getOrElse(default: () -> @UnsafeVariance T): T = when(this) {
        is Some -> value
        is None -> default()
    }

    fun orElse(default: () -> Option<@UnsafeVariance T>): Option<T> = map { this }.getOrElse(default)

    private object None : Option<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "None"
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = 0
    }

    private data class Some<out T>(val value: T) : Option<T>() {
        override fun isEmpty(): Boolean = false
    }

    companion object {
        operator fun <T> invoke(value: T? = null): Option<T> = value?.let { Some(it) } ?: None
    }

}