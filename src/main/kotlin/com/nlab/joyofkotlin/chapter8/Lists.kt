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

package com.nlab.joyofkotlin.chapter8

import com.nlab.joyofkotlin.chapter5.*
import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter6.Option
import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter7.map2
import java.util.concurrent.ExecutorService

/**
 * @author Doohyun
 */

fun <T> flattenResult(list: List<Result<T>>): List<T> = flatten(
    map(list) { result -> result.map { List(it) }.getOrElse(List()) }
)

fun <T> sequence(list: List<Result<T>>): Result<List<T>> = traverse(list) { result: Result<T> -> result }

fun <T, U> traverse(list: List<T>, f: (T) -> Result<U>): Result<List<U>> = foldRight(list, Result(List())) { t: T ->
    { ru: Result<List<U>> -> map2(ru, f(t)) { list: List<U> -> { u: U -> list.construct(u) } } }
}

fun <T, U, V> zipWith(
    list1: List<T>,
    list2: List<U>,
    f: (T) -> (U) -> V
): List<V> {

    tailrec fun zipWithRec(
        acc: List<V>,
        subList1: List<T>,
        subList2: List<U>
    ): List<V> = if (subList1 is List.Cons && subList2 is List.Cons) {
        zipWithRec(
            acc.construct(f(subList1.head)(subList2.head)),
            subList1.tail,
            subList2.tail
        )
    } else {
        acc
    }

    return zipWithRec(List(), list1, list2).reverse()
}

fun <T, U, V> product(
    list1: List<T>,
    list2: List<U>,
    f: (T) -> (U) -> V
): List<V> = flatMap(list1) { t: T ->
    map(list2) { u: U ->
        f(t)(u)
    }
}

fun <T, U> unzip(
    list: List<Pair<T, U>>
): Pair<List<T>, List<U>> = unzip(list) { pair -> pair }

fun <T, U, V> unzip(
    list: List<T>,
    f: (T) -> Pair<U, V>
): Pair<List<U>, List<V>> = foldRight(list.reverse(), Pair(List(), List())) { t: T ->
    { acc: Pair<List<U>, List<V>> ->
        f(t).let { newPair ->
            Pair(acc.first.construct(newPair.first), acc.second.construct(newPair.second))
        }
    }
}

fun <T, U> List<T>.foldLeft(identity: U, p: (U) -> Boolean, f: (U) -> (T) -> U): U = when(this) {
    is List.Nil -> identity
    is List.Cons -> {
        tailrec fun foldLeftRec(
            acc: U,
            list: List<T>
        ): U = if (p(acc)) {
            acc
        } else {
            when(list) {
                is List.Nil -> acc
                is List.Cons -> foldLeftRec(f(acc)(list.head), list.tail)
            }
        }

        foldLeftRec(identity, this)
    }
}

fun <T> List<T>.splitAt(index: Int): Pair<List<T>, List<T>> {

    tailrec fun splitAtRec(list1: List<T>, list2: List<T>, i: Int): Pair<List<T>, List<T>> = when(list1) {
        is List.Nil -> Pair(list1, list2.reverse())
        is List.Cons -> {
            if (i == 0) {
                Pair(list1, list2.reverse())
            } else {
                splitAtRec(list1.tail, list2.construct(list1.head), i - 1)
            }
        }
    }

    return when {
        index < 0 -> splitAt(0)
        index > sizeMemoized -> splitAt(sizeMemoized)
        else -> splitAtRec(this, List(), sizeMemoized - index)
    }
}

fun <T> List<T>.splitListAt(index: Int): List<List<T>> = splitAt(index).let { List(it.first, it.second) }

fun <T> List<T>.divide(depth: Int): List<List<T>> {
    tailrec fun divideRec(acc: List<List<T>>, depth: Int): List<List<T>> = when(acc) {
        is List.Nil -> acc
        is List.Cons -> {
            if (acc.head.sizeMemoized < 2 || depth < 1) {
                acc
            } else {
                divideRec(
                    flatMap(acc) { list -> list.splitListAt(list.sizeMemoized / 2) },
                    depth - 1
                )
            }
        }
    }

    return if (isEmpty()) {
        List(this)
    } else {
        divideRec(List(this), depth)
    }
}

fun <T, U> List<T>.parFoldLeft(
    es: ExecutorService,
    identity: U,
    f: (U) -> (T) -> U,
    m: (U) -> (U) -> U
): Result<U> = try {
    Result(
        foldLeft(
            map(map(divide(1024)) { list: List<T> -> es.submit<U> { foldLeft(list, identity, f)  } }) {
                it.get() },
            identity,
            m
        )
    )
} catch (e: Exception) {
    Result.failure(e)
}

fun <T, U> List<T>.parMap(es: ExecutorService, f: (T) -> U): Result<List<U>> = try {
    Result(map(this) { t: T -> es.submit<U> { f(t) }.get() })
} catch (e: Exception) {
    Result.failure(e)
}


fun <T, U> unfold(initializeValue: T, f: (T) -> Option<Pair<U, T>>): List<U> {
    tailrec fun unfoldRec(acc: List<U>, value: T): List<U> = when(val next = f(value)) {
        Option.None -> acc
        is Option.Some -> unfoldRec(acc.construct(next.value.first), next.value.second)
    }

    return unfoldRec(List(), initializeValue).reverse()
}

fun range(start: Int, end: Int): List<Int> = unfold(start) { i ->
    if (i < end) {
        Option(Pair(i, i + 1))
    } else {
        Option()
    }
}