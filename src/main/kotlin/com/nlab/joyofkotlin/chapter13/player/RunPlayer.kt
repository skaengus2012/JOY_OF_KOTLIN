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

package com.nlab.joyofkotlin.chapter13.player

import com.nlab.joyofkotlin.chapter13.SimpleActor
import com.nlab.joyofkotlin.chapter7.Result
import java.util.concurrent.Semaphore

/**
 * @author Doohyun
 */
private val semaphore = Semaphore(1)

fun main(args: Array<String>) {
    val referee = SimpleActor<Int>("Referee") { message ->
        {
            println("Game ended after $message shots")
            semaphore.release()
        }
    }

    val player1 = Player("Player1", "Ping", referee)
    val player2 = Player("Player2", "Pong", referee)
    semaphore.acquire()
    player1.tell(1, Result(player2))
    semaphore.acquire()
}