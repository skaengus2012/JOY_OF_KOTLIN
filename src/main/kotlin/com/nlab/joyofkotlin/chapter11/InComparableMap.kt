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
import com.nlab.joyofkotlin.chapter5.filter
import com.nlab.joyofkotlin.chapter5.foldLeft
import com.nlab.joyofkotlin.chapter6.Option
import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter10.Tree as SimpleTree

/**
 * @author Doohyun
 */
class InComparableMap<K : Any, V>(
    private val delegate: SimpleTree<InComparableMapEntry<Int, List<Pair<K, V>>>> = SimpleTree()
) {

    private fun getAll(
        key: K
    ): Option<List<Pair<K, V>>> = delegate[InComparableMapEntry(key.hashCode())].flatMap { it.value }

    operator fun plus(entry: Pair<K, V>): InComparableMap<K, V> = InComparableMap(
        delegate + InComparableMapEntry(
            entry.first.hashCode(),
            getAll(entry.first).map { lst ->
                foldLeft(lst, List(entry)) { acc ->
                    { pair -> if (pair.first == entry.first) acc else acc.construct(pair) }
                }
            }.getOrElse { List(entry) }
        )
    )

    operator fun minus(key: K): InComparableMap<K, V> = getAll(key)
        .map { lst ->
            foldLeft(lst, List<Pair<K, V>>()) { acc ->
                { pair -> if (pair.first == key) acc else acc.construct(pair) }
            }
        }
        .getOrElse { List() }
        .let { deletedValues ->
            InComparableMap(
                if (deletedValues.isEmpty()) {
                    delegate.remove(InComparableMapEntry(key.hashCode()))
                } else {
                    delegate + InComparableMapEntry(key.hashCode(), deletedValues)
                }
            )
        }

    fun contains(key: K): Boolean = getAll(key)
        .map { lst -> lst.exists { pair -> pair.first == key } }
        .getOrElse { false }

    fun get(key: K): Result<V> = getAll(key)
        .map { lst -> filter(lst) { pair -> pair.first == key}.headSafe().map { it.second } }
        .getOrElse { Result.failure("Data was empty") }

}