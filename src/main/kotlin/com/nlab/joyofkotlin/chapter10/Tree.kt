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

package com.nlab.joyofkotlin.chapter10

import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter5.foldLeft
import com.nlab.joyofkotlin.chapter6.Option
import kotlin.math.abs
import kotlin.math.max

/**
 * @author Doohyun
 */
sealed class Tree<out T : Comparable<@UnsafeVariance T>> {

    abstract fun isEmpty(): Boolean
    abstract fun max(): Option<T>
    abstract fun min(): Option<T>
    abstract fun <U> foldLeft(identity: U, f: (U) -> (T) -> U, g: (U) -> (U) -> (U)): U
    abstract fun <U> foldRight(identity: U, f: (T) -> (U) -> (U), g: (U) -> (U) -> (U)): U
    abstract fun <U> foldInOrder(identity: U, f: (U) -> (T) -> (U) -> (U)): U
    abstract fun <U> foldInReverseOrder(identity: U, f: (U) -> (T) -> (U) -> (U)): U
    abstract fun <U> foldPreOrder(identity: U, f: (T) -> (U) -> (U) -> (U)): U
    abstract fun <U> foldPostOrder(identity: U, f: (U) -> (U) -> (T) -> U): U
    abstract fun <U : Comparable<U>> map(f: (T) -> U): Tree<U>
    abstract val size: Int
    abstract val height: Int

    protected abstract fun rotateRight(): Tree<T>
    protected abstract fun rotateLeft(): Tree<T>

    operator fun plus(element: @UnsafeVariance T): Tree<T> = when(this) {
        is Empty -> TreeEx(left = Empty, value = element, right = Empty)
        is TreeEx -> when {
            element < value -> TreeEx(left = left + element, value = value, right = right)
            element > value -> TreeEx(left = left, value = value, right = right + element)
            else -> TreeEx(left = Empty, value = element, right = Empty)
        }
    }

    operator fun contains(element: @UnsafeVariance T): Boolean = when(this) {
        is Empty -> false
        is TreeEx -> when {
            value == element -> true
            value > element -> left.contains(element)
            else -> right.contains(element)
        }
    }

    operator fun get(element: @UnsafeVariance T): Option<T> = when(this) {
        is Empty -> Option()
        is TreeEx -> when {
            value == element -> Option(value)
            value > element -> left[element]
            else -> right[element]
        }
    }

    fun remove(element: @UnsafeVariance T): Tree<T> = when(this) {
        Empty -> this
        is TreeEx -> when {
            value == element -> removeMerge(right, left)
            value > element -> TreeEx(left.remove(element), value, right)
            else -> TreeEx(left, value, right.remove(element))
        }
    }

    fun merge(tree: Tree<@UnsafeVariance T>): Tree<T> = when(this) {
        Empty -> tree
        is TreeEx -> when(tree) {
            Empty -> this
            is TreeEx -> when {
                tree.value < value -> {
                    TreeEx(left.merge(TreeEx(tree.left, tree.value, Empty)), value, right).merge(tree.right)
                }
                tree.value > value -> {
                    TreeEx(left, value, right.merge(TreeEx(Empty, tree.value, tree.right))).merge(tree.left)
                }
                else -> {
                    TreeEx(left.merge(tree.left), value, right.merge(tree.right))
                }
            }
        }
    }

    fun toListByDesc(): List<T> {
        tailrec fun toListByDescRec(acc: List<T>, tree: Tree<T>): List<T> = when(tree) {
            Empty -> acc
            is TreeEx -> when(tree.left) {
                Empty -> toListByDescRec(acc.construct(tree.value), tree.right)
                is TreeEx -> toListByDescRec(acc, tree.rotateRight())
            }
        }

        return toListByDescRec(List(), this)
    }

