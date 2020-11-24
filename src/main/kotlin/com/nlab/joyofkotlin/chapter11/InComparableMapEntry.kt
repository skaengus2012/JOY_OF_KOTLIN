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

import com.nlab.joyofkotlin.chapter6.Option

/**
 * @author Doohyun
 */
class InComparableMapEntry<K : Any, V> private constructor(
    private val key: K,
    val value: Option<V>
) : Comparable<InComparableMapEntry<K, V>> {

    override fun compareTo(
        other: InComparableMapEntry<K, V>
    ): Int = hashCode().compareTo(other.hashCode())

    override fun toString(): String {
        return "InComparableMapEntry($key, $value)"
    }

    override fun equals(other: Any?): Boolean = (this === other) || when(other) {
        is InComparableMapEntry<*, *> -> other.key == key
        else -> false
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    companion object {
        operator fun <K : Comparable<K>, V> invoke(
            key: K,
            value: V
        ): InComparableMapEntry<K, V> = InComparableMapEntry(key, Option(value))
        operator fun <K : Comparable<K>, V> invoke(
            pair: Pair<K, V>
        ): InComparableMapEntry<K, V> = invoke(pair.first, pair.second)
        operator fun <K : Comparable<K>, V> invoke(
            key: K
        ): InComparableMapEntry<K, V> = InComparableMapEntry(key, Option())
    }

}