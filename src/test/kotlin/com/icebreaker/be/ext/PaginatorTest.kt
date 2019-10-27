package com.icebreaker.be.ext

import org.junit.Assert
import org.junit.Test
import java.lang.Integer.min


internal class PaginatorTest {


    private fun <T> getCallback(list: List<T>, name: String): (limit: Int, offset: Int) -> List<T> {
        return { l: Int, o: Int ->
            println("name: l=$l o=$o")
            if (o < list.size) {
                if (o + l < list.size)
                    list.subList(o, l + o)
                else
                    list.subList(o, list.size)
            } else {
                emptyList()
            }
        }
    }

    @Test
    fun testLimit4() {

        val limit = 4

        val list1 = listOf(1, 2, 3, 4, 5, 6, 7)
        val list2 = listOf(8, 9, 10, 11, 12, 13, 14, 15)

        val callback1 = getCallback(list1, "list1")
        val callback2 = getCallback(list2, "list2")

        val paginate1 = Paginator.paginate(limit, 0, callback1, callback2)
        println(paginate1)
        Assert.assertEquals(listOf(1, 2, 8, 9), paginate1)

        val paginate2 = Paginator.paginate(limit, limit, callback1, callback2)
        println(paginate2)
        Assert.assertEquals(listOf(3, 4, 10, 11), paginate2)

        val paginate3 = Paginator.paginate(limit, limit * 2, callback1, callback2)
        println(paginate3)

        Assert.assertEquals(listOf(5, 6, 12, 13), paginate3)

        val paginate4 = Paginator.paginate(limit, limit * 3, callback1, callback2)
        println(paginate4)

        Assert.assertEquals(listOf(7, 14, 15), paginate4)


        val paginate5 = Paginator.paginate(limit, limit * 4, callback1, callback2)
        println(paginate5)
        Assert.assertEquals(emptyList<Int>(), paginate5)
    }

    @Test
    fun testLimit10() {

        val limit = 10
        val list1 = listOf(1, 2, 3, 4, 5, 6, 7)
        val list2 = listOf(8, 9, 10, 11, 12, 13, 14, 15)

        val callback1 = getCallback(list1, "list1")
        val callback2 = getCallback(list2, "list2")

        val paginate1 = Paginator.paginate(limit, 0, callback1, callback2)
        println(paginate1)
        Assert.assertEquals(listOf(1, 2, 3, 4, 5, 8, 9, 10, 11, 12), paginate1)

        val paginate2 = Paginator.paginate(limit, limit, callback1, callback2)
        println(paginate2)
        Assert.assertEquals(listOf(6, 7, 13, 14, 15), paginate2)

        val paginate3 = Paginator.paginate(limit, limit * 2, callback1, callback2)
        println(paginate3)
        Assert.assertEquals(emptyList<Int>(), paginate3)
    }

    @Test
    fun testLimit20() {

        val limit = 20
        val list1 = listOf(1, 2, 3, 4, 5, 6, 7)
        val list2 = listOf(8, 9, 10, 11, 12, 13, 14, 15)

        val callback1 = getCallback(list1, "list1")
        val callback2 = getCallback(list2, "list2")

        val paginate1 = Paginator.paginate(limit, 0, callback1, callback2)
        println(paginate1)
        Assert.assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), paginate1)

        val paginate2 = Paginator.paginate(limit, limit, callback1, callback2)
        println(paginate2)
        Assert.assertEquals(emptyList<Int>(), paginate2)
    }

    @Test
    fun firstEmptyList() {

        val limit = 4

        val list1 = emptyList<Int>()
        val list2 = listOf(8, 9, 10, 11, 12, 13, 14, 15)

        val callback1 = getCallback(list1, "list1")
        val callback2 = getCallback(list2, "list2")

        val paginate1 = Paginator.paginate(limit, 0, callback1, callback2)
        println(paginate1)
        Assert.assertEquals(listOf(8, 9, 10, 11), paginate1)

    }

    @Test
    fun secondEmptyList() {

        val limit = 8

        val list1 = listOf(1, 2, 3, 4, 5, 6, 7)
        val list2 = emptyList<Int>()

        val callback1 = getCallback(list1, "list1")
        val callback2 = getCallback(list2, "list2")

        val paginate1 = Paginator.paginate(limit, 0, callback1, callback2)
        println(paginate1)
        Assert.assertEquals(listOf(1, 2, 3, 4), paginate1)

        val paginate2 = Paginator.paginate(limit, limit, callback1, callback2)
        println(paginate2)
        Assert.assertEquals(listOf(5, 6, 7), paginate2)

    }

}