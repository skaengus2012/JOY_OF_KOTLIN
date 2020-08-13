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

package com.nlab.joyofkotlin.chapter5

import java.lang.StringBuilder

/**
 * @author Doohyun
 */
sealed class List<out T> {
    abstract fun isEmpty(): Boolean

    fun construct(item: @UnsafeVariance T): List<T> = Cons(item, this)

    fun setHead(
        item: @UnsafeVariance T
    ): List<T> = when(this) {
        is Nil -> throw IllegalStateException("setHead called on an empty list")
        is Cons -> tail.construct(item)
    }

    fun drop(n: Int): List<T> {
        tailrec fun <T> dropRec(acc: List<T>, n: Int): List<T> = when(acc) {
            is Nil -> acc
            is Cons -> if (n <= 0) acc else dropRec(acc.tail, n - 1)
        }

        return dropRec(this, n)
    }

    fun dropWhile(p: (T) -> Boolean): List<T> {
        tailrec fun <T> dropWhileRec(acc: List<T>, p: (T) -> Boolean): List<T> = when(acc) {
            is Nil -> acc
            is Cons -> if (p(acc.head)) dropWhileRec(acc.tail, p) else acc
        }

        return dropWhileRec(this, p)
    }

    fun reverse(): List<T> = foldLeft(this, invoke()) { acc -> { x -> acc.construct(x) } }

    fun size(): Long = foldLeft(this, 0L) { acc -> { acc + 1 } }

    fun init(): List<T> = reverse().drop(1).reverse()

    internal object Nil : List<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "[Nil]"
    }

    internal class Cons<T>(internal val head: T, internal val tail: List<T>) : List<T>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = StringBuilder()
            .append("[")
            .also { toStringBuilder(it, this) }
            .append("Nil")
            .append("]")
            .toString()

        private tailrec fun toStringBuilder(
            acc: StringBuilder,
            list: List<T>
        ): StringBuilder = when(list) {
            is Nil -> acc
            is Cons -> toStringBuilder(acc.append("${list.head}, "), list.tail)
        }

    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        operator fun <T> invoke(
            vararg items: T
        ): List<T> = items.foldRight(Nil as List<T>) { t: T, acc: List<T> -> Cons(t, acc) }
    }
}