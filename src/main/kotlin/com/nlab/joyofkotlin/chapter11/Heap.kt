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

package com.nlab.joyofkotlin.chapter11

import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter6.Option

/**
 * 레프티스트 힙
 *
 * @author Doohyun
 */
sealed class Heap<out T : Comparable<@UnsafeVariance T>> {

    internal abstract val left: Option<Heap<T>>
    internal abstract val right: Option<Heap<T>>
    internal abstract val head: Option<T>

    protected abstract val rank: Int

    abstract val size: Int
    abstract val isEmpty: Boolean
    abstract operator fun plus(element: @UnsafeVariance T): Heap<T>
    abstract fun tail(): Option<Heap<T>>
    abstract fun pop(): Option<Pair<T, Heap<T>>>

    fun get(index: Int): Option<T> {
        require(index >= 0) { "Invalid index : $index" }

        tailrec fun getRec(
            heap: Option<Heap<T>>,
            index: Int
        ): Option<Heap<T>> =
            if (index == 0 || heap.isEmpty()) heap else getRec(heap.flatMap { it.tail() }, index - 1)

        return getRec(Option(this), index).flatMap { it.head }
    }

    fun toList(): List<T> = foldLeft(List<T>()) { acc -> { value: T -> acc.construct(value) } }.reverse()

    fun <U> foldLeft(identity: U, f: (U) -> (T) -> (U)): U = unfold(this, { it.pop() }, identity, f)

    internal abstract class Empty<out T : Comparable<@UnsafeVariance T>> : Heap<T>() {
        override val left: Option<Heap<T>> = Option()
        override val right: Option<Heap<T>> = Option()
        override val head: Option<T> = Option()
        override val rank: Int = 0
        override val size: Int = 0
        override val isEmpty: Boolean = true
        override fun plus(element: @UnsafeVariance T): Heap<T> = invoke(element)
        override fun tail(): Option<Heap<T>> = Option()
        override fun pop(): Option<Pair<T, Heap<T>>> = Option()
    }

    internal object E : Empty<Nothing>()

    internal class H<out T : Comparable<@UnsafeVariance T>>(
        override val rank: Int,
        private val lft: Heap<T>,
        private val hd: T,
        private val rght: Heap<T>
    ) : Heap<T>() {
        override val left: Option<Heap<T>> = Option(lft)
        override val right: Option<Heap<T>> = Option(rght)
        override val head: Option<T> = Option(hd)
        override val size: Int = 1 + lft.size + rght.size
        override val isEmpty: Boolean = false
        override fun plus(element: @UnsafeVariance T): Heap<T> = merge(this, invoke(element))
        override fun tail(): Option<Heap<T>> = Option(merge(lft, rght))
        override fun pop(): Option<Pair<T, Heap<T>>> = Option(Pair(hd, tail().getOrElse { E }))
    }

    companion object {
        operator fun <T : Comparable<T>> invoke(): Heap<T> = E
        operator fun <T : Comparable<T>> invoke(element: T): Heap<T> = H(rank = 1, lft = E, hd = element, rght = E)

        private fun <T : Comparable<T>> merge(
            head: T,
            first: Heap<T>,
            second: Heap<T>
        ): Heap<T> {
            // 왼쪽은 오른쪽보다 랭크가 크거나 같아야 한다.
            // 랭크 비교에 따라 작은 쪽이 오른쪽으로 가며, 깊이가 한개 더 늘어나기 때문에 작은 쪽의 랭크를 더해준다.
            return if (first.rank >= second.rank) {
                H(second.rank + 1, first, head, second)
            } else {
                H(first.rank + 1, second, head, first)
            }
        }

        protected fun <T : Comparable<T>> merge(
            first: Heap<T>,
            second: Heap<T>
        ): Heap<T> = first.head.flatMap { fh ->
            second.head.flatMap { sh ->
                when {
                    fh <= sh -> first.left.flatMap { fl ->
                        first.right.map { fr ->
                            // 오른쪽이 언제나 원소가 적기 때문에 오른쪽과 타겟을 병합함.
                            merge(fh, fl, merge(fr, second))
                        }
                    }
                    else -> second.left.flatMap { sl ->
                        second.right.map { sr ->
                            merge(sh, sl, merge(sr, first))
                        }
                    }
                }
            }
        }.getOrElse {
            when {
                first.isEmpty -> second
                else -> first
            }
        }

        private fun <T, S, U> unfold(z: S, getNext: (S) -> Option<Pair<T, S>>, identity: U, f: (U) -> (T) -> (U)): U {
            tailrec fun unFoldRec(
                acc: U,
                z: S
            ): U = when(val next = getNext(z)) {
                is Option.None -> acc
                is Option.Some -> unFoldRec(f(acc)(next.value.first), next.value.second)
            }

            return unFoldRec(identity, z)
        }
    }

}