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

package com.nlab.joyofkotlin.chapter9

import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter7.Result

/**
 * @author Doohyun
 */
sealed class Stream<out T> {

    abstract fun isEmpty(): Boolean
    abstract fun head(): Result<T>
    abstract fun tail(): Result<Stream<T>>
    abstract fun takeAtMost(n: Int): Stream<T>
    abstract fun dropAtMost(n: Int): Stream<T>
    abstract fun takeWhile(p: (T) -> Boolean): Stream<T>
    abstract fun <U> foldRight(z: Lazy<U>, f: (T) -> (Lazy<U>) -> U): U
    fun dropWhile(p: (T) -> Boolean): Stream<T> = dropWhile(this, p)
    fun exists(p: (T) -> Boolean): Boolean = exists(this, p)
    fun toList(): List<T> = toList(this)
    fun takeWhileViaFoldRight(p: (T) -> Boolean): Stream<T> = takeWhileViaFoldRight(this, p)
    fun headSafeViaFoldRight(): Result<T> = headSafeViaFoldRight(this)
    fun <U> map(f: (T) -> U): Stream<U> = map(this, f)
    fun filter(f: (T) -> Boolean): Stream<T> = filter(this, f)
    fun append(stream: Lazy<Stream<@UnsafeVariance T>>): Stream<T> = append(this, stream)
    fun find(p: (T) -> Boolean): Result<T> = find(this, p)
    fun filter2(p: (T) -> Boolean): Stream<T> = filter2(this, p)

    fun <U> flatMap(f: (T) -> Stream<U>): Stream<U> = flatMap(this, f)

    // Nothing 타입은 선언은 어느 파라미터 제네릭의 공변형이 될 수 있음
    private object Empty : Stream<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun head(): Result<Nothing> = Result()
        override fun tail(): Result<Stream<Nothing>> = Result()
        override fun takeAtMost(n: Int): Stream<Nothing> = this
        override fun dropAtMost(n: Int): Stream<Nothing> = this
        override fun takeWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this
        override fun <U> foldRight(z: Lazy<U>, f: (Nothing) -> (Lazy<U>) -> U): U = z()
    }

    private class Cons<out T>(
        val _head: Lazy<T>,
        val _tail: Lazy<Stream<T>>
    ) : Stream<T>() {

        override fun isEmpty(): Boolean = false

        override fun head(): Result<T> = Result(_head())

        override fun tail(): Result<Stream<T>> = Result(_tail())

        override fun takeAtMost(n: Int): Stream<T> = when {
            n > 0 -> cons(_head, tail = _tail.map { it.takeAtMost(n - 1) } )
            else -> Empty
        }

        override fun dropAtMost(n: Int): Stream<T> {
            tailrec fun dropAtMost(number: Int, acc: Stream<T>): Stream<T> = when {
                number > 0 -> {
                    when(acc) {
                        Empty -> acc
                        is Cons -> dropAtMost(number - 1, acc._tail())
                    }
                }
                else -> acc
            }

            return dropAtMost(n, this)
        }

        override fun takeWhile(p: (T) -> Boolean): Stream<T> = when {
            p(_head()) -> cons(_head, _tail.map { it.takeWhile(p) })
            else -> Empty
        }

        override fun <U> foldRight(
            z: Lazy<U>,
            f: (T) -> (Lazy<U>) -> U
        ): U = f(_head())(Lazy { _tail().foldRight(z, f) })
    }

    companion object {

        operator fun <T> invoke(): Stream<T> = Empty

        fun <T> cons(head: Lazy<T>, tail: Lazy<Stream<T>>): Stream<T> = Cons(head, tail)

        fun <T> iterate(seed: T, f: (T) -> T): Stream<T> = cons(
            head = Lazy { seed },
            tail = Lazy { iterate(f(seed), f) }
        )

        fun <T> repeat(f: () -> T): Stream<T> = cons(
            head = Lazy { f() },
            tail = Lazy { repeat(f) }
        )

        private fun <T> toList(stream: Stream<T>): List<T> {
            fun toList(acc: List<T>, stream: Stream<T>): List<T> = when(stream) {
                Empty -> acc
                is Cons -> toList(acc.construct(stream._head()), stream._tail())
            }

            return toList(List(), stream).reverse()
        }

        private tailrec fun <T> dropWhile(acc: Stream<T>, p: (T) -> Boolean): Stream<T> = when(acc) {
            Empty -> acc
            is Cons -> if (p(acc._head())) dropWhile(acc._tail(), p) else acc
        }

        private tailrec fun <T> exists(stream: Stream<T>, p: (T) -> Boolean): Boolean = when(stream) {
            Empty -> false
            is Cons -> if (p(stream._head())) true else exists(stream._tail(), p)
        }

        private fun <T> takeWhileViaFoldRight(
            stream: Stream<T>,
            p: (T) -> Boolean
        ): Stream<T> = stream.foldRight(Lazy { Empty }) { value: T ->
            { acc: Lazy<Stream<T>> ->
                if (p(value)) cons(Lazy { value }, acc) else Empty
            }
        }

        private fun <T> headSafeViaFoldRight(
            stream: Stream<T>
        ): Result<T> = stream.foldRight(Lazy { Result<T>() }) { value: T -> { Result(value) } }

        private fun <T, U> map(
            stream: Stream<T>,
            f: (T) -> U
        ): Stream<U> = stream.foldRight(Lazy { Empty }) { value: T ->
            { acc: Lazy<Stream<U>> -> cons(Lazy { f(value) }, acc) }
        }

        private fun <T> filter(
            stream: Stream<T>,
            p: (T) -> Boolean
        ): Stream<T> = stream.foldRight(Lazy { Empty }) { value: T ->
            { acc: Lazy<Stream<T>> ->
                if (p(value)) cons(Lazy { value }, acc) else acc()
            }
        }

        private fun <T> append(
            target: Stream<T>,
            added: Lazy<Stream<T>>
        ): Stream<T> = target.foldRight(added) { value: T ->
            { acc: Lazy<Stream<T>> -> cons(Lazy { value }, acc) }
        }

        private fun <T, U> flatMap(
            stream: Stream<T>,
            f: (T) -> Stream<U>
        ): Stream<U> = stream.foldRight(Lazy<Stream<U>> { Empty }) { value: T ->
            { acc: Lazy<Stream<U>> -> append(f(value), acc) }
        }

        private fun <T> find(
            stream: Stream<T>,
            p: (T) -> Boolean
        ): Result<T> = stream.filter(p).head()

        fun <T, U> unFold(
            seed: U,
            f: (U) -> Result<Pair<T, U>>
        ): Stream<T> = f(seed).map { (t, u) -> cons(Lazy { t }, Lazy { unFold(u, f) }) }.getOrElse(Empty)

        private fun <T> filter2(
            stream: Stream<T>,
            p: (T) -> Boolean
        ): Stream<T> = stream.dropWhile { !p(it) }.let { afterDropWhile ->
            when(afterDropWhile) {
                Empty -> afterDropWhile
                is Cons -> cons(afterDropWhile._head, Lazy { filter2(afterDropWhile._tail(), p) })
            }
        }
    }

}