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

/**
 * @author Doohyun
 */
interface Actor<T> {
    val context: ActorContext<T>

    fun self(): Result<Actor<T>> = Result(this)
    fun tell(message: T, sender: Result<Actor<T>> = self())
    fun shutdown()
    fun tell(message: T, sender: Actor<T>) = tell(message, Result(sender))

    companion object {
        fun <T> noSender(): Result<Actor<T>> = Result()
    }
}