    fun toListByAsc(): List<T> {
        tailrec fun toListByAscRec(acc: List<T>, tree: Tree<T>): List<T> = when(tree) {
            Empty -> acc
            is TreeEx -> when(tree.right) {
                Empty -> toListByAscRec(acc.construct(tree.value), tree.left)
                is TreeEx -> toListByAscRec(acc, tree.rotateLeft())
            }
        }

        return toListByAscRec(List(), this)
    }

    private object Empty : Tree<Nothing>() {
        override val size: Int = 0
        override val height: Int = -1
        override fun isEmpty(): Boolean = true
        override fun max(): Option<Nothing> = Option.None
        override fun min(): Option<Nothing> = Option.None
        override fun <U> foldLeft(identity: U, f: (U) -> (Nothing) -> U, g: (U) -> (U) -> U): U = identity
        override fun <U> foldRight(identity: U, f: (Nothing) -> (U) -> U, g: (U) -> (U) -> U): U = identity
        override fun <U> foldInOrder(identity: U, f: (U) -> (Nothing) -> (U) -> U): U = identity
        override fun <U> foldInReverseOrder(identity: U, f: (U) -> (Nothing) -> (U) -> U): U = identity
        override fun <U> foldPreOrder(identity: U, f: (Nothing) -> (U) -> (U) -> U): U = identity
        override fun <U> foldPostOrder(identity: U, f: (U) -> (U) -> (Nothing) -> U): U = identity
        override fun <U : Comparable<U>> map(f: (Nothing) -> U): Tree<U> = this
        override fun rotateRight(): Tree<Nothing> = this
        override fun rotateLeft(): Tree<Nothing> = this
        override fun toString(): String = "E"
    }

    private class TreeEx<out T : Comparable<@UnsafeVariance T>>(
        val left: Tree<T>,
        val value: T,
        val right: Tree<T>
    ) : Tree<T>() {
        override val size: Int = 1 + left.size + right.size
        override val height: Int = 1 + max(left.height, right.height)
        override fun isEmpty(): Boolean = false
        override fun max(): Option<T> = right.max().orElse { Option(value) }
        override fun min(): Option<T> = left.min().orElse { Option(value) }
        override fun <U> foldLeft(
            identity: U,
            f: (U) -> (T) -> U,
            g: (U) -> (U) -> U
        ): U = g(right.foldLeft(identity, f, g))(f(left.foldLeft(identity, f, g))(value))
        override fun <U> foldRight(
            identity: U,
            f: (T) -> (U) -> U,
            g: (U) -> (U) -> U
        ): U = g(f(value)(left.foldRight(identity, f, g)))(right.foldRight(identity, f, g))
        override fun <U> foldInOrder(
            identity: U,
            f: (U) -> (T) -> (U) -> U
        ): U = f(left.foldInOrder(identity, f))(value)(right.foldInOrder(identity, f))
        override fun <U> foldInReverseOrder(
            identity: U,
            f: (U) -> (T) -> (U) -> U
        ): U = f(right.foldInReverseOrder(identity, f))(value)(left.foldInReverseOrder(identity, f))
        override fun <U> foldPreOrder(
            identity: U,
            f: (T) -> (U) -> (U) -> U
        ): U = f(value)(left.foldPreOrder(identity, f))(right.foldPreOrder(identity, f))
        override fun <U> foldPostOrder(
            identity: U,
            f: (U) -> (U) -> (T) -> U
        ): U = f(left.foldPostOrder(identity, f))(right.foldPostOrder(identity, f))(value)
        override fun <U : Comparable<U>> map(f: (T) -> U): Tree<U> = foldPreOrder(invoke()) { value: T ->
            { result1: Tree<U> -> { result2: Tree<U> -> invoke(result1, f(value), result2) } }
        }
        override fun rotateRight(): Tree<T> = when(left) {
            Empty -> this
            is TreeEx -> TreeEx(left.left, left.value, TreeEx(left.right, value, right))
        }
        override fun rotateLeft(): Tree<T> = when(right) {
            Empty -> this
            is TreeEx -> TreeEx(TreeEx(left, value, right.left), right.value, right.right)
        }
        override fun toString(): String = "[$left $value $right]"
    }

