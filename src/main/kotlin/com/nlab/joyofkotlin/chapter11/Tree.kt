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


/**
 * @author Doohyun
 */
sealed class Tree<out T : Comparable<@UnsafeVariance T>> {
    abstract val size: Int
    abstract val height: Int
    internal abstract val color: Color
    internal abstract val isTB: Boolean
    internal abstract val isTR: Boolean
    internal abstract val right: Tree<T>
    internal abstract val left: Tree<T>
    internal abstract val value: T

    protected abstract fun blacken(): Tree<T>

    protected abstract fun balance(
        color: Color,
        left: Tree<@UnsafeVariance T>,
        value: @UnsafeVariance T,
        right: Tree<@UnsafeVariance T>
    ): Tree<T>

    protected abstract fun add(value: @UnsafeVariance T): Tree<T>

    operator fun plus(value: @UnsafeVariance T): Tree<T> = add(value).blacken()

    internal abstract class Empty<out T : Comparable<@UnsafeVariance T>> : Tree<T>() {
        override val size: Int = 0
        override val height: Int = -1
        override val color: Color = Color.B
        override val isTB: Boolean = false
        override val isTR: Boolean = false
        override val right: Tree<T> get() = throw IllegalStateException("right called on Empty tree.")
        override val left: Tree<T> get() = throw IllegalStateException("left called on Empty tree.")
        override val value: T get() = throw IllegalStateException("value called on Empty tree.")

        override fun blacken(): Tree<T> = E

        override fun balance(
            color: Color,
            left: Tree<@UnsafeVariance T>,
            value: @UnsafeVariance T,
            right: Tree<@UnsafeVariance T>
        ): Tree<T> = TreeImpl(Color.B, left, value, right)

        override fun add(value: @UnsafeVariance T): Tree<T> = TreeImpl(Color.R, E, value, E)

        override fun toString(): String = "E"
    }

    internal object E : Empty<Nothing>()

    internal class TreeImpl<out T : Comparable<@UnsafeVariance T>>(
        override val color: Color,
        override val left: Tree<T>,
        override val value: T,
        override val right: Tree<T>
    ) : Tree<T>() {
        override val size: Int = left.size + right.size + 1
        override val height: Int = maxOf(left.height, right.height) + 1
        override val isTB: Boolean = (color === Color.B)
        override val isTR: Boolean = (color === Color.R)

        override fun blacken(): Tree<T> = TreeImpl(Color.B, left, value, right)

        override fun balance(
            color: Color,
            left: Tree<@UnsafeVariance T>,
            value: @UnsafeVariance T,
            right: Tree<@UnsafeVariance T>
        ): Tree<T> = when {
            color === Color.B && left.isTR && left.left.isTR -> {
                TreeImpl(
                    Color.R,
                    left.left.left.blacken(),
                    left.left.value,
                    TreeImpl(
                        Color.B,
                        left.right,
                        value,
                        right
                    )
                )
            }

            color === Color.B && left.isTR && left.right.isTR -> {
                TreeImpl(
                    Color.R,
                    TreeImpl(
                        Color.B,
                        left.left,
                        left.value,
                        left.right.left
                    ),
                    left.right.value,
                    TreeImpl(
                        Color.B,
                        left.right.right,
                        value,
                        right
                    )
                )
            }

            color === Color.B && right.isTR && right.left.isTR -> {
                TreeImpl(
                    Color.R,
                    TreeImpl(
                        Color.B,
                        left,
                        value,
                        right.left.left
                    ),
                    right.left.value,
                    TreeImpl(
                        Color.B,
                        right.left.right,
                        right.value,
                        right.right
                    )
                )
            }

            color === Color.B && right.isTR && right.right.isTR -> {
                TreeImpl(
                    Color.R,
                    TreeImpl(
                        Color.B,
                        left,
                        value,
                        right.left
                    ),
                    right.value,
                    right.right.blacken()
                )
            }

            else -> TreeImpl(color, left, value, right)
        }

        override fun add(value: @UnsafeVariance T): Tree<T> = when {
            this.value > value -> balance(color, left.add(value), this.value, right)
            this.value < value -> balance(color, left, this.value, right.add(value))
            else -> TreeImpl(color, left, value, right)
        }

        override fun toString(): String = "(T $color $left $value $right)"
    }

    companion object {
        operator fun <T : Comparable<T>> invoke(): Tree<T> = E
    }

}