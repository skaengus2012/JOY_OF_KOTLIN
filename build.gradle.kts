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


group = "com.github.skaengus2012"
version = "1.0-SNAPSHOT"

plugins {
    kotlin(module = "jvm") version "1.4.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin(module = "stdlib-jdk8"))

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.2")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.2")

    testImplementation("com.nhaarman.mockitokotlin2", "mockito-kotlin", "2.2.0")
    testImplementation("org.mockito", "mockito-inline", "3.5.10")
    testImplementation("io.kotlintest", "kotlintest-runner-junit5", "3.4.2")
    testRuntimeOnly("org.slf4j", "slf4j-nop", "1.7.30")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    test {
        useJUnitPlatform()
    }
}