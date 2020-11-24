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
import com.nlab.joyofkotlin.chapter5.concatViaFoldLeft
import com.nlab.joyofkotlin.chapter5.concatViaFoldRight
import com.nlab.joyofkotlin.chapter6.Option
import com.nlab.joyofkotlin.chapter6.sequence
import com.nlab.joyofkotlin.chapter10.Tree as SimpleTree

/**
 * @author Doohyun
 */
class Map<out K : Comparable<@UnsafeVariance K>, V>(
    private val delegate: SimpleTree<MapEntry<K, V>> = SimpleTree()
) {
    operator fun plus(entry: Pair<@UnsafeVariance K, V>): Map<K, V> = Map(delegate + MapEntry(entry))

    operator fun minus(key: @UnsafeVariance K): Map<K, V> = Map(delegate.remove(MapEntry(key)))

    fun contains(key: @UnsafeVariance K): Boolean = delegate.contains(MapEntry(key))

    fun get(key: @UnsafeVariance K): Option<V> = delegate[MapEntry(key)].flatMap { entry -> entry.value }

    fun isEmpty(): Boolean = delegate.isEmpty()

    fun size(): Int = delegate.size

    fun values(): List<V> = sequence(
        delegate.foldInReverseOrder(List<Option<V>>()) { result1 -> { entry -> { result2 ->
            concatViaFoldRight(result2, result1.construct(entry.value)) } } })
        .getOrElse { List() }

    companion object {
        operator fun invoke(): Map<Nothing, Nothing> = Map()
    }

}