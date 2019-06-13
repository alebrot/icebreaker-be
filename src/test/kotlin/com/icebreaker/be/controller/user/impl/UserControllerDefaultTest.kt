package com.icebreaker.be.controller.user.impl

import com.icebreaker.be.BeApplicationTests
import com.icebreaker.be.controller.user.CREATE_USER
import com.icebreaker.be.controller.user.GET_ADMIN_ME
import com.icebreaker.be.controller.user.GET_USER_BY_ID
import com.icebreaker.be.controller.user.GET_USER_ME
import com.icebreaker.be.controller.user.dto.*
import org.junit.Assert
import org.junit.Test
import org.springframework.http.HttpStatus

internal class UserControllerDefaultTest : BeApplicationTests() {
    @Test
    fun getUserById() {
//        authenticate()
        authenticateSocial("EAAg5KGcfXAABAHYGtcvlQHSNaEmhyOwZAjl4991hynibTl6fZB7cR9QGpF76oefN8c4aTJZA0r1PZBIn1jpYqpqpU36EIBOiNndqxovzz5c7ZCUdZCloVZAEwVUZBPFUijoRBJm7vOoFqIVjVy3SgJ4HzqS4ZBesx3N9wLiaZCaf0MVKI9j45SGPYlruow4yfXwdgcy4IaHY2ZCJVjVnF1Buk4GRhIhfkTf58ew09gZAjGWw9QZDZD")

        val userId = 1

        val response = testRestTemplate.getForEntity(GET_USER_BY_ID.replace("{userId}", userId.toString()), GetUserByIdResponse::class.java)
        Assert.assertNotNull(response)
        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertNotNull(response.body)
        Assert.assertNotNull(response.body!!.user)
    }

    @Test
    fun getUserMe() {
        authenticate("email2@email.com", "password")
        val response = testRestTemplate.getForEntity(GET_USER_ME, GetUserMeResponse::class.java)
        Assert.assertNotNull(response)
        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertNotNull(response.body)
        Assert.assertNotNull(response.body!!.context)
    }

    @Test
    fun getAdminMe() {
        authenticate()
        val response = testRestTemplate.getForEntity(GET_ADMIN_ME, GetAdminMeResponse::class.java)
        Assert.assertNotNull(response)
        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertNotNull(response.body)
        Assert.assertNotNull(response.body!!.context)
    }

    @Test
    fun createUser() {

        val email = "test@email.com"
        val password = "password"
        val createUserRequest = CreateUserRequest(email, "new firstname", "new lastname", password)

        val responseEntity = testRestTemplate.postForEntity(CREATE_USER, createUserRequest, CreateUserResponse::class.java)
        Assert.assertEquals(responseEntity.statusCode, HttpStatus.OK)
        Assert.assertNotNull(responseEntity)
        Assert.assertNotNull(responseEntity.body)
        Assert.assertNotNull(responseEntity.body!!.user.id)


        authenticate(email, password)
    }

}