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

package com.nlab.joyofkotlin.chapter5

import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter8.foldLeft
import java.lang.StringBuilder

/**
 * @author Doohyun
 */
sealed class List<out T> {
    abstract val sizeMemoized: Int

    abstract fun isEmpty(): Boolean
    abstract fun headSafe(): Result<T>
    abstract fun lastSafe(): Result<T>
    abstract fun tailSafe(): Result<List<T>>

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

    fun getAt(index: Int): Result<T> {
        return if (index < 0 || index >= sizeMemoized) {
            Result.failure("Index out of bound")
        } else {
            foldLeft(
                ResultPair(Result.failure("Index out of bound"), index),
                p = { pair -> pair.second < 0 },
                f = { acc: ResultPair<T> ->
                    { t: T ->
                        ResultPair(Result(t), acc.second - 1)
                    }
                }
            ).first
        }
    }

    fun startWith(sub: List<@UnsafeVariance T>): Boolean {
        tailrec fun startWithRec(
            target: List<@UnsafeVariance T>,
            subList: List<@UnsafeVariance T>
        ): Boolean = when(subList) {
            is Nil -> true
            is Cons -> {
                if (target is Cons && target.head == subList.head) {
                    startWithRec(target.tail, subList.tail)
                } else {
                    false
                }
            }
        }

        return startWithRec(this, sub)
    }

    fun hasSubList(sub: List<@UnsafeVariance T>): Boolean {
        tailrec fun hasSubListRec(
            target: List<@UnsafeVariance T>
        ): Boolean = when(target) {
            is Nil -> sub.isEmpty()
            is Cons -> {
                if (target.startWith(sub)) {
                    true
                } else {
                    hasSubListRec(target.tail)
                }
            }
        }

        return hasSubListRec(this)
    }

    fun <U> groupBy(mapper: (T) -> U): Map<U, List<T>> = foldLeft(reverse(), mapOf()) { map ->
        { item ->
            mapper(item).let { key -> map + (key to map.getOrDefault(key, Nil).construct(item)) }
        }
    }

    fun exists(p: (T) -> Boolean): Boolean = foldLeft(false, { it }) {
        { t: T -> p(t) }
    }

    inline fun forAll(crossinline p: (T) -> Boolean): Boolean = !exists { !p(it) }

    inline fun <U> map(crossinline mapper: (T) -> U): List<U> = map(this, mapper)

    private data class ResultPair<out T>(
        val first: Result<T>,
        val second: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResultPair<*>

            if (second != other.second) return false

            return true
        }

        override fun hashCode(): Int {
            return second
        }
    }

    internal object Nil : List<Nothing>() {
        override val sizeMemoized: Int = 0
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "[Nil]"
        override fun headSafe(): Result<Nothing> = Result()
        override fun lastSafe(): Result<Nothing> = Result()
        override fun tailSafe(): Result<Nothing> = Result()
    }

    internal class Cons<T>(internal val head: T, internal val tail: List<T>) : List<T>() {

        override val sizeMemoized: Int = tail.sizeMemoized + 1

        override fun isEmpty(): Boolean = false

        override fun headSafe(): Result<T> = Result(head)

        override fun lastSafe(): Result<T> = foldLeft(this, Result()) { { value: T -> Result(value) } }

        override fun tailSafe(): Result<List<T>> = Result(tail)

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