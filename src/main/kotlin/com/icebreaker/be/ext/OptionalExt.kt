package com.icebreaker.be.ext

import java.util.*

fun <T> Optional<T>.toKotlinNotOptionalOrFail(): T {
    return if (this.isPresent) this.get() else throw IllegalArgumentException("${this.javaClass.name} not found")
}

fun <T> Optional<T>.toKotlinOptional(): T? {
    return if (this.isPresent) this.get() else null
}