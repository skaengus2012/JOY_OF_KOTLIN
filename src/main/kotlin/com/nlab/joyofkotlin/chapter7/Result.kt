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

import java.io.Serializable
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.util.*

sealed class Result<out T> : Serializable {

    abstract fun <U> map(f: (T) -> (U)): Result<U>
    abstract fun <U> flatMap(f: (T) -> Result<U>): Result<U>
    abstract fun getOrElse(defaultValue: @UnsafeVariance T): T
    abstract fun orElse(defaultValue: () -> @UnsafeVariance T): Result<T>
    abstract fun mapFailure(lazyMessage: () -> String): Result<T>
    abstract fun foreach(effect: (T) -> Unit)

    fun filter(
        p: (T) -> Boolean,
        lazyMessage: () -> String = { "Condition not matched" }
    ): Result<T> = this.flatMap { value ->
        if (p(value)) {
            this
        } else {
            failure(lazyMessage())
        }
    }
    fun exists(p: (T) -> Boolean): Boolean = map(p).getOrElse(false)

    private class Failure<out T>(val exception: RuntimeException) : Result<T>() {

        override fun toString(): String {
            return "Failure(exception=$exception)"
        }

        override fun <U> map(f: (T) -> U): Result<U> = failure(exception)
        override fun <U> flatMap(f: (T) -> Result<U>): Result<U> = failure(exception)
        override fun getOrElse(defaultValue: @UnsafeVariance T): T = defaultValue
        override fun orElse(defaultValue: () -> @UnsafeVariance T): Result<T> = try {
            invoke(defaultValue())
        } catch (e: RuntimeException) {
            failure(e)
        } catch (e: Exception) {
            failure(e)
        }
        override fun mapFailure(lazyMessage: () -> String): Result<T> = failure(lazyMessage())
        override fun foreach(effect: (T) -> Unit) = Unit
    }

    private class Success<out T>(private val value: T) : Result<T>() {

        override fun toString(): String {
            return "Success(value=$value)"
        }

        override fun <U> map(f: (T) -> U): Result<U> = try {
            Success(f(value))
        } catch (e: RuntimeException) {
            failure(e)
        } catch (e: Exception) {
            failure(e)
        }

        override fun <U> flatMap(f: (T) -> Result<U>): Result<U> = try {
            f(value)
        } catch (e: RuntimeException) {
            failure(e)
        } catch (e: Exception) {
            failure(e)
        }

        override fun getOrElse(defaultValue: @UnsafeVariance T): T = value
        override fun orElse(defaultValue: () -> @UnsafeVariance T): Result<T> = this
        override fun mapFailure(lazyMessage: () -> String): Result<T> = this
        override fun foreach(effect: (T) -> Unit) = effect(value)
    }

    companion object {

        operator fun <T> invoke(t: T? = null): Result<T> = when(t) {
            null -> Failure(NullPointerException())
            else -> Success(t)
        }

        fun <T> failure(message: String): Result<T> = failure(IllegalStateException(message))
        fun <T> failure(exception: RuntimeException): Result<T> = Failure(exception)
        fun <T> failure(exception: Exception): Result<T> = failure(RuntimeException(exception))

        fun <T, U> lift(f: (T) -> U): (Result<T>) -> Result<U> = { it.map(f) }
        fun <T, U, V> lift2(f: (T) -> (U) -> (V)): (Result<T>) -> (Result<U>) -> Result<V> = { t ->
            { u ->
                t.map(f).flatMap { u.map(it) }
            }
        }
        fun <T, U, V> map2(
            oa: Result<T>,
            ob: Result<U>,
            f: (T) -> (U) -> V
        ): Result<V> = lift2(f)(oa)(ob)
    }

}