    companion object {

        operator fun <T : Comparable<T>> invoke(): Tree<T> = Empty

        operator fun <T : Comparable<T>> invoke(list: List<T>): Tree<T> = foldLeft(list, invoke()) { acc: Tree<T> ->
            { element: T -> acc + element }
        }

        operator fun <T : Comparable<T>> invoke(vararg elements: T): Tree<T> = invoke(List(*elements))

        operator fun <T : Comparable<T>> invoke(left: Tree<T>, value: T, right: Tree<T>): Tree<T> = when {
            isOrdered(left, value, right) -> TreeEx(left, value, right)
            isOrdered(right, value, left) -> TreeEx(right, value, left)
            else -> Tree(value).merge(left).merge(right)
        }

        private fun <T : Comparable<T>> isOrdered(
            left: Tree<T>,
            value: T,
            right: Tree<T>
        ): Boolean = left.max()
            .flatMap { lMax -> right.min().map { rMin -> lt(lMax, value, rMin) } }
            .getOrElse {
                (left.isEmpty() && right.max().map { rMax -> lt(value, rMax) }.getOrElse { true })
                        || (right.isEmpty() && left.max().map { lMax -> lt(lMax, value) }.getOrElse { true })
            }

        private fun <T : Comparable<T>> lt(a: T, b: T): Boolean = a < b
        private fun <T : Comparable<T>> lt(a: T, b: T, c: T): Boolean = lt(a, b) && lt(b, c)

        private fun <T : Comparable<T>> removeMerge(a: Tree<T>, b: Tree<T>): Tree<T> = when(a) {
            Empty -> b
            is TreeEx -> {
                when(b) {
                    Empty -> a
                    is TreeEx -> {
                        when {
                            b.value < a.value -> TreeEx(removeMerge(a.left, b), a.value, a.right)
                            else -> TreeEx(a.left, a.value, removeMerge(a.right, b))
                        }
                    }
                }
            }
        }

        private fun <T : Comparable<T>> isUnBalance(tree: Tree<T>): Boolean = when(tree) {
            Empty -> false
            is TreeEx -> abs(tree.left.height - tree.right.height) > (tree.size - 1) % 2
        }

        private fun log2nlz(n: Int) = when(n) {
            0 -> 0
            else -> 31 - Integer.numberOfLeadingZeros(n)
        }

        /**
         * 데이-스타우트-워런 알고리즘
         *
         * 1. Tree 를 완전불균형 트리로 만든다.
         * 2. 양쪽 균형이 맞도록 트리 회전 (트리 size 가 짝수라면 1차이, 홀수라면 동일)
         * 3. 왼쪽, 오른쪽 트리 역시 동일한 처리 필요.
         */
        fun <T : Comparable<T>> balance(
            tree: Tree<T>
        ): Tree<T> = balanceHelper(foldLeft(tree.toListByDesc(), invoke()) { acc ->
            { value -> acc + value }
        })

        private fun <T : Comparable<T>> balanceHelper(tree: Tree<T>): Tree<T> = when(tree) {
            Empty -> tree
            is TreeEx -> when {
                tree.height <= log2nlz(tree.size) -> tree
                else -> when {
                    isUnBalance(tree) -> balanceHelper(balanceFirstLevel(tree))
                    else -> TreeEx(balanceHelper(tree.left), tree.value, balanceHelper(tree.right))
                }
            }
        }

        private fun <T : Comparable<T>> balanceFirstLevel(tree: Tree<T>): Tree<T> = when(tree) {
            Empty -> tree
            is TreeEx -> when {
                !isUnBalance(tree) -> tree
                else -> when {
                    tree.left.height < tree.right.height -> tree.rotateLeft()
                    else -> tree.rotateRight()
                }
            }
        }

    }

}