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

package com.nlab.joyofkotlin.chapter13.fibo

import com.nlab.joyofkotlin.chapter13.SimpleActor
import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter8.range
import com.nlab.joyofkotlin.chapter8.splitAt
import java.util.concurrent.Semaphore
import kotlin.random.Random

/**
 * @author Doohyun
 */
private val semaphore = Semaphore(1)
private const val listLength = 20_000
private const val workers = 8
private val rnd = Random(0)
private val testList = range(0, listLength).map { rnd.nextInt(35) }

fun main(args: Array<String>) {
    semaphore.acquire()
    val startTime = System.currentTimeMillis()

    val client = SimpleActor<Result<List<Int>>>("client") { message ->
        {
            message.foreach (
                onSuccess = { processSuccess(it) },
                onFailure = { processFailure(it.message ?: "Unknown Error") }
            )

            println("Total time: ${System.currentTimeMillis() - startTime}")
            semaphore.release()
        }
    }

    val manager = Manager("Manager", testList, client, workers)
    manager.start()
    semaphore.acquire()
}

private fun processSuccess(lst: List<Int>) {
    println("Input: ${testList.splitAt(40).first}")
    println("Result: ${lst.splitAt(40).first}")
}

private fun processFailure(message: String) {
    println(message)
}