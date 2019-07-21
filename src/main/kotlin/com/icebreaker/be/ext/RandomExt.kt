package com.icebreaker.be.ext

import java.util.*

/**
 *  min (inclusive) and max (inclusive)
 */
fun Random.getIntInRange(min: Int, max: Int): Int {
    if (min >= max) {
        throw IllegalArgumentException("max must be greater than min")
    }
    val r = Random()
    return r.nextInt(max - min + 1) + min
}