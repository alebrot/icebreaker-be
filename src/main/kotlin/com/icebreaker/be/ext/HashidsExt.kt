package com.icebreaker.be.ext

import org.hashids.Hashids

fun Hashids.decodeToInt(string: String): Int {
    return decode(string)[0].toInt()
}