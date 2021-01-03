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

package com.nlab.joyofkotlin.chapter13

import com.nlab.joyofkotlin.chapter7.Result
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException

/**
 * @author Doohyun
 */
abstract class AbstractActor<T>(protected val id: String) : Actor<T> {

    override val context: ActorContext<T> = object : ActorContext<T> {
        var behavior: MessageProcessor<T> = object : MessageProcessor<T> {
            override fun process(message: T, sender: Result<Actor<T>>) {
                onReceive(message, sender)
            }
        }

        @Synchronized
        override fun become(behavior: MessageProcessor<T>) {
            this.behavior = behavior
        }

        override fun behavior(): MessageProcessor<T> = behavior
    }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor(DaemonThreadFactory())

    abstract fun onReceive(message: T, sender: Result<Actor<T>>)

    final override fun self(): Result<Actor<T>> = Result(this)

    final override fun shutdown() {
        executor.shutdown()
    }

    @Synchronized
    final override fun tell(message: T, sender: Result<Actor<T>>) = executor.execute {
        try {
            context.behavior().process(message, sender)
        } catch (e: RejectedExecutionException) {
            // nothing.
            // 액터가 중단되면서 모든 대기중이던 작업이 취소됨.
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}