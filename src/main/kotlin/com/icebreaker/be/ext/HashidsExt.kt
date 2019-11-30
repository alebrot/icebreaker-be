package com.icebreaker.be.ext

import org.hashids.Hashids

fun Hashids.decodeToInt(string: String): Int {
    val decode = decode(string)
    if (decode.isEmpty()) throw IllegalArgumentException()
    return decode[0].toInt()
}