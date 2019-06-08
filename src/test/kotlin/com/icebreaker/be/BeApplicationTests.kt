package com.icebreaker.be

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Config::class])
class BeApplicationTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun contextLoads() {
    }

    fun authenticate(email: String, password: String) {
        val token = getToken(email, password)
        val arrayList = ArrayList<ClientHttpRequestInterceptor>()
        arrayList.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers.add("Authorization", "Bearer ${token.accessToken}")
            execution.execute(request, body)
        })
        testRestTemplate.restTemplate.interceptors = arrayList
    }

    fun authenticate() {
        authenticate("email1@email.com", "password")
    }

    private fun getToken(email: String, password: String): GetTokenResponse {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.set("Access-Control-Allow-Headers", "x-requested-with, authorization")

        val map = LinkedMultiValueMap<String, String>()
        map.add("grant_type", "password")
        map.add("username", email)
        map.add("password", password)

        val request = HttpEntity<MultiValueMap<String, String>>(map, headers)

        val restTemplateWithBasic = testRestTemplate.withBasicAuth("spring-security-oauth2-read-write-client", "spring-security-oauth2-read-write-client-password1234")
        val result = restTemplateWithBasic.postForEntity("/oauth/token", request, GetTokenResponse::class.java)
        Assert.assertNotNull(result)
        Assert.assertEquals(result.statusCode, HttpStatus.OK)
        Assert.assertNotNull(result.body)
        return result.body!!
    }

    data class GetTokenResponse(
            @JsonProperty("access_token") val accessToken: String,
            @JsonProperty("refresh_token") val refreshToken: String,
            @JsonProperty("token_type") val tokenType: String,
            @JsonProperty("expires_in") val expiresIn: Int,
            @JsonProperty("scope") val scope: String)
}

@TestConfiguration
class Config