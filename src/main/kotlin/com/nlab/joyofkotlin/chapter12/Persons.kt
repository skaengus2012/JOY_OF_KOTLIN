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

package com.nlab.joyofkotlin.chapter12

import com.nlab.joyofkotlin.chapter7.Result

/**
 * @author Doohyun
 */

fun parsePerson(input: Input): Result<Pair<Person, Input>> {
    return input.readInt("Enter your Id:").map { it.first }.flatMap { id ->
        input.readString("Enter your first name").map { it.first }.flatMap { firstName ->
            input.readString("Enter your last name").map { (lastName, lastInput) ->
                Pair(Person(id, firstName, lastName), lastInput)
            }
        }
    }
}