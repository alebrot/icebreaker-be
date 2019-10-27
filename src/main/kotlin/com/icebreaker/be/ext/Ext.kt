package com.icebreaker.be.ext

class Paginator {
    companion object {
        fun <T> paginate(limit: Int, offset: Int, callback1: (limit: Int, offset: Int) -> List<T>, callback2: (limit: Int, offset: Int) -> List<T>): List<T> {
            if (limit % 2 != 0) {
                throw IllegalArgumentException("limit should be even")
            }
            if (offset % 2 != 0) {
                throw IllegalArgumentException("offset should be even")
            }

            val l = limit / 2
            val o = offset / 2

            val list1 = callback1(l, o).toMutableList()
            val list2 = callback2(limit - list1.size, o)

            list1.addAll(list2)
            return list1

        }
    }
}


fun Byte.toBoolean(): Boolean {
    return when (this) {
        1.toByte() -> true
        else -> false
    }
}
