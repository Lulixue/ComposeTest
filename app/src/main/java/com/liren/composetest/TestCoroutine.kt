package com.liren.composetest

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun testCoroutine() = runBlocking {
    coroutineScope {
        println("coroutineScope")
    }
    repeat(10) {}
    launch {
        println("launch")
    }
}

fun simple(): Sequence<Int> = sequence { // sequence builder
    for (i in 1..3) {
        Thread.sleep(100) // pretend we are computing it
        yield(i) // yield next value
    }
}

fun main() {
    simple().forEach { value -> println(value) }
}