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

import com.nlab.joyofkotlin.chapter11.Heap
import com.nlab.joyofkotlin.chapter12.forEach
import com.nlab.joyofkotlin.chapter13.AbstractActor
import com.nlab.joyofkotlin.chapter13.Actor
import com.nlab.joyofkotlin.chapter13.MessageProcessor
import com.nlab.joyofkotlin.chapter13.zipWithPosition
import com.nlab.joyofkotlin.chapter5.List
import com.nlab.joyofkotlin.chapter7.Result
import com.nlab.joyofkotlin.chapter8.splitAt
import com.nlab.joyofkotlin.chapter8.sequence

/**
 * @author Doohyun
 */
class Manager(
    id: String,
    list: List<Int>,
    private val client: Actor<Result<List<Int>>>,
    workers: Int
) : AbstractActor<Task>(id) {

    private val initial: List<Task>

    private val workList: List<Task>

    private val resultHeap: Heap<Task> = Heap()

    private val managerFunction: (Manager) -> (Behavior) -> (Task) -> Unit

    init {
        val splitLists = list.zipWithPosition().map { (first, second) -> Task(first, second) }.splitAt(workers)

        this.initial = splitLists.first
        this.workList = splitLists.second

        managerFunction = { manager ->
            { behavior ->
                { task ->
                    val result = behavior.resultHeap + task
                    if (result.size == list.sizeMemoized) {
                        this.client.tell(Result(result.toList().map { it.number }))
                    } else {
                        manager.context.become(Behavior(behavior.workList.tailSafe().getOrElse(List()), result))
                    }
                }
            }
        }
    }

    fun start() {
        onReceive(Task(0, 0), self())
        sequence(initial.map { initWorker(it) }).foreach(
            onSuccess = { initWorker(it) },
            onFailure = { tellClientEmptyResult(it.message ?: "Unknown error") }
        )
    }

    private fun initWorker(t: Task): Result<() -> Unit> = Result {
        Worker("Worker ${t.taskNumber}").tell(t.copy(), self())
    }

    private fun initWorker(lst: List<() -> Unit>) {
        lst.forEach { it() }
    }

    private fun tellClientEmptyResult(string: String) {
        client.tell(Result.failure("$string caused by empty input list"))
    }

    override fun onReceive(message: Task, sender: Result<Actor<Task>>) {
        context.become(Behavior(workList, resultHeap))
    }

    internal inner class Behavior internal constructor(
        internal val workList: List<Task>,
        internal val resultHeap: Heap<Task>
    ) : MessageProcessor<Task> {

        override fun process(message: Task, sender: Result<Actor<Task>>) {
            managerFunction(this@Manager)(this)(message)
            sender.foreach { actor ->
                workList.headSafe().foreach(
                    { actor.tell(it, self()) },
                    { actor.shutdown() }
                )
            }
        }

    }
}